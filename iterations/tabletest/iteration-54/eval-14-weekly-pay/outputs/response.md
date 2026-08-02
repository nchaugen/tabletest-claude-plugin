I've written the test suite driven by the calculation rules, plus a stub `WeeklyPayCalculator`:

**`src/test/java/payroll/WeeklyPayCalculatorTest.java`** — 5 `@TableTest` tables, one per concern:
1. **Weekday regular/overtime pay** — boundary rows at 0, below/at/just-past the 40-hour threshold.
2. **Sunday double-time** — zero and non-zero hours.
3. **Holiday double-time** — zero and non-zero hours (kept separate from Sunday rather than merged, since merging would require branching logic in the test body, which the design rules disallow).
4. **Floor at zero** — a combining table showing the interaction between negative and positive components (see assumption below).
5. **Negative-rate rejection** — zero rate accepted, negative rate rejected, using a `Throws?` column.

**`src/main/java/payroll/WeeklyPayCalculator.java`** — stub `calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate)` that throws `UnsupportedOperationException`, per your request to stub the calculation logic.

Ran `gradle test`: all 13 generated cases execute and fail against the stub as expected — this confirms the tables parse and convert correctly, and gives you a clean red baseline to implement against.

**One assumption worth flagging**: the "total pay cannot go below zero" rule is only reachable if some input can produce negative raw pay, but the spec doesn't say hours can be negative — only that a negative *rate* is rejected. I assumed weekday hours may go negative (e.g., representing a correction/deduction) to make the floor rule testable, and documented this as an open assumption in that table's `@Description`. If hours are actually always ≥ 0 in your domain, that table's premise is wrong and should be revisited — let me know and I'll adjust.