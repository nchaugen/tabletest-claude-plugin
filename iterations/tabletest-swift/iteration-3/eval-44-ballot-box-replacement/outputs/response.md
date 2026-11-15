I wrote the tests in `Tests/PollingTests/BallotBoxTests.swift`. They compile, and all 5 cases fail with `NotImplemented()`, as you wanted. `replaceVote` is still the unimplemented stub.

**Table.** It's one `@TableTest`, `replacesOrRefusesAVoteReplacement`. The fixture has voter `ana` replacing her vote and voter `ben` holding a "no" vote throughout. Each row checks three things: the returned `Replacement`, ana's vote afterwards, and the tally afterwards.

| Scenario | Result | Vote after | Tally after |
|---|---|---|---|
| Open poll, changed mind (yes → no) | `replaced` | no | `[no: 2]` |
| Open poll, same choice again | `replaced` | yes | `[yes: 1, no: 1]` |
| Poll closed after voting | `refused(pollClosed)` | yes | `[yes: 1, no: 1]` |
| Voter has not voted | `refused(noVoteToReplace)` | none | `[no: 1]` |
| Voter has sealed the ballot | `refused(ballotSealed)` | yes | `[yes: 1, no: 1]` |

The refused rows show the vote and tally unchanged, which covers your third rule. The "changed mind" row leaves "yes" out of the tally, because a choice with no votes isn't listed.

**Change outside the test file.** In `Sources/Polling/BallotBox.swift` I added `Codable` to `Refusal` and `Replacement`. Without it, a cell like `refused(pollClosed)` can't be read as a `Replacement`. It adds no behaviour and the stub is untouched. My first attempt at this edit failed silently, which is why the first test run showed conversion errors.

**Assumption.** You didn't say which refusal wins when several apply at once, for example a closed poll with a sealed ballot. I left those combinations out so the tests don't decide it, and noted this in the doc comment. If you want a fixed order, say what it is and I'll add rows for it.