All 16 rows compile and fail only with `UnsupportedOperationException`, as expected before implementation.

## Summary

Wrote `src/test/java/com/example/orders/OrderSplitterTest.java` with 6 `@TableTest` tables (16 rows total), added the `tabletest-junit` dependency to `build.gradle`, and added small package-private stub methods to `OrderSplitter` to expose seams the tables drive at (all still throwing `UnsupportedOperationException`).

**Tables, one rule each:**
1. `groupsItemsThatMustShipSeparately` — fulfillment type + delivery address partitioning (pickup ignores address)
2. `resolvesAvailabilityFromStockStatus` — single-item classification (IN_STOCK → IMMEDIATE, BACKORDERED/PRE_ORDERED → WHEN_AVAILABLE, collapsed via value set)
3. `splitsItemsFromOneWarehouseByAvailability` — proves in-stock items aren't held for backordered ones
4. `choosesTheFewestWarehousesThatCoverTheOrder` — minimal set-cover warehouse selection
5. `prefersCompanionCoLocationWhenItDoesNotCostAnExtraWarehouse` — companion tie-break, including the "when possible" boundary where no shared warehouse exists
6. `splitsOrderAccordingToAllRulesTogether` — one wiring row through the real public `splitOrder` API, combining all rules

**Assumptions made (documented in `@Description`):** companion groups are read from `WarehouseInventory.addCompanionGroup` (catalog-level fact), not `Order.getCompanionGroups()`, which the stub also exposes but this feature doesn't use — that field was left untouched. `OrderItem` gets a `Map<String,String> → OrderItem` type converter (quantity defaults to 1, unused by any rule). I validated all 16 rows' expected values by temporarily implementing the real logic, running the suite (all green), then reverting `OrderSplitter` back to the throwing stub — so the data itself is verified correct, only the implementation is left undone as requested.