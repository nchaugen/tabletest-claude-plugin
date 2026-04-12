# Eval Review — tabletest variant=minimal, Iteration 1

**Model:** sonnet · **Date:** 2026-04-11 · **Evals:** 19

## Summary

218/263 (82.9%) · 1820307 tokens · 3118.4s · $5.4333

## Per-Eval Results

### ✅ Eval eval-1-convert-repetitive-tests

**10/10** · 45581 tokens · 15902ms

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

### ✅ Eval eval-2-parse-dates

**13/13** · 76796 tokens · 45089ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell (not the string 'null') in the table
- ✅ **exception-handled-cleanly**: The empty string / exception case is handled — either via a Throws? column, assertThrows inside the method, or a separate @TableTest
- ✅ **localdate-result-column**: There is a result column typed as LocalDate (or using a string representation that maps to LocalDate)
- ✅ **type-conversion-addressed**: The response addresses type conversion for non-trivial column types
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block
- ✅ **empty-string-uses-quotes**: Empty string input uses quoted syntax (e.g. '' or "") in the table
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules
- ✅ **separates-valid-and-invalid**: Valid parsing cases and error/exception cases are in separate @TableTest methods

### ✅ Eval eval-3-dependency-setup

**4/4** · 45166 tokens · 9782ms

- ✅ **flags-junit-version**: Response explicitly flags that JUnit 5.10.0 is below the required minimum (5.11) and recommends upgrading
- ✅ **provides-correct-groupid**: Response provides the tabletest dependency with groupId 'org.tabletest' (not guessed)
- ✅ **provides-artifactid**: Response includes 'tabletest-junit' as the artifactId
- ✅ **includes-test-scope**: The suggested dependency includes <scope>test</scope>

### ⚠️ Eval eval-7-permission-check

**10/11** · 103666 tokens · 28715ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **uses-value-sets**: Table uses value-set syntax (curly braces like {READ, WRITE} or {READ, WRITE, DELETE}) to express rules that hold across multiple actions
- ✅ **fewer-than-nine-rows**: Table has fewer than 9 data rows (i.e. does not enumerate every role/action combination individually)
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ❌ **no-duplicate-role-output**: No two rows share both the same Role value and the same boolean output
  > USER + true appears twice: USER can READ (true) and USER can WRITE (true)
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions not outcomes

### ✅ Eval eval-8-money-parse

**13/13** · 47533 tokens · 47471ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell in the valid inputs @TableTest table
- ✅ **exception-has-expected-column**: The exception/invalid-input table has an expected column specifying the exception type per row
- ✅ **exception-cases-handled**: The exception cases (empty string, letters-only, negative) are handled
- ✅ **result-column-with-question-mark**: Valid inputs have a result column name ending with '?'
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a clear, descriptive method name
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows express
- ✅ **annotation-order**: Annotations appear in order: @DisplayName, @Description (if present), @TableTest
- ✅ **description-uses-textblock**: If @Description is present and longer than one line, uses triple-quoted string, not concatenation
- ✅ **concerns-decomposed**: Multiple tables are used, each addressing a distinct concern
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules
- ✅ **separates-valid-and-invalid**: Valid parsing cases and error/exception cases are in separate @TableTest methods

### ⚠️ Eval eval-9-bonus-contractor-structure

**9/10** · 46715 tokens · 30698ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **contractor-uses-value-set**: CONTRACTOR row uses a value set for Department (e.g. {SALES, ENGINEERING}) to express 'regardless of department' — not a dummy placeholder like 'ANY' or 'N/A', and not enumerated as separate rows.
- ✅ **four-core-rules-covered**: The four level×department combinations (SENIOR+SALES, SENIOR+ENGINEERING, JUNIOR+SALES, JUNIOR+ENGINEERING) are all present in the table(s)
- ✅ **expects-bonus-percentage**: The expected output column contains bonus percentages (e.g. 15%, 12%, 0%) — not bonus amounts. The method returns a percentage, and the table should reflect that.
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'bonusByLevelAndDepartment' → 'Bonus By Level And Department'). Not a generic name like 'test1' or 'testMethod'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express — such as how bonus rates are applied (e.g. as percentage of base salary), or open questions. Does NOT merely restate the column names or summarise what the rows show. It is acceptable to omit @Description if the table already conveys all relevant context.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Senior in Sales', 'Contractor regardless of department') — not outcomes ('Gets 15%', 'No bonus').
- ❌ **business-language-columns**: Column names use domain/business language (e.g. 'Level', 'Department', 'Bonus %') — not implementation terms like 'employeeLevel', 'deptCode', 'result'.
  > Column header 'Bonus?' uses question mark format, unclear if value or yes/no question; assertion example 'Bonus %' clearly indicates percentage type

