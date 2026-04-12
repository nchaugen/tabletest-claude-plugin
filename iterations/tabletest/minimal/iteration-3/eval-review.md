# Eval Review — tabletest variant=minimal, Iteration 3

**Model:** sonnet · **Date:** 2026-04-12 · **Evals:** 19

## Summary

205/239 (85.8%) · 1529669 tokens · 2168.5s · $4.1031

## Delta vs Iteration 2

**Regressions (15):**
- ❌ eval-2-parse-dates: `null-as-blank-cell`
- ❌ eval-7-permission-check: `uses-value-sets`
- ❌ eval-7-permission-check: `fewer-than-nine-rows`
- ❌ eval-7-permission-check: `has-descriptive-title`
- ❌ eval-8-money-parse: `has-descriptive-title`
- ❌ eval-18-convert-from-code: `annotation-order`
- ❌ eval-22-event-registration-tt: `description-no-redundant-field-values`
- ❌ eval-25-convert-from-spock: `concerns-decomposed`
- ❌ eval-25-convert-from-spock: `business-language-columns`
- ❌ eval-25-convert-from-spock: `has-descriptive-title`
- ❌ eval-26-convert-from-kotest: `no-if-switch-in-method`
- ❌ eval-26-convert-from-kotest: `annotation-order`
- ❌ eval-27-convert-from-testng: `options-as-map`
- ❌ eval-27-convert-from-testng: `concerns-decomposed`
- ❌ eval-27-convert-from-testng: `annotation-order`

**Improvements (11):**
- ✅ eval-18-convert-from-code: `scenario-names-describe-conditions`
- ✅ eval-18-convert-from-code: `depth-premium-boundaries`
- ✅ eval-22-event-registration-tt: `validation-includes-optional-fields`
- ✅ eval-22-event-registration-tt: `descriptive-registration-date`
- ✅ eval-22-event-registration-tt: `cutoff-date-column-if-literal-dates`
- ✅ eval-22-event-registration-tt: `optional-fields-has-expected-column`
- ✅ eval-23-loan-approval-tt: `separates-age-credit-income`
- ✅ eval-27-convert-from-testng: `no-if-switch-in-method`
- ✅ eval-29-shopping-cart-tt: `type-converters-for-complex-objects`
- ✅ eval-29-shopping-cart-tt: `coupon-as-single-column`
- ✅ eval-29-shopping-cart-tt: `description-not-redundant-with-scenarios`

## Resource Comparison vs Iteration 2

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-1-convert-repetitive-tests | 10/10 | 10/10 | 44278 | 74386 | 13.2 | 16.6 |
| eval-2-parse-dates | 12/13 | 10/10 | 46136 | 47757 | 47.1 | 49.2 |
| eval-3-dependency-setup | 4/4 | 4/4 | 43867 | 45373 | 8.3 | 9.4 |
| eval-7-permission-check | 7/11 | 10/11 | 70919 | 45961 | 16.6 | 20.6 |
| eval-8-money-parse | 12/13 | 13/13 | 74739 | 47402 | 48.1 | 44.4 |
| eval-9-bonus-contractor-structure | 11/11 | 11/11 | 44635 | 74201 | 21.6 | 20.9 |
| eval-14-weekly-pay | T/O | 15/18 | — | 328522 | T/O | 377.0 |
| eval-15-reis-discount | T/O | 9/18 | — | 235426 | T/O | 253.7 |
| eval-18-convert-from-code | 15/18 | 14/18 | 54127 | 95293 | 131.9 | 197.3 |
| eval-19-convert-from-parameterized | 8/8 | 8/8 | 44737 | 46361 | 18.7 | 23.5 |
| eval-20-collections-and-quoting | 13/13 | 11/11 | 127350 | 56945 | 184.0 | 186.4 |
| eval-22-event-registration-tt | 20/23 | 17/23 | 94849 | 58772 | 220.3 | 214.4 |
| eval-23-loan-approval-tt | 14/16 | 13/16 | 103192 | 124015 | 83.0 | 139.3 |
| eval-25-convert-from-spock | 7/15 | 10/15 | 50752 | 99672 | 79.3 | 156.4 |
| eval-26-convert-from-kotest | 10/15 | 12/15 | 56106 | 92084 | 136.4 | 122.9 |
| eval-27-convert-from-testng | 11/15 | 12/14 | 52219 | 92942 | 87.4 | 125.3 |
| eval-28-convert-from-methodsource | 12/15 | 12/15 | 93296 | 96649 | 134.4 | 150.9 |
| eval-29-shopping-cart-tt | 21/21 | 18/21 | 218477 | 276390 | 249.2 | 410.7 |
| eval-30-order-splitting-tt | 18/18 | — | 309990 | — | 689.0 | — |

