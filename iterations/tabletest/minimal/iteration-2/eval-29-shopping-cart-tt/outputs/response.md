File write wasn't approved — here's the complete test class:

```java
package com.example.shop;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ShoppingCartTest {

    private static final ProductCatalogue CATALOGUE = ProductCatalogue.of(Map.of(
        "widget", new BigDecimal("10.00"),
        "gadget", new BigDecimal("25.00")
    ));

    // -------------------------------------------------------------------------
    // addItem
    // -------------------------------------------------------------------------

    @DisplayName("Add item to cart")
    @Description("""
        Catalogue fixed for all rows: widget = $10.00, gadget = $25.00.
        Open: behaviour when productId is absent from the catalogue is unspecified.
        Open: whether adding a product already in the cart increments the line quantity
              or replaces it is unspecified.
        """)
    @TableTest("""
        Scenario                      | Cart Items  | Product | Qty | Success? | Message?
        Add single item to empty cart | [:]         | widget  | 1   | true     | Added 1x widget
        Add multiple to empty cart    | [:]         | widget  | 2   | true     | Added 2x widget
        Add item alongside others     | [gadget: 1] | widget  | 3   | true     | Added 3x widget
        """)
    void addItemToCart(Map<String, Integer> cartItems, String product, int qty,
                       boolean success, String message) {
        Cart cart = new Cart(cartItems);
        CartResult result = CartService.addItem(cart, product, qty, CATALOGUE);
        assertThat(result.isSuccess()).isEqualTo(success);
        assertThat(result.getMessage()).isEqualTo(message);
    }

    // -------------------------------------------------------------------------
    // removeItem
    // -------------------------------------------------------------------------

    @DisplayName("Remove item from cart")
    @TableTest("""
        Scenario                     | Cart Items             | Product | Success? | Message?         | Cart Items After?
        Remove the only item         | [widget: 1]            | widget  | true     | Removed widget   | [:]
        Remove one of several items  | [widget: 2, gadget: 1] | widget  | true     | Removed widget   | [gadget: 1]
        Remove item not in cart      | [gadget: 1]            | widget  | false    | Item not in cart | [gadget: 1]
        Remove from empty cart       | [:]                    | widget  | false    | Item not in cart | [:]
        """)
    void removeItemFromCart(Map<String, Integer> cartItems, String product,
                            boolean success, String message,
                            Map<String, Integer> cartItemsAfter) {
        Cart cart = new Cart(cartItems);
        CartResult result = CartService.removeItem(cart, product);
        assertThat(result.isSuccess()).isEqualTo(success);
        assertThat(result.getMessage()).isEqualTo(message);
        assertThat(cart.getItems()).isEqualTo(cartItemsAfter);
    }

    // -------------------------------------------------------------------------
    // applyCoupon
    // -------------------------------------------------------------------------

    @DisplayName("Apply coupon code")
    @Description("""
        Code Status describes the coupon's state in the store:
          ACTIVE    = exists and not expired
          EXPIRED   = exists but past its expiry date
          NOT_FOUND = no record of the code
        Open: exact success message wording (e.g. "Coupon applied" vs "Coupon SAVE10 applied") is unspecified.
        """)
    @TableTest("""
        Scenario                                | Current Coupon | Code   | Code Status | Success? | Active Coupon After? | Message?
        Apply valid coupon to bare cart         |                | SAVE10 | ACTIVE      | true     | SAVE10               | Coupon applied
        Valid coupon replaces existing          | SAVE5          | SAVE10 | ACTIVE      | true     | SAVE10               | Coupon applied
        Expired code — cart unchanged           |                | OLD20  | EXPIRED     | false    |                      | Coupon expired
        Nonexistent code — cart unchanged       |                | FAKE   | NOT_FOUND   | false    |                      | Invalid coupon code
        Expired code does not replace existing  | SAVE5          | OLD20  | EXPIRED     | false    | SAVE5                | Coupon expired
        """)
    void applyCoupon(String currentCoupon, String code, CouponStatus codeStatus,
                     boolean success, String activeCouponAfter, String message) {
        Cart cart = cartWithCoupon(currentCoupon);
        CouponStore couponStore = couponStoreWith(code, codeStatus);
        CartResult result = CartService.applyCoupon(cart, code, couponStore);
        assertThat(result.isSuccess()).isEqualTo(success);
        assertThat(result.getMessage()).isEqualTo(message);
        assertThat(cart.getActiveCouponCode()).isEqualTo(activeCouponAfter);
    }

    // -------------------------------------------------------------------------
    // checkout
    // -------------------------------------------------------------------------

    @DisplayName("Checkout validates cart and stock")
    @Description("""
        Open: when multiple items are short, whether all shortfalls are listed in the
              message or only the first is unspecified.
        """)
    @TableTest("""
        Scenario                       | Cart Items             | Stock Levels            | Success? | Message?
        Empty cart is rejected         | [:]                    | [:]                     | false    | Cart is empty
        All items in stock             | [widget: 2, gadget: 1] | [widget: 5, gadget: 3]  | true     | Order confirmed
        Exactly enough stock           | [widget: 3]            | [widget: 3]             | true     | Order confirmed
        One item short                 | [widget: 3]            | [widget: 2]             | false    | Insufficient stock: widget (need 3, have 2)
        One of several items is short  | [widget: 3, gadget: 2] | [widget: 1, gadget: 5]  | false    | Insufficient stock: widget (need 3, have 1)
        """)
    void checkoutValidatesStock(Map<String, Integer> cartItems, Map<String, Integer> stockLevels,
                                boolean success, String message) {
        Cart cart = new Cart(cartItems);
        InventoryService inventory = new StubInventoryService(stockLevels);
        CheckoutResult result = CartService.checkout(cart, inventory);
        assertThat(result.isSuccess()).isEqualTo(success);
        assertThat(result.getMessage()).isEqualTo(message);
    }

    // -------------------------------------------------------------------------
    // calculateTotal — coupon discount types
    // -------------------------------------------------------------------------

    @DisplayName("Coupon discount type applied to cart total")
    @Description("""
        Catalogue fixed for all rows: widget = $10.00, gadget = $25.00.
        For PERCENTAGE and PRODUCT types, Value is percentage points (e.g. 10 = 10%).
        For FIXED, Value is a monetary amount deducted from the cart total.
        PRODUCT discount applies only to the line subtotal of the target product.
        These rows use amounts where the discount does not exceed the subtotal;
        see totalFloorsAtZero for the boundary behaviour.
        """)
    @TableTest("""
        Scenario                  | Cart Items             | Coupon Type | Value | Target Product | Total?
        No coupon                 | [widget: 2]            |             |       |                | 20.00
        Percentage off whole cart | [widget: 2, gadget: 1] | PERCENTAGE  | 10    |                | 40.50
        Fixed amount off          | [widget: 2]            | FIXED       | 3.00  |                | 17.00
        Product-specific discount | [widget: 2, gadget: 1] | PRODUCT     | 50    | widget         | 35.00
        """)
    void couponDiscountTypes(Map<String, Integer> cartItems, CouponType couponType,
                             BigDecimal couponValue, String targetProduct, BigDecimal total) {
        Cart cart = new Cart(cartItems);
        Coupon coupon = couponType != null ? Coupon.of(couponType, couponValue, targetProduct) : null;
        BigDecimal result = CartService.calculateTotal(cart, coupon, CATALOGUE);
        assertThat(result).isEqualByComparingTo(total);
    }

    // -------------------------------------------------------------------------
    // calculateTotal — floor at zero
    // -------------------------------------------------------------------------

    @DisplayName("Cart total is floored at zero when discount exceeds subtotal")
    @Description("""
        Catalogue fixed for all rows: widget = $10.00.
        Uses FIXED and PERCENTAGE coupon types; the floor applies to all types equally.
        """)
    @TableTest("""
        Scenario                         | Cart Items  | Coupon Type | Value | Total?
        Discount exactly equals subtotal | [widget: 1] | FIXED       | 10.00 | 0.00
        Discount exceeds subtotal        | [widget: 1] | FIXED       | 15.00 | 0.00
        Percentage over 100%             | [widget: 1] | PERCENTAGE  | 150   | 0.00
        """)
    void totalFloorsAtZero(Map<String, Integer> cartItems, CouponType couponType,
                           BigDecimal couponValue, BigDecimal total) {
        Cart cart = new Cart(cartItems);
        Coupon coupon = Coupon.of(couponType, couponValue, null);
        BigDecimal result = CartService.calculateTotal(cart, coupon, CATALOGUE);
        assertThat(result).isEqualByComparingTo(total);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static Cart cartWithCoupon(String couponCode) {
        Cart cart = new Cart(Map.of());
        if (couponCode != null) {
            cart.setActiveCouponCode(couponCode);
        }
        return cart;
    }

    private static CouponStore couponStoreWith(String code, CouponStatus status) {
        return switch (status) {
            case ACTIVE    -> CouponStore.withActiveCoupon(code);
            case EXPIRED   -> CouponStore.withExpiredCoupon(code);
            case NOT_FOUND -> CouponStore.empty();
        };
    }

    enum CouponStatus { ACTIVE, EXPIRED, NOT_FOUND }

    static class StubInventoryService implements InventoryService {
        private final Map<String, Integer> stock;

        StubInventoryService(Map<String, Integer> stock) {
            this.stock = stock;
        }

        @Override
        public int availableStock(String productId) {
            return stock.getOrDefault(productId, 0);
        }
    }
}
```