### ⚠️ Eval eval-14-weekly-pay

**16/19** · 212356 tokens · 242803ms

- ❌ **1.1-traceability-columns**: The pay table includes intermediate expected columns (e.g. Regular pay?, Overtime pay?, Premium pay?) so the reader can trace which rule contributes what — not just a single total Weekly pay? column.
  > All three pay tables contain only 'Pay?' column. Combined table shows formulas in @Description but not as intermediate table columns (Regular pay?, Overtime pay?, etc.)
- ✅ **1.2-error-has-expected-column**: The error/rejection table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
- ✅ **1.3-depth-overtime-boundary**: Overtime threshold boundary is covered: at least a value at the threshold (40) and just above (41 or similar). Not just values well below and well above.
- ✅ **1.4-depth-combined-scenario**: A combined scenario is present: a row with weekday + Sunday + holiday hours, testing the composition of all hour types.
- ❌ **1.5-depth-error-edge-cases**: Error/edge cases covered: at least negative hours (floored at zero) and negative or missing rate (rejected).
  > Only negative rate tested ('Negative rate with hours', 'Negative rate with zero hours'). Negative hours edge case not present in any table.
- ❌ **1.6-readability-empty-cells**: Sunday and Holiday hour columns use empty cells (not 0) when those inputs are not relevant to a scenario — making it easier to spot which rows involve weekend/holiday hours. This is a readability preference; parameter types should be Integer (not int) to support null from blank cells.
  > Method parameters use 'int' not 'Integer'; tables use hardcoded 0 values (e.g., 'Sunday hours = 0 and holiday hours = 0 for all rows') not empty cells
- ✅ **1.7-readability-scenario-names**: Scenario names describe work patterns (e.g. 'Part-time', 'Five hours overtime', 'Full Sunday shift') — not 'Test case 1' or expected outcomes.
- ✅ **1.8-correctness-expected-values**: Expected values in intermediate and total columns are arithmetically correct for every row, consistent with the formulas in @Description.
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
- ✅ **separates-classification-and-calculation**: Payable hours categorisation (1x regular, 1.5x overtime, 2x Sunday/holiday) and pay calculation (hours × rate) are in separate @TableTest methods

### ⚠️ Eval eval-15-reis-discount

**3/20** · 46828 tokens · 94256ms

- ❌ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
  > Response provides Table 1 (discount ladder) and Table 2 (passenger type eligibility) but no rolling window table; questions about rolling window remain unanswered
- ✅ **2.2-children-flat-discount**: Children's flat 20% discount is represented — either as a row in the eligibility table or a separate note. It does not follow the ladder.
- ❌ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers derived from the 'every fifth trip' rule, with boundary values or value sets showing which ticket counts produce which discount. The maximum 40% discount is represented.
  > Table 1 shows individual values (0, 3, 4, 8, 9, 14, 19, 39, 45) as separate rows, not tier boundaries or value sets grouping counts {1-4}, {5-9}, etc.
- ❌ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
  > Response raises 'Open questions' about rolling window semantics but provides no table testing the 30-day boundary condition
- ❌ **2.6-readability-human-readable-values**: Eligibility uses human-readable values (yes/no or equivalent) rather than raw true/false. TypeConverter or similar mechanism mentioned.
  > Values are human-readable (Adult, Senior, Child, percentages) but TypeConverter/TypeConverterSources is not mentioned anywhere
