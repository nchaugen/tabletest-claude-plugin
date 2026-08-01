I've written the tests to `Tests/HotelBookingTests/CancellationFeeTests.swift`. Summary:

**Two tables**, decomposed by concern:

1. **`cancellationFeeByDaysBeforeCheckIn`** — the tier rule. Booking value held fixed at €1000 (declared in a comment, divides evenly by every rate). 7 cases cover all four tiers and both sides of every boundary (30/29, 7/6, 1/0), plus the accept→reject boundary (0 vs -1 days) folded into the same table since it's one boundary — the last accepted case beside the first rejected one — using a `fee`/`error` column pair rather than a separate throwing test.
2. **`cancellationFeeScalesWithBookingValue`** — the arithmetic. Window held fixed at 15 days (50% tier); 3 cases (€0, €100, €1000) prove the fee is proportional to booking value rather than a hardcoded constant.

A shared `attemptCancellationFee` helper captures the return value or thrown `CancellationError` as plain optionals, so each test body only asserts equality — no branching.

**Assumptions stated in-file:** booking values were chosen to divide evenly by the applicable rate (50%/80%) so expected fees don't depend on an unspecified rounding rule, since the prompt didn't define one.

Verified: `swift build` and `swift build --build-tests` both succeed (stub untouched), and `swift test` shows all 10 generated cases failing with clean `Expectation failed` diffs against the `NotImplemented` stub — confirming the tests compile and are correctly wired to fail until the fee logic is implemented.