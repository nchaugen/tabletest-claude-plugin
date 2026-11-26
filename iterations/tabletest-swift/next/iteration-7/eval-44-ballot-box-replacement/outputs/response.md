The tests are in `Tests/PollingTests/BallotBoxTests.swift`. They compile, and all 5 rows fail with `NotImplemented()`, which is the expected state while `replaceVote` is a stub. I ran `swift build --build-tests` and `swift test`.

**The table.** One `@TableTest` covers the whole feature, because "a refused replacement changes nothing" needs a row where the state does change. It uses Ada as the voter and Bea as a bystander. Each row checks the result of `replaceVote`, Ada's vote afterwards, and the tally afterwards.

| Scenario | Poll | Ballot | Votes before | New choice | Result |
|---|---|---|---|---|---|
| Mind changed during the poll | open | unsealed | Ada yes, Bea no | no | replaced; tally `[no: 2]` |
| Same choice repeated | open | unsealed | Ada yes, Bea no | yes | replaced; tally `[yes: 1, no: 1]` |
| Request after the deadline | closed | unsealed | Ada yes, Bea no | no | refused `pollClosed`; Ada still yes, tally unchanged |
| Voter missing from the box | open | unsealed | Bea no | yes | refused `noVoteToReplace`; Ada has no vote, tally `[no: 1]` |
| Request after finalising | open | sealed | Ada yes, Bea no | no | refused `ballotSealed`; Ada still yes, tally unchanged |

**Changes to `BallotBox.swift`.** I added `Codable` to `Replacement` and `Refusal`. TableTest needs it to read `refused(pollClosed)` as one cell. It is a conformance only, with no behaviour change, and `replaceVote` is still the unimplemented stub.

**Assumptions** (also in the test's doc comment):
- **Several refusal reasons at once:** your rules don't say which reason wins when, for example, the poll is closed and the ballot is sealed. No row combines them, so the tests don't pin a precedence.
- **Same choice repeated:** I assumed the box accepts it as a normal replacement.
- **Sealed row:** the sealed voter always has a vote in the box.