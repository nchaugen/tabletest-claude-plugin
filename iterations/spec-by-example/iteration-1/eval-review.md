# Eval Review — spec-by-example, Iteration 1

**Model:** sonnet · **Date:** 2026-04-11 · **Evals:** 10

## Summary

**with_skill:** 104/130 (80.0%) · 446399 tokens · 725.8s · $1.2605

## Per-Eval Results

### ⚠️ Eval eval-4-loan-approval [with_skill]

**10/13** · 41419 tokens · 29174ms

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
  > Single 14-row table mixes age boundaries, credit score rules, and income status—three distinct concerns per domain
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
  > One monolithic table with rows testing same concerns (income) across different ages/scores: "Standard adult, unstable...", "Senior...unstable..."
- ❌ **separates-age-credit-income**: Age boundary policy, credit score categorisation, and income status are separated into distinct tables, with a final table combining these for the expected verdict
  > All three concerns combined in single table; should be 4 separate tables per instructions but uses one monolithic table

### ✅ Eval eval-5-order-transitions [with_skill]

**10/10** · 42138 tokens · 38832ms

- ✅ **produces-markdown-table**: Output contains at least one markdown table
- ✅ **cancellation-coverage**: Table covers cancellation rules — includes rows for states where cancellation is allowed and where it is not
- ✅ **return-window-addressed**: The 30-day return window rule is addressed — either as a threshold column, a separate table, or flagged as an open question
- ✅ **value-set-or-multiple-states**: Uses value sets {PENDING, CONFIRMED} or equivalent to express 'regardless of which starting state' for cases where a rule holds across states
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **row-independence**: Each row is independently verifiable — no row references a prior row's outcome. Each row specifies its own starting state and expected result.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns
- ✅ **separates-transitions-and-returns**: Status transition rules and return eligibility rules are in separate tables

### ✅ Eval eval-6-discount-interaction [with_skill]

**7/7** · 41313 tokens · 29690ms

- ✅ **produces-markdown-table**: Output contains a markdown table
- ✅ **does-not-invent-resolution**: The output does NOT silently resolve the stacking/cap ambiguity — it leaves the conflicting cases as open questions, blank cells, or explicitly marks them as unresolved
- ✅ **covers-both-discounts-applying**: Table includes at least one row where both bulk and loyalty discounts apply simultaneously (exposing the interaction)
- ✅ **open-question-surfaced**: The unresolved decision (stacking vs. higher-only vs. cap) is explicitly surfaced — via an Open Questions column, a '?' cell, or a note
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'
- ✅ **extreme-discount-row**: Table includes at least one row with a high combined discount (e.g. large order + top loyalty tier) that would force a decision about whether a cap applies — making the cap question concrete, not theoretical.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column

### ⚠️ Eval eval-10-subscription-billing [with_skill]

**15/16** · 49077 tokens · 119066ms

- ✅ **produces-multiple-tables**: Output contains at least 2 separate markdown tables (not everything crammed into one table)
- ✅ **tables-have-distinct-concerns**: The tables cover distinct concerns — e.g. pricing/plans is separate from cancellation/refund rules
- ✅ **output-columns-have-question-marks**: At least one output column in each table ends with '?'
- ✅ **refund-exception-covered**: The 24-hour no-refund exception is included as a distinct scenario row
- ✅ **business-language-columns**: Column names use domain/business language — no implementation terms like 'boolean', 'int', 'isProrated', camelCase identifiers
- ✅ **trial-cancellation-row**: Includes a row or scenario for cancelling during the free trial — no charge was made, so no refund applies. This case should be explicit, not omitted.
- ✅ **prorated-refund-formula**: The prorated refund calculation is shown or explained — e.g. days remaining × daily rate, or an equivalent formula. Not just 'prorated refund' without showing how.
- ✅ **24h-boundary-near-boundary**: The 24-hour no-refund window is tested near the boundary (e.g. values at or close to 23h, 24h, 25h) — not just 'within 24h' vs 'well after renewal'.
- ✅ **eligibility-separate-from-amount**: Refund eligibility (yes/no, based on 24h rule and trial status) and refund amount (prorated calculation) are in separate tables — not mixed into one table.
- ✅ **question-mark-only-on-outputs**: The '?' suffix is only used on output or derived columns (e.g. 'Refund Eligible?', 'Refund Amount?') — not on given input columns
- ✅ **output-values-traceable**: Output values (e.g. refund amounts, prices) can be derived from input values by a reader — the calculation is visible or explained.
- ✅ **refund-table-includes-price-paid**: The refund amount table includes the price paid (or daily rate) as an input column, making the table self-contained
- ❌ **refund-table-shows-proportion**: The refund amount table includes an intermediate traceability column (e.g. 'Refund Proportion?', fraction, or daily rate) that makes the calculation step visible before the final amount
  > Table 2 has Plan Price, Days Used, Billing Cycle → Refund Amount; no intermediate column showing proportion, fraction, or daily rate calculation step.
