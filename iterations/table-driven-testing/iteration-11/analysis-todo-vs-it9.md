# Analysis to-do — table-driven-testing, iteration 11

Compared against **iteration 9**, grading claude-sonnet-5/default.

## ⚠️ Partial comparison

**1 of 5 evals comparable.** The rest were excluded from them:

- `eval-32-baggage-fees-py` — the baseline never ran this eval; there is nothing to compare against
- `eval-33-library-fees-py` — the baseline never ran this eval; there is nothing to compare against
- `eval-34-hotel-cancellation-swift` — the baseline never ran this eval; there is nothing to compare against
- `eval-35-cinema-tickets-routing` — the baseline never ran this eval; there is nothing to compare against

**2 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `precedence-visible` — eval-31-travel-insurance-py

Grader said: _Only shows >90-day override when age/clearance would approve (id='just over the limit, senior applicant with clearance'); no case shows override when age/clearance would decline (senior without clearance, trip>90)._

- Output: `eval-31-travel-insurance-py/outputs/`
- Narration: `eval-31-travel-insurance-py/narration.md`
- Raw transcript: `eval-31-travel-insurance-py/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `threshold-as-column` — eval-31-travel-insurance-py

Grader said: _Parametrize columns are only ('trip_days','age','has_medical_clearance','decision') etc; no explicit threshold value column, thresholds only implied via chosen boundary values like 90/91 or 69/70._

- Output: `eval-31-travel-insurance-py/outputs/`
- Narration: `eval-31-travel-insurance-py/narration.md`
- Raw transcript: `eval-31-travel-insurance-py/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
