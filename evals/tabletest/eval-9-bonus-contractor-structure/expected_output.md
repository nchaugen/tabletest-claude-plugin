A single `@TableTest` over `calculateBonusPercentage(Employee)` returning the bonus
**percentage** (a `double`), with columns in business language — `Level`, `Department`,
`Bonus %`. Five rows: the four `Level × Department` combinations enumerated (their outcomes all
differ), plus **one** CONTRACTOR row that uses a value set `{SALES, ENGINEERING}` to state
"regardless of department".

## The reference decomposition

| Scenario | Level | Department | Bonus % |
|---|---|---|---|
| Senior in Sales | SENIOR | SALES | 15 |
| Senior in Engineering | SENIOR | ENGINEERING | 12 |
| Junior in Sales | JUNIOR | SALES | 8 |
| Junior in Engineering | JUNIOR | ENGINEERING | 5 |
| Contractor, any department | CONTRACTOR | `{SALES, ENGINEERING}` | 0 |

## Value sets only where the outcome is shared

This is the discriminating point of the eval. The four senior/junior rows **cannot** be
compressed with value sets — `SENIOR+SALES=15` and `SENIOR+ENGINEERING=12` are different
outcomes, so each is its own row. CONTRACTOR **can**, because both departments yield 0: one row
with `{SALES, ENGINEERING}` expresses "regardless of department" (`contractor-uses-value-set`).
A dummy placeholder like `ANY` or `N/A`, or two enumerated contractor rows, both miss the point —
the first invents a value the enum does not have, the second hides that the outcome is constant.

## Expect a percentage, not an amount

The method returns a percentage; the `Bonus %` column holds `15`, `12`, `0` — not a currency
amount computed against some salary (`expects-bonus-percentage`). No salary is in scope.

## Description carries context, never the rule

`@Description` is optional and must **not** restate the bonus mapping — the rows already carry
it. Its job is context the table cannot show (e.g. that the percentage applies to base salary, or
a note that the rate schedule is HR-owned), or a reading guide for a long table. Reproducing the
`15/12/8/5/0` rules in prose duplicates the table and is the failure mode of
`description-if-present-adds-information`.

## Fully specified

`Level` and `Department` are closed enums and every combination has a stated rate, so — unlike
the parser evals — there is nothing underspecified to surface as an example. The calculator is a
stub; tests are expected not to pass, only `compiles` is asserted.

## Judging

Score first: the four combinations present as distinct rows, the single value-set CONTRACTOR
row, percentages (not amounts), and business-language columns. Scenario names describe the
condition ("Senior in Sales"), not the outcome ("Gets 15%").
