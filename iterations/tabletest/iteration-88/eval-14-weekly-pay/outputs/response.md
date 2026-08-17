Both `@TableTest` tables parse and are correctly aligned, and the full suite compiles and runs red (10/10 failing) against the stub, which is the correct TDD starting point.

**What's in place:**
- `build.gradle` — added the `tabletest-junit` dependency (JUnit Jupiter 6.0.3 already satisfies the ≥5.11 requirement).
- `src/main/java/payroll/PayCalculator.java` — stub `calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate)` that throws `UnsupportedOperationException`.
- `src/test/java/payroll/PayCalculatorTest.java` — two tables:
  - `calculatesWeeklyPay` (8 rows): the 40-hour overtime boundary (40 vs 41 hours), Sunday and holiday double-time, a row proving Sunday/holiday hours don't count toward the weekday overtime cap, and three rows proving the zero-floor (negative raw pay → 0, exact zero, just-above-zero unclipped).
  - `rejectsNegativeHourlyRate` (2 rows): boundary at rate = 0 (accepted) vs -0.01 (rejected with `IllegalArgumentException`).

**Assumptions documented in `@Description`** (worth confirming with you):
- The 40-hour threshold and 1.5×/2× multipliers are fixed policy, not configurable inputs — so they're not table columns, only pinned via the 40/41-hour boundary rows.
- Weekday/Sunday/holiday hours can be negative (e.g. correction entries), which is what makes the "cannot go below zero" rule reachable — since only the hourly rate is stated as rejectable, not hours.

Next step is implementing `PayCalculator.calculateWeeklyPay` to turn these red.