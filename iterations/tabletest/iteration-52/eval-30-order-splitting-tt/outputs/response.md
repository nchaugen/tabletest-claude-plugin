Done. Summary of what I wrote:

**`src/test/java/com/example/orders/OrderSplitterTest.java`** — 4 `@TableTest` methods (14 rows total), each isolating one splitting rule via `OrderSplitter.splitOrder(Order, WarehouseInventory)`:

1. **Fulfillment-type/address partitioning** — same type+address combine, different type or address separate, PICKUP ignores address.
2. **Availability splitting ("don't hold in-stock items")** — in-stock ships `IMMEDIATE`, backordered/pre-ordered ship `WHEN_AVAILABLE` and don't merge with in-stock; backordered+pre-ordered do merge with each other.
3. **Warehouse-combination minimisation** — picks the single covering warehouse when one exists, forces a split when none does, and picks the combination that avoids an unnecessary extra warehouse.
4. **Companion affinity** — breaks ties between equally-minimal warehouse combinations in favor of shipping companions together; falls back to splitting when no warehouse stocks both.

Assumptions recorded in each table's `@Description` (held-constant setup, and — for companions — that shipment-count minimisation takes precedence, with companion affinity only breaking ties).

**Stub adjustments**: removed the redundant `companionGroups` field from `Order` (it duplicated `WarehouseInventory.addCompanionGroup`, creating ambiguity about which one governs); added the `tabletest-junit` dependency to `build.gradle`.

Verified: `compileTestJava` succeeds, and all 14 rows execute and fail only on the stub's `UnsupportedOperationException` — confirming table syntax, converters, and assertions are correct and ready for the real implementation.