# Narration — 17

The agent's visible narration, thinking cost and file writes, in order, distilled from
`conversation.jsonl`. Thinking text appears when the run asked the CLI for a summary
(`thinking_display: "summarized"`); the per-block token counts appear either way.

_[~546 thinking tokens (9 deltas)]_

> (thinking) I'm recognizing this as a good fit for table-driven test scenarios — the shopping cart feature has multiple conditions and edge cases that would benefit from concrete examples laid out systematically before implementation. The skill description is clear that spec-by-example is for scenario brainstorming and clarifying behavior through examples when requirements are vague, which is exactly what the user is asking for here. I'll use the tabletest:spec-by-example skill to work through the test scenarios.

_[~5,379 thinking tokens (137 deltas)]_

> (thinking) I'm breaking down the shopping cart feature into separate decision tables, each focused on a single rule and dimension. Since I can't interact with a domain expert here, I'll make reasonable assumptions about the requirements and build out the tables directly—starting with identifying the key concerns like adding items with quantities, removing items, and applying discounts.
> 
> For the coupon system, I'm noting that there are three discount types (percentage, fixed amount, product-specific), only one can be active at a time, and invalid or expired codes should trigger error messages. I'm also considering the cart total calculation as potentially separate from coupon selection, depending on how the rules interact.
> 
> Now I'm mapping out the first few decision tables: one for adding items to the cart (checking if the product exists in the catalogue, validating the quantity, and handling the cart state), another for removing items, and a third for applying coupon codes.
> 
> For the coupon application specifically, I'm thinking of it as a state transition rule — the table captures the "before" coupon state, the new code being applied, and the "after" state plus any message. This handles the replacement behavior naturally.
> 
> Then I need separate tables for calculating the discount amount based on coupon type and cart contents, and another for the final cart total (subtracting the discount and clamping at zero). Stock verification at checkout gets its own table too.
> 
> Starting to design the first table now — adding an item to the cart. Product not found in catalogue -> Cart After unchanged, Message="Product not found". I'm deciding whether to use a single table with an "Added?" column to capture both successful additions and rejections, which keeps the accumulation behavior visible while documenting why items were rejected.
> 
> Now I'm moving to removal scenarios. The key cases are removing an item that exists in the cart versus attempting to remove something that isn't there, with the question of whether partial removal (reducing quantity rather than deleting entirely) needs its own test case or if it's already covered by the addition logic.
> 
> For the coupon validation table, I'm setting up the state transitions that occur when a user enters a code—checking whether it's valid, expired, or unknown, and what happens to any previously active coupon. I want to add a row showing that replacement works across different coupon types—like replacing a fixed discount with a percentage one—to demonstrate that the type doesn't matter for the replacement logic. Now I'm setting up the final table with columns for scenario, active coupon before, code entered, code validity, active coupon after, and the resulting message. an unknown code, and also when there's no active coupon and an expired code is submitted—both keep the cart unchanged with appropriate error messages. Now I'm moving on to test how discounts are calculated based on coupon type, setting up scenarios with percentage discounts, fixed amounts, and product-specific offers to verify the math works correctly across different cart contents. Unit-based product discounts apply per matching item in the cart, so a $3 discount on Widgets means $6 off for two units. If the coupon targets a product not in the cart, the discount is zero, though I should flag whether we need to surface a message to the user about inapplicable coupons. I'm also considering edge cases like percentage discounts at 0% or 100%.
> 
> Now I'm mapping out how the final cart total gets calculated—subtracting the discount from the subtotal, with a floor at zero to handle cases where the discount exceeds the subtotal amount.
> 
> I'm also setting up a stock verification table that checks whether the items in the cart are actually available before allowing checkout to proceed.
> 
> For the stock check itself, I need to handle cases where some items have insufficient inventory — the message should specify exactly how many units are available for each product that's short. I'm also considering whether to include an empty cart check in this same table or split it out, since that's really a precondition that applies before stock verification even matters. -empty) to establish that the non-empty case passes this particular validation. I'm working through whether each table needs its own baseline row or if that creates redundancy, since the concern about "One Obligation, One Row" suggests avoiding duplicate test scenarios across tables.
> 
> For Table 6a, I'm setting up two rows: one showing an empty cart gets rejected with the message "Your cart is empty," and another with items that proceeds to the next check. Then Table 6b will focus specifically on stock insufficiency, holding the cart as non-empty and varying the available stock levels to test that validation rule in isolation.
> 
> Now I'm thinking through the stock scenarios: when all items have sufficient inventory they're accepted with an empty shortages list, when one or more items fall short they're rejected with a list showing which products and how many are available, and I need boundary cases like when the requested quantity exactly matches available stock (should accept) versus one unit over (should reject). For multiple items short at once, the shortages list captures all of them together. I'm also considering how to structure the shortages data—either as product-quantity pairs or as formatted message strings—and what the exact wording should be for user-facing messages.
> 
> I'll present these test scenarios as markdown tables directly in the response since this is planning work, not code generation yet. I should also add a boundary case for quantity 1 (minimum valid) versus 0 (invalid) to the Add Item table, and I'm finalizing the column structure across all tables while noting any assumptions I'm making about the system's behavior. product not in the catalogue gets rejected with "Product not found", and I'm noticing that rows 1 and 5 might be redundant since they both test minimum valid quantities—row 1 with 2 units and row 5 with 1 unit. But actually, row 5 is the true boundary test showing exactly where acceptance begins, while row 3 tests the rejection boundary just below it with 0 and negative quantities, so that straddling pair effectively validates the quantity constraint.
> 
> I should consolidate by using row 5's qty=1 as the simplest success case instead of row 1's qty=2, keep row 2 to demonstrate accumulation with different quantities, and let the 0/-1 rejection cases contrast with the qty=1 acceptance. This removes the redundancy while preserving both the basic happy path and the boundary coverage.
> 
> So my final test table has four rows: adding a product to an empty cart with qty=1, adding more of an existing product to show accumulation, rejecting zero or negative quantities, and rejecting products not in the catalogue. Now I'm moving on to the Remove operation and setting up its test table.
> 
> For the remove scenarios, I have two cases: removing a product that's in the cart successfully, and rejecting removal of a product that isn't there. I'm noting an open question about whether remove operates on a single unit or the entire line item, since that affects the test design. I should also verify whether the product-discount table needs a "not in cart" row to demonstrate that coupons don't apply to products outside the cart.
> 
> Now I'm restructuring the discount table with "Coupon Type" as the family column to match the design pattern. The table will show percentage-based and fixed-amount discount scenarios with their respective calculations.
> 
> 3. Fixed amount exceeding the subtotal | Widget x2 @ $10.00 (subtotal $20.00) | Fixed | $50.00 off | $50.00 (raw, clamping happens at total calc)
> 4. Discount on a specific product in the cart | Widget x2 @ $10.00 + Gadget x1 @ $15.00 (subtotal $35.00) | Product | Widget: $3.00 off per unit | $6.00
> 5. Discount on a specific product not in the cart | Gadget x1 @ $15.00 (subtotal $15.00) | Product | Widget: $3.00 off per unit | $0.00 [open question]
> 
> Now moving on to the total calculation table.
> 
> Generating markdown table... The titles all follow a consistent pattern with action verbs, so the index is solid. Now I'll compile the full response with all seven tables, document my assumptions clearly, flag any open questions, and present it in markdown with a consolidated assumptions section at the end.

