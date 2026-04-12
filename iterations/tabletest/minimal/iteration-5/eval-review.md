# Eval Review — tabletest variant=minimal, Iteration 5

**Model:** sonnet · **Date:** 2026-04-12 · **Evals:** 19

## Summary

183/226 (81.0%) · 1957016 tokens · 3798.1s · $4.6381

## Delta vs Iteration 3

**Regressions (8):**
- ❌ eval-18-convert-from-code: `business-language-columns`
- ❌ eval-18-convert-from-code: `separates-decision-and-premium`
- ❌ eval-22-event-registration-tt: `validation-includes-optional-fields`
- ❌ eval-22-event-registration-tt: `optional-fields-has-expected-column`
- ❌ eval-27-convert-from-testng: `uses-value-sets`
- ❌ eval-27-convert-from-testng: `no-if-switch-in-method`
- ❌ eval-28-convert-from-methodsource: `annotation-order`
- ❌ eval-30-order-splitting-tt: `concerns-decomposed`

**Improvements (5):**
- ✅ eval-2-parse-dates: `null-as-blank-cell`
- ✅ eval-8-money-parse: `has-descriptive-title`
- ✅ eval-25-convert-from-spock: `concerns-decomposed`
- ✅ eval-25-convert-from-spock: `has-descriptive-title`
- ✅ eval-27-convert-from-testng: `concerns-decomposed`

## Resource Comparison vs Iteration 3

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-1-convert-repetitive-tests | 10/10 | 10/10 | 44345 | 44278 | 14.7 | 13.2 |
| eval-2-parse-dates | 13/13 | 12/13 | 73253 | 46136 | 34.2 | 47.1 |
| eval-3-dependency-setup | 4/4 | 4/4 | 44075 | 43867 | 9.0 | 8.3 |
| eval-7-permission-check | 7/11 | 7/11 | 70959 | 70919 | 13.8 | 16.6 |
| eval-8-money-parse | 13/13 | 12/13 | 103102 | 74739 | 48.3 | 48.1 |
| eval-9-bonus-contractor-structure | 11/11 | 11/11 | 98298 | 44635 | 18.5 | 21.6 |
| eval-14-weekly-pay | T/O | — | — | — | T/O | — |
| eval-15-reis-discount | 5/18 | — | 45131 | — | 78.5 | — |
| eval-18-convert-from-code | 13/18 | 15/18 | 132531 | 54127 | 164.1 | 131.9 |
| eval-19-convert-from-parameterized | 8/8 | 8/8 | 71233 | 44737 | 11.7 | 18.7 |
| eval-20-collections-and-quoting | 13/13 | 13/13 | 67181 | 127350 | 364.9 | 184.0 |
| eval-22-event-registration-tt | 18/23 | 20/23 | 225990 | 94849 | 296.6 | 220.3 |
| eval-23-loan-approval-tt | T/O | 14/16 | — | 103192 | T/O | 83.0 |
| eval-25-convert-from-spock | 9/15 | 7/15 | 118507 | 50752 | 828.8 | 79.3 |
| eval-26-convert-from-kotest | T/O | 10/15 | — | 56106 | T/O | 136.4 |
| eval-27-convert-from-testng | 10/15 | 11/15 | 171044 | 52219 | 625.2 | 87.4 |
| eval-28-convert-from-methodsource | 11/15 | 12/15 | 84912 | 93296 | 82.8 | 134.4 |
| eval-29-shopping-cart-tt | 21/21 | 21/21 | 313859 | 218477 | 621.7 | 249.2 |
| eval-30-order-splitting-tt | 17/18 | 18/18 | 292596 | 309990 | 585.4 | 689.0 |

## Per-Eval Results

### ✅ Eval eval-1-convert-repetitive-tests

**10/10** · 44345 tokens · 14703ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Discount?' or 'Expected?')
- ✅ **has-three-data-rows**: Table has exactly 3 data rows (GOLD, SILVER, BRONZE)
- ✅ **single-assertion-in-method**: Test method body contains only one assertEquals (or equivalent assertion) — not three
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.

### ✅ Eval eval-2-parse-dates

