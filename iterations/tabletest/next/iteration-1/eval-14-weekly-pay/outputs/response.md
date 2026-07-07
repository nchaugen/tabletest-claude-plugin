I've set up the test-driven skeleton for weekly pay calculation:

**`build.gradle`** — added `tabletest-junit:1.2.1` and `assertj-core:3.27.3` alongside the existing JUnit Jupiter 6.0.3.

**`src/main/java/com/example/payroll/PayCalculator.java`** — stub with three methods, each throwing `UnsupportedOperationException`:
- `calculateWeekdayPay(hours, rate)` — regular/overtime split
- `calculateDoubleTimePay(hours, rate)` — shared rule for Sunday/holiday hours (identical formula, so one method covers both)
- `calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, rate)` — the public feature entry point; owns rate validation

**`src/test/java/com/example/payroll/PayCalculatorTest.java`** — 4 `@TableTest` methods (20 rows total), decomposed by concern:
1. Weekday pay: regular vs. overtime, boundary at 40/41 hours
2. Double-time pay: shared Sunday/holiday rule
3. Weekly pay: combines all three categories + the zero-floor rule
4. Hourly rate validation: negative rejected via `IllegalArgumentException`, zero as the valid boundary

Ran `gradle test` to confirm: everything compiles, all tables parse correctly (fixed two issues along the way — `Executable` is `org.junit.jupiter.api.function.Executable`, and `Class<?>` conversion needs the fully-qualified exception name), and all 20 tests fail cleanly against the stub's `UnsupportedOperationException` — the expected red state before implementation.

**Assumption flagged in the test file's `@Description`:** the "total pay cannot go below zero" rule only has teeth if hours can be negative (e.g., payroll correction/adjustment entries), since the spec has no other source of negative pay. I documented this as an open question to confirm with product rather than guessing silently.

Next step would be implementing `PayCalculator` to turn these green — let me know if you'd like me to proceed with that.