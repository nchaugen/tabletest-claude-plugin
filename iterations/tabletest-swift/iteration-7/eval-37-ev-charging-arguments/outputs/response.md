I converted two of the three tests in `ChargingFeeTests.swift` to `@TableTest` and left the third as `@Test(arguments:)`. The file builds, the table formatter ran, and `swift test` passes.

- **`chargesEachStartedQuarter`:** This is now one table with a scenario column, charger, minutes and `Fee (€)?`. It still has all 8 cases. The scenario names describe the situation, such as "Last free minute" and "First minute of next quarter". They don't repeat the fee. The cells use the raw values `ac`, `fast` and `ultrafast`, which `Charger` already reads, so the app code is unchanged.
- **`chargesTheSessionMaximumForAnUnreadMeter`:** The `zip` pairs became three rows of charger and fee. I left out the scenario column because the charger already names each row.
- **`isFreeForFiveMinutes`:** I kept this as `@Test(arguments: Charger.allCases)`. The arguments come from the app, so a charger added later is tested automatically. A table would list the chargers by hand and lose that. I added a comment saying so.

I didn't add rows or columns. The case counts match the originals: 8, 3 and one per charger.