Here's the test scenario breakdown, organized as one table per rule (each table's axis is a single concern — this avoids the trap of one giant "cart behavior" table that mixes unrelated rules).

## 1. Adds an Item to the Cart

| Scenario | Cart Before | Product | Catalogue Price | Quantity Requested | Cart After? | Message? |
|---|---|---|---|---|---|---|
| Adds a new product to an empty cart | [] | Widget | $10.00 | 1 | [Widget x1 @ $10.00] | "Added 1x Widget to cart" |
| Adds more of a product already in the cart | [Widget x2 @ $10.00] | Widget | $10.00 | 3 | [Widget x5 @ $10.00] | "Added 3x Widget to cart" |
| Rejects a non-positive quantity | [] | Widget | $10.00 | {0, -1} | [] unchanged | "Quantity must be at least 1" |
| Rejects a product not in the catalogue | [] | Unicorn Statue | (not in catalogue) | 1 | [] unchanged | "Product not found" |

*Assumption: adding an already-present product increases its quantity rather than erroring or overwriting.*

## 2. Removes an Item from the Cart

| Scenario | Cart Before | Product | Cart After? | Message? |
|---|---|---|---|---|
| Removes a product present in the cart | [Widget x2, Gadget x1] | Widget | [Gadget x1] | "Removed Widget from cart" |
| Rejects removing a product not in the cart | [Gadget x1] | Widget | [Gadget x1] unchanged | "Widget is not in your cart" |

*Open question: does remove take a quantity (decrement) or always remove the whole line item? Table above assumes whole-line removal — flag if partial removal is needed, that'd be a third row/rule.*

## 3. Applies a Coupon Code to the Cart

State-transition table — coupon before, code entered, coupon after.

