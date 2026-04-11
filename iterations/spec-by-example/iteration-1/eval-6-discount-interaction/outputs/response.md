Here is an example table for **Discount Combination** that makes the open questions concrete. Rows where the team must agree on an answer are marked `?`.

---

## Discount Combination

| Scenario | Order Value | Bulk Discount (Policy) | Loyalty Member | Loyalty Discount (Policy) | Discount Cap (Policy) | Final Discount %? | Final Price? |
|---|---|---|---|---|---|---|---|
| No discounts apply | £200 | 15% | no | 10% | ? | 0% | £200.00 |
| Bulk only | £600 | 15% | no | 10% | ? | 15% | £510.00 |
| Loyalty only | £200 | 15% | yes | 10% | ? | 10% | £180.00 |
| Both apply — stacked additively | £600 | 15% | yes | 10% | ? | 25% | £450.00 |
| Both apply — higher only | £600 | 15% | yes | 10% | ? | 15% | £510.00 |
| Both apply — compounded | £600 | 15% | yes | 10% | ? | 23.5% | £459.00 |
| Both apply — capped | £600 | 15% | yes | 10% | 20% | 20% | £480.00 |
| At bulk threshold exactly | £500 | 15% | no | 10% | ? | ? | ? |
| Just below bulk threshold | £499 | 15% | no | 10% | ? | 0% | £499.00 |

---

**Rows 4–7 are the same input — they show the four candidate rules. The team picks one, and the other rows are deleted.**

**Open questions to resolve:**

1. **Combination rule** — rows 4–7 above show the four interpretations. Which one is it?
   - Additive stack (`15% + 10% = 25%`)
   - Higher-only (`max(15%, 10%) = 15%`)
   - Compounded (`1 − (1−0.15)×(1−0.10) = 23.5%`)
   - Capped (apply both, then cap at X%)

2. **Cap** — does one exist? If so, at what value? Is it applied before or after combination?

3. **Bulk threshold** — what is the exact qualifying order value, and is it inclusive (≥ £500) or exclusive (> £500)? The boundary rows at £499/£500 are placeholder until confirmed.

---

Once the team agrees, fill in the `?` cells and delete the candidate rows that do not apply. That table is then ready to hand to `/tabletest` for implementation.