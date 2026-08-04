No existing discount calculator code in this directory — this looks like a fresh ask, so I'll deliver the example table directly here for you to bring to the team.

## Combining Bulk and Loyalty Discounts

**Assumptions I'm making (since I can't ask):**
- The bulk-rate and loyalty-rate are each *already resolved* percentages (e.g., quantity → bulk %, tier → loyalty %) — this table is only about how two known percentages combine, not how each is derived.
- Discounts are computed as percentages off the *original* price, not applied sequentially to an already-discounted price. This matters: additive stacking (5% + 5% = 10% off) and compounding stacking (5% off, then 5% off the remainder = 9.75% off) give different numbers, and the team hasn't said which is intended. I've assumed **additive** below and flagged the alternative as an open question.
- There's no confirmed cap value yet, so I used **25%** purely as an illustrative placeholder — not a recommendation.

Because the actual combination rule is exactly what's undecided, I've laid out three candidate policies side by side on the same inputs, so the team can compare outcomes and pick one. Once a policy is chosen, this collapses into a single output column ready to hand to `/tabletest`.

| Scenario | Bulk Discount | Loyalty Discount | Assumed Cap | Stacked (Additive Sum)? | Higher-Only (Max)? | Capped Additive Stack? |
|---|---|---|---|---|---|---|
| Neither discount qualifies | 0% | 0% | 25% | 0% | 0% | 0% |
| Only the bulk discount qualifies | 10% | 0% | 25% | 10% | 10% | 10% |
| Only the loyalty discount qualifies | 0% | 15% | 25% | 15% | 15% | 15% |
| Both qualify, combined well under the cap | 5% | 5% | 25% | 10% | 5% | 10% |
| Both qualify, combined exactly at the cap | 10% | 15% | 25% | 25% | 15% | 25% |
| Both qualify, combined just over the cap | 15% | 11% | 25% | 26% | 15% | 25% |
| Both qualify, combined well over the cap | 15% | 20% | 25% | 35% | 20% | 25% |
| Loyalty discount alone already at the cap | 0% | 25% | 25% | 25% | 25% | 25% |
| Loyalty discount alone already over the cap | 0% | 30% | 25% | 30% | 30% | **?** |

The last row is the sharpest open question in the whole table: if a *single* discount (say, your top loyalty tier) already exceeds the assumed cap on its own, does the cap clip it too, or does the cap only ever apply to the *combination*? "Capped Additive Stack" gives different real-world guarantees depending on the answer — I've left it as `?` rather than guessing.

**Open questions for the team (in priority order):**
1. **Stack, max, or capped-stack?** — the core question. The table above shows all three side by side.
2. **If capped-stack: does the cap apply only to the sum, or also to any single discount?** — see the last row.
3. **What is the actual cap value, if any?** — 25% here is a placeholder, not a proposal.
4. **Additive or compounding stack?** — e.g. row "well under the cap" (5% + 5%): additive gives 10% off, compounding gives 9.75% off. Only matters if stacking is chosen at all.
5. **Is the bulk/loyalty rate itself independent of the other** (i.e., does reaching a bulk threshold ever change which loyalty tier applies, or vice versa)? I've assumed no — they're computed independently and only combined at the end.

**My recommendation, for discussion, not as a decision:** capped additive stack with the cap applied to the final combined rate (not to either discount individually), since it's the simplest mental model for both customers and support staff, and avoids the "loyal bulk buyer gets less than a bulk-only buyer" trap that pure max-only can produce. But this is the team's call — the table above is built so any of the three can be picked with the boundary rows already worked out.