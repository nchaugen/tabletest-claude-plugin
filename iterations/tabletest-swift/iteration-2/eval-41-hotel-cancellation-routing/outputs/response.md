I wrote the tests in `Tests/HotelBookingTests/CancellationFeeTests.swift`. The stub is untouched. The tests build, and all 10 cases fail with `NotImplemented()`, as intended before the fee logic exists.

The package already depends on tabletest-swift, so the tests use `@TableTest` rather than plain `@Test(arguments:)`.

**Fee table** (`chargesTheFeeForTheDaysRemaining`) — 9 cases from 6 rows. Booking value is 200 euros except in the last two rows:

| Days before check-in | Fee (EUR) |
|---|---|
| 30 and 90 | 0 |
| 7 and 29 | 100 |
| 1 and 6 | 160 |
| 0 | 200 |

- **Tier edges:** each tier states both its first and last day, so every boundary is tested from both sides (29/30, 6/7, 0/1). 90 stands for "well ahead", since the free tier has no upper limit.
- **Other booking value:** two extra rows use a booking value of 150, so the fee can't be a constant per tier. They expect 75 at 15 days and 120 at 3 days.

**Rejection test** (`rejectsCancellationOnceTheStayHasStarted`) is a separate plain `@Test`. It checks that `daysBeforeCheckIn: -1` throws `CancellationError.stayAlreadyStarted`. I kept it out of the table because the fee tiers still make sense without it. Values below -1 would reject for the same reason, so there is only one case.

**Assumptions** (also in the doc comment):
- Amounts are whole euros and days are whole days. Rounding isn't specified, so the booking values were picked to give whole-euro fees.
- 0 days (check-in day) can still be cancelled, at the full fee. Only a negative value means the stay has started, which matches the stub's doc comment.