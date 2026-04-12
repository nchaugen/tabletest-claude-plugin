# Eval Review — tabletest variant=minimal, Iteration 3

**Model:** sonnet · **Date:** 2026-04-12 · **Evals:** 19

## Summary

208/239 (87.0%) · 1529669 tokens · 2168.5s · $4.1031

## Delta vs Iteration 2

**Regressions (11):**
- ❌ eval-7-permission-check: `uses-value-sets`
- ❌ eval-7-permission-check: `fewer-than-nine-rows`
- ❌ eval-7-permission-check: `has-descriptive-title`
- ❌ eval-7-permission-check: `scenario-names-describe-conditions`
- ❌ eval-22-event-registration-tt: `scenario-names-describe-conditions`
- ❌ eval-25-convert-from-spock: `concerns-decomposed`
- ❌ eval-25-convert-from-spock: `business-language-columns`
- ❌ eval-25-convert-from-spock: `has-descriptive-title`
- ❌ eval-26-convert-from-kotest: `business-language-columns`
- ❌ eval-27-convert-from-testng: `options-as-map`
- ❌ eval-27-convert-from-testng: `concerns-decomposed`

**Improvements (10):**
- ✅ eval-18-convert-from-code: `description-no-internals`
- ✅ eval-22-event-registration-tt: `validation-includes-optional-fields`
- ✅ eval-22-event-registration-tt: `descriptive-registration-date`
- ✅ eval-22-event-registration-tt: `cutoff-date-column-if-literal-dates`
- ✅ eval-22-event-registration-tt: `optional-fields-has-expected-column`
- ✅ eval-23-loan-approval-tt: `description-no-redundant-field-values`
- ✅ eval-27-convert-from-testng: `no-if-switch-in-method`
- ✅ eval-29-shopping-cart-tt: `type-converters-for-complex-objects`
- ✅ eval-29-shopping-cart-tt: `coupon-as-single-column`
- ✅ eval-29-shopping-cart-tt: `description-not-redundant-with-scenarios`

## Resource Comparison vs Iteration 2

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-1-convert-repetitive-tests | 10/10 | 10/10 | 44278 | 74386 | 13.2 | 16.6 |
| eval-2-parse-dates | 13/13 | 10/10 | 46136 | 47757 | 47.1 | 49.2 |
| eval-3-dependency-setup | 4/4 | 4/4 | 43867 | 45373 | 8.3 | 9.4 |
| eval-7-permission-check | 6/11 | 10/11 | 70919 | 45961 | 16.6 | 20.6 |
| eval-8-money-parse | 13/13 | 13/13 | 74739 | 47402 | 48.1 | 44.4 |
| eval-9-bonus-contractor-structure | 11/11 | 11/11 | 44635 | 74201 | 21.6 | 20.9 |
| eval-14-weekly-pay | T/O | 15/18 | — | 328522 | T/O | 377.0 |
| eval-15-reis-discount | T/O | 9/18 | — | 235426 | T/O | 253.7 |
| eval-18-convert-from-code | 15/18 | 14/18 | 54127 | 95293 | 131.9 | 197.3 |
| eval-19-convert-from-parameterized | 8/8 | 8/8 | 44737 | 46361 | 18.7 | 23.5 |
| eval-20-collections-and-quoting | 13/13 | 11/11 | 127350 | 56945 | 184.0 | 186.4 |
| eval-22-event-registration-tt | 20/23 | 17/23 | 94849 | 58772 | 220.3 | 214.4 |
| eval-23-loan-approval-tt | 14/16 | 13/16 | 103192 | 124015 | 83.0 | 139.3 |
| eval-25-convert-from-spock | 7/15 | 10/15 | 50752 | 99672 | 79.3 | 156.4 |
| eval-26-convert-from-kotest | 11/15 | 12/15 | 56106 | 92084 | 136.4 | 122.9 |
| eval-27-convert-from-testng | 12/15 | 12/14 | 52219 | 92942 | 87.4 | 125.3 |
| eval-28-convert-from-methodsource | 12/15 | 12/15 | 93296 | 96649 | 134.4 | 150.9 |
| eval-29-shopping-cart-tt | 21/21 | 18/21 | 218477 | 276390 | 249.2 | 410.7 |
| eval-30-order-splitting-tt | 18/18 | — | 309990 | — | 689.0 | — |

## Per-Eval Results

### ✅ Eval eval-1-convert-repetitive-tests

