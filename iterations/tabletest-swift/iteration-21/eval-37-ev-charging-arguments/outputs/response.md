I converted two of the three tests to `@TableTest` and left the third as `@Test(arguments:)`. `swift test` passes, and the case count is unchanged at 8 + 3 + 3.

- **`chargesEachStartedQuarter`:** it's now one table with the columns `Scenario | Charger | Minutes | Fee (euros)?`. Each of the eight tuples is a row, with a scenario name that describes the situation rather than the fee. The `Charger` cells read the raw values `ac`, `fast` and `ultrafast`, so the app needed no changes.
- **`chargesTheSessionMaximumForAnUnreadMeter`:** the `zip` pairs became three rows, one per charger.
- **`isFreeForFiveMinutes`:** I kept this as `@Test(arguments: Charger.allCases)`. The chargers come from the app, so a new charger gets covered automatically. A hand-written table would lose that. A comment in the file says why.

The table macro compiled cleanly, and I ran `tabletest-format`, which realigned the table columns. The file on disk now reflects that formatting.

I made two assumptions:
- **Rate and maximum columns:** I didn't add columns for the per-quarter rate or the session maximum. They're private to `ChargingFee`, so the test couldn't read them.
- **Titles:** I kept the existing display names, apart from rewording the unread-meter title to "Charges the session maximum for an unread meter" so it reads as an action like the others.