I converted two of the three tests in `ChargingFeeTests.swift` to `@TableTest` and left the third as `@Test(arguments:)`. `swift test` passes, and the case counts match the old tests (8, 3 and 3). The tabletest formatter then realigned the columns, and that edit is the file change you saw.

- **`chargesEachStartedQuarter`** is now one table with columns Charger, Minutes and `Fee (euros)?`. The 8 tuples became 8 rows. The scenario names describe where each row sits against the rule, such as "Just past the grace period" and "Into the second quarter". They don't repeat the fee or the input values. `Charger` has `String` raw values, so cells read `fast`, `ac` and `ultrafast` with no conversion code.
- **`chargesTheSessionMaximumForAnUnreadMeter`** is a three-row table. Each row is one pair from the old `zip`.
- **`isFreeForFiveMinutes`** stays as `@Test(arguments: Charger.allCases)`. A new `Charger` case is then tested automatically, and a hand-written table would lose that. I left a comment in the file saying so.

I assumed the tests should keep their existing display names, so both tables use the title form `@TableTest("…", table: …)`. I also named the rows by charger tier (slowest, middle, fastest) because the old `zip` pairs had no descriptions.

I added a doc comment describing the free minutes and the per-quarter rate and session maximum. I didn't add columns for the rates or the maximum, because the `Charging` module keeps those private.