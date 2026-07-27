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