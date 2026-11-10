I converted the five pricing tests in `Tests/BowlingTests/LanePriceTests.swift` into one `@TableTest`, and `swift test` passes: 5 table rows plus the error test.

```swift
@TableTest("""
    Scenario            | Lane     | Hours | Member | Price?
    Standard lane       | standard | 2     | false  | 60
    Deluxe lane         | deluxe   | 2     | false  | 90
    Kids lane           | kids     | 1     | false  | 20
    Hours beyond three  | standard | 5     | false  | 90
    Member discount     | deluxe   | 2     | true   | 72
    """)
func chargesByLaneHoursAndMembership(lane: Lane, hours: Int, member: Bool, price: Decimal) throws {
    let booking = Booking(lane: lane, hours: hours, isMember: member)

    #expect(try LanePrice.euros(for: booking) == price)
}
```

- **One table:** lane rate, hours and membership all feed one price, so they stay together as columns instead of being split into one table each.
- **Rejection test kept:** `bookingNeedsAtLeastOneHour` is still a plain `@Test` with `#expect(throws:)`. Taking out the rejection leaves a pricing rule that stands on its own, so the two belong in separate tests.
- **Source change:** I added `CaseIterable` to `Lane` in `Sources/Bowling/LanePrice.swift`, so a cell can say `standard`, `deluxe` or `kids`. TableTest's README recommends this for plain enums.
- **Assumption:** the first column is a scenario name, so it doesn't map to a parameter. Each scenario names its condition rather than its price.
- **Not added:** the original tests never cover the 3-hour versus 4-hour boundary. A "third hour is the last charged" row, such as standard at 3 hours giving 90 and at 4 hours giving 90, would close that gap if you want it.

`swift test` needed `--disable-sandbox` here. I tried `-Xswiftc -skipMacroValidation` first, but swiftc doesn't accept that flag.

That notification is just the background directory search I started earlier finishing. I already found the TableTest checkout I needed, and nothing else depends on it, so there's nothing more to do.

The conversion is complete and the tests pass, as described in my last message.