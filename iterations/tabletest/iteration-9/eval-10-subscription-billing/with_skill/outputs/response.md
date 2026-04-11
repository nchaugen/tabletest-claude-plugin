Here are the example tables for this feature. Three separate concerns emerge from the rules — I've split them out accordingly.

---

## Table 1: Cancellation Refund Eligibility

The key decision: does a cancellation produce a refund?

| Scenario | Plan Type | Subscription Status | Within 24h of Renewal Charge | Refund Eligible? | Reason? |
|---|---|---|---|---|---|
| Cancel during free trial | {Monthly, Annual} | Free Trial | n/a | no | No charge taken yet |
| Cancel within 24h of renewal | {Monthly, Annual} | Active | yes | no | Within no-refund window |
| Cancel just outside 24h, monthly | Monthly | Active | no | yes | Prorated for unused days |
| Cancel just outside 24h, annual | Annual | Active | no | yes | Prorated for unused days |
| Cancel on last day of cycle | {Monthly, Annual} | Active | no | yes | Refund of £0.00 — zero days remaining |
| Cancel at exactly 24h after renewal | {Monthly, Annual} | Active | ? | ? | **Open: is the 24h boundary inclusive or exclusive?** |

---

## Table 2: Prorated Refund Amount

The calculation when a refund is eligible. Cycle Length is shown explicitly because it is a policy value, not derived from the cancellation request.

| Scenario | Plan Type | Plan Cost | Cycle Length (Policy) | Days Remaining | Refund Amount? |
|---|---|---|---|---|---|
| Monthly, 20 days remaining | Monthly | £9.99 | 30 days | 20 | £6.66 |
| Monthly, 10 days remaining | Monthly | £9.99 | 30 days | 10 | £3.33 |
| Monthly, 1 day remaining | Monthly | £9.99 | 30 days | 1 | £0.33 |
| Monthly, 0 days remaining | Monthly | £9.99 | 30 days | 0 | £0.00 |
| Annual, 180 days remaining | Annual | £95.88 | 365 days | 180 | £47.28 |
| Annual, 1 day remaining | Annual | £95.88 | 365 days | 1 | £0.26 |
| Annual, 0 days remaining | Annual | £95.88 | 365 days | 0 | £0.00 |

Assumed formula: **Refund = Plan Cost × Days Remaining ÷ Cycle Length**, floored to the nearest penny. Confirm rounding rule — see open questions.

---

## Table 3: Free Trial Lifecycle

| Scenario | Days Since Trial Start | Event | Subscription Status After? | Charge Taken? |
|---|---|---|---|---|
| Subscriber cancels mid-trial | 10 | Manual cancellation | Cancelled | none |
| Trial expires, not cancelled | 30 | Auto-conversion | Monthly Active | £9.99 |
| Subscriber cancels on day 30 | 30 | Manual cancellation (same day as conversion) | ? | ? |

---

## Open Questions

1. **24h boundary**: Is "within 24 hours" strictly less than 24h, or does exactly 24h also fall inside the no-refund window?

2. **First post-trial charge**: Does the 24h no-refund window apply to the first charge taken at trial conversion, or only to subsequent renewal charges? (This matters for a subscriber who converts and immediately regrets it.)

3. **Cancel on trial day 30**: If a subscriber cancels on the same day the trial expires, does the auto-conversion charge fire first — creating a 24h window scenario — or does the cancellation prevent conversion and the charge entirely?

4. **Monthly cycle length**: Is a monthly billing cycle always 30 days, or calendar-month days (28/29/30/31)? This affects the daily rate and whether the same plan costs the same to cancel mid-February vs mid-March.

5. **Annual plan + leap year**: Is the annual cycle always 365 days, or 366 in a leap year?

6. **Rounding**: How are fractional pence handled? (e.g. £9.99 ÷ 30 × 1 day = £0.333...) — floor, round half-up, or banker's rounding?

---

Questions 2 and 3 in particular are likely to surprise new team members and are worth resolving before implementation — they affect which rows in Table 1 are reachable in practice.