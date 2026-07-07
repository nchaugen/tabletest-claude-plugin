# Eval Review — tabletest variant=next, Iteration 3

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-haiku-4-5 · **Date:** 2026-07-07 · **Evals:** 5

## Summary

90/103 (87.4%) · 9190932 tokens · 1916.3s · $6.3860

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

## Delta vs Iteration 2

**Regressions (2):**
- ❌ eval-15-reis-discount: `2.21-readability-relative-time`
- ❌ eval-22-event-registration-tt: `descriptive-registration-date`

**Improvements (6):**
- ✅ eval-14-weekly-pay: `1.15-format-clean-method`
- ✅ eval-15-reis-discount: `2.9-correctness-value-set-tier-semantics`
- ✅ eval-15-reis-discount: `2.15-ticket-count-uses-value-sets`
- ✅ eval-15-reis-discount: `2.20-readability-one-row-per-tier`
- ✅ eval-15-reis-discount: `minimal-rows-per-concern`
- ✅ eval-30-order-splitting-tt: `scalar-quantity-for-warehouse`

## Resource Comparison vs Iteration 2

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-14-weekly-pay | 19/20 | 18/20 | 926010 | 660607 | 292.6 | 230.6 |
| eval-15-reis-discount | 18/20 | 15/20 | 2030862 | 903931 | 535.6 | 353.1 |
| eval-22-event-registration-tt | 20/25 | 21/25 | 744418 | 696253 | 186.9 | 119.3 |
| eval-23-loan-approval-tt | 13/18 | 13/18 | 801240 | 660004 | 188.9 | 167.4 |
| eval-30-order-splitting-tt | 20/20 | 19/20 | 4688402 | 1847744 | 712.3 | 798.0 |

## Per-Eval Results

### ⚠️ Eval eval-14-weekly-pay

**19/20** · 926010 tokens · 292630ms

- ✅ **1.2-error-has-expected-column**: The error/rejection table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
- ✅ **1.3-depth-overtime-boundary**: Overtime threshold boundary is covered: at least a value at the threshold (40) and just above (41 or similar). Not just values well below and well above.
- ✅ **1.4-depth-combined-scenario**: A combined scenario is present: a row with weekday + Sunday + holiday hours, testing the composition of all hour types.
- ✅ **1.5-depth-error-edge-cases**: Error/edge cases covered: at least negative hours (floored at zero) and negative or missing rate (rejected).
- ❌ **1.6-readability-empty-cells**: Sunday and Holiday hour columns use empty cells (not 0) when those inputs are not relevant to a scenario — making it easier to spot which rows involve weekend/holiday hours. This is a readability preference; parameter types should be Integer (not int) to support null from blank cells.
  > Sunday/Holiday columns use explicit 0 values (e.g. 'No hours worked | 0 | 40 | 0 | 0'), not empty cells. Parameters are BigDecimal, not nullable Integer.
- ✅ **1.7-readability-scenario-names**: Scenario names describe work patterns (e.g. 'Part-time', 'Five hours overtime', 'Full Sunday shift') — not 'Test case 1' or expected outcomes.
- ✅ **1.8-correctness-expected-values**: Expected values in intermediate and total columns are arithmetically correct for every row, consistent with the formulas in @Description.
- ✅ **1.9-correctness-value-set-semantics**: Value sets are only used where all values in the set produce the same expected result (e.g. any rate × 0 = 0). Not used where results would differ.
- ✅ **1.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ✅ **1.11-format-description**: @Description is present on at least the pay calculation method and provides context beyond what the table rows already express — such as the overtime threshold (40 hours), premium multipliers (Sunday 2×, Holiday 2.5×), or other fixed assumptions not visible in the table.
- ✅ **1.12-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **1.13-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **1.14-depth-zero-rate**: A row where the hourly rate is zero, showing that pay is zero regardless of hours worked. This is a distinct edge case from negative rate (which is rejected).
- ✅ **1.15-format-clean-method**: The test method body contains no if/ternary null-handling or parsing logic. Null-to-default conversion (e.g. blank Sunday hours → 0) is handled via a @TypeConverter method or a private helper, keeping the test method limited to arrange/act/assert.
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Weekday hours', 'Sunday hours', 'Hourly rate', 'Weekly pay?') — not implementation terms like 'weekdayHrs', 'int', 'param1'.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ✅ **separates-classification-and-calculation**: Payable hours categorisation (1x regular, 1.5x overtime, 2x Sunday/holiday) and pay calculation (hours × rate) are in separate @TableTest methods
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-15-reis-discount

**18/20** · 2030862 tokens · 535562ms

