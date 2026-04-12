# Eval Review — tabletest variant=minimal, Iteration 2

**Model:** sonnet · **Date:** 2026-04-12 · **Evals:** 19

## Summary

209/251 (83.3%) · 1938151 tokens · 2518.8s · $4.7114

## Delta vs Iteration 1

**Regressions (17):**
- ❌ eval-14-weekly-pay: `separates-classification-and-calculation`
- ❌ eval-18-convert-from-code: `scenario-names-describe-conditions`
- ❌ eval-18-convert-from-code: `description-no-internals`
- ❌ eval-18-convert-from-code: `depth-decision-boundaries`
- ❌ eval-18-convert-from-code: `depth-premium-boundaries`
- ❌ eval-22-event-registration-tt: `validation-includes-optional-fields`
- ❌ eval-22-event-registration-tt: `descriptive-registration-date`
- ❌ eval-22-event-registration-tt: `cutoff-date-column-if-literal-dates`
- ❌ eval-22-event-registration-tt: `discount-column-preferred`
- ❌ eval-22-event-registration-tt: `optional-fields-has-expected-column`
- ❌ eval-23-loan-approval-tt: `description-no-redundant-field-values`
- ❌ eval-25-convert-from-spock: `no-if-switch-in-method`
- ❌ eval-28-convert-from-methodsource: `options-as-map`
- ❌ eval-28-convert-from-methodsource: `no-if-switch-in-method`
- ❌ eval-29-shopping-cart-tt: `type-converters-for-complex-objects`
- ❌ eval-29-shopping-cart-tt: `coupon-as-single-column`
- ❌ eval-29-shopping-cart-tt: `description-not-redundant-with-scenarios`

**Improvements (20):**
- ✅ eval-9-bonus-contractor-structure: `business-language-columns`
- ✅ eval-14-weekly-pay: `1.1-traceability-columns`
- ✅ eval-15-reis-discount: `2.3-depth-tier-boundaries`
- ✅ eval-15-reis-discount: `2.6-readability-human-readable-values`
- ✅ eval-15-reis-discount: `2.16-no-duplicate-tier-mapping`
- ✅ eval-15-reis-discount: `2.10-format-displayname`
- ✅ eval-15-reis-discount: `2.11-format-description`
- ✅ eval-15-reis-discount: `2.12-format-typeconverter`
- ✅ eval-15-reis-discount: `2.13-format-annotation-order`
- ✅ eval-15-reis-discount: `minimal-rows-per-concern`
- ✅ eval-18-convert-from-code: `black-box-columns`
- ✅ eval-18-convert-from-code: `observable-io-only`
- ✅ eval-18-convert-from-code: `business-language-columns`
- ✅ eval-18-convert-from-code: `concerns-decomposed`
- ✅ eval-18-convert-from-code: `minimal-rows-per-concern`
- ✅ eval-18-convert-from-code: `separates-decision-and-premium`
- ✅ eval-23-loan-approval-tt: `scenario-names-describe-conditions`
- ✅ eval-23-loan-approval-tt: `business-language-columns`
- ✅ eval-26-convert-from-kotest: `no-if-switch-in-method`
- ✅ eval-27-convert-from-testng: `options-as-map`

## Resource Comparison vs Iteration 1

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-1-convert-repetitive-tests | 10/10 | 10/10 | 74386 | 45581 | 16.6 | 15.9 |
| eval-2-parse-dates | 10/10 | 13/13 | 47757 | 76796 | 49.2 | 45.1 |
| eval-3-dependency-setup | 4/4 | 4/4 | 45373 | 45166 | 9.4 | 9.8 |
| eval-7-permission-check | 10/11 | 10/11 | 45961 | 103666 | 20.6 | 28.7 |
| eval-8-money-parse | 13/13 | 13/13 | 47402 | 47533 | 44.4 | 47.5 |
| eval-9-bonus-contractor-structure | 11/11 | 9/10 | 74201 | 46715 | 20.9 | 30.7 |
| eval-14-weekly-pay | 15/18 | 16/19 | 328522 | 212356 | 377.0 | 242.8 |
| eval-15-reis-discount | 9/18 | 3/20 | 235426 | 46828 | 253.7 | 94.3 |
| eval-18-convert-from-code | 14/18 | 8/14 | 95293 | 87263 | 197.3 | 126.4 |
| eval-19-convert-from-parameterized | 8/8 | 8/8 | 46361 | 73812 | 23.5 | 16.1 |
| eval-20-collections-and-quoting | 11/11 | 13/13 | 56945 | 59059 | 186.4 | 197.6 |
| eval-22-event-registration-tt | 17/23 | 20/21 | 58772 | 96270 | 214.4 | 212.2 |
| eval-23-loan-approval-tt | 13/16 | 10/14 | 124015 | 152472 | 139.3 | 140.7 |
| eval-25-convert-from-spock | 10/15 | 11/15 | 99672 | 64902 | 156.4 | 241.0 |
| eval-26-convert-from-kotest | 12/15 | 11/15 | 92084 | 60971 | 122.9 | 186.7 |
| eval-27-convert-from-testng | 12/14 | 12/15 | 92942 | 60835 | 125.3 | 184.6 |
| eval-28-convert-from-methodsource | 12/15 | 13/14 | 96649 | 56510 | 150.9 | 126.6 |
| eval-29-shopping-cart-tt | 18/21 | 16/16 | 276390 | 140744 | 410.7 | 549.3 |
| eval-30-order-splitting-tt | T/O | 18/18 | — | 342828 | T/O | 622.5 |

