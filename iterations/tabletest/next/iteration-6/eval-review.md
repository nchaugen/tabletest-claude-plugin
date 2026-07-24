# Eval Review — tabletest variant=next, Iteration 6

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-haiku-4-5 · **Date:** 2026-07-24 · **Evals:** 5

## Summary

100/123 (81.3%) · 6720848 tokens · 1387.6s · $4.8235

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

> ⚠️ **15 assertion verdicts moved** vs official iteration 40. These are deltas, not attributions: read each eval's `outputs/` and `narration.md` before explaining any of them, and do not start the next iteration until every entry in `analysis-todo.md` has a cause.

## Delta vs Iteration 5

**Regressions (10):**
- ❌ eval-15-reis-discount: `2.9-correctness-value-set-tier-semantics`
- ❌ eval-15-reis-discount: `2.15-ticket-count-uses-value-sets`
- ❌ eval-15-reis-discount: `2.20-readability-one-row-per-tier`
- ❌ eval-15-reis-discount: `minimal-rows-per-concern`
- ❌ eval-18-convert-from-code: `observable-io-only`
- ❌ eval-18-convert-from-code: `depth-premium-boundaries`
- ❌ eval-22-event-registration-tt: `minimal-rows-per-concern`
- ❌ eval-23-loan-approval-tt: `business-language-columns`
- ❌ eval-23-loan-approval-tt: `description-no-redundant-field-values`
- ❌ eval-23-loan-approval-tt: `title-states-system-behaviour`

**Improvements (27):**
- ✅ eval-15-reis-discount: `2.19-depth-all-tiers`
- ✅ eval-15-reis-discount: `2.21-readability-relative-time`
- ✅ eval-18-convert-from-code: `rule-falsifiable-by-a-row`
- ✅ eval-18-convert-from-code: `title-states-system-behaviour`
- ✅ eval-29-shopping-cart-tt: `has-tabletest-annotation`
- ✅ eval-29-shopping-cart-tt: `concerns-decomposed`
- ✅ eval-29-shopping-cart-tt: `minimal-rows-per-concern`
- ✅ eval-29-shopping-cart-tt: `separates-item-coupon-total-checkout`
- ✅ eval-29-shopping-cart-tt: `rows-independently-executable`
- ✅ eval-29-shopping-cart-tt: `coupon-before-after-columns`
- ✅ eval-29-shopping-cart-tt: `coupon-validity-not-coupon-types`
- ✅ eval-29-shopping-cart-tt: `scenario-column-present`
- ✅ eval-29-shopping-cart-tt: `has-question-mark-column`
- ✅ eval-29-shopping-cart-tt: `scenario-names-describe-conditions`
- ✅ eval-29-shopping-cart-tt: `no-if-switch-in-method`
- ✅ eval-29-shopping-cart-tt: `annotation-order`
- ✅ eval-29-shopping-cart-tt: `has-descriptive-title`
- ✅ eval-29-shopping-cart-tt: `description-uses-textblock`
- ✅ eval-29-shopping-cart-tt: `single-assertion-in-method`
- ✅ eval-29-shopping-cart-tt: `test-data-visible`
- ✅ eval-29-shopping-cart-tt: `type-converters-for-complex-objects`
- ✅ eval-29-shopping-cart-tt: `uses-standard-map-syntax`
- ✅ eval-29-shopping-cart-tt: `description-not-redundant-with-scenarios`
- ✅ eval-29-shopping-cart-tt: `has-tabletest-dependency`
- ✅ eval-29-shopping-cart-tt: `rule-falsifiable-by-a-row`
- ✅ eval-29-shopping-cart-tt: `title-states-system-behaviour`
- ✅ eval-29-shopping-cart-tt: `compiles`

## Resource Comparison vs Iteration 5

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-15-reis-discount | 19/25 | 21/25 | 2932356 | 1737644 | 400.5 | 300.4 |
| eval-18-convert-from-code | 15/24 | 15/24 | 973033 | 776593 | 190.2 | 144.3 |
| eval-22-event-registration-tt | 26/27 | 27/27 | 623840 | 767232 | 214.3 | 132.1 |
| eval-23-loan-approval-tt | 17/21 | 20/21 | 685625 | 733038 | 157.6 | 124.4 |
| eval-29-shopping-cart-tt | 23/26 | 0/26 | 1505994 | 0 | 425.0 | 1027.4 |

