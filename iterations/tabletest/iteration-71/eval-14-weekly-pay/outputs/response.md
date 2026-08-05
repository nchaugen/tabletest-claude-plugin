Repo is left in the expected red state: 16 tests, all failing against `UnsupportedOperationException` from the stub.

## Summary

Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle`, then:

- **`src/main/java/payroll/WeeklyPayCalculator.java`** — stub `calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate)` returning `BigDecimal`, throws `UnsupportedOperationException`.
- **`src/test/java/payroll/WeeklyPayCalculatorTest.java`** — 6 `@TableTest` methods (16 rows total), one per concern:
  1. Weekday regular/overtime tier (boundary at 40h, 1.5×)
  2. Sunday double-time, no overtime cap
  3. Holiday double-time, no overtime cap
  4. Additivity — the three components sum independently, no cross-effects
  5. Pay floored at zero
  6. Negative hourly rate rejected

**Assumptions worth flagging** (documented in each table's `@Description`):
- The 40-hour overtime threshold is a fixed business rule, not a runtime parameter.
- Hour inputs may be negative (timesheet corrections); only the *final total* is floored at zero, individual components aren't.
- Sunday/holiday hours never count toward the weekday overtime threshold and are never overtime-capped themselves.

I temporarily implemented the real logic to verify all 16 expected values are arithmetically correct (all passed), then reverted `WeeklyPayCalculator` back to the stub per your TDD request — the tests currently fail red against `UnsupportedOperationException`, ready for you to implement against.