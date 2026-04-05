There are three distinct concerns here — I'd suggest three tables. Let me draft them and surface the open questions as I go.

---

## Table 1: Trial Eligibility

Straightforward — the two rules combine cleanly:

| Scenario | Plan | Loyalty Member | Trial Available? |
|---|---|---|---|
| New monthly subscriber | Monthly | no | yes |
| New monthly loyalty member | Monthly | yes | yes |
| New annual subscriber | Annual | no | no |
| New annual loyalty member | Annual | yes | no |

No surprises — loyalty status doesn't change trial eligibility.

---

## Table 2: Subscription Charge

What gets charged at each billing event:

| Scenario | Plan | Loyalty Member | In Trial | Amount Charged? |
|---|---|---|---|---|
| Monthly during trial | Monthly | no | yes | £0.00 |
| Monthly, post-trial | Monthly | no | no | £9.99 |
| Monthly loyalty member | Monthly | yes | no | £9.99 |
| Annual subscriber | Annual | no | no | £95.88 |
| Annual loyalty member | Annual | yes | no | £86.29 |

**Open question:** The rules give loyalty discounts on annual only. Is it intentional that monthly loyalty members pay full price? Worth confirming.

---

## Table 3: Cancellation Refund

This is where the rules interact. Key inputs: plan, loyalty status (affects price paid), whether they're in trial, within 24h of renewal, and days remaining:

| Scenario | Plan | Loyalty Member | In Trial | Within 24h of Renewal | Days Used | Cycle Days | Refund? |
|---|---|---|---|---|---|---|---|
| Cancel during trial | Monthly | no | yes | no | 10 | 30 | £0.00 |
| Monthly mid-cycle cancel | Monthly | no | no | no | 15 | 30 | £5.00 |
| Monthly cancel within 24h of renewal | Monthly | {yes, no} | no | yes | 29 | 30 | £0.00 |
| Annual mid-year cancel | Annual | no | no | no | 165 | 365 | £52.54 |
| Annual loyalty mid-year cancel | Annual | yes | no | no | 165 | 365 | £47.28 |
| Annual cancel within 24h of renewal | Annual | {yes, no} | no | yes | 364 | 365 | £0.00 |

Traces: `£9.99 × 15/30 = £5.00` · `£95.88 × 200/365 = £52.54` · `£86.29 × 200/365 = £47.28`

Value sets on the within-24h rows confirm: loyalty status is irrelevant when the no-refund rule applies.

---

## Open Questions

These aren't covered by the rules as stated:

1. **Trial-to-annual upgrade.** If a monthly subscriber is in their 30-day trial and wants to switch to annual — do they: lose the trial and pay immediately, complete the trial first, or is switching blocked during trial?

2. **Loyalty status changes mid-cycle.** If an annual subscriber gains loyalty status after paying full price, or loses it mid-year — does the discount apply at the *next* renewal only, or is there a partial adjustment mid-cycle?

3. **Cancel during trial — is it "£0 refund" or "£0 charged, no refund needed"?** The distinction matters for how you model it: one is a refund decision of zero, the other is a billing event that never happened. The table above treats it as the latter.

4. **Rounding rule** for prorated refunds: round to nearest penny, always up, always down?

The loyalty-discount × trial interaction is actually simple (they're mutually exclusive — annual has loyalty discounts but no trial, monthly has trial but no loyalty discount). The thornier questions are #1 (mid-trial plan change) and #2 (loyalty status timing).

Want to resolve any of these, or shall I adjust any rows in the tables?