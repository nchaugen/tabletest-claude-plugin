# Narration — 45

The agent's visible narration, thinking cost and file writes, in order, distilled from
`conversation.jsonl`. Thinking text appears when the run asked the CLI for a summary
(`thinking_display: "summarized"`); the per-block token counts appear either way.

_[~159 thinking tokens (10 deltas)]_

> (thinking) This is a Swift project that uses TableTest, so the tabletest-swift skill is the right fit. I'll invoke it.

_[~883 thinking tokens (98 deltas)]_

> (thinking) I'm settling on how to design `text(for:)`, which produces the notice text. The text is long and depends on the notice kind, the service, the minutes, and what comes next, so I'm weighing whether asserting the full string in tests is worth the brittleness, since it is the contract.
> 
> I'm now settling which cases the `text(for:)` table needs: one row for each of the four kinds, plus a cancelled departure that also has a delay, so that the cancelled text takes precedence. I'm leaving the exact boundary values at 4/5 and 59/60 to `notice(for:)`'s own tests, though I'm still weighing whether a few of them belong in the table too.
> 
> I'm laying out the rows for the `text(for:)` table: on-time departures at 0 and 4 minutes, delayed departures at 5 and 59 minutes (where the text changes with the minutes), a refund case at 60, and a cancelled departure that also has a delay of 0 or 90 minutes, so the cancelled text wins. Next departure only matters for cancelled rows, so I'll hold a couple of values there for the others, quoting the times in the collection.
> 
> I'm now filling in the table columns and values: the cancelled flag, the delay in minutes, and the text cell, with the delay and next-departure values ignored for cancelled rows. I'm going to write the table and run it.

**Write /private/var/folders/fz/zxx_qbvj49gbql83cc5l47pr0000gn/T/eval-next-1795477365986/eval-45-work/Tests/DeparturesTests/DelayNoticeTests.swift (38 lines)**

I wrote one table test for `DelayNotice.text(for:)` in `Tests/DeparturesTests/DelayNoticeTests.swift`. `swift build --build-tests` and `swift test` both pass, with all 18 test cases green. The formatter then realigned the table, which is the on-disk change you can see.

The test builds a `Departure` from each row and compares the full notice text, since passengers read that wording as is. Each row covers one case:

- **On time:** delays of 0 and 4 minutes, with the next departure varied. The text ignores the next departure.
- **Delayed:** 5 minutes (first minute of a delay) and 59 minutes (last minute before a refund). The minutes appear in the text, so each edge needs its own row.
- **Refund:** 60 minutes and 185 minutes.
- **Cancelled:** one row with delays of 0, 4, 5, 59, 60 and 185. Cancelled takes precedence over any delay.

Each row's scenario name describes the situation, not the expected result.

**Assumptions**
- I compare the exact full strings, so any wording change will fail these tests.
- I tested the 5-minute and 60-minute thresholds only through the text they produce. Testing `notice(for:)` directly would be a separate table, which I didn't write because you asked only about `text(for:)`.
- I left `text(of:service:)` alone because it's internal, and `text(for:)` already exercises it.

