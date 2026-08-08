Here's the worked-through spec, split into three tables (classification, arithmetic, and validation) since those are separate rules per the skill's "separate rules from arithmetic" guidance.

## Assumptions (stated since this is non-interactive)

- **"Weekday" = not Sunday, not a holiday.** Saturday is treated as an ordinary weekday since only Sunday gets a special premium in your rules.
- **The 40-hour overtime threshold applies only within weekday hours.** Sunday and holiday hours are a separate bucket — they don't count toward the 40-hour threshold and aren't eligible to stack with the 1.5× overtime multiplier (they're already at 2×).
- **Hours are non-negative inputs.** Only the hourly rate is explicitly validated against negative values in your rules. I've flagged this as an open question below since it affects whether the pay-floor rule can ever actually trigger.

---

### Table 1 — Splits Weekday Hours Into Regular and Overtime

| Scenario | Weekday Hours | Overtime Threshold (hrs) | Regular Hours? | Overtime Hours? |
|---|---|---|---|---|
| No weekday hours worked | 0 | 40 | 0 | 0 |
| Below the overtime threshold | 32 | 40 | 32 | 0 |
| At the overtime threshold | 40 | 40 | 40 | 0 |
| Just past the overtime threshold | 41 | 40 | 40 | 1 |

### Table 2 — Computes Weekly Pay From Classified Hours and Rate

| Scenario | Regular Hours | Overtime Hours | Sunday Hours | Holiday Hours | Hourly Rate ($) | Weekly Pay ($)? | Open Questions |
|---|---|---|---|---|---|---|---|
| Regular hours only | 40 | 0 | 0 | 0 | 20 | 800 | |
| Regular and overtime hours | 40 | 5 | 0 | 0 | 20 | 950 | |
| Sunday hours only | 0 | 0 | 8 | 0 | 20 | 320 | |
| Holiday hours only | 0 | 0 | 0 | 8 | 20 | 320 | |
| All hour types combined | 40 | 5 | 8 | 8 | 20 | 1590 | |
| No hours worked | 0 | 0 | 0 | 0 | 20 | 0 | |
| Zero hourly rate | 40 | 5 | 8 | 8 | 0 | 0 | |
| Negative correction hours | -50 | 0 | 0 | 0 | 20 | 0 (floored) | Are negative hour entries valid (e.g. payroll corrections)? If hours are always ≥ 0 in practice, the pay-floor rule is currently unreachable. |

### Table 3 — Validates Hourly Rate Rejects Negative Values

| Scenario | Hourly Rate ($) | Valid? | Rejection Reason? |
|---|---|---|---|
| Typical positive rate | 20 | yes | |
| At zero (lower boundary) | 0 | yes | |
| Just below zero | -0.01 | no | Hourly rate cannot be negative |

---

## Open questions to confirm before coding

1. **Can hours ever be negative?** (Table 2, last row) — this is the only scenario I could construct where the "pay cannot go below zero" rule actually does something. If hours are always validated ≥ 0 upstream, that rule is dead code under these five other rules combined — worth confirming it's still wanted (e.g. for future correction-entry support) or whether hours need their own negative-rejection rule symmetric to the rate one.
2. **Is $0/hr a valid rate?** I assumed yes since your rule only bans *negative* rates, not zero. Flag if that's wrong.
3. **Does Sunday-hours or holiday-hours volume affect the weekday 40-hour threshold?** I assumed no — weekday overtime is computed purely from weekday hours, independent of how many Sunday/holiday hours were also worked that week. If your payroll rules actually roll all hours into one weekly total for overtime purposes, Table 1 needs another input column and different boundary rows.

Let me know if these assumptions hold or need adjusting, then we can hand this straight to `/tabletest` for the implementation.