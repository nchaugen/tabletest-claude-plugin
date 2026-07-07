All tests compile and run (27 red — expected, since the implementation is a stub). Here's what I built:

**`build.gradle`** — added `org.tabletest:tabletest-junit:1.2.1` test dependency.

**`src/main/java/payroll/WeeklyPayCalculator.java`** — stub with `calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate)` throwing `UnsupportedOperationException`.

**`src/test/java/payroll/WeeklyPayCalculatorTest.java`** — six `@TableTest` methods, one per concern:
1. **Weekday regular/overtime** — 40-hour threshold made visible as a column, with boundary rows at 39/40/41 and a second rate to catch hardcoded values.
2. **Sunday double-time** — always 2×, no threshold of its own (tested with 45 hours).
3. **Holiday double-time** — same shape as Sunday.
4. **Combined categories** — proves the three categories add independently in the same week (e.g., weekday overtime + Sunday + holiday together).
5. **Floor at zero** — documented assumption in `@Description`: only the rate is rejected for negativity; hours aren't validated, so negative hours can still reach the calculation and must be floored at the *net* total, not per-category.
6. **Negative rate rejection** — `Throws?` column, with zero/positive rate as accepted boundary rows.

Ran `gradle test` to confirm everything compiles and the tables parse correctly — all 27 generated cases fail solely on the stub's `UnsupportedOperationException`, not on syntax or table-conversion errors. Next step would be implementing `calculateWeeklyPay` to turn these green.