## Per-Eval Results

### ✅ Eval eval-1-convert-repetitive-tests

**10/10** · 44278 tokens · 13170ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Discount?' or 'Expected?')
- ✅ **has-three-data-rows**: Table has exactly 3 data rows (GOLD, SILVER, BRONZE)
- ✅ **single-assertion-in-method**: Test method body contains only one assertEquals (or equivalent assertion) — not three
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'discountByCustomerTier' → 'Discount By Customer Tier'). Not a generic name like 'test1' or 'testMethod'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express — such as fixed values shared by all rows, where/when the rule applies, or open questions. Does NOT merely restate the column names or summarise what the rows show. It is acceptable to omit @Description if the table already conveys all relevant context (e.g. fixed values are included as a column rather than hardcoded in the method body).
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.

### ⚠️ Eval eval-2-parse-dates

**12/13** · 46136 tokens · 47056ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **null-as-blank-cell**: Null input is represented as a blank cell (not the string 'null') in the table
  > Not graded
- ✅ **exception-handled-cleanly**: The empty string / exception case is handled — either via a Throws? column, assertThrows inside the method, or a separate @TableTest — rather than being silently omitted
- ✅ **localdate-result-column**: There is a result column typed as LocalDate (or using a string representation that maps to LocalDate)
- ✅ **type-conversion-addressed**: The response addresses type conversion for non-trivial column types — either by providing a @TypeConverter/converter method, or by using a representation that leverages built-in conversion (e.g., FQCN for Class<?>, ISO format for LocalDate)
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'dateParsing' → 'Date Parsing'). Not a generic name like 'test1' or 'testMethod'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express — such as where/when the rule applies, or open questions. Does NOT merely restate the column names or summarise what the rows show. It is acceptable to omit @Description if the table already conveys all relevant context (e.g. supported date formats are visible as table rows).
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **empty-string-uses-quotes**: Empty string input uses quoted syntax (e.g. '' or "") in the table — not a blank cell, which represents null. The distinction between empty string and null is preserved.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ✅ **separates-valid-and-invalid**: Valid parsing cases and error/exception cases are in separate @TableTest methods

### ✅ Eval eval-3-dependency-setup

**4/4** · 43867 tokens · 8278ms

- ✅ **flags-junit-version**: Response explicitly flags that JUnit 5.10.0 is below the required minimum (5.11) and recommends upgrading
- ✅ **provides-correct-groupid**: Response provides the tabletest dependency with groupId 'org.tabletest' (not guessed)
- ✅ **provides-artifactid**: Response includes 'tabletest-junit' as the artifactId
- ✅ **includes-test-scope**: The suggested dependency includes <scope>test</scope>

### ⚠️ Eval eval-7-permission-check

**7/11** · 70919 tokens · 16621ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **uses-value-sets**: Table uses value-set syntax (curly braces like {READ, WRITE} or {READ, WRITE, DELETE}) to express rules that hold across multiple actions
  > Table uses individual rows (ADMIN | READ | true, ADMIN | WRITE | true, etc.) without curly brace value-set syntax
- ❌ **fewer-than-nine-rows**: Table has fewer than 9 data rows
  > Table contains exactly 9 data rows from 'Admin reads' through 'Guest cannot delete'
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ❌ **no-duplicate-role-output**: No two rows share both the same Role value and the same boolean output
  > ADMIN | READ | true, ADMIN | WRITE | true, ADMIN | DELETE | true share Role=ADMIN and Allowed=true
