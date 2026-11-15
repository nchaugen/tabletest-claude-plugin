I wrote the tests in `Tests/PollingTests/BallotBoxTests.swift`. `swift build --build-tests` succeeds. `swift test` runs 8 cases and all fail with `NotImplemented()`, which is what you wanted while the stub is unimplemented.

**The test**
- It is one `@TableTest`, `replacesAVoteOnlyWhileThePollIsOpenAndTheBallotIsUnsealed`. All three rules are about the outcome of one replacement request, so they share a table.
- Each row sets up the box, calls `replaceVote(_:for:)`, then checks the result, Ana's vote afterwards and the tally afterwards.
- Each row has its own box. Ben has voted yes in every row, so the tally shows that other voters' votes are untouched.

| Scenario | Result | Ana's vote after |
|---|---|---|
| Open poll, unsealed ballot (yes → no) | `replaced` | no |
| Same choice cast again | `replaced` | yes |
| Closed poll | `refused(pollClosed)` | unchanged |
| Voter without a vote in the box | `refused(noVoteToReplace)` | still none |
| Sealed ballot | `refused(ballotSealed)` | unchanged |

- In the refused rows, the tally is unchanged too.
- The replacement choice is a value set (`{yes, no}`) on the refused rows. That says a refusal doesn't depend on which choice was offered.

**Change to the source file**
I added `Codable` to `Replacement` and `Refusal` in `BallotBox.swift`. That lets a table cell read `refused(pollClosed)`. The stub still throws `NotImplemented`.

**Assumptions**
- A refusal comes back as `.refused(reason)`. The stub signature returns `Replacement`, so I did not expect it to throw.
- The rules don't say which reason wins when several apply, such as a closed poll with a sealed ballot. Each refused row has only one reason. If you want a precedence, it needs its own rows.
- Replacing a vote with the same choice counts as `.replaced`.