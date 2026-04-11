Let's name this **Weekly Pay Calculation** and work through it systematically.

Here's a starting table covering the main rules and boundaries:

| Scenario | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Pay? | Error? |
|---|---|---|---|---|---|---|
| Standard week, exactly at threshold | 40 | | | $10 | $400 | |
| No hours worked | 0 | | | $10 | $0 | |
| One hour over overtime threshold | 41 | | | $10 | $415 | |
| 10 hours of overtime | 50 | | | $10 | $550 | |
| Sunday hours only | | 8 | | $10 | $160 | |
| Holiday hours only | | | 8 | $10 | $160 | |
| Weekday (under threshold) plus Sunday | 32 | 8 | | $10 | $480 | |
| Weekday overtime plus Sunday | 48 | 8 | | $10 | $680 | |
| Sunday and holiday in same week | | 8 | 8 | $10 | $320 | |
| Negative hourly rate | {0, 40} | | | -$10 | | Invalid rate |

Calculations I used:
- **$415** = 40×$10 + 1×$15
- **$550** = 40×$10 + 10×$15
- **$160** = 8×$20
- **$480** = 32×$10 + 8×$20
- **$680** = 40×$10 + 8×$15 + 8×$20

Before we go further, a few questions that affect how I fill in the table:

**1. Does the 40-hour overtime threshold count only weekday hours?**
For the "Weekday overtime plus Sunday" row I assumed yes — 48 weekday hours trigger 8 hrs of overtime, and the 8 Sunday hours are always double-time regardless. Is that right, or do Sunday/holiday hours count toward the 40-hour bucket?

**2. What if a day is both a Sunday and a holiday?**
The rules list them separately at the same rate (2×). Is it still 2× (they don't stack), or is there a combined rate?

**3. What does "weekday" include — Mon–Sat or Mon–Fri?**
Saturday being weekday vs. its own category would change whether a typical 5-day-plus-Saturday worker hits overtime through regular weekday accumulation.

**4. The "pay cannot go below zero" floor** — with the inputs as described (non-negative hours, valid rate), the minimum is naturally $0. Is this floor protecting against a deductions scenario not yet in scope, or just a safety guard on the output?

What's your read on these?