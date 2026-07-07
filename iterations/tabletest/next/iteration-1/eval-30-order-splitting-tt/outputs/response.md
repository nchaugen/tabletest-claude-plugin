Everything is clean — no leftover spike files, and only the intended changes exist. Here's a summary of what I delivered:

**`build.gradle`**: added `org.tabletest:tabletest-junit:1.2.1` as a test dependency.

**`src/test/java/com/example/orders/OrderSplitterTest.java`**: four `@TableTest` methods, one per concern, verified to compile and correctly invoke the real API (all 17 rows fail with `UnsupportedOperationException` as expected, since `splitOrder` is still a stub):

1. `splitsByFulfillmentTypeAndAddress` — asserts that differing fulfillment type or delivery address forces separate shipments, same values merge.
2. `shipsInStockItemsWithoutWaitingForUnavailableOnes` — asserts in-stock items ship `IMMEDIATE` while backordered/pre-ordered ones ship `WHEN_AVAILABLE` in a separate shipment, and that the two non-immediate statuses merge together.
3. `minimizesShipmentCountAcrossWarehouses` — asserts the chosen warehouse combination yields the fewest shipments (single/pair/triple-warehouse cases), asserting only shipment count since ties aren't otherwise specified.
4. `keepsCompanionsTogetherWhenPossible` — asserts companion products land in the same shipment when it costs no extra shipments, and ship apart when no shared warehouse or partial overlap makes that impossible.

No production logic in `OrderSplitter`/stubs was changed beyond what's needed to compile — I only added an `OrderItem` and a `WarehouseInventory` type converter in the test class.

Key assumptions I made (documented inline in each table's `@Description`):
- Companion groups are read from `WarehouseInventory.addCompanionGroup`, not `Order`'s companion-groups constructor param (both exist in the stub; I treated the inventory-side one as the catalog/fulfillment-level source of truth).
- Companion affinity is a tie-break among equally-minimal warehouse combinations, never something that increases shipment count beyond the minimum.
- Where multiple warehouse combinations tie for the same minimal shipment count, only the count is asserted — the specific combination isn't prescribed unless the companion rule disambiguates it.

I verified this by running `gradle test` against the real `tabletest-junit` dependency (not just eyeballing syntax) — confirmed nested map values, list-of-sets, and custom per-item type converters all parse correctly before finalizing the tables.