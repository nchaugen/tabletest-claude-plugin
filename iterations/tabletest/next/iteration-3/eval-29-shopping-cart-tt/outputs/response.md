All 24 rows ran and generated correctly — every failure is the expected `UnsupportedOperationException` from the stubs, confirming the table syntax, type converters, and parameter binding all parse and wire correctly. No unimplemented behavior was added.

## Summary

Wrote `src/test/java/com/example/cart/ShoppingCartTest.java` with 5 `@TableTest` methods, one per concern:

1. **`addsItemsToCart`** — pricing from catalogue, quantity accumulation on repeat add, unknown product error, non-positive quantity rejection
2. **`removesItemsFromCart`** — successful removal, remove-not-in-cart error, removing last item empties cart
3. **`appliesCouponCodes`** — valid code activates/replaces, expired/nonexistent codes error without replacing the active coupon
4. **`checksOutCart`** — sufficient stock, insufficient stock (single and multiple products, with shortfall details), empty cart rejection
5. **`calculatesCartTotal`** — subtotal arithmetic, all three coupon types, floor-at-zero on over-discount

Added `@TypeConverter` methods for `ProductCatalogue`, `InventoryService`, `CouponStore`, and `Coupon` (parsing readable table notation like `"20% off"`, `"$5.00 off"`, `"$3.00 off widget"`) so the tables stay declarative.

**Assumptions documented in `@Description` blocks** (since the spec didn't fully pin these down):
- Adding an already-present product accumulates quantity rather than replacing it
- Non-positive quantities are rejected without mutating the cart
- Product-specific coupon discount is a flat amount tied to the product's presence, not scaled by its quantity
- Exact message wording (e.g. `"Unknown product: ghost"`, `"Insufficient stock: widget (1 available, 3 requested)"`) is illustrative, following the style of the examples given

**Build change**: added `testImplementation 'org.tabletest:tabletest-junit:1.2.2'` to `build.gradle` — no other stub/domain code changes were needed; the existing types already matched what the tests required. Verified with `gradle test`: all 24 generated cases run and fail only with the stub's `UnsupportedOperationException`, confirming the tests compile and are correctly wired against the real API ahead of implementation.