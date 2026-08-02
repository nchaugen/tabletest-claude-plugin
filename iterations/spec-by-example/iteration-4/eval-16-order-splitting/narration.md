# Narration — 16

The agent's visible narration, thinking cost and file writes, in order, distilled from
`conversation.jsonl`. Thinking text appears when the run asked the CLI for a summary
(`thinking_display: "summarized"`); the per-block token counts appear either way.

_[~177 thinking tokens (6 deltas)]_

> (thinking) I'm recognizing this as a scenario where I should map out concrete test cases to clarify the behavior of order splitting across different conditions like fulfillment type, inventory distribution, and delivery addresses. This is a good opportunity to use example-based specification to work through the combinations before implementation.

This is exactly the kind of multi-condition behavior (fulfillment type, availability, warehouse inventory splits, delivery addresses, companion-product bundling, shipment minimization) that benefits from spec-by-example before locking down test cases. Let me use that skill to work through it.

_[~18,358 thinking tokens (481 deltas)]_

> (thinking) I'm mapping out the core data structures needed here—fulfillment methods like pickup versus delivery, inventory states for in-stock and pre-order items, handling multiple addresses, optimizing warehouse distribution to minimize shipments, and managing companion product bundling.
> 
> The key insight is that this is a constraint-satisfaction problem with a hierarchy: hard constraints that force items into separate shipments (fulfillment type, delivery address, availability/ship date) come first, then optimization layers like warehouse minimization and companion bundling preferences stack on top. bination allows it, even if that's not the only valid split" — so companion bundling might take priority over strict minimum shipment count in some cases. Let me re-read the requirement: "If possible those should ship together from the same location." That's a soft constraint. I'll interpret this as: companion-together is preferred when it doesn't increase total shipments beyond the minimum, but if keeping companions together requires more shipments, we minimize shipment count first since that was stated as the primary goal. So the hierarchy is: minimize shipment count first, then use companion bundling as a tie-breaker among equally-minimal solutions, and prefer warehouse combinations that co-locate companions even if they're just one of several minimal options.
> 
> Now I'm thinking through what happens when companions need to be split across hard boundaries like fulfillment type, address, or availability status — for instance, a camera body in stock but the lens on backorder. Hard partition constraints always win, so companions can only ship together if they fall in the same partition bucket. If they don't, they get split because violating a hard constraint isn't an option.
> 
> I'm laying out a table-based approach to handle this systematically, starting with one table that shows how fulfillment type (pickup vs delivery) drives shipment splits while keeping other factors constant, then another table examining how availability status and expected ship dates create separate shipment groups. The key question I'm wrestling with is whether two backorder items with different expected dates should merge into one shipment or split further by date.
> 
> Based on the prompt's language about items shipping when they become available, I'm going to assume that expected ship date becomes the grouping key—items ready immediately go together, and backorders with different expected dates split into separate shipments. Then I'll add a table for delivery address splits, and finally tackle the most complex one: how to minimize warehouse shipments when fulfilling a single line item's quantity across multiple warehouse locations.
> 
> For that last table, I need concrete scenarios: one warehouse having sufficient stock for a single shipment, multiple warehouses combining to meet demand requiring two shipments, and a tie-breaking case where Philadelphia and Washington each have three units—showing that two equally-minimal warehouse combinations exist and establishing a rule for which one to prefer.
> 
> When multiple warehouse combinations could fulfill the order with the same minimal number of shipments, I'll break ties by lowest total shipping distance or cost to the customer, then fall back to a deterministic warehouse priority order if costs are equal. I should note this as an assumption since actual shipping costs aren't provided—I'll include a "Total Shipping Cost" column to demonstrate the tie-break logic. I also need to handle the case where total inventory across all warehouses is insufficient to fulfill the order at all.
> 
> Now I'm building out a table for how companion items get bundled into the same shipment, looking at scenarios where companions are available together at a single warehouse versus split across multiple locations. I'm realizing I can collapse all these conflicting constraint scenarios into a single dimension: what constraint prevents companions from shipping together? That gives me a cleaner table structure where each row represents a companion pair and a column captures which competing rule (if any) forces them apart—whether it's availability, fulfillment type, delivery address, or warehouse cost optimization.
> 
> For the combining table, I need to justify whether it adds real value beyond the individual dimension tables. It only makes sense if it demonstrates the actual precedence and interaction of rules—showing how hard constraints like fulfillment type and address create initial buckets, then warehouse minimization and companion preference resolve within those buckets. A concrete example with multiple line items across different dimensions would prove whether this interaction is visible elsewhere or truly needs its own table.
> 
> I'm also reconsidering whether fulfillment type, availability, and address should be grouped as a single "hard partition constraints" family rather than three separate tables, since they all function as independent splitting conditions that don't overlap. I'm realizing these three splitting dimensions—fulfillment type, availability, and address—actually share a common structure: they're all reasons two items can't travel together in the same shipment. This fits the family pattern better than I initially thought, similar to how dose adjustments (renal, weight, drug interactions) are all variations of the same rule applied to different factors. So a single "Splits Shipments by Hard Partition Dimension" family table with rows for each splitting reason might work after all, capturing the structural similarity while keeping the logic unified.
> 
> But I'm second-guessing myself because the actual values differ significantly—fulfillment types are enums, addresses are strings, availability is dates—and each represents a distinct business rule that domain experts would review separately. It's probably cleaner to keep three focused tables since they're genuinely different value types and each addresses an independent business concern.
> 
> Now I'm moving forward with that structure: three separate dimension tables plus the warehouse, companion, and combining tables for six total. Let me start mapping out concrete rows for the fulfillment type table, holding the warehouse constant with full stock and immediate availability.
> 
> I'll set up scenarios where delivery items create one shipment, pickup items create their own grouping (treating pickup locations as distinct from delivery addresses), and mixed fulfillment types split into separate shipments. The key distinction here is fulfillment type itself — I don't need to layer in additional variations like item category since that's not what drives the splitting rule.
> 
> Now I'm moving to the second table, which will examine how shipments split based on availability and expected ship dates while keeping fulfillment type constant. In-stock items combined with backorders on different dates create separate shipments for each date, while backorders sharing the same expected ship date consolidate into a single shipment. I'm working through concrete examples: all in-stock ships immediately, mixed inventory splits into now and a future date, and multiple backorder dates each get their own shipment unless they align.
> 
> Now I'm setting up a second table to show how delivery addresses further fragment shipments — same address keeps items together, but different delivery locations split them apart, even when everything else is identical. I'm noting that address matching logic—whether to use exact string comparison or normalized/geocoded matching—is an open question I should flag rather than assume. Now moving to the warehouse fulfillment table, which needs scenarios showing how a single line item's quantity gets distributed across available warehouses, with columns for the item ordered, warehouse stock levels, and resulting shipment groupings.
> 
> The key boundary cases are: when one warehouse has enough stock to cover the entire order, we ship from just that warehouse even if others have inventory (avoiding unnecessary splits), and when no single warehouse suffices but two or more combine to meet the demand exactly, we need multiple shipments from those warehouses—though I'm realizing the example given (Chicago 4 + LA 1 + Houston 1 for 6 shirts) actually uses three warehouses, which might not be the minimal case. That confirms the tie-break rule from the example. When there's no single-warehouse option, a forced 2-warehouse split is the only path forward—like needing Chicago's 4 and Denver's 2 to fulfill an order of 6. But when multiple 2-warehouse pairs can each satisfy the order exactly, I need a tiebreaker, probably based on distance or cost to the delivery location.
> 
> I'll add a "Warehouse Priority" column as a simple deterministic ranking for each warehouse relative to the order, representing proximity or cost preference. This avoids inventing a full distance model while still giving the algorithm a clear way to pick between equally-minimal combinations.
> 
> For partial fulfillment when total stock falls short of the order quantity, I'm treating the shortfall as a backorder — the fulfillable portion ships now from available warehouses, and the remainder is flagged as pending with no ETA. I'll note this as an assumption since the user didn't specify exact backorder handling. The output format will show shipments as a set of warehouse-to-quantity pairs, like {Chicago: 6 shirts, Philadelphia: 3 shirts}.
> 
> Now I'm building out the test cases for Table 4: a single warehouse covering the full order, multiple warehouses needed to reach the total, choosing the minimal combination when several options exist, and breaking ties between equally-minimal combinations using warehouse priority order. The tie-breaker picks Atlanta and Miami as the two lowest-priority warehouses, so the shipment is {Atlanta: 3, Miami: 3}. For row E, the order is 6 units but only Chicago has stock with 4 units available, so Chicago ships 4 and the remaining 2 units go into a backorder shipment group.
> 
> I'm thinking about whether to add a "Minimum Warehouses Needed" traceability column to show the intermediate step of minimization before the final shipment groups — that would help domain experts verify the logic is being applied correctly. The minimization rule itself isn't a numeric threshold, so I don't think I need to expose that separately. Now I'm moving on to how bundles handle companion line items.
> 
> For the companion bundling table, I should use a "Constraint Blocking Bundling" column that captures the specific reason items can't ship together: fulfillment type mismatch, address mismatch, availability mismatch, no common warehouse, or the extra-shipment tradeoff. I'm sketching out scenarios like when both items are in stock at the same warehouse so they ship together, versus when they could each fulfill faster from different locations. When availability or fulfillment constraints differ between companions—like one item in stock and another backordered, or different delivery methods requested—those hard constraints override the companion preference, forcing separate shipments. Similarly, if companions are destined for different addresses, they must ship separately. In edge cases where bundling companions together would actually increase total shipments beyond the minimum, I prioritize minimizing shipment count overall, so the companions split despite their pairing. 's just a split shipment). So the tradeoff only materializes when a common warehouse actually exists and holding both there would reduce total shipments compared to the split approach. separate shipments from Denver still gives 2 total (body + lens both from Denver), so bundling doesn't increase shipment count here—it's a wash. I need a scenario where bundling genuinely forces an extra shipment: maybe one companion has stock spread across multiple warehouses while the other is concentrated, so bundling them together requires pulling from more locations than if shipped separately. even if that warehouse choice would otherwise be less preferred. Let me test a genuine tradeoff scenario: if the body needs 6 units split across Philadelphia (3) and Washington (3), that's a minimum of 2 shipments no matter what. For the lens with 1 unit, if it's available in both Philadelphia and Washington, bundling happens automatically without extra cost. But if the body can be sourced from a single warehouse like Chicago with 6 units (1 shipment), and the lens is only in Denver, then bundling the companion requires either accepting that extra shipment from Denver or splitting them — that's the real tradeoff I'm looking for. to achieve the same minimum shipment count anyway. So bundling doesn't actually reduce shipments here — it's just a constraint that forces us to pick a warehouse that works for both items, but since they're in different warehouses, we end up shipping from both regardless. count (merges 2 potential shipments into 1) — it can never increase shipment count for the companion pair itself, since worst case is same count as splitting them (2 vs 2) or better (1 vs 2). The conflict could arise for a different reason: what if bundling companions at a common warehouse requires MORE shipments for the REST of the order? E.g., other non-companion items' warehouse selection needs to change to accommodate co-locating companions, causing extra shipments elsewhere.
> 
> For instance, if an order has Body and Lens as companions plus six unrelated Shirts, and Chicago has the Body and all the Shirts but no Lens, while Denver has the Body but no Shirts — bundling the companions might force me to split the Shirts across warehouses, creating extra shipments that wouldn't exist if I'd sourced the companions separately.
> 
> But wait, I'm realizing this might not actually be a tradeoff. If I source the Body from Denver instead of Chicago to co-locate it with the Lens, I can still ship the Shirts from Chicago as a separate shipment. That's still two shipments total, same as before, except now the companions are bundled together — a win with no cost.
> 
> The real question is whether forcing companion bundling ever *increases* the shipment count. Let me think through a scenario where that might happen: if an order has Body and Lens as companions plus a large Shirts quantity, and Chicago can handle both Body and Shirts in one shipment while Denver only has Lens, then bundling companions at Denver would force Body away from Chicago, splitting what could've been a single shipment into two separate ones. That's where the tradeoff actually emerges. Chicago has all the Shirts, so I'd need to split the order across three shipments to force companions together: Denver sends Lens, Chicago sends Body in one shipment, then Chicago sends Shirts separately. That's 3 shipments with the bundling constraint versus 2 without it—finally a genuine increase from the companion preference.
> 
> But wait, if a warehouse holds both companions but in insufficient quantity for one of them, that's where a real tradeoff emerges.  Let me work through a concrete example: Body (qty 4, companion) and Lens (qty 1, companion). Body is stocked at Chicago (4 units) and Denver (4 units), but Lens only exists at Denver (1 unit). Without the companion constraint, I'd pick one shipment for Body and one for Lens, but the constraint forces me to consider whether bundling them from Denver works or if I need to split.
> 
> Now I'm testing a scenario where bundling actually hurts: Body qty 6 and Lens qty 1, both companions. Denver has Body 2 and Lens 1 (partial Body stock), while Chicago has the full Body qty 6 but no Lens. If I ignore the companion preference, I ship Body from Chicago (1 shipment) and Lens from Denver (1 shipment) for 2 total. But forcing them together from Denver only covers part of the Body order, so I'd still need Chicago for the remaining Body units—that's actually 3 shipments, worse than splitting.
> 
> Wait, let me reconsider: Denver{Body:2, Lens:1} plus Chicago{Body:4} is still just 2 shipments, not 3. The companions ride together on the Denver shipment but the Body gets split anyway. So bundling doesn't increase the count here either.
> 
> The core insight is that bundling can never force more shipments than the split-optimal approach because in the worst case, I just don't bundle and fall back to splitting—the shipment count stays the same or improves.
> 
> But there's a scenario where forcing 100% bundling (insisting all units of both items come from the same warehouse) could genuinely increase shipments: if the only warehouse with the companion item doesn't have enough of the primary item, I'd need to source the primary item from multiple warehouses to meet the bundling constraint, fragmenting it further.
> 
> For example, if Body needs 6 units and Lens needs 1, and Lens only exists at Philadelphia with 3 Body available there, while Chicago has all 6 Body—splitting optimally gives 2 shipments (Chicago for Body, Philadelphia for Lens). But forcing them bundled at Philadelphia means I can only get 3 Body there, so I'd need a second warehouse for the remaining 3 Body units, creating 3 total shipments instead of 2.
> 
> However, I'm realizing this might not actually increase the count in many cases: if Body's minimum shipment count was already 2 warehouses (like Philadelphia + Washington), then bundling just redirects some of Body's quantity to the warehouse that has Lens, keeping the total shipment count the same. The key insight is that bundling can only increase shipments if it forces Body to use MORE warehouses than it would independently. icago and Denver each handle their respective items since Denver lacks body stock and Chicago lacks lens stock—this confirms the forced two-shipment minimum, which is the same constraint pattern I identified earlier where bundling becomes impossible without a shared warehouse. keeps shipments at the minimum necessary. So the real insight is that companion bundling never forces additional shipments—it only consolidates when it's possible to do so.
> 
> The actual tension emerges when multiple equally-minimal warehouse combinations exist for an order, where some bundle companions and others don't, and I need to prefer the bundling option. Finalizing the companion bundling scenarios: bundling applies when a common warehouse holds full stock of both items, or as a tie-breaker when multiple single-warehouse options are equally minimal; otherwise companions ship separately if no shared source exists or if they differ in availability status, fulfillment type, or delivery address. This gives me a clean, constraint-driven table that avoids artificial cost tradeoffs.
> 
> Now I'm moving to Table 6, which captures the precedence rules across all dimensions—specifically, how hard constraints like fulfillment type, availability, and address differences always create separate buckets first, while warehouse minimization and companion bundling only operate within those buckets, never across them. I need to demonstrate this interaction with concrete examples.
> 
> I'm constructing two rows: the first shows a complex multi-item order with mixed fulfillment types, addresses, and stock statuses, then traces how the hard constraints partition it into separate shipments before any bundling logic applies. The second row presents a scenario where companions would naturally bundle together, but they fall into different hard-partition buckets—one in stock, one backordered—to prove that hard constraints override companion preference even in intricate multi-dimensional cases. So the shirts split across two warehouses means two separate shipments, while the camera body and lens bundle together in Denver since that's their common warehouse. Then I have the store pickup as its own shipment, and the pre-order item arriving in 2026 as another. That gives me five total shipments. But I'm realizing the body and lens shouldn't necessarily be separate from the shirts just because they're different SKUs—I need to think through whether they actually share any warehouses or if the warehouse locations force them apart. Now I'm working through Row 2, which demonstrates how hard constraints override companion preferences. Item F (camera body, in stock) and Item G (lens, backordered until 2026) are companions, but their different availability statuses force them into separate shipments despite the pairing preference. Item H (shirt, in stock at Chicago) gets bundled with the body since they share warehouse availability, while the lens ships separately once it arrives. Item G (Lens) has an ETA of 2026-09-10 and is a companion of F, so we'd expect them to ship together. But since the Body is in stock now while the Lens is backordered, this scenario demonstrates the core question: does the companion relationship force both items to wait, or does availability take precedence and let the Body ship early? Based on the stated goal of not making customers wait unnecessarily, the Body ships now from Chicago while the Lens ships later on its ETA date — the companion grouping breaks because in-stock availability wins out.
> 
> Now I'm reconsidering whether I need a complex Row 1 with five items. The guidance warns against "wiring" — showing how multiple rules combine just for the sake of it. A simpler row that demonstrates the full precedence chain (hard constraints → warehouse minimums → companion bundling) with a clear shipment key concept (fulfillment, address/store, availability, warehouse) would be more focused and actually show what matters.
> 
> The real insight is that the shipment key itself is a rule the individual tables don't capture — each single-rule table holds the others constant, so none reveals how these dimensions combine to define the key. That's worth one row. Row 2 resolves the open question about companion versus availability precedence. Both rows earn their place by showing interaction that no single table demonstrates, so I'll keep them both but trim Row 1 to be leaner.
> 
> For the "Shipments?" column, I'll use a compact notation showing shipment groups as sets with location and item quantities. I also need to flag the open assumptions: the tie-break rule for equally-minimal warehouse combinations assumes a deterministic priority ranking (lower preferred), but real systems might optimize for shipping cost, carbon footprint, or delivery speed instead. Companion bundling only acts as a tie-break among equally-minimal solutions and falls back to split when necessary.
> 
> I'm also noting that the business rule text "if possible" supports a soft interpretation where bundling is preferred but doesn't explicitly forbid paying for an extra shipment—I'll flag this as an open question for stakeholder confirmation. Other assumptions to call out: address matching uses exact-match on normalized addresses (fuzzy/geocoded matching is open), partial stock shortfalls are assumed to ship the fulfillable portion now with the rest as a backorder (not explicitly specified), and companion pairs split by availability ship the in-stock item now rather than waiting for both.
> 
> For pickup fulfillment, I'm treating it as its own shipment group tied to a store location rather than warehouse-selected, since pickup implies a customer-chosen store—flagging this as an assumption. I want to verify the companion table's tie-break example numbers are consistent and double-check Table 4's rows are clean, including Row E on shortfall handling, which is a good edge case to keep as a test scenario. Now I'm ready to write up the final deliverable as markdown tables.
> 
> I'm drafting action-oriented scenario titles that follow a consistent grammatical shape, using condition-based naming where relevant and marking outputs with "?" suffixes where appropriate. The titles I'm working with are "Splits Shipments by Fulfillment Type," "Splits Shipments by Availability / Ship-Readiness," "Splits Shipments by Delivery Address," "Minimises the Number of Warehouses Used to Fulfil a Line Item," "Bundles Companion Line Items Into the Same Shipment When Feasible," and "Resolves Precedence When Multiple Splitting Rules Apply to One Order"—checking that they maintain one consistent grammatical shape across the set.
> 
> I'm noticing that the first three titles all open with "Splits Shipments by X," which creates a repetitive pattern. While the distinguishing word (Fulfillment Type, Availability, Delivery Address) does vary, the guidance suggests that three titles sharing the same uninformative opener can make the list unscannable. I'm comparing this against the guidance examples to see whether the repetition here is actually problematic or whether the varying X values make it sufficiently scannable. Generating example tables... Now I'm building a second test table that holds fulfillment method constant at delivery with the same address, focusing instead on inventory status and expected ship dates. I'm covering scenarios where all items are in stock, mixed in-stock and pre-order situations, and multiple backorders with staggered arrival dates to test how shipments group by expected delivery date.
> 
> Then I'm moving to a third table that keeps fulfillment and inventory constant but varies the delivery addresses, starting with items going to the same location and then introducing a gift shipment to a different address.
> 
> Now I'm setting up the main table that will test warehouse selection logic, with columns for the scenario, ordered items and quantities, warehouse stock levels, warehouse priority for tie-breaking, and the resulting shipments. The first scenario is straightforward—a single warehouse has enough inventory to fulfill the entire order.
> 
> For this row, Chicago has 10 units in stock while LA has only 5, so Chicago alone satisfies the order of 6 shirts. Since there's no tie between warehouses, the priority column doesn't affect the outcome, so I'll mark it as "n/a (no tie)" to indicate the priority exists but is irrelevant here. Now I'm laying out the concrete examples: one where a single warehouse covers the full order, another where two warehouses are needed because no single source has enough stock, and a third showing how a two-warehouse solution beats a three-warehouse alternative even though both technically work.
> 
> Then there's a case where multiple two-warehouse combinations exist with identical stock totals, so the algorithm picks the pair with the lowest combined priority ranking. Finally, I'm setting up a scenario where the total available inventory across all warehouses doesn't meet the order quantity.
> 
> I'm also thinking about adding a "Min Warehouses Needed?" column for traceability and a final "Shipments?" column to show the results. Rather than using abstract priority numbers, I should make this more concrete by using actual distance values—like "Distance to Delivery Address (mi)"—which better represents the real-world tie-breaking logic. Let me restructure the examples with this distance metric instead.
> 
> For Row A, Chicago is 50 miles away with 10 units available, while LA is 1800 miles away with 5 units. Row B has Chicago at 50 miles with 4 units and Denver at 900 miles with 2 units. Row C spreads across multiple warehouses: Chicago (50 miles, 4 units), LA (1800 miles, 1 unit), Houston (1000 miles, 1 unit), Philadelphia (700 miles, 3 units), and Washington (750 miles, 3 units). For Row D, I'm creating a genuine tie where multiple warehouses have exactly 3 units each, so I need to break it by total distance—Atlanta and Denver combined would be 1200 miles versus Atlanta and Miami at 1500 miles, making the Atlanta-Denver pair the better choice.
> 
> Row E only has Chicago available at 50 miles with 4 units. Using distance as the tie-breaker is more concrete and realistic than abstract priority numbers, and it directly answers the question with a clear business rule: when warehouses have equal inventory, choose the pair with the lowest total shipping distance to the delivery address.
> 
> Now I'm setting up the final row values for the warehouses in Row D: Atlanta, Denver, Miami, and Chicago each have exactly 3 shirts, and the order needs 6 total. Since any pair of warehouses sums to 6 shirts, I need the minimum of 2 warehouses. Comparing all possible pairs by distance, Atlanta plus Denver at 1200 miles total is the winner, beating all other combinations. So the shipment goes to Atlanta and Denver for 3 shirts each.
> 
> Now I'm moving on to Table 5 to cover companion bundling scenarios.
> 
> I'm deciding on the table structure—there are many constraints that affect bundling (fulfillment type, delivery address, availability, warehouse stock), but listing them all as separate columns would be unwieldy. Instead, I'll keep the line items compact within a single column, showing only the details that vary per scenario, and use separate columns just for the blocking constraint and shipment outcome.
> 
> Now I'm working through concrete examples to illustrate how bundling decisions play out. The first scenario shows both items fully stocked at the same warehouse, so they bundle together cleanly. The second scenario introduces a complication where the body item could come from either of two equally-stocked warehouses, but the lens is only available at one location, which breaks the tie and forces a specific warehouse choice.
> 
> The third scenario presents the opposite problem—no single warehouse has both items, so they must ship from separate locations. The fourth scenario adds another wrinkle where one item is in stock while its companion is backordered, creating an availability mismatch that blocks bundling.
> 
> The fifth and sixth scenarios introduce fulfillment type and address mismatches respectively—one item needs store pickup while the other requires delivery, or they're destined for different addresses. That covers the main blocking patterns.
> 
> Now I'm moving to Table 6, which combines multiple independent splitting rules within a single order. The first row involves a complex scenario where the shirt has multiple warehouse options across different cities, the body has a tie-breaker situation, the lens is a companion of the body, the mug requires store pickup, and the candle is on backorder—testing how all these constraints interact when determining shipments.
> 
> The second row tests a critical precedence rule: when a companion pair has conflicting availability (one in stock, one backordered), the hard constraint of availability takes priority over the preference to bundle companions together, so they ship separately on different dates.
> 
> For Table 6, I'm checking whether Row 1 risks being redundant "wiring" that just re-applies existing rules. But it's actually demonstrating something new—how the shipment bucketing logic (fulfillment × address × availability, then warehouse minimization and companion bundling within each bucket) creates an emergent interaction: companions can coexist in the same bucket alongside unrelated items like shirts, yet still end up in different shipments due to warehouse routing decisions. That's a legitimate rule about bucket composition and warehouse selection that no single-rule table captures on its own.
> 
> Now I'm laying out the assumptions upfront before the tables and open questions after, then mentally checking against the design checklist: one rule per table holds, outputs are consistently shipments, and the partial fulfillment in Table 4 Row E is just an outcome value rather than a rejection case needing special handling. For scenario naming, I need to verify they're condition-based rather than outcome-based. I'm refining the scenario descriptions to focus purely on conditions rather than outcomes, so the table results speak for themselves. I'll tighten up the wording for rows A through E and then move on to fixing the scenario names in the other tables. Renaming Table6 Row2 to focus on the condition rather than the outcome: "Companion items differ in availability status" lets the result speak for itself. For Table6 Row1, I'm shifting from a topic-like description to a condition-based one that captures the setup: "An order mixes multiple warehouses, a companion pair, a pickup item, and a backordered item." Now I'm ready to compile the full deliverable with organized markdown tables, assumptions, and open questions sections.

