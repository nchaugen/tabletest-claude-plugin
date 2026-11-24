I wrote three `@TableTest` tables in `Tests/WashingMachineTests/WashPlannerTests.swift`. `swift build --build-tests` and `swift test` pass: 23 cases across the three tables.

| Table | Cases | What it covers |
|---|---|---|
| Refuses a temperature above the fabric's limit | 7 | The last accepted and first refused temperature for each fabric. A final row shows that a refusal wins over prewash, extra rinse and a delayed start. |
| Adds wash time for fabric, temperature, prewash and extra rinse | 12 | The base time per fabric, nothing added below 40 °C, the step at 49/50 and 59/60 °C, each addition alone, and combined. |
| Schedules a wash with a delayed start without changing its duration | 4 | No delay, a delay of 0, a delay of 8 hours, and a delay with all the additions. |

**One change to app code.** To read `cotton` and `runs(minutes: 120)` straight from the table cells, I added `CaseIterable` to `Fabric` and `Codable` to `WashOutcome` in `Sources/WashingMachine/WashPlanner.swift`. Both are synthesised conformances, so nothing behaves differently. Swift can't synthesise them from the test target. If you'd rather not touch the source, I can move the conversion into the tests, but that needs hand-written conversion code there.

The doc comment doesn't settle three things, so I pinned what the code does:
- **Part-way to a step:** I read "each 10 °C above 40" as full steps, so 49 °C adds nothing and 50 °C adds 10 minutes.
- **Below 40 °C:** nothing is added, so 30 °C takes the base time.
- **A delay of 0 hours:** it counts as scheduled. Only a blank delay starts the wash now.

If any of these readings is wrong, change the matching rows and the code together.

I also ran the table formatter, which realigned the columns.