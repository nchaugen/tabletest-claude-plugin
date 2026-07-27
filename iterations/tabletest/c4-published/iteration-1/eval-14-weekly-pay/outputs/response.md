Done. I set up the weekly-pay feature test-first with TableTest, split into focused concern tables:

**`src/test/java/payroll/PayCalculatorTest.java`**
1. **Weekday hours classification** (`classifyWeekdayHours`) — 3 rows proving the 40-hour regular/overtime split (below, at, just past the threshold).
2. **Weekly pay arithmetic** (`calculatePay`) — 7 rows isolating each rate multiplier (regular ×1, overtime ×1.5, Sunday ×2, holiday ×2), one row combining all four, a zero-rate boundary row, and a negative-hours row proving pay floors at zero.
3. **Hourly rate validation** (`validateHourlyRate`) — rejects negative rates with `IllegalArgumentException`.
4. **One standalone `@Test`** wiring `calculateWeeklyPay` end-to-end (weekday split + Sunday/holiday) through the real public entry point.

**`src/main/java/payroll/`** — `PayCalculator` (stub methods throwing `UnsupportedOperationException`) and `WeekdayHoursBreakdown` record, giving tests something to compile against without pre-empting the implementation.

Assumptions documented in `@Description`: hours aren't restricted to non-negative, so the "cannot go below zero" rule is treated as a floor on the total rather than a rejection (only the rate is explicitly rejected when negative).

Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` and ran `gradle test` — it compiles cleanly and all 12 test cases fail with `UnsupportedOperationException`/assertion errors as expected for the red phase of TDD. Next step is implementing the real logic to turn these green.