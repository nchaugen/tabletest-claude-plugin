I've written the tests before any real implementation:

**`build.gradle`** — added the `tabletest-junit:1.2.1` test dependency (JUnit Jupiter 6.0.3 already satisfies the ≥5.11 requirement).

**`src/main/java/PayCalculator.java`** — stub with one method, `calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate)`, that throws `UnsupportedOperationException`.

**`src/test/java/PayCalculatorTest.java`** — three `@TableTest` tables, one per concern:
1. `shouldCalculateWeeklyPay` — the core rule: regular vs. overtime split at the 40-hour weekday threshold (boundary rows at 30/40/41/50 hours), Sunday and holiday double-time, and a combined scenario mixing all three.
2. `shouldRejectNegativeRate` — negative rate throws `IllegalArgumentException`; zero is the accepted boundary.
3. `shouldNotAllowTotalPayBelowZero` — the pay floor, using negative hours as a stand-in for pay corrections.

One thing worth flagging: the "total pay cannot go below zero" rule only makes sense if hours can go negative (a correction/adjustment), since non-negative hours × non-negative rate can never produce a negative total on its own. I made that assumption explicit in a `@Description` on that table as an open question — worth confirming with whoever owns the requirements before implementing.

Ran `gradle test`: it compiles, and all 14 generated cases fail with `UnsupportedOperationException` as expected (red state). Next step would be implementing `calculateWeeklyPay` to turn these green.