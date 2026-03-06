# Subscription Billing — Spec by Example

These rules cover two distinct concerns: plan pricing, and cancellation/refund behaviour. They belong in separate tables — the inputs and outputs are different, and they change for different reasons.

---

## Table 1: Plan Pricing

| Scenario                        | Plan     | Billing Cycle | Price?    |
|---------------------------------|----------|---------------|-----------|
| Monthly plan standard rate      | Monthly  | Monthly       | £9.99     |
| Annual plan standard rate       | Annual   | Annual        | £95.88    |
| New subscriber free trial       | Monthly  | Trial (30d)   | £0.00     |
| Trial auto-converts to monthly  | Monthly  | Post-trial    | £9.99     |

---

## Table 2: Cancellation and Refund

| Scenario                                    | Plan     | Days Into Cycle | Renewal Just Charged? | Days in Cycle | Refund?                  |
|---------------------------------------------|----------|-----------------|-----------------------|---------------|--------------------------|
| Cancel mid-cycle, monthly                   | Monthly  | 10              | no                    | 30            | £6.66 (20 unused days)   |
| Cancel mid-cycle, annual                    | Annual   | 90              | no                    | 365           | £71.77 (275 unused days) |
| Cancel within 24h of renewal — no refund    | Monthly  | 0               | yes                   | 30            | £0.00                    |
| Cancel on last day of cycle                 | Monthly  | 29              | no                    | 30            | £0.33 (1 unused day)     |
| Cancel first day after trial converts       | Monthly  | 0               | no                    | 30            | £9.66 (29 unused days)   |

---

## Notes on open questions

- "Renewal Just Charged?" captures the 24-hour exception. If the renewal was charged within the last 24 hours, no refund is given regardless of days remaining.
- The prorated refund formula assumed: `(unused days / days in cycle) × plan price`. The exact rounding behaviour (floor, round, truncate) is unspecified — this should be confirmed.
- Does the free trial period qualify for a refund if cancelled early, or is it simply ended? The table assumes £0.00 is charged during trial so there is nothing to refund, but this should be confirmed.
- Annual plan: is the refund based on calendar days or a fixed 365-day year? The table assumes 365 days.
