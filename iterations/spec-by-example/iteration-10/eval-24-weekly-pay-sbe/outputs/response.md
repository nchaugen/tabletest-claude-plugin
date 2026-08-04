Here's the worked-through specification, built as four tables — a classification, an arithmetic/combining table, a validation table, and a floor table. I split classification from arithmetic (per spec-by-example practice) because "which hours become overtime" and "what those hours are worth" are two different rules, and merging them would make cells impossible to predict in one step.

**Assumptions** (stated because I can't ask follow-ups — flag if any are wrong):
- Input is already bucketed into three mutually-exclusive hour counts per week: **Weekday Hours**, **Sunday Hours**, **Holiday Hours**. A day that is both Sunday and a holiday contributes its hours to the Holiday bucket (doesn't affect pay either way, since both are 2×).
- The 40-hour overtime threshold applies **only** to Weekday Hours — Sunday and holiday hours never count toward it and are never eligible for overtime multipliers beyond the flat 2×.
- Hours are normally ≥ 0; the "floor at zero" rule only has something to clamp if hours can go negative (e.g., a payroll correction). This is called out below as an open question.

---

### 1. Splits Weekday Hours Into Regular And Overtime

| Scenario | Weekday Hours | Overtime Threshold (Hours) | Regular Hours? | Overtime Hours? |
|---|---|---|---|---|
| No hours worked | 0 | 40 | 0 | 0 |
| Below the threshold | 32 | 40 | 32 | 0 |
| At the threshold | 40 | 40 | 40 | 0 |
| Just past the threshold | 41 | 40 | 40 | 1 |

### 2. Calculates Weekly Pay By Combining Rate Categories

| Scenario | Regular Hours | Overtime Hours | Sunday Hours | Holiday Hours | Base Rate ($) | Total Pay ($)? |
|---|---|---|---|---|---|---|
| No hours worked | 0 | 0 | 0 | 0 | 20.00 | 0 |
| Regular hours only | 40 | 0 | 0 | 0 | 20.00 | 800 |
| Overtime hours only | 0 | 8 | 0 | 0 | 20.00 | 240 |
| Sunday hours only | 0 | 0 | 8 | 0 | 20.00 | 320 |
| Holiday hours only | 0 | 0 | 0 | 8 | 20.00 | 320 |
| Mixed week combining all categories | 40 | 5 | 8 | 8 | 20.00 | 1590 |
| Lower base rate | 20 | 0 | 0 | 0 | 15.50 | 310 |

### 3. Rejects A Negative Hourly Rate

| Scenario | Base Rate ($) | Valid? | Rejection Reason? |
|---|---|---|---|
| Non-negative rate | {0.00, 20.00} | yes | |
| Negative rate | -0.01 | no | Hourly rate cannot be negative |

*(Zero is treated as valid since only "negative" is disallowed by the stated rule — flag if zero should also be rejected.)*

### 4. Floors Total Pay At Zero

| Scenario | Regular Hours | Overtime Hours | Sunday Hours | Holiday Hours | Base Rate ($) | Total Pay ($)? | Open Questions |
|---|---|---|---|---|---|---|---|
| Correction hours drive the raw total negative | -100 | 0 | 0 | 0 | 20.00 | 0 | Can hour inputs actually be negative (e.g. payroll corrections)? If hours are always ≥ 0 and the rate is validated ≥ 0 (table 3), this floor can never trigger in practice — worth confirming whether it's a defensive safeguard or whether there's a deduction path not yet described. |

---

**Open questions to confirm before/while implementing:**
1. Can hour inputs be negative at all (corrections), or should negative hours be rejected the same way negative rate is? This determines whether table 4 is reachable code or dead defensive code.
2. Should a rate of exactly `0` be valid, or does "not allowed" implicitly mean "must be positive"?
3. Confirm the mutually-exclusive-bucket assumption for Sunday/holiday overlap is how you want the input modeled (vs. e.g. a per-day list with a day-type flag).

Let me know which assumptions to adjust, and we can move straight to a `@TableTest` from these four tables.