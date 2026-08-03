All 5 concerns are covered with `@TableTest` tables in `src/test/java/com/example/cart/ShoppingCartTest.java`, compiling against the existing stubbed API and failing only with the stub's `UnsupportedOperationException` (25 rows total). I also added the `tabletest-junit` dependency to `build.gradle`.

**Tables written** (one rule per table, per the shopping-cart rules):
- `addsAProductToTheCart` — new item, quantity accumulation, coexisting products, unknown product rejected, non-positive quantity rejected
- `removesAProductFromTheCart` — removes an item, leaves others intact, rejects removing an item not in the cart
- `appliesACouponCode` — activates first coupon, new valid code replaces old, expired code rejected (keeps active), nonexistent code rejected (keeps active)
- `checksOutTheCart` — empty cart rejected, exact-stock boundary, surplus stock, one-short boundary, shortage list excludes sufficient items, multiple shortages listed
- `calculatesTheCartTotal` — no coupon, percentage, fixed, product-specific, boundary just-short-of-zero, floored-at-zero, expired coupon applies no discount

**Assumptions documented in each table's `@Description`** (since the spec doesn't pin these down):
- Quantity must be positive; catalogue product name doubles as id in fixtures
- Coupon type doesn't affect acceptance (only replacement/expiry does)
- Shortage message format: `"Insufficient stock: <Product> (have X, need Y)"`, comma-separated per product
- Product-specific coupons discount the line once (flat), not per unit
- `calculateTotal` honors the `Coupon.expired` flag directly (in case it's stale by the time total is computed), separate from `applyCoupon`'s own expiry check against the coupon store

No behavior was implemented — `ShoppingCart` stubs are untouched. Ready for implementation to make these pass.