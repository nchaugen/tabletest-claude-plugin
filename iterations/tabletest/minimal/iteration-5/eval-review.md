# Eval Review — tabletest variant=minimal, Iteration 4

**Model:** sonnet · **Date:** 2026-04-12 · **Evals:** 19

## Summary

138/170 (81.2%) · 1531314 tokens · 2972.5s · $3.5277

## Delta vs Iteration 3

**Regressions (3):**
- ❌ eval-25-convert-from-spock: `scenario-names-describe-conditions`
- ❌ eval-27-convert-from-testng: `uses-value-sets`
- ❌ eval-27-convert-from-testng: `no-if-switch-in-method`

**Improvements (4):**
- ✅ eval-25-convert-from-spock: `concerns-decomposed`
- ✅ eval-25-convert-from-spock: `business-language-columns`
- ✅ eval-25-convert-from-spock: `has-descriptive-title`
- ✅ eval-27-convert-from-testng: `concerns-decomposed`

## Resource Comparison vs Iteration 3

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-1-convert-repetitive-tests | 10/10 | 10/10 | 44345 | 44278 | 14.7 | 13.2 |
| eval-2-parse-dates | 13/13 | 13/13 | 73253 | 46136 | 34.2 | 47.1 |
| eval-3-dependency-setup | 4/4 | 4/4 | 44075 | 43867 | 9.0 | 8.3 |
| eval-7-permission-check | 6/11 | 6/11 | 70959 | 70919 | 13.8 | 16.6 |
| eval-8-money-parse | 13/13 | 13/13 | 103102 | 74739 | 48.3 | 48.1 |
| eval-9-bonus-contractor-structure | 10/10 | 11/11 | 98298 | 44635 | 18.5 | 21.6 |
| eval-14-weekly-pay | T/O | — | — | — | T/O | — |
| eval-15-reis-discount | 4/18 | — | 45131 | — | 78.5 | — |
| eval-18-convert-from-code | — | 15/18 | — | 54127 | — | 131.9 |
| eval-19-convert-from-parameterized | 8/8 | 8/8 | 71233 | 44737 | 11.7 | 18.7 |
| eval-20-collections-and-quoting | — | 13/13 | — | 127350 | — | 184.0 |
| eval-22-event-registration-tt | — | 20/23 | — | 94849 | — | 220.3 |
| eval-23-loan-approval-tt | T/O | 14/16 | — | 103192 | T/O | 83.0 |
| eval-25-convert-from-spock | 9/15 | 7/15 | 118507 | 50752 | 828.8 | 79.3 |
| eval-26-convert-from-kotest | T/O | 11/15 | — | 56106 | T/O | 136.4 |
| eval-27-convert-from-testng | 11/15 | 12/15 | 171044 | 52219 | 625.2 | 87.4 |
| eval-28-convert-from-methodsource | 12/15 | 12/15 | 84912 | 93296 | 82.8 | 134.4 |
| eval-29-shopping-cart-tt | 21/21 | 21/21 | 313859 | 218477 | 621.7 | 249.2 |
| eval-30-order-splitting-tt | 17/17 | 18/18 | 292596 | 309990 | 585.4 | 689.0 |

## Per-Eval Results

### ✅ Eval eval-1-convert-repetitive-tests

**10/10** · 44345 tokens · 14703ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Discount?' or 'Expected?')
- ✅ **has-three-data-rows**: Table has exactly 3 data rows (GOLD, SILVER, BRONZE)
- ✅ **single-assertion-in-method**: Test method body contains only one assertEquals (or equivalent assertion) — not three
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string), not string concatenation with +

### ✅ Eval eval-2-parse-dates

**13/13** · 73253 tokens · 34202ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell (not the string 'null') in the table
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

**4/4** · 44075 tokens · 9035ms

- ✅ **flags-junit-version**: Response explicitly flags that JUnit 5.10.0 is below the required minimum (5.11) and recommends upgrading
- ✅ **provides-correct-groupid**: Response provides the tabletest dependency with groupId 'org.tabletest' (not guessed)
- ✅ **provides-artifactid**: Response includes 'tabletest-junit' as the artifactId
- ✅ **includes-test-scope**: The suggested dependency includes <scope>test</scope>

### ⚠️ Eval eval-7-permission-check

