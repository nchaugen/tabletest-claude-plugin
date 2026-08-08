Worked through this using the spec-by-example approach — decision rules and arithmetic split into separate tables, boundaries made explicit, and every ambiguity resolved with a stated assumption rather than left implicit.

## Assumptions

1. **Trial applies to either plan.** "Auto-convert to monthly" is read as "auto-convert to a paid subscription, defaulting to Monthly when no plan was actively chosen." If a subscriber actively selects Annual, the same 30-day trial applies and the first charge is £95.88 instead. *(Flagged as an open question below — the literal text only describes Monthly.)*
2. **Every charge counts as a "renewal charge" for the 24-hour rule** — including the very first post-trial conversion charge, not just second-and-later renewals. This is the safer, simpler, and more consistent reading, and it closes an obvious trial-abuse gap (cancel instantly after conversion, keep the refund).
3. **The 24-hour window is inclusive of the boundary.** Exactly 24h00m00s since the charge still counts as "within 24 hours" → no refund. 24h00m01s onward is eligible.
4. **Proration denominator is the actual number of days in the current billing cycle** — 28/29/30/31 for monthly, 365/366 for annual — not a flat 30-day month or 365-day year. This is more accurate and avoids under/over-refunding in short or leap periods.
5. **Cancellation is always immediate** (access ends right away), whether or not a refund is granted. This is what makes "unused days" a coherent concept in the first place — if access continued to period end, there would be no unused days to refund.
6. **Rounding: standard round-half-up to the nearest penny.**
7. **Plan upgrade/downgrade is out of scope** — the given rules don't describe switching plans mid-subscription, so it isn't modelled (see Open Questions).

## Open Questions

- Does Annual get a free trial at all, or does Annual signup charge £95.88 immediately with no trial? (Assumption 1 picks "yes, same trial.")
- Does switching Monthly → Annual (or back) count as a cancellation-with-refund of the old plan plus a fresh charge, or a direct credit/swap? Not specified — needs a product decision before it can be spec'd.
- Is "within 24 hours" meant to include the boundary instant itself, or strictly less-than? (Assumption 3 picks inclusive.)

---

### Table 1 — Triggers a Billing Charge Across the Subscription Lifecycle

| Scenario | Subscription Age | Selected Plan | Charge Triggered? | Charge Amount? |
|---|---|---|---|---|
| Signup, trial begins | Day 0 | {Monthly, Annual} | no | — |
| Trial in progress | Day 15 | {Monthly, Annual} | no | — |
| Cancel during trial | Day 0–29 | {Monthly, Annual} | no | — |
| Trial ends, no plan actively chosen | Day 30 | Monthly (default) | yes | £9.99 |
| Trial ends, Annual actively chosen | Day 30 | Annual | yes | £95.88 |
| Monthly renewal | 30 days after prior charge | Monthly | yes | £9.99 |
| Annual renewal | 365/366 days after prior charge | Annual | yes | £95.88 |

---

### Table 2 — Refund Eligibility on Cancellation

| Scenario | Hours Since Last Charge | Which Charge | Refund Eligible? | Reason? |
|---|---|---|---|---|
| Cancel during trial | N/A — no charge made | — | no | No charge has been made yet |
| Cancel right after first (trial-conversion) charge | 0 | 1st (trial→paid) | no | Within 24 hours of the charge |
| Cancel exactly at the 24-hour mark | 24 | 1st (trial→paid) | no | Within 24 hours of the charge |
| Cancel just past the 24-hour mark | 24.5 | 1st (trial→paid) | yes | |
| Cancel right after a later renewal | 5 | 2nd renewal | no | Within 24 hours of the charge |
| Cancel well into a monthly cycle | 360 (15 days) | Monthly renewal | yes | |
| Cancel well into an annual cycle | 2,400 (100 days) | Annual renewal | yes | |

The 5th row exists specifically to confirm the 24-hour rule applies to every renewal charge, not just the first.

---

### Table 3 — Prorated Refund Calculation

`Refund = Cycle Price × Unused Days / Days in Cycle`, rounded half-up to the nearest penny.

| Scenario | Plan | Cycle Price | Days in Cycle | Days Used | Unused Days? | Refund Amount? |
|---|---|---|---|---|---|---|
| Exact rounding boundary (.XX5) | Monthly | £9.99 | 30 | 15 | 15 | £5.00 |
| Long month (31 days) | Monthly | £9.99 | 31 | 10 | 21 | £6.77 |
| Short month, non-leap Feb (28 days) | Monthly | £9.99 | 28 | 10 | 18 | £6.42 |
| Non-leap year | Annual | £95.88 | 365 | 100 | 265 | £69.61 |
| Leap year | Annual | £95.88 | 366 | 100 | 266 | £69.68 |
| Just past the 24h exception (1 day used) | Monthly | £9.99 | 30 | 1 | 29 | £9.66 |
| Last day of cycle | Monthly | £9.99 | 30 | 29 | 1 | £0.33 |
| Cancelled at the instant of renewal, no new charge yet | Monthly | £9.99 | 30 | 30 | 0 | £0.00 |

---

## Summary Spec

- **Trial (days 0–29):** no charge; cancelling here ends access immediately with no refund, since nothing was ever charged.
- **Day 30 (or annual equivalent):** first charge fires — £9.99 (Monthly, default) or £95.88 (Annual, if actively selected).
- **Any charge, including this first one, starts a 24-hour no-refund window.** Cancelling at or before the 24h mark ends access immediately with **no refund**. This applies uniformly to every renewal, not just the first.
- **Past the 24-hour window**, cancelling ends access immediately and refunds `Price × Unused Days / Days in that specific cycle`, rounded half-up to the penny — using real calendar days (28–31 for monthly, 365/366 for annual), not flat 30/365.
- Cancelling on the very last day before the next charge would post correctly yields a £0.00 refund via the formula itself — no special-case needed.
- Plan switching (upgrade/downgrade) is **not covered** by this spec — flagged as an open product decision.