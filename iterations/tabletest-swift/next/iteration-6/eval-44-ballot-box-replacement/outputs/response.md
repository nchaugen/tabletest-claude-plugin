The tests are in `Tests/PollingTests/BallotBoxTests.swift`. They compile, and all 7 cases fail with `NotImplemented()`, as expected while the stub throws. `replaceVote` is still unimplemented.

**One change outside the test file.** In `Sources/Polling/BallotBox.swift` I added `Codable` to `Replacement` and `Refusal`. The tables write results as `refused(pollClosed)`, and TableTest can only read an enum with data if it is `Codable`. The change adds no behaviour. If you'd rather not touch the source, I can write a test-side conversion instead.

**Tests.** The rules are three independent conditions, so there is one table per condition. Each holds the other two at the value that allows replacement. Every table has the same columns:
- **Inputs:** poll state, vote on file, ballot state, new vote.
- **Checks:** the result, the voter's vote afterwards, and the tally afterwards.

| Test | Rows |
|---|---|
| `replacesAVoteOnlyWhileThePollIsOpen` | An open poll replaces the vote and the tally moves to `[no: 1]`. A closed poll returns `refused(pollClosed)`, with vote and tally unchanged. |
| `replacesAVoteOnlyWhereTheVoterHasOne` | The same accepted row. A voter with no vote gets `refused(noVoteToReplace)`, with no vote and an empty tally. The new vote is `{yes, no}`, so it runs once for each. |
| `replacesAVoteOnlyWhileTheBallotIsUnsealed` | The same accepted row. A sealed ballot returns `refused(ballotSealed)`, with vote and tally unchanged. |

**Assumptions:**
- **Several refusal reasons at once.** The rules don't say which one wins when more than one applies, for example a closed poll with a sealed ballot. No row combines reasons. Once you decide the precedence, add a table for it.
- **A single voter.** The box holds one voter, so the tally is just that voter's vote. A voter sealed without a vote is not tested, because of the same precedence question.
- **Replacing with the same choice.** I did not test it, because the rules don't say whether that counts as a replacement.

`swift build --build-tests` succeeds. The sandbox blocks swift-build's cache database and prints warnings about it, but they don't stop the build.