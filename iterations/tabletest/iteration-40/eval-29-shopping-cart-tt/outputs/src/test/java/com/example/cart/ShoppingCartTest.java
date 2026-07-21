package com.example.cart;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShoppingCartTest {

    @Description("""
        Adding a product already in the cart increases its quantity rather than
        replacing it (assumption — the rules don't state this explicitly).
        Quantity must be a positive integer; zero or negative quantities are
        rejected without modifying the cart.
        """)
    @TableTest("""
        Scenario                                | Catalogue                   | Cart Before  | Product Id | Quantity | Cart After?            | Success? | Message?
        Add new item to empty cart              | [Widget: 9.99]              | [:]          | Widget     | 2        | [Widget: 2]            | true     | Added 2x Widget
        Add a different item alongside another  | [Widget: 9.99, Gadget: 4.50]| [Widget: 1]  | Gadget     | 3        | [Widget: 1, Gadget: 3] | true     | Added 3x Gadget
        Add more of an item already in the cart | [Widget: 9.99]              | [Widget: 1]  | Widget     | 2        | [Widget: 3]            | true     | Added 2x Widget
        Product not in the catalogue            | [Widget: 9.99]              | [:]          | Gadget     | 1        | [:]                    | false    | Unknown product: Gadget
        Zero quantity is rejected                | [Widget: 9.99]              | [:]          | Widget     | 0        | [:]                    | false    | Quantity must be positive
        Negative quantity is rejected            | [Widget: 9.99]              | [Widget: 1]  | Widget     | -2       | [Widget: 1]            | false    | Quantity must be positive
        """)
    void addsItemsFromTheCatalogue(ProductCatalogue catalogue, Cart cartBefore, String productId, int quantity,
                                    Cart cartAfter, boolean success, String message) {
        CartResult result = CartService.addItem(cartBefore, productId, quantity, catalogue);

        assertEquals(success, result.isSuccess());
        assertEquals(message, result.getMessage());
        assertEquals(cartAfter, result.getCart());
    }

    @Description("""
        Removing a product removes the whole line item; partial-quantity
        removal is out of scope. An empty cart is just the boundary case of
        "product not in the cart".
        """)
    @TableTest("""
        Scenario                           | Cart Before            | Product Id | Cart After?  | Success? | Message?
        Remove the only item in the cart   | [Widget: 2]            | Widget     | [:]          | true     | Removed Widget
        Remove one item, others remain     | [Widget: 2, Gadget: 1] | Gadget     | [Widget: 2]  | true     | Removed Gadget
        Remove an item not in the cart     | [Widget: 2]            | Gadget     | [Widget: 2]  | false    | Gadget is not in the cart
        Remove from an empty cart          | [:]                    | Widget     | [:]          | false    | Widget is not in the cart
        """)
    void removesItemsFromTheCart(Cart cartBefore, String productId, Cart cartAfter, boolean success, String message) {
        CartResult result = CartService.removeItem(cartBefore, productId);

        assertEquals(success, result.isSuccess());
        assertEquals(message, result.getMessage());
        assertEquals(cartAfter, result.getCart());
    }

    @Description("""
        Only the coupon code under test is modelled in the store, since
        applyCoupon looks up exactly one code per call. "Coupon In Store"
        encodes what the store holds for that code: a PERCENT/FIXED/PRODUCT
        definition, EXPIRED for an expired coupon, or a blank cell for a code
        the store doesn't recognise.
        """)
    @TableTest("""
        Scenario                                            | Active Coupon Before | Coupon Code | Coupon In Store     | Active Coupon After? | Success? | Message?
        Apply a percentage coupon with none active          |                       | SAVE10      | PERCENT 10          | SAVE10               | true     | Coupon applied
        Fixed coupon replaces an active percentage coupon   | SAVE10                | FIVEOFF     | FIXED 5.00          | FIVEOFF              | true     | Coupon applied
        Product coupon replaces the active coupon           | SAVE10                | WIDGET3     | PRODUCT Widget 3.00 | WIDGET3              | true     | Coupon applied
        Expired coupon does not replace the active coupon   | SAVE10                | OLDCODE     | EXPIRED             | SAVE10               | false    | Coupon expired
        Unknown coupon code does not replace the active one | SAVE10                | FAKE        |                     | SAVE10               | false    | Coupon not found
        Expired coupon with none active                     |                       | OLDCODE     | EXPIRED             |                      | false    | Coupon expired
        Unknown coupon code with none active                |                       | FAKE        |                     |                      | false    | Coupon not found
        """)
    void appliesCouponsToTheCart(String activeCouponBefore, String couponCode, Coupon couponInStore,
                                  String activeCouponAfter, boolean success, String message) {
        Cart cart = Cart.withItems(Map.of()).withActiveCouponCode(activeCouponBefore);
        CouponStore store = code -> Optional.ofNullable(couponInStore);

        CartResult result = CartService.applyCoupon(cart, couponCode, store);

        assertEquals(success, result.isSuccess());
        assertEquals(message, result.getMessage());
        assertEquals(activeCouponAfter, result.getCart().getActiveCouponCode());
    }

    @Description("""
        Checkout only checks cart quantities against available inventory; it
        doesn't need the product catalogue. "Message Mentions?" lists the
        substrings the checkout message must contain — the exact wording of
        the message (beyond naming what's short) isn't specified by the rules.
        """)
    @TableTest("""
        Scenario                                    | Cart Items              | Stock                    | Success? | Message Mentions?
        Empty cart cannot check out                 | [:]                     | [:]                      | false    | [Cart is empty]
        Sufficient stock for a single item          | [Widget: 2]             | [Widget: 5]              | true     | [Order placed]
        Stock exactly matches demand                | [Widget: 5]             | [Widget: 5]              | true     | [Order placed]
        Insufficient stock by one unit               | [Widget: 6]             | [Widget: 5]              | false    | [Widget]
        Insufficient stock for one of several items  | [Widget: 2, Gadget: 5] | [Widget: 5, Gadget: 2]   | false    | [Gadget]
        Insufficient stock for multiple items        | [Widget: 6, Gadget: 5] | [Widget: 5, Gadget: 2]   | false    | [Widget, Gadget]
        Requested product missing from inventory     | [Widget: 1]             | [:]                      | false    | [Widget]
        """)
    void checksOutTheCart(Cart cart, InventoryService inventory, boolean success, List<String> messageMentions) {
        CheckoutResult result = CartService.checkout(cart, inventory);

        assertEquals(success, result.isSuccess());
        for (String fragment : messageMentions) {
            assertTrue(result.getMessage().contains(fragment),
                    () -> "Expected message to mention '" + fragment + "' but was: " + result.getMessage());
        }
    }

    @Description("""
        A product-specific coupon subtracts a single flat amount from the
        cart total when the targeted product is present in the cart
        (assumption); it has no effect otherwise. The total is floored at
        zero regardless of which coupon type produced the discount.
        """)
    @TableTest("""
        Scenario                                     | Catalogue                    | Cart Items             | Active Coupon        | Total?
        No coupon, single item                       | [Widget: 10.00]              | [Widget: 3]            |                       | 30.00
        No coupon, multiple items                     | [Widget: 10.00, Gadget: 5.00]| [Widget: 2, Gadget: 4] |                       | 40.00
        Percentage coupon discounts the whole cart    | [Widget: 10.00]              | [Widget: 2]            | PERCENT 20            | 16.00
        Fixed coupon below the subtotal               | [Widget: 10.00]              | [Widget: 2]            | FIXED 5.00            | 15.00
        Fixed coupon exactly matches the subtotal     | [Widget: 10.00]              | [Widget: 2]            | FIXED 20.00           | 0.00
        Fixed coupon exceeds the subtotal, floored    | [Widget: 10.00]              | [Widget: 2]            | FIXED 25.00           | 0.00
        Product coupon applies to its target product | [Widget: 10.00, Gadget: 5.00]| [Widget: 2, Gadget: 1] | PRODUCT Widget 5.00   | 20.00
        Product coupon ignored when product absent   | [Widget: 10.00, Gadget: 5.00]| [Gadget: 1]            | PRODUCT Widget 5.00   | 5.00
        Empty cart, no coupon                         | [Widget: 10.00]              | [:]                    |                       | 0.00
        Empty cart, any coupon                         | [Widget: 10.00]              | [:]                    | {PERCENT 50, FIXED 5.00} | 0.00
        """)
    void calculatesTheCartTotal(ProductCatalogue catalogue, Cart cart, Coupon activeCoupon, BigDecimal total) {
        BigDecimal result = CartService.calculateTotal(cart, activeCoupon, catalogue);

        assertEquals(0, total.compareTo(result), () -> "Expected " + total + " but was " + result);
    }

    @TypeConverter
    public static ProductCatalogue parseCatalogue(Map<String, BigDecimal> prices) {
        return ProductCatalogue.from(prices == null ? Map.of() : prices);
    }

    @TypeConverter
    public static Cart parseCart(Map<String, Integer> items) {
        return Cart.withItems(items == null ? Map.of() : items);
    }

    @TypeConverter
    public static InventoryService parseInventory(Map<String, Integer> stock) {
        Map<String, Integer> levels = stock == null ? Map.of() : stock;
        return productId -> levels.getOrDefault(productId, 0);
    }

    @TypeConverter
    public static Coupon parseCoupon(String definition) {
        if (definition == null || definition.isBlank()) {
            return null;
        }
        if (definition.equals("EXPIRED")) {
            return new Coupon(CouponType.PERCENT, BigDecimal.ZERO, null, true);
        }
        String[] parts = definition.split(" ");
        return switch (parts[0]) {
            case "PERCENT" -> Coupon.percentage(Integer.parseInt(parts[1]));
            case "FIXED" -> Coupon.fixed(new BigDecimal(parts[1]));
            case "PRODUCT" -> Coupon.productSpecific(parts[1], new BigDecimal(parts[2]));
            default -> throw new IllegalArgumentException("Unknown coupon definition: " + definition);
        };
    }
}
