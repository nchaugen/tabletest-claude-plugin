That's fine — no need for git status here.

## Summary

Wrote `Tests/HotelBookingTests/CancellationFeeTests.swift` with three table-driven `@Test` functions against the existing `cancellationFee(bookingValue:daysBeforeCheckIn:)` stub:

1. **`feeTierByDaysBeforeCheckIn`** — booking value fixed at 1000, varies `daysBeforeCheckIn` across all four tiers with both sides of every boundary (30/29, 7/6, 1/0), plus a far-in-advance case.
2. **`feeScalesWithBookingValue`** — days fixed within each tier, varies `bookingValue` to confirm the percentage arithmetic isn't hardcoded to one value. I chose booking values that divide evenly by each tier's rate (50%/80%) since the spec doesn't define a rounding rule for fractional cents — that's a stated assumption, not something baked silently into the numbers.
3. **`cancellationAfterStayStarted`** — separate test using `#expect(throws: CancellationError.stayAlreadyStarted)` for negative `daysBeforeCheckIn`, kept apart from the value-returning tests per the skill's error-separation rule.

Verified with `swift build` (compiles clean) and `swift test` (all 17 generated cases fail against the `NotImplemented()` stub, as intended — the stub itself was left untouched).