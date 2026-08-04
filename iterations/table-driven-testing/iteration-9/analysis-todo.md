# Analysis to-do — table-driven-testing, iteration 9

Compared against **iteration 8**, grading claude-sonnet-5/default.

**1 of 1 evals comparable.**

**1 assertion verdict moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `no-duplicate-rows-within-a-table` — eval-31-travel-insurance-py

Grader said: _Each of the three tables' cases discharge distinct branches (boundary, over-boundary with different age/clearance combos, and below/at age threshold with clearance variance) rather than repeating a linear walk-up; no case merely re-confirms an already-established direction._

- Output: `eval-31-travel-insurance-py/outputs/`
- Narration: `eval-31-travel-insurance-py/narration.md`
- Raw transcript: `eval-31-travel-insurance-py/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **J1's illustration half, and for the right reason.** iteration-8 wrote a full
  2×2×2 stack (age × trip_days × clearance) all expecting DECLINED. iteration-9 writes the
  restricted-destination rule as **two paired cases** — `pytest.param(30, 45, False, id="young
  applicant on a short trip")` and `pytest.param(75, 120, False, id="older applicant on a long
  trip")` — so the two inputs this rule ignores vary *together* in one case list. That is the shape
  `shared/table-design/examples/table-driven-testing/08` was rewritten to, and both the pytest and
  Swift mechanics passages now point at the single statement instead of recommending the stack again.
  Checked against the alternative reading: this is not merely a shorter file. The other two tests keep
  the cases the rule does need (trip-length boundary 90/91, age boundary 69/70 with a value-set pair
  under 70), so the count fell only where the product was carrying no claim.
