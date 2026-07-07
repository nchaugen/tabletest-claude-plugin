# Eval Review — tabletest, Iteration 37

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-haiku-4-5 · **Date:** 2026-07-07 · **Evals:** 19

## Summary

268/323 (83.0%) · 15915084 tokens · 3291.0s · $12.0587

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

## Delta vs Iteration 36

⚠️ **Model changed since iteration 36:** claude-sonnet-4-6 → claude-sonnet-5 — deltas reflect model and skill changes combined.

**Regressions (28):**
- ❌ eval-2-parse-dates: `type-conversion-addressed`
- ❌ eval-2-parse-dates: `empty-string-uses-quotes`
- ❌ eval-14-weekly-pay: `1.15-format-clean-method`
- ❌ eval-14-weekly-pay: `separates-classification-and-calculation`
- ❌ eval-15-reis-discount: `2.1-decomposition-concern-separation`
- ❌ eval-15-reis-discount: `2.2-children-flat-discount`
- ❌ eval-15-reis-discount: `2.3-depth-tier-boundaries`
- ❌ eval-15-reis-discount: `2.16-no-duplicate-tier-mapping`
- ❌ eval-15-reis-discount: `2.10-format-displayname`
- ❌ eval-15-reis-discount: `2.11-format-description`
- ❌ eval-15-reis-discount: `2.13-format-annotation-order`
- ❌ eval-15-reis-discount: `2.14-format-description-textblock`
- ❌ eval-15-reis-discount: `2.18-adult-senior-value-set`
- ❌ eval-15-reis-discount: `2.19-depth-all-tiers`
- ❌ eval-15-reis-discount: `2.21-readability-relative-time`
- ❌ eval-15-reis-discount: `concerns-decomposed`
- ❌ eval-15-reis-discount: `minimal-rows-per-concern`
- ❌ eval-15-reis-discount: `has-tabletest-dependency`
- ❌ eval-15-reis-discount: `compiles`
- ❌ eval-18-convert-from-code: `scenario-names-describe-conditions`
- ❌ eval-23-loan-approval-tt: `concerns-decomposed`
- ❌ eval-23-loan-approval-tt: `minimal-rows-per-concern`
- ❌ eval-25-convert-from-spock: `dimensions-as-list`
- ❌ eval-26-convert-from-kotest: `no-kotest-syntax`
- ❌ eval-27-convert-from-testng: `options-as-map`
- ❌ eval-27-convert-from-testng: `options-type-converter`
- ❌ eval-27-convert-from-testng: `business-language-columns`
- ❌ eval-28-convert-from-methodsource: `no-if-switch-in-method`

**Improvements (31):**
- ✅ eval-14-weekly-pay: `1.5-depth-error-edge-cases`
- ✅ eval-14-weekly-pay: `1.14-depth-zero-rate`
- ✅ eval-18-convert-from-code: `observable-io-only`
- ✅ eval-18-convert-from-code: `separates-decision-and-premium`
- ✅ eval-19-convert-from-parameterized: `compiles`
- ✅ eval-22-event-registration-tt: `validation-includes-optional-fields`
- ✅ eval-22-event-registration-tt: `descriptive-registration-date`
- ✅ eval-22-event-registration-tt: `cutoff-date-column-if-literal-dates`
- ✅ eval-22-event-registration-tt: `optional-fields-has-expected-column`
- ✅ eval-25-convert-from-spock: `tests-pass`
- ✅ eval-27-convert-from-testng: `tests-pass`
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
- ✅ eval-29-shopping-cart-tt: `annotation-order`
- ✅ eval-29-shopping-cart-tt: `has-descriptive-title`
- ✅ eval-29-shopping-cart-tt: `description-uses-textblock`
- ✅ eval-29-shopping-cart-tt: `test-data-visible`
- ✅ eval-29-shopping-cart-tt: `type-converters-for-complex-objects`
- ✅ eval-29-shopping-cart-tt: `coupon-as-single-column`
- ✅ eval-29-shopping-cart-tt: `uses-standard-map-syntax`
- ✅ eval-29-shopping-cart-tt: `description-not-redundant-with-scenarios`
- ✅ eval-29-shopping-cart-tt: `has-tabletest-dependency`
- ✅ eval-29-shopping-cart-tt: `compiles`

