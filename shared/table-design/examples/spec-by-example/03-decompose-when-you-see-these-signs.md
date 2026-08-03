One family, one table with an adjustment column — not three tables each fixing the same setup:

| Scenario                   | Adjustments                        | Daily Dose? |
|----------------------------|------------------------------------|-------------|
| No adjustment              | [:]                                | 500         |
| Renal impairment           | [renal: severe]                    | 250         |
| Low body weight            | [weightKg: 20]                     | 200         |
| Interacting drug           | [interaction: true]                | 400         |
| Renal and interacting drug | [renal: severe, interaction: true] | 200         |

`Adjustment` works as a column header with those as its values, which is what makes it a family and
not a bag.
