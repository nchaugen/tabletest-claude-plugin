The same request as eval-34, in a package that already depends on TableTest and imports it in its
test file. The routing-correct answer writes the fee cases as a `@TableTest`, not as
`@Test(arguments:)`: the project has chosen TableTest. The stub stays unimplemented, so the tests
compile and fail.

## The fee table

One `@TableTest` inside `CancellationFeeTests`, with the days before check-in as the input and a
`?` expectation for the fee. Every tier and both sides of every boundary: 30 days (free) against
29 (50 %), 7 (50 %) against 6 (80 %), and 1 (80 %) against 0 (the full value). Expected fees are
concrete numbers for a concrete booking value — 0, 100, 160 and 200 for a 200-euro booking — not
expressions such as `bookingValue / 2`. The booking value is either a fixture stated once or a
column. The body makes one `#expect` with no branching.

## The started stay

A separate `@Test` with `#expect(throws: CancellationError.stayAlreadyStarted)` for a negative
number of days. It is not a row of the fee table with a sentinel or optional fee.

## Equally good

A scenario column is optional; when present, its names state the condition of the row, not the
fee. Titles and column names are free. `bookingValue` as a column with a value set is acceptable
only when the fees stay concrete per row.
