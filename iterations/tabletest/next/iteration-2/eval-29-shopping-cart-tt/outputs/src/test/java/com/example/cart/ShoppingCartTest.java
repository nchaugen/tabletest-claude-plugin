package com.example.cart;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

public class ShoppingCartTest {

    @Description("""
        Price always comes from the catalogue, never the customer. Quantity is the amount
        added in this operation; adding a product already in the cart increases its
        existing quantity rather than replacing it. An unknown product id is rejected and
        leaves the cart unchanged.
        """)
    @TableTest("""
        Scenario                      | Cart Before | Product Id | Quantity | Catalogue            | Success? | Cart After?    | Message?
        Add new product to empty cart | [:]         | W1         | 2        | [W1: 9.99]           | true     | [W1: 2]        | "Added 2x W1"
        Add more of existing product  | [W1: 2]     | W1         | 3        | [W1: 9.99]           | true     | [W1: 5]        | "Added 3x W1"
        Add a second product          | [W1: 2]     | W2         | 1        | [W1: 9.99, W2: 4.50] | true     | [W1: 2, W2: 1] | "Added 1x W2"
        Unknown product id            | [:]         | GHOST      | 1        | [W1: 9.99]           | false    | [:]            | "Product not found: GHOST"
        """)
    void shouldAddItemToCart(Cart cartBefore, String productId, int quantity, ProductCatalogue catalogue,
            boolean success, Cart cartAfter, String message) {
        CartResult result = CartService.addItem(cartBefore, productId, quantity, catalogue);

        assertEquals(success, result.success());
        assertEquals(cartAfter, result.cart());
        assertEquals(message, result.message());
    }

    @TableTest("""
        Scenario                     | Cart Before    | Product Id | Success? | Cart After? | Message?
        Remove existing item         | [W1: 2]        | W1         | true     | [:]         | "Removed W1"
        Remove one of several items  | [W1: 2, W2: 1] | W2         | true     | [W1: 2]     | "Removed W2"
        Item not in an empty cart    | [:]            | W1         | false    | [:]         | "Item not in cart: W1"
        Item not in a nonempty cart  | [W2: 1]        | W1         | false    | [W2: 1]     | "Item not in cart: W1"
        """)
    void shouldRemoveItemFromCart(Cart cartBefore, String productId,
            boolean success, Cart cartAfter, String message) {
        CartResult result = CartService.removeItem(cartBefore, productId);

        assertEquals(success, result.success());
        assertEquals(cartAfter, result.cart());
        assertEquals(message, result.message());
    }

    @Description("""
        Coupon Code Status stands in for what the coupon store returns when the code is
        looked up: valid (an active, non-expired coupon), expired, or unknown (no such
        code). The concrete discount a valid code carries doesn't matter to this rule, so
        every valid row shares the same underlying coupon. Expired and unknown codes must
        be rejected without disturbing whatever coupon was already active.
        """)
    @TableTest("""
        Scenario                            | Active Coupon Before | Coupon Code | Coupon Code Status | Success? | Active Coupon After? | Message?
        Apply first coupon                  |                       | SAVE10      | valid               | true     | SAVE10                | "Coupon applied: SAVE10"
        Replace an active coupon             | SAVE10                | FLAT5       | valid               | true     | FLAT5                 | "Coupon applied: FLAT5"
        Expired code, coupon already active  | SAVE10                | OLD20       | expired             | false    | SAVE10                | "Coupon expired: OLD20"
        Expired code, no coupon active       |                       | OLD20       | expired             | false    |                       | "Coupon expired: OLD20"
        Unknown code, coupon already active  | SAVE10                | BOGUS       | unknown             | false    | SAVE10                | "Unknown coupon code: BOGUS"
        Unknown code, no coupon active       |                       | BOGUS       | unknown             | false    |                       | "Unknown coupon code: BOGUS"
        """)
    void shouldApplyCoupon(String activeCouponBefore, String couponCode, String couponCodeStatus,
            boolean success, String activeCouponAfter, String message) {
        Cart cart = Cart.empty().withActiveCouponCode(activeCouponBefore);
        CouponStore couponStore = buildCouponStore(couponCode, couponCodeStatus);

        CartResult result = CartService.applyCoupon(cart, couponCode, couponStore);

        assertEquals(success, result.success());
        assertEquals(activeCouponAfter, result.cart().getActiveCouponCode());
        assertEquals(message, result.message());
    }

