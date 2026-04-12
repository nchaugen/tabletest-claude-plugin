# Eval Review — tabletest, Iteration 29

**Model:** sonnet · **Date:** 2026-04-12 · **Evals:** 19

## Summary

200/259 (77.2%) · 2197638 tokens · 2711.5s · $4.7062

## Delta vs Iteration 28

No changes.

## Resource Comparison vs Iteration 28

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-1-convert-repetitive-tests | 10/10 | — | 66658 | — | 16.6 | — |
| eval-2-parse-dates | 11/13 | — | 75285 | — | 59.6 | — |
| eval-3-dependency-setup | 4/4 | — | 66366 | — | 16.7 | — |
| eval-7-permission-check | 10/11 | — | 70730 | — | 41.6 | — |
| eval-8-money-parse | 12/13 | — | 105414 | — | 57.7 | — |
| eval-9-bonus-contractor-structure | 11/11 | — | 71239 | — | 48.5 | — |
| eval-14-weekly-pay | 14/18 | — | 194257 | — | 210.2 | — |
| eval-15-reis-discount | 6/18 | — | 155937 | — | 147.1 | — |
| eval-18-convert-from-code | 11/18 | — | 76891 | — | 99.9 | — |
| eval-19-convert-from-parameterized | 8/8 | — | 116788 | — | 21.7 | — |
| eval-20-collections-and-quoting | 13/13 | — | 166154 | — | 229.3 | — |
| eval-22-event-registration-tt | 18/23 | — | 82502 | — | 223.3 | — |
| eval-23-loan-approval-tt | 11/16 | — | 112878 | — | 152.3 | — |
| eval-25-convert-from-spock | 8/17 | — | 104664 | — | 127.7 | — |
| eval-26-convert-from-kotest | 10/15 | — | 118622 | — | 147.8 | — |
| eval-27-convert-from-testng | 10/15 | — | 113461 | — | 105.3 | — |
| eval-28-convert-from-methodsource | 12/15 | — | 176079 | — | 167.5 | — |
| eval-29-shopping-cart-tt | 21/21 | 17/17 | 323713 | 323952 | 838.6 | 853.1 |
| eval-30-order-splitting-tt | T/O | 13/18 | — | 400297 | T/O | 897.3 |

## Per-Eval Results

### ✅ Eval eval-1-convert-repetitive-tests

**10/10** · 66658 tokens · 16619ms

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

### ⚠️ Eval eval-2-parse-dates

**11/13** · 75285 tokens · 59574ms

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
  > Empty string is tested in separate @Test method, not in the table row
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ❌ **separates-valid-and-invalid**: Valid parsing cases and error/exception cases are in separate @TableTest methods
  > Valid cases in @TableTest; exception case in @Test method (not @TableTest)

### ✅ Eval eval-3-dependency-setup

**4/4** · 66366 tokens · 16732ms

- ✅ **flags-junit-version**: Response explicitly flags that JUnit 5.10.0 is below the required minimum (5.11) and recommends upgrading
- ✅ **provides-correct-groupid**: Response provides the tabletest dependency with groupId 'org.tabletest' (not guessed)
- ✅ **provides-artifactid**: Response includes 'tabletest-junit' as the artifactId
- ✅ **includes-test-scope**: The suggested dependency includes <scope>test</scope>

### ⚠️ Eval eval-7-permission-check

**10/11** · 70730 tokens · 41596ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **uses-value-sets**: Table uses value-set syntax (curly braces like {READ, WRITE} or {READ, WRITE, DELETE}) to express rules that hold across multiple actions — rather than one row per role/action combination
- ✅ **fewer-than-nine-rows**: Table has fewer than 9 data rows (i.e. does not enumerate every role/action combination individually)
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ❌ **no-duplicate-role-output**: No two rows share both the same Role value and the same boolean output — forces value set consolidation
  > USER/READ=true and USER/WRITE=true are separate rows with same role & output
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express — such as where permissions are checked in the request lifecycle, or open questions. Does NOT merely restate the column names or summarise what the rows show. It is acceptable to omit @Description if the table already conveys all relevant context.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Admin performs any action', 'Guest reads') — not outcomes ('Allowed', 'Denied').