**10/10** · 44278 tokens · 13170ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?'
- ✅ **has-three-data-rows**: Table has exactly 3 data rows (GOLD, SILVER, BRONZE)
- ✅ **single-assertion-in-method**: Test method body contains only one assertEquals
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario column as leftmost column
- ✅ **has-descriptive-title**: Method name reads as descriptive title when converted
- ✅ **description-if-present-adds-information**: If @Description present, adds context beyond table rows
- ✅ **annotation-order**: Annotations in order: @DisplayName, @Description, @TableTest
- ✅ **description-uses-textblock**: If @Description longer than one line, uses text block

### ✅ Eval eval-2-parse-dates

**13/13** · 46136 tokens · 47056ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell (not the string 'null') in the table
- ✅ **exception-handled-cleanly**: The empty string / exception case is handled — either via a Throws? column, assertThrows inside the method, or a separate @TableTest
- ✅ **localdate-result-column**: There is a result column typed as LocalDate (or using a string representation that maps to LocalDate)
- ✅ **type-conversion-addressed**: The response addresses type conversion for non-trivial column types — either by providing a @TypeConverter/converter method, or by using a representation that leverages built-in conversion
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express. It is acceptable to omit @Description if the table already conveys all relevant context
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string). Passes if @Description is absent
- ✅ **empty-string-uses-quotes**: Empty string input uses quoted syntax (e.g. '' or "") in the table — not a blank cell
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations
- ✅ **separates-valid-and-invalid**: Valid parsing cases and error/exception cases are in separate @TableTest methods

### ✅ Eval eval-3-dependency-setup

**4/4** · 43867 tokens · 8278ms

- ✅ **flags-junit-version**: Response explicitly flags that JUnit 5.10.0 is below the required minimum (5.11) and recommends upgrading
- ✅ **provides-correct-groupid**: Response provides the tabletest dependency with groupId 'org.tabletest' (not guessed)
- ✅ **provides-artifactid**: Response includes 'tabletest-junit' as the artifactId
- ✅ **includes-test-scope**: The suggested dependency includes <scope>test</scope>

### ⚠️ Eval eval-7-permission-check

**6/11** · 70919 tokens · 16621ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **uses-value-sets**: Table uses value-set syntax (curly braces like {READ, WRITE} or {READ, WRITE, DELETE}) to express rules that hold across multiple actions
  > All rows use explicit single actions (READ, WRITE, DELETE), no curly braces like {READ, WRITE, DELETE}
- ❌ **fewer-than-nine-rows**: Table has fewer than 9 data rows (i.e. does not enumerate every role/action combination individually)
  > Exactly 9 data rows: Admin reads, Admin writes, Admin deletes, User reads, User writes, User cannot delete, Guest reads, Guest cannot write, Guest cannot delete
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ❌ **no-duplicate-role-output**: No two rows share both the same Role value and the same boolean output
  > ADMIN with true: 3 rows; USER with true: 2 rows; GUEST with false: 2 rows
- ❌ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case
  > Method name 'checkPermission' converts to 'Check Permission', which is generic like the disallowed example 'canPerform'
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block
- ❌ **scenario-names-describe-conditions**: Scenario names describe conditions ('Admin performs any action', 'Guest reads') — not outcomes ('Allowed', 'Denied')
  > Names like 'User cannot delete' and 'Guest cannot write' describe outcomes, not conditions like 'User deletes'

### ✅ Eval eval-8-money-parse

