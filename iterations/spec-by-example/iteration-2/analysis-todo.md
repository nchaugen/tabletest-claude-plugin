# Analysis to-do — spec-by-example, iteration 2

Compared against **iteration 1**, grading claude-sonnet-5/default.

> ⚠️ **The baseline was graded under a different regime** (unknown/default vs claude-sonnet-5/default).
> A comparison is void across a change of grading regime — re-baseline rather than interpret this.

**10 of 10 evals comparable.**

**25 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

---

## ✅ Gate closed 2026-08-01 — read this before the list

**The comparison this report made is void, and its 25 lines are not the finding.** It compares
against `iteration-1`, which was graded on no recorded model before the sonnet standard, so every
line below mixes that regime change with the § 0b instrument change.

**The comparison that means something is inside this directory:** `benchmark.json` against
`benchmark-s6b.json` — same stored outputs, same grader model, same prompts, only the assertion
texts moved. That pair reads **110/130 → 105/130, 13 slots moved**, and it is written up in
`docs/assertion-triage.md` § *Porting the repaired texts to the two thin suites*. Read that, not the
per-line causes below.

**The level fell and the accuracy rose.** All five new FAILs on the two rewritten assertions were
checked against the generated output and are correct; two of them sat behind an old PASS whose
evidence was a list of *correct* examples.

Every cause line below is filled with one of three resolutions:

- **rename artefact** — `minimal-rows-per-concern` was retired and replaced in place by
  `no-duplicate-rows-within-a-table`. A vanished id reads as WON. No verdict moved.
- **instrument, verified** — the assertion's text changed in § 0b and the new verdict was read
  against the artefact.
- **void — not attributable here** — the assertion's text did not change, so nothing in this report
  separates the regime change from grader instability. Six such slots moved on the in-iteration pair;
  four of them are eval-16's single contested reading of Table 1, not four independent flips.

---

## LOST `no-duplicate-rows-within-a-table` — eval-10-subscription-billing

Grader said: _Table 1: PASS (5 distinct scenarios, no repeats). Table 2: FAIL — rows '0 hours', '12 hours', '23 hours 59 minutes' all just restate 'no' before the boundary, and '24 hours 1 minute', '10 days', '29 days' all just restate 'yes' after the boundary, exceeding the below/at/above obligation with redundant middle samples. Table 3: PASS (each row is a distinct boundary/plan case: 20 days, 1 day, 0 days, annual 265 days)._

- Output: `eval-10-subscription-billing/outputs/`
- Narration: `eval-10-subscription-billing/narration.md`
- Raw transcript: `eval-10-subscription-billing/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): instrument, verified — text rewritten in § 0b; new verdict read against the output and correct. See `docs/assertion-triage.md` § Porting the repaired texts.

## WON `refund-table-shows-proportion` — eval-10-subscription-billing

Grader said: _Table 3 columns 'Unused Days' and 'Cycle Length (Policy)' precede 'Refund Amount?', showing the calculation step._

- Output: `eval-10-subscription-billing/outputs/`
- Narration: `eval-10-subscription-billing/narration.md`
- Raw transcript: `eval-10-subscription-billing/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): void — not attributable from this comparison; this assertion's text did not change. See the in-iteration pair in `docs/assertion-triage.md`.

## WON `open-question-surfaced` — eval-12-subscription-loyalty-trial

Grader said: _Loyalty membership never affects Monthly pricing or trial eligibility — the discount rule only touches Annual._

- Output: `eval-12-subscription-loyalty-trial/outputs/`
- Narration: `eval-12-subscription-loyalty-trial/narration.md`
- Raw transcript: `eval-12-subscription-loyalty-trial/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): void — not attributable from this comparison; this assertion's text did not change. See the in-iteration pair in `docs/assertion-triage.md`.

## WON `question-mark-only-on-outputs` — eval-12-subscription-loyalty-trial

Grader said: _Trial Granted? | Trial Length? ... Refund Amount?_

- Output: `eval-12-subscription-loyalty-trial/outputs/`
- Narration: `eval-12-subscription-loyalty-trial/narration.md`
- Raw transcript: `eval-12-subscription-loyalty-trial/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): void — not attributable from this comparison; this assertion's text did not change. See the in-iteration pair in `docs/assertion-triage.md`.

## WON `minimal-rows-per-concern` — eval-13-shipping-partial-applicability

- Output: `eval-13-shipping-partial-applicability/outputs/`
- Narration: `eval-13-shipping-partial-applicability/narration.md`
- Raw transcript: `eval-13-shipping-partial-applicability/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): rename artefact — `minimal-rows-per-concern` was retired and replaced by `no-duplicate-rows-within-a-table`. No verdict moved.

## LOST `no-duplicate-rows-within-a-table` — eval-13-shipping-partial-applicability

Grader said: _Table 1: 'Overnight in the UK' and 'Overnight in Ireland' rows duplicate the same Available=yes outcome; Table 2: 'Express above threshold, UK' repeats the free-tier outcome already shown at threshold, and 'Overnight, UK' / 'Overnight, Ireland' duplicate the same £14.99 outcome_

- Output: `eval-13-shipping-partial-applicability/outputs/`
- Narration: `eval-13-shipping-partial-applicability/narration.md`
- Raw transcript: `eval-13-shipping-partial-applicability/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): instrument, verified — text rewritten in § 0b; new verdict read against the output and correct. See `docs/assertion-triage.md` § Porting the repaired texts.

