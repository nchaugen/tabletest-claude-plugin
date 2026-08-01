Since this is non-interactive, I've worked through the elicitation myself and made the assumptions explicit. Four tables emerged as separate concerns: trial eligibility, pricing, plan switching (where your open question lives), and cancellation refunds.

## Assumptions (stated because no follow-up is possible)

- **"Within 24 hours of a renewal charge"** covers the very first annual charge too (there's no trial to soften it, so the first charge is treated as the cycle's "renewal" for this rule). This is genuinely ambiguous — flagged again below.
- **"Within 24 hours" is strictly less than 24h** — at exactly 24h00m, refund eligibility resumes.
- **Monthly cycle = 30 days, annual cycle = 365 days** for proration math (not actual calendar days).
- **Refunds are prorated from the price actually paid** — so a loyalty member's annual refund is prorated from the discounted £86.29, not the £95.88 list price.
- **Switching from trial to annual forfeits the remaining trial immediately** (annual has no trial concept at all, so there's nothing to carry over) and triggers an immediate charge.
- **Rounding**: nearest penny, round-half-up.
- **Loyalty membership never affects Monthly pricing or trial eligibility** — the discount rule only touches Annual. Shown explicitly below rather than left implicit.

---

## 1. Free Trial Eligibility

| Scenario | Subscriber Status | Selected Plan | Loyalty Member | Trial Granted? | Trial Length? |
|---|---|---|---|---|---|
| New subscriber picks monthly | New | Monthly | {yes, no} | yes | 30 days |
| New subscriber picks annual | New | Annual | {yes, no} | no | |
| Returning subscriber picks monthly | Returning | Monthly | {yes, no} | ? | ? |
| Returning subscriber picks annual | Returning | Annual | {yes, no} | no | |

**Open question**: the rules only say "new subscribers" get a trial. Whether a lapsed/returning subscriber counts as "new" again is undefined — marked `?`.

---

## 2. Subscription Price

| Scenario | Plan | Loyalty Member | Loyalty Discount (Policy) | Price? |
|---|---|---|---|---|
| Monthly plan | Monthly | {yes, no} | 10% | £9.99 |
| Annual plan, not a loyalty member | Annual | no | 10% | £95.88 |
| Annual plan, loyalty member | Annual | yes | 10% | £86.29 |

Value set `{yes, no}` on the Monthly row makes explicit that loyalty membership is irrelevant to the monthly price — it's not that the case wasn't considered.

---

## 3. Plan Switching (the trial/loyalty edge case)

| Scenario | Current State | Loyalty Member | Switches To | Trial Forfeited? | Charged Immediately? | Price Charged? |
|---|---|---|---|---|---|---|
| Trial subscriber switches to annual early | On trial, day 5 of 30, Monthly track | no | Annual | yes | yes | £95.88 |
| Trial subscriber switches to annual early, loyalty member | On trial, day 5 of 30, Monthly track | yes | Annual | yes | yes | £86.29 |
| Trial subscriber lets trial run out | On trial, day 30 of 30, Monthly track | {yes, no} | *(auto-converts to Monthly)* | no | yes | £9.99 |
| Active monthly subscriber switches to annual | Monthly, active, post-trial | no | Annual | n/a — no trial active | yes | £95.88 |
| Active monthly subscriber switches to annual, loyalty member | Monthly, active, post-trial | yes | Annual | n/a — no trial active | yes | £86.29 |
| Annual subscriber switches to monthly mid-cycle | Annual, active | {yes, no} | Monthly | n/a | ? | ? |

**This directly answers your open question**: a loyalty member on trial gets zero benefit from that membership *until* they're actually on the annual plan — the trial is a monthly-track concept, and switching to annual ends it outright rather than blending the two. The discount only ever prices the annual charge itself.

**Open question**: switching *from* annual to monthly mid-cycle isn't covered by any given rule — does it trigger the same prorated-refund logic as a cancellation (§4), or does the remaining annual time just get forfeited? Needs a decision.

---

## 4a. Refund Eligibility on Cancellation

| Scenario | Time Since Renewal Charge | Refund Window (Policy) | Refund Given? |
|---|---|---|---|
| Cancel well after renewal | 10 days | 24 hours | yes |
| Cancel just inside the no-refund window | 23 hours 59 minutes | 24 hours | no |
| Cancel exactly at the window boundary | {24 hours, 24 hours 1 minute} | 24 hours | yes |
| Cancel during free trial (no charge has occurred yet) | n/a — nothing charged | 24 hours | n/a — no payment to refund |

The last row is deliberately `n/a`, not `no` — during a trial there's no charge to prorate against, which is a different situation from "charged but not refundable."

## 4b. Prorated Refund Amount (arithmetic only, once eligible)

Formula: `Refund = Price Paid × (Unused Days / Cycle Length)`, rounded to the nearest penny.

| Scenario | Price Paid | Cycle Length (Policy) | Unused Days | Refund Amount? |
|---|---|---|---|---|
| Monthly plan | £9.99 | 30 days | 10 | £3.33 |
| Annual plan, not a loyalty member | £95.88 | 365 days | 65 | £17.07 |
| Annual plan, loyalty member | £86.29 | 365 days | 65 | £15.37 |

The third row is the key point: the refund is computed from the *discounted* price the subscriber actually paid, not the list price — otherwise loyalty members would be over-refunded relative to what they were charged.

---

## Open questions to resolve before implementation

1. Do returning subscribers get a fresh trial? (§1)
2. Does "within 24 hours of a renewal charge" include the very first annual charge, or only genuine renewals? (assumed yes above)
3. What happens on an annual → monthly mid-cycle switch — refund, forfeiture, or credit? (§3)
4. If a loyalty membership lapses mid-annual-cycle, does the discount hold for the rest of the paid term or only for future renewals? (not covered by any given rule — worth asking the business)

Ready to hand off to `/tabletest` once these are resolved — the column structure carries straight over.