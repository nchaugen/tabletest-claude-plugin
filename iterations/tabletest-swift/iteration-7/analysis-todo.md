# Analysis to-do — tabletest-swift, iteration 7

Compared against **iteration 6**, grading claude-sonnet-5-5/default.

**9 of 9 evals comparable.**

**3 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `outcome-cell-spelled-as-swift` — eval-38-washing-machine-cells

Grader said: _Refusal table uses "Refusal Limit (°C)?" with 90/60/40 and a refusalLimit helper, not tooHot(limit: 40)._

- Output: `eval-38-washing-machine-cells/outputs/`
- Narration: `eval-38-washing-machine-cells/narration.md`
- Raw transcript: `eval-38-washing-machine-cells/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): A real fault, the draw 1 shape again (2 of 3 split draws, 0 of 2 before the split). The refusal table expects `Refusal Limit (°C)?` (blank, or 90/60/40) through a private `refusalLimit` projection on `WashOutcome`, not `tooHot(limit: 90)`. The narration follows *Expressing a Rejection* ("the column holds the error, and is blank where nothing is rejected") and reads the reference last, after the one-cell rule and its check in `SKILL.md`. Repair candidate: *Expressing a Rejection* states that a refusal with data is one cell as Swift spells it, never its data alone.

## LOST `program-rows-state-only-what-they-set` — eval-38-washing-machine-cells

Grader said: _Refusal table gives every row all five settings: "Prewash | Extra Rinse | Delay Start (h)", mostly false/blank._

- Output: `eval-38-washing-machine-cells/outputs/`
- Narration: `eval-38-washing-machine-cells/narration.md`
- Raw transcript: `eval-38-washing-machine-cells/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): A real fault, first in five draws. The narration settles "a shared column layout for every table", so the refusal table carries `Prewash`, `Extra Rinse` and `Delay Start (h)` that only one row sets. Cross-table consistency over-applied to the inputs a table does not vary. One draw: watch, no repair.

## WON `scenario-names-add-to-the-row` — eval-42-dog-daycare-admission

Grader said: _"Last week of puppyhood, one shot" and "First week past puppyhood" name boundary positions beyond cell values._

- Output: `eval-42-dog-daycare-admission/outputs/`
- Narration: `eval-42-dog-daycare-admission/narration.md`
- Raw transcript: `eval-42-dog-daycare-admission/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): Real: the new names ("Last week of puppyhood, one shot", "First week past puppyhood") name the boundary, not the cells. The slot flips (F F before, P F P since the split), so it is not attributed. This draw is the first split draw that called `Skill` and read no reference: peak context 35.5k against 52.6k, 110,407 tokens against 190,472 / 190,282.
