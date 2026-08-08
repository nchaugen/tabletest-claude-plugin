All 24 rows across 6 tables parsed, converted, and reached the stubbed API correctly — every failure is the expected `UnsupportedOperationException` from the unimplemented stub, confirming the tables are correctly wired to the real `CartService` API with no test-side bugs.

## Summary

Wrote `src/test/java/com/example/cart/ShoppingCartTest.java` with 6 `@TableTest` methods (24 rows total) covering the stated rules, one table per concern:

1. **`addsItemsPricedFromTheCatalogue`** — add item, quantity accumulation, unknown product rejection
2. **`removesItemsFromTheCart`** — remove item, rejection when not present
3. **`appliesCouponsToTheCart`** — replace-on-apply, expired/nonexistent rejection without replacing
4. **`computesCartTotalByCouponType`** — percentage/fixed/product-specific discount computation
5. **`floorsCartTotalAtZero`** — boundary table for the zero-floor rule (kept separate from #4 since it's arithmetic, not type dispatch)
6. **`verifiesStockAtCheckout`** — empty-cart rejection, stock boundary, single/multi-item shortfall detail

Added 5 `@TypeConverter` methods (`Cart`, `ProductCatalogue`, `InventoryService`, `CouponStore`, `Coupon`) so table cells read as domain notation (`[widget: 2, coupon: SAVE10]`, `10% off`, `widget: $3 off`) instead of raw constructor calls in test bodies.

Documented assumptions in each `@Description` since the behavior isn't implemented yet: repeat-add accumulates quantity, product-specific discounts apply once per cart not per unit, exact message wording, and checkout failure message format. Added `tabletest-junit:1.2.2` to `build.gradle`; no stub signatures needed changing. `gradle test` confirms all 24 rows compile, convert, and reach `ShoppingCartTest` → `CartService` → stub, failing only with the expected `UnsupportedOperationException`.