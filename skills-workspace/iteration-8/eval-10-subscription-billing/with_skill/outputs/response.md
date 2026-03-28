These rules split into three distinct concerns. I'll work through each one, surfacing the edge cases and open questions.

---

## Table 1: Trial Lifecycle

The "auto-convert to monthly" rule raises questions about what happens at the boundary and when cancellation during trial incurs a charge.

| Scenario | Days Since Sign-up | Trial Length (Policy) | Status After? | Charge? |
|---|---|---|---|---|
| Active mid-trial | 15 | 30 days | Trial | £0 |
| Last day of trial | 30 | 30 days | Trial | £0 |
| Trial just expired | 31 | 30 days | Monthly active | £9.99 |
| Cancel on day 5 of trial | 5 | 30 days | Cancelled | £0 |
| Cancel on last day of trial | 30 | 30 days | Cancelled | £0 |
| Annual subscriber during trial | 15 | 30 days | ? | ? |

**Open questions:**
- Does the trial apply to annual plan sign-ups, or only monthly? The rules say "auto-convert to monthly" — does an annual subscriber start on trial-then-monthly, then have to upgrade to annual separately?
- Is the charge triggered at the start of day 31, or at the exact moment 30 × 24h elapses from sign-up?

---

## Table 2: Cancellation Refund Eligibility

This is the 24-hour exception rule. The interesting boundary and the trial case are both worth pinning down explicitly.

| Scenario | Subscription Status | Hours Since Last Charge | Refund Eligible? | Reason? |
|---|---|---|---|---|
| Cancel during trial | Trial | N/A — nothing charged | no | Nothing charged yet |
| Cancel monthly, 15 days in | Monthly active | 360 | yes | |
| Cancel monthly, 12h after renewal | Monthly active | 12 | no | Within 24h window |
| Cancel annual, 30 days in | Annual active | 720 | yes | |
| Cancel annual, 6h after renewal | Annual active | 6 | no | Within 24h window |
| Cancel at exactly 24h after renewal | Monthly active | 24 | ? | Open: is boundary inclusive? |
| Cancel first renewal after trial converts | Monthly active | 6 | no | Within 24h window |

**Open questions:**
- Is "within 24 hours" strictly `< 24h`, or `≤ 24h`? (The 24h boundary row is where this matters.)
- Does the 24h no-refund window apply to the very first charge after trial converts, or only to subsequent renewals? (The last row assumes yes — confirm this.)

---

## Table 3: Prorated Refund Amount

Separate concern: once eligibility is confirmed, how much is refunded? This is arithmetic driven by days remaining.

| Scenario | Plan | Charge | Days in Cycle | Days Remaining | Refund Amount? |
|---|---|---|---|---|---|
| Monthly, 10 days left | Monthly | £9.99 | 30 | 10 | £3.33 |
| Monthly, exactly half-cycle left | Monthly | £9.99 | 30 | 15 | £4.99 or £5.00 — open |
| Monthly, 1 day left | Monthly | £9.99 | 30 | 1 | £0.33 |
| Annual, ~6 months left | Annual | £95.88 | 365 | 182 | £47.81 |
| Annual, 1 day left | Annual | £95.88 | 365 | 1 | £0.26 |
| Annual, 364 days left | Annual | £95.88 | 365 | 364 | £95.62 |

**Open questions:**
- **Rounding rule:** £9.99 × 15/30 = £4.995 — round half-up (→ £5.00) or truncate (→ £4.99)? This should be a named policy, not an implementation detail.
- **Monthly cycle length:** Fixed 30 days, or calendar-month days (28/29/31)? A February renewal and a January renewal would give different refund amounts for the same number of days remaining if calendar-based.
- **Minimum refund:** Is there a floor below which no refund is issued (e.g., < £0.50)?

---

## Summary of open questions to resolve

| # | Question | Why it matters |
|---|---|---|
| 1 | Does the trial apply to annual plan subscribers? | Determines whether an annual subscriber can ever be in Trial status |
| 2 | Is the 24h window `< 24h` or `≤ 24h`? | Boundary row behaviour |
| 3 | Does the 24h window apply to the first post-trial charge? | Prevents refund on immediate cancel after first billing |
| 4 | Rounding rule for proration | Half-penny amounts will occur regularly |
| 5 | Monthly cycle: 30 days fixed, or calendar days? | Affects refund amounts and predictability |
| 6 | Minimum refund threshold? | Operational cost of issuing tiny refunds |

The refund eligibility table (Table 2) is the most business-critical to get confirmed first — especially the 24h boundary and the post-trial first-charge question, as both are likely edge cases that will generate support tickets if they behave unexpectedly.