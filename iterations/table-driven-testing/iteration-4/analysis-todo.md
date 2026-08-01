# Analysis to-do — table-driven-testing, iteration 4

Compared against **iteration 3**, grading claude-sonnet-5/default.

> ⚠️ **The baseline was graded under a different regime** (claude-haiku-4-5/default vs claude-sonnet-5/default).
> A comparison is void across a change of grading regime — re-baseline rather than interpret this.

**5 of 5 evals comparable.**

**3 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `minimal-rows-per-concern` — eval-31-travel-insurance-py

Grader said: _test_restricted_destination... stacks age×trip_days×clearance = 8 cross-product cases; test_long_trip... stacks age×clearance = 6 cases, more than needed to show override._

- Output: `eval-31-travel-insurance-py/outputs/`
- Narration: `eval-31-travel-insurance-py/narration.md`
- Raw transcript: `eval-31-travel-insurance-py/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `threshold-as-column` — eval-31-travel-insurance-py

Grader said: _Rows only have age, has_medical_clearance, decision — no explicit threshold column value like 'age_limit=70' shown alongside age._

- Output: `eval-31-travel-insurance-py/outputs/`
- Narration: `eval-31-travel-insurance-py/narration.md`
- Raw transcript: `eval-31-travel-insurance-py/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `concrete-domain-values` — eval-34-hotel-cancellation-swift

Grader said: _(daysBeforeCheckIn: 15, bookingValue: 100, expectedFee: 50), (daysBeforeCheckIn: 3, bookingValue: 250, expectedFee: 200)_

- Output: `eval-34-hotel-cancellation-swift/outputs/`
- Narration: `eval-34-hotel-cancellation-swift/narration.md`
- Raw transcript: `eval-34-hotel-cancellation-swift/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
