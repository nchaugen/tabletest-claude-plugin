I wrote the tests in `Tests/HotelBookingTests/CancellationFeeTests.swift`. They compile with `swift build --build-tests`, and all 9 cases fail under `swift test`. Every one fails because the stub throws `NotImplemented`, which is the expected state before the fee logic exists. I left the stub unchanged.

**Fee table.** One `@TableTest` covers the fee for each notice period. Its columns are Booking Value (€), Days Before Check-in and Fee (€)?. The rows are:

| Row | Days before check-in | Booking value (€) | Fee (€) |
|---|---|---|---|
| A month or more ahead | 30 and 365 | 200 | 0 |
| Between a week and a month | 7 and 29 | 200 | 100 |
| Between a day and a week | 1 and 6 | 200 | 160 |
| On the day of check-in | 0 | 200 | 200 |
| Different booking value | 7 | 450 | 225 |

- **Boundaries:** each tier row covers its own first and last day, so the boundaries at 30/29, 7/6 and 1/0 are each tested from both sides. The "month or more ahead" tier has no upper end, so 365 stands in for it.
- **Booking value:** the 450 row checks that the fee scales with the booking value, so a flat fee would fail.
- **Stay started:** this is a separate plain `@Test` that expects `CancellationError.stayAlreadyStarted` for -1 days. I split it from the table because the fee tiers still make sense without it. One row is enough, since a stay further along is rejected for the same reason.

**Assumptions**
- A negative `daysBeforeCheckIn` means the stay has started, as the stub's doc comment says.
- Rounding of fractional fees isn't specified, so I chose booking values whose fees come out in whole euros (the doc comment says so).
- `bookingValue` is a whole number of euros.