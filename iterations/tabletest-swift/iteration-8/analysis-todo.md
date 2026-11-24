# Analysis to-do — tabletest-swift variant=next, iteration 3

Compared against **official (iterations 7, 6, 5, 4, 3, 2, 1 merged)**, grading claude-sonnet-5-5/default.

**1 of 1 evals comparable.**

**2 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `outcome-cell-spelled-as-swift` — eval-38-washing-machine-cells

Grader said: _Outcome? cells read `runs(minutes: 170)` and `tooHot(limit: 90)`; WashOutcome gained Codable; no test-side mapping._

- Output: `eval-38-washing-machine-cells/outputs/`
- Narration: `eval-38-washing-machine-cells/narration.md`
- Raw transcript: `eval-38-washing-machine-cells/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `program-rows-state-only-what-they-set` — eval-38-washing-machine-cells

Grader said: _Columns design: tables 1-2 give only Fabric and Temperature; the body builds `WashProgram(fabric:temperature:)` with defaults._

- Output: `eval-38-washing-machine-cells/outputs/`
- Narration: `eval-38-washing-machine-cells/narration.md`
- Raw transcript: `eval-38-washing-machine-cells/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
