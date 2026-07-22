package com.example.cart;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShoppingCartTest {

    @Description("""
        Prices always come from the catalogue keyed by product id; the customer only supplies id and quantity.
        Assumption: adding a product already in the cart accumulates its quantity rather than replacing it.
        Assumption: a non-positive quantity is rejected without changing the cart.
        """)
    @TableTest("""
        Scenario                           | Cart Before  | Product Id | Quantity | Catalogue                    | Cart After?            | Message?
        Add new product to empty cart      | [:]          | widget     | 2        | [widget: 5.00]               | [widget: 2]            | Added 2x widget
        Add same product again accumulates | [widget: 2]  | widget     | 3        | [widget: 5.00]               | [widget: 5]            | Added 3x widget
        Add second distinct product        | [widget: 2]  | gadget     | 1        | [widget: 5.00, gadget: 9.00] | [widget: 2, gadget: 1] | Added 1x gadget
        Unknown product id                 | [:]          | ghost      | 1        | [widget: 5.00]               | [:]                    | "Unknown product: ghost"
        Non-positive quantity rejected     | [widget: 2]  | widget     | {0, -1}  | [widget: 5.00]               | [widget: 2]            | Quantity must be positive
        """)
    void addsItemsToCart(Map<String, Integer> cartBefore, String productId, int quantity,
                          ProductCatalogue catalogue, Map<String, Integer> cartAfter, String message) {
        CartResult result = CartService.addItem(Cart.withItems(cartBefore), productId, quantity, catalogue);
        assertEquals(message, result.message());
        assertEquals(cartAfter, result.cart().items());
    }

    @TableTest("""
        Scenario                       | Cart Before             | Product Id | Cart After?      | Message?
        Remove existing item           | [widget: 2, gadget: 1]  | widget     | [gadget: 1]      | Removed widget
        Remove item not in cart        | [widget: 2]             | gadget     | [widget: 2]      | gadget not in cart
        Remove last item empties cart  | [widget: 2]             | widget     | [:]              | Removed widget
        """)
    void removesItemsFromCart(Map<String, Integer> cartBefore, String productId,
                               Map<String, Integer> cartAfter, String message) {
        CartResult result = CartService.removeItem(Cart.withItems(cartBefore), productId);
        assertEquals(message, result.message());
        assertEquals(cartAfter, result.cart().items());
    }

    @Description("""
        Only one coupon may be active at a time; entering a new valid code replaces it.
        Expired or unknown codes return an error and leave the previously active coupon (if any) unchanged.
        Cart contents don't affect coupon application, so every row uses an otherwise-empty cart.
        """)
    @TableTest("""
        Scenario                                         | Active Coupon Before | Coupon Code | Coupon Store                       | Active Coupon After? | Message?
        Apply valid code with no active coupon           |                       | SAVE10      | [SAVE10: valid]                     | SAVE10                | Coupon applied
        Valid code replaces existing coupon               | SAVE10                | WELCOME20   | [SAVE10: valid, WELCOME20: valid]   | WELCOME20             | Coupon applied
        Expired code does not replace active coupon       | SAVE10                | OLDCODE     | [SAVE10: valid, OLDCODE: expired]   | SAVE10                | Coupon expired
        Expired code with no active coupon stays empty    |                       | OLDCODE     | [OLDCODE: expired]                  |                       | Coupon expired
        Nonexistent code does not replace active coupon   | SAVE10                | GHOST       | [SAVE10: valid]                     | SAVE10                | Invalid coupon code
        """)
    void appliesCouponCodes(String activeCouponBefore, String couponCode, CouponStore couponStore,
                             String activeCouponAfter, String message) {
        Cart cart = Cart.empty().withActiveCouponCode(activeCouponBefore);
        CartResult result = CartService.applyCoupon(cart, couponCode, couponStore);
        assertEquals(message, result.message());
        assertEquals(activeCouponAfter, result.cart().activeCouponCode());
    }

    @Description("""
        Checkout verifies each cart line against available inventory; a shortfall lists every short product
        with its available and requested quantities. An empty cart cannot be checked out regardless of inventory.
        """)
    @TableTest("""
        Scenario                              | Cart Items              | Inventory                | Success? | Message?
        Sufficient stock for all items        | [widget: 2, gadget: 1]  | [widget: 5, gadget: 3]   | true     | Checkout complete
        Insufficient stock for one item       | [widget: 3]             | [widget: 1]              | false    | "Insufficient stock: widget (1 available, 3 requested)"
        Insufficient stock for multiple items | [widget: 3, gadget: 2]  | [widget: 1, gadget: 0]   | false    | "Insufficient stock: widget (1 available, 3 requested), gadget (0 available, 2 requested)"
        Empty cart cannot check out           | [:]                     | [widget: 5]              | false    | Cart is empty
        """)
    void checksOutCart(Map<String, Integer> cartItems, InventoryService inventory,
                        boolean success, String message) {
        CheckoutResult result = CartService.checkout(Cart.withItems(cartItems), inventory);
        assertEquals(success, result.success());
        assertEquals(message, result.message());
    }

    @Description("""
        Total = sum(quantity x catalogue price) minus the coupon discount, floored at zero.
        Percentage coupons discount the whole cart; fixed coupons subtract a flat amount; product-specific
        coupons subtract a flat amount only when the target product is present in the cart (assumption: the
        discount is a flat amount tied to the product's presence, not multiplied by its quantity).
        """)
    @TableTest("""
        Scenario                                                | Cart Items              | Catalogue                    | Coupon           | Total?
        No coupon sums quantity times price                     | [widget: 2, gadget: 1]  | [widget: 5.00, gadget: 9.00] |                  | 19.00
        Percentage off whole cart                                | [widget: 2]             | [widget: 10.00]              | 20% off          | 16.00
        Fixed amount off                                         | [widget: 2]             | [widget: 10.00]              | $5.00 off        | 15.00
        Fixed amount exceeding subtotal floors at zero           | [widget: 1]             | [widget: 3.00]               | $10.00 off       | 0.00
        Product-specific discount applies when product present  | [widget: 2, gadget: 1]  | [widget: 5.00, gadget: 9.00] | $3.00 off widget | 16.00
        Product-specific discount has no effect when absent     | [gadget: 1]             | [gadget: 9.00]               | $3.00 off widget | 9.00
        """)
    void calculatesCartTotal(Map<String, Integer> cartItems, ProductCatalogue catalogue,
                              Coupon coupon, BigDecimal total) {
        BigDecimal actual = CartService.calculateTotal(Cart.withItems(cartItems), coupon, catalogue);
        assertEquals(0, total.compareTo(actual));
    }

    @TypeConverter
    public static ProductCatalogue parseCatalogue(Map<String, BigDecimal> prices) {
        return ProductCatalogue.from(prices);
    }

    @TypeConverter
    public static InventoryService parseInventory(Map<String, Integer> available) {
        return productId -> available.getOrDefault(productId, 0);
    }

    @TypeConverter
    public static CouponStore parseCouponStore(Map<String, String> statuses) {
        return code -> {
            String status = statuses.get(code);
            if (status == null) {
                return Optional.empty();
            }
            if ("expired".equals(status)) {
                return Optional.of(new Coupon(CouponType.PERCENT, BigDecimal.ZERO, null, true));
            }
            return Optional.of(Coupon.percentage(10));
        };
    }

    @TypeConverter
    public static Coupon parseCoupon(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        if (value.endsWith("% off")) {
            int percent = Integer.parseInt(value.substring(0, value.indexOf('%')).trim());
            return Coupon.percentage(percent);
        }
        String withoutSign = value.substring(1);
        if (withoutSign.contains(" off ")) {
            String[] parts = withoutSign.split(" off ", 2);
            return Coupon.productSpecific(parts[1], new BigDecimal(parts[0]));
        }
        String amount = withoutSign.substring(0, withoutSign.length() - " off".length());
        return Coupon.fixed(new BigDecimal(amount));
    }
}
