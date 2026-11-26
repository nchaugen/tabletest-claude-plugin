I converted the five pricing tests into one `@TableTest` in `Tests/BowlingTests/LanePriceTests.swift`. `swift build --build-tests` and `swift test` both pass: the table runs 6 cases, plus the one plain rejection test.

**The table** (`pricesABookingFromLaneHoursAndMembership`) has the columns `Lane | Hours | Member | Price (€)?`. Its rows are:

| Scenario | Lane | Hours | Member | Price (€)? |
|---|---|---|---|---|
| Standard hourly rate | standard | 2 | false | 60 |
| Deluxe hourly rate | deluxe | 2 | false | 90 |
| Kids hourly rate | kids | 1 | false | 20 |
| Last hour charged | standard | 3 | false | 90 |
| First hour free | standard | 4 | false | 90 |
| Member on a paid lane | deluxe | 2 | true | 72 |

**Changes you may not expect:**
- **App source:** I added `CaseIterable` to `Lane` in `Sources/Bowling`. That lets the table cells read `standard`, `deluxe` and `kids` directly. It adds no behaviour.
- **Hours rows:** the old "5 hours" test is now two rows, 3 and 4 hours. They sit on either side of the point where charging stops, which 5 hours alone didn't show. This takes the count from five pricing cases to six. The old 5-hour case is no longer tested directly.
- **Rejection test:** `bookingNeedsAtLeastOneHour` stays a separate plain `@Test`, renamed `rejectsABookingWithoutHours`. Without the rejected row, the pricing rows still state a rule, so I kept the rejection out of the table.
- **Fixed values:** the three-hour limit and the 80 % member share are not columns, because the app has no constants to check them against. They are stated in the table's doc comment. The straddling rows pin the three-hour limit.