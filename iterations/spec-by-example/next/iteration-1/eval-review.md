# Eval Review — spec-by-example variant=next, Iteration 1

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-sonnet-5 · **Date:** 2026-08-01 · **Evals:** 10

## Summary

112/130 (86.2%) · 875820 tokens · 1519.9s · $3.6875

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

> ⚠️ **17 assertion verdicts moved** vs official (iterations 2, 1 merged). These are deltas, not attributions: read each eval's `outputs/` and `narration.md` before explaining any of them, and do not start the next iteration until every entry in `analysis-todo.md` has a cause.

## Per-Eval Results

### ⚠️ Eval eval-4-loan-approval

**12/13** · 88605 tokens · 214364ms

- ✅ **produces-markdown-table**: Output contains a markdown table (using | column | syntax)
- ✅ **output-column-has-question-mark**: At least one output column name ends with '?' (e.g. 'Eligible?' or 'Approved?')
- ✅ **business-language-columns**: Column names use domain/business language — no implementation terms like 'creditScore', 'isEligible', 'boolean', 'int'
- ✅ **senior-threshold-row**: Table includes at least one row specifically for the senior applicant (65+) threshold scenario
- ✅ **missing-income-marked-open**: The missing income scenario is represented — either as a row with an open/uncertain expected value (?, TBD, or blank) or as an explicit open question note
- ✅ **scenario-names-describe-conditions**: Scenario names name the variation the row exercises, not the result it produces. The decidable test: FAILS when a scenario name states or paraphrases a value that appears in an expectation column of that same row — 'User cannot delete' beside an Allowed? cell of false, 'No discount applies' beside a Discount? cell of 0.00, 'Contractor gets no bonus' beside a Bonus? cell of 0. Also FAILS on generic labels ('Test 1', 'Test case 2'), which name no variation at all. PASSES otherwise. Naming the rule or the situation is correct even when it makes the outcome inferable — 'Delivery and pickup items always split', 'At the standard threshold, not above it', 'EU express, light package', 'Renewal with no claims', 'Missing name' all PASS. The failure this catches is a name echoing its own expectation cell, not a name that describes what the row is about, and not a name from which a reader who knows the rule could predict the outcome. A single offending name fails the assertion. Judge every table in the response.
- ✅ **threshold-values-visible**: The policy thresholds (650 for standard, 600 for senior) appear as concrete values in the Credit Score column — not just described in prose. The reader can see the boundary values in the table rows.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **threshold-as-column**: Policy thresholds (650/600 credit scores) appear in a dedicated policy column (e.g. 'Credit threshold') separate from the applicant's actual credit score column. The reader sees both the threshold and the score, making the comparison explicit.
- ✅ **concrete-domain-values**: Cell values use concrete domain terms (e.g. '65', '700', 'Stable', 'yes', 'no') — not abstract codes or raw booleans like 'true', 'false', 'CATEGORY_A', '1'.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **no-duplicate-rows-within-a-table**: Judge each table in isolation. The assertion FAILS if a row re-covers an obligation an earlier row in the SAME table already discharged (e.g. a full region x speed x weight cross-product where one row per rate band would state the same rule). Judge each table against its concern's coverage obligations — the distinct behaviours the rule must demonstrate. Where the expected output states those obligations for a concern, judge against that list and do not invent a stricter one; where it does not, derive them from the concern itself. A row that discharges an obligation no other row reaches earns its place however simple it looks, and a value set that covers several values in one row is the preferred discharge, never a failure. Rows that walk one input up while everything else is held are excess when the third and later ones only re-show a direction the first two already established — 'standard applicant, no claims' at ages 30, 40 and 50, or a linear premium sampled at 0, 2 and 4 claims: two samples state the effect and the rest exercise the same arithmetic. They are NOT excess when each value is a boundary, a band, or a distinct branch of the rule — 40 against 41 hours, below/at/above a threshold, one row per rate band — because each then discharges an obligation of its own. ROWS YOU THINK ARE MISSING ARE NEVER A FAILURE HERE — this assertion judges excess rows only, and coverage is judged by this eval's own coverage assertions. Judge every table in the response; your evidence must name each table with PASS or FAIL.
- ❌ **separates-age-credit-income**: Age boundary policy, credit score categorisation, and income status are separated into distinct tables, with a final table combining these for the expected verdict
  > Only two tables shown: age threshold selection and a combined 'Credit Score, Threshold And Income Stability' table — credit score categorisation and income status are not separated into their own tables before a final combining table.

### ✅ Eval eval-5-order-transitions

**10/10** · 83795 tokens · 139064ms

