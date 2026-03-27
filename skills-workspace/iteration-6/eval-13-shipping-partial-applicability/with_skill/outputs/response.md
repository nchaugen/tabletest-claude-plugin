## Shipping Cost Calculator

Here's the example table covering the rules you described:

| Scenario | Shipping Method | Destination | Order Value | Shipping Cost? |
|---|---|---|---|---|
| Standard, any destination and order value | Standard | {UK, Ireland, International} | {£20, £75} | £3.99 |
| Express, order below threshold | Express | {UK, Ireland, International} | £30 | £9.99 |
| Express, order just below threshold | Express | {UK, Ireland, International} | £49.99 | £9.99 |
| Express, order at threshold in UK | Express | UK | £50 | £0.00 |
| Express, order above threshold in UK | Express | UK | £75 | £0.00 |
| Express, order at or above threshold outside UK | Express | {Ireland, International} | {£50, £75} | £9.99 |
| Overnight, UK or Ireland | Overnight | {UK, Ireland} | {£20, £75} | £14.99 |
| Overnight, international destination | Overnight | International | {£20, £75} | unavailable |

A few notes on the design:

- The `{...}` value sets on rows 1, 6, and 7 express "regardless of" — e.g. Standard costs £3.99 no matter which of the three destinations you pick, and no matter the order value.
- Rows 3 and 4 pin the Express threshold boundary: £49.99 → £9.99, £50.00 → £0.00 (in UK). Without these two rows, the `≥` vs `>` semantics are ambiguous.
- Row 6 explicitly states that Ireland and International both pay £9.99 at £50+ — the free tier is UK-only. This is the highest-risk row to get wrong silently.

**One open question worth confirming:** What does "unavailable" mean in your API contract — does requesting Overnight for an international destination throw an error, return a sentinel value, or exclude the option from the choices offered to the user? That distinction affects how row 8 should be tested.

**A second question:** Does "Ireland" mean Republic of Ireland only, or does it also cover Northern Ireland? Northern Ireland is part of the UK, so if your destination list distinguishes them, Overnight availability and Express free tier eligibility for Northern Ireland addresses may differ from what's shown here.