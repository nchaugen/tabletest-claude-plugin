# Narration — eval-29-shopping-cart-tt

The agent's visible narration and file writes, in order, distilled from
`conversation.jsonl`. Thinking text is absent unless the model returns it in the
clear (Claude 5-family models encrypt it).

I'll start by invoking the tabletest skill since this is a request to write TableTests for a Java feature.

Let me explore the existing project structure to understand the domain types and stubbed API.

Now let me add the TableTest dependency to build.gradle.

**Edit ./build.gradle (edit)**

Now let me write the test file with all five concern tables.

**Write ./src/test/java/com/example/cart/ShoppingCartTest.java (155 lines)**

Now let's verify it compiles by running the build.

Compiles cleanly. Now let's run the tests to confirm they execute (they're expected to fail since behavior isn't implemented, but let's check they run and produce the expected `UnsupportedOperationException` failures, not compilation/table-parsing errors).

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

