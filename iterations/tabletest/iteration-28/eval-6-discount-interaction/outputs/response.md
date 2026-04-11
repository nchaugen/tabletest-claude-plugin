Good trigger for this skill. Let me work through the elicitation with you.

**Step 1 — Name the concern**

A clean name focuses the table. The decision here is: *given a customer's loyalty status and order quantity, what discount do they receive?*

Proposed name: **Discount Combination** (bulk + loyalty)

Does that capture it, or is there a more precise term your team uses?

---

**Step 2 — First example**

Before the interaction question, let's anchor the table with the simple cases. Fill in what you know:

| Scenario | Order Quantity | Loyalty Member | Bulk Discount | Loyalty Discount | Discount Applied? |
|---|---|---|---|---|---|
| No discounts apply | 5 | no | 0% | 0% | 0% |
| Bulk only | 50 | no | 10% | 0% | 10% |
| Loyalty only | 5 | yes | 0% | 5% | 5% |
| Both apply — **???** | 50 | yes | 10% | 5% | **?** |

The last row is where the team needs to decide. Three candidate rules:

| Rule | Both-apply result | Example (10% bulk, 5% loyalty) |
|---|---|---|
| **Stack** (additive) | bulk + loyalty | 15% |
| **Stack** (multiplicative) | `1 - (1-bulk)×(1-loyalty)` | 14.5% |
| **Higher wins** | max(bulk, loyalty) | 10% |

---

**Step 3 — Questions to resolve with the team**

1. **Which stacking rule (if any)?** Additive is simplest but can be gamed. Multiplicative is common in retail. Higher-wins avoids runaway discounts.

2. **Is there a cap?** If you stack, what's the ceiling — 20%? 30%? No cap at all? This needs its own column once decided:

   | Scenario | Order Qty | Loyalty Member | Bulk Discount | Loyalty Discount | Cap (Policy) | Discount Applied? |
   |---|---|---|---|---|---|---|
   | Both hit the cap | 50 | yes | 15% | 10% | 20% | 20% |
   | Both under cap | 50 | yes | 10% | 5% | 20% | 15% |

3. **Does loyalty discount scale, or is it flat?** If loyalty has tiers (1 year = 5%, 3 years = 10%), you may need a second table for loyalty tier lookup before tackling the combination table.

4. **Does the bulk threshold interact with loyalty?** E.g., do loyal customers get a lower bulk threshold? That would be a separate concern.

---

**Suggested next step**

Take the three-row candidate table to the team meeting — the "Both apply" row with `?` in the output is the conversation starter. Fill in the outcome they agree on, then add:
- A boundary row at exactly the cap (if there is one)
- A row just over the cap
- A row where loyalty is *higher* than bulk, to confirm higher-wins works symmetrically if that's the chosen rule

Once those rows are agreed, the table is ready to hand to `/tabletest`.