## LOST `standard-destination-value-set-or-blank` — eval-13-shipping-partial-applicability

Grader said: _"Standard, regardless of order value | UK" and "Standard, regardless of destination | {Ireland, Other}" are split into two rows rather than one {UK, Ireland, Other} set_

- Output: `eval-13-shipping-partial-applicability/outputs/`
- Narration: `eval-13-shipping-partial-applicability/narration.md`
- Raw transcript: `eval-13-shipping-partial-applicability/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): void — not attributable from this comparison; this assertion's text did not change. See the in-iteration pair in `docs/assertion-triage.md`.

## LOST `3.2-depth-fulfillment-scenarios` — eval-16-order-splitting

Grader said: _No standalone fulfillment table exists; fulfillment rows are embedded in the combined 'Table 1'._

- Output: `eval-16-order-splitting/outputs/`
- Narration: `eval-16-order-splitting/narration.md`
- Raw transcript: `eval-16-order-splitting/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): void — not attributable from this comparison; this assertion's text did not change. See the in-iteration pair in `docs/assertion-triage.md`.

## LOST `3.3-depth-delivery-address-scenarios` — eval-16-order-splitting

Grader said: _No standalone delivery-address table; address scenarios only appear inside combined 'Table 1'._

- Output: `eval-16-order-splitting/outputs/`
- Narration: `eval-16-order-splitting/narration.md`
- Raw transcript: `eval-16-order-splitting/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): void — not attributable from this comparison; this assertion's text did not change. See the in-iteration pair in `docs/assertion-triage.md`.

## LOST `3.4-depth-availability-scenarios` — eval-16-order-splitting

Grader said: _Availability scenarios are only in the merged 'Table 1', and pre-order/backorder equivalence is left as an open question rather than resolved: 'Pre-order vs. backorder — are these the same...category...?'_

- Output: `eval-16-order-splitting/outputs/`
- Narration: `eval-16-order-splitting/narration.md`
- Raw transcript: `eval-16-order-splitting/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): void — not attributable from this comparison; this assertion's text did not change. See the in-iteration pair in `docs/assertion-triage.md`.

## LOST `3.5-depth-warehouse-scenarios` — eval-16-order-splitting

Grader said: _Table 2 lacks a clear scenario demonstrating a fewer-warehouse split being preferred over an available more-warehouse alternative._

- Output: `eval-16-order-splitting/outputs/`
- Narration: `eval-16-order-splitting/narration.md`
- Raw transcript: `eval-16-order-splitting/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): void — not attributable from this comparison; this assertion's text did not change. See the in-iteration pair in `docs/assertion-triage.md`.

## WON `3.8-readability-item-property-mapping` — eval-16-order-splitting

Grader said: _Item A Fulfillment | Item A Destination | Item A Availability | Item B Fulfillment ... columns keep mapping unambiguous_

- Output: `eval-16-order-splitting/outputs/`
- Narration: `eval-16-order-splitting/narration.md`
- Raw transcript: `eval-16-order-splitting/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): void — not attributable from this comparison; this assertion's text did not change. See the in-iteration pair in `docs/assertion-triage.md`.

## WON `concerns-decomposed` — eval-16-order-splitting

Grader said: _Table 1: Shipment Grouping Decision, Table 2: Warehouse Allocation..., Table 3: Companion Product Co-location_

- Output: `eval-16-order-splitting/outputs/`
- Narration: `eval-16-order-splitting/narration.md`
- Raw transcript: `eval-16-order-splitting/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): void — not attributable from this comparison; this assertion's text did not change. See the in-iteration pair in `docs/assertion-triage.md`.

## WON `minimal-rows-per-concern` — eval-16-order-splitting

- Output: `eval-16-order-splitting/outputs/`
- Narration: `eval-16-order-splitting/narration.md`
- Raw transcript: `eval-16-order-splitting/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): rename artefact — `minimal-rows-per-concern` was retired and replaced by `no-duplicate-rows-within-a-table`. No verdict moved.

## WON `4.6-depth-cart-total-scenarios` — eval-17-shopping-cart

Grader said: _Rows: 'No coupon active', 'Percentage-off-cart coupon', 'Fixed-amount-off-cart coupon', 'Fixed discount exceeds subtotal — clamped at zero', 'Product-specific coupon, product present twice', 'Product-specific coupon, product not in cart'._

- Output: `eval-17-shopping-cart/outputs/`
- Narration: `eval-17-shopping-cart/narration.md`
- Raw transcript: `eval-17-shopping-cart/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): void — not attributable from this comparison; this assertion's text did not change. See the in-iteration pair in `docs/assertion-triage.md`.

## WON `minimal-rows-per-concern` — eval-17-shopping-cart

- Output: `eval-17-shopping-cart/outputs/`
- Narration: `eval-17-shopping-cart/narration.md`
- Raw transcript: `eval-17-shopping-cart/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): rename artefact — `minimal-rows-per-concern` was retired and replaced by `no-duplicate-rows-within-a-table`. No verdict moved.

