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

public class ShoppingCartTest {

    @Description("""
        Adding a product already in the cart increases its quantity rather than
        replacing it, and an unknown product ID is rejected without changing the
        cart (both design assumptions, since prices always come from the
        catalogue rather than the caller).
        """)
    @TableTest("""
        Scenario                               | Cart Before | Product Id | Quantity | Catalogue                    | Success? | Message?                 | Cart After?
        Adds a new product to an empty cart    | [:]         | widget     | 2        | [widget: 2.50]               | true     | Added 2x widget          | [widget: 2]
        Adds more of an existing product       | [widget: 2] | widget     | 3        | [widget: 2.50]               | true     | Added 3x widget          | [widget: 5]
        Adds a second distinct product         | [widget: 2] | gadget     | 1        | [widget: 2.50, gadget: 9.99] | true     | Added 1x gadget          | [widget: 2, gadget: 1]
        Rejects a product not in the catalogue | [:]         | unknown    | 1        | [widget: 2.50]               | false    | Unknown product: unknown | [:]
        """)
    void addsItemsPricedFromTheCatalogue(Cart cartBefore, String productId, int quantity, ProductCatalogue catalogue,
                                          boolean success, String message, Cart cartAfter) {
        CartResult result = CartService.addItem(cartBefore, productId, quantity, catalogue);
        assertEquals(success, result.success());
        assertEquals(message, result.message());
        assertEquals(cartAfter, result.cart());
    }

    @Description("""
        Removing a product not present in the cart is rejected with an error
        message and leaves the cart unchanged.
        """)
    @TableTest("""
        Scenario                                 | Cart Before            | Product Id | Success? | Message?                 | Cart After?
        Removes an item present in the cart      | [widget: 2, gadget: 1] | widget     | true     | Removed widget           | [gadget: 1]
        Removes the only item, emptying the cart | [widget: 2]            | widget     | true     | Removed widget           | [:]
        Rejects removing an item not in the cart | [gadget: 1]            | widget     | false    | Item not in cart: widget | [gadget: 1]
        """)
    void removesItemsFromTheCart(Cart cartBefore, String productId, boolean success, String message, Cart cartAfter) {
        CartResult result = CartService.removeItem(cartBefore, productId);
        assertEquals(success, result.success());
        assertEquals(message, result.message());
        assertEquals(cartAfter, result.cart());
    }

    @Description("""
        Coupon validity here depends only on whether the code is known and
        unexpired; which discount type the coupon carries does not affect this
        rule (verified separately by the coupon-type table below). A rejected
        code leaves the previously active coupon, if any, in place.
        """)
    @TableTest("""
        Scenario                                                | Cart Before      | Coupon Code | Coupon Store                    | Success? | Message?                | Cart After?
        Applies a valid coupon when none is active              | [:]              | SAVE10      | [SAVE10: valid]                 | true     | Coupon applied: SAVE10  | [coupon: SAVE10]
        Replaces an active coupon with a new valid one          | [coupon: OLD5]   | SAVE10      | [OLD5: valid, SAVE10: valid]    | true     | Coupon applied: SAVE10  | [coupon: SAVE10]
        Rejects an expired coupon and keeps the active one      | [coupon: SAVE10] | OLD10       | [SAVE10: valid, OLD10: expired] | false    | Coupon expired          | [coupon: SAVE10]
        Rejects an unknown coupon code and keeps the active one | [coupon: SAVE10] | BOGUS       | [SAVE10: valid]                 | false    | Coupon not found: BOGUS | [coupon: SAVE10]
        Rejects an expired coupon when none is active           | [:]              | OLD10       | [OLD10: expired]                | false    | Coupon expired          | [:]
        """)
    void appliesCouponsToTheCart(Cart cartBefore, String couponCode, CouponStore couponStore,
                                  boolean success, String message, Cart cartAfter) {
        CartResult result = CartService.applyCoupon(cartBefore, couponCode, couponStore);
        assertEquals(success, result.success());
        assertEquals(message, result.message());
        assertEquals(cartAfter, result.cart());
    }

