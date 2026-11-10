I wrote the tests in `Tests/HotelBookingTests/CancellationFeeTests.swift` and left the stub in `Sources/HotelBooking` as it was. The package builds, and `swift test` fails as expected: all 7 cases report `NotImplemented()`.

**Fee by days before check-in (6 rows)**

This is a table-driven `@Test(arguments:)` over labelled tuples. Each row carries a scenario name, a booking value of 200, the days before check-in, and the expected fee.

| Days before check-in | Fee | Boundary |
|---|---|---|
| 30 | 0 | first free day |
| 29 | 100 | just inside the 50% band |
| 7 | 100 | last day of the 50% band |
| 6 | 160 | first day of the 80% band |
| 1 | 160 | last day of the 80% band |
| 0 | 200 | day of check-in |

The pairs 30/29, 7/6 and 1/0 straddle all three boundaries. I left out values further past a boundary, such as 45 days, because they add no new rule.

**Stay already started (1 test)**

A separate `@Test` calls `cancellationFee` with `daysBeforeCheckIn: -1` and expects `CancellationError.stayAlreadyStarted` via `#expect(throws:)`. I kept it apart because the fee table still states the rules without it. It also avoids branching in the table-driven test body.

**Assumptions**
- Booking values are whole euros, and the test values divide evenly by 2 and 5. How an odd amount rounds (for example 50% of 101) is not specified, so the tests don't pin it.
- `daysBeforeCheckIn` is a whole number of days, and a negative value means the stay has started. This follows the stub's doc comment.
- I used plain Swift Testing arguments. The `TableTest` dependency is in `Package.swift`, but I didn't use it, so I dropped the unused `import TableTest` from the test file. Say if you'd prefer its table syntax.