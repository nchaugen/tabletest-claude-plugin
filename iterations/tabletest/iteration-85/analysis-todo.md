# Analysis to-do — tabletest, iteration 85

Compared against **iteration 79**, grading claude-sonnet-5/default.

**1 of 1 evals comparable.**

**5 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `all-outputs-same-table` — eval-30-order-splitting-tt

Grader said: _Warehouse coverage table only outputs 'Warehouses Used?' (a set of warehouse ids), while the actual per-warehouse product assignment ('Warehouse Assignment?') is only exercised in the separate companion-grouping table, splitting the warehouse-allocation concern's outputs across two methods._

- Output: `eval-30-order-splitting-tt/outputs/`
- Narration: `eval-30-order-splitting-tt/narration.md`
- Raw transcript: `eval-30-order-splitting-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `concern-companion-products` — eval-30-order-splitting-tt

Grader said: _prefersCompanionCoLocationWhenItDoesNotCostAnExtraWarehouse table shows companions shipping together or apart based on feasibility._

- Output: `eval-30-order-splitting-tt/outputs/`
- Narration: `eval-30-order-splitting-tt/narration.md`
- Raw transcript: `eval-30-order-splitting-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `native-collection-output` — eval-30-order-splitting-tt

Grader said: _Shipments? column: 'DELIVERY@addr-1@W1@IMMEDIATE: {p1}, ...' keyed by a string built via the helper `shipmentKey` (`shipment.getFulfillmentType() + "@" + address + "@" + warehouseId + "@" + availability`), packing four values into one stringified key. Other columns (Shipment Groups?, Ships Immediately?/Ships When Available?, Warehouses Used?, Warehouse Assignment?, Availability?) are native sets/maps/scalars and pass._

- Output: `eval-30-order-splitting-tt/outputs/`
- Narration: `eval-30-order-splitting-tt/narration.md`
- Raw transcript: `eval-30-order-splitting-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `no-duplicate-rows-within-a-table` — eval-30-order-splitting-tt

Grader said: _groupsItemsThatMustShipSeparately: PASS, resolvesAvailabilityFromStockStatus: PASS, splitsItemsFromOneWarehouseByAvailability: PASS, choosesTheFewestWarehousesThatCoverTheOrder: PASS, prefersCompanionCoLocationWhenItDoesNotCostAnExtraWarehouse: PASS, splitsOrderAccordingToAllRulesTogether: PASS — each table's rows cover distinct obligations with no repeats._

- Output: `eval-30-order-splitting-tt/outputs/`
- Narration: `eval-30-order-splitting-tt/narration.md`
- Raw transcript: `eval-30-order-splitting-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `rule-falsifiable-by-a-row` — eval-30-order-splitting-tt

Grader said: _splitsOrderAccordingToAllRulesTogether has only one row; 'Shipments?' trivially holds one fixed value and a hardcoded-return implementation would satisfy it, violating condition (3)._

- Output: `eval-30-order-splitting-tt/outputs/`
- Narration: `eval-30-order-splitting-tt/narration.md`
- Raw transcript: `eval-30-order-splitting-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
