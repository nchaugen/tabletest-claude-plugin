I wrote the tests in `Tests/PollingTests/BallotBoxTests.swift`. They compile, and all 7 cases fail with `NotImplemented`, as intended. I left `replaceVote` as the unimplemented stub.

**Tests**
- **`replacesTheVotersVoteWhileThePollIsOpen`** has 4 rows. They cover switching sides alone, switching among other voters, re-picking the same choice, and switching from no to yes. Each row checks the `Replacement`, the voter's vote afterwards, and the full tally.
- **`refusesToReplaceAVoteThatMustStay`** has 3 rows: poll closed, voter has no vote, and ballot sealed. Each row has exactly one reason to refuse, and checks the exact `Refusal`, the voter's vote and the tally.

**One change outside the tests:** I added `Codable` to `Refusal` and `Replacement` in `BallotBox.swift`. That lets table cells read `refused(pollClosed)`. The conformances are synthesized, so behaviour doesn't change, and the stub is untouched. Revert them if you'd rather not touch the source. The alternative is a hand-written conversion in the test target.

**Assumptions**
- **Precedence:** The rules don't say which refusal wins when several apply, for example a closed poll for a voter with no vote. No row combines reasons, and the doc comment records this as an open question. Once you decide, add a precedence table.
- **Fixture:** The voter is "Ana", and other voters are numbered "other-N". The success table holds the poll open and the ballot unsealed, and says so in its doc comment.
- **Sealing without a vote:** A voter with no vote and an unsealed ballot is the "no vote" case. I didn't test sealing a ballot before voting.