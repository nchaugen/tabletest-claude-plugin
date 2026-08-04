# Narration — 30

The agent's visible narration, thinking cost and file writes, in order, distilled from
`conversation.jsonl`. Thinking text appears when the run asked the CLI for a summary
(`thinking_display: "summarized"`); the per-block token counts appear either way.

_[~93 thinking tokens (4 deltas)]_

> (thinking) The user is asking for TableTests for the order splitting feature, which aligns with the tabletest skill I can use.

_[~84 thinking tokens (4 deltas)]_

> (thinking) I'll start by examining the existing project structure to get a sense of the domain model and what's already been stubbed out.

I'll start by exploring the existing project structure to understand the domain model and stub `OrderSplitter`.

_[~268 thinking tokens (9 deltas)]_

> (thinking) I'm noticing the domain model has companion groups defined in two places with different structures — Order stores them as a list of lists of strings, while WarehouseInventory uses a list of sets of strings. This feels redundant, and I'm wondering if one of these is actually unnecessary or if they serve different purposes in the shipping logic.
> 
> Let me check what tests already exist to understand how the project is structured.

_[~102 thinking tokens (5 deltas)]_

> (thinking) I should check whether a settings.gradle file exists since it's optional for single-module projects, and verify what Java version is available locally along with whether the Gradle wrapper is set up.

_[~185 thinking tokens (4 deltas)]_

> (thinking) There's a Java version mismatch — the build requires Java 25 but only Java 21 is available locally. Since this is a stub project, the tests might run in a different environment during grading, but I should still try running them locally to verify everything works.

_[~20,559 thinking tokens (455 deltas)]_

