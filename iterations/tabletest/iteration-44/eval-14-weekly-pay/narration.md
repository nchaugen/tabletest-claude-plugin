# Narration — 14

The agent's visible narration and file writes, in order, distilled from
`conversation.jsonl`. Thinking text is absent unless the model returns it in the
clear (Claude 5-family models encrypt it).

**Edit ./build.gradle (edit)**

Now creating the stub implementation and the `WeekdayHoursSplit` record.

**Write ./src/main/java/payroll/WeekdayHoursSplit.java (5 lines)**

**Write ./src/main/java/payroll/WeeklyPayCalculator.java (24 lines)**

**Write ./src/test/java/payroll/WeeklyPayCalculatorTest.java (74 lines)**

Gradle is available. Let's compile the test sources to verify everything is wired correctly.

Compiles cleanly. Now let's run the tests to confirm they fail red (as expected against stubs) rather than erroring on setup/parsing.

All 14 rows parsed correctly and fail red against the stub (`UnsupportedOperationException`), exactly as expected for TDD's starting point.

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

