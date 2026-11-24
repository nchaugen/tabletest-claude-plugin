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