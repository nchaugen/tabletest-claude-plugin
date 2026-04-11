I'll work through the rules and propose two tables — one for pricing at signup (where the trial and loyalty rules live), and one for cancellation refunds. The loyalty/trial interaction will surface as concrete rows and open questions.

---

## Subscription Pricing

| Scenario | Plan | Loyalty Member | Trial Period? | Charge at Signup? | Recurring Charge? |
|---|---|---|---|---|---|
| New monthly subscriber | Monthly | {yes, no} | 30 days | £0 | £9.99/month |
| New annual subscriber | Annual | no | none | £95.88 | £95.88/year |
| New annual loyalty subscriber | Annual | yes | none | £86.29 | £86.29/year |

The `{yes, no}` on monthly rows makes explicit that loyalty membership does not affect monthly pricing or trial eligibility — the discount is annual-only.

---

## Cancellation Refund

| Scenario | Plan | Loyalty Member | Billing Stage | Days Used | Cycle Length | Within 24h of Renewal Charge | Refund? |
|---|---|---|---|---|---|---|---|
| Cancel during trial | Monthly | {yes, no} | Trial | — | 30 days | n/a | £0 |
| Cancel mid-month | Monthly | {yes, no} | Active | 10 | 30 days | no | £6.66 |
| Cancel within 24h of monthly renewal | Monthly | {yes, no} | Active | 1 | 30 days | yes | £0 |
| Cancel mid-year, standard annual | Annual | no | Active | 100 | 365 days | no | £69.61 |
| Cancel mid-year, loyalty annual | Annual | yes | Active | 100 | 365 days | no | £62.65 |
| Cancel within 24h of annual renewal | Annual | {yes, no} | Active | 1 | 365 days | yes | £0 |

Refunds are traceable: £6.66 = (20/30) × £9.99; £69.61 = (265/365) × £95.88; £62.65 = (265/365) × £86.29. The loyalty discount affects the annual refund because it changes what was actually paid.

---

## Open Questions

Two edge cases need a decision before these tables are complete:

**1. Trial-to-monthly conversion and the 24h exception**

When the 30-day trial ends, the subscriber is auto-converted and charged their first £9.99. If they cancel the next morning — is that "within 24 hours of a renewal charge"? The current rules say yes, which would mean no refund. But this is their very first charge, not a renewal in the usual sense. Which behaviour is intended?

| Scenario | Plan | Billing Stage | Days Used | Within 24h of First Monthly Charge | Refund? |
|---|---|---|---|---|---|
| Cancel day after trial converts | Monthly | Active | 1 | yes | ? |

**2. Mid-trial switch to annual**

If a subscriber on the monthly trial switches to annual before day 30, several things are unclear:

| Scenario | Plan Switched To | Loyalty Member | Days of Trial Used | Charge at Switch? | Remaining Trial Days? |
|---|---|---|---|---|---|
| Switch to annual mid-trial | Annual | no | 15 | ? | ? |
| Switch to annual mid-trial | Annual | yes | 15 | ? | ? |

Specifically:
- Are the unused 15 trial days forfeited, or credited?
- Is the annual charge (discounted or otherwise) applied immediately at switch, or at day 30?
- Does loyalty discount apply from the moment of switch?

These two scenarios are the loyalty/trial interaction you flagged. Once these are decided, both tables can be completed with those rows filled in.