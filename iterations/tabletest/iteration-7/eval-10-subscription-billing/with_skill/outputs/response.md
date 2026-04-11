Two distinct concerns here: whether a refund is given and how much (driven by proration and the 24h exception), and what happens during the trial period. I'll spec them as separate tables since they have different input shapes.

---

## Subscription Cancellation Refund

| Scenario | Plan | Within 24h of renewal? | Days remaining in cycle | Cycle length (days) | Refund given? | Refund amount? |
|---|---|---|---|---|---|---|
| Cancel mid-monthly cycle | Monthly | no | 20 | 30 | yes | £6.66 |
| Cancel near end of monthly cycle | Monthly | no | 5 | 30 | yes | £1.67 |
| Cancel within 24h of monthly renewal | Monthly | yes | 29 | 30 | no | £0.00 |
| Cancel exactly 24h after monthly renewal | Monthly | at boundary | 29 | 30 | ? | Open — see Q4 |
| Cancel mid-annual cycle | Annual | no | 335 | 365 | yes | £88.00 |
| Cancel within 24h of annual renewal | Annual | yes | 364 | 365 | no | £0.00 |

Refund formula: `days remaining × (plan price / cycle length)` — e.g. `20 × (£9.99 / 30) = £6.66`.

**Open questions:**
1. **Cycle length — monthly:** fixed 30 days, or the actual calendar month (28–31 days)? Matters for both proration and scheduling the next renewal.
2. **Cycle length — annual:** always 365 days, or 366 in a leap year?
3. **Rounding:** the £1.67 row rounds a £1.665 value — which direction, and to which precision?
4. **24h boundary:** if the subscriber cancels exactly 24h after renewal, are they inside or outside the no-refund window?

---

## Trial Lifecycle

| Scenario | Days since sign-up | Action | Plan | Charged? | Refund? |
|---|---|---|---|---|---|
| Cancel early in trial | 5 | Cancel | (none) | no | £0.00 |
| Cancel before trial expires | 28 | Cancel | (none) | no | £0.00 |
| Trial expires, auto-converts | 30 | (automatic) | Monthly | yes — £9.99 | n/a |
| Cancel on day 30, **after** renewal charge | 30 | Cancel | Monthly | yes — £9.99 | £0.00 — within 24h of renewal |

**Open questions:**
5. **Annual sign-up path:** can a user choose annual upfront, or does everyone start on the monthly trial? If they can choose annual directly, does the same 30-day trial apply?
6. **Trial expiry timing:** does the trial end at midnight on day 30, or exactly 720 hours after sign-up? This determines whether a "day 30 cancellation" can beat the renewal charge.
7. **Upgrade mid-subscription:** if a monthly subscriber upgrades to annual, is the remaining monthly cycle prorated? (Not covered by the current rules.)

---

**Worth flagging:** the last row in the trial table is likely to surprise users — someone who forgets to cancel during the trial, then cancels the same day they're first charged, gets no refund because the 24h no-refund window kicks in immediately. This probably needs explicit UX communication (e.g. "Your trial ends on [date]. Cancel before then to avoid being charged.").

Once questions 1, 4, and 6 are resolved, these tables are ready to hand to `/tabletest`.