**13/13** · 74739 tokens · 48060ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell in the valid inputs @TableTest table (not the string 'null', and not as a separate @Test method). The blank cell maps to a null parameter.
- ✅ **exception-has-expected-column**: The exception/invalid-input table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
- ✅ **exception-cases-handled**: The exception cases (empty string, letters-only, negative) are handled — via a Throws? column, assertThrows in the method body, or a separate @TableTest — not silently omitted
- ✅ **result-column-with-question-mark**: Valid inputs have a result column name ending with '?' (e.g. 'Money?' or 'Result?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'moneyParsing' → 'Money Parsing'). Not a generic name like 'test1' or 'testMethod'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express — such as the expected currency format, or open questions. Does NOT merely restate the column names or summarise what the rows show. It is acceptable to omit @Description if the table already conveys all relevant context.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ✅ **separates-valid-and-invalid**: Valid parsing cases and error/exception cases are in separate @TableTest methods

### ✅ Eval eval-9-bonus-contractor-structure

**11/11** · 44635 tokens · 21647ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **contractor-uses-value-set**: CONTRACTOR row uses a value set for Department (e.g. {SALES, ENGINEERING}) to express 'regardless of department' — not a dummy placeholder like 'ANY' or 'N/A', and not enumerated as separate rows.
- ✅ **four-core-rules-covered**: The four level×department combinations (SENIOR+SALES, SENIOR+ENGINEERING, JUNIOR+SALES, JUNIOR+ENGINEERING) are all present in the table(s)
- ✅ **expects-bonus-percentage**: The expected output column contains bonus percentages (e.g. 15%, 12%, 0%) — not bonus amounts. The method returns a percentage, and the table should reflect that.
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express. It is acceptable to omit @Description if the table already conveys all relevant context.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Senior in Sales', 'Contractor regardless of department') — not outcomes ('Gets 15%', 'No bonus').
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Level', 'Department', 'Bonus %') — not implementation terms like 'employeeLevel', 'deptCode', 'result'.

### ⚠️ Eval eval-18-convert-from-code

**15/18** · 54127 tokens · 131888ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **black-box-columns**: Table columns represent the method's public inputs (applicant type, age, claim count) and observable outputs (decision, premium) — not internal state like hasActivePolicy, internalRiskScore, or calculateRiskScore.
- ✅ **observable-io-only**: No column is named after private fields or internal variables (hasActivePolicy, internalRiskScore, riskScore). The test treats the method as a black box.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Applicant type', 'Age', 'Claims', 'Decision?', 'Premium?') — not code identifiers like 'applicantType', 'claimCount', 'String'.
- ❌ **scenario-names-describe-conditions**: Scenario names describe conditions ('Renewal with no claims', 'Senior applicant') — not outcomes ('Auto approved', 'Rejected').
  > "Standard applicant approved", "Senior applicant approved" include outcome "approved"
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Decision?' or 'Premium?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ✅ **separates-decision-and-premium**: Decision logic (AUTO_APPROVED/REJECTED/APPROVED) and premium calculation are in separate @TableTest methods
- ✅ **no-reimplemented-internals**: Test method body does not recompute internal formulas (e.g. risk score calculation). It only calls the public API (evaluateApplication) and asserts on the return value's fields. No arithmetic duplicating private method logic appears in the test.
- ✅ **description-no-internals**: @Description explains the test's purpose and business context — not internal formulas or implementation details (e.g. 'riskScore = age/10 + claimCount*15', 'rejected when riskScore > 75'). Black-box tests describe observable behaviour, not code internals.
- ❌ **depth-decision-boundaries**: Approval decision table covers the rejection boundary thoroughly: 5 claims triggers rejection regardless of realistic age (risk score crosses 75), 4 claims results in approval. Rows at 4 and 5 claims with varying ages demonstrate this cliff. Senior vs non-senior distinction (64 vs 65) also visible in the decision table.
  > Claims: 0, 5, 0, 0, 2 (no 4-claim); ages: 40, 40, 30, 70, 40 (no 64/65)
- ❌ **depth-premium-boundaries**: Approved premium table covers boundary effects: the premium jump from 0 to 1 claim (impact of +15 risk score on premium), and the premium difference at age 64 vs 65 (standard vs senior formula). These boundary conditions document how premium changes at each threshold.
  > Claims values 0, 0, 2, 2 (missing 0 vs 1 jump); ages show 64/65 boundary

### ✅ Eval eval-19-convert-from-parameterized

**8/8** · 44737 tokens · 18729ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions not outcomes
- ✅ **annotation-order**: Annotations appear in correct order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Test method has descriptive title via @DisplayName or method name
- ✅ **description-uses-textblock**: @Description uses text block if present and multi-line

### ✅ Eval eval-20-collections-and-quoting

**13/13** · 127350 tokens · 184018ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **list-syntax-correct**: List values in the table use bracket syntax like [tech:java, biz:sales, dev:ci] — not comma-separated strings without brackets.
- ✅ **empty-list-explicit**: Empty list input uses [] (not a blank cell, which represents null). The distinction between empty list and null is preserved.
- ✅ **special-chars-quoted**: Values containing pipes (|), brackets, or colons (:) are properly quoted (e.g. "tech:java") so they don't conflict with table syntax. Colons without quoting would be mis-interpreted as map key:value entries.
- ✅ **set-syntax-correct**: Set<String> values in the table use curly brace syntax like {tech, dev} — not bracket syntax [tech, dev] which is for lists. The distinction between Set and List types is preserved in the table notation.
- ✅ **newline-in-cell**: Cell values containing newlines use \n escaping — not literal line breaks that would break the table row structure.
- ✅ **pipe-quoted**: Values containing pipe characters (|) are quoted so they don't conflict with the markdown table column separator. For example, a tag like 'biz:hr|recruiting' must be quoted.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Filtered tags?' or 'Result?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.

### ⚠️ Eval eval-22-event-registration-tt

**20/23** · 94849 tokens · 220285ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **validation-rules-covered**: Email validation and name-required error scenarios are present — at least one row for invalid email and one for missing name.
- ✅ **blank-for-absent-optional**: Dietary requirements and/or accessibility needs use blank cells (null) when not provided — not 'N/A' or 'none'. These are genuinely optional and absent.
- ✅ **blank-vs-value-set-correct**: When testing pricing rules, irrelevant inputs (e.g. dietary requirements don't affect price) use value sets or representative values — not blanks (which mean null). Blanks are reserved for genuinely absent/null inputs.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Result?', 'Price?')
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Name', 'Email', 'Registration date', 'Group size', 'Price?') — not code identifiers.
- ❌ **scenario-names-describe-conditions**: Scenario names describe conditions ('Early-bird single attendee', 'Missing name') — not outcomes ('Rejected', '20% off').
  > 'No discount' scenario names the outcome (no discount applied) rather than the conditions (after cutoff, single attendee) that produce it
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
  > Pricing @Description states 'Early-bird (20% off) applies...' and 'Group discount (15% off) applies...' and 'higher discount wins' — all demonstrated by table rows
- ❌ **discount-column-preferred**: Output column is 'Discount?' (percentage or amount off) rather than 'Price?' — making verification simpler (discount is the rule's direct output; price requires knowing the base price). If 'Price?' is used instead, both 'Base price' and 'Price?' columns must be present so the reader can trace the calculation.
  > Pricing table uses 'Price?' column but lacks 'Base price' column; base price appears in @Description text, not as a table column
- ✅ **single-assertion-in-method**: Each @TableTest method has a single, uniform assertion pattern applied to all rows — not different assertions per scenario. Validation checks one thing (e.g. error message or acceptance status); pricing checks one thing (e.g. discount or price). No if/switch in assertion logic.
- ✅ **optional-fields-has-expected-column**: The optional-fields acceptance table includes an expectation/output column (e.g. 'Approved?', 'Result?', or 'Status?') asserting that each combination is accepted — not just varying inputs without any expected column.
- ✅ **description-no-redundant-field-values**: Scenario descriptions do not include specific field values that are already visible in the table or that serve as 'any valid value' placeholders. Stating 'name: John, email: john@example.com, registration date: 2025-04-01' in a description adds maintenance burden without value — it suffices to say 'valid values supplied' or describe the scenario condition. Applies to all tables.

### ⚠️ Eval eval-23-loan-approval-tt

**14/16** · 103192 tokens · 83016ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **threshold-as-column**: Policy thresholds (650/600 credit scores) appear in a dedicated policy column (e.g. 'Credit threshold') separate from the applicant's actual credit score column
  > Tables show only 'Credit Score' column with applicant values (700, 625, 600, etc.). No dedicated 'Credit Threshold' or similar column showing threshold values 650 or 600.
- ✅ **concrete-domain-values**: Cell values use concrete domain terms (e.g. '70', '650', 'APPROVED') — not abstract codes like 'CATEGORY_A' or '1'
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Senior at lower threshold', 'Missing income') — not outcomes ('Approved', 'Rejected')
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Age', 'Credit score', 'Stable income', 'Decision?') — not code identifiers like 'customerAge', 'boolean'
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Decision?' or 'Status?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns
- ❌ **separates-age-credit-income**: Age boundary policy, credit score categorisation, and income status are separated into distinct @TableTest methods, with a method combining these for the expected verdict
  > Response has 3 methods grouped by applicant profile (standard, senior, missing income), not 4+ methods decomposed by policy dimension (age, credit, income separately + combining). Response explicitly states decomposition into applicant concerns, not policy dimensions.
- ✅ **description-no-redundant-field-values**: Scenario descriptions and @Description text do not include specific field values that are already visible in the table or that serve as 'any valid value' placeholders
- ✅ **depth-stable-income-effect**: The standard applicant table clearly documents the effect of stable income: rows show that above-threshold credit score + stable income = APPROVED, and above-threshold credit score + no stable income = REJECTED

### ⚠️ Eval eval-25-convert-from-spock

**7/15** · 50752 tokens · 79350ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500]
  > Table has separate columns | fragile | insuredValue | handling | instead of single Options column
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions
  > No @TypeConverter present; code uses null checks (if (insuredValue != null)) instead
- ❌ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table — not as three separate length/width/height columns
  > Table has separate columns | length | width | height | not a single Dimensions list column
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence
- ❌ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern
  > Only one @TableTest method; one monolithic table mixing base rates, surcharges, dimensional weight, and carrier equivalence
- ❌ **numeric-types-correct**: Weight and cost parameters use BigDecimal (not double or String). Dimensions use Integer or int.
  > Method signature shows 'double weight' and 'double cost', not BigDecimal
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions not outcomes or generic labels
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ❌ **business-language-columns**: Column names use domain language ('Zone', 'Weight (kg)', 'Dimensions', 'Options', 'Cost?')
  > insuredValue uses camelCase; fragile/insuredValue/handling are separate instead of 'Options' column; length/width/height separate instead of 'Dimensions'
- ❌ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
  > Method contains 'if (insuredValue != null)' and 'if (handling != null)' statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest
- ❌ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title
  > No @DisplayName annotation; method name 'calculateShippingCost' is generic and does not describe the specific test scenarios
- ✅ **no-groovy-syntax**: Output contains no Groovy or Spock syntax: no def, where:, expect:, given:, etc.

### ⚠️ Eval eval-26-convert-from-kotest

**11/15** · 56106 tokens · 136419ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells. Rows with no options use a blank cell or [:].
  > Second table has three separate columns: | Fragile | Insured value | Handling | not one map column
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
  > No @TypeConverter method in code; response notes 'no @TypeConverter needed'
- ✅ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table — not as three separate length/width/height columns
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
- ✅ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern — not one monolithic table mixing base rates, surcharges, dimensional weight, and carrier equivalence
- ✅ **numeric-types-correct**: Weight and cost parameters use BigDecimal (not double or String). Dimensions use Integer or int.
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('EU express, light package', 'Oversized with fragile') — not outcomes ('8.00', 'Surcharge applied') or generic labels ('Test 1').
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ❌ **business-language-columns**: Column names use domain language ('Zone', 'Weight (kg)', 'Dimensions', 'Options', 'Cost?') — not code identifiers ('shippingZone', 'weightKg', 'dims')
  > 'Dims' used instead of 'Dimensions' (code abbreviation, not domain language)
- ✅ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ❌ **no-kotest-syntax**: Output contains no Kotest syntax: no forAll, row(), withData, shouldBe, context(), FunSpec, or Kotest imports.
  > shouldBe expectedCost appears in both test methods (Kotest matcher syntax)

### ⚠️ Eval eval-27-convert-from-testng

**12/15** · 52219 tokens · 87390ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options are collapsed into a single map column like [fragile: true, insuredValue: 500] — not three separate columns
  > Fragile | Insured value | Handling are three separate columns, not one map
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> and returns PackageOptions
  > Only @TypeConverter public static ShippingZone parseShippingZone present; no PackageOptions converter
- ✅ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX}
- ❌ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern
  > Only one method void calculateShippingCost(...) defined; all scenarios in single table
