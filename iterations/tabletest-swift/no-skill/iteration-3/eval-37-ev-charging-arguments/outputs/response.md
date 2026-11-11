I converted two of the three tests in `Tests/ChargingTests/ChargingFeeTests.swift` to `@TableTest`. `swift test` passes with the same case counts as before: 8, 3 and 3.

- **`chargesEachStartedQuarter`** is now a table with a `Scenario` column, one row per old tuple. The rows are unchanged. I named each row after its condition, for example "Last free minute" and "First minute of the second quarter". I did not add new rows.
- **`chargesTheSessionMaximumForAnUnreadMeter`** is now a `Charger | Fee (€)?` table. This replaces the `zip` of two arrays, so each charger sits next to its maximum.
- **`isFreeForFiveMinutes`** is still a plain `@Test(arguments: Charger.allCases)`. It has a single column, so a table would add nothing. `allCases` also covers any charger added later. A short comment in the file says this.

Two things to know:
- **Scenario column:** it has no matching function parameter, so TableTest uses it only as the row name in test output.
- **Column order:** TableTest maps columns to parameters by position, not by name. The header `Fee (€)?` therefore lines up with the `euros` parameter.