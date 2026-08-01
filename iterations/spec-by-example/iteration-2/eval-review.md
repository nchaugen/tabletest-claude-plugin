# Eval Review — spec-by-example, Iteration 2

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-sonnet-5 · **Date:** 2026-08-01 · **Evals:** 10

## Summary

105/130 (80.8%) · 755855 tokens · 793.3s · $2.3654

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

> ⚠️ **25 assertion verdicts moved** vs iteration 1. These are deltas, not attributions: read each eval's `outputs/` and `narration.md` before explaining any of them, and do not start the next iteration until every entry in `analysis-todo.md` has a cause.

## Delta vs Iteration 1

**Regressions (12):**
- ❌ eval-4-loan-approval: `scenario-names-describe-conditions`
- ❌ eval-5-order-transitions: `no-duplicate-rows-within-a-table`
- ❌ eval-6-discount-interaction: `extreme-discount-row`
- ❌ eval-10-subscription-billing: `no-duplicate-rows-within-a-table`
- ❌ eval-13-shipping-partial-applicability: `standard-destination-value-set-or-blank`
- ❌ eval-13-shipping-partial-applicability: `no-duplicate-rows-within-a-table`
- ❌ eval-16-order-splitting: `3.2-depth-fulfillment-scenarios`
- ❌ eval-16-order-splitting: `3.3-depth-delivery-address-scenarios`
- ❌ eval-16-order-splitting: `3.4-depth-availability-scenarios`
- ❌ eval-16-order-splitting: `3.5-depth-warehouse-scenarios`
- ❌ eval-21-event-registration-sbe: `scenario-names-describe-conditions`
- ❌ eval-24-weekly-pay-sbe: `no-duplicate-rows-within-a-table`

**Improvements (13):**
- ✅ eval-4-loan-approval: `minimal-rows-per-concern`
- ✅ eval-10-subscription-billing: `refund-table-shows-proportion`
- ✅ eval-12-subscription-loyalty-trial: `open-question-surfaced`
- ✅ eval-12-subscription-loyalty-trial: `question-mark-only-on-outputs`
- ✅ eval-13-shipping-partial-applicability: `minimal-rows-per-concern`
- ✅ eval-16-order-splitting: `3.8-readability-item-property-mapping`
- ✅ eval-16-order-splitting: `concerns-decomposed`
- ✅ eval-16-order-splitting: `minimal-rows-per-concern`
- ✅ eval-17-shopping-cart: `4.6-depth-cart-total-scenarios`
- ✅ eval-17-shopping-cart: `minimal-rows-per-concern`
- ✅ eval-21-event-registration-sbe: `minimal-rows-per-concern`
- ✅ eval-24-weekly-pay-sbe: `concerns-decomposed`
- ✅ eval-24-weekly-pay-sbe: `minimal-rows-per-concern`

## Resource Comparison vs Iteration 1

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-4-loan-approval | 10/13 | 10/13 | 66599 | 41419 | 43.0 | 29.2 |
| eval-5-order-transitions | 9/10 | 10/10 | 67003 | 42138 | 39.0 | 38.8 |
| eval-6-discount-interaction | 6/7 | 7/7 | 106994 | 41313 | 58.6 | 29.7 |
| eval-10-subscription-billing | 15/16 | 15/16 | 74488 | 49077 | 114.2 | 119.1 |
| eval-12-subscription-loyalty-trial | 12/13 | 10/13 | 75075 | 47311 | 93.8 | 119.4 |
| eval-13-shipping-partial-applicability | 8/12 | 9/12 | 69806 | 44430 | 67.8 | 63.8 |
| eval-16-order-splitting | 11/19 | 12/19 | 74397 | 49408 | 95.2 | 151.4 |
| eval-17-shopping-cart | 17/18 | 15/18 | 75612 | 44224 | 79.2 | 60.1 |
| eval-21-event-registration-sbe | 10/13 | 10/13 | 74974 | 43926 | 121.5 | 68.2 |
| eval-24-weekly-pay-sbe | 7/9 | 6/9 | 70907 | 43153 | 80.8 | 46.1 |

## Per-Eval Results

### ⚠️ Eval eval-4-loan-approval

**10/13** · 66599 tokens · 43046ms

