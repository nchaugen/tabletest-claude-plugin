Great use case for spec-by-example. Let me propose a table, then we can review it together and confirm the expected values before coding starts.

---

## Weekly Pay Calculation

| Scenario | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Overtime Threshold | Total Pay? |
|---|---|---|---|---|---|---|
| Regular weekday hours under limit | 20 | | | $10 | 40 | $200 |
| Weekday hours exactly at threshold | 40 | | | $10 | 40 | $400 |
| One hour over threshold | 41 | | | $10 | 40 | $415 |
| Substantial overtime | 50 | | | $10 | 40 | $550 |
| Sunday hours only | | 8 | | $10 | 40 | $160 |
| Holiday hours only | | | 8 | $10 | 40 | $160 |
| Sunday and holiday hours together | | 8 | 8 | $10 | 40 | $320 |
| Sunday/holiday do not count toward overtime threshold | 38 | 10 | | $10 | 40 | $580 |
| Weekday regular + Sunday | 40 | 8 | | $10 | 40 | $560 |
| Weekday overtime + Sunday | 45 | 8 | | $10 | 40 | $635 |
| All three hour types with overtime | 50 | 4 | 4 | $10 | 40 | $710 |
| Zero hours | 0 | | | $10 | 40 | $0 |
| Zero rate | 40 | 8 | | $0 | 40 | $0 |

*Rates used: regular = 1× ($10), overtime = 1.5× ($15), Sunday/holiday = 2× ($20)*

The "Sunday/holiday do not count toward overtime threshold" row is the important one — 38 weekday + 10 Sunday totals 48 hours but no overtime applies since weekday hours are under 40. Worth confirming this is the intended interpretation.

---

## Input Validation

| Scenario | Hourly Rate | Valid? | Error? |
|---|---|---|---|
| Positive rate | $10 | yes | |
| Zero rate | $0 | yes | |
| Negative rate | -$5 | no | Hourly rate cannot be negative |

---

## Open Questions

A few things worth resolving before implementation:

1. **What counts as a "weekday"?** Mon–Fri only, or Mon–Sat? Saturday is commonly included in shift work.

2. **Sunday that falls on a holiday** — both rules give 2×, so pay is the same either way, but is the classification meaningful (e.g. for reporting)?

3. **"Total pay cannot go below zero"** — with non-negative hours and a valid rate, pay is always ≥ 0. When would this floor actually trigger? Are there deductions (e.g. advances, penalties) that could bring it negative? If not, is this rule defensive code or does it reflect a real scenario?

4. **Fractional hours** — can an employee work 40.5 hours, or are hours whole numbers?

5. **Holiday overlap with weekday hours** — if someone works 6 hours on a weekday that is also a holiday, do those 6 hours count toward the 40-hour weekday threshold, or are they treated purely as holiday hours?

Which of these need resolving first, and do any of the table rows look wrong to you?