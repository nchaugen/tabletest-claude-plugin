# Narration — 29

The agent's visible narration and file writes, in order, distilled from
`conversation.jsonl`. Thinking text is absent unless the model returns it in the
clear (Claude 5-family models encrypt it).

I'll start by examining the existing project structure and stubbed API.

Good, Java 26 available which supports source compatibility 25. Now let me add the TableTest dependency to build.gradle.

**Edit /private/var/folders/fz/zxx_qbvj49gbql83cc5l47pr0000gn/T/eval-c3-names-1785148166106/eval-29-work/build.gradle (edit)**

Now I'll write the test class with all five concern tables.

**Write /private/var/folders/fz/zxx_qbvj49gbql83cc5l47pr0000gn/T/eval-c3-names-1785148166106/eval-29-work/src/test/java/com/example/cart/ShoppingCartTest.java (156 lines)**

I need to fix an error in the coupon table — I used `[]` (list syntax) for blank String cells instead of leaving them properly blank.

**Edit /private/var/folders/fz/zxx_qbvj49gbql83cc5l47pr0000gn/T/eval-c3-names-1785148166106/eval-29-work/src/test/java/com/example/cart/ShoppingCartTest.java (edit)**

Now let's compile the test sources to verify everything type-checks correctly.

Compiles cleanly. Now let's run the tests — they're expected to fail since the cart logic isn't implemented yet (still throwing `UnsupportedOperationException`), but a run confirms the table parsing/converters work correctly.

All tables parse except `shouldApplyCoupons`, which throws a parse exception. Let me look at the error detail.

The issue is nested colons inside the `Coupon Store` map values conflicting with map `key: value` syntax — need to quote those values.

**Edit /private/var/folders/fz/zxx_qbvj49gbql83cc5l47pr0000gn/T/eval-c3-names-1785148166106/eval-29-work/src/test/java/com/example/cart/ShoppingCartTest.java (edit)**

All 29 test cases now parse correctly and fail only with the expected `UnsupportedOperationException` from the unimplemented stubs — confirming the tables, converters, and assertions are all wired correctly. Let me check the full run summary for the coupon table specifically.

All 6 coupon rows and 7 total-calculation rows now parse and execute as expected (failing only on the unimplemented `UnsupportedOperationException`, which is correct for tests written ahead of implementation).

## Summary

Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` and wrote `src/test/java/com/example/cart/ShoppingCartTest.java` with 5 `@TableTest` methods (29 rows total), one per concern:

- **`shouldAddItemsToCart`** — new/accumulating items, unknown product, non-positive quantity
- **`shouldRemoveItemsFromCart`** — normal removal, emptying the cart, removing an absent item
- **`shouldApplyCoupons`** — replacing the active coupon across all three coupon types, and rejecting nonexistent/expired codes without disturbing the active one
- **`shouldCalculateCartTotal`** — arithmetic for each coupon type plus the zero-floor boundary
- **`shouldVerifyStockAtCheckout`** — sufficient/exact/short stock, multi-item shortages, and the empty-cart case

No production logic was touched — `ShoppingCart.java` stubs still throw `UnsupportedOperationException`, so every row currently fails on that exception rather than an assertion, confirming the tests compile and exercise the intended API shape correctly. I added `@TypeConverter`s in the test class for `Cart`, `ProductCatalogue`, `InventoryService`, `Coupon`, and `CouponStore` so table cells map to domain objects without any conditional logic in the test bodies.

Key assumptions (documented in each table's `@Description`):
- Adding a product already in the cart increases its quantity rather than replacing it.
- Messages follow patterns like `Added 2x widget`, `Product not found: bogus`, `Insufficient stock: widget (need 5, have 4)`.
- A product-specific coupon's discount is multiplied by that product's quantity in the cart, and has no effect if the product isn't present.
- Inventory entries missing from the stock map count as zero available.

