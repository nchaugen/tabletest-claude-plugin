# Eval Review — tabletest variant=minimal, Iteration 4

**Model:** sonnet · **Date:** 2026-04-12 · **Evals:** 19

## Summary

128/160 (80.0%) · 1286509 tokens · 1706.2s · $2.1337

## Delta vs Iteration 3

**Regressions (6):**
- ❌ eval-8-money-parse: `null-as-blank-cell`
- ❌ eval-9-bonus-contractor-structure: `contractor-uses-value-set`
- ❌ eval-18-convert-from-code: `scenario-names-describe-conditions`
- ❌ eval-18-convert-from-code: `depth-premium-boundaries`
- ❌ eval-22-event-registration-tt: `validation-includes-optional-fields`
- ❌ eval-22-event-registration-tt: `optional-fields-has-expected-column`

**Improvements (5):**
- ✅ eval-2-parse-dates: `null-as-blank-cell`
- ✅ eval-7-permission-check: `has-descriptive-title`
- ✅ eval-8-money-parse: `has-descriptive-title`
- ✅ eval-18-convert-from-code: `annotation-order`
- ✅ eval-18-convert-from-code: `description-no-internals`

## Resource Comparison vs Iteration 3

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-1-convert-repetitive-tests | 10/10 | 10/10 | 98875 | 44278 | 17.7 | 13.2 |
| eval-2-parse-dates | 13/13 | 12/13 | 100691 | 46136 | 56.9 | 47.1 |
| eval-3-dependency-setup | 4/4 | 4/4 | 44008 | 43867 | 9.6 | 8.3 |
| eval-7-permission-check | 8/11 | 7/11 | 71109 | 70919 | 16.5 | 16.6 |
| eval-8-money-parse | 12/13 | 12/13 | 103864 | 74739 | 59.8 | 48.1 |
| eval-9-bonus-contractor-structure | 10/11 | 11/11 | 71109 | 44635 | 14.4 | 21.6 |
| eval-14-weekly-pay | 11/18 | — | 220805 | — | 170.5 | — |
| eval-15-reis-discount | 6/18 | — | 46527 | — | 100.3 | — |
| eval-18-convert-from-code | 15/18 | 15/18 | 51804 | 54127 | 248.9 | 131.9 |
| eval-19-convert-from-parameterized | 8/8 | 8/8 | 71384 | 44737 | 262.0 | 18.7 |
| eval-20-collections-and-quoting | 13/13 | 13/13 | 58563 | 127350 | 253.9 | 184.0 |
| eval-22-event-registration-tt | 18/23 | 20/23 | 347770 | 94849 | 495.6 | 220.3 |
| eval-23-loan-approval-tt | T/O | 14/16 | — | 103192 | T/O | 83.0 |
| eval-25-convert-from-spock | T/O | 7/15 | — | 50752 | T/O | 79.3 |
| eval-26-convert-from-kotest | T/O | 10/15 | — | 56106 | T/O | 136.4 |
| eval-27-convert-from-testng | T/O | 11/15 | — | 52219 | T/O | 87.4 |
| eval-28-convert-from-methodsource | T/O | 12/15 | — | 93296 | T/O | 134.4 |
| eval-29-shopping-cart-tt | T/O | 21/21 | — | 218477 | T/O | 249.2 |
| eval-30-order-splitting-tt | T/O | 18/18 | — | 309990 | T/O | 689.0 |

## Per-Eval Results

### ✅ Eval eval-1-convert-repetitive-tests

**10/10** · 98875 tokens · 17710ms

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

**13/13** · 100691 tokens · 56947ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell (not the string 'null') in the table
- ✅ **exception-handled-cleanly**: The empty string / exception case is handled — either via a Throws? column, assertThrows inside the method, or a separate @TableTest
- ✅ **localdate-result-column**: There is a result column typed as LocalDate (or using a string representation that maps to LocalDate)
- ✅ **type-conversion-addressed**: The response addresses type conversion for non-trivial column types
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block
- ✅ **empty-string-uses-quotes**: Empty string input uses quoted syntax (e.g. '' or "") in the table — not a blank cell
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules
- ✅ **separates-valid-and-invalid**: Valid parsing cases and error/exception cases are in separate @TableTest methods

