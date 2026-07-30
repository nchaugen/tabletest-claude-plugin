I've written failing (TDD red) tests plus stub production code for the weekly pay calculation. Here's what's in place:

**Production stubs** (`src/main/java/payroll/`)
- `PayCalculator.splitWeekdayHours(weekdayHours)` → `WeekdaySplit` (regular/overtime hours)
- `PayCalculator.calculatePay(regularHours, overtimeHours, sundayHours, holidayHours, hourlyRate)` → `BigDecimal`
- `PayCalculator.calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate)` → `BigDecimal` (top-level entry point)

All three throw `UnsupportedOperationException` for now.

**Tests** (`src/test/java/payroll/PayCalculatorTest.java`), three `@TableTest` methods plus one wiring `@Test`:
1. **Split classification** — weekday hours → regular/overtime, boundary-tested at 40 (below/at/just past).
2. **Pay arithmetic** — regular/overtime/Sunday/holiday hours × rate → total pay, one row per hour-type multiplier plus a combined row and a floor-at-zero row.
3. **Rate validation** — zero/positive rate accepted, negative rate throws `IllegalArgumentException`.
4. A single plain `@Test` proving the split and arithmetic wire together correctly end-to-end.

I split classification from arithmetic (per the skill's "Separate Rules from Arithmetic" guidance) rather than one fused table, since predicting total pay from raw weekday hours alone requires classifying first and computing second — two rules, two tables.

**Assumption flagged in `@Description`:** the floor-at-zero rule is only reachable if hours can go negative (e.g., a correction/adjustment), since rate is validated non-negative and hours×rate is otherwise always ≥ 0. I documented this as an open question rather than guessing silently — worth confirming if corrections aren't actually in scope.

Also added the `tabletest-junit:1.2.2` dependency to `build.gradle`. Ran `gradle test`: everything compiles, the table rows generate correctly (13 test cases), and all fail against the stubs as expected for red-phase TDD. Cleaned up the `build/` directory afterward.