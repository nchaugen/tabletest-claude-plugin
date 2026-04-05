## Weekly Pay Calculation — Spec by Example

Here are the key rules to pin down, with concrete examples:

**Inputs:** Weekday Hours, Sunday Hours, Holiday Hours, Hourly Rate  
**Outputs:** Weekly Pay?, Error?

| Scenario | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Weekly Pay? | Error? |
|---|---|---|---|---|---|---|
| Standard 40-hour week | 40 | | | $10 | $400.00 | |
| Under 40 hours | 32 | | | $10 | $320.00 | |
| Just below overtime threshold | 39 | | | $10 | $390.00 | |
| At overtime threshold — no overtime applies | 40 | | | $10 | $400.00 | |
| Just above overtime threshold | 41 | | | $10 | $415.00 | |
| Significant overtime | 50 | | | $10 | $550.00 | |
| Sunday hours only | | 8 | | $10 | $160.00 | |
| Holiday hours only | | | 8 | $10 | $160.00 | |
| Sunday and holiday hours are independent | | 8 | 8 | $10 | $320.00 | |
| Weekday + Sunday | 40 | 8 | | $10 | $560.00 | |
| Overtime + Sunday + Holiday | 50 | 8 | 8 | $10 | $870.00 | |
| No hours worked | 0 | 0 | 0 | $10 | $0.00 | |
| Zero rate — all hours produce nothing | {40, 0} | | | $0 | $0.00 | |
| Negative hourly rate — rejected | 40 | | | -$5 | | Invalid rate |

**Arithmetic checks (key rows):**

- Row 7 (just above threshold): `(40 × $10) + (1 × $15) = $400 + $15 = $415`
- Row 8 (significant overtime): `(40 × $10) + (10 × $15) = $400 + $150 = $550`
- Row 11 (Sunday only): `8 × $20 = $160`
- Row 14 (overtime + Sunday + holiday): `$550 + (8×$20) + (8×$20) = $550 + $160 + $160 = $870`

**Open questions / decisions to confirm before coding:**

1. Are **zero weekday/Sunday/holiday hours** each valid inputs, or should missing hours default to zero when not submitted?
2. Is a **zero hourly rate** valid (produces $0), or should it also be rejected alongside negative rates?
3. The spec says "pay cannot go below zero" — with non-negative hours and a non-negative rate this is always satisfied. Is there a case where **negative hours** could be submitted (e.g. a correction/reversal), or is this purely a safety floor?
4. Are **fractional hours** (e.g. 40.5 weekday hours) in scope, or are hours always whole numbers?

---

Ready to convert this into a `@TableTest`? I'll invoke `/tabletest` once the open questions above are resolved (or I can proceed with reasonable defaults if you'd like to move forward now).