- ❌ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title
  > Method is named 'checkPermission' which is generic; no @DisplayName annotation present
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest
- ✅ **description-uses-textblock**: If @Description is present and text is longer than a single short line, it uses a text block
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Admin performs any action', 'Guest reads') — not outcomes ('Allowed', 'Denied')

### ⚠️ Eval eval-8-money-parse

**12/13** · 74739 tokens · 48060ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell in the valid inputs @TableTest table
- ✅ **exception-has-expected-column**: The exception/invalid-input table has an expected column specifying exception type per row
- ✅ **exception-cases-handled**: The exception cases (empty string, letters-only, negative) are handled via a Throws? column
- ✅ **result-column-with-question-mark**: Valid inputs have a result column name ending with '?'
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ❌ **has-descriptive-title**: Test method has a descriptive title (method name or @DisplayName)
  > Method names 'parsesValidInput' and 'rejectsInvalidInput' are generic patterns; no @DisplayName annotation present to provide a clear title
- ✅ **description-if-present-adds-information**: If @Description present, it adds context beyond table rows; acceptable to omit
- ✅ **annotation-order**: Annotations appear in correct order: @DisplayName, @Description, @TableTest
- ✅ **description-uses-textblock**: If @Description present and long, uses text block (triple-quoted), not concatenation
- ✅ **concerns-decomposed**: Multiple tables address distinct concerns: valid parsing vs exception handling
- ✅ **minimal-rows-per-concern**: Each table has only minimal rows needed to express concern rules
- ✅ **separates-valid-and-invalid**: Valid parsing cases and error cases are in separate @TableTest methods

### ✅ Eval eval-9-bonus-contractor-structure

**11/11** · 44635 tokens · 21647ms

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

### ⚠️ Eval eval-18-convert-from-code

**15/18** · 54127 tokens · 131888ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **black-box-columns**: Table columns represent the method's public inputs (applicant type, age, claim count) and observable outputs (decision, premium) — not internal state
- ✅ **observable-io-only**: No column is named after private fields or internal variables. The test treats the method as a black box.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **business-language-columns**: Column names use domain/business language — not code identifiers
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions — not outcomes
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?'
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ❌ **annotation-order**: Annotations appear in order: @DisplayName, @Description, @TableTest
  > Second method has @Description before @TableTest, but no consistent ordering. First method lacks @Description/@DisplayName.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title
- ✅ **description-uses-textblock**: If @Description is present and longer than one line, it uses a text block, not string concatenation. Passes if @Description is absent.
- ✅ **concerns-decomposed**: Multiple tables are used, each addressing a distinct concern — not one monolithic table
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations
- ✅ **separates-decision-and-premium**: Decision logic and premium calculation are in separate @TableTest methods
- ✅ **no-reimplemented-internals**: Test method body does not recompute internal formulas. Only calls public API and asserts on return values.
- ❌ **description-no-internals**: @Description explains purpose/context — not internal formulas or implementation details
  > Response verification table shows formulas: 'riskScore = age/10 + claims*15' and 'Premium = 100 + riskScore×2' — internals exposed outside @Description but conceptually outside method body
- ❌ **depth-decision-boundaries**: Approval decision table covers the rejection boundary: 5 claims triggers rejection, 4 claims results in approval. Senior vs non-senior (64 vs 65) visible.
  > First table shows 5 claims → REJECTED, but lacks 4-claims row. No age 64 vs 65 comparison in routing table.
- ✅ **depth-premium-boundaries**: Approved premium table covers boundary effects: 0-vs-1 claim impact and age 64-vs-65 threshold

### ✅ Eval eval-19-convert-from-parameterized

**8/8** · 44737 tokens · 18729ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions — not outcomes or 'Test case 1'
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest
- ✅ **has-descriptive-title**: Test method has @DisplayName or descriptive method name
- ✅ **description-uses-textblock**: If @Description present and long, uses text block not concatenation. Passes if absent.

### ✅ Eval eval-20-collections-and-quoting

