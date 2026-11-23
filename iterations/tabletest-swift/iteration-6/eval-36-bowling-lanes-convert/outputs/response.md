I converted the five pricing tests in `Tests/BowlingTests/LanePriceTests.swift` into one `@TableTest`. `swift build --build-tests` succeeds and `swift test` passes: 5 table cases plus the rejection test.

```swift
/// Prices in euros. Each lane has its own hourly rate, the hours after the third are free,
/// and a member pays 80 % of the price.
@TableTest("""
    Scenario                 | Lane     | Hours | Member | Price (EUR)?
    Standard lane            | standard | 2     | false  | 60
    Deluxe lane              | deluxe   | 2     | false  | 90
    Kids lane                | kids     | 1     | false  | 20
    Booking past three hours | standard | 5     | false  | 90
    Member on a deluxe lane  | deluxe   | 2     | true   | 72
    """)
func pricesABookingByLaneHoursAndMembership(lane: Lane, hours: Int, member: Bool, price: Decimal) throws {
    let booking = Booking(lane: lane, hours: hours, isMember: member)

    #expect(try LanePrice.euros(for: booking) == price)
}
```

- **Production change:** I added `CaseIterable` to `Lane` in `Sources/Bowling/LanePrice.swift`. That lets a cell hold `standard`, `deluxe` or `kids` directly, without converting the string in the test. The conformance is synthesised, so no behaviour changes.
- **Rejection test kept:** `bookingNeedsAtLeastOneHour` is still a plain `@Test`. It's a single case for a separate concern, so a table would add nothing.
- **Same cases:** I kept the original five bookings and added none. There's no row on either side of the three-hour boundary, such as 3 and 4 hours, and no row at the free-hours boundary. I can add those if you want them.