- ✅ **produces-markdown-table**: Output contains at least one markdown table
- ✅ **cancellation-coverage**: Table covers cancellation rules — includes rows for states where cancellation is allowed and where it is not
- ✅ **return-window-addressed**: The 30-day return window rule is addressed — either as a threshold column, a separate table, or flagged as an open question
- ✅ **value-set-or-multiple-states**: Uses value sets {PENDING, CONFIRMED} or equivalent to express 'regardless of which starting state' for cases where a rule holds across states
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **row-independence**: Each row is independently verifiable — no row references a prior row's outcome. Each row specifies its own starting state and expected result.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **no-duplicate-rows-within-a-table**: Judge each table in isolation. The assertion FAILS if a row re-covers an obligation an earlier row in the SAME table already discharged (e.g. a full region x speed x weight cross-product where one row per rate band would state the same rule). Judge each table against its concern's coverage obligations — the distinct behaviours the rule must demonstrate. Where the expected output states those obligations for a concern, judge against that list and do not invent a stricter one; where it does not, derive them from the concern itself. A row that discharges an obligation no other row reaches earns its place however simple it looks, and a value set that covers several values in one row is the preferred discharge, never a failure. Rows that walk one input up while everything else is held are excess when the third and later ones only re-show a direction the first two already established — 'standard applicant, no claims' at ages 30, 40 and 50, or a linear premium sampled at 0, 2 and 4 claims: two samples state the effect and the rest exercise the same arithmetic. They are NOT excess when each value is a boundary, a band, or a distinct branch of the rule — 40 against 41 hours, below/at/above a threshold, one row per rate band — because each then discharges an obligation of its own. ROWS YOU THINK ARE MISSING ARE NEVER A FAILURE HERE — this assertion judges excess rows only, and coverage is judged by this eval's own coverage assertions. Judge every table in the response; your evidence must name each table with PASS or FAIL.
- ✅ **separates-transitions-and-returns**: Status transition rules and return eligibility rules are in separate tables

### ✅ Eval eval-6-discount-interaction

**7/7** · 78752 tokens · 97905ms

- ✅ **produces-markdown-table**: Output contains a markdown table
- ✅ **does-not-invent-resolution**: The output does NOT silently resolve the stacking/cap ambiguity — it leaves the conflicting cases as open questions, blank cells, or explicitly marks them as unresolved
- ✅ **covers-both-discounts-applying**: Table includes at least one row where both bulk and loyalty discounts apply simultaneously (exposing the interaction)
- ✅ **open-question-surfaced**: The unresolved decision (stacking vs. higher-only vs. cap) is explicitly surfaced — via an Open Questions column, a '?' cell, or a note
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'
- ✅ **extreme-discount-row**: Table includes at least one row with a high combined discount (e.g. large order + top loyalty tier) that would force a decision about whether a cap applies — making the cap question concrete, not theoretical.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column

### ⚠️ Eval eval-10-subscription-billing

**14/16** · 80895 tokens · 112528ms

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
  > Table 3 columns: Cycle Amount (£), Cycle Length (Days), Days Remaining (Unused), Refund Amount (£)? — no fraction/proportion/daily-rate column shown.
- ✅ **rules-separate-from-arithmetic**: Refund eligibility (yes/no rule based on 24h window, trial status) and refund amount (prorated calculation) are treated as separate concerns — not mixed into one table where rules and arithmetic are interleaved.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ❌ **no-duplicate-rows-within-a-table**: Judge each table in isolation. The assertion FAILS if a row re-covers an obligation an earlier row in the SAME table already discharged (e.g. a full region x speed x weight cross-product where one row per rate band would state the same rule). Judge each table against its concern's coverage obligations — the distinct behaviours the rule must demonstrate. Where the expected output states those obligations for a concern, judge against that list and do not invent a stricter one; where it does not, derive them from the concern itself. A row that discharges an obligation no other row reaches earns its place however simple it looks, and a value set that covers several values in one row is the preferred discharge, never a failure. Rows that walk one input up while everything else is held are excess when the third and later ones only re-show a direction the first two already established — 'standard applicant, no claims' at ages 30, 40 and 50, or a linear premium sampled at 0, 2 and 4 claims: two samples state the effect and the rest exercise the same arithmetic. They are NOT excess when each value is a boundary, a band, or a distinct branch of the rule — 40 against 41 hours, below/at/above a threshold, one row per rate band — because each then discharges an obligation of its own. ROWS YOU THINK ARE MISSING ARE NEVER A FAILURE HERE — this assertion judges excess rows only, and coverage is judged by this eval's own coverage assertions. Judge every table in the response; your evidence must name each table with PASS or FAIL.
  > Table 1: PASS (3 distinct scenarios: 29 days, 30 days, mid-trial cancel). Table 2: FAIL — rows at 24.5h and 72h both yield 'yes', with 72h ('well into the cycle') re-confirming the same above-threshold branch already discharged by 24.5h ('just past the 24-hour window'), making it an excess sample rather than a new boundary/branch. Table 3: PASS (half-cycle vs last-day, for both monthly and annual, each a distinct branch).

