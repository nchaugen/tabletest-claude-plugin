# Analysis to-do — tabletest variant=next, iteration 6

Compared against **official iteration 40**. **15 assertion verdicts moved.**

These are deltas, not attributions. Before explaining any of them, read the generated
output for that eval and its narration — a grader justification can name the right
assertion and still name the wrong cause, and graders do misfire outright. Fill in the
cause line from the artefact, not from the justification.

Do not start the next iteration until every line below has a cause.

## LOST `2.15-ticket-count-uses-value-sets` — eval-15-reis-discount

Grader said: _ReisDiscountLadderTest enumerates one row per boundary (0, 3, 4, 8, 9, 14, 19, 24, 29, 34, 39, 44) rather than grouping tiers with value sets like {4, 5, 6, 7, 8, 9} for 5%._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## WON `2.17-zone-irrelevance-visible` — eval-15-reis-discount

Grader said: _ReisDiscountCalculatorTest has Zone column with value set {ZONE_1, ZONE_2, ZONE_3} in both rows, demonstrating discount is independent of zone._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `2.20-readability-one-row-per-tier` — eval-15-reis-discount

Grader said: _ReisDiscountLadderTest has 12 rows but only 9 tiers. Tiers 0%, 5%, and 40% are split across multiple rows (e.g., rows 1-2 both show 0%, rows 3-4 both show 5%), violating one-row-per-tier._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `2.9-correctness-value-set-tier-semantics` — eval-15-reis-discount

Grader said: _ReisDiscountLadderTest does not use value sets at all. Each row has a single 'Prior Single Tickets' value, not a set like {4, 5, 6, 7, 8, 9} for the 5% tier._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `minimal-rows-per-concern` — eval-15-reis-discount

Grader said: _ReisDiscountLadderTest has 12 rows for 9 tiers; rows 1-2 (0%), 3-4 (5%), and 11-12 (40%) duplicate tier outcomes without value sets, creating unnecessary rows._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `zone-independent-counting` — eval-15-reis-discount

Grader said: _SingleTicketFrequencyCounterTest has no zone variation in history rows. PastPurchaseFixtures hardcodes ZoneValidity.ZONE_1 for all parsed purchases, making zone irrelevance claimed but not exercised by data._

- Output: `eval-15-reis-discount/outputs/`
- Narration: `eval-15-reis-discount/narration.md`
- Raw transcript: `eval-15-reis-discount/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `concerns-decomposed` — eval-18-convert-from-code

Grader said: _First table mixes renewal auto-approval rule with premium calculation in a single @TableTest, combining decision and premium concerns._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `minimal-rows-per-concern` — eval-18-convert-from-code

Grader said: _First table has 3 rows mixing renewal auto-approval (decision concern) with premium examples; should separate these concerns into distinct tables._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `observable-io-only` — eval-18-convert-from-code

Grader said: _Column 'Max Risk Score' appears in rejectsApplicationsAboveRiskThreshold table, exposing internal risk calculation._

- Output: `eval-18-convert-from-code/outputs/`
- Narration: `eval-18-convert-from-code/narration.md`
- Raw transcript: `eval-18-convert-from-code/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `minimal-rows-per-concern` — eval-22-event-registration-tt

Grader said: _Validation table has 9 rows including multiple email format variants (missing @, missing local part, missing domain, no TLD) and blank/whitespace name variants. Pricing table has 5 rows with value sets {1,2,3,4} and {5,6,20} creating implicit permutations rather than minimal distinct cases._

- Output: `eval-22-event-registration-tt/outputs/`
- Narration: `eval-22-event-registration-tt/narration.md`
- Raw transcript: `eval-22-event-registration-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `business-language-columns` — eval-23-loan-approval-tt

Grader said: _Column 'Customer Age' is used instead of 'Age'; 'Has Stable Income' instead of 'Stable income'. These are verbose but not code identifiers. However, the reference decomposition uses 'Age' and 'Stable income' as the standard. The response uses 'Customer Age' and 'Has Stable Income', which are business language but deviate from the expected concise form._

- Output: `eval-23-loan-approval-tt/outputs/`
- Narration: `eval-23-loan-approval-tt/narration.md`
- Raw transcript: `eval-23-loan-approval-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `description-no-redundant-field-values` — eval-23-loan-approval-tt

Grader said: _Table 1 @Description: 'Stable income is held constant (true) here' — but 'true' is not shown in a column, so this passes. Table 2 @Description: 'age is held constant (40, a standard applicant) and the credit score always qualifies (700, above the standard threshold of 650)' — states 40 and 700 which are fixed in the method call, not columns, so passes. Table 3 @Description: 'Age is held constant (40...) and the credit score (600) is below the standard threshold of 650' — states 40 and 600 which are fixed in the method call, not columns. However, the description also states 'standard threshold of 650' and 'senior threshold of 600', which are policy values not in the table columns, so this is acceptable. All descriptions pass._

- Output: `eval-23-loan-approval-tt/outputs/`
- Narration: `eval-23-loan-approval-tt/narration.md`
- Raw transcript: `eval-23-loan-approval-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `rule-statable-from-table` — eval-23-loan-approval-tt

Grader said: _Table 1 states 'Standard applicant at the threshold | 40 | 650 | REJECTED' but the rule that 650 is the exact boundary (not 649 or 651) and that the threshold is *strictly above* is not derivable from the table alone—it requires the @Description or method body. The age-65 threshold of 600 is similarly implicit._

- Output: `eval-23-loan-approval-tt/outputs/`
- Narration: `eval-23-loan-approval-tt/narration.md`
- Raw transcript: `eval-23-loan-approval-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `coupon-as-single-column` — eval-29-shopping-cart-tt

Grader said: _The `appliesOneCouponAtATime` table uses a `Coupons` column (a map of code→spec), not a single coupon column. The `calculatesCartTotalAfterCoupon` table has `Active Coupon` as a single column, but the `appliesOneCouponAtATime` table violates this by using a map-based `Coupons` store instead of a single coupon value per row._

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 

## LOST `rule-statable-from-table` — eval-29-shopping-cart-tt

Grader said: _The `calculatesCartTotalAfterCoupon` table shows 'PRODUCT Widget 3.00' in the coupon column and expects 19.00 for [Widget:2, Gadget:1] with prices [Widget:10.00, Gadget:5.00]. The rule '2×10 + 1×5 − (2×3) = 19' requires inferring that the product coupon is a per-unit discount; the @Description flags this as an open assumption, but the table itself does not state the operation (per-unit vs. flat deduction) — it only demonstrates one case._

- Output: `eval-29-shopping-cart-tt/outputs/`
- Narration: `eval-29-shopping-cart-tt/narration.md`
- Raw transcript: `eval-29-shopping-cart-tt/conversation.jsonl` (gitignored, trimmed each cycle — mine it now)
- Cause (from artefact): 
