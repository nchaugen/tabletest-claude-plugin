# Analysis to-do — tabletest-swift variant=next, iteration 7

Compared against **official (iterations 9, 8, 7, 6, 5, 4, 3, 2, 1 merged)**, grading claude-sonnet-5-5/default.

**2 of 2 evals comparable.**

**2 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## WON `irrelevant-input-is-a-value-set` — eval-39-playlist-screen-columns

Grader said: _Library order and the unread count are fixed by the fixture comment; no two rows differ only in such an input._

- Output: `eval-39-playlist-screen-columns/outputs/`
- Narration: `eval-39-playlist-screen-columns/narration.md`
- Raw transcript: `eval-39-playlist-screen-columns/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): the published crossing text is back, and the agent crossed again: "screen-by-change combinations" (narration line 11), the new combination "removing every rule … new for that screen" (line 21), then grouped by answer into value sets (line 52). The library's order and the unread rule went into value sets, which is what this slot asks. Crossing slot P.

## WON `no-branch-on-table-enum-outside-it` — eval-44-ballot-box-replacement

Grader said: _2 test-local enum(s); every branch on one is a member of that enum_

- Output: `eval-44-ballot-box-replacement/outputs/`
- Narration: `eval-44-ballot-box-replacement/narration.md`
- Raw transcript: `eval-44-ballot-box-replacement/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): the "that cell's own type" fix fired. `Poll.apply(to:)` holds only `if self == .closed`, and `Ballot.apply(to:for:)` holds only `if self == .sealed`; no member branches on another enum's cell, unlike next/iteration-5.
