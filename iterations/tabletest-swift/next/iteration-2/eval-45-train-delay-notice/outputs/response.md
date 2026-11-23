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