Here's the mapped-out set of example tables, organized as one table per independent splitting rule, plus a combining table for precedence. I made a few explicit assumptions where the rules were ambiguous — flagged inline and summarized at the end.

## Model

A shipment groups items by a **key**: `(Fulfillment Type, Delivery Address or Store, Availability Group)`. Items with different keys can never share a shipment. Within one key, the ordered quantity is sourced from one or more warehouses, and each distinct warehouse used produces its own shipment — minimized in count, with companion items bundled onto the same warehouse when that's achievable without exceeding the minimum.

---

### 1. Splits Shipments Between Store Pickup and Home Delivery

*Held constant: all items in stock, single address, single warehouse.*

| Scenario | Line Items (Item: Fulfillment Type) | Shipments? |
|---|---|---|
| All items requested for home delivery | Shirt: Delivery, Mug: Delivery, Hat: Delivery | {Delivery: Shirt, Mug, Hat} |
| All items requested for store pickup | Shirt: Pickup, Mug: Pickup | {Pickup: Shirt, Mug} |
| Order mixes pickup and delivery items | Shirt: Pickup, Mug: Pickup, Hat: Delivery | {Pickup: Shirt, Mug}, {Delivery: Hat} |

---

### 2. Delays Shipment Until Items Are Available to Ship

