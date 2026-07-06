# Eval Review — tabletest variant=minimal, Iteration 7

**Model:** sonnet · **Date:** 2026-04-17 · **Evals:** 19

## Summary

268/320 (83.8%) · 5118964 tokens · 2501.6s · $5.8923

## Delta vs Iteration 6

**Regressions (18):**
- ❌ eval-2-parse-dates: `empty-string-uses-quotes`
- ❌ eval-14-weekly-pay: `1.12-format-annotation-order`
- ❌ eval-14-weekly-pay: `separates-classification-and-calculation`
- ❌ eval-15-reis-discount: `2.1-decomposition-concern-separation`
- ❌ eval-15-reis-discount: `2.21-readability-relative-time`
- ❌ eval-15-reis-discount: `concerns-decomposed`
- ❌ eval-18-convert-from-code: `has-tabletest-annotation`
- ❌ eval-18-convert-from-code: `scenario-column-present`
- ❌ eval-18-convert-from-code: `has-question-mark-column`
- ❌ eval-18-convert-from-code: `no-if-switch-in-method`
- ❌ eval-18-convert-from-code: `has-descriptive-title`
- ❌ eval-18-convert-from-code: `no-reimplemented-internals`
- ❌ eval-18-convert-from-code: `has-tabletest-dependency`
- ❌ eval-20-collections-and-quoting: `newline-in-cell`
- ❌ eval-25-convert-from-spock: `business-language-columns`
- ❌ eval-25-convert-from-spock: `tests-pass`
- ❌ eval-26-convert-from-kotest: `concerns-decomposed`
- ❌ eval-28-convert-from-methodsource: `tests-pass`

**Improvements (26):**
- ✅ eval-1-convert-repetitive-tests: `has-tabletest-dependency`
- ✅ eval-14-weekly-pay: `1.2-error-has-expected-column`
- ✅ eval-14-weekly-pay: `1.8-correctness-expected-values`
- ✅ eval-14-weekly-pay: `1.14-depth-zero-rate`
- ✅ eval-14-weekly-pay: `has-tabletest-dependency`
- ✅ eval-14-weekly-pay: `compiles`
- ✅ eval-15-reis-discount: `2.16-no-duplicate-tier-mapping`
- ✅ eval-15-reis-discount: `2.10-format-displayname`
- ✅ eval-15-reis-discount: `2.11-format-description`
- ✅ eval-15-reis-discount: `2.18-adult-senior-value-set`
- ✅ eval-15-reis-discount: `2.19-depth-all-tiers`
- ✅ eval-15-reis-discount: `has-tabletest-dependency`
- ✅ eval-20-collections-and-quoting: `annotation-order`
- ✅ eval-22-event-registration-tt: `description-no-redundant-field-values`
- ✅ eval-23-loan-approval-tt: `business-language-columns`
- ✅ eval-25-convert-from-spock: `concerns-decomposed`
- ✅ eval-25-convert-from-spock: `numeric-types-correct`
- ✅ eval-25-convert-from-spock: `no-if-switch-in-method`
- ✅ eval-25-convert-from-spock: `has-descriptive-title`
- ✅ eval-25-convert-from-spock: `compiles`
- ✅ eval-26-convert-from-kotest: `no-kotest-syntax`
- ✅ eval-27-convert-from-testng: `options-type-converter`
- ✅ eval-27-convert-from-testng: `no-if-switch-in-method`
- ✅ eval-29-shopping-cart-tt: `uses-standard-map-syntax`
- ✅ eval-29-shopping-cart-tt: `description-not-redundant-with-scenarios`
- ✅ eval-30-order-splitting-tt: `scalar-quantity-for-warehouse`

## Resource Comparison vs Iteration 6

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-1-convert-repetitive-tests | 13/13 | 12/13 | 203698 | 204666 | 25.1 | 40.1 |
| eval-2-parse-dates | 14/15 | 15/15 | 164908 | 262048 | 53.0 | 77.8 |
| eval-3-dependency-setup | 4/4 | 4/4 | 44339 | 44389 | 10.0 | 8.4 |
| eval-7-permission-check | 11/13 | 11/13 | 186933 | 246187 | 34.2 | 41.4 |
| eval-8-money-parse | 15/15 | 15/15 | 232011 | 221694 | 90.4 | 50.1 |
| eval-9-bonus-contractor-structure | 13/13 | 13/13 | 213628 | 219005 | 29.9 | 42.3 |
| eval-14-weekly-pay | 15/20 | 12/20 | 297900 | 217422 | 164.1 | 153.7 |
| eval-15-reis-discount | 11/20 | 8/20 | 244941 | 47692 | 108.4 | 151.1 |
| eval-18-convert-from-code | 11/21 | 18/21 | 122236 | 229075 | 167.9 | 178.1 |
| eval-19-convert-from-parameterized | 11/11 | 11/11 | 157557 | 243171 | 25.6 | 35.7 |
| eval-20-collections-and-quoting | 14/15 | 14/15 | 223364 | 246389 | 137.0 | 122.9 |
| eval-22-event-registration-tt | 20/25 | 19/25 | 456915 | 252202 | 183.4 | 134.2 |
| eval-23-loan-approval-tt | 15/18 | 14/18 | 263802 | 204639 | 91.4 | 103.9 |
| eval-25-convert-from-spock | 14/19 | 10/18 | 378869 | 264539 | 198.7 | 114.8 |
| eval-26-convert-from-kotest | 15/19 | 14/18 | 202995 | 258939 | 125.7 | 106.1 |
| eval-27-convert-from-testng | 14/18 | 12/18 | 270028 | 195787 | 114.0 | 84.8 |
| eval-28-convert-from-methodsource | 16/18 | 17/18 | 283536 | 264418 | 154.4 | 162.5 |
| eval-29-shopping-cart-tt | 22/23 | 20/23 | 492674 | 932156 | 319.1 | 455.9 |
| eval-30-order-splitting-tt | 20/20 | 19/20 | 678630 | 494225 | 469.3 | 281.7 |

