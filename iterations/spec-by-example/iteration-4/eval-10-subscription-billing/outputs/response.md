Here's the spec, built from concrete examples using the four distinct concerns hiding inside "cancellation with proration": trial conversion, refund eligibility, refund calculation, and how eligibility and calculation interact.

## Assumptions (stated explicitly since I can't ask follow-ups)

1. **Trial always converts to Monthly.** The annual plan has no trial — it's purchased outright. A trial user can't end up on annual via auto-conversion. *(Flag: confirm whether annual should get a trial, or support a trial→annual upgrade path.)*
2. Trial = days 1–30 from signup; the conversion charge fires entering day 31. Cancelling any time through day 30 avoids the charge entirely.
3. The "renewal charge" in the 24-hour exception includes the **initial trial→paid conversion charge**, not just later recurring renewals.
4. The 24-hour window is **inclusive**: exactly 24h00m since the charge still counts as "within 24 hours" → no refund. Refund eligibility starts strictly after 24h. *(Flag: could reasonably go the other way — confirm.)*
5. Proration uses a **fixed cycle length**, not actual calendar days: Monthly = 30 days, Annual = 365 days. *(Flag: real calendar months (28–31 days) and leap years would need a different rule if calendar-accurate proration is required.)*
6. "Days Remaining in Cycle" = whole days between cancellation and the next scheduled renewal, excluding the current (already-used) day.
7. Refunds round to the nearest penny, standard round-half-up.
8. Mid-subscription plan changes (monthly↔annual) are out of scope — not covered by the given rules.

---

### Converts Trial Subscribers to a Paid Plan

| Scenario | Cancelled During Trial | Day Cancelled | Resulting Plan? | Charge Made? |
|---|---|---|---|---|
| Cancels partway through the trial | yes | {5, 30} | None (subscription ends) | no |
| Completes the trial without cancelling | no | | Monthly | yes, £9.99 |

The value set `{5, 30}` states that cancelling any day up to and including the last trial day avoids the charge — there's no partial-trial forfeiture.

### Determines Whether a Cancellation Refund Applies

| Scenario | Plan | Hours Since Last Charge | Refund Window (Policy, hrs) | Refund Applies? |
|---|---|---|---|---|
| Cancels shortly after a charge | {Monthly, Annual} | 2 | 24 | no |
| Cancels at the exact edge of the exception window | {Monthly, Annual} | 24 | 24 | no |
| Cancels just after the exception window closes | {Monthly, Annual} | 25 | 24 | yes |
| Cancels well into the billing cycle | {Monthly, Annual} | 360 | 24 | yes |

The 24↔25 hour pair pins down Assumption 4's boundary; the value sets show the rule doesn't care which plan it is.

### Calculates Prorated Refund Amount

*(Assumes eligibility already established by the table above — this table is purely the arithmetic.)*

| Scenario | Plan | Plan Price | Billing Cycle (days) | Days Remaining | Daily Rate? | Refund Amount? |
|---|---|---|---|---|---|---|
| Monthly plan, cancels the day after renewal | Monthly | £9.99 | 30 | 29 | £0.333 | £9.66 |
| Monthly plan, cancels mid-cycle | Monthly | £9.99 | 30 | 15 | £0.333 | £5.00 |
| Monthly plan, cancels on the last day of the cycle | Monthly | £9.99 | 30 | 0 | £0.333 | £0.00 |
| Annual plan, cancels mid-cycle | Annual | £95.88 | 365 | 182 | £0.2627 | £47.81 |
| Annual plan, cancels near the end of the cycle | Annual | £95.88 | 365 | 1 | £0.2627 | £0.26 |

### Applies the 24-Hour Exception Ahead of Proration

*(This earns its own table — it's the one thing neither table above shows on its own: the exception overrides proration entirely, even with many unused days on the books.)*

| Scenario | Plan | Hours Since Last Charge | Days Remaining | Refund Amount? |
|---|---|---|---|---|
| Cancels within the exception window despite many unused days | Monthly | 2 | 29 | £0.00 |
| Cancels just after the exception window with the same unused days | Monthly | 25 | 29 | £9.66 |
| Cancels within the exception window right after trial converts to paid | Monthly | 1 | 29 | £0.00 |
| Cancels well outside the exception window with few unused days | Monthly | 360 | 5 | £1.67 |

Rows 1 and 2 share `Days Remaining = 29` and diverge purely on the hour count — proof the exception is a hard gate, not a factor in the proration formula. Row 3 pins down Assumption 3 (trial-conversion charge counts as a "renewal charge" for this rule).

---

## Open questions to confirm before building

- **Business risk**: an annual subscriber cancelling just past the 24h window very early in the year (e.g., day 3 of 365) is owed a refund north of £95 — nearly the full annual fee. Confirm this exposure is intentional.
- Does the 24-hour exception apply to the trial-conversion charge, or only true recurring renewals (Assumption 3)?
- Is the 24-hour boundary inclusive or exclusive (Assumption 4)?
- Should proration use actual calendar days instead of fixed 30/365 (Assumption 5)?
- Should annual plan ever be reachable via trial, or via upgrade from a monthly trial conversion?

This is markdown-table form, ready to hand to `/tabletest` if/when you want these turned into executable tests.