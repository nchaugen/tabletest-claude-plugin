package com.example.cart;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CartServiceTest {

    @Description("""
        Price is resolved from the catalogue by product id; the customer never
        supplies a price. Assumes adding a product already in the cart increases
        its existing quantity rather than replacing it.
        """)
    @TableTest("""
        Scenario                                          | Cart Before   | Product Id | Quantity | Catalogue                  | Success? | Cart After?              | Message?
        New product added to an empty cart                | [:]           | widget     | 2        | [widget: 9.99]             | true     | [widget: 2]              | Added 2x widget
        Additional quantity of a product already in cart  | [widget: 2]   | widget     | 3        | [widget: 9.99]             | true     | [widget: 5]              | Added 3x widget
        Different product added alongside an existing one | [widget: 2]   | gadget     | 1        | [widget: 9.99, gadget: 4.50] | true   | [widget: 2, gadget: 1]   | Added 1x gadget
        Product not found in the catalogue                | [:]           | bogus      | 1        | [widget: 9.99]             | false    | [:]                      | "Unknown product: bogus"
        """)
    void addsAnItemToTheCartPricingItFromTheCatalogue(
            Cart cartBefore, String productId, int quantity, ProductCatalogue catalogue,
            boolean success, Map<String, Integer> cartAfter, String message) {
        CartResult result = CartService.addItem(cartBefore, productId, quantity, catalogue);

        assertEquals(success, result.success());
        assertEquals(cartAfter, result.cart().getItems());
        assertEquals(message, result.message());
    }

    @TableTest("""
        Scenario                                       | Cart Before             | Product Id | Success? | Cart After?    | Message?
        Item present in the cart                       | [widget: 2]             | widget     | true     | [:]            | Removed widget
        One of several items in the cart                | [widget: 2, gadget: 1]  | widget     | true     | [gadget: 1]    | Removed widget
        Item missing from an empty cart                 | [:]                     | widget     | false    | [:]            | widget is not in the cart
        Item never added to a nonempty cart              | [gadget: 1]             | widget     | false    | [gadget: 1]    | widget is not in the cart
        """)
    void removesAnItemFromTheCart(
            Cart cartBefore, String productId, boolean success, Map<String, Integer> cartAfter, String message) {
        CartResult result = CartService.removeItem(cartBefore, productId);

        assertEquals(success, result.success());
        assertEquals(cartAfter, result.cart().getItems());
        assertEquals(message, result.message());
    }

    @Description("""
        Coupon codes are validated against the coupon store. Only one coupon can be
        active at a time, and applying a new valid code replaces it. Invalid or
        expired codes are rejected without disturbing whichever coupon, if any, was
        already active.
        """)
    @TableTest("""
        Scenario                                                    | Coupons                        | Active Before | Code Entered | Success? | Active After? | Message?
        No coupon active yet                                        | [SAVE10: 10%]                  |               | SAVE10       | true     | SAVE10        | Applied coupon SAVE10
        A different coupon already active                          | [SAVE10: 10%, FLAT5: $5]       | SAVE10        | FLAT5        | true     | FLAT5         | Applied coupon FLAT5
        Code not found in the coupon store, coupon already active   | [SAVE10: 10%]                  | SAVE10        | BOGUS        | false    | SAVE10        | "Invalid coupon code: BOGUS"
        Code belongs to an expired coupon, coupon already active    | [SAVE10: 10%, OLD10: expired]  | SAVE10        | OLD10        | false    | SAVE10        | "Coupon expired: OLD10"
        Code not found in the coupon store, no coupon active yet    | [:]                             |               | BOGUS        | false    |               | "Invalid coupon code: BOGUS"
        """)
    void appliesACouponCodeReplacingAnyPreviouslyActiveOne(
            CouponStore couponStore, String activeBefore, String codeEntered,
            boolean success, String activeAfter, String message) {
        Cart cart = Cart.empty().withActiveCouponCode(activeBefore);

        CartResult result = CartService.applyCoupon(cart, codeEntered, couponStore);

        assertEquals(success, result.success());
        assertEquals(activeAfter, result.cart().getActiveCouponCode());
        assertEquals(message, result.message());
    }

    @Description("""
        Coupon notation: "10%" is a percentage off the whole cart, "$5" is a fixed
        amount off the whole cart, and "$3 off widget" is a discount on a specific
        product.
        """)
    @TableTest("""
        Scenario                                            | Cart Items              | Catalogue                    | Coupon           | Total?
        Single product, no coupon                           | [widget: 2]             | [widget: 9.99]                |                  | 19.98
        Multiple products, no coupon                        | [widget: 2, gadget: 3]  | [widget: 10.00, gadget: 2.00] |                  | 26.00
        Percentage-off coupon active                        | [widget: 2]             | [widget: 10.00]               | 10%              | 18.00
        Fixed-amount coupon below the subtotal               | [widget: 2]             | [widget: 10.00]               | $5               | 15.00
        Fixed-amount coupon equal to the subtotal            | [widget: 1]             | [widget: 5.00]                | $5               | 0.00
        Fixed-amount coupon exceeding the subtotal            | [widget: 1]             | [widget: 5.00]                | $20              | 0.00
        Product-specific coupon targeting an item in the cart | [widget: 2, gadget: 1] | [widget: 10.00, gadget: 5.00] | $3 off widget    | 22.00
        Product-specific coupon targeting an item not in the cart | [gadget: 1]        | [gadget: 5.00]                | $3 off widget    | 5.00
        """)
    void calculatesTheCartTotalFromItemPricesAndTheActiveCouponsDiscount(
            Map<String, Integer> items, ProductCatalogue catalogue, Coupon coupon, BigDecimal total) {
        Cart cart = Cart.withItems(items);

        BigDecimal result = CartService.calculateTotal(cart, coupon, catalogue);

        assertEquals(0, total.compareTo(result));
    }

    @Description("""
        When more than one product is short, the message lists each shortfall in
        the order the items were added to the cart.
        """)
    @TableTest("""
        Scenario                                  | Cart Items              | Available Stock         | Success? | Message?
        Cart has no items                         | [:]                     | [:]                     | false    | Cart is empty
        Stock exceeds the quantity needed          | [widget: 2]             | [widget: 5]             | true     | Checkout successful
        Stock exactly matches the quantity needed  | [widget: 3]             | [widget: 3]             | true     | Checkout successful
        Stock falls short of the quantity needed   | [widget: 3]             | [widget: 2]             | false    | "Insufficient stock for widget: need 3, have 2"
        Stock falls short for more than one product | [widget: 5, gadget: 3] | [widget: 2, gadget: 1]  | false    | "Insufficient stock for widget: need 5, have 2; Insufficient stock for gadget: need 3, have 1"
        """)
    void verifiesStockAvailabilityAtCheckout(
            Map<String, Integer> cartItems, InventoryService availableStock, boolean success, String message) {
        Cart cart = Cart.withItems(cartItems);

        CheckoutResult result = CartService.checkout(cart, availableStock);

        assertEquals(success, result.success());
        assertEquals(message, result.message());
    }

    @TypeConverter
    public static Cart parseCart(Map<String, Integer> items) {
        return Cart.withItems(items);
    }

    @TypeConverter
    public static ProductCatalogue parseCatalogue(Map<String, BigDecimal> prices) {
        return ProductCatalogue.from(prices);
    }

    @TypeConverter
    public static InventoryService parseInventory(Map<String, Integer> stock) {
        return productId -> stock.getOrDefault(productId, 0);
    }

    @TypeConverter
    public static Coupon parseCoupon(String spec) {
        if (spec.endsWith("%")) {
            return Coupon.percentage(Integer.parseInt(spec.substring(0, spec.length() - 1)));
        }
        if (spec.contains(" off ")) {
            String[] parts = spec.split(" off ", 2);
            return Coupon.productSpecific(parts[1], new BigDecimal(parts[0].substring(1)));
        }
        return Coupon.fixed(new BigDecimal(spec.substring(1)));
    }

    @TypeConverter
    public static CouponStore parseCouponStore(Map<String, String> specs) {
        Map<String, Coupon> available = new LinkedHashMap<>();
        Set<String> expiredCodes = new LinkedHashSet<>();
        for (Map.Entry<String, String> entry : specs.entrySet()) {
            if ("expired".equals(entry.getValue())) {
                expiredCodes.add(entry.getKey());
            } else {
                available.put(entry.getKey(), parseCoupon(entry.getValue()));
            }
        }
        return CouponStore.of(available, expiredCodes);
    }
}