**13/13** · 73253 tokens · 34202ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell (not the string 'null') in the table
- ✅ **exception-handled-cleanly**: The empty string / exception case is handled — either via a Throws? column, assertThrows inside the method, or a separate @TableTest — rather than being silently omitted
- ✅ **localdate-result-column**: There is a result column typed as LocalDate (or using a string representation that maps to LocalDate)
- ✅ **type-conversion-addressed**: The response addresses type conversion for non-trivial column types — either by providing a @TypeConverter/converter method, or by using a representation that leverages built-in conversion
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express — such as where/when the rule applies, or open questions. Does NOT merely restate the column names or summarise what the rows show. It is acceptable to omit @Description if the table already conveys all relevant context
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent
- ✅ **empty-string-uses-quotes**: Empty string input uses quoted syntax (e.g. '' or "") in the table — not a blank cell, which represents null. The distinction between empty string and null is preserved
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns
- ✅ **separates-valid-and-invalid**: Valid parsing cases and error/exception cases are in separate @TableTest methods

### ✅ Eval eval-3-dependency-setup

**4/4** · 44075 tokens · 9035ms

- ✅ **flags-junit-version**: Response explicitly flags that JUnit 5.10.0 is below the required minimum (5.11) and recommends upgrading
- ✅ **provides-correct-groupid**: Response provides the tabletest dependency with groupId 'org.tabletest' (not guessed)
- ✅ **provides-artifactid**: Response includes 'tabletest-junit' as the artifactId
- ✅ **includes-test-scope**: The suggested dependency includes <scope>test</scope>

### ⚠️ Eval eval-7-permission-check

**7/11** · 70959 tokens · 13760ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **uses-value-sets**: Table uses value-set syntax (curly braces like {READ, WRITE} or {READ, WRITE, DELETE}) to express rules that hold across multiple actions — rather than one row per role/action combination
  > Each row uses individual action values (READ, WRITE, DELETE) not curly brace sets like {READ, WRITE, DELETE}
- ❌ **fewer-than-nine-rows**: Table has fewer than 9 data rows (i.e. does not enumerate every role/action combination individually)
  > Table has exactly 9 data rows (Admin can read through Guest cannot delete), enumerating all role/action combinations
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ❌ **no-duplicate-role-output**: No two rows share both the same Role value and the same boolean output — forces value set consolidation
  > ADMIN rows all have action + true output; User can read/write both true; should consolidate with value sets
- ❌ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case
  > Method named 'canPerform' — a generic action name, not descriptive like 'permissionsByRoleAndAction'
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string), not string concatenation with +
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Admin performs any action', 'Guest reads') — not outcomes ('Allowed', 'Denied')

### ✅ Eval eval-8-money-parse

**13/13** · 103102 tokens · 48262ms

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

**11/11** · 98298 tokens · 18505ms

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

### ⚠️ Eval eval-15-reis-discount

**5/18** · 45131 tokens · 78482ms

- ❌ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
  > Response provides discount tier table, passenger type table, and zones table, but NO dedicated rolling window counting table with boundary testing (30 days included/excluded).
- ✅ **2.2-children-flat-discount**: Children's flat 20% discount is represented — either as a row in the eligibility table or a separate note.
- ✅ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers derived from the 'every fifth trip' rule, with boundary values or value sets showing which ticket counts produce which discount. The maximum 40% discount is represented.
- ❌ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
  > No rolling window boundary test table present. Response acknowledges 'Rolling window edge: If exactly 30 days ago you bought 3 tickets, are they included or excluded?' as unresolved open question.
- ❌ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
  > Only one value set appears: 'Child — flat discount regardless | Child | {4, 20, 40} | 20%'. Discount tier table uses individual counts (5, 7, 9, 10, 20, 40), not value sets per tier.
- ❌ **2.15-ticket-count-uses-value-sets**: The discount ladder table uses value sets for the ticket count column to group counts within the same tier (e.g. {5, 6, 7, 8, 9} or {5, 9} for min/max → 5%) — not one row per boundary value.
  > Tier table uses individual rows: 'At first discount threshold | 5 | 5%' and 'Mid first tier | 7 | 5%' and 'Top of first tier | 9 | 5%' — three separate rows instead of one row with value set {5..9}.
- ❌ **2.16-no-duplicate-tier-mapping**: The tier-to-discount mapping is expressed once — not repeated across multiple tables.
  > Tier mapping appears in both 'Reis Discount Tier' table and 'Discount by Passenger Type' table (Adult rows at 4→5%, 40→40%), creating duplication.
- ❌ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
  > Response contains markdown tables only. No @TableTest methods or @DisplayName annotations present in the output.
- ❌ **2.11-format-description**: @Description is present on at least the class or the tier method and provides context beyond what the table rows already express.
  > Response contains no @Description annotations. Open questions section describes ambiguities but is not part of annotation metadata.
- ❌ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
  > No annotations present in response — only markdown tables.