## Resource Comparison vs Iteration 36

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-1-convert-repetitive-tests | 13/13 | 13/13 | 523165 | 248046 | 45.8 | 62.7 |
| eval-2-parse-dates | 13/15 | 15/15 | 609045 | 216304 | 64.1 | 65.0 |
| eval-3-dependency-setup | 4/4 | 4/4 | 183155 | 60481 | 35.0 | 15.9 |
| eval-7-permission-check | 13/13 | 13/13 | 689001 | 255052 | 53.1 | 52.0 |
| eval-8-money-parse | 15/15 | 15/15 | 746266 | 221126 | 76.7 | 87.1 |
| eval-9-bonus-contractor-structure | 13/13 | 13/13 | 557278 | 216789 | 48.5 | 52.9 |
| eval-14-weekly-pay | 16/20 | 16/20 | 700566 | 370658 | 171.1 | 273.5 |
| eval-15-reis-discount | 0/20 | 15/20 | 555712 | 511140 | 139.3 | 367.5 |
| eval-18-convert-from-code | 18/21 | 17/21 | 892642 | 375380 | 181.6 | 288.3 |
| eval-19-convert-from-parameterized | 11/11 | 10/11 | 532336 | 320012 | 182.3 | 49.5 |
| eval-20-collections-and-quoting | 15/15 | 15/15 | 1359246 | 430826 | 208.0 | 520.6 |
| eval-22-event-registration-tt | 22/25 | 18/25 | 490058 | 442240 | 141.8 | 212.0 |
| eval-23-loan-approval-tt | 13/18 | 15/18 | 743981 | 243006 | 171.0 | 198.5 |
| eval-25-convert-from-spock | 16/20 | 16/20 | 1297643 | 399820 | 273.2 | 275.6 |
| eval-26-convert-from-kotest | 16/20 | 17/20 | 1483578 | 349007 | 363.3 | 181.8 |
| eval-27-convert-from-testng | 16/19 | 18/19 | 1443603 | 300482 | 214.7 | 328.0 |
| eval-28-convert-from-methodsource | 15/18 | 16/18 | 1192800 | 257726 | 320.5 | 277.3 |
| eval-29-shopping-cart-tt | 20/23 | 0/23 | 947195 | 0 | 234.5 | 0.0 |
| eval-30-order-splitting-tt | 19/20 | 19/20 | 967814 | 330517 | 366.7 | 551.5 |

## Per-Eval Results

### ✅ Eval eval-1-convert-repetitive-tests

**13/13** · 523165 tokens · 45796ms

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

**13/15** · 609045 tokens · 64116ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell (not the string 'null') in the table
- ✅ **exception-handled-cleanly**: The empty string / exception case is handled — either via a Throws? column, assertThrows inside the method, or a separate @TableTest — rather than being silently omitted
- ✅ **localdate-result-column**: There is a result column typed as LocalDate (or using a string representation that maps to LocalDate)
- ❌ **type-conversion-addressed**: The response addresses type conversion for non-trivial column types — either by providing a @TypeConverter/converter method, or by using a representation that leverages built-in conversion
  > No @TypeConverter method provided; response relies on implicit string-to-LocalDate conversion without explicit handling mechanism
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **empty-string-uses-quotes**: Empty string input uses quoted syntax (e.g. '' or "") in the table — not a blank cell, which represents null. The distinction between empty string and null is preserved.
  > No quoted empty string ('""' or "''") found in table cells
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns
- ✅ **separates-valid-and-invalid**: Valid parsing cases and error/exception cases are in separate @TableTest methods
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ✅ Eval eval-3-dependency-setup

**4/4** · 183155 tokens · 34950ms

- ✅ **flags-junit-version**: Response explicitly flags that JUnit 5.10.0 is below the required minimum (5.11) and recommends upgrading
- ✅ **provides-correct-groupid**: Response provides the tabletest dependency with groupId 'org.tabletest' (not guessed)
- ✅ **provides-artifactid**: Response includes 'tabletest-junit' as the artifactId
- ✅ **includes-test-scope**: The suggested dependency includes <scope>test</scope>

### ✅ Eval eval-7-permission-check