*Held constant: all items delivery, same address, single warehouse.*

| Scenario | Line Items (Item: Availability, Expected Ship Date) | Shipments? |
|---|---|---|
| All items in stock | Shirt: In Stock, Mug: In Stock | {Now: Shirt, Mug} |
| One item in stock, one on pre-order | Shirt: In Stock, Camera: Pre-order (ETA 2026-09-01) | {Now: Shirt}, {2026-09-01: Camera} |
| Two backordered items have different expected dates | Camera: Backorder (ETA 2026-09-01), Lens: Backorder (ETA 2026-09-15) | {2026-09-01: Camera}, {2026-09-15: Lens} |
| Two backordered items share the same expected date | Tripod: Backorder (ETA 2026-09-15), Lens: Backorder (ETA 2026-09-15) | {2026-09-15: Tripod, Lens} |

---

### 3. Splits Shipments Across Differing Delivery Addresses

*Held constant: all items delivery, all in stock, single warehouse.*

| Scenario | Line Items (Item: Delivery Address) | Shipments? |
|---|---|---|
| All items go to the same address | Shirt: 12 Main St, Mug: 12 Main St | {12 Main St: Shirt, Mug} |
| One item is a gift sent to a different address | Shirt: 12 Main St, Mug: 12 Main St, Watch: 5 Oak Ave | {12 Main St: Shirt, Mug}, {5 Oak Ave: Watch} |

