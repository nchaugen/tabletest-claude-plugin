The key insight first: **the loyalty discount (annual-only) and the free trial (monthly-only) are mutually exclusive by design** — a subscriber cannot be in both states simultaneously. That resolves the core ambiguity you flagged. The interesting edge cases are elsewhere.

Three tables, one concern each:

---

### Table 1: Trial Eligibility

| Scenario | Plan | Loyalty Member | Trial Available? | Reason? |
|---|---|---|---|---|
| New subscriber on monthly | Monthly | {yes, no} | yes | |
| New subscriber on annual | Annual | {yes, no} | no | Annual plans excluded |
| Returning subscriber re-subscribing | Monthly | {yes, no} | ? | Open: "new subscribers" — does this mean first-ever only? |

*Loyalty membership is irrelevant to trial availability in all cases — expressed as `{yes, no}`.*

---

### Table 2: Amount Charged

| Scenario | Plan | Loyalty Member | Billing Occasion | Amount Charged? |
|---|---|---|---|---|
| New monthly, trial period | Monthly | {yes, no} | Sign-up | £0.00 |
| Monthly, trial converts to paid | Monthly | no | First renewal | £9.99 |
| Monthly renewal, loyalty member | Monthly | yes | Any renewal | £9.99 |
| New annual, no loyalty | Annual | no | Sign-up | £95.88 |
| New annual, loyalty member | Annual | yes | Sign-up | £86.29 |
| Annual renewal, still a loyalty member | Annual | yes | Annual renewal | £86.29 |
| Annual renewal, loyalty lapsed | Annual | no | Annual renewal | £95.88 |

*Row 3 is an important explicit statement: loyalty membership does not discount monthly plans.*

**Open question:** If a loyalty member signs up for annual mid-year, does the 10% apply immediately or from the next renewal?

**Open question:** If loyalty membership lapses during an annual period, does the discounted rate hold for the rest of that period?

---

### Table 3: Cancellation Refund

| Scenario | Plan | Loyalty Member | Day Cancelled | Cycle Length | Amount Charged | Within 24h of Renewal? | Refund? |
|---|---|---|---|---|---|---|---|
| Monthly, cancel during trial | Monthly | {yes, no} | Day 15 | 30-day trial | £0.00 | no | £0.00 |
| Monthly, mid-cycle | Monthly | {yes, no} | Day 10 | 30 days | £9.99 | no | £6.66 |
| Monthly, near end of cycle | Monthly | {yes, no} | Day 29 | 30 days | £9.99 | no | £0.33 |
| Monthly, within 24h of renewal | Monthly | {yes, no} | Day 1 of new cycle | 30 days | £9.99 | yes | £0.00 |
| Annual, mid-year, no loyalty | Annual | no | Day 100 | 365 days | £95.88 | no | £69.61 |
| Annual, mid-year, loyalty member | Annual | yes | Day 100 | 365 days | £86.29 | no | £62.65 |
| Annual, within 24h of renewal | Annual | {yes, no} | Day 1 of new cycle | 365 days | {£95.88, £86.29} | yes | £0.00 |

*Refund arithmetic: (unused days / cycle days) × amount charged. The loyalty row uses the discounted amount — the refund is on what was actually paid, not the list price.*

*The final row uses value sets to state: the 24h no-refund rule applies regardless of loyalty status or which rate was charged.*

---

### Remaining open questions

| # | Question | Why it matters |
|---|---|---|
| 1 | Are returning subscribers (who previously cancelled) eligible for a new free trial? | Rules say "new subscribers" — is that first-ever or first on a given plan? |
| 2 | Can a subscriber on a free trial switch to annual mid-trial? | If yes: does the trial end immediately? Are they charged £95.88 (or £86.29 if loyalty member) straight away? |
| 3 | When does loyalty discount first apply on annual sign-up — immediately or from renewal? | Affects the very first charge |
| 4 | If loyalty membership lapses mid-annual period, does the discounted rate hold until renewal? | Could be significant since the annual period is 365 days |
| 5 | What is the rounding convention for prorated refunds? | Nearest penny, always round down, or other? |

Questions 3 and 4 are the ones most worth resolving before implementation — they directly affect the billing logic for annual loyalty subscribers.