- ✅ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
- ✅ **2.2-children-flat-discount**: Children's flat 20% discount is represented — either as a row in the eligibility table or a separate note. It does not follow the ladder.
- ✅ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers derived from the 'every fifth trip' rule, with boundary values or value sets showing which ticket counts produce which discount. The maximum 40% discount is represented.
- ✅ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
- ✅ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
- ✅ **2.15-ticket-count-uses-value-sets**: The discount ladder table uses value sets for the ticket count column to group counts within the same tier (e.g. {5, 6, 7, 8, 9} or {5, 9} for min/max → 5%) — not one row per boundary value. The 'every fifth trip' rule creates tiers; the table should express tiers, not enumerate boundaries.
- ✅ **2.16-no-duplicate-tier-mapping**: The tier-to-discount mapping is expressed once — not repeated across multiple tables. A price application table (if present) should test the arithmetic (base price × discount → final price) without re-enumerating which ticket counts map to which tier.
- ✅ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ✅ **2.11-format-description**: @Description is present on at least the class or the tier method and provides context beyond what the table rows already express — such as the discount ladder structure, eligibility rules, or application context. Does NOT merely restate the column names or summarise what the rows show.
- ✅ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **2.14-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **2.17-zone-irrelevance-visible**: The eligibility/passenger-type table includes a Zone column (or equivalent) with a value set to show that the discount applies regardless of zone — rather than silently omitting zone. The requirement explicitly says 'regardless of the zones you travel in'.
- ✅ **2.18-adult-senior-value-set**: Adult and senior are expressed as a value set {ADULT, SENIOR} (or equivalent) in at least one row of the eligibility table, since they follow identical discount rules. Not enumerated as separate rows with the same outcome.
- ❌ **2.19-depth-all-tiers**: All 9 discount tiers are represented in the ladder table: 0% (tickets 1-4), 5% (5-9), 10% (10-14), 15% (15-19), 20% (20-24), 25% (25-29), 30% (30-34), 35% (35-39), and 40% (40+). Not just a subset of tiers.
  > ReisDiscountLadderTest shows only 0%, 5%, 10%, 35%, 40%. Missing tiers: 15%, 20%, 25%, 30%.
- ✅ **2.20-readability-one-row-per-tier**: Each tier in the discount ladder is expressed as a single row with a value set for ticket counts — not split across multiple rows (e.g. separate rows for 'tier kicks in' and 'tier holds'). One tier = one row.
- ❌ **2.21-readability-relative-time**: The rolling window table expresses time relatively (e.g. '14 days ago', '30 days ago', or an integer 'Days ago' column) rather than using absolute dates, making the table readable without needing to know the reference date.
  > TripNumberCalculatorTest uses absolute dates: '2026-06-15T00:00:00', '2026-05-16T00:00:00', '2026-05-15T00:00:00', not relative time like '30 days ago'
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-22-event-registration-tt

**20/25** · 744418 tokens · 186872ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **validation-rules-covered**: Email validation and name-required error scenarios are present — at least one row for invalid email and one for missing name.
- ✅ **blank-for-absent-optional**: Dietary requirements and/or accessibility needs use blank cells (null) when not provided — not 'N/A' or 'none'.
- ✅ **blank-vs-value-set-correct**: When testing pricing rules, irrelevant inputs use value sets or representative values — not blanks.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Result?', 'Price?')
- ✅ **business-language-columns**: Column names use domain/business language — not code identifiers.
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions — not outcomes.
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **concerns-decomposed**: Multiple @TableTest methods address distinct concerns — not one monolithic table.
- ✅ **minimal-rows-per-concern**: Each table has only rows needed to express its concern's rules — no unnecessary permutations.
- ✅ **separates-validation-and-pricing**: Input validation and pricing/discount calculation are in separate @TableTest methods.
- ✅ **validation-includes-optional-fields**: Validation @TableTest includes Dietary Requirements and Accessibility Needs columns, with rows showing both null and non-null values are accepted.
- ❌ **descriptive-registration-date**: Registration date uses descriptive values with @TypeConverter — not raw date literals.
  > shouldApplyEarlyBirdDiscount and other pricing tables use raw date literals like '2025-01-15', '2025-02-28' instead of descriptive values like 'before cutoff'
- ✅ **cutoff-date-column-if-literal-dates**: If registration dates are literal, cutoff date appears as a separate policy column so reader can verify the comparison.
- ❌ **description-no-irrelevant-information**: @Description does not include information already visible in columns or derivable from table structure.
  > @Description states 'Base price is £100' and 'group size is fixed at 1' which are implementation details not visible in table; 'Group threshold: 5' appears in shouldApplyGroupDiscount description when it's in the table
- ❌ **discount-column-preferred**: Output column is 'Discount?' rather than 'Price?' — or if 'Price?' is used, both 'Base price' and 'Price?' must be present.
  > Tables use both 'Discount?' and 'Price?' columns but no 'Base price' column; reader cannot trace Price calculation without external knowledge
