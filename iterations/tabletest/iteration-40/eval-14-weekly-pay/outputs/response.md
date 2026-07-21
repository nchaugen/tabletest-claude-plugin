Setup complete. Summary of what was added:

**`build.gradle`** — added `org.tabletest:tabletest-junit:1.2.1` (JUnit Jupiter 6.0.3 already meets the 5.11+ requirement).

**`src/main/java/payroll/WeeklyPayCalculator.java`** — stub with 4 methods, each throwing `UnsupportedOperationException`:
- `calculateWeekdayPay(weekdayHours, hourlyRate)` — base rate + 1.5x overtime beyond 40h
- `calculatePremiumPay(sundayHours, holidayHours, hourlyRate)` — both always at 2x
- `combineWeeklyPay(weekdayPay, premiumPay)` — sums components, floors at zero
- `calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate)` — validates rate, wires the above together

**`src/test/java/payroll/WeeklyPayCalculatorTest.java`** — 5 `@TableTest` methods (27 rows total), one per concern:
1. Weekday pay rule (boundary at 40h, overtime multiplier, different rates)
2. Premium pay rule (Sunday/holiday individually and combined)
3. Combine + floor-at-zero rule (isolated from the rate arithmetic)
4. Negative-rate rejection (`Throws?` column)
5. End-to-end integration scenarios mixing all components, plus the zero-rate boundary

Ran `gradle test`: all 27 rows compile and execute, each failing with the stub's `UnsupportedOperationException` — the expected TDD red state before implementation.

**Assumptions documented in `@Description`** (since the spec doesn't cover these): negative hours are treated as pay adjustments/corrections — the only way "total pay cannot go below zero" would ever bind, given rates and hours are otherwise non-negative; and Sunday/holiday hours are a separate premium category that doesn't interact with the 40-hour weekday overtime threshold.

Next step is implementing the four methods to turn this suite green — let me know when you want that.