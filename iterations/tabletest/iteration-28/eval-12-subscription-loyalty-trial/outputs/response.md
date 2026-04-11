Looking at your rules, I can see two distinct concerns: **pricing and trial eligibility** (who pays what, who gets a trial), and **cancellation refunds** (how much is returned). The loyalty/trial interaction is actually cleaner than it might seem — loyalty discounts only apply to annual plans, and trials only apply to monthly plans, so the two rules can never apply to the same subscriber simultaneously. But there are still edge cases worth pinning down.

Let me draft two tables.

---

## Table 1: Subscription Pricing & Trial Eligibility

| Scenario | Plan Type | Loyalty Member | Trial Available? | Monthly Charge? | Annual Charge? |
|---|---|---|---|---|---|
| Standard monthly signup | Monthly | no | yes | £9.99 | |
| Loyalty member on monthly | Monthly | yes | yes | £9.99 | |
| Standard annual signup | Annual | no | no | | £95.88 |
| Loyalty member on annual | Annual | yes | no | | £86.29 |

**Notes:**
- Loyalty discount (10%) only applies to annual. A loyalty member on monthly gets the trial but no discount.
- Annual plan subscribers never get a trial regardless of loyalty status — `{yes, no}` would work in that Loyalty Member cell for the "no trial" rule, but pricing differs so they need separate rows.
- £86.29 = £95.88 × 0.90 (rounded to pence)

**Open questions to resolve:**
- Can a subscriber who completes a trial upgrade to annual? If so, does the loyalty discount apply immediately at upgrade, or only at the next renewal?
- Does a loyalty member who joins an annual plan mid-year get the discount immediately, or from the next renewal cycle?

---

## Table 2: Cancellation Refund

| Scenario | Plan Type | Loyalty Member | Days into Cycle | Within 24h of Renewal | Refund? |
|---|---|---|---|---|---|
| Monthly, mid-cycle | Monthly | {yes, no} | 15 | no | £5.00 |
| Monthly, within 24h of renewal | Monthly | {yes, no} | 1 | yes | £0.00 |
| Trial period, cancelled before conversion | Monthly (trial) | {yes, no} | 20 | no | £0.00 |
| Trial converts, cancelled within 24h of first charge | Monthly | {yes, no} | 1 | yes | £0.00 |
| Annual standard, mid-year cancellation | Annual | no | 100 | no | £69.53 |
| Annual loyalty, mid-year cancellation | Annual | yes | 100 | no | £62.58 |
| Annual, within 24h of renewal | Annual | {yes, no} | 1 | yes | £0.00 |

**Refund calculations for traceability:**
- Monthly day 15: (15 unused / 30 day cycle) × £9.99 = £5.00
- Annual standard day 100: (265 unused / 365) × £95.88 = £69.53
- Annual loyalty day 100: (265 unused / 365) × £86.29 = £62.58

**Open questions:**
- Trial cancellation (row 3): no charge has been taken yet, so £0.00 refund seems right — but should this trigger any cancellation fee or just silently end?
- Does "within 24h of renewal" apply when the trial converts to monthly (first-ever charge)? The rule says "renewal charge" — is first charge a renewal? I've assumed yes in row 4 — worth confirming.
- If a loyalty member on annual loses their loyalty status mid-year, is their next renewal at full price (£95.88)? Does their mid-year refund use the discounted or full price?

---

The biggest unresolved area is the **loyalty status change mid-cycle** scenario, and the **trial-to-annual upgrade path** (if that's supported). Want to work through either of those now?