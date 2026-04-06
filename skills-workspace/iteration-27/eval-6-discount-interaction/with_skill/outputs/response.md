Good case for spec-by-example — let's nail down the rules through concrete examples before writing any more code.

**Step 1: Name the behaviour**

I'd suggest: **"Discount Calculation — Bulk and Loyalty Interaction"**

Does that capture what you need, or is it broader (e.g. does a cap apply to the total order price, not just these two discounts)?

---

**Step 2: First example**

Let me propose a starting table with the obvious cases, then we'll work through the disputed combinations:

| Scenario | Order Value | Bulk Discount | Loyalty Discount | Discount Applied? | Final Price? | Notes |
|---|---|---|---|---|---|---|
| No discounts apply | £50 | 0% | 0% | 0% | £50.00 | baseline |
| Bulk only | £200 | 10% | 0% | 10% | £180.00 | |
| Loyalty only | £50 | 0% | 15% | 15% | £42.50 | |
| Both apply — stacked? | £200 | 10% | 15% | 25%? | £150.00? | **disputed** |
| Both apply — higher only? | £200 | 10% | 15% | 15%? | £170.00? | **disputed** |
| Both apply — capped? | £200 | 10% | 15% | 20%? | £160.00? | **disputed** |

The three "disputed" rows are the crux. To resolve them, I need a few things from you:

---

**Questions to bring to the team:**

1. **Stacking vs. best-of**: Should a loyal bulk buyer get *both* discounts, or just the better one? (Stacking rewards loyalty + volume; best-of is simpler and cheaper for the business.)

2. **Stacking order matters if stacking**: Is it applied sequentially (bulk on base price, then loyalty on the discounted price — giving `£200 × 0.9 × 0.85 = £153`), or additively (add both percentages first, then apply once — giving `£200 × 0.75 = £150`)? These are different numbers.

3. **Cap**: Is there a maximum discount percentage, and if so, what is it? Does it apply per-discount or to the combined total?

4. **Thresholds**: What order value triggers bulk? What qualifies a customer as loyal? (These will be columns once we know the rules — constants now may become configurable later.)

---

Once you have answers to even 2–3 of those, we can fill in the disputed rows and mark the rest as open. Which of these is least clear to the team right now?