**13/13** · 689001 tokens · 53113ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **uses-value-sets**: Table uses value-set syntax (curly braces like {READ, WRITE} or {READ, WRITE, DELETE}) to express rules that hold across multiple actions — rather than one row per role/action combination
- ✅ **fewer-than-nine-rows**: Table has fewer than 9 data rows (i.e. does not enumerate every role/action combination individually)
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **no-duplicate-role-output**: No two rows share both the same Role value and the same boolean output — forces value set consolidation
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Admin performs any action', 'Guest reads') — not outcomes ('Allowed', 'Denied')
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ✅ Eval eval-8-money-parse

**15/15** · 746266 tokens · 76654ms

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

**13/13** · 557278 tokens · 48480ms

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

**16/20** · 700566 tokens · 171092ms

- ✅ **1.2-error-has-expected-column**: The error/rejection table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
- ✅ **1.3-depth-overtime-boundary**: Overtime threshold boundary is covered: at least a value at the threshold (40) and just above (41 or similar). Not just values well below and well above.
- ✅ **1.4-depth-combined-scenario**: A combined scenario is present: a row with weekday + Sunday + holiday hours, testing the composition of all hour types.
- ✅ **1.5-depth-error-edge-cases**: Error/edge cases covered: at least negative hours (floored at zero) and negative or missing rate (rejected).
- ❌ **1.6-readability-empty-cells**: Sunday and Holiday hour columns use empty cells (not 0) when those inputs are not relevant to a scenario — making it easier to spot which rows involve weekend/holiday hours. This is a readability preference; parameter types should be Integer (not int) to support null from blank cells.
  > Table uses '0' for unused hour types (e.g. 'Under overtime threshold | 30 | 0 | 0') instead of empty cells; parameters are 'double' not 'Integer'
- ✅ **1.7-readability-scenario-names**: Scenario names describe work patterns (e.g. 'Part-time', 'Five hours overtime', 'Full Sunday shift') — not 'Test case 1' or expected outcomes.
- ✅ **1.8-correctness-expected-values**: Expected values in intermediate and total columns are arithmetically correct for every row, consistent with the formulas in @Description.
- ✅ **1.9-correctness-value-set-semantics**: Value sets are only used where all values in the set produce the same expected result (e.g. any rate × 0 = 0). Not used where results would differ.
- ✅ **1.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ✅ **1.11-format-description**: @Description is present on at least the pay calculation method and provides context beyond what the table rows already express — such as the overtime threshold (40 hours), premium multipliers (Sunday 2×, Holiday 2.5×), or other fixed assumptions not visible in the table. Does NOT merely restate the column names or summarise what the rows show.
- ✅ **1.12-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **1.13-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **1.14-depth-zero-rate**: A row where the hourly rate is zero, showing that pay is zero regardless of hours worked. This is a distinct edge case from negative rate (which is rejected).
- ❌ **1.15-format-clean-method**: The test method body contains no if/ternary null-handling or parsing logic. Null-to-default conversion (e.g. blank Sunday hours → 0) is handled via a @TypeConverter method or a private helper, keeping the test method limited to arrange/act/assert.
  > shouldRejectNegativeRate method body has 'if (throws_ == null) { assertDoesNotThrow(...) } else { assertThrows(...) }' conditional logic for handling null
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Weekday hours', 'Sunday hours', 'Hourly rate', 'Weekly pay?') — not implementation terms like 'weekdayHrs', 'int', 'param1'.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ❌ **separates-classification-and-calculation**: Payable hours categorisation (1x regular, 1.5x overtime, 2x Sunday/holiday) and pay calculation (hours × rate) are in separate @TableTest methods
  > Both classification and calculation are combined in single shouldCalculateWeeklyPay table; no separate table isolates hour categorization logic
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-15-reis-discount

**0/20** · 555712 tokens · 139265ms

- ✅ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
- ❌ **2.2-children-flat-discount**: Children's flat 20% discount is represented — either as a row in the eligibility table or a separate note. It does not follow the ladder.
  > Response mentions 'a small final-price arithmetic table' and 'child flat 20% discount' but provides no actual table content showing how children's 20% is represented.
- ✅ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers derived from the 'every fifth trip' rule, with boundary values or value sets showing which ticket counts produce which discount. The maximum 40% discount is represented.
- ✅ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
- ❌ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
  > Mockup shows one row per boundary (4, 5, 9, 10, 40, 45) rather than value sets like {5,6,7,8,9}. No actual value sets are present in the mockup.
