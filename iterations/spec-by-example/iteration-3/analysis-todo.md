# Analysis to-do — spec-by-example variant=next, iteration 1

Compared against **official (iterations 2, 1 merged)**, grading claude-sonnet-5/default.

**10 of 10 evals comparable.**

**17 assertion verdicts moved.**

> **Gate closed 2026-08-01. 105/130 -> 112/130, +7 net; 12 won, 5 lost.** Pre-registered prediction:
> 3 of 4 held.
>
> **Held:** `no-duplicate-rows-within-a-table` won on **3 of its 4** (evals 5, 13, 24 — predicted ≥2).
> **The falsifier passed:** `scenario-names-describe-conditions` flipped to PASS on *both* evals 4 and
> 21, so inlining a rule whose text the grader also uses does change behaviour. Net improved.
>
> **Wrong:** I predicted eval-16's 8-slot cluster would move as a block or not at all. Two of its
> slots moved independently and in opposite directions (`3.5-depth-warehouse` won,
> `concerns-decomposed` lost), and the other six did not move. The block framing was wrong.
>
> **The five losses are three different kinds. Do not read them as one.**
>
> 1. **`question-mark-only-on-outputs`/12 — a real content loss caused by this migration, now fixed.**
>    The published skill illustrates the `?`-on-inputs rule with a good/bad table; the migration kept
>    rule 13's *statement* and dropped its *illustration*. The output then used `Loyalty Member?` as
>    an input column — the exact defect that table prevented. The good/bad table is now part of rule
>    13's example for all three skills. **Unmeasured.**
> 2. **`scenario-names-describe-conditions`/24 and `separates-availability-and-cost`/13 — real
>    misses.** The rules are present and correct; they did not fire on these two outputs. "Sum lands
>    exactly at zero" restates its own `Total Weekly Pay?` cell. Salience, not correctness — and the
>    same assertion won on two other evals, so net +1.
> 3. **`concerns-decomposed`/16 and `refund-table-shows-proportion`/10 — known unstable slots.** Both
>    flipped in the 2026-08-01 `s6b` regrade too, in the opposite direction. `concerns-decomposed` is
>    also still on **un-unified** text, since sharing it needs a `tabletest` regrade. Do not attribute
>    either to this change without a confirming re-grade.


These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `refund-table-shows-proportion` — eval-10-subscription-billing

Grader said: _Table 3 columns: Cycle Amount (£), Cycle Length (Days), Days Remaining (Unused), Refund Amount (£)? — no fraction/proportion/daily-rate column shown._

- Output: `eval-10-subscription-billing/outputs/`
- Narration: `eval-10-subscription-billing/narration.md`
- Raw transcript: `eval-10-subscription-billing/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `question-mark-only-on-outputs` — eval-12-subscription-loyalty-trial

Grader said: _"Loyalty Member?" is used as an input column header in Tables 1, 2, and 3a, not just outputs_

- Output: `eval-12-subscription-loyalty-trial/outputs/`
- Narration: `eval-12-subscription-loyalty-trial/narration.md`
- Raw transcript: `eval-12-subscription-loyalty-trial/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `express-uses-value-sets` — eval-13-shipping-partial-applicability

Grader said: _Row: 'Express | 49.99 | {UK, Ireland, France} | 50 | 9.99' and 'Express | 50.00 | {Ireland, France} | 50 | 9.99'_

- Output: `eval-13-shipping-partial-applicability/outputs/`
- Narration: `eval-13-shipping-partial-applicability/narration.md`
- Raw transcript: `eval-13-shipping-partial-applicability/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `no-duplicate-rows-within-a-table` — eval-13-shipping-partial-applicability

Grader said: _Table 1: PASS (each row distinct: below/at-threshold UK/outside-UK). Table 2: PASS (eligible vs ineligible destinations, each distinct)._

- Output: `eval-13-shipping-partial-applicability/outputs/`
- Narration: `eval-13-shipping-partial-applicability/narration.md`
- Raw transcript: `eval-13-shipping-partial-applicability/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `overnight-grouped` — eval-13-shipping-partial-applicability

Grader said: _'Eligible destination, any order value | {UK, Ireland} | {10, 500} | 14.99 |'_

- Output: `eval-13-shipping-partial-applicability/outputs/`
- Narration: `eval-13-shipping-partial-applicability/narration.md`
- Raw transcript: `eval-13-shipping-partial-applicability/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `separates-availability-and-cost` — eval-13-shipping-partial-applicability

Grader said: _Table 2 merges eligibility and cost: '| Destination | Order Value (£) | Cost (£)? | Rejection Reason? |'_

- Output: `eval-13-shipping-partial-applicability/outputs/`
- Narration: `eval-13-shipping-partial-applicability/narration.md`
- Raw transcript: `eval-13-shipping-partial-applicability/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `3.5-depth-warehouse-scenarios` — eval-16-order-splitting

