I've written the test suite and stub implementation. Here's a summary:

**`build.gradle`**: added `org.tabletest:tabletest-junit:1.2.2` test dependency.

**`src/main/java/payroll/`** (stubs only, no logic — `UnsupportedOperationException`):
- `HoursSplit.java` — record holding `regularHours`/`overtimeHours`
- `WeeklyPayCalculator.java` — `classifyWeekdayHours(weekdayHours, overtimeThreshold)` and `calculatePay(regularHours, overtimeHours, sundayHours, holidayHours, hourlyRate)`

**`src/test/java/payroll/WeeklyPayCalculatorTest.java`** — three `@TableTest`s, one per concern:
1. `splitsWeekdayHoursIntoRegularAndOvertime` — classification, boundary at the 40-hour threshold (below/at/just past/well past)
2. `calculatesTotalPayFromClassifiedHoursAndRate` — arithmetic given already-classified hours: regular @1×, overtime @1.5×, Sunday/holiday @2×, and a combined row
3. `rejectsNegativeHourlyRate` — validation boundary at rate 0 (accepted, value set with a typical positive rate) vs. just below 0 (rejected with `IllegalArgumentException`)

Documented assumptions in `@Description`: overtime/double-time multipliers are fixed business rules, not configurable policy (so not columns); hours are assumed non-negative since the spec only calls out rejecting a negative rate; and given that, the "pay cannot go below zero" floor is unreachable by any input beyond the zero-hours baseline, so no separate clamp-triggering row exists.

Verified with `gradle compileTestJava` (compiles clean) and `gradle test` (13 cases execute, all fail red against the stubs as expected for TDD).