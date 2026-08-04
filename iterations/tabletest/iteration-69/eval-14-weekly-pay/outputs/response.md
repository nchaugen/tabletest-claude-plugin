The build compiles and all 10 generated test cases fail against the stub as expected for the TDD red phase.

Everything's set up: `build.gradle` now pulls in `tabletest-junit:1.2.2`, `src/main/java/payroll/PayCalculator.java` has a stub `calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate)` that throws `UnsupportedOperationException`, and `src/test/java/payroll/PayCalculatorTest.java` has two `@TableTest` tables:

- **`calculatesWeeklyPayFromHoursAndRate`** (8 rows) — the core weighted-sum rule: regular vs. overtime pay straddling the 40-hour threshold, Sunday and holiday hours at double time, all types combined, a zero-rate edge case, and a negative-hours row proving the zero floor.
- **`rejectsNegativeHourlyRate`** (2 rows) — validates the rate boundary at 0 vs. -0.01, using a `Throws?` column.

Two assumptions worth flagging (documented in the `@Description` blocks): the 40-hour threshold is treated as a fixed rule rather than a parameter (matches your stated API of hours + rate only), and negative weekday hours are assumed to be valid input (e.g. a correction) since that's the only way the "pay cannot go below zero" floor is reachable given a non-negative rate is enforced separately — flag if that's not the intended source of negative pay.

All 10 generated cases currently fail against the stub, confirming the tests exercise real behavior once you implement `PayCalculator.calculateWeeklyPay`.