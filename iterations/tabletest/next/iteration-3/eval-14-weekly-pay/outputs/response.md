I've written the tests (red/TDD state confirmed) and stub implementation for the weekly pay calculator.

**Files added:**
- `src/main/java/payroll/HourSplit.java` — record holding classified regular/overtime hours
- `src/main/java/payroll/WeeklyPayCalculator.java` — stub with 3 static methods, each throwing `UnsupportedOperationException`
- `src/test/java/payroll/WeeklyPayCalculatorTest.java` — 4 `@TableTest` methods, 21 total cases
- `build.gradle` — added `org.tabletest:tabletest-junit:1.2.1` test dependency

**Test decomposition** (rule vs. arithmetic vs. composition vs. validation, each its own concern):
1. `shouldSplitWeekdayHoursIntoRegularAndOvertime` — the 40-hour threshold rule (boundary rows at 0, under, at, just-over, well-over, and a fractional-hours case)
2. `shouldCalculatePayFromClassifiedHours` — the arithmetic (1.5× overtime, 2× Sunday/holiday), taking pre-split hours directly so this table isn't re-deriving the threshold
3. `shouldCalculateWeeklyPay` — the composed public feature (raw weekday/Sunday/holiday hours → total pay), covering straight pay, overtime, all categories combined, and the zero-floor rule
4. `shouldRejectNegativeHourlyRate` — negative rate rejection via `IllegalArgumentException`, using a value set to show it's rejected regardless of hours worked

**Assumptions made (documented in `@Description` on the affected tables, since this is a non-interactive run):**
- Only the hourly rate is validated for negativity, per the spec's explicit wording ("a negative hourly rate is not allowed"). Hours are not independently validated.
- Since valid (non-negative hours, non-negative rate) inputs can never produce a negative total mathematically, the "total pay cannot go below zero" rule is only reachable if a raw hours value is negative (e.g., from an upstream correction) — flagged as an open question in the description rather than silently assumed.
- Fractional hours are supported (e.g., 40.5 hours splits into 40 regular + 0.5 overtime).

Ran `gradle test` locally: all 21 generated cases fail with `UnsupportedOperationException` from the stub — confirming the tests compile, the table wiring is correct, and we're at a clean TDD red state ready for implementation.