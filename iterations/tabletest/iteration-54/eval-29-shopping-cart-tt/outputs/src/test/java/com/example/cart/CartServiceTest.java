package com.example.cart;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CartServiceTest {

    private static final ProductCatalogue ADD_ITEM_CATALOGUE = productId -> switch (productId) {
        case "widget" -> Optional.of(new Product("widget", "Widget", new BigDecimal("9.99")));
        case "gadget" -> Optional.of(new Product("gadget", "Gadget", new BigDecimal("4.50")));
        default -> Optional.empty();
    };

    private static final ProductCatalogue TOTAL_CATALOGUE = ProductCatalogue.from(Map.of(
            "widget", new BigDecimal("10.00"),
            "gadget", new BigDecimal("5.00")
    ));

    private static final CouponStore COUPON_STORE = CouponStore.of(
            Map.of(
                    "SAVE10", Coupon.percentage(10),
                    "OFF5", Coupon.fixed(BigDecimal.valueOf(5))
            ),
            Set.of("EXPIRED10")
    );

    @DisplayName("Adds an item priced from the catalogue")
    @Description("""
            Catalogue fixture for this table: widget = $9.99 ("Widget"), gadget = $4.50 ("Gadget").
            "doesnotexist" is not registered in the catalogue.
            Assumption: the added-quantity message reports the quantity just added, not the
            line's resulting total.
            """)
    @TableTest("""
            Scenario                                       | Cart Before | Product Id   | Quantity | Cart After?            | Success? | Message?
            New item added to an empty cart                | [:]         | widget       | 2        | [widget: 2]            | true     | Added 2x Widget
            Adding more increases the existing line        | [widget: 1] | widget       | 2        | [widget: 3]            | true     | Added 2x Widget
            Adding a different item keeps the existing one | [widget: 1] | gadget       | 1        | [widget: 1, gadget: 1] | true     | Added 1x Gadget
            Product is not in the catalogue                | [widget: 1] | doesnotexist | 1        | [widget: 1]            | false    | Unknown product: doesnotexist
            """)
    void addsItemPricedFromCatalogue(Cart cartBefore, String productId, int quantity,
                                      Map<String, Integer> cartAfter, boolean success, String message) {
        CartResult result = CartService.addItem(cartBefore, productId, quantity, ADD_ITEM_CATALOGUE);

        assertEquals(success, result.success());
        assertEquals(message, result.message());
        assertEquals(cartAfter, result.cart().items());
    }

    @DisplayName("Removes an item from the cart")
    @Description("""
            Assumption: removing an item removes the whole line regardless of its quantity,
            since the operation takes no quantity argument.
            """)
    @TableTest("""
            Scenario                          | Cart Before            | Product Id | Cart After? | Success? | Message?
            Removes the only item in the cart | [widget: 2]            | widget     | [:]         | true     | Removed widget
            Removes one item, others remain   | [widget: 2, gadget: 1] | widget     | [gadget: 1] | true     | Removed widget
            Item is not in the cart           | [gadget: 1]            | widget     | [gadget: 1] | false    | Item not in cart: widget
            """)
    void removesItemFromCart(Cart cartBefore, String productId, Map<String, Integer> cartAfter,
                              boolean success, String message) {
        CartResult result = CartService.removeItem(cartBefore, productId);

        assertEquals(success, result.success());
        assertEquals(message, result.message());
        assertEquals(cartAfter, result.cart().items());
    }

    @DisplayName("Applies a coupon code, replacing any coupon already active")
    @Description("""
            Coupon store fixture: SAVE10 and OFF5 are valid; EXPIRED10 is expired; BOGUS is not
            registered. Cart items do not affect this rule and are held empty throughout.
            """)
    @TableTest("""
            Scenario                                             | Active Coupon Before | Coupon Code | Active Coupon After? | Success? | Message?
            Applies a coupon when none is active                 |                      | SAVE10      | SAVE10               | true     | Coupon applied: SAVE10
            A new valid code replaces the active coupon          | SAVE10               | OFF5        | OFF5                 | true     | Coupon applied: OFF5
            An expired code is rejected, active coupon unchanged | SAVE10               | EXPIRED10   | SAVE10               | false    | Coupon expired
            An unknown code is rejected, active coupon unchanged | SAVE10               | BOGUS       | SAVE10               | false    | Coupon not found
            """)
    void appliesCouponReplacingActive(String activeCouponBefore, String couponCode, String activeCouponAfter,
                                       boolean success, String message) {
        Cart cart = Cart.empty().withActiveCouponCode(activeCouponBefore);

        CartResult result = CartService.applyCoupon(cart, couponCode, COUPON_STORE);

        assertEquals(success, result.success());
        assertEquals(message, result.message());
        assertEquals(activeCouponAfter, result.cart().activeCouponCode());
    }

    @DisplayName("Checks out a cart after verifying stock")
    @Description("""
            Assumption: an insufficient-stock message lists every short item as
            "productId requested X, available Y", joined with "; " when several items are short.
            """)
    @TableTest("""
            Scenario                                    | Cart                   | Stock                  | Success? | Message?
            An empty cart cannot check out              | [:]                    | [:]                    | false    | Cart is empty
            Stock exactly covers the requested quantity | [widget: 2]            | [widget: 2]            | true     | Checkout successful
            Requested quantity exceeds available stock  | [widget: 5]            | [widget: 2]            | false    | Insufficient stock: widget requested 5, available 2
            Several items are short at once             | [widget: 5, gadget: 3] | [widget: 2, gadget: 1] | false    | Insufficient stock: widget requested 5, available 2; gadget requested 3, available 1
            """)
    void checksOutCartAfterVerifyingStock(Cart cart, InventoryService stock, boolean success, String message) {
        CheckoutResult result = CartService.checkout(cart, stock);

        assertEquals(success, result.success());
        assertEquals(message, result.message());
    }

    @DisplayName("Calculates the cart total after applying the active coupon")
    @Description("""
            Catalogue fixture for this table: widget = $10.00, gadget = $5.00.
            """)
    @TableTest("""
            Scenario                                                 | Cart                   | Coupon        | Total?
            No coupon active, total is the sum of the line items     | [widget: 2]            |               | 20.00
            Percentage coupon discounts the whole cart               | [widget: 2]            | 10% off       | 18.00
            Fixed-amount coupon discounts the whole cart             | [widget: 1, gadget: 1] | $5 off        | 10.00
            Fixed-amount coupon larger than the total floors at zero | [widget: 1]            | $50 off       | 0.00
            Product coupon discounts only its targeted product       | [widget: 1, gadget: 1] | $3 off widget | 12.00
            Product coupon has no effect when its target is absent   | [gadget: 2]            | $3 off widget | 10.00
            """)
    void calculatesTotalAfterDiscount(Cart cart, Coupon coupon, BigDecimal total) {
        BigDecimal actual = CartService.calculateTotal(cart, coupon, TOTAL_CATALOGUE);

        assertEquals(0, total.compareTo(actual));
    }

    @TypeConverter
    public static Cart parseCart(Map<String, Integer> items) {
        return Cart.withItems(items);
    }

    @TypeConverter
    public static InventoryService parseInventory(Map<String, Integer> stock) {
        return productId -> stock.getOrDefault(productId, 0);
    }

    @TypeConverter
    public static Coupon parseCoupon(String value) {
        if (value.endsWith("% off")) {
            int percent = Integer.parseInt(value.substring(0, value.indexOf('%')));
            return Coupon.percentage(percent);
        }
        if (value.contains(" off ")) {
            String[] parts = value.split(" off ");
            return Coupon.productSpecific(parts[1], new BigDecimal(parts[0].substring(1)));
        }
        return Coupon.fixed(new BigDecimal(value.substring(1, value.indexOf(" off"))));
    }
}
