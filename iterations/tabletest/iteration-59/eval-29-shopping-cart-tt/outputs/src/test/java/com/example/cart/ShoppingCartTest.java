package com.example.cart;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShoppingCartTest {

    @Description("""
        Quantity must be a positive integer; zero is rejected (and, by the same rule,
        so is any negative value). The product name shown in messages comes from the
        catalogue; in this table's fixtures the id doubles as the display name (e.g.
        "Widget"), so the two are always equal.
        """)
    @TableTest("""
        Scenario                                                  | Items Before | Product Id | Quantity | Catalogue                    | Items After?           | Added? | Message?
        First unit of a new product                               | [:]          | Widget     | 2        | [Widget: 9.99]               | [Widget: 2]            | true   | Added 2x Widget
        Adding more of a product already in the cart accumulates  | [Widget: 2]  | Widget     | 3        | [Widget: 9.99]               | [Widget: 5]            | true   | Added 3x Widget
        Adding a different product leaves the first one untouched | [Widget: 2]  | Gadget     | 1        | [Widget: 9.99, Gadget: 4.50] | [Widget: 2, Gadget: 1] | true   | Added 1x Gadget
        Product not found in the catalogue is rejected            | [:]          | Mystery    | 1        | [Widget: 9.99]               | [:]                    | false  | Unknown product: Mystery
        Zero quantity is rejected                                 | [:]          | Widget     | 0        | [Widget: 9.99]               | [:]                    | false  | Quantity must be positive
        """)
    void addsAProductToTheCart(Map<String, Integer> itemsBefore, String productId, int quantity,
                                Map<String, BigDecimal> catalogue, Map<String, Integer> itemsAfter,
                                boolean added, String message) {
        Cart cart = Cart.withItems(itemsBefore);

        CartResult result = CartService.addItem(cart, productId, quantity, ProductCatalogue.from(catalogue));

        assertEquals(added, result.success());
        assertEquals(message, result.message());
        assertEquals(itemsAfter, result.cart().items());
    }

    @TableTest("""
        Scenario                                          | Items Before           | Product Id | Items After? | Removed? | Message?
        Removes the only item in the cart                 | [Widget: 2]            | Widget     | [:]          | true     | Removed Widget
        Removes one item, leaving the others intact       | [Widget: 2, Gadget: 1] | Widget     | [Gadget: 1]  | true     | Removed Widget
        Rejects removing a product that isn't in the cart | [Widget: 2]            | Gadget     | [Widget: 2]  | false    | Gadget is not in your cart
        """)
    void removesAProductFromTheCart(Map<String, Integer> itemsBefore, String productId,
                                     Map<String, Integer> itemsAfter, boolean removed, String message) {
        Cart cart = Cart.withItems(itemsBefore);

        CartResult result = CartService.removeItem(cart, productId);

        assertEquals(removed, result.success());
        assertEquals(message, result.message());
        assertEquals(itemsAfter, result.cart().items());
    }

    @Description("""
        Coupon type never affects whether a code is accepted; the type-specific discount
        amount is exercised in the total-calculation table below. A blank "Coupon Before"
        or "Coupon After" cell means no coupon is active. Cart contents don't affect this
        rule, so items are held empty throughout.
        """)
    @TableTest("""
        Scenario                                                       | Coupon Before | Coupon Code | Available Coupons                     | Expired Codes | Coupon After? | Applied? | Message?
        First coupon activates an empty slot                           |               | SAVE10      | [SAVE10: [type: PERCENT, amount: 10]] | {}            | SAVE10        | true     | Applied coupon SAVE10
        A new valid coupon replaces the active one                     | SAVE10        | FLAT5       | [FLAT5: [type: FIXED, amount: 5]]     | {}            | FLAT5         | true     | Applied coupon FLAT5
        Expired coupon code is rejected, keeping the active coupon     | SAVE10        | OLDCODE     | [:]                                   | {OLDCODE}     | SAVE10        | false    | Coupon expired
        Nonexistent coupon code is rejected, keeping the active coupon | SAVE10        | BOGUS       | [:]                                   | {}            | SAVE10        | false    | Invalid coupon code
        """)
    void appliesACouponCode(String couponBefore, String couponCode, Map<String, Coupon> availableCoupons,
                             Set<String> expiredCodes, String couponAfter, boolean applied, String message) {
        Cart cart = Cart.withItems(Map.of()).withActiveCouponCode(couponBefore);
        CouponStore couponStore = CouponStore.of(availableCoupons, expiredCodes);

        CartResult result = CartService.applyCoupon(cart, couponCode, couponStore);

        assertEquals(applied, result.success());
        assertEquals(message, result.message());
        assertEquals(couponAfter, result.cart().activeCouponCode());
    }

    @Description("""
        Shortage message format: "Insufficient stock: <Product> (have X, need Y)", with
        entries for several short products comma-separated. Products with enough stock
        are left out of the message.
        """)
    @TableTest("""
        Scenario                                                  | Items                  | Stock Levels           | Checked Out? | Message?
        Empty cart cannot check out                               | [:]                    | [:]                    | false        | Cart is empty
        Checkout succeeds when stock exactly matches demand       | [Widget: 3]            | [Widget: 3]            | true         | Checkout complete
        Checkout succeeds with surplus stock across several items | [Widget: 2, Gadget: 1] | [Widget: 5, Gadget: 3] | true         | Checkout complete
        Checkout fails when stock is one short                    | [Widget: 3]            | [Widget: 2]            | false        | Insufficient stock: Widget (have 2, need 3)
        Checkout reports only the item that's short               | [Widget: 3, Gadget: 2] | [Widget: 2, Gadget: 5] | false        | Insufficient stock: Widget (have 2, need 3)
        Checkout lists every short item when several fall short   | [Widget: 3, Gadget: 2] | [Widget: 2, Gadget: 0] | false        | Insufficient stock: Widget (have 2, need 3), Gadget (have 0, need 2)
        """)
    void checksOutTheCart(Map<String, Integer> items, Map<String, Integer> stockLevels,
                           boolean checkedOut, String message) {
        Cart cart = Cart.withItems(items);
        InventoryService inventory = productId -> stockLevels.getOrDefault(productId, 0);

        CheckoutResult result = CartService.checkout(cart, inventory);

        assertEquals(checkedOut, result.success());
        assertEquals(message, result.message());
    }

    @Description("""
        Product-specific coupons subtract a flat amount from that product's line once,
        not per unit. The Coupon passed in already carries its own expired state, set by
        the caller; when true, no discount is applied even though it's the value being
        passed as the active coupon. A blank "Active Coupon" cell means no coupon.
        """)
    @TableTest("""
        Scenario                                                     | Items                  | Catalogue               | Active Coupon                                 | Total?
        No coupon applied, total is the cart subtotal                | [Widget: 2, Gadget: 1] | [Widget: 10, Gadget: 5] |                                               | 25.00
        Percentage coupon discounts the whole cart                   | [Widget: 2]            | [Widget: 10]            | [type: PERCENT, amount: 20]                   | 16.00
        Fixed amount coupon subtracts a flat discount                | [Widget: 2]            | [Widget: 10]            | [type: FIXED, amount: 5]                      | 15.00
        Product-specific coupon discounts only its target product    | [Widget: 2, Gadget: 1] | [Widget: 10, Gadget: 5] | [type: PRODUCT, amount: 3, productId: Widget] | 22.00
        Discount just short of the subtotal leaves a small remainder | [Widget: 1]            | [Widget: 10]            | [type: FIXED, amount: 9.99]                   | 0.01
        Discount equal to the subtotal floors the total at zero      | [Widget: 1]            | [Widget: 10]            | [type: FIXED, amount: 10]                     | 0.00
        Expired active coupon applies no discount                    | [Widget: 2]            | [Widget: 10]            | [type: PERCENT, amount: 50, expired: true]    | 20.00
        """)
    void calculatesTheCartTotal(Map<String, Integer> items, Map<String, BigDecimal> catalogue,
                                 Coupon activeCoupon, BigDecimal total) {
        Cart cart = Cart.withItems(items);

        BigDecimal result = CartService.calculateTotal(cart, activeCoupon, ProductCatalogue.from(catalogue));

        assertEquals(0, total.compareTo(result));
    }

    @TypeConverter
    public static Coupon parseCoupon(Map<String, String> fields) {
        CouponType type = CouponType.valueOf(fields.get("type"));
        BigDecimal amount = new BigDecimal(fields.get("amount"));
        boolean expired = Boolean.parseBoolean(fields.getOrDefault("expired", "false"));
        Coupon coupon = switch (type) {
            case PERCENT -> Coupon.percentage(amount.intValue());
            case FIXED -> Coupon.fixed(amount);
            case PRODUCT -> Coupon.productSpecific(fields.get("productId"), amount);
        };
        return expired ? new Coupon(coupon.type(), coupon.amount(), coupon.targetProductId(), true) : coupon;
    }
}
