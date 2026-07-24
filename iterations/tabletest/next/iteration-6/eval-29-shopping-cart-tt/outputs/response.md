All 22 TableTest cases compile and run cleanly against the stubbed API, currently failing only with `UnsupportedOperationException` (expected, since the cart logic isn't implemented yet).

**What was added:**
- `build.gradle`: added `tabletest-junit:1.2.2` test dependency
- `src/test/java/com/example/cart/CartServiceTest.java`: 5 `@TableTest` methods, one per concern:
  1. `addsItemsByProductIdAndQuantity` — new item, quantity accumulation on existing item, other items preserved, unknown product rejected
  2. `removesItemsFromCart` — removes item, preserves others, rejects removal of item not in cart
  3. `appliesOneCouponAtATime` — first apply, replacement, expired-code rejected (no replace), unknown-code rejected (no replace)
  4. `verifiesStockAtCheckout` — empty cart rejected, sufficient/exact-boundary stock succeeds, single and multi-item shortfalls report details
  5. `calculatesCartTotalAfterCoupon` — baseline sum, percentage/fixed/product coupon types, product coupon irrelevant when absent from cart, floor-at-zero

**Assumptions documented via `@Description`** (since the spec didn't fully pin these down):
- Product-specific coupon discount is a **per-unit** deduction on the matching line (open question flagged for confirmation before implementing).
- "Coupon expired" vs "coupon not found" are distinguished via `CouponStore` returning an expired-flagged coupon vs. an empty lookup, matching the existing `CouponStore.of` stub.

**Stub adjustments:** none were needed beyond the build file — the existing `Cart`, `Coupon`, `CouponStore`, `ProductCatalogue`, `InventoryService` APIs were sufficient; four `@TypeConverter` methods (`ProductCatalogue`, `InventoryService`, `CouponStore`, `Coupon`) were added in the test class to bridge table syntax to these types, following the map-of-strings converter pattern. No cart/coupon/checkout logic was implemented.