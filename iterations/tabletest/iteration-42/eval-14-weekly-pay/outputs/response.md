Everything's in place. Note: I couldn't run `./gradlew test` to verify compilation/execution because the Bash tool is failing at the session-init level in this environment (`EPERM: mkdir '/Users/nch/.claude/session-env/...'`) — this isn't specific to any command, it fails even on `echo ok`. You'll want to run the build yourself to confirm.

Summary of what I delivered:

**`build.gradle`** — added `org.tabletest:tabletest-junit:1.2.2` (JUnit Jupiter 6.0.3 already present, satisfies the ≥5.11 requirement).

**`src/main/java/payroll/WeekdayHoursSplit.java`** — a record `(regularHours, overtimeHours)`.

**`src/main/java/payroll/WeeklyPayCalculator.java`** — stub with two static methods, both throwing `UnsupportedOperationException`:
- `classifyWeekdayHours(double weekdayHours)` → `WeekdayHoursSplit`
- `calculateWeeklyPay(regularHours, overtimeHours, sundayHours, holidayHours, hourlyRate)` → `double`, throws `IllegalArgumentException` for negative rate once implemented

**`src/test/java/payroll/WeeklyPayCalculatorTest.java`** — three `@TableTest` tables, each isolating one concern:
1. **Classification** — splits weekday hours into regular/overtime at the 40-hour boundary (below, at, just past).
2. **Arithmetic** — takes regular/overtime/Sunday/holiday hours + rate and checks the weighted sum (1×, 1.5×, 2×, 2×), including a combined row and a floor-at-zero row.
3. **Rate validation** — positive/zero/negative rate, asserting `Throws?`.

Key assumptions (documented in the table's `@Description`, since this was ambiguous in the spec): hour inputs can be negative to represent timesheet corrections, which is the only way to construct a scenario that actually exercises the "pay cannot go below zero" floor — otherwise that rule would never trigger given the other stated constraints (non-negative hours, non-negative rate). Flag if that's not the intended semantics.