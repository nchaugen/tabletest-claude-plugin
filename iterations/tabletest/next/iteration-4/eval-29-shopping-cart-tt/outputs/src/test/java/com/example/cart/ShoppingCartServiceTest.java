package com.example.cart;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShoppingCartServiceTest {

    @Description("""
        Catalogue prices: Widget = 10.00, Gadget = 5.00, Thingamajig = 2.00. Prices always
        come from the catalogue, never from the caller. Assumption: adding a product already
        in the cart increases its existing quantity rather than replacing it.
        """)
    @TableTest("""
        Scenario                                | Cart Before  | Product Id | Quantity | Success? | Cart After?             | Message?
        New product added to empty cart         | [:]           | Widget     | 2        | true     | [Widget: 2]             | Added 2x Widget
        Existing product quantity increases     | [Widget: 2]   | Widget     | 3        | true     | [Widget: 5]             | Added 3x Widget
        Second distinct product keeps first     | [Widget: 2]   | Gadget     | 1        | true     | [Widget: 2, Gadget: 1]  | Added 1x Gadget
        Unknown product rejected, cart unchanged| [Widget: 2]   | Sprocket   | 1        | false    | [Widget: 2]             | "Unknown product: Sprocket"
        """)
    void addsItemToCart(Cart cartBefore, String productId, int quantity,
                         boolean success, Cart cartAfter, String message) {
        CartResult result = CartService.addItem(cartBefore, productId, quantity, catalogue());

        assertEquals(success, result.success());
        assertEquals(message, result.message());
        assertEquals(cartAfter, result.cart());
    }

    @Description("""
        Removing an item takes out the whole line regardless of its quantity; there is no
        partial-quantity removal in this feature.
        """)
    @TableTest("""
        Scenario                                | Cart Before             | Product Id | Success? | Cart After?  | Message?
        Removes item present in cart            | [Widget: 2, Gadget: 1]  | Widget     | true     | [Gadget: 1]  | Removed Widget
        Removes last item, cart becomes empty   | [Widget: 2]             | Widget     | true     | [:]          | Removed Widget
        Removing item not in cart returns error | [Gadget: 1]             | Widget     | false    | [Gadget: 1]  | Widget is not in the cart
        Removing from an empty cart returns error| [:]                    | Widget     | false    | [:]          | Widget is not in the cart
        """)
    void removesItemFromCart(Cart cartBefore, String productId,
                              boolean success, Cart cartAfter, String message) {
        CartResult result = CartService.removeItem(cartBefore, productId);

        assertEquals(success, result.success());
        assertEquals(message, result.message());
        assertEquals(cartAfter, result.cart());
    }

    @Description("""
        Coupon store: SAVE10 = 10% off the whole cart, FLAT5 = $5 fixed off the whole cart,
        WIDGET5 = $5 off the "Widget" product specifically, EXPIRED10 = an expired code,
        BOGUS = a code the store does not recognise. Only one coupon can be active at a
        time; a new valid code replaces the previous one. An expired or unknown code is
        rejected and must leave the previously active coupon untouched.
        """)
    @TableTest("""
        Scenario                                  | Active Coupon Before | Coupon Code | Success? | Active Coupon After? | Message?
        First coupon applied to cart with none    |                       | SAVE10      | true     | SAVE10               | "Coupon applied: SAVE10"
        New coupon replaces the previous one      | SAVE10                | FLAT5       | true     | FLAT5                | "Coupon applied: FLAT5"
        Product-specific coupon applied           |                       | WIDGET5     | true     | WIDGET5              | "Coupon applied: WIDGET5"
        Expired code rejected, active coupon kept | SAVE10                | EXPIRED10   | false    | SAVE10               | Coupon expired
        Unknown code rejected, active coupon kept | SAVE10                | BOGUS       | false    | SAVE10               | "Unknown coupon code: BOGUS"
        Expired code rejected when none active    |                       | EXPIRED10   | false    |                      | Coupon expired
        """)
    void appliesCouponReplacingActiveOne(String activeCouponBefore, String couponCode,
                                          boolean success, String activeCouponAfter, String message) {
        Cart cart = Cart.empty().withActiveCouponCode(activeCouponBefore);

        CartResult result = CartService.applyCoupon(cart, couponCode, couponStore());

        assertEquals(success, result.success());
        assertEquals(message, result.message());
        assertEquals(activeCouponAfter, result.cart().activeCouponCode());
    }

    @Description("""
        Inventory reflects available stock by product id; a product absent from inventory
        is treated as zero stock. Checkout requires at least one item in the cart and
        sufficient stock for every line item; when stock is insufficient, the message
        reports every short product with the quantity needed versus available.
        """)
    @TableTest("""
        Scenario                                    | Cart Items             | Stock Levels           | Success? | Message?
        Empty cart cannot check out                 | [:]                    | [:]                    | false    | Cart is empty
        Sufficient stock succeeds                   | [Widget: 2]            | [Widget: 5]            | true     | Checkout successful
        Stock exactly matches quantity succeeds     | [Widget: 3]            | [Widget: 3]            | true     | Checkout successful
        Insufficient stock reports shortfall        | [Widget: 5]            | [Widget: 2]            | false    | "Insufficient stock for Widget: need 5, have 2"
        Missing product treated as zero stock       | [Widget: 1]            | [:]                    | false    | "Insufficient stock for Widget: need 1, have 0"
        Only the short product is reported          | [Widget: 2, Gadget: 1] | [Widget: 5, Gadget: 0] | false    | "Insufficient stock for Gadget: need 1, have 0"
        Every short product is reported             | [Widget: 5, Gadget: 3] | [Widget: 2, Gadget: 1] | false    | "Insufficient stock for Widget: need 5, have 2; Insufficient stock for Gadget: need 3, have 1"
        """)
    void verifiesStockAtCheckout(Cart cart, Map<String, Integer> stockLevels,
                                  boolean success, String message) {
        InventoryService inventory = productId -> stockLevels.getOrDefault(productId, 0);

        CheckoutResult result = CartService.checkout(cart, inventory);

        assertEquals(success, result.success());
        assertEquals(message, result.message());
    }

    @Description("""
        Catalogue prices: Widget = 10.00, Gadget = 5.00, Thingamajig = 2.00. Coupon
        notation: "N% off" is a percentage off the whole cart, "$N off" is a fixed amount
        off the whole cart, "$N off <Product>" is a flat amount off that product's line
        only, applied once regardless of quantity, and only when the product is present
        in the cart. Total = sum(quantity x price) minus the coupon discount, floored
        at zero.
        """)
    @TableTest("""
        Scenario                                       | Cart Items             | Coupon           | Total?
        Single item, no coupon                         | [Widget: 2]            |                  | 20.00
        Multiple items, no coupon                      | [Widget: 1, Gadget: 2] |                  | 20.00
        Percentage discount reduces the whole cart      | [Widget: 2]            | 10% off          | 18.00
        Fixed amount discount reduces the whole cart    | [Widget: 2]            | $5 off           | 15.00
        Product coupon applies once regardless of qty  | [Widget: 3]            | $3 off Widget    | 27.00
        Product coupon ignored when product absent      | [Gadget: 1]            | $3 off Widget    | 5.00
        Discount is floored at zero, never negative     | [Widget: 1]            | $50 off          | 0.00
        Empty cart totals zero even with a coupon       | [:]                    | 10% off          | 0.00
        """)
    void calculatesCartTotal(Cart cart, Coupon coupon, BigDecimal total) {
        BigDecimal result = CartService.calculateTotal(cart, coupon, catalogue());

        assertEquals(0, total.compareTo(result));
    }

    private static ProductCatalogue catalogue() {
        return ProductCatalogue.from(Map.of(
                "Widget", BigDecimal.valueOf(10.00),
                "Gadget", BigDecimal.valueOf(5.00),
                "Thingamajig", BigDecimal.valueOf(2.00)));
    }

    private static CouponStore couponStore() {
        return CouponStore.of(
                Map.of(
                        "SAVE10", Coupon.percentage(10),
                        "FLAT5", Coupon.fixed(BigDecimal.valueOf(5)),
                        "WIDGET5", Coupon.productSpecific("Widget", BigDecimal.valueOf(5))),
                Set.of("EXPIRED10"));
    }

    @TypeConverter
    public static Cart parseCart(Map<String, Integer> items) {
        return Cart.withItems(items);
    }

    @TypeConverter
    public static Coupon parseCoupon(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        if (value.endsWith("% off")) {
            String percent = value.substring(0, value.indexOf('%'));
            return Coupon.percentage(Integer.parseInt(percent));
        }
        String withoutSign = value.substring(1);
        int offIndex = withoutSign.indexOf(" off");
        String amount = withoutSign.substring(0, offIndex);
        String target = withoutSign.substring(offIndex + " off".length()).trim();
        if (target.isEmpty()) {
            return Coupon.fixed(new BigDecimal(amount));
        }
        return Coupon.productSpecific(target, new BigDecimal(amount));
    }
}