## Per-Eval Results

### ⚠️ Eval eval-15-reis-discount

**19/25** · 2932356 tokens · 400547ms

- ✅ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
- ✅ **2.2-children-flat-discount**: Children's flat 20% discount is represented — either as a row in the eligibility table or a separate note.
- ✅ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers derived from the 'every fifth trip' rule, with boundary values or value sets showing which ticket counts produce which discount. The maximum 40% discount is represented.
- ✅ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
- ❌ **window-boundary-uses-purchase-time**: The 30-day window is exercised at time-of-purchase granularity, not whole days only. At least one pair of rows must distinguish just-inside from just-outside by less than a day.
  > SingleTicketFrequencyCounterTest uses only whole-day boundaries (30 vs 31 days). The fixture format 'daysAgo-TicketType' and converter parse only integer days, with no sub-day precision (e.g., 29 days 23 hours vs 30 days 1 hour).
- ❌ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
  > ReisDiscountLadderTest does not use value sets at all. Each row has a single 'Prior Single Tickets' value, not a set like {4, 5, 6, 7, 8, 9} for the 5% tier.
- ❌ **2.15-ticket-count-uses-value-sets**: The discount ladder table uses value sets for the ticket count column to group counts within the same tier — not one row per boundary value.
  > ReisDiscountLadderTest enumerates one row per boundary (0, 3, 4, 8, 9, 14, 19, 24, 29, 34, 39, 44) rather than grouping tiers with value sets like {4, 5, 6, 7, 8, 9} for 5%.
- ✅ **2.16-no-duplicate-tier-mapping**: The tier-to-discount mapping is expressed once — not repeated across multiple tables.
- ✅ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ✅ **2.11-format-description**: @Description is present on at least the class or the tier method and provides context beyond what the table rows already express.
- ✅ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **2.14-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **2.17-zone-irrelevance-visible**: The eligibility/passenger-type table includes a Zone column with a value set to show that the discount applies regardless of zone.
- ❌ **zone-independent-counting**: The rolling-window counting concern shows by row that a past purchase's zone does not affect whether it counts toward the travel count — for example history entries whose zones differ (or a zone value set) reaching the same count.
  > SingleTicketFrequencyCounterTest has no zone variation in history rows. PastPurchaseFixtures hardcodes ZoneValidity.ZONE_1 for all parsed purchases, making zone irrelevance claimed but not exercised by data.
- ✅ **2.18-adult-senior-value-set**: Adult and senior are expressed as a value set {ADULT, SENIOR} (or equivalent) in at least one row of the eligibility table, since they follow identical discount rules.
- ✅ **2.19-depth-all-tiers**: All 9 discount tiers are represented in the ladder table: 0% (tickets 1-4), 5% (5-9), 10% (10-14), 15% (15-19), 20% (20-24), 25% (25-29), 30% (30-34), 35% (35-39), and 40% (40+).
- ❌ **2.20-readability-one-row-per-tier**: Each tier in the discount ladder is expressed as a single row with a value set for ticket counts — not split across multiple rows.
  > ReisDiscountLadderTest has 12 rows but only 9 tiers. Tiers 0%, 5%, and 40% are split across multiple rows (e.g., rows 1-2 both show 0%, rows 3-4 both show 5%), violating one-row-per-tier.
- ✅ **2.21-readability-relative-time**: The rolling window table expresses time relatively (e.g. '14 days ago', '30 days ago', or an integer 'Days ago' column) rather than using absolute dates.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules.
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.
  > ReisDiscountLadderTest has 12 rows for 9 tiers; rows 1-2 (0%), 3-4 (5%), and 11-12 (40%) duplicate tier outcomes without value sets, creating unnecessary rows.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. Expectation column must vary and not be verbatim copies of input columns.
- ✅ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body. No threshold or operation should appear only in method body or field.
- ✅ **title-states-system-behaviour**: Each @DisplayName states what the code under test does, not an external fact it depends on. Striking the system should make the sentence false.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-18-convert-from-code

**15/24** · 973033 tokens · 190158ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **black-box-columns**: Table columns represent the method's public inputs (applicant type, age, claim count) and observable outputs (decision, premium) — not internal state like hasActivePolicy, internalRiskScore, or calculateRiskScore.
  > The second @TableTest includes column 'Max Risk Score' which is internal state, not a public input or observable output of evaluateApplication.
