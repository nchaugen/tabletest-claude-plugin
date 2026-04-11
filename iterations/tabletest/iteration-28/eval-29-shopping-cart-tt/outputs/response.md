Here's the complete test class:

```java
import io.tabletest.junit.Description;
import io.tabletest.junit.TableTest;
import io.tabletest.junit.TypeConverter;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ShoppingCartTest {

    private final ShoppingCartService service = new ShoppingCartService();

    // ─── Add item ─────────────────────────────────────────────────────────────

    @DisplayName("Add item to cart")
    @Description("""
            Price is always resolved from the catalogue — the customer supplies only a
            product ID and quantity.
            Open: if a product already exists in the cart, does addItem accumulate the
            quantity or replace it? Assumed here: each call adds a new entry; revise if
            accumulation is intended.
            """)
    @TableTest("""
            Scenario              | Cart Before   | Product | Qty | Catalogue           | Cart After?             | Message?
            Add to empty cart     | [:]           | widget  | 2   | [widget: 10.00]     | [widget: 2]             | Added 2x widget
            Add to non-empty cart | [widget: 1]   | gadget  | 1   | [gadget: 5.00]      | [widget: 1, gadget: 1]  | Added 1x gadget
            Add multiple units    | [:]           | widget  | 3   | [widget: 10.00]     | [widget: 3]             | Added 3x widget
            """)
    void addItemToCart(Map<String, Integer> before, String product, int qty,
                       Map<String, String> cataloguePrices,
                       Map<String, Integer> expectedContents, String expectedMessage) {
        Cart cart = cartFrom(before);
        CartResult result = service.addItem(cart, product, qty, catalogueFrom(cataloguePrices));
        assertAll(
                () -> assertEquals(expectedMessage, result.getMessage()),
                () -> assertEquals(expectedContents, cartContents(result.getCart()))
        );
    }

    // ─── Remove item ──────────────────────────────────────────────────────────

    @DisplayName("Remove item from cart")
    @Description("Open: exact wording for 'item not found' — using 'Item not found: {id}' below.")
    @TableTest("""
            Scenario                    | Cart Before              | Product | Cart After?             | Message?
            Remove only item            | [widget: 1]              | widget  | [:]                     | Removed widget
            Remove from multi-item cart | [widget: 1, gadget: 1]   | widget  | [gadget: 1]             | Removed widget
            Remove item not in cart     | [widget: 1]              | gadget  | [widget: 1]             | Item not found: gadget
            Remove from empty cart      | [:]                      | widget  | [:]                     | Item not found: widget
            """)
    void removeItemFromCart(Map<String, Integer> before, String product,
                             Map<String, Integer> expectedContents, String expectedMessage) {
        Cart cart = cartFrom(before);
        CartResult result = service.removeItem(cart, product);
        assertAll(
                () -> assertEquals(expectedMessage, result.getMessage()),
                () -> assertEquals(expectedContents, cartContents(result.getCart()))
        );
    }

    // ─── Apply coupon ─────────────────────────────────────────────────────────

    @DisplayName("Apply coupon to cart")
    @Description("""
            Only one coupon is active at a time. A valid new code replaces the existing
            coupon; expired or nonexistent codes are rejected and the active coupon is
            preserved unchanged.
            Coupon store column format: [CODE: TYPE:value]
              PERCENT:10       → 10% off whole cart
              FIXED:5.00       → $5.00 off whole cart
              PRODUCT:widget:20 → 20% off widget line only
              EXPIRED          → marks the coupon as already expired
            Open: exact success-message wording per coupon type — assumed below, confirm
            with UX.
            """)
    @TableTest("""
            Scenario                           | Active Before | Code    | Coupon Store                                  | Active After? | Message?
            Apply first coupon                 |               | SAVE10  | [SAVE10: PERCENT:10]                          | SAVE10        | Coupon applied: 10% off
            Replace active with percent coupon | FIXED5        | SAVE10  | [FIXED5: FIXED:5.00, SAVE10: PERCENT:10]      | SAVE10        | Coupon applied: 10% off
            Replace active with fixed coupon   | SAVE10        | FIXED5  | [SAVE10: PERCENT:10, FIXED5: FIXED:5.00]      | FIXED5        | Coupon applied: $5.00 off
            Apply product-specific coupon      |               | WDGT20  | [WDGT20: PRODUCT:widget:20]                   | WDGT20        | Coupon applied: 20% off widget
            Nonexistent code, no active coupon |               | BOGUS   | [:]                                           |               | Invalid coupon code: BOGUS
            Nonexistent code, preserve active  | SAVE10        | BOGUS   | [SAVE10: PERCENT:10]                          | SAVE10        | Invalid coupon code: BOGUS
            Expired code, no active coupon     |               | GONE    | [GONE: EXPIRED]                               |               | Coupon expired: GONE
            Expired code, preserve active      | SAVE10        | GONE    | [SAVE10: PERCENT:10, GONE: EXPIRED]           | SAVE10        | Coupon expired: GONE
            """)
    void applyCouponToCart(String activeBefore, String code, Map<String, String> storeDescriptors,
                            String expectedActiveCode, String expectedMessage) {
        Cart cart = new Cart();
        cart.setActiveCouponCode(activeBefore);
        CouponStore store = couponStoreFrom(storeDescriptors);
        CartResult result = service.applyCoupon(cart, code, store);
        assertAll(
                () -> assertEquals(expectedMessage, result.getMessage()),
                () -> assertEquals(expectedActiveCode, result.getCart().getActiveCouponCode())
        );
    }

    // ─── Checkout ─────────────────────────────────────────────────────────────

    @DisplayName("Checkout cart")
    @Description("""
            Each item's requested quantity is verified against live inventory before
            the order is placed. The cart itself is not modified by checkout.
            Open: when multiple items are under-stocked, are all shortfalls reported
            in one message? Assumed below: yes, semicolon-separated in cart-iteration order.
            """)
    @TableTest("""
            Scenario                       | Cart                       | Inventory                     | Success? | Message?
            Empty cart                     | [:]                        | [:]                           | false    | Cart is empty
            Sufficient stock               | [widget: 2]                | [widget: 5]                   | true     | Order placed
            Exact stock                    | [widget: 5]                | [widget: 5]                   | true     | Order placed
            Insufficient stock             | [widget: 10]               | [widget: 3]                   | false    | Insufficient stock: widget (need 10, have 3)
            One of two items short         | [widget: 2, gadget: 10]    | [widget: 5, gadget: 3]        | false    | Insufficient stock: gadget (need 10, have 3)
            Both items short               | [widget: 10, gadget: 10]   | [widget: 3, gadget: 1]        | false    | Insufficient stock: widget (need 10, have 3); gadget (need 10, have 1)
            """)
    void checkoutCart(Map<String, Integer> cartItems, Map<String, Integer> stockLevels,
                       boolean expectedSuccess, String expectedMessage) {
        Cart cart = cartFrom(cartItems);
        InventoryService inventory = inventoryFrom(stockLevels);
        CheckoutResult result = service.checkout(cart, inventory);
        assertAll(
                () -> assertEquals(expectedSuccess, result.isSuccess()),
                () -> assertEquals(expectedMessage, result.getMessage())
        );
    }

    // ─── Calculate total ──────────────────────────────────────────────────────

    @DisplayName("Calculate cart total")
    @Description("""
            Total = sum(qty × price) − discount, floored at zero.
            Percentage coupon: discount = subtotal × pct / 100.
            Fixed coupon: discount = fixed amount; result floored at zero if discount > subtotal.
            Product coupon: discount = qty × price × pct / 100 for the named product only.
            Subtotal? is a traceability column — it shows the pre-discount sum so each Total?
            value is independently verifiable without arithmetic.
            Subtotal is asserted by calling calculateTotal with no active coupon (null).
            """)
    @TableTest("""
            Scenario                       | Cart                       | Catalogue                         | Coupon             | Subtotal? | Total?
            No coupon                      | [widget: 2]                | [widget: 10.00]                   |                    | 20.00     | 20.00
            Percentage off                 | [widget: 2]                | [widget: 10.00]                   | PERCENT:10         | 20.00     | 18.00
            Fixed amount off               | [widget: 2]                | [widget: 10.00]                   | FIXED:5.00         | 20.00     | 15.00
            Fixed coupon equals subtotal   | [widget: 2]                | [widget: 5.00]                    | FIXED:10.00        | 10.00     | 0.00
            Fixed coupon exceeds subtotal  | [widget: 1]                | [widget: 3.00]                    | FIXED:10.00        | 3.00      | 0.00
            Product-specific discount      | [widget: 2, gadget: 1]     | [widget: 10.00, gadget: 5.00]     | PRODUCT:widget:50  | 25.00     | 15.00
            Multiple items, no coupon      | [widget: 2, gadget: 3]     | [widget: 10.00, gadget: 5.00]     |                    | 35.00     | 35.00
            """)
    void calculateCartTotal(Map<String, Integer> cartItems, Map<String, String> cataloguePrices,
                             Coupon coupon, BigDecimal expectedSubtotal, BigDecimal expectedTotal) {
        Cart cart = cartFrom(cartItems);
        ProductCatalogue catalogue = catalogueFrom(cataloguePrices);
        assertAll(
                () -> assertEquals(expectedSubtotal, service.calculateTotal(cart, null, catalogue),
                        "subtotal (no coupon)"),
                () -> assertEquals(expectedTotal, service.calculateTotal(cart, coupon, catalogue),
                        "total after coupon")
        );
    }

    // ─── Type converters ──────────────────────────────────────────────────────

    @TypeConverter
    public static Coupon couponFromString(String s) {
        if (s == null || s.isBlank()) return null;
        String[] parts = s.split(":");
        return switch (parts[0]) {
            case "PERCENT" -> Coupon.percentOff(new BigDecimal(parts[1]));
            case "FIXED"   -> Coupon.fixedOff(new BigDecimal(parts[1]));
            case "PRODUCT" -> Coupon.productOff(parts[1], new BigDecimal(parts[2]));
            default        -> throw new IllegalArgumentException("Unknown coupon descriptor: " + s);
        };
    }

    // ─── Test helpers ─────────────────────────────────────────────────────────

    private static Cart cartFrom(Map<String, Integer> items) {
        Cart cart = new Cart();
        // Assumes Cart exposes addItemDirectly(String productId, int qty) for test setup,
        // bypassing catalogue lookup — prices are not stored in the cart.
        items.forEach(cart::addItemDirectly);
        return cart;
    }

    private static Map<String, Integer> cartContents(Cart cart) {
        Map<String, Integer> map = new LinkedHashMap<>();
        cart.getItems().forEach(item -> map.put(item.getProductId(), item.getQuantity()));
        return map;
    }

    private static ProductCatalogue catalogueFrom(Map<String, String> prices) {
        return productId -> new BigDecimal(prices.get(productId));
    }

    private static CouponStore couponStoreFrom(Map<String, String> descriptors) {
        Map<String, Coupon> coupons = new HashMap<>();
        descriptors.forEach((code, descriptor) -> {
            if ("EXPIRED".equals(descriptor)) {
                coupons.put(code, Coupon.expired(code));
            } else {
                coupons.put(code, couponFromString(descriptor));
            }
        });
        return code -> Optional.ofNullable(coupons.get(code));
    }

    private static InventoryService inventoryFrom(Map<String, Integer> stock) {
        return productId -> stock.getOrDefault(productId, 0);
    }
}
```

---

A few things to note when wiring this up:

**Assumptions about your domain types** — the test helpers assume:
- `Cart.addItemDirectly(String productId, int qty)` exists for test setup (bypassing catalogue). If Cart is immutable or constructed differently, adjust `cartFrom`.
- `Cart.getActiveCouponCode()` / `Cart.setActiveCouponCode(String)` exist for the coupon-replacement tests.
- `CartResult.getCart()`, `CartResult.getMessage()` and `CheckoutResult.isSuccess()`, `CheckoutResult.getMessage()` exist.
- `Coupon.percentOff(BigDecimal)`, `Coupon.fixedOff(BigDecimal)`, `Coupon.productOff(String, BigDecimal)`, `Coupon.expired(String)` are factory methods.
- `CouponStore` is a `Function<String, Optional<Coupon>>` or similar single-method interface.
- `InventoryService` is a `Function<String, Integer>` or similar.

**Open questions captured in `@Description`:**
1. Does `addItem` accumulate quantity for duplicate products or add a new line?
2. Exact UX message wording per coupon type (% off, $ off, product off).
3. Multi-shortfall checkout message format — all listed vs. first-only.