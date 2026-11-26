# Narration — 44

The agent's visible narration, thinking cost and file writes, in order, distilled from
`conversation.jsonl`. Thinking text appears when the run asked the CLI for a summary
(`thinking_display: "summarized"`); the per-block token counts appear either way.

_[~184 thinking tokens (15 deltas)]_

> (thinking) The skill's instructions say to read references/table-design.md in full before writing anything. I also need to read it from the skill's base directory, and check Package.swift.

_[~1,932 thinking tokens (273 deltas)]_

> (thinking) I'm settling the rules for the design: three refusal reasons, plus a "replaced" case, and the state stays unchanged whenever a vote is refused. The open question is what happens when several conditions apply at once, like a closed poll with no vote. That's unspecified, so I'll test the single-reason cases and leave precedence out, noting it in the doc.
> 
> I'm laying out the test table now, with columns for the poll state, the votes before, the ballot state, the voter's prior vote, the replacement choice, and the resulting state. I'm planning to use a fixed voter and plain Bool-style inputs for the poll and ballot, and to build the fixture with test-local enums that apply their own changes to the box.
> 
> I'm now deciding how to build the fixture: I'll use a helper that sets up the votes and then applies the poll and ballot states to the box, since the table body can't loop over cells.
> 
> I'm settling the details of the tally and vote fields: the tally will be a map from each choice to a count, and the vote-after column will be blank when the voter has no vote. The replacement type needs to be Codable for the table to decode it, which would touch the app type, so I'm weighing whether adding that conformance is acceptable even though the stub should stay unimplemented.
> 
> I'm weighing whether to make the replacement type Codable so the table can decode it, which means touching the app type, and the plan is to keep the stub itself unimplemented. Then I'm laying out the rows for the replacement scenarios, with the voter fixed as ada.
> 
> I'm laying out the replacement rows now: the table gets a column for the replacement choice, whether a replacement happened, the voter's vote after, and the tally after. The first row has an open, unsealed poll where ada switches from yes to no while bea has voted no, so the tally should show only no: 2. The second row replaces with the same choice, which is a no-op case I'm weighing whether to keep.
> 
> I'm checking the remaining rows of the table: a closed poll with an unsealed box refuses with the poll-closed reason and leaves the votes as they were, a box with no vote from the voter refuses the replacement, and a sealed ballot refuses it too. I'm also weighing whether to include an empty-box row, which would just leave the tally empty.
> 
> I'm checking whether the "Poll closed" scenario name just repeats the input value, since the name would duplicate the column content, and I'm leaving the refusal rows' replacement choice fixed so a wrongful change stays visible.
> 
> I'm weighing scenario names that describe the condition without repeating the value in the expectation columns, so "Vote cast before the poll closed" and "Voter absent from the box" rather than names that echo the refusal reasons. The sealed case needs a name that doesn't just restate the sealed state either, so I'm leaving that one open for now.
> 
> I'm setting up the voter columns and naming conventions for the table: the voter is fixed as ada, so the headers just say "Vote After?" rather than repeating her name. For the poll and ballot enums, I'll use lowercase raw values, "open"/"closed" and "unsealed"/"sealed", and note the fixed voter in a doc comment. Next I'm planning a dictionary extension that builds the box inside the cell's own type, which avoids a loop in the table body.
> 
> I'm now checking the parameter types for the test: the votes map uses String keys with Choice values, and the choice enum should convert cleanly from its raw values. I also need to make Replacement Codable, and I'll use the synthesized refused(pollClosed) spelling and run the build to see whether the conversion errors point anywhere useful.

_[~186 thinking tokens (9 deltas)]_

> (thinking) Refusal also needs Codable, since it uses String raw values like Choice does, so I'll add it there too.

_[~204 thinking tokens (14 deltas)]_

> (thinking) The tests now compile and fail with NotImplemented, which is what I expected. All five rows parse and convert correctly. Next I'll wrap up with the summary, and I should note that using `if` inside the enum members is allowed.

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