### ⚠️ Eval eval-8-money-parse

**12/13** · 105414 tokens · 57680ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **null-as-blank-cell**: Null input is represented as a blank cell in the valid inputs @TableTest table (not the string 'null', and not as a separate @Test method). The blank cell maps to a null parameter.
  > Not graded
- ✅ **exception-has-expected-column**: Exception table has expected column specifying exception type per row
- ✅ **exception-cases-handled**: Exception cases (empty string, letters-only, negative) are handled
- ✅ **result-column-with-question-mark**: Valid inputs have a result column name ending with '?'
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **has-descriptive-title**: Test method has @DisplayName with clear descriptive title
- ✅ **description-if-present-adds-information**: @Description provides context beyond table rows
- ✅ **annotation-order**: Annotations appear in order: @DisplayName, @Description, @TableTest
- ✅ **description-uses-textblock**: @Description uses text block (triple-quoted string) for multi-line text
- ✅ **concerns-decomposed**: Multiple tables address distinct concerns, not one monolithic table
- ✅ **minimal-rows-per-concern**: Each table has only rows needed, no unnecessary permutations
- ✅ **separates-valid-and-invalid**: Valid and invalid cases are in separate @TableTest methods

### ✅ Eval eval-9-bonus-contractor-structure

**11/11** · 71239 tokens · 48485ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **contractor-uses-value-set**: CONTRACTOR row uses a value set for Department (e.g. {SALES, ENGINEERING}) to express 'regardless of department'
- ✅ **four-core-rules-covered**: The four level×department combinations are all present in the table(s)
- ✅ **expects-bonus-percentage**: Expected output column contains bonus percentages (e.g. 15%, 12%, 0%)
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **has-descriptive-title**: Test method has a descriptive title (@DisplayName or method name)
- ✅ **description-if-present-adds-information**: @Description provides context beyond table, or is absent
- ✅ **annotation-order**: Annotations appear in order: @DisplayName, @Description, @TableTest
- ✅ **description-uses-textblock**: @Description uses triple-quoted string if present, or absent
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes
- ✅ **business-language-columns**: Column names use business language (Level, Department, Bonus %), not implementation terms

### ⚠️ Eval eval-14-weekly-pay

**14/18** · 194257 tokens · 210180ms

- ✅ **1.2-error-has-expected-column**: The error/rejection table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
- ✅ **1.3-depth-overtime-boundary**: Overtime threshold boundary is covered: at least a value at the threshold (40) and just above (41 or similar). Not just values well below and well above.
- ✅ **1.4-depth-combined-scenario**: A combined scenario is present: a row with weekday + Sunday + holiday hours, testing the composition of all hour types.
- ❌ **1.5-depth-error-edge-cases**: Error/edge cases covered: at least negative hours (floored at zero) and negative or missing rate (rejected).
  > Negative rate covered ('Negative rate' and 'Just negative' rows), but no rows testing negative hours or flooring logic
- ❌ **1.6-readability-empty-cells**: Sunday and Holiday hour columns use empty cells (not 0) when those inputs are not relevant to a scenario — making it easier to spot which rows involve weekend/holiday hours. This is a readability preference; parameter types should be Integer (not int) to support null from blank cells.
  > shouldCalculateWeekdayPay row 'No hours | 0 | 20.00 | 0.00' uses explicit 0, not empty cell; parameters are 'int' not 'Integer'
- ✅ **1.7-readability-scenario-names**: Scenario names describe work patterns (e.g. 'Part-time', 'Five hours overtime', 'Full Sunday shift') — not 'Test case 1' or expected outcomes.
- ❌ **1.8-correctness-expected-values**: Expected values in intermediate and total columns are arithmetically correct for every row, consistent with the formulas in @Description.
  > 'Sunday and holiday | 0 | 4 | 4' expects $320, but 2× (4+4) × $20 = $320 is correct only if both are 2×; 'All types with overtime | 45 | 8 | 4' expects $1430 but (40×20)+(5×30)+(8×40)+(4×40)=$1430 assumes holiday is 2× not matching stated 2× multiplier clarity
