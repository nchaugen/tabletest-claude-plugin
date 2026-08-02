Here's the decomposition. This splits into five tables — classifying weekday hours, pricing weekday hours, pricing Sunday/holiday hours (one family, since both are flat double-time), summing to a floored total, and rate validation. I made a few assumptions where your rules didn't fully pin things down — flagged below since this is a non-interactive session, but I've picked concrete answers so the tables are complete rather than blocking on them.

## Assumptions (please override any of these before I implement)

1. **Inputs are pre-categorized hour totals** — the function receives `weekdayHours`, `sundayHours`, `holidayHours` already bucketed; it does not itself classify calendar dates. Saturday isn't mentioned in your rules — I'm treating it as an ordinary weekday (subject to the 40-hour/overtime rule) unless you say otherwise.
2. **The 40-hour threshold applies to weekday hours only**, not to total hours across all categories. ("Weekday hours beyond 40" reads as scoped to that bucket.)
3. **Hours can be negative** (timesheet corrections/deductions) — this is the only way "total pay cannot go below zero" is ever reachable, since positive hours × non-negative rate never goes negative. If hours are actually always ≥ 0 in your system, the floor rule is dead code and rules 5 can be dropped. Worth confirming.
4. **The zero-floor applies to the total**, not to each category independently — a category can go negative internally (e.g., a large correction) as long as the sum is floored at 0.

## Table 1 — Splits weekday hours into regular and overtime bands (threshold: 40 hrs)

| Scenario                    | Weekday Hours | Regular Hours? | Overtime Hours? |
|------------------------------|--------------|-----------------|------------------|
| Below the 40-hour threshold  | 32           | 32              | 0                |
| Exactly at the threshold     | 40           | 40              | 0                |
| One hour past the threshold  | 41           | 40              | 1                |

## Table 2 — Computes weekday pay from regular and overtime hours (overtime at 1.5×)

| Scenario                          | Regular Hours | Overtime Hours | Base Rate ($/hr) | Weekday Pay? |
|------------------------------------|---------------|-----------------|-------------------|--------------|
| Regular hours only                 | 32            | 0               | 15                | 480          |
| Regular and overtime hours         | 40            | 8               | 20                | 1040         |
| No hours worked                    | 0             | 0               | 20                | 0            |
| Negative regular hours (correction)| -6            | 0               | 20                | -120         |

## Table 3 — Computes double-time pay for Sunday and holiday hours

Sunday and holiday hours pay identically (2× base rate), so they're one family with an `Hour Type` column rather than two near-duplicate tables.

| Scenario                                   | Hour Type          | Premium Hours | Base Rate ($/hr) | Premium Pay? |
|---------------------------------------------|---------------------|----------------|-------------------|--------------|
| Sunday hours paid at double time             | Sunday              | 8              | 20                | 320          |
| Holiday hours paid at double time            | Holiday             | 8              | 20                | 320          |
| No premium hours worked                      | {Sunday, Holiday}   | 0              | 20                | 0            |
| Negative premium hours (correction), either type | {Sunday, Holiday} | -4             | 20                | -160         |

## Table 4 — Sums weekday and premium pay into total weekly pay, floored at zero

| Scenario                                | Weekday Pay | Sunday Pay | Holiday Pay | Total Weekly Pay? |
|-------------------------------------------|-------------|------------|--------------|--------------------|
| Ordinary week, no premium hours            | 800         | 0          | 0            | 800                |
| Combines weekday and premium pay           | 1040        | 320        | 320          | 1680               |
| Sum lands exactly at zero                  | -120        | 0          | 120          | 0                  |
| Sum would go negative, floored at zero     | -120        | -160       | 0            | 0                  |

## Table 5 — Rejects a negative hourly rate before calculating pay

| Scenario                        | Base Rate ($/hr) | Valid? | Rejection Reason?            |
|-----------------------------------|-------------------|--------|-------------------------------|
| Positive rate accepted            | 20                | yes    |                               |
| Zero rate accepted (boundary)     | 0                 | yes    |                               |
| Just below zero, rejected         | -0.01             | no     | Hourly rate cannot be negative |

---

Open question I couldn't resolve without you: **is assumption 3 (negative/correction hours) actually correct for your domain?** If hours are never negative in practice, Table 2's/3's last rows and Table 4's floor rows are testing an unreachable path, and rule 5 can be simplified away. Let me know and I'll adjust before we move to implementation — otherwise this is ready to hand to `/tabletest` or `/table-driven-testing` depending on your target language.