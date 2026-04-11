Let me work through the rules systematically, surfacing the decisions and edge cases that aren't fully pinned down yet.

---

## Cancellation Refund

The central behaviour is: **what refund does a subscriber receive when they cancel?**

The prorated refund formula is: `Refund = (Days Remaining / Days in Billing Period) × Amount Paid`

| Scenario | Plan | Amount Paid | Days in Billing Period | Days Used | Within 24h of Renewal Charge | Refund? |
|---|---|---|---|---|---|---|
| Typical monthly cancellation mid-cycle | Monthly | £9.99 | 30 | 10 | no | £6.66 |
| Monthly — cancel on second day, outside window | Monthly | £9.99 | 30 | 1 | no | £9.66 |
| Monthly — cancel with one day left | Monthly | £9.99 | 30 | 29 | no | £0.33 |
| Monthly — cancel within 24h of renewal | Monthly | £9.99 | 30 | 1 | yes | £0.00 |
| Annual cancellation mid-year | Annual | £95.88 | 365 | 100 | no | £69.61 |
| Annual — cancel within 24h of renewal | Annual | £95.88 | 365 | 1 | yes | £0.00 |
| Cancel during free trial (no charge yet) | Trial | £0.00 | — | — | no | £0.00 |
| Trial converts to monthly; cancel same day | Monthly | £9.99 | 30 | 1 | yes | £0.00 |

**Notes on the last row:** The trial-to-monthly auto-conversion on day 31 is the first charge event. If the subscriber cancels within 24h of it, that falls under the "no refund within 24h of renewal" exception — assumed yes, but worth confirming.

---

### Open Questions

These rows can't be filled in without a decision:

| Open Question | Impact |
|---|---|
| Rounding rule for prorated refund? | £95.88 / 365 × 265 = £69.6115... — round half-up gives £69.62, truncate gives £69.61 |
| Is monthly period always 30 days, or actual calendar days? | Cancelling a March subscription has 31 days paid; using 30 overpays slightly |
| Is the trial-to-monthly conversion treated as a renewal charge for the 24h rule? | Row 8 above — assumed yes |
| Is the 24h window strictly less than 24h, or ≤ 24h? | What happens at exactly 24h00m00s? |
| Does annual plan use 365 days always, or 366 in a leap year? | Small difference but real money at scale |

---

### A row to confirm with domain experts

The "cancel on second day, outside 24h window" row (row 2) is the sharpest case: the subscriber has used 1 day and gets **£9.66 back** — nearly the full monthly charge. This is logically consistent with the prorate rule, but it's worth a deliberate confirmation that this is intended, especially since it sits very close to (but outside) the 24h no-refund window.

---

Once the open questions are resolved, this maps directly to a `@TableTest` — run `/tabletest` when you're ready to implement.