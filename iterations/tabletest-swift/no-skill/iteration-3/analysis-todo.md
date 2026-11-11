# Analysis to-do — tabletest-swift variant=no-skill, iteration 3

Compared against **iteration 2**, grading claude-sonnet-5-5/default.

**6 of 6 evals comparable.**

**2 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `computed-arguments-stay` — eval-37-ev-charging-arguments

Grader said: _Comment: "`allCases` picks up new chargers automatically." Kept @Test(arguments: Charger.allCases)_

- Output: `eval-37-ev-charging-arguments/outputs/`
- Narration: `eval-37-ev-charging-arguments/narration.md`
- Raw transcript: `eval-37-ev-charging-arguments/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `full-tier-enumeration` — eval-41-hotel-cancellation-routing

Grader said: _Rows 30,29,7,6,1,0 present, but the 80% tier is wrong: 6 days with 250 gives fee 200 (80%) OK; yet 1 day 250→200 fine; the 50% tier is covered_

- Output: `eval-41-hotel-cancellation-routing/outputs/`
- Narration: `eval-41-hotel-cancellation-routing/narration.md`
- Raw transcript: `eval-41-hotel-cancellation-routing/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
