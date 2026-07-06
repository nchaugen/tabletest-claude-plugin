# Eval Review — tabletest variant=minimal, Iteration 8

**Model:** sonnet · **Date:** 2026-04-17 · **Evals:** 19

## Summary

266/320 (83.1%) · 4382410 tokens · 2468.7s · $5.5604

## Delta vs Iteration 7

**Regressions (16):**
- ❌ eval-2-parse-dates: `concerns-decomposed`
- ❌ eval-2-parse-dates: `minimal-rows-per-concern`
- ❌ eval-7-permission-check: `fewer-than-nine-rows`
- ❌ eval-8-money-parse: `no-if-switch-in-method`
- ❌ eval-14-weekly-pay: `1.14-depth-zero-rate`
- ❌ eval-15-reis-discount: `2.11-format-description`
- ❌ eval-15-reis-discount: `2.18-adult-senior-value-set`
- ❌ eval-15-reis-discount: `2.19-depth-all-tiers`
- ❌ eval-15-reis-discount: `has-tabletest-dependency`
- ❌ eval-27-convert-from-testng: `options-type-converter`
- ❌ eval-27-convert-from-testng: `no-if-switch-in-method`
- ❌ eval-28-convert-from-methodsource: `options-type-converter`
- ❌ eval-28-convert-from-methodsource: `concerns-decomposed`
- ❌ eval-28-convert-from-methodsource: `no-if-switch-in-method`
- ❌ eval-28-convert-from-methodsource: `has-descriptive-title`
- ❌ eval-29-shopping-cart-tt: `business-language-columns`

**Improvements (14):**
- ✅ eval-14-weekly-pay: `1.12-format-annotation-order`
- ✅ eval-18-convert-from-code: `has-tabletest-annotation`
- ✅ eval-18-convert-from-code: `scenario-column-present`
- ✅ eval-18-convert-from-code: `business-language-columns`
- ✅ eval-18-convert-from-code: `has-question-mark-column`
- ✅ eval-18-convert-from-code: `no-if-switch-in-method`
- ✅ eval-18-convert-from-code: `has-descriptive-title`
- ✅ eval-18-convert-from-code: `no-reimplemented-internals`
- ✅ eval-18-convert-from-code: `has-tabletest-dependency`
- ✅ eval-20-collections-and-quoting: `newline-in-cell`
- ✅ eval-25-convert-from-spock: `business-language-columns`
- ✅ eval-25-convert-from-spock: `tests-pass`
- ✅ eval-26-convert-from-kotest: `has-descriptive-title`
- ✅ eval-28-convert-from-methodsource: `tests-pass`

## Resource Comparison vs Iteration 7

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-1-convert-repetitive-tests | 13/13 | 13/13 | 176044 | 203698 | 24.3 | 25.1 |
| eval-2-parse-dates | 12/15 | 14/15 | 162916 | 164908 | 48.8 | 53.0 |
| eval-3-dependency-setup | 4/4 | 4/4 | 44238 | 44339 | 8.8 | 10.0 |
| eval-7-permission-check | 10/13 | 11/13 | 188134 | 186933 | 28.3 | 34.2 |
| eval-8-money-parse | 14/15 | 15/15 | 201701 | 232011 | 89.0 | 90.4 |
| eval-9-bonus-contractor-structure | 13/13 | 13/13 | 190755 | 213628 | 36.3 | 29.9 |
| eval-14-weekly-pay | 15/20 | 15/20 | 267152 | 297900 | 182.3 | 164.1 |
| eval-15-reis-discount | 7/20 | 11/20 | 66528 | 244941 | 92.3 | 108.4 |
| eval-18-convert-from-code | 19/21 | 11/21 | 420608 | 122236 | 108.0 | 167.9 |
| eval-19-convert-from-parameterized | 11/11 | 11/11 | 210138 | 157557 | 29.4 | 25.6 |
| eval-20-collections-and-quoting | 15/15 | 14/15 | 287433 | 223364 | 261.6 | 137.0 |
| eval-22-event-registration-tt | 20/25 | 20/25 | 213734 | 456915 | 153.0 | 183.4 |
| eval-23-loan-approval-tt | 15/18 | 15/18 | 167432 | 263802 | 75.8 | 91.4 |
| eval-25-convert-from-spock | 16/19 | 14/19 | 456613 | 378869 | 234.2 | 198.7 |
| eval-26-convert-from-kotest | 16/19 | 15/19 | 197221 | 202995 | 74.0 | 125.7 |
| eval-27-convert-from-testng | 12/18 | 14/18 | 267141 | 270028 | 120.8 | 114.0 |
| eval-28-convert-from-methodsource | 13/18 | 16/18 | 200865 | 283536 | 113.4 | 154.4 |
| eval-29-shopping-cart-tt | 21/23 | 22/23 | 442647 | 492674 | 471.7 | 319.1 |
| eval-30-order-splitting-tt | 20/20 | 20/20 | 221110 | 678630 | 316.6 | 469.3 |

## Per-Eval Results

### ✅ Eval eval-1-convert-repetitive-tests

**13/13** · 176044 tokens · 24272ms

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
- ✅ **has-tabletest-dependency**: The build file (pom.xml) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against DiscountService

### ⚠️ Eval eval-2-parse-dates

