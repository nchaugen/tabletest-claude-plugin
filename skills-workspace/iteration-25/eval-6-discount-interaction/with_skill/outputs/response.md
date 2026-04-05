Good timing to pause and nail this down. Let's build a concrete example table that surfaces exactly what the team needs to agree on.

**Discount Interaction** is the right name for this — it's the decision of how bulk and loyalty discounts combine.

Here's a starting table with the cases I can derive from what you've described, plus explicit open cells where the policy isn't agreed yet:

---

**Discount Interaction**

| Scenario | Order Value | Bulk Threshold | Bulk Discount (Policy) | Loyalty Member | Loyalty Discount (Policy) | Discount Cap (Policy) | Discount Applied? | Final Price? |
|---|---|---|---|---|---|---|---|---|
| No discounts apply | £80 | £100 | 10% | no | 5% | ? | 0% | £80 |
| Loyalty only | £80 | £100 | 10% | yes | 5% | ? | 5% | £76 |
| Bulk only | £150 | £100 | 10% | no | 5% | ? | 10% | £135 |
| Both apply — stacked | £150 | £100 | 10% | yes | 5% | ? | 15% | £127.50 |
| Both apply — higher only | £150 | £100 | 10% | yes | 5% | ? | 10% | £135 |
| Both apply — capped | £150 | £100 | 10% | yes | 5% | 12% | ? | ? |
| Large order, high loyalty | £500 | £100 | 10% | yes | 15% | ? | ? | ? |

---

The three open questions this makes explicit:

1. **Stacking vs. higher-only**: Rows 4 and 5 show the two options — which is it?
2. **Cap**: Does a cap exist? If so, what is it — and does it apply before or after stacking?
3. **Large order + high loyalty**: Row 7 is the stress test — with bigger discounts the stakes are higher. Fill this in once rows 4–6 are resolved.

**Suggested questions to put to the team:**

- "If a loyal customer places a bulk order, do they get 15% off or 10% off?"
- "Is there a maximum total discount we'd ever give, regardless of how many discount types apply?"
- "Does the cap apply to each discount individually, or to the combined total?"

Once the team fills in the `?` cells, this table is ready to hand to `/tabletest` to become executable code.