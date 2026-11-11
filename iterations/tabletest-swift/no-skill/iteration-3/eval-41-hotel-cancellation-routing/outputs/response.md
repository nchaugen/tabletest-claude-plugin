I've written the tests in `Tests/HotelBookingTests/CancellationFeeTests.swift`. They compile, and all 7 cases fail on the unimplemented stub, which is what you wanted. The stub in `Sources` is untouched.

The tests are two table-driven Swift Testing tests. The fee rule and the rejection are separate tests because the fee tiers still make sense without the rejection row. Each row has a scenario name that describes the condition, not the outcome.

**Fee by days before check-in**

| Scenario | Booking value | Days | Fee |
|---|---|---|---|
| first day of free cancellation | 400 | 30 | 0 |
| last day before free cancellation | 400 | 29 | 200 |
| first day of half-price window | 400 | 7 | 200 |
| last day before half-price window | 250 | 6 | 200 |
| one day before check-in | 250 | 1 | 200 |
| day of check-in | 250 | 0 | 250 |

Each of the four tiers appears once, and each boundary (30/29, 7/6, 1/0) is tested from both sides. I left out rows that sit further inside a tier, because they would test nothing the boundary rows don't.

**Rejection**: a separate test expects `CancellationError.stayAlreadyStarted` for `daysBeforeCheckIn = -1`. A single row is enough, since further negative values are the same rule.

**Assumptions**
- `daysBeforeCheckIn` is whole days until check-in, where 0 is the day of check-in and negative means the stay has started. This follows the doc comment on the stub.
- Fees are whole euros. I used booking values that are multiples of 10 so every percentage is exact, because the spec doesn't say how to round.
- I used the plain Swift Testing `@Test(arguments:)` form with labelled tuples. The `TableTest` import is still in the file but unused, since I don't know that library's API.