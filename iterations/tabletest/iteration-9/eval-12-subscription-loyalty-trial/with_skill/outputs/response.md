The rules have several non-obvious interactions worth pinning down. Here are two focused tables and the open questions worth resolving before implementation.

---

## Table 1: Subscription Pricing

| Scenario | Plan | Loyalty Member | Trial applies? | Charge at signup? | Recurring charge? |
|---|---|---|---|---|---|
| Standard new subscriber | Monthly | no | yes — 30 days | £0 | £9.99/month |
| Loyalty member on monthly | Monthly | yes | yes — 30 days | £0 | £9.99/month |
| New annual subscriber | Annual | no | no | £95.88 | £95.88/year |
| Loyalty member on annual | Annual | yes | no | £86.29 | £86.29/year |

**Rule summary visible from the table:** the two features are orthogonal — trial is a function of plan (monthly only), loyalty discount is a function of plan (annual only). A loyalty member on monthly gets a trial but no discount; a loyalty member on annual gets a discount but no trial.

---

## Table 2: Cancellation Refund

| Scenario | Plan | Loyalty Member | Phase | Time Since Last Charge | Days Used | Days in Period | Refund? |
|---|---|---|---|---|---|---|---|
| Cancel during trial | Monthly | {yes, no} | Trial | n/a — nothing charged | 15 | 30 | £0 |
| Cancel monthly mid-cycle | Monthly | {yes, no} | Active | > 24h | 10 | 30 | £6.66 |
| Cancel monthly within 24h of renewal | Monthly | {yes, no} | Active | ≤ 24h | 0 | 30 | £0 |
| Cancel within 24h of trial→monthly conversion | Monthly | {yes, no} | Just converted | ≤ 24h | 0 | 30 | **?** |
| Cancel annual mid-cycle (standard) | Annual | no | Active | > 24h | 100 | 365 | £69.61 |
| Cancel annual mid-cycle (loyalty) | Annual | yes | Active | > 24h | 100 | 365 | £62.65 |
| Cancel annual within 24h of renewal | Annual | {yes, no} | Active | ≤ 24h | 0 | 365 | £0 |

Prorated formula: `charge paid × unused days / days in period`. For loyalty annual, refund is based on the discounted price paid (£86.29), not the list price.

---

## Open Questions

1. **24h window at trial-to-monthly conversion** — when the trial auto-converts and the first £9.99 charge fires, does the 24h no-refund window apply? The rule says "within 24 hours of a renewal charge" — is the initial post-trial charge a "renewal"? This is the most significant ambiguity. Row marked `?` above.

2. **Loyalty discount price rounding** — £95.88 × 0.9 = £86.292. Should this be £86.29 or £86.30? And is the prorated refund then calculated from the rounded figure?

3. **Loyalty status lost mid-annual-cycle** — if a subscriber loses loyalty programme membership partway through their annual period, does the discount apply at the next renewal, or is there an immediate mid-cycle adjustment?

4. **Switching from trial to annual** — can a subscriber on a 30-day trial elect to upgrade to annual (forfeiting the remaining trial days)? If so, do they pay the loyalty-discounted rate if they're a loyalty member?

5. **Loyalty member joins loyalty programme during trial** — no discount effect immediately (they're on monthly trial, discount is annual-only), but worth confirming this is intentional.

---

Questions 1 and 3 are the ones most likely to cause a production surprise. Worth getting a definitive answer before implementation.