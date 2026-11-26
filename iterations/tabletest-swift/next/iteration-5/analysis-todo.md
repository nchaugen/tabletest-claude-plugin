# Analysis to-do — tabletest-swift variant=next, iteration 5

Compared against **official (iterations 9, 8, 7, 6, 5, 4, 3, 2, 1 merged)**, grading claude-sonnet-5-5/default.

**4 of 4 evals comparable.**

**1 assertion verdict moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `crossing-the-inputs-finds-missing-rows` — eval-39-playlist-screen-columns

Grader said: _Unread-rule rows only cover "no change" and "adds a rating rule"; no row removes every rule there. Response: "no order change on the unread-rule playlist"._

- Output: `eval-39-playlist-screen-columns/outputs/`
- Narration: `eval-39-playlist-screen-columns/narration.md`
- Raw transcript: `eval-39-playlist-screen-columns/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): the agent did not cross the inputs. The narration has no crossing step and ends "the playlist and unread-rule rows cover the same cases as before … no order change on the unread-rule playlist" (line 49). The restored operation is in the reference, but the variant crossed in 1 of 3 draws (next/iteration-2 F, -3 P, -5 F) against 5 of 5 baseline draws that all name a crossing step in their narration. The remaining difference in the crossing text is item 6's limits, in the reference and in the "Crossed inputs" check. Separately, eval-44 in this iteration failed `no-branch-on-table-enum-outside-it` with no verdict move against the official F: `Poll.box(holding:ballot:of:)` branches on `Ballot`, a member of the wrong enum. The rule says "a member of the cell type or of the test-local enum", and the agent read "the test-local enum" as any one.
