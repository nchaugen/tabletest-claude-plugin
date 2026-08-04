Compiles cleanly and all 12 cases fail against the unimplemented stub, as expected.

I wrote `Tests/HotelBookingTests/CancellationFeeTests.swift` with three table-driven `@Test`s:

- **`feeByDaysBeforeCheckIn`** — the tier ladder (free / 50% / 80% / 100%), 7 rows covering every tier and both sides of every boundary (30, 29, 7, 6, 1, 0), plus one row proving the free tier ignores booking value.
- **`roundsFeeToNearestEuro`** — isolates the rounding rule for booking values that don't divide evenly by the tier percentage, since the API fuses classification and arithmetic into one function.
- **`rejectsCancellationAfterStayStarted`** — uses `#expect(throws:)` for the `stayAlreadyStarted` case, with the boundary value (-1) and a further value with a different booking value to show rejection doesn't depend on it.

Two assumptions I made and documented in the file's header comment (since the spec didn't pin them down):
1. Fees round to the nearest whole euro, ties rounding away from zero (e.g. 50% of €33 = €16.5 → €17).
2. Classification and arithmetic are tested together in one table (rather than split into two, as the skill's general guidance prefers) because `cancellationFee` is the only public entry point — there's no exposed intermediate value to test separately.