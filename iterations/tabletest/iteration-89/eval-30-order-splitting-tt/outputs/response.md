Still all 14 rows compile and fail only on `UnsupportedOperationException`, as expected for TDD red state.

## Summary

Added `src/test/java/com/example/orders/OrderSplitterTest.java` with 6 `@TableTest` methods (14 rows total) covering the five splitting rules:

1. **`splitsShipmentsByFulfillmentType`** — delivery vs. pickup items split into separate shipments
2. **`splitsDeliveryShipmentsByAddress`** — different delivery addresses split into separate shipments
3. **`resolvesShipmentAvailabilityFromStockStatus`** — classification table: `IN_STOCK`→`IMMEDIATE`, `BACKORDERED`/`PRE_ORDERED`→`WHEN_AVAILABLE` (kept separate from the grouping rule per "separate rules from arithmetic")
4. **`shipsInStockItemsWithoutHoldingForUnavailableItems`** — in-stock items don't wait for backordered ones
5. **`choosesWarehousesToMinimizeShipmentCount`** — picks the warehouse combination minimizing shipment count, including a non-trivial 3-item case where a flexible item must join an already-required warehouse rather than opening a new one
6. **`shipsCompanionProductsFromTheSameWarehouseWhenPossible`** — companion products consolidate to a shared warehouse only when it doesn't cost an extra shipment

Changes made to production stubs to let this compile without implementing logic:
- `OrderSplitter`: added `resolveAvailability(StockStatus)` — a narrow stub for rule 3's classification table.
- `build.gradle`: added the `tabletest-junit` test dependency.

Documented assumptions (in `@Description` blocks):
- Companion membership is read from `WarehouseInventory.addCompanionGroup`, not `Order.companionGroups` (companions treated as a catalog fact, not order-specific) — both fields existed in the stub, so this is flagged explicitly.
- Warehouse-tie-break scenarios were designed so minimization alone always has a unique answer in table 4; genuine ties (multiple equally-minimal warehouse combinations) are reserved for table 6, where companion grouping is the documented tie-breaker.

All 14 rows compile, parse, and run — every failure is `UnsupportedOperationException` from the unimplemented stubs, confirming the tests are correctly wired and ready to go green once `OrderSplitter` is implemented.