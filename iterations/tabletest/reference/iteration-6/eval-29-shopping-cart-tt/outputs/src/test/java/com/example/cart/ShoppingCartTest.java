package com.example.cart;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShoppingCartTest {

    private static final String ACTIVE_COUPON_KEY = "coupon";
    private static final String EXPIRED = "expired";
    private static final Coupon COUPON_OF_IMMATERIAL_WORTH = Coupon.percentage(10);

    @DisplayName("Adds items at catalogue prices")
    @Description("""
        Prices come from the catalogue, so the product column carries a name the catalogue knows.
        Two readings the requirement leaves open are settled here rather than in the method body:
        adding a product already in the cart accumulates rather than replaces, and a quantity that
        is not positive is refused. Rejection wording is this table's assumption — the requirement
        fixes only that every operation answers with a message.
        """)
    @TableTest("""
        Scenario                            | Catalogue                    | Cart Before | Product | Quantity | Success? | Message?                  | Cart After?
        New product, empty cart             | [widget: 2.50]               | [:]         | widget  | 2        | true     | Added 2x widget           | [widget: 2]
        Product already in the cart         | [widget: 2.50]               | [widget: 1] | widget  | 2        | true     | Added 2x widget           | [widget: 3]
        Second product beside the first     | [widget: 2.50, gadget: 9.99] | [widget: 1] | gadget  | 3        | true     | Added 3x gadget           | [widget: 1, gadget: 3]
        Product absent from the catalogue   | [widget: 2.50]               | [:]         | gadget  | 1        | false    | Unknown product: gadget   | [:]
        Quantity of zero                    | [widget: 2.50]               | [:]         | widget  | 0        | false    | Quantity must be positive | [:]
        Negative quantity                   | [widget: 2.50]               | [widget: 1] | widget  | -2       | false    | Quantity must be positive | [widget: 1]
        """)
    void addsItemsAtCataloguePrices(
            ProductCatalogue catalogue,
            Cart cartBefore,
            String product,
            int quantity,
            boolean success,
            String message,
            Cart cartAfter) {
        CartResult result = CartService.addItem(cartBefore, product, quantity, catalogue);

        assertEquals(success, result.success());
        assertEquals(message, result.message());
        assertEquals(cartAfter, result.cart());
    }

    @DisplayName("Removes items already in the cart")
    @Description("""
        Removal takes a whole line rather than a quantity, which is what the requirement describes.
        Rejection wording is this table's assumption.
        """)
    @TableTest("""
        Scenario                  | Cart Before            | Product | Success? | Message?                 | Cart After?
        Only item in the cart     | [widget: 2]            | widget  | true     | Removed widget           | [:]
        One of several items      | [widget: 2, gadget: 1] | gadget  | true     | Removed gadget           | [widget: 2]
        Product not in the cart   | [widget: 2]            | gadget  | false    | Not in the cart: gadget  | [widget: 2]
        Empty cart                | [:]                    | widget  | false    | Not in the cart: widget  | [:]
        """)
    void removesItemsAlreadyInTheCart(
            Cart cartBefore,
            String product,
            boolean success,
            String message,
            Cart cartAfter) {
        CartResult result = CartService.removeItem(cartBefore, product);

        assertEquals(success, result.success());
        assertEquals(message, result.message());
        assertEquals(cartAfter, result.cart());
    }

    @DisplayName("Keeps one coupon active at a time")
    @Description("""
        This table decides which coupon is active, never what a coupon is worth — discount sizes are
        the totalling table's business, so the store column records only whether a code is known and
        current. The cart carries its active coupon, so the before and after columns show the
        replacement rule directly: a valid code takes over, and an expired or unrecognised one leaves
        the previous coupon exactly where it was.
        """)
    @TableTest("""
        Scenario                            | Cart Before      | Coupon Code | Coupon Store                  | Success? | Message?               | Cart After?
        No coupon active, code valid        | [:]              | SAVE10      | [SAVE10: valid]               | true     | Coupon applied: SAVE10 | [coupon: SAVE10]
        Coupon active, new code valid       | [coupon: OLD5]   | SAVE10      | [OLD5: valid, SAVE10: valid]  | true     | Coupon applied: SAVE10 | [coupon: SAVE10]
        Coupon active, code expired         | [coupon: SAVE10] | LAPSED      | [SAVE10: valid, LAPSED: expired] | false | Coupon expired         | [coupon: SAVE10]
        Coupon active, code unrecognised    | [coupon: SAVE10] | FAKE        | [SAVE10: valid]               | false    | Unknown coupon: FAKE   | [coupon: SAVE10]
        No coupon active, code expired      | [:]              | LAPSED      | [LAPSED: expired]             | false    | Coupon expired         | [:]
        No coupon active, code unrecognised | [:]              | FAKE        | [:]                           | false    | Unknown coupon: FAKE   | [:]
        """)
    void keepsOneCouponActiveAtATime(
            Cart cartBefore,
            String couponCode,
            CouponStore couponStore,
            boolean success,
            String message,
            Cart cartAfter) {
        CartResult result = CartService.applyCoupon(cartBefore, couponCode, couponStore);

        assertEquals(success, result.success());
        assertEquals(message, result.message());
        assertEquals(cartAfter, result.cart());
    }

    @DisplayName("Totals the cart net of the active coupon")
    @Description("""
        The total is the sum of quantity times catalogue price less the coupon, and it never goes
        below zero. A blank coupon cell means none is active. A product coupon takes its amount off
        the cart once rather than once per unit, and has no effect when its target is not in the
        cart — both readings the requirement leaves open. Money is compared by value, so 16.00 and
        16.0 are the same total.
        """)
    @TableTest("""
        Scenario                             | Catalogue                     | Cart                   | Coupon                                | Total?
        No coupon, one line                  | [widget: 10.00]               | [widget: 3]            |                                       | 30.00
        No coupon, several lines             | [widget: 10.00, gadget: 5.00] | [widget: 2, gadget: 4] |                                       | 40.00
        Percentage off the whole cart        | [widget: 10.00]               | [widget: 2]            | 20% off cart                          | 16.00
        Fixed amount below the subtotal      | [widget: 10.00]               | [widget: 2]            | $5 off                                | 15.00
        Fixed amount equal to the subtotal   | [widget: 10.00]               | [widget: 2]            | $20 off                               | 0.00
        Fixed amount above the subtotal      | [widget: 10.00]               | [widget: 2]            | $25 off                               | 0.00
        Product coupon, target in the cart   | [widget: 10.00, gadget: 5.00] | [widget: 2, gadget: 1] | $5 off widget                         | 20.00
        Product coupon, target not in cart   | [widget: 10.00, gadget: 5.00] | [gadget: 1]            | $5 off widget                         | 5.00
        Empty cart, whatever the coupon      | [widget: 10.00]               | [:]                    | {20% off cart, $5 off, $5 off widget} | 0.00
        """)
    void totalsTheCartNetOfTheActiveCoupon(
            ProductCatalogue catalogue,
            Cart cart,
            Coupon coupon,
            BigDecimal total) {
        BigDecimal calculated = CartService.calculateTotal(cart, coupon, catalogue);

        assertEquals(0, total.compareTo(calculated));
    }

    @DisplayName("Verifies stock before checking out")
    @Description("""
        The requirement fixes that a failure names what is short, never how the sentence reads, so
        each row lists the words the message must contain and the assertion checks for those rather
        than for an exact wording. An empty list means no fragment is required.
        """)
    @TableTest("""
        Scenario                       | Cart                   | Stock                  | Success? | Message Mentions?
        Empty cart                     | [:]                    | [:]                    | false    | [empty]
        Stock exactly meets demand     | [widget: 5]            | [widget: 5]            | true     | []
        Short by one unit              | [widget: 6]            | [widget: 5]            | false    | [widget]
        One of two products short      | [widget: 2, gadget: 5] | [widget: 5, gadget: 2] | false    | [gadget]
        Both products short            | [widget: 6, gadget: 5] | [widget: 5, gadget: 2] | false    | [widget, gadget]
        Product absent from inventory  | [widget: 1]            | [:]                    | false    | [widget]
        """)
    void verifiesStockBeforeCheckingOut(
            Cart cart,
            InventoryService stock,
            boolean success,
            List<String> messageMentions) {
        CheckoutResult result = CartService.checkout(cart, stock);

        assertEquals(success, result.success());
        assertTrue(messageMentions.stream().allMatch(result.message()::contains));
    }

    /**
     * The cart cell carries its line quantities and, under the key {@code coupon}, the code that is
     * active on it — so one column shows the whole cart state a row starts or ends with.
     */
    @TypeConverter
    public static Cart parseCart(Map<String, String> cartState) {
        Map<String, Integer> lines = new LinkedHashMap<>();
        cartState.forEach((key, value) -> {
            if (!ACTIVE_COUPON_KEY.equals(key)) {
                lines.put(key, Integer.valueOf(value));
            }
        });
        return Cart.withItems(lines).withActiveCouponCode(cartState.get(ACTIVE_COUPON_KEY));
    }

    @TypeConverter
    public static ProductCatalogue parseCatalogue(Map<String, BigDecimal> prices) {
        return ProductCatalogue.from(prices);
    }

    @TypeConverter
    public static InventoryService parseStock(Map<String, Integer> quantities) {
        return productId -> quantities.getOrDefault(productId, 0);
    }

    /**
     * What the store holds against each code. Only the standing of a code matters to the coupon
     * table, so a current code maps to a coupon whose worth is immaterial there.
     */
    @TypeConverter
    public static CouponStore parseCouponStore(Map<String, String> standingByCode) {
        Map<String, Coupon> current = new LinkedHashMap<>();
        Set<String> expired = new LinkedHashSet<>();
        standingByCode.forEach((code, standing) -> {
            if (EXPIRED.equals(standing)) {
                expired.add(code);
            } else {
                current.put(code, COUPON_OF_IMMATERIAL_WORTH);
            }
        });
        return CouponStore.of(current, expired);
    }

    @TypeConverter
    public static Coupon parseCoupon(String offer) {
        if (offer.endsWith("% off cart")) {
            return Coupon.percentage(Integer.parseInt(offer.substring(0, offer.indexOf('%'))));
        }
        String[] words = offer.split(" ");
        BigDecimal amount = new BigDecimal(words[0].substring(1));
        return words.length > 2
                ? Coupon.productSpecific(words[2], amount)
                : Coupon.fixed(amount);
    }
}
