All tables are well-formed and already aligned (matching the auto-format from the write hook).

## Summary

Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` and wrote `src/test/java/com/example/cart/ShoppingCartTest.java` with 6 `@TableTest` methods (27 rows total) covering the five operations, no `ShoppingCart` logic touched:

- **`addsItemsPricedFromTheCatalogue`** — new item, quantity increment on existing item, adding alongside other items, unknown product rejection (price/name always sourced from `ProductCatalogue`, never the caller)
- **`removesItemsFromTheCart`** — removal from single/multi-item carts, absent-item error from empty and non-empty carts
- **`appliesACouponCodeToTheCart`** — all three coupon types applied fresh, replacing an active coupon, expired/unknown codes rejected without clearing the existing (or absent) active coupon
- **`requiresANonEmptyCartToCheckOut`** — empty-cart guard, split out as its own concern from stock checking (an "and" rule split into two tables)
- **`verifiesStockLevelsAtCheckout`** — sufficient/exact-boundary/one-short/multi-item-short stock, with aggregated shortage detail in the message
- **`calculatesTheCartTotalAfterAnyCoupon`** — subtotal sum, each coupon type, zero-floor boundary, and the product-specific-coupon-with-absent-target edge case

Since message wording and a couple of coupon-discount rules (e.g. product-specific coupon when its target isn't in the cart) aren't specified, I documented those as assumptions/open questions in each table's `@Description`. Small additions to make it compile: three `@TypeConverter` methods (`ProductCatalogue`, `InventoryService`, `Coupon` from domain-readable strings like `"10% off"`, `"$5 off"`, `"widget: $3 off"`, `"expired"`) — no production code was changed.

Ran `gradle test`: all 27 rows compile and execute, each failing with `UnsupportedOperationException` from the stub — confirming the tables, conversions, and parameter wiring are correct and ready to drive the implementation.