**12/15** · 162916 tokens · 48797ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell (not the string 'null') in the table
- ✅ **exception-handled-cleanly**: The empty string / exception case is handled — either via a Throws? column, assertThrows inside the method, or a separate @TableTest — rather than being silently omitted
- ✅ **localdate-result-column**: There is a result column typed as LocalDate (or using a string representation that maps to LocalDate)
- ✅ **type-conversion-addressed**: The response addresses type conversion for non-trivial column types — either by providing a @TypeConverter/converter method, or by using a representation that leverages built-in conversion (e.g., FQCN for Class<?>, ISO format for LocalDate)
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'dateParsing' → 'Date Parsing'). Not a generic name like 'test1' or 'testMethod'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express — such as where/when the rule applies, or open questions. Does NOT merely restate the column names or summarise what the rows show. It is acceptable to omit @Description if the table already conveys all relevant context (e.g. supported date formats are visible as table rows).
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **empty-string-uses-quotes**: Empty string input uses quoted syntax (e.g. '' or "") in the table — not a blank cell, which represents null. The distinction between empty string and null is preserved.
  > No quoted empty string ('""' or "''") found in table cells
- ❌ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
  > Single @TableTest table mixes valid parsing cases with null input case; separate @Test for exception. Null input belongs logically with valid cases but null is distinct from format testing.
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
  > The @TableTest includes 'Null input |  |' row alongside format examples; null handling could be separate or omitted from format-focused table
- ✅ **separates-valid-and-invalid**: Valid parsing cases and error/exception cases are in separate @TableTest methods
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ✅ Eval eval-3-dependency-setup

**4/4** · 44238 tokens · 8819ms

- ✅ **flags-junit-version**: Response explicitly flags that JUnit 5.10.0 is below the required minimum (5.11) and recommends upgrading
- ✅ **provides-correct-groupid**: Response provides the tabletest dependency with groupId 'org.tabletest' (not guessed)
- ✅ **provides-artifactid**: Response includes 'tabletest-junit' as the artifactId
- ✅ **includes-test-scope**: The suggested dependency includes <scope>test</scope>

### ⚠️ Eval eval-7-permission-check

**10/13** · 188134 tokens · 28314ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **uses-value-sets**: Table uses value-set syntax (curly braces like {READ, WRITE} or {READ, WRITE, DELETE}) to express rules that hold across multiple actions — rather than one row per role/action combination
- ❌ **fewer-than-nine-rows**: Table has fewer than 9 data rows (i.e. does not enumerate every role/action combination individually)
  > Found 9 data row(s)
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ❌ **no-duplicate-role-output**: No two rows share both the same Role value and the same boolean output — forces value set consolidation
  > Admin appears in 3 rows (all true), USER in 3 rows (2 true, 1 false), GUEST in 3 rows (1 true, 2 false). Not consolidated into value sets like {READ, WRITE} per role.
- ❌ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title
  > Method name is 'canPerform' — a generic name explicitly listed as unacceptable in the requirement (e.g. 'test1' or 'canPerform').
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Admin performs any action', 'Guest reads') — not outcomes ('Allowed', 'Denied')
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-8-money-parse

**14/15** · 201701 tokens · 88985ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell in the valid inputs @TableTest table (not the string 'null', and not as a separate @Test method). The blank cell maps to a null parameter.
- ✅ **exception-has-expected-column**: The exception/invalid-input table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
- ✅ **exception-cases-handled**: The exception cases (empty string, letters-only, negative) are handled — via a Throws? column, assertThrows in the method body, or a separate @TableTest — not silently omitted
- ✅ **result-column-with-question-mark**: Valid inputs have a result column name ending with '?' (e.g. 'Money?' or 'Result?')
- ❌ **no-if-switch-in-method**: Test method body contains no if or switch statements
  > ternary operator found in method body
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'moneyParsing' → 'Money Parsing'). Not a generic name like 'test1' or 'testMethod'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express — such as the expected currency format, or open questions. Does NOT merely restate the column names or summarise what the rows show. It is acceptable to omit @Description if the table already conveys all relevant context.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ✅ **separates-valid-and-invalid**: Valid parsing cases and error/exception cases are in separate @TableTest methods
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ✅ Eval eval-9-bonus-contractor-structure

**13/13** · 190755 tokens · 36337ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **contractor-uses-value-set**: CONTRACTOR row uses a value set for Department (e.g. {SALES, ENGINEERING}) to express 'regardless of department' — not a dummy placeholder like 'ANY' or 'N/A', and not enumerated as separate rows.
- ✅ **four-core-rules-covered**: The four level×department combinations (SENIOR+SALES, SENIOR+ENGINEERING, JUNIOR+SALES, JUNIOR+ENGINEERING) are all present in the table(s)
- ✅ **expects-bonus-percentage**: The expected output column contains bonus percentages (e.g. 15%, 12%, 0%) — not bonus amounts. The method returns a percentage, and the table should reflect that.
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'bonusByLevelAndDepartment' → 'Bonus By Level And Department'). Not a generic name like 'test1' or 'testMethod'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table already express — such as how bonus rates are applied (e.g. as percentage of base salary), or open questions. Does NOT merely restate the column names or summarise what the rows show. It is acceptable to omit @Description if the table already conveys all relevant context.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Senior in Sales', 'Contractor regardless of department') — not outcomes ('Gets 15%', 'No bonus').
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Level', 'Department', 'Bonus %') — not implementation terms like 'employeeLevel', 'deptCode', 'result'.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-14-weekly-pay

**15/20** · 267152 tokens · 182335ms

- ✅ **1.2-error-has-expected-column**: The error/rejection table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
- ✅ **1.3-depth-overtime-boundary**: Overtime threshold boundary is covered: at least a value at the threshold (40) and just above (41 or similar). Not just values well below and well above.
- ✅ **1.4-depth-combined-scenario**: A combined scenario is present: a row with weekday + Sunday + holiday hours, testing the composition of all hour types.
- ❌ **1.5-depth-error-edge-cases**: Error/edge cases covered: at least negative hours (floored at zero) and negative or missing rate (rejected).
  > Only negative rate is covered (Negative rate | -10.00). No test for negative hours floored to zero.
