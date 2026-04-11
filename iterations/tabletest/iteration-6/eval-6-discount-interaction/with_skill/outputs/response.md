Good trigger for spec-by-example. Let me draft a table that makes the three candidate behaviours concrete and surfaces exactly what needs a team decision.

---

## Discount Calculation — Bulk + Loyalty Interaction

The core question is: given a customer who qualifies for both discounts, what do they actually get?

| Scenario | Order Value | Bulk Discount | Loyalty Discount | Discount Applied? | Notes / Open Question |
|---|---|---|---|---|---|
| No discounts qualify | £80 | none | none | 0% | |
| Bulk only | £500 | 10% | none | 10% | |
| Loyalty only | £80 | none | 15% | 15% | |
| Both qualify — stacked | £500 | 10% | 15% | **25%?** | Open: do they stack? |
| Both qualify — higher only | £500 | 10% | 15% | **15%?** | Open: or take the higher? |
| Both qualify — stacked but capped | £500 | 10% | 15% | **20%?** | Open: or stack with a cap? |
| Both qualify, loyalty wins clearly | £500 | 5% | 25% | **?** | Which rule wins here? |
| Both qualify, bulk wins clearly | £500 | 30% | 5% | **?** | Does outcome flip? |
| Large order, high loyalty | £5,000 | 20% | 25% | **?** | Cap matters most here |

---

**Open decisions this table forces:**

1. **Stacking rule** — three candidates:
   - Additive: `bulk% + loyalty%`
   - Higher only: `max(bulk%, loyalty%)`
   - Stacked with cap: `min(bulk% + loyalty%, cap%)`

2. **Cap** — if stacking, is there a maximum discount? The £5,000 high-loyalty row makes this concrete: without a cap, that customer gets 45% off.

3. **Order of application** — if stacking, does it matter which is applied first (it does when the base changes, e.g. loyalty on post-bulk price vs. pre-bulk price)?

---

**Suggested next step:** Take rows 4–9 to the team with just the inputs filled in, and ask them to fill in the `Discount Applied?` column. Where they disagree or hesitate is where the rule isn't settled. The rows where `bulk` and `loyalty` clearly differ in magnitude (rows 7–8) are especially useful — they reveal whether the logic is symmetric.

Once the team agrees on the outputs for those rows, you'll know which rule to implement, whether a cap is needed, and what its value should be.