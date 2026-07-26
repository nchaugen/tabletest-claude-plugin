# Analysis to-do — tabletest, iteration 42

Compared against **iteration 41**, grading claude-sonnet-5/default.

## ⚠️ Partial comparison

**1 of 6 evals comparable.** The rest were excluded from them:

- `eval-14-weekly-pay` — the baseline never ran this eval; there is nothing to compare against
- `eval-15-reis-discount` — the baseline never ran this eval; there is nothing to compare against
- `eval-18-convert-from-code` — the baseline never ran this eval; there is nothing to compare against
- `eval-22-event-registration-tt` — the baseline never ran this eval; there is nothing to compare against
- `eval-23-loan-approval-tt` — the baseline never ran this eval; there is nothing to compare against

**4 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `concern-not-over-split` — eval-26-convert-from-kotest

Grader said: _appliesSurchargesToBaseCost is one table with 6 rows covering oversize, fragile, insured, fragile+insured, hazmat_

- Output: `eval-26-convert-from-kotest/outputs/`
- Narration: `eval-26-convert-from-kotest/narration.md`
- Raw transcript: `eval-26-convert-from-kotest/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `consistent-quantity-naming` — eval-26-convert-from-kotest

Grader said: _Base rate table and dimensional weight table both use 'Base Cost?'; surcharges and carrier tables both use 'Total Cost?'_

- Output: `eval-26-convert-from-kotest/outputs/`
- Narration: `eval-26-convert-from-kotest/narration.md`
- Raw transcript: `eval-26-convert-from-kotest/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `no-duplicate-rows-within-a-table` — eval-26-convert-from-kotest

Grader said: _determinesBaseRateByRegionSpeedAndWeight: PASS (8 distinct region/speed/tier rows); selectsEffectiveWeightFromActualOrDimensional: PASS (2 rows, actual vs dimensional); appliesSurchargesToBaseCost: PASS (6 distinct surcharge scenarios); carrierDoesNotAffectCost: PASS (1 value-set row {DHL, UPS, FEDEX})_

- Output: `eval-26-convert-from-kotest/outputs/`
- Narration: `eval-26-convert-from-kotest/narration.md`
- Raw transcript: `eval-26-convert-from-kotest/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `rule-statable-from-table` — eval-26-convert-from-kotest

Grader said: _determinesBaseRateByRegionSpeedAndWeight: FAIL (no boundary rows at 1/5/15kg, bracket cutoffs never published in table or @Description); selectsEffectiveWeightFromActualOrDimensional: PASS (description states 'volume / 5000, rounded to 3 decimal places'); appliesSurchargesToBaseCost: PASS (description names each surcharge and order); carrierDoesNotAffectCost: PASS (method name states invariance)_

- Output: `eval-26-convert-from-kotest/outputs/`
- Narration: `eval-26-convert-from-kotest/narration.md`
- Raw transcript: `eval-26-convert-from-kotest/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
