Everything compiles and all 11 cases fail against the unimplemented stub, exactly as expected.

Wrote `Tests/HotelBookingTests/CancellationFeeTests.swift`:

- **`cancellationFeeByDaysBeforeCheckIn`** — 9 rows covering all four fee tiers with both sides of every boundary (30/29, 7/6, 1/0), varying `bookingValue` (200, 340, 150) so the expected fees are traceably `50%`/`80%`/`100%` of the input rather than hardcoded to one number.
- **`cancellationAfterStayStarted`** — separate test asserting `CancellationError.stayAlreadyStarted` via `#expect(throws:)` for negative `daysBeforeCheckIn`, kept apart from the value-returning table per the skill's error-separation rule.

Assumption: "30 or more days" and "7 to 29 days" etc. are inclusive on both ends as stated, verified by boundary rows on each side. The `cancellationFee` stub was left untouched (still throws `NotImplemented()`); `swift build --build-tests` succeeds and `swift test` shows all 11 cases failing against the stub as intended.