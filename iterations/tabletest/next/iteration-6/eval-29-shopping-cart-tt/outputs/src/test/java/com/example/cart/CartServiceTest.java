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

    @TableTest("""
        Scenario                          | Cart Items Before | Product Id | Quantity | Catalogue                    | Success? | Cart Items After?      | Message?
        New product added to empty cart   | [:]                | Widget     | 2        | [Widget: 9.99]                | true     | [Widget: 2]             | Added 2x Widget
        Existing product quantity adds up | [Widget: 1]        | Widget     | 2        | [Widget: 9.99]                | true     | [Widget: 3]             | Added 2x Widget
        Other cart items are preserved    | [Widget: 1]        | Gadget     | 1        | [Widget: 9.99, Gadget: 5.00]  | true     | [Widget: 1, Gadget: 1]  | Added 1x Gadget
        Unknown product is rejected       | [:]                | Ghost      | 1        | [Widget: 9.99]                | false    | [:]                     | "Product not found: Ghost"
        """)
    void addsItemsByProductIdAndQuantity(Map<String, Integer> cartItemsBefore, String productId, int quantity,
                                          ProductCatalogue catalogue, boolean success,
                                          Map<String, Integer> cartItemsAfter, String message) {
        Cart cart = Cart.withItems(cartItemsBefore);

        CartResult result = CartService.addItem(cart, productId, quantity, catalogue);

        assertEquals(success, result.success());
        assertEquals(message, result.message());
        assertEquals(cartItemsAfter, result.cart().items());
    }

    @TableTest("""
        Scenario                                      | Cart Items Before      | Product Id | Success? | Cart Items After? | Message?
        Removes an item from the cart                 | [Widget: 2]            | Widget     | true     | [:]               | Removed Widget
        Other cart items are preserved                 | [Widget: 2, Gadget: 1] | Widget     | true     | [Gadget: 1]       | Removed Widget
        Removing an item not in the cart is rejected   | [Gadget: 1]            | Widget     | false    | [Gadget: 1]       | "Item not in cart: Widget"
        """)
    void removesItemsFromCart(Map<String, Integer> cartItemsBefore, String productId, boolean success,
                               Map<String, Integer> cartItemsAfter, String message) {
        Cart cart = Cart.withItems(cartItemsBefore);

        CartResult result = CartService.removeItem(cart, productId);

        assertEquals(success, result.success());
        assertEquals(message, result.message());
        assertEquals(cartItemsAfter, result.cart().items());
    }

    @Description("""
        Coupon codes are looked up in a CouponStore; an entry of "EXPIRED" represents
        a code that exists but is no longer valid, distinct from a code that is not
        found in the store at all.
        """)
    @TableTest("""
        Scenario                                                        | Active Coupon Before | Coupons                                | Coupon Code | Success? | Active Coupon After? | Message?
        First coupon is applied                                         |                       | [SAVE10: PERCENT 20]                    | SAVE10      | true     | SAVE10               | "Coupon applied: SAVE10"
        New coupon replaces the active one                              | OLD5                  | [OLD5: FIXED 5.00, SAVE10: PERCENT 20]  | SAVE10      | true     | SAVE10               | "Coupon applied: SAVE10"
        Expired coupon is rejected, active coupon stays unchanged       | OLD5                  | [OLD5: FIXED 5.00, EXP1: EXPIRED]       | EXP1        | false    | OLD5                 | "Coupon expired: EXP1"
        Unknown coupon code is rejected, active coupon stays unchanged  | OLD5                  | [OLD5: FIXED 5.00]                      | BOGUS       | false    | OLD5                 | "Coupon not found: BOGUS"
        """)
    void appliesOneCouponAtATime(String activeCouponBefore, CouponStore couponStore, String couponCode,
                                  boolean success, String activeCouponAfter, String message) {
        Cart cart = Cart.empty().withActiveCouponCode(activeCouponBefore);

        CartResult result = CartService.applyCoupon(cart, couponCode, couponStore);

        assertEquals(success, result.success());
        assertEquals(message, result.message());
        assertEquals(activeCouponAfter, result.cart().activeCouponCode());
    }

    @TableTest("""
        Scenario                                               | Cart Items             | Stock                  | Success? | Message?
        Empty cart cannot check out                            | [:]                    | [:]                    | false    | Cart is empty
        Sufficient stock checks out successfully               | [Widget: 2]            | [Widget: 5]            | true     | Checkout successful
        Stock exactly matching demand checks out successfully  | [Widget: 2]            | [Widget: 2]            | true     | Checkout successful
        Stock one short of demand fails with shortfall detail  | [Widget: 3]            | [Widget: 2]            | false    | "Insufficient stock: Widget (need 3, have 2)"
        Multiple short items are all listed                    | [Widget: 5, Gadget: 3] | [Widget: 2, Gadget: 1] | false    | "Insufficient stock: Widget (need 5, have 2), Gadget (need 3, have 1)"
        """)
    void verifiesStockAtCheckout(Map<String, Integer> cartItems, InventoryService stock, boolean success,
                                  String message) {
        Cart cart = Cart.withItems(cartItems);

        CheckoutResult result = CartService.checkout(cart, stock);

        assertEquals(success, result.success());
        assertEquals(message, result.message());
    }

    @Description("""
        Assumption: a PRODUCT-type coupon's amount is a per-unit discount applied only
        to matching line items (e.g. "$3 off Widget" reduces every Widget unit by $3),
        not a single flat deduction per line regardless of quantity. Open question:
        confirm this against the intended coupon semantics before implementing.
        """)
    @TableTest("""
        Scenario                                                          | Cart Items              | Catalogue                     | Active Coupon         | Total?
        No coupon sums quantity times price across every item             | [Widget: 2, Gadget: 1]  | [Widget: 10.00, Gadget: 5.00] |                        | 25.00
        Percentage coupon discounts the whole cart                        | [Widget: 2]             | [Widget: 10.00]               | PERCENT 20             | 16.00
        Fixed amount coupon discounts the whole cart                      | [Widget: 2]             | [Widget: 10.00]               | FIXED 5.00             | 15.00
        Product coupon discounts only the matching line, per unit         | [Widget: 2, Gadget: 1]  | [Widget: 10.00, Gadget: 5.00] | PRODUCT Widget 3.00    | 19.00
        Product coupon has no effect when its product is absent from cart | [Gadget: 1]             | [Gadget: 5.00]                | PRODUCT Widget 3.00    | 5.00
        Discount is floored at zero rather than going negative            | [Widget: 1]             | [Widget: 5.00]                | FIXED 20.00            | 0.00
        """)
    void calculatesCartTotalAfterCoupon(Map<String, Integer> cartItems, ProductCatalogue catalogue,
                                         Coupon activeCoupon, BigDecimal total) {
        Cart cart = Cart.withItems(cartItems);

        BigDecimal result = CartService.calculateTotal(cart, activeCoupon, catalogue);

        assertEquals(0, total.compareTo(result));
    }

    @TypeConverter
    public static ProductCatalogue parseCatalogue(Map<String, String> prices) {
        Map<String, BigDecimal> parsed = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : prices.entrySet()) {
            parsed.put(entry.getKey(), new BigDecimal(entry.getValue()));
        }
        return ProductCatalogue.from(parsed);
    }

    @TypeConverter
    public static InventoryService parseInventory(Map<String, String> stock) {
        Map<String, Integer> parsed = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : stock.entrySet()) {
            parsed.put(entry.getKey(), Integer.valueOf(entry.getValue()));
        }
        return productId -> parsed.getOrDefault(productId, 0);
    }

    @TypeConverter
    public static CouponStore parseCouponStore(Map<String, String> entries) {
        Map<String, Coupon> available = new LinkedHashMap<>();
        Set<String> expired = new LinkedHashSet<>();
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            if ("EXPIRED".equals(entry.getValue())) {
                expired.add(entry.getKey());
            } else {
                available.put(entry.getKey(), parseCoupon(entry.getValue()));
            }
        }
        return CouponStore.of(available, expired);
    }

    @TypeConverter
    public static Coupon parseCoupon(String spec) {
        if (spec == null || spec.isBlank()) {
            return null;
        }
        String[] parts = spec.split(" ");
        return switch (parts[0]) {
            case "PERCENT" -> Coupon.percentage(Integer.parseInt(parts[1]));
            case "FIXED" -> Coupon.fixed(new BigDecimal(parts[1]));
            case "PRODUCT" -> Coupon.productSpecific(parts[1], new BigDecimal(parts[2]));
            default -> throw new IllegalArgumentException("Unknown coupon spec: " + spec);
        };
    }
}
