# Eval Review — tabletest variant=next, Iteration 5

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-haiku-4-5 · **Date:** 2026-07-24 · **Evals:** 5

## Summary

83/123 (67.5%) · 4014507 tokens · 1728.6s · $2.8459

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

## Delta vs Iteration 4

**Regressions (30):**
- ❌ eval-15-reis-discount: `2.19-depth-all-tiers`
- ❌ eval-15-reis-discount: `2.21-readability-relative-time`
- ❌ eval-18-convert-from-code: `concerns-decomposed`
- ❌ eval-18-convert-from-code: `rule-falsifiable-by-a-row`
- ❌ eval-18-convert-from-code: `title-states-system-behaviour`
- ❌ eval-29-shopping-cart-tt: `has-tabletest-annotation`
- ❌ eval-29-shopping-cart-tt: `concerns-decomposed`
- ❌ eval-29-shopping-cart-tt: `minimal-rows-per-concern`
- ❌ eval-29-shopping-cart-tt: `separates-item-coupon-total-checkout`
- ❌ eval-29-shopping-cart-tt: `rows-independently-executable`
- ❌ eval-29-shopping-cart-tt: `coupon-before-after-columns`
- ❌ eval-29-shopping-cart-tt: `coupon-validity-not-coupon-types`
- ❌ eval-29-shopping-cart-tt: `scenario-column-present`
- ❌ eval-29-shopping-cart-tt: `has-question-mark-column`
- ❌ eval-29-shopping-cart-tt: `scenario-names-describe-conditions`
- ❌ eval-29-shopping-cart-tt: `no-if-switch-in-method`
- ❌ eval-29-shopping-cart-tt: `annotation-order`
- ❌ eval-29-shopping-cart-tt: `has-descriptive-title`
- ❌ eval-29-shopping-cart-tt: `description-uses-textblock`
- ❌ eval-29-shopping-cart-tt: `single-assertion-in-method`
- ❌ eval-29-shopping-cart-tt: `test-data-visible`
- ❌ eval-29-shopping-cart-tt: `type-converters-for-complex-objects`
- ❌ eval-29-shopping-cart-tt: `coupon-as-single-column`
- ❌ eval-29-shopping-cart-tt: `uses-standard-map-syntax`
- ❌ eval-29-shopping-cart-tt: `description-not-redundant-with-scenarios`
- ❌ eval-29-shopping-cart-tt: `has-tabletest-dependency`
- ❌ eval-29-shopping-cart-tt: `rule-falsifiable-by-a-row`
- ❌ eval-29-shopping-cart-tt: `rule-statable-from-table`
- ❌ eval-29-shopping-cart-tt: `title-states-system-behaviour`
- ❌ eval-29-shopping-cart-tt: `compiles`

**Improvements (11):**
- ✅ eval-15-reis-discount: `2.9-correctness-value-set-tier-semantics`
- ✅ eval-15-reis-discount: `2.15-ticket-count-uses-value-sets`
- ✅ eval-15-reis-discount: `2.20-readability-one-row-per-tier`
- ✅ eval-18-convert-from-code: `depth-premium-boundaries`
- ✅ eval-23-loan-approval-tt: `threshold-verifiable-from-table`
- ✅ eval-23-loan-approval-tt: `scenario-names-describe-conditions`
- ✅ eval-23-loan-approval-tt: `concerns-decomposed`
- ✅ eval-23-loan-approval-tt: `minimal-rows-per-concern`
- ✅ eval-23-loan-approval-tt: `description-no-redundant-field-values`
- ✅ eval-23-loan-approval-tt: `rule-falsifiable-by-a-row`
- ✅ eval-23-loan-approval-tt: `title-states-system-behaviour`

## Resource Comparison vs Iteration 4

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-15-reis-discount | 21/25 | 20/25 | 1737644 | 1314175 | 300.4 | 306.3 |
| eval-18-convert-from-code | 15/24 | 17/24 | 776593 | 840562 | 144.3 | 236.3 |
| eval-22-event-registration-tt | 27/27 | 27/27 | 767232 | 672731 | 132.1 | 137.3 |
| eval-23-loan-approval-tt | 20/21 | 13/21 | 733038 | 701022 | 124.4 | 120.7 |
| eval-29-shopping-cart-tt | 0/26 | 25/26 | 0 | 1002722 | 1027.4 | 295.7 |

