I wrote four `@TableTest` tables for `WashPlanner.outcome(of:)` in `Tests/WashingMachineTests/WashPlannerTests.swift`. `swift test` passes all 22 cases.

I also had to edit `Sources/WashingMachine/WashPlanner.swift`, which you said was done. `Fabric` is now `CaseIterable` and `WashOutcome` is `Codable`. Table cells can't read those types without those conformances, and they add no behaviour. The `tabletest-format` command reported two files formatted. The only edit I made to `WashPlanner.swift` was that `sed` change, so I didn't check whether the formatter touched it too.

| Test | Cases | What it covers |
|---|---|---|
| Refuses a temperature above the fabric's limit | 6 | Each fabric at its limit and one degree past it: cotton 90/91, synthetics 60/61, wool 40/41. |
| Sets the wash time from the fabric and the temperature | 7 | Base time per fabric. Cotton at 30, 40, 50 and 60 °C to show +10 minutes per step. Synthetics at 60 °C. |
| Adds time for a prewash and an extra rinse | 5 | Neither, each alone, both together, plus a wool row showing the additions don't depend on fabric. |
| Schedules a wash with a delayed start | 4 | Blank delay runs now. Two different delays carry the same wash time. A refused temperature wins over a delay. |

**Assumptions**, also noted in the doc comments:
- Below 40 °C the wash time stays at the 40 °C time. The rules don't say, but the code does this.
- Temperatures between steps (such as 45 °C) and a delay of 0 hours aren't in the rules, so I left them untested.

Every table has the fabric and temperature as columns, so no value is fixed only in the test body.