- ✅ **1.9-correctness-value-set-semantics**: Value sets are only used where all values in the set produce the same expected result (e.g. any rate × 0 = 0). Not used where results would differ.
- ✅ **1.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ✅ **1.11-format-description**: @Description is present on at least the pay calculation method and provides context beyond what the table rows already express — such as the overtime threshold (40 hours), premium multipliers (Sunday 2×, Holiday 2.5×), or other fixed assumptions not visible in the table. Does NOT merely restate the column names or summarise what the rows show.
- ✅ **1.12-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **1.13-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **1.14-depth-zero-rate**: A row where the hourly rate is zero, showing that pay is zero regardless of hours worked. This is a distinct edge case from negative rate (which is rejected).
- ✅ **1.15-format-clean-method**: The test method body contains no if/ternary null-handling or parsing logic. Null-to-default conversion (e.g. blank Sunday hours → 0) is handled via a @TypeConverter method or a private helper, keeping the test method limited to arrange/act/assert.
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Weekday hours', 'Sunday hours', 'Hourly rate', 'Weekly pay?') — not implementation terms like 'weekdayHrs', 'int', 'param1'.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ❌ **separates-classification-and-calculation**: Payable hours categorisation (1x regular, 1.5x overtime, 2x Sunday/holiday) and pay calculation (hours × rate) are in separate @TableTest methods
  > No separate table for hour classification; both classification (overtime at 40 threshold) and calculation are tested together in shouldCalculateWeekdayPay and shouldCalculateWeeklyPay

### ⚠️ Eval eval-15-reis-discount

**6/18** · 155937 tokens · 147086ms

- ❌ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
  > Only two @TableTest methods provided: 'reisDiscountTier' and 'discountByPassengerType'. No separate rolling window table; the 30-day window is implicit in trip counts.
- ✅ **2.2-children-flat-discount**: Children's flat 20% discount is represented — either as a row in the eligibility table or a separate note. It does not follow the ladder.
- ✅ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers derived from the 'every fifth trip' rule, with boundary values or value sets showing which ticket counts produce which discount. The maximum 40% discount is represented.
- ❌ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
  > No explicit test of the 30-day boundary. The response notes 'Open question' about counting semantics but provides no boundary test rows for 30-day vs 31-day scenarios.
- ❌ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
  > reisDiscountTier table uses single values per row (0, 4, 5, 9, 10...), not value sets. Only discountByPassengerType uses value sets ({0, 5, 40} for Child), and that row spans multiple discount values (0%, 20%, 40%), violating tier semantics.
- ❌ **2.15-ticket-count-uses-value-sets**: The discount ladder table uses value sets for the ticket count column to group counts within the same tier (e.g. {5, 6, 7, 8, 9} or {5, 9} for min/max → 5%) — not one row per boundary value.
  > reisDiscountTier uses one row per boundary (rows for 5, 9, 10, 15...), not value sets grouping counts within tiers. Each row shows a single trip count, not a set.
- ❌ **2.16-no-duplicate-tier-mapping**: The tier-to-discount mapping is expressed once — not repeated across multiple tables.
  > Discount mappings (5→5%, 10→10%, 15→15%, etc.) appear in both reisDiscountTier and discountByPassengerType tables, causing duplication of the tier structure.
- ✅ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ❌ **2.11-format-description**: @Description is present on at least the class or the tier method and provides context beyond what the table rows already express.
  > No @Description annotation is present on the class or methods. No contextual explanation of the 'every fifth trip' rule or rolling window semantics provided in annotations.
- ✅ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **2.14-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **2.17-zone-irrelevance-visible**: The eligibility/passenger-type table includes a Zone column (or equivalent) with a value set to show that the discount applies regardless of zone.
  > discountByPassengerType table has columns: Scenario, Passenger Type, Trips in last 30 days, Discount?. No Zone column present.
- ❌ **2.18-adult-senior-value-set**: Adult and senior are expressed as a value set {ADULT, SENIOR} (or equivalent) in at least one row of the eligibility table, since they follow identical discount rules.
  > Adults and seniors appear in separate rows (rows 2-3, 4-5, 6-7) with identical discount outcomes. Not grouped into a single value set {ADULT, SENIOR}.
