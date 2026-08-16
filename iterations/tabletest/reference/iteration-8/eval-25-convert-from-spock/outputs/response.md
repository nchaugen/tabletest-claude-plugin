Hand-authored reference answer for eval-25, from
`products/claude-plugin/plans/slice-10-reference-answers-remaining-evals.md` section Findings.
Not agent output. Must never be read as a skill measurement.

Four `@TableTest` methods — rate bracket, effective weight, surcharges, carrier invariance — and one
`@TypeConverter`. Compiled and run against the real scaffolding: 24 cases, all passing.

**The oversize fee sits in the surcharges table, not its own.** Every stored solution but one gave it
a separate method, and that is the `concern-not-over-split` failure in 8 of 10 completed runs: two
tables holding the same EU-standard 3 kg fixture, sharing one output column, each varying one thing.
One table with a Dimensions column and an Options column is the collapse the assertion asks for, and
it stays at ten rows.

**Every value needed to predict a cost is published.** The volumetric divisor is a column of the
effective-weight table, bound to a parameter the call never receives — the *Make Thresholds Visible*
route. The oversize limit, the hazmat fee, the fragile multiplier and the insurance rate and floor
are in the surcharge table's `@Description`. `rule-statable-from-table` fails in 8 of 10 completed
runs and the divisor is named in every one of those verdicts; the only stored run that passed it had
dropped the concern instead of publishing the constant.

**Brackets are stated by pairs, not by samples.** 1 against 1.01, 5 against 5.01, 15 against 15.01 —
each pair straddles the change of outcome, so a reader can see the bound is inclusive rather than
infer it. The three region/speed rows then hold the weight at 5 kg, so those rows differ from the
EU-standard 5 kg row in one cell and the rate difference is the rule.

**One name for one quantity.** Every table's expectation column is `Shipping cost?`, because every
table calls `calculateShippingCost` and asserts its return. Splitting it into `Base rate?` and
`Total cost?` is defensible and is what the ground truth's reference decomposition does, but the two
names describe the same returned value under different fixtures.

**Options are one map column with `[:]` on the no-options rows**, so the `@TypeConverter` runs on
every row and supplies the defaults; a blank cell would hand the method `null` instead.

`compareTo` throughout — the SUT returns `stripTrailingZeros()`, so `7.5` must match `7.50`.