### ⚠️ Eval eval-12-subscription-loyalty-trial

**11/13** · 84473 tokens · 139107ms

- ✅ **produces-multiple-tables**: Output contains at least 2 separate markdown tables
- ✅ **annual-no-trial-rule-represented**: A table or note explicitly captures that the free trial is not available on annual plans (not just implied)
- ✅ **loyalty-discount-row-present**: At least one table row demonstrates the loyalty programme discount scenario for annual plan subscribers
- ✅ **surfaces-genuine-open-questions**: The response identifies at least one genuinely underspecified interaction — such as: what happens when a trial subscriber upgrades to annual mid-trial, when a subscriber joins the loyalty programme mid-billing-cycle, or whether a cancellation refund is based on the discounted or full price. These are NOT stated in the rules and require a product decision.
- ✅ **open-question-surfaced**: The annual/loyalty/trial interaction is correctly resolved with a note explaining that they are mutually exclusive (annual gets loyalty discounts but no trial, monthly gets trial but no loyalty discount) — not left as an unresolved open question
- ✅ **loyalty-discount-annual-only**: Makes clear (via table structure, a note, or explicit rows) that the loyalty discount applies to the annual plan only — a loyalty member on monthly pays the same as anyone else.
- ✅ **loyalty-refund-ambiguity**: The loyalty annual subscriber's prorated cancellation refund is either surfaced as an open question (discounted vs list price) OR calculated from the discounted price with a visible trace — since that is what was actually paid
- ✅ **cancellation-refund-table**: Includes a cancellation/refund table or section (not just pricing) — the loyalty discount creates a refund calculation ambiguity that should be shown with concrete rows.
- ✅ **refund-table-includes-loyalty-dimension**: The cancellation/refund table includes a loyalty member column (or equivalent) to distinguish loyalty vs non-loyalty annual refund rows — not just plan type alone.
- ❌ **question-mark-only-on-outputs**: The '?' suffix is only used on output or derived columns (e.g. 'Refund?', 'Price?') — not on given input columns like plan type, loyalty status, or subscription phase. A column that interprets or classifies an input value (e.g. deriving whether a cancellation falls within 24h from a raw time value) is a valid derived output and may use '?'.
  > "Loyalty Member?" is used as an input column header in Tables 1, 2, and 3a, not just outputs
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **no-duplicate-rows-within-a-table**: Judge each table in isolation. The assertion FAILS if a row re-covers an obligation an earlier row in the SAME table already discharged (e.g. a full region x speed x weight cross-product where one row per rate band would state the same rule). Judge each table against its concern's coverage obligations — the distinct behaviours the rule must demonstrate. Where the expected output states those obligations for a concern, judge against that list and do not invent a stricter one; where it does not, derive them from the concern itself. A row that discharges an obligation no other row reaches earns its place however simple it looks, and a value set that covers several values in one row is the preferred discharge, never a failure. Rows that walk one input up while everything else is held are excess when the third and later ones only re-show a direction the first two already established — 'standard applicant, no claims' at ages 30, 40 and 50, or a linear premium sampled at 0, 2 and 4 claims: two samples state the effect and the rest exercise the same arithmetic. They are NOT excess when each value is a boundary, a band, or a distinct branch of the rule — 40 against 41 hours, below/at/above a threshold, one row per rate band — because each then discharges an obligation of its own. ROWS YOU THINK ARE MISSING ARE NEVER A FAILURE HERE — this assertion judges excess rows only, and coverage is judged by this eval's own coverage assertions. Judge every table in the response; your evidence must name each table with PASS or FAIL.
- ❌ **separates-pricing-trial-loyalty-refund**: Pricing/plans, trial eligibility, loyalty discount applicability, and cancellation/refund are in separate tables
  > "the loyalty discount lives inside pricing (it's an input to price, not a separate decision)" and Table 2 titled "Prices the Subscription Charge, Discounting Annual Plans for Loyalty Members" merges pricing and loyalty into one table instead of separating them.

### ⚠️ Eval eval-13-shipping-partial-applicability

**10/12** · 83506 tokens · 142280ms