## Per-Eval Results

### ✅ Eval eval-1-convert-repetitive-tests

**13/13** · 203698 tokens · 25147ms

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

**14/15** · 164908 tokens · 52982ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell (not the string 'null') in the table
- ✅ **exception-handled-cleanly**: The empty string / exception case is handled — either via a Throws? column, assertThrows inside the method, or a separate @TableTest — rather than being silently omitted
- ✅ **localdate-result-column**: There is a result column typed as LocalDate (or using a string representation that maps to LocalDate)
- ✅ **type-conversion-addressed**: The response addresses type conversion for non-trivial column types — either by providing a @TypeConverter/converter method, or by using a representation that leverages built-in conversion (e.g., FQCN for Class<?>, ISO format for LocalDate)
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express — such as where/when the rule applies, or open questions. Does NOT merely restate the column names or summarise what the rows show. It is acceptable to omit @Description if the table already conveys all relevant context
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **empty-string-uses-quotes**: Empty string input uses quoted syntax (e.g. '' or "") in the table — not a blank cell, which represents null. The distinction between empty string and null is preserved.
  > No quoted empty string ('""' or "''") found in table cells
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ✅ **separates-valid-and-invalid**: Valid parsing cases and error/exception cases are in separate @TableTest methods
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ✅ Eval eval-3-dependency-setup

**4/4** · 44339 tokens · 9995ms

- ✅ **flags-junit-version**: Response explicitly flags that JUnit 5.10.0 is below the required minimum (5.11) and recommends upgrading
- ✅ **provides-correct-groupid**: Response provides the tabletest dependency with groupId 'org.tabletest' (not guessed)
- ✅ **provides-artifactid**: Response includes 'tabletest-junit' as the artifactId
- ✅ **includes-test-scope**: The suggested dependency includes <scope>test</scope>

### ⚠️ Eval eval-7-permission-check

**11/13** · 186933 tokens · 34163ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **uses-value-sets**: Table uses value-set syntax (curly braces like {READ, WRITE} or {READ, WRITE, DELETE}) to express rules that hold across multiple actions — rather than one row per role/action combination
- ✅ **fewer-than-nine-rows**: Table has fewer than 9 data rows (i.e. does not enumerate every role/action combination individually)
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ❌ **no-duplicate-role-output**: No two rows share both the same Role value and the same boolean output — forces value set consolidation
  > USER appears in 3 rows (READ=true, WRITE=true, DELETE=false) and GUEST in 3 rows (READ=true, WRITE=false, DELETE=false). These should be consolidated into value sets like {READ, WRITE} and {READ} per role/outcome.
- ❌ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title
  > Method is named 'checkPermission' with no @DisplayName annotation. This is generic and does not clearly describe the test's intent as a camelCase-to-title conversion would require.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Admin performs any action', 'Guest reads') — not outcomes ('Allowed', 'Denied')
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ✅ Eval eval-8-money-parse

**15/15** · 232011 tokens · 90390ms

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
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ✅ Eval eval-9-bonus-contractor-structure

**13/13** · 213628 tokens · 29929ms

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
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-14-weekly-pay

**15/20** · 297900 tokens · 164091ms

- ✅ **1.2-error-has-expected-column**: The error/rejection table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
- ✅ **1.3-depth-overtime-boundary**: Overtime threshold boundary is covered: at least a value at the threshold (40) and just above (41 or similar). Not just values well below and well above.
- ✅ **1.4-depth-combined-scenario**: A combined scenario is present: a row with weekday + Sunday + holiday hours, testing the composition of all hour types.
- ❌ **1.5-depth-error-edge-cases**: Error/edge cases covered: at least negative hours (floored at zero) and negative or missing rate (rejected).
  > Only negative rate (-10.00) is tested in invalidRateRejected. No test for negative hours (floored to zero).
- ❌ **1.6-readability-empty-cells**: Sunday and Holiday hour columns use empty cells (not 0) when those inputs are not relevant to a scenario — making it easier to spot which rows involve weekend/holiday hours. This is a readability preference; parameter types should be Integer (not int) to support null from blank cells.
  > weekdayOvertimeThreshold table shows explicit '0' values, not empty cells (e.g., 'Sunday and holiday | 0 | 0'). Parameter types are 'int' not 'Integer'.
- ✅ **1.7-readability-scenario-names**: Scenario names describe work patterns (e.g. 'Part-time', 'Five hours overtime', 'Full Sunday shift') — not 'Test case 1' or expected outcomes.
- ✅ **1.8-correctness-expected-values**: Expected values in intermediate and total columns are arithmetically correct for every row, consistent with the formulas in @Description.
- ✅ **1.9-correctness-value-set-semantics**: Value sets are only used where all values in the set produce the same expected result (e.g. any rate × 0 = 0). Not used where results would differ.
- ✅ **1.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ❌ **1.11-format-description**: @Description is present on at least the pay calculation method and provides context beyond what the table rows already express — such as the overtime threshold (40 hours), premium multipliers (Sunday 2×, Holiday 2.5×), or other fixed assumptions not visible in the table. Does NOT merely restate the column names or summarise what the rows show.
  > @Description on weekdayOvertimeThreshold states '1.5× applies beyond 40' but uses 'Sunday and holiday hours are fixed at 0' instead of explaining the 2× multiplier for those. combinedWeeklyPay lacks @Description entirely.