- ✅ **produces-markdown-table**: Output contains a markdown table (using | column | syntax)
- ✅ **output-column-has-question-mark**: At least one output column name ends with '?' (e.g. 'Eligible?' or 'Approved?')
- ✅ **business-language-columns**: Column names use domain/business language — no implementation terms like 'creditScore', 'isEligible', 'boolean', 'int'
- ✅ **senior-threshold-row**: Table includes at least one row specifically for the senior applicant (65+) threshold scenario
- ✅ **missing-income-marked-open**: The missing income scenario is represented — either as a row with an open/uncertain expected value (?, TBD, or blank) or as an explicit open question note
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Senior applicant at threshold') not outcomes ('Approved')
- ✅ **threshold-values-visible**: The policy thresholds (650 for standard, 600 for senior) appear as concrete values in the Credit Score column — not just described in prose. The reader can see the boundary values in the table rows.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **threshold-as-column**: Policy thresholds (650/600 credit scores) appear in a dedicated policy column (e.g. 'Credit threshold') separate from the applicant's actual credit score column. The reader sees both the threshold and the score, making the comparison explicit.
- ✅ **concrete-domain-values**: Cell values use concrete domain terms (e.g. '65', '700', 'Stable', 'yes', 'no') — not abstract codes or raw booleans like 'true', 'false', 'CATEGORY_A', '1'.
- ❌ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
  > Single monolithic table 'Loan Approval Decision' combines age, credit score, and income rules in one table
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
  > 10 rows mixing threshold, senior, and income permutations in one table rather than minimal per-concern tables
- ❌ **separates-age-credit-income**: Age boundary policy, credit score categorisation, and income status are separated into distinct tables, with a final table combining these for the expected verdict
  > No separate tables for age boundary, credit score, income; all combined into one 'Loan Approval Decision' table

### ⚠️ Eval eval-5-order-transitions

**9/10** · 67003 tokens · 39050ms

- ✅ **produces-markdown-table**: Output contains at least one markdown table
- ✅ **cancellation-coverage**: Table covers cancellation rules — includes rows for states where cancellation is allowed and where it is not
- ✅ **return-window-addressed**: The 30-day return window rule is addressed — either as a threshold column, a separate table, or flagged as an open question
- ✅ **value-set-or-multiple-states**: Uses value sets {PENDING, CONFIRMED} or equivalent to express 'regardless of which starting state' for cases where a rule holds across states
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **row-independence**: Each row is independently verifiable — no row references a prior row's outcome. Each row specifies its own starting state and expected result.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ✅ **separates-transitions-and-returns**: Status transition rules and return eligibility rules are in separate tables

### ⚠️ Eval eval-6-discount-interaction

**6/7** · 106994 tokens · 58588ms

- ✅ **produces-markdown-table**: Output contains a markdown table
- ✅ **does-not-invent-resolution**: The output does NOT silently resolve the stacking/cap ambiguity — it leaves the conflicting cases as open questions, blank cells, or explicitly marks them as unresolved
- ✅ **covers-both-discounts-applying**: Table includes at least one row where both bulk and loyalty discounts apply simultaneously (exposing the interaction)
- ✅ **open-question-surfaced**: The unresolved decision (stacking vs. higher-only vs. cap) is explicitly surfaced — via an Open Questions column, a '?' cell, or a note
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'
- ✅ **extreme-discount-row**: Table includes at least one row with a high combined discount (e.g. large order + top loyalty tier) that would force a decision about whether a cap applies — making the cap question concrete, not theoretical.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column

### ⚠️ Eval eval-10-subscription-billing

**15/16** · 74488 tokens · 114229ms

