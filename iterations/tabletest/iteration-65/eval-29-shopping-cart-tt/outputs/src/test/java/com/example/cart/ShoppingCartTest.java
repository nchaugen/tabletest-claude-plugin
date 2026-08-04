package com.example.cart;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShoppingCartTest {

    private static final ProductCatalogue CATALOGUE = ProductCatalogue.from(Map.of(
            "Widget", new BigDecimal("5.00"),
            "Gadget", new BigDecimal("3.00")));

    private static final CouponStore COUPON_STORE = CouponStore.of(
            Map.of(
                    "SAVE10", Coupon.percentage(10),
                    "FLAT5", Coupon.fixed(new BigDecimal("5.00")),
                    "WIDGET2", Coupon.productSpecific("Widget", new BigDecimal("2.00"))),
            Set.of("OLD10"));

    @Description("""
            Cart starts empty in every row; how quantities accumulate once a product
            is already in the cart is covered by accumulatesQuantityWhenAddingItems.
            """)
    @TableTest("""
        Scenario                                | Product Id | Quantity | Catalogue      | Cart After? | Message?
        Product exists in the catalogue         | Widget     | 2        | [Widget: 5.00] | [Widget: 2] | Added 2x Widget
        Product does not exist in the catalogue | Bogus      | 1        | [Widget: 5.00] | [:]         | Unknown product: Bogus
        """)
    void addsAnItemFoundInTheCatalogueOrRejectsAnUnknownProduct(
            String productId, int quantity, ProductCatalogue catalogue, Map<String, Integer> cartAfter, String message) {
        CartResult result = CartService.addItem(Cart.empty(), productId, quantity, catalogue);
        assertEquals(cartAfter, result.cart().getItems());
        assertEquals(message, result.message());
    }

    @Description("""
            Catalogue is fixed: Widget = 5.00, Gadget = 3.00. Whether a product is found
            in the catalogue is covered by addsAnItemFoundInTheCatalogueOrRejectsAnUnknownProduct.
            """)
    @TableTest("""
        Scenario                                                 | Cart Before | Product Id | Quantity | Cart After?            | Message?
        Adds a new product to an empty cart                      | [:]         | Widget     | 2        | [Widget: 2]            | Added 2x Widget
        Adds more of a product already in the cart               | [Widget: 2] | Widget     | 3        | [Widget: 5]            | Added 3x Widget
        Adds a second distinct product alongside an existing one | [Widget: 2] | Gadget     | 1        | [Widget: 2, Gadget: 1] | Added 1x Gadget
        """)
    void accumulatesQuantityWhenAddingItems(
            Cart cartBefore, String productId, int quantity, Map<String, Integer> cartAfter, String message) {
        CartResult result = CartService.addItem(cartBefore, productId, quantity, CATALOGUE);
        assertEquals(cartAfter, result.cart().getItems());
        assertEquals(message, result.message());
    }

    @TableTest("""
        Scenario                                 | Cart Before            | Product Id | Cart After? | Message?
        Removes one of several items             | [Widget: 2, Gadget: 1] | Widget     | [Gadget: 1] | Removed Widget
        Removes the only item, emptying the cart | [Widget: 2]            | Widget     | [:]         | Removed Widget
        """)
    void removesAnItemPresentInTheCart(
            Cart cartBefore, String productId, Map<String, Integer> cartAfter, String message) {
        CartResult result = CartService.removeItem(cartBefore, productId);
        assertEquals(cartAfter, result.cart().getItems());
        assertEquals(message, result.message());
    }

    @TableTest("""
        Scenario                                   | Cart Before | Product Id | Cart After? | Message?
        Cart contains other items but not this one | [Gadget: 1] | Widget     | [Gadget: 1] | Widget is not in the cart
        Cart is empty                              | [:]         | Widget     | [:]         | Widget is not in the cart
        """)
    void rejectsRemovingAProductNotInTheCart(
            Cart cartBefore, String productId, Map<String, Integer> cartAfter, String message) {
        CartResult result = CartService.removeItem(cartBefore, productId);
        assertEquals(cartAfter, result.cart().getItems());
        assertEquals(message, result.message());
    }

    @Description("""
            Coupon store fixture: SAVE10 = 10% off, FLAT5 = 5.00 off, WIDGET2 = 2.00 off
            Widget, OLD10 = expired. Cart items are held empty throughout; item
            content does not affect coupon activation.
            """)
    @TableTest("""
        Scenario                                                 | Active Coupon Before | Coupon Code | Active Coupon After? | Message?
        No coupon active, applies a percentage coupon            |                      | SAVE10      | SAVE10               | Coupon applied: SAVE10
        Replaces an active coupon with a fixed-amount coupon     | SAVE10               | FLAT5       | FLAT5                | Coupon applied: FLAT5
        Replaces an active coupon with a product-specific coupon | FLAT5                | WIDGET2     | WIDGET2              | Coupon applied: WIDGET2
        """)
    void activatesACouponReplacingAnyPreviouslyActiveCoupon(
            String activeCouponBefore, String couponCode, String activeCouponAfter, String message) {
        Cart cart = Cart.empty().withActiveCouponCode(activeCouponBefore);
        CartResult result = CartService.applyCoupon(cart, couponCode, COUPON_STORE);
        assertEquals(activeCouponAfter, result.cart().getActiveCouponCode());
        assertEquals(message, result.message());
    }

    @TableTest("""
        Scenario                    | Active Coupon Before | Coupon Code | Active Coupon After? | Message?
        Code does not exist         | SAVE10               | BOGUS       | SAVE10               | Coupon not found: BOGUS
        Code exists but has expired | SAVE10               | OLD10       | SAVE10               | Coupon expired
        """)
    void rejectsAnInvalidCouponCodeAndKeepsTheExistingCouponActive(
            String activeCouponBefore, String couponCode, String activeCouponAfter, String message) {
        Cart cart = Cart.empty().withActiveCouponCode(activeCouponBefore);
        CartResult result = CartService.applyCoupon(cart, couponCode, COUPON_STORE);
        assertEquals(activeCouponAfter, result.cart().getActiveCouponCode());
        assertEquals(message, result.message());
    }

    @Description("""
            Assumed message format for a shortfall is "<Product> (need <requested>, have
            <available>)", joined with ", " when several products are short.
            """)
    @TableTest("""
        Scenario                          | Cart Items             | Inventory              | Checkout Succeeds? | Message?
        Stock exactly matches demand      | [Widget: 3]            | [Widget: 3]            | true               | Checkout complete
        Stock is short for one item       | [Widget: 5]            | [Widget: 2]            | false              | Insufficient stock: Widget (need 5, have 2)
        Stock is short for multiple items | [Widget: 5, Gadget: 5] | [Widget: 2, Gadget: 1] | false              | Insufficient stock: Widget (need 5, have 2), Gadget (need 5, have 1)
        """)
    void verifiesStockLevelsBeforeCompletingCheckout(
            Cart cartItems, InventoryService inventory, boolean checkoutSucceeds, String message) {
        CheckoutResult result = CartService.checkout(cartItems, inventory);
        assertEquals(checkoutSucceeds, result.success());
        assertEquals(message, result.message());
    }

    @TableTest("""
        Scenario   | Cart Items | Inventory          | Checkout Succeeds? | Message?
        Empty cart | [:]        | {[:], [Widget: 5]} | false              | Cart is empty
        """)
    void rejectsCheckoutOfAnEmptyCartRegardlessOfInventory(
            Cart cartItems, InventoryService inventory, boolean checkoutSucceeds, String message) {
        CheckoutResult result = CartService.checkout(cartItems, inventory);
        assertEquals(checkoutSucceeds, result.success());
        assertEquals(message, result.message());
    }

    @Description("""
            Assumption: a product-specific coupon subtracts its amount once from the
            cart total when the product is present, not once per unit, and has no
            effect when the product is absent from the cart.
            """)
    @TableTest("""
        Scenario                                                         | Items                  | Catalogue                    | Coupon        | Total?
        Single item, no coupon                                           | [Widget: 2]            | [Widget: 5.00]               |               | 10.00
        Multiple items, no coupon                                        | [Widget: 2, Gadget: 1] | [Widget: 5.00, Gadget: 3.00] |               | 13.00
        Percentage coupon discounts the whole cart                       | [Widget: 2, Gadget: 1] | [Widget: 5.00, Gadget: 3.00] | 10% off       | 11.70
        Fixed-amount coupon subtracts from the total                     | [Widget: 2]            | [Widget: 5.00]               | $3 off        | 7.00
        Fixed-amount coupon larger than the total is floored at zero     | [Widget: 1]            | [Widget: 5.00]               | $20 off       | 0.00
        Product-specific coupon discounts only its product               | [Widget: 2, Gadget: 1] | [Widget: 5.00, Gadget: 3.00] | $2 off Widget | 11.00
        Product-specific coupon has no effect when its product is absent | [Gadget: 1]            | [Gadget: 3.00]               | $2 off Widget | 3.00
        """)
    void calculatesCartTotalAsSumMinusCouponDiscountFlooredAtZero(
            Cart items, ProductCatalogue catalogue, Coupon coupon, BigDecimal total) {
        BigDecimal actual = CartService.calculateTotal(items, coupon, catalogue);
        assertEquals(0, total.compareTo(actual));
    }

    @TypeConverter
    public static Cart toCart(Map<String, Integer> items) {
        return Cart.withItems(items);
    }

    @TypeConverter
    public static ProductCatalogue toCatalogue(Map<String, BigDecimal> prices) {
        return ProductCatalogue.from(prices);
    }

    @TypeConverter
    public static InventoryService toInventory(Map<String, Integer> stock) {
        return productId -> stock.getOrDefault(productId, 0);
    }

    private static final Pattern PRODUCT_COUPON = Pattern.compile("\\$(\\d+(?:\\.\\d+)?) off (\\w+)");
    private static final Pattern FIXED_COUPON = Pattern.compile("\\$(\\d+(?:\\.\\d+)?) off");
    private static final Pattern PERCENT_COUPON = Pattern.compile("(\\d+)% off");

    @TypeConverter
    public static Coupon toCoupon(String spec) {
        Matcher productMatcher = PRODUCT_COUPON.matcher(spec);
        if (productMatcher.matches()) {
            return Coupon.productSpecific(productMatcher.group(2), new BigDecimal(productMatcher.group(1)));
        }
        Matcher fixedMatcher = FIXED_COUPON.matcher(spec);
        if (fixedMatcher.matches()) {
            return Coupon.fixed(new BigDecimal(fixedMatcher.group(1)));
        }
        Matcher percentMatcher = PERCENT_COUPON.matcher(spec);
        if (percentMatcher.matches()) {
            return Coupon.percentage(Integer.parseInt(percentMatcher.group(1)));
        }
        throw new IllegalArgumentException("Unrecognised coupon spec: " + spec);
    }
}
