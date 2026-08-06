All 11 tests compile and fail as expected (red phase) — the tables parse correctly and all rows are being generated (3 + 6 + 2 = 11). This confirms the test design is sound before any implementation exists.

## Summary

Wrote three `@TableTest` tables in `src/test/java/payroll/PayCalculatorTest.java` against a stub `PayCalculator.calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate)` in `src/main/java/payroll/PayCalculator.java` (throws `UnsupportedOperationException`), plus added the `tabletest-junit` dependency to `build.gradle`.

- **`calculatesWeeklyPay`** — the core rule (weekday hours are a contribution-to-total combining case, so it's one table): regular hours, the 40-hour overtime boundary from both sides, Sunday double-time, holiday double-time, and a combined row proving the pieces sum correctly.
- **`flooredAtZero`** — the zero-floor rule, kept separate since it needs negative hours to trigger, which the main table never touches.
- **`rejectsNegativeHourlyRate`** — validation boundary at rate = 0.00 (accepted) vs -0.01 (rejected), following the skill's standard rejection-boundary pattern.

Two assumptions I made explicit in `@Description` (since the spec didn't cover them):
1. Overtime threshold is a standard 40-hour week, and Sunday/holiday hours don't count toward it or accrue their own overtime.
2. The zero-floor rule implies hours can be negative (e.g., correction entries) — that's the only way the sum could go negative given negative rates are rejected outright.

All 11 generated test cases currently fail with `UnsupportedOperationException`/assertion errors as expected — that's the red phase. Next step would be implementing `PayCalculator` to turn them green.