I wrote four `@TableTest` tables in `Tests/WashingMachineTests/WashPlannerTests.swift`. `swift build --build-tests` and `swift test` pass, and the formatter has aligned the tables.

I also made one change you didn't ask for, in `Sources/WashingMachine/WashPlanner.swift`. I added `CaseIterable` to `Fabric` and `Codable` to `WashOutcome`. That lets table cells read `cotton` and `runs(minutes: 120)` directly. It adds no behaviour. The tool's "formatted 2 files" message also covers `WashPlanner.swift`, so check that diff for stray whitespace changes.

The four tables:
- **`timesAWashFromFabricAndTemperature`** covers base time per fabric and the 10 °C steps above 40. Each temperature band is a value set whose ends are the band's own first and last values. There is also a row below 40.
- **`refusesATemperatureAboveTheFabricLimit`** has a pair of rows around each fabric's limit: 90/91 for cotton, 60/61 for synthetics and 40/41 for wool. The limits differ per fabric, so each gets its own pair.
- **`addsTimeForPrewashAndExtraRinse`** crosses prewash and extra rinse (four rows), with cotton at 40 °C held fixed.
- **`schedulesADelayedWashWithoutChangingItsDuration`** covers no delay, a delay, a delay with both options, and a delay with a refused temperature. The last row shows the refusal wins over the delay.

The doc comment doesn't settle two things, so I assumed the following. Both match the current code, and both are written in the first table's doc comment.
- The surcharge counts whole 10 °C steps, so 49 °C adds nothing and 50 °C adds 10.
- A wash below 40 °C takes the 40 °C time.

The tables compare the whole outcome, so rows around the limit include the run time, for example `runs(minutes: 170)`. The refusal cases can't be checked without it.

I didn't test a delay of 0 hours, because the rules don't say whether it counts as scheduled.