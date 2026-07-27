I've written the tests and stub implementation for weekly pay calculation. Summary:

**Files added/changed:**
- `build.gradle` — added `tabletest-junit:1.2.2` test dependency
- `src/main/java/payroll/WeekdayHoursSplit.java` — record for regular/overtime hour split
- `src/main/java/payroll/WeeklyPayCalculator.java` — stub API (all methods throw `UnsupportedOperationException`)
- `src/test/java/payroll/WeeklyPayCalculatorTest.java` — three `@TableTest` methods

**API decomposition** (one table per rule, per the tabletest skill's guidance to separate rules from arithmetic):
1. `splitWeekdayHours(weekdayHours)` — classifies weekday hours into regular (≤40) vs. overtime (>40)
2. `computeGrossPay(regularHours, overtimeHours, sundayHours, holidayHours, hourlyRate)` — applies the 1x/1.5x/2x/2x multipliers, floors at zero
3. `validateHourlyRate(hourlyRate)` — rejects negative rates

**Assumptions made (documented in `@Description`, since this is non-interactive):**
- Weekday hours can be negative (representing a prior-period correction), which is the only way "total pay cannot go below zero" becomes a testable, non-trivial rule — otherwise non-negative hours × a validated non-negative rate can never go negative.
- A top-level `calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate)` stub exists to wire the pieces together for real callers, but isn't separately tested since it's pure delegation whose contract is already covered by the three tables above.

Verified with `gradle compileTestJava` (compiles cleanly) and `gradle test` (all 14 generated rows run and fail red with `UnsupportedOperationException`, confirming the tables are wired correctly against the stub). Next step is implementing the three methods to turn these green.