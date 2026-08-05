# Analysis to-do — spec-by-example, iteration 13

Compared against **iteration 11**, grading claude-sonnet-5/default.

## ⚠️ Partial comparison

**1 of 3 evals comparable.** The rest were excluded from them:

- `eval-4-loan-approval` — the baseline never ran this eval; there is nothing to compare against
- `eval-12-subscription-loyalty-trial` — the baseline never ran this eval; there is nothing to compare against

**1 assertion verdict moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `separates-availability-and-cost` — eval-13-shipping-partial-applicability

Grader said: _Table3 columns: "Destination | Available? | Cost?" combine availability and cost in one table_

- Output: `eval-13-shipping-partial-applicability/outputs/`
- Narration: `eval-13-shipping-partial-applicability/narration.md`
- Raw transcript: `eval-13-shipping-partial-applicability/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
