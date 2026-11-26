I wrote three `@TableTest` tables in `Tests/WashingMachineTests/WashPlannerTests.swift`. All 21 cases pass under `swift test`.

**One change outside the test file.** In `WashPlanner.swift` I added `CaseIterable` to `Fabric` and `Codable` to `WashOutcome`. The tables read `runs(minutes: 170)` and `cotton` straight from their cells, and that needs those conformances on the app types. They are synthesised, so behaviour doesn't change.

**The tables:**
- **`refusesATemperatureAboveTheFabricLimit`** (7 rows) has each fabric at its limit and one degree past it: cotton 90/91, synthetics 60/61, wool 40/41. A last row shows that a too-hot program with a delayed start is still refused.
- **`addsWashTimeForTemperatureAndOptions`** (11 rows) covers:
  - the base time per fabric;
  - the 10 °C steps: below 40, 49, 50 and 60;
  - prewash only, extra rinse only, and both;
  - a hot synthetics wash with both options, which shows temperature and options adding together.
- **`schedulesADelayedStart`** (3 rows) has no delay, a delay, and a delay with a prewash. The last two show that the wash time is unchanged.

**Assumptions:**
- A partial 10 °C step adds nothing, so 49 °C adds 0 minutes and 50 °C adds 10. The doc comment is ambiguous here, so I pinned what the code does.
- A temperature below 40 °C doesn't shorten the wash. This is also read from the code.
- A zero-hour delay isn't specified, so no row covers it.
- The "at limit" rows in the refusal table also assert the wash minutes. The planner returns refusal and duration as one result, so they can't be separated. The duration rules have their own table.