*Open question: whether address matching is exact-string or normalized/geocoded (e.g., "12 Main St" vs "12 Main Street" vs a mistyped ZIP) — not specified.*

---

### 4. Minimises the Number of Warehouses Used to Fulfil an Order

*Held constant: fulfillment=Delivery, address=12 Main St, all items in stock. Assumption: ties between equally-minimal warehouse combinations are broken by lowest total shipping distance to the delivery address.*

| Scenario | Item & Qty Ordered | Warehouse Stock: Distance/Qty | Minimum Warehouses Needed? | Shipments? |
|---|---|---|---|---|
| A single warehouse holds enough stock for the whole order | Shirt ×6 | Chicago: 50mi/10, LA: 1800mi/5 | 1 | {Chicago: Shirt×6} |
| Order quantity requires drawing from more than one warehouse | Shirt ×6 | Chicago: 50mi/4, Denver: 900mi/2 | 2 | {Chicago: Shirt×4, Denver: Shirt×2} |
| A two-warehouse combination exists alongside a three-warehouse alternative | Shirt ×6 | Chicago: 50mi/4, LA: 1800mi/1, Houston: 1000mi/1, Philadelphia: 700mi/3, Washington: 750mi/3 | 2 | {Philadelphia: Shirt×3, Washington: Shirt×3} |
| Two warehouse combinations tie on warehouse count | Shirt ×6 | Atlanta: 300mi/3, Denver: 900mi/3, Miami: 1200mi/3, Chicago: 1500mi/3 | 2 | {Atlanta: Shirt×3, Denver: Shirt×3} |
| Combined stock across every warehouse is less than the order quantity | Shirt ×6 | Chicago: 50mi/4 (only source) | 1 (partial) | {Chicago: Shirt×4 (now)}, {Backorder (ETA unknown): Shirt×2} |

