# Narration — 44

The agent's visible narration, thinking cost and file writes, in order, distilled from
`conversation.jsonl`. Thinking text appears when the run asked the CLI for a summary
(`thinking_display: "summarized"`); the per-block token counts appear either way.

_[~176 thinking tokens (8 deltas)]_

> (thinking) I need to read the table-design reference file in full, which should be at skills/tabletest-swift/references/table-design.md in the work directory.

_[~1,147 thinking tokens (160 deltas)]_

> (thinking) I'm settling on the design: each rule should cover one concern, with the replacement outcome and the state afterward kept together. The rejection becomes an expected column, so I'm weighing `replaced` against `refused(pollClosed)`. The open question is Codable, since Replacement is only Equatable right now and the enum's associated values need it to decode.
> 
> I'm picking the column layout for the test table: poll state, whether the voter already voted, ballot sealed or not, and the new vote. I'm also checking whether a Codable conformance on the app type is acceptable here, and planning to add another voter's vote to the fixture so the tally covers more than one entry.
> 
> I'm leaving the refusal precedence open, since the spec doesn't say which refusal wins when several apply. I'll only assert cases where one condition applies at a time, and note the gap in the doc comment. Next comes a table of rows: an open poll with a yes vote swapped to no, a same-choice replacement, and a closed poll that refuses.
> 
> I'm setting up the test table with a no-to-yes direction as well, plus a case where the voter's previous vote is blank, so the refusal rows each differ from what was there before. For the closed-poll case, the voter had already voted yes and the new vote is no. I'm also leaving out any assertion that combines two refusal conditions, since the precedence isn't specified, and I'll define the poll and ballot states as local enums inside the test.
> 
> I'm writing the test helper next: a static fixture function that builds the box from the poll, prior vote, and seal state, so the test body stays free of branching. I'm also making Replacement Codable so the enum's associated values can decode, and I'm leaving the stub change as small as possible.
> 
> I'm making Refusal and Replacement both Codable, since the enum's associated values need it for decoding. I'm also setting the tally as a map keyed by Choice, with the voter "ana" and the other voter "bea" in the fixture.

_[~192 thinking tokens (15 deltas)]_

> (thinking) The sed edit didn't take because BSD sed needs an empty string after -i. The tests compile, so what's left is adding the Codable conformance to the stub, which is a small change and not a logic problem.

The tests are written in `Tests/PollingTests/BallotBoxTests.swift`. They compile, and all 6 cases fail with `NotImplemented()`, as you expected while the stub is unimplemented.

**What's in it:** one `@TableTest`, `replacesAVoteOrRefusesWithoutChangingAnything`. Each row sets up a box, calls `replaceVote`, then checks three things: the returned `Replacement`, the voter's vote afterwards, and the tally afterwards.

| Case | Expected |
|---|---|
| Open poll, yes→no | `replaced`, tally `[no: 2]` |
| Open poll, no→yes | `replaced`, tally `[yes: 1, no: 1]` |
| Open poll, same vote again | `replaced`, tally unchanged |
| Poll closed after voting | `refused(pollClosed)`, vote and tally unchanged |
| Voter has not voted | `refused(noVoteToReplace)`, still no vote, tally `[no: 1]` |
| Ballot sealed | `refused(ballotSealed)`, vote and tally unchanged |

**Change to the source:** I added `Codable` to `Refusal` and `Replacement` in `BallotBox.swift` so the table can write outcomes like `refused(pollClosed)`. It's conformance only. `replaceVote` is still the stub.

**Assumptions:**
- A second voter, "bea", has voted no in every row. This shows a refusal or replacement leaves other voters' votes and the tally alone.
- Each refusal row has only one reason to refuse. The rules don't say which refusal wins when several apply, such as closed and sealed together. No row combines them, and the doc comment says so. If you want a precedence, it needs its own rows.
- Replacing a vote with the same choice counts as `replaced`.

