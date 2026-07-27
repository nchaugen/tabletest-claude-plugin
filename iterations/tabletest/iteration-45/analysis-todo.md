# Analysis to-do — tabletest, iteration 45

Compared against **iteration 44**, grading claude-sonnet-5/default.

## ⚠️ Partial comparison

**4 of 17 evals comparable.** The rest were excluded from them:

- `eval-1-convert-repetitive-tests` — the baseline never ran this eval; there is nothing to compare against
- `eval-2-parse-dates` — the baseline never ran this eval; there is nothing to compare against
- `eval-7-permission-check` — the baseline never ran this eval; there is nothing to compare against
- `eval-8-money-parse` — the baseline never ran this eval; there is nothing to compare against
- `eval-9-bonus-contractor-structure` — the baseline never ran this eval; there is nothing to compare against
- `eval-20-collections-and-quoting` — the baseline never ran this eval; there is nothing to compare against
- `eval-22-event-registration-tt` — the baseline never ran this eval; there is nothing to compare against
- `eval-23-loan-approval-tt` — the baseline never ran this eval; there is nothing to compare against
- `eval-25-convert-from-spock` — the baseline never ran this eval; there is nothing to compare against
- `eval-26-convert-from-kotest` — the baseline never ran this eval; there is nothing to compare against
- `eval-27-convert-from-testng` — the baseline never ran this eval; there is nothing to compare against
- `eval-29-shopping-cart-tt` — the baseline never ran this eval; there is nothing to compare against
- `eval-30-order-splitting-tt` — the baseline never ran this eval; there is nothing to compare against

**13 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `1.15-format-clean-method` — eval-14-weekly-pay

Grader said: _Method bodies only call calculator methods and assertEquals; no if/ternary logic present._

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `2.1-decomposition-concern-separation` — eval-15-reis-discount

Grader said: _Three @TableTest classes: SingleTicketHistoryCounterTest, ReisDiscountLadderTest, SingleTicketDiscountCalculatorTest_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `2.17-zone-irrelevance-visible` — eval-15-reis-discount

Grader said: _Adult or senior, no qualifying travel history | {ADULT, SENIOR} | {ZONE_1, ZONE_2, ZONE_3}_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `no-table-reproves-another` — eval-15-reis-discount

Grader said: _SingleTicketDiscountCalculatorTest rows 'Adult or senior reaches the first Reis tier' (5%) and 'busier travel pattern' (10%) re-derive tier percentages already established in ReisDiscountLadderTest_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `quantifier-covered-by-rows` — eval-15-reis-discount

Grader said: _"Traveler category and zone on past purchases do not affect the count and are fixed by the history converter." yet converter hardcodes ZoneValidity.ZONE_1 for every entry, so the zone-independence claim for counting is never exercised across rows._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `title-states-system-behaviour` — eval-15-reis-discount

Grader said: _"Raises the discount by 5% for every five tickets, capped at 40%" restates the ladder policy itself rather than describing what the tested method does._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `zone-independent-counting` — eval-15-reis-discount

Grader said: _parseHistoryEntry always returns 'new PastPurchase(purchasedAt, TravelerCategory.ADULT, ticketType, ZoneValidity.ZONE_1)' — zone is hardcoded to ZONE_1 in the converter, so counting is never exercised with varying zones._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `black-box-columns` — eval-18-convert-from-code

Grader said: _Column header 'Risk Score?' appears in both tables, exposing the internal risk score value directly._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `observable-io-only` — eval-18-convert-from-code

Grader said: _'Risk Score?' column with values like 93, 75, 76 exposes the internal risk-score computation, not just observable I/O._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `scenario-names-describe-conditions` — eval-18-convert-from-code

Grader said: _Names like 'At the risk limit', 'Just past the risk limit', 'Standard tier with claims' describe conditions, not outcome values._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `concern-not-over-split` — eval-28-convert-from-methodsource

Grader said: _appliesSurchargesToBaseCost combines fragile, insured, hazmat, oversize into one 6-row table_

- Output: `eval-28-convert-from-methodsource/outputs/`
- Narration: `eval-28-convert-from-methodsource/narration.md`
- Raw transcript: `eval-28-convert-from-methodsource/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `consistent-quantity-naming` — eval-28-convert-from-methodsource

Grader said: _"Cost?", "Weight (kg)", "Dimensions (cm)", "Zone", "Options" columns used consistently across all three tables_

- Output: `eval-28-convert-from-methodsource/outputs/`
- Narration: `eval-28-convert-from-methodsource/narration.md`
- Raw transcript: `eval-28-convert-from-methodsource/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `rule-statable-from-table` — eval-28-convert-from-methodsource

Grader said: _resolvesBaseCost: FAIL (dimensional weight divisor 5000 never published); appliesSurchargesToBaseCost: FAIL (surcharge amounts/multipliers/floor not stated in table or description); costIsUnaffectedByCarrier: PASS_

- Output: `eval-28-convert-from-methodsource/outputs/`
- Narration: `eval-28-convert-from-methodsource/narration.md`
- Raw transcript: `eval-28-convert-from-methodsource/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
