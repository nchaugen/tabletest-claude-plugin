All tables are already formatted (the post-write hook took care of it). Summary of what was delivered:

**`build.gradle`**: added `testImplementation 'org.tabletest:tabletest-junit:1.2.2'`.

**`src/test/java/com/example/cart/ShoppingCartTest.java`**: 9 `@TableTest` methods covering the stated rules:
- `addsAnItemFoundInTheCatalogueOrRejectsAnUnknownProduct` / `accumulatesQuantityWhenAddingItems` — catalogue lookup vs. quantity accumulation, split as separate concerns
- `removesAnItemPresentInTheCart` / `rejectsRemovingAProductNotInTheCart` — success and error paths for removal
- `activatesACouponReplacingAnyPreviouslyActiveCoupon` / `rejectsAnInvalidCouponCodeAndKeepsTheExistingCouponActive` — all three coupon types activating/replacing, vs. nonexistent/expired codes rejected
- `verifiesStockLevelsBeforeCompletingCheckout` / `rejectsCheckoutOfAnEmptyCartRegardlessOfInventory` — stock verification with shortfall detail, empty-cart precondition as its own concern
- `calculatesCartTotalAsSumMinusCouponDiscountFlooredAtZero` — sum − discount for all three coupon types plus the floor-at-zero boundary

Also added a `Product` record isn't touched — used existing stubs as-is, only made the test class `public` (required for TableTest to discover `@TypeConverter` methods) and added converters for `Cart`, `ProductCatalogue`, `InventoryService`, and a domain-notation `Coupon` parser (`"10% off"`, `"$3 off"`, `"$2 off Widget"`).

Key assumptions, documented in each table's `@Description`:
- Message wording for unknown product, remove-rejection, coupon-not-found, and checkout messages (exact wording wasn't specified) — the two message strings given in the prompt ("Added 2x Widget", "Coupon expired") are used verbatim.
- Adding an already-present product increases its quantity rather than replacing it.
- A product-specific coupon discounts its amount once (flat), not per unit, and has no effect if its product isn't in the cart.
- Insufficient-stock message format: `"<Product> (need X, have Y)"`, joined with `", "` for multiple shortfalls.

Verified with `gradle compileTestJava` (succeeds) and `gradle test` (26 generated cases, all fail only with the stub's `UnsupportedOperationException` — confirming the tables are structurally correct and ready for the real implementation).