    @Description("""
        Insufficient-stock failures list every short product with requested vs. available
        quantities, not just the first one found.
        """)
    @TableTest("""
        Scenario                          | Cart Items     | Inventory      | Success? | Message?
        Empty cart cannot check out       | [:]            | [:]            | false    | "Cart is empty"
        Sufficient stock, single item     | [W1: 2]        | [W1: 5]        | true     | "Checkout complete"
        Sufficient stock, multiple items  | [W1: 2, W2: 1] | [W1: 5, W2: 3] | true     | "Checkout complete"
        Stock exactly matches demand      | [W1: 3]        | [W1: 3]        | true     | "Checkout complete"
        Insufficient stock, single item   | [W1: 5]        | [W1: 2]        | false    | "Insufficient stock: W1 (requested 5, available 2)"
        Insufficient stock, every item    | [W1: 5, W2: 4] | [W1: 2, W2: 1] | false    | "Insufficient stock: W1 (requested 5, available 2), W2 (requested 4, available 1)"
        One item ok, one item short       | [W1: 2, W2: 5] | [W1: 2, W2: 1] | false    | "Insufficient stock: W2 (requested 5, available 1)"
        """)
    void shouldCheckout(Cart cartItems, InventoryService inventory, boolean success, String message) {
        CheckoutResult result = CartService.checkout(cartItems, inventory);

        assertEquals(success, result.success());
        assertEquals(message, result.message());
    }

    @Description("""
        A product-specific coupon discounts only the named product, by the given amount
        per unit held in the cart, and has no effect when that product isn't present. A
        percentage coupon applies its percentage to the pre-discount subtotal. The total is
        floored at zero when the discount would otherwise exceed the subtotal.
        """)
    @TableTest("""
        Scenario                                | Cart Items     | Catalogue             | Coupon Type | Coupon Amount | Coupon Target Product | Total?
        Empty cart                               | [:]            | [:]                   |             |               |                        | 0.00
        No coupon                                | [W1: 2]        | [W1: 10.00]           |             |               |                        | 20.00
        Percentage off the whole cart            | [W1: 2]        | [W1: 10.00]           | PERCENT     | 10            |                        | 18.00
        Fixed amount off the whole cart          | [W1: 2]        | [W1: 10.00]           | FIXED       | 5.00          |                        | 15.00
        Product-specific discount applies        | [W1: 2, W2: 1] | [W1: 10.00, W2: 8.00] | PRODUCT     | 3.00          | W1                     | 22.00
        Product-specific discount, product absent| [W2: 1]        | [W2: 8.00]            | PRODUCT     | 3.00          | W1                     | 8.00
        Fixed discount floored at zero           | [W1: 1]        | [W1: 5.00]            | FIXED       | 20.00         |                        | 0.00
        Percentage discount exactly clears cart  | [W1: 1]        | [W1: 5.00]            | PERCENT     | 100           |                        | 0.00
        """)
    void shouldCalculateTotal(Cart cart, ProductCatalogue catalogue, CouponType couponType, BigDecimal couponAmount,
            String couponTargetProduct, BigDecimal total) {
        Coupon activeCoupon = buildCoupon(couponType, couponAmount, couponTargetProduct);

        BigDecimal result = CartService.calculateTotal(cart, activeCoupon, catalogue);

        assertEquals(0, total.compareTo(result));
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

    private static Coupon buildCoupon(CouponType type, BigDecimal amount, String targetProductId) {
        return type == null ? null : new Coupon(type, amount, targetProductId, false);
    }

    private static CouponStore buildCouponStore(String code, String codeStatus) {
        return switch (codeStatus) {
            case "valid" -> CouponStore.of(Map.of(code, Coupon.percentage(10)), Set.of());
            case "expired" -> CouponStore.of(Map.of(), Set.of(code));
            case "unknown" -> CouponStore.of(Map.of(), Set.of());
            default -> throw new IllegalArgumentException("Unknown coupon code status: " + codeStatus);
        };
    }
}
