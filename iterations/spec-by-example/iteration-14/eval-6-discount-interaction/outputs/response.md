Bulk and loyalty are two contributions to *one* combined-discount decision, so this is a single table (per the "one rule, one axis" test) — the open question is which combination rule applies, not two separate rules. Since I can't ask follow-ups, I made concrete assumptions, built the table with four candidate policies computed side by side so the team has real numbers to react to, and left the actual answer open.

## Assumptions (stated since I have no code to check against)

- Bulk and loyalty discounts are each already resolved to a **percentage** by other rules (quantity tiers → bulk %, membership tier → loyalty %) — this table only covers how the two percentages *combine*, not how they're individually derived.
- Both discounts, when applicable, are computed against the same base (order subtotal) — not against each other's post-discount price, unless the team picks the "multiplicative" candidate below.
- "Absent" (blank) means the customer/order doesn't qualify for that discount at all — not a 0% discount.

## Open decisions for the team

1. **Stack or take-higher?** If a customer qualifies for both, do the percentages combine, or does only the larger one apply?
2. **If stacking, additive or multiplicative?** `bulk% + loyalty%` (e.g. 10+10=20%) vs. sequential `1-(1-bulk%)(1-loyalty%)` (e.g. 10%+10%→19%). Your original question used "stacked," which is ambiguous between these two.
3. **Is there a cap, and at what value?** The table below uses **20% as an illustrative placeholder** — not a proposal, just something concrete to react to.
4. **Does the cap apply to a single discount too**, or only when two are stacked? (See the last row — a 25% bulk discount alone is a real case if cap scope isn't explicit.)

## Combines Bulk and Loyalty Discounts into a Total Discount

| Scenario | Bulk % | Loyalty % | Cap (TBD, shown as 20%) | Additive %? | Multiplicative %? | Additive, Capped %? | Higher-Only %? | Agreed Combined %? | Open Question |
|---|---|---|---|---|---|---|---|---|---|
| No discount eligibility | | | 20 | 0 | 0 | 0 | 0 | 0 | |
| Bulk discount only | 10 | | 20 | 10 | 10 | 10 | 10 | 10 | |
| Loyalty discount only | | 8 | 20 | 8 | 8 | 8 | 8 | 8 | |
| Both apply, bulk higher | 10 | 5 | 20 | 15 | 14.5 | 15 | 10 | ? | Stack or higher-only? |
| Both apply, equal tiers | 10 | 10 | 20 | 20 | 19 | 20 | 10 | ? | Additive happens to land exactly on the placeholder cap here — coincidence, not by design |
| Both apply, sum exceeds cap | 15 | 10 | 20 | 25 | 23.5 | 20 | 15 | ? | Cap actually bites: 25% vs 20% diverge — this is the case that forces the cap decision |
| Both apply, extreme tiers | 30 | 20 | 20 | 50 | 44 | 20 | 30 | ? | Shows how far apart the policies get at high tiers — worth showing the team to motivate urgency |
| Single discount alone exceeds cap | 25 | | 20 | 25 | 25 | 20 or 25? | 25 | ? | Does the cap apply to a single, non-stacked discount? |

The first three rows already agree across every candidate policy — the disagreement only starts once both discounts are present, which is exactly rows 4–8.

## If you want a starting proposal (rather than a blank canvas)

A common default that's easy to defend: **additive stacking, capped**, with the cap applying uniformly (including to single discounts, so a customer with one huge discount isn't treated inconsistently from one with two smaller ones). That resolves questions 1, 2, and 4 in one stroke — leaving only "what's the cap number" for finance/product to set. Worth floating as a strawman rather than starting from zero.

## Next step

Once the team fills in the "Agreed Combined %?" column (and picks a real cap), this table hands off directly to `/tabletest` to become the executable `@TableTest` for the combination rule — just drop the candidate-policy columns and keep Scenario / Bulk % / Loyalty % / Cap / Agreed Combined %?.

Want me to save this as a markdown file you can drop into the PR or share with the team?