**13/13** · 127350 tokens · 184018ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **list-syntax-correct**: List values in the table use bracket syntax like [tech:java, biz:sales, dev:ci] — not comma-separated strings without brackets.
- ✅ **empty-list-explicit**: Empty list input uses [] (not a blank cell, which represents null). The distinction between empty list and null is preserved.
- ✅ **special-chars-quoted**: Values containing pipes (|), brackets, or colons (:) are properly quoted (e.g. "tech:java") so they don't conflict with table syntax.
- ✅ **set-syntax-correct**: Set<String> values in the table use curly brace syntax like {tech, dev} — not bracket syntax [tech, dev].
- ✅ **newline-in-cell**: Cell values containing newlines use \n escaping — not literal line breaks that would break the table row structure.
- ✅ **pipe-quoted**: Values containing pipe characters (|) are quoted so they don't conflict with the markdown table column separator.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Filtered tags?' or 'Result?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +.

### ⚠️ Eval eval-22-event-registration-tt

**20/23** · 94849 tokens · 220285ms

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
  > Pricing @Description states 'Early-bird (20% off) applies for registrations before 2025-03-01. Group discount (15% off) applies for 5 or more attendees. When both discounts qualify, only the higher discount (early-bird) is applied.' — these rules are already demonstrated by the table rows
