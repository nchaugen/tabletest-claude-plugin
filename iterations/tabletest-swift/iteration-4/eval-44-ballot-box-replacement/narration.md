# Narration — 44

The agent's visible narration, thinking cost and file writes, in order, distilled from
`conversation.jsonl`. Thinking text appears when the run asked the CLI for a summary
(`thinking_display: "summarized"`); the per-block token counts appear either way.

_[~151 thinking tokens (6 deltas)]_

> (thinking) I'm going to check the package setup first.

_[~1,575 thinking tokens (227 deltas)]_

> (thinking) I'm weighing whether `Replacement` needs `Codable` for the `refused(pollClosed)` cell, since it currently only has `Equatable` and `Sendable`. The user said the stub stays unimplemented, so adding `Codable` would be a type change I'd rather avoid. I'm considering leaving the expectation column's `Refusal?` blank instead.
> 
> I'm thinking through whether `Replacement` should conform to `Codable` so the `refused(pollClosed)` case can be encoded in the table, since it currently only has `Equatable` and `Sendable`. I'm leaning toward adding it directly to the app type, since it seems like the minimal step, and I'll mention that change when I report back.
> 
> I'm settling the fixture shape now: poll state and ballot state as small enums that apply themselves to the box, with the existing vote passed as an optional choice that a helper turns into a cast only when present. I'll keep the refusal reason precedence out of the table, since the rules don't specify it, and note that in a doc comment. Next I'm sketching the first table, replacement while the poll is open, starting with the yes-to-no and no-to-yes cases.
> 
> I'm working out the replacement rows now: one where the vote flips yes to no with no other votes, one where it flips among other voters, and one where the same choice is resubmitted, checking the tally after each.
> 
> I'm laying out the success table now: for the replacement cases I'll record the replaced outcome, the vote and tally afterward. The refusal rows need their own table with poll state, ballot state, and the other votes, and the expected vote should stay unchanged after a refusal.
> 
> I'm now going through the refusal rows, starting with a closed poll that already holds a yes vote, and then a voter with no existing vote on an open poll.
> 
> I'm settling the refusal rows next: a closed poll, a voter with no vote to replace, and a sealed ballot, each with only one failing condition so the expected reason is exact. I'm leaving out any row that depends on precedence, since the rules don't specify it.
> 
> I'm sorting out the poll and ballot states as columns versus constants, and I'll keep the open poll and unsealed ballot as fixed values stated in a doc comment. The replacement call throws, so the test will use `try`, and it should fail now since the stub isn't implemented yet. Then I'll check the replacement result, the stored vote, and the tally.
> 
> I'm filling the helper with a loop that casts the other voters' ballots, which is fine inside the fixture. Then I'll add `Codable` to `Replacement` and edit the source.

_[~225 thinking tokens (21 deltas)]_

> (thinking) Refusal also has String raw values, so it needs Codable declared explicitly, since that isn't synthesized on its own. Replacement can only be synthesized as Codable if Refusal conforms too, so I'll add it there as well.

I wrote the tests in `Tests/PollingTests/BallotBoxTests.swift`. They compile, and all 7 cases fail with `NotImplemented`, as intended. I left `replaceVote` as the unimplemented stub.

**Tests**
- **`replacesTheVotersVoteWhileThePollIsOpen`** has 4 rows. They cover switching sides alone, switching among other voters, re-picking the same choice, and switching from no to yes. Each row checks the `Replacement`, the voter's vote afterwards, and the full tally.
- **`refusesToReplaceAVoteThatMustStay`** has 3 rows: poll closed, voter has no vote, and ballot sealed. Each row has exactly one reason to refuse, and checks the exact `Refusal`, the voter's vote and the tally.

**One change outside the tests:** I added `Codable` to `Refusal` and `Replacement` in `BallotBox.swift`. That lets table cells read `refused(pollClosed)`. The conformances are synthesized, so behaviour doesn't change, and the stub is untouched. Revert them if you'd rather not touch the source. The alternative is a hand-written conversion in the test target.

**Assumptions**
- **Precedence:** The rules don't say which refusal wins when several apply, for example a closed poll for a voter with no vote. No row combines reasons, and the doc comment records this as an open question. Once you decide, add a precedence table.
- **Fixture:** The voter is "Ana", and other voters are numbered "other-N". The success table holds the poll open and the ballot unsealed, and says so in its doc comment.
- **Sealing without a vote:** A voter with no vote and an unsealed ballot is the "no vote" case. I didn't test sealing a ballot before voting.

