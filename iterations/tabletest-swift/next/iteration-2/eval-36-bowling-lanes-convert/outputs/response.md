I converted the five price tests in `LanePriceTests.swift` into one `@TableTest`. `swift build --build-tests` and `swift test` pass, with 5 table cases plus the rejection test, so the case count is unchanged.

```swift
@TableTest("""
    Scenario                     | Lane     | Hours | Member | Price (EUR)?
    Standard lane, within limit  | standard | 2     | false  | 60
    Deluxe lane, within limit    | deluxe   | 2     | false  | 90
    Kids lane, single hour       | kids     | 1     | false  | 20
    Standard lane, past 3 hours  | standard | 5     | false  | 90
    Member on a deluxe lane      | deluxe   | 2     | true   | 72
    """)
func pricesABookingFromLaneHoursAndMembership(lane: Lane, hours: Int, member: Bool, price: Decimal) throws
```

- **App change:** I added `CaseIterable` to `Lane` in `Sources/Bowling/LanePrice.swift`. That lets a table cell read `standard`, `deluxe` or `kids` directly, with no conversion code in the test target. The conformance is synthesised and changes no behaviour.
- **Rejection test:** `bookingNeedsAtLeastOneHour` stays a plain `@Test`. It is a single rejection case, and the price table still makes sense without it, so it is a separate concern.
- **Assumptions:** I kept exactly the five original scenarios. I did not add boundary rows (for example exactly 3 hours against 4 hours, or a member past 3 hours), although those would strengthen the table. Scenario names describe the condition rather than the price, and `Member` is spelled out as `false` on non-member rows.