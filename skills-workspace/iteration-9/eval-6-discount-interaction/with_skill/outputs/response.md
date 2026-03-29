Here's a concrete example table to bring to your team. The rows where both discounts apply are left open — those are exactly the cells the team needs to fill in together.

---

## Discount Calculation

| Scenario | Order Amount | Bulk Discount | Loyalty Discount | Discount Applied? | Final Price? | Open Questions |
|---|---|---|---|---|---|---|
| No discounts | £80 | none | none | 0% | £80 | |
| Bulk only (small non-loyalty order) | £120 | 20% | none | 20% | £96 | |
| Loyalty only (small loyal order) | £40 | none | 10% | 10% | £36 | |
| Both apply, bulk is higher | £120 | 20% | 10% | ? | ? | Stack → £86.40 or £84; higher-only → £96; capped stack? |
| Both apply, loyalty is higher | £120 | 5% | 10% | ? | ? | Stack → £102.60 or £102; higher-only → £108 |
| Both apply, equal discounts | £100 | 15% | 15% | ? | ? | Stack → £72.25 or £70; higher-only → £85 |
| Both apply at high rates (cap concern) | £200 | 30% | 25% | ? | ? | Stack → £105 (47.5%); does a cap apply here? |

---

Three distinct behaviours to agree on:

1. **Stacked (additive)** — add both percentages: 20% + 10% = 30% off
2. **Stacked (compounded)** — apply sequentially: 20% off, then 10% off the remainder → £100 × 0.8 × 0.9 = £72
3. **Higher only** — take the larger discount, ignore the other

And if you go with stacking, a fourth question: **is there a cap** (e.g. max 35% discount total)?

The last row is designed to force the cap question — 30% + 25% stacked is 55% off, which may or may not be intentional.

Bring the table to the team and ask them to fill in the `?` cells. Disagreement on a single row will reveal the rule.