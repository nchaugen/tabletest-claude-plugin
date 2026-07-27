# Analysis to-do — tabletest variant=c3-names, iteration 1

Compared against **official (iterations 42, 41, 40 merged)**, grading claude-sonnet-5/default.

**5 of 5 evals comparable.**

**9 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `rule-falsifiable-by-a-row` — eval-22-event-registration-tt

Grader said: _appliesHigherDiscountWhenBothApply has only one row: 'Early-bird and group discount both apply | before cutoff | 5 | 20.00 | 80.00' - a fixed-value implementation would satisfy this single-row table, and it is not framed as an invariance claim over a value set_

- Output: `eval-22-event-registration-tt/outputs/`
- Narration: `eval-22-event-registration-tt/narration.md`
- Raw transcript: `eval-22-event-registration-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `scenario-names-describe-conditions` — eval-22-event-registration-tt

Grader said: _'Early-bird and group discount both apply' names the rule, not the 20.00/80.00 outcome_

- Output: `eval-22-event-registration-tt/outputs/`
- Narration: `eval-22-event-registration-tt/narration.md`
- Raw transcript: `eval-22-event-registration-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `business-language-columns` — eval-29-shopping-cart-tt

Grader said: _Headers like 'Cart Before', 'Cart After?', 'Success?', 'Message?', 'Coupon Store', 'Total?' are business language; 'Product Id' is a minor nit only_

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `all-outputs-same-table` — eval-30-order-splitting-tt

Grader said: _Warehouse allocation split into 'minimisesShipmentCountAcrossWarehouseChoices' (Shipment Count?) and a separate 'determinesWarehousesUsedWhenCombinationIsForced' (Warehouses Used?) method, exactly the split the assertion warns against._

- Output: `eval-30-order-splitting-tt/outputs/`
- Narration: `eval-30-order-splitting-tt/narration.md`
- Raw transcript: `eval-30-order-splitting-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `assertion-criteria-declared` — eval-30-order-splitting-tt

Grader said: _normalizedProductGroups sorts inner and outer lists (".sorted().toList()" then ".sorted(Comparator.comparing(...))") before assertEquals, with no column/title/description stating shipments are compared without regard to order._

- Output: `eval-30-order-splitting-tt/outputs/`
- Narration: `eval-30-order-splitting-tt/narration.md`
- Raw transcript: `eval-30-order-splitting-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `no-duplicate-rows-within-a-table` — eval-30-order-splitting-tt

Grader said: _groupsShipmentsByFulfillmentTypeAndAddress: PASS; doesNotHoldInStockItemsForItemsAwaitingStock: PASS; minimisesShipmentCountAcrossWarehouseChoices: PASS; determinesWarehousesUsedWhenCombinationIsForced: PASS; groupsCompanionProductsOnTheSameWarehouseWhenPossible: PASS - each row covers a distinct scenario/obligation._

- Output: `eval-30-order-splitting-tt/outputs/`
- Narration: `eval-30-order-splitting-tt/narration.md`
- Raw transcript: `eval-30-order-splitting-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `no-duplicate-role-output` — eval-7-permission-check

Grader said: _Each role appears with distinct allowed values, no duplicates: ADMIN true; USER true/false; GUEST true/false_

- Output: `eval-7-permission-check/outputs/`
- Narration: `eval-7-permission-check/narration.md`
- Raw transcript: `eval-7-permission-check/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `scenario-names-describe-conditions` — eval-7-permission-check

Grader said: _"Admin role, any action", "User role, delete action", "Guest role, read action" name conditions, not outcomes_

- Output: `eval-7-permission-check/outputs/`
- Narration: `eval-7-permission-check/narration.md`
- Raw transcript: `eval-7-permission-check/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `scenario-names-describe-conditions` — eval-9-bonus-contractor-structure

Grader said: _Senior in sales, Contractor regardless of department_

- Output: `eval-9-bonus-contractor-structure/outputs/`
- Narration: `eval-9-bonus-contractor-structure/narration.md`
- Raw transcript: `eval-9-bonus-contractor-structure/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
