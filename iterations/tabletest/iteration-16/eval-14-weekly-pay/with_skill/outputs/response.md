Looking at the rules, I can identify several concerns to cover: the overtime threshold boundary, double-time categories, the zero-pay floor, and rate validation. Let me build the example table.

---

## Weekly Pay Calculation

The overtime threshold (40 hours) is made explicit as a column — this makes the boundary rows legible and signals it's a configurable policy value.

| Scenario | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Overtime Threshold | Weekly Pay? | Error? |
|---|---|---|---|---|---|---|---|
| Standard 40-hour week | 40 | | | $10.00 | 40 | $400.00 | |
| Just below overtime threshold | 39 | | | $10.00 | 40 | $390.00 | |
| First hour of overtime | 41 | | | $10.00 | 40 | $415.00 | |
| Significant overtime | 50 | | | $10.00 | 40 | $550.00 | |
| Sunday hours only (double time) | | 8 | | $10.00 | 40 | $160.00 | |
| Holiday hours only (double time) | | | 8 | $10.00 | 40 | $160.00 | |
| Regular week with Sunday and holiday | 40 | 8 | 8 | $10.00 | 40 | $720.00 | |
| Overtime week with Sunday and holiday | 45 | 8 | 8 | $10.00 | 40 | $795.00 | |
| No hours worked | 0 | 0 | 0 | $10.00 | 40 | $0.00 | |
| Zero hourly rate | 40 | 8 | 8 | $0.00 | 40 | $0.00 | |
| Negative hourly rate | 40 | 0 | 0 | -$5.00 | 40 | | Negative rate not allowed |

---

### How the outputs trace to inputs

- **39h × $10.00** = $390.00 (all regular)
- **40h × $10.00** = $400.00 (still all regular — threshold is inclusive)
- **41h × $10.00** = 40 × $10.00 + 1 × $15.00 = **$415.00**
- **50h × $10.00** = 40 × $10.00 + 10 × $15.00 = **$550.00**
- **8h Sunday × $10.00** = 8 × $20.00 = **$160.00**
- **40h + 8h Sunday + 8h holiday × $10.00** = $400 + $160 + $160 = **$720.00**
- **45h + 8h Sunday + 8h holiday × $10.00** = $475 + $160 + $160 = **$795.00**

---

### Design notes

**Boundary rows (39h and 41h)** are deliberately adjacent to the threshold. A 40-hour row alone does not specify whether overtime starts at 40 or 41 — both boundary rows together do.

**Blank cells** for Sunday/Holiday Hours mean those hours are not submitted (zero). Filling them with `0` would obscure which components are active in each scenario.

**Sunday and holiday are separate columns**, not a combined "special hours" column. The rate is the same (2×), but the employee reports them differently and the system may need to distinguish them for payroll records.

---

### Open questions

1. **"Total pay cannot go below zero"** — with the rules as stated (non-negative hours × non-negative rate), the total is always ≥ $0.00. Is there a deduction mechanism not described here that this floor is guarding against? If so, it needs its own table.

2. **Sunday that is also a holiday** — if an employee works a Sunday that falls on a public holiday, which rate applies? Both are 2×, so the *amount* is the same — but does it matter for payroll categorisation?

3. **Fractional hours** — are 8.5h, 40.5h valid inputs? If so, do the same thresholds and rates apply?

4. **Can hours be negative?** — the spec rejects negative rates but says nothing about negative hours. A defensive floor of $0.00 suggests this may have been considered.

---

Ready to hand off to `/tabletest` — the column structure maps directly to method parameters and assertions.