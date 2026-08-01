# Analysis to-do — spec-by-example, iteration 2

Compared against **iteration 1**, grading claude-sonnet-5/default.

> ⚠️ **The baseline was graded under a different regime** (unknown/default vs claude-sonnet-5/default).
> A comparison is void across a change of grading regime — re-baseline rather than interpret this.

**10 of 10 evals comparable.**

**10 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `open-question-surfaced` — eval-12-subscription-loyalty-trial

Grader said: _Loyalty membership never affects Monthly pricing or trial eligibility — the discount rule only touches Annual._

- Output: `eval-12-subscription-loyalty-trial/outputs/`
- Narration: `eval-12-subscription-loyalty-trial/narration.md`
- Raw transcript: `eval-12-subscription-loyalty-trial/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `question-mark-only-on-outputs` — eval-12-subscription-loyalty-trial

Grader said: _Trial Granted? Trial Length? Price? Charged Immediately? Refund Given? Refund Amount?_

- Output: `eval-12-subscription-loyalty-trial/outputs/`
- Narration: `eval-12-subscription-loyalty-trial/narration.md`
- Raw transcript: `eval-12-subscription-loyalty-trial/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `standard-destination-value-set-or-blank` — eval-13-shipping-partial-applicability

Grader said: _Standard, regardless of order value ... UK ... £3.99' and 'Standard, regardless of destination ... {Ireland, Other} ... £3.99' split destination across two rows rather than one {UK, Ireland, Other} row_

- Output: `eval-13-shipping-partial-applicability/outputs/`
- Narration: `eval-13-shipping-partial-applicability/narration.md`
- Raw transcript: `eval-13-shipping-partial-applicability/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `3.5-depth-warehouse-scenarios` — eval-16-order-splitting

Grader said: _No scenario explicitly compares a feasible fewer-warehouse split against a more-warehouse alternative to show preference for fewer warehouses._

- Output: `eval-16-order-splitting/outputs/`
- Narration: `eval-16-order-splitting/narration.md`
- Raw transcript: `eval-16-order-splitting/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `3.8-readability-item-property-mapping` — eval-16-order-splitting

Grader said: _Columns 'Item A Fulfillment | Item A Destination | Item A Availability | Item B Fulfillment | Item B Destination | Item B Availability' keep per-item properties distinct_

- Output: `eval-16-order-splitting/outputs/`
- Narration: `eval-16-order-splitting/narration.md`
- Raw transcript: `eval-16-order-splitting/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `4.6-depth-cart-total-scenarios` — eval-17-shopping-cart

Grader said: _Rows: 'No coupon active', 'Percentage-off-cart coupon', 'Fixed-amount-off-cart coupon', 'Fixed discount exceeds subtotal — clamped at zero', product-specific present/absent._

- Output: `eval-17-shopping-cart/outputs/`
- Narration: `eval-17-shopping-cart/narration.md`
- Raw transcript: `eval-17-shopping-cart/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `minimal-rows-per-concern` — eval-17-shopping-cart

Grader said: _Table 3 rows address only coupon-type/total interplay (7 rows) without mixing in item add/remove or checkout logic_

- Output: `eval-17-shopping-cart/outputs/`
- Narration: `eval-17-shopping-cart/narration.md`
- Raw transcript: `eval-17-shopping-cart/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `minimal-rows-per-concern` — eval-21-event-registration-sbe

Grader said: _Table 1 has 9 rows focused on validity; Table 2 has 7 rows focused on pricing_

- Output: `eval-21-event-registration-sbe/outputs/`
- Narration: `eval-21-event-registration-sbe/narration.md`
- Raw transcript: `eval-21-event-registration-sbe/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `concerns-decomposed` — eval-24-weekly-pay-sbe

Grader said: _Table 1: Weekday Regular and Overtime Pay ... Table 2: Sunday and Holiday Premium Pay ... Table 3 ... Table 4_

- Output: `eval-24-weekly-pay-sbe/outputs/`
- Narration: `eval-24-weekly-pay-sbe/narration.md`
- Raw transcript: `eval-24-weekly-pay-sbe/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `minimal-rows-per-concern` — eval-24-weekly-pay-sbe

Grader said: _Table 1 has 5 rows, Table 2 has 4 rows, Table 3 has 3 rows, Table 4 has 3 rows_

- Output: `eval-24-weekly-pay-sbe/outputs/`
- Narration: `eval-24-weekly-pay-sbe/narration.md`
- Raw transcript: `eval-24-weekly-pay-sbe/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