- ❌ **observable-io-only**: No column is named after private fields or internal variables (hasActivePolicy, internalRiskScore, riskScore). The test treats the method as a black box.
  > Column 'Max Risk Score' appears in rejectsApplicationsAboveRiskThreshold table, exposing internal risk calculation.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Applicant type', 'Age', 'Claims', 'Decision?', 'Premium?') — not code identifiers like 'applicantType', 'claimCount', 'String'.
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Renewal with no claims', 'Senior applicant') — not outcomes ('Auto approved', 'Rejected').
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Decision?' or 'Premium?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules.
  > First table mixes renewal auto-approval rule with premium calculation in a single @TableTest, combining decision and premium concerns.
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.
  > First table has 3 rows mixing renewal auto-approval (decision concern) with premium examples; should separate these concerns into distinct tables.
- ❌ **separates-decision-and-premium**: Decision logic (AUTO_APPROVED/REJECTED/APPROVED) and premium calculation are in separate @TableTest methods
  > First @TableTest 'autoApprovesRenewalsWithZeroClaimsRegardlessOfAge' includes both Decision? and Premium? columns in the same table.
- ✅ **no-reimplemented-internals**: Test method body does not recompute internal formulas (e.g. risk score calculation). It only calls the public API (evaluateApplication) and asserts on the return value's fields.
- ❌ **description-no-internals**: @Description explains the test's purpose and business context — not internal formulas or implementation details.
  > Second @Description states 'Internal risk score is age/10 + claimCount*15' and 'risk score threshold of 75', exposing internal formulas. Third @Description includes 'standard premium is 100 + riskScore*2.0, senior premium is 200 + riskScore*3.5'.
- ❌ **depth-decision-boundaries**: Approval decision table covers the rejection boundary thoroughly: 5 claims triggers rejection regardless of realistic age (risk score crosses 75), 4 claims results in approval. Rows at 4 and 5 claims with varying ages demonstrate this cliff.
  > No decision table tests the 4-vs-5 claims boundary. Second table tests age 9 vs 10 at 5 claims (both near 75), not the claims cliff. First table has only 0, 1, 5 claims—missing the critical 4-claim row.
- ❌ **depth-premium-boundaries**: Approved premium table covers boundary effects: the premium jump from 0 to 1 claim (impact of +15 risk score on premium), and the premium difference at age 64 vs 65 (standard vs senior formula). These boundary conditions document how premium changes at each threshold.
  > The premium table `calculatesPremiumByAgeTierAndRiskScore` shows age 64 vs 65 boundary (112.0 vs 221.0) but does NOT isolate the 0-to-1 claim jump. It shows 0 claims and 2 claims, skipping the 1-claim boundary at age 30 that the spec requires.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. Judge each @TableTest table independently; the assertion FAILS if any table meets any of these conditions: (1) its expectation column holds the same value in every row; (2) in every row, each expectation cell is a verbatim copy of a cell in an input column of the same row; (3) an implementation that ignored the input columns entirely and returned a fixed value would satisfy every row. Otherwise the assertion PASSES.
- ❌ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body. The assertion FAILS if any of these hold for any @TableTest: (1) a value needed to predict the expectation (a threshold, rate, cutoff, multiplier, reference date) appears only in the method body or a field, and in neither a column, the @DisplayName, nor the @Description; (2) the operation applied to the inputs cannot be named from the column headers and cell values alone; (3) a helper method used in the assertion applies a comparison criterion (ordering, tolerance, subset matching) that is stated nowhere in the table, title, or description. Otherwise the assertion PASSES.
  > The @Description in `rejectsApplicationsAboveRiskThreshold` states 'Internal risk score is age/10 + claimCount*15' and 'risk score threshold of 75', exposing internal formulas and thresholds that should remain hidden in a black-box test per the spec requirement 'must not appear as a column or in @Description'.
- ✅ **title-states-system-behaviour**: Each @DisplayName (or the method name it falls back to) states what the code under test does, not an external fact it depends on. Apply the check mechanically: strike the system under test from the sentence — if it still reads as true, because it merely restates a regulation, standard, format, or domain fact, the title is about something else and fails.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against InsuranceEvaluator

### ⚠️ Eval eval-22-event-registration-tt

