## Summary

Added `src/test/java/com/example/orders/OrderSplitterTest.java` with 4 `@TableTest` methods (17 rows total) covering the splitting rules as separate concerns:

1. **`splitsByFulfillmentTypeAndAddress`** — delivery vs. pickup and differing addresses force separate shipments
2. **`splitsByAvailability`** — in-stock items ship immediately, backordered/pre-ordered items merge into a "when available" shipment, without holding up in-stock items
3. **`choosesWarehouseCombinationMinimizingShipments`** — set-cover-style warehouse selection that minimizes shipment count (rows are constructed to have an unambiguous optimum, so exact warehouse sets can be asserted)
4. **`groupsCompanionsOnSameWarehouseWhenPossible`** — companion products prefer the same warehouse when tied on cost, but hard constraints (fulfillment type) still override that preference

Changes made:
- `build.gradle`: added `testImplementation 'org.tabletest:tabletest-junit:1.2.1'`
- Wrote 3 `@TypeConverter` methods (`OrderItem` from a compact `productId@TYPE@address` spec, `WarehouseInventory` from a `productId -> warehouseId@STATUS[+warehouseId@STATUS]` map, and `Set<Set<String>>` shipment-group assertions from a `a,b;c` spec) plus small private helpers — no changes needed to the existing domain model or the `OrderSplitter` stub.

**Verification performed:** I temporarily wrote a full reference implementation of `OrderSplitter` (brute-force set-cover + companion tie-breaking), ran the suite — all 17 rows passed, confirming the expected values are actually correct — then reverted `OrderSplitter.java` back to its original `throw new UnsupportedOperationException(...)` stub. The suite currently fails all 17 tests with that expected exception, confirming the tests compile, the tables parse, and they're ready to drive the real implementation.

Key assumptions documented via `@Description` on each table: quantity is fixed at 1 (irrelevant to splitting since `WarehouseInventory` has no quantity concept), pickup items reuse `deliveryAddress` as a store/location id, and each table isolates one concern by holding the others constant (e.g. all-in-stock/single-warehouse when testing grouping, single warehouse when testing availability).