- ❌ **1.12-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
  > Order violations: @DisplayName (line 69) after @Description (line 54)
- ✅ **1.13-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **1.14-depth-zero-rate**: A row where the hourly rate is zero, showing that pay is zero regardless of hours worked. This is a distinct edge case from negative rate (which is rejected).
- ✅ **1.15-format-clean-method**: The test method body contains no if/ternary null-handling or parsing logic. Null-to-default conversion (e.g. blank Sunday hours → 0) is handled via a @TypeConverter method or a private helper, keeping the test method limited to arrange/act/assert.
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Weekday hours', 'Sunday hours', 'Hourly rate', 'Weekly pay?') — not implementation terms like 'weekdayHrs', 'int', 'param1'.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ❌ **separates-classification-and-calculation**: Payable hours categorisation (1x regular, 1.5x overtime, 2x Sunday/holiday) and pay calculation (hours × rate) are in separate @TableTest methods
  > No separate table for hour classification. All tables directly test pay calculation without isolating the categorisation logic.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-15-reis-discount

**11/20** · 244941 tokens · 108382ms

- ❌ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
  > Only 2 @TableTest methods present. No separate rolling window table testing 30-day boundary (e.g., ticket at exactly 30 days included vs 31 days excluded).
- ✅ **2.2-children-flat-discount**: Children's flat 20% discount is represented — either as a row in the eligibility table or a separate note. It does not follow the ladder.
- ✅ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers derived from the 'every fifth trip' rule, with boundary values or value sets showing which ticket counts produce which discount. The maximum 40% discount is represented.
- ❌ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
  > No rolling window boundary test present. Tables do not test 30-day vs 31-day inclusion/exclusion.
- ❌ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
  > adultAndSeniorDiscountTier enumerates one ticket count per row (0, 4, 5, 9, 10, 15, 20, 25, 30, 35, 40, 50) rather than value sets like {5,6,7,8,9} per tier.
- ❌ **2.15-ticket-count-uses-value-sets**: The discount ladder table uses value sets for the ticket count column to group counts within the same tier (e.g. {5, 6, 7, 8, 9} or {5, 9} for min/max → 5%) — not one row per boundary value. The 'every fifth trip' rule creates tiers; the table should express tiers, not enumerate boundaries.
  > Table has separate rows per tier boundary: 4→0%, 5→5%, 9→5%, 10→10%, etc. No value sets like {5,6,7,8,9} grouping counts within same tier.
- ✅ **2.16-no-duplicate-tier-mapping**: The tier-to-discount mapping is expressed once — not repeated across multiple tables. A price application table (if present) should test the arithmetic (base price × discount → final price) without re-enumerating which ticket counts map to which tier.
- ✅ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ✅ **2.11-format-description**: @Description is present on at least the class or the tier method and provides context beyond what the table rows already express — such as the discount ladder structure, eligibility rules, or application context. Does NOT merely restate the column names or summarise what the rows show.
- ✅ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **2.14-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **2.17-zone-irrelevance-visible**: The eligibility/passenger-type table includes a Zone column (or equivalent) with a value set to show that the discount applies regardless of zone — rather than silently omitting zone. The requirement explicitly says 'regardless of the zones you travel in'.
  > No Zone column in any table. Discount rule for zone-irrelevance not explicitly represented.
- ✅ **2.18-adult-senior-value-set**: Adult and senior are expressed as a value set {ADULT, SENIOR} (or equivalent) in at least one row of the eligibility table, since they follow identical discount rules. Not enumerated as separate rows with the same outcome.
- ✅ **2.19-depth-all-tiers**: All 9 discount tiers are represented in the ladder table: 0% (tickets 1-4), 5% (5-9), 10% (10-14), 15% (15-19), 20% (20-24), 25% (25-29), 30% (30-34), 35% (35-39), and 40% (40+). Not just a subset of tiers.
- ❌ **2.20-readability-one-row-per-tier**: Each tier in the discount ladder is expressed as a single row with a value set for ticket counts — not split across multiple rows (e.g. separate rows for 'tier kicks in' and 'tier holds'). One tier = one row.
  > Table has 12 rows. Tiers split across multiple rows: 5% tier uses rows for 5 and 9 (two rows per tier, not one).
- ❌ **2.21-readability-relative-time**: The rolling window table expresses time relatively (e.g. '14 days ago', '30 days ago', or an integer 'Days ago' column) rather than using absolute dates, making the table readable without needing to know the reference date.
  > No rolling window table present. Cannot assess relative time representation.
- ❌ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
  > Only 2 methods: one for adult/senior, one for children. Missing third table for rolling window boundary testing. Discount ladder and eligibility could be further decomposed.
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
  > adultAndSeniorDiscountTier has 12 rows enumerating near-complete state space (every threshold + interior). Could reduce to 9 rows (one per tier) with value sets.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-18-convert-from-code

**11/21** · 122236 tokens · 167930ms

- ❌ **has-tabletest-annotation**: Output contains a @TableTest annotation
  > No @TableTest annotation found