- ✅ **produces-multiple-tables**: Output contains at least 2 separate markdown tables (not everything crammed into one table)
- ✅ **tables-have-distinct-concerns**: The tables cover distinct concerns — e.g. pricing/plans is separate from cancellation/refund rules
- ✅ **output-columns-have-question-marks**: At least one output column in each table ends with '?'
- ✅ **refund-exception-covered**: The 24-hour no-refund exception is included as a distinct scenario row
- ✅ **business-language-columns**: Column names use domain/business language — no implementation terms like 'boolean', 'int', 'isProrated', camelCase identifiers
- ✅ **trial-cancellation-row**: Includes a row or scenario for cancelling during the free trial — no charge was made, so no refund applies. This case should be explicit, not omitted.
- ✅ **prorated-refund-formula**: The prorated refund calculation is shown or explained — e.g. days remaining × daily rate, or an equivalent formula. Not just 'prorated refund' without showing how.
- ✅ **24h-boundary-near-boundary**: The 24-hour no-refund window is tested near the boundary (e.g. values at or close to 23h, 24h, 25h) — not just 'within 24h' vs 'well after renewal'.
- ✅ **eligibility-separate-from-amount**: Refund eligibility (yes/no, based on 24h rule and trial status) and refund amount (prorated calculation) are in separate tables — not mixed into one table. This separation makes each concern independently reviewable.
- ✅ **question-mark-only-on-outputs**: The '?' suffix is only used on output or derived columns (e.g. 'Refund Eligible?', 'Refund Amount?') — not on given input columns like plan type, loyalty status, or subscription phase. A column that interprets or classifies an input value (e.g. deriving whether a cancellation falls within 24h from a raw time value) is a valid derived output and may use '?'.
- ✅ **output-values-traceable**: Output values (e.g. refund amounts, prices) can be derived from input values by a reader — the calculation is visible or explained. Not just magic numbers with no traceable origin.
- ✅ **refund-table-includes-price-paid**: The refund amount table includes the price paid (or daily rate) as an input column, making the table self-contained — a reader should not need to refer to another table or the spec to verify the refund calculation.
- ❌ **refund-table-shows-proportion**: The refund amount table includes an intermediate traceability column (e.g. 'Refund Proportion?', fraction, or daily rate) that makes the calculation step visible before the final amount — not just inputs and a final number.
  > Table 3 only lists 'Renewal Charge', 'Cycle Length', 'Unused Days', 'Refund Amount?' with no dedicated proportion/rate column.
- ✅ **rules-separate-from-arithmetic**: Refund eligibility (yes/no rule based on 24h window, trial status) and refund amount (prorated calculation) are treated as separate concerns — not mixed into one table where rules and arithmetic are interleaved.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.

### ⚠️ Eval eval-12-subscription-loyalty-trial

**12/13** · 75075 tokens · 93844ms

- ✅ **produces-multiple-tables**: Output contains at least 2 separate markdown tables
- ✅ **annual-no-trial-rule-represented**: A table or note explicitly captures that the free trial is not available on annual plans (not just implied)
- ✅ **loyalty-discount-row-present**: At least one table row demonstrates the loyalty programme discount scenario for annual plan subscribers
- ✅ **surfaces-genuine-open-questions**: The response identifies at least one genuinely underspecified interaction — such as: what happens when a trial subscriber upgrades to annual mid-trial, when a subscriber joins the loyalty programme mid-billing-cycle, or whether a cancellation refund is based on the discounted or full price. These are NOT stated in the rules and require a product decision.
- ✅ **open-question-surfaced**: The annual/loyalty/trial interaction is correctly resolved with a note explaining that they are mutually exclusive (annual gets loyalty discounts but no trial, monthly gets trial but no loyalty discount) — not left as an unresolved open question
- ✅ **loyalty-discount-annual-only**: Makes clear (via table structure, a note, or explicit rows) that the loyalty discount applies to the annual plan only — a loyalty member on monthly pays the same as anyone else.
- ✅ **loyalty-refund-ambiguity**: The loyalty annual subscriber's prorated cancellation refund is either surfaced as an open question (discounted vs list price) OR calculated from the discounted price with a visible trace — since that is what was actually paid
- ✅ **cancellation-refund-table**: Includes a cancellation/refund table or section (not just pricing) — the loyalty discount creates a refund calculation ambiguity that should be shown with concrete rows.
- ✅ **refund-table-includes-loyalty-dimension**: The cancellation/refund table includes a loyalty member column (or equivalent) to distinguish loyalty vs non-loyalty annual refund rows — not just plan type alone.
- ✅ **question-mark-only-on-outputs**: The '?' suffix is only used on output or derived columns (e.g. 'Refund?', 'Price?') — not on given input columns like plan type, loyalty status, or subscription phase. A column that interprets or classifies an input value (e.g. deriving whether a cancellation falls within 24h from a raw time value) is a valid derived output and may use '?'.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ❌ **separates-pricing-trial-loyalty-refund**: Pricing/plans, trial eligibility, loyalty discount applicability, and cancellation/refund are in separate tables
  > Table '2. Subscription Price' merges pricing and loyalty discount applicability into one table rather than giving loyalty its own separate table.

### ⚠️ Eval eval-13-shipping-partial-applicability

**8/12** · 69806 tokens · 67791ms

