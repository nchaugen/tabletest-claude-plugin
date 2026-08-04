# Analysis to-do — tabletest, iteration 66

Compared against **iteration 63**, grading claude-sonnet-5/default.

**1 of 1 evals comparable.**

**3 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `assertion-criteria-declared` — eval-30-order-splitting-tt

Grader said: _Helpers like 'shipmentGroups' and 'shipmentsByWarehouseId' convert to Set/Map types matching the native column semantics; no undisclosed sorting, tolerance, or subset matching is applied beyond inherent Set/Map equality._

- Output: `eval-30-order-splitting-tt/outputs/`
- Narration: `eval-30-order-splitting-tt/narration.md`
- Raw transcript: `eval-30-order-splitting-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Won as a second-order effect of `fbb1126`, and the prediction said it would stay failing.** The
  reshape replaced iteration-63's single `List<Set<String>>` expectation — set-ified in the method
  body where no reader sees it — with two expectation columns, `Immediate Shipment?` and
  `When-Available Shipment?`, each a `Set<String>`. Unorderedness is now visible in the notation,
  which is rule 10's own second repair. **The repair reached further than the {{row}} pair it was
  aimed at**, and that is the information: reshaping the column changed the expectation too.

## WON `no-duplicate-rows-within-a-table` — eval-30-order-splitting-tt

Grader said: _groupsItemsByFulfillmentTypeAndDeliveryAddress: PASS (5 distinct obligations, no repeat). doesNotHoldInStockItemsForItemsAwaitingAvailability: PASS (3 rows match 3 obligations via value-set). choosesTheWarehouseCombinationWithFewestShipments: PASS (2 rows, no excess, though under-covers per separate assertion). shipsCompanionProductsFromTheSameWarehouseWhenPossible: PASS (4 rows each show distinct behaviour: together, separate-infeasible, address override, availability override - no exact repeats)._

- Output: `eval-30-order-splitting-tt/outputs/`
- Narration: `eval-30-order-splitting-tt/narration.md`
- Raw transcript: `eval-30-order-splitting-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Predicted, confirmed, and the repair is visible in the table.** `fbb1126` said the collapse needs
  the column reshaped first, and that is exactly what happened: stock status left the map cell
  (`[p1: IN_STOCK, p2: BACKORDERED]`) and became two columns, `Item 1 Stock` and `Item 2 Stock`,
  where a value set expands. `{BACKORDERED, PRE_ORDERED}` then collapses the pair iteration-63 wrote
  twice — four {{rows}} become three, and 26/26 returns. **Note which branch fired:** the "give the
  value its own column" branch, not the third-item branch iteration-55 used. Identity stayed
  answerable because the two items are fixed in the method body and named in the expectation
  columns.

## WON `scenario-names-describe-conditions` — eval-30-order-splitting-tt

Grader said: _Names like 'Companions ship together when a common warehouse stocks both' and 'Both items in stock ship together immediately' name the rule/mechanism, mirroring the reference's own accepted style ('All in stock ship together')._

- Output: `eval-30-order-splitting-tt/outputs/`
- Narration: `eval-30-order-splitting-tt/narration.md`
- Raw transcript: `eval-30-order-splitting-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **Not attributed — third-worst flipper in the triage**, and it moved the same way on eval-29 in the
  same portion, which is the signature of grader variance rather than two evals improving at once.
  The names it approves here (*"Both items in stock ship together immediately"*) are the style it
  rejected in iteration-63.
