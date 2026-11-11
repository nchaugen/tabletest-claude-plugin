I wrote four `@TableTest` tables for `WashPlanner.outcome(of:)` in `Tests/WashingMachineTests/WashPlannerTests.swift`. All 25 cases pass against the existing planner.

**One change outside the test file.** The table cells needed `Fabric` and `WashOutcome` to be readable, so I made two small edits in `Sources/WashingMachine/WashPlanner.swift`. `Fabric` now conforms to `CaseIterable` and `WashOutcome` to `Codable`. Both conformances are synthesised and change no behaviour. They let a cell say `cotton` or `runs(minutes: 170)`.

**The tables:**
1. **Refusal (6 cases):** each fabric has its own pair around its limit: 90/91 for cotton, 60/61 for synthetics, 40/41 for wool.
2. **Wash time (9 cases):** each fabric's base time at 40 °C. The cotton ladder runs 30, 40, 49, 50, 60 and 70 °C. Synthetics and wool each add one row.
3. **Options (6 cases):** prewash alone, extra rinse alone, both, and both again on another fabric and at a higher temperature.
4. **Delayed start (4 cases):** a blank delay runs now, a delay schedules with the same minutes, and a too-hot program on a delay is still refused.

**Assumptions where the doc comment is silent.** Each is written in the relevant test's doc comment, and the planner matches all of them:
- "Each 10 °C above 40" counts full steps only, so 49 °C adds nothing.
- A temperature below 40 °C adds nothing and takes the base time.
- A refusal takes precedence over a delayed start.

I didn't test a delay of 0 hours, because the rules don't say whether that means "now" or "scheduled in 0 hours".

**Known compromise.** The refusal table also shows the wash time on its accepted rows, because `outcome(of:)` returns refusal and time together and I can't split them without a narrower function.