- ❌ **1.6-readability-empty-cells**: Sunday and Holiday hour columns use empty cells (not 0) when those inputs are not relevant to a scenario — making it easier to spot which rows involve weekend/holiday hours. This is a readability preference; parameter types should be Integer (not int) to support null from blank cells.
  > weekdayPay table has '0' values instead of empty cells for Sunday/Holiday. Parameter types are double, not Integer/nullable.
- ✅ **1.7-readability-scenario-names**: Scenario names describe work patterns (e.g. 'Part-time', 'Five hours overtime', 'Full Sunday shift') — not 'Test case 1' or expected outcomes.
- ✅ **1.8-correctness-expected-values**: Expected values in intermediate and total columns are arithmetically correct for every row, consistent with the formulas in @Description.
- ✅ **1.9-correctness-value-set-semantics**: Value sets are only used where all values in the set produce the same expected result (e.g. any rate × 0 = 0). Not used where results would differ.
- ✅ **1.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ❌ **1.11-format-description**: @Description is present on at least the pay calculation method and provides context beyond what the table rows already express — such as the overtime threshold (40 hours), premium multipliers (Sunday 2×, Holiday 2.5×), or other fixed assumptions not visible in the table. Does NOT merely restate the column names or summarise what the rows show.
  > @Description texts are vague: 'Sunday and holiday hours are 0 for all rows' restates column values rather than explaining multipliers or thresholds
- ✅ **1.12-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **1.13-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **1.14-depth-zero-rate**: A row where the hourly rate is zero, showing that pay is zero regardless of hours worked. This is a distinct edge case from negative rate (which is rejected).
  > No row with rate = 0.00 present in any table
- ✅ **1.15-format-clean-method**: The test method body contains no if/ternary null-handling or parsing logic. Null-to-default conversion (e.g. blank Sunday hours → 0) is handled via a @TypeConverter method or a private helper, keeping the test method limited to arrange/act/assert.
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Weekday hours', 'Sunday hours', 'Hourly rate', 'Weekly pay?') — not implementation terms like 'weekdayHrs', 'int', 'param1'.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ❌ **separates-classification-and-calculation**: Payable hours categorisation (1x regular, 1.5x overtime, 2x Sunday/holiday) and pay calculation (hours × rate) are in separate @TableTest methods
  > No explicit @TableTest method for categorising hours into 1x/1.5x/2x buckets. Tables show only final pay calculation results.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-15-reis-discount

**7/20** · 66528 tokens · 92337ms

- ❌ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
  > Single monolithic table mixing traveler category and discount tier rules. No separate rolling window table for boundary testing (30-day inclusion/exclusion).
- ✅ **2.2-children-flat-discount**: Children's flat 20% discount is represented — either as a row in the eligibility table or a separate note. It does not follow the ladder.
- ✅ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers derived from the 'every fifth trip' rule, with boundary values or value sets showing which ticket counts produce which discount. The maximum 40% discount is represented.
- ❌ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
  > No separate rolling window table; no explicit boundary test for 30-day inclusion/exclusion at exactly 30 vs 31 days.
- ❌ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
  > Row 'Senior — same rules as adult | Senior | {0, 4, 9} | {0%, 5%, 10%}' mixes three separate discount tiers in one row; semantically unclear if {0,4,9} all map to {0%,5%,10%} or if pairing is positional.
- ❌ **2.15-ticket-count-uses-value-sets**: The discount ladder table uses value sets for the ticket count column to group counts within the same tier (e.g. {5, 6, 7, 8, 9} or {5, 9} for min/max → 5%) — not one row per boundary value. The 'every fifth trip' rule creates tiers; the table should express tiers, not enumerate boundaries.
  > Table enumerates individual boundary rows (e.g. 'Adult — fifth purchase, first discount kicks in | Adult | 4 | 5%') rather than grouping ticket counts into value sets per tier like {4..8} → 5%.
- ✅ **2.16-no-duplicate-tier-mapping**: The tier-to-discount mapping is expressed once — not repeated across multiple tables. A price application table (if present) should test the arithmetic (base price × discount → final price) without re-enumerating which ticket counts map to which tier.
- ✅ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ❌ **2.11-format-description**: @Description is present on at least the class or the tier method and provides context beyond what the table rows already express — such as the discount ladder structure, eligibility rules, or application context. Does NOT merely restate the column names or summarise what the rows show.
  > Response is specification only; no code with @Description annotations generated. Notes section restates column meanings but does not provide rich context on the domain intent.
- ✅ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **2.14-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **2.17-zone-irrelevance-visible**: The eligibility/passenger-type table includes a Zone column (or equivalent) with a value set to show that the discount applies regardless of zone — rather than silently omitting zone. The requirement explicitly says 'regardless of the zones you travel in'.
  > No Zone column in table. Notes mention 'Zone validity does not affect the discount' but this is silent omission, not explicit representation in the table structure.
- ❌ **2.18-adult-senior-value-set**: Adult and senior are expressed as a value set {ADULT, SENIOR} (or equivalent) in at least one row of the eligibility table, since they follow identical discount rules. Not enumerated as separate rows with the same outcome.
  > Adult and Senior rows are enumerated separately (multiple 'Adult' rows, one 'Senior' row), not consolidated into a {ADULT, SENIOR} value set.
- ❌ **2.19-depth-all-tiers**: All 9 discount tiers are represented in the ladder table: 0% (tickets 1-4), 5% (5-9), 10% (10-14), 15% (15-19), 20% (20-24), 25% (25-29), 30% (30-34), 35% (35-39), and 40% (40+). Not just a subset of tiers.
  > Table shows tiers at 0%, 5%, 10%, 15%, 40% but omits 20%, 25%, 30%, 35% tiers. Only 5 of 9 tiers represented.
