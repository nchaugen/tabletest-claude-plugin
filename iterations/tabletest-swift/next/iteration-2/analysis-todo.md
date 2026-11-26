# Analysis to-do — tabletest-swift variant=next, iteration 2

Compared against **official (iterations 9, 8, 7, 6, 5, 4, 3, 2, 1 merged)**, grading claude-sonnet-5-5/default.

**3 of 3 evals comparable.**

**2 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `crossing-the-inputs-finds-missing-rows` — eval-39-playlist-screen-columns

Grader said: _No row pairs "a playlist with an unread rule" with "removes every rule"; response says "has no row"_

- Output: `eval-39-playlist-screen-columns/outputs/`
- Narration: `eval-39-playlist-screen-columns/narration.md`
- Raw transcript: `eval-39-playlist-screen-columns/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): item 6 removed the operation. The baseline narration (iteration-7, line 48) crosses the two inputs and adds `unread rule × removes every rule`. The variant's section keeps only the limits ("not crossed", "has no row"), and the agent wrote "I kept the old test's 10 cases and added no new combinations" (narration line 42). Five passes before, so this is a regression of item 6, not noise.

## LOST `edit-columns-are-the-deciding-inputs` — eval-39-playlist-screen-columns

Grader said: _Columns are "Opened From" (a="a playlist with an unread rule") and "Change"; the unread-rules count is folded into Opened From_

- Output: `eval-39-playlist-screen-columns/outputs/`
- Narration: `eval-39-playlist-screen-columns/narration.md`
- Raw transcript: `eval-39-playlist-screen-columns/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): not a flip; the regrade (`grading-regrade1.json`) fails it again. The column design is unchanged from iteration-7, which passed: both fold the unread rule into `Opened From` (`playlist with an unread rule`, with `unreadRules: 1` inside the enum). The assertion asks for "the unread rules" as an input, and the grader reads the same shape both ways. The visible difference is the variant's scenario column, whose names open with "Unread rule". Not attributable to the variant text; an assertion candidate (ambiguous on a situation enum that holds the input).