### ✅ Eval eval-3-dependency-setup

**4/4** · 44008 tokens · 9589ms

- ✅ **flags-junit-version**: Response explicitly flags that JUnit 5.10.0 is below the required minimum (5.11) and recommends upgrading
- ✅ **provides-correct-groupid**: Response provides the tabletest dependency with groupId 'org.tabletest' (not guessed)
- ✅ **provides-artifactid**: Response includes 'tabletest-junit' as the artifactId
- ✅ **includes-test-scope**: The suggested dependency includes <scope>test</scope>

### ⚠️ Eval eval-7-permission-check

**8/11** · 71109 tokens · 16546ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **uses-value-sets**: Table uses value-set syntax (curly braces like {READ, WRITE} or {READ, WRITE, DELETE}) to express rules that hold across multiple actions — rather than one row per role/action combination
  > Actual table has 9 individual rows with single action values (READ, WRITE, DELETE) not value sets like {READ, WRITE, DELETE}
- ❌ **fewer-than-nine-rows**: Table has fewer than 9 data rows (i.e. does not enumerate every role/action combination individually)
  > Table has exactly 9 data rows: Admin read, Admin write, Admin delete, User read, User write, User cannot delete, Guest read, Guest write, Guest delete
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ❌ **no-duplicate-role-output**: No two rows share both the same Role value and the same boolean output — forces value set consolidation
  > ADMIN with READ=true, ADMIN with WRITE=true, ADMIN with DELETE=true all share role ADMIN and output true
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string), not string concatenation with +. Passes if @Description is absent.
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Admin performs any action', 'Guest reads') — not outcomes ('Allowed', 'Denied')

### ⚠️ Eval eval-8-money-parse

**12/13** · 103864 tokens · 59771ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **null-as-blank-cell**: Null input is represented as a blank cell in the valid inputs @TableTest table (not the string 'null', and not as a separate @Test method). The blank cell maps to a null parameter.
  > Not graded
- ✅ **exception-has-expected-column**: The exception/invalid-input table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row
- ✅ **exception-cases-handled**: The exception cases (empty string, letters-only, negative) are handled
- ✅ **result-column-with-question-mark**: Valid inputs have a result column name ending with '?'
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest
- ✅ **description-uses-textblock**: If @Description is present and longer than one line, uses text block (triple-quoted string), not string concatenation
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules
- ✅ **separates-valid-and-invalid**: Valid parsing cases and error/exception cases are in separate @TableTest methods

### ⚠️ Eval eval-9-bonus-contractor-structure

**10/11** · 71109 tokens · 14446ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **contractor-uses-value-set**: CONTRACTOR row uses a value set for Department (e.g. {SALES, ENGINEERING}) to express 'regardless of department' — not a dummy placeholder like 'ANY' or 'N/A', and not enumerated as separate rows.
  > CONTRACTOR is enumerated as two separate rows (Contractor Sales and Contractor Engineering), not as a single row with {SALES, ENGINEERING} value set.
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

**11/18** · 220805 tokens · 170525ms

- ✅ **1.2-error-has-expected-column**: The error/rejection table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
- ✅ **1.3-depth-overtime-boundary**: Overtime threshold boundary is covered: at least a value at the threshold (40) and just above (41 or similar). Not just values well below and well above.
- ❌ **1.4-depth-combined-scenario**: A combined scenario is present: a row with weekday + Sunday + holiday hours, testing the composition of all hour types.
  > combinedWeeklyPay table has no row with all three hour types (weekday, Sunday, holiday) >0. 'Overtime and Sunday' has holiday=0, 'Regular with holiday' has Sunday=0
- ❌ **1.5-depth-error-edge-cases**: Error/edge cases covered: at least negative hours (floored at zero) and negative or missing rate (rejected).
  > Negative rate is tested but negative hours are not. No test row with negative weekday/Sunday/holiday hours
