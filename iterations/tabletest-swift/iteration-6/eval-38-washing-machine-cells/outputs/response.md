I wrote four `@TableTest` tables in `Tests/WashingMachineTests/WashPlannerTests.swift`. All 22 cases pass against the existing planner.

| Test | Rows | What it checks |
|---|---|---|
| `refusesATemperatureAboveTheFabricLimit` | 6 | Each fabric runs at its limit (cotton 90, synthetics 60, wool 40) and is refused 1 °C above it. |
| `timesAWashFromFabricAndTemperature` | 8 | The base time per fabric at 40 °C. Below 40 adds nothing, and cotton gets 3 consecutive 10 °C steps (+10 each). One synthetics row shows the temperature steps apply to other fabrics too. |
| `addsTimeForPrewashAndExtraRinse` | 4 | Every combination of prewash (+20) and extra rinse (+15), on cotton at 40 °C. |
| `schedulesADelayedWashWithTheSameDuration` | 4 | A wash starts now, with a 3-hour delay or a 12-hour delay, and the duration stays the same. A fourth row checks that the temperature limit wins over a delay. |

**One change outside the test file:** the cells needed to read the app types, so I added a one-word conformance to each in `Sources/WashingMachine/WashPlanner.swift`. `Fabric` is now `CaseIterable` and `WashOutcome` is now `Codable`. This adds no behaviour. If you'd rather not touch the source, the alternative is a test-local converter, which I avoided.

**Assumptions:** the doc comment doesn't cover these cases, so I left them untested and noted that in the test comments.
- **Partial 10 °C steps:** a temperature such as 45 °C, which is not a whole 10 °C step above 40. The code adds nothing for it, but the doc comment doesn't say that's intended.
- **A zero-hour delay:** the code treats it as scheduled, but the doc comment doesn't say.

I ran the table formatter, which reformatted the file. The tests ran with `swift test`.