- ✅ **numeric-types-correct**: Weight and cost parameters use BigDecimal; Dimensions use Integer or int
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes or generic labels
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **business-language-columns**: Column names use domain language, not code identifiers
- ✅ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Each test method has @DisplayName or method name that reads as clear descriptive title
- ✅ **no-testng-artifacts**: Output contains no TestNG syntax (@DataProvider, @Test, Object[][], org.testng.*)

### ⚠️ Eval eval-28-convert-from-methodsource

**12/15** · 93296 tokens · 134435ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500]
  > Surcharge table shows: | Fragile | Insured Value | Handling | (separate columns)
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
  > Only parseShippingZone @TypeConverter present; PackageOptions constructed in method
- ✅ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table — not as three separate length/width/height columns
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
- ✅ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern — not one monolithic table mixing base rates, surcharges, dimensional weight, and carrier equivalence
- ✅ **numeric-types-correct**: Weight and cost parameters use BigDecimal (not double or String). Dimensions use Integer or int.
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('EU express, light package', 'Oversized with fragile') — not outcomes ('8.00', 'Surcharge applied') or generic labels ('Test 1').
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ✅ **business-language-columns**: Column names use domain language ('Zone', 'Weight (kg)', 'Dimensions', 'Options', 'Cost?') — not code identifiers ('shippingZone', 'weightKg', 'dims')
- ❌ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
  > shippingCostSurcharges body: if (insuredValue != null)... if (handling != null)...
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **no-methodsource-artifacts**: Output contains no @MethodSource syntax: no @MethodSource, Stream<Arguments>, Arguments.of, or JUnit Jupiter parameterized imports (org.junit.jupiter.params.provider.*).

