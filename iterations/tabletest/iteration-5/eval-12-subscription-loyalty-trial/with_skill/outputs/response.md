Let me build this out across a few tables — the rules split into distinct concerns — and flag the open questions where the loyalty/trial interaction is genuinely unclear.

---

## Table 1: Subscription Cost

| Scenario | Plan | Loyalty Member? | Price? |
|---|---|---|---|
| Standard monthly subscriber | Monthly | no | £9.99/month |
| Monthly subscriber in loyalty programme | Monthly | yes | £9.99/month |
| Standard annual subscriber | Annual | no | £95.88/year |
| Annual subscriber in loyalty programme | Annual | yes | £86.29/year |

**Notes:** Loyalty discount (10%) only applies to annual. Monthly subscribers in loyalty get no discount.

---

## Table 2: Trial Availability

| Scenario | Plan | New Subscriber? | Trial Available? | Trial Duration? |
|---|---|---|---|---|
| New monthly subscriber | Monthly | yes | yes | 30 days |
| Existing subscriber re-subscribes | Monthly | no | no | |
| New annual subscriber | Annual | yes | no | |
| New annual subscriber in loyalty programme | Annual | yes | no | |

**Note:** The last row makes explicit that loyalty membership does not unlock a trial on annual plans.

---

## Table 3: Cancellation Refund

| Scenario | Plan | Time Since Last Charge | Refund? | Refund Amount? |
|---|---|---|---|---|
| Cancel mid-cycle, monthly | Monthly | 15 days in | yes | 15 unused days prorated |
| Cancel within 24h of monthly renewal | Monthly | < 24 hours | no | |
| Cancel mid-year, annual | Annual | 6 months in | yes | ~6 months prorated |
| Cancel within 24h of annual renewal | Annual | < 24 hours | no | |
| Cancel during free trial | Monthly (trial) | N/A — no charge taken | ? | Open question |

---

## Open Questions: Loyalty + Trial Interaction

These are the cases where the rules leave genuine gaps. Rather than invent answers, mark them for resolution:

| Scenario | Plan | Loyalty Member? | Subscribed as New? | Expected Outcome? | Open Question |
|---|---|---|---|---|---|
| Loyalty member signs up for monthly to get trial, then upgrades to annual | Monthly → Annual | yes | yes | ? | Does switching to annual during trial forfeit the remaining trial days? When does the loyalty discount start — immediately on switch, or at next renewal? |
| Non-loyalty subscriber on monthly trial joins loyalty programme during trial | Monthly (trial) | joins mid-trial | yes | ? | No discount applies (monthly), but when trial ends and converts to monthly: does the loyalty discount ever apply if they stay monthly? |
| Loyalty member wants both trial and discount | Annual | yes | yes | no trial available | Clear — no trial on annual. But should the system proactively tell them they can get a trial on monthly instead? Out of scope? |
| Loyalty member on annual plan leaves loyalty programme mid-year | Annual | loses membership | — | ? | Does the discount disappear immediately, at next renewal, or is it locked for the current annual period? |

---

The first two interaction rows are the ones most likely to cause implementation surprises — particularly the "upgrade mid-trial" case, since it involves two plan changes and a billing edge. Worth resolving those before coding the plan-switch logic.