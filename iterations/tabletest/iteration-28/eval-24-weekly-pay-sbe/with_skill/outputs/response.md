Let me work through this systematically. Starting with a draft table — confirm or challenge any row, and I'll flag the open questions I spotted.

---

## Weekly Pay Calculation

| Scenario | Weekday hours | Sunday hours | Holiday hours | Hourly rate | Total pay? |
|---|---|---|---|---|---|
| Standard week, no overtime | 35 | | | $20 | $700 |
| Exactly at the 40h threshold | 40 | | | $20 | $800 |
| One hour past the threshold | 41 | | | $20 | $830 |
| Heavy overtime week | 50 | | | $20 | $1,100 |
| Sunday work only | | 8 | | $20 | $320 |
| Holiday work only | | | 8 | $20 | $320 |
| Regular week plus Sunday | 35 | 8 | | $20 | $1,020 |
| Exactly 40 weekday plus Sunday | 40 | 8 | | $20 | $1,120 |
| Overtime week plus Sunday | 45 | 8 | | $20 | $1,270 |
| No hours worked | 0 | 0 | 0 | $20 | $0 |
| Negative hourly rate | {any} | {any} | {any} | -$1 | error: negative rate |

*(Blank cells = zero hours of that type)*

Traceability checks:
- Row 3: `40×$20 + 1×$30 = $800 + $30 = $830`
- Row 4: `40×$20 + 10×$30 = $800 + $300 = $1,100`
- Row 7: `35×$20 + 8×$40 = $700 + $320 = $1,020`
- Row 9: `40×$20 + 5×$30 + 8×$40 = $800 + $150 + $320 = $1,270`

---

## Open questions before we proceed

**1. Do Sunday / holiday hours count toward the 40-hour threshold?**

The rules say "weekday hours beyond 40 are overtime", which implies Sunday and holiday hours are separate and don't push weekday hours into overtime. But I want to confirm: if someone works 38 weekday hours plus 8 holiday hours, do those 8 holiday hours count toward the threshold at all?

My assumption: **no** — they're always 2× regardless. If that's wrong, we need rows showing the interaction.

**2. What if a holiday falls on a Sunday?**

Both rules say 2× — so the rate is the same either way. But does it need to be explicitly handled (e.g. "no double-dipping to 4×"), or is "2× regardless" the full answer?

**3. When can total pay go below zero?**

With negative rates rejected, and zero or more hours worked, pay is always ≥ $0. The "cannot go below zero" rule seems unreachable as stated. Is there a case you have in mind — e.g. deductions applied elsewhere? Or is this a defensive floor we should keep anyway?

---

Once you confirm these, I can add the missing rows and hand the table off to `/tabletest` to produce the `@TableTest` code.