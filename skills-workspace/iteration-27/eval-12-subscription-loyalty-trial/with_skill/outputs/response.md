These rules break into two clean concerns: **what subscribers are charged** and **what they're refunded when they cancel**. The loyalty-trial interaction is actually straightforward in most cases — the complexity lives in the cancellation refund rules. Let me work through both, and surface the open questions.

---

## Subscription Charge

The key insight: the loyalty discount only applies to annual plans, and trials only apply to monthly plans. They don't interact at all at signup — but it's worth making that explicit with concrete rows.

| Scenario | Plan | Loyalty Member | Charge at Start? | Billing Begins? |
|---|---|---|---|---|
| Standard monthly signup | Monthly | no | £0 | After 30-day trial |
| Loyalty member, monthly signup | Monthly | yes | £0 | After 30-day trial |
| Standard annual signup | Annual | no | £95.88 | Immediately |
| Loyalty member, annual signup | Annual | yes | £86.29 | Immediately |
| Monthly post-trial renewal | Monthly | {yes, no} | £9.99 | Monthly from then on |
| Annual renewal | Annual | no | £95.88 | Yearly from then on |
| Loyalty member, annual renewal | Annual | yes | £86.29 | Yearly from then on |

**Note:** £86.29 = £95.88 × 90%, rounded to the nearest penny. Worth confirming rounding convention.

The monthly loyalty member rows make explicit that joining the loyalty programme doesn't affect a monthly subscriber's price — they'd need to switch to annual to benefit.

---

## Cancellation Refund

This is where the edge cases live. The interesting rules are: proration, the 24-hour exception, and how the loyalty discount affects the refund base.

| Scenario | Plan | Loyalty Member | Days Used in Cycle | Days in Cycle | Price Paid | Within 24h of Renewal? | Refund? |
|---|---|---|---|---|---|---|---|
| Monthly, mid-cycle cancellation | Monthly | no | 15 | 30 | £9.99 | no | £5.00 |
| Monthly, within 24h of renewal | Monthly | no | 1 | 30 | £9.99 | yes | £0 |
| Annual, mid-year cancellation | Annual | no | 100 | 365 | £95.88 | no | £69.66 |
| Loyalty annual, mid-year — discounted basis | Annual | yes | 100 | 365 | £86.29 | no | £62.63 |
| Loyalty annual, mid-year — full price basis | Annual | yes | 100 | 365 | £86.29 | no | £69.66 |
| Annual, within 24h of renewal | Annual | {yes, no} | 1 | 365 | {£95.88, £86.29} | yes | £0 |
| Cancellation during free trial | Monthly | {yes, no} | 15 | 30 (trial) | £0 | no | £0 |
| Trial auto-converts, cancel within 24h | Monthly | {yes, no} | 1 | 30 | £9.99 | ? | ? |

---

## Open Questions

**1. Refund basis for loyalty annual members (rows 4 & 5 above)**
The loyalty member paid £86.29, not £95.88. When they cancel mid-year, is the refund calculated on:
- **£86.29** (what they actually paid) → £62.63 for the example above, or
- **£95.88** (the undiscounted rate) → £69.66

The first is the simpler interpretation. The second would mean the loyalty discount partially evaporates on cancellation, which seems surprising.

**2. Trial auto-conversion and the 24-hour rule (last row)**
When the 30-day trial ends and the first monthly charge of £9.99 is taken automatically, does the 24-hour no-refund rule apply to that first charge?

- If **yes**: a subscriber who tries the service and cancels on day 31 gets nothing back, even though they didn't actively choose to renew.
- If **no**: the 24-hour exception only applies to ongoing renewals, and the first real charge follows normal proration rules.

This is probably the most surprising edge case for new subscribers — worth a deliberate policy decision.

**3. Can a monthly subscriber (on trial or active) upgrade to annual?**
If yes: do they lose remaining trial days? Does the loyalty discount apply immediately if they're a loyalty member? This could open another table if plan-switching is in scope.

---

Once you've resolved questions 1 and 2, the refund table is complete and ready to hand to `/tabletest` for implementation.