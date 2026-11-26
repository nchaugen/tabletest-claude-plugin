I wrote the tests in `Tests/PollingTests/BallotBoxTests.swift`. They compile, and all 6 cases fail with `NotImplemented()` against the stub, as intended. `replaceVote` is still unimplemented.

**Table.** One `@TableTest` covers the whole rule, with one row per case:

| Scenario | Poll | Ana's ballot | Result |
|---|---|---|---|
| Yes voter, ballot still unsealed | open | voted yes | replaced; vote and tally change |
| No voter, ballot still unsealed | open | voted no | replaced; vote and tally change |
| Change of mind after the deadline | closed | voted yes | refused (`pollClosed`) |
| Voter who never voted | open | no vote | refused (`noVoteToReplace`) |
| Voter who finalised their ballot | open | voted yes, sealed | refused (`ballotSealed`) |

- **Unchanged state:** every row checks the result, Ana's vote and the whole tally. The two replaced rows show that the checks can see a change, so the refusal rows prove nothing changed.
- **Fixture:** a second voter, Bea, has voted yes in every row, so the tally shows that nobody else's vote was touched.
- **Never-voted row:** its replacement choice is a value set (`{yes, no}`), so it runs twice, once for each choice. That is why there are 6 cases from 5 rows.

**Change outside the test file.** I added `Codable` to `Refusal` and `Replacement` in `Sources/Polling/BallotBox.swift`. This lets a table cell read `refused(pollClosed)` directly. It is a conformance only and adds no behavior.

**Assumptions** (also in the test's doc comment):
- **Several refusal reasons at once.** The rules don't say which reason wins, for example when the poll is closed and the ballot is sealed. No row combines them, so the implementation is free to choose.
- **Replacing with the voter's current choice.** The rules don't say whether this counts as `replaced`, so it isn't tested.
- **Sealing with no vote.** The test doesn't cover this combination, so a voter with a sealed ballot but no vote is unspecified.