*Assumption: when total network stock can't cover the order, the fulfillable portion ships now and the shortfall becomes its own backorder shipment group with no ETA yet. This wasn't specified and should be confirmed — an alternative would be to reject the whole line item instead of partially shipping.*

---

### 5. Bundles Companion Line Items Into the Same Shipment When Feasible

*Held constant: quantities are 1 unit each unless noted; companions are Body + Lens.*

| Scenario | Companion Items (Fulfillment, Address, Availability, Warehouse Stock) | Constraint Blocking Bundling? | Shipments? |
|---|---|---|---|
| Both companions are fully stocked at one shared warehouse | Body: Delivery, 12 Main St, In Stock, Chicago(3); Lens: Delivery, 12 Main St, In Stock, Chicago(2) | (none) | {Chicago: Body, Lens} |
| One of two tied warehouse choices also stocks the companion item | Body: Delivery, 12 Main St, In Stock, Chicago(3) & Denver(3) [either alone sufficient]; Lens: Delivery, 12 Main St, In Stock, Denver(2) only | (none — tie broken toward Denver) | {Denver: Body, Lens} |
| No warehouse stocks both companions | Body: Delivery, 12 Main St, In Stock, Denver(3) only; Lens: Delivery, 12 Main St, In Stock, Chicago(2) only | no common warehouse | {Denver: Body}, {Chicago: Lens} |
| Companions differ in availability status | Body: Delivery, 12 Main St, In Stock, Chicago(3); Lens: Delivery, 12 Main St, Backorder (ETA 2026-09-10) | availability mismatch | {Chicago: Body (now)}, {2026-09-10: Lens} |
| Companions differ in fulfillment type | Body: Store Pickup, In Stock; Lens: Delivery, 12 Main St, In Stock | fulfillment type mismatch | {Pickup: Body}, {Delivery/12 Main St: Lens} |
| Companions are addressed to different recipients | Body: Delivery, 12 Main St, In Stock; Lens: Delivery, 5 Oak Ave (gift), In Stock | address mismatch | {12 Main St: Body}, {5 Oak Ave: Lens} |

