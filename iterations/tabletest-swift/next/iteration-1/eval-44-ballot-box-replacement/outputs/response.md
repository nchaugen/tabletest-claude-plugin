I wrote the tests in `Tests/PollingTests/BallotBoxTests.swift`. They compile, and all 9 cases fail because `replaceVote` still throws `NotImplemented`. I didn't implement it.

One `@TableTest`, `replacesAVoteUnlessTheBoxRefuses`, covers all three rules. Each row sets up the poll state, the ballot state and the voter's vote before. It then calls `replaceVote` and checks the result, the voter's vote afterwards and the tally afterwards.

| Scenario | Result | Vote after | Tally after |
|---|---|---|---|
| Open poll, vote changes (yes→no) | `replaced` | no | yes 1, no 1 |
| Open poll, vote changes back (no→yes) | `replaced` | yes | yes 2 |
| Open poll, same choice again | `replaced` | yes | yes 2 |
| Poll closed after voting | `refused(pollClosed)` | yes | yes 2 |
| Voter has not voted | `refused(noVoteToReplace)` | none | yes 1 |
| Voter's ballot sealed | `refused(ballotSealed)` | yes | yes 2 |

The three refusal rows each expand to two cases, one per replacement choice, because a refusal shouldn't depend on the choice offered.

**Change to the stub file:** I added `Codable` to `Refusal` and `Replacement` in `BallotBox.swift`. This lets a table cell read `refused(pollClosed)`. It adds no behaviour, and `replaceVote` is untouched.

**Assumptions** (also in the doc comment above the test):
- **Fixture:** a second voter, "bo", has a `yes` vote in every box. The tally can then show that a refusal or replacement changed nothing beyond the voter's own vote.
- **Refusal precedence:** I didn't test which refusal wins when several apply, such as a closed poll with a sealed ballot, or a sealed ballot with no vote. The rules don't say, so each refusal row triggers only its own reason.
- **Same-choice replacement:** I treated replacing a vote with the same choice as `replaced`, not a refusal.