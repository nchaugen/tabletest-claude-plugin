# Analysis to-do — table-driven-testing variant=next, iteration 1

Compared against **official (iterations 4, 3 merged)**, grading claude-sonnet-5/default.

**5 of 5 evals comparable.**

**3 assertion verdicts moved.**

> **Verdict on this run (2026-08-01): the two losses are correct and share one cause — rule 14's
> wording, not the shared core's length.** Net 42/44 -> 41/44. The `threshold-as-column` win is the
> shared core firing as intended. Rule 14 now carries a decidable test; that fix is **unmeasured**.


These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `threshold-as-column` — eval-31-travel-insurance-py

Grader said: _pytest.param(69, ...), pytest.param(70, ...) and trip_days 90 vs 91 show the threshold value directly as the applicant's actual value_

- Output: `eval-31-travel-insurance-py/outputs/`
- Narration: `eval-31-travel-insurance-py/narration.md`
- Raw transcript: `eval-31-travel-insurance-py/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): the shared core's *Make Thresholds Visible* fired. The output puts the applicant's actual value at 69 and 70, and trip length at 90 and 91, so both boundaries are visible in the rows rather than buried in the code. This is the rule doing what it was extracted to do.

## LOST `exception-case-separated` — eval-32-baggage-fees-py

Grader said: _pytest.param(46, None, BagNotAccepted, id=...) mixed into same table; def _fee_or_error(weight_kg): try: ... except Exception as error: return None, type(error) — branching, not a separate pytest.raises test_

- Output: `eval-32-baggage-fees-py/outputs/`
- Narration: `eval-32-baggage-fees-py/narration.md`
- Raw transcript: `eval-32-baggage-fees-py/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **real loss, and the assertion is right.** Read `outputs/tests/test_baggage.py`: one parametrized test carries the whole fee ladder (23, 24, 32, 33, 45) *plus* the 46 kg rejection. That is rejection as one outcome among several, which rule 14 says is two concerns — but the agent read the carve-out's phrase "the last accepted value beside the first rejected one" as licensing a rejection row appended to a ladder, because a ladder does contain such a pair. **Cause is rule 14's wording, not the assertion and not the core's length.** Fixed by giving the rule a decidable test (strike the rejected rows; if a rule remains, split), which is untested as of this run.

## LOST `exception-case-separated` — eval-34-hotel-cancellation-swift

Grader said: _plus the accept→reject boundary (0 vs -1 days) folded into the same table ... using a fee/error column pair rather than a separate throwing test._

- Output: `eval-34-hotel-cancellation-swift/outputs/`
- Narration: `eval-34-hotel-cancellation-swift/narration.md`
- Raw transcript: `eval-34-hotel-cancellation-swift/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): **same cause as eval-32, same defect.** `CancellationFeeTests.swift` puts the tier ladder (30, 29, 7, 6, 1, 0) and the -1 rejection in one `arguments:` list with a `fee`/`error` column pair. Strike the rejection and the ladder still states the tiers, so it is two concerns. Not a length effect and not a stale assertion.
