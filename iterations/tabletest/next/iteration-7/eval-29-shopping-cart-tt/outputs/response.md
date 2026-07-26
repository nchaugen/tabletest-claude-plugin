All 29 generated test cases fail with `UnsupportedOperationException` only — confirming the tables, type converters, and value-set expansion are all correct, and no assertion or parsing errors exist.

## Summary

Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` and wrote `src/test/java/com/example/cart/CartServiceTest.java` with 5 `@TableTest` methods covering all stated rules:

- **`addsItemsToCart`** — new item, quantity accumulation on existing item, unknown product error, and zero/negative quantity rejection (assumption documented in `@Description`, since the spec didn't address it)
- **`removesItemsFromCart`** — removing an item (alone or among others) and the not-in-cart error
- **`appliesCoupons`** — one row per coupon type replacing the active coupon, plus expired/nonexistent codes failing without replacing it (fixture documented in `@Description`)
- **`checksOutCart`** — empty-cart precondition, stock exactly matching/exceeding demand, and insufficient-stock messages for one and several items
- **`calculatesCartTotal`** — no coupon, percentage, product-specific (including "product not in cart"), and three boundary rows around the zero-floor, with a value set proving the floor applies identically across all three coupon types

Added 4 `@TypeConverter` methods (`Cart`, `ProductCatalogue`, `InventoryService`, `Coupon`) so the tables can express cart contents, catalogues, stock levels, and coupons as readable literals. No production logic was touched — `ShoppingCart` still throws `UnsupportedOperationException`, and I verified via `gradle test` that all 29 generated cases compile and fail only on that exception, confirming the tables themselves are correct.