- ❌ **2.15-ticket-count-uses-value-sets**: The discount ladder table uses value sets for the ticket count column to group counts within the same tier (e.g. {5, 6, 7, 8, 9} or {5, 9} for min/max → 5%) — not one row per boundary value. The 'every fifth trip' rule creates tiers; the table should express tiers, not enumerate boundaries.
  > Mockup shows 'Tickets in last 30 days' column with single values (4, 5, 9, 10, 40, 45), not value sets like {5,6,7,8,9}. Response says 'say so and I'll go ahead and write the tables'—tables not yet written.
- ❌ **2.16-no-duplicate-tier-mapping**: The tier-to-discount mapping is expressed once — not repeated across multiple tables. A price application table (if present) should test the arithmetic (base price × discount → final price) without re-enumerating which ticket counts map to which tier.
  > Response mentions 'final-price arithmetic table' but provides no content. Cannot verify that tier mapping is not duplicated without seeing actual tables.
- ❌ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
  > No test methods shown. Response only provides mockup table structure, not actual @TableTest method code with @DisplayName annotations.
- ❌ **2.11-format-description**: @Description is present on at least the class or the tier method and provides context beyond what the table rows already express — such as the discount ladder structure, eligibility rules, or application context. Does NOT merely restate the column names or summarise what the rows show.
  > No @Description annotations shown. Response states 'Before I write the full tables' and asks questions; actual test class code not provided.
- ✅ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **2.14-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **2.17-zone-irrelevance-visible**: The eligibility/passenger-type table includes a Zone column (or equivalent) with a value set to show that the discount applies regardless of zone — rather than silently omitting zone. The requirement explicitly says 'regardless of the zones you travel in'.
  > Mockup shows no Zone column. Response does not mention zone-independence in table structure.
- ✅ **2.18-adult-senior-value-set**: Adult and senior are expressed as a value set {ADULT, SENIOR} (or equivalent) in at least one row of the eligibility table, since they follow identical discount rules. Not enumerated as separate rows with the same outcome.
- ❌ **2.19-depth-all-tiers**: All 9 discount tiers are represented in the ladder table: 0% (tickets 1-4), 5% (5-9), 10% (10-14), 15% (15-19), 20% (20-24), 25% (25-29), 30% (30-34), 35% (35-39), and 40% (40+). Not just a subset of tiers.
  > Mockup shows only 6 scenario rows covering tiers 0%, 5%, 10%, and 40%, missing tiers for 15%, 20%, 25%, 30%, 35%.
- ❌ **2.20-readability-one-row-per-tier**: Each tier in the discount ladder is expressed as a single row with a value set for ticket counts — not split across multiple rows (e.g. separate rows for 'tier kicks in' and 'tier holds'). One tier = one row.
  > Mockup shows 'Before first threshold' and 'First discount applies' and 'Between tiers' as separate rows; tier 5% is split across at least 2 rows (5 and 9).
- ✅ **2.21-readability-relative-time**: The rolling window table expresses time relatively (e.g. '14 days ago', '30 days ago', or an integer 'Days ago' column) rather than using absolute dates, making the table readable without needing to know the reference date.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
  > Mockup 'Discount tier by trailing ticket count' has 6 rows for only 4 discount tiers (0%, 5%, 10%, 40%); rows like 'Between tiers' with value 9 are redundant given 'First discount applies' with value 5.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-18-convert-from-code

**18/21** · 892642 tokens · 181640ms

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
  > No @Description annotations present in either @TableTest method. Scenario names provide some context but no formal @Description block exists.
- ❌ **depth-decision-boundaries**: Approval decision table covers the rejection boundary thoroughly: 5 claims triggers rejection regardless of realistic age (risk score crosses 75), 4 claims results in approval. Rows at 4 and 5 claims with varying ages demonstrate this cliff. Senior vs non-senior distinction (64 vs 65) also visible in the decision table.
  > Decision table shows 'Risk score at the rejection threshold is approved | NEW | 0 | 5 | APPROVED' and 'Risk score just above threshold is rejected | NEW | 10 | 5 | REJECTED', but no 4-claim row present to show the cliff; no age 64/65 boundary in decision table.