- ❌ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
  > Table 1 (Reis Discount Tier) lists individual values (4, 8, 9, 14, 19, 39, 45) as separate rows, not value sets grouping counts by tier
- ❌ **2.15-ticket-count-uses-value-sets**: The discount ladder table uses value sets for the ticket count column to group counts within the same tier (e.g. {5, 6, 7, 8, 9} or {5, 9} for min/max → 5%) — not one row per boundary value.
  > Table 1 enumerates rows: '4 | 5%', '8 | 5%', '9 | 10%' as separate rows instead of grouping {4-8} → 5% in a single value set
- ❌ **2.16-no-duplicate-tier-mapping**: The tier-to-discount mapping is expressed once — not repeated across multiple tables.
  > Table 1 and Table 2 both contain mappings: 4→5%, 9→10%, 39→40%. E.g., 'Adult at first tier | Adult | 4 | 5%' repeats '4 | 5%' from Table 1
- ❌ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
  > Response is a specification without showing actual test method code or @DisplayName annotations
- ❌ **2.11-format-description**: @Description is present on at least the class or the tier method and provides context beyond what the table rows already express.
  > No @Description annotation shown; response contains explanatory text but not in @Description format on code
- ❌ **2.12-format-typeconverter**: TypeConverterSources is used for at least one human-readable conversion (yes/no, or rolling window notation).
  > TypeConverterSources not mentioned anywhere in the response
- ❌ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
  > No annotations shown; response provides specification not test code with annotation order
- ✅ **2.14-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **2.17-zone-irrelevance-visible**: The eligibility/passenger-type table includes a Zone column (or equivalent) with a value set to show that the discount applies regardless of zone.
  > Table 2 'Discount by Passenger Type' lacks a Zone column; only a note says zones would hold 'any value in every row' if added
- ✅ **2.18-adult-senior-value-set**: Adult and senior are expressed as a value set {ADULT, SENIOR} (or equivalent) in at least one row of the eligibility table.
- ❌ **2.19-depth-all-tiers**: All 9 discount tiers are represented in the ladder table: 0% (tickets 1-4), 5% (5-9), 10% (10-14), 15% (15-19), 20% (20-24), 25% (25-29), 30% (30-34), 35% (35-39), and 40% (40+).
  > Table 1 shows 0%, 5%, 10%, 15%, 20%, 40% but is missing 25%, 30%, and 35% tiers
- ❌ **2.20-readability-one-row-per-tier**: Each tier in the discount ladder is expressed as a single row with a value set for ticket counts — not split across multiple rows.
  > Table 1 splits 5% tier across two rows: 'At first threshold — fifth ticket | 4 | 5%' and 'Within first tier | 8 | 5%'
- ❌ **2.21-readability-relative-time**: The rolling window table expresses time relatively (e.g. '14 days ago', '30 days ago', or an integer 'Days ago' column) rather than using absolute dates.
  > No rolling window table provided; response only raises open questions about rolling window semantics
- ❌ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules.
  > Response provides 2 tables (discount ladder, passenger type) but missing rolling window table; expected output requires 3 separate tables
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.
  > Table 2 shows unnecessary permutation: 'Child — flat rate regardless of trip count | Child | {4, 20, 39} | 20%' showing same tier mapping in both tables

### ⚠️ Eval eval-18-convert-from-code

**8/14** · 87263 tokens · 126419ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **black-box-columns**: Table columns represent the method's public inputs (applicant type, age, claim count) and observable outputs (decision, premium) — not internal state
  > Table includes 'Risk Score?' column, which is an internal calculation (age/10 + claimCount*15), violating black-box principle
- ❌ **observable-io-only**: No column is named after private fields or internal variables. The test treats the method as a black box.
  > 'Risk Score?' column exposes internal riskScore calculation; violates black-box testing
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ❌ **business-language-columns**: Column names use domain/business language, not code identifiers
  > Uses code identifiers: 'applicantType' (should be 'Applicant Type'), 'claimCount' (should be 'Claim Count')
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?'
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a clear method name
- ✅ **description-uses-textblock**: Multi-line @Description uses text block (triple quotes), not string concatenation
- ❌ **concerns-decomposed**: Multiple tables address distinct concerns, not one monolithic table mixing unrelated rules
  > Single @TableTest method mixes multiple concerns: auto-approval, rejection boundary, standard premium, senior premium calculations
