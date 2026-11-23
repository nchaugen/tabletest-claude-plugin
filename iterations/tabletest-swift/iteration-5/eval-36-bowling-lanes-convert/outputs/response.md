I converted the five pricing tests into one `@TableTest` in `Tests/BowlingTests/LanePriceTests.swift`. The build passes, and `swift test` passes with all 5 table cases and the rejection test.

- **Pricing table:** the columns are Lane, Hours, Member and `Price (EUR)?`. The rows are the original five bookings, unchanged, with scenario names that describe the booking rather than the outcome. I kept the five as they were, so the case count is the same as before.
- **Zero hours:** `bookingNeedsAtLeastOneHour` is still a plain `@Test`, now named `rejectsABookingWithoutHours`. It is a single rejection case and a different rule from pricing, so I didn't put it in the table.
- **App change:** I added `CaseIterable` to `Lane` in `Sources`, so the Lane column can read `standard`, `deluxe` and `kids` directly. It adds no behaviour.
- **Formatting:** I ran `tabletest-format`, which aligned the table columns.

The table has no row for exactly 3 hours or 4 hours, so the boundary where hours become free isn't tested on both sides. The old tests didn't cover it either, and 5 hours alone doesn't show where the free hours begin. I didn't add rows because that would change what you asked me to convert. Adding `standard | 3 | false | 90` and `standard | 4 | false | 90` would close the gap.