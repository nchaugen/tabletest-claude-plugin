Work is complete. Summary of what I did:

**Added dependency**: `org.tabletest:tabletest-junit:1.2.1` to `build.gradle` (JUnit Jupiter 6.0.3 already satisfies the 5.11+ requirement).

**New file**: `src/test/java/com/example/orders/OrderSplitterTest.java` with 4 focused `@TableTest` methods (19 rows total), one per splitting concern:

1. **`splitsShipmentsByFulfillmentTypeAndAddress`** — delivery/pickup and different addresses force separate shipments; pickup items with no address group together.
2. **`splitsShipmentsByStockAvailability`** — in-stock items ship `IMMEDIATE` without waiting on backordered/pre-ordered items (`WHEN_AVAILABLE`); documents the assumption that backordered and pre-ordered items combine into one shipment since `Shipment.Availability` only has two states.
3. **`choosesWarehouseCombinationMinimizingShipmentCount`** — verifies the fewest-shipments warehouse combination is chosen, including a case where overlapping stock could tempt a needless split and a case requiring the *correct* warehouse pair (not just any pair).
4. **`keepsCompanionProductsTogetherWhenPossible`** — companions win genuine ties between equally-minimal warehouse combinations, split apart only when truly impossible, and need no trade-off when already co-located. Documents the assumption that companion relationships live on `WarehouseInventory.addCompanionGroup` (catalog-level), not `Order`'s own `companionGroups` constructor param — both existed in the stubs, so I picked one and noted it in `@Description`.

Each table uses custom `@TypeConverter`s (`OrderItem`, `StockedItem`) and canonicalized/sorted string comparisons for shipment groupings so results are order-independent.

I validated the logic by temporarily swapping in a brute-force reference `OrderSplitter` implementation, confirming all 19 rows pass, then reverted `OrderSplitter.java` to its original `UnsupportedOperationException` stub — no splitting logic was left implemented. Running the tests now, all 19 fail solely with `UnsupportedOperationException`, confirming they compile and are ready to drive the real implementation.