## Per-Eval Results

### ✅ Eval eval-1-convert-repetitive-tests

**10/10** · 74386 tokens · 16599ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Discount?' or 'Expected?')
- ✅ **has-three-data-rows**: Table has exactly 3 data rows (GOLD, SILVER, BRONZE)
- ✅ **single-assertion-in-method**: Test method body contains only one assertEquals (or equivalent assertion) — not three
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest
- ✅ **description-uses-textblock**: If @Description is present and longer than single short line, uses text block (""")

### ✅ Eval eval-2-parse-dates

**10/10** · 47757 tokens · 49186ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **localdate-result-column**: There is a result column typed as LocalDate
- ✅ **type-conversion-addressed**: Response addresses type conversion for non-trivial column types
- ✅ **has-descriptive-title**: Test method has a clear, descriptive title
- ✅ **description-if-present-adds-information**: If @Description present, it adds context beyond table rows; omission is acceptable
- ✅ **annotation-order**: Annotations appear in order: @DisplayName, @Description, @TableTest
- ✅ **empty-string-uses-quotes**: Empty string input uses quoted syntax ('') not blank cell
- ✅ **concerns-decomposed**: Multiple @TableTest methods address distinct concerns
- ✅ **minimal-rows-per-concern**: Each table has only rows needed to express its concern
- ✅ **separates-valid-and-invalid**: Valid parsing cases and error cases in separate @TableTest methods

### ✅ Eval eval-3-dependency-setup

**4/4** · 45373 tokens · 9435ms

- ✅ **flags-junit-version**: Response explicitly flags that JUnit 5.10.0 is below the required minimum (5.11) and recommends upgrading
- ✅ **provides-correct-groupid**: Response provides the tabletest dependency with groupId 'org.tabletest' (not guessed)
- ✅ **provides-artifactid**: Response includes 'tabletest-junit' as the artifactId
- ✅ **includes-test-scope**: The suggested dependency includes <scope>test</scope>

### ⚠️ Eval eval-7-permission-check

**10/11** · 45961 tokens · 20618ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **uses-value-sets**: Table uses value-set syntax (curly braces like {READ, WRITE} or {READ, WRITE, DELETE}) to express rules that hold across multiple actions
- ✅ **fewer-than-nine-rows**: Table has fewer than 9 data rows (i.e. does not enumerate every role/action combination individually)
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ❌ **no-duplicate-role-output**: No two rows share both the same Role value and the same boolean output — forces value set consolidation
  > Row 2 (USER, true) and Row 3 (USER, true) both have Role=USER and Allowed=true
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express; omitting it is acceptable if the table conveys all relevant context
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest
- ✅ **description-uses-textblock**: If @Description is present and longer than one line, it uses triple-quoted string, not concatenation. Passes if @Description is absent.
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Admin performs any action', 'Guest reads') — not outcomes ('Allowed', 'Denied')

### ✅ Eval eval-8-money-parse

**13/13** · 47402 tokens · 44354ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell in the valid inputs @TableTest table
- ✅ **exception-has-expected-column**: The exception/invalid-input table has an expected column specifying the exception type per row
- ✅ **exception-cases-handled**: The exception cases (empty string, letters-only, negative) are handled
- ✅ **result-column-with-question-mark**: Valid inputs have a result column name ending with '?'
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest
- ✅ **description-uses-textblock**: If @Description is present and longer than a single short line, it uses text block
- ✅ **concerns-decomposed**: Multiple tables are used, each addressing a distinct concern
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules
- ✅ **separates-valid-and-invalid**: Valid parsing cases and error/exception cases are in separate @TableTest methods

### ✅ Eval eval-9-bonus-contractor-structure

**11/11** · 74201 tokens · 20877ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **contractor-uses-value-set**: CONTRACTOR row uses a value set for Department (e.g. {SALES, ENGINEERING}) to express 'regardless of department' — not a dummy placeholder like 'ANY' or 'N/A', and not enumerated as separate rows.
- ✅ **four-core-rules-covered**: The four level×department combinations (SENIOR+SALES, SENIOR+ENGINEERING, JUNIOR+SALES, JUNIOR+ENGINEERING) are all present in the table(s)
- ✅ **expects-bonus-percentage**: The expected output column contains bonus percentages (e.g. 15%, 12%, 0%) — not bonus amounts. The method returns a percentage, and the table should reflect that.
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'bonusByLevelAndDepartment' → 'Bonus By Level And Department'). Not a generic name like 'test1' or 'testMethod'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express — such as how bonus rates are applied (e.g. as percentage of base salary), or open questions. Does NOT merely restate the column names or summarise what the rows show. It is acceptable to omit @Description if the table already conveys all relevant context.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Senior in Sales', 'Contractor regardless of department') — not outcomes ('Gets 15%', 'No bonus').
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Level', 'Department', 'Bonus %') — not implementation terms like 'employeeLevel', 'deptCode', 'result'.

### ⚠️ Eval eval-14-weekly-pay

**15/18** · 328522 tokens · 377004ms

- ✅ **1.2-error-has-expected-column**: The error/rejection table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row
- ✅ **1.3-depth-overtime-boundary**: Overtime threshold boundary is covered: at least a value at the threshold (40) and just above (41)
- ✅ **1.4-depth-combined-scenario**: A combined scenario is present: a row with weekday + Sunday + holiday hours
- ❌ **1.5-depth-error-edge-cases**: Error/edge cases covered: at least negative hours (floored at zero) and negative rate (rejected)
  > Negative rate tested; no test with negative hours (minimum across all tables is 0)
- ❌ **1.6-readability-empty-cells**: Sunday/Holiday columns use empty cells (not 0) when irrelevant; parameter types are Integer not int
  > 'Holiday only | 0 | 6' uses 0 not empty; method signatures use 'int' not 'Integer'
- ✅ **1.7-readability-scenario-names**: Scenario names describe work patterns (e.g. 'Part-time', 'Five hours overtime') not test outcomes
- ✅ **1.8-correctness-expected-values**: Expected values in columns are arithmetically correct for every row
- ✅ **1.9-correctness-value-set-semantics**: Value sets only used where all values produce same expected result
- ✅ **1.10-format-displayname**: @DisplayName is present on test methods with clear descriptive titles
- ✅ **1.11-format-description**: @Description provides context beyond table rows (thresholds, multipliers, assumptions)
- ✅ **1.12-format-annotation-order**: Annotations appear in order: @DisplayName, @Description, @TableTest
- ✅ **1.13-format-description-textblock**: Multi-line @Description uses triple-quoted string, not concatenation with +
- ✅ **1.14-depth-zero-rate**: A row where hourly rate is zero, showing pay is zero regardless of hours
- ✅ **1.15-format-clean-method**: Test method body contains no if/ternary logic; null-handling via @TypeConverter
- ✅ **business-language-columns**: Column names use domain language (e.g. 'Weekday hours', 'Hourly rate') not implementation terms
- ✅ **concerns-decomposed**: Multiple @TableTest methods used, each addressing a distinct concern
- ✅ **minimal-rows-per-concern**: Each table has only rows needed to express concern's rules, no unnecessary permutations
- ❌ **separates-classification-and-calculation**: Payable hours categorisation and pay calculation are in separate @TableTest methods
  > No intermediate columns showing categorisation (e.g., 'Regular hours', 'Overtime hours'); all tables combine input→output

### ⚠️ Eval eval-15-reis-discount

**9/18** · 235426 tokens · 253683ms

- ❌ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
  > Single table 'discountPercentByPassengerTypeAndTrips' mixes Passenger type and Prior trips columns; no rolling window table present
- ✅ **2.2-children-flat-discount**: Children's flat 20% discount is represented — either as a row in the eligibility table or a separate note.
- ✅ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers derived from the 'every fifth trip' rule, with boundary values or value sets showing which ticket counts produce which discount. The maximum 40% discount is represented.
- ❌ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
  > No rolling window table exists; no rows testing boundary at 30 vs 31 days
- ❌ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
  > Tier table uses single ticket count values (0, 3, 4, 8, 9, 19, 39, 100) not value sets like {5,6,7,8,9} per tier
- ❌ **2.15-ticket-count-uses-value-sets**: The discount ladder table uses value sets for the ticket count column to group counts within the same tier (e.g. {5, 6, 7, 8, 9} or {5, 9} for min/max → 5%) — not one row per boundary value.
  > 'Prior trips in last 30 days' column: 0, 3, 4, 8, 9, 19, 39, 100 — individual values, not value sets
- ✅ **2.16-no-duplicate-tier-mapping**: The tier-to-discount mapping is expressed once — not repeated across multiple tables.
- ✅ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles.
- ✅ **2.11-format-description**: @Description is present on at least the class or the tier method and provides context beyond what the table rows already express.
- ✅ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **2.14-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +.
- ❌ **2.17-zone-irrelevance-visible**: The eligibility/passenger-type table includes a Zone column (or equivalent) with a value set to show that the discount applies regardless of zone.
  > No Zone column in table; 'Zone has no effect' mentioned only in @Description text, not shown in table structure
- ✅ **2.18-adult-senior-value-set**: Adult and senior are expressed as a value set {ADULT, SENIOR} (or equivalent) in at least one row of the eligibility table.
- ❌ **2.19-depth-all-tiers**: All 9 discount tiers are represented in the ladder table: 0% (tickets 1-4), 5% (5-9), 10% (10-14), 15% (15-19), 20% (20-24), 25% (25-29), 30% (30-34), 35% (35-39), and 40% (40+).
  > Table shows only tiers: 0%, 5%, 10%, 20%, 40%; missing tiers 15%, 25%, 30%, 35%
- ❌ **2.20-readability-one-row-per-tier**: Each tier in the discount ladder is expressed as a single row with a value set for ticket counts — not split across multiple rows.
  > 0% tier: rows 'No prior trips (0)' and 'Below first tier (3)'; 5% tier: 'At first tier (4)' and 'Within first tier (8)' — split for boundary testing
- ❌ **2.21-readability-relative-time**: The rolling window table expresses time relatively (e.g. '14 days ago', '30 days ago', or an integer 'Days ago' column).
  > No rolling window table present in response; 'Prior trips in last 30 days' uses absolute count, not relative time
- ❌ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules.
  > First table mixes 'Passenger type' and 'Prior trips' columns (discount ladder + eligibility); rolling window table absent
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.

### ⚠️ Eval eval-18-convert-from-code

**14/18** · 95293 tokens · 197320ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **black-box-columns**: Table columns represent the method's public inputs (applicant type, age, claim count) and observable outputs (decision, premium) — not internal state like hasActivePolicy, internalRiskScore, or calculateRiskScore.
- ✅ **observable-io-only**: No column is named after private fields or internal variables (hasActivePolicy, internalRiskScore, riskScore). The test treats the method as a black box.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Applicant type', 'Age', 'Claims', 'Decision?', 'Premium?') — not code identifiers like 'applicantType', 'claimCount', 'String'.
- ❌ **scenario-names-describe-conditions**: Scenario names describe conditions ('Renewal with no claims', 'Senior applicant') — not outcomes ('Auto approved', 'Rejected').
  > Scenarios include outcomes: 'Renewal, no claims auto-approved', 'Renewal, high risk score rejected', 'Standard applicant, low risk approved'
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Decision?' or 'Premium?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ✅ **separates-decision-and-premium**: Decision logic (AUTO_APPROVED/REJECTED/APPROVED) and premium calculation are in separate @TableTest methods
- ✅ **no-reimplemented-internals**: Test method body does not recompute internal formulas (e.g. risk score calculation). It only calls the public API (evaluateApplication) and asserts on the return value's fields. No arithmetic duplicating private method logic appears in the test.
- ❌ **description-no-internals**: @Description explains the test's purpose and business context — not internal formulas or implementation details (e.g. 'riskScore = age/10 + claimCount*15', 'rejected when riskScore > 75'). Black-box tests describe observable behaviour, not code internals.
  > First @Description states 'Risk score = (age / 10) + (claimCount × 15)' and 'Rejection threshold: risk score > 75'; second states premium formulas with riskScore
- ❌ **depth-decision-boundaries**: Approval decision table covers the rejection boundary thoroughly: 5 claims triggers rejection regardless of realistic age (risk score crosses 75), 4 claims results in approval. Rows at 4 and 5 claims with varying ages demonstrate this cliff. Senior vs non-senior distinction (64 vs 65) also visible in the decision table.
  > Table has 5-claim rows but no 4-claim row to show approval boundary; age 65 shown but no age 64 for senior threshold comparison
- ❌ **depth-premium-boundaries**: Approved premium table covers boundary effects: the premium jump from 0 to 1 claim (impact of +15 risk score on premium), and the premium difference at age 64 vs 65 (standard vs senior formula). These boundary conditions document how premium changes at each threshold.
  > Table lacks paired rows: no (age X, 0 claims) vs (age X, 1 claim) at same age; no age 64 vs age 65 pair to show senior threshold effect

### ✅ Eval eval-19-convert-from-parameterized

**8/8** · 46361 tokens · 23487ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Valid?' or 'Expected?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Valid standard format', 'Missing local part') — not outcomes ('True', 'False') or 'Test case 1'.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.

### ✅ Eval eval-20-collections-and-quoting

**11/11** · 56945 tokens · 186403ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **empty-list-explicit**: Empty list input uses [] (not a blank cell, which represents null). The distinction between empty list and null is preserved.
- ✅ **special-chars-quoted**: Values containing pipes (|), brackets, or colons (:) are properly quoted (e.g. "tech:java") so they don't conflict with table syntax.
- ✅ **set-syntax-correct**: Set<String> values in the table use curly brace syntax like {tech, dev} — not bracket syntax [tech, dev].
- ✅ **newline-in-cell**: Cell values containing newlines use \\n escaping — not literal line breaks that would break the table row structure.
- ✅ **pipe-quoted**: Values containing pipe characters (|) are quoted so they don't conflict with the markdown table column separator.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Filtered tags?' or 'Result?')
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +.

### ⚠️ Eval eval-22-event-registration-tt

**17/23** · 58772 tokens · 214370ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **validation-rules-covered**: Email validation and name-required error scenarios are present — at least one row for invalid email and one for missing name.
- ✅ **blank-for-absent-optional**: Dietary requirements and/or accessibility needs use blank cells (null) when not provided — not 'N/A' or 'none'.
- ✅ **blank-vs-value-set-correct**: When testing pricing rules, irrelevant inputs use value sets or representative values — not blanks.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **business-language-columns**: Column names use domain/business language, not code identifiers.
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes.
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Test method has either @DisplayName or descriptive method name
- ✅ **description-uses-textblock**: If @Description is present and longer than one line, use text block (triple quotes), not concatenation.
- ✅ **concerns-decomposed**: Multiple tables address distinct concerns, not one monolithic table.
- ✅ **minimal-rows-per-concern**: Each table has only rows needed to express its concern's rules.
- ✅ **separates-validation-and-pricing**: Input validation and pricing are in separate @TableTest methods
- ❌ **validation-includes-optional-fields**: Validation @TableTest includes Dietary Requirements and Accessibility Needs columns, not hardcoded as null.
  > Validation method hardcodes optional fields: 'null, null, LocalDate.of(2025, 4, 1), 1'. Separate table used instead.
- ❌ **descriptive-registration-date**: Registration date uses descriptive values (e.g. 'before cutoff'), not raw date literals like '2025-02-28'.
  > Table includes literal dates: '2025-03-01' and '2025-02-28' alongside descriptive values
- ❌ **cutoff-date-column-if-literal-dates**: If literal dates used, early-bird cutoff date appears as separate policy column
  > Literal dates present ('2025-02-28', '2025-03-01') but no cutoff-date column; cutoff only in @Description
- ❌ **description-no-irrelevant-information**: @Description does not restate discount rules already visible in table or list fixed input values.
  > Restates rules: 'Early-bird: 20% off...', 'Group: 15% off...' and lists values: 'name is "Alice Smith"...'
- ❌ **discount-column-preferred**: Output is 'Discount?' not 'Price?'; or if 'Price?' used, both 'Base price' and 'Price?' columns present.
  > Uses 'Price?' column without 'Base price' column; base price (£100) only in @Description
- ✅ **single-assertion-in-method**: Each @TableTest method has a single, uniform assertion pattern for all rows.
- ❌ **optional-fields-has-expected-column**: Optional-fields table includes output/expectation column asserting acceptance
  > acceptsOptionalFields table has no output column; only columns: Scenario, Dietary Requirements, Accessibility Needs
- ✅ **description-no-redundant-field-values**: Scenario names do not redundantly include specific field values visible in table columns.

### ⚠️ Eval eval-23-loan-approval-tt

**13/16** · 124015 tokens · 139266ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **threshold-as-column**: Policy thresholds (650/600 credit scores) appear in a dedicated policy column (e.g. 'Credit threshold') separate from the applicant's actual credit score column. The reader sees both the threshold and the score, making the comparison explicit. Not just threshold values used as data in the score column.
  > Tables have only 'Credit Score' column with actual scores (651, 650, 750). No separate 'Credit threshold' column exists. Thresholds appear only in scenario names and @Description text.
- ✅ **concrete-domain-values**: Cell values use concrete domain terms (e.g. '70', '650', 'APPROVED') — not abstract codes like 'CATEGORY_A' or '1'. Boolean true/false is acceptable for yes/no flags when the parameter type is boolean.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Senior at lower threshold', 'Missing income') — not outcomes ('Approved', 'Rejected').
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Age', 'Credit score', 'Stable income', 'Decision?') — not code identifiers like 'customerAge', 'boolean'.
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Decision?' or 'Status?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ❌ **separates-age-credit-income**: Age boundary policy, credit score categorisation, and income status are separated into distinct @TableTest methods, with a method combining these for the expected verdict
  > Three methods test age boundary, credit score, and income separately, but there is no fourth method combining all three into an overall verdict.
- ❌ **description-no-redundant-field-values**: Scenario descriptions and @Description text do not include specific field values that are already visible in the table or that serve as 'any valid value' placeholders. Stating 'Customer age is 35 for all rows' in @Description when age is fixed in the method body, or including fixed values in scenario names that are already columns, adds maintenance burden without value. Describe the scenario condition or policy being tested instead.
  > @Description: 'Customer age is 35 for all rows (non-senior)' — this is the exact example given in the assertion as redundant when age is already fixed in method body.
- ✅ **depth-stable-income-effect**: The standardApplicantCreditThreshold table (or equivalent non-senior table) clearly documents the effect of stable income: rows show that above-threshold credit score + stable income = APPROVED, and above-threshold credit score + no stable income = REJECTED. The stable income parameter's effect on the outcome must be visible from the table rows, not just implied.

### ⚠️ Eval eval-25-convert-from-spock

**10/15** · 99672 tokens · 156439ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells.
  > | fragile | insuredValue | Cost? and | handling | Cost? keep options as separate columns, not collapsed map
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
  > No @TypeConverter method present in output
- ❌ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table — not as three separate length/width/height columns
  > | weight | length | width | height | Cost? shows three separate dimension columns, not [L, W, H]
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
- ✅ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern — not one monolithic table mixing base rates, surcharges, dimensional weight, and carrier equivalence
- ❌ **numeric-types-correct**: Weight and cost parameters use BigDecimal (not double or String). Dimensions use Integer or int.
  > double weight, double expectedCost (should be BigDecimal). Dimensions use int, which is correct.
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('EU express, light package', 'Oversized with fragile') — not outcomes ('8.00', 'Surcharge applied') or generic labels ('Test 1').
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ✅ **business-language-columns**: Column names use domain language ('Zone', 'Weight (kg)', 'Dimensions', 'Options', 'Cost?') — not code identifiers ('shippingZone', 'weightKg', 'dims')
- ❌ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
  > fragilAndInsuranceSurcharges and specialHandlingSurcharges contain if (insuredValue != null) and if (handling != null)
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **no-groovy-syntax**: Output contains no Groovy or Spock syntax: no def, where:, expect:, given:, GString interpolation (#variable), Spock assertions (thrown(), old()), or Groovy list literals ([] as Set).

### ⚠️ Eval eval-26-convert-from-kotest

**12/15** · 92084 tokens · 122874ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options are collapsed into a single map column like [fragile: true, insuredValue: 500]
  > packageSurcharges table has three separate columns: 'Fragile', 'Insured Value', 'Handling' — not collapsed into one map column
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> and returns PackageOptions
  > No @TypeConverter method appears in the code; PackageOptions constructed in method body instead
- ✅ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX}
- ✅ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern
- ✅ **numeric-types-correct**: Weight and cost parameters use BigDecimal; Dimensions use Integer or int
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes or generic labels
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **business-language-columns**: Column names use domain language, not code identifiers
- ✅ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
- ✅ **annotation-order**: Annotations in order: @DisplayName, @Description (if present), @TableTest
- ✅ **has-descriptive-title**: Each test method has @DisplayName or clear method name
- ❌ **no-kotest-syntax**: Output contains no Kotest syntax (no shouldBe, forAll, etc.)
  > Code imports 'io.kotest.matchers.shouldBe' and uses 'shouldBe' operator throughout test assertions

### ⚠️ Eval eval-27-convert-from-testng

**12/14** · 92942 tokens · 125342ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> and returns PackageOptions
  > Only @TypeConverter present is parseShippingZone; no Map-to-PackageOptions converter shown
- ✅ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX}
- ✅ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern
- ✅ **numeric-types-correct**: Weight and cost parameters use BigDecimal; Dimensions use Integer or int
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes or generic labels
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **business-language-columns**: Column names use domain language, not code identifiers
- ❌ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
  > packageOptionSurcharges() has: if (insuredValue != null) and if (handling != null) statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Each test method has @DisplayName annotation or descriptive method name
- ✅ **no-testng-artifacts**: Output contains no TestNG syntax

### ⚠️ Eval eval-28-convert-from-methodsource

**12/15** · 96649 tokens · 150889ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column
  > Fragile | Insured value | Handling columns are separate with individual cells, not [fragile: true, insuredValue: 500] format
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> and returns PackageOptions
  > Only @TypeConverter shown is for ShippingZone; no Map→PackageOptions converter present
- ✅ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX}
- ✅ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern
- ✅ **numeric-types-correct**: Weight and cost use BigDecimal; dimensions use Integer or int
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes or generic labels
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **business-language-columns**: Column names use domain language, not code identifiers
- ❌ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
  > packageOptionSurcharges has: if (insuredValue != null) ... if (handling != null)
- ✅ **annotation-order**: Annotations appear in order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Each test method has descriptive @DisplayName or method name
- ✅ **no-methodsource-artifacts**: Output contains no @MethodSource, Stream<Arguments>, or Jupiter params

### ⚠️ Eval eval-29-shopping-cart-tt

**18/21** · 276390 tokens · 410687ms

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
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Product', 'Quantity', 'Message?', 'Cart total?') — not code identifiers like 'productId', 'result'.
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **single-assertion-in-method**: Each @TableTest method has a single, uniform assertion pattern applied to all rows — not different assertions per scenario.
- ✅ **test-data-visible**: Product prices and coupon rules (type, amount, expiry status) are visible in the table via columns, @Description, or explicit setup — not silently hardcoded. The reader can trace how inputs lead to outputs.
- ❌ **type-converters-for-complex-objects**: TypeConverter methods are used to convert map/string table values into domain objects (e.g. Map → Cart, Map → InventoryService, map → Coupon). Test method bodies contain only arrange-act-assert — no object construction from raw table values.
  > Test bodies construct: 'Cart cart = new Cart(cartItems);' and 'new StubInventoryService(stockLevels)' — direct construction, not @TypeConverter methods
- ❌ **coupon-as-single-column**: Coupon data is expressed in a single column with a @TypeConverter — not spread across separate sparse columns for coupon type, value, and target product. The column format may be map syntax (e.g. [type: PERCENTAGE, value: 10]) or readable shorthand (e.g. '20% cart', '$5 off', '50% off widget').
  > couponDiscountTypes table has three coupon columns: 'Coupon Type | Value | Target Product' (not single column)
- ✅ **uses-standard-map-syntax**: Columns representing maps (e.g. cart contents, product catalogue, inventory levels) use standard TableTest map syntax — [k: v, k2: v2] for entries and [:] for empty map — not a custom bracket notation. Custom string-parsing TypeConverters are not needed for data that maps naturally to Map<String, X>.
- ❌ **description-not-redundant-with-scenarios**: @Description annotations do not repeat information that the scenario names already convey. Coupon type behavior (what PERCENTAGE/FIXED/PRODUCT mean) should be expressed through descriptive scenario names, not duplicated in the description.
  > @Description: 'For PERCENTAGE...Value is percentage points' / 'For FIXED, Value is a monetary amount' repeats what scenario names 'Percentage off whole cart' / 'Fixed amount off' already convey

## Variant vs Official (iterations 29, 28, 27, 26, 25 merged)

### Per-Eval Resource Comparison

| Eval | Pass (off) | Pass (var) | Tokens (off) | Tokens (var) | Tok Δ | Cost (off) | Cost (var) | Cost Δ | Time (off) | Time (var) | Time Δ |
|------|------------|------------|--------------|--------------|-------|------------|------------|--------|------------|------------|--------|
| eval-1-convert-repetitive-tests | 10/10 | 10/10 | 66658 | 74386 | +12% | $0.0789 | $0.0951 | +21% | 16.6s | 16.6s | +-0% |
| eval-2-parse-dates | 12/13 | 10/10 | 75285 | 47757 | -37% | $0.1649 | $0.1154 | -30% | 59.6s | 49.2s | -17% |
| eval-3-dependency-setup | 4/4 | 4/4 | 66366 | 45373 | -32% | $0.0747 | $0.0792 | +6% | 16.7s | 9.4s | -44% |
| eval-7-permission-check | 10/11 | 10/11 | 70730 | 45961 | -35% | $0.1065 | $0.1093 | +3% | 41.6s | 20.6s | -50% |
| eval-8-money-parse | 13/13 | 13/13 | 105414 | 47402 | -55% | $0.1595 | $0.1104 | -31% | 57.7s | 44.4s | -23% |
| eval-9-bonus-contractor-structure | 11/11 | 11/11 | 71239 | 74201 | +4% | $0.1170 | $0.0970 | -17% | 48.5s | 20.9s | -57% |
| eval-14-weekly-pay | 15/18 | 15/18 | 194257 | 328522 | +69% | $0.3672 | $0.6242 | +70% | 210.2s | 377.0s | +79% |
| eval-15-reis-discount | 6/18 | 9/18 | 155937 | 235426 | +51% | $0.3412 | $0.4236 | +24% | 147.1s | 253.7s | +72% |
| eval-18-convert-from-code | 12/18 | 14/18 | 76891 | 95293 | +24% | $0.1780 | $0.3485 | +96% | 99.9s | 197.3s | +98% |
| eval-19-convert-from-parameterized | 8/8 | 8/8 | 116788 | 46361 | -60% | $0.0997 | $0.1134 | +14% | 21.7s | 23.5s | +8% |
| eval-20-collections-and-quoting | 12/13 | 11/11 | 166154 | 56945 | -66% | $0.3946 | $0.2493 | -37% | 229.3s | 186.4s | -19% |
| eval-22-event-registration-tt | 19/23 | 17/23 | 82502 | 58772 | -29% | $0.2826 | $0.2788 | -1% | 223.3s | 214.4s | -4% |
| eval-23-loan-approval-tt | 10/16 | 13/16 | 112878 | 124015 | +10% | $0.2445 | $0.2587 | +6% | 152.3s | 139.3s | -9% |
| eval-25-convert-from-spock | 9/15 | 10/15 | 104664 | 99672 | -5% | $0.2157 | $0.3030 | +40% | 127.7s | 156.4s | +22% |
| eval-26-convert-from-kotest | 10/15 | 12/15 | 118622 | 92084 | -22% | $0.2558 | $0.2619 | +2% | 147.8s | 122.9s | -17% |
| eval-27-convert-from-testng | 10/15 | 12/14 | 113461 | 92942 | -18% | $0.2117 | $0.2564 | +21% | 105.3s | 125.3s | +19% |
| eval-28-convert-from-methodsource | 12/15 | 12/15 | 176079 | 96649 | -45% | $0.2852 | $0.2798 | -2% | 167.5s | 150.9s | -10% |
| eval-29-shopping-cart-tt | 20/21 | 18/21 | 323713 | 276390 | -15% | $1.1285 | $0.7074 | -37% | 838.6s | 410.7s | -51% |
| **Totals (18 comparable)** | **203/257** | **209/251** | **2197638** | **1938151** | **-12%** | **$4.7062** | **$4.7114** | **+0%** | **2711.5s** | **2518.8s** | **-7%** |

**Comparable summary (18 evals in both):**

| Source | Pass Rate | Tokens | Cost | Time |
|--------|-----------|--------|------|------|
| minimal (iter 2) | 209/251 (83.3%) | 1938151 | $4.7114 | 2518.8s |
| official | 203/257 (79.0%) | 2197638 | $4.7062 | 2711.5s |
| **Δ** | | **-12%** | **+0%** | **-7%** |

### Per-Assertion Comparison

| Eval | Assertion | official | minimal |
|------|-----------|----------|---------|
| eval-2-parse-dates | separates-valid-and-invalid | ❌ | ✅ |
| eval-14-weekly-pay | 1.11-format-description | ❌ | ✅ |
| eval-14-weekly-pay | separates-classification-and-calculation | ✅ | ❌ |
| eval-15-reis-discount | 2.16-no-duplicate-tier-mapping | ❌ | ✅ |
| eval-15-reis-discount | 2.11-format-description | ❌ | ✅ |
| eval-15-reis-discount | 2.18-adult-senior-value-set | ❌ | ✅ |
| eval-15-reis-discount | 2.19-depth-all-tiers | ✅ | ❌ |
| eval-15-reis-discount | minimal-rows-per-concern | ❌ | ✅ |
| eval-18-convert-from-code | scenario-names-describe-conditions | ✅ | ❌ |
| eval-18-convert-from-code | concerns-decomposed | ❌ | ✅ |
| eval-18-convert-from-code | minimal-rows-per-concern | ❌ | ✅ |
| eval-18-convert-from-code | separates-decision-and-premium | ❌ | ✅ |
| eval-20-collections-and-quoting | special-chars-quoted | ❌ | ✅ |
| eval-22-event-registration-tt | validation-includes-optional-fields | ✅ | ❌ |
| eval-22-event-registration-tt | optional-fields-has-expected-column | ✅ | ❌ |
| eval-23-loan-approval-tt | scenario-names-describe-conditions | ❌ | ✅ |
| eval-23-loan-approval-tt | concerns-decomposed | ❌ | ✅ |
| eval-23-loan-approval-tt | minimal-rows-per-concern | ❌ | ✅ |
| eval-25-convert-from-spock | concerns-decomposed | ❌ | ✅ |
| eval-26-convert-from-kotest | concerns-decomposed | ❌ | ✅ |
| eval-26-convert-from-kotest | no-if-switch-in-method | ❌ | ✅ |
| eval-27-convert-from-testng | concerns-decomposed | ❌ | ✅ |
| eval-27-convert-from-testng | has-descriptive-title | ❌ | ✅ |
| eval-29-shopping-cart-tt | type-converters-for-complex-objects | ✅ | ❌ |
| eval-29-shopping-cart-tt | coupon-as-single-column | ✅ | ❌ |
| eval-29-shopping-cart-tt | uses-standard-map-syntax | ❌ | ✅ |
| eval-29-shopping-cart-tt | description-not-redundant-with-scenarios | ✅ | ❌ |

### Load-Bearing Assertions

Assertions that pass with official skill but fail with minimal variant:

- eval-14-weekly-pay: `separates-classification-and-calculation`
- eval-15-reis-discount: `2.19-depth-all-tiers`
- eval-18-convert-from-code: `scenario-names-describe-conditions`
- eval-22-event-registration-tt: `validation-includes-optional-fields`
- eval-22-event-registration-tt: `optional-fields-has-expected-column`
- eval-29-shopping-cart-tt: `type-converters-for-complex-objects`
- eval-29-shopping-cart-tt: `coupon-as-single-column`
- eval-29-shopping-cart-tt: `description-not-redundant-with-scenarios`

### Attention Needed

Evals with failed assertions or disproportionate resource usage (>2x official):

| Eval | Issue | Details |
|------|-------|---------|
| eval-14-weekly-pay | Failed assertions | `separates-classification-and-calculation` |
| eval-15-reis-discount | Failed assertions | `2.19-depth-all-tiers` |
| eval-18-convert-from-code | Failed assertions | `scenario-names-describe-conditions` |
| eval-22-event-registration-tt | Failed assertions | `validation-includes-optional-fields`, `optional-fields-has-expected-column` |
| eval-29-shopping-cart-tt | Failed assertions | `type-converters-for-complex-objects`, `coupon-as-single-column`, `description-not-redundant-with-scenarios` |

