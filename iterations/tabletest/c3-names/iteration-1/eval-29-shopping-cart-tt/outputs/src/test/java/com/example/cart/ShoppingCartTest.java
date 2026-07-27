package com.example.cart;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShoppingCartTest {

    @Description("""
        Prices always come from the catalogue, keyed by product id. Adding a product
        already in the cart increases its quantity rather than replacing it.
        """)
    @TableTest("""
        Scenario                            | Cart Before          | Product Id | Quantity | Catalogue                     | Cart After?          | Success? | Message?
        New product added to empty cart     | [:]                  | widget     | 2        | [widget: 5.00]                | [widget: 2]          | true     | Added 2x widget
        Existing product quantity accumulates | [widget: 2]        | widget     | 3        | [widget: 5.00]                | [widget: 5]          | true     | Added 3x widget
        Second distinct product added        | [widget: 2]          | gadget     | 1        | [widget: 5.00, gadget: 9.99]  | [widget: 2, gadget: 1] | true   | Added 1x gadget
        Product not in catalogue is rejected | [:]                  | bogus      | 1        | [widget: 5.00]                | [:]                  | false    | Product not found: bogus
        Non-positive quantity is rejected    | [:]                  | widget     | {0, -1}  | [widget: 5.00]                | [:]                  | false    | Quantity must be positive
        """)
    void shouldAddItemsToCart(Cart cartBefore, String productId, int quantity, ProductCatalogue catalogue,
                               Cart cartAfter, boolean success, String message) {
        CartResult result = CartService.addItem(cartBefore, productId, quantity, catalogue);

        assertEquals(success, result.success());
        assertEquals(message, result.message());
        assertEquals(cartAfter, result.cart());
    }

    @TableTest("""
        Scenario                             | Cart Before            | Product Id | Cart After?           | Success? | Message?
        Remove item, others remain            | [widget: 2, gadget: 1] | widget     | [gadget: 1]           | true     | Removed widget
        Remove last item empties the cart     | [widget: 2]            | widget     | [:]                   | true     | Removed widget
        Remove item not in the cart is an error | [gadget: 1]           | widget     | [gadget: 1]           | false    | widget is not in the cart
        """)
    void shouldRemoveItemsFromCart(Cart cartBefore, String productId, Cart cartAfter, boolean success, String message) {
        CartResult result = CartService.removeItem(cartBefore, productId);

        assertEquals(success, result.success());
        assertEquals(message, result.message());
        assertEquals(cartAfter, result.cart());
    }

    @Description("""
        Cart contents are fixed and irrelevant to coupon selection, so only the active
        coupon code before and after are tracked. Coupon specs use a test-only shorthand:
        PERCENT:<percent>, FIXED:<amount>, PRODUCT:<productId>:<amount>, or EXPIRED.
        A nonexistent or expired code returns an error and leaves the active coupon unchanged.
        """)
    @TableTest("""
        Scenario                                  | Active Before | Coupon Store                                              | Coupon Code | Active After? | Success? | Message?
        Percentage coupon applied with none active |               | [SAVE10: 'PERCENT:10']                                    | SAVE10      | SAVE10        | true     | Coupon applied
        Fixed coupon replaces active percentage    | SAVE10        | [SAVE10: 'PERCENT:10', FLAT5: 'FIXED:5.00']               | FLAT5       | FLAT5         | true     | Coupon applied
        Product-specific coupon applied            |               | [WIDGETOFF: 'PRODUCT:widget:2.00']                        | WIDGETOFF   | WIDGETOFF     | true     | Coupon applied
        Nonexistent code keeps active coupon       | SAVE10        | [SAVE10: 'PERCENT:10']                                    | BOGUS       | SAVE10        | false    | Invalid coupon code
        Expired code keeps active coupon           | SAVE10        | [SAVE10: 'PERCENT:10', OLD10: EXPIRED]                    | OLD10       | SAVE10        | false    | Coupon expired
        Nonexistent code with none active          |               | [SAVE10: 'PERCENT:10']                                    | BOGUS       |               | false    | Invalid coupon code
        """)
    void shouldApplyCoupons(String activeBefore, CouponStore couponStore, String couponCode,
                             String activeAfter, boolean success, String message) {
        Cart cartBefore = Cart.withItems(Map.of("widget", 2)).withActiveCouponCode(activeBefore);

        CartResult result = CartService.applyCoupon(cartBefore, couponCode, couponStore);

        assertEquals(success, result.success());
        assertEquals(message, result.message());
        assertEquals(activeAfter, result.cart().activeCouponCode());
    }

    @Description("""
        Coupon specs use the same PERCENT/FIXED/PRODUCT shorthand as the coupon table above;
        a blank cell means no active coupon. A product-specific discount is multiplied by the
        quantity of the target product in the cart and has no effect when that product is
        absent. Discount is floored at zero when it would exceed the cart subtotal.
        """)
    @TableTest("""
        Scenario                                        | Cart Items             | Catalogue                     | Coupon                | Total?
        No coupon, single item                          | [widget: 3]            | [widget: 10.00]                |                        | 30.00
        No coupon, multiple items                        | [widget: 2, gadget: 1] | [widget: 10.00, gadget: 5.00]  |                        | 25.00
        Percentage discount off whole cart               | [widget: 4]            | [widget: 10.00]                | PERCENT:20             | 32.00
        Fixed amount discount                            | [widget: 4]            | [widget: 10.00]                | FIXED:15.00            | 25.00
        Discount equal to subtotal floors total at zero  | [widget: 2]            | [widget: 10.00]                | FIXED:20.00            | 0.00
        Product-specific discount scales with quantity   | [widget: 2, gadget: 1] | [widget: 10.00, gadget: 5.00]  | PRODUCT:widget:3.00    | 19.00
        Product-specific discount ignored when absent    | [gadget: 1]            | [gadget: 5.00]                  | PRODUCT:widget:3.00    | 5.00
        """)
    void shouldCalculateCartTotal(Cart cartItems, ProductCatalogue catalogue, Coupon coupon, BigDecimal total) {
        BigDecimal actual = CartService.calculateTotal(cartItems, coupon, catalogue);

        assertEquals(0, total.compareTo(actual));
    }

    @Description("""
        Products absent from the inventory map are treated as having zero available stock.
        Shortage details in the message list every short product as "id (need X, have Y)",
        joined with ", ".
        """)
    @TableTest("""
        Scenario                            | Cart Items             | Inventory                    | Success? | Message?
        Stock covers demand                 | [widget: 5]            | [widget: 10]                 | true     | Checkout successful
        Stock exactly matches demand        | [widget: 5]            | [widget: 5]                  | true     | Checkout successful
        Stock short by one unit             | [widget: 5]            | [widget: 4]                  | false    | Insufficient stock: widget (need 5, have 4)
        Multiple items short                | [widget: 5, gadget: 3] | [widget: 4, gadget: 1]        | false    | Insufficient stock: widget (need 5, have 4), gadget (need 3, have 1)
        Product missing from inventory      | [widget: 2]            | [:]                           | false    | Insufficient stock: widget (need 2, have 0)
        Empty cart cannot check out         | [:]                    | {[:], [widget: 5]}            | false    | Cart is empty
        """)
    void shouldVerifyStockAtCheckout(Cart cartItems, InventoryService inventory, boolean success, String message) {
        CheckoutResult result = CartService.checkout(cartItems, inventory);

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
        String[] parts = spec.split(":");
        return switch (parts[0]) {
            case "PERCENT" -> Coupon.percentage(Integer.parseInt(parts[1]));
            case "FIXED" -> Coupon.fixed(new BigDecimal(parts[1]));
            case "PRODUCT" -> Coupon.productSpecific(parts[1], new BigDecimal(parts[2]));
            case "EXPIRED" -> new Coupon(CouponType.PERCENT, BigDecimal.ZERO, null, true);
            default -> throw new IllegalArgumentException("Unknown coupon spec: " + spec);
        };
    }

    @TypeConverter
    public static CouponStore parseCouponStore(Map<String, String> specs) {
        Map<String, Coupon> coupons = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : specs.entrySet()) {
            coupons.put(entry.getKey(), parseCoupon(entry.getValue()));
        }
        return code -> Optional.ofNullable(coupons.get(code));
    }
}
