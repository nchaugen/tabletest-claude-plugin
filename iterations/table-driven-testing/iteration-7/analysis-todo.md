# Analysis to-do — table-driven-testing, iteration 7

Compared against **iteration 5**, grading claude-sonnet-5/default.

**5 of 5 evals comparable.**

**3 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `no-duplicate-rows-within-a-table` — eval-31-travel-insurance-py

Grader said: _Each table's cases cover distinct boundaries/axes (age boundary+clearance interaction, 90/91-day boundary plus older-applicant precedence, four distinct destination-override scenarios) with no repeated obligation._

- Output: `eval-31-travel-insurance-py/outputs/`
- Narration: `eval-31-travel-insurance-py/narration.md`
- Raw transcript: `eval-31-travel-insurance-py/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): confirmed in `outputs/`. Each table's rows sit on distinct boundaries
  rather than repeating one obligation. Consistent with cluster 3's rule 03 extension (FIX 3), which
  tells the agent what sparse columns feed before it decides to add a row. Suite total 44/44 — the
  tripwire did not fire.

## WON `exception-case-separated` — eval-32-baggage-fees-py

Grader said: _def test_rejects_a_checked_bag_just_over_the_oversize_limit(): with pytest.raises(BagNotAccepted): fee_for_checked_bag(46)_

- Output: `eval-32-baggage-fees-py/outputs/`
- Narration: `eval-32-baggage-fees-py/narration.md`
- Raw transcript: `eval-32-baggage-fees-py/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): confirmed in `outputs/tests/test_baggage.py`. The five weight tiers are one
  `parametrize` table; the over-limit rejection is a separate `test_rejects_...` with
  `pytest.raises`. The strike test (rule 14) resolved correctly for an ecosystem with no `Throws?`
  column — note the JVM rendering of the same rule sends eval-14 the other way, to a `Throws?`
  column, and that also passed. The rule is reading correctly through both vocabularies.

## WON `exception-case-separated` — eval-34-hotel-cancellation-swift

Grader said: _func cancellationAfterStayHasStarted(daysBeforeCheckIn: Int) { #expect(throws: CancellationError.stayAlreadyStarted) { ... } }_

- Output: `eval-34-hotel-cancellation-swift/outputs/`
- Narration: `eval-34-hotel-cancellation-swift/narration.md`
- Raw transcript: `eval-34-hotel-cancellation-swift/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): confirmed in `outputs/`. Same strike-test resolution as eval-32 — the
  stay-already-started rejection is its own `@Test` with `#expect(throws:)`.
  **Worth carrying to the tabletest side:** this output also holds booking value constant at 1000
  *and adds a 2000 row* to demonstrate the fee stays proportional, with the reason in a comment.
  That is the behaviour eval-29 (tabletest) failed to produce, where the same claim of indifference
  went into the `@Description` with no varying row and lost `quantifier-covered-by-rows`. Same
  shared rule, opposite outcome — so the gap is salience in the JVM rendering, not a missing rule.
