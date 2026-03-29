Here's a spec table that makes the disagreement concrete, so your team can answer by pointing at rows rather than talking in the abstract.

---

## Discount Combination

| Scenario | Order Value | Bulk Discount | Loyalty Discount | Discount Applied? | Final Price? |
|---|---|---|---|---|---|
| Bulk discount only | £200 | 15% | — | 15% | £170 |
| Loyalty discount only | £200 | — | 10% | 10% | £180 |
| Neither discount | £200 | — | — | 0% | £200 |
| Both — bulk is higher | £200 | 15% | 10% | **?** | **?** |
| Both — loyalty is higher | £200 | 10% | 20% | **?** | **?** |
| Both — equal rates | £200 | 15% | 15% | **?** | **?** |
| Both — stacking would exceed cap | £200 | 20% | 15% | **?** | **?** |

---

**The two open questions, with the concrete stakes:**

**Q1 — Stack or higher-only?**

For "Both — bulk is higher" (row 4):
- If stack → 25% applied → £150
- If higher-only → 15% applied → £170

For "Both — loyalty is higher" (row 5):
- If stack → 30% applied → £140
- If higher-only → 20% applied → £160

For "Both — equal rates" (row 6):
- If stack → 30% applied → £140
- If higher-only → 15% applied → £170

**Q2 — Is there a cap?** (Only relevant if you go with stacking)

For "Both — stacking would exceed cap" (row 7, 20% + 15% = 35%):
- If no cap → 35% applied → £130
- If cap at 25% → 25% applied → £150
- If cap at 30% → 30% applied → £140

---

**Suggested order to decide:** Resolve Q1 first (stack vs higher-only), then fill in the `?` cells in rows 4–6. If you choose stacking, resolve Q2 and fill in row 7. If you choose higher-only, row 7 is moot (the cap can never be exceeded by definition).

Once the team agrees on the filled values, this table hands directly to `/tabletest` for implementation.