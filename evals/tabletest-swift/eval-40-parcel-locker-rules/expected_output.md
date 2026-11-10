`@TableTest` tables inside `PickupTests` for the four rules. The stub stays unimplemented, so the
tests compile and fail.

## The result cell

`PickupResult` has a case with an associated value. Add `Codable` to it in the app — one
synthesised word, which keeps the stub unimplemented — and an expectation cell reads the case as
Swift spells it: `opens`, `wrongCode(attemptsLeft: 2)`, `blocked`, `returnedToDepot`. No
`init(from:)` in the app, and no test-side `switch` that maps words to cases.

## The rules and their rows

- **The pickup code opens the locker.**
- **Wrong codes count down:** a first wrong code leaves 2 attempts, a second leaves 1, and a third
  blocks. The count of wrong codes in a row is an input column, because `Parcel` carries it.
- **A right code before the third wrong one opens** — after two wrong codes the pickup code still
  opens. This kills an implementation that blocks too early.
- **A blocked parcel stays blocked**, for the right code as for a wrong one. A value set such as
  `{482915, 111111}` shows that the code does not matter.
- **72 hours:** 72 hours after delivery the code still opens; at 73 the result is
  `returnedToDepot` for any code.

Concrete codes, hours and counts in the cells. The parcel's own pickup code is a fixture, stated
once (a doc comment or a helper), not a column whose value never changes.

## Underspecified

- **A blocked parcel past 72 hours.** The rules do not say whether it is `blocked` or
  `returnedToDepot`. Either reading is acceptable, and a row should state the one taken: a parcel
  with three wrong codes in a row, past 72 hours, and its result.

Nothing else is open: "up to 72 hours" includes hour 72, and "the third wrong code in a row" blocks.

## Equally good

One table or several; the split above by rule is one choice. Scenario names, titles and column
order are free.