---

**Six `@TableTest` methods covering six concerns:**

| Method | Rows | Key design notes |
|---|---|---|
| `addItemToCart` | 3 | Maps `[:]` / `[gadget: 1]` for cart state; JUnit converts `Map<String,Integer>` natively |
| `removeItemFromCart` | 4 | Includes `Cart Items After?` — failure rows assert the cart is *not* mutated |
| `applyCoupon` | 5 | Test-local `CouponStatus` enum drives the stub; blank cells = null coupon (no active coupon) |
| `checkoutValidatesStock` | 5 | `StubInventoryService` built from a map; "exactly enough stock" covers the ≥ boundary |
| `couponDiscountTypes` | 4 | `CouponType` is the domain enum (JUnit converts it); `null` couponType = no coupon |
| `totalFloorsAtZero` | 3 | Separated from coupon types — different concern (arithmetic boundary, not rule selection) |

**Math verification** (widget=$10, gadget=$25):
- 10% of (2×10 + 1×25 = 45) → 40.50 ✓
- $3 off (2×10 = 20) → 17.00 ✓
- 50% of widget line (2×10 = 20) → 45 − 10 = 35.00 ✓
- $15 off widget (1×10 = 10) → floor(−5, 0) = 0.00 ✓

**Open questions captured in `@Description`:** product name casing in messages, duplicate-add behaviour, unknown product ID handling, multi-shortfall message format.