- ✅ **2.14-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **2.17-zone-irrelevance-visible**: The eligibility/passenger-type table includes a Zone column (or equivalent) with a value set to show that the discount applies regardless of zone.
- ❌ **2.18-adult-senior-value-set**: Adult and senior are expressed as a value set {ADULT, SENIOR} (or equivalent) in at least one row of the eligibility table.
  > Adult and Senior listed as separate rows: 'Adult — no travel history yet | Adult | 0 | 0%' and 'Senior — same ladder as adult | Senior | 4 | 5%' — not combined in a value set.
- ❌ **2.19-depth-all-tiers**: All 9 discount tiers are represented in the ladder table: 0% (1-4), 5% (5-9), 10% (10-14), 15% (15-19), 20% (20-24), 25% (25-29), 30% (30-34), 35% (35-39), and 40% (40+).
  > Tier table shows only: 0%, 5%, 10%, 20%, 40%. Missing: 15%, 25%, 30%, 35% tiers.
- ❌ **2.20-readability-one-row-per-tier**: Each tier in the discount ladder is expressed as a single row with a value set for ticket counts — not split across multiple rows.
  > 5% tier split across three rows: 'At first discount threshold | 5 | 5%', 'Mid first tier | 7 | 5%', 'Top of first tier | 9 | 5%'.
- ❌ **2.21-readability-relative-time**: The rolling window table expresses time relatively (e.g. '14 days ago', '30 days ago') rather than using absolute dates.
  > No rolling window table present in response.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table.
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations.
  > Discount by Passenger Type table includes 3 Adult rows (0, 4, 40) and 3 Child rows, duplicating tier mapping already in Reis Discount Tier table.

### ⚠️ Eval eval-18-convert-from-code

**13/18** · 132531 tokens · 164099ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **black-box-columns**: Table columns represent the method's public inputs (applicant type, age, claim count) and observable outputs (decision, premium) — not internal state like hasActivePolicy, internalRiskScore, or calculateRiskScore.
- ✅ **observable-io-only**: No column is named after private fields or internal variables (hasActivePolicy, internalRiskScore, riskScore). The test treats the method as a black box.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ❌ **business-language-columns**: Column names use domain/business language (e.g. 'Applicant type', 'Age', 'Claims', 'Decision?', 'Premium?') — not code identifiers like 'applicantType', 'claimCount', 'String'.
  > Columns use code identifiers 'applicantType' and 'claimCount' instead of business language like 'Applicant Type' or 'Claims'
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Renewal with no claims', 'Senior applicant') — not outcomes ('Auto approved', 'Rejected').
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Decision?' or 'Premium?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ❌ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
  > Annotation order is @DisplayName, @Description, @TableTest for both methods, but @Description should not appear before @TableTest in standard JUnit order
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.
- ❌ **separates-decision-and-premium**: Decision logic (AUTO_APPROVED/REJECTED/APPROVED) and premium calculation are in separate @TableTest methods
  > First table 'routesApplicationDecision' includes both Status? and Premium? columns; both concerns are tested in one method
- ✅ **no-reimplemented-internals**: Test method body does not recompute internal formulas (e.g. risk score calculation). It only calls the public API (evaluateApplication) and asserts on the return value's fields.
- ❌ **description-no-internals**: @Description explains the test's purpose and business context — not internal formulas or implementation details.
  > @Description includes 'Risk score = (age / 10) + (claimCount × 15). Rejection threshold is score > 75' which describes internal formulas
- ❌ **depth-decision-boundaries**: Approval decision table covers the rejection boundary thoroughly: 5 claims triggers rejection regardless of realistic age (risk score crosses 75), 4 claims results in approval.
  > Table shows 5 claims with REJECTED but no row with 4 claims to demonstrate the approval/rejection boundary cliff
- ✅ **depth-premium-boundaries**: Approved premium table covers boundary effects: the premium jump from 0 to 1 claim (impact of +15 risk score on premium), and the premium difference at age 64 vs 65 (standard vs senior formula).

### ✅ Eval eval-19-convert-from-parameterized

**8/8** · 71233 tokens · 11668ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions not outcomes or 'Test case N'
- ✅ **annotation-order**: Annotations appear in correct order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Test method has descriptive title via @DisplayName or method name
- ✅ **description-uses-textblock**: @Description uses text block (triple-quoted) not concatenation; passes if absent

### ✅ Eval eval-20-collections-and-quoting

**13/13** · 67181 tokens · 364887ms

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

