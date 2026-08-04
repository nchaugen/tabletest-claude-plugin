Tables are correctly formatted and all parse/convert cleanly. Everything is in order.

## Summary

Wrote `src/test/java/com/example/orders/OrderSplitterTest.java` with 5 `@TableTest` methods, each isolating one splitting rule as its own axis:

1. **`separatesShipmentsByFulfillmentType`** — DELIVERY vs PICKUP items always land in separate shipments; same-type items still merge.
2. **`separatesDeliveryShipmentsByAddress`** — different delivery addresses force separate shipments.
3. **`shipsInStockItemsWithoutWaitingForItemsThatAreNotYetAvailable`** — in-stock items ship `IMMEDIATE` without waiting on backordered/pre-ordered items, which ship `WHEN_AVAILABLE`.
4. **`choosesWarehouseCombinationThatMinimisesShipmentCount`** — proves genuine set-cover minimization (not greedy first-fit), including reusing an already-needed warehouse for a flexible item.
5. **`keepsCompanionProductsTogetherWhenPossible`** — companion items break warehouse ties in favor of shipping together, but still split when no warehouse carries both.

Each table holds irrelevant dimensions (address, warehouse, stock) constant and documents that in `@Description`. Custom `@TypeConverter`s turn table cells directly into domain objects (`OrderItem`, and two small result records `AvailabilityGroup`/`WarehouseGroup` used only for expectation comparison). Assertions compare shipments as unordered sets, since shipment order isn't part of the contract.

**Adjustments made to compile/test infra** (no splitting logic touched):
- Added `tabletest-junit:1.2.2` test dependency to `build.gradle`.
- Added a `settings.gradle` (none existed).
- Left `OrderSplitter.splitOrder` throwing `UnsupportedOperationException` as-is.

**Assumptions documented in the tests themselves** (via `@Description`, since the spec left them open):
- Backordered and pre-ordered items ship together (both map to `WHEN_AVAILABLE`) since `Shipment.Availability` only has two values and nothing says they must split from each other.
- Companion-warehouse preference is a tie-breaker among equally-minimal warehouse combinations, never a reason to add a shipment — this keeps it consistent with the separately-stated minimization rule.

Verified with `gradle test`: all 17 generated cases compile and run, each failing with exactly `UnsupportedOperationException` — meaning every table row parses and converts correctly, and the tests are ready to drive the real implementation.