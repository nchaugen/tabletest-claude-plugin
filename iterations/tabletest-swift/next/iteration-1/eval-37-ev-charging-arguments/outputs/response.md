I converted two of the three tests in `Tests/ChargingTests/ChargingFeeTests.swift` to `@TableTest`. The build succeeds and all tests pass.

- **Fee per session:** this is now one table with `Charger`, `Minutes` and `Euros?` columns. It has the same 8 rows as before, each with a scenario name. `Charger` is a `String`-raw-value enum, so the cells read `fast`, `ac` and `ultrafast` directly. I left the app code unchanged.
- **Unread meter:** the `zip` pairs became a 3-row table of charger and maximum euros. It runs the same 3 cases as before.
- **Free for five minutes:** I kept this as `@Test(arguments: Charger.allCases)`. The chargers come from the app, so a new charger is covered without editing the test. A hand-written table would lose that. I added a comment saying so.

The case counts are unchanged at 8, 3 and 3. I renamed the fee test to `chargesEachStartedQuarterAfterTheFreeMinutes`, so it reads as an action. The `Charger` enum already conforms to `CaseIterable`, so the new tests needed no changes outside this file.