## Per-Eval Results

### ⚠️ Eval eval-15-reis-discount

**21/25** · 1737644 tokens · 300370ms

- ✅ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
- ✅ **2.2-children-flat-discount**: Children's flat 20% discount is represented — either as a row in the eligibility table or a separate note.
- ✅ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers derived from the 'every fifth trip' rule, with boundary values or value sets showing which ticket counts produce which discount. The maximum 40% discount is represented.
- ✅ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
- ❌ **window-boundary-uses-purchase-time**: The 30-day window is exercised at time-of-purchase granularity, not whole days only.
  > ReisPurchaseCounterTest uses LocalDate (whole days only): 'Exactly 30 days before still counts | [2026-05-31]' and 'One day past the window is excluded | [2026-05-30]'. No sub-day distinction like '29 days 23 hours' vs '30 days 1 hour'
- ✅ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
- ✅ **2.15-ticket-count-uses-value-sets**: The discount ladder table uses value sets for the ticket count column to group counts within the same tier — not one row per boundary value.
- ✅ **2.16-no-duplicate-tier-mapping**: The tier-to-discount mapping is expressed once — not repeated across multiple tables.
- ✅ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ✅ **2.11-format-description**: @Description is present on at least the class or the tier method and provides context beyond what the table rows already express.
- ✅ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **2.14-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **2.17-zone-irrelevance-visible**: The eligibility/passenger-type table includes a Zone column with a value set to show that the discount applies regardless of zone.
- ❌ **zone-independent-counting**: The rolling-window counting concern shows by row that a past purchase's zone does not affect whether it counts toward the travel count — for example history entries whose zones differ (or a zone value set) reaching the same count.
  > ReisPurchaseCounterTest hardcodes all history entries to ZoneValidity.ZONE_1; no row varies zone in the history. The assertion requires zone variation in the data itself, not just in a separate table.
- ✅ **2.18-adult-senior-value-set**: Adult and senior are expressed as a value set {ADULT, SENIOR} (or equivalent) in at least one row of the eligibility table, since they follow identical discount rules.
- ❌ **2.19-depth-all-tiers**: All 9 discount tiers are represented in the ladder table: 0% (tickets 1-4), 5% (5-9), 10% (10-14), 15% (15-19), 20% (20-24), 25% (25-29), 30% (30-34), 35% (35-39), and 40% (40+).
  > ReisDiscountLadderTest covers: 0%, 5%, 10%, 15%, 35%, 40%. Missing tiers: 20%, 25%, 30%. Only 6 of 9 tiers are represented.
- ✅ **2.20-readability-one-row-per-tier**: Each tier in the discount ladder is expressed as a single row with a value set for ticket counts — not split across multiple rows.
- ❌ **2.21-readability-relative-time**: The rolling window table expresses time relatively (e.g. '14 days ago', '30 days ago', or an integer 'Days ago' column) rather than using absolute dates.
  > ReisPurchaseCounterTest uses absolute dates: 'Single Ticket Dates | [2026-06-01, 2026-06-15]' and 'Now | 2026-06-30'. No relative time expressions like 'days ago' appear.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. Implementation ignoring inputs and returning fixed values would not satisfy every row.
- ✅ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body. No threshold or operation appears only in method body or field.
- ✅ **title-states-system-behaviour**: Each @DisplayName states what the code under test does, not an external fact it depends on.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-18-convert-from-code

**15/24** · 776593 tokens · 144250ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **black-box-columns**: Table columns represent the method's public inputs (applicant type, age, claim count) and observable outputs (decision, premium) — not internal state like hasActivePolicy, internalRiskScore, or calculateRiskScore.
  > The second test's @Description states 'Risk score is derived from age and claim count' and the scenario names reference 'rejection threshold' and 'risk score lands on' — exposing internal risk score logic in the description.