- ❌ **discount-column-preferred**: Output column is 'Discount?' (percentage or amount off) rather than 'Price?' — making verification simpler (discount is the rule's direct output; price requires knowing the base price). If 'Price?' is used instead, both 'Base price' and 'Price?' columns must be present so the reader can trace the calculation.
  > Pricing table uses 'Price?' column but does not include a 'Base price' column, breaking the traceability requirement
- ✅ **single-assertion-in-method**: Each @TableTest method has a single, uniform assertion pattern applied to all rows — not different assertions per scenario. Validation checks one thing (e.g. error message or acceptance status); pricing checks one thing (e.g. discount or price). No if/switch in assertion logic.
- ✅ **optional-fields-has-expected-column**: The optional-fields acceptance table includes an expectation/output column (e.g. 'Approved?', 'Result?', or 'Status?') asserting that each combination is accepted — not just varying inputs without any expected column.
- ❌ **description-no-redundant-field-values**: Scenario descriptions do not include specific field values that are already visible in the table or that serve as 'any valid value' placeholders. Stating 'name: John, email: john@example.com, registration date: 2025-04-01' in a description adds maintenance burden without value — it suffices to say 'valid values supplied' or describe the scenario condition. Applies to all tables.
  > Pricing @Description states 'Name is 'Alice Smith', email is 'alice@example.com'' which are visible/fixed in the table columns and test method

### ⚠️ Eval eval-23-loan-approval-tt

**14/16** · 103192 tokens · 83016ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **threshold-as-column**: Policy thresholds (650/600 credit scores) appear in a dedicated policy column separate from applicant's actual credit score column
  > No dedicated 'Credit threshold' column exists. Thresholds (650/600) are documented in @Description text and scenario names, not as explicit table columns.
- ✅ **concrete-domain-values**: Cell values use concrete domain terms (e.g. '70', '650', 'APPROVED') — not abstract codes
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions — not outcomes
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Age', 'Credit score') — not code identifiers
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title
- ✅ **description-uses-textblock**: If @Description is present and longer than a single short line, it uses a text block (triple-quoted string), not concatenation
- ✅ **concerns-decomposed**: Multiple tables are used, each addressing a distinct concern — not one monolithic table
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations
- ✅ **separates-age-credit-income**: Age boundary policy, credit score categorisation, and income status are separated into distinct @TableTest methods
- ❌ **description-no-redundant-field-values**: Scenario descriptions and @Description text do not redundantly include field values already visible in the table
  > @Description states 'Requires hasStableIncome to be declared Boolean (boxed) in the method signature' — this is implementation detail, not a scenario description.
- ✅ **depth-stable-income-effect**: The standardApplicantCreditThreshold table clearly documents the effect of stable income through rows showing above-threshold + stable income = APPROVED, above-threshold + no stable income = REJECTED

### ⚠️ Eval eval-25-convert-from-spock

**7/15** · 50752 tokens · 79350ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells. Rows with no options use a blank cell or [:].
  > fragile | insuredValue | handling columns remain separate throughout the table; no map syntax like [fragile: true, insuredValue: 500] is used
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
  > No @TypeConverter method is present in the response; manual null-checks and setters are used instead
- ❌ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table — not as three separate length/width/height columns
  > length | width | height columns remain separate; no [L, W, H] list syntax is used in the table
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
- ❌ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern — not one monolithic table mixing base rates, surcharges, dimensional weight, and carrier equivalence
  > Single @TableTest method with 15 rows mixing base rates, surcharges, dimensional weight, and carrier tests
- ❌ **numeric-types-correct**: Weight and cost parameters use BigDecimal (not double or String). Dimensions use Integer or int.
  > double weight and double cost parameters used; dimensions use int (correct) but cost should be BigDecimal
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('EU express, light package', 'Oversized with fragile') — not outcomes ('8.00', 'Surcharge applied') or generic labels ('Test 1').
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ❌ **business-language-columns**: Column names use domain language ('Zone', 'Weight (kg)', 'Dimensions', 'Options', 'Cost?') — not code identifiers ('shippingZone', 'weightKg', 'dims')
  > Columns use 'region', 'speed', 'weight', 'length', 'width', 'height', 'carrier' instead of domain language like 'Zone', 'Dimensions', 'Options'
- ❌ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
  > if statement found in method body
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ❌ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
  > Method named 'calculateShippingCost' with no @DisplayName; does not describe the test's purpose clearly
- ✅ **no-groovy-syntax**: Output contains no Groovy or Spock syntax: no def, where:, expect:, given:, GString interpolation (#variable), Spock assertions (thrown(), old()), or Groovy list literals ([] as Set).

### ⚠️ Eval eval-26-convert-from-kotest

**10/15** · 56106 tokens · 136419ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options collapsed into single map column like [fragile: true, insuredValue: 500]
  > Options kept as three separate columns: 'Fragile', 'Insured value', 'Handling' with mostly-blank cells
- ❌ **options-type-converter**: A @TypeConverter method present accepting Map<String, String> returning PackageOptions
  > No @TypeConverter method shown. Response states 'no @TypeConverter needed' relying on nullable parameter types
- ✅ **dimensions-as-list**: Dimensions represented as [L, W, H] list in table
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX}
- ✅ **concerns-decomposed**: Multiple @TableTest methods addressing distinct concerns
- ✅ **numeric-types-correct**: Weight and cost use BigDecimal; dimensions use Integer or int
- ✅ **scenario-column-present**: Each table has scenario/description column as leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes or generic labels
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **business-language-columns**: Column names use domain language, not code identifiers
- ❌ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
  > Second method body contains: 'insuredValue?.let { this.insuredValue = it }' and 'handling?.let { this.handling = it }' which use if-like control flow via Kotlin's .let
- ❌ **annotation-order**: Annotations in order: @DisplayName, @Description, @TableTest
  > Second test has '@Description' before '@TableTest', but no @DisplayName present
- ✅ **has-descriptive-title**: Each test method has @DisplayName or descriptive method name
- ❌ **no-kotest-syntax**: Output contains no Kotest syntax
  > Method bodies use 'shouldBe' which is Kotest syntax: 'calculateShippingCost(...) shouldBe expectedCost'

### ⚠️ Eval eval-27-convert-from-testng

**11/15** · 52219 tokens · 87390ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options collapsed into single map column like [fragile: true, insuredValue: 500]
  > Options are kept as three separate columns: 'Fragile | Insured value | Handling' with blank cells, not collapsed into one map column
- ❌ **options-type-converter**: A @TypeConverter method present that accepts Map<String, String> and returns PackageOptions
  > Only @TypeConverter present is for ShippingZone; no converter for Map to PackageOptions exists
- ✅ **dimensions-as-list**: Dimensions represented as [L, W, H] list in table
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX}
- ❌ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing distinct concern
  > Only one @TableTest method 'calculateShippingCost' present; all scenarios mixed in one table
