package com.example.cart;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

public class CartServiceTest {

    @TableTest("""
        Scenario                     | Items Before   | Product Id | Quantity | Catalogue                        | Items After?                 | Message?
        Adds a new product           | [:]             | widget-1   | 2        | [widget-1: 9.99]                  | [widget-1: 2]                 | Added 2x widget-1
        Increases existing quantity  | [widget-1: 2]   | widget-1   | 3        | [widget-1: 9.99]                  | [widget-1: 5]                 | Added 3x widget-1
        Adds a second distinct product | [widget-1: 2] | gadget-2   | 1        | [widget-1: 9.99, gadget-2: 4.50]  | [widget-1: 2, gadget-2: 1]    | Added 1x gadget-2
        Unknown product id           | [widget-1: 2]   | bogus      | 1        | [widget-1: 9.99]                  | [widget-1: 2]                 | "Unknown product: bogus"
        """)
    void addsItemsPricedFromCatalogue(Map<String, Integer> itemsBefore, String productId, int quantity,
                                       Map<String, BigDecimal> catalogue,
                                       Map<String, Integer> itemsAfter, String message) {
        Cart cart = Cart.withItems(itemsBefore);

        CartResult result = CartService.addItem(cart, productId, quantity, ProductCatalogue.from(catalogue));

        assertEquals(message, result.message());
        assertEquals(itemsAfter, result.cart().items());
    }

    @TableTest("""
        Scenario                      | Items Before               | Product Id | Items After?   | Message?
        Removes an existing item      | [widget-1: 2]                | widget-1   | [:]             | Removed widget-1
        Removes one of several items  | [widget-1: 2, gadget-2: 1]   | gadget-2   | [widget-1: 2]   | Removed gadget-2
        Item not in cart              | [widget-1: 2]                | bogus      | [widget-1: 2]   | "Item not in cart: bogus"
        Empty cart                    | [:]                           | widget-1   | [:]             | "Item not in cart: widget-1"
        """)
    void removesItemsFromCart(Map<String, Integer> itemsBefore, String productId,
                               Map<String, Integer> itemsAfter, String message) {
        Cart cart = Cart.withItems(itemsBefore);

        CartResult result = CartService.removeItem(cart, productId);

        assertEquals(message, result.message());
        assertEquals(itemsAfter, result.cart().items());
    }

    @TableTest("""
        Scenario                          | Existing Active Code | New Code | New Coupon                          | Code Expired In Store | Active Code After? | Message?
        First coupon applied              |                       | SAVE10   | {10% off, $5 off, $3 off widget-1}  | false                  | SAVE10              | "Coupon applied: SAVE10"
        Replaces an existing coupon       | SAVE10                | FLAT5    | $5 off                              | false                  | FLAT5               | "Coupon applied: FLAT5"
        Expired code does not replace     | SAVE10                | OLDCODE  |                                     | true                   | SAVE10              | Coupon expired
        Nonexistent code does not replace | SAVE10                | BOGUS    |                                     | false                  | SAVE10              | Invalid coupon code
        """)
    void appliesCouponReplacingActiveOne(String existingActiveCode, String newCode, Coupon newCoupon,
                                          boolean codeExpiredInStore,
                                          String activeCodeAfter, String message) {
        Cart cart = Cart.empty().withActiveCouponCode(existingActiveCode);
        Map<String, Coupon> available = newCoupon == null ? Map.of() : Map.of(newCode, newCoupon);
        Set<String> expired = codeExpiredInStore ? Set.of(newCode) : Set.of();
        CouponStore store = CouponStore.of(available, expired);

        CartResult result = CartService.applyCoupon(cart, newCode, store);

        assertEquals(message, result.message());
        assertEquals(activeCodeAfter, result.cart().activeCouponCode());
    }

    @TableTest("""
        Scenario                                 | Cart Items                  | Stock                       | Success? | Message?
        Empty cart cannot check out               | [:]                          | [:]                          | false    | Cart is empty
        Sufficient stock for all items            | [widget-1: 2]                | [widget-1: 5]                | true     | Checkout successful
        Insufficient stock for one product        | [widget-1: 5]                | [widget-1: 2]                | false    | "Insufficient stock for widget-1: requested 5, available 2"
        Insufficient stock for several products   | [widget-1: 5, gadget-2: 3]   | [widget-1: 2, gadget-2: 1]   | false    | "Insufficient stock for widget-1: requested 5, available 2; Insufficient stock for gadget-2: requested 3, available 1"
        """)
    void checksStockLevelsBeforeCheckout(Map<String, Integer> cartItems, Map<String, Integer> stock,
                                          boolean success, String message) {
        Cart cart = Cart.withItems(cartItems);
        InventoryService inventory = productId -> stock.getOrDefault(productId, 0);

        CheckoutResult result = CartService.checkout(cart, inventory);

        assertEquals(success, result.isSuccess());
        assertEquals(message, result.getMessage());
    }

