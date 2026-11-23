I wrote the tests in `Tests/PollingTests/BallotBoxTests.swift`. They compile, and all 10 cases fail with `NotImplemented()` because the stub is untouched. I left `BallotBox.swift` as it was.

**What the tests cover**
- **`replacesTheVoteWhileThePollIsOpen`** has 4 rows. Each one expects `.replaced`, the voter's new vote, and the new tally.
  - yes changed to no, with no other voters. This makes the `yes` entry drop out of the tally.
  - no changed to yes.
  - a replacement equal to the current vote.
  - a replacement among other voters, which shows their votes survive.
- **`refusesAReplacementAndChangesNothing`** has 3 rows, each with the new choice set to `{yes, no}`. That gives 6 cases, since the refusal shouldn't depend on the new choice. Each row expects the specific refusal, the voter's vote unchanged, and the tally unchanged.
  - poll closed gives `pollClosed`.
  - voter has no vote gives `noVoteToReplace`, with the voter's vote still `nil`.
  - ballot sealed gives `ballotSealed`.

**Assumptions**
- **Refusal precedence:** the rules don't say which refusal wins when several apply, for example a closed poll with a sealed ballot, or a sealed voter who has no vote. Each refusal row triggers exactly one reason, and the doc comment says so. Add a row once you decide the order.
- **Refusals return a value:** a refused replacement returns `.refused(reason)` rather than throwing. Only `NotImplemented` is thrown by the stub, so the tests expect a returned `Replacement`.
- **Unchanged tally:** the refusal tables check the tally with other voters present, so "nothing changes" covers more than the voter's own vote.
- **No app type changes:** `Choice` and `Refusal` already read from their raw values, so I didn't add `Codable` to `Replacement`. The success and refusal cases are two tables for that reason.