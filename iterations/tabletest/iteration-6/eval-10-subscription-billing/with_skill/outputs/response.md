Here are two tables covering the two distinct concerns in this feature: whether a refund applies, and how much it is.

---

## Cancellation Refund Eligibility

| Scenario | Plan | Phase | Hours Since Renewal Charge | Refund Eligible? | Reason? |
|---|---|---|---|---|---|
| Cancel well into monthly cycle | Monthly | Active | 120 | yes | |
| Cancel shortly after monthly renewal | Monthly | Active | 6 | no | Within 24h of renewal |
| Cancel just before 24h window closes | Monthly | Active | 23 | no | Within 24h of renewal |
| Cancel just after 24h window | Monthly | Active | 25 | yes | |
| Cancel at exactly 24h boundary | Monthly | Active | 24 | ? | Open: is boundary inclusive or exclusive? |
| Cancel well into annual cycle | Annual | Active | 2160 | yes | |
| Cancel shortly after annual renewal | Annual | Active | 18 | no | Within 24h of renewal |
| Cancel during free trial | Monthly | Trial | — | no | Trial is free; no charge made |
| Cancel just after first post-trial charge | Monthly | Active (first cycle) | 10 | no | Within 24h of renewal |

---

## Refund Amount

| Scenario | Plan | Days in Cycle | Days Remaining | Daily Rate | Refund Amount? |
|---|---|---|---|---|---|
| Monthly, halfway through | Monthly | 30 | 15 | £0.333 | £5.00 |
| Monthly, near end of cycle | Monthly | 30 | 3 | £0.333 | £1.00 |
| Annual, one quarter remaining | Annual | 365 | 91 | £0.2627 | £23.90 |

The arithmetic is: `days remaining × daily rate`, rounded to the nearest penny. This table is only reached when eligibility (above) is `yes`.

---

## Open Questions

A few things the rules leave unresolved — worth pinning down before implementation:

1. **24h boundary**: Is `hours since renewal = 24` a refund or no refund? (The rows marked `?` above.)
2. **Month length**: Is a monthly cycle always 30 days, or does it follow the calendar (28–31 days)? This affects the daily rate and therefore refund amount.
3. **Rounding direction**: For refund amounts, do you round half-up, half-even, or always truncate? (Matters at boundaries like £0.5005.)
4. **Trial cancellation access**: When a subscriber cancels mid-trial, do they lose access immediately or retain it until day 30?
5. **Annual plan availability on signup**: Can a new subscriber choose annual directly, bypassing the trial? Or is the trial mandatory for all new accounts?
6. **Upgrade during trial**: If a subscriber upgrades to annual during their trial, does the 30-day trial still apply, and does the "no refund within 24h" rule apply to that first annual charge?