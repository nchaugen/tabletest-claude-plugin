I wrote the tests in `Tests/PollingTests/BallotBoxTests.swift`. They compile, and all 8 cases fail with `NotImplemented()`, which is the expected state while the stub is unimplemented. Every table cell converts, so the failures come from the stub and not from the table.

**The test.** There is one `@TableTest`, `replacesAVoteOnlyWhileThePollIsOpenAndTheBallotIsUnsealed`. Its five rows expand to 8 cases:

| Scenario | Expected |
|---|---|
| Change of mind while voting | `replaced`, the vote becomes `no`, the tally is `[no: 2]` |
| Only voter for the choice | `replaced`, the tally is `[no: 1]`, so a choice with no votes drops out |
| Change of mind after the deadline | `refused(pollClosed)`, vote and tally unchanged |
| Voter who abstained so far | `refused(noVoteToReplace)`, no vote is created, tally unchanged |
| Change of mind after confirming | `refused(ballotSealed)`, vote and tally unchanged |

- **Refusal rows:** each row checks the result, alice's vote afterwards and the tally afterwards. The replacement choice takes both values (`{yes, no}`), because a refusal must change nothing whichever choice is offered.
- **Fixture:** alice is the voter, and bob's vote is in the box to show it stays untouched. The test builds the box through `cast`, `seal` and `close`.

**One edit to the source.** I added `Codable` to `Replacement` and `Refusal` in `BallotBox.swift`. A table cell like `refused(pollClosed)` can't be read without it. The stub body and signature are unchanged.

**Assumptions.** The rules don't settle these, so I kept every row independent of them. They are also listed in the test's doc comment.
- When several refusal reasons apply at once, which one is reported is unspecified. For example, a closed poll with no vote could report either reason.
- A sealed ballot with no vote could be refused as sealed or as no vote.
- Replacing a vote with the same choice may or may not count as `replaced`.

If you decide any of these, each one is a row to add.