- ✅ **numeric-types-correct**: Weight and cost use BigDecimal; dimensions use Integer or int
- ✅ **scenario-column-present**: Each table has scenario/description column as leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes or generic labels
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **business-language-columns**: Column names use domain language, not code identifiers
- ✅ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
- ❌ **annotation-order**: Annotations appear in order: @DisplayName, @Description, @TableTest
  > @TableTest appears directly without @DisplayName or @Description before it
- ✅ **has-descriptive-title**: Each test method has @DisplayName or readable method name
- ✅ **no-testng-artifacts**: Output contains no TestNG syntax (@DataProvider, @Test, Object[][], org.testng.*)

### ⚠️ Eval eval-28-convert-from-methodsource

**12/15** · 93296 tokens · 134435ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options collapsed into single map column like [fragile: true, insuredValue: 500]
  > Surcharge table uses separate columns: 'Fragile | Insured Value | Handling', not collapsed map
- ❌ **options-type-converter**: A @TypeConverter method accepts Map<String, String> and returns PackageOptions
  > Only @TypeConverter present is parseShippingZone; no converter for PackageOptions from map
- ✅ **dimensions-as-list**: Dimensions represented as [L, W, H] list in table
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX}
- ✅ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing distinct concern
- ✅ **numeric-types-correct**: Weight and cost use BigDecimal; dimensions use Integer or int
- ✅ **scenario-column-present**: Each table has scenario/description column as leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes or generic labels
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **business-language-columns**: Column names use domain language, not code identifiers
- ❌ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
  > shippingCostSurcharges contains: 'if (insuredValue != null)' and 'if (handling != null)'
- ✅ **annotation-order**: Annotations in order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Each method has @DisplayName or descriptive name from camelCase
- ✅ **no-methodsource-artifacts**: Output contains no @MethodSource syntax or parameterized imports

### ✅ Eval eval-29-shopping-cart-tt

**21/21** · 218477 tokens · 249152ms

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
- ✅ **type-converters-for-complex-objects**: TypeConverter methods are used to convert map/string table values into domain objects (e.g. Map → Cart, Map → InventoryService, map → Coupon). Test method bodies contain only arrange-act-assert — no object construction from raw table values.
- ✅ **coupon-as-single-column**: Coupon data is expressed in a single column with a @TypeConverter — not spread across separate sparse columns for coupon type, value, and target product. The column format may be map syntax (e.g. [type: PERCENTAGE, value: 10]) or readable shorthand (e.g. '20% cart', '$5 off', '50% off widget').
- ✅ **uses-standard-map-syntax**: Columns representing maps (e.g. cart contents, product catalogue, inventory levels) use standard TableTest map syntax — [k: v, k2: v2] for entries and [:] for empty map — not a custom bracket notation. Custom string-parsing TypeConverters are not needed for data that maps naturally to Map<String, X>.
- ✅ **description-not-redundant-with-scenarios**: @Description annotations do not repeat information that the scenario names already convey. Coupon type behavior (what PERCENTAGE/FIXED/PRODUCT mean) should be expressed through descriptive scenario names, not duplicated in the description.

### ✅ Eval eval-30-order-splitting-tt

**18/18** · 309990 tokens · 689023ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **concerns-decomposed**: Multiple @TableTest methods are used, each addressing a distinct concern
- ✅ **minimal-rows-per-concern**: Each @TableTest has only the rows needed to express its concern
- ✅ **concern-fulfillment-method**: Fulfillment method splitting is represented as its own @TableTest
- ✅ **concern-delivery-address**: Delivery address splitting is represented as its own @TableTest
- ✅ **concern-availability**: Availability splitting is represented as its own @TableTest
- ✅ **concern-warehouse-allocation**: Warehouse allocation is represented as its own @TableTest
- ✅ **concern-companion-products**: Companion product grouping is represented as its own @TableTest
- ✅ **all-outputs-same-table**: Each @TableTest includes all output columns for its concern in the same table
- ✅ **scalar-quantity-for-warehouse**: Warehouse allocation table uses scalar quantity columns
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **scenario-names-describe-conditions**: Scenario names describe the business situation
- ✅ **business-language-columns**: Column names use business/domain language
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Test method has a clear, descriptive title
- ✅ **description-uses-textblock**: Multi-line @Description uses text block, not string concatenation

