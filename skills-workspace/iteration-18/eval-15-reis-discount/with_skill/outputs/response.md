Looking at this feature, I can identify a few distinct concerns:

1. **Reis discount tier** — how trip count maps to discount percentage (adult/senior)
2. **Discount by passenger type** — adults/seniors get dynamic Reis discount, children get flat 20%
3. **Final ticket price** — arithmetic check (minimal)

---

## Reis Discount Tier

The discount an adult or senior earns based on single tickets purchased in the last 30 days, measured at time of purchase.

| Scenario | Trips in last 30 days | Discount? |
|---|---|---|
| No purchase history | 0 | 0% |
| Occasional traveller, below first threshold | 3 | 0% |
| Just below first threshold | 4 | 0% |
| At first threshold | 5 | 5% |
| Between first and second threshold | 7 | 5% |
| Just below second threshold | 9 | 5% |
| At second threshold | 10 | 10% |
| Mid-ladder | 20 | 20% |
| At second-to-last rung | 35 | 35% |
| At maximum threshold | 40 | 40% |
| Beyond maximum (capped) | 45 | 40% |

**Open questions:**
- Does the ticket being purchased now count toward the 30-day total, or only prior tickets?
- Is the threshold boundary inclusive (≥ 5) or exclusive (> 5)?

---

## Discount by Passenger Type

Which discount mechanism applies, and whether travel history matters.

| Scenario | Passenger type | Trips in last 30 days | Discount? |
|---|---|---|---|
| Adult/Senior, no history | {Adult, Senior} | 0 | 0% |
| Adult/Senior, first tier reached | {Adult, Senior} | 5 | 5% |
| Adult/Senior, maximum discount | {Adult, Senior} | 40 | 40% |
| Child, regardless of travel history | Child | {0, 5, 40} | 20% |

The value set `{Adult, Senior}` on the first three rows expresses that both types follow the same Reis discount tier. The value set on the child row expresses that trip count is irrelevant for children.

---

## Ticket Price After Discount

Arithmetic check only — the interesting rules are in the tables above.

| Scenario | Standard single ticket price | Discount | Price paid? |
|---|---|---|---|
| No discount yet | 40.00 | 0% | 40.00 |
| Adult at first tier | 40.00 | 5% | 38.00 |
| Child flat discount | 40.00 | 20% | 32.00 |
| Adult at maximum | 40.00 | 40% | 24.00 |

**Open question:** Is rounding applied at the ticket level, and if so, is it round-half-up or truncation? (Matters when the discount produces a fractional øre/cent.)

---

When you're ready to implement, hand these to `/tabletest` — the column structure carries over directly. The open questions above should be resolved before coding the boundary conditions.