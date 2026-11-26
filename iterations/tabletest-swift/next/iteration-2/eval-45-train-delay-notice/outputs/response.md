I wrote the tests in `Tests/DeparturesTests/DelayNoticeTests.swift`, and `swift test` passes: 9 cases across 2 tests.

`text(for:)` produces fixed sentences that a person reads. I didn't copy each sentence into a test. A copy would only check that two copies of the text agree. So I tested the decision behind the text and added one check of the wording:

- **Table test, `decidesWhatTheNoticeTellsPassengers`:** this checks `DelayNotice.notice(for:)`, the function that picks which notice a departure gets. Each row gives the cancelled flag and the delay, and expects a `Notice`.
  - **Boundaries:** the rows cover both sides of each boundary: 0 and 4 minutes (on time), 5 and 59 (delayed), and 60 (refund).
  - **Cancelled:** a cancelled departure gets `cancelled(next: '09:15')` at any delay, and the row tries 0, 30 and 90 minutes.
- **Plain test, `textNamesTheServiceAndTheDelay`:** this calls `text(for:)` for a 12-minute delay and compares the full string. It checks that the text names the service ("The 08:15 service to Leeds") and fills in the minutes. I didn't add a similar check for the cancelled text, so the next-departure time in that sentence is only covered through the table.

**App change:** I added `Codable` to the `Notice` enum in `Sources/Departures/DelayNotice.swift`. That lets a table cell write the expected notice directly, such as `delayed(minutes: 5)`. It adds no behaviour.

**Assumptions:**
- The 5-minute and 60-minute limits are stated in the doc comment, not as table columns. The code keeps them as literals, so a column would be an unused parameter that no row could change the result of.
- The test data uses a made-up service: 08:15 to Leeds, with the next departure at 09:15.
- By "tests for `text(for:)`" I took you to mean the decision plus one wording check, as described above. If you want every sentence asserted exactly, say so and I'll add them.