- ✅ **observable-io-only**: No column is named after private fields or internal variables (hasActivePolicy, internalRiskScore, riskScore). The test treats the method as a black box.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Applicant type', 'Age', 'Claims', 'Decision?', 'Premium?') — not code identifiers like 'applicantType', 'claimCount', 'String'.
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Renewal with no claims', 'Senior applicant') — not outcomes ('Auto approved', 'Rejected').
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Decision?' or 'Premium?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules.
  > The first test mixes renewal auto-approval, standard new applicant approval, and renewal-with-claim approval in one table. The second test conflates rejection threshold with premium pricing (includes 'Premium?' column when testing rejection). Should be 2 tables: decision (renewal/claims/rejection) and premium (age tiers and claim impact).
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.
  > First test has 3 rows mixing three different concerns (renewal auto-approval, new applicant approval, renewal with claim). Second test has 2 rows but includes Premium? column when testing rejection boundary, mixing concerns.
- ❌ **separates-decision-and-premium**: Decision logic (AUTO_APPROVED/REJECTED/APPROVED) and premium calculation are in separate @TableTest methods
  > The second test 'rejectsApplicationsAboveRiskThreshold' includes both 'Decision?' and 'Premium?' columns, mixing decision and premium concerns in one method.
- ✅ **no-reimplemented-internals**: Test method body does not recompute internal formulas (e.g. risk score calculation). It only calls the public API (evaluateApplication) and asserts on the return value's fields.
- ❌ **description-no-internals**: @Description explains the test's purpose and business context — not internal formulas or implementation details (e.g. 'riskScore = age/10 + claimCount*15', 'rejected when riskScore > 75').
  > Second test @Description: 'Risk score is derived from age and claim count and rejects the application once it exceeds 75' and 'integer division gives both the same underlying risk score (6)' — directly exposes internal risk score formula and logic.
- ❌ **depth-decision-boundaries**: Approval decision table covers the rejection boundary thoroughly: 5 claims triggers rejection regardless of realistic age (risk score crosses 75), 4 claims results in approval. Rows at 4 and 5 claims with varying ages demonstrate this cliff.
  > No row tests 4 claims. The second test uses 5 claims with ages 9 and 10 to show rejection, but does not demonstrate the 4-vs-5 claims boundary. Expected reference table shows 'NEW | 40 | 4 | APPROVED' and 'NEW | 40 | 5 | REJECTED' — these rows are absent.
- ✅ **depth-premium-boundaries**: Approved premium table covers boundary effects: the premium jump from 0 to 1 claim (impact of +15 risk score on premium), and the premium difference at age 64 vs 65 (standard vs senior formula). These boundary conditions document how premium changes at each threshold.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ❌ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. Judge each @TableTest table independently; the assertion FAILS if any table meets any of these conditions: (1) its expectation column holds the same value in every row; (2) in every row, each expectation cell is a verbatim copy of a cell in an input column of the same row; (3) an implementation that ignored the input columns entirely and returned a fixed value would satisfy every row. Otherwise the assertion PASSES.
  > rejectsApplicationsAboveRiskThreshold table: both rows have identical inputs (applicantType {NEW, RENEWAL}, claimCount 5) differing only in age (9 vs 10). The value set {NEW, RENEWAL} expands to 4 rows total, but the core distinction (age 9 APPROVED vs age 10 REJECTED) relies on a single-point difference that could be satisfied by a fixed threshold lookup ignoring the actual formula.
- ❌ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body. The assertion FAILS if any of these hold for any @TableTest: (1) a value needed to predict the expectation (a threshold, rate, cutoff, multiplier, reference date) appears only in the method body or a field, and in neither a column, the @DisplayName, nor the @Description; (2) the operation applied to the inputs cannot be named from the column headers and cell values alone; (3) a helper method used in the assertion applies a comparison criterion (ordering, tolerance, subset matching) that is stated nowhere in the table, title, or description. Otherwise the assertion PASSES.
  > @Description in rejectsApplicationsAboveRiskThreshold states 'Risk score is derived from age and claim count and rejects the application once it exceeds 75' — the internal formula 'age/10 + claims*15' and threshold '75' appear only in the description and method body, not in columns or observable from the table rows alone.
