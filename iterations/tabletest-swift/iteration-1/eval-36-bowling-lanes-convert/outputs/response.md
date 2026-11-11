I converted the five pricing tests in `Tests/BowlingTests/LanePriceTests.swift` into one `@TableTest`. The sixth test, `bookingNeedsAtLeastOneHour`, stays a plain `@Test`. The build succeeds and `swift test` passes: the table runs 5 cases and the plain test passes, so the case count is unchanged.

The table has columns `Lane`, `Hours`, `Member` and `Price (EUR)?`, and a scenario column named by condition. The rows are the original five cases:

| Scenario | Lane | Hours | Member | Price (EUR)? |
|---|---|---|---|---|
| Standard lane | standard | 2 | false | 60 |
| Deluxe lane | deluxe | 2 | false | 90 |
| Kids lane | kids | 1 | false | 20 |
| Booking past third hour | standard | 5 | false | 90 |
| Member booking | deluxe | 2 | true | 72 |

- **Rejection test:** it checks a thrown error, not a price. A single rejection case belongs in a plain `#expect(throws:)` test rather than in a table with a rejection column. I renamed it to `rejectsABookingWithoutHours`.
- **Library change:** I added `CaseIterable` to `Lane` in `Sources/Bowling/LanePrice.swift`, so table cells can read `standard`, `deluxe` and `kids`. It's synthesised and changes no behaviour, but it is an edit outside the test file.
- **Test name:** the table's function is `pricesABookingFromLaneHoursAndMembership`.
- **Rows kept as they were:** I added no new rows. The table still doesn't pin the boundary at 3 hours versus 4, or the member discount on a booking past the third hour. Say if you want those.