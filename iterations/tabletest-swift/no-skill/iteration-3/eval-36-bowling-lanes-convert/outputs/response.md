I converted five of the six tests in `Tests/BowlingTests/LanePriceTests.swift` into one `@TableTest` and kept the zero-hours test separate. `swift test` passes: the table runs 5 cases and the zero-hours test passes.

```swift
@TableTest("""
    Scenario                   | Lane     | Hours | Member | Price?
    Standard hourly rate       | standard | 2     | false  | 60
    Deluxe hourly rate         | deluxe   | 2     | false  | 90
    Kids hourly rate           | kids     | 1     | false  | 20
    Hours after the third      | standard | 5     | false  | 90
    Member discount            | deluxe   | 2     | true   | 72
    """)
func chargesTheLaneRateForTheBookedHours(lane: Lane, hours: Int, member: Bool, price: Decimal) throws
```

- **Same rows as before:** each row is one of the original bookings with the same expected price. I added no new cases, including no boundary pair around the third hour. Say if you want one.
- **Zero-hours test kept separate:** it asserts a thrown `BookingError.noHours` with `#expect(throws:)`. The five pricing rows would still state the pricing rule without it. Folding it into the table would need a branch in the test body.
- **`Lane` conversion:** `Lane` isn't `Codable` or `CaseIterable`, so I added a small `TableTextDecodable` extension in the test file that maps `standard`, `deluxe` and `kids` to the cases. It leaves production code untouched. I left off `@retroactive` because the compiler rejects it for a type in the same package. Making `Lane` `CaseIterable` in `Sources` would let you delete the extension.
- **Member column:** I gave it an explicit `false` rather than relying on the `Booking` default, so the table shows which rows are members.