- ❌ **single-assertion-in-method**: Each @TableTest method has a single, uniform assertion pattern applied to all rows — not different assertions per scenario. Validation checks one thing (e.g. error message or acceptance status); pricing checks one thing (e.g. discount or price). No if/switch in assertion logic.
  > Method has 2 assertions; Method has 2 assertions; Method has 2 assertions; Method has 2 assertions
- ✅ **optional-fields-has-expected-column**: The optional-fields acceptance table includes an expectation/output column asserting acceptance.
- ❌ **description-no-redundant-field-values**: Scenario descriptions do not include specific field values already visible in the table.
  > shouldResolveDiscountWhenBothApply row 'Both apply, early-bird wins regardless of group size' includes 'Registration Date: 2025-01-15' and 'Group Size: 5, 10' in scenario name; these values are visible in table columns
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-23-loan-approval-tt

**13/18** · 801240 tokens · 188935ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **threshold-as-column**: Policy thresholds (650/600 credit scores) appear in a dedicated policy column (e.g. 'Credit threshold') separate from the applicant's actual credit score column. The reader sees both the threshold and the score, making the comparison explicit. Not just threshold values used as data in the score column.
  > The table has 'Credit Score' as a single column with concrete values (700, 650, 651, 600). No separate 'Credit threshold' column exists; thresholds are only mentioned in @Description text, not visible in table columns.
- ✅ **concrete-domain-values**: Cell values use concrete domain terms (e.g. '70', '650', 'APPROVED') — not abstract codes like 'CATEGORY_A' or '1'. Boolean true/false is acceptable for yes/no flags when the parameter type is boolean.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Senior at lower threshold', 'Missing income') — not outcomes ('Approved', 'Rejected').
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Age', 'Credit score', 'Stable income', 'Decision?') — not code identifiers like 'customerAge', 'boolean'.
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Decision?' or 'Status?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
  > Only one @TableTest method 'evaluatesLoanApproval' exists. Age boundary, credit score categorisation, and income status are all combined in a single 12-row table rather than separated.
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
  > The single table has 12 rows testing age boundary (rows 8-11), credit score thresholds (rows 1-5), and income effects (rows 5-7, 12) simultaneously, creating unnecessary permutations.
- ❌ **separates-age-credit-income**: Age boundary policy, credit score categorisation, and income status are separated into distinct @TableTest methods, with a method combining these for the expected verdict
  > Only one @TableTest method exists. No separate methods for age boundary, credit score categorisation, or income status; all combined in 'evaluatesLoanApproval'.
- ❌ **description-no-redundant-field-values**: Scenario descriptions and @Description text do not include specific field values that are already visible in the table or that serve as 'any valid value' placeholders. Stating 'Customer age is 35 for all rows' in @Description when age is fixed in the method body, or including fixed values in scenario names that are already columns, adds maintenance burden without value. Describe the scenario condition or policy being tested instead.
  > @Description includes 'standard applicants (under 65)' and 'senior applicants (65+)' — policy-relevant groupings already visible in age column rows. Scenario 'At senior age boundary, lower threshold applies' redundantly mentions the threshold when it's tested in the table values.
- ✅ **depth-stable-income-effect**: The standardApplicantCreditThreshold table (or equivalent non-senior table) clearly documents the effect of stable income: rows show that above-threshold credit score + stable income = APPROVED, and above-threshold credit score + no stable income = REJECTED. The stable income parameter's effect on the outcome must be visible from the table rows, not just implied.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ✅ Eval eval-30-order-splitting-tt

**20/20** · 4688402 tokens · 712293ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **concerns-decomposed**: Multiple @TableTest methods are used, each addressing a distinct concern — not one monolithic table mixing all splitting rules.
- ✅ **minimal-rows-per-concern**: Each @TableTest has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.
- ✅ **concern-fulfillment-method**: Fulfillment method splitting is represented as its own @TableTest — items with different fulfillment types (store pickup vs home delivery) cannot share a shipment.
- ✅ **concern-delivery-address**: Delivery address splitting is represented as its own @TableTest — items going to different addresses must be in separate shipments.
- ✅ **concern-availability**: Availability splitting is represented as its own @TableTest — in-stock items ship immediately, backordered/pre-ordered items ship when available.
- ✅ **concern-warehouse-allocation**: Warehouse allocation (minimising shipments) is represented as its own @TableTest — choosing which warehouses to ship from to minimise total shipment count.
- ✅ **concern-companion-products**: Companion product grouping is represented as its own @TableTest — paired items (e.g. camera body and lens) should ship together from the same location when possible.
- ✅ **all-outputs-same-table**: Each @TableTest method includes all output columns for its concern in the same table — e.g. warehouse allocation includes both the assignment and shipment count, not split across methods. All outputs of the same concern belong together.
- ✅ **scalar-quantity-for-warehouse**: Warehouse allocation table uses scalar quantity columns (e.g. 'Qty ordered') not item lists, when product identity doesn't matter for the logic.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Shipments?', 'Groups?')
- ✅ **scenario-names-describe-conditions**: Scenario names describe the business situation (e.g. 'Consolidate: two beats three', 'Everything at one warehouse') — not 'Test case 2'.
- ✅ **business-language-columns**: Column names use business/domain language (e.g. 'Shipments?' not 'Result?', 'Warehouse stock' not 'inventory_map'). The tables read as a specification a product person could review.
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

