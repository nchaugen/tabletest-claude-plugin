I wrote `src/test/java/com/example/cart/CartServiceTest.java` with 5 `@TableTest` methods (21 rows total), one per rule, added the `tabletest-junit:1.2.2` dependency to `build.gradle`, and verified everything compiles and runs (failing only on the stub's `UnsupportedOperationException`, as expected pre-implementation).

Coverage:
- **Adds an item priced from the catalogue** — new item, quantity accumulation on existing line, unrelated items preserved, unknown product rejected.
- **Removes an item from the cart** — removes sole item, removes one of several, rejects an item not in the cart.
- **Applies a coupon code, replacing any coupon already active** — first application, replacement, expired code rejected (unchanged), unknown code rejected (unchanged).
- **Checks out a cart after verifying stock** — empty cart rejected, stock exactly sufficient, one item short, several items short at once (with itemized shortage message).
- **Calculates the cart total after applying the active coupon** — no coupon, percentage, fixed, fixed-exceeding-total floored at zero, product-specific applied, product-specific with absent target (no effect).

Assumptions I made (documented in each table's `@Description` since the spec didn't pin down exact wording/semantics):
- Exact error/success message text beyond the two given examples (`"Added 2x Widget"`, `"Coupon expired"`).
- "Added Nx" reports the quantity just added, not the line's running total.
- Removing an item removes the whole line (API takes no quantity).
- Insufficient-stock message format: `"productId requested X, available Y"`, joined with `"; "` for multiple shortages.

I also added a `Cart`/`InventoryService`/`Coupon` `@TypeConverter` in the test class (test-only helpers) so table cells can express carts as item maps, coupons as domain phrases (`"10% off"`, `"$5 off"`, `"$3 off widget"`), and stock as maps — no production code was touched beyond adding the test dependency.