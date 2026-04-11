Two behaviours need separate tables here: the **discount tier** (how many trips maps to what percentage) and the **passenger type routing** (who gets which calculation).

---

## Table 1: Reis Discount Tier (Adult / Senior)

The discount is `floor(trips_in_last_30_days / 5) × 5%`, capped at 40%. "Trips in last 30 days" counts the ticket currently being purchased.

| Scenario | Trips in last 30 days (incl. this one) | Discount %? |
|---|---|---|
| First ever trip | 1 | 0% |
| Second trip | 2 | 0% |
| Third trip | 3 | 0% |
| Just below first discount threshold | 4 | 0% |
| First discount kicks in (ticket 5) | 5 | 5% |
| Mid-tier, still first band | 7 | 5% |
| Last trip in first band | 9 | 5% |
| Second band threshold | 10 | 10% |
| Third band threshold | 15 | 15% |
| Fourth band threshold | 20 | 20% |
| Fifth band threshold | 25 | 25% |
| Sixth band threshold | 30 | 30% |
| Seventh band threshold | 35 | 35% |
| At maximum discount | 40 | 40% |
| Above maximum — cap applies | 41 | 40% |
| Well above maximum — cap applies | 100 | 40% |

**Open question:** Does "trips in last 30 days" include the ticket currently being purchased, or only prior tickets? The spec says "the first discount applies to ticket number five" — the table above assumes **inclusive** (current trip counted), which makes ticket 5 → 5% work cleanly. If the count excludes the current trip, thresholds shift by one (4 prior purchases → 5% on the 5th purchase).

---

## Table 2: Discount by Passenger Type

| Scenario | Passenger type | Trips in last 30 days | Discount %? |
|---|---|---|---|
| Child — no travel history | Child | 0 | 20% |
| Child — regular traveller | Child | 10 | 20% |
| Child — heavy traveller, cap does not apply | Child | 50 | 20% |
| Adult — no travel history | Adult | 1 | 0% |
| Adult — first discount threshold | Adult | 5 | 5% |
| Adult — mid ladder | Adult | 20 | 20% |
| Adult — at maximum | Adult | 40 | 40% |
| Senior — no travel history | Senior | 1 | 0% |
| Senior — first discount threshold | Senior | 5 | 5% |
| Senior — at maximum | Senior | 40 | 40% |

**Notes:**
- Children always receive exactly **20%**, regardless of trip count or zones — the adult/senior ladder does not apply.
- Adults and seniors use the same ladder — they are treated identically for discount purposes.
- Zones are irrelevant to the discount calculation for all passenger types; the column is deliberately absent.

**Open question:** Are there other passenger types (e.g. youth, student)? The spec only names adult, senior, and child. If additional types exist, they need their own rows here.

---

Ready to move to `@TableTest` code whenever you are — shall I proceed?