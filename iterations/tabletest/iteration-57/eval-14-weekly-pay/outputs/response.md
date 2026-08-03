All 4 tables parse and are correctly formatted.

## Summary

Set up the project for TDD with `org.tabletest:tabletest-junit:1.2.2` added to `build.gradle`, and created:

- **`src/main/java/payroll/HoursSplit.java`** — record holding `regularHours`/`overtimeHours`
- **`src/main/java/payroll/PayCalculator.java`** — stub with `splitWeekdayHours`, the core `calculateWeeklyPay(regularHours, overtimeHours, sundayHours, holidayHours, hourlyRate)`, and a public-facing overload `calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate)` that wires the two together (all throw `UnsupportedOperationException`)
- **`src/test/java/payroll/PayCalculatorTest.java`** — 4 tables, one per rule:
  1. `splitsWeekdayHours` — classifies weekday hours into regular/overtime at the 40-hour boundary
  2. `combinesHoursIntoWeeklyPay` — regular/overtime/Sunday/holiday hours combine at their respective rates (1x/1.5x/2x/2x) into one total
  3. `floorsWeeklyPayAtZero` — pay never goes negative
  4. `rejectsNegativeHourlyRate` — validates the rate boundary (accepts 0, rejects just below it)

Key assumption (documented in the floor table's `@Description`): hour inputs can be negative to represent correction/adjustment entries — that's the only way the "never below zero" rule can ever engage, since rates are non-negative and ordinary hours are too.

Verified: `gradle test` compiles cleanly and all 16 generated test cases run against the stub, currently failing with `UnsupportedOperationException` (or assertion mismatches) as expected — confirming the tables are correctly wired to drive the real implementation next.