# Analysis to-do — tabletest-swift variant=next, iteration 1

Compared against **official (iterations 4, 3, 2, 1 merged)**, grading claude-sonnet-5-5/default.

## ⚠️ Partial comparison

**8 of 9 evals comparable.** The rest were excluded from them:

- `eval-45-train-delay-notice` — generation failed (timeout or crash), so this eval produced no answer to compare — re-run it before reading anything into the gap

**4 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `outcome-cell-spelled-as-swift` — eval-38-washing-machine-cells

Grader said: _Refusal table uses a "Refused Above?" Int? column and a limitExceeded(by:) helper, not tooHot(limit: 40)_

- Output: `eval-38-washing-machine-cells/outputs/`
- Narration: `eval-38-washing-machine-cells/narration.md`
- Raw transcript: `eval-38-washing-machine-cells/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): PLAUSIBLE regression, one draw. Real fault: the refusal table projects `WashOutcome` to `Refused Above?: Int?` through `limitExceeded(by:)`, which branches with `if case`. The narration reasons from *Expressing a Rejection* ("blank when the wash is accepted"). This is the no-skill shape (no-skill draws 2 and 3); neither skill draw (iterations 1, 2) wrote it. The agent read the reference in full, and *Enums with Associated Values* ("an outcome with data is one cell") is still in SKILL.md, so the rule was present; the split moved *Expressing a Rejection* to sit after *Absent Values*. Needs draw 2.

## WON `irrelevant-input-is-a-value-set` — eval-39-playlist-screen-columns

Grader said: _Library: `{recentlyAdded, artist}` for order; playlist: `{read in full, with an unread rule}` where the unread rule is irrelevant._

- Output: `eval-39-playlist-screen-columns/outputs/`
- Narration: `eval-39-playlist-screen-columns/narration.md`
- Raw transcript: `eval-39-playlist-screen-columns/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): Real in this output: both tables use value sets for the ignored inputs (`{recentlyAdded, artist}`, `{read in full, with an unread rule}`). The baseline draw (iteration-2) failed it; iteration-1 passed it, so the slot flips between skill draws. Not attributable to the split. Note: this agent read the reference in part (lines ~20–620 and 891–1095 of a persisted `cat`).

## WON `scenario-names-add-to-the-row` — eval-42-dog-daycare-admission

Grader said: _"At the age a puppy becomes a dog" marks the 16-week boundary; "Puppy with a second vaccination" names the dose._

- Output: `eval-42-dog-daycare-admission/outputs/`
- Narration: `eval-42-dog-daycare-admission/narration.md`
- Raw transcript: `eval-42-dog-daycare-admission/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): Real: "At the age a puppy becomes a dog" names the 16-week line. This agent never called `Skill`; it read SKILL.md and the whole reference with `cat` from the worktree. Both baseline draws had the same design rules in SKILL.md, so the candidate cause is the new workflow step ("in the form of the other names") or variance. One draw.

## WON `no-branch-on-table-enum-outside-it` — eval-44-ballot-box-replacement

Grader said: _VACUOUS: the test files declare no enum with String raw values, so there was no branch to place._

- Output: `eval-44-ballot-box-replacement/outputs/`
- Narration: `eval-44-ballot-box-replacement/narration.md`
- Raw transcript: `eval-44-ballot-box-replacement/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): Not a win: VACUOUS. The answer declares no test-local enum with `String` raw values, so the checker had nothing to judge. Draw 1 of the baseline also passed. No evidence about F3.
