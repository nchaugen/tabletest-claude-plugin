# Narration — 14

The agent's visible narration and file writes, in order, distilled from
`conversation.jsonl`. Thinking text is absent unless the model returns it in the
clear (Claude 5-family models encrypt it).

This matches the tabletest skill's trigger for writing tests for a Java feature — I'll invoke it now.

Now I'll add the tabletest-junit dependency and write the test class following the skill's design principles.

**Edit /private/var/folders/fz/zxx_qbvj49gbql83cc5l47pr0000gn/T/eval-skill-1785164919741/eval-14-work/build.gradle (edit)**

**Write /private/var/folders/fz/zxx_qbvj49gbql83cc5l47pr0000gn/T/eval-skill-1785164919741/eval-14-work/src/main/java/payroll/WeekdayHoursSplit.java (5 lines)**

**Write /private/var/folders/fz/zxx_qbvj49gbql83cc5l47pr0000gn/T/eval-skill-1785164919741/eval-14-work/src/main/java/payroll/WeeklyPayCalculator.java (15 lines)**

Now the test class with three `@TableTest` methods.

**Write /private/var/folders/fz/zxx_qbvj49gbql83cc5l47pr0000gn/T/eval-skill-1785164919741/eval-14-work/src/test/java/payroll/WeeklyPayCalculatorTest.java (78 lines)**

Now let's compile and run the tests to verify everything wires up correctly.

No gradle wrapper present. I'll use the system-installed gradle.

`@DisplayName` is JUnit's own annotation, not tabletest's. Fixing the import.

**Edit /private/var/folders/fz/zxx_qbvj49gbql83cc5l47pr0000gn/T/eval-skill-1785164919741/eval-14-work/src/test/java/payroll/WeeklyPayCalculatorTest.java (edit)**

Tests compile and run cleanly against the stub — all 13 generated cases fail with `UnsupportedOperationException` (or the wrong-exception assertion for the rate table), which is the expected red state before implementation.

**What was written:**
- `src/main/java/payroll/WeeklyPayCalculator.java` — stub with `calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate)` and `splitWeekdayHours(weekdayHours)`
- `src/main/java/payroll/WeekdayHoursSplit.java` — small record for the regular/overtime hours split
- `src/test/java/payroll/WeeklyPayCalculatorTest.java` — 3 `@TableTest` methods:
  1. **`splitsWeekdayHours`** — isolates the 40-hour classification rule (regular vs. overtime hours) as its own table, so the pay table's expected values are one-step arithmetic rather than fused classify+compute
  2. **`calculatesWeeklyPay`** — the public API, exercising regular pay, overtime pay, Sunday double-time, holiday double-time, an additive combination, and the zero-floor rule
  3. **`rejectsNegativeHourlyRate`** — boundary pair (`0.00` allowed vs. `-0.01` rejected) using a `Throws?` column
- `build.gradle` — added `org.tabletest:tabletest-junit:1.2.2` test dependency

**Assumptions stated in the test `@Description`** (since this is non-interactive, delivered rather than asked):
- Hour inputs may be negative (e.g. correcting a prior overpayment) — that's the only way "total pay cannot go below zero" is reachable given a non-negative rate, so the floor test drives pay negative via `weekdayHours = -10`.
- Package name `payroll` chosen since none was specified.
- Money uses `BigDecimal`, hours use `double`; pay is compared via `compareTo` (not `equals`) so scale differences don't cause false failures.

Next step is implementing `WeeklyPayCalculator` to turn these red tests green.

