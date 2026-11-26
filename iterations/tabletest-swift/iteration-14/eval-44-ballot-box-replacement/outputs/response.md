I wrote the vote-replacement tests in `Tests/PollingTests/BallotBoxTests.swift`. They compile, and all 5 rows fail because `replaceVote` is still the unimplemented stub, which throws `NotImplemented`. I didn't check the failure messages to confirm that's the only cause.

**The table**

It's a single `@TableTest`, `replacesAVoteOnlyInAnOpenPollForAVoterWithAnUnsealedVote`. Each row gives the poll status, the voter's vote, whether the ballot is sealed, the other voters' votes and the new choice. It then checks three outcomes: the result, the voter's vote afterwards, and the tally afterwards.

| Scenario | Expected result |
|---|---|
| Change of mind among other voters | `replaced`; the vote changes and the tally moves |
| Change of mind by the only voter | `replaced`; the old choice drops out of the tally |
| After the deadline | `refused(pollClosed)`; vote and tally unchanged |
| Voter yet to vote | `refused(noVoteToReplace)`; vote and tally unchanged |
| Voter who signed off | `refused(ballotSealed)`; vote and tally unchanged |

The two accepted rows are in the same table as the refusals. They show that the vote and tally do change when a replacement is accepted, so the "unchanged" checks on the refused rows mean something.

**Change outside the test file:** I added `Codable` to `Refusal` and `Replacement` in `BallotBox.swift` so a table cell can read `refused(pollClosed)`. That doesn't add any behaviour, and `replaceVote` is still the stub.

**Assumptions** (also written in the test's doc comment):
- **Combined reasons:** no row combines two refusal reasons, such as a closed poll with a sealed ballot, because your rules don't say which reason wins.
- **Same choice:** no row replaces a vote with the same choice, because your rules don't say whether that counts as `replaced`.
- **Refusals aren't thrown:** a refusal is returned as `.refused(...)`, not thrown, because the `Replacement` type has a `refused` case. The test is marked `throws` only because the stub is.

If you settle either open question, each is one more row.