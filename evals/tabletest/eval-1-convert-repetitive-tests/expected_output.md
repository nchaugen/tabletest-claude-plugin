A single `@TableTest` method replacing the three `@Test` methods, with a leftmost scenario
column, a customer-tier column, an order-amount column, and a `Discount?` expectation column.
Exactly three rows — GOLD, SILVER, BRONZE — one per original test. The method body holds a
single `assertEquals` and no `if`/`switch`.

This is a **mechanical conversion, not a modelling exercise.** The three source tests fully
determine the table: `GOLD@100 → 20`, `SILVER@100 → 10`, `BRONZE@100 → 0`. There is no domain
to pick apart and nothing underspecified to resolve — the only judgement left is naming and
whether a description earns its place.

## What the conversion must preserve

- **One row per source test, no more.** The eval scores a faithful conversion; inventing extra
  rows (other tiers, other amounts) is out of scope and changes the row count the source fixed
  at three.
- **One assertion for three rows.** The point of the conversion is that the three near-identical
  method bodies collapse to a single `assertEquals` driven by the table.

## Title and description

- **Title** carries the rule as a declarative phrase — "Discount by customer tier" or similar,
  not `test1` or the bare method name.
- **`@Description` is optional and usually omittable here.** The rows already carry everything.
  The one latent fact a description *could* add: because every row fixes the amount at 100, the
  returned discount (20/10/0) coincides numerically with the tier's percentage, so the table
  cannot by itself distinguish "20% discount" from "20 currency units". Noting that the value is
  an absolute amount is legitimate context; restating the tier→value mapping is not — that is
  what the rows are for.

## Judging

Exactly three rows, a single assertion, a clean method body, a descriptive title. Everything
else is latitude. A present-but-redundant `@Description` that merely echoes the columns is a
minor legibility miss, not a decomposition failure.
