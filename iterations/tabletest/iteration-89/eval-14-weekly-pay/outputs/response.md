Everything compiles and parses correctly — all 15 rows fail exactly as expected against the stub (red state before implementation). This confirms the table syntax, type conversion, and rejection-column handling are all wired correctly.

## Summary

Wrote `src/test/java/payroll/PayCalculatorTest.java` with 5 `@TableTest` tables against a stub `PayCalculator.calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate)` in `src/main/java/payroll/PayCalculator.java` (throws `UnsupportedOperationException`), plus the `tabletest-junit` dependency in `build.gradle`.

Table breakdown (one rule per table):
1. **Weekday base/overtime split** — straddles the 40-hour boundary (39/40/41 hours)
2. **Sunday & holiday double-time** — collapsed into one table since both follow the identical "always 2×" rule, with a row proving Sunday hours past 40 don't stack additional overtime
3. **Combining table** — the one genuine interaction: proves the 40-hour threshold is scoped to weekday hours only, not the total across categories (a 35-weekday + 10-Sunday week stays non-overtime)
4. **Zero floor** — straddles zero from both sides, under the documented assumption (stated in `@Description`) that hours can be negative to represent corrections
5. **Rate validation** — boundary pair (0 accepted, -0.01 rejected) using a `Throws?` column

15 rows total, all currently red against the stub — ready for you to implement `calculateWeeklyPay` to turn them green.

One assumption worth flagging: nothing in the spec says hours can be negative, but "total pay cannot go below zero" is otherwise unreachable with non-negative hours and a non-negative rate, so I read it as guarding against negative hour adjustments. If that's not the intended scenario, let me know and I'll rework table 4 around the actual trigger.