    @Description("""
        Cart total is the sum of quantity times catalogue price, minus the
        active coupon's discount. Each coupon type computes its discount
        differently: a percentage coupon discounts the whole cart, a fixed
        coupon subtracts a flat amount, and a product-specific coupon
        discounts only its target product's line, once per cart rather than
        per unit (a design assumption, since the amount per unit vs. per line
        is not specified).
        """)
    @TableTest("""
        Scenario                                                  | Cart Items             | Catalogue                     | Coupon         | Total?
        No coupon charges the full price                          | [widget: 2]            | [widget: 10.00]               |                | 20.00
        Percentage coupon discounts the whole cart                | [widget: 2, gadget: 1] | [widget: 10.00, gadget: 5.00] | 10% off        | 22.50
        Fixed amount coupon subtracts a flat sum                  | [widget: 2]            | [widget: 10.00]               | $5 off         | 15.00
        Product-specific coupon discounts only its target         | [widget: 2, gadget: 1] | [widget: 10.00, gadget: 5.00] | widget: $3 off | 22.00
        Product-specific coupon has no effect if absent from cart | [gadget: 1]            | [gadget: 5.00]                | widget: $3 off | 5.00
        """)
    void computesCartTotalByCouponType(Cart cartItems, ProductCatalogue catalogue, Coupon coupon, BigDecimal total) {
        assertEquals(0, total.compareTo(CartService.calculateTotal(cartItems, coupon, catalogue)));
    }

    @Description("""
        Floor behavior is independent of coupon type; demonstrated here with a
        fixed-amount coupon.
        """)
    @TableTest("""
        Scenario                                       | Cart Items  | Catalogue       | Coupon             | Total?
        Discount just below the total leaves a balance | [widget: 1] | [widget: 10.00] | $9 off             | 1.00
        Discount at or beyond the total floors at zero | [widget: 1] | [widget: 10.00] | {$10 off, $15 off} | 0.00
        """)
    void floorsCartTotalAtZero(Cart cartItems, ProductCatalogue catalogue, Coupon coupon, BigDecimal total) {
        assertEquals(0, total.compareTo(CartService.calculateTotal(cartItems, coupon, catalogue)));
    }

    @Description("""
        Checkout failure messages list each short item with the quantity
        requested and the quantity available (a design assumption, since the
        exact wording is not yet specified).
        """)
    @TableTest("""
        Scenario                                                     | Cart Items             | Inventory              | Success? | Message?
        Rejects checkout for an empty cart                           | [:]                    | [:]                    | false    | Cart is empty
        Checks out when requested quantity exactly matches stock     | [widget: 3]            | [widget: 3]            | true     | Checkout successful
        Fails checkout when stock is short by one, detailing the gap | [widget: 3]            | [widget: 2]            | false    | Insufficient stock for widget: requested 3, available 2
        Fails checkout listing every item that is short              | [widget: 5, gadget: 4] | [widget: 2, gadget: 1] | false    | Insufficient stock for widget: requested 5, available 2; gadget: requested 4, available 1
        """)
    void verifiesStockAtCheckout(Cart cartItems, InventoryService inventory, boolean success, String message) {
        CheckoutResult result = CartService.checkout(cartItems, inventory);
        assertEquals(success, result.success());
        assertEquals(message, result.message());
    }

    @TypeConverter
    public static Cart parseCart(Map<String, String> fields) {
        Map<String, Integer> items = new LinkedHashMap<>();
        String couponCode = null;
        for (Map.Entry<String, String> field : fields.entrySet()) {
            if ("coupon".equals(field.getKey())) {
                couponCode = field.getValue();
            } else {
                items.put(field.getKey(), Integer.parseInt(field.getValue()));
            }
        }
        return new Cart(items, couponCode);
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
    public static CouponStore parseCouponStore(Map<String, String> codes) {
        Map<String, Coupon> available = new LinkedHashMap<>();
        Set<String> expired = new LinkedHashSet<>();
        codes.forEach((code, state) -> {
            if ("expired".equals(state)) {
                expired.add(code);
            } else {
                available.put(code, Coupon.percentage(10));
            }
        });
        return CouponStore.of(available, expired);
    }

    @TypeConverter
    public static Coupon parseCoupon(String spec) {
        if (spec.contains(": $")) {
            String[] parts = spec.split(": \\$", 2);
            String productId = parts[0];
            BigDecimal amount = new BigDecimal(parts[1].replace(" off", ""));
            return Coupon.productSpecific(productId, amount);
        }
        if (spec.endsWith("% off")) {
            int percent = Integer.parseInt(spec.replace("% off", ""));
            return Coupon.percentage(percent);
        }
        if (spec.startsWith("$") && spec.endsWith(" off")) {
            BigDecimal amount = new BigDecimal(spec.substring(1, spec.length() - " off".length()));
            return Coupon.fixed(amount);
        }
        throw new IllegalArgumentException("Unrecognized coupon spec: " + spec);
    }
}
