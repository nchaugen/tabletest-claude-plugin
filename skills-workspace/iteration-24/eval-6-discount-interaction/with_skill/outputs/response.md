Good moment to use this skill. Let me work through the elicitation.

**Naming the behaviour**: "Discount Combination" — specifically how bulk and loyalty discounts interact when both apply.

Let me draft a table that makes the open questions visible. First I need to understand the inputs:

- What triggers the **bulk** discount? (order quantity, order value, or both?)
- What triggers the **loyalty** discount? (membership tier, years as customer, number of orders?)
- What is the output — a final discount percentage, a discount amount, or a final price?

While you gather those, here is a starting table structured around the three competing rules your team needs to decide between, with the disputed rows marked open:

---

**Discount Combination**

| Scenario | Order Value | Bulk Threshold | Loyalty Tier | Bulk Discount | Loyalty Discount | Discount Applied? | Notes / Open |
|---|---|---|---|---|---|---|---|
| Neither discount qualifies | £80 | £100 | None | 0% | 0% | 0% | |
| Bulk only | £150 | £100 | None | 10% | 0% | 10% | |
| Loyalty only | £80 | £100 | Silver (5%) | 0% | 5% | 5% | |
| Both qualify — stacked? | £150 | £100 | Silver (5%) | 10% | 5% | **?** | 15% if stacked, 10% if best-of |
| Both qualify — capped? | £500 | £100 | Gold (15%) | 20% | 15% | **?** | 35% if stacked — is there a cap? |
| At the bulk threshold exactly | £100 | £100 | None | 10% | 0% | 10% | Confirm: ≥ threshold or > threshold? |

---

The three rows with `?` are the decisions your team needs to make. They surface three distinct sub-questions:

1. **Stacking vs. best-of** — row 4: when both apply, is the result 15% (stacked) or 10% (best-of)?
2. **Cap** — row 5: if stacking is allowed, does 35% get capped (at what value?), or is it uncapped?
3. **Threshold boundary** — row 6: is the bulk discount triggered at exactly £100, or only above it?

A few things worth checking before the team meeting:

- Should the **cap** (if it exists) be a column? If it varies by customer segment or product category, it belongs in the table as an explicit input rather than a hidden constant in the code.
- Is "best-of" actually "apply both but floor at zero, sum at cap"? That would be a third option beyond the two obvious ones.
- Does the **order** of applying discounts matter for stacking? (e.g., 10% bulk on £150 = £135, then 5% loyalty on £135 = £128.25 vs. 15% on £150 = £127.50)

Would you like to fill in the `?` cells, or should I add more rows to cover cases the team will need?