### ✅ Eval eval-29-shopping-cart-tt

**21/21** · 218477 tokens · 249152ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **concerns-decomposed**: Multiple @TableTest methods are used, each addressing a distinct concern
- ✅ **minimal-rows-per-concern**: Each @TableTest has only rows needed for its concern—no unnecessary permutations
- ✅ **separates-item-coupon-total-checkout**: Item, coupon, total, and checkout logic in separate @TableTest methods
- ✅ **rows-independently-executable**: Each row independently executable; no row depends on prior row's result
- ✅ **coupon-before-after-columns**: Coupon table uses before/after columns like 'Active coupon' / 'Active coupon after?'
- ✅ **coupon-validity-not-coupon-types**: Coupon table tests validity & replacement, not coupon type effects on price
- ✅ **scenario-column-present**: Table has scenario/description column as leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes
- ✅ **business-language-columns**: Column names use domain/business language, not code identifiers
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations in order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Test method has @DisplayName or clear descriptive method name
- ✅ **description-uses-textblock**: If @Description present and multi-line, uses triple-quoted string, not +
- ✅ **single-assertion-in-method**: Each @TableTest has uniform assertion pattern applied to all rows
- ✅ **test-data-visible**: Product prices and coupon rules visible in table/description/setup
- ✅ **type-converters-for-complex-objects**: TypeConverter methods convert map/string values to domain objects; test bodies only arrange-act-assert
- ✅ **coupon-as-single-column**: Coupon data in single column with @TypeConverter, not separate sparse columns
- ✅ **uses-standard-map-syntax**: Map columns use standard syntax [k: v, k2: v2] and [:], not custom notation
- ✅ **description-not-redundant-with-scenarios**: @Description does not repeat scenario info; coupon type behavior in scenario names, not description

