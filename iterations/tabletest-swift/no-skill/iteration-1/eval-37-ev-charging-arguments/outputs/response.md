I converted two of the three tests in `Tests/ChargingTests/ChargingFeeTests.swift` to `@TableTest`, and `swift test` passes. That is 8 + 3 + 3 cases.

- **Fee per session:** the `sessions` tuple array is now a table with columns `Scenario | Charger | Minutes | Fee in euros?`. The 8 rows are unchanged, and each has a scenario name describing the case, such as "End of the free minutes" or "Capped at session maximum". This is the one that gains most, since the numbers and their names now sit side by side.
- **Unread meter:** the `zip([...], [...])` pairing is now a three-row `Charger | Fee in euros?` table. This also removes the risk of the two arrays drifting out of step.
- **Free for five minutes:** I left this as `@Test(arguments: Charger.allCases)` on purpose. It has no data to tabulate, and `allCases` means it also covers any charger added later. A hand-written table would lose that. I added a short comment saying so.

**Assumptions:**
- I kept the original 8 rows as they were. I did not merge cases into value sets or add boundary rows.
- The chargers are written as their raw values (`ac`, `fast`, `ultrafast`), which is how TableTest reads a `String`-backed enum.
- Columns map to parameters by position, so the parameter names (`euros`) stay as they were, and the header says `Fee in euros?`.