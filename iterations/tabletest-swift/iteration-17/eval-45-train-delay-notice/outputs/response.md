I wrote the tests in `Tests/DeparturesTests/DelayNoticeTests.swift`. `swift build --build-tests` and `swift test` pass: 2 tests, 8 cases in total.

**One edit outside the tests:** I added `Codable` to `Notice` in `Sources/Departures/DelayNotice.swift`. `Notice` is synthesised `Codable` and nothing else changed. TableTest needs it so a cell can read `delayed(minutes: 5)`.

**What the tests cover:**
- **Table test:** `text(for:)` only wraps the result of `notice(for:)` in wording, so I tested the decision. The table has 5 rows:
  - A delay of 0 or 4 minutes gives `onTime`.
  - 5 minutes gives `delayed(minutes: 5)`.
  - 59 minutes gives `delayed(minutes: 59)`.
  - 60 minutes gives `delayedWithRefund(minutes: 60)`.
  - A cancelled departure gives `cancelled(next: 15:05)` whatever the delay (0, 5 or 90).
- **Wiring test:** a plain `@Test` checks that `text(for:)` builds "The 14:05 service to Leeds" and uses the wording for the decided notice. It compares against `text(of:service:)` rather than repeating the long sentences.

**Assumptions:**
- The 5-minute and 60-minute limits are literals in the code, not constants, so they're stated in the doc comment and the rows sit on both sides of each.
- I used one fixed fixture (14:05 to Leeds, next departure 15:05). The decision doesn't read the time or destination.
- Early departures (negative delay) aren't tested. The code treats them as on time, but the spec doesn't say so.