- ❌ **1.6-readability-empty-cells**: Sunday and Holiday hour columns use empty cells (not 0) when those inputs are not relevant to a scenario — making it easier to spot which rows involve weekend/holiday hours. This is a readability preference; parameter types should be Integer (not int) to support null from blank cells.
  > weekdayOvertimeThreshold and doubleTimeRates use explicit 0 values instead of empty cells; parameters are primitive int not Integer
- ✅ **1.7-readability-scenario-names**: Scenario names describe work patterns (e.g. 'Part-time', 'Five hours overtime', 'Full Sunday shift') — not 'Test case 1' or expected outcomes.
- ❌ **1.8-correctness-expected-values**: Expected values in intermediate and total columns are arithmetically correct for every row, consistent with the formulas in @Description.
  > combinedWeeklyPay 'Overtime and Sunday' row: 50h weekday + 8h Sunday at rate 10.00 expects 710, but calculation shown is unclear; formulae not documented in @Description
- ✅ **1.9-correctness-value-set-semantics**: Value sets are only used where all values in the set produce the same expected result (e.g. any rate × 0 = 0). Not used where results would differ.
- ✅ **1.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ❌ **1.11-format-description**: @Description is present on at least the pay calculation method and provides context beyond what the table rows already express — such as the overtime threshold (40 hours), premium multipliers (Sunday 2×, Holiday 2.5×), or other fixed assumptions not visible in the table.
  > @Description mentions fixed rate/hours but never states the overtime threshold (40h), Sunday multiplier (2×), or Holiday multiplier values
- ✅ **1.12-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **1.13-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **1.14-depth-zero-rate**: A row where the hourly rate is zero, showing that pay is zero regardless of hours worked. This is a distinct edge case from negative rate (which is rejected).
  > No test row with rate=0.00. 'Zero hours' row has rate=25.00, not rate=0.00
- ✅ **1.15-format-clean-method**: The test method body contains no if/ternary null-handling or parsing logic. Null-to-default conversion (e.g. blank Sunday hours → 0) is handled via a @TypeConverter method or a private helper, keeping the test method limited to arrange/act/assert.
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Weekday hours', 'Sunday hours', 'Hourly rate', 'Weekly pay?') — not implementation terms like 'weekdayHrs', 'int', 'param1'.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ❌ **separates-classification-and-calculation**: Payable hours categorisation (1x regular, 1.5x overtime, 2x Sunday/holiday) and pay calculation (hours × rate) are in separate @TableTest methods
  > No table explicitly classifies hours into 1x/1.5x/2x categories; all tables mix classification and calculation

### ⚠️ Eval eval-15-reis-discount

**6/18** · 46527 tokens · 100251ms

- ✅ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
- ✅ **2.2-children-flat-discount**: Children's flat 20% discount is represented — either as a row in the eligibility table or a separate note. It does not follow the ladder.
- ❌ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers derived from the 'every fifth trip' rule, with boundary values or value sets showing which ticket counts produce which discount. The maximum 40% discount is represented.
  > Table 1 enumerates individual ticket counts (5, 9, 10, 14, 15, 20, 25, 30, 35, 40, 50) as separate rows rather than using value sets to group tier ranges.
- ❌ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
  > Table 2 has 'Ticket exactly 30 days ago | 2026-04-12 | 2026-03-13 | ?' with '?' as the outcome, leaving the boundary condition unanswered.
- ❌ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
  > No value sets are used in Table 1; each row specifies a single ticket count rather than grouping counts that yield the same discount.
- ❌ **2.15-ticket-count-uses-value-sets**: The discount ladder table uses value sets for the ticket count column to group counts within the same tier (e.g. {5, 6, 7, 8, 9} or {5, 9} for min/max → 5%) — not one row per boundary value. The 'every fifth trip' rule creates tiers; the table should express tiers, not enumerate boundaries.
  > Table 1 has separate rows for each example count: 'Adult, fifth — first discount applies | Adult | 5 | 5%' and 'Adult, ninth — still in first tier | Adult | 9 | 5%' instead of a single row with {5, 6, 7, 8, 9}.
