I wrote three `@TableTest` tables for `WashPlanner.outcome(of:)` in `Tests/WashingMachineTests/WashPlannerTests.swift`. All 21 rows pass against the existing code.

**The three tables**
- **Refusal (6 rows):** each fabric at its limit and one degree above it, with the limit as a column. This covers cotton 90 °C, synthetics 60 °C and wool 40 °C.
- **Wash time (11 rows):** the base time for each fabric at 40 °C, then temperature steps above 40 (50, 60 and 90 °C on cotton), and 30 °C on cotton adding nothing. Prewash, extra rinse and both together are covered, plus one row combining heat with both options.
- **Delayed start (4 rows):** no delay gives `runs`, and delays give `scheduled` with the same minutes. One delayed row includes the options, to show the wash time doesn't change.

**Test-side conformances:** `Fabric` and `WashOutcome` have no table-cell conversion, and I left the production code alone. I added `TableTextDecodable` conformances in the test file. Cells read `cotton`, or `scheduled(inHours: 3, minutes: 85)`.

**Assumptions** (also noted in a comment at the top of the file)
- Temperatures that aren't a multiple of 10 above 40 (such as 45 °C) are not tested, because the rules don't say how they round.
- A temperature below 40 °C adds no minutes.
- Combining a delayed start with a too-hot temperature is not tested, because the rules don't cover it.
- A delay of 0 hours is not tested, for the same reason.