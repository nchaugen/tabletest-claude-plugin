Summary of work done:

- Added the `tabletest-junit:1.2.1` dependency to `build.gradle` (alongside the existing JUnit Jupiter 6 setup — no other changes needed to build config or stubs).
- Wrote `src/test/java/com/example/cart/CartServiceTest.java` with 8 `@TableTest` methods covering each rule as a separate concern:
  - `addsItemsPricedFromCatalogue` — new product, quantity increment, second product, unknown product id
  - `removesItemsFromCart` — remove existing, remove among several, not-in-cart error, empty cart
  - `appliesCouponReplacingActiveOne` — first coupon (value-set over all 3 coupon types since replace behavior doesn't depend on type), replace existing, expired code rejected without replacing, nonexistent code rejected without replacing
  - `checksStockLevelsBeforeCheckout` — empty cart rejected, sufficient stock, insufficient stock (single and multiple products with shortage details)
  - Four `calculateTotal*` tables split by coupon type (none/percentage/fixed/product-specific), each verifying the floor-at-zero rule and traceable arithmetic
- Added one `@TypeConverter` (`parseCoupon`) to let coupon tables use readable domain notation (`"10% off"`, `"$5 off"`, `"$3 off widget-1"`) instead of constructing `Coupon` objects inline.
- No cart-logic behavior was implemented — `ShoppingCart` methods still throw `UnsupportedOperationException`, confirmed by running `gradle test`: all 29 generated test cases compile, execute, and fail on the stub exception as expected for TDD.

Next step would be implementing `ShoppingCart`'s methods to make these pass.