- ✅ **produces-markdown-table**: Output contains a markdown table
- ✅ **express-split-by-uk-free-tier**: Express shipping is split into at least two cases distinguishing the UK free tier (UK, order £50+, free) from the paid tier. The split is driven by the UK-only free threshold rule.
- ❌ **express-uses-value-sets**: Express rows use value sets to consolidate cases with the same outcome — e.g. non-UK destinations at/above threshold in one row (such as {Ireland, Other} or {Ireland, International}), or UK at/above threshold in one row ({£50, £75}). Not enumerated as separate rows per destination or per order value when the result is identical. Any reasonable label for non-UK/Ireland destinations is acceptable (International, Other, Rest of World, etc.).
  > Express at threshold, Ireland ... £9.99' and 'Express above threshold, Other ... £9.99' are kept as two separate rows instead of one {Ireland, Other} row
- ❌ **overnight-grouped**: Overnight UK and Ireland are in one row using a value set ({UK, Ireland}) — not separate rows, since both have the same cost and availability.
  > Overnight, UK, regardless of order value ... £14.99' and 'Overnight, Ireland, regardless of order value ... £14.99' are separate rows, not {UK, Ireland}
- ❌ **standard-destination-value-set-or-blank**: Standard shipping destination uses a value set covering all options (e.g. {UK, Ireland, International} or {UK, Ireland, Other}) or is blank/absent — either approach is valid since destination is irrelevant to the cost. Any reasonable label for non-UK/Ireland destinations is acceptable.
  > Standard, regardless of order value ... UK ... £3.99' and 'Standard, regardless of destination ... {Ireland, Other} ... £3.99' split destination across two rows rather than one {UK, Ireland, Other} row
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'
- ✅ **concrete-domain-values**: Cell values use concrete domain terms (e.g. 'Standard', 'Express', 'UK', '£3.99') — not abstract codes or raw booleans like 'true', 'false', 'TYPE_1', '1'.
- ✅ **blank-vs-value-set-correct**: Value sets (not blanks) are used for irrelevant inputs — e.g. Standard shipping destination uses a value set like {UK, Ireland, International} because destination exists but doesn't affect the cost. Blanks are reserved for genuinely absent/N/A inputs. The distinction between 'irrelevant to this rule' (value set) and 'not applicable' (blank) is correct.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
  > Separate rows for Overnight UK and Ireland, and separate rows for Express Ireland and Other, add unnecessary rows
- ✅ **separates-availability-and-cost**: Shipping method availability and shipping cost are in separate tables
- ✅ **blank-output-for-na**: When a shipping method is unavailable (e.g. Overnight outside UK/Ireland), the output clearly indicates non-applicability — either via a blank cost cell in a combined table, or by the availability table showing 'No'/unavailable and the cost table omitting those combinations entirely. Filler text like 'N/A', '-', or '0' is not used for unavailable outcomes.

### ⚠️ Eval eval-16-order-splitting

**11/19** · 74397 tokens · 95160ms

- ❌ **3.1a-concern-fulfillment-method**: Fulfillment method splitting is represented as its own table — items with different fulfillment types (store pickup vs home delivery) cannot share a shipment.
  > Fulfillment is merged into 'Table 1: Shipment Grouping Decision' alongside destination and availability, not its own table.
- ❌ **3.1b-concern-delivery-address**: Delivery address splitting is represented as its own table — items going to different addresses must be in separate shipments.
  > Delivery address columns appear only inside the combined 'Table 1: Shipment Grouping Decision', not a separate table.
- ❌ **3.1c-concern-availability**: Availability splitting is represented as its own table — in-stock items ship immediately, backordered/pre-ordered items ship when available.
  > Availability columns are part of the same merged 'Table 1: Shipment Grouping Decision', not isolated in their own table.
- ✅ **3.1d-concern-warehouse-allocation**: Warehouse allocation (minimising shipments) is represented as its own table — choosing which warehouses to ship from to minimise total shipment count.
- ✅ **3.1e-concern-companion-products**: Companion product grouping is represented as its own table — paired items (e.g. camera body and lens) should ship together from the same location when possible.
- ✅ **3.2-depth-fulfillment-scenarios**: Fulfillment table covers at least: all items for home delivery, all items for store pickup, and a mix of delivery and pickup items.
- ✅ **3.3-depth-delivery-address-scenarios**: Delivery address table covers at least: all items to the same address, and items to different addresses.
- ✅ **3.4-depth-availability-scenarios**: Availability table covers at least: all items in stock, a mix of in-stock and pre-order/backorder, and two backordered/pre-ordered items with different dates. Pre-order and backorder are recognised as equivalent for splitting semantics — either by comment, note, or by including both with the same rules.
- ❌ **3.5-depth-warehouse-scenarios**: Warehouse allocation table covers at least: single warehouse has full quantity, multi-warehouse split needed, fewer-warehouse split preferred over more warehouses, and cannot fulfill (insufficient total stock).
  > No scenario explicitly compares a feasible fewer-warehouse split against a more-warehouse alternative to show preference for fewer warehouses.
