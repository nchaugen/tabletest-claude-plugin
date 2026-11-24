I wrote the tests in `Tests/HotelBookingTests/CancellationFeeTests.swift`. They compile and currently fail, as expected. The failures are all `NotImplemented()` from the stub, which I left untouched. `swift build --build-tests` succeeds, and `swift test` runs 9 cases that all fail.

The package already depends on `tabletest-swift`, so the fee tests use `@TableTest`.

**Fee tests (6 cases from 4 rows).** The booking value is held at 250 EUR.

| Days before check-in | Fee |
|---|---|
| 30 | 0 |
| 7 and 29 | 125 |
| 1 and 6 | 200 |
| 0 | 250 |

Each tier's first and last day are in the table, so every boundary is checked from both sides: 30 against 29, 7 against 6, and 1 against 0. The free tier has no upper limit, so it has one row.

**Rejection test (3 cases).** A separate `@Test` expects `CancellationError.stayAlreadyStarted` when `daysBeforeCheckIn` is -1. The booking value is varied across 0, 250 and 1,000, because it shouldn't affect the outcome. I kept this out of the fee table because the fee table still makes sense without it. I tested only -1, since lower values would add no new information.

**Assumptions** (also written in the doc comments):
- `daysBeforeCheckIn` is a whole number, 0 means the day of check-in, and negative means the stay has started. This matches the stub's doc comment.
- 250 EUR gives whole-euro fees at 50% and 80%. The spec doesn't say how to round fractional euros, so I didn't test it. You may want a rounding rule and rows for it.
- A zero booking value is only covered in the rejection test, not as a fee row.