- ✅ **rules-separate-from-arithmetic**: Refund eligibility (yes/no rule based on 24h window, trial status) and refund amount (prorated calculation) are treated as separate concerns
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.

### ⚠️ Eval eval-12-subscription-loyalty-trial [with_skill]

**10/13** · 47311 tokens · 119388ms

- ✅ **produces-multiple-tables**: Output contains at least 2 separate markdown tables
- ✅ **annual-no-trial-rule-represented**: A table or note explicitly captures that the free trial is not available on annual plans
- ✅ **loyalty-discount-row-present**: At least one table row demonstrates the loyalty programme discount scenario for annual plan subscribers
- ✅ **surfaces-genuine-open-questions**: The response identifies at least one genuinely underspecified interaction
- ❌ **open-question-surfaced**: The annual/loyalty/trial interaction is correctly resolved with a note explaining mutual exclusivity
  > Note states 'loyalty membership does not affect monthly pricing or trial eligibility — the discount is annual-only' but does not explicitly state 'annual gets no trial, monthly gets no loyalty discount' or declare them mutually exclusive
- ✅ **loyalty-discount-annual-only**: Makes clear that the loyalty discount applies to the annual plan only
- ✅ **loyalty-refund-ambiguity**: The loyalty annual subscriber's prorated cancellation refund is calculated from the discounted price with visible trace
- ✅ **cancellation-refund-table**: Includes a cancellation/refund table or section with concrete rows
- ✅ **refund-table-includes-loyalty-dimension**: The cancellation/refund table includes a loyalty member column to distinguish loyalty vs non-loyalty annual refund rows
- ❌ **question-mark-only-on-outputs**: The '?' suffix is only used on output or derived columns, not input columns
  > Cancellation Refund table has 'Within 24h of Renewal Charge' (derived/interpreted) without '?', while Open Questions table has 'Within 24h of First Monthly Charge?' with '?' — inconsistent application
- ✅ **concerns-decomposed**: Multiple tables address distinct concerns, not one monolithic table
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules
- ❌ **separates-pricing-trial-loyalty-refund**: Pricing/plans, trial eligibility, loyalty discount applicability, and cancellation/refund are in separate tables
  > Subscription Pricing table bundles pricing, trial eligibility (Trial Period? column), and loyalty discount applicability (Loyalty Member column with different charges) into one table; only separated from cancellation/refund

### ⚠️ Eval eval-13-shipping-partial-applicability [with_skill]

**9/12** · 44430 tokens · 63814ms

- ✅ **produces-markdown-table**: Output contains a markdown table
- ✅ **express-split-by-uk-free-tier**: Express shipping is split into at least two cases distinguishing the UK free tier (UK, order £50+, free) from the paid tier. The split is driven by the UK-only free threshold rule.
- ❌ **express-uses-value-sets**: Express rows use value sets to consolidate cases with the same outcome — e.g. non-UK destinations at/above threshold in one row (such as {Ireland, Other} or {Ireland, International}), or UK at/above threshold in one row ({£50, £75}). Not enumerated as separate rows per destination or per order value when the result is identical. Any reasonable label for non-UK/Ireland destinations is acceptable (International, Other, Rest of World, etc.).
  > Express UK at £50 and £75 both cost £0.00 but shown as separate rows instead of consolidat…
- ❌ **overnight-grouped**: Overnight UK and Ireland are in one row using a value set ({UK, Ireland}) — not separate rows, since both have the same cost and availability.
  > Availability table shows separate rows: Overnight | UK | yes and Overnight | Ireland | yes