- ❌ **minimal-rows-per-concern**: Each table has only rows needed for its concern; fewer rows when concerns are separated
  > Single table has 7 rows covering multiple concerns; would have fewer rows per table if decision and premium logic were separated
- ❌ **separates-decision-and-premium**: Decision logic and premium calculation are in separate @TableTest methods
  > Only one @TableTest method provided; decision (Status?) and premium (Premium?) columns in same table

### ✅ Eval eval-19-convert-from-parameterized

**8/8** · 73812 tokens · 16069ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes or generic names
- ✅ **annotation-order**: Annotations appear in order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Test method has descriptive title via @DisplayName or method name
- ✅ **description-uses-textblock**: If @Description present and multi-line, uses text block triple quotes

### ✅ Eval eval-20-collections-and-quoting

**13/13** · 59059 tokens · 197647ms

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

**20/21** · 96270 tokens · 212193ms

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
  > Second @Description restates rules derivable from rows: 'Groups of 5 or more qualify for 15% off' is shown by row 'Group discount, at threshold...5...15'; 'When both discounts apply, only the higher is used' is shown by 'Both apply' row; mentions 'Alice Smith, alice@example.com' which are visible in the table
- ✅ **discount-column-preferred**: Output column is 'Discount?' (percentage or amount off) rather than 'Price?' — making verification simpler (discount is the rule's direct output; price requires knowing the base price). If 'Price?' is used instead, both 'Base price' and 'Price?' columns must be present so the reader can trace the calculation.
- ✅ **single-assertion-in-method**: Each @TableTest method has a single, uniform assertion pattern applied to all rows — not different assertions per scenario. Validation checks one thing (e.g. error message or acceptance status); pricing checks one thing (e.g. discount or price). No if/switch in assertion logic.

### ⚠️ Eval eval-23-loan-approval-tt

**10/14** · 152472 tokens · 140714ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **threshold-as-column**: Policy thresholds (650/600 credit scores) appear in a dedicated policy column separate from actual credit score column
  > Table columns are: Scenario | age | creditScore | hasStableIncome | Result?. No dedicated 'Credit threshold' column.
- ✅ **concrete-domain-values**: Cell values use concrete domain terms not abstract codes
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ❌ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes
  > Scenarios start with outcomes: 'Approved — above threshold', 'Rejected — below threshold', 'Pending — income not provided'
- ❌ **business-language-columns**: Column names use domain/business language, not code identifiers
  > Columns include code identifiers: 'creditScore' (should be 'Credit score'), 'hasStableIncome' (should be 'Stable income?')
- ✅ **has-question-mark-column**: At least one output column name ends with ?
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Test method has @DisplayName or descriptive method name
- ✅ **description-uses-textblock**: If @Description longer than one line, uses text block (triple quotes) not concatenation
- ✅ **concerns-decomposed**: Multiple tables address distinct concerns, not one monolithic table
- ✅ **minimal-rows-per-concern**: Each table has only rows needed to express its rules, no unnecessary permutations
- ❌ **separates-age-credit-income**: Age boundary, credit score categorization, and income status separated into distinct methods
  > Credit score and income are tested together within each age-based method; no distinct method for credit categorization or income status

### ⚠️ Eval eval-25-convert-from-spock

**11/15** · 64902 tokens · 240997ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options collapsed into single map column [fragile: true, insuredValue: 500]
  > Fragile | Insured value | Handling are three separate columns with mostly-blank cells
- ❌ **options-type-converter**: A @TypeConverter method present accepting Map<String, String> and returning PackageOptions
  > No @TypeConverter annotation present; only packageOptions() helper without @TypeConverter
- ❌ **dimensions-as-list**: Dimensions represented as [L, W, H] list — not three separate columns
  > dimensionalWeightOverride table has Length | Width | Height as separate columns
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX}
- ✅ **concerns-decomposed**: Multiple @TableTest methods addressing distinct concerns
- ❌ **numeric-types-correct**: Weight and cost use BigDecimal; dimensions use Integer/int
  > double weight and double expectedCost used instead of BigDecimal
