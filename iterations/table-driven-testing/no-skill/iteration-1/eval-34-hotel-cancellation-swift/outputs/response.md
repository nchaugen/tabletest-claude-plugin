It compiles cleanly and all 12 test cases fail exactly as expected (against the unimplemented stub, which still throws `NotImplemented`). The stub in `CancellationPolicy.swift` was left untouched.

Summary:
- Added `Tests/HotelBookingTests/CancellationFeeTests.swift` with two `@Test` functions using Swift Testing's `arguments:` parameterization.
- `fee(testCase:)` covers all 9 pricing cases (free ≥30 days, 50% for 7–29 days, 80% for 1–6 days, full price on day 0), each with distinct booking values and boundary days to catch off-by-one and hardcoded-value bugs.
- `stayAlreadyStarted(daysBeforeCheckIn:)` covers three negative-day inputs, asserting `CancellationError.stayAlreadyStarted` is thrown.
- Removed the now-unneeded `Tests/HotelBookingTests/.gitkeep`.
- Verified with `swift build`/`swift test`: build succeeds, all 12 parameterized cases currently fail against the stub (as intended pre-implementation).