- ✅ **standard-destination-value-set-or-blank**: Standard shipping destination uses a value set covering all options (e.g. {UK, Ireland, International} or {UK, Ireland, Other}) or is blank/absent — either approach is valid since destination is irrelevant to the cost. Any reasonable label for non-UK/Ireland destinations is acceptable.
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'
- ✅ **concrete-domain-values**: Cell values use concrete domain terms (e.g. 'Standard', 'Express', 'UK', '£3.99') — not abstract codes or raw booleans like 'true', 'false', 'TYPE_1', '1'.
- ✅ **blank-vs-value-set-correct**: Value sets (not blanks) are used for irrelevant inputs — e.g. Standard shipping destination uses a value set like {UK, Ireland, International} because destination exists but doesn't affect the cost. Blanks are reserved for genuinely absent/N/A inputs. The distinction between 'irrelevant to this rule' (value set) and 'not applicable' (blank) is correct.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
  > Express UK at £50.00 and £75.00 both cost £0.00 but are separate rows; should consolidate…
- ✅ **separates-availability-and-cost**: Shipping method availability and shipping cost are in separate tables
- ✅ **blank-output-for-na**: When a shipping method is unavailable (e.g. Overnight outside UK/Ireland), the output clearly indicates non-applicability — either via a blank cost cell in a combined table, or by the availability table showing 'No'/unavailable and the cost table omitting those combinations entirely. Filler text like 'N/A', '-', or '0' is not used for unavailable outcomes.

### ⚠️ Eval eval-16-order-splitting [with_skill]

**12/19** · 49408 tokens · 151387ms

- ❌ **3.1a-concern-fulfillment-method**: Fulfillment method splitting is represented as its own table — items with different fulfillment types (store pickup vs home delivery) cannot share a shipment.
  > Fulfillment type is a column in 'Order Split Groups' table mixed with availability and addresses. Expected output specifies five distinct tables, but response provides three, combining concerns.
- ❌ **3.1b-concern-delivery-address**: Delivery address splitting is represented as its own table — items going to different addresses must be in separate shipments.
  > Delivery Addresses is a column in 'Order Split Groups' mixed with fulfillment type and availability. Expected five tables with each concern separate; response combines three concerns in one table.
- ❌ **3.1c-concern-availability**: Availability splitting is represented as its own table — in-stock items ship immediately, backordered/pre-ordered items ship when available.
  > Availability is a column in 'Order Split Groups' mixed with fulfillment and addresses. Expected structure calls for five distinct tables; response has only three tables mixing unrelated concerns.
- ✅ **3.1d-concern-warehouse-allocation**: Warehouse allocation (minimising shipments) is represented as its own table — choosing which warehouses to ship from to minimise total shipment count.
- ✅ **3.1e-concern-companion-products**: Companion product grouping is represented as its own table — paired items (e.g. camera body and lens) should ship together from the same location when possible.
- ✅ **3.2-depth-fulfillment-scenarios**: Fulfillment table covers at least: all items for home delivery, all items for store pickup, and a mix of delivery and pickup items.
- ✅ **3.3-depth-delivery-address-scenarios**: Delivery address table covers at least: all items to the same address, and items to different addresses.
- ✅ **3.4-depth-availability-scenarios**: Availability table covers at least: all items in stock, a mix of in-stock and pre-order/backorder, and two backordered/pre-ordered items with different dates. Pre-order and backorder are recognised as equivalent for splitting semantics — either by comment, note, or by including both with the same rules.
- ✅ **3.5-depth-warehouse-scenarios**: Warehouse allocation table covers at least: single warehouse has full quantity, multi-warehouse split needed, fewer-warehouse split preferred over more warehouses, and cannot fulfill (insufficient total stock).
- ❌ **3.6-depth-companion-scenarios**: Companion table covers at least: single warehouse has both companions, unavoidable split (no warehouse has both but both are available), and fulfillment not possible (one companion not available anywhere).
  > Nine scenarios in Companion table cover co-location and unavoidable split ('Companions only available at separate warehouses'), but no scenario shows one companion completely unavailable.
- ✅ **3.7-readability-scenario-names**: Scenario names describe the business situation (e.g. 'Consolidate: two beats three', 'Everything at one warehouse') — not 'Test case 2'.
- ❌ **3.8-readability-item-property-mapping**: Trigger tables represent multi-item orders where each item has its own fulfillment type, address, and availability. The notation makes the item-to-property mapping unambiguous — not value sets like {delivery, pickup} which lose which item has which property.
  > Order Split Groups row 'Pickup and delivery in same order' shows 'Fulfillment Types: Home Delivery, Store Pickup' without mapping which items have which type. Ambiguous which item has pickup vs delivery.