| Scenario | Active Coupon Before | Code Entered | Code Validity | Active Coupon After? | Message? |
|---|---|---|---|---|---|
| Applies the first coupon when none is active | (none) | SAVE10 (10% off cart) | valid | 10% off cart (SAVE10) | "Coupon applied: SAVE10 (10% off)" |
| Replaces an active coupon with a different type | $10 off cart (FIXED10) | SAVE10 (10% off cart) | valid | 10% off cart (SAVE10) | "Coupon applied: SAVE10 (10% off)" |
| Rejects an expired code, keeping the previous coupon | 10% off cart (SAVE10) | EXPIRED5 | expired | 10% off cart (SAVE10) unchanged | "Coupon has expired" |
| Rejects an unknown code, keeping the previous coupon | 10% off cart (SAVE10) | BOGUS | unknown | 10% off cart (SAVE10) unchanged | "Coupon code not found" |
| Rejects an expired code when none was active | (none) | EXPIRED5 | expired | (none) unchanged | "Coupon has expired" |

The second row exists specifically to prove replacement doesn't care about coupon *type* — a fixed coupon replaced by a percentage coupon.

## 4. Calculates the Discount from an Active Coupon

One table, "Coupon Type" as the family column — percentage/fixed/product-specific are members of one rule ("computes a discount"), not three separate tables.

| Scenario | Cart Contents (Subtotal) | Coupon Type | Coupon Detail | Discount? |
|---|---|---|---|---|
| Percentage off the whole cart | Widget x2 @ $10.00 ($20.00) | Percentage | 10% | $2.00 |
| Fixed amount off the whole cart | Widget x2 @ $10.00 ($20.00) | Fixed | $5.00 off | $5.00 |
| Fixed amount exceeding the subtotal | Widget x2 @ $10.00 ($20.00) | Fixed | $50.00 off | $50.00 |
| Discount on a specific product in the cart | Widget x2 @ $10.00 + Gadget x1 @ $15.00 ($35.00) | Product | Widget: $3.00 off/unit | $6.00 |
| Discount on a specific product not in the cart | Gadget x1 @ $15.00 ($15.00) | Product | Widget: $3.00 off/unit | $0.00 |

*Note: row 3's raw $50.00 discount on a $20.00 subtotal is intentional — clamping happens downstream (table 5), so this table just proves the discount calculation itself is correct before clamping.*
*Open question: should a product coupon that matches nothing in the cart also emit its own message (e.g. "Your coupon doesn't apply to any items in your cart"), or silently produce $0 discount?*

## 5. Calculates the Cart Total from Subtotal and Discount

Pure arithmetic, separated from the discount *decision* above — this table just proves the clamp.

| Scenario | Subtotal | Discount | Total? |
|---|---|---|---|
| No discount applied | $20.00 | $0.00 | $20.00 |
| Discount less than the subtotal | $20.00 | $5.00 | $15.00 |
| Discount equal to the subtotal | $20.00 | $20.00 | $0.00 |
| Discount exceeding the subtotal is clamped at zero | $20.00 | $50.00 | $0.00 |

Rows 3 and 4 are the boundary pair for the "never below zero" rule.

## 6. Rejects Checkout on an Empty Cart

| Scenario | Cart Contents | Checkout Result? | Message? |
|---|---|---|---|
| Empty cart is rejected | [] | rejected | "Your cart is empty" |
| Non-empty cart proceeds past this check | [Widget x2] | proceeds to stock check | (none) |

## 7. Rejects Checkout When Stock Is Insufficient

Kept separate from table 6 — "cart not empty" and "stock sufficient" are independent conditions (an "and"), so each gets its own table, holding the other satisfied.

| Scenario | Cart Contents (Qty Requested) | Available Stock | Checkout Result? | Shortages? |
|---|---|---|---|---|
| All items sufficiently stocked | Widget x2 | Widget: 5 | accepted | [] |
| Requested quantity exactly matches stock | Widget x3 | Widget: 3 | accepted | [] |
| Requested quantity exceeds stock by one | Widget x4 | Widget: 3 | rejected | [Widget: only 3 available] |
| Multiple items simultaneously short | Widget x4, Gadget x2 | Widget: 3, Gadget: 1 | rejected | [Widget: only 3 available, Gadget: only 1 available] |

`Shortages?` is a real list, not a flattened string — that's what lets the frontend render "Only 3 available" per line item rather than parsing a sentence.

---

### Assumptions made (flag any that are wrong)
- Re-adding a product already in the cart increments its quantity rather than replacing it or erroring.
- Remove takes no quantity — it removes the whole line item. If partial decrement is a real requirement, that needs its own table.
- Product-specific coupons discount a fixed amount *per unit* of the matching product (not a flat one-time amount) — worth confirming, since the spec just says "a discount on a specific product."
- A coupon replacement is silent (no "previous coupon removed" message) — only the new coupon's applied/error message is shown.
- Checkout success has its own message too (not specified in your examples) — worth deciding wording now so it's covered in tests.

Want me to turn these into executable tests? Let me know the language/framework (JVM → `@TableTest`, otherwise pytest/Jest/Go table tests etc.) and I'll convert directly.

