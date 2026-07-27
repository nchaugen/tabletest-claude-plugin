package com.example.cart;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShoppingCartTest {

    @DisplayName("Adds items to the cart with catalogue-sourced prices")
    @Description("""
            Adding a product already in the cart increases its quantity rather than
            replacing it. An unknown product id is rejected and the cart is left unchanged.
            Message format ('Added <qty>x <name>' / 'Product not found: <id>') is assumed;
            the requirements only give an example. The add-item message does not carry price -
            see calculatesCartTotal for how catalogue prices drive the total.
            """)
    @TableTest("""
            Scenario                        | Cart Before      | Product Id | Quantity | Catalogue           | Cart After?      | Success? | Message?
            New product added to empty cart | [:]              | Widget     | 2        | [Widget: 10.00]     | [Widget: 2]      | true     | Added 2x Widget
            Existing product quantity grows | [Widget: 1]      | Widget     | 2        | [Widget: 10.00]     | [Widget: 3]      | true     | Added 2x Widget
            Unknown product is rejected     | [:]              | Ghost      | 1        | [Widget: 10.00]     | [:]              | false    | Product not found: Ghost
            """)
    void addsItemsToCart(Cart cartBefore, String productId, int quantity, ProductCatalogue catalogue,
                         Cart cartAfter, boolean success, String message) {
        CartResult result = CartService.addItem(cartBefore, productId, quantity, catalogue);
        assertEquals(success, result.success());
        assertEquals(message, result.message());
        assertEquals(cartAfter, result.cart());
    }

    @DisplayName("Removes items from the cart")
    @Description("""
            Message format ('Removed <name>' / '<name> not in cart') is assumed.
            """)
    @TableTest("""
            Scenario                          | Cart Before               | Product Id | Cart After?  | Success? | Message?
            Item in cart is removed           | [Widget: 2]               | Widget     | [:]          | true     | Removed Widget
            Other items remain after removal  | [Widget: 2, Gadget: 1]    | Widget     | [Gadget: 1]  | true     | Removed Widget
            Item not in cart returns an error | [Gadget: 1]               | Widget     | [Gadget: 1]  | false    | Widget not in cart
            """)
    void removesItemsFromCart(Cart cartBefore, String productId, Cart cartAfter, boolean success, String message) {
        CartResult result = CartService.removeItem(cartBefore, productId);
        assertEquals(success, result.success());
        assertEquals(message, result.message());
        assertEquals(cartAfter, result.cart());
    }

    @DisplayName("Applies a coupon, replacing any previous one")
    @Description("""
            Coupon type (percentage, fixed, product-specific) does not affect apply/replace
            behaviour, so a single type is used per row - see calculatesCartTotal for how each
            type affects the total. Message format ('Coupon applied: <code>' / 'Coupon not found')
            is assumed; 'Coupon expired' is given directly in the requirements.
            """)
    @TableTest("""
            Scenario                                          | Cart Before               | Coupon Code | Coupon Store                 | Cart After?               | Success? | Message?
            First coupon is activated                         | [:]                       | SAVE10      | [SAVE10: 10%]                 | [activeCoupon: SAVE10]    | true     | Coupon applied: SAVE10
            New coupon replaces the active one                | [activeCoupon: OLD5]      | SAVE10      | [OLD5: 5%, SAVE10: 10%]       | [activeCoupon: SAVE10]    | true     | Coupon applied: SAVE10
            Expired coupon is rejected, active one unchanged   | [activeCoupon: SAVE10]    | STALE       | [SAVE10: 10%, STALE: expired] | [activeCoupon: SAVE10]    | false    | Coupon expired
            Nonexistent coupon is rejected, active one unchanged | [activeCoupon: SAVE10]  | GHOST       | [SAVE10: 10%]                 | [activeCoupon: SAVE10]    | false    | Coupon not found
            """)
    void appliesCoupon(Cart cartBefore, String couponCode, CouponStore couponStore,
                       Cart cartAfter, boolean success, String message) {
        CartResult result = CartService.applyCoupon(cartBefore, couponCode, couponStore);
        assertEquals(success, result.success());
        assertEquals(message, result.message());
        assertEquals(cartAfter, result.cart());
    }

    @DisplayName("Checks out the cart after verifying stock")
    @Description("""
            Message format for success ('Checkout complete') and for an empty cart
            ('Cart is empty') is assumed. The insufficient-stock message lists every short
            product as '<name>: need <required>, have <available>', joined with '; '.
            """)
    @TableTest("""
            Scenario                                          | Cart                     | Inventory                  | Success? | Message?
            Empty cart cannot check out                       | [:]                      | [:]                        | false    | Cart is empty
            Sufficient stock checks out successfully          | [Widget: 2, Gadget: 1]   | [Widget: 5, Gadget: 5]     | true     | Checkout complete
            Insufficient stock for one item fails             | [Widget: 5]              | [Widget: 2]                | false    | Insufficient stock for Widget: need 5, have 2
            Insufficient stock for multiple items lists each  | [Widget: 5, Gadget: 3]   | [Widget: 2, Gadget: 1]     | false    | Insufficient stock for Widget: need 5, have 2; Gadget: need 3, have 1
            """)
    void checksOutCart(Cart cart, InventoryService inventory, boolean success, String message) {
        CheckoutResult result = CartService.checkout(cart, inventory);
        assertEquals(success, result.success());
        assertEquals(message, result.message());
    }

    @DisplayName("Calculates the cart total after coupon discounts")
    @Description("""
            A product-specific discount is a flat deduction applied once when the targeted
            product is present in the cart, regardless of quantity, and has no effect when
            the product is absent. Totals are compared with BigDecimal.compareTo to ignore
            scale differences.
            """)
    @TableTest("""
            Scenario                                            | Cart                    | Catalogue                        | Coupon        | Total?
            No coupon applied                                   | [Widget: 2]             | [Widget: 10.00]                   |               | 20.00
            Percentage discount reduces the whole cart           | [Widget: 2, Gadget: 1]  | [Widget: 10.00, Gadget: 5.00]     | 10%           | 22.50
            Fixed amount off the whole cart                      | [Widget: 2]             | [Widget: 10.00]                   | $5            | 15.00
            Product-specific discount applies to targeted product | [Widget: 2, Gadget: 1] | [Widget: 10.00, Gadget: 5.00]     | "Widget:$3"   | 22.00
            Product-specific discount has no effect when absent | [Widget: 2]             | [Widget: 10.00]                   | "Gadget:$3"   | 20.00
            Discount larger than total floors at zero            | [Widget: 1]             | [Widget: 5.00]                    | $20           | 0.00
            """)
    void calculatesCartTotal(Cart cart, ProductCatalogue catalogue, Coupon coupon, BigDecimal total) {
        BigDecimal actual = CartService.calculateTotal(cart, coupon, catalogue);
        assertEquals(0, total.compareTo(actual));
    }

    @TypeConverter
    public static Cart parseCart(Map<String, String> spec) {
        Map<String, Integer> items = new LinkedHashMap<>();
        String activeCouponCode = null;
        for (Map.Entry<String, String> entry : spec.entrySet()) {
            if ("activeCoupon".equals(entry.getKey())) {
                activeCouponCode = entry.getValue();
            } else {
                items.put(entry.getKey(), Integer.parseInt(entry.getValue()));
            }
        }
        return Cart.withItems(items).withActiveCouponCode(activeCouponCode);
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
    public static CouponStore parseCouponStore(Map<String, String> specs) {
        Map<String, Coupon> available = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : specs.entrySet()) {
            available.put(entry.getKey(), parseCouponSpec(entry.getValue()));
        }
        return code -> Optional.ofNullable(available.get(code));
    }

    @TypeConverter
    public static Coupon parseCoupon(String spec) {
        return parseCouponSpec(spec);
    }

    private static Coupon parseCouponSpec(String spec) {
        if ("expired".equals(spec)) {
            return new Coupon(CouponType.PERCENT, BigDecimal.ZERO, null, true);
        }
        if (spec.endsWith("%")) {
            return Coupon.percentage(Integer.parseInt(spec.substring(0, spec.length() - 1)));
        }
        if (spec.startsWith("$")) {
            return Coupon.fixed(new BigDecimal(spec.substring(1)));
        }
        int colon = spec.indexOf(':');
        String productId = spec.substring(0, colon);
        String amount = spec.substring(colon + 2);
        return Coupon.productSpecific(productId, new BigDecimal(amount));
    }
}
