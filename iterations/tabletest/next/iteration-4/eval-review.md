# Eval Review — tabletest variant=next, Iteration 4

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-haiku-4-5 · **Date:** 2026-07-23 · **Evals:** 5

## Summary

103/121 (85.1%) · 4531212 tokens · 1096.2s · $3.7212

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

## Delta vs Iteration 3

**Regressions (5):**
- ❌ eval-22-event-registration-tt: `discount-column-preferred`
- ❌ eval-22-event-registration-tt: `description-no-redundant-field-values`
- ❌ eval-22-event-registration-tt: `rule-statable-from-table`
- ❌ eval-23-loan-approval-tt: `scenario-names-describe-conditions`
- ❌ eval-23-loan-approval-tt: `description-no-redundant-field-values`

## Resource Comparison vs Iteration 3

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-15-reis-discount | 19/23 | — | 1314175 | — | 306.3 | — |
| eval-18-convert-from-code | 20/24 | — | 840562 | — | 236.3 | — |
| eval-22-event-registration-tt | 22/27 | 25/27 | 672731 | 771866 | 137.3 | 176.1 |
| eval-23-loan-approval-tt | 18/21 | 20/21 | 701022 | 787298 | 120.7 | 197.6 |
| eval-29-shopping-cart-tt | 24/26 | 24/26 | 1002722 | 852532 | 295.7 | 289.9 |

## Per-Eval Results

### ⚠️ Eval eval-15-reis-discount

**19/23** · 1314175 tokens · 306252ms

- ✅ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
- ✅ **2.2-children-flat-discount**: Children's flat 20% discount is represented — either as a row in the eligibility table or a separate note. It does not follow the ladder.
- ✅ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers derived from the 'every fifth trip' rule, with boundary values or value sets showing which ticket counts produce which discount. The maximum 40% discount is represented.
- ✅ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
- ❌ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
  > ReisDiscountLadderTest uses single Travel Count values per row (1, 4, 5, 7, 10, 15, 39, 40, 45, 100), not value sets grouping counts within tiers
- ❌ **2.15-ticket-count-uses-value-sets**: The discount ladder table uses value sets for the ticket count column to group counts within the same tier (e.g. {5, 6, 7, 8, 9} or {5, 9} for min/max → 5%) — not one row per boundary value. The 'every fifth trip' rule creates tiers; the table should express tiers, not enumerate boundaries.
  > ReisDiscountLadderTest has separate rows for each boundary (5, 7, 10, 15, 39, 40, 45, 100) rather than value sets like {5,6,7,8,9}→5%
- ✅ **2.16-no-duplicate-tier-mapping**: The tier-to-discount mapping is expressed once — not repeated across multiple tables. A price application table (if present) should test the arithmetic (base price × discount → final price) without re-enumerating which ticket counts map to which tier.
- ✅ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ✅ **2.11-format-description**: @Description is present on at least the class or the tier method and provides context beyond what the table rows already express — such as the discount ladder structure, eligibility rules, or application context. Does NOT merely restate the column names or summarise what the rows show.
- ✅ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **2.14-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **2.17-zone-irrelevance-visible**: The eligibility/passenger-type table includes a Zone column (or equivalent) with a value set to show that the discount applies regardless of zone — rather than silently omitting zone. The requirement explicitly says 'regardless of the zones you travel in'.
- ❌ **2.18-adult-senior-value-set**: Adult and senior are expressed as a value set {ADULT, SENIOR} (or equivalent) in at least one row of the eligibility table, since they follow identical discount rules. Not enumerated as separate rows with the same outcome.
  > DiscountCalculatorTest has separate rows for ADULT and SENIOR with identical outcomes (both reach 5% at first rung), not combined as {ADULT, SENIOR}