- ✅ **produces-markdown-table**: Output contains a markdown table
- ✅ **express-split-by-uk-free-tier**: Express shipping is split into at least two cases distinguishing the UK free tier (UK, order £50+, free) from the paid tier. The split is driven by the UK-only free threshold rule.
- ✅ **express-uses-value-sets**: Express rows use value sets to consolidate cases with the same outcome — e.g. non-UK destinations at/above threshold in one row (such as {Ireland, Other} or {Ireland, International}), or UK at/above threshold in one row ({£50, £75}). Not enumerated as separate rows per destination or per order value when the result is identical. Any reasonable label for non-UK/Ireland destinations is acceptable (International, Other, Rest of World, etc.).
- ✅ **overnight-grouped**: Overnight UK and Ireland are in one row using a value set ({UK, Ireland}) — not separate rows, since both have the same cost and availability.
- ❌ **standard-destination-value-set-or-blank**: Standard shipping destination uses a value set covering all options (e.g. {UK, Ireland, International} or {UK, Ireland, Other}) or is blank/absent — either approach is valid since destination is irrelevant to the cost. Any reasonable label for non-UK/Ireland destinations is acceptable.
  > 'Standard, regardless of value or destination | Standard | {10, 500} | {UK, France} | | 3.99' omits Ireland from the value set
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'
- ✅ **concrete-domain-values**: Cell values use concrete domain terms (e.g. 'Standard', 'Express', 'UK', '£3.99') — not abstract codes or raw booleans like 'true', 'false', 'TYPE_1', '1'.
- ✅ **blank-vs-value-set-correct**: Value sets (not blanks) are used for irrelevant inputs — e.g. Standard shipping destination uses a value set like {UK, Ireland, International} because destination exists but doesn't affect the cost. Blanks are reserved for genuinely absent/N/A inputs. The distinction between 'irrelevant to this rule' (value set) and 'not applicable' (blank) is correct.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **no-duplicate-rows-within-a-table**: Judge each table in isolation. The assertion FAILS if a row re-covers an obligation an earlier row in the SAME table already discharged (e.g. a full region x speed x weight cross-product where one row per rate band would state the same rule). Judge each table against its concern's coverage obligations — the distinct behaviours the rule must demonstrate. Where the expected output states those obligations for a concern, judge against that list and do not invent a stricter one; where it does not, derive them from the concern itself. A row that discharges an obligation no other row reaches earns its place however simple it looks, and a value set that covers several values in one row is the preferred discharge, never a failure. Rows that walk one input up while everything else is held are excess when the third and later ones only re-show a direction the first two already established — 'standard applicant, no claims' at ages 30, 40 and 50, or a linear premium sampled at 0, 2 and 4 claims: two samples state the effect and the rest exercise the same arithmetic. They are NOT excess when each value is a boundary, a band, or a distinct branch of the rule — 40 against 41 hours, below/at/above a threshold, one row per rate band — because each then discharges an obligation of its own. ROWS YOU THINK ARE MISSING ARE NEVER A FAILURE HERE — this assertion judges excess rows only, and coverage is judged by this eval's own coverage assertions. Judge every table in the response; your evidence must name each table with PASS or FAIL.
- ❌ **separates-availability-and-cost**: Shipping method availability and shipping cost are in separate tables
  > Table 2 merges eligibility and cost: '| Destination | Order Value (£) | Cost (£)? | Rejection Reason? |'
- ✅ **blank-output-for-na**: When a shipping method is unavailable (e.g. Overnight outside UK/Ireland), the output clearly indicates non-applicability — either via a blank cost cell in a combined table, or by the availability table showing 'No'/unavailable and the cost table omitting those combinations entirely. Filler text like 'N/A', '-', or '0' is not used for unavailable outcomes.

### ⚠️ Eval eval-16-order-splitting

**11/19** · 95954 tokens · 268886ms

- ❌ **3.1a-concern-fulfillment-method**: Fulfillment method splitting is represented as its own table — items with different fulfillment types (store pickup vs home delivery) cannot share a shipment.
  > Table 1 combines fulfillment, address, and ship date into one table: 'Groups Order Lines into Shipment Buckets by Fulfillment, Address, and Ship Date' — no dedicated fulfillment table.
- ❌ **3.1b-concern-delivery-address**: Delivery address splitting is represented as its own table — items going to different addresses must be in separate shipments.
  > Delivery address is a column within the same combined Table 1, not its own table.
- ❌ **3.1c-concern-availability**: Availability splitting is represented as its own table — in-stock items ship immediately, backordered/pre-ordered items ship when available.
  > Availability (Expected Ship Date) is folded into Table 1 alongside fulfillment and address, not a separate table.
- ✅ **3.1d-concern-warehouse-allocation**: Warehouse allocation (minimising shipments) is represented as its own table — choosing which warehouses to ship from to minimise total shipment count.
- ✅ **3.1e-concern-companion-products**: Companion product grouping is represented as its own table — paired items (e.g. camera body and lens) should ship together from the same location when possible.
- ❌ **3.2-depth-fulfillment-scenarios**: Fulfillment table covers at least: all items for home delivery, all items for store pickup, and a mix of delivery and pickup items.
  > No dedicated fulfillment table exists; scenarios like 'Two store pickup items' are embedded in the combined Table 1.
- ❌ **3.3-depth-delivery-address-scenarios**: Delivery address table covers at least: all items to the same address, and items to different addresses.
  > No dedicated delivery-address table; address scenarios appear only inside combined Table 1.