## WON `minimal-rows-per-concern` — eval-21-event-registration-sbe

- Output: `eval-21-event-registration-sbe/outputs/`
- Narration: `eval-21-event-registration-sbe/narration.md`
- Raw transcript: `eval-21-event-registration-sbe/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): rename artefact — `minimal-rows-per-concern` was retired and replaced by `no-duplicate-rows-within-a-table`. No verdict moved.

## LOST `scenario-names-describe-conditions` — eval-21-event-registration-sbe

Grader said: _'Standard registration, no discount' echoes the Early-Bird Applies?/Group Discount Applies? = no expectation._

- Output: `eval-21-event-registration-sbe/outputs/`
- Narration: `eval-21-event-registration-sbe/narration.md`
- Raw transcript: `eval-21-event-registration-sbe/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): instrument, verified — text rewritten in § 0b; new verdict read against the output and correct. See `docs/assertion-triage.md` § Porting the repaired texts.

## WON `concerns-decomposed` — eval-24-weekly-pay-sbe

Grader said: _Response splits into 'Table 1: Weekday Regular and Overtime Pay', 'Table 2: Sunday and Holiday Premium Pay', 'Table 3: Weekly Pay Combination', 'Table 4: Hourly Rate Validation'._

- Output: `eval-24-weekly-pay-sbe/outputs/`
- Narration: `eval-24-weekly-pay-sbe/narration.md`
- Raw transcript: `eval-24-weekly-pay-sbe/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): void — not attributable from this comparison; this assertion's text did not change. See the in-iteration pair in `docs/assertion-triage.md`.

## WON `minimal-rows-per-concern` — eval-24-weekly-pay-sbe

- Output: `eval-24-weekly-pay-sbe/outputs/`
- Narration: `eval-24-weekly-pay-sbe/narration.md`
- Raw transcript: `eval-24-weekly-pay-sbe/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): rename artefact — `minimal-rows-per-concern` was retired and replaced by `no-duplicate-rows-within-a-table`. No verdict moved.

## LOST `no-duplicate-rows-within-a-table` — eval-24-weekly-pay-sbe

Grader said: _Table 1 rows '40 | 40 | 20 | 800' and '41 | 40 | 20 | 830' already establish the overtime boundary; 'Well into overtime | 50 | 40 | 20 | 1100' is a third same-branch sample re-showing the same arithmetic, making it excess. Tables 2-4 show no such duplication._

- Output: `eval-24-weekly-pay-sbe/outputs/`
- Narration: `eval-24-weekly-pay-sbe/narration.md`
- Raw transcript: `eval-24-weekly-pay-sbe/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): instrument, verified — text rewritten in § 0b; new verdict read against the output and correct. See `docs/assertion-triage.md` § Porting the repaired texts.

## WON `minimal-rows-per-concern` — eval-4-loan-approval

- Output: `eval-4-loan-approval/outputs/`
- Narration: `eval-4-loan-approval/narration.md`
- Raw transcript: `eval-4-loan-approval/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): rename artefact — `minimal-rows-per-concern` was retired and replaced by `no-duplicate-rows-within-a-table`. No verdict moved.

## LOST `scenario-names-describe-conditions` — eval-4-loan-approval

Grader said: _'Senior applicant, mid-range score only qualifies due to age' paraphrases the Approved?=yes result_

- Output: `eval-4-loan-approval/outputs/`
- Narration: `eval-4-loan-approval/narration.md`
- Raw transcript: `eval-4-loan-approval/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): instrument, verified — text rewritten in § 0b; new verdict read against the output and correct. See `docs/assertion-triage.md` § Porting the repaired texts.

## LOST `no-duplicate-rows-within-a-table` — eval-5-order-transitions

Grader said: _Table 1 PASS: rows each cover distinct transition branches. Table 2 FAIL: 'Just past the boundary | 31 days | no' and 'Long past the window | 90 days | no' both restate the same 'past boundary -> no' rule with no new branch, making the 90-day row excess._

- Output: `eval-5-order-transitions/outputs/`
- Narration: `eval-5-order-transitions/narration.md`
- Raw transcript: `eval-5-order-transitions/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): instrument, verified — text rewritten in § 0b; new verdict read against the output and correct. See `docs/assertion-triage.md` § Porting the repaired texts.

## LOST `extreme-discount-row` — eval-6-discount-interaction

Grader said: _All Table 2 rows use the same fixed 'Base Price 100, Bulk 15%, Loyalty 10%' scenario with no large-order or top-tier variation to make an extreme discount concrete._

- Output: `eval-6-discount-interaction/outputs/`
- Narration: `eval-6-discount-interaction/narration.md`
- Raw transcript: `eval-6-discount-interaction/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): void — not attributable from this comparison; this assertion's text did not change. See the in-iteration pair in `docs/assertion-triage.md`.
