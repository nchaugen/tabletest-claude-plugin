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