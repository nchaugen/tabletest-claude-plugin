I converted the five pricing tests in `Tests/BowlingTests/LanePriceTests.swift` into one `@TableTest`, and `swift test` passes with all 5 rows plus the rejection test.

```swift
@TableTest("""
    Scenario                   | Lane     | Hours | Member | Price (EUR)?
    Standard lane hourly rate  | standard | 2     | false  | 60
    Deluxe lane hourly rate    | deluxe   | 2     | false  | 90
    Kids lane hourly rate      | kids     | 1     | false  | 20
    Hours after the third      | standard | 5     | false  | 90
    Member discount            | deluxe   | 2     | true   | 72
    """)
func pricesABookingFromLaneHoursAndMembership(lane: Lane, hours: Int, member: Bool, price: Decimal) throws
```

- **Rejection stays a separate `@Test`.** I renamed it `rejectsABookingWithoutHours`. If you remove it, the price table still states the pricing rule, so the two are separate concerns.
- **Lane conformance.** `Lane` isn't `Codable` or `CaseIterable`, so I added a small `TableTextDecodable` extension in the test file. It reads `standard`, `deluxe` and `kids`. It doesn't use `@retroactive`, because the compiler rejects that for a type in the same package.
- **Rows match the originals.** I didn't add boundary rows such as 3 vs 4 hours. Say if you want them.
- **Test count.** The table is reported as one test with 5 cases, so the suite shows 2 tests rather than 6.