> (thinking) The Gradle daemon is set up with JDK 26, which should work fine for source and target compatibility at version 25. I need to add the tabletest dependency to the build configuration, then think through the domain rules for how shipments should be organized—items with different fulfillment types or delivery addresses need to go into separate shipments, and there's a distinction between in-stock items that ship immediately versus backorders.
> 
> Looking at the rules more carefully, I see that fulfillment type and delivery address are both grouping keys that determine which items belong together in a shipment. These two rules are closely related, so I should combine them into a single table that groups items by their fulfillment context rather than treating them as separate concerns. For PICKUP items, I'll set deliveryAddress to null since they don't need an address. For testing the OrderSplitter, I need to verify both the shipment count and how items are grouped—just counting shipments alone is too weak, so I'll represent the expected output as a list of shipment groups, each containing the productIds, to check how items partition into shipments regardless of their order.
> 
> I'll create a helper that converts the actual List<Shipment> result into a Set<Set<String>> of product ID groupings, then compare it against the expected Set<Set<String>>. This approach works consistently across all test tables for fulfillment/address splits, stock timing splits, warehouse selection, and companion grouping.
> 
> Now I'm planning the test tables, starting with one that verifies items with different fulfillment types or delivery addresses can't share a shipment.
> 
> For the first table, I need an Items column representing a list of order items, each with productId, fulfillmentType, and deliveryAddress. Since OrderItem requires a quantity field but it's not part of the splitting rules, I'll default quantity to 1 across all rows via a converter, focusing only on the fields that actually affect grouping.
> 
> Rather than spreading items across multiple columns, I'll use a list-of-maps pattern where each map contains the three varying fields, then convert each map to an OrderItem with the quantity defaulted.
> 
> For the WarehouseInventory, since all test items are in-stock at the same warehouse, I can build that as a fixture—either through a converter or a helper method—to keep the test method clean and focused on arrange-act-assert.
> 
> I'm thinking about how to represent the expected shipments: they're groups of product IDs, so using Set<Set<String>> makes sense since the order from the splitter is unspecified and we only care about which products end up together. So when the parameter type is actually Set<Set<String>>, the outer braces map directly to the Set type without triggering cartesian expansion—that only happens when the column type isn't a Set. The nested notation like {{1,2},{3}} becomes Set<Set<Integer>>, and for product IDs like "camera-body", I won't need quoting unless they contain special characters. This approach should work. Now I'm sketching out a helper method to extract shipment groups...
> 
> Writing helper method...
> 
> Now I need to design the test tables consistently, using this comparison logic throughout. The first table will verify that items separate into different shipments based on fulfillment type and delivery address—so two items with the same type and address go together, but different types or addresses force them apart.
> 
> Let me map out the test scenarios: items with matching delivery address and type should group into one shipment, items with different addresses should split, mixing delivery and pickup types should separate them, and pickup items without addresses should still group together. I'm also checking the three-item case where two share an address and one doesn't, creating two shipments. That gives me five solid test rows covering the grouping behavior.
> 
> For the address field in the test data, I'm thinking about how to handle pickup items that don't have a delivery address. Rather than trying to represent blank values in the map syntax, I'll just omit the address key entirely for pickup items—the converter can use `getOrDefault` to return null when the key is missing, which is cleaner than forcing an empty value into the map structure.
> 
> The test data will have variable-width rows: delivery items include the address key while pickup items don't. The converter method handles this gracefully by calling `fields.get("address")`, which naturally returns null if the key isn't present. Since quantity doesn't factor into the splitting rules, I'm keeping it fixed at 1 throughout the tests rather than varying it as a test dimension.
> 
> Now moving to the second table covering stock timing — items that are in-stock ship immediately, while backordered or pre-ordered items ship separately once they become available, without holding back the in-stock inventory.
> 
> I need to capture the stock status for each item from warehouse inventory and verify that shipments are properly grouped by availability. The expectation should show which items end up together in each shipment along with that shipment's availability status, so I'm modeling this as a list of shipment groups where each group tracks its items and corresponding availability. whether backordered and pre-ordered items should ship together or separately depends on the same rule: they're both non-immediate, so they'd group into a single when-available shipment rather than splitting further. we need to be careful about how we structure the test row since we're using a value set for the status field. A two-item scenario with separate columns for each item's stock status should be sufficient to demonstrate the grouping behavior, since the rule is specifically about how stock availability determines shipment grouping rather than handling arbitrary list structures. The challenge now is figuring out how to represent each shipment group's availability in the output, since the grouping itself is already determined by which items are in-stock versus backordered or pre-ordered.
> 
> Rather than showing a generic shipments column, I'll make the availability explicit by adding two separate expectation columns: one for items shipping immediately and one for items shipping when available. This directly reflects the domain rule and makes the test expectations clearer without requiring the test to reimplement the grouping logic itself.
> 
> Generating test table... Using value sets across different columns would create a cartesian product of all combinations, but that's actually fine here since all those permutations (backordered with backordered, backordered with pre-ordered, etc.) should produce the same grouped result anyway, making it a good candidate for consolidation with value sets.
> 
> I can collapse the previous three separate rows into one elegant test case that uses value sets for both items' stock statuses, demonstrating that backordered and pre-ordered are equivalent from the rule's perspective while covering all four combinations in a single row.
> 
> Then I need a row showing that in-stock items ship immediately even when paired with non-in-stock items, which expands to two cases covering both backordered and pre-ordered scenarios. And finally a row for when both items are in stock, where everything ships immediately.
> 
> That gives me three rows total with seven test cases that cover all the key obligations without redundancy. Now I'm wondering whether I actually need separate product ID columns since I'm always using the same two items p1 and p2.
> 
> I should clarify in the test description that productIds are fixed as p1 and p2 across all rows, and that fulfillment type and address are also held constant at DELIVERY and addr1 since those are tested separately in the first table. The warehouse W1 is the specific inventory fixture holding both items with their respective stock statuses. For the expectation columns, I'm using Set<String> to represent shipment types, where a blank cell naturally maps to null when there's no shipment of that type.
> 
> Now I need to set up the WarehouseInventory fixture with a single warehouse W1 where p1 has its stock status and p2 has its stock status, with a helper method to build this. The third table tests warehouse selection logic — the rule is to pick the combination of warehouses that covers the order in the fewest shipments, so I need to set up multiple warehouses with partial stock to verify the splitter actually minimizes shipment count rather than just picking the first available warehouse.
> 
> For the test case: if Warehouse B has all three items in stock, it should be chosen over using Warehouse A for items 1&2 plus another warehouse for item 3, since that would create two shipments instead of one. The obligations are straightforward — when a single warehouse covers everything, use it; when multiple warehouses could work but one alone covers all items, that one gets selected.
> 
> When no single warehouse has everything, I need to find the minimum set of warehouses that together cover all items. This becomes a set-cover problem where I'm minimizing the number of distinct warehouses used. I'm designing a test scenario with three products and warehouse configurations where a two-warehouse combination like {A,B} covers all items, but a three-warehouse option {A,C,D} also exists (each warehouse holding one item). The algorithm should correctly pick the two-warehouse solution. So I need to refine the test case to ensure there's a unique minimal solution rather than multiple equally valid warehouse combinations. Let me design the items and warehouse stock more carefully so the algorithm has only one optimal choice. I'm realizing the test might be flaky if the minimal solution allows multiple valid item groupings, so I should redesign the scenario to have a unique optimal solution where the warehouse assignments are forced by the product distribution itself—maybe using four products split exclusively across two warehouses with no overlap, so there's only one way to achieve the minimum shipment count.
> 
> But that's too trivial since there's no alternative to compare against. What I really need is a scenario where a naive algorithm would pick a suboptimal solution—like assigning items individually to their best-stocked warehouse—while the optimal approach groups everything together. For instance, with three products where one warehouse has all three in stock and another has just one, a naive per-item assignment might split across both warehouses, but the optimal solution consolidates everything into a single shipment from the warehouse with full coverage.
> 
> For testing the case where multiple warehouses are needed, I need products that genuinely require combining warehouses, but I also want to verify the algorithm picks the minimal combination. The challenge is setting up a scenario with multiple valid combinations so I can confirm it chooses the one with the fewest shipments rather than just defaulting to the only possible option.
> 
> If I add a third warehouse that duplicates some inventory from the first, I create ambiguity in which warehouse gets selected—but that's actually fine for my test. Since I'm comparing shipments by their product groupings rather than warehouse IDs, two different warehouse combinations that produce the same product groupings will look identical to my assertion logic, so the test passes regardless of which specific warehouse was chosen. W1 has p1 and p2, while W2 stocks all three products. This setup forces a choice between two valid minimal covers: either picking W1 alone (incomplete, doesn't work) or W2 alone (complete, 1 shipment), demonstrating that when multiple options exist, the algorithm must select the genuinely minimal one. Row B flips the scenario—W2 has everything while W1 only has two items, so the algorithm should still pick W2 for a single shipment. Row C introduces a genuinely different case where no warehouse covers the full order, forcing a split across two warehouses. I'm questioning whether rows A and B are actually testing different behaviors or if they're redundant given they both expect the same outcome (one shipment from the warehouse with complete coverage). So I should keep only B since it's the stronger test—a decoy covering more items creates a tougher trap that wrong algorithms might fall into. Now I'm reconsidering the rule itself: when splitting across warehouses, the obligation is to minimize shipment count by picking the warehouse combination that covers the order in the fewest shipments, starting with the case where a single warehouse covers the whole order.
> 
> For the second obligation, I need a scenario with multiple warehouses where a 2-warehouse combination and a 3-warehouse combination both fully cover the order, but the 2-warehouse option is chosen. Let me construct this with four products across four warehouses: W1 has p1 and p2, W2 has p3 and p4, W3 has only p1, and W4 has p2, p3, and p4. The combination {W1, W2} covers all four products in exactly 2 shipments, which is the minimal count and unambiguously better than any 3-warehouse alternative, so this satisfies the obligation.
> 
> But I'm realizing I need to test whether the algorithm actually chooses the minimum when multiple valid options exist. If I include W3 with full coverage, that collapses back to obligation 1. Instead, I should construct a case where a greedy item-by-item approach might unnecessarily produce 3 shipments, while the optimal solution uses only 2 — that would really demonstrate the algorithm's ability to minimize. Actually, checking whether any single warehouse covers all three items—W1 has p1 and p3, W2 has p1 and p2, so no single warehouse has all three. The minimum is indeed 2 shipments, and the naive greedy approach happens to achieve that here. For a test, I don't need to construct an adversarial case that breaks every possible wrong implementation; I just need to specify the correct expected output for the given rule, and the test validates that behavior.
> 
> I'll use two test rows: one where a single warehouse covers the entire order (with a decoy partial warehouse to ensure the algorithm picks the full-cover option), and another where products are split across warehouses requiring a minimal two-shipment solution. That demonstrates the core minimization principle without over-engineering the example. The shipment groupings already capture the warehouse count implicitly, so adding a separate count column would be redundant. I should verify that `Shipment.getWarehouseId()` is traceable to ensure the warehouse choices make sense, though the specific warehouse IDs might not matter for the test expectations themselves.
> 
> Since I've already removed decoys from the test data (W1 provides full coverage while W2 only partial), the warehouse ID becomes unambiguous and worth including in the expectation. I'll restructure the expectation as a map from warehouse ID to product IDs, which directly captures which warehouse fulfills which items and verifies that the algorithm picked the correct warehouse combination. This stronger representation makes sense for Table 3 and its companions where warehouse identity matters, though Tables 1 and 2 don't need this level of detail.
> 
> The tricky part is consistency — the same concept (expected shipments) should ideally use the same notation across tables, but the rules also allow different concerns to have different needs. I think it's acceptable to use two notations: a simple grouping (set of sets) for Tables 1-2 and 5 when warehouse identity isn't critical, and a warehouse-to-products map for Table 3 where warehouse selection is the core rule being tested. Actually, Table 5 (companions) does care about warehouse identity since companions must ship from the same warehouse, so I should use the map notation there too or represent it as a set of warehouse-item pairs.
> 
> Now for Table 4: companion products should ship together from the same warehouse when possible, but this yields when incompatible (different fulfillment addresses or stock unavailability forces a split). The obligation is that if companions are both available at a common warehouse, they ship together from that warehouse even if another warehouse could serve one alone; if they're not available together anywhere, the rule allows them to split. The hard constraint of fulfillment/address splitting overrides the soft preference for companion grouping, so I'll document this precedence as a row within the companion table itself—it's really just clarifying what "when possible" means, since hard rules naturally take priority. Can't ship together when availability differs, even from the same warehouse—that's the timing rule taking precedence over companionship. So the core obligations are: companions prefer a shared warehouse when both items are stocked there, they split to separate warehouses when no common option exists, and hard constraints like different addresses override the companion preference entirely.
> 
> Let me test this with a simple two-product scenario using a camera body and lens marked as companions. First case: both items stock at W1, but W1 is also the only place with the camera body alone—they should ship together from W1 since that's the shared warehouse. Second case: camera body only at W1 and lens only at W2, forcing them into separate shipments from their respective warehouses.
> 
> For the third scenario, different delivery addresses override the companion preference entirely—the camera body goes to address 1 and the lens to address 2, so even though W1 has both in stock, they split into two separate shipments. Since there's only one warehouse available, I don't need to track warehouse IDs in the expected output; the key point is that the address rule takes precedence, resulting in two singleton shipment groups.
> 
> Now I'm considering a fourth test case around stock timing: the camera body is in stock at W1 while the lens is backordered there. Even though they'd normally ship from the same warehouse to the same address, the immediate-versus-when-available split overrides the companion preference, creating two shipments. Between these four scenarios—items shipped together when possible, split when no common warehouse exists, split by address override, and split by stock-timing differences—I have enough coverage of the distinct fulfillment rules without over-engineering the test suite.
> 
> For the first test case, I need to verify that the algorithm actually chose the common warehouse rather than splitting items across separate warehouses. Using a simple set-based grouping approach works well here: I expect a single shipment containing both items, and if the algorithm incorrectly split them, the test would show two separate shipment sets and fail. This keeps the notation consistent across all test cases without needing to track warehouse identities explicitly.
> 
> Now I'm reconsidering whether Table 3 should also stick with grouping-based expectations rather than exposing warehouse IDs. The core rule being tested is about minimizing shipment count through smart grouping, and warehouse identity is really just an implementation detail—the test author arbitrarily labeled them "W1" and "W2" anyway. What matters is which items end up grouped together, not the specific warehouse ID label.
> 
> But then again, since `getWarehouseId()` is part of the actual Shipment output and the rule is fundamentally about "which warehouse combination is chosen," including the warehouse ID in the expectation does add real traceability value. It directly tests that the system selected the right warehouse, not just that items were grouped correctly. The notation gets tricky with nested collections though—I need to find a cleaner way to express this without making the expectation overly complex.
> 
> I'll use a Map<String, Set<String>> format where the key is the warehouse ID and the value is the set of product IDs assigned to that warehouse. For a single shipment, I can write it as `[W1: {p1,p2,p3}]`, and for multiple shipments across warehouses, something like `[W1: {p1,p2}, W2: {p3,p4}]`. That's much cleaner and directly shows the warehouse-to-products mapping.
> 
> Now I'm laying out the actual test rows for Table 3 using this notation, starting with the case where one warehouse covers the entire order versus a partial alternative, then moving into scenarios where the system needs to split across multiple warehouses.
> 
> I'm also considering whether to add a third test case that explicitly validates the minimization logic—something that shows the system choosing a 2-warehouse combination over a valid but larger 3-warehouse alternative, to rigorously test that it's not just splitting when forced but actually optimizing for the smallest number of shipments. I'll stick with the two test cases since they adequately demonstrate the core minimization logic without introducing unnecessary complexity. Now I'm reconsidering whether the table structure itself needs adjustment. Companion grouping adds a fourth table with 4 rows to test that rule. Now I'm mapping each rule to its corresponding table: fulfillment type and delivery address variations go to Table 1, stock availability timing to Table 2, shipment minimization to Table 3, and companion grouping to Table 4.
> 
> For the type converters, I'll create an OrderItem converter that handles the Map notation with productId, type, and optional address keys, but since Tables 2-4 use simpler item structures where stock status comes from WarehouseInventory rather than OrderItem itself, I'll just hardcode the fixture items directly in each test method as a small helper function.
> 
> I'm planning out the static helper methods I'll need at the bottom of the class: a method to build WarehouseInventory with stock statuses mapped by warehouse ID, and I can construct Order objects inline using the List.of() constructor rather than a separate wrapper method.
> 
> For the table converters, I need a TypeConverter to parse OrderItem from the table's row fields, and then comparison helpers to extract the actual shipment groups and warehouse-to-product mappings from the results so I can verify them against the expected values. Since StockStatus and FulfillmentType are enums, the framework's built-in enum conversion should handle those directly without needing custom converters. For the OrderSplitter tests, I need to decide whether to use Order's companionGroups field (which the constructor accepts directly) or WarehouseInventory's companionGroups field — both exist in the stub but seem redundant. Since Order already takes companionGroups in its constructor, that's the more straightforward approach for testing. I'll document the assumption about companion groups at the class level to keep things clear, then move on to building the OrderItem converter for the first table. For the remaining tables, I'll construct OrderItem instances directly in the test methods since they follow a consistent pattern with the same product IDs and fulfillment details across all rows.
> 
> For the WarehouseInventory fixtures, I'm setting up inline helpers that add stock entries for each warehouse and product combination. Table 3 will need a different approach since its columns represent which warehouses have which items in stock, so I'll use a map structure to express that relationship more naturally.
> 
> Writing the TypeConverter... flict with the converter approach since those tables construct WarehouseInventory directly without going through the TypeConverter mechanism. But looking more carefully at Table4, it actually does need per-row varying inventory too — the companion products have different stock availability across warehouses that changes per row, so I need to reconsider whether Table4 should also use a converter or handle its inventory representation differently.
> 
> The challenge is that Table4's stock variations are more complex than just IN_STOCK sets; Row4 specifically needs mixed statuses like one item IN_STOCK while another is BACKORDERED. A simple Map<String,Set<String>> converter won't capture that granularity, so I'd need something like a nested structure mapping warehouses to per-item status maps, though that gets unwieldy with three levels of nesting.
> 
> Writing converter logic... I'm weighing whether to use the nested inventory map notation for Table2 or keep explicit item-specific stock columns—the nested approach is consistent with Tables 3 and 4, but explicit columns like "Item1 Stock" and "Item2 Stock" are clearer for representing the specific value sets (BACKORDERED, PRE_ORDERED) that matter for this table's concern around stock status timing. For Table3 and Table4, I'll use the nested map notation consistently to represent warehouse stock across both tables with a shared converter. Table3's rows will use this format even though they only need IN_STOCK status everywhere, since the consistency with Table4 is worth the slight verbosity. Now I need to handle the OrderItem lists for both tables. Building the item mapping... For Table4, I need to handle the companion products (camera and lens) with separate address columns since row3 specifically tests address overrides—so I'll add "Camera Warehouse Stock" and "Lens Address" columns, where the lens address defaults to match the camera's address except in row3 where it diverges to test that interaction. Filling in the table rows with specific warehouse stock configurations and shipment outcomes for each scenario. I'm deciding whether testing one hard constraint override (the address split) plus one stock-timing override is sufficient to demonstrate the boundary principle, rather than exhaustively re-crossing every hard rule against the companion preference rule—which would be redundant since I'm already establishing that constraints override soft preferences through these two examples.
> 
> Now I'll create the OrderItem instances directly in the test method with fixed product IDs for "cb" and "lens" at quantity 1, using the address columns and setting fulfillment to DELIVERY. Then I need to write the test class itself and set up helper conversion methods: one to convert a map of fields into an OrderItem, and another to convert a nested map structure into WarehouseInventory for the stock data. I'm also reconsidering whether I actually need a separate converter for the Products list in Table3, since that's just a list of strings that the framework should handle natively.
> 
> For the assertions, I'll need some helper methods to extract and compare shipment data — one to group shipments by their product sets, another to organize shipments by warehouse. But Table2 is different since it expects individual availability columns rather than grouped shipments, so I'll need a helper that extracts product IDs matching a specific availability level, returning null if no shipments match that availability, or the union of product IDs if multiple shipments exist with the same availability.
> 
> For Table1 where there's no stock variation, I'll create a helper that builds a default WarehouseInventory with all items marked as IN_STOCK at a single warehouse.
> 
> Writing assertion methods... Writing test cases... Writing test cases... Finishing the assertion for when-available shipments, then handling null values in the test table for cases where immediate shipment isn't applicable. Setting up another test scenario where all items are delivery to the same address with varying warehouse coverage, focusing on how fulfillment gets grouped by warehouse rather than by availability timing. Writing test methods... Writing test method... different converters handle OrderItem and WarehouseInventory separately, which is correct. I'm verifying the quoting rules for cell values—plain tokens like "p1", "DELIVERY", and "addr1" don't need quotes since they lack special characters. Now checking the nested map syntax depth for the warehouse stock cells to ensure the bracket nesting is consistent across tables. I should verify the nested map structure using the format-table.sh parse-check script as the skill instructions recommend, and ideally run the gradle tests to validate everything compiles correctly. For the "Shipments By Warehouse" column with `[W1: {p1, p2, p3}]`, that's a Map<String, Set<String>> which the built-in recursion should handle directly without needing a custom converter. Now I'm checking Table2's value set columns where `{BACKORDERED, PRE_ORDERED}` represents the entire cell value for a StockStatus enum parameter.
> 
> The curly braces at the cell level trigger Cartesian product expansion for non-collection parameter types, which matches the skill's documented behavior. Now I'm ready to write the full test file with proper imports and helper methods. I'll name it OrderSplitterTest and place it in src/test/java/com/example/orders/. For imports, I need the TableTest framework classes, static assertion methods, and Java utilities like Collectors for modern stream operations. Given Java 25 as the target, I can use var declarations and Stream.toList() for cleaner code.
> 
> I need to update build.gradle to add the tabletest-junit dependency alongside the existing JUnit Jupiter 6.0.3, which already satisfies the 5.11+ requirement. Then I'll define helper methods to construct test fixtures—one to populate warehouse inventory with all items in stock, another to extract product groupings from shipments, and a third to organize shipments by warehouse ID.
> 
> Adding helper methods... Finishing the type converter for warehouse inventory, then verifying that the Shipment class has the necessary methods and that OrderSplitter has a no-arg constructor available. Now I'll write out the file and run the format validation script, followed by a test compilation to check for any issues. Now I need to update the build.gradle file to add the necessary dependencies and configuration.

Now let me check the TableTest scripts and update build.gradle with the required dependency.

**Edit ./build.gradle (edit)**

Now let me write the test class incrementally, starting with the first table.

**Write ./src/test/java/com/example/orders/OrderSplitterTest.java (59 lines)**

_[~93 thinking tokens (4 deltas)]_

> (thinking) I need to check what the formatter changed in the file before making any further modifications.

Good, the table was auto-formatted and parsed correctly (confirming it's valid syntax, since the formatter only reformats parseable tables). Now let's add Table 2 (stock timing).

**Edit ./src/test/java/com/example/orders/OrderSplitterTest.java (edit)**

Table 2 formatted cleanly, confirming it parsed. Now let's add Table 3 (warehouse minimization) with its `WarehouseInventory` converter.

**Edit ./src/test/java/com/example/orders/OrderSplitterTest.java (edit)**

Now let's add the final table for companion products, then review the whole file.

Table 3 also formatted cleanly. Now let's add the final table for companion products.

**Edit ./src/test/java/com/example/orders/OrderSplitterTest.java (edit)**

All four tables parsed cleanly. Let's read the final file to check overall structure and formatting.

All four tables parsed correctly. Let's move helper/converter methods to the bottom of the file for readability, then compile to confirm everything works.

**Write ./src/test/java/com/example/orders/OrderSplitterTest.java (151 lines)**

All tables reformatted cleanly, confirming they parse. Now let's compile the test sources to verify everything is correct (the tests themselves will fail at runtime since `OrderSplitter` is an unimplemented stub — that's expected).

Compilation succeeds. Let's run the tests to confirm the tables load correctly and produce the expected number of test cases (they'll fail at assertion time since `OrderSplitter` throws `UnsupportedOperationException`, which is expected).

