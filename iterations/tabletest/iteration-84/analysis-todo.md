# Analysis to-do — tabletest, iteration 84

Compared against **iteration 81**, grading claude-sonnet-5/default.

**1 of 1 evals comparable.**

**3 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `1.11-format-description` — eval-14-weekly-pay

Grader said: _"Overtime Threshold (hrs) is the weekday hour count above which the overtime rate applies... every row holds it at 40." adds context not restating multipliers_

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `1.7-readability-scenario-names` — eval-14-weekly-pay

Grader said: _'Weekday hours just past the overtime threshold', 'Sunday hours paid at double time' etc describe work patterns_

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `concerns-decomposed` — eval-14-weekly-pay

Grader said: _calculatesWeeklyPay mixes overtime-boundary classification and pay-multiplication into one table/method._

- Output: `eval-14-weekly-pay/outputs/`
- Narration: `eval-14-weekly-pay/narration.md`
- Raw transcript: `eval-14-weekly-pay/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
