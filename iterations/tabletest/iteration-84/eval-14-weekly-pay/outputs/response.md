All 12 test cases compile and run — the correct red state for TDD (all fail against the stub `WeeklyPayCalculator.calculate`, which throws `UnsupportedOperationException`).

Three `@TableTest` methods, one table per concern:

1. **`calculatesWeeklyPay`** — weekday regular/overtime, Sunday double-time, and holiday double-time combine into one weekly total (they're contributions to a single sum). Covers the 40-hour boundary from both sides, Sunday-only, holiday-only, and a row combining all three.
2. **`floorsWeeklyPayAtZero`** — pay never goes negative. Assumption stated in `@Description`: negative hours can occur (e.g. correction entries) even though only a negative *rate* is explicitly rejected; any negative subtotal floors to zero.
3. **`rejectsNegativeHourlyRate`** — boundary at rate = 0 (accepted) vs. rate = -0.01 (rejected with `IllegalArgumentException`).

`WeeklyPayCalculator.calculate(weekdayHours, sundayHours, holidayHours, hourlyRate)` is stubbed to `throw new UnsupportedOperationException`, so the calculation logic is next.

One assumption worth flagging: I used `double` for hours/rate/pay rather than `BigDecimal`. That's fine for this arithmetic-only stage, but if rounding/precision matters for real money, worth revisiting before implementation.

Ready to implement the calculation logic when you want to move to green.