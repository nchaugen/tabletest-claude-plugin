Summary of what was built (tests written first, TDD-style; stub implementation added just so everything compiles — all tests are red as expected until real logic is implemented):

**Package `payroll`**, decomposed into four concerns, each its own `@TableTest`:

1. **`OvertimeSplitTest`** — `OvertimeSplit.from(weekdayHours)`: splits weekday hours into regular/overtime at the 40-hour threshold (boundary rows at 40, 41, and beyond).
2. **`HourlyRateTest`** — `new HourlyRate(rate)`: two tables (accept vs. reject) since the assertion logic differs — accepting valid rates (incl. zero boundary) asserts equality; rejecting negative rates asserts `IllegalArgumentException`.
3. **`PayCalculatorTest.calculatesPay`** — the core arithmetic: regular pay, 1.5× overtime, 2× Sunday, 2× holiday, combined, plus the zero-floor rule.
4. **`PayCalculatorTest.calculatesWeeklyPay`** — a small integration table proving the public entry point (`calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, rate)`) correctly wires the split and arithmetic together; detailed edge cases aren't repeated here.

**Assumption documented in-table** (`@Description`): the "total pay cannot go below zero" rule only has teeth if hours can be negative (e.g., pay-correction adjustments) — this wasn't specified, so I noted it as an assumption directly in the `calculatesPay` table description rather than asking, and added rows proving the floor kicks in only when the net raw total is negative.

Also added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` (JUnit Jupiter was already 6.0.3, which satisfies the ≥5.11 requirement).

Ran `gradle test`: compiles cleanly, 22 tests execute, 20 fail as expected (stub methods throw `UnsupportedOperationException`; the rate-rejection rows fail because validation isn't implemented yet). The 2 passing tests are the "accepts non-negative rate" cases, which trivially pass against the unvalidated stub record. Next step is implementing `OvertimeSplit.from`, `HourlyRate`'s validation, and `PayCalculator`'s two methods to turn these green.