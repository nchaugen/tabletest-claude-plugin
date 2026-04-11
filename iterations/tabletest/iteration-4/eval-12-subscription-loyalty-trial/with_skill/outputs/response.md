Looking at these rules, there are two clean concerns: **what gets charged at sign-up** and **what refund applies on cancellation**. Let me build both tables, then surface the genuine open questions about conversion.

---

## Table 1: Subscription Sign-up Billing

| Scenario | Plan | Loyalty Member? | Trial available? | Charge at sign-up? | Ongoing charge? |
|---|---|---|---|---|---|
| New monthly subscriber | Monthly | no | yes | £0 | £9.99/month after trial |
| Loyalty member, monthly plan | Monthly | yes | yes | £0 | £9.99/month after trial |
| New annual subscriber | Annual | no | no | £95.88 | £95.88/year |
| Loyalty member, annual plan | Annual | yes | no | £86.29 | £86.29/year |

**Note:** The loyalty discount only applies to annual — the rule doesn't extend it to monthly. A loyalty member starting a trial gets the trial, then £9.99/month, no discount.

---

## Table 2: Cancellation Refund

| Scenario | Plan | Amount charged | Days used | Within 24h of renewal? | Refund? |
|---|---|---|---|---|---|
| Monthly, mid-cycle | Monthly | £9.99 | 15 | no | £5.00 (15 unused × £0.333/day) |
| Monthly, day after renewal | Monthly | £9.99 | 1 | yes | £0 |
| Annual, mid-year | Annual | £95.88 | 180 | no | £48.59 (185 unused × £0.263/day) |
| Annual loyalty, mid-year | Annual | £86.29 | 180 | no | £43.74 (185 unused × £0.236/day) |
| Annual, day after renewal | Annual | £95.88 | 1 | yes | £0 |
| Trial subscriber, cancels before trial ends | Monthly | £0 | 10 | no | £0 (nothing charged yet) |

---

## Open Questions

These are the cases where the rules don't yet give a clear answer:

| Scenario | What happens? | Open Question |
|---|---|---|
| Loyalty member on trial, upgrades to annual | Trial ends early? | Does the remaining trial carry over, or does the annual charge start immediately? |
| Non-loyalty subscriber on trial, upgrades to annual | ? | Same — does the trial apply to the first annual billing period or is it forfeited? |
| Subscriber joins loyalty programme mid-annual plan | ? | Does the 10% discount apply from the next renewal, or is a prorated credit applied immediately? |
| Annual loyalty subscriber cancels: is refund based on £86.29 or £95.88? | ? | The prorated rate in Table 2 assumes £86.29 — confirm this is correct |

The loyalty/trial interaction at sign-up is actually clean (they can't both apply: loyalty discount is annual-only, trial is monthly-only). The ambiguity is entirely in **mid-subscription transitions**. Worth resolving the first two before implementing the upgrade flow.