Grader said: _Rows cover single warehouse ('A single warehouse holds the full quantity'), multi-warehouse split, fewer-vs-more warehouse preference ('Both a two-warehouse and a three-warehouse combination are available'), and shortfall ('Combined warehouse stock falls short of the quantity needed')._

- Output: `eval-16-order-splitting/outputs/`
- Narration: `eval-16-order-splitting/narration.md`
- Raw transcript: `eval-16-order-splitting/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `concerns-decomposed` — eval-16-order-splitting

Grader said: _Table 1 merges fulfillment, address, and ship-date splitting into one table: "Groups Order Lines into Shipment Buckets by Fulfillment, Address, and Ship Date" instead of three distinct concern tables._

- Output: `eval-16-order-splitting/outputs/`
- Narration: `eval-16-order-splitting/narration.md`
- Raw transcript: `eval-16-order-splitting/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `blank-for-absent-optional` — eval-21-event-registration-sbe

Grader said: _'Complete registration, no extras | Ada | ada@example.com | | |' shows blank Dietary/Accessibility cells_

- Output: `eval-21-event-registration-sbe/outputs/`
- Narration: `eval-21-event-registration-sbe/narration.md`
- Raw transcript: `eval-21-event-registration-sbe/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `scenario-names-describe-conditions` — eval-21-event-registration-sbe

Grader said: _Names like 'Well before cutoff', 'Below threshold', 'Neither applies', 'Both apply (assumed: stack)' describe conditions, not output values_

- Output: `eval-21-event-registration-sbe/outputs/`
- Narration: `eval-21-event-registration-sbe/narration.md`
- Raw transcript: `eval-21-event-registration-sbe/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `no-duplicate-rows-within-a-table` — eval-24-weekly-pay-sbe

Grader said: _Table1 PASS (32/40/41 boundaries); Table2 PASS (distinct branches incl. negative); Table3 PASS (Sunday/Holiday type mapping + zero + negative); Table4 PASS (ordinary/combined/zero/floored); Table5 PASS (positive/zero/below-zero boundary)._

- Output: `eval-24-weekly-pay-sbe/outputs/`
- Narration: `eval-24-weekly-pay-sbe/narration.md`
- Raw transcript: `eval-24-weekly-pay-sbe/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `scenario-names-describe-conditions` — eval-24-weekly-pay-sbe

Grader said: _"Sum lands exactly at zero" and "Sum would go negative, floored at zero" directly restate the Total Weekly Pay?=0 outcome; "Positive rate accepted"/"rejected" restate the Valid? outcome._

- Output: `eval-24-weekly-pay-sbe/outputs/`
- Narration: `eval-24-weekly-pay-sbe/narration.md`
- Raw transcript: `eval-24-weekly-pay-sbe/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `separates-classification-and-calculation` — eval-24-weekly-pay-sbe

Grader said: _Table 1 'Splits weekday hours into regular and overtime bands' is separate from Table 2 'Computes weekday pay from regular and overtime hours' and Table 3 premium pay calculation._

- Output: `eval-24-weekly-pay-sbe/outputs/`
- Narration: `eval-24-weekly-pay-sbe/narration.md`
- Raw transcript: `eval-24-weekly-pay-sbe/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `concerns-decomposed` — eval-4-loan-approval

Grader said: _'I decomposed it into two tables since threshold selection ... and the approval decision ... are separate rules'_

- Output: `eval-4-loan-approval/outputs/`
- Narration: `eval-4-loan-approval/narration.md`
- Raw transcript: `eval-4-loan-approval/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `scenario-names-describe-conditions` — eval-4-loan-approval

Grader said: _Names like 'Score exactly at the threshold', 'Score one point above the threshold' describe conditions, not results._

- Output: `eval-4-loan-approval/outputs/`
- Narration: `eval-4-loan-approval/narration.md`
- Raw transcript: `eval-4-loan-approval/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `no-duplicate-rows-within-a-table` — eval-5-order-transitions

Grader said: _Table 1 PASS: each row is a distinct transition/rejection branch, and a redundant row was explicitly dropped ('I dropped a redundant "rejects cancelling a delivered order" row'). Table 2 PASS: days-since-delivery rows (5, 30, 31) are boundary cases (within/last day/just past), not a linear walk, plus a value-set row for non-delivered statuses — no duplicates in either table._

- Output: `eval-5-order-transitions/outputs/`
- Narration: `eval-5-order-transitions/narration.md`
- Raw transcript: `eval-5-order-transitions/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `extreme-discount-row` — eval-6-discount-interaction

Grader said: _"Both at maximum tier | 15% | 20% | 35% | 32% | 20% |" forces cap decision_

- Output: `eval-6-discount-interaction/outputs/`
- Narration: `eval-6-discount-interaction/narration.md`
- Raw transcript: `eval-6-discount-interaction/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