- ✅ **3.9-readability-scalar-column**: Warehouse allocation table uses a scalar count column (e.g. 'Qty ordered') not item lists, when product identity doesn't matter for the logic.
- ✅ **3.10-readability-business-language**: Column names use business/domain language (e.g. 'Shipments?' not 'Result?', 'Warehouse stock' not 'inventory_map'). The tables read as a specification a product person could review.
- ✅ **3.11-readability-output-column-question-mark**: Output/expected columns end with '?' (e.g. 'Shipments?', 'Groups?') to distinguish them from input columns.
- ✅ **3.12-correctness-valid-assignments**: Inventory assignments are physically valid: items are only assigned to locations that have them. Shipment counts match the assignments shown.
- ✅ **3.13-depth-open-questions**: Multiple open questions are surfaced across the tables — ambiguities, edge cases, or product decisions that the requirements don't fully specify.
- ❌ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
  > Order Split Groups mixes fulfillment type, availability, and delivery address in one 13-row table. Expected output specifies five distinct tables; response provides three with first one combining three concerns.
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
  > Order Split Groups has 13 rows combining fulfillment, availability, and address concerns (e.g. 'All splits in one order'). Combining concerns creates unnecessary permutations that would be eliminated by separate tables.

### ⚠️ Eval eval-17-shopping-cart [with_skill]

**15/18** · 44224 tokens · 60084ms

- ✅ **4.1a-concern-item-operations**: Item operations (add/remove) are represented as their own table or tables — separate from coupon and cart total logic.
- ✅ **4.1b-concern-coupon-application**: Coupon application (entering, replacing, rejecting codes) is represented as its own table — separate from item operations and cart total calculation.
- ✅ **4.1c-concern-cart-total**: Cart total calculation (how different coupon types affect the price) is represented as its own table — separate from coupon application and item operations.
- ✅ **4.1d-concern-checkout**: Checkout (stock verification) is represented as its own table — separate from the other concerns.
- ✅ **4.3-rows-independently-executable**: Each table row is independently executable — no row depends on a prior row's result. The table expresses rules, not a sequential test script.
- ✅ **4.4-depth-item-operations**: Item operations cover at least: add to empty cart, add to non-empty cart, add more of an item already in cart, remove item from cart, remove last item in cart, remove item not in cart.
- ❌ **4.5-depth-coupon-application**: Coupon application table covers validity and replacement — not coupon types. Scenarios include at least: add valid coupon (none active), add valid coupon (another active), add expired coupon (none active), add expired coupon (another active), add nonexistent code (none active), add nonexistent code (another active).
  > Missing required scenario: 'Enter nonexistent code when one already active'. Table shows 3 separate rows for valid coupon with different coupon types (percentage, fixed, product-specific) rather than unified coverage.
- ❌ **4.6-depth-cart-total-scenarios**: Cart total table covers how different coupon types affect the price. Scenarios include at least: no coupon, percentage off whole cart, fixed amount off, fixed amount exceeding cart total (floors at zero), product-specific discount (product in cart), product-specific discount (product not in cart).
  > Missing scenario: 'Product-specific discount (product not in cart)'. Table only shows 'Product-specific discount' with Widget in cart, not when target product is absent.
- ✅ **4.7-depth-checkout-scenarios**: Checkout table covers at least: empty cart, all items in stock, and not all items in stock (insufficient inventory).
- ✅ **4.8-readability-business-language**: Column names use business/user-perspective language (e.g. 'Message?' not 'Result?', 'Coupon code' not 'input'). The tables read as a specification a product person could review.
- ✅ **4.9-readability-test-data-visible**: Test data that affects the outcome — product prices, coupon rules (type, amount, expiry) — is visible in the table via columns, notes, or explicit setup, not silently hardcoded. The reader can trace how inputs lead to outputs.
- ✅ **4.10-depth-open-questions**: Multiple open questions are surfaced — ambiguities or product decisions the requirements don't specify (e.g. what happens to a product-specific coupon when that product is removed, or how percentage and product-specific discounts interact).
- ✅ **4.11-correctness-cart-totals**: Cart totals are arithmetically correct: sum(quantity × price) − coupon = total, for every row in the total calculation table.
- ✅ **4.12-coupon-expiry-column**: Coupon application table uses a column to signal expiry status — either an 'Expired?' yes/no column, a 'Status' column, or equivalent. Expiry is visible per row, not just implied by the scenario name.
- ✅ **4.14-coupon-before-after-columns**: Coupon application table uses before/after columns (e.g. 'Active coupon' / 'Active coupon after?') to specify the state change when entering, replacing, or entering invalid coupon codes.
- ✅ **4.15-readability-scenario-names**: Scenario names describe the business situation (e.g. 'Add more of same product', 'Replace active coupon') — not 'Test case 1' or generic labels.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
  > Coupon Application table has 3 separate rows for the same scenario 'valid coupon, none active': one each for percentage, fixed, and product-specific coupon types. Since coupon type is a separate concern (tested in Cart Total table), this is an unnecessary permutation combining concerns.

