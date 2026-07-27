# Narration — 30

The agent's visible narration and file writes, in order, distilled from
`conversation.jsonl`. Thinking text is absent unless the model returns it in the
clear (Claude 5-family models encrypt it).

I'll start by exploring the existing domain model and stub to understand the current API shape.

**Edit /private/var/folders/fz/zxx_qbvj49gbql83cc5l47pr0000gn/T/eval-skill-1785164919741/eval-30-work/build.gradle (edit)**

Now I'll write the test class. Let me create it with all five concern tables.

**Write /private/var/folders/fz/zxx_qbvj49gbql83cc5l47pr0000gn/T/eval-skill-1785164919741/eval-30-work/src/test/java/com/example/orders/OrderSplitterTest.java (184 lines)**

Now let's compile and run to validate the table syntax and converters actually work.

**Edit /private/var/folders/fz/zxx_qbvj49gbql83cc5l47pr0000gn/T/eval-skill-1785164919741/eval-30-work/src/test/java/com/example/orders/OrderSplitterTest.java (edit)**

This confirms the design works correctly: all 12 test cases compiled, all table parsing/type-conversion succeeded (custom `List<OrderItem>` element conversion, `WarehouseInventory` conversion, and nested `Map<String, Set<String>>` expectation columns all resolved fine), and every failure is exactly `UnsupportedOperationException` from the stub — i.e., they fail only because the splitting logic isn't implemented yet, as expected.

Now let me verify the expected values I hand-computed are actually correct by reasoning through the scenarios once more, and double check the file for quality.

Let me refine a few scenario names that restate the outcome rather than describing the condition (per the skill's "Name Scenarios Descriptively" rule).

**Edit /private/var/folders/fz/zxx_qbvj49gbql83cc5l47pr0000gn/T/eval-skill-1785164919741/eval-30-work/src/test/java/com/example/orders/OrderSplitterTest.java (edit)**

**Edit /private/var/folders/fz/zxx_qbvj49gbql83cc5l47pr0000gn/T/eval-skill-1785164919741/eval-30-work/src/test/java/com/example/orders/OrderSplitterTest.java (edit)**

Let's re-run the tests to confirm everything still compiles and parses correctly after the renames.

All 12 test cases still parse and dispatch correctly — every failure is the expected `UnsupportedOperationException` from the unimplemented stub, confirming the table syntax, custom converters, and nested map/set expectation columns are all wired correctly.

I've written the tests. Here's a summary of the work.

## What I did

Added `src/test/java/com/example/orders/OrderSplitterTest.java` with 5 `@TableTest` methods, one per splitting concern, plus added the `tabletest-junit` dependency to `build.gradle`:

1. **`separatesShipmentsByFulfillmentType`** — DELIVERY vs PICKUP items land in separate shipments (single warehouse, all in stock, held constant).
2. **`separatesShipmentsByDeliveryAddress`** — different delivery addresses split shipments (all DELIVERY, in stock, single warehouse).
3. **`shipsInStockItemsWithoutWaitingForDelayedItems`** — in-stock items ship `IMMEDIATE`, backordered/pre-ordered ship `WHEN_AVAILABLE`, and the two delayed statuses pool into the same shipment rather than splitting further.
4. **`minimisesShipmentCountAcrossWarehouses`** — prefers a single covering warehouse when one exists (rather than an arbitrary/flexible assignment that would fragment it), and correctly splits to the minimum count when no single warehouse suffices.
5. **`keepsCompanionsTogetherWhenPossible`** — when two warehouse combinations achieve the same minimal shipment count, the one keeping companions together wins; but companions still split when no shared warehouse exists.

Each concern isolates its one axis (fulfillment type, address, availability, warehouse coverage, companion affinity) while holding everything else constant, per the skill's "one rule, one axis" guidance. Assumptions are recorded in each table's `@Description`.

**Assumptions made** (stated since this is non-interactive):
- Companion grouping is a tie-breaker among equally-minimal warehouse combinations — it doesn't override rule 4's shipment-count minimization.
- PICKUP items carry no delivery address (`null`).
- Item quantity doesn't affect splitting, so it's defaulted to 1 (not a tested concern).
- I used `WarehouseInventory` (not `Order`'s own `companionGroups` field) for companion data in one path and `Order`'s constructor overload in the other — both existed in the stub as duplicate mechanisms; I picked `Order`'s constructor since it's the more direct entry point.

**Verification**: Since implementing the splitter wasn't in scope, I compiled and ran the suite against the stub — all 12 generated test cases parse and dispatch correctly (custom `OrderItem`/`WarehouseInventory` converters, nested `Map<String, Set<String>>` expectation columns), each failing only with the stub's `UnsupportedOperationException`, confirming the tests are wired correctly and ready for the real implementation.

No production stubs needed adjusting beyond adding the test dependency — the existing `Order`, `OrderItem`, `WarehouseInventory`, and `Shipment` APIs were sufficient.

