Looking at the feature, there are two separable concerns: the discount tier ladder for adults/seniors, and the discount scheme by passenger type. Let me write tables for each.

---

## Reis Discount Tier

| Scenario | Prior single tickets in last 30 days | Discount? |
|---|---|---|
| No travel history | 0 | 0% |
| Below first threshold | 3 | 0% |
| Just below first threshold | 4 | 0% | 
| At first discount threshold | 5 | 5% |  ← open question: is this prior=4 or prior=5? |
| Mid first tier | 7 | 5% |
| Top of first tier | 9 | 5% |
| At second tier | 10 | 10% |
| Mid ladder | 20 | 20% |
| At maximum tier | 40 | 40% |
| Above maximum | 45 | 40% |

> **Open question — threshold boundary**: Does "first discount applies to ticket number five" mean the discount is calculated from 4 prior tickets (the 5th purchase gets 5%) or 5 prior tickets? The table rows for "Just below first threshold" and "At first discount threshold" are placeholders — the exact cutoff needs confirming.

---

## Discount by Passenger Type

| Scenario | Passenger type | Prior single tickets in last 30 days | Discount? |
|---|---|---|---|
| Adult — no travel history yet | Adult | 0 | 0% |
| Adult — at first tier | Adult | 4 | 5% |
| Adult — at maximum | Adult | 40 | 40% |
| Senior — same ladder as adult | Senior | 4 | 5% |
| Child — flat discount, no travel history | Child | 0 | 20% |
| Child — flat discount regardless of travel | Child | {4, 20, 40} | 20% |

---

## Zones Do Not Affect Discount

The spec states the discount applies "regardless of the zones you travel in." This is worth pinning explicitly:

| Scenario | Passenger type | Zones | Prior tickets | Discount? |
|---|---|---|---|---|
| Adult, single zone | Adult | 1 zone | 4 | 5% |
| Adult, multiple zones | Adult | 3 zones | 4 | 5% |

---

**Key open questions to resolve before implementation:**

1. **Threshold boundary**: When buying ticket 5, the prior count is 4. Does `floor(4/5) * 5% = 0%` or is the rule `(prior + 1) divisible by 5 → increment tier`? This is the most important ambiguity.
2. **Ticket type scope**: Do transferred or refunded tickets count toward the 30-day window?
3. **Rolling window edge**: If exactly 30 days ago you bought 3 tickets, are they included or excluded?
4. **Passenger types**: Is "adult" explicitly defined by age (e.g., 18+)? What are the age bounds for "senior"?

Once the threshold boundary (question 1) is resolved, hand these tables to `/tabletest` to generate the `@TableTest` implementation.