- ❌ **title-states-system-behaviour**: Each @DisplayName (or the method name it falls back to) states what the code under test does, not an external fact it depends on. Apply the check mechanically: strike the system under test from the sentence — if it still reads as true, because it merely restates a regulation, standard, format, or domain fact, the title is about something else and fails.
  > rejectsApplicationsAboveRiskThreshold: striking 'rejects applications' leaves 'above risk threshold' — a domain fact independent of the system. The title restates the business rule rather than stating what evaluateApplication does (which is to return REJECTED when risk score > 75).
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against InsuranceEvaluator

### ✅ Eval eval-22-event-registration-tt

**27/27** · 767232 tokens · 132139ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **validation-rules-covered**: Email validation and name-required error scenarios are present — at least one row for invalid email and one for missing name.
- ✅ **blank-for-absent-optional**: Dietary requirements and/or accessibility needs use blank cells (null) when not provided — not 'N/A' or 'none'.
- ✅ **blank-vs-value-set-correct**: When testing pricing rules, irrelevant inputs (e.g. dietary requirements don't affect price) use value sets or representative values — not blanks.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Result?', 'Price?')
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Name', 'Email', 'Registration date', 'Group size', 'Price?') — not code identifiers.
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Early-bird single attendee', 'Missing name') — not outcomes ('Rejected', '20% off').
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.
- ✅ **separates-validation-and-pricing**: Input validation and pricing/discount calculation are in separate @TableTest methods.
- ✅ **validation-includes-optional-fields**: Validation @TableTest includes Dietary Requirements and Accessibility Needs columns, with rows showing both null (blank) and non-null values are accepted.
- ✅ **descriptive-registration-date**: Registration date uses descriptive values (e.g. 'before cutoff', 'on cutoff', 'after cutoff') with a @TypeConverter method converting to actual dates — not raw date literals.
- ✅ **cutoff-date-column-if-literal-dates**: If registration dates are literal (e.g. '2025-02-28' instead of descriptive), the early-bird cutoff date appears as a separate policy column so the reader can see both dates and verify the comparison. Passes automatically if descriptive date values are used.
- ✅ **description-no-irrelevant-information**: If @Description is present, it does not include information already visible in the table columns or derivable from the table structure — such as fixed input values that could be columns, or restating a rule the rows already demonstrate. It PASSES when the description adds what the rows cannot: a threshold or constant fixed in the method body, currency, where/when the rule applies, or naming an interpretation as a deliberate assumption.
- ✅ **discount-column-preferred**: Output column is 'Discount?' (percentage or amount off) rather than 'Price?' — making verification simpler (discount is the rule's direct output; price requires knowing the base price). If 'Price?' is used instead, both 'Base price' and 'Price?' columns must be present so the reader can trace the calculation.
- ✅ **single-assertion-in-method**: Each @TableTest method applies one uniform assertion pattern to every row — not different assertions per scenario. Several unconditional assertions checking distinct observable outputs of one result (e.g. success, message, resulting cart) count as one uniform pattern; the anti-pattern is branching on the row (if/switch/ternary/try-catch) to choose what is asserted.
- ✅ **optional-fields-has-expected-column**: The optional-fields acceptance table includes an expectation/output column (e.g. 'Approved?', 'Result?', or 'Status?') asserting that each combination is accepted — not just varying inputs without any expected column.
- ✅ **description-no-redundant-field-values**: Scenario names and @Description text do not restate values the table already shows. The assertion FAILS if either: (1) a scenario name contains a literal value that also appears in an input cell of the same row; or (2) @Description states a fixed value for an input that is already a column in that table. Otherwise it PASSES.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. The assertion FAILS if any table meets any of these conditions: (1) its expectation column holds the same value in every row; (2) in every row, each expectation cell is a verbatim copy of a cell in an input column of the same row; (3) an implementation that ignored the input columns entirely and returned a fixed value would satisfy every row. Otherwise the assertion PASSES.
- ✅ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body. The assertion FAILS if any of these hold for any @TableTest: (1) a value needed to predict the expectation (a threshold, rate, cutoff, multiplier, reference date) appears only in the method body or a field, and in neither a column, the @DisplayName, nor the @Description; (2) the operation applied to the inputs cannot be named from the column headers and cell values alone; (3) a helper method used in the assertion applies a comparison criterion that is stated nowhere in the table, title, or description. Otherwise the assertion PASSES.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-23-loan-approval-tt

**20/21** · 733038 tokens · 124435ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **threshold-verifiable-from-table**: Each policy threshold (650 for non-seniors, 600 for seniors) is verifiable from the table, in one of two ways: (a) a dedicated policy column (e.g. 'Credit threshold') beside the applicant's score, or (b) boundary rows that bracket it — within one age band, a row that qualifies just above the threshold and a row that fails at/below it, carrying different decisions.
- ✅ **concrete-domain-values**: Cell values use concrete domain terms (e.g. '70', '650', 'APPROVED') — not abstract codes like 'CATEGORY_A' or '1'. Boolean true/false is acceptable for yes/no flags when the parameter type is boolean.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Senior at lower threshold', 'Missing income') — not outcomes ('Approved', 'Rejected').
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Age', 'Credit score', 'Stable income', 'Decision?') — not code identifiers like 'customerAge', 'boolean'.
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Decision?' or 'Status?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **concerns-decomposed**: The distinct concerns (age→threshold, credit boundary, income status) are each covered, without a monolithic table that re-proves unrelated rules through redundant permutations. A single table is acceptable when the concerns share the same input columns and each is covered by only a row or two.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ✅ **covers-age-credit-income**: Age boundary policy, credit score categorisation, and income status are each covered by minimal scenarios. This may be one @TableTest whose rows exercise each concern or separate methods per concern with a combining verdict — both are correct given only a row or two per concern.
- ✅ **description-no-redundant-field-values**: Scenario names and @Description text do not restate values the table already shows. The assertion FAILS if either: (1) a scenario name contains a literal value that also appears in an input cell of the same row; or (2) @Description states a fixed value for an input that is already a column in that table.
- ✅ **depth-stable-income-effect**: The effect of stable income on the outcome is visible from table rows, not merely implied. The assertion PASSES if some single @TableTest table contains two rows that differ in the stable-income column (or equivalent) while holding the credit score and age columns equal, and those two rows carry different expected decisions.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. The assertion FAILS if any table meets any of these conditions: (1) its expectation column holds the same value in every row; (2) in every row, each expectation cell is a verbatim copy of a cell in an input column of the same row; (3) an implementation that ignored the input columns entirely and returned a fixed value would satisfy every row.
- ❌ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body. The assertion FAILS if any of these hold for any @TableTest: (1) a value needed to predict the expectation (a threshold, rate, cutoff, multiplier, reference date) appears only in the method body or a field, and in neither a column, the @DisplayName, nor the @Description; (2) the operation applied to the inputs cannot be named from the column headers and cell values alone; (3) a helper method used in the assertion applies a comparison criterion (ordering, tolerance, subset matching) that is stated nowhere in the table, title, or description. Otherwise the assertion PASSES. Setup that merely constructs the objects named by the columns is not a failure.
  > Table 1 includes a 'Threshold?' column (650, 600) that duplicates the policy values already encoded in the boundary rows themselves. The threshold is a policy constant, not test data, and should be inferred from bracketing (650 rejected, 651 approved), not restated as a column. This violates the @Description discipline: 'the rows carry the rules and the policy numbers, so the @Description must not restate the 650/600 thresholds.'
- ✅ **title-states-system-behaviour**: Each @DisplayName (or the method name it falls back to) states what the code under test does, not an external fact it depends on. Apply the check mechanically: strike the system under test from the sentence — if it still reads as true, because it merely restates a regulation, standard, format, or domain fact, the title is about something else and fails.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-29-shopping-cart-tt

**0/26** · 0 tokens · 1027426ms


## Variant vs Official (iterations 40, 39 merged)

### Per-Eval Resource Comparison

| Eval | Pass (off) | Pass (var) | Tokens (off) | Tokens (var) | Tok Δ | Cost (off) | Cost (var) | Cost Δ | Time (off) | Time (var) | Time Δ |
|------|------------|------------|--------------|--------------|-------|------------|------------|--------|------------|------------|--------|
| eval-15-reis-discount | 23/25 | 21/25 | 2287509 | 1737644 | -24% | $1.4813 | $1.1729 | -21% | 411.3s | 300.4s | -27% |
| eval-18-convert-from-code | 18/24 | 15/24 | 826034 | 776593 | -6% | $0.5551 | $0.5622 | +1% | 148.2s | 144.3s | -3% |
| eval-22-event-registration-tt | 27/27 | 27/27 | 573142 | 767232 | +34% | $0.5159 | $0.5712 | +11% | 154.0s | 132.1s | -14% |
| eval-23-loan-approval-tt | 20/21 | 20/21 | 540843 | 733038 | +36% | $0.4822 | $0.5395 | +12% | 149.9s | 124.4s | -17% |
| eval-29-shopping-cart-tt | 25/26 | 0/26 | 968444 | 0 | -100% | $0.9054 | $0.0000 | -100% | 322.4s | 1027.4s | +219% |
| **Totals (5 comparable)** | **113/123** | **83/123** | **5195972** | **4014507** | **-23%** | **$3.9399** | **$2.8459** | **-28%** | **1185.8s** | **1728.6s** | **+46%** |

**Comparable summary (5 evals in both):**

| Source | Pass Rate | Tokens | Cost | Time |
|--------|-----------|--------|------|------|
| next (iter 5) | 83/123 (67.5%) | 4014507 | $2.8459 | 1728.6s |
| official | 113/123 (91.9%) | 5195972 | $3.9399 | 1185.8s |
| **Δ** | | **-23%** | **-28%** | **+46%** |

### Per-Assertion Comparison

| Eval | Assertion | official | next |
|------|-----------|----------|------|
| eval-15-reis-discount | 2.17-zone-irrelevance-visible | ❌ | ✅ |
| eval-15-reis-discount | zone-independent-counting | ✅ | ❌ |
| eval-15-reis-discount | 2.19-depth-all-tiers | ✅ | ❌ |
| eval-15-reis-discount | 2.21-readability-relative-time | ✅ | ❌ |
| eval-18-convert-from-code | concerns-decomposed | ✅ | ❌ |
| eval-18-convert-from-code | minimal-rows-per-concern | ✅ | ❌ |
| eval-18-convert-from-code | depth-premium-boundaries | ❌ | ✅ |
| eval-18-convert-from-code | rule-falsifiable-by-a-row | ✅ | ❌ |
| eval-18-convert-from-code | title-states-system-behaviour | ✅ | ❌ |
| eval-23-loan-approval-tt | rule-statable-from-table | ✅ | ❌ |
| eval-23-loan-approval-tt | title-states-system-behaviour | ❌ | ✅ |

### Load-Bearing Assertions

Assertions that pass with official skill but fail with next variant:

- eval-15-reis-discount: `zone-independent-counting`
- eval-15-reis-discount: `2.19-depth-all-tiers`
- eval-15-reis-discount: `2.21-readability-relative-time`
- eval-18-convert-from-code: `concerns-decomposed`
- eval-18-convert-from-code: `minimal-rows-per-concern`
- eval-18-convert-from-code: `rule-falsifiable-by-a-row`
- eval-18-convert-from-code: `title-states-system-behaviour`
- eval-23-loan-approval-tt: `rule-statable-from-table`

### Attention Needed

Evals with failed assertions or disproportionate resource usage (>2x official):

| Eval | Issue | Details |
|------|-------|---------|
| eval-15-reis-discount | Failed assertions | `zone-independent-counting`, `2.19-depth-all-tiers`, `2.21-readability-relative-time` |
| eval-18-convert-from-code | Failed assertions | `concerns-decomposed`, `minimal-rows-per-concern`, `rule-falsifiable-by-a-row`, `title-states-system-behaviour` |
| eval-23-loan-approval-tt | Failed assertions | `rule-statable-from-table` |