**6/11** · 70959 tokens · 13760ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **uses-value-sets**: Table uses value-set syntax (curly braces like {READ, WRITE} or {READ, WRITE, DELETE}) to express rules that hold across multiple actions
  > Table uses individual rows (ADMIN | READ | true, ADMIN | WRITE | true) instead of {READ, WRITE, DELETE}. Response states: 'Value sets could collapse ADMIN's rows into one...but explicit rows...'
- ❌ **fewer-than-nine-rows**: Table has fewer than 9 data rows (does not enumerate every role/action combination individually)
  > 9 data rows listed: Admin can read through Guest cannot delete—exactly 9 combinations for 3 roles × 3 actions, not fewer
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ❌ **no-duplicate-role-output**: No two rows share both the same Role value and the same boolean output
  > Rows 1–3 all have ADMIN | true; rows 4–5 have USER | true; rows 8–9 have GUEST | false. Multiple duplicates violate consolidation rule.
- ❌ **has-descriptive-title**: Test method has either a @DisplayName annotation or a descriptive method name (not generic like 'test1' or 'canPerform')
  > Method name is canPerform, which the assertion explicitly lists as a bad example: 'Not a generic name like...canPerform'
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest
- ✅ **description-uses-textblock**: If @Description is present and text is longer than one line, it uses a text block (triple-quoted string), not string concatenation
- ❌ **scenario-names-describe-conditions**: Scenario names describe conditions ('Admin performs any action', 'Guest reads') — not outcomes ('Allowed', 'Denied')
  > Scenarios embed outcome modalities: 'Admin can read', 'User cannot delete', 'Guest cannot write'—'can' and 'cannot' encode expected outcomes rather than pure conditions

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

**10/10** · 98298 tokens · 18505ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **contractor-uses-value-set**: CONTRACTOR row uses a value set for Department (e.g. {SALES, ENGINEERING}) to express 'regardless of department' — not a dummy placeholder like 'ANY' or 'N/A', and not enumerated as separate rows.
- ✅ **four-core-rules-covered**: The four level×department combinations (SENIOR+SALES, SENIOR+ENGINEERING, JUNIOR+SALES, JUNIOR+ENGINEERING) are all present in the table(s)
- ✅ **expects-bonus-percentage**: The expected output column contains bonus percentages (e.g. 15%, 12%, 0%) — not bonus amounts. The method returns a percentage, and the table should reflect that.
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'bonusByLevelAndDepartment' → 'Bonus By Level And Department'). Not a generic name like 'test1' or 'testMethod'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express — such as how bonus rates are applied (e.g. as percentage of base salary), or open questions. Does NOT merely restate the column names or summarise what the rows show. It is acceptable to omit @Description if the table already conveys all relevant context.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Senior in Sales', 'Contractor regardless of department') — not outcomes ('Gets 15%', 'No bonus').
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Level', 'Department', 'Bonus %') — not implementation terms like 'employeeLevel', 'deptCode', 'result'.

### ⚠️ Eval eval-15-reis-discount

**4/18** · 45131 tokens · 78482ms

- ❌ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
  > Response has two tables ('Reis Discount Tier', 'Discount by Passenger Type') but no rolling window table; rolling window boundary is listed as an open question
- ✅ **2.2-children-flat-discount**: Children's flat 20% discount is represented — either as a row in the eligibility table or a separate note.
- ✅ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers derived from the 'every fifth trip' rule, with boundary values or value sets showing which ticket counts produce which discount. The maximum 40% discount is represented.
- ❌ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
  > No rolling window table exists; response lists this as an open question: 'Rolling window edge: If exactly 30 days ago...'
- ❌ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
  > 'Reis Discount Tier' table has no value sets; uses individual rows '5 | 5%', '7 | 5%', '9 | 5%' instead of grouped values
- ❌ **2.15-ticket-count-uses-value-sets**: The discount ladder table uses value sets for the ticket count column to group counts within the same tier (e.g. {5, 6, 7, 8, 9} or {5, 9} for min/max → 5%) — not one row per boundary value.
  > 'At first discount threshold | 5 | 5%', 'Mid first tier | 7 | 5%', 'Top of first tier | 9 | 5%' are separate rows; no value sets used
- ❌ **2.16-no-duplicate-tier-mapping**: The tier-to-discount mapping is expressed once — not repeated across multiple tables.
  > Both 'Reis Discount Tier' and 'Discount by Passenger Type' tables express that ticket counts 5+ map to 5% discount, duplicating the tier mapping