- ✅ **2.19-depth-all-tiers**: All 9 discount tiers are represented in the ladder table: 0% (tickets 1-4), 5% (5-9), 10% (10-14), 15% (15-19), 20% (20-24), 25% (25-29), 30% (30-34), 35% (35-39), and 40% (40+). Not just a subset of tiers.
- ✅ **2.20-readability-one-row-per-tier**: Each tier in the discount ladder is expressed as a single row with a value set for ticket counts — not split across multiple rows (e.g. separate rows for 'tier kicks in' and 'tier holds'). One tier = one row.
- ✅ **2.21-readability-relative-time**: The rolling window table expresses time relatively (e.g. '14 days ago', '30 days ago', or an integer 'Days ago' column) rather than using absolute dates, making the table readable without needing to know the reference date.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. Judge each @TableTest table independently; the assertion FAILS if any table meets any of these conditions: (1) its expectation column holds the same value in every row; (2) in every row, each expectation cell is a verbatim copy of a cell in an input column of the same row; (3) an implementation that ignored the input columns entirely and returned a fixed value would satisfy every row. Otherwise the assertion PASSES. Do not fail a table merely because its values are simple or round — only these three conditions matter.
- ❌ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body. The assertion FAILS if any of these hold for any @TableTest: (1) a value needed to predict the expectation (a threshold, rate, cutoff, multiplier, reference date) appears only in the method body or a field, and in neither a column, the @DisplayName, nor the @Description; (2) the operation applied to the inputs cannot be named from the column headers and cell values alone; (3) a helper method used in the assertion applies a comparison criterion (ordering, tolerance, subset matching) that is stated nowhere in the table, title, or description. Otherwise the assertion PASSES. Setup that merely constructs the objects named by the columns is not a failure.
  > ReisTravelCountTest @Description states '30 days' window but table rows (SINGLE@30 vs SINGLE@31) do not explicitly label the boundary; reader must infer from scenario text. ReisDiscountLadderTest: '5% every 5th ticket, starting at ticket 5' stated in @Description, not in column headers or table values alone.
- ✅ **title-states-system-behaviour**: Each @DisplayName (or the method name it falls back to) states what the code under test does, not an external fact it depends on. Apply the check mechanically: strike the system under test from the sentence — if it still reads as true, because it merely restates a regulation, standard, format, or domain fact, the title is about something else and fails.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-18-convert-from-code

**20/24** · 840562 tokens · 236287ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **black-box-columns**: Table columns represent the method's public inputs (applicant type, age, claim count) and observable outputs (decision, premium) — not internal state like hasActivePolicy, internalRiskScore, or calculateRiskScore.
- ✅ **observable-io-only**: No column is named after private fields or internal variables (hasActivePolicy, internalRiskScore, riskScore). The test treats the method as a black box.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Applicant type', 'Age', 'Claims', 'Decision?', 'Premium?') — not code identifiers like 'applicantType', 'claimCount', 'String'.
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Renewal with no claims', 'Senior applicant') — not outcomes ('Auto approved', 'Rejected').
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Decision?' or 'Premium?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ❌ **separates-decision-and-premium**: Decision logic (AUTO_APPROVED/REJECTED/APPROVED) and premium calculation are in separate @TableTest methods
  > All three @TableTest methods include both 'Decision?' and 'Premium?' columns. No separation of decision-only vs. premium-only tables.
- ✅ **no-reimplemented-internals**: Test method body does not recompute internal formulas (e.g. risk score calculation). It only calls the public API (evaluateApplication) and asserts on the return value's fields.
- ❌ **description-no-internals**: @Description explains the test's purpose and business context — not internal formulas or implementation details.
  > @Description in rejectsApplicationsAboveRiskThreshold states 'Risk score = (age / 10) + (claimCount * 15)' and 'rejected once this score exceeds 75' — explicitly documents internal formula.
- ❌ **depth-decision-boundaries**: Approval decision table covers the rejection boundary thoroughly: 5 claims triggers rejection regardless of realistic age (risk score crosses 75), 4 claims results in approval. Rows at 4 and 5 claims with varying ages demonstrate this cliff.
  > rejectsApplicationsAboveRiskThreshold table has only 2 rows with 5 claims (age 0 APPROVED, age 10 REJECTED). No row with 4 claims to show the 4-vs-5 cliff boundary explicitly.
