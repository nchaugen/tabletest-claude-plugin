Here's a draft example table that makes the open questions concrete and visible for your team discussion. Cells marked `?` are the decisions you need to make together.

---

## Discount Calculation

| Scenario | Order Value | Loyalty Member | Bulk Discount (Policy) | Loyalty Discount (Policy) | Discount Cap (Policy) | Discount Applied? | Final Price? |
|---|---|---|---|---|---|---|---|
| No discount applies | £80 | no | 20% at £100+ | 15% | ? | 0% | £80.00 |
| Bulk only | £200 | no | 20% at £100+ | 15% | ? | 20% | £160.00 |
| Loyalty only | £80 | yes | 20% at £100+ | 15% | ? | 15% | £68.00 |
| Both — stacked (additive) | £200 | yes | 20% at £100+ | 15% | ? | 35% | £130.00 |
| Both — higher only | £200 | yes | 20% at £100+ | 15% | ? | 20% | £160.00 |
| Both — stacked, hits cap | £200 | yes | 25% at £100+ | 20% | 30% | ? | ? |
| Equal discounts, higher-only rule | £200 | yes | 15% at £100+ | 15% | ? | ? | ? |

---

**The rows that expose the core questions:**

- **Rows 4 vs 5** — the "both apply" rows are intentionally duplicated with different outcomes. The team picks one and deletes (or marks wrong) the other.
- **Row 6** — only matters if you choose stacking *and* add a cap. If you go with "higher only", this row disappears.
- **Row 7** — the tie-breaking edge case: if both discounts are equal and you use "higher only", does it matter which one is recorded as applied? (Affects audit trails, reporting.)

**Open questions to resolve:**

1. **Stacking rule**: additive (`bulk% + loyalty%`), multiplicative (`(1−bulk%) × (1−loyalty%)`), or higher-only (`max(bulk%, loyalty%)`)?
2. **Cap**: is there a maximum total discount? If so, what is it, and is it a business policy that could change per customer tier or product category?
3. **Tie-breaking** (row 7): relevant only if you go higher-only — does it matter which discount "wins"?

Once the team fills in the `?` cells in rows 4–7, implementation is unambiguous. Hand the agreed table to `/tabletest` to turn it into a `@TableTest`.