_[~315 thinking tokens (7 deltas)]_

> (thinking) I'm verifying the test count breakdown across all four tables—Table1 contributes 5 tests, Table2 generates 7 through value-set expansion (1 + 4 + 2 scenarios), Table3 adds 2, and Table4 adds 4, totaling 18 as expected. All tests are failing with UnsupportedOperationException, which confirms the tables are parsing correctly and expanding value-sets properly.

All 18 generated test cases compile and run (failing only with `UnsupportedOperationException` from the unimplemented stub, as expected). The count breaks down as: Table 1 (5 rows) + Table 2 (3 rows, 7 cases via value-set expansion) + Table 3 (2 rows) + Table 4 (4 rows) = 18, confirming the value-set cartesian expansion worked correctly too.

## Summary

Wrote `src/test/java/com/example/orders/OrderSplitterTest.java` with four `@TableTest` methods, one per splitting rule:

1. **`groupsItemsByFulfillmentTypeAndDeliveryAddress`** — fulfillment type and delivery address as the grouping key (single warehouse, all in stock, held constant).
2. **`doesNotHoldInStockItemsForItemsAwaitingAvailability`** — in-stock items ship immediately while backordered/pre-ordered items wait, using a value set `{BACKORDERED, PRE_ORDERED}` to show both statuses behave identically.
3. **`choosesTheWarehouseCombinationWithFewestShipments`** — prefers a single warehouse that covers the whole order, and splits across the minimal warehouse set when none does alone.
4. **`shipsCompanionProductsFromTheSameWarehouseWhenPossible`** — companions group onto a shared warehouse when possible, split when no warehouse stocks both, and yield to the hard address/availability constraints when those conflict.

Each table compares actual `Shipment` output via small helpers (`shipmentGroups`, `shipmentsByWarehouseId`, `productIdsWithAvailability`) rather than exact list equality, since shipment ordering isn't part of the contract. Two `@TypeConverter` methods handle the composite cell shapes: one builds `OrderItem` from a map, the other builds `WarehouseInventory` from a nested warehouse→product→status map.

**Assumptions made** (documented in each table's `@Description`, and noted here since they affect the implementation you'll write):
- Companion groups come from `Order`'s constructor param (`List<List<String>>`), not `WarehouseInventory`'s separate `companionGroups` field — the latter appears to be an unused/duplicate stub field, so I left it untouched.
- `PICKUP` items carry no delivery address (omitted from the item map rather than blank).
- Quantity doesn't affect splitting per the stated rules, so it's fixed at 1 everywhere and not exposed as a column.

Also added the `tabletest-junit:1.2.2` dependency to `build.gradle`. `gradle compileTestJava` succeeds, and `gradle test` runs all 18 generated cases, each failing only on the stub's `UnsupportedOperationException` — confirming the tests are structurally sound and ready for the real implementation.

