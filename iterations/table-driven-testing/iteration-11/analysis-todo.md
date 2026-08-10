# Analysis to-do — table-driven-testing, iteration 11

Compared against **iteration 10**, grading claude-sonnet-5/default.

## ⚠️ Partial comparison

**4 of 5 evals comparable.** The rest were excluded from them:

- `eval-31-travel-insurance-py` — the baseline never ran this eval; there is nothing to compare against

**1 assertion verdict moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `scenario-names-describe-conditions` — eval-32-baggage-fees-py

Grader said: _pytest.param(23, 0, id="at the no-fee limit") paraphrases the fee_eur=0 expectation as 'no-fee'_

- Output: `eval-32-baggage-fees-py/outputs/`
- Narration: `eval-32-baggage-fees-py/narration.md`
- Raw transcript: `eval-32-baggage-fees-py/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
