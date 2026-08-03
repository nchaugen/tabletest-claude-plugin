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
        Product display name equals its catalogue id, since ProductCatalogue.from()
        (used by the Catalogue Prices column converter) has no separate display name.
        Assumed messages: "Added <qty>x <id>" on success, "Unknown product: <id>" when
        the id is not in the catalogue.
        """)
    @TableTest("""
        Scenario                                          | Cart Items Before | Catalogue Prices              | Product Id | Quantity | Cart Items After?      | Message?
        Adds a new item to an empty cart                  | [:]               | [widget: 9.99]                | widget     | 2        | [widget: 2]            | Added 2x widget
        Increases the quantity of an item already in cart | [widget: 1]       | [widget: 9.99]                | widget     | 3        | [widget: 4]            | Added 3x widget
        Adds a second item alongside an existing one      | [widget: 2]       | [widget: 9.99, gadget: 19.99] | gadget     | 1        | [widget: 2, gadget: 1] | Added 1x gadget
        Rejects a product that is not in the catalogue    | [:]               | [widget: 9.99]                | doohickey  | 1        | [:]                    | Unknown product: doohickey
        """)
    void addsItemsPricedFromTheCatalogue(Map<String, Integer> itemsBefore, ProductCatalogue catalogue,
                                          String productId, int quantity,
                                          Map<String, Integer> itemsAfter, String message) {
        CartResult result = CartService.addItem(Cart.withItems(itemsBefore), productId, quantity, catalogue);

        assertEquals(itemsAfter, result.cart().items());
        assertEquals(message, result.message());
    }

    @Description("""
        Assumed messages: "Removed <id>" on success, "<id> is not in the cart" when the
        product is absent, whether or not other items are present.
        """)
    @TableTest("""
        Scenario                                  | Cart Items Before      | Product Id | Cart Items After? | Message?
        Removes the only item in the cart         | [widget: 2]            | widget     | [:]               | Removed widget
        Removes one item, other items remain      | [widget: 2, gadget: 1] | widget     | [gadget: 1]       | Removed widget
        Item absent from an empty cart            | [:]                    | widget     | [:]               | widget is not in the cart
        Item absent, other items remain untouched | [gadget: 1]            | widget     | [gadget: 1]       | widget is not in the cart
        """)
    void removesItemsFromTheCart(Map<String, Integer> itemsBefore, String productId,
                                  Map<String, Integer> itemsAfter, String message) {
        CartResult result = CartService.removeItem(Cart.withItems(itemsBefore), productId);

        assertEquals(itemsAfter, result.cart().items());
        assertEquals(message, result.message());
    }

    @Description("""
        Cart contents are fixed to a single item throughout, since applyCoupon does not
        read cart items - only the active coupon code. Assumed messages: "Applied coupon
        <code>" on success, "Coupon <code> has expired", "Coupon code <code> not found".
        """)
    @TableTest("""
        Scenario                                                 | Active Coupon Before | Coupon Code | Coupon In Store | Active Coupon After? | Message?
        Applies a percentage-off coupon, none active before      |                      | SAVE10      | 10% off         | SAVE10               | Applied coupon SAVE10
        Applies a fixed-amount coupon, none active before        |                      | FLAT5       | $5 off          | FLAT5                | Applied coupon FLAT5
        Applies a product-specific coupon, none active before    |                      | WIDGET3     | widget: $3 off  | WIDGET3              | Applied coupon WIDGET3
        Replaces an already active coupon with a new valid one   | SAVE10               | FLAT5       | $5 off          | FLAT5                | Applied coupon FLAT5
        Rejects an expired code, previous coupon stays active    | SAVE10               | OLDCODE     | expired         | SAVE10               | Coupon OLDCODE has expired
        Rejects an unknown code, previous coupon stays active    | SAVE10               | GHOST       |                 | SAVE10               | Coupon code GHOST not found
        Rejects an unknown code when no coupon was active before |                      | GHOST       |                 |                      | Coupon code GHOST not found
        """)
    void appliesACouponCodeToTheCart(String activeCouponBefore, String couponCode, Coupon couponInStore,
                                      String activeCouponAfter, String message) {
        Cart cart = Cart.withItems(Map.of("widget", 1)).withActiveCouponCode(activeCouponBefore);
        CouponStore store = code -> Optional.ofNullable(couponInStore);

        CartResult result = CartService.applyCoupon(cart, couponCode, store);

        assertEquals(activeCouponAfter, result.cart().activeCouponCode());
        assertEquals(message, result.message());
    }

    @Description("""
        Inventory is stubbed to always have ample stock in this table, since stock
        shortages are covered separately in verifiesStockLevelsAtCheckout. Assumed
        messages: "Checkout complete" on success, "Cart is empty" when nothing to check out.
        """)
    @TableTest("""
        Scenario                            | Cart Items  | Success? | Message?
        Empty cart cannot check out         | [:]         | false    | Cart is empty
        Non-empty cart proceeds to checkout | [widget: 1] | true     | Checkout complete
        """)
    void requiresANonEmptyCartToCheckOut(Map<String, Integer> items, boolean success, String message) {
        InventoryService inventory = productId -> Integer.MAX_VALUE;

        CheckoutResult result = CartService.checkout(Cart.withItems(items), inventory);

        assertEquals(success, result.success());
        assertEquals(message, result.message());
    }

    @Description("""
        Assumed message for a shortage: "Insufficient stock for <id>: requested <qty>,
        available <qty>", joined with "; " when more than one item is short, in cart
        item order. "Checkout complete" on success.
        """)
    @TableTest("""
        Scenario                                     | Cart Items             | Available Stock         | Success? | Message?
        Stock comfortably covers every item          | [widget: 2, gadget: 1] | [widget: 10, gadget: 5] | true     | Checkout complete
        Stock exactly matches the requested quantity | [widget: 2]            | [widget: 2]             | true     | Checkout complete
        Stock is one short of the requested quantity | [widget: 2]            | [widget: 1]             | false    | Insufficient stock for widget: requested 2, available 1
        Stock is short for more than one item        | [widget: 2, gadget: 3] | [widget: 1, gadget: 0]  | false    | Insufficient stock for widget: requested 2, available 1; gadget: requested 3, available 0
        """)
    void verifiesStockLevelsAtCheckout(Map<String, Integer> items, InventoryService inventory,
                                        boolean success, String message) {
        CheckoutResult result = CartService.checkout(Cart.withItems(items), inventory);

        assertEquals(success, result.success());
        assertEquals(message, result.message());
    }

    @Description("""
        A product-specific coupon whose target product is absent from the cart has no
        effect on the total (open question: should applying such a coupon be rejected
        instead - not specified by the feature description).
        """)
    @TableTest("""
        Scenario                                                               | Cart Items             | Catalogue Prices               | Active Coupon  | Total?
        Sums quantity times price with no coupon applied                       | [widget: 2, gadget: 1] | [widget: 9.99, gadget: 19.99]  |                | 39.97
        Applies a percentage-off coupon to the subtotal                        | [widget: 2]            | [widget: 10.00]                | 10% off        | 18.00
        Applies a fixed-amount coupon below the subtotal                       | [widget: 2]            | [widget: 10.00]                | $5 off         | 15.00
        Floors the total at zero when the fixed discount exceeds the subtotal  | [widget: 1]            | [widget: 10.00]                | $50 off        | 0.00
        Applies a product-specific discount when its target is in the cart     | [widget: 1, gadget: 1] | [widget: 10.00, gadget: 20.00] | widget: $3 off | 27.00
        Leaves the total unaffected when the product-specific target is absent | [gadget: 1]            | [gadget: 20.00]                | widget: $3 off | 20.00
        """)
    void calculatesTheCartTotalAfterAnyCoupon(Map<String, Integer> items, ProductCatalogue catalogue,
                                               Coupon activeCoupon, BigDecimal total) {
        BigDecimal result = CartService.calculateTotal(Cart.withItems(items), activeCoupon, catalogue);

        assertEquals(0, total.compareTo(result));
    }

    @TypeConverter
    public static ProductCatalogue toCatalogue(Map<String, BigDecimal> prices) {
        return ProductCatalogue.from(prices);
    }

    @TypeConverter
    public static InventoryService toInventory(Map<String, Integer> stock) {
        return productId -> stock.getOrDefault(productId, 0);
    }

    @TypeConverter
    public static Coupon toCoupon(String text) {
        if (text.equals("expired")) {
            return new Coupon(CouponType.PERCENT, BigDecimal.ZERO, null, true);
        }
        if (text.endsWith("% off")) {
            return Coupon.percentage(Integer.parseInt(text.substring(0, text.indexOf('%'))));
        }
        if (text.contains(":")) {
            String[] parts = text.split(":", 2);
            return Coupon.productSpecific(parts[0].trim(), parseAmount(parts[1].trim()));
        }
        return Coupon.fixed(parseAmount(text));
    }

    private static BigDecimal parseAmount(String text) {
        return new BigDecimal(text.replaceAll("[^0-9.]", ""));
    }
}
