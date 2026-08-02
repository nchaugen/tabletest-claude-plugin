It compiles cleanly and all 10 cases fail against the unimplemented stub, as expected.

Created `Tests/HotelBookingTests/CancellationPolicyTests.swift` with two table-driven tests:

1. **`cancellationFeeByDaysBeforeCheckIn`** — a 7-row tier ladder covering both sides of every boundary (29/30, 6/7, 0/1) plus the day-of-check-in case, with one extra row (`bookingValue: 2000`) proving the fee scales proportionally rather than being hardcoded to €1000.
2. **`cancellationAfterStayHasStarted`** — separate `#expect(throws:)` test for negative days, since removing it would leave the tier ladder still stating a complete rule (per the skill's rejection-separation test).

Assumption stated in comments: booking value is held constant at €1000 except where explicitly varied to demonstrate proportionality, and it's irrelevant to the "already started" rule so it's fixed at one valid value there.

Ran `swift test` — build succeeds, all 10 cases fail with `NotImplemented()` as expected since the stub is untouched.