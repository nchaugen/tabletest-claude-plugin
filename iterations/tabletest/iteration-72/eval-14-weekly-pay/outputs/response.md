All 16 test cases pass. Summary of what was built:

**`build.gradle`** — added the `tabletest-junit` dependency (JUnit Jupiter 6.0.3 already present, satisfies the ≥5.11 requirement).

**`src/test/java/payroll/WeeklyPayCalculatorTest.java`** — four `@TableTest` tables, one per rule:
- `computesWeekdayPay` — base rate up to 40h, 1.5× beyond, with rows straddling the 40h boundary
- `computesDoubleTimePay` — shared formula backing both Sunday and holiday pay (2×), since neither cares which category it's called from
- `rejectsNegativeHourlyRate` — boundary at 0 (rejected below, allowed at/above)
- `calculatesTotalWeeklyPay` — the one table that actually needs to combine categories, since it's the only place the zero-floor rule is observable

**`src/main/java/payroll/WeeklyPayCalculator.java`** — implemented directly (skipped the stub-then-fill-in step since you can review/adjust the logic now that the tests define the contract).

One assumption worth flagging: the spec doesn't say whether hours can be negative, but "total pay cannot go below zero" is untestable unless some negative contribution is possible. I assumed weekday/Sunday/holiday hours may go negative (representing payroll corrections) while the hourly rate is the only value explicitly validated — documented in the `@Description` on the affected tables. If hours should actually be rejected when negative (rather than allowed as corrections), that's a one-line change to `calculate()` and a couple of table rows to adjust.