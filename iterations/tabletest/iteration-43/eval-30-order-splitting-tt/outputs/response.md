All 21 generated test cases parse and run correctly, failing only on `UnsupportedOperationException` from the stub `OrderSplitter` — exactly the expected state since the splitting logic isn't implemented yet. The table syntax, value sets, and cartesian expansion all worked (e.g., the availability table's value sets expanded into 7 concrete cases).

## Summary

Added `src/test/java/com/example/orders/OrderSplitterTest.java` with 5 `@TableTest` methods, one per rule, plus `org.tabletest:tabletest-junit:1.2.2` to `build.gradle`. No production code needed changes — the existing `Order`/`OrderItem`/`WarehouseInventory`/`Shipment` API was sufficient.

Tables (21 generated cases total):
1. **`groupsShipmentsByFulfillmentTypeAndAddress`** — segmentation by fulfillment type + delivery address, isolated with a single always-in-stock warehouse.
2. **`doesNotHoldInStockItemsForItemsAwaitingStock`** — in-stock ships immediately vs. backordered/pre-ordered ships when-available, without holding up in-stock items; uses value sets since backordered/pre-ordered behave identically.
3. **`minimisesShipmentCountAcrossWarehouseChoices`** + **`determinesWarehousesUsedWhenCombinationIsForced`** — split into two tables because only some scenarios have a uniquely-determined warehouse combination (avoids asserting a non-deterministic tie).
4. **`groupsCompanionProductsOnTheSameWarehouseWhenPossible`** — companion tie-breaking among equally-minimal warehouse combinations, plus the boundary case where no shared warehouse exists at all.

Documented assumptions via `@Description`, most notably that `deliveryAddress` differentiates `PICKUP` shipments too (as pickup location), and flagged as an open question that companion groups are duplicated on both `Order` and `WarehouseInventory` with no clear authority — I populate both to stay correct either way.

Ran `gradle test`: all 21 cases fail with `UnsupportedOperationException` from the stub, confirming the tests are wired correctly and ready to drive the implementation.