- ✅ **black-box-columns**: Table columns represent the method's public inputs (applicant type, age, claim count) and observable outputs (decision, premium) — not internal state like hasActivePolicy, internalRiskScore, or calculateRiskScore.
- ✅ **observable-io-only**: No column is named after private fields or internal variables (hasActivePolicy, internalRiskScore, riskScore). The test treats the method as a black box.
- ❌ **scenario-column-present**: Table has a scenario/description column as the leftmost column
  > No table headers found
- ❌ **business-language-columns**: Column names use domain/business language (e.g. 'Applicant type', 'Age', 'Claims', 'Decision?', 'Premium?') — not code identifiers like 'applicantType', 'claimCount', 'String'.
  > Columns use code identifiers like 'applicantType', 'claimCount' instead of business language like 'Applicant type', 'Claims', 'Decision?', 'Premium?'.
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Renewal with no claims', 'Senior applicant') — not outcomes ('Auto approved', 'Rejected').
- ❌ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Decision?' or 'Premium?')
  > No table headers found
- ❌ **no-if-switch-in-method**: Test method body contains no if or switch statements
  > No @TableTest method bodies found
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ❌ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
  > No @DisplayName annotations or test method names are shown in the response. Only scenario descriptions are provided without method declarations.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ✅ **separates-decision-and-premium**: Decision logic (AUTO_APPROVED/REJECTED/APPROVED) and premium calculation are in separate @TableTest methods
- ❌ **no-reimplemented-internals**: Test method body does not recompute internal formulas (e.g. risk score calculation). It only calls the public API (evaluateApplication) and asserts on the return value's fields. No arithmetic duplicating private method logic appears in the test.
  > No test method body is shown. Response only provides table structures without demonstrating how the test body calls evaluateApplication without reimplementing risk score logic.
- ❌ **description-no-internals**: @Description explains the test's purpose and business context — not internal formulas or implementation details (e.g. 'riskScore = age/10 + claimCount*15', 'rejected when riskScore > 75'). Black-box tests describe observable behaviour, not code internals.
  > Note explains 'age=64 and age=65 with no claims both yield riskScore=6' and references 'integer division', exposing internal risk score calculation details.
- ❌ **depth-decision-boundaries**: Approval decision table covers the rejection boundary thoroughly: 5 claims triggers rejection regardless of realistic age (risk score crosses 75), 4 claims results in approval. Rows at 4 and 5 claims with varying ages demonstrate this cliff. Senior vs non-senior distinction (64 vs 65) also visible in the decision table.
  > Decision table has no row with 4 claims. Only one row with claimCount=5 (REJECTED), one with claimCount=2. No 4-vs-5 boundary demonstration in Concern 1 table.
- ✅ **depth-premium-boundaries**: Approved premium table covers boundary effects: the premium jump from 0 to 1 claim (impact of +15 risk score on premium), and the premium difference at age 64 vs 65 (standard vs senior formula). These boundary conditions document how premium changes at each threshold.
- ❌ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
  > Build file does not contain tabletest-junit dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against InsuranceEvaluator

### ✅ Eval eval-19-convert-from-parameterized

**11/11** · 157557 tokens · 25615ms

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

### ⚠️ Eval eval-20-collections-and-quoting

**14/15** · 223364 tokens · 137005ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **list-syntax-correct**: List values in the table use bracket syntax like [tech:java, biz:sales, dev:ci] — not comma-separated strings without brackets.
- ✅ **empty-list-explicit**: Empty list input uses [] (not a blank cell, which represents null). The distinction between empty list and null is preserved.
- ✅ **special-chars-quoted**: Values containing pipes (|), brackets, or colons (:) are properly quoted (e.g. "tech:java") so they don't conflict with table syntax.
- ✅ **set-syntax-correct**: Set<String> values in the table use curly brace syntax like {tech, dev} — not bracket syntax [tech, dev] which is for lists.
- ❌ **newline-in-cell**: Cell values containing newlines use \n escaping — not literal line breaks that would break the table row structure.
  > @Test method used instead of table row; comment states 'Java text blocks process \n to real newline before TableTest parses'
- ✅ **pipe-quoted**: Values containing pipe characters (|) are quoted so they don't conflict with the markdown table column separator. For example, a tag like 'biz:hr|recruiting' must be quoted.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Filtered tags?' or 'Result?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-22-event-registration-tt

**20/25** · 456915 tokens · 183385ms

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
- ✅ **separates-validation-and-pricing**: Input validation and pricing/discount calculation are in separate @TableTest methods
- ❌ **validation-includes-optional-fields**: Validation @TableTest includes Dietary Requirements and Accessibility Needs columns, with rows showing both null and non-null values are accepted.
  > Validation table does not include Dietary Requirements or Accessibility Needs columns; optional fields are fixed null in method body
- ✅ **descriptive-registration-date**: Registration date uses descriptive values (e.g. 'before cutoff', 'on cutoff', 'after cutoff') with a @TypeConverter method converting to actual dates.
- ✅ **cutoff-date-column-if-literal-dates**: If registration dates are literal, the early-bird cutoff date appears as a separate policy column. Passes automatically if descriptive date values are used.
- ❌ **description-no-irrelevant-information**: If @Description is present, it does not include information already visible in table columns or fixed input values that could be columns.
  > @Description states 'dietaryRequirements and accessibilityNeeds are null in all rows' — already visible from fixed method inputs
- ❌ **discount-column-preferred**: Output column is 'Discount?' rather than 'Price?' — or if 'Price?' is used, both 'Base price' and 'Price?' columns must be present.
  > Pricing table uses 'Price?' column without 'Base price' column; reader cannot independently verify discount calculation