- ❌ **3.4-depth-availability-scenarios**: Availability table covers at least: all items in stock, a mix of in-stock and pre-order/backorder, and two backordered/pre-ordered items with different dates. Pre-order and backorder are recognised as equivalent for splitting semantics — either by comment, note, or by including both with the same rules.
  > Row 'Two backordered delivery items restocking on the same date' uses same date, not different dates as required, and no dedicated table exists.
- ✅ **3.5-depth-warehouse-scenarios**: Warehouse allocation table covers at least: single warehouse has full quantity, multi-warehouse split needed, fewer-warehouse split preferred over more warehouses, and cannot fulfill (insufficient total stock).
- ❌ **3.6-depth-companion-scenarios**: Companion table covers at least: single warehouse has both companions, unavoidable split (no warehouse has both but both are available), and fulfillment not possible (one companion not available anywhere).
  > No row shows a companion item unavailable anywhere; all four Table 3 rows have both companions in stock somewhere.
- ✅ **3.7-readability-scenario-names**: Scenario names describe the business situation (e.g. 'Consolidate: two beats three', 'Everything at one warehouse') — not 'Test case 2'.
- ✅ **3.8-readability-item-property-mapping**: Trigger tables represent multi-item orders where each item has its own fulfillment type, address, and availability. The notation makes the item-to-property mapping unambiguous — not value sets like {delivery, pickup} which lose which item has which property.
- ✅ **3.9-readability-scalar-column**: Warehouse allocation table uses a scalar count column (e.g. 'Qty ordered') not item lists, when product identity doesn't matter for the logic.
- ✅ **3.10-readability-business-language**: Column names use business/domain language (e.g. 'Shipments?' not 'Result?', 'Warehouse stock' not 'inventory_map'). The tables read as a specification a product person could review.
- ✅ **3.11-readability-output-column-question-mark**: Output/expected columns end with '?' (e.g. 'Shipments?', 'Groups?') to distinguish them from input columns.
- ✅ **3.12-correctness-valid-assignments**: Inventory assignments are physically valid: items are only assigned to locations that have them. Shipment counts match the assignments shown.
- ✅ **3.13-depth-open-questions**: Multiple open questions are surfaced across the tables — ambiguities, edge cases, or product decisions that the requirements don't fully specify.
- ❌ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
  > Table 1 merges fulfillment, address, and ship-date splitting into one table: "Groups Order Lines into Shipment Buckets by Fulfillment, Address, and Ship Date" instead of three distinct concern tables.
- ✅ **no-duplicate-rows-within-a-table**: Judge each table in isolation. The assertion FAILS if a row re-covers an obligation an earlier row in the SAME table already discharged (e.g. a full region x speed x weight cross-product where one row per rate band would state the same rule). Judge each table against its concern's coverage obligations — the distinct behaviours the rule must demonstrate. Where the expected output states those obligations for a concern, judge against that list and do not invent a stricter one; where it does not, derive them from the concern itself. A row that discharges an obligation no other row reaches earns its place however simple it looks, and a value set that covers several values in one row is the preferred discharge, never a failure. Rows that walk one input up while everything else is held are excess when the third and later ones only re-show a direction the first two already established — 'standard applicant, no claims' at ages 30, 40 and 50, or a linear premium sampled at 0, 2 and 4 claims: two samples state the effect and the rest exercise the same arithmetic. They are NOT excess when each value is a boundary, a band, or a distinct branch of the rule — 40 against 41 hours, below/at/above a threshold, one row per rate band — because each then discharges an obligation of its own. ROWS YOU THINK ARE MISSING ARE NEVER A FAILURE HERE — this assertion judges excess rows only, and coverage is judged by this eval's own coverage assertions. Judge every table in the response; your evidence must name each table with PASS or FAIL.

### ⚠️ Eval eval-17-shopping-cart

**17/18** · 87531 tokens · 175938ms

