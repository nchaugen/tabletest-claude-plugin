Still all 16 tests reach the stub cleanly and fail only on `UnsupportedOperationException`. The deliverable is complete and verified.

## Summary

Wrote `src/test/java/com/example/orders/OrderSplitterTest.java` with 5 `@TableTest` methods (16 rows total), one per splitting rule, all exercising the existing `OrderSplitter.splitOrder(Order, WarehouseInventory)` API:

1. **`splitsShipmentsByFulfillmentType`** — DELIVERY vs PICKUP items land in separate shipments
2. **`splitsDeliveryShipmentsByAddress`** — different delivery addresses split shipments
3. **`shipsInStockItemsImmediatelyWithoutHoldingForBackorder`** — in-stock ships IMMEDIATE, backordered/pre-ordered ship WHEN_AVAILABLE without holding in-stock items
4. **`choosesWarehouseCombinationMinimisingShipmentCount`** — prefers the warehouse combination covering the order in fewest shipments over a naive/greedy split
5. **`shipsCompanionProductsFromTheSameWarehouseWhenPossible`** — companion products break ties toward shipping together, but still split when no single warehouse stocks both

Added two `@TypeConverter`s (`OrderItem` from a map cell, `WarehouseInventory` from a nested warehouse→product→status map) shared across all tables, plus a small `allInStockAt` helper for tables that hold warehouse/stock constant.

**Assumptions made (documented in each table's `@Description`)**:
- Companion groups are supplied via `Order`'s `(items, companionGroups)` constructor overload — `WarehouseInventory` also exposes an unused-looking `addCompanionGroup`, flagged as an open question in case the real implementation reads from there instead.
- PICKUP items carry no delivery address (blank cell → `null`).

**Changes to non-test code**: only added the `tabletest-junit:1.2.2` test dependency to `build.gradle`. `OrderSplitter` and the rest of the domain model were left untouched — the stub still throws `UnsupportedOperationException`, and all 16 test rows currently fail there (verified via `gradle test`), confirming the tables compile, parse, and correctly drive the unimplemented API ahead of writing the splitting logic.