- ✅ **scenario-column-present**: Each table has scenario/description column as leftmost
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions not outcomes
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **business-language-columns**: Columns use domain language not code identifiers
- ✅ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
- ✅ **annotation-order**: Annotations in order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Each method has @DisplayName with clear descriptive title
- ✅ **no-groovy-syntax**: No Groovy syntax: no def, where:, expect:, GString, Spock assertions

### ⚠️ Eval eval-26-convert-from-kotest

**11/15** · 60971 tokens · 186683ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column
  > Scenario | Fragile | Insured Value | Handling | Cost? — three separate columns, not collapsed
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> and returns PackageOptions
  > "No custom `@TypeConverter` needed" — method is absent from code
- ✅ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX}
- ✅ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern
- ✅ **numeric-types-correct**: Weight and cost parameters use BigDecimal; Dimensions use Integer or int
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes or generic labels
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **business-language-columns**: Column names use domain language, not code identifiers
- ❌ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
  > appliesPackageOptionSurcharges has: if (fragile) isFragile = true; insuredValue?.let {...}
- ✅ **annotation-order**: Annotations appear in order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Each test method has a @DisplayName annotation with a clear, descriptive title
- ❌ **no-kotest-syntax**: Output contains no Kotest syntax (no shouldBe, forAll, row(), etc.)
  > import io.kotest.matchers.shouldBe and shouldBe expectedCost used throughout

### ⚠️ Eval eval-27-convert-from-testng

**12/15** · 60835 tokens · 184570ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options are collapsed into a single map column like [fragile: true, insuredValue: 500]
  > | Fragile | Insured Value | Handling | — three separate columns with blank cells
- ❌ **options-type-converter**: A @TypeConverter method is present for PackageOptions from Map<String, String>
  > No @TypeConverter method present; options manually constructed with if statements
- ✅ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX}
- ✅ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern
- ✅ **numeric-types-correct**: Weight and cost use BigDecimal; dimensions use Integer
- ✅ **scenario-column-present**: Each table has a scenario column as leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions not outcomes or generic labels
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **business-language-columns**: Column names use domain language not code identifiers
- ❌ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
  > packageSurcharges has: if (fragile)... if (insuredValue != null)... if (handling != null)...
- ✅ **annotation-order**: Annotations appear in order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Each test method has @DisplayName with clear descriptive title
- ✅ **no-testng-artifacts**: Output contains no TestNG syntax

### ⚠️ Eval eval-28-convert-from-methodsource

**13/14** · 56510 tokens · 126626ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
  > Has packageOptions() helper, not @TypeConverter; takes boolean/Integer/String params, not Map
- ✅ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table — not as three separate length/width/height columns
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
- ✅ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern — not one monolithic table mixing base rates, surcharges, dimensional weight, and carrier equivalence
- ✅ **numeric-types-correct**: Weight and cost parameters use BigDecimal (not double or String). Dimensions use Integer or int.
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('EU express, light package', 'Oversized with fragile') — not outcomes ('8.00', 'Surcharge applied') or generic labels ('Test 1').
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ✅ **business-language-columns**: Column names use domain language ('Zone', 'Weight (kg)', 'Dimensions', 'Options', 'Cost?') — not code identifiers ('shippingZone', 'weightKg', 'dims')
- ✅ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **no-methodsource-artifacts**: Output contains no @MethodSource syntax: no @MethodSource, Stream<Arguments>, Arguments.of, or JUnit Jupiter parameterized imports (org.junit.jupiter.params.provider.*).

### ✅ Eval eval-29-shopping-cart-tt