**18/23** · 225990 tokens · 296552ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **validation-rules-covered**: Email validation and name-required error scenarios are present
- ✅ **blank-for-absent-optional**: Dietary requirements/accessibility needs use blank cells when absent
- ✅ **blank-vs-value-set-correct**: Irrelevant inputs use value sets, not blanks
- ✅ **scenario-column-present**: Table has a scenario/description column as leftmost
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **business-language-columns**: Column names use domain/business language
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in correct order
- ✅ **has-descriptive-title**: Test method has descriptive title via annotation or name
- ✅ **description-uses-textblock**: Multi-line @Description uses text block (triple-quoted), not concatenation
- ✅ **concerns-decomposed**: Multiple tables address distinct concerns
- ✅ **minimal-rows-per-concern**: Each table has only rows needed for its concern
- ✅ **separates-validation-and-pricing**: Validation and pricing are in separate @TableTest methods
- ❌ **validation-includes-optional-fields**: Validation includes optional-field columns with null and non-null values
  > Validation table has no columns for dietaryRequirements or accessibilityNeeds; they are fixed in @Description instead
- ✅ **descriptive-registration-date**: Registration date uses descriptive values with @TypeConverter
- ✅ **cutoff-date-column-if-literal-dates**: Cutoff date visible if using literal dates; passes if descriptive
- ❌ **description-no-irrelevant-information**: @Description does not duplicate information visible in table
  > Pricing @Description states 'name = ...Alice Smith, email = alice@example.com' which are fixed inputs already implicit from test
- ❌ **discount-column-preferred**: Output is 'Discount?' not 'Price?', or both 'Base price' and 'Price?'
  > Uses 'Price?' column without 'Base price' column; discount column would be clearer
- ✅ **single-assertion-in-method**: Each method has uniform single assertion pattern across rows
- ❌ **optional-fields-has-expected-column**: Optional-fields table includes expected output column
  > No dedicated optional-fields table exists; optional fields not tested with acceptance column
- ❌ **description-no-redundant-field-values**: Scenario descriptions avoid redundant field values visible in columns
  > Pricing @Description includes 'name = Alice Smith, email = alice@example.com' which repeat fixed inputs

### ⚠️ Eval eval-25-convert-from-spock

**9/15** · 118507 tokens · 828778ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells. Rows with no options use a blank cell or [:].
  > Options appear as three separate columns: 'Fragile | Insured Value | Handling'. Not collapsed into map syntax like [fragile: true, insuredValue: 500].
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
  > No @TypeConverter method is present. Code manually constructs PackageOptions with if-statements: 'if (fragile) opts.setFragile(true)'.
- ❌ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table — not as three separate length/width/height columns
  > Dimensions appear as three separate columns: 'Length | Width | Height'. Not collapsed into [L, W, H] list syntax.
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
- ✅ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern — not one monolithic table mixing base rates, surcharges, dimensional weight, and carrier equivalence
- ❌ **numeric-types-correct**: Weight and cost parameters use BigDecimal (not double or String). Dimensions use Integer or int.
  > Weight and cost use 'double': 'double weight' and 'double expectedCost'. Should be BigDecimal. Dimensions correctly use 'int'.
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('EU express, light package', 'Oversized with fragile') — not outcomes ('8.00', 'Surcharge applied') or generic labels ('Test 1').
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ❌ **business-language-columns**: Column names use domain language ('Zone', 'Weight (kg)', 'Dimensions', 'Options', 'Cost?') — not code identifiers ('shippingZone', 'weightKg', 'dims')
  > First table uses code identifiers: 'Region', 'Speed', 'Weight', 'Length', 'Width', 'Height', 'Carrier' instead of collapsed 'Dimensions' or 'Options' or 'Zone'.
- ❌ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
  > if statement found in method body
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **no-groovy-syntax**: Output contains no Groovy or Spock syntax: no def, where:, expect:, given:, GString interpolation (#variable), Spock assertions (thrown(), old()), or Groovy list literals ([] as Set).

### ⚠️ Eval eval-27-convert-from-testng

**10/15** · 171044 tokens · 625199ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options collapsed into single map column like [fragile: true, insuredValue: 500]
  > Surcharges table has separate columns: Fragile | Insured Value | Handling, not a map column
- ❌ **options-type-converter**: A @TypeConverter method present accepting Map<String, String> returning PackageOptions
  > Only parseShippingZone @TypeConverter exists; no @TypeConverter for PackageOptions
- ✅ **dimensions-as-list**: Dimensions represented as [L, W, H] list in table
- ❌ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} for carrier equivalence
  > carrierAgnosticPricing has three separate rows (DHL, UPS, FEDEX), not collapsed with {DHL, UPS, FEDEX} syntax
