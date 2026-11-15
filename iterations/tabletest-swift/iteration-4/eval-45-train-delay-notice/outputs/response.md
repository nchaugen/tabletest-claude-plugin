I wrote the tests for `DelayNotice.text(for:)` in `Tests/DeparturesTests/DelayNoticeTests.swift`, as two `@TableTest` tables. `swift test` passes: 2 tests, 15 cases.

**Table 1, `wordsTheNoticeFromTheDeparturesStatus`.** The departure is fixed as the 08:15 service to Bergen, and the doc comment says so. The rows are:
- **On time:** delays of -2, 0 and 4 minutes, with two different next departures.
- **Delayed:** a delay of 5 minutes (the first delayed value) and 59 minutes (the last one before the refund wording).
- **Refund:** a delay of 60 minutes.
- **Cancelled:** delays of 0, 30 and 90 minutes (a 90-minute delay would otherwise get the refund wording, so this checks cancellation wins). A second row uses a different next departure, to check it appears in the text.

**Table 2, `namesTheServiceFromScheduledTimeAndDestination`.** An on-time departure with two different scheduled times and destinations, which checks the service description in the text.

Assumptions:
- A negative delay (a train running early) counts as on time. `notice(for:)` treats anything under 5 minutes that way, but the code doesn't state it as a requirement, so I wrote it into the doc comment.
- I compare against the full literal text. That makes the cells long, but each expected message can be read straight from its row.
- I tested `text(for:)` only. `notice(for:)` and the internal `text(of:service:)` are covered only indirectly through it.