**16/16** · 140744 tokens · 549287ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **minimal-rows-per-concern**: Each @TableTest has only the rows needed to express its concern's rules
- ✅ **separates-item-coupon-total-checkout**: Item operations, coupon, cart total, and checkout are in separate @TableTest methods
- ✅ **rows-independently-executable**: Each table row is independently executable with its own preconditions
- ✅ **coupon-before-after-columns**: Coupon application table uses before/after columns
- ✅ **coupon-validity-not-coupon-types**: Coupon application table tests validity and replacement, not coupon type effects
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes
- ✅ **business-language-columns**: Column names use domain/business language, not code identifiers
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in correct order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Test method has descriptive title via @DisplayName or method name
- ✅ **description-uses-textblock**: If @Description present and long, uses text block (triple quotes), not concatenation
- ✅ **single-assertion-in-method**: Each method has single, uniform assertion pattern applied to all rows
- ✅ **test-data-visible**: Product prices, coupon rules visible in table; reader can trace inputs to outputs

### ✅ Eval eval-30-order-splitting-tt

**18/18** · 342828 tokens · 622513ms

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

## Variant vs Official (iterations 28, 27, 26, 25 merged)

### Per-Eval Resource Comparison

| Eval | Pass (off) | Pass (var) | Tokens (off) | Tokens (var) | Tok Δ | Cost (off) | Cost (var) | Cost Δ | Time (off) | Time (var) | Time Δ |
|------|------------|------------|--------------|--------------|-------|------------|------------|--------|------------|------------|--------|
| eval-1-convert-repetitive-tests | 10/10 | 10/10 | 67108 | 45581 | -32% | $0.1630 | $0.0842 | -48% | 21.6s | 15.9s | -26% |
| eval-2-parse-dates | 13/13 | 13/13 | 112865 | 76796 | -32% | $0.2363 | $0.1254 | -47% | 64.5s | 45.1s | -30% |
| eval-3-dependency-setup | 4/4 | 4/4 | 103483 | 45166 | -56% | $0.1660 | $0.0791 | -52% | 11.6s | 9.8s | -16% |
| eval-7-permission-check | 11/11 | 10/11 | 108161 | 103666 | -4% | $0.1066 | $0.1385 | +30% | 36.3s | 28.7s | -21% |
| eval-8-money-parse | 13/13 | 13/13 | 118583 | 47533 | -60% | $0.1774 | $0.1149 | -35% | 320.5s | 47.5s | -85% |
| eval-9-bonus-contractor-structure | 11/11 | 9/10 | 109012 | 46715 | -57% | $0.1186 | $0.1025 | -14% | 51.3s | 30.7s | -40% |
| eval-14-weekly-pay | 15/19 | 16/19 | 170186 | 212356 | +25% | $0.3296 | $0.3646 | +11% | 207.2s | 242.8s | +17% |
| eval-15-reis-discount | 15/20 | 3/20 | 223772 | 46828 | -79% | $0.5049 | $0.1454 | -71% | 413.8s | 94.3s | -77% |
| eval-18-convert-from-code | 12/14 | 8/14 | 194698 | 87263 | -55% | $0.2405 | $0.2345 | -2% | 122.8s | 126.4s | +3% |
| eval-19-convert-from-parameterized | 8/8 | 8/8 | 104360 | 73812 | -29% | $0.0874 | $0.0935 | +7% | 24.2s | 16.1s | -33% |
| eval-20-collections-and-quoting | 9/10 | 13/13 | 158341 | 59059 | -63% | $0.2126 | $0.3045 | +43% | 123.4s | 197.6s | +60% |
| eval-22-event-registration-tt | 16/20 | 20/21 | 178842 | 96270 | -46% | $0.4017 | $0.3127 | -22% | 276.9s | 212.2s | -23% |
| eval-23-loan-approval-tt | 9/14 | 10/14 | 209546 | 152472 | -27% | $0.2598 | $0.2669 | +3% | 132.2s | 140.7s | +6% |
| eval-25-convert-from-spock | 10/15 | 11/15 | 265980 | 64902 | -76% | $0.3948 | $0.3693 | -6% | 209.5s | 241.0s | +15% |
| eval-26-convert-from-kotest | 12/15 | 11/15 | 210373 | 60971 | -71% | $0.3374 | $0.2791 | -17% | 206.4s | 186.7s | -10% |
| eval-27-convert-from-testng | 14/15 | 12/15 | 164842 | 60835 | -63% | $0.2883 | $0.3032 | +5% | 190.6s | 184.6s | -3% |
| eval-28-convert-from-methodsource | 13/15 | 13/14 | 133727 | 56510 | -58% | $0.3157 | $0.2129 | -33% | 202.5s | 126.6s | -37% |
| eval-29-shopping-cart-tt | 17/17 | 16/16 | 323952 | 140744 | -57% | $1.2289 | $0.8160 | -34% | 853.1s | 549.3s | -36% |
| eval-30-order-splitting-tt | 13/18 | 18/18 | 400297 | 342828 | -14% | $1.3004 | $1.0860 | -16% | 897.3s | 622.5s | -31% |
| **Totals (19 comparable)** | **225/262** | **218/263** | **3358128** | **1820307** | **-46%** | **$6.8699** | **$5.4333** | **-21%** | **4365.7s** | **3118.4s** | **-29%** |

