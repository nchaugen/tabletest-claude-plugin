I wrote three `@TableTest` tables for `WashPlanner.outcome(of:)` in `Tests/WashingMachineTests/WashPlannerTests.swift`. All 24 rows pass against the existing code under `swift test`.

**The tables**
- **`refusesTemperatureAboveFabricLimit`** (6 rows) pins each fabric's limit from both sides: cotton 90/91, synthetics 60/61, wool 40/41. The `Refusal limit?` column is blank when the program is accepted.
- **`addsMinutesToTheBaseWashTime`** (14 rows) covers the base time per fabric, the +10 minutes per 10 °C step, prewash (+20), extra rinse (+15), and the two together. Temperature steps run through cotton 50, 60 and 70 °C, so +10 per step is distinguishable from a flat surcharge. One row combines every addition at 90 °C and expects 205.
- **`schedulesTheWashWhenStartIsDelayed`** (4 rows) checks no delay, a delay, and a delay with a longer wash (same minutes as an undelayed run). A fourth row shows that a refused program stays `tooHot` even with a delay set.

**Helpers in the test file**
- `Fabric` conforms to `CaseIterable` and `TableValueDecodable`, so cells like `cotton` convert to the enum.
- `WashOutcome` conforms to `TableTextDecodable`, so cells like `scheduled(inHours: 8, minutes: 120)` convert to the enum.
- A small `refusalLimit` property returns the limit for a refused outcome and nil otherwise. It lets the first table compare an optional value without branching in the test body.

**Assumptions the doc comment doesn't settle**
Rows pin the first three; the fourth is untested.
- A temperature below 40 °C adds no time and takes no less than 40 °C (row at 30 °C).
- Only a full 10 °C step above 40 adds time: 49 °C adds nothing, 50 °C adds 10 (rows at 49 and 50).
- "Just above the limit" means limit + 1 °C, since temperatures are whole degrees.
- I did not test a `delayStartHours` of 0. The code treats it as `scheduled(inHours: 0, …)`, but the doc doesn't say whether that means "start now".

That background search for a local copy of TableTest's docs has finished. I no longer need it, since I got the `@TableTest` syntax from the project's GitHub page. It doesn't change anything in the tests, and the earlier result stands: all 24 rows across the three tables pass.