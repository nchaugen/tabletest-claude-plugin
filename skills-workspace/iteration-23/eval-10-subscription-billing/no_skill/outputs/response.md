## Subscription Billing Spec

### Plans

| Plan | Price | Billing cycle |
|------|-------|---------------|
| Monthly | £9.99/month | Every 30 days |
| Annual | £95.88/year (£7.99/month equiv.) | Every 365 days |

---

### Trial

- All new subscribers start on a **30-day free trial** (no charge at signup).
- At trial end, auto-convert to **monthly plan** and charge £9.99.
- Trial counts as a billing cycle: the first renewal date is 30 days after signup.
- Cancellation during trial: no charge, access ends at trial expiry.

---

### Renewal

- Charge is attempted on the renewal date.
- On charge failure: define a retry policy (e.g. retry after 3 days, then cancel) — **needs decision**.
- Successful charge starts a new billing cycle from that date.

---

### Cancellation & Refunds

**Standard case — cancel mid-cycle:**
```
refund = (price / cycle_days) × days_remaining
```
- `cycle_days`: 30 for monthly, 365 for annual.
- `days_remaining`: ceiling or floor? — **needs decision** (recommend `ceil` to favour subscriber).
- Access continues until cycle end, or terminates immediately? — **needs decision**.

**Exception — cancel within 24 hours of a renewal charge:**
- No refund issued.
- The 24-hour window starts at the moment the charge succeeds (not the renewal date).

**Cancellation during trial:**
- No refund (no charge was made).

---

### Edge Cases to Resolve

1. **Plan switch (monthly → annual or vice versa):** prorate the remaining days on the current plan and credit toward the new plan, or start fresh? Define whether switching resets the renewal date.
2. **Retry on failed renewal:** how many attempts, over how many days, before the account is suspended vs. cancelled?
3. **Refund access policy:** after a mid-cycle refund, does the subscriber lose access immediately or at the original cycle end?
4. **Timezone for the 24-hour renewal window:** use UTC or the subscriber's local timezone?
5. **Currency handling:** amounts always in GBP, or do you need multi-currency support?
6. **Tax:** is £9.99 inclusive or exclusive of VAT?

---

### Key Data to Track per Subscription

```
subscription_id
user_id
plan               -- "trial" | "monthly" | "annual"
status             -- "active" | "cancelled" | "past_due"
trial_start_at
trial_end_at
current_cycle_start_at
current_cycle_end_at
last_charge_at       -- for the 24-hour renewal window check
last_charge_amount
cancelled_at
```

---

### Refund Calculation Example

Subscriber on annual (£95.88), cancels with 200 days remaining, **not** within 24h of renewal:
```
daily_rate  = 95.88 / 365 = £0.2627
refund      = ceil(200) × 0.2627 = £52.54
```

Subscriber cancels 10 hours after a renewal charge → **no refund** (within 24h window).

---

The four open questions I flagged are the main things to nail down before implementation. Want me to draft the API contract or state machine for the subscription lifecycle next?