- ❌ **3.6-depth-companion-scenarios**: Companion table covers at least: single warehouse has both companions, unavoidable split (no warehouse has both but both are available), and fulfillment not possible (one companion not available anywhere).
  > Table 3 has only 'shared warehouse', 'different single warehouses', and 'multiple shared warehouses (tie)' rows; no 'companion not available anywhere' scenario.
- ✅ **3.7-readability-scenario-names**: Scenario names describe the business situation (e.g. 'Consolidate: two beats three', 'Everything at one warehouse') — not 'Test case 2'.
- ✅ **3.8-readability-item-property-mapping**: Trigger tables represent multi-item orders where each item has its own fulfillment type, address, and availability. The notation makes the item-to-property mapping unambiguous — not value sets like {delivery, pickup} which lose which item has which property.
- ✅ **3.9-readability-scalar-column**: Warehouse allocation table uses a scalar count column (e.g. 'Qty ordered') not item lists, when product identity doesn't matter for the logic.
- ✅ **3.10-readability-business-language**: Column names use business/domain language (e.g. 'Shipments?' not 'Result?', 'Warehouse stock' not 'inventory_map'). The tables read as a specification a product person could review.
- ✅ **3.11-readability-output-column-question-mark**: Output/expected columns end with '?' (e.g. 'Shipments?', 'Groups?') to distinguish them from input columns.
- ✅ **3.12-correctness-valid-assignments**: Inventory assignments are physically valid: items are only assigned to locations that have them. Shipment counts match the assignments shown.
- ✅ **3.13-depth-open-questions**: Multiple open questions are surfaced across the tables — ambiguities, edge cases, or product decisions that the requirements don't fully specify.
- ❌ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
  > Table 1 merges fulfillment, destination, and availability splitting into one 'Shipment Grouping Decision' table instead of three separate concern tables as expected
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
  > Table 1 needs 9 rows varying fulfillment/destination/availability together since concerns weren't separated, e.g. rows for pickup vs delivery mixed with availability sets

### ⚠️ Eval eval-17-shopping-cart

**17/18** · 75612 tokens · 79243ms

- ✅ **4.1a-concern-item-operations**: Item operations (add/remove) are represented as their own table or tables — separate from coupon and cart total logic.
- ✅ **4.1b-concern-coupon-application**: Coupon application (entering, replacing, rejecting codes) is represented as its own table — separate from item operations and cart total calculation.
- ✅ **4.1c-concern-cart-total**: Cart total calculation (how different coupon types affect the price) is represented as its own table — separate from coupon application and item operations.
- ✅ **4.1d-concern-checkout**: Checkout (stock verification) is represented as its own table — separate from the other concerns.
- ✅ **4.3-rows-independently-executable**: Each table row is independently executable — no row depends on a prior row's result. The table expresses rules, not a sequential test script.
- ✅ **4.4-depth-item-operations**: Item operations cover at least: add to empty cart, add to non-empty cart, add more of an item already in cart, remove item from cart, remove last item in cart, remove item not in cart.
- ❌ **4.5-depth-coupon-application**: Coupon application table covers validity and replacement — not coupon types. Scenarios include at least: add valid coupon (none active), add valid coupon (another active), add expired coupon (none active), add expired coupon (another active), add nonexistent code (none active), add nonexistent code (another active).
  > Expired row only has 'Active Coupon Before' as a set of coupons, no expired-with-none-active or nonexistent-with-none-active row.
