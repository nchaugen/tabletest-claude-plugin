Let me work through the full rule set and surface the edge cases, particularly around the loyalty + trial interaction.

---

## Subscription Billing

### Table 1: Plan Setup & Pricing

| Scenario | Subscriber Type | Plan | Loyalty Member? | Trial? | Trial Duration? | Price? |
|---|---|---|---|---|---|---|
| Standard new monthly subscriber | New | Monthly | no | yes | 30 days | £9.99/month after trial |
| New monthly subscriber, loyalty member | New | Monthly | yes | yes | 30 days | £9.99/month after trial |
| Standard new annual subscriber | New | Annual | no | no | | £95.88/year |
| New annual subscriber, loyalty member | New | Annual | yes | no | | £86.29/year |

**Note:** Loyalty discount applies to annual plan only — a loyalty member on monthly pays the same £9.99 as anyone else.

**Open question — loyalty + trial interaction:**

> A loyalty member signing up for monthly gets a trial, then auto-converts to monthly at £9.99. If they later switch to annual, do they get the loyalty discount from that point? Is a mid-subscription plan switch even a supported flow?

---

### Table 2: Cancellation Refund

| Scenario | Plan | Hours since last renewal | Remaining days in cycle | Loyalty Member? | Refund? |
|---|---|---|---|---|---|
| Monthly, mid-cycle cancellation | Monthly | > 24 | 15 | {yes, no} | £4.99 (15/30 × £9.99) |
| Monthly, cancel within 24h of renewal | Monthly | ≤ 24 | 29 | {yes, no} | none |
| Annual, mid-cycle cancellation (no loyalty) | Annual | > 24 | 180 | no | £47.27 (180/365 × £95.88) |
| Annual, mid-cycle cancellation (loyalty) | Annual | > 24 | 180 | yes | ? |
| Annual, cancel within 24h of renewal | Annual | ≤ 24 | 364 | {yes, no} | none |
| Cancel during free trial | Monthly (trial) | n/a — no charge yet | n/a | {yes, no} | none |

**Open questions — loyalty + cancellation:**

> For the loyalty annual cancellation row (marked `?`): is the prorated refund calculated from what the subscriber **actually paid** (£86.29) or from the **list price** (£95.88)? This is the most likely source of a bug — the two differ by £9.59, and the answer may not be obvious to whoever implements it.

---

### Other Open Questions to Resolve

| # | Question | Why It Matters |
|---|---|---|
| 1 | Loyalty annual refund: actual-paid or list price? | See Table 2 row 4 — directly affects refund amount |
| 2 | Can a subscriber switch from monthly trial to annual? | If yes: does trial end immediately? Is loyalty discount applied? |
| 3 | Does loyalty discount apply at next renewal or immediately when loyalty membership is confirmed? | A subscriber might join a loyalty programme mid-cycle |
| 4 | What if loyalty membership lapses while on annual? Does next renewal revert to £95.88? | Discount could disappear mid-subscription |
| 5 | Do returning subscribers (cancelled and rejoining) qualify for a new free trial? | "New subscribers" is ambiguous |

---

The loyalty + trial interaction is largely non-overlapping by design (trial → monthly only; loyalty discount → annual only), but question 2 is where they directly collide. Worth getting an answer on that before implementation, as it affects both the trial logic and the discount application path.