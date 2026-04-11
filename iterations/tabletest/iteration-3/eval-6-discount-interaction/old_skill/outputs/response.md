# Discount Interaction — Worked Examples

This is a classic case where the examples help surface the disagreement rather than paper over it. Let me lay out the scenarios so the team can see exactly what they need to decide.

## Example Table

| Scenario                                      | Bulk Discount | Loyalty Discount | Final Discount? |
|-----------------------------------------------|---------------|------------------|-----------------|
| No discounts apply                            | None          | None             | 0%              |
| Bulk discount only                            | 10%           | None             | 10%             |
| Loyalty discount only                         | None          | 15%              | 15%             |
| Both apply — lower loyalty, higher bulk       | 10%           | 5%               | ?               |
| Both apply — equal discounts                  | 10%           | 10%              | ?               |
| Both apply — lower bulk, higher loyalty       | 5%            | 15%              | ?               |
| Both apply — large values, cap may matter     | 20%           | 25%              | ?               |

## Open Questions

The `?` cells above are the whole point of this exercise — the team needs to decide:

1. **Stacking vs. higher-only** — When both discounts apply, do we add them together (10% + 15% = 25%), take the higher one (15%), or something else (e.g. apply sequentially: 10% then 15% on the remainder)?

2. **Is there a cap?** — If we stack discounts, is there a maximum total discount? (e.g. 30% cap?) If so, what is it, and does it apply when taking the higher-only value too?

3. **Which baseline does loyalty apply to?** — If we stack sequentially, does loyalty apply to the original price or the already-bulk-discounted price? This changes the maths.

Once the team agrees on these rules, we can fill in the `?` column and also decide whether we need additional boundary rows (e.g. a row at exactly the cap value).

## Suggested Decision Record

Before writing any code, record the team's decision in a single sentence of the form:

> "When both discounts apply, we [stack / take the higher / apply sequentially], [with / without] a cap of [X%]."

That sentence becomes the rule we implement and test against.
