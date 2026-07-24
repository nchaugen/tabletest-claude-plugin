# Narration — eval-14-weekly-pay

The agent's visible narration and file writes, in order, distilled from
`conversation.jsonl`. Thinking text is absent unless the model returns it in the
clear (Claude 5-family models encrypt it).

I'll set up the project (dependency, stub implementation) and write the tests incrementally, one concern at a time.

JUnit Jupiter 6.0.3 already satisfies the ≥5.11 requirement. I just need to add the tabletest-junit dependency.

**Edit /private/var/folders/fz/zxx_qbvj49gbql83cc5l47pr0000gn/T/eval-skill-1783416908758/eval-14-work/build.gradle (edit)**

Now the stub implementation, decomposed into the four functions the tables will drive (weekday pay, premium pay, combination with the zero-floor, and the validated entry point):

**Write /private/var/folders/fz/zxx_qbvj49gbql83cc5l47pr0000gn/T/eval-skill-1783416908758/eval-14-work/src/main/java/payroll/WeeklyPayCalculator.java (24 lines)**

Now the test class, starting with the weekday pay concern (base rate vs. overtime):

**Write /private/var/folders/fz/zxx_qbvj49gbql83cc5l47pr0000gn/T/eval-skill-1783416908758/eval-14-work/src/test/java/payroll/WeeklyPayCalculatorTest.java (32 lines)**

Now the premium pay concern (Sunday and holiday hours, both always at double time):

**Edit /private/var/folders/fz/zxx_qbvj49gbql83cc5l47pr0000gn/T/eval-skill-1783416908758/eval-14-work/src/test/java/payroll/WeeklyPayCalculatorTest.java (edit)**

Now the combination concern (summing components, with the zero-floor rule):

**Edit /private/var/folders/fz/zxx_qbvj49gbql83cc5l47pr0000gn/T/eval-skill-1783416908758/eval-14-work/src/test/java/payroll/WeeklyPayCalculatorTest.java (edit)**

Now the rate-validation concern (rejecting negative rates):

**Edit /private/var/folders/fz/zxx_qbvj49gbql83cc5l47pr0000gn/T/eval-skill-1783416908758/eval-14-work/src/test/java/payroll/WeeklyPayCalculatorTest.java (edit)**

Finally, the end-to-end integration table that exercises `calculateWeeklyPay` across combined scenarios (including the zero-rate boundary):

**Edit /private/var/folders/fz/zxx_qbvj49gbql83cc5l47pr0000gn/T/eval-skill-1783416908758/eval-14-work/src/test/java/payroll/WeeklyPayCalculatorTest.java (edit)**

Let me check the final file and remove the unused `@Test` import, then compile/run to confirm structure is valid (expecting red failures from the stub).

**Edit /private/var/folders/fz/zxx_qbvj49gbql83cc5l47pr0000gn/T/eval-skill-1783416908758/eval-14-work/src/test/java/payroll/WeeklyPayCalculatorTest.java (edit)**

Now let's compile and run to confirm the table syntax and wiring are correct (expecting failures against the stub — that's the intended red state):

No wrapper — using the installed `gradle` directly.

Everything compiles cleanly and all 27 rows run (each failing on the stub's `UnsupportedOperationException`, exactly the expected TDD red state) — confirming the table syntax, type conversions, and method wiring are correct.

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

