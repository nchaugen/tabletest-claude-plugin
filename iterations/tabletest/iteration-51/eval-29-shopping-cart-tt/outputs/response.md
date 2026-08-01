## Summary

Added `src/test/java/com/example/cart/ShoppingCartTest.java` with 5 `@TableTest` methods covering every stated rule (20 rows total), plus added `tabletest-junit:1.2.2` to `build.gradle`. No production code changes were needed — the existing stub signatures already fit.

**Tables written:**
- `addsItemsToCart` — new item, cumulative quantity on existing item, unknown-product rejection
- `removesItemsFromCart` — removal, isolation from other items, not-in-cart error
- `appliesCoupon` — first activation, replacement, expired-leaves-unchanged, nonexistent-leaves-unchanged
- `checksOutCart` — empty cart, success, single shortfall, multi-item shortfall detail
- `calculatesCartTotal` — no coupon, all 3 coupon types, product-specific coupon with absent target, floor-at-zero boundary

**Assumptions documented in each `@Description`** (message text/formats weren't fully specified): exact wording for success/error messages beyond the two examples given ("Added 2x Widget", "Coupon expired"), that a product-specific coupon is a flat one-time deduction (not per-unit) that only applies when its target product is in the cart, and that coupon type doesn't affect apply/replace behavior.

Verified with `gradle compileTestJava` (clean compile) and `gradle test` — all 20 cases fail solely with `UnsupportedOperationException` from the unimplemented `ShoppingCart`, confirming the table syntax and type converters are correct and ready for the implementation to make green.