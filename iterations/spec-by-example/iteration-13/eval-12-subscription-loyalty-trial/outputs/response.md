Here's the full spec, worked through as example tables. Since this is non-interactive, I've made explicit assumptions everywhere the rules were silent — flagged clearly so you can correct any of them in one pass.

## Assumptions made to resolve the ambiguity

1. **Trial only exists on Monthly.** Signing up for Annual skips the trial entirely and charges immediately (this is the direct reading of "free trial is not available on annual plans").
2. **Upgrading to Annual — from Trial or from an active Monthly cycle — ends the current state immediately** and starts the Annual cycle right away (no partial/annual trial, consistent with rule 6).
3. **An upgrade from Monthly-Active to Annual is treated as cancel-then-signup**: the unused Monthly time is refunded under the normal proration rule, and the Annual charge (with loyalty discount if applicable) is billed immediately.
4. **Loyalty discount eligibility is evaluated at the moment each charge occurs** (signup or renewal) and locked in for that cycle. A mid-cycle change in loyalty status doesn't retroactively change the current cycle's charge or refund — only the next charge event sees the new status.
5. **Refund is calculated on the amount actually paid for the cycle**, not recalculated against current loyalty status.
6. **"Within 24 hours of a renewal charge" is inclusive** — the no-refund rule applies at exactly 24h00m, not just under it.
7. **The 24-hour no-refund rule applies to any charge that starts a billing cycle**, including the very first charge (trial-conversion or immediate Annual signup), not only second-cycle-onward renewals. *(This is the most literal-reading-dependent assumption — "renewal" could arguably exclude first charges. Flagged again below.)*
8. **Monthly cycle = 30 days, Annual cycle = 365 days**, fixed regardless of calendar month length or leap years.
9. **10% discount rounds to the nearest penny**, standard round-half-up: £95.88 × 0.9 = £86.292 → **£86.29**.
10. **Trial is a one-time benefit at first signup** — re-subscribing after cancellation is out of scope here (see open questions).

---

### Table 1 — Calculates the subscription charge from plan and loyalty status

| Scenario | Plan | Loyalty Member? | Charge? |
|---|---|---|---|
| Monthly subscriber (loyalty status makes no difference) | Monthly | {yes, no} | £9.99 |
| Annual subscriber, not a loyalty member | Annual | no | £95.88 |
| Annual subscriber, loyalty member | Annual | yes | £86.29 |

*Note: discount rounds to the nearest penny, round-half-up.*

---

### Table 2 — Grants a free trial based on the plan selected at signup

| Scenario | Plan Selected | Trial Granted? | Trial Length (Days)? | Immediate Charge? |
|---|---|---|---|---|
| Signs up for Monthly | Monthly | yes | 30 |  |
| Signs up for Annual | Annual | no |  | yes |

*Note: all rows assume a first-time subscriber. Immediate Charge amount comes from Table 1.*

---

### Table 3 — Transitions subscribers between trial, active, and cancelled states

| Scenario | State Before | Days Remaining in Prior Paid Cycle | Action | Loyalty Member? | State After | Charge? | Refund? |
|---|---|---|---|---|---|---|---|
| Trial completes its 30 days | Trial (day 30) |  | trial ends | {yes, no} | Monthly Active | £9.99 |  |
| Cancels during the trial | Trial (day 15) |  | cancel | {yes, no} | Cancelled | £0.00 | £0.00 |
| Upgrades to Annual during the trial, not a loyalty member | Trial (day 15) |  | upgrade to Annual | no | Annual Active | £95.88 | £0.00 |
| Upgrades to Annual during the trial, loyalty member | Trial (day 15) |  | upgrade to Annual | yes | Annual Active | £86.29 | £0.00 |
| Monthly subscriber upgrades to Annual mid-cycle, not a loyalty member | Monthly Active | 20 | upgrade to Annual | no | Annual Active | £95.88 | £6.66 |
| Monthly subscriber upgrades to Annual mid-cycle, loyalty member | Monthly Active | 20 | upgrade to Annual | yes | Annual Active | £86.29 | £6.66 |

*Note: blank "Days Remaining"/"Refund" means the concept doesn't apply (no prior paid cycle during trial). The refund on upgrade rows follows the same proration rule as Table 5, applied to the Monthly cycle being abandoned; the new Annual charge follows Table 1.*

---

### Table 4 — Determines refund eligibility from time since the last charge

| Scenario | Plan | Hours Since Last Charge | No-Refund Window (Hours) | Refund Eligible? |
|---|---|---|---|---|
| Cancels shortly after a charge | {Monthly, Annual} | 1 | 24 | no |
| Just under the no-refund window | {Monthly, Annual} | 23 | 24 | no |
| Exactly at the no-refund window | {Monthly, Annual} | 24 | 24 | no |
| Just past the no-refund window | {Monthly, Annual} | 25 | 24 | yes |
| Cancels well into the cycle | {Monthly, Annual} | 720 | 24 | yes |

*Note: when not eligible, Refund = £0.00 regardless of days remaining. When eligible, the amount comes from Table 5.*

---

### Table 5 — Calculates the prorated refund for an eligible cancellation

| Scenario | Plan | Cycle Length (Days) | Cycle Charge | Days Remaining in Cycle | Refund? |
|---|---|---|---|---|---|
| No unused days left | Annual | 365 | £95.88 | 0 | £0.00 |
| Monthly, typical mid-cycle cancellation | Monthly | 30 | £9.99 | 20 | £6.66 |
| Monthly, cancels almost immediately (just past the eligibility window) | Monthly | 30 | £9.99 | 29 | £9.66 |
| Annual, typical mid-cycle cancellation, not a loyalty member | Annual | 365 | £95.88 | 265 | £69.61 |
| Annual, typical mid-cycle cancellation, loyalty member | Annual | 365 | £86.29 | 265 | £62.65 |
| Annual, cancels almost immediately (just past the eligibility window) | Annual | 365 | £95.88 | 364 | £95.62 |

*Note: "0 days remaining → £0.00" holds for any plan/charge combination — shown once here rather than duplicated per plan.*

---

## Open questions to confirm

- **Does "renewal charge" in the 24h exception include the very first charge** (trial→Monthly conversion, or immediate Annual signup), or only second-cycle-onward renewals? I assumed it includes the first charge (Table 4) — this is the most literal-reading-dependent call in the spec.
- **Does cancelling and re-subscribing later grant a new free trial?** Assumed no, not modeled.
- **Simultaneity**: if loyalty membership lapses at the exact instant of a renewal charge, which status wins? Assumed "status at moment of charge," but the exact ordering isn't specified.
- **Downgrade path** (Annual → Monthly) isn't in the original rule set — not modeled here; flag if it needs its own table.

Want me to hand this off to `/tabletest` to turn it into executable tests, or do you want to adjust any of the assumptions first?