- ✅ **concerns-decomposed**: Multiple @TableTest methods each addressing distinct concern
- ✅ **numeric-types-correct**: Weight and cost use BigDecimal; dimensions use Integer or int
- ✅ **scenario-column-present**: Each table has scenario/description column as leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes or generic labels
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **business-language-columns**: Column names use domain language, not code identifiers
- ❌ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
  > surcharges method contains: if (fragile) opts.setFragile(true); if (insuredValue != null)...
- ❌ **annotation-order**: Annotations in order: @DisplayName, @Description, @TableTest
  > No @DisplayName or @Description annotations present; only @TableTest and @TypeConverter
- ✅ **has-descriptive-title**: Each test method has @DisplayName or descriptive method name
- ✅ **no-testng-artifacts**: Output contains no TestNG syntax (@DataProvider, @Test(dataProvider=...), Object[][], TestNG imports)

### ⚠️ Eval eval-28-convert-from-methodsource

**11/15** · 84912 tokens · 82781ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options collapsed into single map column like [fragile: true, insuredValue: 500]
  > Options appear as separate columns: 'Fragile | Insured Value | Handling' with individual values, not collapsed map format
- ❌ **options-type-converter**: A @TypeConverter method present accepting Map<String, String> returning PackageOptions
  > No @TypeConverter method shown; PackageOptions constructed manually with setFragile/setInsuredValue/setHandling calls
- ✅ **dimensions-as-list**: Dimensions represented as [L, W, H] list in table
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX}
- ✅ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing distinct concern
- ✅ **numeric-types-correct**: Weight and cost use BigDecimal; dimensions use Integer or int
- ✅ **scenario-column-present**: Each table has scenario/description column as leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions not outcomes or generic labels
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **business-language-columns**: Column names use domain language not code identifiers
- ❌ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
  > appliesSurcharges contains 'if (insuredValue != null)' and 'if (handling != null)' null guards
- ❌ **annotation-order**: Annotations in order: @DisplayName, @Description, @TableTest
  > @Description appears before @TableTest but no @DisplayName present; order is @Description then @TableTest
- ✅ **has-descriptive-title**: Each test method has @DisplayName or descriptive method name
- ✅ **no-methodsource-artifacts**: Output contains no @MethodSource, Stream<Arguments>, Arguments.of, or parameterized imports

### ✅ Eval eval-29-shopping-cart-tt

**21/21** · 313859 tokens · 621747ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **concerns-decomposed**: Multiple @TableTest methods are used, each addressing a distinct concern
- ✅ **minimal-rows-per-concern**: Each @TableTest has only the rows needed to express its concern's rules
- ✅ **separates-item-coupon-total-checkout**: Item operations, coupon application, cart total, and checkout are in separate methods
- ✅ **rows-independently-executable**: Each table row is independently executable — no row depends on a prior row's result
- ✅ **coupon-before-after-columns**: Coupon application table uses before/after columns
- ✅ **coupon-validity-not-coupon-types**: Coupon application table tests validity and replacement, not coupon type effects on price
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes
- ✅ **business-language-columns**: Column names use domain/business language, not code identifiers
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Test method has a clear, descriptive title
- ✅ **description-uses-textblock**: If @Description is present and long, it uses a text block, not string concatenation
- ✅ **single-assertion-in-method**: Each @TableTest method has a single, uniform assertion pattern applied to all rows
- ✅ **test-data-visible**: Product prices and coupon rules are visible in the table or @Description
- ✅ **type-converters-for-complex-objects**: TypeConverter methods convert map/string table values into domain objects
- ✅ **coupon-as-single-column**: Coupon data is expressed in a single column with a @TypeConverter
- ✅ **uses-standard-map-syntax**: Columns representing maps use standard TableTest map syntax [k: v, k2: v2] and [:] for empty
- ✅ **description-not-redundant-with-scenarios**: @Description does not repeat information that scenario names already convey

### ⚠️ Eval eval-30-order-splitting-tt

**17/18** · 292596 tokens · 585417ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **concerns-decomposed**: Multiple @TableTest methods are used, each addressing a distinct concern — not one monolithic table mixing all splitting rules.
  > Not graded
- ✅ **minimal-rows-per-concern**: Each @TableTest has only the rows needed to express its concern's rules
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
- ✅ **annotation-order**: Annotations appear in correct order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Test method has descriptive title via @DisplayName or method name
- ✅ **description-uses-textblock**: If @Description is present and longer than one line, uses text block (triple quotes)