**Comparable summary (19 evals in both):**

| Source | Pass Rate | Tokens | Cost | Time |
|--------|-----------|--------|------|------|
| minimal (iter 1) | 218/263 (82.9%) | 1820307 | $5.4333 | 3118.4s |
| official | 225/262 (85.9%) | 3358128 | $6.8699 | 4365.7s |
| **Δ** | | **-46%** | **-21%** | **-29%** |

### Per-Assertion Comparison

| Eval | Assertion | official | minimal |
|------|-----------|----------|---------|
| eval-7-permission-check | no-duplicate-role-output | ✅ | ❌ |
| eval-9-bonus-contractor-structure | business-language-columns | ✅ | ❌ |
| eval-14-weekly-pay | separates-classification-and-calculation | ❌ | ✅ |
| eval-15-reis-discount | 2.3-depth-tier-boundaries | ✅ | ❌ |
| eval-15-reis-discount | 2.4-depth-rolling-window-boundary | ✅ | ❌ |
| eval-15-reis-discount | 2.9-correctness-value-set-tier-semantics | ✅ | ❌ |
| eval-15-reis-discount | 2.15-ticket-count-uses-value-sets | ✅ | ❌ |
| eval-15-reis-discount | 2.16-no-duplicate-tier-mapping | ✅ | ❌ |
| eval-15-reis-discount | 2.10-format-displayname | ✅ | ❌ |
| eval-15-reis-discount | 2.11-format-description | ✅ | ❌ |
| eval-15-reis-discount | 2.13-format-annotation-order | ✅ | ❌ |
| eval-15-reis-discount | 2.19-depth-all-tiers | ✅ | ❌ |
| eval-15-reis-discount | 2.20-readability-one-row-per-tier | ✅ | ❌ |
| eval-15-reis-discount | concerns-decomposed | ✅ | ❌ |
| eval-15-reis-discount | minimal-rows-per-concern | ✅ | ❌ |
| eval-18-convert-from-code | black-box-columns | ✅ | ❌ |
| eval-18-convert-from-code | observable-io-only | ✅ | ❌ |
| eval-18-convert-from-code | business-language-columns | ✅ | ❌ |
| eval-18-convert-from-code | minimal-rows-per-concern | ✅ | ❌ |
| eval-20-collections-and-quoting | special-chars-quoted | ❌ | ✅ |
| eval-22-event-registration-tt | descriptive-registration-date | ❌ | ✅ |
| eval-22-event-registration-tt | cutoff-date-column-if-literal-dates | ❌ | ✅ |
| eval-22-event-registration-tt | discount-column-preferred | ❌ | ✅ |
| eval-23-loan-approval-tt | business-language-columns | ✅ | ❌ |
| eval-23-loan-approval-tt | concerns-decomposed | ❌ | ✅ |
| eval-23-loan-approval-tt | minimal-rows-per-concern | ❌ | ✅ |
| eval-25-convert-from-spock | dimensions-as-list | ✅ | ❌ |
| eval-25-convert-from-spock | concerns-decomposed | ❌ | ✅ |
| eval-25-convert-from-spock | business-language-columns | ❌ | ✅ |
| eval-26-convert-from-kotest | no-if-switch-in-method | ✅ | ❌ |
| eval-27-convert-from-testng | options-as-map | ✅ | ❌ |
| eval-27-convert-from-testng | options-type-converter | ✅ | ❌ |
| eval-27-convert-from-testng | concerns-decomposed | ❌ | ✅ |
| eval-27-convert-from-testng | no-if-switch-in-method | ✅ | ❌ |
| eval-28-convert-from-methodsource | options-type-converter | ✅ | ❌ |
| eval-28-convert-from-methodsource | concerns-decomposed | ❌ | ✅ |
| eval-30-order-splitting-tt | concerns-decomposed | ❌ | ✅ |
| eval-30-order-splitting-tt | minimal-rows-per-concern | ❌ | ✅ |
| eval-30-order-splitting-tt | concern-fulfillment-method | ❌ | ✅ |
| eval-30-order-splitting-tt | concern-delivery-address | ❌ | ✅ |
| eval-30-order-splitting-tt | scalar-quantity-for-warehouse | ❌ | ✅ |