- ✅ **2.16-no-duplicate-tier-mapping**: The tier-to-discount mapping is expressed once — not repeated across multiple tables. A price application table (if present) should test the arithmetic (base price × discount → final price) without re-enumerating which ticket counts map to which tier.
- ❌ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
  > Response contains no test code with @DisplayName annotations or method names; it shows only table markup and prose analysis.
- ❌ **2.11-format-description**: @Description is present on at least the class or the tier method and provides context beyond what the table rows already express — such as the discount ladder structure, eligibility rules, or application context. Does NOT merely restate the column names or summarise what the rows show.
  > Response contains no @Description annotations; it shows table markup without test class or method annotations.
- ❌ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
  > Response does not include any Java annotations or test code structure; only tables and prose.
- ✅ **2.14-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **2.17-zone-irrelevance-visible**: The eligibility/passenger-type table includes a Zone column (or equivalent) with a value set to show that the discount applies regardless of zone — rather than silently omitting zone. The requirement explicitly says 'regardless of the zones you travel in'.
  > Response explicitly states 'Zone does not affect discount... no zone column is needed', choosing omission over demonstrating irrelevance.
- ❌ **2.18-adult-senior-value-set**: Adult and senior are expressed as a value set {ADULT, SENIOR} (or equivalent) in at least one row of the eligibility table, since they follow identical discount rules. Not enumerated as separate rows with the same outcome.
  > Table 1 has separate rows: 'Senior receives same discount as adult | Senior | 5 | 5%' and 'Adult, fifth — first discount applies | Adult | 5 | 5%' instead of combining into a single row with {Adult, Senior}.
- ✅ **2.19-depth-all-tiers**: All 9 discount tiers are represented in the ladder table: 0% (tickets 1-4), 5% (5-9), 10% (10-14), 15% (15-19), 20% (20-24), 25% (25-29), 30% (30-34), 35% (35-39), and 40% (40+). Not just a subset of tiers.
- ❌ **2.20-readability-one-row-per-tier**: Each tier in the discount ladder is expressed as a single row with a value set for ticket counts — not split across multiple rows (e.g. separate rows for 'tier kicks in' and 'tier holds'). One tier = one row.
  > Table 1 splits each tier across multiple rows: '5% (5-9)' is split into 'Adult, fifth' and 'Adult, ninth' as separate rows instead of one row with {5, 6, 7, 8, 9}.
- ❌ **2.21-readability-relative-time**: The rolling window table expresses time relatively (e.g. '14 days ago', '30 days ago', or an integer 'Days ago' column) rather than using absolute dates, making the table readable without needing to know the reference date.
  > Table 2 uses absolute dates: 'Purchase date | 2026-04-12' and 'Previous ticket dates | 2026-03-20, 2026-03-25' instead of relative offsets like '30 days ago'.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
  > Table 1 has 17 rows; it could use value sets to reduce to 9 rows (one per tier). Rows like 'Adult, fifth' and 'Adult, ninth' are redundant enumeration of tier 5% rather than terse tier expression.

### ⚠️ Eval eval-18-convert-from-code

**15/18** · 51804 tokens · 248911ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **black-box-columns**: Table columns represent the method's public inputs (applicant type, age, claim count) and observable outputs (decision, premium) — not internal state
- ✅ **observable-io-only**: No column is named after private fields or internal variables (hasActivePolicy, internalRiskScore, riskScore). The test treats the method as a black box.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **business-language-columns**: Column names use domain/business language — not code identifiers like 'applicantType', 'claimCount'
- ❌ **scenario-names-describe-conditions**: Scenario names describe conditions — not outcomes
  > 'Rejected - high risk' describes outcome, not condition. 'Auto-approved renewal' also describes outcome
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Decision?' or 'Premium?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string). Passes if @Description is absent.
- ✅ **concerns-decomposed**: Multiple tables are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations
- ✅ **separates-decision-and-premium**: Decision logic and premium calculation are in separate @TableTest methods
- ✅ **no-reimplemented-internals**: Test method body does not recompute internal formulas. It only calls the public API and asserts on return value's fields.
- ✅ **description-no-internals**: @Description explains the test's purpose and business context — not internal formulas or implementation details. Passes if @Description absent.
- ❌ **depth-decision-boundaries**: Approval decision table covers the rejection boundary thoroughly: 5 claims triggers rejection, 4 claims approval. Senior vs non-senior (64 vs 65) distinction visible.
  > Table includes age=0,claims=5 (APPROVED) and age=10,claims=5 (REJECTED) for the 75 boundary, but lacks the 4-vs-5 claims comparison and 64-vs-65 age boundary