*Assumption (resolves the "conflict with minimization" question): companion bundling only ever consolidates shipments or is infeasible — it never costs an extra shipment beyond the true minimum. It is applied only as a tie-breaker among equally-minimal warehouse solutions (row 2) and falls back to independent split-optimal sourcing when no common warehouse can cover both (row 3). If your intent was instead that companions should sometimes force one extra shipment to stay together, that's a different rule and should be confirmed.*

---

### 6. Resolves Precedence When Splitting Rules Apply Together

*This table exists only to show the interaction between rules — not to re-prove any single rule.*

| Scenario | Line Items | Shipments? |
|---|---|---|
| An order mixes a multi-warehouse item, a companion pair, a pickup item, and a backordered item | Shirt×6 (Delivery,12 Main St,In Stock; Philadelphia(3)+Washington(3)); Body (Delivery,12 Main St,In Stock,Denver(1)+Chicago(1), companion of Lens); Lens (Delivery,12 Main St,In Stock,Denver(1) only, companion of Body); Mug (Pickup, In Stock); Candle (Delivery,12 Main St,Backorder ETA 2026-09-01) | {Philadelphia: Shirt×3}, {Washington: Shirt×3}, {Denver: Body, Lens}, {Pickup: Mug}, {2026-09-01: Candle} — 5 shipments |
| Companion items differ in availability status | Body (Delivery,12 Main St,In Stock,Chicago(1), companion of Lens); Lens (Delivery,12 Main St,Backorder ETA 2026-09-10, companion of Body) | {Chicago: Body (now)}, {2026-09-10: Lens} — companions split |

Row 1 shows the shipment key composing correctly across dimensions (hard constraints partition first; within a partition, warehouse minimization and companion bundling apply, and can still yield multiple shipments per partition). Row 2 answers the open question about partial companion availability: **the in-stock item ships immediately rather than waiting for its companion**, since the stated business goal is "don't make the customer wait for everything." Hard partition constraints (fulfillment/address/availability) always outrank companion bundling.

---

## Assumptions requiring sign-off

1. **Tie-break rule** for equally-minimal warehouse combinations: lowest total shipping distance to the delivery address. (Could instead be cost, carrier SLA, or a fixed warehouse priority list.)
2. **Companion bundling never costs an extra shipment** — it's a free consolidation/tie-break, not a hard requirement that can increase shipment count.
3. **Partial companion availability**: the available companion ships now; the pair is not held back to wait for the other.
4. **Stock shortfall across the whole network**: fulfillable quantity ships now, shortfall becomes an open-ended backorder group. (Alternative: reject the item instead.)
5. **Address matching** is exact/normalized string match — fuzzy/geocoded matching is unconfirmed.
6. **Store pickup** items are fulfilled from a single customer-chosen store, not warehouse-selected/minimized like delivery items.