- ✅ **4.1a-concern-item-operations**: Item operations (add/remove) are represented as their own table or tables — separate from coupon and cart total logic.
- ✅ **4.1b-concern-coupon-application**: Coupon application (entering, replacing, rejecting codes) is represented as its own table — separate from item operations and cart total calculation.
- ✅ **4.1c-concern-cart-total**: Cart total calculation (how different coupon types affect the price) is represented as its own table — separate from coupon application and item operations.
- ✅ **4.1d-concern-checkout**: Checkout (stock verification) is represented as its own table — separate from the other concerns.
- ✅ **4.3-rows-independently-executable**: Each table row is independently executable — no row depends on a prior row's result. The table expresses rules, not a sequential test script.
- ✅ **4.4-depth-item-operations**: Item operations cover at least: add to empty cart, add to non-empty cart, add more of an item already in cart, remove item from cart, remove last item in cart, remove item not in cart.
- ❌ **4.5-depth-coupon-application**: Coupon application table covers validity and replacement — not coupon types. Scenarios include at least: add valid coupon (none active), add valid coupon (another active), add expired coupon (none active), add expired coupon (another active), add nonexistent code (none active), add nonexistent code (another active).
  > Missing 'nonexistent code (another active)' scenario; only 'Unknown code is rejected' with none active is present
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
- ✅ **no-duplicate-rows-within-a-table**: Judge each table in isolation. The assertion FAILS if a row re-covers an obligation an earlier row in the SAME table already discharged (e.g. a full region x speed x weight cross-product where one row per rate band would state the same rule). Judge each table against its concern's coverage obligations — the distinct behaviours the rule must demonstrate. Where the expected output states those obligations for a concern, judge against that list and do not invent a stricter one; where it does not, derive them from the concern itself. A row that discharges an obligation no other row reaches earns its place however simple it looks, and a value set that covers several values in one row is the preferred discharge, never a failure. Rows that walk one input up while everything else is held are excess when the third and later ones only re-show a direction the first two already established — 'standard applicant, no claims' at ages 30, 40 and 50, or a linear premium sampled at 0, 2 and 4 claims: two samples state the effect and the rest exercise the same arithmetic. They are NOT excess when each value is a boundary, a band, or a distinct branch of the rule — 40 against 41 hours, below/at/above a threshold, one row per rate band — because each then discharges an obligation of its own. ROWS YOU THINK ARE MISSING ARE NEVER A FAILURE HERE — this assertion judges excess rows only, and coverage is judged by this eval's own coverage assertions. Judge every table in the response; your evidence must name each table with PASS or FAIL.

### ⚠️ Eval eval-21-event-registration-sbe

**12/13** · 77677 tokens · 83845ms

- ✅ **produces-markdown-table**: Output contains at least one markdown table (using | column | syntax)
- ✅ **validation-rules-covered**: Email validation and name-required scenarios are present — at least one row for invalid email and one for missing name.
- ✅ **blank-for-absent-optional**: Dietary requirements and/or accessibility needs use blank cells when the attendee has not provided them (genuinely absent) — not 'N/A' or 'none'.
- ✅ **output-column-has-question-mark**: At least one output column name ends with '?' (e.g. 'Accepted?', 'Price?')
- ✅ **scenario-names-describe-conditions**: Scenario names name the variation the row exercises, not the result it produces. The decidable test: FAILS when a scenario name states or paraphrases a value that appears in an expectation column of that same row — 'User cannot delete' beside an Allowed? cell of false, 'No discount applies' beside a Discount? cell of 0.00, 'Contractor gets no bonus' beside a Bonus? cell of 0. Also FAILS on generic labels ('Test 1', 'Test case 2'), which name no variation at all. PASSES otherwise. Naming the rule or the situation is correct even when it makes the outcome inferable — 'Delivery and pickup items always split', 'At the standard threshold, not above it', 'EU express, light package', 'Renewal with no claims', 'Missing name' all PASS. The failure this catches is a name echoing its own expectation cell, not a name that describes what the row is about, and not a name from which a reader who knows the rule could predict the outcome. A single offending name fails the assertion. Judge every table in the response.
- ✅ **open-question-surfaced**: The discount stacking ambiguity (early-bird + group) is surfaced as an open question — not silently resolved.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **no-duplicate-rows-within-a-table**: Judge each table in isolation. The assertion FAILS if a row re-covers an obligation an earlier row in the SAME table already discharged (e.g. a full region x speed x weight cross-product where one row per rate band would state the same rule). Judge each table against its concern's coverage obligations — the distinct behaviours the rule must demonstrate. Where the expected output states those obligations for a concern, judge against that list and do not invent a stricter one; where it does not, derive them from the concern itself. A row that discharges an obligation no other row reaches earns its place however simple it looks, and a value set that covers several values in one row is the preferred discharge, never a failure. Rows that walk one input up while everything else is held are excess when the third and later ones only re-show a direction the first two already established — 'standard applicant, no claims' at ages 30, 40 and 50, or a linear premium sampled at 0, 2 and 4 claims: two samples state the effect and the rest exercise the same arithmetic. They are NOT excess when each value is a boundary, a band, or a distinct branch of the rule — 40 against 41 hours, below/at/above a threshold, one row per rate band — because each then discharges an obligation of its own. ROWS YOU THINK ARE MISSING ARE NEVER A FAILURE HERE — this assertion judges excess rows only, and coverage is judged by this eval's own coverage assertions. Judge every table in the response; your evidence must name each table with PASS or FAIL.
- ✅ **separates-validation-and-pricing**: Input validation rules and pricing/discount calculation are in separate tables
- ❌ **descriptive-registration-date**: Registration date uses descriptive values (e.g. 'before cutoff', 'on cutoff', 'after cutoff') rather than raw dates — making the table readable without knowing the actual cutoff date.
  > Table2 'Registration Date' column uses raw dates like '2026-01-15', '2026-02-28', '2026-03-01', '2026-03-02' rather than descriptive labels