- ❌ **depth-premium-boundaries**: Approved premium table covers boundary effects: the premium jump from 0 to 1 claim, and premium difference at age 64 vs 65 (standard vs senior formula).
  > Premium table has 0-vs-2 claims (missing 1 claim), and age 65,70 vs 40 but no explicit 64-vs-65 comparison

### ✅ Eval eval-19-convert-from-parameterized

**8/8** · 71384 tokens · 262008ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions not outcomes or 'Test case X'
- ✅ **annotation-order**: Annotations appear in correct order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Test method has descriptive title via @DisplayName or method name
- ✅ **description-uses-textblock**: If @Description present and long, uses text block not concatenation

### ✅ Eval eval-20-collections-and-quoting

**13/13** · 58563 tokens · 253900ms

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
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.

### ⚠️ Eval eval-22-event-registration-tt

**18/23** · 347770 tokens · 495615ms

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
  > shouldValidateRequiredFields table has no Dietary Requirements or Accessibility Needs columns; nulls hardcoded in register(..., null, null, ...)
- ✅ **descriptive-registration-date**: Registration date uses descriptive values (e.g. 'before cutoff', 'on cutoff', 'after cutoff') with a @TypeConverter method converting to actual dates — not raw date literals like '2025-02-28'. Readability over precision.
- ✅ **cutoff-date-column-if-literal-dates**: If registration dates are literal (e.g. '2025-02-28' instead of descriptive), the early-bird cutoff date appears as a separate policy column so the reader can see both dates and verify the comparison. Passes automatically if descriptive date values are used.
- ❌ **description-no-irrelevant-information**: If @Description is present, it does not include information that is already visible in the table columns or that can be derived from the table structure — such as fixed input values that could be columns, or restating the discount rules that the rows already demonstrate.
  > @Description states 'dietary requirements and accessibility needs are null' and 'group size is 1' which are visible in table or method body; also restates discount rules (20%, 15%)
- ❌ **discount-column-preferred**: Output column is 'Discount?' (percentage or amount off) rather than 'Price?' — making verification simpler (discount is the rule's direct output; price requires knowing the base price). If 'Price?' is used instead, both 'Base price' and 'Price?' columns must be present so the reader can trace the calculation.
  > Output column is 'Price?' without a separate 'Base price' column; reader cannot independently verify the discount calculation
- ✅ **single-assertion-in-method**: Each @TableTest method has a single, uniform assertion pattern applied to all rows — not different assertions per scenario. Validation checks one thing (e.g. error message or acceptance status); pricing checks one thing (e.g. discount or price). No if/switch in assertion logic.
- ❌ **optional-fields-has-expected-column**: The optional-fields acceptance table includes an expectation/output column (e.g. 'Approved?', 'Result?', or 'Status?') asserting that each combination is accepted — not just varying inputs without any expected column.
  > No separate optional-fields table present; optional fields (Dietary Requirements, Accessibility Needs) not tested for acceptance
- ❌ **description-no-redundant-field-values**: Scenario descriptions do not include specific field values that are already visible in the table or that serve as 'any valid value' placeholders. Stating 'name: John, email: john@example.com, registration date: 2025-04-01' in a description adds maintenance burden without value — it suffices to say 'valid values supplied' or describe the scenario condition. Applies to all tables.
  > @Description for shouldValidateRequiredFields lists 'Registration date is 2025-04-01' and 'group size is 1' which are implementation details not part of validation concern

