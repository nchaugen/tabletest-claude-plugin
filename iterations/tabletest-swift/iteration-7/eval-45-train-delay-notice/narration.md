# Narration — 45

The agent's visible narration, thinking cost and file writes, in order, distilled from
`conversation.jsonl`. Thinking text appears when the run asked the CLI for a summary
(`thinking_display: "summarized"`); the per-block token counts appear either way.

_[~157 thinking tokens (11 deltas)]_

> (thinking) I'm working on a Swift project that uses TableTest, so the tabletest-swift skill is the right one to apply here.

_[~759 thinking tokens (77 deltas)]_

> (thinking) I'm laying out the design: `text(for:)` will combine `notice(for:)` and `text(of:service:)`. Classification and wording are kept as separate concerns, so I'm planning tests that check the text for each notice outcome and service phrase. I'm also noting that the long texts can sit in table cells since there are no newlines or `|` characters to escape, and that colons and commas are fine inside single values.
> 
> I'm settling on the test tiers now: on-time values at -2, 0 and 4, delayed at 5 and 59, and refund at 60. A cancelled trip should show the cancellation text even with a delay, so I'll check that with delays of 0 and 90.
> 
> I'm deciding how to lay out the test table: the columns will be scheduled time, destination, delay, cancelled flag, next departure, and expected text. Delay values will come from a small set, and the next departure column gets plain values since colons inside a value set would break the syntax.

**Write /private/var/folders/fz/zxx_qbvj49gbql83cc5l47pr0000gn/T/eval-skill-1795478877492/eval-45-work/Tests/DeparturesTests/DelayNoticeTests.swift (38 lines)**

I've written the tests for `DelayNotice.text(for:)` as one `@TableTest` in `Tests/DeparturesTests/DelayNoticeTests.swift`. `swift test` passes all 10 cases. The formatter then realigned the columns, which is the file change you may have noticed.

**Rows**
- **On time:** a delay of `{-2, 0, 4}` minutes, all giving the same "on time" text.
- **Delayed, both edges:** 5 minutes (first delayed) and 59 minutes (last before a refund).
- **Refund:** 60 minutes (first refund) and 125 minutes (a long delay).
- **Cancelled:** a delay of `{0, 10, 90}` minutes, all giving the cancelled text with the next departure.

The expected text is written out in full, with the scheduled time, destination and delay minutes in each row's own cells.

**Assumptions**
- A negative delay (an early departure) counts as on time, because the code's `..<5` case includes it. The doc comment says so.
- A cancellation overrides any delay.
- `text(for:)` is the only function under test. The notice classification (`notice(for:)`) is covered through it and has no table of its own.

I did not test `text(of:service:)` directly, since it is internal and `text(for:)` already reaches every case of it.

