Here's the spec, worked through as a set of example tables. I split it into five small tables — trial eligibility, price calculation, trial-state transitions, refund eligibility, and refund arithmetic — because each is a distinct rule, and the loyalty/trial interaction falls out naturally once trial-eligibility and price-calculation are separated.

## Assumptions (the ambiguity you flagged, resolved explicitly)

Since these aren't stated in your rules, I made the following calls. Flag any of these that should go the other way:

1. **Annual signup skips the trial entirely and bills immediately.** "Free trial is not available on annual plans" means choosing Annual at signup triggers an immediate charge, not a trial.
2. **A trial subscriber can request an upgrade to Annual mid-trial.** This forfeits the remaining trial (trial + Annual are mutually exclusive) and triggers an immediate Annual charge, discounted if they're a loyalty member. This action isn't in your rule list — I inferred it needs to exist, since otherwise "trial + loyalty + annual" has no path to interact at all. Confirm this is actually a supported action.
3. **Auto-conversion at trial end always lands on Monthly, never Annual, regardless of loyalty status** — this one's explicit in your rules, just stating it for completeness since it's the thing that makes loyalty-discount-during-trial a non-event.
4. **The loyalty discount is reassessed at every charge (signup, renewal, mid-trial upgrade), not fixed at signup.** A subscriber who joins or leaves the loyalty programme mid-term gets re-evaluated at their next Annual charge.
5. **Refunds are calculated on the amount actually paid** (i.e., post-discount for loyalty members), not the list price.
6. **The 24-hour no-refund window is inclusive of the 24h00m boundary itself** — cancelling at exactly 24h00m after a charge still gets no refund; eligibility starts strictly after that.
7. **Proration uses fixed cycle lengths**: 30 days for Monthly, 365 for Annual (not actual calendar/leap-year days).
8. **Discount rounding**: standard round-half-up to the nearest penny (£95.88 × 0.9 = £86.292 → £86.29).

---

### Table 1 — Grants Trial Eligibility at Signup

| Scenario                  | Plan Selected | Loyalty Programme Member | Trial Granted? | Charged Today? |
|----------------------------|----------------|---------------------------|-----------------|------------------|
| Monthly plan chosen         | Monthly        | {yes, no}                 | yes             | no               |
| Annual plan chosen          | Annual         | {yes, no}                 | no              | yes              |

*Loyalty status doesn't affect trial eligibility at all — only the plan does.*

### Table 2 — Calculates Subscription Amount Charged

| Scenario                              | Plan    | Loyalty Programme Member | Charge Occasion                                  | List Price | Amount Charged? |
|-----------------------------------------|---------|---------------------------|---------------------------------------------------|------------|--------------------|
| Monthly charge, any loyalty status      | Monthly | {yes, no}                 | {Trial Conversion, Renewal}                        | £9.99      | £9.99              |
| Annual charge, not a loyalty member     | Annual  | no                        | {Initial Signup, Renewal, Mid-Trial Upgrade}       | £95.88     | £95.88             |
| Annual charge, loyalty member           | Annual  | yes                       | {Initial Signup, Renewal, Mid-Trial Upgrade}       | £95.88     | £86.29             |

*This is the table that answers your question: the 10% discount applies uniformly to every Annual charge — first signup, renewal, or a mid-trial upgrade — as long as the subscriber is a loyalty member **at the moment of that charge**. It never applies to Monthly, no matter the loyalty status, because Monthly never carries the discount rule.*

### Table 3 — Transitions Trial State

| Scenario                                  | State Before    | Action                         | State After?              | Charge Occasion? |
|---------------------------------------------|------------------|----------------------------------|------------------------------|---------------------|
| Trial runs its full 30 days                | Trial (Monthly)  | 30 days elapse                   | Monthly Active               | Trial Conversion    |
| Subscriber cancels before trial ends        | Trial (Monthly)  | Cancel                           | Cancelled                    | (none — no charge)  |
| Subscriber upgrades to Annual mid-trial     | Trial (Monthly)  | Switch to Annual                 | Annual Active (trial forfeited) | Mid-Trial Upgrade |

*Amounts for each `Charge Occasion?` are looked up in Table 2 — this table only decides which occasion applies, not how much is charged.*

### Table 4 — Determines Refund Eligibility

| Scenario                                         | Hours Since Last Charge | Refund Eligible? |
|-----------------------------------------------------|---------------------------|---------------------|
| Cancels during trial, before any charge occurred    |                            | N/A — nothing paid  |
| Cancels exactly 24h00m after a charge               | 24:00                      | no                  |
| Cancels just past the 24h window                    | 24:01                      | yes                 |

### Table 5 — Calculates Prorated Refund Amount

| Scenario                                          | Plan    | Amount Paid At Last Charge | Cycle Length (Days) | Days Remaining | Refund Amount? |
|------------------------------------------------------|---------|-------------------------------|------------------------|------------------|--------------------|
| Monthly subscriber cancels mid-cycle                  | Monthly | £9.99                          | 30                      | 20               | £6.66              |
| Annual subscriber (no discount) cancels mid-cycle     | Annual  | £95.88                         | 365                     | 200              | £52.52             |
| Annual subscriber (loyalty discount) cancels mid-cycle| Annual  | £86.29                         | 365                     | 200              | £47.28             |

*The last row is the one that answers your original question concretely: a loyalty member's refund is prorated off the £86.29 they actually paid, not the £95.88 list price — so the discount carries through to the refund, not just the initial charge.*

---

## Open questions (need a product decision, not just an assumption)

- Is a mid-trial upgrade to Annual (Table 3, row 3) actually a feature you want, or should trial subscribers only be able to switch plans *after* the trial converts to Monthly?
- Should the 30-day Monthly cycle and 365-day Annual cycle use real calendar dates instead of fixed lengths (affects Table 5 precision by a few pence)?
- If a loyalty member's membership lapses *during* an active Annual term, is the discount removed only at the next renewal (as I assumed), or clawed back / adjusted immediately?

Ready to hand these off to `/tabletest` once you confirm the assumptions, or adjust any rows first if something's off.