- ❌ **2.20-readability-one-row-per-tier**: Each tier in the discount ladder is expressed as a single row with a value set for ticket counts — not split across multiple rows (e.g. separate rows for 'tier kicks in' and 'tier holds'). One tier = one row.
  > 5% tier split across two rows ('Adult — fifth purchase...' and 'Adult — ninth purchase, same tier'); 10% tier also split ('Adult — tenth purchase' and 'Adult — fourteenth purchase').
- ❌ **2.21-readability-relative-time**: The rolling window table expresses time relatively (e.g. '14 days ago', '30 days ago', or an integer 'Days ago' column) rather than using absolute dates, making the table readable without needing to know the reference date.
  > No separate rolling window table present. No relative time column (e.g. 'Days ago') to test boundary at exactly 30 vs 31 days.
- ❌ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
  > Single monolithic table mixes three concerns: discount tier rule, traveler eligibility (child vs adult/senior), and rolling window history counting.
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
  > Table enumerates 13 rows covering boundary scenarios; separate tables would reduce rows per concern (e.g. 9 rows for tier ladder alone, 3 rows for eligibility).
- ❌ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
  > Build file does not contain tabletest-junit dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-18-convert-from-code

**19/21** · 420608 tokens · 107985ms

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
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ✅ **separates-decision-and-premium**: Decision logic (AUTO_APPROVED/REJECTED/APPROVED) and premium calculation are in separate @TableTest methods
- ✅ **no-reimplemented-internals**: Test method body does not recompute internal formulas (e.g. risk score calculation). It only calls the public API (evaluateApplication) and asserts on the return value's fields. No arithmetic duplicating private method logic appears in the test.
- ❌ **description-no-internals**: @Description explains the test's purpose and business context — not internal formulas or implementation details (e.g. 'riskScore = age/10 + claimCount*15', 'rejected when riskScore > 75'). Black-box tests describe observable behaviour, not code internals.
  > @Description for decision test states 'Risk score = (age / 10) + (claimCount * 15). Rejection threshold is risk score > 75.' This explicitly exposes internal formulas.
- ❌ **depth-decision-boundaries**: Approval decision table covers the rejection boundary thoroughly: 5 claims triggers rejection regardless of realistic age (risk score crosses 75), 4 claims results in approval. Rows at 4 and 5 claims with varying ages demonstrate this cliff. Senior vs non-senior distinction (64 vs 65) also visible in the decision table.
  > Decision table has only 0, 0, 2, 0, 0 claims in rows—no 4 vs 5 boundary test. No age 64 vs 65 senior threshold boundary test present.
- ✅ **depth-premium-boundaries**: Approved premium table covers boundary effects: the premium jump from 0 to 1 claim (impact of +15 risk score on premium), and the premium difference at age 64 vs 65 (standard vs senior formula). These boundary conditions document how premium changes at each threshold.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against InsuranceEvaluator

### ✅ Eval eval-19-convert-from-parameterized

**11/11** · 210138 tokens · 29410ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column — this was missing in the @ParameterizedTest and should be added.
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Valid?' or 'Expected?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Valid standard format', 'Missing local part') — not outcomes ('True', 'False') or 'Test case 1'.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against EmailValidator

### ✅ Eval eval-20-collections-and-quoting

**15/15** · 287433 tokens · 261598ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **list-syntax-correct**: List values in the table use bracket syntax like [tech:java, biz:sales, dev:ci] — not comma-separated strings without brackets.
- ✅ **empty-list-explicit**: Empty list input uses [] (not a blank cell, which represents null). The distinction between empty list and null is preserved.
- ✅ **special-chars-quoted**: Values containing pipes (|), brackets, or colons (:) are properly quoted (e.g. "tech:java") so they don't conflict with table syntax.
- ✅ **set-syntax-correct**: Set<String> values in the table use curly brace syntax like {tech, dev} — not bracket syntax [tech, dev] which is for lists.
- ✅ **newline-in-cell**: Cell values containing newlines use \n escaping — not literal line breaks that would break the table row structure.
- ✅ **pipe-quoted**: Values containing pipe characters (|) are quoted so they don't conflict with the markdown table column separator.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Filtered tags?' or 'Result?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-22-event-registration-tt

**20/25** · 213734 tokens · 153019ms

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
- ❌ **validation-includes-optional-fields**: Validation @TableTest includes Dietary Requirements and Accessibility Needs columns, with rows showing both null (blank) and non-null values are accepted — not hardcoded as null in the method body.
  > @Description states optional fields are 'null throughout' and method call uses 'null' for these parameters; table does not include columns showing both null and non-null values
- ✅ **descriptive-registration-date**: Registration date uses descriptive values (e.g. 'before cutoff', 'on cutoff', 'after cutoff') with a @TypeConverter method converting to actual dates — not raw date literals like '2025-02-28'. Readability over precision.
- ✅ **cutoff-date-column-if-literal-dates**: If registration dates are literal (e.g. '2025-02-28' instead of descriptive), the early-bird cutoff date appears as a separate policy column so the reader can see both dates and verify the comparison. Passes automatically if descriptive date values are used.
- ❌ **description-no-irrelevant-information**: If @Description is present, it does not include information that is already visible in the table columns or that can be derived from the table structure — such as fixed input values that could be columns, or restating the discount rules that the rows already demonstrate.
  > appliesDiscount @Description restates rules already shown in table: 'Base price is £100...early-bird cutoff is 2025-03-01: registrations strictly before that date get 20% off. Groups of 5 or more get 15% off...'
