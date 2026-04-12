Here is the complete `ShoppingCartTest.java`:

```java
package com.example.shop;

import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ShoppingCartTest {

    // ── addItem: price taken from catalogue, not supplied by customer ─────────

    @TableTest("""
            Scenario                  | Product ID | Qty | Catalogue                            | Cart items before | Items after?                | Stored price? | Message?
            Add to empty cart         | widget-1   | 2   | [widget-1: 10.00]                    | [:]               | [widget-1: 2]               | 10.00         | Added 2x widget-1
            Add to non-empty cart     | gadget-2   | 1   | [widget-1: 10.00, gadget-2: 15.00]   | [widget-1: 2]     | [widget-1: 2, gadget-2: 1]  | 15.00         | Added 1x gadget-2
            Add multiple quantity     | widget-1   | 5   | [widget-1: 10.00]                    | [:]               | [widget-1: 5]               | 10.00         | Added 5x widget-1
            """)
    void addItemStoresCataloguePriceAndReturnsMessage(
            String productId, int qty,
            Map<String, BigDecimal> cataloguePrices,
            Map<String, Integer> cartItemsBefore,
            Map<String, Integer> expectedItems,
            BigDecimal expectedPrice,
            String expectedMessage) {
        Cart cart = buildCart(cartItemsBefore, cataloguePrices);
        ProductCatalogue catalogue = buildCatalogue(cataloguePrices);

        CartResult result = new ShoppingCartService().addItem(cart, productId, qty, catalogue);

        assertAll(
            () -> assertEquals(expectedMessage, result.getMessage()),
            () -> assertEquals(expectedItems, itemQuantities(result.getCart())),
            () -> assertEquals(0, expectedPrice.compareTo(itemPrice(result.getCart(), productId)))
        );
    }

    // ── removeItem: item removed, or error when not present ───────────────────

    @TableTest("""
            Scenario                  | Cart items                    | Product ID | Items after?               | Message?
            Remove only item          | [widget-1: 2]                 | widget-1   | [:]                        | Removed widget-1
            Remove one of two items   | [widget-1: 2, gadget-2: 1]   | gadget-2   | [widget-1: 2]              | Removed gadget-2
            Remove item not in cart   | [widget-1: 2]                 | gadget-2   | [widget-1: 2]              | "Item not in cart: gadget-2"
            Remove from empty cart    | [:]                           | widget-1   | [:]                        | "Item not in cart: widget-1"
            """)
    void removeItemUpdatesCartAndReturnsMessage(
            Map<String, Integer> cartItems,
            String productId,
            Map<String, Integer> expectedItems,
            String expectedMessage) {
        Cart cart = buildCartWithDefaultPrices(cartItems);

        CartResult result = new ShoppingCartService().removeItem(cart, productId);

        assertAll(
            () -> assertEquals(expectedMessage, result.getMessage()),
            () -> assertEquals(expectedItems, itemQuantities(result.getCart()))
        );
    }

    // ── applyCoupon: applied/replaced on valid code, rejected on invalid ──────

    @Description("""
        Valid coupons become the active coupon regardless of whether one was already set.
        Invalid or expired codes leave the active coupon unchanged.
        Discount calculation for each coupon type is covered by calculateTotal tests.
        """)
    @TableTest("""
            Scenario                      | Code     | Coupon in store                           | Previous active code | Active code after? | Message?
            Percentage off whole cart     | SAVE10   | "SAVE10=PERCENTAGE:0.10:valid"            |                      | SAVE10             | "Coupon applied: 10% off"
            Fixed amount off              | FLAT5    | "FLAT5=FIXED:5.00:valid"                  |                      | FLAT5              | "Coupon applied: $5.00 off"
            Product-specific discount     | WIDGET20 | "WIDGET20=PRODUCT:0.20:widget-1:valid"    |                      | WIDGET20           | "Coupon applied: 20% off widget-1"
            New coupon replaces existing  | SAVE10   | "SAVE10=PERCENTAGE:0.10:valid"            | FLAT5                | SAVE10             | "Coupon applied: 10% off"
            Expired coupon                | EXPIRED10| "EXPIRED10=PERCENTAGE:0.10:expired"       | FLAT5                | FLAT5              | Coupon expired
            Nonexistent code              | FAKE99   |                                           |                      |                    | Invalid coupon code
            """)
    void couponApplicationResult(
            String code, CouponStore couponStore, String previousCode,
            String expectedCode, String expectedMessage) {
        Cart cart = buildCartWithActiveCoupon(previousCode);

        CartResult result = new ShoppingCartService().applyCoupon(cart, code, couponStore);

        assertAll(
            () -> assertEquals(expectedMessage, result.getMessage()),
            () -> assertEquals(expectedCode, result.getCart().getActiveCouponCode())
        );
    }

    @TypeConverter
    public static CouponStore parseCouponStore(String input) {
        if (input == null || input.isBlank()) {
            return CouponStore.empty();
        }
        String[] parts = input.split("=", 2);
        String code = parts[0];
        String[] details = parts[1].split(":");
        CouponType type = CouponType.valueOf(details[0]);
        BigDecimal value = new BigDecimal(details[1]);
        boolean expired;
        String productId = null;
        if (type == CouponType.PRODUCT) {
            productId = details[2];
            expired = "expired".equals(details[3]);
        } else {
            expired = "expired".equals(details[2]);
        }
        return CouponStore.of(new Coupon(code, type, value, productId, expired));
    }

    // ── checkout: empty cart rejected, insufficient stock fails with details ──

    @TableTest("""
            Scenario                    | Cart items                    | Inventory                        | Success? | Message?
            Empty cart                  | [:]                           | [widget-1: 5]                    | false    | Cannot checkout empty cart
            Sufficient stock            | [widget-1: 2]                 | [widget-1: 5]                    | true     | Order placed
            One item short              | [widget-1: 10]                | [widget-1: 3]                    | false    | "Insufficient stock: widget-1 (need 10, have 3)"
            One item short, one ok      | [widget-1: 2, gadget-2: 10]  | [widget-1: 5, gadget-2: 3]      | false    | "Insufficient stock: gadget-2 (need 10, have 3)"
            Two items short             | [widget-1: 10, gadget-2: 5]  | [widget-1: 3, gadget-2: 1]      | false    | "Insufficient stock: gadget-2 (need 5, have 1), widget-1 (need 10, have 3)"
            """)
    void checkoutVerifiesStockAndReturnsMessage(
            Map<String, Integer> cartItems,
            Map<String, Integer> inventory,
            boolean expectedSuccess,
            String expectedMessage) {
        Cart cart = buildCartWithDefaultPrices(cartItems);
        InventoryService inventoryService = buildInventory(inventory);

        CheckoutResult result = new ShoppingCartService().checkout(cart, inventoryService);

        assertAll(
            () -> assertEquals(expectedSuccess, result.isSuccess()),
            () -> assertEquals(expectedMessage, result.getMessage())
        );
    }

    // ── calculateTotal: sum × qty, minus coupon discount, floored at zero ─────

    @Description("""
        Product-specific coupons discount only the target product; other items are charged at full price.
        The floor of zero prevents negative totals when the coupon value exceeds the cart subtotal.
        """)
    @TableTest("""
            Scenario                       | Cart items                    | Catalogue                            | Coupon                   | Total?
            No coupon                      | [widget-1: 2]                 | [widget-1: 10.00]                    |                          | 20.00
            Percentage off whole cart      | [widget-1: 2]                 | [widget-1: 10.00]                    | "PERCENTAGE:0.10"        | 18.00
            Fixed amount off               | [widget-1: 2]                 | [widget-1: 10.00]                    | "FIXED:5.00"             | 15.00
            Product-specific on target     | [widget-1: 2, gadget-2: 1]   | [widget-1: 10.00, gadget-2: 15.00]  | "PRODUCT:0.20:widget-1"  | 31.00
            Product-specific on non-target | [widget-1: 2, gadget-2: 1]   | [widget-1: 10.00, gadget-2: 15.00]  | "PRODUCT:0.20:gadget-2"  | 32.00
            Discount exceeds total         | [widget-1: 1]                 | [widget-1: 10.00]                    | "FIXED:50.00"            | 0.00
            """)
    void calculateTotalAppliesDiscountFlooredAtZero(
            Map<String, Integer> cartItems,
            Map<String, BigDecimal> cataloguePrices,
            Coupon activeCoupon,
            BigDecimal expectedTotal) {
        Cart cart = buildCart(cartItems, cataloguePrices);
        ProductCatalogue catalogue = buildCatalogue(cataloguePrices);

        BigDecimal total = new ShoppingCartService().calculateTotal(cart, activeCoupon, catalogue);

        assertEquals(0, expectedTotal.compareTo(total));
    }

    @TypeConverter
    public static Coupon parseCoupon(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }
        String[] parts = input.split(":");
        CouponType type = CouponType.valueOf(parts[0]);
        BigDecimal value = new BigDecimal(parts[1]);
        String productId = (type == CouponType.PRODUCT) ? parts[2] : null;
        return new Coupon(null, type, value, productId, false);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Cart buildCart(Map<String, Integer> items, Map<String, BigDecimal> prices) {
        Cart cart = new Cart();
        if (items != null) {
            items.forEach((id, qty) -> cart.addItem(new CartItem(id, qty, prices.get(id))));
        }
        return cart;
    }

    private Cart buildCartWithDefaultPrices(Map<String, Integer> items) {
        Cart cart = new Cart();
        if (items != null) {
            items.forEach((id, qty) -> cart.addItem(new CartItem(id, qty, BigDecimal.ZERO)));
        }
        return cart;
    }

    private Cart buildCartWithActiveCoupon(String couponCode) {
        Cart cart = new Cart();
        if (couponCode != null && !couponCode.isBlank()) {
            cart.setActiveCoupon(new Coupon(couponCode, CouponType.FIXED, BigDecimal.ONE, null, false));
        }
        return cart;
    }

    private ProductCatalogue buildCatalogue(Map<String, BigDecimal> prices) {
        return productId -> prices.get(productId);
    }

    private InventoryService buildInventory(Map<String, Integer> stock) {
        return productId -> stock.getOrDefault(productId, 0);
    }

    private Map<String, Integer> itemQuantities(Cart cart) {
        Map<String, Integer> result = new LinkedHashMap<>();
        cart.getItems().forEach(item -> result.put(item.getProductId(), item.getQuantity()));
        return result;
    }

    private BigDecimal itemPrice(Cart cart, String productId) {
        return cart.getItems().stream()
            .filter(item -> item.getProductId().equals(productId))
            .map(CartItem::getPrice)
            .findFirst()
            .orElseThrow();
    }
}
```

