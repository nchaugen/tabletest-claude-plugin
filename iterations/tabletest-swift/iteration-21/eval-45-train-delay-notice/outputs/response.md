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