I converted the five pricing tests in `Tests/BowlingTests/LanePriceTests.swift` into one `@TableTest`. `swift build --build-tests` and `swift test` both pass, with 5 table cases and the rejection test.

- **Table:** The columns are `Lane | Hours | Member | Price?`, and each of the five original bookings is one row. The test builds `Booking(lane:hours:isMember:)` from the columns and checks `LanePrice.euros(for:)` against `Price?`.
- **Rejection test:** `bookingNeedsAtLeastOneHour` stays a plain `@Test` (now named `rejectsABookingWithoutHours`). It is a single rejected case, and the pricing table would still make sense without it, so it is a separate concern.
- **App change:** I added `CaseIterable` to `Lane` in `Sources/Bowling/LanePrice.swift`. This lets the table read `standard`, `deluxe` and `kids` directly, with no conversion code in the tests. It doesn't change how `Lane` behaves.

I kept the case count the same as before, so the table has the same coverage gaps as the old tests:

- The "hours after the third are free" rule is only tested with 5 hours. A row at 3 hours and one at 4 hours would test the boundary.
- Nothing checks the member discount together with the free hours, or the standard and kids lanes with the discount.

I can add those rows if you want them.