### ⚠️ Eval eval-21-event-registration-sbe [with_skill]

**10/13** · 43926 tokens · 68212ms

- ✅ **produces-markdown-table**: Output contains at least one markdown table (using | column | syntax)
- ✅ **validation-rules-covered**: Email validation and name-required scenarios are present — at least one row for invalid email and one for missing name.
- ❌ **blank-for-absent-optional**: Dietary requirements and/or accessibility needs use blank cells when the attendee has not provided them (genuinely absent) — not 'N/A' or 'none'.
  > Dietary and accessibility columns are completely absent from both tables; response asks 'Dietary requirements and accessibility needs don't appear in the output columns' but does not demonstrate blank cells as required.
- ✅ **output-column-has-question-mark**: At least one output column name ends with '?' (e.g. 'Accepted?', 'Price?')
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Missing email', 'Early-bird single attendee') — not outcomes ('Rejected', 'Discounted').
- ✅ **open-question-surfaced**: The discount stacking ambiguity (early-bird + group) is surfaced as an open question — not silently resolved.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules.
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.
  > Table 1 contains both 'Typical valid registration' and 'All fields provided' rows—both show identical data (Alice Smith, alice@example.com, yes) making one row redundant
- ✅ **separates-validation-and-pricing**: Input validation rules and pricing/discount calculation are in separate tables
- ❌ **descriptive-registration-date**: Registration date uses descriptive values (e.g. 'before cutoff', 'on cutoff', 'after cutoff') rather than raw dates — making the table readable without knowing the actual cutoff date.
  > Registration Date column contains raw dates: 2026-06-20, 2026-06-01, 2026-06-15 instead of descriptive values like 'before cutoff' or 'after cutoff'
- ✅ **no-redundant-policy-columns**: If registration date uses descriptive values like 'before cutoff', the cutoff date is not also present as a separate column (it's already encoded in the description). Numeric policy thresholds like min group size are acceptable as columns since they are configuration values worth discussing with business experts.
- ✅ **group-size-policy-as-column**: The minimum group size threshold appears as a policy column (e.g. 'Min group size (policy)') — surfacing this as a configurable business rule, not just implied by the boundary rows.

### ⚠️ Eval eval-24-weekly-pay-sbe [with_skill]

**6/9** · 43153 tokens · 46127ms

- ✅ **produces-markdown-table**: Output contains at least one markdown table (using | column | syntax)
- ✅ **output-column-has-question-mark**: At least one output column name ends with '?' (e.g. 'Pay?', 'Rate?')
- ✅ **scenario-names-describe-conditions**: Scenario names describe work patterns or conditions ('Part-time weekday', 'Full week with overtime', 'Sunday shift') — not outcomes ('Gets overtime', 'Double pay').
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Weekday hours', 'Sunday hours', 'Hourly rate', 'Weekly pay?') — not code identifiers.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **overtime-boundary-covered**: The overtime threshold boundary (40 hours) is covered with at least a value at the threshold and one above.
- ❌ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
  > Only one table '**Weekly Pay Calculation**' present; expected output specifies 'Multiple markdown tables'
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
  > Single 11-row table mixes classification and calculation concerns without separation
- ❌ **separates-classification-and-calculation**: Payable hours categorisation (1x regular, 1.5x overtime, 2x Sunday/holiday) and pay calculation (hours × rate) are in separate tables
  > Weekday/Sunday/Holiday Hours columns and Pay? column in same table