- ✅ **4.6-depth-cart-total-scenarios**: Cart total table covers how different coupon types affect the price. Scenarios include at least: no coupon, percentage off whole cart, fixed amount off, fixed amount exceeding cart total (floors at zero), product-specific discount (product in cart), product-specific discount (product not in cart).
- ✅ **4.7-depth-checkout-scenarios**: Checkout table covers at least: empty cart, all items in stock, and not all items in stock (insufficient inventory).
- ✅ **4.8-readability-business-language**: Column names use business/user-perspective language (e.g. 'Message?' not 'Result?', 'Coupon code' not 'input'). The tables read as a specification a product person could review.
- ✅ **4.9-readability-test-data-visible**: Test data that affects the outcome — product prices, coupon rules (type, amount, expiry) — is visible in the table via columns, notes, or explicit setup, not silently hardcoded. The reader can trace how inputs lead to outputs.
- ✅ **4.10-depth-open-questions**: Multiple open questions are surfaced — ambiguities or product decisions the requirements don't specify (e.g. what happens to a product-specific coupon when that product is removed, or how percentage and product-specific discounts interact).
- ✅ **4.11-correctness-cart-totals**: Cart totals are arithmetically correct: sum(quantity × price) − coupon = total, for every row in the total calculation table.
- ✅ **4.12-coupon-expiry-column**: Coupon application table uses a column to signal expiry status — either an 'Expired?' yes/no column, a 'Status' column, or equivalent. Expiry is visible per row, not just implied by the scenario name.
- ✅ **4.14-coupon-before-after-columns**: Coupon application table uses before/after columns (e.g. 'Active coupon' / 'Active coupon after?') to specify the state change when entering, replacing, or entering invalid coupon codes.
- ✅ **4.15-readability-scenario-names**: Scenario names describe the business situation (e.g. 'Add more of same product', 'Replace active coupon') — not 'Test case 1' or generic labels.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.

### ⚠️ Eval eval-21-event-registration-sbe

**10/13** · 74974 tokens · 121537ms

- ✅ **produces-markdown-table**: Output contains at least one markdown table (using | column | syntax)
- ✅ **validation-rules-covered**: Email validation and name-required scenarios are present — at least one row for invalid email and one for missing name.
- ❌ **blank-for-absent-optional**: Dietary requirements and/or accessibility needs use blank cells when the attendee has not provided them (genuinely absent) — not 'N/A' or 'none'.
  > | Missing name, regardless of other fields | | valid@example.com | {Vegetarian, (none)} | {Wheelchair access, (none)} | no | Name is required |
- ✅ **output-column-has-question-mark**: At least one output column name ends with '?' (e.g. 'Accepted?', 'Price?')
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Missing email', 'Early-bird single attendee') — not outcomes ('Rejected', 'Discounted').
- ✅ **open-question-surfaced**: The discount stacking ambiguity (early-bird + group) is surfaced as an open question — not silently resolved.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ✅ **separates-validation-and-pricing**: Input validation rules and pricing/discount calculation are in separate tables
- ❌ **descriptive-registration-date**: Registration date uses descriptive values (e.g. 'before cutoff', 'on cutoff', 'after cutoff') rather than raw dates — making the table readable without knowing the actual cutoff date.
  > Registration Date column uses raw dates like '2026-07-01', '2026-05-15', not descriptive terms.
- ✅ **no-redundant-policy-columns**: If registration date uses descriptive values like 'before cutoff', the cutoff date is not also present as a separate column (it's already encoded in the description). Numeric policy thresholds like min group size are acceptable as columns since they are configuration values worth discussing with business experts.
- ✅ **group-size-policy-as-column**: The minimum group size threshold appears as a policy column (e.g. 'Min group size (policy)') — surfacing this as a configurable business rule, not just implied by the boundary rows.

### ⚠️ Eval eval-24-weekly-pay-sbe

**7/9** · 70907 tokens · 80826ms

- ✅ **produces-markdown-table**: Output contains at least one markdown table (using | column | syntax)
- ✅ **output-column-has-question-mark**: At least one output column name ends with '?' (e.g. 'Pay?', 'Rate?')
- ✅ **scenario-names-describe-conditions**: Scenario names describe work patterns or conditions ('Part-time weekday', 'Full week with overtime', 'Sunday shift') — not outcomes ('Gets overtime', 'Double pay').
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Weekday hours', 'Sunday hours', 'Hourly rate', 'Weekly pay?') — not code identifiers.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **overtime-boundary-covered**: The overtime threshold boundary (40 hours) is covered with at least a value at the threshold and one above.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ❌ **separates-classification-and-calculation**: Payable hours categorisation (1x regular, 1.5x overtime, 2x Sunday/holiday) and pay calculation (hours × rate) are in separate tables
  > Table 1 embeds formula '830 = 40×20 + 1×20×1.5' directly in pay column, mixing classification and calculation rather than separating rate-selection from arithmetic verification.