- ✅ **depth-premium-boundaries**: Approved premium table covers boundary effects: the premium jump from 0 to 1 claim (impact of +15 risk score on premium), and the premium difference at age 64 vs 65 (standard vs senior formula). These boundary conditions document how premium changes at each threshold.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. Judge each @TableTest table independently; the assertion FAILS if any table meets any of these conditions: (1) its expectation column holds the same value in every row; (2) in every row, each expectation cell is a verbatim copy of a cell in an input column of the same row; (3) an implementation that ignored the input columns entirely and returned a fixed value would satisfy every row. Otherwise the assertion PASSES. Do not fail a table merely because its values are simple or round — only these three conditions matter.
- ❌ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body. The assertion FAILS if any of these hold for any @TableTest: (1) a value needed to predict the expectation (a threshold, rate, cutoff, multiplier, reference date) appears only in the method body or a field, and in neither a column, the @DisplayName, nor the @Description; (2) the operation applied to the inputs cannot be named from the column headers and cell values alone; (3) a helper method used in the assertion applies a comparison criterion (ordering, tolerance, subset matching) that is stated nowhere in the table, title, or description. Otherwise the assertion PASSES. Setup that merely constructs the objects named by the columns is not a failure.
  > @Description states 'Risk score = (age / 10) + (claimCount * 15)' and 'threshold...exceeds 75' but these formulas and the 75 threshold appear only in @Description/method body, not in column headers or cells. A reader cannot derive the rule from table structure alone.
- ✅ **title-states-system-behaviour**: Each @DisplayName (or the method name it falls back to) states what the code under test does, not an external fact it depends on. Apply the check mechanically: strike the system under test from the sentence — if it still reads as true, because it merely restates a regulation, standard, format, or domain fact, the title is about something else and fails.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against InsuranceEvaluator

### ⚠️ Eval eval-22-event-registration-tt

**22/27** · 672731 tokens · 137261ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **validation-rules-covered**: Email validation and name-required error scenarios are present — at least one row for invalid email and one for missing name.
- ✅ **blank-for-absent-optional**: Dietary requirements and/or accessibility needs use blank cells (null) when not provided — not 'N/A' or 'none'. These are genuinely optional and absent.
- ✅ **blank-vs-value-set-correct**: When testing pricing rules, irrelevant inputs (e.g. dietary requirements don't affect price) use value sets or representative values — not blanks (which mean null). Blanks are reserved for genuinely absent/null inputs.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Result?', 'Price?')
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Name', 'Email', 'Registration date', 'Group size', 'Price?') — not code identifiers.
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Early-bird single attendee', 'Missing name') — not outcomes ('Rejected', '20% off').
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ✅ **separates-validation-and-pricing**: Input validation and pricing/discount calculation are in separate @TableTest methods
- ✅ **validation-includes-optional-fields**: Validation @TableTest includes Dietary Requirements and Accessibility Needs columns, with rows showing both null (blank) and non-null values are accepted — not hardcoded as null in the method body.
- ✅ **descriptive-registration-date**: Registration date uses descriptive values (e.g. 'before cutoff', 'on cutoff', 'after cutoff') with a @TypeConverter method converting to actual dates — not raw date literals like '2025-02-28'. Readability over precision.
- ✅ **cutoff-date-column-if-literal-dates**: If registration dates are literal (e.g. '2025-02-28' instead of descriptive), the early-bird cutoff date appears as a separate policy column so the reader can see both dates and verify the comparison. Passes automatically if descriptive date values are used.
- ❌ **description-no-irrelevant-information**: If @Description is present, it does not include information that is already visible in the table columns or that can be derived from the table structure — such as fixed input values that could be columns, or restating the discount rules that the rows already demonstrate.
  > @Description states 'Base price is £100 for every row' and 'early-bird pricing applies...20% off' and 'group discount applies...15% off' — all rules already demonstrated by table rows.
