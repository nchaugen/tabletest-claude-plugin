# Discount Combination Rules

This table pins down how the bulk discount and loyalty discount interact when both apply to the same order. The open questions column marks decisions that still need team agreement.

| Scenario                                      | Order Value | Bulk Discount (Policy) | Loyalty Discount (Policy) | Bulk Threshold (Policy) | Loyalty Threshold (Policy) | Discount Cap (Policy) | Combination Rule (Policy) | Final Discount?      | Amount Charged? | Open Questions                                                      |
|-----------------------------------------------|-------------|------------------------|---------------------------|-------------------------|----------------------------|-----------------------|--------------------------|----------------------|-----------------|---------------------------------------------------------------------|
| Neither discount applies                      | £80         | 10%                    | 15%                       | £100                    | 12 months                  | ?                     | ?                        | 0%                   | £80.00          |                                                                     |
| Bulk discount only (new customer, large order) | £150        | 10%                    | 15%                       | £100                    | 12 months                  | ?                     | ?                        | 10%                  | £135.00         |                                                                     |
| Loyalty discount only (long-standing, small order) | £80    | 10%                    | 15%                       | £100                    | 12 months                  | ?                     | ?                        | 15%                  | £68.00          |                                                                     |
| Both discounts apply — stacked (additive)     | £150        | 10%                    | 15%                       | £100                    | 12 months                  | none                  | stack                    | 25%                  | £112.50         | Is stacking the agreed rule, or do we take the higher one only?     |
| Both discounts apply — higher wins            | £150        | 10%                    | 15%                       | £100                    | 12 months                  | none                  | higher only              | 15%                  | £127.50         | Is this what "only the higher one" means in practice?               |
| Both discounts apply — stacked but capped     | £150        | 10%                    | 15%                       | £100                    | 12 months                  | 20%                   | stack, cap applies       | 20%                  | £120.00         | If we do stack, is there a cap? What is it?                         |
| Both discounts apply — cap equals stacked result | £150     | 10%                    | 15%                       | £100                    | 12 months                  | 25%                   | stack, cap not triggered | 25%                  | £112.50         | Does this match "stacking with a cap at 25%"?                       |
| High-value order, both discounts, no cap      | £500        | 10%                    | 15%                       | £100                    | 12 months                  | none                  | stack                    | 25%                  | £375.00         | Does the stacking behaviour hold at high order values?              |
| Exactly at bulk threshold                     | £100        | 10%                    | 15%                       | £100                    | 12 months                  | ?                     | ?                        | ?                    | ?               | Is the bulk threshold inclusive (≥ £100) or exclusive (> £100)?     |
| Just below bulk threshold                     | £99         | 10%                    | 15%                       | £100                    | 12 months                  | ?                     | ?                        | 0% or 15%            | ?               | Only loyalty applies if customer qualifies; bulk does not trigger   |
| Exactly at loyalty tenure threshold           | 12 months   | 10%                    | 15%                       | £100                    | 12 months                  | ?                     | ?                        | ?                    | ?               | Is the loyalty threshold inclusive (≥ 12 months) or exclusive?      |
| Both rates equal                              | £150        | 15%                    | 15%                       | £100                    | 12 months                  | none                  | higher only              | 15%                  | £127.50         | If both rates are equal and rule is "higher only", which one wins — or does it not matter? |
| Both discounts apply — stacked exceeds cap    | £150        | 20%                    | 20%                       | £100                    | 12 months                  | 30%                   | stack, cap applies       | 30%                  | £105.00         | Does the cap apply to the combined rate, or per-discount?           |

---

## Open Decisions (for team discussion)

These questions need a decision before implementation can proceed:

1. **Combination rule**: When both discounts apply, do we stack them (add the percentages), take only the higher one, or apply some other logic?
2. **Cap**: Is there a maximum total discount a customer can receive in a single order? If so, what is it?
3. **Threshold inclusivity**: Are the bulk-order and loyalty-tenure thresholds inclusive (≥) or exclusive (>)?
4. **Cap scope**: If there is a cap, does it apply to the combined discount, or to each discount individually before combining?

The rows marked with `?` in the `Final Discount?` column cannot be agreed on until the above decisions are made. Rows that show two candidate outcomes (e.g. "stack" vs "higher only") are the concrete cases the team needs to choose between.
