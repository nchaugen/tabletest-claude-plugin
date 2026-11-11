I converted two of the three tests in `Tests/ChargingTests/ChargingFeeTests.swift` to `@TableTest`, and the third stays a plain `@Test(arguments:)`. `swift test` passes with the same case counts as before: 8, 3 and 3.

- **Fee per session** is now one table with columns `Charger`, `Minutes` and `Fee (EUR)?`. Each row has a scenario name saying which situation it covers, such as the free minutes, a quarter boundary or a long session. It keeps all 8 original rows, including both sides of the 5/6 and 15/16 minute boundaries.
- **Unread meter** was a `zip` of chargers and maximums, so each pair is now one row. I dropped the scenario column because the charger already names each row.
- **"Every charger is free for five minutes"** is unchanged, with a comment saying why. Its chargers come from `Charger.allCases` in the app, so a new charger is covered automatically. A hand-written table would lose that.

I gave the tables titles in the form "Charges …" so they read as actions the code performs. `Charger` already has `String` raw values (`ac`, `fast`, `ultrafast`), so the cells read directly with no conversion code.