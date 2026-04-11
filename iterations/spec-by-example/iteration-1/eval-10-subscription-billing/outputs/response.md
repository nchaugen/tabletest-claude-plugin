Three distinct concerns emerge here. I'll draft a table for each, then flag the open questions that need answers before implementation.

---

## Table 1: Cancellation Refund Eligibility

The 24h grace window and trial status are the interesting decisions — this table pins those down before touching arithmetic.

| Scenario | Plan | Time Since Last Charge | Refund Eligible? | No-Refund Reason? |
|---|---|---|---|---|
| Cancel during free trial | Trial | n/a | no | No charge taken |
| Monthly — cancel 6h after renewal | Monthly | 6 hours | no | Within 24h grace window |
| Annual — cancel 20h after renewal | Annual | 20 hours | no | Within 24h grace window |
| Monthly — cancel 25h after renewal | Monthly | 25 hours | yes | |
| Monthly — cancel 15 days in | Monthly | 15 days | yes | |
| Annual — cancel 6 months in | Annual | 6 months | yes | |
| Cancel exactly at 24h mark | {Monthly, Annual} | 24 hours | ? | **Open: is the boundary < 24h or ≤ 24h?** |

---

## Table 2: Prorated Refund Amount

Only applies when Table 1 says "yes". Arithmetic here — 2–3 rows per plan to verify the formula `(cycle_length − days_used) / cycle_length × plan_price`.

| Scenario | Plan | Plan Price | Days Used | Billing Cycle | Refund Amount? |
|---|---|---|---|---|---|
| Monthly — 1 day used | Monthly | £9.99 | 1 | 30 days | £9.66 |
| Monthly — 10 days used | Monthly | £9.99 | 10 | 30 days | £6.66 |
| Monthly — 29 days used | Monthly | £9.99 | 29 | 30 days | £0.33 |
| Annual — 1 day used | Annual | £95.88 | 1 | 365 days | £95.62 |
| Annual — 100 days used | Annual | £95.88 | 100 | 365 days | £69.61 |

---

## Table 3: Trial Lifecycle

| Scenario | Days Since Sign-up | Status? | Next Charge? |
|---|---|---|---|
| Active mid-trial | 15 | Trial | — |
| Trial just elapsed | 31 | Monthly | £9.99 today |
| Cancel on day 10 of trial | 10 | Cancelled | none |
| Cancel on day 30 (final trial day) | 30 | Cancelled | none |

---

## Open Questions

Before implementation, these need answers:

| # | Question | Why it matters |
|---|---|---|
| 1 | Is the 24h window `< 24h` or `≤ 24h`? | Determines refund for exact-boundary cancellations |
| 2 | Is monthly billing 30 fixed days, or a calendar month? | February billing cycles are 28/29 days; per-day rate varies |
| 3 | Is a year always 365 days, or does it track leap years? | Affects per-day rate and refund amounts |
| 4 | What rounding rule applies to refund amounts? | `15/30 × £9.99 = £4.995` — rounds to £4.99 or £5.00? |
| 5 | Does the first charge after trial conversion count as a "renewal charge" for the 24h rule? | A subscriber could convert then immediately cancel — is that covered? |
| 6 | Do both monthly and annual plans get the 30-day trial? | Spec says "new subscribers" without qualifying by plan |
| 7 | What happens if the trial-conversion payment fails? | Status should be defined (retry? grace period? revert to trial?) |

The 24h boundary (Q1) and rounding rule (Q4) are the most likely to cause disputes — worth resolving explicitly before writing any calculation code.