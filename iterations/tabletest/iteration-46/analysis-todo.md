# Analysis to-do — tabletest variant=register, iteration 1

Compared against **official (iterations 45, 44, 43, 42, 41, 40 merged)**, grading claude-sonnet-5/default.

**4 of 4 evals comparable.**

**9 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `2.21-readability-relative-time` — eval-15-reis-discount

Grader said: _['2024-06-29T12:00:00@SINGLE']' uses absolute timestamps, not relative 'days ago' phrasing_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `no-table-reproves-another` — eval-15-reis-discount

Grader said: _combinesRecentTicketCountAndCategoryIntoAFinalDiscount is a plain @Test, not a @TableTest, so no table re-proves another_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `quantifier-covered-by-rows` — eval-15-reis-discount

Grader said: _"Adult and senior tickets use the accumulation ladder regardless of zone | ... | Zone | {ZONE_1, ZONE_2, ZONE_3}"_

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `title-states-system-behaviour` — eval-15-reis-discount

Grader said: _"Selects the discount rule to apply by traveler category, ignoring zone" describes system behavior, not a restated domain fact._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `quantifier-covered-by-rows` — eval-22-event-registration-tt

Grader said: _"acceptance here does not depend on either" claims independence but registrationDate/groupSize are fixed to a single value, not varied or shown as columns_

- Output: `eval-22-event-registration-tt/outputs/`
- Narration: `eval-22-event-registration-tt/narration.md`
- Raw transcript: `eval-22-event-registration-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `description-no-redundant-field-values` — eval-23-loan-approval-tt

Grader said: _Scenario 'At 65, in the senior score range' restates Age=65 cell value in same row_

- Output: `eval-23-loan-approval-tt/outputs/`
- Narration: `eval-23-loan-approval-tt/narration.md`
- Raw transcript: `eval-23-loan-approval-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `no-duplicate-rows-within-a-table` — eval-23-loan-approval-tt

Grader said: _appliesAgeAdjustedCreditScoreThreshold: 5 distinct rows PASS; decidesFromStableIncomeAboveThreshold: 3 rows w/ value sets PASS; rejectsBelowThresholdRegardlessOfIncome: 2 rows PASS_

- Output: `eval-23-loan-approval-tt/outputs/`
- Narration: `eval-23-loan-approval-tt/narration.md`
- Raw transcript: `eval-23-loan-approval-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `title-states-system-behaviour` — eval-23-loan-approval-tt

Grader said: _'Applies an age-adjusted credit score threshold', 'Decides approval from stable income once the score clears its threshold', and 'Rejects a below-threshold score even when income is missing' each restate the lending policy itself (threshold exists, income decides approval, rejection precedence) rather than describing computation in system-neutral terms; striking the implicit subject leaves each as a true domain/policy statement._

- Output: `eval-23-loan-approval-tt/outputs/`
- Narration: `eval-23-loan-approval-tt/narration.md`
- Raw transcript: `eval-23-loan-approval-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `scenario-names-describe-conditions` — eval-7-permission-check

Grader said: _'USER deleting', 'GUEST writing or deleting' etc. describe conditions, not outcomes_

- Output: `eval-7-permission-check/outputs/`
- Narration: `eval-7-permission-check/narration.md`
- Raw transcript: `eval-7-permission-check/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