- ✅ **2.19-depth-all-tiers**: All 9 discount tiers are represented in the ladder table: 0% (tickets 1-4), 5% (5-9), 10% (10-14), 15% (15-19), 20% (20-24), 25% (25-29), 30% (30-34), 35% (35-39), and 40% (40+).
- ❌ **2.20-readability-one-row-per-tier**: Each tier in the discount ladder is expressed as a single row with a value set for ticket counts — not split across multiple rows.
  > reisDiscountTier has multiple rows per tier (rows for 5 and 9 both show 5% discount). Should be one row per tier with value set like {5, 6, 7, 8, 9}.
- ❌ **2.21-readability-relative-time**: The rolling window table expresses time relatively (e.g. '14 days ago', '30 days ago', or an integer 'Days ago' column) rather than using absolute dates.
  > No separate rolling window table provided. The 30-day window is referenced only in the column name 'Trips in last 30 days' without explicit boundary test rows.
- ❌ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules.
  > Only two @TableTest methods. Missing third table for rolling window boundaries. Expected three separate tables per spec description.
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.
  > discountByPassengerType repeats tier boundaries (4, 5, 40) across ADULT and SENIOR rows instead of defining tiers once and applying to all passenger types. Creates unnecessary permutations.

### ⚠️ Eval eval-18-convert-from-code

**11/18** · 76891 tokens · 99900ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **black-box-columns**: Table columns represent public inputs and observable outputs, not internal state
- ✅ **observable-io-only**: No column named after private fields (hasActivePolicy, internalRiskScore, riskScore)
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **business-language-columns**: Column names use domain/business language, not code identifiers
- ❌ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes
  > 'High risk score' describes a condition, but 'Risk score boundary (75)' mixes condition+detail; however, 'Renewal, no claims', 'Senior applicant', 'Standard applicant' all describe conditions appropriately. The row 'High risk score' is condition-based (5 claims), not outcome-based. Overall passes.
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?'
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Test method has @DisplayName annotation or clear descriptive method name
- ✅ **description-uses-textblock**: If @Description present and long, uses text block (triple-quoted), not string concatenation
- ❌ **concerns-decomposed**: Multiple @TableTest methods address distinct concerns, not one monolithic table
  > Only one @TableTest method present; assertion asks for multiple tables for decision and premium concerns to be separated
- ❌ **minimal-rows-per-concern**: Each table has only necessary rows for its concern, no unnecessary permutations
  > Single table with 6 rows covers both decision AND premium logic; if separated by concern, each would have fewer rows
- ❌ **separates-decision-and-premium**: Decision logic and premium calculation in separate @TableTest methods
  > Only one @TableTest method; decision (Decision? column) and premium (Premium? column) are in same table
- ✅ **no-reimplemented-internals**: Test does not recompute internal formulas, only calls public API and asserts results
- ❌ **description-no-internals**: @Description explains purpose/context, not internal formulas or implementation details
  > @Description includes 'Risk score = (age / 10) + (claimCount × 15)' and 'senior premium: 200 + (riskScore × 3.5)' — explicit internal formulas
- ❌ **depth-decision-boundaries**: Decision table covers rejection boundary: 5 claims rejects, 4 claims approves; shows 64 vs 65 distinction
  > Table shows 5 claims at age 0 results in APPROVED (risk score 75); no row with 4 claims for comparison; age 64 vs 65 not shown
- ❌ **depth-premium-boundaries**: Premium table covers 0-vs-1 claim boundary and age 64-vs-65 threshold effects
  > Shows 0 claims (age 30, risk 3) at 0.0 and 1 claim (age 30, risk 18) at 136.0; age 64 vs 65 distinction not visible in table rows

### ✅ Eval eval-19-convert-from-parameterized

**8/8** · 116788 tokens · 21712ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions not outcomes or 'Test case 1'
- ✅ **annotation-order**: Annotations appear in order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Test method has descriptive title via @DisplayName or method name
- ✅ **description-uses-textblock**: If @Description present and long, uses text block not string concatenation

### ✅ Eval eval-20-collections-and-quoting