### Load-Bearing Assertions

Assertions that pass with official skill but fail with minimal variant:

- eval-7-permission-check: `no-duplicate-role-output`
- eval-9-bonus-contractor-structure: `business-language-columns`
- eval-15-reis-discount: `2.3-depth-tier-boundaries`
- eval-15-reis-discount: `2.4-depth-rolling-window-boundary`
- eval-15-reis-discount: `2.9-correctness-value-set-tier-semantics`
- eval-15-reis-discount: `2.15-ticket-count-uses-value-sets`
- eval-15-reis-discount: `2.16-no-duplicate-tier-mapping`
- eval-15-reis-discount: `2.10-format-displayname`
- eval-15-reis-discount: `2.11-format-description`
- eval-15-reis-discount: `2.13-format-annotation-order`
- eval-15-reis-discount: `2.19-depth-all-tiers`
- eval-15-reis-discount: `2.20-readability-one-row-per-tier`
- eval-15-reis-discount: `concerns-decomposed`
- eval-15-reis-discount: `minimal-rows-per-concern`
- eval-18-convert-from-code: `black-box-columns`
- eval-18-convert-from-code: `observable-io-only`
- eval-18-convert-from-code: `business-language-columns`
- eval-18-convert-from-code: `minimal-rows-per-concern`
- eval-23-loan-approval-tt: `business-language-columns`
- eval-25-convert-from-spock: `dimensions-as-list`
- eval-26-convert-from-kotest: `no-if-switch-in-method`
- eval-27-convert-from-testng: `options-as-map`
- eval-27-convert-from-testng: `options-type-converter`
- eval-27-convert-from-testng: `no-if-switch-in-method`
- eval-28-convert-from-methodsource: `options-type-converter`

### Attention Needed

Evals with failed assertions or disproportionate resource usage (>2x official):

| Eval | Issue | Details |
|------|-------|---------|
| eval-7-permission-check | Failed assertions | `no-duplicate-role-output` |
| eval-9-bonus-contractor-structure | Failed assertions | `business-language-columns` |
| eval-15-reis-discount | Failed assertions | `2.3-depth-tier-boundaries`, `2.4-depth-rolling-window-boundary`, `2.9-correctness-value-set-tier-semantics`, `2.15-ticket-count-uses-value-sets`, `2.16-no-duplicate-tier-mapping`, `2.10-format-displayname`, `2.11-format-description`, `2.13-format-annotation-order`, `2.19-depth-all-tiers`, `2.20-readability-one-row-per-tier`, `concerns-decomposed`, `minimal-rows-per-concern` |
| eval-18-convert-from-code | Failed assertions | `black-box-columns`, `observable-io-only`, `business-language-columns`, `minimal-rows-per-concern` |
| eval-23-loan-approval-tt | Failed assertions | `business-language-columns` |
| eval-25-convert-from-spock | Failed assertions | `dimensions-as-list` |
| eval-26-convert-from-kotest | Failed assertions | `no-if-switch-in-method` |
| eval-27-convert-from-testng | Failed assertions | `options-as-map`, `options-type-converter`, `no-if-switch-in-method` |
| eval-28-convert-from-methodsource | Failed assertions | `options-type-converter` |

