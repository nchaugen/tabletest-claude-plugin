package com.example.cart;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CartServiceTest {

    private static final CouponStore TEST_COUPONS = CouponStore.of(
            Map.of(
                    "SAVE10", Coupon.percentage(10),
                    "OFF5", Coupon.fixed(new BigDecimal("5.00")),
                    "WIDGET3", Coupon.productSpecific("widget-1", new BigDecimal("3.00"))),
            Set.of("EXPIRED1"));

    @Description("""
            Quantity must be positive; zero or negative quantity is rejected as invalid
            input rather than silently ignored or removing the item.
            """)
    @TableTest("""
            Scenario                            | Cart Before                | Product Id | Quantity | Catalogue                        | Success? | Message?                   | Cart After?
            Add new item to empty cart          | [:]                        | widget-1   | 2        | [widget-1: 9.99]                  | true     | Added 2x widget-1          | [widget-1: 2]
            Increase quantity of existing item  | [widget-1: 2]              | widget-1   | 3        | [widget-1: 9.99]                  | true     | Added 3x widget-1          | [widget-1: 5]
            Add second item alongside first     | [widget-1: 2]              | widget-2   | 1        | [widget-1: 9.99, widget-2: 4.50]  | true     | Added 1x widget-2          | [widget-1: 2, widget-2: 1]
            Unknown product id                  | [widget-1: 2]              | widget-9   | 1        | [widget-1: 9.99]                  | false    | Unknown product: widget-9  | [widget-1: 2]
            Zero quantity rejected              | [widget-1: 2]              | widget-1   | 0        | [widget-1: 9.99]                  | false    | Quantity must be positive  | [widget-1: 2]
            Negative quantity rejected          | [widget-1: 2]              | widget-1   | -1       | [widget-1: 9.99]                  | false    | Quantity must be positive  | [widget-1: 2]
            """)
    void addsItemsToCart(Cart cartBefore, String productId, int quantity, ProductCatalogue catalogue,
                         boolean success, String message, Cart cartAfter) {
        CartResult result = CartService.addItem(cartBefore, productId, quantity, catalogue);

        assertEquals(success, result.success());
        assertEquals(message, result.message());
        assertEquals(cartAfter, result.cart());
    }

    @TableTest("""
            Scenario                       | Cart Before                 | Product Id | Success? | Message?             | Cart After?
            Remove only item in cart       | [widget-1: 2]               | widget-1   | true     | Removed widget-1     | [:]
            Remove one of several items    | [widget-1: 2, widget-2: 1]  | widget-2   | true     | Removed widget-2     | [widget-1: 2]
            Remove item not in cart        | [widget-1: 2]               | widget-9   | false    | widget-9 not in cart | [widget-1: 2]
            Remove from empty cart         | [:]                         | widget-1   | false    | widget-1 not in cart | [:]
            """)
    void removesItemsFromCart(Cart cartBefore, String productId,
                              boolean success, String message, Cart cartAfter) {
        CartResult result = CartService.removeItem(cartBefore, productId);

        assertEquals(success, result.success());
        assertEquals(message, result.message());
        assertEquals(cartAfter, result.cart());
    }

    @Description("""
            TEST_COUPONS fixture: SAVE10 = 10% off cart, OFF5 = $5.00 off cart,
            WIDGET3 = $3.00 off widget-1, EXPIRED1 = expired code, BOGUS = unregistered code.
            Cart items are irrelevant to coupon validation, so every row uses an empty cart
            and only the active coupon code is tracked before/after.
            """)
    @TableTest("""
            Scenario                                     | Active Coupon Before | Coupon Code | Success? | Message?               | Active Coupon After?
            First coupon applied, percentage type        |                       | SAVE10      | true     | Applied coupon SAVE10  | SAVE10
            Valid coupon replaces existing, fixed type   | SAVE10                | OFF5        | true     | Applied coupon OFF5    | OFF5
            Valid coupon replaces existing, product type | OFF5                  | WIDGET3     | true     | Applied coupon WIDGET3 | WIDGET3
            Expired code does not replace active coupon  | SAVE10                | EXPIRED1    | false    | Coupon expired         | SAVE10
            Unknown code does not replace active coupon  | SAVE10                | BOGUS       | false    | Coupon not found       | SAVE10
            """)
    void appliesCoupons(String couponBefore, String couponCode,
                        boolean success, String message, String couponAfter) {
        Cart cart = new Cart(Map.of(), couponBefore);

        CartResult result = CartService.applyCoupon(cart, couponCode, TEST_COUPONS);

        assertEquals(success, result.success());
        assertEquals(message, result.message());
        assertEquals(couponAfter, result.cart().activeCouponCode());
    }

    @TableTest("""
            Scenario                              | Cart Items                 | Available Stock              | Success? | Message?
            Empty cart cannot check out           | [:]                        | [:]                           | false    | Cart is empty
            Stock exactly matches demand          | [widget-1: 2]              | [widget-1: 2]                 | true     | Checkout successful
            Stock exceeds demand                  | [widget-1: 2]              | [widget-1: 10]                | true     | Checkout successful
            Insufficient stock for one item       | [widget-1: 5]              | [widget-1: 2]                 | false    | Insufficient stock for widget-1: requested 5, available 2
            Insufficient stock for several items  | [widget-1: 5, widget-2: 3] | [widget-1: 2, widget-2: 1]    | false    | Insufficient stock for widget-1: requested 5, available 2; widget-2: requested 3, available 1
            """)
    void checksOutCart(Cart cart, InventoryService inventory, boolean success, String message) {
        CheckoutResult result = CartService.checkout(cart, inventory);

        assertEquals(success, result.success());
        assertEquals(message, result.message());
    }

    @Description("""
            Product-specific coupons apply a flat amount once when the target product is
            present in the cart, regardless of its quantity; they have no effect when the
            target product is absent. Open question: should a product-specific discount be
            capped at that product's own line total rather than the whole cart total?
            Assumed no for now - it reduces the cart total like any other discount.
            """)
    @TableTest("""
            Scenario                                          | Cart Items                  | Catalogue                        | Coupon                     | Total?
            No coupon applied                                 | [widget-1: 2]               | [widget-1: 9.99]                 |                            | 19.98
            Percentage off whole cart                         | [widget-1: 2]               | [widget-1: 10.00]                | 10%                        | 18.00
            Product-specific discount on matching product     | [widget-1: 2, widget-2: 1]  | [widget-1: 10.00, widget-2: 5.00] | $3.00 off widget-1         | 22.00
            Product-specific discount, product not in cart    | [widget-2: 1]               | [widget-2: 5.00]                 | $3.00 off widget-1         | 5.00
            Discount just under cart subtotal                 | [widget-1: 2]               | [widget-1: 10.00]                 | $19.99                     | 0.01
            Discount fully covers subtotal, any coupon type    | [widget-1: 2]               | [widget-1: 10.00]                 | {100%, $20.00, $20.00 off widget-1} | 0.00
            Discount exceeds subtotal, floored at zero         | [widget-1: 2]               | [widget-1: 10.00]                 | $25.00                     | 0.00
            """)
    void calculatesCartTotal(Cart cart, ProductCatalogue catalogue, Coupon coupon, BigDecimal total) {
        BigDecimal actual = CartService.calculateTotal(cart, coupon, catalogue);

        assertEquals(0, total.compareTo(actual));
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
    public static Coupon parseCoupon(String value) {
        if (value.endsWith("%")) {
            return Coupon.percentage(Integer.parseInt(value.substring(0, value.length() - 1)));
        }
        int offIndex = value.indexOf(" off ");
        if (offIndex >= 0) {
            BigDecimal amount = new BigDecimal(value.substring(1, offIndex));
            String productId = value.substring(offIndex + " off ".length());
            return Coupon.productSpecific(productId, amount);
        }
        return Coupon.fixed(new BigDecimal(value.substring(1)));
    }
}
