## Assumptions (resolving the loyalty/trial ambiguity)

The six rules don't fully determine behaviour where trial and loyalty/annual interact. I'm resolving the gaps as follows — flag any of these if they don't match intent:

1. **Trial is monthly-only at signup.** Signing up directly for annual charges the full (or loyalty-discounted) annual price immediately — no trial.
2. **Trial always converts to monthly.** Since the loyalty discount rule is annual-only, a converting trialist never gets the discount, even if they're a loyalty member — the plan they land on is monthly, not annual.
3. **Switching to annual mid-trial ends the trial immediately** (annual has no trial) and charges the annual price, discounted if already a loyalty member. This isn't stated in the rules directly — it's the most literal reading of "trial not available on annual," but worth confirming.
4. **Cancelling during the trial triggers no charge and no refund** — proration only applies once a paid cycle has started.
5. **The 24-hour exception is plan-agnostic** and measured from the renewal charge; "within 24 hours" = `< 24h` → no refund, `≥ 24h` → prorated refund.
6. **Refund proration uses the price actually charged** (discounted annual price where applicable), not the list price.
7. **All amounts round to the nearest penny**, standard rounding.
8. **Open, unresolved:** if someone joins/leaves the loyalty programme mid-cycle, does the discount apply at the *next* charge only, or retroactively? Rules don't say — I've assumed next-charge-only but this needs a decision.

---

### Table 1 — Calculates Subscription Price from Plan and Loyalty Membership

| Scenario | Plan | Loyalty Member | Price? |
|---|---|---|---|
| Monthly plan, any loyalty status | Monthly | {yes, no} | £9.99 |
| Annual plan, not a loyalty member | Annual | no | £95.88 |
| Annual plan, loyalty member | Annual | yes | £86.29 |

*Note: discount = 10% off £95.88 = £86.292, rounded to £86.29. Loyalty status never affects monthly pricing.*

### Table 2 — Grants Free Trial Based on Plan Selected at Signup

| Scenario | Plan Selected | Trial Granted? | Trial Length (Days)? | Charged at Signup? |
|---|---|---|---|---|
| Signs up for monthly | Monthly | yes | 30 | no |
| Signs up for annual | Annual | no | | yes |

### Table 3 — Converts Trial Subscribers to Paid Plans

| Scenario | State Before | Loyalty Member | Trigger | State After? | Price Charged? |
|---|---|---|---|---|---|
| Trial completes, non-member | Trial (day 30) | no | 30 days elapse | Monthly, active | £9.99 |
| Trial completes, loyalty member | Trial (day 30) | yes | 30 days elapse | Monthly, active | £9.99 |
| Switches to annual during trial, non-member | Trial (any day) | no | switches to annual | Annual, active | £95.88 |
| Switches to annual during trial, loyalty member | Trial (any day) | yes | switches to annual | Annual, active | £86.29 |
| Cancels during trial | Trial (any day) | {yes, no} | cancels | Cancelled | £0.00 |

*Note: row 2 shows the key resolution — a loyalty member's trial still converts to monthly, so the discount (annual-only) never applies. Rows 3–4 are the assumed mid-trial-upgrade behaviour (assumption 3 above); flag if unsupported.*

### Table 4 — Determines Refund Eligibility from Time Since Renewal Charge

| Scenario | Plan | Time Since Last Charge | Refund Eligible? |
|---|---|---|---|
| Cancels shortly after charge | {Monthly, Annual} | 30 minutes | no |
| Cancels just before the 24-hour cutoff | {Monthly, Annual} | 23h 59m | no |
| Cancels exactly at the 24-hour cutoff | {Monthly, Annual} | 24h 00m | yes |
| Cancels just after the 24-hour cutoff | {Monthly, Annual} | 24h 01m | yes |
| Cancels well into the cycle | {Monthly, Annual} | 15 days | yes |

### Table 5 — Calculates Prorated Refund for Eligible Cancellations

| Scenario | Plan | Price Charged | Billing Cycle (Days) | Days Remaining | Refund Amount? |
|---|---|---|---|---|---|
| Monthly, cancels at earliest eligible point | Monthly | £9.99 | 30 | 29 | £9.66 |
| Monthly, cancels on last day of cycle | Monthly | £9.99 | 30 | 1 | £0.33 |
| Annual, not a loyalty member | Annual | £95.88 | 365 | 100 | £26.27 |
| Annual, loyalty member | Annual | £86.29 | 365 | 100 | £23.64 |

*Note: "29 days remaining" is the maximum possible for an eligible refund — anything earlier falls inside the 24h no-refund window from Table 4, so the two tables together cap the achievable refund at 29/30 of the cycle. Row 4 confirms the discount carries through to the refund base (assumption 6).*

---

**Open questions still needing a decision:** mid-cycle loyalty programme join/leave timing (point 8), and whether mid-trial upgrade to annual is actually a supported action at all (if not, table 3's rows 3–4 should be deleted, not resolved as assumptions).

These tables are ready to hand to `/tabletest` once confirmed.