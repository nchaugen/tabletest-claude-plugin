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

**11 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `1.15-format-clean-method` — eval-14-weekly-pay

Grader said: _calculatesWeeklyPay body is only 'BigDecimal actual = calculator.calculateWeeklyPay(...); assertEquals(...)' with no if/ternary logic_

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `2.1-decomposition-concern-separation` — eval-15-reis-discount

Grader said: _Three @TableTest methods: SingleTicketHistoryCounterTest, ReisDiscountLadderTest, SingleTicketDiscountCalculatorTest_

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

## LOST `title-states-system-behaviour` — eval-15-reis-discount

Grader said: _"Raises the discount by 5% for every five tickets, capped at 40%" merely restates the domain rule with no reference to system behavior_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `zone-independent-counting` — eval-15-reis-discount

Grader said: _parseHistoryEntry always returns 'new PastPurchase(purchasedAt, TravelerCategory.ADULT, ticketType, ZoneValidity.ZONE_1)' — zone fixed to ZONE_1 in the converter, never varied in a table row._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `black-box-columns` — eval-18-convert-from-code

Grader said: _Column header 'Risk Score?' appears in both @TableTest tables, exposing internal state._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `observable-io-only` — eval-18-convert-from-code

Grader said: _'Risk Score?' column and 'Integer riskScore' parameter directly expose the internal risk score._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `scenario-names-describe-conditions` — eval-18-convert-from-code

Grader said: _Names like 'Renewal with a poor claims record', 'At the risk limit', 'Standard tier with claims' describe conditions, not outcomes._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `separates-decision-and-premium` — eval-18-convert-from-code

Grader said: _routesApplicationToDecision table includes 'Premium?' column with values like '108.0' and '250.0' alongside 'Decision?'._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `concern-not-over-split` — eval-28-convert-from-methodsource

Grader said: _appliesSurchargesToBaseCost handles all surcharges in one table with 6 rows_

- Output: `eval-28-convert-from-methodsource/outputs/`
- Narration: `eval-28-convert-from-methodsource/narration.md`
- Raw transcript: `eval-28-convert-from-methodsource/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `consistent-quantity-naming` — eval-28-convert-from-methodsource

Grader said: _"Cost?" used consistently across all three tables; Zone, Weight (kg), Dimensions (cm) reused identically_

- Output: `eval-28-convert-from-methodsource/outputs/`
- Narration: `eval-28-convert-from-methodsource/narration.md`
- Raw transcript: `eval-28-convert-from-methodsource/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
