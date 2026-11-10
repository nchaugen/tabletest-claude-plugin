`@TableTest` tables inside `WashPlannerTests` that cover the four rules of the doc comment. The
tests pass. Two app types need one word each to convert from a cell, and nothing else in the app
changes.

## App types in cells

- **`WashOutcome`** is an enum with associated values. Add `Codable` to it in the app; a cell then
  reads the case as Swift spells it: `runs(minutes: 120)`, `tooHot(limit: 40)`,
  `scheduled(inHours: 3, minutes: 120)`. The expectation column holds the whole outcome.
- **`Fabric`** is a plain enum. Add `CaseIterable` or `Codable` to it in the app; a cell then reads
  the case name: `wool`.

Both are synthesised and add no behaviour. These are wrong: an `init(from:)` written into the app;
a test-side `switch` or dictionary that turns words into cases; an outcome split into a `String`
case-name column plus number columns that the body puts back together; and a field of
`WashProgram` made optional, or its default removed, so that a synthesised decoder accepts a cell.

## The program

The program the user sets is an input. Two forms are equally good:

- **Columns:** one column for each setting that a table varies — fabric, temperature, prewash,
  extra rinse, delay — and the body builds the `WashProgram`, leaving the other settings at their
  defaults.
- **A map cell** that names only the settings the row sets, such as `[fabric: wool, temperature: 50]`.
  A synthesised `Decodable` needs a key for every field that is not optional, also a defaulted one,
  so this cell needs a `TableValueDecodable` conformance on `WashProgram` in the test target, for
  example with `tableValue.readingFields`. Its initialiser is `public`. The package is a Swift
  package, so the conformance has no `@retroactive`.

## The rules

1. **Temperature limit:** each fabric at its limit runs, and one step above it is refused —
   cotton 90 / 100, synthetics 60 / 70, wool 40 / 50, with `tooHot(limit:)` giving the fabric's limit.
2. **Time:** 120, 90 and 50 minutes for cotton, synthetics and wool at 40 °C, and 10 more minutes
   for each 10 °C above 40 — at least two temperatures for one fabric, so the step shows.
3. **Options:** a prewash adds 20 minutes and an extra rinse adds 15. A row with both shows that
   they add up.
4. **Delayed start:** a delay gives `scheduled(inHours:minutes:)` with the same minutes; no delay
   gives `runs(minutes:)`.

Expected values are concrete cells, not computed in the body. One table per rule, or fewer tables
whose rows still show each rule, are both acceptable.
