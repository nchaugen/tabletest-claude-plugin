All 29 rows across the 5 tables parsed correctly and executed, each failing at the expected `UnsupportedOperationException` line in the unimplemented `ShoppingCart` stub — confirming table syntax, type converters, and column-to-parameter binding all work correctly.

## Summary

Wrote `src/test/java/com/example/cart/ShoppingCartTest.java` with 5 `@TableTest` methods, one per concern:

1. **`shouldAddItemToCart`** — new item, quantity accumulation on repeat add, second product, unknown product id error.
2. **`shouldRemoveItemFromCart`** — remove existing item, remove one of several, "not in cart" error (empty and nonempty cart).
3. **`shouldApplyCoupon`** — first coupon applied, replacing an active coupon, expired/unknown codes rejected without disturbing the existing active coupon (state-transition style, before/after columns).
4. **`shouldCheckout`** — empty cart rejected, sufficient/exact/insufficient stock, multi-item shortfall listing.
5. **`shouldCalculateTotal`** — no coupon, all three coupon types, product-specific coupon when the target isn't in the cart, and floor-at-zero (both via excess fixed discount and exact 100% clearance).

Key decisions, documented via `@Description` on each table:
- Product-specific coupons discount the target product by the given amount **per unit** in the cart.
- Adding an already-present product **accumulates** quantity rather than replacing it.
- `Coupon Code Status` (valid/expired/unknown) stands in for the coupon-store lookup outcome, since the concrete coupon a valid code carries doesn't affect the apply/replace rule.
- Checkout failure messages enumerate every short product, not just the first.

Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` (only dependency the stub project was missing). Added three `@TypeConverter` methods (`Cart`, `ProductCatalogue`, `InventoryService` from map columns) plus two private helpers (`buildCoupon`, `buildCouponStore`) — no cart logic was implemented; `ShoppingCart` stubs still throw `UnsupportedOperationException`, which is exactly what `gradle test` shows failing.