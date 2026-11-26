I've written the tests for `DelayNotice.text(for:)` in `Tests/DeparturesTests/DelayNoticeTests.swift`. `swift build --build-tests` and `swift test` both pass (2 tests, 8 cases).

**Approach.** `text(for:)` renders the result of `notice(for:)`, so the table tests that decision (my assumption about what "tests for `text(for:)`" should cover). A single plain test checks the text itself, as a wiring check. I didn't assert each of the four constant sentences, since that would only check the sentence against a copy of itself.

**Tests**
- **`decidesTheNoticeOfADeparture`** is a `@TableTest` over delay, cancelled and next departure, expecting a `Notice`. Rows:
  - On-time allowance: delays of 0 and 4, as a value set.
  - 5 minutes: the first delay that gets announced.
  - 59 minutes: the last delay before a refund.
  - 60 minutes: the refund threshold.
  - Cancelled with any delay (0, 30 or 90): shows cancellation wins over delay.
- **`textNamesTheServiceAndTheDelay`** is a plain `@Test`. It compares the full text for a 12-minute delay on the 09:30 to Oslo, so it covers the service name and the minutes appearing in the sentence.

**Change to app code.** I added `Codable` to `Notice` in `Sources/Departures/DelayNotice.swift`. It adds no behaviour. It lets table cells like `delayed(minutes: 5)` and `cancelled(next: 10:15)` convert directly.

**Assumptions**
- The 5- and 60-minute thresholds are hard-coded in `notice(for:)`, so they can't be table columns. They are stated in the doc comment instead.
- Negative delays are not tested, because the spec says nothing about them.