## Variant vs Official (iterations 38, 37, 35 merged)

### Per-Eval Resource Comparison

| Eval | Pass (off) | Pass (var) | Tokens (off) | Tokens (var) | Tok Δ | Cost (off) | Cost (var) | Cost Δ | Time (off) | Time (var) | Time Δ |
|------|------------|------------|--------------|--------------|-------|------------|------------|--------|------------|------------|--------|
| eval-14-weekly-pay | 16/20 | 19/20 | 700566 | 926010 | +32% | $0.5940 | $0.8397 | +41% | 171.1s | 292.6s | +71% |
| eval-15-reis-discount | 12/20 | 18/20 | 3752644 | 2030862 | -46% | $2.1646 | $1.6833 | -22% | 553.4s | 535.6s | -3% |
| eval-22-event-registration-tt | 22/25 | 20/25 | 490058 | 744418 | +52% | $0.4791 | $0.6102 | +27% | 141.8s | 186.9s | +32% |
| eval-23-loan-approval-tt | 13/18 | 13/18 | 743981 | 801240 | +8% | $0.6012 | $0.5773 | -4% | 171.0s | 188.9s | +11% |
| eval-30-order-splitting-tt | 19/20 | 20/20 | 967814 | 4688402 | +384% | $1.0599 | $2.6755 | +152% | 366.7s | 712.3s | +94% |
| **Totals (5 comparable)** | **82/103** | **90/103** | **6655063** | **9190932** | **+38%** | **$4.8988** | **$6.3860** | **+30%** | **1404.0s** | **1916.3s** | **+36%** |

**Comparable summary (5 evals in both):**

| Source | Pass Rate | Tokens | Cost | Time |
|--------|-----------|--------|------|------|
| next (iter 3) | 90/103 (87.4%) | 9190932 | $6.3860 | 1916.3s |
| official | 82/103 (79.6%) | 6655063 | $4.8988 | 1404.0s |
| **Δ** | | **+38%** | **+30%** | **+36%** |

### Per-Assertion Comparison

| Eval | Assertion | official | next |
|------|-----------|----------|------|
| eval-14-weekly-pay | 1.11-format-description | ❌ | ✅ |
| eval-14-weekly-pay | 1.15-format-clean-method | ❌ | ✅ |
| eval-14-weekly-pay | separates-classification-and-calculation | ❌ | ✅ |
| eval-15-reis-discount | 2.4-depth-rolling-window-boundary | ❌ | ✅ |
| eval-15-reis-discount | 2.9-correctness-value-set-tier-semantics | ❌ | ✅ |
| eval-15-reis-discount | 2.15-ticket-count-uses-value-sets | ❌ | ✅ |
| eval-15-reis-discount | 2.17-zone-irrelevance-visible | ❌ | ✅ |
| eval-15-reis-discount | 2.18-adult-senior-value-set | ❌ | ✅ |
| eval-15-reis-discount | 2.20-readability-one-row-per-tier | ❌ | ✅ |
| eval-22-event-registration-tt | descriptive-registration-date | ✅ | ❌ |
| eval-22-event-registration-tt | description-no-redundant-field-values | ✅ | ❌ |
| eval-30-order-splitting-tt | scalar-quantity-for-warehouse | ❌ | ✅ |

### Load-Bearing Assertions

Assertions that pass with official skill but fail with next variant:

- eval-22-event-registration-tt: `descriptive-registration-date`
- eval-22-event-registration-tt: `description-no-redundant-field-values`

### Attention Needed

Evals with failed assertions or disproportionate resource usage (>2x official):

| Eval | Issue | Details |
|------|-------|---------|
| eval-30-order-splitting-tt | High tokens | 4688402 vs 967814 (+384%) |
| eval-30-order-splitting-tt | High cost | $2.6755 vs $1.0599 (+152%) |
| eval-22-event-registration-tt | Failed assertions | `descriptive-registration-date`, `description-no-redundant-field-values` |

