# Analysis to-do — tabletest, iteration 57

Compared against **iteration 54**, grading claude-sonnet-5/default.

## ⚠️ Partial comparison

**2 of 3 evals comparable.** The rest were excluded from them:

- `eval-29-shopping-cart-tt` — generation failed (timeout or crash), so this eval produced no answer to compare — re-run it before reading anything into the gap

**16 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `1.11-format-description` — eval-14-weekly-pay

Grader said: _'Regular hours are paid at the base hourly rate; overtime is 1.5x and Sunday and holiday hours are 2x.' restates multipliers already shown in rows_

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `1.4-depth-combined-scenario` — eval-14-weekly-pay

Grader said: _Regular, overtime, Sunday and holiday | 40 | 5 | 8 | 8 | 20 | 1590_

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `concern-not-over-split` — eval-14-weekly-pay

Grader said: _floorsWeeklyPayAtZero and rejectsNegativeHourlyRate address distinct concerns (floor behavior vs rate validation), not fragmented sub-rules of one setup_

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `separates-classification-and-calculation` — eval-14-weekly-pay

Grader said: _splitsWeekdayHours (classification) is separate from combinesHoursIntoWeeklyPay (calculation)_

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `2.1-decomposition-concern-separation` — eval-15-reis-discount

Grader said: _No table found showing scheme mapping separate from ladder; only DiscountLadderTest present._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `2.16-no-duplicate-tier-mapping` — eval-15-reis-discount

Grader said: _Only one table shown (DiscountLadderTest); no second price-application table to verify no duplication, response incomplete due to API error_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `2.17-zone-irrelevance-visible` — eval-15-reis-discount

Grader said: _DiscountLadderTest has no Zone column at all_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `2.18-adult-senior-value-set` — eval-15-reis-discount

Grader said: _No table shown exercises traveller category with a value set {ADULT, SENIOR}; converter defaults category to ADULT only_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `2.2-children-flat-discount` — eval-15-reis-discount

Grader said: _No mention of child flat 20% discount anywhere in the response files._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `2.4-depth-rolling-window-boundary` — eval-15-reis-discount

Grader said: _No rolling window test file (e.g. 30 vs 31 days) present in the response._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `concerns-decomposed` — eval-15-reis-discount

Grader said: _Only one @TableTest method (computesLadderDiscount) is present in the generated files; scheme selection, countability, rolling window, and aggregation dimensions have no corresponding tables._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `count-derived-from-raw-history` — eval-15-reis-discount

Grader said: _DiscountLadderTest consumes a bare 'Ticket Count' int, not derived from a history table._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `no-table-reproves-another` — eval-15-reis-discount

Grader said: _Only one @TableTest method exists (computesLadderDiscount); no other table exists to re-prove it._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `period-ticket-excluded-from-count` — eval-15-reis-discount

Grader said: _No table shows period tickets not counting toward travel count; no such rows exist._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `scheme-derived-once` — eval-15-reis-discount

Grader said: _No scheme mapping table exists at all in the provided files._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `window-boundary-uses-purchase-time` — eval-15-reis-discount

Grader said: _No sub-day boundary rows exist; only 'daysAgo' integer field in PastPurchaseConverters._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
