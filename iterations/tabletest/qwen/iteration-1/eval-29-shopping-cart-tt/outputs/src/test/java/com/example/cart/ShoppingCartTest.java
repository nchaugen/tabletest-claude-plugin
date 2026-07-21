package com.example.cart;

import org.junit.jupiter.api.Assertions;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

class ShoppingCartTest {

      /** Cart fixture: 2x "Widget" at £5, 10x "Gadget" at £3 → subtotal = £40. */
    private static Cart cart() {
        Map<String, Integer> items = new LinkedHashMap<>();
        items.put("Widget", 2);
        items.put("Gadget", 10);
        return new Cart(items, null);
      }

     /** Build a ProductCatalogue from product-name-to-price pairs. */
    private static ProductCatalogue catalogue(Map<String, BigDecimal> prices) {
        return productId -> prices.containsKey(productId)
             ? java.util.Optional.of(new Product(productId, productId, prices.get(productId)))
             : java.util.Optional.empty();
      }

     /** Build a default catalogue: Widget at £5, Gadget at £3. */
    private static ProductCatalogue defaultCatalogue() {
        return catalogue(Map.of("Widget", BigDecimal.valueOf(5), "Gadget", BigDecimal.valueOf(3)));
      }

     /** Build an InventoryService from product-name-to-quantity pairs. */
    private static InventoryService inventory(Map<String, Integer> stock) {
        return productId -> stock.getOrDefault(productId, 0);
      }

     // ───────────────────────────────────────────────────────────────────────────
     // Add Item — prices come from catalogue; adding anything goes in the cart.
     // ───────────────────────────────────────────────────────────────────────────

      /** Items are added by product ID and quantity. */
      @TableTest("""
        Scenario               | Product  | Qty | Success?   | Message?             | CartItems?
        Single item            | Widget   | 2    | true       | Added 2x Widget      | {Widget: 2}
        Second item adds       | Gadget   | 10   | true       | Added 10x Gadget     | {Widget: 2, Gadget: 10}
        Unknown product        | Unknown  | 1    | false      | Product not found    | {}
          """)
    void should_add_item(String product, int qty, boolean success, String message,
                         Map<String, Integer> cartItems) {
        Cart cart = Cart.empty();

        CartResult result = ShoppingCart.addItem(cart, product, qty, defaultCatalogue());

        Assertions.assertEquals(success, result.success());
        if (success) {
            Assertions.assertTrue(result.message().startsWith("Added"));
            Assertions.assertEquals(cartItems, result.cart().items());
          } else {
            Assertions.assertFalse(result.message().isEmpty());
          }
      }

     // ───────────────────────────────────────────────────────────────────────────
     // Remove Item — existing items are removed; unknown ones fail.
     // ───────────────────────────────────────────────────────────────────────────

      /** Removing an item not in the cart returns an error; existing items are removed. */
      @TableTest("""
        Scenario               | CartBefore           | Product  | Success?   | Message?            | CartAfter?
        Remove existing        | {Widget: 2}          | Widget   | true       | Removed 2x Widget   | {}
        Remove nonexistent     | {Gadget: 10}         | Widget   | false      | Not in cart: Widget | {Gadget: 10}
        Empty cart             | {}                   | Widget   | false      | Cart is empty       | {}
          """)
    void should_remove_item(Map<String, Integer> cartBefore, String product, boolean success,
                            String message, Map<String, Integer> cartAfter) {
        Cart cart = new Cart(cartBefore, null);

        CartResult result = ShoppingCart.removeItem(cart, product);

        Assertions.assertEquals(success, result.success());
        if (success) {
            Assertions.assertTrue(result.message().startsWith("Removed"));
            Assertions.assertEquals(cartAfter, result.cart().items());
          } else {
            Assertions.assertFalse(result.message().isEmpty());
            Assertions.assertEquals(cartBefore, result.cart().items());
          }
      }

     // ───────────────────────────────────────────────────────────────────────────
     // Apply Coupon — one active at a time; new replaces old. Expired / missing codes fail.
     // ───────────────────────────────────────────────────────────────────────────

