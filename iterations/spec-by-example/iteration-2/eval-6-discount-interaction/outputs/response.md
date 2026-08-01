## Discount Interaction: Bulk × Loyalty — Example Tables

I split this into two tables per the skill's decomposition guidance: **eligibility** (not ambiguous) is separate from **combination** (the open question). Mixing them would bury the real disagreement under rows that already have a clear answer.

**Assumption flagged up front:** I don't have your actual bulk/loyalty rates or thresholds, so I used illustrative numbers (bulk 15% at ≥20 units, loyalty 10% for members) purely to make the interaction concrete. Swap in your real policy values before treating this as final — the *structure* of the tables is the deliverable, not these specific percentages.

### Table 1 — Discount Eligibility (not in dispute)

| Scenario | Order Quantity | Bulk Threshold (Policy) | Loyalty Status | Bulk Discount Applies? | Loyalty Discount Applies? |
|---|---|---|---|---|---|
| Small order, non-member | 5 units | 20 units | Non-member | no | no |
| Bulk order, non-member | 25 units | 20 units | Non-member | yes | no |
| Small order, loyalty member | 5 units | 20 units | Member | no | yes |
| Bulk order, loyalty member | 25 units | 20 units | Member | yes | yes |
| At the bulk threshold | 20 units | 20 units | Non-member | yes | no |
| Just below bulk threshold | 19 units | 20 units | Non-member | no | no |

This part isn't ambiguous — quantity determines bulk eligibility, status determines loyalty eligibility, independently. The dispute only exists in the "both apply" row, which is where Table 2 picks up.

### Table 2 — Combining Discounts When Both Apply (OPEN — needs team decision)

Same scenario (Base Price 100, Bulk 15%, Loyalty 10%, both eligible) computed under each candidate policy, so the team can see the real dollar difference rather than debate in the abstract:

| Scenario | Base Price | Bulk Discount (Policy) | Loyalty Discount (Policy) | Final Discount? | Final Price? |
|---|---|---|---|---|---|
| Stacked — additive | 100 | 15% | 10% | 25% | 75.00 |
| Stacked — sequential (bulk then loyalty) | 100 | 15% | 10% | 23.5% | 76.50 |
| Higher discount only | 100 | 15% | 10% | 15% | 85.00 |
| Stacked, capped at 20% | 100 | 15% | 10% | 20% | 80.00 |

Notes on the math, so no one has to trust it blind:
- **Additive stacking**: 15% + 10% = 25% off → 100 × 0.75 = 75.00
- **Sequential stacking**: 100 × 0.85 × 0.90 = 76.50 (order doesn't matter — multiplication commutes, so "bulk then loyalty" and "loyalty then bulk" give the same result)
- **Higher-only**: max(15%, 10%) = 15% → 100 × 0.85 = 85.00
- **Capped stack**: additive 25% clipped to a 20% cap → 100 × 0.80 = 80.00

### Open Questions for the Team

| # | Question | Why it matters |
|---|---|---|
| 1 | Stacked, higher-only, or capped? | Table 2 shows a $10 swing (75.00 vs 85.00) on a single order — not a rounding difference |
| 2 | If stacked: additive or sequential? | 75.00 vs 76.50 — small here, but diverges more as rates grow |
| 3 | If capped: what's the cap value, and is it a % cap on combined discount or a $ cap on savings? | Determines whether "capped at 20%" is even the right number |
| 4 | Does the cap (if any) apply per line item, per order, or per customer over time? | A per-customer cap needs state the calculator doesn't currently track |
| 5 | Can bulk + loyalty ever combine with a *third* discount (promo code, clearance)? | Out of scope for this table, but worth confirming it's out of scope explicitly rather than by omission |

### Suggested boundary rows to add once #1–#3 are resolved

Once the team picks a policy, add these to Table 2 to pin down edges the same way Table 1 pins down the bulk threshold:
- A combined-discount scenario that sits exactly at the cap (if capped)
- A combined-discount scenario one unit past the cap
- A scenario where bulk and loyalty rates are equal (tests whether "higher discount only" has a defined tie-break, or whether it's moot because equal rates make the tie-break irrelevant)

This is ready to bring to the team as-is — Table 1 needs no discussion, Table 2 is the actual meeting agenda. Once you have an answer, I can fold the resolved rule back into Table 2 as a single definitive table and hand it to `/tabletest` for implementation.