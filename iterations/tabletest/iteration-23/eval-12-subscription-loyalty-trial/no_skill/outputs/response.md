Here's a structured spec with the edge cases reasoned out.

---

## Subscription Billing Spec

### Plans

| Plan | Price | Trial |
|---|---|---|
| Monthly | £9.99/month | 30 days free, then auto-converts |
| Annual | £95.88/year | Not available |
| Annual + Loyalty | £86.29/year (10% off) | Not available |

---

### Free Trial

- Available on monthly plan only; annual plan purchase immediately charges the full amount.
- After 30 days, auto-converts to paid monthly at £9.99/month.
- If a user on a free trial upgrades to annual mid-trial: the trial ends immediately and the annual charge is applied on the day of upgrade.
- Loyalty discount does not apply to monthly plans, so a loyalty member who auto-converts from trial pays £9.99/month.

---

### Loyalty Discount

- Applies to annual plan only (10% off → £86.29/year).
- Discount is verified and applied at the point of charge (sign-up or renewal).
- **Loyalty membership acquired mid-annual-period:** discount applies at next renewal, not retroactively.
- **Loyalty membership lapses mid-annual-period:** subscriber keeps the discounted rate for the current paid period; full rate applies at next renewal.

---

### Cancellation & Refunds

- Mid-cycle cancellation → prorated refund for unused days, calculated on the amount actually charged (i.e. discounted rate for loyalty annual subscribers).
- **No refund** if cancelled within 24 hours of a renewal charge. This applies to both monthly and annual plans regardless of loyalty status.
- During free trial: cancellation is free with no charge; no refund concept applies since no payment was taken.

---

### Edge Cases Resolved

| Scenario | Resolution |
|---|---|
| User is a loyalty member but on monthly plan | No discount; loyalty discount is annual-only |
| User on free trial wants to switch to annual | Trial ends immediately; annual charge (with loyalty discount if applicable) applies on upgrade date |
| Loyalty member cancels annual plan mid-year | Prorated refund based on £86.29 paid, not £95.88 |
| Loyalty membership expires 6 months into annual plan | No mid-period adjustment; full £95.88 charged at next renewal |
| Loyalty membership gained on day of annual renewal | Discount applies to that renewal charge |
| User cancels annual plan within 24h of renewal | No refund, regardless of loyalty status |

---

### Open Questions to Confirm

1. **Rounding:** £95.88 × 90% = £86.292 — should this be rounded to £86.29 or a different figure?
2. **Loyalty verification:** Is membership checked at charge time via an API call, or is it a flag set on the user record that staff/integrations update?
3. **Trial → Annual upgrade:** Should the remaining trial days be forfeited entirely, or is any credit applied? (Spec above assumes forfeited for simplicity.)
4. **Annual plan downgrade to monthly:** If a subscriber switches from annual to monthly mid-period, does the prorated refund rule apply, and does it use the discounted or full annual rate as the basis?