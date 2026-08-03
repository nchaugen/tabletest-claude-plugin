# Analysis to-do — table-driven-testing, iteration 8

Compared against **iteration 7**, grading claude-sonnet-5/default.

**5 of 5 evals comparable.**

**1 assertion verdict moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `no-duplicate-rows-within-a-table` — eval-31-travel-insurance-py

Grader said: _test_declines_restricted_destinations... uses a full 2x2x2 cross product (age{30,80} x trip_days{30,120} x clearance{True,False}) all expecting DECLINED, which is the exact 'full cross-product' excess pattern the assertion flags as duplicative; the other two tests (age/clearance and trip-length) show distinct boundary cases and PASS._

- Output: `eval-31-travel-insurance-py/outputs/`
- Narration: `eval-31-travel-insurance-py/narration.md`
- Raw transcript: `eval-31-travel-insurance-py/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
