All 29 rows across the 5 `@TableTest` methods parse and bind correctly — each fails only on the stub's `UnsupportedOperationException`, confirming the table syntax, type converters, and parameter binding are all valid (the expected TDD red state).

## Summary

Added `src/test/java/com/example/cart/ShoppingCartServiceTest.java` with one `@TableTest` per concern, plus `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` (JUnit Jupiter was already 6.0.3, well above the 5.11 minimum). No production code changed — `ShoppingCart` stubs still throw `UnsupportedOperationException`.

**Five concern tables:**
1. `addsItemToCart` — accumulates quantity on repeat add, rejects unknown catalogue products.
2. `removesItemFromCart` — removes a whole line, errors when the product isn't present.
3. `appliesCouponReplacingActiveOne` — replacement semantics; expired/unknown codes leave the active coupon untouched.
4. `verifiesStockAtCheckout` — empty-cart rejection, exact-stock boundary, missing-product-as-zero-stock, and multi-product shortfall reporting.
5. `calculatesCartTotal` — all three coupon types plus the zero-floor rule.

Two `@TypeConverter`s: `parseCart(Map<String,Integer>)` (shared by the item/checkout tables) and `parseCoupon(String)`, which reads a domain notation (`"10% off"`, `"$5 off"`, `"$3 off Widget"`) into a `Coupon`.

**Assumptions documented via `@Description`** (since the spec didn't fully pin these down):
- Catalogue prices: Widget=10.00, Gadget=5.00, Thingamajig=2.00.
- Adding an already-present product increases its quantity rather than replacing it.
- Removing an item takes the whole line (no partial-quantity removal).
- A product-specific coupon discount is a flat amount applied once per matching line, regardless of quantity, and only when that product is in the cart.
- Multiple stock shortfalls at checkout are joined with `"; "` in one message.

Verified with `gradle compileTestJava` (clean compile) and `gradle test` (29/29 tests reach the stub and fail identically on `UnsupportedOperationException`, not on table parsing).