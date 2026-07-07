Tests compile and run as expected: all 12 cases fail against the `NotImplemented()` stub, confirming the tests are wired correctly and will pass once `cancellationFee` is implemented.

I wrote `Tests/HotelBookingTests/CancellationFeeTests.swift` with two `@Test` groups:

1. **`feeByDaysBeforeCheckIn`** — 9 rows covering all four fee tiers with both sides of each boundary (30/29 days, 7/6 days, 1/0 days), plus a mid-tier value for each band. Booking values vary per row but are chosen so percentages land on whole numbers, keeping expected fees literal and traceable.
2. **`cancellationAfterStayStarted`** — a separate throws-only test (per the skill's rule to isolate expected-exception cases) covering three negative `daysBeforeCheckIn` values, asserting `CancellationError.stayAlreadyStarted` via `#expect(throws:)`.

The stub in `CancellationPolicy.swift` was left untouched — `swift build` succeeds and `swift test` shows all 12 test cases failing with `NotImplemented()`/`stayAlreadyStarted` mismatches, exactly as expected before the fee logic is written.