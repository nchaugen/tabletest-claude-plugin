# Narration — 16

The agent's visible narration, thinking cost and file writes, in order, distilled from
`conversation.jsonl`. Thinking text appears when the run asked the CLI for a summary
(`thinking_display: "summarized"`); the per-block token counts appear either way.

_[~15,624 thinking tokens (369 deltas)]_

> (thinking) I'm breaking down the fulfillment logic into distinct rules based on fulfillment method and availability status, then mapping out how warehouse allocation works to minimize shipment counts when a single warehouse can't cover the full order quantity. so I need to decide whether these three constraints—fulfillment method, delivery address, and availability status—should be one table or three separate ones. Each constraint can stand alone as a rule: items with different fulfillment methods can't combine, items going to different addresses can't combine, items with different availability can't combine. They're independent conditions that don't reference each other, which suggests they should be separate tables per the one-rule-one-axis principle, but I'm realizing there might be a practical reason to keep them together.
> 
> Looking at this as an actual system workflow: the first step is partitioning order lines into shipment groups based on these hard constraints—fulfillment method, address, and availability status—then within each group, assigning a warehouse and checking companion affinity. Two items can only ship together if they match on all these dimensions simultaneously, plus warehouse compatibility.
> 
> I'm seeing this as three distinct rules: the first groups lines by those non-negotiable attributes, the second assigns warehouses to minimize shipment count, and the third acts as a tiebreaker when multiple warehouse combinations are equally minimal—keeping companion items together. That third rule isn't really a separate combining table; it's more of a preference within the warehouse allocation decision itself.
> 
> So I should consolidate this: Table 2 should handle both minimizing shipments AND keeping companions together as part of the same warehouse-assignment logic, since the companion preference only kicks in when shipment counts are tied. The question is whether this dual objective actually creates a combining table or if it's just one rule with two criteria.
> 
> The key insight is that the tie-breaking condition depends on the first objective—it only applies when shipment counts are equal—so this is genuinely a single decision procedure with a primary and secondary objective. For the table structure, I can use compound cell notation to represent complex inputs like warehouse stock matrices and order line quantities, similar to how maps are represented.
> 
> Now I'm thinking through how to design the actual tables. The first table should partition order lines into shipment groups based on hard constraints—fulfillment method, ship-to address, and availability—that force lines into separate shipments regardless of inventory considerations. Each row would show a scenario with line items carrying those three attributes, and the output would be the resulting shipment groups. I'm settling on a notation where each line item is represented as `Item(Method, Address, Availability)` and the output groups items by their attribute combinations. Using compound cell values like `[A: pickup/Home/in-stock, B: delivery/Home/in-stock]` keeps it readable without needing to scale columns for each item, which would get unwieldy with variable order sizes.
> 
> For testing the grouping logic, I should focus on pairwise comparisons since the rule is symmetric — two items belong together if they share the same method, address, and availability status. So a table with two items per row makes sense, varying their attributes to see whether they end up in the same shipment group or not.
> 
> Generating markdown table... A plain AND of independent conditions doesn't require a combining table, so I'll stick with three separate tables—one for fulfillment method, one for ship-to address, and one for availability status—each treating the others as constant constraints.
> 
> Table 4 handles the inventory-distribution problem: allocating warehouses to fulfill item quantities while minimizing shipment count. Table 5 is where companion products interact with warehouse minimization—the question is whether this is a separate rule or integrated into Table 4. Since keeping companions together acts as a tie-breaker when warehouse allocation allows multiple solutions, I'm exploring whether these conditions reference each other or operate independently.
> 
> The key insight: "keep companions together" only has meaning within the context of minimizing shipments—it's a preference for which minimal-shipment solution to choose. This isn't a separate concern but rather part of the same rule: "allocate warehouses to minimize shipment count, breaking ties by keeping companion items together." Since tie-breaking is inherently a sub-clause of minimization, not an independent condition, I'm folding companions into Table 4 as part of the tie-breaking logic rather than treating it as a separate table.
> 
> But I should clarify the user's intent: is companion-together a soft preference (only applied when it doesn't increase shipments) or a hard constraint that could force more shipments than the theoretical minimum? The prompt says "if possible those should ship together," which suggests a soft preference, but I should flag this as an open question and potentially test a scenario where the two objectives genuinely conflict to pin down the actual requirement. I'll mark the companion-split conflict as an open question with a `?` since it's a key policy decision the user needs to confirm. The core insight is that hard constraints like fulfillment method and address take precedence over companion affinity — companions can only stay together when the fulfillment and availability rules don't force them apart, so companion grouping is really just a secondary optimization within the warehouse-selection layer. I'll add a small precedence table showing that hard shipment-group splits override companion affinity rules. Then I need to think through partial availability edge cases—specifically, when an item is completely out of stock or when total inventory across all warehouses falls short of the order quantity, should the system ship what's available and backorder the rest, or treat the entire line as backorder? Given the principle of shipping items when available, I'm leaning toward a split approach where available quantity ships now and the shortfall becomes a backorder.
> 
> For the warehouse minimization example with 6 shirts across Chicago (4), LA (1), and Houston (1), I need to verify that all three warehouses are actually required since no two-warehouse combination reaches 6 units.
> 
> Now I'm thinking through the algorithm more carefully: it should search across all possible warehouse combinations to find the minimum number needed to fulfill the order, rather than just taking warehouses in sequence. For instance, if Philadelphia and Washington each had 3 units, that two-warehouse solution would be better than splitting across three locations.
> 
> I'm setting up a table with realistic warehouse scenarios to test this logic, with columns for the item, ordered quantity, warehouse stock distribution, the resulting allocation, and shipment count. So the minimal shipment count is 2 warehouses (Philadelphia and Washington), which aligns with what the user was looking for. I'm also considering scenarios where no two-warehouse combination reaches the required quantity—like when stock is split across three locations (Chicago, LA, Houston)—forcing three shipments instead. There's also the case where total available stock falls short of the order, requiring a partial shipment now and a backorder for the remainder. And I need to handle tie-breaking when multiple warehouse pairs achieve the same minimal shipment count.
> 
> Now I'm thinking through how companion products factor in—when line items need to stay together at a single warehouse location, that constraint can override the minimal shipment count if the warehouses with the lowest count don't have all companion items in stock together.
> 
> The key scenarios are: companions available together at a warehouse that's already part of the minimal solution (straightforward), companions split would still hit the same shipment count but an alternative keeps them together (prefer the together option as a tie-breaker), keeping companions together requires extra shipments than the true minimum (open question whether the system accepts this trade-off or prioritizes minimization), companions can't be fulfilled together anywhere (ship separately since it's impossible), or one item is pickup and another is delivery (hard constraint forces separate shipments regardless).
> 
> I'm thinking through whether to combine companion affinity and warehouse minimization into a single table or keep them separate. The cleaner approach is two tables: one that handles pure warehouse allocation minimization without considering companions, and a second that specifically addresses tie-breaking when multiple equally-minimal solutions exist, showing how companion items get prioritized to stay together in those scenarios.
> 
> But there's a third dimension I need to account for—what happens when keeping companions together actually forces a non-minimal solution? That's a genuine conflict case that deserves its own rows in the tie-break table, marked as open questions since the precedence rule there isn't yet defined.
> 
> I also need to establish a separate precedence table showing that hard constraints like fulfillment method, address, and availability always override companion affinity and warehouse minimization, even when it means splitting companions across different shipments.
> 
> For the availability dimension, I'm distinguishing between in-stock, backorder, and pre-order items, though I'll treat backorder and pre-order as functionally equivalent for splitting purposes—both are "not currently available"—with the understanding that they might differ in their ETA fields but that's outside the core splitting logic.
> 
> I'm also recognizing that partial line quantities need their own handling: if a customer orders 3 units but only 2 are in stock, the line itself splits—ship the 2 available now and backorder the 1 remainder.
> 
> For multiple ship-to addresses without gift context, Table 2 already covers this: each item's specified address forces a group split, so there's no need for separate gift-specific logic—a gift item and a personal item are just two different addresses following the same rule.
> 
> Now I'm reconsidering whether Tables 1–3 (fulfillment method, address, availability) should remain separate or consolidate into a single table using a composite grouping key.
> 
> The core insight is that items ship together if and only if their method, address, and availability tuples match. But looking at the skill's guidance on families—if you can name what several tables have in common in one term, they're one concern—I'm questioning whether these three dimensions really form a family. A family works when members are different values of a single dimension, like adjustment types (renal impairment, low body weight, interacting drug). Here, fulfillment method, address, and availability are orthogonal attributes, not members of one family.
> 
> So keeping them as three separate tables, each holding the others constant, is the right call. Now I'm ready to draft concrete examples for all three tables using simple formats like `A(qty)` for line items and short labels for addresses and fulfillment methods.
> 
> For the first table on fulfillment splits, I'll set up columns for each item's fulfillment method, the shared address, shared availability, and a boolean column showing whether they ship together. The key scenarios are when both items use the same method versus when they differ—that's what determines if they go in the same shipment. Now I'm setting up the second table to show how shipments split by delivery address—same address keeps items together, but different addresses like a gift recipient versus the customer's home would create separate shipments. The third table gets more complex since availability affects not just grouping but also timing: items available now ship immediately while unavailable items get held, and I need to account for partial quantities per line item.
> 
> For the availability-based split, I'm designing a table that tracks what happens when some inventory is on hand and some isn't—fully stocked items ship right away, backordered and pre-order items stay held back, and the table captures the quantity shipped now versus held for each scenario. I'm realizing I need to split this into two separate decision tables: one for grouping items by availability readiness (matching the pairwise pattern of the earlier tables), and another for handling quantity splits within a single line item when it's only partially in stock. The grouping logic determines which items ship together, while the quantity arithmetic is a distinct rule for how much of each item goes into each shipment. Of course, the real question is whether backorder and pre-order items should be grouped together or split further based on their expected arrival dates. For this test suite, I'm treating all unavailable items as deferred—they get excluded from the immediate shipment—and whether they eventually ship together later depends on whether their ETAs match up, which is beyond the scope of this decision point.
> 
> So I'm simplifying Table 3a to focus on the core binary: in stock versus not in stock. I'll add a row noting the open question about whether two different unavailable items with different ETAs would split into separate future shipments, then move through the test cases—both items in stock ship together, mixed availability means the in-stock item ships now while the unavailable one gets held back, and so on.
> 
> For Table 3b, I'm mapping out how the system calculates split shipments based on available inventory: if I order more than what's in stock, some quantity ships immediately and the rest is held back pending availability.
> 
> Now moving to Table 4, which tackles the core warehouse selection problem. I need to define how the system chooses which warehouses to pull from when fulfilling an order, aiming to minimize the number of separate shipment locations. The warehouse stock will be represented as a map showing inventory levels across locations like Chicago, LA, and Houston, and the allocation will show which warehouses actually contribute to the shipment.
> 
> I'm laying out four scenarios: one where a single warehouse covers the full order, a two-warehouse split that matches the user's shirt example perfectly, a forced three-way split when no two warehouses suffice, and a case where total inventory falls short. I need to clarify the column naming — "Quantity To Fulfill" better captures that this is post-availability-split data, not the raw order quantity. But there's a tricky edge case: what if the combined warehouse stock falls short of the order, creating a partial fulfillment scenario where the SKU shows as in-stock overall but the network can't fully cover this particular order? The precedence matters here: I need to first determine what quantity can actually be fulfilled from available stock, then apply warehouse minimization only to that fulfillable portion. So I'm creating Table 4c to capture this combining scenario—when total network stock falls short of the order, it shows that availability-shortfall computation happens before warehouse selection, and the minimization constraint only applies to what's actually shippable now. The example demonstrates this: ordering 6 units against 5 total stock (split across Chicago and LA) means shipping 5 immediately using both warehouses and backordering the remaining 1.
> 
> For the tie-breaking question in Table 4, I need to add a row that surfaces when two different minimal warehouse combinations both satisfy the order quantity equally—this exposes the open question about how the system breaks ties. I'm constructing a scenario with multiple warehouses that have equal stock levels so that different two-warehouse combinations could both fulfill the order with the same warehouse count, making the tie-break rule explicit.
> 
> Now moving to Table 5, which focuses on companion line items—ensuring that when multiple warehouse allocations achieve the minimum shipment count, the system keeps related SKUs together at a single location rather than splitting them across warehouses.
> 
> For this table, I need to structure the warehouse stock data differently since companions are distinct SKUs (like a camera body and lens) rather than quantities of the same item split across locations. So I'm using a nested structure where each warehouse maps to its inventory per SKU—for example, Chicago might have Body:5 and Lens:5, while LA has Body:1 and Lens:0.
> 
> The first scenario is straightforward: both companion items are available together at one warehouse with sufficient stock, so they're allocated there as a single shipment. This establishes the baseline pattern before introducing the complexity of tie-breaking.
> 
> For the second scenario, I'm testing whether keeping companions together is always optimal or if splitting them across warehouses could yield the same minimal shipment count. I'm constructing a case where Denver has both items but other warehouses have them split, so I can see whether the algorithm correctly identifies that Denver alone is the better choice—or if I need to adjust the scenario so that keeping companions together and splitting them actually result in the same count, which would then let companion preference be the deciding factor. Let me construct a genuine tie-break scenario by adding an independent item—Cable—alongside Body and Lens. With stock distributed across Chicago, LA, and Denver such that no single location has everything, I need to find two different allocation strategies that result in the same shipment count but differ on whether companions stay together. Let me verify that LA and Denver together would also achieve 2 shipments: LA covers Body and Cable, Denver covers Lens, so that works as a 2-warehouse solution too. But since Chicago and LA keep the Body-Lens companions together while LA and Denver split them, the companion preference should favor Chicago and LA.
> 
> Now I'm looking at a case where keeping companions together would actually require more shipments than the true minimum. If I have Body and Lens as companions, but Denver has both items in stock while Chicago and LA each have only one, then using just Denver gives me 1 shipment—better than splitting across multiple warehouses. plies both Body and Lens but not Cable, so we'd need Chicago plus LA to get all three items—that's 2 warehouses minimum. But if we require companions (Body+Lens) to ship together, Chicago already has both, so we could fulfill Body+Lens from Chicago and Cable from LA, still using 2 warehouses. So this doesn't create a conflict either. The real conflict case needs a scenario where the minimum warehouse count to satisfy quantities forces a companion split, but adding one more warehouse lets us keep them together—which seems hard to construct since if a warehouse has both companions, using it should only help. The real conflict emerges when an alternative exists where companions could ship together, but choosing that option costs an extra shipment compared to the true minimum. For instance, with Body:1, Lens:1, Cable:1 where Chicago has both companions but lacks Cable, while another warehouse has Cable alone—forcing a choice between splitting them for the minimum or keeping them together at a higher cost. I see the issue now—keeping companions together at Chicago actually still gives a 2-shipment solution since I just pair it with one other location for the remaining items. To force the "keep together" constraint to be more expensive, I need to construct an order where Chicago (the only source of both companions) would require multiple additional shipments to fulfill everything else, while splitting the companions across different warehouses lets me consolidate better elsewhere. So I'm building an example with Body, Lens, Cable, and Mouse where Chicago has only the companions but lacks other items, forcing extra shipments if I insist on keeping them together. I'm realizing that constructing a genuine conflict between minimizing shipments and keeping companions together is tricky—if companions coexist at a warehouse with enough capacity, you can usually match the minimum shipment count by just adding another warehouse for remaining items. For companions-together to actually force more shipments, the warehouse holding them would need to lack capacity for other required items in a way that no alternative split could improve.
> 
> Let me work through a concrete example with five items: Body, Lens, Cable, Mouse, and Keyboard. If I split them optimally across two warehouses—say one covering Body, Cable, and Keyboard while another covers Lens and Mouse—I hit the minimum of two shipments. But if I require Body and Lens to ship together from a single warehouse that only stocks those two items, I'd still need two additional warehouses to cover the remaining three items, pushing me to three shipments total.
> 
> The key insight is that the split solution cleverly merges the companion pair (Body and Lens) into one of the "rest" warehouses, whereas the together constraint forces them into their own dedicated warehouse. To make this work, I need the remaining items to be distributed such that no single warehouse can cover all three—so Chicago holds just the companions while Denver covers Body plus two of the remaining items. So the split approach gives us 2 shipments total while keeping companions separated, but the together approach requires 3 shipments to keep Body and Lens in the same shipment. This is a clear case where the constraint creates a genuine trade-off—minimizing shipments means splitting the companions, but honoring the companion requirement costs an extra shipment. This example nicely illustrates the tension between those two objectives.
> 
> I'll present both solutions as candidate outcomes and mark this as the key decision point for the user, since the business policy on whether to prioritize shipment count or companion integrity isn't determined yet. I'll use simplified notation with a legend to keep the warehouse stock tables readable despite the complexity.
> 
> Now I'm working through the precedence rules — hard shipment-group splits based on fulfillment method, delivery address, or availability status override companion affinity entirely. So even if items are companions, if one's pickup and the other's delivery, or one's in stock while the other's backordered, they ship separately regardless of the companion preference.
> 
> I'm also flagging a couple of open questions that need resolution: when multiple warehouse combinations result in the same minimal shipment count and companions aren't a factor, what's the secondary tie-breaker — is it shipping cost, distance to customer, or warehouse priority? And in cases where minimizing shipments genuinely conflicts with keeping companions together, which objective takes precedence?
> 
> There's also the question of how to handle different backorder and pre-order items with different ETAs — do they get grouped into one future shipment or split by their availability dates? And once a backordered item becomes available, does it automatically merge with other pending items for that customer or ship separately?
> 
> I'm documenting these as open items that require explicit business decisions, and I'll verify the tables against the checklist to ensure each one has a clear rule, concrete domain values, scenario names as conditions rather than outcomes, and proper use of thresholds and value sets.
> 
> For the final output, I'll write the markdown tables directly in my response with a notation guide beneath them explaining the warehouse and item quantity format, since the user is asking for test case scenarios rather than code that needs file editing. Finalizing the test tables with proper consolidation—merging the warehouse allocation scenarios into a single comprehensive table since they share the same concern, then organizing everything as a markdown response with a notation legend and open questions at the end. I'll keep the table simple without redundant columns since readers can sum the warehouse stock themselves. For the insufficient stock scenario, I'm setting Quantity Ordered to 6 with warehouse stock totaling 5, which means 2 shipments go out and 1 unit gets backordered. I'm also working through the boundary case where the order quantity exactly matches total available stock.
> 
> Now I'm laying out the final rows for Table 4: a single-warehouse scenario where one location has enough inventory, a two-warehouse combination that's more efficient than splitting across three locations, and a forced three-way split where no two-warehouse pairing would suffice.
> 
> I'm also including an exact-fit boundary case where the ordered quantity matches the total available stock across two warehouses, then a just-past-boundary case where we're short by one unit and need to backorder, and finally a tie scenario where multiple warehouse pairs have equal counts—leaving the selection open as a design choice.
> 
> Now I'm moving to Table 5 to cover tie-breaking and genuine conflicts. I want to simplify the conflict example down from five items if possible, so I'm testing whether four items—Body, Lens (companions), Cable, and Mouse—can still demonstrate the mathematical conflict where split-minimization creates a genuine incompatibility.
> 
> With a 2-warehouse split like W1{Body, Cable} and W2{Lens, Mouse}, I get split-min = 2. But if I force Body and Lens together in Chicago, then Cable and Mouse can't share a warehouse without violating the conflict constraint, so I'd need Denver for Cable and El Paso for Mouse, giving together-min = 3. The question is whether a 2-warehouse solution exists that avoids Chicago entirely—but if Body only appears at Chicago in this setup, that becomes impossible, which creates the genuine conflict I need to illustrate. So I've settled on a cleaner example with 4 items across 3 warehouses: Chicago holds Body and Lens together, Denver has Body and Cable, ElPaso has Lens and Mouse. For the order requesting all four items with Body-Lens as companions, splitting them into two shipments (Denver + ElPaso) keeps companions apart, while fulfilling together through Chicago plus the other two warehouses requires three shipments but keeps companions together. Now I'm moving on to explore another aspect of the problem. Now I'm setting up the table rows with specific scenarios—starting with the baseline case where companions are already co-located at the optimal warehouse, then moving into a tie-breaking scenario where choosing the warehouse that keeps companions together becomes the deciding factor. The fourth scenario presents a genuine conflict where keeping companions together requires an extra shipment compared to the optimal split strategy, and I'm flagging this as an open policy question about whether to prioritize co-location or minimize shipment count.
> 
> I'm planning to add a legend explaining the warehouse stock notation, then move into a table showing how hard splits take precedence over companion affinity. Before diving into the detailed tables, I should probably include a brief overview paragraph walking through the decision pipeline—hard grouping constraints first, then quantity-driven splits, warehouse allocation with minimization, and finally companion tie-breaking—to help orient readers through the complexity.

Here's a full spec-by-example breakdown of order-splitting behavior, organized as one table per independent rule, plus two tables that show how the rules interact.

**Pipeline assumption:** splitting happens in two phases. Phase 1 partitions order lines into shipment groups using three *hard* constraints (fulfillment method, ship-to address, availability) — these can never be merged back together. Phase 2 decides, within each group, which warehouse(s) fulfill it, minimizing shipment count and preferring to keep companion items co-located. Hard constraints always win over the companion preference (Table 6).

**Notation:** `[Warehouse: Qty, ...]` = single-SKU stock/allocation. `{Item: Qty, ...}` = per-warehouse stock for multi-SKU orders. Blank = absent/not applicable. `?` = open question, not yet decided.

---

### Table 1 — Splits an order by fulfillment method

| Scenario | Item A Method | Item B Method | Ship-To Address (both) | Availability (both) | Same Shipment Group? |
|---|---|---|---|---|---|
| Both items for pickup | Pickup | Pickup | Home | In Stock | yes |
| Both items for delivery | Delivery | Delivery | Home | In Stock | yes |
| One pickup, one delivery | Pickup | Delivery | Home | In Stock | no |

### Table 2 — Splits an order by ship-to address

| Scenario | Item A Address | Item B Address | Fulfillment Method (both) | Availability (both) | Same Shipment Group? |
|---|---|---|---|---|---|
| Both items to the same address | Home | Home | Delivery | In Stock | yes |
| Gift item and personal item to different addresses | Recipient's Address | Customer's Home | Delivery | In Stock | no |

### Table 3a — Splits an order by item availability (ready-now grouping)

| Scenario | Item A Availability | Item B Availability | Fulfillment Method (both) | Ship-To Address (both) | Same Shipment Group? |
|---|---|---|---|---|---|
| Both items in stock | In Stock | In Stock | Delivery | Home | yes |
| One in stock, one not yet available | In Stock | {Backorder, Pre-Order} | Delivery | Home | no |
| Both not yet available | {Backorder, Pre-Order} | {Backorder, Pre-Order} | Delivery | Home | yes |
| Backorder item and pre-order item with different ETAs | Backorder (ETA next week) | Pre-Order (ETA next month) | Delivery | Home | ? |

Row 4 is open: it's unclear whether two not-yet-available items should ship together once *both* are ready, or split further by ETA so the earlier-ready one doesn't wait on the later one.

### Table 3b — Computes quantity shipped now vs. held back for a single line

| Scenario | Quantity Ordered | Quantity Available Now | Quantity Shipped Now? | Quantity Held Back? |
|---|---|---|---|---|
| Fully available | 3 | 5 | 3 | (blank) |
| Fully unavailable | 3 | 0 | (blank) | 3 |
| Partially available | 6 | 4 | 4 | 2 |
| Exactly available (boundary) | 6 | 6 | 6 | (blank) |

*Assumption: a single line can itself split — the available portion ships now, the shortfall becomes a separate deferred shipment. If your system instead holds the whole line until fully available, tell me and I'll redo this table.*

### Table 4 — Selects warehouses to fulfill a line, minimizing shipment count

| Scenario | Quantity Ordered | Warehouse Stock | Warehouses Used? | Shipment Count? | Quantity Backordered? |
|---|---|---|---|---|---|
| Single warehouse covers it | 6 | [Chicago: 10] | [Chicago: 6] | 1 | (blank) |
| Two-warehouse combo beats a worse three-way split | 6 | [Chicago: 2, Philadelphia: 3, Washington: 3] | [Philadelphia: 3, Washington: 3] | 2 | (blank) |
| No two-warehouse combo suffices, three-way forced | 6 | [Chicago: 4, LA: 1, Houston: 1] | [Chicago: 4, LA: 1, Houston: 1] | 3 | (blank) |
| Network stock exactly matches order (boundary) | 5 | [Chicago: 4, LA: 1] | [Chicago: 4, LA: 1] | 2 | (blank) |
| Network stock falls one short (just past boundary) | 6 | [Chicago: 4, LA: 1] | [Chicago: 4, LA: 1] | 2 | 1 |
| Multiple equally-minimal combos exist (tie) | 6 | [Denver: 3, Atlanta: 3, Dallas: 3, ElPaso: 3] | ? | 2 | (blank) |

Last row is open: several warehouse pairs achieve the minimum of 2 shipments — need a tie-break rule (e.g., lowest shipping cost/distance to customer, warehouse inventory balancing, or fixed priority order).

### Table 5 — Keeps companion items together when choosing among warehouse allocations

| Scenario | Items & Qty | Companion Pair | Warehouse Stock | Minimum Shipment Count? | Chosen Warehouses Used? | Companions Shipped Together? |
|---|---|---|---|---|---|---|
| Companions already at the sole warehouse needed | Body: 1, Lens: 1 | Body–Lens | Chicago: {Body: 5, Lens: 5} | 1 | Chicago: {Body: 1, Lens: 1} | yes |
| Tie between combos; the together-option is chosen | Body: 1, Lens: 1, Cable: 1 | Body–Lens | Chicago: {Body: 1, Lens: 1}, LA: {Body: 1, Cable: 1}, Denver: {Lens: 1, Cable: 1} | 2 | Chicago: {Body: 1, Lens: 1}, LA: {Cable: 1} | yes |
| No warehouse has both companions — split is the only option | Body: 1, Lens: 1 | Body–Lens | Chicago: {Body: 1}, Denver: {Lens: 1} | 2 | Chicago: {Body: 1}, Denver: {Lens: 1} | no |
| Keeping companions together costs one extra shipment vs. the true minimum | Body: 1, Lens: 1, Cable: 1, Mouse: 1 | Body–Lens | Chicago: {Body: 1, Lens: 1}, Denver: {Body: 1, Cable: 1}, ElPaso: {Lens: 1, Mouse: 1} | 2 (split: Denver{Body,Cable} + ElPaso{Lens,Mouse}) | ? | ? |

Last row is the key open question the whole exercise turns on: is "ship companions together" a *soft tie-break* (only applied when it's free) or a *hard preference* worth paying for an extra shipment? The user's phrasing — "if possible" — reads as soft, but I'd confirm before building it, since it directly trades off cost against the "camera without a lens" experience problem.

### Table 6 — Hard group splits take precedence over companion affinity

| Scenario | Item A | Item B | Companions? | Same Shipment? |
|---|---|---|---|---|
| Companions, same method/address/availability, co-located in stock | Delivery, Home, In Stock | Delivery, Home, In Stock | yes | yes |
| Companions, but one is pickup and one is delivery | Pickup, Home, In Stock | Delivery, Home, In Stock | yes | no |
| Companions, but one is in stock and one is backordered | Delivery, Home, In Stock | Delivery, Home, Backorder | yes | no |
| Companions, but shipped to different addresses | Delivery, Home, In Stock | Delivery, Recipient's Address, In Stock | yes | no |

---

### Open questions to resolve before implementation

1. **Table 3a, row 4** — do two different not-yet-available items (different ETAs) ship together once both are ready, or split further by ETA?
2. **Table 4, tie row** — what's the tie-break when multiple warehouse combinations achieve the same minimal shipment count (cost, distance, inventory balancing, fixed priority)?
3. **Table 5, last row — the big one** — when keeping companions together costs strictly more shipments than the true minimum, does companion-togetherness win, or does minimization win? This needs a business decision, not an engineering guess.
4. **Table 3b** — does a held-back remainder, once it becomes available, automatically merge with any other still-pending shipment for that customer, or always ship alone?

### Assumptions I made to keep this concrete

- A single order line can itself split by quantity (part ships now, remainder is backordered) — Table 3b.
- Backorder and pre-order are treated identically for grouping purposes (both "not ready now") — flagged as open in Table 3a where it matters.
- Companion affinity is evaluated only *after* hard splits and *within* warehouse selection — never overrides Tables 1–3.

Once questions 2 and 3 are answered, these tables map directly onto `@TableTest` cases if you want to hand them to `/tabletest`.

