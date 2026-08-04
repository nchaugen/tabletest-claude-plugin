Here's the spec, built as three focused example tables (one rule per table) plus the assumptions needed to resolve the trial/loyalty ambiguity you flagged.

## Table 1 — Grants Free Trial Only on the Monthly Plan

| Scenario | Plan Selected | Loyalty Member | Trial Granted? | Immediate Charge? |
|---|---|---|---|---|
| New subscriber picks monthly | Monthly | {yes, no} | yes | no |
| New subscriber picks annual | Annual | {yes, no} | no | yes |

Loyalty membership is a value set here — it does not affect trial eligibility, only price (Table 2). This directly answers "does a loyalty member get a trial on annual?" — no, plan choice alone decides it.

## Table 2 — Applies the Loyalty Discount Only to Annual Plans

| Scenario | Plan | Loyalty Member | List Price | Price Charged? |
|---|---|---|---|---|
| Monthly, discount never applies | Monthly | {yes, no} | £9.99 | £9.99 |
| Annual, non-member | Annual | no | £95.88 | £95.88 |
| Annual, loyalty member | Annual | yes | £95.88 | £86.29 |

Applies to any charge event on that plan — initial annual charge, or the trial-to-monthly conversion charge. The monthly row's value set shows loyalty membership makes no difference off the annual plan, resolving "what if a trial user is a loyalty member?" — nothing, until/unless they're on annual.

## Table 3a — Decides Whether the 24-Hour No-Refund Exception Applies

| Scenario | Hours Since Last Charge | Refund Cutoff (Hours) | Exception Applied? |
|---|---|---|---|
| Cancels just before the cutoff | 23 | 24 | yes |
| Cancels exactly at the cutoff | 24 | 24 | no |

Assumption: "within 24 hours" is strictly `<24h`; at exactly 24h the exception no longer applies.

## Table 3b — Prorates the Refund by Unused Days

| Scenario | Amount Paid For Cycle | Unused Days | Cycle Length (Days) | Exception Applied? | Refund Amount? |
|---|---|---|---|---|---|
| Monthly plan, ordinary mid-cycle cancellation | £9.99 | 15 | 30 | no | £5.00 |
| Annual plan, loyalty discount already applied | £86.29 | 200 | 365 | no | £47.28 |
| Cancellation blocked by the 24-hour exception | £9.99 | 29 | 30 | yes | £0.00 |
| Cancellation on the final day (nothing unused) | £9.99 | 0 | 30 | no | £0.00 |
| Cancellation during the free trial | £0.00 |  |  | no | £0.00 |

This is the table that answers "does the discount apply to prorated refunds?" — yes, via row 2: the refund is computed off the *amount actually paid* (£86.29, already discounted), not the list price. It also separates the two different reasons a refund can be £0 — blocked by the exception vs. naturally zero — which the rules alone don't distinguish. Row 5 uses a blank for Unused Days/Cycle Length (no paid cycle exists yet) rather than 0, since the trial subscriber genuinely has no cycle to prorate.

---

## Assumptions made (non-interactive run, so stated rather than asked)

- **Rounding**: all charges/discounts/refunds round to the nearest penny (standard rounding).
- **Cycle lengths**: monthly = 30 days, annual = 365 days — not stated in the rules, inferred for proration math.
- **24-hour exception scope**: applies to *any* charge that starts a billing cycle — the initial annual charge and the trial→monthly conversion charge count as "renewal charges" for this purpose, not just recurring renewals.
- **Loyalty discount never applies during trial**: trial has no charge, so there's nothing to discount; discount only ever applies to an annual charge.
- **No mid-trial upsell to annual**: nothing in the rules lets a monthly-trial subscriber "become" annual before day 30; they either chose annual at signup (no trial) or chose monthly (trial, then convert to paid monthly).

## Open questions (not resolvable from the rules as given)

- Can a subscriber **switch plans post-conversion** (e.g. monthly → annual later) and claim the loyalty discount then? Not covered by any rule — flagging as out of scope until specified.
- What happens if a loyalty member's **programme membership lapses mid-annual-cycle** — does the discount get revoked, prorated, or honoured until renewal? Not specified.
- Is the 24-hour cutoff based on **wall-clock time** or could time zones/billing-system latency shift the boundary? Assumed wall-clock, exact.

These tables are ready to hand to `/tabletest` for implementation once you confirm the assumptions.