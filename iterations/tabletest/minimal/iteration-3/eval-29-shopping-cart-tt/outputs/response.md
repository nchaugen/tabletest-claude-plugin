Here are the TableTests for the shopping cart feature:

```java
package com.example.shop;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ShoppingCartTest {

    // Product prices used across all tests
    private static final ProductCatalogue CATALOGUE = ProductCatalogue.of(Map.of(
        "WIDGET",    new BigDecimal("10.00"),
        "GADGET",    new BigDecimal("25.00"),
        "DOOHICKEY", new BigDecimal("5.00")
    ));

    // Coupons available in the test store:
    //   SAVE10    = 10% off whole cart   (valid)
    //   FLAT5     = $5.00 off whole cart (valid)
    //   WIDGET50  = 50% off WIDGET       (valid, product-specific)
    //   EXPIRED20 = 20% off whole cart   (expired)
    private static final CouponStore COUPON_STORE = CouponStore.of(Map.of(
        "SAVE10",    Coupon.percentOff("SAVE10", 10),
        "FLAT5",     Coupon.fixedOff("FLAT5", new BigDecimal("5.00")),
        "WIDGET50",  Coupon.productOff("WIDGET50", "WIDGET", 50),
        "EXPIRED20", Coupon.expired("EXPIRED20")
    ));

    // -------------------------------------------------------------------------
    // addItem
    // -------------------------------------------------------------------------

    @DisplayName("Add item to cart")
    @Description("""
        Catalogue: WIDGET=10.00, GADGET=25.00, DOOHICKEY=5.00.
        Adding a product already in the cart increases its quantity rather than creating a duplicate entry.
        """)
    @TableTest("""
        Scenario                      | Cart Before            | Product | Qty | Cart After?                  | Message?
        New item, empty cart          | [:]                    | WIDGET  | 2   | [WIDGET: 2]                  | Added 2x Widget
        New item alongside existing   | [WIDGET: 1]            | GADGET  | 1   | [WIDGET: 1, GADGET: 1]       | Added 1x Gadget
        Increase quantity of existing | [WIDGET: 2]            | WIDGET  | 3   | [WIDGET: 5]                  | Added 3x Widget
        """)
    void addItemToCart(Map<String, Integer> cartBefore, String productId, int qty,
                       Map<String, Integer> expectedItems, String message) {
        Cart cart = cartFrom(cartBefore);
        CartResult result = CartService.addItem(cart, productId, qty, CATALOGUE);
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getMessage()).isEqualTo(message);
        assertThat(itemsOf(result.getCart())).isEqualTo(expectedItems);
    }

    // -------------------------------------------------------------------------
    // removeItem
    // -------------------------------------------------------------------------

    @TableTest("""
        Scenario                   | Cart Before             | Product | Success? | Message?                   | Cart After?
        Remove the only item       | [WIDGET: 2]             | WIDGET  | true     | Removed Widget             | [:]
        Remove one of two items    | [WIDGET: 1, GADGET: 1]  | GADGET  | true     | Removed Gadget             | [WIDGET: 1]
        Remove item not in cart    | [WIDGET: 2]             | GADGET  | false    | GADGET is not in your cart | [WIDGET: 2]
        """)
    void removeItemFromCart(Map<String, Integer> cartBefore, String productId,
                             boolean success, String message, Map<String, Integer> expectedItems) {
        Cart cart = cartFrom(cartBefore);
        CartResult result = CartService.removeItem(cart, productId);
        assertThat(result.isSuccess()).isEqualTo(success);
        assertThat(result.getMessage()).isEqualTo(message);
        assertThat(itemsOf(result.getCart())).isEqualTo(expectedItems);
    }

    // -------------------------------------------------------------------------
    // applyCoupon
    // -------------------------------------------------------------------------

    @Description("""
        Coupons in test store: SAVE10 (10% off cart), FLAT5 ($5 off), WIDGET50 (50% off WIDGET), EXPIRED20 (expired).
        BADCODE does not exist in the store.
        A blank Active Coupon means no coupon is currently applied.
        An invalid code must not replace the currently active coupon.
        """)
    @TableTest("""
        Scenario                              | Active Coupon | New Code  | Success? | Applied Coupon? | Message?
        Apply coupon when none is active      |               | SAVE10    | true     | SAVE10          | Coupon SAVE10 applied
        Replace active coupon with valid code | SAVE10        | FLAT5     | true     | FLAT5           | Coupon FLAT5 applied
        Expired code rejected                 | SAVE10        | EXPIRED20 | false    | SAVE10          | Coupon expired
        Nonexistent code rejected             | SAVE10        | BADCODE   | false    | SAVE10          | Invalid coupon code
        Expired code, no prior coupon         |               | EXPIRED20 | false    |                 | Coupon expired
        """)
    void applyCoupon(String activeCouponCode, String newCode,
                     boolean success, String expectedAppliedCode, String message) {
        Cart cart = cartWithCoupon(activeCouponCode);
        CartResult result = CartService.applyCoupon(cart, newCode, COUPON_STORE);
        assertThat(result.isSuccess()).isEqualTo(success);
        assertThat(result.getMessage()).isEqualTo(message);
        assertThat(couponCodeOf(result.getCart())).isEqualTo(expectedAppliedCode);
    }

    // -------------------------------------------------------------------------
    // checkout
    // -------------------------------------------------------------------------

    @Description("""
        Stock columns use [productId: available quantity].
        The shortage message lists every short item in order, each with the required and available quantities.
        """)
    @TableTest("""
        Scenario                    | Cart                    | Stock                   | Success? | Message?
        Empty cart                  | [:]                     | [:]                     | false    | Cart is empty
        All items in stock          | [WIDGET: 2, GADGET: 1]  | [WIDGET: 5, GADGET: 3]  | true     | Order placed
        Exact stock available       | [WIDGET: 3]             | [WIDGET: 3]             | true     | Order placed
        One item short              | [WIDGET: 3]             | [WIDGET: 2]             | false    | Insufficient stock: WIDGET (need 3, have 2)
        Two items short             | [WIDGET: 3, GADGET: 2]  | [WIDGET: 1, GADGET: 1]  | false    | Insufficient stock: WIDGET (need 3, have 1), GADGET (need 2, have 1)
        One short, one in stock     | [WIDGET: 3, GADGET: 1]  | [WIDGET: 1, GADGET: 5]  | false    | Insufficient stock: WIDGET (need 3, have 1)
        """)
    void checkout(Map<String, Integer> cartItems, Map<String, Integer> stock,
                  boolean success, String message) {
        Cart cart = cartFrom(cartItems);
        InventoryService inventory = inventoryFrom(stock);
        CheckoutResult result = CartService.checkout(cart, inventory);
        assertThat(result.isSuccess()).isEqualTo(success);
        assertThat(result.getMessage()).isEqualTo(message);
    }

    // -------------------------------------------------------------------------
    // calculateTotal
    // -------------------------------------------------------------------------

    @Description("""
        Catalogue: WIDGET=10.00, GADGET=25.00, DOOHICKEY=5.00.
        Coupon column format: PERCENT:<pct>, FIXED:<amount>, PRODUCT:<productId>:<pct>. Blank = no coupon.
        Product-specific discounts apply only to cart lines matching the target product.
        Total is floored at 0.00 when the discount exceeds the subtotal.
        """)
    @TableTest("""
        Scenario                                  | Cart                    | Coupon            | Total?
        No coupon                                 | [WIDGET: 2]             |                   | 20.00
        Percentage off whole cart                 | [WIDGET: 2]             | PERCENT:10        | 18.00
        Fixed amount off                          | [WIDGET: 2, GADGET: 1]  | FIXED:5.00        | 40.00
        Product discount on target item           | [WIDGET: 2]             | PRODUCT:WIDGET:50 | 10.00
        Product discount does not affect other items | [GADGET: 1]          | PRODUCT:WIDGET:50 | 25.00
        Product discount, mixed cart              | [WIDGET: 2, GADGET: 1]  | PRODUCT:WIDGET:50 | 35.00
        Discount floors total at zero             | [DOOHICKEY: 1]          | FIXED:10.00       | 0.00
        """)
    void calculateTotal(Map<String, Integer> cartItems, Coupon coupon, BigDecimal expectedTotal) {
        Cart cart = cartFrom(cartItems);
        assertThat(CartService.calculateTotal(cart, coupon, CATALOGUE))
            .isEqualByComparingTo(expectedTotal);
    }

    // -------------------------------------------------------------------------
    // Type converter
    // -------------------------------------------------------------------------

    @TypeConverter
    public static Coupon parseCoupon(String value) {
        if (value == null || value.isBlank()) return null;
        String[] parts = value.split(":");
        return switch (parts[0]) {
            case "PERCENT" -> Coupon.percentOff(value, Integer.parseInt(parts[1]));
            case "FIXED"   -> Coupon.fixedOff(value, new BigDecimal(parts[1]));
            case "PRODUCT" -> Coupon.productOff(value, parts[1], Integer.parseInt(parts[2]));
            default -> throw new IllegalArgumentException("Unknown coupon format: " + value);
        };
    }

    // -------------------------------------------------------------------------
    // Test helpers
    // -------------------------------------------------------------------------

    private static Cart cartFrom(Map<String, Integer> items) {
        Cart cart = new Cart();
        items.forEach(cart::addItemDirectly);
        return cart;
    }

    private static Cart cartWithCoupon(String couponCode) {
        Cart cart = new Cart();
        if (couponCode != null && !couponCode.isBlank()) {
            cart.setActiveCoupon(COUPON_STORE.find(couponCode));
        }
        return cart;
    }

    private static Map<String, Integer> itemsOf(Cart cart) {
        Map<String, Integer> result = new LinkedHashMap<>();
        cart.getItems().forEach(item -> result.put(item.getProductId(), item.getQuantity()));
        return result;
    }

    private static String couponCodeOf(Cart cart) {
        Coupon coupon = cart.getActiveCoupon();
        return coupon == null ? null : coupon.getCode();
    }

    private static InventoryService inventoryFrom(Map<String, Integer> stock) {
        return productId -> stock.getOrDefault(productId, 0);
    }
}
```

