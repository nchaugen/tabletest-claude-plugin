That's fine — the temp scratch files in `/tmp` don't affect the repo. Here's a summary of the work.

## Summary

Wrote `src/test/java/com/example/orders/OrderSplitterTest.java` — 3 `@TableTest` methods, 14 rows total — covering the order-splitting rules, plus added the `org.tabletest:tabletest-junit:1.2.1` dependency to `build.gradle`.

**Tables (each isolates one concern per the skill's decomposition guidance):**
1. `groupsItemsByFulfillmentTypeAddressAndAvailability` (7 rows, single warehouse) — DELIVERY vs PICKUP split, different addresses split, same type+address merge, in-stock ships immediately without waiting on backordered/pre-ordered companions, and backordered+pre-ordered items merge into one `WHEN_AVAILABLE` shipment.
2. `choosesWarehouseCombinationMinimisingShipmentCount` (4 rows) — single warehouse covering everything, forced 2-way and 3-way splits, and preferring one warehouse covering most items plus a leftover.
3. `prefersShippingCompanionsFromTheSameWarehouseWhenItDoesNotCostExtraShipments` (3 rows) — companions grouped when free, companions sacrificed when grouping would add a shipment (minimization takes priority), and companions split when infeasible.

**Assumptions documented via `@Description`:** companion data lives in `WarehouseInventory.addCompanionGroup` rather than `Order`'s duplicate `companionGroups` field (the stub had both — I picked inventory as the more natural home and left `Order`'s field unused); `IN_STOCK` → `IMMEDIATE`, `BACKORDERED`/`PRE_ORDERED` → `WHEN_AVAILABLE`; companion grouping is a tie-break, never overriding the fewest-shipments goal.

**Verification:** compiled and ran the suite (all 14 fail only with the stub's `UnsupportedOperationException`, confirming syntax/converters are sound). Also had a reviewer agent independently re-derive every row by hand — it caught one real bug (`lens+cable` should've been `cable+lens` per the alphabetical descriptor convention) and one genuine ambiguity (a tie between two equally-valid warehouses for a leftover item), both now fixed. Finally, I wrote a throwaway brute-force reference implementation (not committed) and confirmed all 14 rows pass against it, then restored the original stub.

No production logic was implemented — `OrderSplitter.splitOrder` still throws `UnsupportedOperationException` as required.