**13/13** · 166154 tokens · 229310ms

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

**18/23** · 82502 tokens · 223338ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **validation-rules-covered**: Email validation and name-required error scenarios are present — at least one row for invalid email and one for missing name.
- ✅ **blank-for-absent-optional**: Dietary requirements and/or accessibility needs use blank cells (null) when not provided — not 'N/A' or 'none'.
- ✅ **blank-vs-value-set-correct**: When testing pricing rules, irrelevant inputs (e.g. dietary requirements don't affect price) use value sets or representative values — not blanks.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Result?', 'Price?')
- ✅ **business-language-columns**: Column names use domain/business language — not code identifiers.
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions — not outcomes.
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a clear, descriptive method name.
- ✅ **description-uses-textblock**: If @Description is present and longer than a single line, it uses a text block (triple-quoted string), not string concatenation.
- ✅ **concerns-decomposed**: Multiple tables are used, each addressing a distinct concern — validation vs pricing.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations.
- ✅ **separates-validation-and-pricing**: Input validation and pricing/discount calculation are in separate @TableTest methods
- ✅ **validation-includes-optional-fields**: Validation @TableTest includes Dietary Requirements and Accessibility Needs columns, with rows showing both null and non-null values are accepted — not hardcoded as null.
- ❌ **descriptive-registration-date**: Registration date uses descriptive values with @TypeConverter, not raw date literals.
  > Pricing table uses literal dates '2025-05-01', '2025-02-28', '2025-03-01' instead of descriptive labels like 'before cutoff'
- ❌ **cutoff-date-column-if-literal-dates**: If registration dates are literal, the early-bird cutoff date appears as a separate policy column.
  > Literal dates used but no separate cutoff-date column; cutoff only mentioned in @Description text
- ❌ **description-no-irrelevant-information**: @Description does not include information already visible in table columns or derived from table structure.
  > @Description states 'Base price is £100... Early-bird (before 2025-03-01): 20% off → £80' which restates discount rules demonstrated in rows
- ❌ **discount-column-preferred**: Output column is 'Discount?' (percentage or amount off) rather than 'Price?' — or if 'Price?' is used, both 'Base price' and 'Price?' columns are present.
  > Column is 'Final Price?' but no 'Base price' column present to trace calculation
- ✅ **single-assertion-in-method**: Each @TableTest method has a single, uniform assertion pattern applied to all rows.
- ✅ **optional-fields-has-expected-column**: The optional-fields acceptance table includes an expectation/output column asserting that each combination is accepted.
- ❌ **description-no-redundant-field-values**: Scenario descriptions do not include specific field values already visible in the table.
  > @Description for pricing states 'Name ("John Smith") and email ("john@example.com") are fixed valid values' — these field identities are already obvious from the test body

### ⚠️ Eval eval-23-loan-approval-tt

**11/16** · 112878 tokens · 152321ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **threshold-as-column**: Policy thresholds (650/600 credit scores) appear in a dedicated policy column separate from applicant's actual credit score column.
  > Credit score threshold values (650, 600) are embedded in scenario names ('score 651 passes', 'score 650 fails') and are not shown as a separate dedicated column. There is only 'Credit Score' column with actual scores.
- ✅ **concrete-domain-values**: Cell values use concrete domain terms (e.g. '70', '650', 'APPROVED') — not abstract codes.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Senior at lower threshold', 'Missing income') — not outcomes ('Approved', 'Rejected').
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Age', 'Credit score', 'Stable income', 'Decision?') — not code identifiers.
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Decision?' or 'Status?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title.
- ✅ **description-uses-textblock**: If @Description is present and text is longer than one short line, it uses a text block (triple-quoted string), not string concatenation with +.
- ❌ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules.
  > Response contains only one @TableTest method mixing all concerns: age boundaries, credit thresholds, stable income, and senior vs standard applicants in one table
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.
  > Single table contains 15 rows mixing age boundaries, thresholds, and income scenarios. Rows 14-15 use value set {true, false} and rows deliberately cross multiple concerns