---

**Design decisions worth noting:**

**5 tables, one concern each:**
- `addItemToCart` — item merging and message format
- `removeItemFromCart` — happy path + the one error case (not-in-cart)
- `applyCoupon` — replacement logic and both rejection types in one table (same assertion code)
- `checkout` — empty cart + stock boundary (exact stock passes, anything less fails)
- `calculateTotal` — all three coupon types + the floor-at-zero rule

**`@TypeConverter` for `Coupon`** — the inline `PERCENT:10` / `FIXED:5.00` / `PRODUCT:WIDGET:50` encoding keeps the coupon type visible in the table without requiring a separate coupon-name lookup table. `Map<String, Integer>` for cart items and stock uses JUnit's built-in map conversion.

**Cart state** is represented as `[:]` (empty map) / `[WIDGET: 2, GADGET: 1]`. JUnit converts these to `Map<String, Integer>` natively; the test helper `cartFrom` builds the domain object.

**Expected totals verified** independently: `[WIDGET:2]` at 10.00 = 20.00 subtotal; PERCENT:10 → 18.00, FIXED:5.00 on 45.00 → 40.00, PRODUCT:WIDGET:50 on [WIDGET:2, GADGET:1] → 10+25=35.00, FIXED:10.00 on DOOHICKEY (5.00) → floored to 0.00. All correct.