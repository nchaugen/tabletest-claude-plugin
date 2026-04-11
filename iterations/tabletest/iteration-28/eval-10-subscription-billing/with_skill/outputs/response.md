Let me work through this systematically. I see two distinct concerns: the **refund calculation on cancellation** (the complex one with the 24h exception), and the **trial lifecycle and auto-conversion**. Let me draft both.

---

## Refund on Cancellation

> Daily rate: Monthly = £9.99 ÷ 30 = £0.333/day · Annual = £95.88 ÷ 365 = £0.263/day  
> Refund = daily rate × days remaining in cycle (see open questions on rounding)

| Scenario | Plan | Days remaining in cycle | Within 24h of renewal charge | Refund? |
|---|---|---|---|---|
| Monthly, cancel at midpoint | Monthly | 15 | no | £5.00 |
| Monthly, cancel near end of cycle | Monthly | 2 | no | £0.67 |
| Monthly, cancel within 24h of renewal | Monthly | 29 | yes | £0.00 |
| Annual, cancel mid-year | Annual | 265 | no | £69.61 |
| Annual, cancel near start (after grace) | Annual | 363 | no | £95.49 |
| Annual, cancel within 24h of renewal | Annual | 364 | yes | £0.00 |

The annual + within-24h row is worth calling out explicitly: a subscriber who cancels the day after renewal gets nothing back, even though nearly the full year's value (£95.62) is unused. That's a significant UX consequence — worth confirming the rule is intentional.

---

## Trial Lifecycle and Auto-Conversion

| Scenario | Subscription state | Days into trial | Event | State after? | Charge? |
|---|---|---|---|---|---|
| New subscriber activates | New | — | Sign up | Trial | £0.00 |
| Mid-trial, no action taken | Trial | 15 | — | Trial | £0.00 |
| Trial expires, no cancellation | Trial | 30 | Auto-expiry | Monthly | £9.99 |
| Cancel on day 1 of trial | Trial | 1 | Cancel | Cancelled | £0.00 |
| Cancel mid-trial | Trial | 15 | Cancel | Cancelled | £0.00 |
| Cancel on final day of trial | Trial | 30 | Cancel | Cancelled | £0.00 |

---

## Open Questions

These need answers before implementation can proceed confidently:

1. **Rounding rule for refund amounts** — £9.99 ÷ 30 × 15 = £4.995. Round half-up (→ £5.00)? Banker's rounding? Always round down in favour of the company? This changes real amounts on most cancellations.

2. **Does the first charge after trial count as a "renewal charge" for the 24h exception?** — If trial auto-converts and the subscriber is charged £9.99 on day 31, and they cancel within 24h, does the no-refund exception apply? The rule says "renewal" which could exclude a first-time charge.

3. **What is "days remaining" based on?** — For monthly plans, calendar months vary (28–31 days). Is the cycle always fixed at 30 days as implied by the pricing, or does it follow the actual calendar month? This affects the daily rate and the refund calculation.

4. **Does cancelling during trial end access immediately or at the end of the 30-day period?** — Common industry practice is to keep access until the trial expires, but the rules don't specify. This also affects whether "cancel on final day of trial" is meaningful.

5. **Can a previously-cancelled trial subscriber get another trial on re-subscription?** — The rule says "new subscribers" get a trial. Is that one-per-email, or one-per-subscription?

---

Any of these rows wrong, or cases missing? Once the open questions are resolved these tables map directly to a `@TableTest` via `/tabletest`.