- ❌ **discount-column-preferred**: Output column is 'Discount?' (percentage or amount off) rather than 'Price?' — making verification simpler (discount is the rule's direct output; price requires knowing the base price). If 'Price?' is used instead, both 'Base price' and 'Price?' columns must be present so the reader can trace the calculation.
  > Table has both 'Discount?' and 'Price?' columns, but no 'Base price' column; reader cannot trace Price calculation from Discount and Price alone.
- ❌ **single-assertion-in-method**: Each @TableTest method has a single, uniform assertion pattern applied to all rows — not different assertions per scenario. Validation checks one thing (e.g. error message or acceptance status); pricing checks one thing (e.g. discount or price). No if/switch in assertion logic.
  > Method has 2 assertions; Method has 2 assertions
- ✅ **optional-fields-has-expected-column**: The optional-fields acceptance table includes an expectation/output column (e.g. 'Approved?', 'Result?', or 'Status?') asserting that each combination is accepted — not just varying inputs without any expected column.
- ❌ **description-no-redundant-field-values**: Scenario names and @Description text do not restate values the table already shows. The assertion FAILS if either: (1) a scenario name contains a literal value that also appears in an input cell of the same row (e.g. scenario 'Age 35, score 700' beside Age and Credit score columns holding 35 and 700); or (2) @Description states a fixed value for an input that is already a column in that table. Otherwise it PASSES.
  > validatesRegistration @Description states 'Registration date and group size do not affect validation, so every row uses a fixed representative date and group size' — restating fixed input values already shown as columns.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. Judge each @TableTest table independently; the assertion FAILS if any table meets any of these conditions: (1) its expectation column holds the same value in every row; (2) in every row, each expectation cell is a verbatim copy of a cell in an input column of the same row; (3) an implementation that ignored the input columns entirely and returned a fixed value would satisfy every row. Otherwise the assertion PASSES.
- ❌ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body. The assertion FAILS if any of these hold for any @TableTest: (1) a value needed to predict the expectation (a threshold, rate, cutoff, multiplier, reference date) appears only in the method body or a field, and in neither a column, the @DisplayName, nor the @Description; (2) the operation applied to the inputs cannot be named from the column headers and cell values alone; (3) a helper method used in the assertion applies a comparison criterion (ordering, tolerance, subset matching) that is stated nowhere in the table, title, or description. Otherwise the assertion PASSES.
  > appliesPricingAndDiscount: cutoff date '2025-03-01' appears only in @Description and parseRegistrationTiming method body, not in any column; reader cannot verify 'before cutoff' means before 2025-03-01 from table alone.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-23-loan-approval-tt

**18/21** · 701022 tokens · 120677ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **threshold-as-column**: Policy thresholds (650/600 credit scores) appear in a dedicated policy column (e.g. 'Credit threshold') separate from the applicant's actual credit score column.
  > Thresholds (650, 600) are mentioned only in @Description text and scenario names, not in a dedicated 'Credit threshold' column. The 'Credit Score' column contains applicant scores only.
- ✅ **concrete-domain-values**: Cell values use concrete domain terms (e.g. '70', '650', 'APPROVED') — not abstract codes.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ❌ **scenario-names-describe-conditions**: Scenario names describe conditions ('Senior at lower threshold', 'Missing income') — not outcomes ('Approved', 'Rejected').
  > Scenario names include outcomes: 'Approved: standard threshold, stable income', 'Rejected: standard threshold, no stable income', 'Rejected: below standard threshold'.
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Age', 'Credit score', 'Stable income', 'Decision?') — not code identifiers.
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Decision?' or 'Status?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations.
- ✅ **separates-age-credit-income**: Age boundary policy, credit score categorisation, and income status are separated into distinct @TableTest methods.
- ❌ **description-no-redundant-field-values**: Scenario names and @Description text do not restate values the table already shows.
  > Scenario 'Approved: standard threshold, stable income' restates 'stable income' which appears as 'true' in the Has Stable Income column of that row.
- ✅ **depth-stable-income-effect**: The effect of stable income on the outcome is visible from table rows, not merely implied.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable.
- ✅ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body.
- ✅ **title-states-system-behaviour**: Each @DisplayName states what the code under test does, not an external fact it depends on.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-29-shopping-cart-tt

**24/26** · 1002722 tokens · 295673ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **concerns-decomposed**: Multiple @TableTest methods are used, each addressing a distinct concern — not one monolithic table mixing item operations, coupon logic, total calculation, and checkout.
- ✅ **minimal-rows-per-concern**: Each @TableTest has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.
- ✅ **separates-item-coupon-total-checkout**: Item operations (add/remove), coupon application (validity and replacement), cart total calculation (coupon type effects on price), and checkout (stock verification) are in separate @TableTest methods.
- ✅ **rows-independently-executable**: Each table row is independently executable — no row depends on a prior row's result. The table expresses rules, not a sequential test script. Each row specifies its own preconditions (e.g. 'Cart with items' as an input column, not implied from a previous row).
- ✅ **coupon-before-after-columns**: Coupon application table uses before/after columns (e.g. 'Active coupon' / 'Active coupon after?') to specify the state change when entering, replacing, or entering invalid coupon codes.
- ✅ **coupon-validity-not-coupon-types**: Coupon application table tests validity and replacement (valid code, expired code, nonexistent code, replacing active coupon) — not coupon type effects on price. Coupon type effects belong in the cart total table.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Message?', 'Total?', 'Active coupon after?')
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Add to empty cart', 'Replace active coupon with expired') — not outcomes ('Error', 'Success').
- ❌ **business-language-columns**: Column names use domain/business language (e.g. 'Product', 'Quantity', 'Message?', 'Cart total?') — not code identifiers like 'productId', 'result'.
  > Column 'Product Id' uses code identifier 'Id' instead of business language; should be 'Product' or 'Product Name'
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **single-assertion-in-method**: Each @TableTest method has a single, uniform assertion pattern applied to all rows — not different assertions per scenario.
  > Method has 3 assertions; Method has 3 assertions; Method has 3 assertions; Method has 2 assertions
- ✅ **test-data-visible**: Product prices and coupon rules (type, amount, expiry status) are visible in the table via columns, @Description, or explicit setup — not silently hardcoded. The reader can trace how inputs lead to outputs.
- ✅ **type-converters-for-complex-objects**: TypeConverter methods are used to convert map/string table values into domain objects (e.g. Map → Cart, Map → InventoryService, map → Coupon). Test method bodies contain only arrange-act-assert — no object construction from raw table values.
- ✅ **coupon-as-single-column**: Coupon data is expressed in a single column with a @TypeConverter — not spread across separate sparse columns for coupon type, value, and target product. The column format may be map syntax (e.g. [type: PERCENTAGE, value: 10]) or readable shorthand (e.g. '20% cart', '$5 off', '50% off widget').
- ✅ **uses-standard-map-syntax**: Columns representing maps (e.g. cart contents, product catalogue, inventory levels) use standard TableTest map syntax — [k: v, k2: v2] for entries and [:] for empty map — not a custom bracket notation. Custom string-parsing TypeConverters are not needed for data that maps naturally to Map<String, X>.
- ✅ **description-not-redundant-with-scenarios**: @Description annotations do not repeat information that the scenario names already convey. Coupon type behavior (what PERCENTAGE/FIXED/PRODUCT mean) should be expressed through descriptive scenario names, not duplicated in the description.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. Judge each @TableTest table independently; the assertion FAILS if any table meets any of these conditions: (1) its expectation column holds the same value in every row; (2) in every row, each expectation cell is a verbatim copy of a cell in an input column of the same row; (3) an implementation that ignored the input columns entirely and returned a fixed value would satisfy every row. Otherwise the assertion PASSES.
- ✅ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body. The assertion FAILS if any of these hold for any @TableTest: (1) a value needed to predict the expectation (a threshold, rate, cutoff, multiplier, reference date) appears only in the method body or a field, and in neither a column, the @DisplayName, nor the @Description; (2) the operation applied to the inputs cannot be named from the column headers and cell values alone; (3) a helper method used in the assertion applies a comparison criterion (ordering, tolerance, subset matching) that is stated nowhere in the table, title, or description. Otherwise the assertion PASSES.
- ✅ **title-states-system-behaviour**: Each @DisplayName (or the method name it falls back to) states what the code under test does, not an external fact it depends on. Apply the check mechanically: strike the system under test from the sentence — if it still reads as true, because it merely restates a regulation, standard, format, or domain fact, the title is about something else and fails.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

## Variant vs Official (iterations 40, 39 merged)

### Per-Eval Resource Comparison

| Eval | Pass (off) | Pass (var) | Tokens (off) | Tokens (var) | Tok Δ | Cost (off) | Cost (var) | Cost Δ | Time (off) | Time (var) | Time Δ |
|------|------------|------------|--------------|--------------|-------|------------|------------|--------|------------|------------|--------|
| eval-15-reis-discount | 21/23 | 19/23 | 2287509 | 1314175 | -43% | $1.4813 | $1.0505 | -29% | 411.3s | 306.3s | -26% |
| eval-18-convert-from-code | 20/24 | 20/24 | 826034 | 840562 | +2% | $0.5551 | $0.6560 | +18% | 148.2s | 236.3s | +59% |
| eval-22-event-registration-tt | 23/27 | 22/27 | 573142 | 672731 | +17% | $0.5159 | $0.5520 | +7% | 154.0s | 137.3s | -11% |
| eval-23-loan-approval-tt | 16/21 | 18/21 | 540843 | 701022 | +30% | $0.4822 | $0.5095 | +6% | 149.9s | 120.7s | -19% |
| eval-29-shopping-cart-tt | 23/26 | 24/26 | 968444 | 1002722 | +4% | $0.9054 | $0.9531 | +5% | 322.4s | 295.7s | -8% |
| **Totals (5 comparable)** | **103/121** | **103/121** | **5195972** | **4531212** | **-13%** | **$3.9399** | **$3.7212** | **-6%** | **1185.8s** | **1096.2s** | **-8%** |

**Comparable summary (5 evals in both):**

| Source | Pass Rate | Tokens | Cost | Time |
|--------|-----------|--------|------|------|
| next (iter 4) | 103/121 (85.1%) | 4531212 | $3.7212 | 1096.2s |
| official | 103/121 (85.1%) | 5195972 | $3.9399 | 1185.8s |
| **Δ** | | **-13%** | **-6%** | **-8%** |

### Per-Assertion Comparison

| Eval | Assertion | official | next |
|------|-----------|----------|------|
| eval-15-reis-discount | 2.9-correctness-value-set-tier-semantics | ✅ | ❌ |
| eval-15-reis-discount | 2.15-ticket-count-uses-value-sets | ✅ | ❌ |
| eval-15-reis-discount | 2.17-zone-irrelevance-visible | ❌ | ✅ |
| eval-15-reis-discount | 2.18-adult-senior-value-set | ✅ | ❌ |
| eval-18-convert-from-code | black-box-columns | ❌ | ✅ |
| eval-18-convert-from-code | separates-decision-and-premium | ✅ | ❌ |
| eval-22-event-registration-tt | discount-column-preferred | ✅ | ❌ |
| eval-23-loan-approval-tt | scenario-names-describe-conditions | ✅ | ❌ |
| eval-23-loan-approval-tt | concerns-decomposed | ❌ | ✅ |
| eval-23-loan-approval-tt | minimal-rows-per-concern | ❌ | ✅ |
| eval-23-loan-approval-tt | separates-age-credit-income | ❌ | ✅ |
| eval-29-shopping-cart-tt | rule-statable-from-table | ❌ | ✅ |

### Load-Bearing Assertions

Assertions that pass with official skill but fail with next variant:

- eval-15-reis-discount: `2.9-correctness-value-set-tier-semantics`
- eval-15-reis-discount: `2.15-ticket-count-uses-value-sets`
- eval-15-reis-discount: `2.18-adult-senior-value-set`
- eval-18-convert-from-code: `separates-decision-and-premium`
- eval-22-event-registration-tt: `discount-column-preferred`
- eval-23-loan-approval-tt: `scenario-names-describe-conditions`

### Attention Needed

Evals with failed assertions or disproportionate resource usage (>2x official):

| Eval | Issue | Details |
|------|-------|---------|
| eval-15-reis-discount | Failed assertions | `2.9-correctness-value-set-tier-semantics`, `2.15-ticket-count-uses-value-sets`, `2.18-adult-senior-value-set` |
| eval-18-convert-from-code | Failed assertions | `separates-decision-and-premium` |
| eval-22-event-registration-tt | Failed assertions | `discount-column-preferred` |
| eval-23-loan-approval-tt | Failed assertions | `scenario-names-describe-conditions` |

