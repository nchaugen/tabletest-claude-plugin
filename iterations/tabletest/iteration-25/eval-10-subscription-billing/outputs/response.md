Let me work through the behaviour systematically. The subscription rules are clear on the surface, but the cancellation logic has several cases worth pinning down with concrete examples before coding starts.

I'll start with the most interesting decision: **whether a cancellation earns a refund**.

---

## Table 1 — Cancellation Refund Eligibility

| Scenario | Plan | Subscription Phase | Hours Since Last Charge | Refund Eligible? | Reason? |
|---|---|---|---|---|---|
| Cancel mid-cycle, monthly | Monthly | Active | 168 | yes | |
| Cancel within 24h of monthly renewal | Monthly | Active | 12 | no | Within 24h of renewal |
| Cancel just outside 24h window (monthly) | Monthly | Active | 25 | yes | |
| At exactly 24h mark | Monthly | Active | 24 | ? | Is boundary inclusive or exclusive? |
| Cancel mid-cycle, annual | Annual | Active | 720 | yes | |
| Cancel within 24h of annual renewal | Annual | Active | 18 | no | Within 24h of renewal |
| Cancel during free trial | {Monthly, Annual} | Trial | — | no | No charge has been taken |
| Cancel within 24h of trial-to-monthly conversion charge | Monthly | Active | 6 | ? | Does the 24h rule apply to the first-ever charge? |

Two open questions are surfaced here. I'd suggest resolving both before implementation:

1. **24h boundary**: is "within 24 hours" strictly less than 24h, or does exactly 24h still block the refund?
2. **First charge after trial**: when the 30-day trial ends and the first £9.99 is charged, does that charge count as a "renewal charge" for the 24h exception?

---

## Table 2 — Prorated Refund Amount

The calculation itself is arithmetic — worth a small table to nail down the formula and edge cases, but not the main decision table.

| Scenario | Plan | Days Remaining in Cycle | Days in Billing Cycle | Refund Amount? |
|---|---|---|---|---|
| Half a month remaining | Monthly | 15 | 30 | £4.99 |
| One week remaining (31-day month) | Monthly | 7 | 31 | £2.26 |
| Nearly full month remaining | Monthly | 29 | 30 | £9.66 |
| Just under half a year remaining | Annual | 182 | 365 | £47.79 |
| One month remaining (annual) | Annual | 31 | 365 | £8.14 |

Three open questions here:

3. **Cycle length for monthly**: is it the actual calendar days in that month (28–31), or always a fixed 30?
4. **Cycle length for annual**: 365 always, or 366 in a leap year?
5. **Day of cancellation**: is the day you cancel counted as "remaining" or "used"? (i.e. if you cancel at 9am on day 10 of 30, do you get 20 or 21 days' refund?)

---

## What's still missing

A third table may be useful for **trial auto-conversion** behaviour — what exactly happens when the 30-day trial expires:

| Scenario | Trial Days Elapsed | Card on File | Outcome? | Plan After? |
|---|---|---|---|---|
| Trial expires normally | 30 | yes | Charge £9.99, convert to monthly | Monthly |
| Trial expires, no payment method | 30 | no | ? | ? |
| User cancels on trial day 29 | 29 | {yes, no} | Cancel, no charge | — |

**Open question 6**: what happens if a subscriber reaches day 30 with no valid payment method — do they get a grace period, or are they immediately suspended?

---

Once you've resolved the open questions (especially 1, 2, and 5 — those will directly affect whether edge-case cancellations get money back or not), these tables map directly to a `/tabletest` implementation.