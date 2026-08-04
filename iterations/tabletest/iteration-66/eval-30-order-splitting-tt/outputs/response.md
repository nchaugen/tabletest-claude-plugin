All 18 generated test cases compile and run (failing only with `UnsupportedOperationException` from the unimplemented stub, as expected). The count breaks down as: Table 1 (5 rows) + Table 2 (3 rows, 7 cases via value-set expansion) + Table 3 (2 rows) + Table 4 (4 rows) = 18, confirming the value-set cartesian expansion worked correctly too.

## Summary

Wrote `src/test/java/com/example/orders/OrderSplitterTest.java` with four `@TableTest` methods, one per splitting rule:

1. **`groupsItemsByFulfillmentTypeAndDeliveryAddress`** — fulfillment type and delivery address as the grouping key (single warehouse, all in stock, held constant).
2. **`doesNotHoldInStockItemsForItemsAwaitingAvailability`** — in-stock items ship immediately while backordered/pre-ordered items wait, using a value set `{BACKORDERED, PRE_ORDERED}` to show both statuses behave identically.
3. **`choosesTheWarehouseCombinationWithFewestShipments`** — prefers a single warehouse that covers the whole order, and splits across the minimal warehouse set when none does alone.
4. **`shipsCompanionProductsFromTheSameWarehouseWhenPossible`** — companions group onto a shared warehouse when possible, split when no warehouse stocks both, and yield to the hard address/availability constraints when those conflict.

Each table compares actual `Shipment` output via small helpers (`shipmentGroups`, `shipmentsByWarehouseId`, `productIdsWithAvailability`) rather than exact list equality, since shipment ordering isn't part of the contract. Two `@TypeConverter` methods handle the composite cell shapes: one builds `OrderItem` from a map, the other builds `WarehouseInventory` from a nested warehouse→product→status map.

**Assumptions made** (documented in each table's `@Description`, and noted here since they affect the implementation you'll write):
- Companion groups come from `Order`'s constructor param (`List<List<String>>`), not `WarehouseInventory`'s separate `companionGroups` field — the latter appears to be an unused/duplicate stub field, so I left it untouched.
- `PICKUP` items carry no delivery address (omitted from the item map rather than blank).
- Quantity doesn't affect splitting per the stated rules, so it's fixed at 1 everywhere and not exposed as a column.

Also added the `tabletest-junit:1.2.2` dependency to `build.gradle`. `gradle compileTestJava` succeeds, and `gradle test` runs all 18 generated cases, each failing only on the stub's `UnsupportedOperationException` — confirming the tests are structurally sound and ready for the real implementation.