- ❌ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
  > Response contains markdown tables, not Java code; no @DisplayName annotations present
- ❌ **2.11-format-description**: @Description is present on at least the class or the tier method and provides context beyond what the table rows already express.
  > No @Description annotations present in response
- ❌ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
  > Response has no Java annotations; tables are in markdown format
- ✅ **2.14-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **2.17-zone-irrelevance-visible**: The eligibility/passenger-type table includes a Zone column (or equivalent) with a value set to show that the discount applies regardless of zone.
  > 'Zones Do Not Affect Discount' table has Zone column but separate rows '1 zone' and '3 zones' instead of value set
- ❌ **2.18-adult-senior-value-set**: Adult and senior are expressed as a value set {ADULT, SENIOR} (or equivalent) in at least one row of the eligibility table.
  > 'Adult — at first tier | Adult | 4 | 5%' and 'Senior — same ladder as adult | Senior | 4 | 5%' are enumerated as separate rows
- ❌ **2.19-depth-all-tiers**: All 9 discount tiers are represented in the ladder table: 0% (tickets 1-4), 5% (5-9), 10% (10-14), 15% (15-19), 20% (20-24), 25% (25-29), 30% (30-34), 35% (35-39), and 40% (40+).
  > Table shows only 0%, 5%, 10%, 20%, 40%; missing tiers: 15%, 25%, 30%, 35%
- ❌ **2.20-readability-one-row-per-tier**: Each tier in the discount ladder is expressed as a single row with a value set for ticket counts — not split across multiple rows.
  > 5% tier split across 'At first discount threshold | 5 | 5%', 'Mid first tier | 7 | 5%', 'Top of first tier | 9 | 5%' rows
- ❌ **2.21-readability-relative-time**: The rolling window table expresses time relatively (e.g. '14 days ago', '30 days ago', or an integer 'Days ago' column).
  > No rolling window table exists; response mentions 'last 30 days' in text but no dedicated boundary-testing table
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules.
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.
  > 'Reis Discount Tier' has 10 rows for 5 tiers (0%, 5%, 10%, 20%, 40%); could be 5 rows if consolidated with value sets

### ✅ Eval eval-19-convert-from-parameterized

**8/8** · 71233 tokens · 11668ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions — not outcomes or 'Test case X'
- ✅ **annotation-order**: Annotations appear in order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Test method has descriptive title via @DisplayName or method name
- ✅ **description-uses-textblock**: If @Description present, uses text block not concatenation

### ⚠️ Eval eval-25-convert-from-spock

**9/15** · 118507 tokens · 828778ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells
  > Table header shows "Fragile | Insured Value | Handling | Cost?" as three separate columns
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys
  > No @TypeConverter method present; code uses boxed Integer and String parameters directly
- ❌ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table — not as three separate length/width/height columns
  > Table shows "Length | Width | Height" as three separate columns, not [L, W, H]
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence
- ✅ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern — not one monolithic table
- ❌ **numeric-types-correct**: Weight and cost parameters use BigDecimal (not double or String). Dimensions use Integer or int
  > Parameters are "double weight" and "double expectedCost" instead of BigDecimal; dimensions correctly use int
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ❌ **scenario-names-describe-conditions**: Scenario names describe conditions ('EU express, light package', 'Oversized with fragile') — not outcomes ('8.00', 'Surcharge applied') or generic labels ('Test 1')
  > Second table uses "Fragile surcharge" and "Insured surcharge" which include outcome language
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ✅ **business-language-columns**: Column names use domain language ('Zone', 'Weight (kg)', 'Dimensions', 'Options', 'Cost?') — not code identifiers
- ❌ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
  > packageOptionSurcharges contains: "if (fragile) opts.setFragile(true)..." three times
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order
- ✅ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase
- ✅ **no-groovy-syntax**: Output contains no Groovy or Spock syntax: no def, where:, expect:, given:, GString interpolation, Spock assertions, or Groovy list literals

### ⚠️ Eval eval-27-convert-from-testng

**11/15** · 171044 tokens · 625199ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells.
  > surcharges table has three separate columns: 'Fragile | Insured Value | Handling' instead of one collapsed options map
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
  > Only @TypeConverter present is for ShippingZone; no PackageOptions converter from Map exists
