# Narration — 45

The agent's visible narration, thinking cost and file writes, in order, distilled from
`conversation.jsonl`. Thinking text appears when the run asked the CLI for a summary
(`thinking_display: "summarized"`); the per-block token counts appear either way.

Swift project depending on TableTest → tabletest-swift skill.

_[~1,055 thinking tokens (115 deltas)]_

> (thinking) I'm settling on a design: the decision logic already lives in `notice(for:)`, so I'll table that decision and add wiring tests for the text side, since the user asked for tests on `text(for:)`. That means making the app type `Codable` so `Notice` can be tabled, and handling the text part, which comes from app code through minutes interpolation and a service string, with one plain wiring test covering a few cases.
> 
> I'm weighing whether to add a few more checks on the text side, such as a test that a cancelled departure points to the next one. I'm leaning toward keeping to a single wiring test on a delayed departure, which covers the interpolated minutes, the service phrase, and the scheduled time and destination, without asserting each constant sentence. I'm also noting that `Notice` will need `Codable` added so the decision table can work with it.
> 
> I'm laying out the test table with its columns: delay, cancelled status, a fixed next departure time, and the expected notice. The first rows cover the boundaries, starting with a four-minute delay that should read as on time and a five-minute delay that should become a delayed notice.
> 
> I'm filling in the test rows with their scenario names and checking the cancelled cases, which should ignore the delay and point to the next departure. I'm also weighing whether the threshold values need their own columns, since the app constants aren't readable from the tests.
> 
> I'm going to put the threshold values in a doc comment rather than in table columns, since the tests can't read those constants. Next I'll write a single wiring test for the text side.

I wrote the tests in `Tests/DeparturesTests/DelayNoticeTests.swift`, and `swift test` passes (2 tests, 7 cases).

**The table tests the decision, not the sentences.** `text(for:)` chooses a `Notice` and then renders fixed prose. I put the table on `DelayNotice.notice(for:)`, so each wording isn't asserted a second time. Each row compares the whole `Notice` case, for example `delayedWithRefund(minutes: 60)`.

| Scenario | Delay (min) | Expected notice |
|---|---|---|
| Just under the delay limit | 4 | `onTime` |
| At the delay limit | 5 | `delayed(minutes: 5)` |
| Last minute before refund | 59 | `delayed(minutes: 59)` |
| At the refund limit | 60 | `delayedWithRefund(minutes: 60)` |
| Cancelled, whatever the delay | 0, 30 or 90 (one row, three cases) | `cancelled(next: 10:45)` |

**One plain `@Test` checks `text(for:)` itself.** It asserts the full delayed text for the 10:30 service to Leeds with a 20-minute delay. That confirms the service name and the minutes are filled in. I didn't assert every sentence separately.

**Changes beyond the test file:**
- **App source edit:** I added `Codable` to `Notice` in `Sources/Departures/DelayNotice.swift`. The table cells read the case as Swift spells it, so the enum needs it. It adds no behaviour.
- **Formatter:** I ran the `tabletest-format` command, which realigned the table in the test file.

**Assumptions:**
- The 5-minute and 60-minute limits are not exposed as constants in the code. I stated them in the doc comment instead of as table columns, since nothing could check a column against them.
- The next departure is a column in every row, because the cancelled row's expected value comes from it.

