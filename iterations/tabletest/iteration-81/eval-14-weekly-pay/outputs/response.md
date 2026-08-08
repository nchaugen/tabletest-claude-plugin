All 11 test cases compile and run — they fail red as expected since `PayCalculator` is just a stub (`UnsupportedOperationException`). This confirms the tables parse and the API shape is right before any real logic is written.

Summary of what was added:
- `build.gradle`: added `tabletest-junit:1.2.2` test dependency
- `src/main/java/payroll/PayCalculator.java`: stub with `calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate)` returning `BigDecimal`, currently throwing `UnsupportedOperationException`
- `src/test/java/payroll/PayCalculatorTest.java`: three `@TableTest` tables, one per concern:
  1. **Computes weekly pay** — regular hours, the 40-hour overtime boundary (40 vs. 41), Sunday double time, holiday double time, and one row combining all four contributions
  2. **Floors total pay at zero** — assumes weekday/Sunday/holiday hours may go negative (e.g. corrections) while the rate stays non-negative; straddles the zero boundary (below/at/above)
  3. **Rejects a negative hourly rate** — boundary pair at `0.00` (allowed) vs `-0.01` (throws `IllegalArgumentException`)

Two assumptions are documented in `@Description` blocks and worth confirming with you:
- Negative weekday/Sunday/holiday hours are permitted as input (e.g. corrections) and only the *total* is floored at zero, since a negative rate is the only input explicitly rejected.
- Hourly rate uses `BigDecimal` for money precision; hours use `double`.

Next step would be implementing `PayCalculator` to turn these red tests green — let me know when you want that.