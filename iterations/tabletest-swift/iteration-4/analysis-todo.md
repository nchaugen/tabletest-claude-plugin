# Analysis to-do — tabletest-swift, iteration 4

Compared against **iteration 3**, grading claude-sonnet-5-5/default.

**4 of 4 evals comparable.**

**1 assertion verdict moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `no-branch-on-table-enum-outside-it` — eval-44-ballot-box-replacement

Grader said: _Tests/PollingTests/BallotBoxTests.swift: box branches on the test-local enum Poll (if/guard on poll) outside enum Poll; Tests/PollingTests/BallotBoxTests.swift: box branches on the test-local enum Ballot (if/guard on ballot) outside enum Ballot_

- Output: `eval-44-ballot-box-replacement/outputs/`
- Narration: `eval-44-ballot-box-replacement/narration.md`
- Raw transcript: `eval-44-ballot-box-replacement/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): a real fault, the F3 shape. `static func box(poll:vote:ballot:otherVotes:)` takes both test-local enums and branches with `if ballot == .sealed` and `if poll == .closed`; it also has `vote.map` and a loop. Draw 1 (iteration-3) put the same branches in `apply(to:)` members of each enum. Not a checker misfire: the slot flips between draws on the answer, not on the grader. 