- ❌ **single-assertion-in-method**: Each @TableTest method has a single, uniform assertion pattern applied to all rows — not different assertions per scenario. Validation checks one thing (e.g. error message or acceptance status); pricing checks one thing (e.g. discount or price). No if/switch in assertion logic.
  > Method has 2 assertions; Method has 2 assertions
- ❌ **optional-fields-has-expected-column**: The optional-fields acceptance table includes an expectation/output column (e.g. 'Approved?', 'Result?', 'Status?') asserting acceptance.
  > No optional-fields table present; validation table does not focus on acceptance of optional-field combinations
- ✅ **description-no-redundant-field-values**: Scenario descriptions do not include specific field values already visible in the table or serving as placeholders.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-23-loan-approval-tt

**15/18** · 263802 tokens · 91412ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **threshold-as-column**: Policy thresholds (650/600 credit scores) appear in a dedicated policy column (e.g. 'Credit threshold') separate from the applicant's actual credit score column.
  > The tables show 'Credit Score' as a single column with values (700, 650, 600, etc.), but no separate 'Credit threshold' column. Thresholds are implicit in the test logic, not explicit columns.
- ✅ **concrete-domain-values**: Cell values use concrete domain terms (e.g. '70', '650', 'APPROVED') — not abstract codes like 'CATEGORY_A' or '1'.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Senior at lower threshold', 'Missing income') — not outcomes ('Approved', 'Rejected').
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Age', 'Credit score', 'Stable income', 'Decision?') — not code identifiers like 'customerAge', 'boolean'.
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Decision?' or 'Status?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.
- ❌ **separates-age-credit-income**: Age boundary policy, credit score categorisation, and income status are separated into distinct @TableTest methods, with a method combining these for the expected verdict
  > Age boundary is not separated into its own method. The two methods mix age boundaries with credit score and income. No separate age-boundary method exists.
- ❌ **description-no-redundant-field-values**: Scenario descriptions and @Description text do not include specific field values that are already visible in the table or that serve as 'any valid value' placeholders.
  > @Description states 'The row with age 64 and score 620 confirms the threshold applies only from 65 onwards' — redundantly references values already in the visible table row.
- ✅ **depth-stable-income-effect**: The standardApplicantCreditThreshold table (or equivalent non-senior table) clearly documents the effect of stable income: above-threshold credit score + stable income = APPROVED, and above-threshold credit score + no stable income = REJECTED.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-25-convert-from-spock

**14/19** · 378869 tokens · 198707ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells. Rows with no options use a blank cell or [:].
  > surchargesOnBaseRate table has three separate columns: 'Fragile | Insured Value | Handling' instead of a single map-like column
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
  > Response states 'No custom @TypeConverter needed' and none is present in the code
- ❌ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table — not as three separate length/width/height columns
  > Both tables have three columns: 'Length | Width | Height' instead of a single 'Dimensions' column with [L, W, H] format
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
- ✅ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern — not one monolithic table mixing base rates, surcharges, dimensional weight, and carrier equivalence
- ✅ **numeric-types-correct**: Parameter types correspond to the ShippingCostCalculator.calculateShippingCost interface — weight, cost, and dimension types match the method signature.
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('EU express, light package', 'Oversized with fragile') — not outcomes ('8.00', 'Surcharge applied') or generic labels ('Test 1').
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ❌ **business-language-columns**: Column names use domain language ('Zone', 'Weight (kg)', 'Dimensions', 'Options', 'Cost?') — not code identifiers ('shippingZone', 'weightKg', 'dims')
  > Columns use 'Region', 'Speed', 'Weight', 'Length', 'Width', 'Height', 'Carrier' — mostly business language but 'Region'/'Speed' and three dimension columns deviate from expected consolidated format