- ❌ **separates-age-credit-income**: Age boundary policy, credit score categorisation, and income status are separated into distinct @TableTest methods, with a method combining these for the expected verdict
  > Only one @TableTest method exists. No separate methods for age boundaries, credit thresholds, or income status
- ❌ **description-no-redundant-field-values**: Scenario descriptions and @Description text do not include specific field values that are already visible in table.
  > Scenario names include specific values already in columns: 'score 651 passes', 'score 650 fails', 'Age 64', 'Age 65' appear in scenario names and also have dedicated columns
- ✅ **depth-stable-income-effect**: The standardApplicantCreditThreshold table clearly documents the effect of stable income: above-threshold + stable income = APPROVED, above-threshold + no stable income = REJECTED.

### ⚠️ Eval eval-25-convert-from-spock

**8/17** · 104664 tokens · 127739ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells. Rows with no options use a blank cell or [:].
  > Options are in 3 separate columns: 'Fragile | Insured Value | Handling', with blank cells for unset values, not a single map column.
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
  > No @TypeConverter method is present. Options are built in method body with null checks instead.
- ❌ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table — not as three separate length/width/height columns
  > Dimensions are in 3 separate columns: 'Length | Width | Height', not a single [L, W, H] list column.
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
- ❌ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern — not one monolithic table mixing base rates, surcharges, dimensional weight, and carrier equivalence
  > Only one @TableTest method exists mixing base rates, surcharges, dimensional weight, and carrier equivalence in a single table.
- ❌ **numeric-types-correct**: Weight and cost parameters use BigDecimal (not double or String). Dimensions use Integer or int.
  > Weight uses 'double weight', not BigDecimal. Dimensions correctly use 'int length, int width, int height'.
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('EU express, light package', 'Oversized with fragile') — not outcomes ('8.00', 'Surcharge applied') or generic labels ('Test 1').
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ✅ **business-language-columns**: Column names use domain language ('Zone', 'Weight (kg)', 'Dimensions', 'Options', 'Cost?') — not code identifiers ('shippingZone', 'weightKg', 'dims')
- ❌ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
  > if statement found in method body
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ❌ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
  > Method is named 'calculateShippingCost' (camelCase) with no @DisplayName annotation; not a clear descriptive test title.
- ✅ **no-groovy-syntax**: Output contains no Groovy or Spock syntax: no def, where:, expect:, given:, GString interpolation (#variable), Spock assertions (thrown(), old()), or Groovy list literals ([] as Set).
- ❌ **compiles**: Generated test code compiles successfully
  > No build-result.json found
- ❌ **tests-pass**: Generated tests pass when run
  > No build-result.json found

### ⚠️ Eval eval-26-convert-from-kotest

**10/15** · 118622 tokens · 147818ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options collapsed into a single map column like [fragile: true, insuredValue: 500]
  > Three separate columns (Fragile, Insured value, Handling) with mostly-blank cells; options not collapsed into map syntax [fragile: true, insuredValue: 500]
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> and returns PackageOptions
  > No @TypeConverter method present; response explicitly states 'no @TypeConverter methods needed'
- ✅ **dimensions-as-list**: Dimensions represented as a [L, W, H] list in the table
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX}
- ❌ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern
  > Only one @TableTest method present (shouldCalculateShippingCost); single monolithic table mixing base rates, dimensional weight, surcharges, fragile/insured options, and carrier equivalence
- ✅ **numeric-types-correct**: Weight and cost parameters use BigDecimal; Dimensions use Integer or int
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes or generic labels
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **business-language-columns**: Column names use domain language, not code identifiers
- ❌ **no-if-switch-in-method**: Test method body contains no if or switch statements
  > Method body contains 'if (fragile == true)', 'if (insuredValue != null)', 'if (handling != null)' conditionals
- ✅ **annotation-order**: Annotations appear in order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Each test method has @DisplayName annotation or descriptive method name
- ❌ **no-kotest-syntax**: Output contains no Kotest syntax (forAll, row(), withData, shouldBe, etc.)
  > Line contains 'shouldBe expectedCost' which is Kotest syntax; should use standard JUnit assertion instead

### ⚠️ Eval eval-27-convert-from-testng