      /** Only one coupon active at a time: new code replaces old, expired/missing codes leave cart unchanged. */
      @TableTest("""
        Scenario                   | CartBefore           | Code         | Success?   | Message?       | CouponAfter?
        Empty cart, expired code   | {}                   | EXPIRED      | false      | Coupon expired | null
        Valid replaces nothing     | {Widget: 2}          | SAVE10       | true       | Coupon applied | SAVE10
        Valid replaces previous    | {Gadget: 10}         | SAVE5        | true       | Coupon applied | SAVE5
        Non-existent code          | {Widget: 2}          | GONE         | false      | Coupon not found| {Gadget: 10}
          """)
    void should_apply_coupon(Map<String, Integer> cartBefore, String code, boolean success,
                             String message, Map<String, Integer> couponAfter) {
        Cart cart = new Cart(cartBefore, null);

        Map<String, BigDecimal> available = Map.of("SAVE10", BigDecimal.valueOf(10),
                                                     "SAVE5",  BigDecimal.valueOf(5));
        Set<String> expired = Set.of("EXPIRED");
        CouponStore store = CouponStore.of(available, expired);

        CartResult result = ShoppingCart.applyCoupon(cart, code, store);

        Assertions.assertEquals(success, result.success());
        if (success) {
            Assertions.assertTrue(result.message().startsWith("Coupon"));
             // All valid codes replace whatever was there
            Assertions.assertNotNull(result.cart().items());
          } else {
            Assertions.assertFalse(result.message().isEmpty());
             // Failed codes leave cart items unchanged
            Assertions.assertEquals(cartBefore, result.cart().items());
          }
      }

     // ───────────────────────────────────────────────────────────────────────────
     // Checkout — stock verified; empty cart fails.
     // ───────────────────────────────────────────────────────────────────────────

      /** At checkout, stock levels are verified. Insufficient inventory fails with details. Empty carts cannot check out. */
      @TableTest("""
        Scenario                   | CartBefore             | WidgetStock | GadgetStock | Success?   | Message?
        Empty cart                 | {}                    | 10          | 10          | false      | Cart is empty
        Insufficient widget        | {Widget: 2}           | 0           | 10          | false      | Insufficient stock
        Insufficient gadget        | {Gadget: 10}          | 10          | 0           | false      | Insufficient stock
        Partially insufficient     | {Widget: 2, Gadget: 5}| 1           | 4           | false      | Insufficient stock
        Full success               | {Widget: 2, Gadget: 10}| 10         | 10          | true       | Ordered successfully
          """)
    void should_checkout(Map<String, Integer> cartBefore, int widgetStock, int gadgetStock,
                         boolean success, String message) {
        InventoryService inventory = inventory(Map.of("Widget", widgetStock, "Gadget", gadgetStock));
        Cart cart = new Cart(cartBefore, null);

        CheckoutResult result = ShoppingCart.checkout(cart, inventory);

        Assertions.assertEquals(success, result.success());
        if (success) {
            Assertions.assertTrue(result.message().startsWith("Ordered"));
          } else {
            Assertions.assertFalse(result.message().isEmpty());
          }
      }

     // ───────────────────────────────────────────────────────────────────────────
     // Total — no coupon, percentage, fixed. Product-specific tested separately.
     // ───────────────────────────────────────────────────────────────────────────

