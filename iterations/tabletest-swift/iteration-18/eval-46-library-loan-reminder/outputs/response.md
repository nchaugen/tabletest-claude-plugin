I wrote the tests in `Tests/LibraryTests/LoanReminderTests.swift`, and `swift build --build-tests` and `swift test` both pass. To get there I made a small change to `Sources/Library/LoanReminder.swift`.

**Source change.** `text(for:)` returned only prose, so there was nothing to assert on except the wording. I added `LoanReminder.Kind` and `LoanReminder.kind(for:)`, which hold the decision. The five kinds are `notDue`, `renewable`, `reservedDueSoon`, `overdue` and `accountBlocked`. `text(for:)` now switches on that kind. The sentences and the branch logic are unchanged. If you'd rather not touch the source, say so and I'll test through the text only.

**Tests.**
1. **`choosesTheReminderForALoan`** is a `@TableTest` over the decision. It has 5 rows, which expand to 16 cases.
   - **Boundaries.** The rows pin every edge at both sides: 4 | 3, 0 | -1 and -29 | -30. The renewal window (0 to 3) and the overdue band (-29 to -1) are covered at both ends.
   - **Reservation.** It splits the renewal window into free and reserved. In the other three tiers it varies reservation as `{true, false}`, which shows it makes no difference there.
   - **Thresholds.** The 3-day window and the 30-day block are written inline in the source, so they can't be columns. The doc comment on the table says this.
2. **`wordsTheReminderWithTheQuotedTitle`** is one plain `@Test`. It checks that `text(for:)` wraps the loan title in quotes and uses the wording for the decided kind. I didn't assert every sentence, because that would just compare the strings with copies of themselves.

I assumed that "tests for `text(for:)`" means testing the decision behind the wording, plus one check that the text is wired to it. Days Until Due is 0 on the due date, as the doc comment says.