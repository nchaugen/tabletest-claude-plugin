It compiles cleanly and all 7 cases fail against the unimplemented stub, exactly as expected. The stub in `CancellationPolicy.swift` was left untouched.

Wrote `Tests/HotelBookingTests/CancellationPolicyTests.swift` with two Swift Testing suites:

1. **`feeByDaysBeforeCheckIn`** — table-driven over `(daysBeforeCheckIn, bookingValue, fee)`, one case per tier and both sides of each boundary (30/29, 7/6, 1/0), with varying booking values per row so each expected fee is traceable as `bookingValue × tier rate`.
2. **`rejectsCancellationAfterStayStarted`** — separate `#expect(throws:)` test for `daysBeforeCheckIn: -1` → `CancellationError.stayAlreadyStarted`, kept apart from the fee table since striking it would still leave the fee ladder intact as its own rule.

Assumptions (documented in code comments):
- `daysBeforeCheckIn` negative means the stay has started (per the existing doc comment on the stub).
- `bookingValue` is irrelevant to the rejection rule, so it's held at one representative value there rather than varied.