I wrote four `@TableTest` tables in `Tests/WashingMachineTests/WashPlannerTests.swift`, and all 25 cases pass against the existing planner.

**The tables:**
- **`refusesATemperatureAboveTheFabricLimit`** (6 rows): each fabric gets a pair at its limit and one step above it, since the limits differ (cotton 90, synthetics 60, wool 40).
- **`timesAWashFromFabricAndTemperature`** (9 rows): base times at 40 °C, plus 10 minutes per 10 °C above 40. Cotton runs through 40, 50, 60 and 70 °C, so the rule reads as per step rather than a flat extra. A colder wash takes no less time.
- **`addsTimeForPrewashAndExtraRinse`** (5 rows): all four option combinations on cotton, plus both options on wool to show the additions don't depend on fabric.
- **`schedulesADelayedStartWithTheSameWashTime`** (5 rows): start now, two delays, a different fabric, and a delay with a too-hot temperature. That last row shows a refusal beats scheduling.

**One change to the app code:** I added `CaseIterable` to `Fabric` and `Codable` to `WashOutcome` in `Sources/WashingMachine/WashPlanner.swift`. TableTest needs them to read cells like `cotton` and `scheduled(inHours: 3, minutes: 140)`. They add no behaviour. If you'd rather not touch the app, say so and I'll move the conversion into the test target.

**Assumptions:**
- **Temperatures:** I only use multiples of 10 °C, because the doc comment doesn't say what 45 °C should do. The code currently rounds it down.
- **Delay:** I left out a 0-hour delay, which is also unspecified.
- **Refusal table:** the rows at the limit show the full wash time, so that table checks minutes as well as refusal.