---

**Five tables, one concern each:**

| Method | Concern | Rows |
|---|---|---|
| `addItemStoresCataloguePriceAndReturnsMessage` | Price comes from catalogue; `Stored price?` makes this explicit | 3 |
| `removeItemUpdatesCartAndReturnsMessage` | Cart updated + error when item absent | 4 |
| `couponApplicationResult` | All 3 types applied, replacement, expired, nonexistent — unified via a `@TypeConverter` for `CouponStore` | 6 |
| `checkoutVerifiesStockAndReturnsMessage` | Empty cart, stock ok, single shortage, partial shortage, multi-shortage | 5 |
| `calculateTotalAppliesDiscountFlooredAtZero` | Each discount type + floor-at-zero; product-specific tested against both target and non-target | 6 |

**Key design choices:**

- `Catalogue` and `Inventory` use `Map<String, BigDecimal/Integer>` — built-in JUnit map conversion, no custom converter needed.
- `Coupon in store` encodes the full coupon as `CODE=TYPE:VALUE[:PRODUCT]:valid|expired` — a single `@TypeConverter(String → CouponStore)` handles all variants; blank → empty store (nonexistent code case).
- `Coupon` for `calculateTotal` uses `TYPE:VALUE[:PRODUCT]` with a `@TypeConverter(String → Coupon)`; blank → null (no coupon).
- Multi-item shortage message lists items alphabetically — this is a contract assertion; the implementation must sort.
- `calculateTotal` separates concern from `applyCoupon`: the coupon table verifies *which code becomes active*, not the discount math.