- ✅ **no-redundant-policy-columns**: If registration date uses descriptive values like 'before cutoff', the cutoff date is not also present as a separate column (it's already encoded in the description). Numeric policy thresholds like min group size are acceptable as columns since they are configuration values worth discussing with business experts.
- ✅ **group-size-policy-as-column**: The minimum group size threshold appears as a policy column (e.g. 'Min group size (policy)') — surfacing this as a configurable business rule, not just implied by the boundary rows.

### ⚠️ Eval eval-24-weekly-pay-sbe

**8/9** · 114632 tokens · 145968ms

- ✅ **produces-markdown-table**: Output contains at least one markdown table (using | column | syntax)
- ✅ **output-column-has-question-mark**: At least one output column name ends with '?' (e.g. 'Pay?', 'Rate?')
- ❌ **scenario-names-describe-conditions**: Scenario names name the variation the row exercises, not the result it produces. The decidable test: FAILS when a scenario name states or paraphrases a value that appears in an expectation column of that same row — 'User cannot delete' beside an Allowed? cell of false, 'No discount applies' beside a Discount? cell of 0.00, 'Contractor gets no bonus' beside a Bonus? cell of 0. Also FAILS on generic labels ('Test 1', 'Test case 2'), which name no variation at all. PASSES otherwise. Naming the rule or the situation is correct even when it makes the outcome inferable — 'Delivery and pickup items always split', 'At the standard threshold, not above it', 'EU express, light package', 'Renewal with no claims', 'Missing name' all PASS. The failure this catches is a name echoing its own expectation cell, not a name that describes what the row is about, and not a name from which a reader who knows the rule could predict the outcome. A single offending name fails the assertion. Judge every table in the response.
  > "Sum lands exactly at zero" and "Sum would go negative, floored at zero" directly restate the Total Weekly Pay?=0 outcome; "Positive rate accepted"/"rejected" restate the Valid? outcome.
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Weekday hours', 'Sunday hours', 'Hourly rate', 'Weekly pay?') — not code identifiers.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **overtime-boundary-covered**: The overtime threshold boundary (40 hours) is covered with at least a value at the threshold and one above.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **no-duplicate-rows-within-a-table**: Judge each table in isolation. The assertion FAILS if a row re-covers an obligation an earlier row in the SAME table already discharged (e.g. a full region x speed x weight cross-product where one row per rate band would state the same rule). Judge each table against its concern's coverage obligations — the distinct behaviours the rule must demonstrate. Where the expected output states those obligations for a concern, judge against that list and do not invent a stricter one; where it does not, derive them from the concern itself. A row that discharges an obligation no other row reaches earns its place however simple it looks, and a value set that covers several values in one row is the preferred discharge, never a failure. Rows that walk one input up while everything else is held are excess when the third and later ones only re-show a direction the first two already established — 'standard applicant, no claims' at ages 30, 40 and 50, or a linear premium sampled at 0, 2 and 4 claims: two samples state the effect and the rest exercise the same arithmetic. They are NOT excess when each value is a boundary, a band, or a distinct branch of the rule — 40 against 41 hours, below/at/above a threshold, one row per rate band — because each then discharges an obligation of its own. ROWS YOU THINK ARE MISSING ARE NEVER A FAILURE HERE — this assertion judges excess rows only, and coverage is judged by this eval's own coverage assertions. Judge every table in the response; your evidence must name each table with PASS or FAIL.
- ✅ **separates-classification-and-calculation**: Payable hours categorisation (1x regular, 1.5x overtime, 2x Sunday/holiday) and pay calculation (hours × rate) are in separate tables

## Variant vs Official (iterations 2, 1 merged)

### Per-Eval Resource Comparison

| Eval | Pass (off) | Pass (var) | Tokens (off) | Tokens (var) | Tok Δ | Cost (off) | Cost (var) | Cost Δ | Time (off) | Time (var) | Time Δ |
|------|------------|------------|--------------|--------------|-------|------------|------------|--------|------------|------------|--------|
| eval-4-loan-approval | 10/13 | 12/13 | 66599 | 88605 | +33% | $0.1894 | $0.4567 | +141% | 43.0s | 214.4s | +398% |
| eval-5-order-transitions | 9/10 | 10/10 | 67003 | 83795 | +25% | $0.1549 | $0.3825 | +147% | 39.0s | 139.1s | +256% |
| eval-6-discount-interaction | 6/7 | 7/7 | 106994 | 78752 | -26% | $0.2375 | $0.3080 | +30% | 58.6s | 97.9s | +67% |
| eval-10-subscription-billing | 15/16 | 14/16 | 74488 | 80895 | +9% | $0.3032 | $0.3372 | +11% | 114.2s | 112.5s | -1% |
| eval-12-subscription-loyalty-trial | 12/13 | 11/13 | 75075 | 84473 | +13% | $0.2686 | $0.3425 | +27% | 93.8s | 139.1s | +48% |
| eval-13-shipping-partial-applicability | 8/12 | 10/12 | 69806 | 83506 | +20% | $0.1908 | $0.3358 | +76% | 67.8s | 142.3s | +110% |
| eval-16-order-splitting | 11/19 | 11/19 | 74397 | 95954 | +29% | $0.2583 | $0.5144 | +99% | 95.2s | 268.9s | +183% |
| eval-17-shopping-cart | 17/18 | 17/18 | 75612 | 87531 | +16% | $0.2763 | $0.3867 | +40% | 79.2s | 175.9s | +122% |
| eval-21-event-registration-sbe | 10/13 | 12/13 | 74974 | 77677 | +4% | $0.2724 | $0.2518 | -8% | 121.5s | 83.8s | -31% |
| eval-24-weekly-pay-sbe | 7/9 | 8/9 | 70907 | 114632 | +62% | $0.2140 | $0.3720 | +74% | 80.8s | 146.0s | +81% |
| **Totals (10 comparable)** | **105/130** | **112/130** | **755855** | **875820** | **+16%** | **$2.3654** | **$3.6875** | **+56%** | **793.3s** | **1519.9s** | **+92%** |

**Comparable summary (10 evals in both):**

| Source | Pass Rate | Tokens | Cost | Time |
|--------|-----------|--------|------|------|
| next (iter 1) | 112/130 (86.2%) | 875820 | $3.6875 | 1519.9s |
| official | 105/130 (80.8%) | 755855 | $2.3654 | 793.3s |
| **Δ** | | **+16%** | **+56%** | **+92%** |

### Per-Assertion Comparison

| Eval | Assertion | official | next |
|------|-----------|----------|------|
| eval-4-loan-approval | scenario-names-describe-conditions | ❌ | ✅ |
| eval-4-loan-approval | concerns-decomposed | ❌ | ✅ |
| eval-5-order-transitions | no-duplicate-rows-within-a-table | ❌ | ✅ |
| eval-6-discount-interaction | extreme-discount-row | ❌ | ✅ |
| eval-10-subscription-billing | refund-table-shows-proportion | ✅ | ❌ |
| eval-12-subscription-loyalty-trial | question-mark-only-on-outputs | ✅ | ❌ |
| eval-13-shipping-partial-applicability | express-uses-value-sets | ❌ | ✅ |
| eval-13-shipping-partial-applicability | overnight-grouped | ❌ | ✅ |
| eval-13-shipping-partial-applicability | no-duplicate-rows-within-a-table | ❌ | ✅ |
| eval-13-shipping-partial-applicability | separates-availability-and-cost | ✅ | ❌ |
| eval-16-order-splitting | 3.5-depth-warehouse-scenarios | ❌ | ✅ |
| eval-16-order-splitting | concerns-decomposed | ✅ | ❌ |
| eval-21-event-registration-sbe | blank-for-absent-optional | ❌ | ✅ |
| eval-21-event-registration-sbe | scenario-names-describe-conditions | ❌ | ✅ |
| eval-24-weekly-pay-sbe | scenario-names-describe-conditions | ✅ | ❌ |
| eval-24-weekly-pay-sbe | no-duplicate-rows-within-a-table | ❌ | ✅ |
| eval-24-weekly-pay-sbe | separates-classification-and-calculation | ❌ | ✅ |

### Load-Bearing Assertions

Assertions that pass with official skill but fail with next variant:

- eval-10-subscription-billing: `refund-table-shows-proportion`
- eval-12-subscription-loyalty-trial: `question-mark-only-on-outputs`
- eval-13-shipping-partial-applicability: `separates-availability-and-cost`
- eval-16-order-splitting: `concerns-decomposed`
- eval-24-weekly-pay-sbe: `scenario-names-describe-conditions`

### Attention Needed

Evals with failed assertions or disproportionate resource usage (>2x official):

| Eval | Issue | Details |
|------|-------|---------|
| eval-4-loan-approval | High cost | $0.4567 vs $0.1894 (+141%) |
| eval-5-order-transitions | High cost | $0.3825 vs $0.1549 (+147%) |
| eval-10-subscription-billing | Failed assertions | `refund-table-shows-proportion` |
| eval-12-subscription-loyalty-trial | Failed assertions | `question-mark-only-on-outputs` |
| eval-13-shipping-partial-applicability | Failed assertions | `separates-availability-and-cost` |
| eval-16-order-splitting | Failed assertions | `concerns-decomposed` |
| eval-24-weekly-pay-sbe | Failed assertions | `scenario-names-describe-conditions` |

