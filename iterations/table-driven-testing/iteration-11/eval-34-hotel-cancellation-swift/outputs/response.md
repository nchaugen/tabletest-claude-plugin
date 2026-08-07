Compiles cleanly and every case fails against the unimplemented stub, exactly as expected before the fee logic is written.

## Summary

Created `Tests/HotelBookingTests/CancellationPolicyTests.swift` with two table-driven Swift Testing tests:

1. **`cancellationFeeByDaysBeforeCheckIn`** — one ladder table covering all four fee tiers, with both sides of each boundary (30/29, 7/6, 1/0).
2. **`cancellationRejectedAfterStayStarted`** — separate test for the `stayAlreadyStarted` error (removing it wouldn't leave the fee ladder stating nothing, so it's a distinct concern), with two rows at the same boundary (`daysBeforeCheckIn: -1`) varying `bookingValue` to show the error doesn't depend on it.

**Assumptions made** (documented in the file):
- Booking values in each row are chosen to divide evenly by that tier's percentage (e.g. 340 → 170 at 50%), since rounding behavior for fractional fees isn't specified by the rules and shouldn't be pinned down by these tests.
- The classification (which tier) and the arithmetic (% of booking value) are verified together in one table rather than split into two, since no narrower function exposing the tier/percentage exists in the public API — splitting would require reimplementing that logic inside the test.
- `daysBeforeCheckIn` is documented in the stub as whole days, so day-granularity boundaries are correct as the finest unit.

`swift build` and `swift test` both confirm the tests compile and fail cleanly against the `NotImplemented()` stub — ready for the fee logic to be implemented next.