- ✅ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table — not as three separate length/width/height columns
- ❌ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
  > carrierAgnosticPricing table has three separate rows with individual Carrier values, not {DHL, UPS, FEDEX} syntax
- ✅ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern — not one monolithic table mixing base rates, surcharges, dimensional weight, and carrier equivalence
- ✅ **numeric-types-correct**: Weight and cost parameters use BigDecimal (not double or String). Dimensions use Integer or int.
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('EU express, light package', 'Oversized with fragile') — not outcomes ('8.00', 'Surcharge applied') or generic labels ('Test 1').
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ✅ **business-language-columns**: Column names use domain language ('Zone', 'Weight (kg)', 'Dimensions', 'Options', 'Cost?') — not code identifiers ('shippingZone', 'weightKg', 'dims')
- ❌ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
  > surcharges method contains: 'if (fragile)...', 'if (insuredValue != null)...', 'if (handling != null)...'
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **no-testng-artifacts**: Output contains no TestNG syntax: no @DataProvider, @Test(dataProvider=...), Object[][], or TestNG imports (org.testng.*).

### ⚠️ Eval eval-28-convert-from-methodsource

**12/15** · 84912 tokens · 82781ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options are collapsed into a single map column like [fragile: true, insuredValue: 500]
  > appliesSurcharges table shows three separate columns: Fragile | Insured Value | Handling, not collapsed into a map
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> and returns PackageOptions
  > No @TypeConverter method found. Code uses manual opts.setFragile/setInsuredValue/setHandling calls instead
- ✅ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX}
- ✅ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern
- ✅ **numeric-types-correct**: Weight and cost use BigDecimal; Dimensions use Integer or int
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes or generic labels
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **business-language-columns**: Column names use domain language, not code identifiers
- ❌ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
  > appliesSurcharges body contains: if (insuredValue != null) and if (handling != null) statements
- ✅ **annotation-order**: Annotations appear in correct order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Each test method has descriptive @DisplayName or method name
- ✅ **no-methodsource-artifacts**: Output contains no @MethodSource, Stream<Arguments>, Arguments.of, or parameterized imports

### ✅ Eval eval-29-shopping-cart-tt

**21/21** · 313859 tokens · 621747ms

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
- ✅ **uses-standard-map-syntax**: Columns representing maps (e.g. cart contents, product catalogue, inventory levels) use standard TableTest map syntax — [k: v, k2: v2] for entries and [:] for empty map — not a custom bracket notation.
- ✅ **coupon-as-single-column**: Coupon data is expressed in a single column with a @TypeConverter — not spread across separate sparse columns for coupon type, value, and target product. The column format may be map syntax (e.g. [type: PERCENTAGE, value: 10]) or readable shorthand (e.g. '20% cart', '$5 off', '50% off widget').
- ✅ **description-not-redundant-with-scenarios**: @Description annotations do not repeat information that the scenario names already convey. Coupon type behavior (what PERCENTAGE/FIXED/PRODUCT mean) should be expressed through descriptive scenario names, not duplicated in the description.

### ✅ Eval eval-30-order-splitting-tt

**17/17** · 292596 tokens · 585417ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **minimal-rows-per-concern**: Each @TableTest has only the rows needed to express its concern's rules
- ✅ **concern-fulfillment-method**: Fulfillment method splitting is represented as its own @TableTest
- ✅ **concern-delivery-address**: Delivery address splitting is represented as its own @TableTest
- ✅ **concern-availability**: Availability splitting is represented as its own @TableTest
- ✅ **concern-warehouse-allocation**: Warehouse allocation is represented as its own @TableTest
- ✅ **concern-companion-products**: Companion product grouping is represented as its own @TableTest
- ✅ **all-outputs-same-table**: Each @TableTest includes all output columns for its concern
- ✅ **scalar-quantity-for-warehouse**: Warehouse allocation uses scalar quantity, not item lists
- ✅ **scenario-column-present**: Table has scenario column as leftmost column
- ✅ **has-question-mark-column**: At least one output column ends with '?'
- ✅ **scenario-names-describe-conditions**: Scenario names describe business situations
- ✅ **business-language-columns**: Column names use business/domain language
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations in order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Test method has @DisplayName or clear method name
- ✅ **description-uses-textblock**: Multi-line @Description uses text block, not concatenation