**10/15** · 113461 tokens · 105311ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells. Rows with no options use a blank cell or [:].
  > Options kept as three separate columns: '| Fragile | Insured Value | Handling |' with mostly blank cells, not collapsed into single map column
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
  > No @TypeConverter method present. Options are constructed manually in method body: 'if (Boolean.TRUE.equals(fragile)) opts.setFragile(true);'
- ✅ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table — not as three separate length/width/height columns
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
- ❌ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern — not one monolithic table mixing base rates, surcharges, dimensional weight, and carrier equivalence
  > Only one @TableTest method present. Single table mixes base rates (rows 1–8), dimensional weight (row 9), surcharges (rows 10–13), and carrier equivalence (row 14)
- ✅ **numeric-types-correct**: Weight and cost parameters use BigDecimal (not double or String). Dimensions use Integer or int.
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('EU express, light package', 'Oversized with fragile') — not outcomes ('8.00', 'Surcharge applied') or generic labels ('Test 1').
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ✅ **business-language-columns**: Column names use domain language ('Zone', 'Weight (kg)', 'Dimensions', 'Options', 'Cost?') — not code identifiers ('shippingZone', 'weightKg', 'dims')
- ❌ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
  > Method body contains if statements: 'if (Boolean.TRUE.equals(fragile))...', 'if (insuredValue != null)...', 'if (handling != null)...'
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ❌ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
  > Method name 'calculateShippingCost' is generic. No @DisplayName annotation present to provide descriptive title
- ✅ **no-testng-artifacts**: Output contains no TestNG syntax: no @DataProvider, @Test(dataProvider=...), Object[][], or TestNG imports (org.testng.*).

### ⚠️ Eval eval-28-convert-from-methodsource

**12/15** · 176079 tokens · 167471ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options collapsed into a single map column like [fragile: true, insuredValue: 500]
  > Options are split into three separate columns: Fragile, Insured value, Handling — not collapsed into a map
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> and returns PackageOptions
  > No @TypeConverter method present. Author states 'No `@TypeConverter` needed'
- ✅ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX}
- ✅ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern
- ✅ **numeric-types-correct**: Weight and cost use BigDecimal, dimensions use Integer or int
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes or generic labels
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **business-language-columns**: Column names use domain language, not code identifiers
- ❌ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
  > shouldApplySurcharges contains: if (Boolean.TRUE.equals(fragile)) and if (insuredValue != null) and if (handling != null)
- ✅ **annotation-order**: Annotations appear in order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Each test method has @DisplayName or clear method name
- ✅ **no-methodsource-artifacts**: Output contains no @MethodSource, Stream, Arguments.of, or parameterized imports

### ✅ Eval eval-29-shopping-cart-tt

**21/21** · 323713 tokens · 838606ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **concerns-decomposed**: Multiple @TableTest methods are used, each addressing a distinct concern
- ✅ **minimal-rows-per-concern**: Each @TableTest has only the rows needed to express its concern's rules
- ✅ **separates-item-coupon-total-checkout**: Item operations, coupon application, total calculation, and checkout are in separate @TableTest methods
- ✅ **rows-independently-executable**: Each table row is independently executable with its own preconditions
- ✅ **coupon-before-after-columns**: Coupon application table uses before/after columns to specify state change
- ✅ **coupon-validity-not-coupon-types**: Coupon application table tests validity and replacement, not type effects on price
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes
- ✅ **business-language-columns**: Column names use domain/business language, not code identifiers
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Test method has @DisplayName annotation or clear method name
- ✅ **description-uses-textblock**: If @Description is present and longer than one line, uses text block (triple-quoted string)
- ✅ **single-assertion-in-method**: Each @TableTest method has a single, uniform assertion pattern
- ✅ **test-data-visible**: Product prices, coupon rules, and test data are visible in table or @Description
- ✅ **type-converters-for-complex-objects**: TypeConverter methods convert map/string values into domain objects
- ✅ **coupon-as-single-column**: Coupon data expressed in a single column with @TypeConverter
- ✅ **uses-standard-map-syntax**: Map columns use standard TableTest syntax [k: v, ...] and [:] for empty
- ✅ **description-not-redundant-with-scenarios**: @Description does not duplicate information scenario names convey

