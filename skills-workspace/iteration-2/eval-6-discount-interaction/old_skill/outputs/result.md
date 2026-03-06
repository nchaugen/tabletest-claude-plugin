# Discount Interaction: Bulk + Loyalty

This table pins down how the bulk discount and loyalty discount combine on a single order. The three open questions that drove this spec session are:

1. Are discounts stacked (applied together) or is only the higher one taken?
2. If stacked, is there a cap on the combined discount?
3. What happens when only one discount applies?

The table expresses the agreed-upon examples along with the cells that remain open for team decision.

---

## Example Table: Combined Discount Calculation

| Scenario | Base Order Value | Bulk Discount Rate (Policy) | Bulk Threshold (Policy) | Loyalty Years | Loyalty Discount Rate (Policy) | Discount Cap (Policy) | Discount Applied? | Final Price? | Open Questions |
|---|---|---|---|---|---|---|---|---|---|
| No discounts qualify | £80 | 10% | £100 | 0 | 5% | 25% | 0% | £80.00 | |
| Bulk discount only, loyalty not earned | £120 | 10% | £100 | 0 | 5% | 25% | 10% | £108.00 | |
| Loyalty discount only, order below bulk threshold | £80 | 10% | £100 | 3 | 5% | 25% | 5% | £76.00 | |
| Both discounts qualify — stacked or higher only? | £120 | 10% | £100 | 3 | 5% | 25% | ? | ? | Team decision needed: stacked (15%) giving £102, or higher only (10%) giving £108? |
| Both discounts qualify, loyalty rate is higher | £120 | 10% | £100 | 3 | 15% | 25% | ? | ? | Same question: stacked (25%) giving £90, or higher only (15%) giving £102? |
| Stacked discounts would exceed cap | £120 | 10% | £100 | 3 | 20% | 25% | ? | ? | Stacked = 30% but cap is 25%; does cap apply, giving £90, or does "higher only" make cap irrelevant here? |
| Order exactly at bulk threshold | £100 | 10% | £100 | 0 | 5% | 25% | ? | ? | Is threshold inclusive (£100 qualifies) or exclusive (£100 does not qualify)? |
| Order one penny below bulk threshold | £99.99 | 10% | £100 | 0 | 5% | 25% | 0% | £99.99 | Assumes threshold is strictly greater than; confirm. |
| Long-tenure customer, small order | £50 | 10% | £100 | 10 | 5% | 25% | 5% | £47.50 | Loyalty still applies even though bulk does not. |
| Zero loyalty years, order qualifies for bulk | £200 | 10% | £100 | 0 | 5% | 25% | 10% | £180.00 | |
| Loyalty discount rate at cap alone | £120 | 10% | £100 | 3 | 25% | 25% | ? | ? | If cap equals loyalty rate, does bulk discount still add anything or is it absorbed? |

---

## Open Decisions

The following questions must be resolved before implementation can continue:

1. **Stacking vs. higher-only**: When both bulk and loyalty discounts qualify, are they added together or is only the higher one applied?

2. **Discount cap**: Is there a maximum combined discount percentage? If yes, what is it and does it apply only to stacked discounts or also to a single discount that happens to be high?

3. **Bulk threshold boundary**: Is the threshold inclusive (order value ≥ threshold qualifies) or exclusive (order value > threshold qualifies)?

The rows marked `?` in `Discount Applied?` and `Final Price?` become unblocked as soon as those three decisions are made. All other rows in the table are ready for implementation.
