Here's the full spec, structured as example tables per concern. Since this is non-interactive, I've made explicit assumptions where the rules were ambiguous — flagged in the **Assumptions** section and again as **Open Questions** where they need real confirmation.

## Assumptions

1. **Cycle length for proration**: Monthly cycle = 30 days, annual cycle = 365 days (not actual calendar days in the month). This avoids variable-month-length complexity.
2. **Refund formula**: `refund = round_half_up(plan_price × days_remaining / cycle_length_days, 2 decimal places)`.
3. **"Within 24 hours" boundary**: interpreted as `hours_since_charge < 24` → no refund; `hours_since_charge >= 24` → normal proration applies. (Flagged as open question below.)
4. The 24-hour no-refund window applies to **both** monthly and annual renewal charges, not just monthly.
5. Cancelling **during the trial** (before any charge has occurred) results in no charge and no refund logic — there's nothing to prorate.
6. Trial always converts to **Monthly**, per the stated rule, regardless of any plan preference indicated at signup. Annual subscribers are assumed to bypass the trial entirely and are charged immediately at signup (open question).
7. `Days Remaining in Cycle` is treated as a whole number for these examples; how partial-day cancellations round is an open question.
8. Plan upgrade/downgrade mid-cycle is out of scope — not mentioned in the source rules.

---

## Plan Reference Data

| Plan | Price? | Billing Cycle Length (Days)? |
|------|--------|-------------------------------|
| Monthly | £9.99 | 30 |
| Annual | £95.88 | 365 |

---

## Converts a Trial Subscriber to a Paid Monthly Plan

| Scenario | Days Since Signup | Trial Length (Policy, Days) | Cancelled? | Subscription State? | Charged Amount? |
|---|---|---|---|---|---|
| Mid-trial, still active | 15 | 30 | no | Trial | £0.00 |
| Last day of trial | 29 | 30 | no | Trial | £0.00 |
| Trial expires, auto-converts | 30 | 30 | no | Active — Monthly | £9.99 |
| Cancels mid-trial | 15 | 30 | yes | Cancelled | £0.00 |
| Cancels on last trial day | 29 | 30 | yes | Cancelled | £0.00 |

---

## Determines Refund Eligibility From the No-Refund Window After a Renewal Charge

| Scenario | Hours Since Renewal Charge | No-Refund Window (Policy, Hours) | Plan | Refund Eligible? |
|---|---|---|---|---|
| Just charged | 0 | 24 | {Monthly, Annual} | no |
| Still inside the window | 23 | 24 | {Monthly, Annual} | no |
| Exactly at the boundary | 24 | 24 | {Monthly, Annual} | yes |
| Just past the boundary | 25 | 24 | {Monthly, Annual} | yes |
| Well after the charge | 720 | 24 | {Monthly, Annual} | yes |

*Applies only once a renewal charge has occurred — trial-period cancellations never reach this rule (see previous table). Plan type does not affect eligibility, hence the value set.*

---

## Calculates the Prorated Refund Amount for Unused Days

*Rows assume `Refund Eligible? = yes` (see table above) — the no-refund window is not in play here.*

| Scenario | Plan Price | Cycle Length (Policy, Days) | Days Remaining in Cycle | Refund Amount? |
|---|---|---|---|---|
| Monthly, half the cycle left | £9.99 | 30 | 15 | £5.00 |
| Monthly, one day left | £9.99 | 30 | 1 | £0.33 |
| Monthly, cancels on last day | £9.99 | 30 | 0 | £0.00 |
| Monthly, almost the full cycle left | £9.99 | 30 | 29 | £9.66 |
| Annual, half the cycle left | £95.88 | 365 | 180 | £47.27 |
| Annual, one day left | £95.88 | 365 | 1 | £0.26 |
| Annual, cancels on last day | £95.88 | 365 | 0 | £0.00 |

---

## Overrides the Prorated Refund When Cancellation Falls Inside the No-Refund Window

| Scenario | Hours Since Renewal Charge | Plan Price | Cycle Length (Days) | Days Remaining in Cycle | Refund Eligible? | Refund Amount? |
|---|---|---|---|---|---|---|
| Cancels inside the window despite most of the cycle unused | 12 | £9.99 | 30 | 29 | no | £0.00 |

*Contrast with the "almost the full cycle left" row above (same 29 days remaining, but ≥24h since charge): that row pays out £9.66. This table shows the eligibility gate overrides the arithmetic entirely — it isn't just "a smaller refund," it's zero regardless of days unused.*

---

## Open Questions

1. **24-hour boundary inclusivity**: is exactly 24h00m since the charge eligible (as assumed), or does "within 24 hours" include the boundary itself (making it ineligible)?
2. **Annual + trial interaction**: does an annual-plan signup get the 30-day trial at all, and if so, does it convert to Monthly (per the literal rule) or to Annual? As written, the rule only mentions conversion to monthly, which seems to assume trial is a monthly-only on-ramp.
3. **Partial-day proration**: if a subscriber cancels partway through a calendar day, does the unused day count as used or unused? Affects the exact `Days Remaining in Cycle` value fed into the refund calculation.
4. **Rounding convention**: confirmed as round-half-up to the nearest penny — flag if the business uses a different convention (e.g., round-half-even/banker's rounding, or always round down in the company's favor).

Once these are confirmed, this table set is ready to hand off to `/tabletest` for executable test generation.