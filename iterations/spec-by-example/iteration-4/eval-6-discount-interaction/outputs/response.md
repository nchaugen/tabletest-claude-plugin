I don't have the actual discount calculator code to pull real tier values from — there's no such code in this working directory — so this is built from reasonable domain assumptions, flagged explicitly below. Since this is a policy question (stack vs. highest vs. capped) rather than an already-agreed rule, I've laid it out as a comparison table: same input scenarios, computed three ways side by side, so the team can look at concrete numbers and pick one.

## Assumptions (state these when you bring this to the team)

- Bulk % and loyalty % are each already resolved by their own tier lookup (not this table's concern) — this table takes them as given inputs.
- A candidate cap of **25%** is used as a placeholder — no cap value has been agreed, so treat this as a number to argue about, not a decision.
- "Stack" means simple addition of both percentages; "Highest Only" means max of the two; "Stack, Capped" means addition then clamped to the cap.

## Combines Bulk and Loyalty Discount Percentages Under Candidate Policies

| Scenario | Bulk Discount % | Loyalty Discount % | Combined % — Stack (Uncapped)? | Combined % — Highest Only? | Combined % — Stack, Capped at 25%? |
|---|---|---|---|---|---|
| Neither discount is earned | 0 | 0 | 0 | 0 | 0 |
| Only the bulk discount is earned | 10 | 0 | 10 | 10 | 10 |
| Only the loyalty discount is earned | 0 | 15 | 15 | 15 | 15 |
| Both earned, well under any reasonable cap | 10 | 5 | 15 | 10 | 15 |
| Both earned, combined exactly at the proposed cap | 15 | 10 | 25 | 15 | 25 |
| Both earned, combined just over the proposed cap | 15 | 12 | 27 | 15 | 25 |
| Both at maximum tier, combined far exceeds the proposed cap | 20 | 25 | 45 | 25 | 25 |

Final price, if you want it, is straightforward arithmetic once the % is picked: `Order Total × (1 − Combined %)` — kept out of the table so it doesn't obscure the policy comparison.

## What this table shows, worth raising explicitly with the team

1. **The policy choice is invisible for single-discount orders.** Rows 1–3 converge no matter which column you pick — the decision only matters once a customer qualifies for both. That should lower the stakes/urgency framing if the team is stuck.
2. **Row 7 is the sharpest disagreement to resolve.** At max tiers (20% bulk + 25% loyalty), "Stack, Capped at 25%" gives *exactly* the same result as "Highest Only" — your best loyalty customers get zero extra benefit from also buying in bulk. If the intent of stacking is to reward combined behavior, a flat 25% cap defeats that purpose at the top end. Worth asking: should the cap be higher than the max single-discount tier, or is capping-down-to-parity acceptable?
3. **The cap value (25%) is unconstrained by anything I know about the business** — it's a placeholder so the boundary rows (5 and 6) have something concrete to test against. Get the team to commit to a real number, or confirm "uncapped stacking" is actually fine.
4. **Inclusive boundary assumed**: row 5 shows combined == cap not triggering any special handling (25% stays 25%, not reduced further) — confirm that's intended (`<=` cap) rather than the cap being a strict "less than" ceiling.

## Next step

Once the team picks a policy (and, if "capped", a cap value), this table converts directly into a `@TableTest` or table-driven test — collapse to the single chosen output column, drop the other two candidate columns, and the row set here already covers baseline/single/boundary/extreme cases.