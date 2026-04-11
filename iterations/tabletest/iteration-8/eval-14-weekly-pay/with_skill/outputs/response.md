Here is the spec-by-example table for **Weekly Pay Calculation**.

---

## Weekly Pay Calculation

| Scenario | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Weekly Pay? |
|---|---|---|---|---|---|
| No hours worked | 0 | 0 | 0 | $10 | $0 |
| Weekday hours only, under threshold | 39 | | | $10 | $390 |
| Weekday hours exactly at overtime threshold | 40 | | | $10 | $400 |
| Weekday hours just over overtime threshold | 41 | | | $10 | $415 |
| Weekday hours well into overtime | 45 | | | $10 | $475 |
| Sunday hours only | | 8 | | $10 | $160 |
| Holiday hours only | | | 8 | $10 | $160 |
| Weekday and Sunday, no overtime | 40 | 8 | | $10 | $560 |
| Weekday and holiday, no overtime | 40 | | 8 | $10 | $560 |
| Sunday and holiday hours do not count toward weekday overtime threshold | 35 | 10 | | $10 | $550 |
| All three hour types, weekday overtime | 45 | 8 | 8 | $10 | $795 |
| Zero rate produces zero pay | 45 | 8 | 8 | $0 | $0 |

**Traceability:**
- Row 3: 39 × $10 = $390
- Row 4: 40 × $10 = $400
- Row 5: (40 × $10) + (1 × $10 × 1.5) = $400 + $15 = $415
- Row 6: (40 × $10) + (5 × $10 × 1.5) = $400 + $75 = $475
- Row 7: 8 × $10 × 2 = $160
- Row 8: 8 × $10 × 2 = $160
- Row 9: (40 × $10) + (8 × $10 × 2) = $400 + $160 = $560
- Row 10: (40 × $10) + (8 × $10 × 2) = $400 + $160 = $560
- Row 11: (35 × $10) + (10 × $10 × 2) = $350 + $200 = $550 — weekday hours are 35, below 40, so no overtime despite 45 total hours
- Row 12: (40 × $10) + (5 × $10 × 1.5) + (8 × $10 × 2) + (8 × $10 × 2) = $400 + $75 + $160 + $160 = $795
- Row 13: anything × $0 = $0

---

## Input Validation

| Scenario | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Error? |
|---|---|---|---|---|---|
| Negative hourly rate is rejected | {0, 40} | {0} | {0} | -$10 | Negative rate not allowed |

The value set `{0, 40}` on weekday hours documents that the rejection happens regardless of hours worked — the rate check fires before any pay calculation.

---

## Open Questions

| Question | Notes |
|---|---|
| Can hours be fractional (e.g., 40.5 weekday hours)? | The rules don't specify; the 40-hour threshold behaviour with decimals (e.g., 40.5 h → 0.5 h overtime) should be confirmed |
| Does the overtime threshold apply to the total of weekday + Sunday + holiday, or weekday only? | The spec says "weekday hours beyond 40" — row 11 above tests this explicitly, but worth confirming with the domain |
| Is a zero hourly rate valid, or should it also be rejected? | Row 13 treats it as valid; if it should be rejected, move it to the validation table |
| What is the expected error format — an exception, a result type, an error message string? | Affects the `Error?` column values but not the business rules |

---

The central rule to nail down is row 11: **Sunday and holiday hours do not push weekday hours into overtime.** The overtime clock runs on weekday hours alone. This is the most likely source of implementation bugs and the row most worth confirming with a domain expert before writing code.