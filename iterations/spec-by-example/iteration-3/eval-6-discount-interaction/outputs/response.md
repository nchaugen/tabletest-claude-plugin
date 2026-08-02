Since the interaction rule genuinely isn't agreed yet, I built this as a decision-support table: concrete example rows showing what each candidate combination policy would actually produce, so the team can compare real numbers rather than argue in the abstract. Here's the complete artifact.

## Assumptions (flag/correct before the meeting)

Since the actual tier structures weren't specified, I assumed plausible values to make the table concrete — swap these for your real numbers if they differ; the interaction question is unaffected by what they are:

- **Bulk tiers**: <10 units → 0%, 10–49 → 5%, 50–99 → 10%, 100+ → 15%
- **Loyalty tiers**: none → 0%, Silver → 5%, Gold → 10%, Platinum → 20%

The combination table below takes discount % directly as input (not order quantity or tenure) — the bulk-tier and loyalty-tier lookups are separate, already-agreed rules; only how their *outputs* combine is in question.

## Combining Bulk and Loyalty Discounts

| Scenario | Bulk Discount % | Loyalty Discount % | Additive Stack | Multiplicative Stack | Higher-Only | Agreed Combined Discount? |
|---|---|---|---|---|---|---|
| Neither discount applies | 0% | 0% | 0% | 0% | 0% | |
| Bulk discount only, no loyalty tier | 15% | 0% | 15% | 15% | 15% | |
| Loyalty discount only, no bulk tier | 0% | 20% | 20% | 20% | 20% | |
| Both at lowest qualifying tier | 5% | 5% | 10% | 9.75% | 5% | |
| Bulk tier exceeds loyalty tier | 15% | 5% | 20% | 19.25% | 15% | |
| Loyalty tier exceeds bulk tier | 5% | 20% | 25% | 24% | 20% | |
| Bulk and loyalty tiers equal | 10% | 10% | 20% | 19% | 10% | |
| Both at maximum tier | 15% | 20% | 35% | 32% | 20% | |

Note the first three rows are uncontroversial — all three formulas agree whenever one discount is 0%. Rows 4–8 are where the disagreement lives, and the last row is the extreme case: additive stacking gives away over a third off, which is exactly the scenario a cap exists to prevent.

## If stacking (additive) is chosen — illustrating a cap

Assuming a candidate cap of 25% (pick your own number — this just shows the mechanism):

| Scenario | Bulk % | Loyalty % | Uncapped Additive | Cap (Policy) | Combined Discount |
|---|---|---|---|---|---|
| Well under the cap | 5% | 5% | 10% | 25% | 10% |
| Exactly at the cap | 10% | 15% | 25% | 25% | 25% |
| Just over the cap | 15% | 15% | 30% | 25% | 25% |
| Far over the cap | 15% | 20% | 35% | 25% | 25% |

## My recommendation (to confirm, not a decision)

**Additive stacking with a cap** (commonly 20–25% in retail) — it rewards both behaviors you're trying to incentivize (bulk purchasing and retention) rather than only the stronger one, while the cap keeps margin risk bounded on the "both at max tier" case. Higher-only is simpler to reason about but effectively makes loyalty worthless for any customer who also qualifies for a large bulk order. Multiplicative stacking is defensible but harder to explain to customers ("why isn't 15% + 20% = 35%?") and rarely worth the complexity over additive+cap.

## Open questions for the team

1. Stack, or higher-only?
2. If stacking: additive or multiplicative?
3. Is there a cap? What value?
4. Does the cap apply only when *both* discounts are active, or also cap a single large discount (e.g., a future bulk tier alone exceeding 25%)?

Once these are answered, fill in the blank column and this table becomes the spec — ready to hand to `/tabletest` for the actual test. Let me know if you want this saved to a file in the repo.