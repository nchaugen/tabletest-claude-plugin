I wrote the tests in `Tests/WashingMachineTests/WashPlannerTests.swift` as four `@TableTest` tables. `swift test` passes all 24 cases.

The tables are:
- **`refusesATemperatureAboveTheFabricLimit`** (6 rows): each fabric gets a row at its limit and one 1 °C past it. The limits are cotton 90, synthetics 60 and wool 40.
- **`lengthensTheWashForEachTenDegreesAboveForty`** (8 rows): the base time per fabric, plus times at 30, 39, 40, 49, 50 and 60 °C. The 40, 50 and 60 °C rows show the 10 minutes is added per step, not once.
- **`addsTimeForPrewashAndExtraRinse`** (5 rows): neither option, each alone, both together, and both on wool.
- **`schedulesADelayedStartWithoutChangingTheWash`** (5 rows): no delay, a delayed plain wash, and a long wash run with and without a delay to show the time is the same. A refusal row shows that a too-hot program is still refused when a delay is set.

**Change outside the test file:** `@TableTest` cells need the app types to be convertible. I added `CaseIterable` to `Fabric` and `Codable` to `WashOutcome` in `Sources/WashingMachine/WashPlanner.swift`. Both are synthesised, so behaviour is unchanged.

The format command also reformatted that file, I think only whitespace. There's no git repo here, so I couldn't diff it to check.

Assumptions, also noted in the doc comments:
- A partial 10 °C step adds nothing, so 49 °C adds nothing and 50 °C adds 10. This follows the implementation.
- Below 40 °C the wash isn't shortened.
- A delay of 0 hours isn't tested, because the rules don't say whether it counts as delayed. The code returns `scheduled(inHours: 0, …)`.
- The limit table expects `runs(minutes:)` for accepted rows, because the planner exposes no function that only checks the temperature.