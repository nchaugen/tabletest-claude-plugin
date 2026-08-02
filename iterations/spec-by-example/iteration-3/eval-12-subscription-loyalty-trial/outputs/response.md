Here's the spec, decomposed into four tables — one per rule so each stays independently readable. Pricing, trial, and refund are separate concerns; the loyalty discount lives inside pricing (it's an input to price, not a separate decision).

## 1. Grants the Free Trial Only to Monthly Signups

| Scenario | Chosen Plan at Signup | Loyalty Member? | Free Trial Granted? |
|---|---|---|---|
| Monthly signup | Monthly | {yes, no} | yes |
| Annual signup | Annual | {yes, no} | no |
| Existing monthly trial subscriber upgrades to annual before the trial ends | Annual (mid-trial upgrade) | {yes, no} | ? |

Loyalty membership is a value set on every row because it doesn't affect trial eligibility either way — that's the direct answer to your question: **trial and loyalty discount never actually collide**, because trial requires monthly and the discount requires annual, and those are mutually exclusive at signup. The one genuine collision is the third row, and the rules you gave don't cover it — see Open Questions below.

## 2. Prices the Subscription Charge, Discounting Annual Plans for Loyalty Members

| Scenario | Plan | Loyalty Member? | List Price (£) | Charge Amount (£)? |
|---|---|---|---|---|
| Monthly subscriber | Monthly | {yes, no} | 9.99 | 9.99 |
| Annual subscriber, not a loyalty member | Annual | no | 95.88 | 95.88 |
| Annual subscriber who is also a loyalty member | Annual | yes | 95.88 | 86.29 |

Monthly loyalty membership is a value set — the rule only names annual, so a loyalty monthly subscriber still pays list price. Annual pays the discounted amount immediately at signup (no trial to precede it, per table 1).

## 3a. Blocks Refunds Within 24 Hours of a Renewal Charge

| Scenario | Plan | Loyalty Member? | Hours Since Renewal Charge | Refund Eligible? |
|---|---|---|---|---|
| Cancelled well within the window | {Monthly, Annual} | {yes, no} | 1 | no |
| Cancelled just before the cutoff | {Monthly, Annual} | {yes, no} | 23 | no |
| Cancelled exactly at the cutoff | {Monthly, Annual} | {yes, no} | 24 | ? |
| Cancelled just after the cutoff | {Monthly, Annual} | {yes, no} | 25 | yes |

Plan and loyalty status are value sets throughout — nothing in the rule suggests this exception depends on either.

## 3b. Prorates the Refund by Unused Days in the Cycle

| Scenario | Refund Eligible? | Amount Paid for Cycle (£) | Cycle Length (Days) | Unused Days | Refund Amount (£)? |
|---|---|---|---|---|---|
| Blocked by the 24-hour exception | no | 9.99 | 30 | 15 | 0.00 |
| Monthly subscriber, half the cycle remaining | yes | 9.99 | 30 | 15 | 5.00 |
| Annual loyalty member, most of the cycle remaining | yes | 86.29 | 365 | 200 | 47.28 |
| Nothing unused at cancellation | yes | 9.99 | 30 | 0 | 0.00 |

Row 3 is the other place the loyalty discount and refund logic actually meet: proration runs on the amount actually paid (£86.29), so the discount automatically flows through to the refund — no special-casing needed.

## Assumptions made (non-interactive run — flag any that are wrong)

- Round monetary results to the nearest penny, half up (95.88 × 0.9 = 86.292 → £86.29; 9.99 × 15/30 = £4.995 → £5.00).
- Monthly billing cycle = 30 days for proration purposes; annual = 365 days.
- "Renewal charge" in the 24-hour rule also covers the very first annual charge and the first post-trial monthly charge, not just later recurring renewals.
- Loyalty discount re-applies at every annual renewal, not just the first charge (status-based, not a one-time offer).
- Refund proration is computed on the amount actually charged (post-discount), not list price.

## Open questions (need a product decision, not an assumption)

- **Table 1, row 3**: if a monthly-trial subscriber upgrades to annual before day 30, does the trial forfeit immediately with an instant (discount-eligible) annual charge, or does the upgrade wait until the trial completes?
- **Table 3a, row 3**: is exactly 24h00m since the renewal charge inside or outside the no-refund window?

Once those two are resolved, this is ready to hand to `/tabletest` — the column structure carries over directly into the parameterised test.