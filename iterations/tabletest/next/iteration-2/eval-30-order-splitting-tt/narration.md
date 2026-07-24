# Narration — eval-30-order-splitting-tt

The agent's visible narration and file writes, in order, distilled from
`conversation.jsonl`. Thinking text is absent unless the model returns it in the
clear (Claude 5-family models encrypt it).

I'll use the tabletest skill to write these TableTests properly.

Let me explore the existing project structure first.

Now I have a clear picture of the domain model. Let me check the git status/log briefly and then write the test file.

**Edit ./build.gradle (edit)**

Now let me write the test file.

**Write ./src/test/java/com/example/orders/OrderSplitterTest.java (192 lines)**

Let me try to build this to verify it compiles.

It compiles successfully. Now let's run the tests to check for any table-parsing issues at runtime (they'll fail on the actual assertion since `splitOrder` throws `UnsupportedOperationException`, but this confirms the tables parse and convert correctly).

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

