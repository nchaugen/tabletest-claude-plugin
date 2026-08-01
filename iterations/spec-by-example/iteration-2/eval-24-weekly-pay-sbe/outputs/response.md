Good — this has several interacting rules (a threshold, two flat multipliers, a floor, and a validation rule), so I'll decompose it into focused tables rather than one giant traceability table, per how we've done this before.

## Assumptions to confirm before we lock these in

1. **"Weekday" = any day that isn't Sunday or a holiday** (so Saturday hours are treated as ordinary weekday hours, not something special). Flag if Saturday should behave differently.
2. **The 40-hour overtime threshold applies only within the weekday bucket** — Sunday and holiday hours never count toward the 40 and are never pulled into the overtime calculation. This is what "weekday hours beyond 40" literally says, but worth confirming since it means someone who works 40 weekday hours *and* 8 Sunday hours gets zero overtime pay, just double time on the Sunday hours.
3. **Holiday hours are excluded from the weekday bucket entirely**, even if the holiday falls on what would otherwise be a weekday. An hour is either "weekday" or "holiday," never both.
4. **The zero-floor rule implies pay can go negative before clamping** — but nothing in the stated rules produces negative pay from non-negative hours and a non-negative rate. This suggests hours themselves might sometimes be negative (e.g., corrections/deductions) elsewhere in the system. I've left this open below rather than guessing at a mechanism.
5. **Zero as an hourly rate** isn't addressed — only negative is explicitly rejected. Left open below.

---

## Table 1: Weekday Regular and Overtime Pay

| Scenario | Weekday Hours | Overtime Threshold (Policy) | Hourly Rate | Weekday Pay? |
|---|---|---|---|---|
| No hours worked | 0 | 40 | 20 | 0 |
| Just under the threshold | 39 | 40 | 20 | 780 |
| At the threshold | 40 | 40 | 20 | 800 |
| Just over the threshold | 41 | 40 | 20 | 830 |
| Well into overtime | 50 | 40 | 20 | 1100 |

`830 = 40×20 + 1×20×1.5`. `1100 = 40×20 + 10×20×1.5`.

## Table 2: Sunday and Holiday Premium Pay

| Scenario | Sunday Hours | Holiday Hours | Weekday Hours Already Worked | Hourly Rate | Premium Pay? |
|---|---|---|---|---|---|
| No Sunday or holiday hours | 0 | 0 | {0, 45} | 20 | 0 |
| Sunday hours only, regardless of weekday load | 8 | 0 | {0, 45} | 20 | 320 |
| Holiday hours only, regardless of weekday load | 0 | 8 | {0, 45} | 20 | 320 |
| Sunday and holiday hours combined | 4 | 8 | 0 | 15 | 360 |

The `{0, 45}` value set on Weekday Hours Already Worked encodes assumption 2 above: whether or not the weekday bucket has already hit overtime, Sunday/holiday hours are always ×2 and never join the overtime pool. If that assumption is wrong, this table needs to change.

## Table 3: Weekly Pay Combination (arithmetic)

| Scenario | Weekday Pay | Premium Pay | Weekly Pay? |
|---|---|---|---|
| Regular week, no premium hours | 800 | 0 | 800.00 |
| Overtime week with Sunday hours | 830 | 320 | 1150.00 |
| Overtime week with Sunday and holiday hours | 712.50 | 360 | 1072.50 |

(712.50 = 40×15 + 5×15×1.5, from a 45-hour weekday week at a $15 rate — just showing the two pools add together once each is already computed by Tables 1 and 2.)

## Table 4: Hourly Rate Validation

| Scenario | Hourly Rate | Result? |
|---|---|---|
| Positive rate | 20 | Accepted |
| Zero rate | 0 | ? — allowed, or also rejected? |
| Negative rate | -5 | Rejected |

## Open question: the zero floor

No combination of non-negative hours and a non-negative rate produces negative pay under these rules, so I can't write a concrete row that exercises the floor without guessing at a mechanism (e.g., negative/correcting hours, a separate deduction). If there's a source of negative pay I'm missing, tell me what it is and I'll add a row; otherwise I'd treat the floor as defensive code with no reachable test case yet, and revisit if a deduction feature shows up later.

---

Once you confirm assumptions 1–5 (or correct them), this is ready to hand to `/tabletest` — the four tables map directly to four `@TableTest` methods.