**26/27** · 623840 tokens · 214316ms

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
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
  > Validation table has 9 rows including multiple email format variants (missing @, missing local part, missing domain, no TLD) and blank/whitespace name variants. Pricing table has 5 rows with value sets {1,2,3,4} and {5,6,20} creating implicit permutations rather than minimal distinct cases.
- ✅ **separates-validation-and-pricing**: Input validation and pricing/discount calculation are in separate @TableTest methods
- ✅ **validation-includes-optional-fields**: Validation @TableTest includes Dietary Requirements and Accessibility Needs columns, with rows showing both null (blank) and non-null values are accepted — not hardcoded as null in the method body.
- ✅ **descriptive-registration-date**: Registration date uses descriptive values (e.g. 'before cutoff', 'on cutoff', 'after cutoff') with a @TypeConverter method converting to actual dates — not raw date literals like '2025-02-28'. Readability over precision.
- ✅ **cutoff-date-column-if-literal-dates**: If registration dates are literal (e.g. '2025-02-28' instead of descriptive), the early-bird cutoff date appears as a separate policy column so the reader can see both dates and verify the comparison. Passes automatically if descriptive date values are used.
- ✅ **description-no-irrelevant-information**: If @Description is present, it does not include information already visible in the table columns or derivable from the table structure — such as fixed input values that could be columns, or restating a rule the rows already demonstrate. It PASSES when the description adds what the rows cannot: a threshold or constant fixed in the method body, currency, where/when the rule applies, or naming an interpretation as a deliberate assumption.
- ✅ **discount-column-preferred**: Output column is 'Discount?' (percentage or amount off) rather than 'Price?' — making verification simpler (discount is the rule's direct output; price requires knowing the base price). If 'Price?' is used instead, both 'Base price' and 'Price?' columns must be present so the reader can trace the calculation.
- ✅ **single-assertion-in-method**: Each @TableTest method applies one uniform assertion pattern to every row — not different assertions per scenario. Several unconditional assertions checking distinct observable outputs of one result (e.g. success, message, resulting cart) count as one uniform pattern; the anti-pattern is branching on the row (if/switch/ternary/try-catch) to choose what is asserted.
- ✅ **optional-fields-has-expected-column**: The optional-fields acceptance table includes an expectation/output column (e.g. 'Approved?', 'Result?', or 'Status?') asserting that each combination is accepted — not just varying inputs without any expected column.
- ✅ **description-no-redundant-field-values**: Scenario names and @Description text do not restate values the table already shows. The assertion FAILS if either: (1) a scenario name contains a literal value that also appears in an input cell of the same row (e.g. scenario 'Age 35, score 700' beside Age and Credit score columns holding 35 and 700); or (2) @Description states a fixed value for an input that is already a column in that table. Otherwise it PASSES.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. Judge each @TableTest table independently; the assertion FAILS if any table meets any of these conditions: (1) its expectation column holds the same value in every row; (2) in every row, each expectation cell is a verbatim copy of a cell in an input column of the same row; (3) an implementation that ignored the input columns entirely and returned a fixed value would satisfy every row. Otherwise the assertion PASSES.
- ✅ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body. The assertion FAILS if any of these hold for any @TableTest: (1) a value needed to predict the expectation (a threshold, rate, cutoff, multiplier, reference date) appears only in the method body or a field, and in neither a column, the @DisplayName, nor the @Description; (2) the operation applied to the inputs cannot be named from the column headers and cell values alone; (3) a helper method used in the assertion applies a comparison criterion (ordering, tolerance, subset matching) that is stated nowhere in the table, title, or description. Otherwise the assertion PASSES.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-23-loan-approval-tt

**17/21** · 685625 tokens · 157606ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **threshold-verifiable-from-table**: Each policy threshold (650 for non-seniors, 600 for seniors) is verifiable from the table, in one of two ways: (a) a dedicated policy column (e.g. 'Credit threshold') beside the applicant's score, or (b) boundary rows that bracket it — within one age band, a row that qualifies just above the threshold and a row that fails at/below it, carrying different decisions.
- ✅ **concrete-domain-values**: Cell values use concrete domain terms (e.g. '70', '650', 'APPROVED') — not abstract codes like 'CATEGORY_A' or '1'. Boolean true/false is acceptable for yes/no flags when the parameter type is boolean.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Senior at lower threshold', 'Missing income') — not outcomes ('Approved', 'Rejected').
- ❌ **business-language-columns**: Column names use domain/business language (e.g. 'Age', 'Credit score', 'Stable income', 'Decision?') — not code identifiers like 'customerAge', 'boolean'.
  > Column 'Customer Age' is used instead of 'Age'; 'Has Stable Income' instead of 'Stable income'. These are verbose but not code identifiers. However, the reference decomposition uses 'Age' and 'Stable income' as the standard. The response uses 'Customer Age' and 'Has Stable Income', which are business language but deviate from the expected concise form.
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Decision?' or 'Status?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **concerns-decomposed**: The distinct concerns (age→threshold, credit boundary, income status) are each covered, without a monolithic table that re-proves unrelated rules through redundant permutations. A single table is acceptable when the concerns share the same input columns and each is covered by only a row or two — as here — so the compact single-table form and a multi-table split are both correct. The assertion FAILS only for a table that cross-multiplies concerns into redundant rows, not for using one table.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ✅ **covers-age-credit-income**: Age boundary policy, credit score categorisation, and income status are each covered by minimal scenarios. This may be one @TableTest whose rows exercise each concern (boundary pairs for age/credit, income held-constant rows for income status) or separate methods per concern with a combining verdict — both are correct given only a row or two per concern. The assertion FAILS if a concern is not exercised at all (e.g. income status never varied against a fixed score/age), not for choosing one table over several.
- ❌ **description-no-redundant-field-values**: Scenario names and @Description text do not restate values the table already shows. The assertion FAILS if either: (1) a scenario name contains a literal value that also appears in an input cell of the same row (e.g. scenario 'Age 35, score 700' beside Age and Credit score columns holding 35 and 700); or (2) @Description states a fixed value for an input that is already a column in that table. Otherwise it PASSES. Stating a constant that is NOT a column — a threshold, a policy figure, a value fixed in the method body — is correct and must not fail this assertion.
  > Table 1 @Description: 'Stable income is held constant (true) here' — but 'true' is not shown in a column, so this passes. Table 2 @Description: 'age is held constant (40, a standard applicant) and the credit score always qualifies (700, above the standard threshold of 650)' — states 40 and 700 which are fixed in the method call, not columns, so passes. Table 3 @Description: 'Age is held constant (40...) and the credit score (600) is below the standard threshold of 650' — states 40 and 600 which are fixed in the method call, not columns. However, the description also states 'standard threshold of 650' and 'senior threshold of 600', which are policy values not in the table columns, so this is acceptable. All descriptions pass.
- ✅ **depth-stable-income-effect**: The effect of stable income on the outcome is visible from table rows, not merely implied. The assertion PASSES if some single @TableTest table contains two rows that differ in the stable-income column (or equivalent) while holding the credit score and age columns equal, and those two rows carry different expected decisions. It FAILS if no such row pair exists in any table — including when stable income appears only in scenario names, only in the @Description, or only in rows that also vary another input.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. Judge each @TableTest table independently; the assertion FAILS if any table meets any of these conditions: (1) its expectation column holds the same value in every row; (2) in every row, each expectation cell is a verbatim copy of a cell in an input column of the same row; (3) an implementation that ignored the input columns entirely and returned a fixed value would satisfy every row. Otherwise the assertion PASSES. Do not fail a table merely because its values are simple or round — only these three conditions matter.
- ❌ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body.
  > Table 1 states 'Standard applicant at the threshold | 40 | 650 | REJECTED' but the rule that 650 is the exact boundary (not 649 or 651) and that the threshold is *strictly above* is not derivable from the table alone—it requires the @Description or method body. The age-65 threshold of 600 is similarly implicit.
- ❌ **title-states-system-behaviour**: Each @DisplayName states what the code under test does, not an external fact it depends on.
  > Title 'seniorApplicantsGetALowerThreshold' — striking 'senior applicants' leaves 'get a lower threshold,' which is true as a domain fact independent of the system. The title restates the regulation, not what the evaluator does.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-29-shopping-cart-tt

**23/26** · 1505994 tokens · 424975ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **concerns-decomposed**: Multiple @TableTest methods are used, each addressing a distinct concern — not one monolithic table mixing item operations, coupon logic, total calculation, and checkout.
- ✅ **minimal-rows-per-concern**: Each @TableTest has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.
- ✅ **separates-item-coupon-total-checkout**: Item operations (add/remove), coupon application (validity and replacement), cart total calculation (coupon type effects on price), and checkout (stock verification) are in separate @TableTest methods.
- ✅ **rows-independently-executable**: Each table row is independently executable — no row depends on a prior row's result. The table expresses rules, not a sequential test script. Each row specifies its own preconditions.
- ✅ **coupon-before-after-columns**: Coupon application table uses before/after columns (e.g. 'Active coupon' / 'Active coupon after?') to specify the state change when entering, replacing, or entering invalid coupon codes.
- ✅ **coupon-validity-not-coupon-types**: Coupon application table tests validity and replacement (valid code, expired code, nonexistent code, replacing active coupon) — not coupon type effects on price. Coupon type effects belong in the cart total table.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Message?', 'Total?', 'Active coupon after?')
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Add to empty cart', 'Replace active coupon with expired') — not outcomes ('Error', 'Success').
- ❌ **business-language-columns**: Column names use domain/business language (e.g. 'Product', 'Quantity', 'Message?', 'Cart total?') — not code identifiers like 'productId', 'result'.
  > Column header 'Product Id' uses code identifier 'Id'; should be 'Product'. Also 'Cart Items Before' and 'Cart Items After?' use 'Items' where 'Cart' alone is clearer per reference.
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **single-assertion-in-method**: Each @TableTest method applies one uniform assertion pattern to every row — not different assertions per scenario. Several unconditional assertions checking distinct observable outputs of one result (e.g. success, message, resulting cart) count as one uniform pattern; the anti-pattern is branching on the row (if/switch/ternary/try-catch) to choose what is asserted.
- ✅ **test-data-visible**: Product prices and coupon rules (type, amount, expiry status) are visible in the table via columns, @Description, or explicit setup — not silently hardcoded. The reader can trace how inputs lead to outputs.
- ✅ **type-converters-for-complex-objects**: TypeConverter methods are used to convert map/string table values into domain objects (e.g. Map → Cart, Map → InventoryService, map → Coupon). Test method bodies contain only arrange-act-assert — no object construction from raw table values.
- ❌ **coupon-as-single-column**: Coupon data is expressed in a single column with a @TypeConverter — not spread across separate sparse columns for coupon type, value, and target product.
  > The `appliesOneCouponAtATime` table uses a `Coupons` column (a map of code→spec), not a single coupon column. The `calculatesCartTotalAfterCoupon` table has `Active Coupon` as a single column, but the `appliesOneCouponAtATime` table violates this by using a map-based `Coupons` store instead of a single coupon value per row.
- ✅ **uses-standard-map-syntax**: Columns representing maps use standard TableTest map syntax — [k: v, k2: v2] for entries and [:] for empty map — not a custom bracket notation.
- ✅ **description-not-redundant-with-scenarios**: @Description annotations do not merely repeat information that the scenario names already convey.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable.
- ❌ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body.
  > The `calculatesCartTotalAfterCoupon` table shows 'PRODUCT Widget 3.00' in the coupon column and expects 19.00 for [Widget:2, Gadget:1] with prices [Widget:10.00, Gadget:5.00]. The rule '2×10 + 1×5 − (2×3) = 19' requires inferring that the product coupon is a per-unit discount; the @Description flags this as an open assumption, but the table itself does not state the operation (per-unit vs. flat deduction) — it only demonstrates one case.
- ✅ **title-states-system-behaviour**: Each @DisplayName states what the code under test does, not an external fact it depends on.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

## Variant vs Official (iterations 40, 39 merged)

### Per-Eval Resource Comparison

| Eval | Pass (off) | Pass (var) | Tokens (off) | Tokens (var) | Tok Δ | Cost (off) | Cost (var) | Cost Δ | Time (off) | Time (var) | Time Δ |
|------|------------|------------|--------------|--------------|-------|------------|------------|--------|------------|------------|--------|
| eval-15-reis-discount | 23/25 | 19/25 | 2287509 | 2932356 | +28% | $1.4813 | $1.6961 | +15% | 411.3s | 400.5s | -3% |
| eval-18-convert-from-code | 18/24 | 15/24 | 826034 | 973033 | +18% | $0.5551 | $0.7058 | +27% | 148.2s | 190.2s | +28% |
| eval-22-event-registration-tt | 27/27 | 26/27 | 573142 | 623840 | +9% | $0.5159 | $0.5841 | +13% | 154.0s | 214.3s | +39% |
| eval-23-loan-approval-tt | 20/21 | 17/21 | 540843 | 685625 | +27% | $0.4822 | $0.5643 | +17% | 149.9s | 157.6s | +5% |
| eval-29-shopping-cart-tt | 25/26 | 23/26 | 968444 | 1505994 | +56% | $0.9054 | $1.2732 | +41% | 322.4s | 425.0s | +32% |
| **Totals (5 comparable)** | **113/123** | **100/123** | **5195972** | **6720848** | **+29%** | **$3.9399** | **$4.8235** | **+22%** | **1185.8s** | **1387.6s** | **+17%** |

**Comparable summary (5 evals in both):**

| Source | Pass Rate | Tokens | Cost | Time |
|--------|-----------|--------|------|------|
| next (iter 6) | 100/123 (81.3%) | 6720848 | $4.8235 | 1387.6s |
| official | 113/123 (91.9%) | 5195972 | $3.9399 | 1185.8s |
| **Δ** | | **+29%** | **+22%** | **+17%** |

### Per-Assertion Comparison

| Eval | Assertion | official | next |
|------|-----------|----------|------|
| eval-15-reis-discount | 2.9-correctness-value-set-tier-semantics | ✅ | ❌ |
| eval-15-reis-discount | 2.15-ticket-count-uses-value-sets | ✅ | ❌ |
| eval-15-reis-discount | 2.17-zone-irrelevance-visible | ❌ | ✅ |
| eval-15-reis-discount | zone-independent-counting | ✅ | ❌ |
| eval-15-reis-discount | 2.20-readability-one-row-per-tier | ✅ | ❌ |
| eval-15-reis-discount | minimal-rows-per-concern | ✅ | ❌ |
| eval-18-convert-from-code | observable-io-only | ✅ | ❌ |
| eval-18-convert-from-code | concerns-decomposed | ✅ | ❌ |
| eval-18-convert-from-code | minimal-rows-per-concern | ✅ | ❌ |
| eval-22-event-registration-tt | minimal-rows-per-concern | ✅ | ❌ |
| eval-23-loan-approval-tt | business-language-columns | ✅ | ❌ |
| eval-23-loan-approval-tt | description-no-redundant-field-values | ✅ | ❌ |
| eval-23-loan-approval-tt | rule-statable-from-table | ✅ | ❌ |
| eval-29-shopping-cart-tt | coupon-as-single-column | ✅ | ❌ |
| eval-29-shopping-cart-tt | rule-statable-from-table | ✅ | ❌ |

### Load-Bearing Assertions

Assertions that pass with official skill but fail with next variant:

- eval-15-reis-discount: `2.9-correctness-value-set-tier-semantics`
- eval-15-reis-discount: `2.15-ticket-count-uses-value-sets`
- eval-15-reis-discount: `zone-independent-counting`
- eval-15-reis-discount: `2.20-readability-one-row-per-tier`
- eval-15-reis-discount: `minimal-rows-per-concern`
- eval-18-convert-from-code: `observable-io-only`
- eval-18-convert-from-code: `concerns-decomposed`
- eval-18-convert-from-code: `minimal-rows-per-concern`
- eval-22-event-registration-tt: `minimal-rows-per-concern`
- eval-23-loan-approval-tt: `business-language-columns`
- eval-23-loan-approval-tt: `description-no-redundant-field-values`
- eval-23-loan-approval-tt: `rule-statable-from-table`
- eval-29-shopping-cart-tt: `coupon-as-single-column`
- eval-29-shopping-cart-tt: `rule-statable-from-table`

### Attention Needed

Evals with failed assertions or disproportionate resource usage (>2x official):

| Eval | Issue | Details |
|------|-------|---------|
| eval-15-reis-discount | Failed assertions | `2.9-correctness-value-set-tier-semantics`, `2.15-ticket-count-uses-value-sets`, `zone-independent-counting`, `2.20-readability-one-row-per-tier`, `minimal-rows-per-concern` |
| eval-18-convert-from-code | Failed assertions | `observable-io-only`, `concerns-decomposed`, `minimal-rows-per-concern` |
| eval-22-event-registration-tt | Failed assertions | `minimal-rows-per-concern` |
| eval-23-loan-approval-tt | Failed assertions | `business-language-columns`, `description-no-redundant-field-values`, `rule-statable-from-table` |
| eval-29-shopping-cart-tt | Failed assertions | `coupon-as-single-column`, `rule-statable-from-table` |

