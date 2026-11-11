# Analysis to-do — tabletest-swift variant=next, iteration 2

Compared against **iteration 1**, grading claude-sonnet-5-5/default.

**6 of 6 evals comparable.**

**6 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `irrelevant-input-is-a-value-set` — eval-39-playlist-screen-columns

Grader said: _'the library | no change' and 'the library | changes the order' both give 'false | []'_

- Output: `eval-39-playlist-screen-columns/outputs/`
- Narration: `eval-39-playlist-screen-columns/narration.md`
- Raw transcript: `eval-39-playlist-screen-columns/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): The table keeps `the library | no change` and `the library | changes the order` as two rows with `false | []`. The narration (line 21) collapses value sets for the playlist rows only; the library rows stay one-to-one with the original cases, which had "the library, another order" as its own case. Draw 1 merged them. A flip on this slot: 1 of 2 skill draws pass, 0 of 2 no-skill draws.

## WON `fee-cases-are-table-rows` — eval-41-hotel-cancellation-routing

Grader said: _@TableTest(""" Scenario | Days Before Check-in | Booking Value (EUR) | Fee (EUR)? ... func chargesTheFeeForTheDaysRemaining_

- Output: `eval-41-hotel-cancellation-routing/outputs/`
- Narration: `eval-41-hotel-cancellation-routing/narration.md`
- Raw transcript: `eval-41-hotel-cancellation-routing/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): Routing. The agent loaded `table-driven-testing` first, read its new "Swift with TableTest: stop here" line (narration line 9: "The table-driven-testing skill points to it too"), then loaded `tabletest-swift` and wrote a `@TableTest` inside a struct with a `Fee (EUR)?` column and a branch-free body. The draft's description did not win the first pick; the exclusion in `table-driven-testing`'s body (plugin `5ef5f5d`) made the switch.

## WON `has-question-mark-column` — eval-41-hotel-cancellation-routing

Grader said: _Question-mark columns: Fee (EUR)?_

- Output: `eval-41-hotel-cancellation-routing/outputs/`
- Narration: `eval-41-hotel-cancellation-routing/narration.md`
- Raw transcript: `eval-41-hotel-cancellation-routing/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): Routing. The agent loaded `table-driven-testing` first, read its new "Swift with TableTest: stop here" line (narration line 9: "The table-driven-testing skill points to it too"), then loaded `tabletest-swift` and wrote a `@TableTest` inside a struct with a `Fee (EUR)?` column and a branch-free body. The draft's description did not win the first pick; the exclusion in `table-driven-testing`'s body (plugin `5ef5f5d`) made the switch.

## WON `has-tabletest-annotation` — eval-41-hotel-cancellation-routing

Grader said: _Found @TableTest annotation_

- Output: `eval-41-hotel-cancellation-routing/outputs/`
- Narration: `eval-41-hotel-cancellation-routing/narration.md`
- Raw transcript: `eval-41-hotel-cancellation-routing/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): Routing. The agent loaded `table-driven-testing` first, read its new "Swift with TableTest: stop here" line (narration line 9: "The table-driven-testing skill points to it too"), then loaded `tabletest-swift` and wrote a `@TableTest` inside a struct with a `Fee (EUR)?` column and a branch-free body. The draft's description did not win the first pick; the exclusion in `table-driven-testing`'s body (plugin `5ef5f5d`) made the switch.

## WON `no-if-switch-in-method` — eval-41-hotel-cancellation-routing

Grader said: _1 method(s) checked — no if/switch/ternary_

- Output: `eval-41-hotel-cancellation-routing/outputs/`
- Narration: `eval-41-hotel-cancellation-routing/narration.md`
- Raw transcript: `eval-41-hotel-cancellation-routing/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): Routing. The agent loaded `table-driven-testing` first, read its new "Swift with TableTest: stop here" line (narration line 9: "The table-driven-testing skill points to it too"), then loaded `tabletest-swift` and wrote a `@TableTest` inside a struct with a `Fee (EUR)?` column and a branch-free body. The draft's description did not win the first pick; the exclusion in `table-driven-testing`'s body (plugin `5ef5f5d`) made the switch.

## WON `tabletest-function-in-a-type` — eval-41-hotel-cancellation-routing

Grader said: _1 @TableTest function(s), each inside a type_

- Output: `eval-41-hotel-cancellation-routing/outputs/`
- Narration: `eval-41-hotel-cancellation-routing/narration.md`
- Raw transcript: `eval-41-hotel-cancellation-routing/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): Routing. The agent loaded `table-driven-testing` first, read its new "Swift with TableTest: stop here" line (narration line 9: "The table-driven-testing skill points to it too"), then loaded `tabletest-swift` and wrote a `@TableTest` inside a struct with a `Fee (EUR)?` column and a branch-free body. The draft's description did not win the first pick; the exclusion in `table-driven-testing`'s body (plugin `5ef5f5d`) made the switch.
