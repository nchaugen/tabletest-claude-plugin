Here are example tables that pin down the behaviour across the key concerns. I've split into two tables because the trial lifecycle and the refund calculation are distinct decisions.

---

## Table 1: Trial Lifecycle

| Scenario | Days since signup | Plan chosen | Event | Status after? | Charge? |
|---|---|---|---|---|---|
| Active trial, no action | 15 | Monthly | — | Trial | £0 |
| Trial expires, auto-converts | 31 | Monthly | Auto-renewal | Monthly | £9.99 |
| Cancel early in trial | 10 | Monthly | Cancellation | Cancelled | £0 |
| Cancel on last day of trial | 30 | Monthly | Cancellation | Cancelled | £0 |
| Cancel on trial-expiry day (race) | 31 | Monthly | Cancellation | ? | ? |

**Open question:** If the subscriber cancels on exactly day 31 — the same day auto-conversion fires — which wins? Is the £9.99 charge reversed, or does the 24h no-refund rule apply to this first charge?

---

## Table 2: Cancellation Refund

| Scenario | Plan | Days elapsed since last charge | Within 24h of charge? | Refund? |
|---|---|---|---|---|
| Cancel during trial | Trial | n/a | n/a | £0 |
| Cancel immediately after monthly renewal | Monthly | 0 | yes | £0 |
| Cancel just past 24h window, monthly | Monthly | 2 | no | £9.33 (28 unused days × £0.333) |
| Cancel halfway through monthly cycle | Monthly | 15 | no | £5.00 (15 unused days × £0.333) |
| Cancel one day before monthly renewal | Monthly | 29 | no | £0.33 (1 unused day × £0.333) |
| Cancel immediately after annual renewal | Annual | 0 | yes | £0 |
| Cancel 100 days into annual | Annual | 100 | no | £69.61 (265 unused days × £0.263) |
| Cancel 364 days into annual | Annual | 364 | no | £0.26 (1 unused day × £0.263) |

Daily rates used: Monthly = £9.99 ÷ 30 = £0.333/day; Annual = £95.88 ÷ 365 = £0.263/day.

---

## Open Questions

| # | Question | Why it matters |
|---|---|---|
| 1 | Does "renewal charge" include the first charge after trial, or only subsequent renewals? | Determines whether the 24h no-refund rule applies to the trial-to-monthly conversion |
| 2 | Are monthly cycles fixed at 30 days, or do they follow calendar months (28–31 days)? | Affects the daily rate and the refund amount — February vs July differ by 3 days |
| 3 | How is "within 24 hours" measured — from the moment the payment processes, or from midnight on the renewal date? | Edge cases near the boundary |
| 4 | What happens if a subscriber on annual wants to switch to monthly mid-cycle, or vice versa? | Not covered by current rules |
| 5 | Are refunds issued back to the original payment method, or as account credit? | Affects the implementation path |

---

The most important thing to nail down before implementing is **open question #1** — the trial-expiry-day race condition in Table 1 row 5 and whether the 24h rule applies to the very first charge. Everything else is arithmetic once you settle the daily-rate convention (question #2).