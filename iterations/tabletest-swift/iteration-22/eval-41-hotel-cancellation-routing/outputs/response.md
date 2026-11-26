I've written the tests in `Tests/HotelBookingTests/CancellationFeeTests.swift`. The stub is unchanged. `swift build --build-tests` succeeds. `swift test` fails every case with `NotImplemented()`, as expected before the logic exists.

**Fee tiers** are one `@TableTest`, with one row per tier. Days are whole days before check-in:

| Scenario | Days before check-in | Booking value | Fee? |
|---|---|---|---|
| Month or more ahead | 30, 90 | 500 | 0 |
| Between a week and a month | 7, 29 | 240 | 120 |
| Within the last week | 1, 6 | 250 | 200 |
| Arriving today | 0 | 180 | 180 |

- **Boundaries:** each tier's first and last day is listed, so every boundary is tested from both sides (30/29, 7/6, 1/0). Because the free tier has no upper limit, I used 90 as its "far ahead" value.
- **Booking values:** these differ per tier, so a wrong percentage can't pass by coincidence.

**Stay already started** is a separate plain `@Test`. It checks that `daysBeforeCheckIn: -1` throws `CancellationError.stayAlreadyStarted`. I kept it out of the table because the fee tiers still make sense without it. Only one negative day is tested, since further negative values would reach the same branch.

Assumptions:
- **Rounding:** the rules don't say how to round fractional fees, such as 50% of an odd amount. I chose booking values that give whole-euro fees, so the tests don't settle that question.
- **Negative days:** I treated any negative `daysBeforeCheckIn` as "stay started", following the stub's doc comment.
- **Fee and percentage:** the fee and the percentage behind it can't be observed separately, so the table checks them together. The table's doc comment says this.