### ✅ Eval eval-30-order-splitting-tt

**18/18** · 309990 tokens · 689023ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **concerns-decomposed**: Multiple @TableTest methods are used, each addressing a distinct concern — not one monolithic table mixing all splitting rules.
- ✅ **minimal-rows-per-concern**: Each @TableTest has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.
- ✅ **concern-fulfillment-method**: Fulfillment method splitting is represented as its own @TableTest — items with different fulfillment types (store pickup vs home delivery) cannot share a shipment.
- ✅ **concern-delivery-address**: Delivery address splitting is represented as its own @TableTest — items going to different addresses must be in separate shipments.
- ✅ **concern-availability**: Availability splitting is represented as its own @TableTest — in-stock items ship immediately, backordered/pre-ordered items ship when available.
- ✅ **concern-warehouse-allocation**: Warehouse allocation (minimising shipments) is represented as its own @TableTest — choosing which warehouses to ship from to minimise total shipment count.
- ✅ **concern-companion-products**: Companion product grouping is represented as its own @TableTest — paired items (e.g. camera body and lens) should ship together from the same location when possible.
- ✅ **all-outputs-same-table**: Each @TableTest method includes all output columns for its concern in the same table — e.g. warehouse allocation includes both the assignment and shipment count, not split across methods.
- ✅ **scalar-quantity-for-warehouse**: Warehouse allocation table uses scalar quantity columns (e.g. 'Qty ordered') not item lists, when product identity doesn't matter for the logic.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Shipments?', 'Groups?')
- ✅ **scenario-names-describe-conditions**: Scenario names describe the business situation (e.g. 'Consolidate: two beats three', 'Everything at one warehouse') — not 'Test case 2'.
- ✅ **business-language-columns**: Column names use business/domain language (e.g. 'Shipments?' not 'Result?', 'Warehouse stock' not 'inventory_map'). The tables read as a specification a product person could review.
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.

