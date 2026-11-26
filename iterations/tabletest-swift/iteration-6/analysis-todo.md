# Analysis to-do — tabletest-swift, iteration 6

Compared against **iteration 5**, grading claude-sonnet-5-5/default.

## ⚠️ Partial comparison

**8 of 9 evals comparable.** The rest were excluded from them:

- `eval-45-train-delay-notice` — generation failed (timeout or crash), so this eval produced no answer to compare — re-run it before reading anything into the gap

**4 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `outcome-cell-spelled-as-swift` — eval-38-washing-machine-cells

Grader said: _Outcome? column holds 'runs(minutes: 170)', 'tooHot(limit: 90)'; WashOutcome gained Codable._

- Output: `eval-38-washing-machine-cells/outputs/`
- Narration: `eval-38-washing-machine-cells/narration.md`
- Raw transcript: `eval-38-washing-machine-cells/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `irrelevant-input-is-a-value-set` — eval-39-playlist-screen-columns

Grader said: _"Rule added | 0 | adds a rule" and "Rule added, unread rule | 1 | adds a rule" differ in count; "Nothing changed | {0, 1}" ok, but order rows differ only in count_

- Output: `eval-39-playlist-screen-columns/outputs/`
- Narration: `eval-39-playlist-screen-columns/narration.md`
- Raw transcript: `eval-39-playlist-screen-columns/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `scenario-names-add-to-the-row` — eval-42-dog-daycare-admission

Grader said: _Names like "Youngest age, one vaccination" and "At 16 weeks, one vaccination" repeat the cells; "Puppy a week under 16" partly states a boundary._

- Output: `eval-42-dog-daycare-admission/outputs/`
- Narration: `eval-42-dog-daycare-admission/narration.md`
- Raw transcript: `eval-42-dog-daycare-admission/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `no-branch-on-table-enum-outside-it` — eval-44-ballot-box-replacement

Grader said: _Tests/PollingTests/BallotBoxTests.swift: box branches on the test-local enum Poll (if/guard on poll) outside enum Poll; Tests/PollingTests/BallotBoxTests.swift: box branches on the test-local enum Ballot (if/guard on ballot) outside enum Ballot_

- Output: `eval-44-ballot-box-replacement/outputs/`
- Narration: `eval-44-ballot-box-replacement/narration.md`
- Raw transcript: `eval-44-ballot-box-replacement/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
