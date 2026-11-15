I wrote tests for `DelayNotice.text(for:)` in `Tests/DeparturesTests/DelayNoticeTests.swift`, and `swift test` passes: 2 tests, 9 cases. The file on disk is the tabletest-format version of what I wrote (columns re-aligned only).

**Status wording (7 cases).** Each row asserts the full passenger text.
- **Delay under 5 minutes:** a delay of 0 or 4 gives the on-time text. The two values are a value set, so each counts as a case.
- **Delayed:** 5 minutes (just over the on-time cutoff) and 59 minutes (just under the refund cutoff) both give the delayed text with the minutes filled in.
- **Refund:** 60 minutes gives the refund text.
- **Cancelled:** a delay of 0 or 90 gives the cancelled text with the next departure, 09:00. The delay is a value set here because a cancellation overrides it.

**Service naming (2 cases).** The scheduled time and destination are columns here. The rows are 08:15 to Oslo and 21:40 to Lillehammer Station, both on time.

**Assumptions**
- I asserted the exact production wording, so any change to the copy will fail these tests.
- The classification boundaries (5 and 60) are tested through `text(for:)`. A direct `notice(for:)` table would be a separate concern.
- Negative delays and the `nextDeparture` value on non-cancelled departures are not covered. I treated negative delays as on time without testing them, since the spec doesn't mention them.