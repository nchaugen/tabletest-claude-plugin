Great use case for a spec-by-example table. Let me work through the rules and build concrete examples you can verify.

**Weekly Pay Calculation**

| Scenario | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Pay? | Error? |
|---|---|---|---|---|---|---|
| No hours worked | 0 | | | 10.00 | 0.00 | |
| Standard week, at overtime threshold | 40 | | | 10.00 | 400.00 | |
| One hour over overtime threshold | 41 | | | 10.00 | 415.00 | |
| Heavy overtime week | 50 | | | 10.00 | 550.00 | |
| Sunday shift only | | 8 | | 10.00 | 160.00 | |
| Holiday hours only | | | 8 | 10.00 | 160.00 | |
| Weekday plus Sunday | 40 | 8 | | 10.00 | 560.00 | |
| Overtime weekday plus holiday | 45 | | 8 | 10.00 | 635.00 | |
| All three types in one week | 45 | 8 | 8 | 10.00 | 795.00 | |
| Zero hourly rate | 40 | | | 0.00 | 0.00 | |
| Negative hourly rate | 40 | | | -5.00 | | Negative rate not allowed |

---

**Calculations for verification:**
- Row 3 (41 hrs): 40 × $10 + 1 × $15 = $415
- Row 4 (50 hrs): 40 × $10 + 10 × $15 = $550
- Row 8 (45 weekday + 8 holiday): (40 × $10 + 5 × $15) + 8 × $20 = $475 + $160 = $635
- Row 9 (45 + 8 + 8): $475 + $160 + $160 = $795

---

**Open questions before we proceed:**

1. **Overtime threshold — weekday hours only?** The rules say "weekday hours up to 40", which implies Sunday and holiday hours do *not* count toward the 40-hour threshold. Row 7 (40 weekday + 8 Sunday = $560) reflects this — the Sunday hours don't push the weekday hours into overtime. Is that correct?

2. **Sunday that is also a holiday** — if a Sunday falls on a holiday, does it pay 2× (already double time, no stacking) or 4×? Worth deciding now even if rare.

3. **Negative hours** — the rules reject a negative rate, but say nothing about negative hours. Can a weekday/Sunday/holiday hour count be negative (e.g., a correction entry)? Should that also be rejected, or does the "pay cannot go below zero" floor handle it?

4. **"Pay cannot go below zero"** — with a non-negative rate and non-negative hours this can never trigger. Is this a safeguard for future rules (e.g., deductions), or does it exist to handle negative hours silently?

Once you confirm these, especially #1, I can add the boundary rows that cover them and we'll have a solid spec ready for `/tabletest`.