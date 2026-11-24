I wrote four `@TableTest` tables for `WashPlanner.outcome(of:)` in `Tests/WashingMachineTests/WashPlannerTests.swift`. `swift test` passes all 24 cases.

**One change outside the test file.** The table cells (`cotton`, `runs(minutes: 170)`) need conversion support. I added `CaseIterable` to `Fabric` and `Codable` to `WashOutcome` in `Sources/WashingMachine/WashPlanner.swift`. These are synthesised conformances, so no behaviour changed. If you'd rather leave the app code untouched, I can add hand-written conversions in the test target instead.

**The tables**
- **`refusesATemperatureAboveTheFabricLimit` (6 rows):** the last accepted temperature and the first refused one (1 °C higher) for each fabric. The expected cell shows the fabric's limit, e.g. `tooHot(limit: 90)`.
- **`addsTenMinutesForEachTenDegreesAboveForty` (7 rows):** cotton at 30, 40, 50, 60 and 70 °C, plus the 40 °C base time for synthetics and wool.
- **`addsTimeForPrewashAndExtraRinse` (6 rows):** all four option combinations on cotton, both options on wool, and both options on hot cotton.
- **`schedulesADelayedWashWithoutChangingItsDuration` (5 rows):**
  - a blank delay (starts now);
  - a delay of 0 hours;
  - a delay of 8 hours;
  - a delay combined with both options;
  - a too-hot wash that has a delay, which is still refused.

**Assumptions** (the doc comments also say these)
- A temperature below 40 °C adds no time. The rules only say "each 10 °C above 40", but the code does this and I assert it with the 30 °C row.
- A delay of 0 hours counts as scheduled, not as "starts now". The rules only say a missing delay starts now.
- I only used temperatures in steps of 10 °C for the duration table. The rules don't say what a part-step like 45 °C should do, so I left it untested.
- The accepted outcomes in the first table include their minutes. The planner only returns the whole outcome, so I couldn't separate refusal from duration there.