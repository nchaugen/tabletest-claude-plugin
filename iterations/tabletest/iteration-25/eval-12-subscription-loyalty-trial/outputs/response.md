The rules split naturally into two concerns: what subscribers are charged, and what refund they receive when they cancel. Let me build both tables, surfacing the edge cases you're unsure about as open questions.

---

## Table 1: Subscription Charge

| Scenario | Plan | Loyalty Member | In Trial | Charge? |
|---|---|---|---|---|
| New monthly subscriber during trial | Monthly | {yes, no} | yes | £0 |
| Monthly subscriber, post-trial renewal | Monthly | {yes, no} | no | £9.99/month |
| Annual subscriber, not a loyalty member | Annual | no | no | £95.88/year |
| Annual subscriber, loyalty member | Annual | yes | no | £86.29/year |
| Loyalty member on monthly plan | Monthly | yes | no | £9.99/month |

Note: Annual + trial is not a valid combination (rule 6), so no row for it. The `{yes, no}` on monthly rows records that loyalty membership does not affect monthly pricing.

**Open question — loyalty member chooses monthly:** Is £9.99/month correct, or does loyalty membership ever apply to monthly plans? The rules only mention annual, but worth confirming.

---

## Table 2: Cancellation Refund

| Scenario | Plan | Loyalty Member | Days Used in Cycle | Within 24h of Renewal? | Refund? |
|---|---|---|---|---|---|
| Monthly, mid-cycle cancellation | Monthly | {yes, no} | 15 of 30 | no | £5.00 (15 days × £0.333/day) |
| Monthly, cancel just before 24h window | Monthly | {yes, no} | 25h after renewal | no | prorated for remaining days |
| Monthly, cancel within 24h of renewal | Monthly | {yes, no} | 2h after renewal | yes | £0 |
| Trial cancellation (never billed) | Monthly | {yes, no} | n/a | n/a | £0 |
| Annual regular, mid-year cancellation | Annual | no | 182 of 365 | no | £48.06 (183 days × £0.263/day) |
| Annual loyalty, mid-year cancellation | Annual | yes | 182 of 365 | no | ? |
| Annual, cancel within 24h of renewal | Annual | {yes, no} | 2h after annual renewal | yes | £0 |

---

**Open questions to resolve:**

1. **Loyalty refund basis** — If a loyalty member on an annual plan cancels mid-year, is the prorated refund calculated on the discounted price (£86.29/year → £0.236/day) or the full price (£95.88/year → £0.263/day)? These give £43.25 vs £48.06 for a 6-month cancellation. The most consistent answer is the discounted price (they paid less, they get less back), but this needs confirming.

2. **24h rule on annual renewals** — The cancellation rule says "within 24 hours of a renewal charge." Does this apply to annual renewals too, or only monthly? A full year's charge within a 24h window is a significant amount to forfeit — worth making explicit.

3. **Trial → monthly conversion: first billing** — When the 30-day trial ends and the subscriber is auto-converted, the first £9.99 charge fires. Does the 24h no-refund rule apply if they cancel in the first 2 hours after that charge? Or is there a grace period for the first billing?

Once you've resolved those three, the tables are complete and ready to hand off to `/tabletest`.