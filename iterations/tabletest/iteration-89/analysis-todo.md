# Analysis to-do — tabletest, iteration 89

Compared against **iteration 88**, grading claude-sonnet-5/default.

## ⚠️ Partial comparison

**2 of 4 evals comparable.** The rest were excluded from them:

- `eval-25-convert-from-spock` — the baseline never ran this eval; there is nothing to compare against
- `eval-30-order-splitting-tt` — the baseline never ran this eval; there is nothing to compare against

**8 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `1.11-format-description` — eval-14-weekly-pay

Grader said: _"The 40-hour overtime threshold applies only to weekday hours, not to the sum of..." adds context beyond rows_

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `1.7-readability-scenario-names` — eval-14-weekly-pay

Grader said: _"Day before the overtime cap", "First hour of overtime", "Sunday hours only"_

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `concern-not-over-split` — eval-14-weekly-pay

Grader said: _Table1 fixes Sunday/Holiday=0 and varies weekday hours; Table2 fixes weekday=0 and varies Sunday/Holiday hours, both sharing the same 'Weekly Pay?' output column - matching the described anti-pattern of same-fixture, single-sub-rule tables that should be merged._

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `held-constants-declared` — eval-14-weekly-pay

Grader said: _In method 1, sundayHours/holidayHours are hardcoded 0 in the call 'calculator.calculateWeeklyPay(weekdayHours, 0, 0, hourlyRate)' with no such column or declared fixed value in title/description; same pattern in method 2 and 5 with weekdayHours=0 hardcoded._

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `black-box-columns` — eval-18-convert-from-code

Grader said: _Columns: 'Scenario | Applicant Type | Age | Claim Count | Decision? | Premium?' — all public I/O, no internal fields._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `description-no-internals` — eval-18-convert-from-code

Grader said: _@Description text like 'isolating the risk-based rejection boundary from that rule' names the rule without exposing 'age/10 + claims*15' or numeric thresholds._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `observable-io-only` — eval-18-convert-from-code

Grader said: _No column named hasActivePolicy, internalRiskScore, or riskScore appears in any table header._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `premium-charge-is-per-claim` — eval-18-convert-from-code

Grader said: _Premium rows found: (RENEWAL,30,0,0),(RENEWAL,30,1,136),(NEW,30,0,106),(NEW,0,5,250),(NEW,10,5,0),(NEW,64,2,172),(NEW,65,2,326) - no age has three consecutive claim counts 0,1,2._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