    @TableTest("""
        Scenario       | Cart Items                  | Catalogue                          | Total?
        Single item    | [widget-1: 2]                | [widget-1: 9.99]                    | 19.98
        Multiple items | [widget-1: 2, gadget-2: 3]   | [widget-1: 9.99, gadget-2: 4.00]    | 31.98
        Empty cart     | [:]                           | [:]                                  | 0.00
        """)
    void calculatesTotalWithoutCoupon(Map<String, Integer> cartItems, Map<String, BigDecimal> catalogue,
                                       BigDecimal total) {
        Cart cart = Cart.withItems(cartItems);

        BigDecimal result = CartService.calculateTotal(cart, null, ProductCatalogue.from(catalogue));

        assertEquals(0, total.compareTo(result));
    }

    @TableTest("""
        Scenario                      | Cart Items      | Catalogue           | Percent Off | Total?
        Percentage off whole cart     | [widget-1: 2]    | [widget-1: 10.00]   | 10          | 18.00
        Full discount floors at zero  | [widget-1: 2]    | [widget-1: 10.00]   | 100         | 0.00
        """)
    void calculatesTotalWithPercentageCoupon(Map<String, Integer> cartItems, Map<String, BigDecimal> catalogue,
                                              int percentOff, BigDecimal total) {
        Cart cart = Cart.withItems(cartItems);

        BigDecimal result = CartService.calculateTotal(cart, Coupon.percentage(percentOff),
                ProductCatalogue.from(catalogue));

        assertEquals(0, total.compareTo(result));
    }

    @TableTest("""
        Scenario                                    | Cart Items      | Catalogue           | Fixed Off | Total?
        Discount less than subtotal                 | [widget-1: 2]    | [widget-1: 10.00]   | 5.00      | 15.00
        Discount equal to subtotal                   | [widget-1: 2]    | [widget-1: 10.00]   | 20.00     | 0.00
        Discount exceeds subtotal, floors at zero    | [widget-1: 2]    | [widget-1: 10.00]   | 25.00     | 0.00
        """)
    void calculatesTotalWithFixedCoupon(Map<String, Integer> cartItems, Map<String, BigDecimal> catalogue,
                                         BigDecimal fixedOff, BigDecimal total) {
        Cart cart = Cart.withItems(cartItems);

        BigDecimal result = CartService.calculateTotal(cart, Coupon.fixed(fixedOff), ProductCatalogue.from(catalogue));

        assertEquals(0, total.compareTo(result));
    }

    @TableTest("""
        Scenario                                    | Cart Items                 | Catalogue                          | Target Product | Discount Amount | Total?
        Discount applies only to target product     | [widget-1: 2, gadget-2: 1]  | [widget-1: 10.00, gadget-2: 5.00]   | widget-1        | 3.00             | 22.00
        Discount floors at zero when it exceeds cart| [widget-1: 1]               | [widget-1: 10.00]                   | widget-1        | 15.00            | 0.00
        Target product not in cart has no effect    | [gadget-2: 1]               | [widget-1: 10.00, gadget-2: 5.00]   | widget-1        | 3.00             | 5.00
        """)
    void calculatesTotalWithProductSpecificCoupon(Map<String, Integer> cartItems, Map<String, BigDecimal> catalogue,
                                                   String targetProduct, BigDecimal discountAmount, BigDecimal total) {
        Cart cart = Cart.withItems(cartItems);
        Coupon coupon = Coupon.productSpecific(targetProduct, discountAmount);

        BigDecimal result = CartService.calculateTotal(cart, coupon, ProductCatalogue.from(catalogue));

        assertEquals(0, total.compareTo(result));
    }

    @TypeConverter
    public static Coupon parseCoupon(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        if (value.endsWith("% off")) {
            int percent = Integer.parseInt(value.substring(0, value.indexOf('%')));
            return Coupon.percentage(percent);
        }
        if (value.startsWith("$")) {
            String rest = value.substring(1);
            int offIndex = rest.indexOf(" off");
            BigDecimal amount = new BigDecimal(rest.substring(0, offIndex));
            String targetProductId = rest.substring(offIndex + " off".length()).trim();
            return targetProductId.isEmpty()
                    ? Coupon.fixed(amount)
                    : Coupon.productSpecific(targetProductId, amount);
        }
        throw new IllegalArgumentException("Unrecognized coupon notation: " + value);
    }
}
