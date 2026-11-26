# Analysis to-do — tabletest-swift variant=next, iteration 8

Compared against **official (iterations 9, 8, 7, 6, 5, 4, 3, 2, 1 merged)**, grading claude-sonnet-5-5/default.

**2 of 2 evals comparable.**

**2 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `irrelevant-input-is-a-value-set` — eval-39-playlist-screen-columns

Grader said: _Library order shown as value set: `{no change, changes the order}` with the same `[]` expectation._

- Output: `eval-39-playlist-screen-columns/outputs/`
- Narration: `eval-39-playlist-screen-columns/narration.md`
- Raw transcript: `eval-39-playlist-screen-columns/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): the agent worked each screen against each change (narration lines 9, 17: "whether the playlist-with-unread-rule rows still hold when the order changes … Removing every rule leaves only the revert option"), with no word "cross". The table has the same three rows for each playlist screen, including `Unread rule, every rule removed`, and the library's order sits in `{no change, changes the order}`. Crossing slot P.

## WON `no-branch-on-table-enum-outside-it` — eval-44-ballot-box-replacement

Grader said: _2 test-local enum(s); every branch on one is a member of that enum_

- Output: `eval-44-ballot-box-replacement/outputs/`
- Narration: `eval-44-ballot-box-replacement/narration.md`
- Raw transcript: `eval-44-ballot-box-replacement/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): the fix held a second time. `PollStatus.apply(to:)` and `BallotState.apply(to:for:)` each branch only on `self`; no member branches on another enum's cell.
