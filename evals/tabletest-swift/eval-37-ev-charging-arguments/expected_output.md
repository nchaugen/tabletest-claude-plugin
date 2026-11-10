Two `@TableTest` functions inside `ChargingFeeTests`, and the `Charger.allCases` test kept as a
`@Test(arguments:)`. The tests pass, and they run the same 14 cases as before.

## The sessions table

The named collection `sessions` becomes one table. Its tuple labels become the columns — the
charger, the minutes, and the euros as the `?` expectation — and it keeps the same eight rows with
the same values: fast 5 → 0, fast 6 → 3, fast 15 → 3, fast 16 → 6, fast 240 → 25, ac 40 → 3,
ultrafast 40 → 15, ultrafast 600 → 40. The `sessions` property is removed. The body makes one
`#expect`.

## The unread-meter table

The `zip` of two inline lists becomes a second table with the charger and the euros: ac → 10,
fast → 25, ultrafast → 40. Three rows, one per pair. It stays a table of its own, because it tests
a different function.

## The `allCases` test stays

`@Test(arguments: Charger.allCases)` takes its list from the app. A table would freeze that list,
and a charger added to the app would then go untested. So this test stays as it is. The response
or a comment says why.

## Equally good

A scenario column on either table is optional; when present, its names state the condition of the
row (the last free minute, the first started quarter), not the fee. `Charger` already has `String`
raw values and `CaseIterable`, so it converts from a cell with no change to the app. Column names,
titles, and the order of the functions are free.
