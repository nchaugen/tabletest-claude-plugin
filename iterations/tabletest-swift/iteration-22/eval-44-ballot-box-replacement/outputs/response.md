The tests are written in `Tests/PollingTests/BallotBoxTests.swift`. They compile and parse, and they fail as intended: all 5 rows hit `NotImplemented()` because `replaceVote` is still a stub.

There is one `@TableTest`, `replacesAVoteOnlyWhileTheBoxAllowsIt`. I kept accepted and refused cases in one table so a row shows the vote and tally changing when a replacement is accepted. The 5 rows:

| Scenario | Poll | Vote Before | Ballot | New Vote | Replacement? | Vote After? | Tally After? |
|---|---|---|---|---|---|---|---|
| Change of mind during the poll | open | yes | unsealed | no | `replaced` | no | `[yes: 1, no: 1]` |
| Same answer again during the poll | open | yes | unsealed | yes | `replaced` | yes | `[yes: 2]` |
| Change after the deadline | closed | yes | unsealed | no | `refused(pollClosed)` | yes | `[yes: 2]` |
| Change by someone who abstained | open | no vote | unsealed | no | `refused(noVoteToReplace)` | blank | `[yes: 1]` |
| Change after confirmation | open | yes | sealed | no | `refused(ballotSealed)` | yes | `[yes: 2]` |

Each refused row asserts the vote and tally are unchanged.

**One change outside the tests.** I added `Codable` to `Refusal` and `Replacement` in `Sources/Polling/BallotBox.swift`, so a cell can write `refused(pollClosed)`. It is synthesised, so it adds no behaviour, and `replaceVote` still throws `NotImplemented`.

**Assumptions:**
- **Fixture:** the voter is "Ada". A second voter, "Ben", has voted yes and never replaces, so the tally shows whether a replacement or refusal touches anyone else. This is in the doc comment.
- **Precedence:** the rules don't say which refusal wins when several apply, for example a closed poll with a sealed ballot. Each refused row has exactly one reason, so none of them pins an order.
- **Same choice:** replacing a vote with the same choice counts as `replaced`, not a refusal.
- **Setup order:** the fixture casts the vote, then seals it, then closes the poll. That order is fixed in the test.

I only ran `swift build --build-tests` and `swift test`; I didn't run the table formatter.