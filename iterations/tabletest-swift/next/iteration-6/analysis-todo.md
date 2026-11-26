# Analysis to-do — tabletest-swift variant=next, iteration 6

Compared against **official (iterations 9, 8, 7, 6, 5, 4, 3, 2, 1 merged)**, grading claude-sonnet-5-5/default.

**2 of 2 evals comparable.**

**1 assertion verdict moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `no-branch-on-table-enum-outside-it` — eval-44-ballot-box-replacement

Grader said: _2 test-local enum(s); every branch on one is a member of that enum_

- Output: `eval-44-ballot-box-replacement/outputs/`
- Narration: `eval-44-ballot-box-replacement/narration.md`
- Raw transcript: `eval-44-ballot-box-replacement/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): the item 1 rule fired. `Poll` and `Ballot` each hold their own branch in a member, and the test function calls them. Second variant draw of eval-44 on this variant; the first (next/iteration-5) failed, see its to-do.
