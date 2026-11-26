# Analysis to-do — tabletest-swift, iteration 22

Compared against **iteration 21**, grading claude-sonnet-5-5/default.

**10 of 10 evals comparable.**

**2 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `price-tests-become-one-table` — eval-36-bowling-lanes-convert

Grader said: _Table has six rows; the 5 h row was replaced by "standard | 3" and "standard | 4" rows._

- Output: `eval-36-bowling-lanes-convert/outputs/`
- Narration: `eval-36-bowling-lanes-convert/narration.md`
- Raw transcript: `eval-36-bowling-lanes-convert/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): draw 2 replaced the original 5-hour case with 3 and 4 hours to straddle the three-hour limit (narration 13, 34: "the old 5-hour case is no longer tested"), so the table has six rows and loses one original case. The boundary obligation applied inside a conversion, in *Converting Existing Tests*, which step 5 did not touch. Draw 1 (iteration-21) kept the five rows; the slot was P in all 6 earlier draws. 1 of 2 on the new skill: watch, not attributable from one draw. Side note: both draws leave the limit out of the columns, as every stored draw did; draw 2's reason is new ("the app has no constants to check them against"), which reads step 4's *A Threshold the Code Holds* as "only a constant earns a column" (rule 17 says otherwise).

## WON `irrelevant-input-is-a-value-set` — eval-39-playlist-screen-columns

Grader said: _Library order is "{recentlyAdded, artist}"; unread rules "{0, 1}" where actions are identical_

- Output: `eval-39-playlist-screen-columns/outputs/`
- Narration: `eval-39-playlist-screen-columns/narration.md`
- Raw transcript: `eval-39-playlist-screen-columns/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): draw 1 (iteration-21) repeated an input that does not change the actions; draw 2 used a value set. The slot flips: P F P F F P P F P across the stored draws, so this WON is the base rate, not a change.
