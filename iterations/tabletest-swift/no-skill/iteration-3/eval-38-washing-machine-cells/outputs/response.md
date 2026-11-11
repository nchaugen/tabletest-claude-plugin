I wrote the tests in `Tests/WashingMachineTests/WashPlannerTests.swift` as four `@TableTest` tables. `swift test` passes: 4 tests, 23 rows.

| Test | Rows | What it covers |
|---|---|---|
| Refuses a temperature above the fabric's limit | 6 | For each fabric, the limit itself (90, 60, 40 °C) is accepted and one degree above is refused with the right limit. |
| Adds up the wash time from fabric, temperature, prewash and extra rinse | 13 | Base time per fabric, 10 minutes per 10 °C step, nothing added below 40 °C, prewash +20, extra rinse +15, and everything combined. |
| Schedules a delayed start without changing the wash time | 3 | No delay runs now, a 1-hour delay and a 12-hour delay schedule, and the wash time matches a program started now. |
| Refuses a too-hot program even when its start is delayed | 1 | A refusal takes precedence over scheduling. |

**Setup needed:** `Fabric` has no built-in table conversion, so I added a small `TableTextDecodable` extension in the test file. It reads `cotton`, `synthetics` and `wool` from the cells.

**Assumptions:** the doc comment doesn't cover these cases, so I left them untested.
- Temperatures are whole °C. A temperature between the 10 °C steps, such as 45, is not tested.
- A temperature below 40 °C adds nothing and does not shorten the wash.
- A delay of 0 hours is not tested. The code currently returns `.scheduled(inHours: 0, …)`.
- Refusal comes before scheduling.

I loaded the `table-driven-testing` skill, which is for projects without TableTest. I used it only for table design and used the library's own `@TableTest` syntax.