- ✅ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **output-is-kotlin**: The generated test file is written in Kotlin (.kt), not Java or Groovy. Uses Kotlin syntax: fun, val/var, Kotlin imports.
- ✅ **no-groovy-syntax**: Output contains no Groovy or Spock syntax: no def, where:, expect:, given:, GString interpolation (#variable), Spock assertions (thrown(), old()), or Groovy list literals ([] as Set).
- ✅ **has-tabletest-dependency**: The build file (build.gradle.kts) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ❌ **tests-pass**: The generated tests pass when executed against ShippingCostCalculator
  > Tests failed: 
33 tests completed, 2 failed

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':test'.
> There were failing tests. See the report at: file:///private/var/folder

### ⚠️ Eval eval-26-convert-from-kotest

**15/19** · 202995 tokens · 125733ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells. Rows with no options use a blank cell or [:].
  > Options are in three separate columns: '| Fragile | Insured Value | Handling |' with mostly-blank cells, not collapsed into a single map column.
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
  > No @TypeConverter for PackageOptions exists. Only 'parseBigDecimal' converter is defined; PackageOptions constructed inline in test method.
- ✅ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table — not as three separate length/width/height columns
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
- ❌ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern — not one monolithic table mixing base rates, surcharges, dimensional weight, and carrier equivalence
  > Only one @TableTest method exists combining base rates, surcharges, dimensional weight, and carrier equivalence in a single table.
- ✅ **numeric-types-correct**: Parameter types correspond to the ShippingCostCalculator.calculateShippingCost interface — weight, cost, and dimension types match the method signature.
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('EU express, light package', 'Oversized with fragile') — not outcomes ('8.00', 'Surcharge applied') or generic labels ('Test 1').
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ✅ **business-language-columns**: Column names use domain language ('Zone', 'Weight (kg)', 'Dimensions', 'Options', 'Cost?') — not code identifiers ('shippingZone', 'weightKg', 'dims')
- ✅ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ❌ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
  > Method name is 'calculateShippingCost' (generic, matches implementation name). No @DisplayName annotation present.
- ✅ **output-is-kotlin**: The generated test file is written in Kotlin (.kt), not Java. Uses Kotlin syntax: fun, val/var, Kotlin imports.
- ✅ **no-kotest-syntax**: Output contains no Kotest syntax: no forAll, row(), withData, shouldBe, context(), FunSpec, or Kotest imports.
- ✅ **has-tabletest-dependency**: The build file (build.gradle.kts) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against ShippingCostCalculator

### ⚠️ Eval eval-27-convert-from-testng

**14/18** · 270028 tokens · 113961ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells. Rows with no options use a blank cell or [:].
  > Options column uses shorthand strings like 'fragile', 'insured:500', 'fragile,insured:200', not map syntax like [fragile: true, insuredValue: 500]
- ✅ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
- ✅ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table — not as three separate length/width/height columns
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
- ❌ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern — not one monolithic table mixing base rates, surcharges, dimensional weight, and carrier equivalence
  > Only one @TableTest method exists combining base rates (EU standard/express), surcharges (fragile, insured, hazmat), dimensional weight, and carrier equivalence in one table
- ✅ **numeric-types-correct**: Parameter types correspond to the ShippingCostCalculator.calculateShippingCost interface — weight, cost, and dimension types match the method signature.
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('EU express, light package', 'Oversized with fragile') — not outcomes ('8.00', 'Surcharge applied') or generic labels ('Test 1').
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ✅ **business-language-columns**: Column names use domain language ('Zone', 'Weight (kg)', 'Dimensions', 'Options', 'Cost?') — not code identifiers ('shippingZone', 'weightKg', 'dims')
- ✅ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ❌ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
  > Method is named 'calculateShippingCost' with no @DisplayName annotation; method name does not read as descriptive when converted
- ✅ **no-testng-artifacts**: Output contains no TestNG syntax: no @DataProvider, @Test(dataProvider=...), Object[][], or TestNG imports (org.testng.*).
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ❌ **tests-pass**: The generated tests pass when executed against ShippingCostCalculator
  > Tests failed: 
FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':test'.
> Test process encountered an unexpected problem.
   > Could not start Gradle Test Executor 33.
      >

### ⚠️ Eval eval-28-convert-from-methodsource

**16/18** · 283536 tokens · 154426ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells. Rows with no options use a blank cell or [:].
  > Options are parsed as a string like 'fragile' or 'insured:500' but not displayed as collapsed map syntax [fragile: true, insuredValue: 500] in the table column itself.
- ✅ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
- ✅ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table — not as three separate length/width/height columns
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
- ✅ **no-methodsource-artifacts**: Output contains no @MethodSource syntax: no @MethodSource, Stream<Arguments>, Arguments.of, or JUnit Jupiter parameterized imports (org.junit.jupiter.params.provider.*).
- ✅ **has-tabletest-dependency**: The build file (pom.xml) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ❌ **tests-pass**: The generated tests pass when executed against ShippingCostCalculator
  > Tests failed: 

### ⚠️ Eval eval-29-shopping-cart-tt

**22/23** · 492674 tokens · 319099ms

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
- ❌ **single-assertion-in-method**: Each @TableTest method has a single, uniform assertion pattern applied to all rows — not different assertions per scenario.
  > Method has 2 assertions; Method has 3 assertions; Method has 3 assertions; Method has 2 assertions
- ✅ **test-data-visible**: Product prices and coupon rules (type, amount, expiry status) are visible in the table via columns, @Description, or explicit setup — not silently hardcoded. The reader can trace how inputs lead to outputs.
- ✅ **type-converters-for-complex-objects**: TypeConverter methods are used to convert map/string table values into domain objects (e.g. Map → Cart, Map → InventoryService, map → Coupon). Test method bodies contain only arrange-act-assert — no object construction from raw table values.
- ✅ **coupon-as-single-column**: Coupon data is expressed in a single column with a @TypeConverter — not spread across separate sparse columns for coupon type, value, and target product. The column format may be map syntax (e.g. [type: PERCENTAGE, value: 10]) or readable shorthand (e.g. '20% cart', '$5 off', '50% off widget').
- ✅ **uses-standard-map-syntax**: Columns representing maps (e.g. cart contents, product catalogue, inventory levels) use standard TableTest map syntax — [k: v, k2: v2] for entries and [:] for empty map — not a custom bracket notation. Custom string-parsing TypeConverters are not needed for data that maps naturally to Map<String, X>.
- ✅ **description-not-redundant-with-scenarios**: @Description annotations do not repeat information that the scenario names already convey. Coupon type behavior (what PERCENTAGE/FIXED/PRODUCT mean) should be expressed through descriptive scenario names, not duplicated in the description.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ✅ Eval eval-30-order-splitting-tt

**20/20** · 678630 tokens · 469276ms

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
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

## Variant vs Official (iterations 34, 33, 32, 31, 30, 29, 28, 27, 26, 25 merged)

### Per-Eval Resource Comparison

| Eval | Pass (off) | Pass (var) | Tokens (off) | Tokens (var) | Tok Δ | Cost (off) | Cost (var) | Cost Δ | Time (off) | Time (var) | Time Δ |
|------|------------|------------|--------------|--------------|-------|------------|------------|--------|------------|------------|--------|
| eval-1-convert-repetitive-tests | 13/13 | 13/13 | 217265 | 203698 | -6% | $0.1486 | $0.1452 | -2% | 31.4s | 25.1s | -20% |
| eval-2-parse-dates | 14/15 | 14/15 | 221643 | 164908 | -26% | $0.2330 | $0.1660 | -29% | 88.5s | 53.0s | -40% |
| eval-3-dependency-setup | 4/4 | 4/4 | 66708 | 44339 | -34% | $0.0748 | $0.0757 | +1% | 11.2s | 10.0s | -11% |
| eval-7-permission-check | 13/13 | 11/13 | 261105 | 186933 | -28% | $0.2391 | $0.1473 | -38% | 60.8s | 34.2s | -44% |
| eval-8-money-parse | 14/15 | 15/15 | 198774 | 232011 | +17% | $0.2061 | $0.2245 | +9% | 76.9s | 90.4s | +18% |
| eval-9-bonus-contractor-structure | 13/13 | 13/13 | 421958 | 213628 | -49% | $0.3219 | $0.1520 | -53% | 94.0s | 29.9s | -68% |
| eval-14-weekly-pay | 15/20 | 15/20 | 308199 | 297900 | -3% | $0.4140 | $0.3601 | -13% | 213.1s | 164.1s | -23% |
| eval-15-reis-discount | 12/20 | 11/20 | 248613 | 244941 | -1% | $0.4402 | $0.2498 | -43% | 276.4s | 108.4s | -61% |
| eval-18-convert-from-code | 15/21 | 11/21 | 199263 | 122236 | -39% | $0.2545 | $0.2817 | +11% | 109.6s | 167.9s | +53% |
| eval-19-convert-from-parameterized | 11/11 | 11/11 | 231473 | 157557 | -32% | $0.1879 | $0.1273 | -32% | 37.4s | 25.6s | -31% |
| eval-20-collections-and-quoting | 14/15 | 14/15 | 662973 | 223364 | -66% | $0.7910 | $0.3196 | -60% | 465.1s | 137.0s | -71% |
| eval-22-event-registration-tt | 19/25 | 20/25 | 244943 | 456915 | +87% | $0.4312 | $0.3964 | -8% | 264.3s | 183.4s | -31% |
| eval-23-loan-approval-tt | 13/18 | 15/18 | 365166 | 263802 | -28% | $0.3736 | $0.2344 | -37% | 159.9s | 91.4s | -43% |
| eval-25-convert-from-spock | 15/19 | 14/19 | 317274 | 378869 | +19% | $0.4024 | $0.4334 | +8% | 189.8s | 198.7s | +5% |
| eval-26-convert-from-kotest | 16/19 | 15/19 | 336452 | 202995 | -40% | $0.4249 | $0.2674 | -37% | 213.9s | 125.7s | -41% |
| eval-27-convert-from-testng | 15/18 | 14/18 | 348223 | 270028 | -22% | $0.3844 | $0.3005 | -22% | 166.6s | 114.0s | -32% |
| eval-28-convert-from-methodsource | 16/18 | 16/18 | 601634 | 283536 | -53% | $0.6091 | $0.3480 | -43% | 293.8s | 154.4s | -47% |
| eval-29-shopping-cart-tt | — | 22/23 | — | 492674 | — | — | $0.6784 | — | — | 319.1s | — |
| eval-30-order-splitting-tt | — | 20/20 | — | 678630 | — | — | $0.9847 | — | — | 469.3s | — |
| **Totals (17 comparable)** | **232/277** | **226/277** | **5251666** | **3947660** | **-25%** | **$5.9366** | **$4.2293** | **-29%** | **2752.5s** | **1713.3s** | **-38%** |

**Comparable summary (17 evals in both):**

| Source | Pass Rate | Tokens | Cost | Time |
|--------|-----------|--------|------|------|
| minimal (iter 7) | 226/277 (81.6%) | 3947660 | $4.2293 | 1713.3s |
| official | 232/277 (83.8%) | 5251666 | $5.9366 | 2752.5s |
| **Δ** | | **-25%** | **-29%** | **-38%** |

### Per-Assertion Comparison

| Eval | Assertion | official | minimal |
|------|-----------|----------|---------|
| eval-2-parse-dates | empty-string-uses-quotes | ✅ | ❌ |
| eval-2-parse-dates | compiles | ❌ | ✅ |
| eval-7-permission-check | no-duplicate-role-output | ✅ | ❌ |
| eval-7-permission-check | has-descriptive-title | ✅ | ❌ |
| eval-8-money-parse | compiles | ❌ | ✅ |
| eval-14-weekly-pay | 1.2-error-has-expected-column | ❌ | ✅ |
| eval-14-weekly-pay | 1.11-format-description | ✅ | ❌ |
| eval-14-weekly-pay | 1.12-format-annotation-order | ✅ | ❌ |
| eval-14-weekly-pay | 1.14-depth-zero-rate | ❌ | ✅ |
| eval-15-reis-discount | 2.9-correctness-value-set-tier-semantics | ✅ | ❌ |
| eval-15-reis-discount | concerns-decomposed | ✅ | ❌ |
| eval-15-reis-discount | compiles | ❌ | ✅ |
| eval-18-convert-from-code | has-tabletest-annotation | ✅ | ❌ |
| eval-18-convert-from-code | scenario-column-present | ✅ | ❌ |
| eval-18-convert-from-code | business-language-columns | ✅ | ❌ |
| eval-18-convert-from-code | has-question-mark-column | ✅ | ❌ |
| eval-18-convert-from-code | no-if-switch-in-method | ✅ | ❌ |
| eval-18-convert-from-code | has-descriptive-title | ✅ | ❌ |
| eval-18-convert-from-code | concerns-decomposed | ❌ | ✅ |
| eval-18-convert-from-code | minimal-rows-per-concern | ❌ | ✅ |
| eval-18-convert-from-code | separates-decision-and-premium | ❌ | ✅ |
| eval-18-convert-from-code | no-reimplemented-internals | ✅ | ❌ |
| eval-18-convert-from-code | depth-premium-boundaries | ❌ | ✅ |
| eval-18-convert-from-code | has-tabletest-dependency | ✅ | ❌ |
| eval-20-collections-and-quoting | special-chars-quoted | ❌ | ✅ |
| eval-20-collections-and-quoting | newline-in-cell | ✅ | ❌ |
| eval-22-event-registration-tt | validation-includes-optional-fields | ✅ | ❌ |
| eval-22-event-registration-tt | descriptive-registration-date | ❌ | ✅ |
| eval-22-event-registration-tt | optional-fields-has-expected-column | ✅ | ❌ |
| eval-22-event-registration-tt | description-no-redundant-field-values | ❌ | ✅ |
| eval-22-event-registration-tt | compiles | ❌ | ✅ |
| eval-23-loan-approval-tt | concerns-decomposed | ❌ | ✅ |
| eval-23-loan-approval-tt | minimal-rows-per-concern | ❌ | ✅ |
| eval-25-convert-from-spock | dimensions-as-list | ✅ | ❌ |
| eval-25-convert-from-spock | concerns-decomposed | ❌ | ✅ |
| eval-25-convert-from-spock | tests-pass | ✅ | ❌ |
| eval-26-convert-from-kotest | concerns-decomposed | ✅ | ❌ |
| eval-26-convert-from-kotest | business-language-columns | ❌ | ✅ |
| eval-26-convert-from-kotest | has-descriptive-title | ✅ | ❌ |
| eval-27-convert-from-testng | options-as-map | ✅ | ❌ |
| eval-28-convert-from-methodsource | options-type-converter | ❌ | ✅ |
| eval-28-convert-from-methodsource | tests-pass | ✅ | ❌ |

### Load-Bearing Assertions

Assertions that pass with official skill but fail with minimal variant:

- eval-2-parse-dates: `empty-string-uses-quotes`
- eval-7-permission-check: `no-duplicate-role-output`
- eval-7-permission-check: `has-descriptive-title`
- eval-14-weekly-pay: `1.11-format-description`
- eval-14-weekly-pay: `1.12-format-annotation-order`
- eval-15-reis-discount: `2.9-correctness-value-set-tier-semantics`
- eval-15-reis-discount: `concerns-decomposed`
- eval-18-convert-from-code: `has-tabletest-annotation`
- eval-18-convert-from-code: `scenario-column-present`
- eval-18-convert-from-code: `business-language-columns`
- eval-18-convert-from-code: `has-question-mark-column`
- eval-18-convert-from-code: `no-if-switch-in-method`
- eval-18-convert-from-code: `has-descriptive-title`
- eval-18-convert-from-code: `no-reimplemented-internals`
- eval-18-convert-from-code: `has-tabletest-dependency`
- eval-20-collections-and-quoting: `newline-in-cell`
- eval-22-event-registration-tt: `validation-includes-optional-fields`
- eval-22-event-registration-tt: `optional-fields-has-expected-column`
- eval-25-convert-from-spock: `dimensions-as-list`
- eval-25-convert-from-spock: `tests-pass`
- eval-26-convert-from-kotest: `concerns-decomposed`
- eval-26-convert-from-kotest: `has-descriptive-title`
- eval-27-convert-from-testng: `options-as-map`
- eval-28-convert-from-methodsource: `tests-pass`

### Attention Needed

Evals with failed assertions or disproportionate resource usage (>2x official):

| Eval | Issue | Details |
|------|-------|---------|
| eval-2-parse-dates | Failed assertions | `empty-string-uses-quotes` |
| eval-7-permission-check | Failed assertions | `no-duplicate-role-output`, `has-descriptive-title` |
| eval-14-weekly-pay | Failed assertions | `1.11-format-description`, `1.12-format-annotation-order` |
| eval-15-reis-discount | Failed assertions | `2.9-correctness-value-set-tier-semantics`, `concerns-decomposed` |
| eval-18-convert-from-code | Failed assertions | `has-tabletest-annotation`, `scenario-column-present`, `business-language-columns`, `has-question-mark-column`, `no-if-switch-in-method`, `has-descriptive-title`, `no-reimplemented-internals`, `has-tabletest-dependency` |
| eval-20-collections-and-quoting | Failed assertions | `newline-in-cell` |
| eval-22-event-registration-tt | Failed assertions | `validation-includes-optional-fields`, `optional-fields-has-expected-column` |
| eval-25-convert-from-spock | Failed assertions | `dimensions-as-list`, `tests-pass` |
| eval-26-convert-from-kotest | Failed assertions | `concerns-decomposed`, `has-descriptive-title` |
| eval-27-convert-from-testng | Failed assertions | `options-as-map` |
| eval-28-convert-from-methodsource | Failed assertions | `tests-pass` |

