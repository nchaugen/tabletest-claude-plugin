Hand-authored reference answer for eval-1, from
`products/claude-plugin/plans/slice-10-reference-answers-remaining-evals.md` section Findings.
Not agent output. Must never be read as a skill measurement.

One `@TableTest`, three rows, one `assertEquals`. Compiled and run against the real
`DiscountService`: 3 cases, all passing.

**The three source tests fully determine the table**, so the only judgement left is naming and
whether a description earns its place. The `@Description` carries the one fact the rows cannot: the
discount is an amount off the order, and every row orders 100, which is the single order value at
which the amount and the tier's percentage read the same. A reader of the table alone cannot tell
`20` from `20%`.
