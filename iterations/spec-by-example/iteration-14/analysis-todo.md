# Analysis to-do — spec-by-example, iteration 14

Compared against **iteration 13**, grading claude-sonnet-5/default.

## ⚠️ Partial comparison

**3 of 10 evals comparable.** The rest were excluded from them:

- `eval-5-order-transitions` — the baseline never ran this eval; there is nothing to compare against
- `eval-6-discount-interaction` — the baseline never ran this eval; there is nothing to compare against
- `eval-10-subscription-billing` — the baseline never ran this eval; there is nothing to compare against
- `eval-16-order-splitting` — the baseline never ran this eval; there is nothing to compare against
- `eval-17-shopping-cart` — the baseline never ran this eval; there is nothing to compare against
- `eval-21-event-registration-sbe` — the baseline never ran this eval; there is nothing to compare against
- `eval-24-weekly-pay-sbe` — the baseline never ran this eval; there is nothing to compare against

**1 assertion verdict moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `question-mark-only-on-outputs` — eval-12-subscription-loyalty-trial

Grader said: _Trial Granted? | Charged Today? ... Amount Charged? ... Refund Eligible? ... Refund Amount?_

- Output: `eval-12-subscription-loyalty-trial/outputs/`
- Narration: `eval-12-subscription-loyalty-trial/narration.md`
- Raw transcript: `eval-12-subscription-loyalty-trial/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **real artefact change, but on a slot that flips.** it-13 put `?` on an
  input column (`Loyalty Member?`); it-14 puts it only on outputs, and narration:69 shows the
  decision taken explicitly (*"making sure 'State After?' and 'Charge Occasion?' both have question
  marks since they're outputs"*). **Not creditable to the batch:** rule 13, which owns this
  convention, was untouched by every commit in it, and the slot reads PASS/fail/PASS/PASS/fail/PASS
  across it-2/3/4/9/13/14. Churn on an unstable slot, in the good direction.

## The other seven evals — compared by hand against their own baselines

The default comparison is iteration-13, which ran 3 evals. The live baseline merges five
iterations, so the seven "nothing to compare against" lines above are an artefact of that default,
not missing data. Resolved with `check-baseline.js`:

| Eval | Baseline | Score | Verdict |
|---|---|---|---|
| 4 loan-approval | it-13 | 12/13 → 12/13 | flat, same slot failing |
| 5 order-transitions | it-9 | 10/10 → 10/10 | flat |
| 6 discount-interaction | it-9 | 7/7 → 7/7 | flat |
| 10 subscription-billing | it-9 | 13/16 → **15/16** | won `question-mark-only-on-outputs`, `refund-table-shows-proportion` |
| 12 loyalty-trial | it-13 | 11/13 → **12/13** | won `question-mark-only-on-outputs` |
| 13 shipping-partial | it-13 | 9/12 → 9/12 | flat, same three slots failing |
| 16 order-splitting | it-12 | 18/19 → 18/19 | flat, same slot failing |
| 17 shopping-cart | it-8 | 17/18 → **18/18** | won `4.5-depth-coupon-application` |
| 21 event-registration | it-10 | 11/13 → 11/13 | flat, same two slots failing |
| 24 weekly-pay | it-10 | 9/9 → 9/9 | flat |

**117/130 → 121/130. Nothing lost anywhere, in any eval, on any slot.**

### The one durable win — `4.5-depth-coupon-application`/17

**It had failed six runs straight** (it-2, 3, 4, 5, 7, 8) and passes here for the first time. The
other three wins are on slots with flip histories; this one has none.

The artefact says how. it-8 was missing the rows *expired coupon (none active)* and *nonexistent
code (another active)*. it-14 writes all four and states the indifference explicitly —
`{Percentage, Fixed Amount, Product Discount}` as a value set on the Coupon Type column, with the
note *"Rows 3–6 use a value set for Coupon Type to state explicitly: rejection doesn't depend on
what kind of coupon it would have been"* (output:119). The narration takes the decision in both
halves: keep the two rejection rows distinct because *"it shows whether a failed attempt
accidentally clears the previous coupon"* (:45), and collapse the type dimension into a value set
because *"the discount type itself doesn't affect the apply logic"* (:43).

**That is repair 1's mechanism** — a value set discharges the obligations it absorbs, so the
collapse decision is decidable in one table. eval-17's baseline `b2921012` predates repair 1
(`05afc86`). **Attribution is not available from this run** — the delta spans repairs 1/2/4/5, the
repair 3 revert, repair 6 and the cut pass at once. What it establishes is that **eval-17 is a
candidate second host for repair 1**, settleable with a single-variable run, where § J19 recorded
repair 1 as confirmed on eval-7 and failing on eval-13.
