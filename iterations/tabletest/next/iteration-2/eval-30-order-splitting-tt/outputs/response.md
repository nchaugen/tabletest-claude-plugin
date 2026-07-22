All 16 tests parsed and ran correctly, failing only on `UnsupportedOperationException` from the unimplemented stub — exactly as expected before implementation. Table syntax, type converters, and generated test cases are all valid.

## Summary

Added `OrderSplitterTest.java` with 4 `@TableTest` methods, one per splitting concern, each isolating its rule by holding the other dimensions constant:

1. **`groupsByFulfillmentTypeAndAddress`** — delivery/pickup and address-based shipment splitting (single warehouse, all in-stock).
2. **`splitsByAvailability`** — in-stock ships immediately, backordered/pre-ordered items split off without holding back in-stock items.
3. **`selectsMinimalWarehouseCombination`** — minimizes distinct warehouses needed to cover the order; test data is crafted so each row's minimal cover is unique (no ties).
4. **`keepsCompanionsTogetherWhenPossible`** — companion products (camera+lens) co-locate to one warehouse when the choice is otherwise tied, but correctness wins when no shared warehouse exists.

Documented assumptions via `@Description`:
- Companions are declared per-order through `Order(items, companionGroups)`, not `WarehouseInventory.addCompanionGroup` (both exist in the stub; I picked the one scoped to the order being split).
- Quantity is fixed to 1 throughout since none of the stated rules depend on it.

Changes made to compile:
- Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` (no other stub changes were needed — `OrderSplitter.splitOrder(Order, WarehouseInventory)` already matched).

Verified with `gradle compileTestJava` (success) and `gradle test` (all 16 generated cases run and fail only on the expected `UnsupportedOperationException`, confirming table parsing/conversions are correct ahead of implementation).