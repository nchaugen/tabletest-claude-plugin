I wrote the tests in `Tests/HotelBookingTests/CancellationFeeTests.swift` and left the stub alone. The package builds, and `swift test` fails with 7 issues as expected: 6 fee cases and 1 rejection case, all from `NotImplemented`.

**Fee tiers** — one table-driven test (`@Test(arguments:)` over labelled tuples), booking value fixed at 200 so each fee is a whole euro amount:

| Scenario | Days before check-in | Fee |
|---|---|---|
| start of the free window | 30 | 0 |
| first day charged 50% | 29 | 100 |
| last day charged 50% | 7 | 100 |
| first day charged 80% | 6 | 160 |
| last day charged 80% | 1 | 160 |
| check-in day | 0 | 200 |

Each tier boundary is checked from both sides.

**Rejection** — a separate test using `#expect(throws: CancellationError.stayAlreadyStarted)` with `daysBeforeCheckIn: -1`. It is separate because the fee tiers still make sense without the rejection case.

**Assumptions**
- `daysBeforeCheckIn` is a whole number of days. 0 is the day of check-in, and a negative value means the stay has started (this matches the stub's doc comment).
- The spec doesn't say how to round, so the tests only use a booking value of 200, where 50% and 80% come out as whole euros. The rounding rule is untested.
- The fee tier and the percentage are checked together, because the function only returns the fee. This is noted in a comment in the file.
- Only -1 is tested for rejection. A value further past the boundary would show nothing new.

The scenario names in the table are condition names rather than outcomes, and a test failure shows the scenario name.