- ❌ **discount-column-preferred**: Output column is 'Discount?' (percentage or amount off) rather than 'Price?' — making verification simpler (discount is the rule's direct output; price requires knowing the base price). If 'Price?' is used instead, both 'Base price' and 'Price?' columns must be present so the reader can trace the calculation.
  > appliesDiscount table has both 'Price?' and 'Discount?' columns but lacks a 'Base price' column; reader cannot independently verify the price calculation from base
- ❌ **single-assertion-in-method**: Each @TableTest method has a single, uniform assertion pattern applied to all rows — not different assertions per scenario. Validation checks one thing (e.g. error message or acceptance status); pricing checks one thing (e.g. discount or price). No if/switch in assertion logic.
  > Method has 2 assertions; Method has 3 assertions
- ❌ **optional-fields-has-expected-column**: The optional-fields acceptance table includes an expectation/output column (e.g. 'Approved?', 'Result?', or 'Status?') asserting that each combination is accepted — not just varying inputs without any expected column.
  > No separate optional-fields table exists; validation table does not demonstrate acceptance of both null and non-null optional field values
- ✅ **description-no-redundant-field-values**: Scenario descriptions do not include specific field values that are already visible in the table or that serve as 'any valid value' placeholders. Stating 'name: John, email: john@example.com, registration date: 2025-04-01' in a description adds maintenance burden without value — it suffices to say 'valid values supplied' or describe the scenario condition. Applies to all tables.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-23-loan-approval-tt

**15/18** · 167432 tokens · 75836ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **threshold-as-column**: Policy thresholds (650/600 credit scores) appear in a dedicated policy column (e.g. 'Credit threshold') separate from the applicant's actual credit score column. The reader sees both the threshold and the score, making the comparison explicit. Not just threshold values used as data in the score column.
  > Credit thresholds (650 and 600) are embedded in row values ('above threshold', 'at threshold') and test logic, not shown as separate columns.
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
  > Age and credit thresholds are in creditScoreThreshold; income effects in incomeInformation. No third method combines all three for complete verdicts.
- ❌ **description-no-redundant-field-values**: Scenario descriptions and @Description text do not include specific field values that are already visible in the table or that serve as 'any valid value' placeholders. Stating 'Customer age is 35 for all rows' in @Description when age is fixed in the method body, or including fixed values in scenario names that are already columns, adds maintenance burden without value. Describe the scenario condition or policy being tested instead.
  > creditScoreThreshold @Description states 'All rows use stable income to isolate the threshold decision' — this fixed value is already visible in the table.
- ✅ **depth-stable-income-effect**: The standardApplicantCreditThreshold table (or equivalent non-senior table) clearly documents the effect of stable income: rows show that above-threshold credit score + stable income = APPROVED, and above-threshold credit score + no stable income = REJECTED. The stable income parameter's effect on the outcome must be visible from the table rows, not just implied.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-25-convert-from-spock

**16/19** · 456613 tokens · 234218ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells. Rows with no options use a blank cell or [:].
  > Options appear as separate columns: 'Fragile | Insured value | Handling' in surchargesAppliedToBaseCost table, not collapsed into a map column.
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
  > No @TypeConverter method is present in the test file. Options are handled directly as separate parameters.
- ❌ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table — not as three separate length/width/height columns
  > surchargesAppliedToBaseCost and dimensionalWeightOverridesActualWeight tables have separate 'Length | Width | Height' columns, not a list column.
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
- ✅ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern — not one monolithic table mixing base rates, surcharges, dimensional weight, and carrier equivalence
- ✅ **numeric-types-correct**: Parameter types correspond to the ShippingCostCalculator.calculateShippingCost interface — weight, cost, and dimension types match the method signature.
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('EU express, light package', 'Oversized with fragile') — not outcomes ('8.00', 'Surcharge applied') or generic labels ('Test 1').
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ✅ **business-language-columns**: Column names use domain language ('Zone', 'Weight (kg)', 'Dimensions', 'Options', 'Cost?') — not code identifiers ('shippingZone', 'weightKg', 'dims')
- ✅ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **output-is-kotlin**: The generated test file is written in Kotlin (.kt), not Java or Groovy. Uses Kotlin syntax: fun, val/var, Kotlin imports.
- ✅ **no-groovy-syntax**: Output contains no Groovy or Spock syntax: no def, where:, expect:, given:, GString interpolation (#variable), Spock assertions (thrown(), old()), or Groovy list literals ([] as Set).
- ✅ **has-tabletest-dependency**: The build file (build.gradle.kts) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against ShippingCostCalculator

### ⚠️ Eval eval-26-convert-from-kotest

**16/19** · 197221 tokens · 73967ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells. Rows with no options use a blank cell or [:].
  > Options are kept as three separate columns: 'Fragile', 'Insured Value', 'Handling' with blank cells for missing values.
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
  > No @TypeConverter method is present in the test class. Options are constructed in test body as 'PackageOptions(isFragile=..., insuredValue=..., handling=...)'.
- ✅ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table — not as three separate length/width/height columns
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
- ❌ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern — not one monolithic table mixing base rates, surcharges, dimensional weight, and carrier equivalence
  > Only one @TableTest method exists. Table rows mix base rates, surcharges, dimensional weight, and carrier equivalence in single monolithic test.
- ✅ **numeric-types-correct**: Parameter types correspond to the ShippingCostCalculator.calculateShippingCost interface — weight, cost, and dimension types match the method signature.
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('EU express, light package', 'Oversized with fragile') — not outcomes ('8.00', 'Surcharge applied') or generic labels ('Test 1').
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ✅ **business-language-columns**: Column names use domain language ('Zone', 'Weight (kg)', 'Dimensions', 'Options', 'Cost?') — not code identifiers ('shippingZone', 'weightKg', 'dims')
- ✅ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **output-is-kotlin**: The generated test file is written in Kotlin (.kt), not Java. Uses Kotlin syntax: fun, val/var, Kotlin imports.
- ✅ **no-kotest-syntax**: Output contains no Kotest syntax: no forAll, row(), withData, shouldBe, context(), FunSpec, or Kotest imports.
- ✅ **has-tabletest-dependency**: The build file (build.gradle.kts) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against ShippingCostCalculator

### ⚠️ Eval eval-27-convert-from-testng

**12/18** · 267141 tokens · 120757ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells. Rows with no options use a blank cell or [:].
  > Options are in three separate columns (Fragile, Insured Value, Handling) with mostly-blank cells, not collapsed into a map column.
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
  > No @TypeConverter for PackageOptions exists. Only ShippingZone has a @TypeConverter. PackageOptions is manually constructed in test body.
- ✅ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table — not as three separate length/width/height columns
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
- ❌ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern — not one monolithic table mixing base rates, surcharges, dimensional weight, and carrier equivalence
  > Only one @TableTest method exists. Table mixes base rates, surcharges, dimensional weight, and carrier equivalence in a single table.
- ✅ **numeric-types-correct**: Parameter types correspond to the ShippingCostCalculator.calculateShippingCost interface — weight, cost, and dimension types match the method signature.
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('EU express, light package', 'Oversized with fragile') — not outcomes ('8.00', 'Surcharge applied') or generic labels ('Test 1').
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ✅ **business-language-columns**: Column names use domain language ('Zone', 'Weight (kg)', 'Dimensions', 'Options', 'Cost?') — not code identifiers ('shippingZone', 'weightKg', 'dims')
- ❌ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
  > if statement found in method body
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ❌ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
  > Method is named 'calculateShippingCost' with no @DisplayName annotation. Name is generic, not descriptive of test scope.
- ✅ **no-testng-artifacts**: Output contains no TestNG syntax: no @DataProvider, @Test(dataProvider=...), Object[][], or TestNG imports (org.testng.*).
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ❌ **tests-pass**: The generated tests pass when executed against ShippingCostCalculator
  > Tests failed: 
FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':test'.
> Test process encountered an unexpected problem.
   > Could not start Gradle Test Executor 29.
      >

### ⚠️ Eval eval-28-convert-from-methodsource

**13/18** · 200865 tokens · 113387ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells. Rows with no options use a blank cell or [:].
  > Options are in three separate columns: 'Fragile', 'Insured value', 'Handling'. They are not collapsed into a single map column.
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
  > No @TypeConverter for PackageOptions/options map exists. Only parseShippingZone converter is present.
- ✅ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table — not as three separate length/width/height columns
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
- ❌ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern — not one monolithic table mixing base rates, surcharges, dimensional weight, and carrier equivalence
  > Only one @TableTest method exists. Table mixes base rates, surcharges, dimensional weight, and carrier scenarios.
- ✅ **numeric-types-correct**: Parameter types correspond to the ShippingCostCalculator.calculateShippingCost interface — weight, cost, and dimension types match the method signature.
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('EU express, light package', 'Oversized with fragile') — not outcomes ('8.00', 'Surcharge applied') or generic labels ('Test 1').
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ✅ **business-language-columns**: Column names use domain language ('Zone', 'Weight (kg)', 'Dimensions', 'Options', 'Cost?') — not code identifiers ('shippingZone', 'weightKg', 'dims')
- ❌ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
  > if statement found in method body
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ❌ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
  > Method 'calculateShippingCost' lacks @DisplayName. No descriptive title annotations present on either test method.
- ✅ **no-methodsource-artifacts**: Output contains no @MethodSource syntax: no @MethodSource, Stream<Arguments>, Arguments.of, or JUnit Jupiter parameterized imports (org.junit.jupiter.params.provider.*).
- ✅ **has-tabletest-dependency**: The build file (pom.xml) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against ShippingCostCalculator

### ⚠️ Eval eval-29-shopping-cart-tt

**21/23** · 442647 tokens · 471698ms

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
  > Column 'Product ID' in addItem table uses camelCase identifier style instead of 'Product'; also uses 'cartItems', 'inventoryService' in checkout vs business-friendly alternatives
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **single-assertion-in-method**: Each @TableTest method has a single, uniform assertion pattern applied to all rows — not different assertions per scenario.
  > Method has 4 assertions; Method has 4 assertions; Method has 4 assertions; Method has 3 assertions
- ✅ **test-data-visible**: Product prices and coupon rules (type, amount, expiry status) are visible in the table via columns, @Description, or explicit setup — not silently hardcoded. The reader can trace how inputs lead to outputs.
- ✅ **type-converters-for-complex-objects**: TypeConverter methods are used to convert map/string table values into domain objects (e.g. Map → Cart, Map → InventoryService, map → Coupon). Test method bodies contain only arrange-act-assert — no object construction from raw table values.
- ✅ **coupon-as-single-column**: Coupon data is expressed in a single column with a @TypeConverter — not spread across separate sparse columns for coupon type, value, and target product. The column format may be map syntax (e.g. [type: PERCENTAGE, value: 10]) or readable shorthand (e.g. '20% cart', '$5 off', '50% off widget').
- ✅ **uses-standard-map-syntax**: Columns representing maps (e.g. cart contents, product catalogue, inventory levels) use standard TableTest map syntax — [k: v, k2: v2] for entries and [:] for empty map — not a custom bracket notation. Custom string-parsing TypeConverters are not needed for data that maps naturally to Map<String, X>.
- ✅ **description-not-redundant-with-scenarios**: @Description annotations do not repeat information that the scenario names already convey. Coupon type behavior (what PERCENTAGE/FIXED/PRODUCT mean) should be expressed through descriptive scenario names, not duplicated in the description.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ✅ Eval eval-30-order-splitting-tt

**20/20** · 221110 tokens · 316625ms

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

## Variant vs Official (iterations 35, 34, 33, 32, 31, 30, 29, 28, 27, 26, 25 merged)

### Per-Eval Resource Comparison

| Eval | Pass (off) | Pass (var) | Tokens (off) | Tokens (var) | Tok Δ | Cost (off) | Cost (var) | Cost Δ | Time (off) | Time (var) | Time Δ |
|------|------------|------------|--------------|--------------|-------|------------|------------|--------|------------|------------|--------|
| eval-1-convert-repetitive-tests | 13/13 | 13/13 | 190762 | 176044 | -8% | $0.1390 | $0.1388 | +-0% | 29.3s | 24.3s | -17% |
| eval-2-parse-dates | 15/15 | 12/15 | 190645 | 162916 | -15% | $0.2084 | $0.1775 | -15% | 63.9s | 48.8s | -24% |
| eval-3-dependency-setup | 4/4 | 4/4 | 66722 | 44238 | -34% | $0.0746 | $0.0740 | -1% | 10.2s | 8.8s | -13% |
| eval-7-permission-check | 12/13 | 10/13 | 251446 | 188134 | -25% | $0.1927 | $0.1679 | -13% | 46.1s | 28.3s | -39% |
| eval-8-money-parse | 15/15 | 14/15 | 224374 | 201701 | -10% | $0.2295 | $0.2167 | -6% | 88.0s | 89.0s | +1% |
| eval-9-bonus-contractor-structure | 13/13 | 13/13 | 252175 | 190755 | -24% | $0.2169 | $0.1578 | -27% | 54.2s | 36.3s | -33% |
| eval-14-weekly-pay | 16/20 | 15/20 | 196073 | 267152 | +36% | $0.3177 | $0.3653 | +15% | 176.2s | 182.3s | +4% |
| eval-15-reis-discount | 10/20 | 7/20 | 68199 | 66528 | -2% | $0.2288 | $0.2127 | -7% | 133.9s | 92.3s | -31% |
| eval-18-convert-from-code | 15/21 | 19/21 | 354903 | 420608 | +19% | $0.2825 | $0.3133 | +11% | 94.2s | 108.0s | +15% |
| eval-19-convert-from-parameterized | 11/11 | 11/11 | 224054 | 210138 | -6% | $0.1708 | $0.1492 | -13% | 29.7s | 29.4s | -1% |
| eval-20-collections-and-quoting | 15/15 | 15/15 | 263766 | 287433 | +9% | $0.4095 | $0.4708 | +15% | 205.0s | 261.6s | +28% |
| eval-22-event-registration-tt | 19/25 | 20/25 | 250561 | 213734 | -15% | $0.3304 | $0.2761 | -16% | 153.0s | 153.0s | +0% |
| eval-23-loan-approval-tt | 11/18 | 15/18 | 207529 | 167432 | -19% | $0.2531 | $0.1914 | -24% | 109.0s | 75.8s | -30% |
| eval-25-convert-from-spock | 13/19 | 16/19 | 347773 | 456613 | +31% | $0.3297 | $0.4884 | +48% | 135.9s | 234.2s | +72% |
| eval-26-convert-from-kotest | 15/19 | 16/19 | 322883 | 197221 | -39% | $0.3603 | $0.2298 | -36% | 159.6s | 74.0s | -54% |
| eval-27-convert-from-testng | 16/18 | 12/18 | 328845 | 267141 | -19% | $0.4284 | $0.2692 | -37% | 204.2s | 120.8s | -41% |
| eval-28-convert-from-methodsource | 16/18 | 13/18 | 316998 | 200865 | -37% | $0.4063 | $0.2502 | -38% | 221.0s | 113.4s | -49% |
| eval-29-shopping-cart-tt | 22/23 | 21/23 | 830562 | 442647 | -47% | $0.8843 | $0.8424 | -5% | 457.6s | 471.7s | +3% |
| eval-30-order-splitting-tt | 18/20 | 20/20 | 506338 | 221110 | -56% | $1.0219 | $0.5691 | -44% | 473.6s | 316.6s | -33% |
| **Totals (19 comparable)** | **269/320** | **266/320** | **5394608** | **4382410** | **-19%** | **$6.4848** | **$5.5604** | **-14%** | **2844.5s** | **2468.7s** | **-13%** |

**Comparable summary (19 evals in both):**

| Source | Pass Rate | Tokens | Cost | Time |
|--------|-----------|--------|------|------|
| minimal (iter 8) | 266/320 (83.1%) | 4382410 | $5.5604 | 2468.7s |
| official | 269/320 (84.1%) | 5394608 | $6.4848 | 2844.5s |
| **Δ** | | **-19%** | **-14%** | **-13%** |

### Per-Assertion Comparison

| Eval | Assertion | official | minimal |
|------|-----------|----------|---------|
| eval-2-parse-dates | empty-string-uses-quotes | ✅ | ❌ |
| eval-2-parse-dates | concerns-decomposed | ✅ | ❌ |
| eval-2-parse-dates | minimal-rows-per-concern | ✅ | ❌ |
| eval-7-permission-check | fewer-than-nine-rows | ✅ | ❌ |
| eval-7-permission-check | has-descriptive-title | ✅ | ❌ |
| eval-8-money-parse | no-if-switch-in-method | ✅ | ❌ |
| eval-14-weekly-pay | 1.11-format-description | ✅ | ❌ |
| eval-15-reis-discount | 2.1-decomposition-concern-separation | ✅ | ❌ |
| eval-15-reis-discount | 2.10-format-displayname | ❌ | ✅ |
| eval-15-reis-discount | 2.18-adult-senior-value-set | ✅ | ❌ |
| eval-15-reis-discount | 2.19-depth-all-tiers | ✅ | ❌ |
| eval-15-reis-discount | 2.21-readability-relative-time | ✅ | ❌ |
| eval-18-convert-from-code | concerns-decomposed | ❌ | ✅ |
| eval-18-convert-from-code | minimal-rows-per-concern | ❌ | ✅ |
| eval-18-convert-from-code | separates-decision-and-premium | ❌ | ✅ |
| eval-18-convert-from-code | depth-premium-boundaries | ❌ | ✅ |
| eval-22-event-registration-tt | validation-includes-optional-fields | ✅ | ❌ |
| eval-22-event-registration-tt | descriptive-registration-date | ❌ | ✅ |
| eval-22-event-registration-tt | cutoff-date-column-if-literal-dates | ❌ | ✅ |
| eval-22-event-registration-tt | optional-fields-has-expected-column | ✅ | ❌ |
| eval-22-event-registration-tt | description-no-redundant-field-values | ❌ | ✅ |
| eval-23-loan-approval-tt | scenario-names-describe-conditions | ❌ | ✅ |
| eval-23-loan-approval-tt | concerns-decomposed | ❌ | ✅ |
| eval-23-loan-approval-tt | minimal-rows-per-concern | ❌ | ✅ |
| eval-23-loan-approval-tt | depth-stable-income-effect | ❌ | ✅ |
| eval-25-convert-from-spock | dimensions-as-list | ✅ | ❌ |
| eval-25-convert-from-spock | concerns-decomposed | ❌ | ✅ |
| eval-25-convert-from-spock | business-language-columns | ❌ | ✅ |
| eval-25-convert-from-spock | has-descriptive-title | ❌ | ✅ |
| eval-25-convert-from-spock | tests-pass | ❌ | ✅ |
| eval-26-convert-from-kotest | options-type-converter | ✅ | ❌ |
| eval-26-convert-from-kotest | has-descriptive-title | ❌ | ✅ |
| eval-26-convert-from-kotest | tests-pass | ❌ | ✅ |
| eval-27-convert-from-testng | options-as-map | ✅ | ❌ |
| eval-27-convert-from-testng | options-type-converter | ✅ | ❌ |
| eval-27-convert-from-testng | no-if-switch-in-method | ✅ | ❌ |
| eval-27-convert-from-testng | has-descriptive-title | ✅ | ❌ |
| eval-28-convert-from-methodsource | concerns-decomposed | ✅ | ❌ |
| eval-28-convert-from-methodsource | no-if-switch-in-method | ✅ | ❌ |
| eval-28-convert-from-methodsource | has-descriptive-title | ✅ | ❌ |
| eval-29-shopping-cart-tt | business-language-columns | ✅ | ❌ |
| eval-30-order-splitting-tt | scalar-quantity-for-warehouse | ❌ | ✅ |
| eval-30-order-splitting-tt | no-if-switch-in-method | ❌ | ✅ |

### Load-Bearing Assertions

Assertions that pass with official skill but fail with minimal variant:

- eval-2-parse-dates: `empty-string-uses-quotes`
- eval-2-parse-dates: `concerns-decomposed`
- eval-2-parse-dates: `minimal-rows-per-concern`
- eval-7-permission-check: `fewer-than-nine-rows`
- eval-7-permission-check: `has-descriptive-title`
- eval-8-money-parse: `no-if-switch-in-method`
- eval-14-weekly-pay: `1.11-format-description`
- eval-15-reis-discount: `2.1-decomposition-concern-separation`
- eval-15-reis-discount: `2.18-adult-senior-value-set`
- eval-15-reis-discount: `2.19-depth-all-tiers`
- eval-15-reis-discount: `2.21-readability-relative-time`
- eval-22-event-registration-tt: `validation-includes-optional-fields`
- eval-22-event-registration-tt: `optional-fields-has-expected-column`
- eval-25-convert-from-spock: `dimensions-as-list`
- eval-26-convert-from-kotest: `options-type-converter`
- eval-27-convert-from-testng: `options-as-map`
- eval-27-convert-from-testng: `options-type-converter`
- eval-27-convert-from-testng: `no-if-switch-in-method`
- eval-27-convert-from-testng: `has-descriptive-title`
- eval-28-convert-from-methodsource: `concerns-decomposed`
- eval-28-convert-from-methodsource: `no-if-switch-in-method`
- eval-28-convert-from-methodsource: `has-descriptive-title`
- eval-29-shopping-cart-tt: `business-language-columns`

### Attention Needed

Evals with failed assertions or disproportionate resource usage (>2x official):

| Eval | Issue | Details |
|------|-------|---------|
| eval-2-parse-dates | Failed assertions | `empty-string-uses-quotes`, `concerns-decomposed`, `minimal-rows-per-concern` |
| eval-7-permission-check | Failed assertions | `fewer-than-nine-rows`, `has-descriptive-title` |
| eval-8-money-parse | Failed assertions | `no-if-switch-in-method` |
| eval-14-weekly-pay | Failed assertions | `1.11-format-description` |
| eval-15-reis-discount | Failed assertions | `2.1-decomposition-concern-separation`, `2.18-adult-senior-value-set`, `2.19-depth-all-tiers`, `2.21-readability-relative-time` |
| eval-22-event-registration-tt | Failed assertions | `validation-includes-optional-fields`, `optional-fields-has-expected-column` |
| eval-25-convert-from-spock | Failed assertions | `dimensions-as-list` |
| eval-26-convert-from-kotest | Failed assertions | `options-type-converter` |
| eval-27-convert-from-testng | Failed assertions | `options-as-map`, `options-type-converter`, `no-if-switch-in-method`, `has-descriptive-title` |
| eval-28-convert-from-methodsource | Failed assertions | `concerns-decomposed`, `no-if-switch-in-method`, `has-descriptive-title` |
| eval-29-shopping-cart-tt | Failed assertions | `business-language-columns` |