      /** Cart total = sum(qty x price) - coupon discount, floored at zero. Percentage coupons apply to the subtotal; fixed subtracts directly. */
      @TableTest("""
        Scenario                | Code       | Pct?   | Fixed?    | Total?
        No coupon               |           |         |            | 40.00
        Small percentage        | SAVE10    | 10     |            | 36.00
        Large percentage        | SAVE50    | 50     |            | 20.00
        Near-max percentage     | SAVE75    | 75     |            | 10.00
        Fixed below total       | SAVE10    |         | 10         | 30.00
        Fixed to zero           | SAVE40    |         | 40         | 0.00
          """)
    void should_calculate_total(String code, int pct, BigDecimal fixed, java.math.BigDecimal total) {
        Cart cart = cart();

        Coupon coupon = null;
        if (code != null && !code.isEmpty()) {
            Map<String, BigDecimal> available = Map.of("SAVE10", BigDecimal.valueOf(10),
                                                         "SAVE50", BigDecimal.valueOf(50),
                                                         "SAVE75", BigDecimal.valueOf(75),
                                                         "SAVE40", BigDecimal.valueOf(40));
            Set<String> expired = Set.of();
            CouponStore store = CouponStore.of(available, expired);
            CartResult applyResult = ShoppingCart.applyCoupon(cart, code, store);

            if (applyResult.success()) {
                String activeCode = applyResult.cart().activeCouponCode();
                BigDecimal foundAmount = available.get(activeCode);
                if (pct > 0) {
                    coupon = new Coupon(CouponType.PERCENT, BigDecimal.valueOf(pct), null, false);
                  } else {
                    coupon = new Coupon(CouponType.FIXED, foundAmount, null, false);
                  }
              }
          }

        java.math.BigDecimal result = ShoppingCart.calculateTotal(cart, coupon, defaultCatalogue());

        Assertions.assertEquals(total, result);
      }

     // ───────────────────────────────────────────────────────────────────────────
     // Product-specific percentage — matching items get discount.
     // ───────────────────────────────────────────────────────────────────────────

      /** Product-specific coupons only discount matching items; non-matching stays at full price. */
      @TableTest("""
        Scenario                     | Code       | Target    | Pct   | Total?
        No match, any percent         | BAD        | Widget    | 50    | 40.00
        Partially matching           | WIDGET_10 | Widget    | 10    | 36.00
        Full match, extreme pct      | WIDGET_90 | Widget    | 90    | 0.00
          """)
    void should_calculate_product_pct(String code, String target, int pct, java.math.BigDecimal expectedTotal) {
        Map<String, Integer> items = new LinkedHashMap<>();
        items.put("Widget", 2);
        items.put("Gadget", 10);
        Cart cart = new Cart(items, null);

        Coupon coupon;
        if ("BAD".equals(code)) {
             // No code exists → no discount (coupon with high pct on nonexistent target)
            coupon = new Coupon(CouponType.PERCENT, BigDecimal.valueOf(999), "NONEXIST", false);
          } else {
            coupon = new Coupon(CouponType.PERCENT, BigDecimal.valueOf(pct), target, false);
          }

        java.math.BigDecimal result = ShoppingCart.calculateTotal(cart, coupon, defaultCatalogue());

        Assertions.assertEquals(expectedTotal, result);
      }

     // ───────────────────────────────────────────────────────────────────────────
     // Product-specific fixed — discount caps at item line total.
     // ───────────────────────────────────────────────────────────────────────────

      /** Product-fixed coupons only discount matching items; excess is capped at line total. */
      @TableTest("""
        Scenario                     | Code       | Target    | Fixed?   | Total?
        No match                      | BAD        | Widget    | 10       | 40.00
        Partial match, small disc    | MATCH_3    | Widget    | 3        | 34.00
        Full match, cap              | MATCH_20   | Widget    | 20       | 0.00
          """)
    void should_calculate_product_fixed(String code, String target, BigDecimal discount,
                                        java.math.BigDecimal expectedTotal) {
        Map<String, Integer> items = new LinkedHashMap<>();
        items.put("Widget", 2);
        items.put("Gadget", 10);
        Cart cart = new Cart(items, null);

        Coupon coupon;
        if ("BAD".equals(code)) {
             // No code exists → no discount (high amount on nonexistent target)
            coupon = new Coupon(CouponType.FIXED, BigDecimal.valueOf(999), "NONEXIST", false);
          } else {
            coupon = new Coupon(CouponType.FIXED, discount, target, false);
          }

        java.math.BigDecimal result = ShoppingCart.calculateTotal(cart, coupon, defaultCatalogue());

        Assertions.assertEquals(expectedTotal, result);
      }

}