- ✅ **depth-premium-boundaries**: Approved premium table covers boundary effects: the premium jump from 0 to 1 claim (impact of +15 risk score on premium), and the premium difference at age 64 vs 65 (standard vs senior formula). These boundary conditions document how premium changes at each threshold.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against InsuranceEvaluator

### ✅ Eval eval-19-convert-from-parameterized

**11/11** · 532336 tokens · 182339ms

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

**15/15** · 1359246 tokens · 207968ms

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

**22/25** · 490058 tokens · 141846ms

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
  > @Description states 'Early-bird applies to registrations made strictly before the 2025-03-01 cutoff' and 'Group discount applies to groups of 5 or more' — these rules are already demonstrated by the table rows and cutoff date is implicit in 'before/on/after cutoff'
- ❌ **discount-column-preferred**: Output column is 'Discount?' (percentage or amount off) rather than 'Price?' — making verification simpler (discount is the rule's direct output; price requires knowing the base price). If 'Price?' is used instead, both 'Base price' and 'Price?' columns must be present so the reader can trace the calculation.
  > Table has both 'Discount?' and 'Price?' columns but no 'Base price' column to allow readers to verify the calculation (Price = 100 - Discount)
- ❌ **single-assertion-in-method**: Each @TableTest method has a single, uniform assertion pattern applied to all rows — not different assertions per scenario. Validation checks one thing (e.g. error message or acceptance status); pricing checks one thing (e.g. discount or price). No if/switch in assertion logic.
  > Method has 2 assertions; Method has 2 assertions
- ✅ **optional-fields-has-expected-column**: The optional-fields acceptance table includes an expectation/output column (e.g. 'Approved?', 'Result?', or 'Status?') asserting that each combination is accepted — not just varying inputs without any expected column.
- ✅ **description-no-redundant-field-values**: Scenario descriptions do not include specific field values that are already visible in the table or that serve as 'any valid value' placeholders. Stating 'name: John, email: john@example.com, registration date: 2025-04-01' in a description adds maintenance burden without value — it suffices to say 'valid values supplied' or describe the scenario condition. Applies to all tables.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-23-loan-approval-tt

**13/18** · 743981 tokens · 170966ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **threshold-as-column**: Policy thresholds (650/600 credit scores) appear in a dedicated policy column (e.g. 'Credit threshold') separate from the applicant's actual credit score column. The reader sees both the threshold and the score, making the comparison explicit. Not just threshold values used as data in the score column.
  > Table has 'Credit Score' column but no dedicated 'Credit threshold' column. Thresholds (650/600) only appear in scenario names and @Description text, not as explicit table columns.
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
  > Single @TableTest method 'evaluatesLoanApplications' with one table mixing age thresholds, credit score rules, stable income effects, and missing income handling — multiple distinct concerns in one table.
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
  > Single table contains 13 rows covering age boundaries, credit thresholds, income effects, and missing data — concerns not separated means rows cannot be minimal per individual concern.
- ❌ **separates-age-credit-income**: Age boundary policy, credit score categorisation, and income status are separated into distinct @TableTest methods, with a method combining these for the expected verdict
  > Only one @TableTest method 'evaluatesLoanApplications' exists. Age, credit score, stable income, and verdict are all tested together in a single method, not separated.
- ✅ **description-no-redundant-field-values**: Scenario descriptions and @Description text do not include specific field values that are already visible in the table or that serve as 'any valid value' placeholders. Stating 'Customer age is 35 for all rows' in @Description when age is fixed in the method body, or including fixed values in scenario names that are already columns, adds maintenance burden without value. Describe the scenario condition or policy being tested instead.
- ✅ **depth-stable-income-effect**: The standardApplicantCreditThreshold table (or equivalent non-senior table) clearly documents the effect of stable income: rows show that above-threshold credit score + stable income = APPROVED, and above-threshold credit score + no stable income = REJECTED. The stable income parameter's effect on the outcome must be visible from the table rows, not just implied.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-25-convert-from-spock

**16/20** · 1297643 tokens · 273153ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells. Rows with no options use a blank cell or [:].
  > Options are passed as separate parameters (fragile: Boolean, insuredValue: BigDecimal?, handling: String?) in the table, not collapsed into a map column
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
  > No @TypeConverter method is present in the generated test file. Options are constructed as PackageOptions(...) in test code, not via a converter
- ❌ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table — not as three separate length/width/height columns
  > Table contains separate columns 'Length | Width | Height' in shouldUseDimensionalWeightWhenItExceedsActualWeight and shouldAddOversizeSurchargeWhenAnyDimensionExceedsThreshold
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
- ✅ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern — not one monolithic table mixing base rates, surcharges, dimensional weight, and carrier equivalence
- ✅ **numeric-types-correct**: Parameter types correspond to the ShippingCostCalculator.calculateShippingCost interface — weight, cost, and dimension types match the method signature.
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('EU express, light package', 'Oversized with fragile') — not outcomes ('8.00', 'Surcharge applied') or generic labels ('Test 1').
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ✅ **business-language-columns**: Column names use domain language ('Zone', 'Weight (kg)', 'Dimensions', 'Options', 'Cost?') — not code identifiers ('shippingZone', 'weightKg', 'dims')
- ✅ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ❌ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
  > @Description annotations are present but @DisplayName is not used. Method names use camelCase but lack @DisplayName for JUnit report clarity
- ✅ **output-is-kotlin**: The generated test file is written in Kotlin (.kt), not Java or Groovy. Uses Kotlin syntax: fun, val/var, Kotlin imports.
- ✅ **no-groovy-syntax**: Output contains no Groovy or Spock syntax: no def, where:, expect:, given:, GString interpolation (#variable), Spock assertions (thrown(), old()), or Groovy list literals ([] as Set).
- ✅ **has-tabletest-dependency**: The build file (build.gradle.kts) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against ShippingCostCalculator

### ⚠️ Eval eval-26-convert-from-kotest

**16/20** · 1483578 tokens · 363305ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells. Rows with no options use a blank cell or [:].
  > PackageOptions is passed as an object constructor (e.g., `PackageOptions(isFragile = fragile)`) not as a map column in tables. Individual boolean/nullable columns used instead.
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
  > No @TypeConverter method present in the test file. PackageOptions constructed manually in test method bodies.
- ✅ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table — not as three separate length/width/height columns
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
- ✅ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern — not one monolithic table mixing base rates, surcharges, dimensional weight, and carrier equivalence
- ✅ **numeric-types-correct**: Parameter types correspond to the ShippingCostCalculator.calculateShippingCost interface — weight, cost, and dimension types match the method signature.
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('EU express, light package', 'Oversized with fragile') — not outcomes ('8.00', 'Surcharge applied') or generic labels ('Test 1').
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ✅ **business-language-columns**: Column names use domain language ('Zone', 'Weight (kg)', 'Dimensions', 'Options', 'Cost?') — not code identifiers ('shippingZone', 'weightKg', 'dims')
- ❌ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
  > No @TableTest method bodies found
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **output-is-kotlin**: The generated test file is written in Kotlin (.kt), not Java. Uses Kotlin syntax: fun, val/var, Kotlin imports.
- ❌ **no-kotest-syntax**: Output contains no Kotest syntax: no forAll, row(), withData, shouldBe, context(), FunSpec, or Kotest imports.
  > Found: io.kotest import
- ✅ **has-tabletest-dependency**: The build file (build.gradle.kts) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against ShippingCostCalculator

### ⚠️ Eval eval-27-convert-from-testng

**16/19** · 1443603 tokens · 214676ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells. Rows with no options use a blank cell or [:].
  > Options appear as three separate columns: 'Fragile | Insured Value | Handling' with individual cells like 'true', '500', 'hazmat'
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
  > No @TypeConverter method present. PackageOptions constructed directly: 'options.setFragile(fragile); options.setInsuredValue(insuredValue);'
- ✅ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table — not as three separate length/width/height columns
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
- ✅ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern — not one monolithic table mixing base rates, surcharges, dimensional weight, and carrier equivalence
- ✅ **numeric-types-correct**: Parameter types correspond to the ShippingCostCalculator.calculateShippingCost interface — weight, cost, and dimension types match the method signature.
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('EU express, light package', 'Oversized with fragile') — not outcomes ('8.00', 'Surcharge applied') or generic labels ('Test 1').
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ❌ **business-language-columns**: Column names use domain language ('Zone', 'Weight (kg)', 'Dimensions', 'Options', 'Cost?') — not code identifiers ('shippingZone', 'weightKg', 'dims')
  > Columns use: 'Region | Speed | Weight | Dimensions | Fragile | Insured Value | Handling' - code-like 'Insured Value' instead of business format
- ✅ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **no-testng-artifacts**: Output contains no TestNG syntax: no @DataProvider, @Test(dataProvider=...), Object[][], or TestNG imports (org.testng.*).
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against ShippingCostCalculator

### ⚠️ Eval eval-28-convert-from-methodsource

**15/18** · 1192800 tokens · 320469ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells. Rows with no options use a blank cell or [:].
  > Options are passed as PackageOptions objects constructed in code (e.g., 'opts.setHandling(handling)'), not as map columns in the table. Table columns show individual fields like 'Handling' and 'Fragile?' separately.
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
  > No @TypeConverter annotation or method is present in the test file. PackageOptions are manually constructed in test method bodies.
- ✅ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table — not as three separate length/width/height columns
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
- ✅ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern — not one monolithic table mixing base rates, surcharges, dimensional weight, and carrier equivalence
- ✅ **numeric-types-correct**: Parameter types correspond to the ShippingCostCalculator.calculateShippingCost interface — weight, cost, and dimension types match the method signature.
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('EU express, light package', 'Oversized with fragile') — not outcomes ('8.00', 'Surcharge applied') or generic labels ('Test 1').
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ✅ **business-language-columns**: Column names use domain language ('Zone', 'Weight (kg)', 'Dimensions', 'Options', 'Cost?') — not code identifiers ('shippingZone', 'weightKg', 'dims')
- ❌ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
  > if statement found in method body; if statement found in method body
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **no-methodsource-artifacts**: Output contains no @MethodSource syntax: no @MethodSource, Stream<Arguments>, Arguments.of, or JUnit Jupiter parameterized imports (org.junit.jupiter.params.provider.*).
- ✅ **has-tabletest-dependency**: The build file (pom.xml) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against ShippingCostCalculator

### ⚠️ Eval eval-29-shopping-cart-tt

**20/23** · 947195 tokens · 234452ms

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
  > Column 'Product Id' (code identifier); 'Items Before' and 'Items After?' use 'Items' not 'Cart contents'; otherwise uses 'Quantity', 'Message?', 'Total?', 'Success?'
- ❌ **no-if-switch-in-method**: Test method body contains no if or switch statements
  > ternary operator found in method body
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **single-assertion-in-method**: Each @TableTest method has a single, uniform assertion pattern applied to all rows — not different assertions per scenario.
  > Method has 2 assertions; Method has 2 assertions; Method has 2 assertions; Method has 2 assertions
- ✅ **test-data-visible**: Product prices and coupon rules (type, amount, expiry status) are visible in the table via columns, @Description, or explicit setup — not silently hardcoded. The reader can trace how inputs lead to outputs.
- ✅ **type-converters-for-complex-objects**: TypeConverter methods are used to convert map/string table values into domain objects (e.g. Map → Cart, Map → InventoryService, map → Coupon). Test method bodies contain only arrange-act-assert — no object construction from raw table values.
- ✅ **coupon-as-single-column**: Coupon data is expressed in a single column with a @TypeConverter — not spread across separate sparse columns for coupon type, value, and target product. The column format may be map syntax (e.g. [type: PERCENTAGE, value: 10]) or readable shorthand (e.g. '20% cart', '$5 off', '50% off widget').
- ✅ **uses-standard-map-syntax**: Columns representing maps (e.g. cart contents, product catalogue, inventory levels) use standard TableTest map syntax — [k: v, k2: v2] for entries and [:] for empty map — not a custom bracket notation. Custom string-parsing TypeConverters are not needed for data that maps naturally to Map<String, X>.
- ✅ **description-not-redundant-with-scenarios**: @Description annotations do not repeat information that the scenario names already convey. Coupon type behavior (what PERCENTAGE/FIXED/PRODUCT mean) should be expressed through descriptive scenario names, not duplicated in the description.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-30-order-splitting-tt

**19/20** · 967814 tokens · 366685ms

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
- ❌ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
  > Methods use camelCase names and have @Description annotations, but no @DisplayName annotations. Method names are descriptive but assertion requires @DisplayName OR descriptive name; only camelCase names provided without @DisplayName
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

