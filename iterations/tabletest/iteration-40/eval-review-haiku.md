# Eval Review — tabletest, Iteration 40

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-haiku-4-5 · **Date:** 2026-07-25 · **Evals:** 17

## Summary

315/365 (86.3%) · 20956288 tokens · 4379.3s · $14.8965

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

## Delta vs Iteration 39

**Eval definition changed — not comparable (17):**
- ⚠️ eval-1-convert-repetitive-tests: fingerprint differs from iteration 39; re-baseline to compare
- ⚠️ eval-2-parse-dates: fingerprint differs from iteration 39; re-baseline to compare
- ⚠️ eval-7-permission-check: fingerprint differs from iteration 39; re-baseline to compare
- ⚠️ eval-8-money-parse: fingerprint differs from iteration 39; re-baseline to compare
- ⚠️ eval-9-bonus-contractor-structure: fingerprint differs from iteration 39; re-baseline to compare
- ⚠️ eval-14-weekly-pay: fingerprint differs from iteration 39; re-baseline to compare
- ⚠️ eval-15-reis-discount: fingerprint differs from iteration 39; re-baseline to compare
- ⚠️ eval-18-convert-from-code: fingerprint differs from iteration 39; re-baseline to compare
- ⚠️ eval-20-collections-and-quoting: fingerprint differs from iteration 39; re-baseline to compare
- ⚠️ eval-22-event-registration-tt: fingerprint differs from iteration 39; re-baseline to compare
- ⚠️ eval-23-loan-approval-tt: fingerprint differs from iteration 39; re-baseline to compare
- ⚠️ eval-25-convert-from-spock: fingerprint differs from iteration 39; re-baseline to compare
- ⚠️ eval-26-convert-from-kotest: fingerprint differs from iteration 39; re-baseline to compare
- ⚠️ eval-27-convert-from-testng: fingerprint differs from iteration 39; re-baseline to compare
- ⚠️ eval-28-convert-from-methodsource: fingerprint differs from iteration 39; re-baseline to compare
- ⚠️ eval-29-shopping-cart-tt: fingerprint differs from iteration 39; re-baseline to compare
- ⚠️ eval-30-order-splitting-tt: fingerprint differs from iteration 39; re-baseline to compare

## Resource Comparison vs Iteration 39

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-1-convert-repetitive-tests | 13/13 | 13/13 | 438137 | 438137 | 48.3 | 48.3 |
| eval-2-parse-dates | 15/15 | 15/15 | 815800 | 815800 | 115.2 | 115.2 |
| eval-7-permission-check | 12/13 | 13/13 | 585941 | 585941 | 59.9 | 59.9 |
| eval-8-money-parse | 14/15 | 15/15 | 606672 | 606672 | 87.6 | 87.6 |
| eval-9-bonus-contractor-structure | 13/13 | 13/13 | 553264 | 553264 | 82.4 | 82.4 |
| eval-14-weekly-pay | 16/21 | 19/20 | 1307679 | 1307679 | 264.9 | 264.9 |
| eval-15-reis-discount | 25/27 | 16/20 | 2287509 | 2287509 | 411.3 | 411.3 |
| eval-18-convert-from-code | 20/26 | 19/21 | 826034 | 826034 | 148.2 | 148.2 |
| eval-20-collections-and-quoting | 16/17 | 15/15 | 1242601 | 1811829 | 325.6 | 301.8 |
| eval-22-event-registration-tt | 27/28 | 21/25 | 573142 | 573142 | 154.0 | 154.0 |
| eval-23-loan-approval-tt | 21/22 | 12/18 | 540843 | 540843 | 149.9 | 149.9 |
| eval-25-convert-from-spock | 21/26 | 19/20 | 3006845 | 3006845 | 514.5 | 514.5 |
| eval-26-convert-from-kotest | 20/26 | 17/20 | 1443172 | 1443172 | 362.4 | 362.4 |
| eval-27-convert-from-testng | 19/25 | 17/19 | 1445041 | 1445041 | 337.7 | 337.7 |
| eval-28-convert-from-methodsource | 18/25 | 16/18 | 986014 | 986014 | 330.2 | 330.2 |
| eval-29-shopping-cart-tt | 26/29 | 20/23 | 968444 | 968444 | 322.4 | 322.4 |
| eval-30-order-splitting-tt | 19/24 | 19/20 | 3329150 | 3329150 | 664.9 | 664.9 |

## Per-Eval Results

### ✅ Eval eval-1-convert-repetitive-tests

**13/13** · 438137 tokens · 48254ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Discount?' or 'Expected?')
- ✅ **has-three-data-rows**: Table has exactly 3 data rows (GOLD, SILVER, BRONZE)
- ✅ **single-assertion-in-method**: Each @TableTest method applies one uniform assertion pattern to every row — not different assertions per scenario. Several unconditional assertions checking distinct observable outputs of one result (e.g. success, message, resulting cart) count as one uniform pattern; the anti-pattern is branching on the row (if/switch/ternary/try-catch) to choose what is asserted.
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'discountByCustomerTier' → 'Discount By Customer Tier'). Not a generic name like 'test1' or 'testMethod'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express — such as fixed values shared by all rows, where/when the rule applies, or open questions. Does NOT merely restate the column names or summarise what the rows show.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **has-tabletest-dependency**: The build file (pom.xml) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against DiscountService

### ✅ Eval eval-2-parse-dates

**15/15** · 815800 tokens · 115205ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell (not the string 'null') in the table
- ✅ **exception-handled-cleanly**: The empty string / exception case is handled — either via a Throws? column, assertThrows inside the method, or a separate @TableTest — rather than being silently omitted
- ✅ **localdate-result-column**: There is a result column typed as LocalDate (or using a string representation that maps to LocalDate)
- ✅ **type-conversion-addressed**: The response addresses type conversion for non-trivial column types — either by providing a @TypeConverter/converter method, or by using a cell representation that TableTest's built-in conversion handles
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'dateParsing' → 'Date Parsing'). Not a generic name like 'test1' or 'testMethod'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express — such as where/when the rule applies, or open questions
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **empty-string-uses-quotes**: Empty string input uses quoted syntax (e.g. '' or "") in the table — not a blank cell, which represents null. The distinction between empty string and null is preserved.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns
- ✅ **separates-valid-and-invalid**: Valid parsing cases and error/exception cases are in separate @TableTest methods
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-7-permission-check

**12/13** · 585941 tokens · 59895ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **uses-value-sets**: Table uses value-set syntax (curly braces like {READ, WRITE} or {READ, WRITE, DELETE}) to express rules that hold across multiple actions — rather than one row per role/action combination
- ✅ **fewer-than-nine-rows**: Table has fewer than 9 data rows (i.e. does not enumerate every role/action combination individually)
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ❌ **no-duplicate-role-output**: No two rows share both the same Role value and the same boolean output
  > Row 6 (Guest cannot write | GUEST | WRITE | false) and Row 7 (Guest cannot delete | GUEST | DELETE | false) both have GUEST role and false outcome
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'permissionsByRoleAndAction' → 'Permissions By Role And Action'). Not a generic name like 'test1' or 'canPerform'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond table rows
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-8-money-parse

**14/15** · 606672 tokens · 87611ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell in the valid inputs @TableTest table (not the string 'null', and not as a separate @Test method). The blank cell maps to a null parameter.
- ✅ **exception-has-expected-column**: The exception/invalid-input table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row
- ✅ **exception-cases-handled**: The exception cases (empty string, letters-only, negative) are handled — via a Throws? column, assertThrows in the method body, or a separate @TableTest
- ✅ **result-column-with-question-mark**: Valid inputs have a result column name ending with '?' (e.g. 'Money?' or 'Result?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'moneyParsing' → 'Money Parsing'). Not a generic name like 'test1' or 'testMethod'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations
  > Valid table has only 3 rows (10.00, 0.01, null). Spec requires rows exploring BigDecimal scale/format: e.g., '5' vs '5.00', '10.5', '10.005', '0.00' boundary case, alternative formats (whitespace, +sign, etc.). Current rows do not expose these underspecified assumptions.
- ✅ **separates-valid-and-invalid**: Valid parsing cases and error/exception cases are in separate @TableTest methods
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ✅ Eval eval-9-bonus-contractor-structure

**13/13** · 553264 tokens · 82396ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **contractor-uses-value-set**: CONTRACTOR row uses a value set for Department (e.g. {SALES, ENGINEERING}) to express 'regardless of department' — not a dummy placeholder like 'ANY' or 'N/A', and not enumerated as separate rows.
- ✅ **four-core-rules-covered**: The four level×department combinations (SENIOR+SALES, SENIOR+ENGINEERING, JUNIOR+SALES, JUNIOR+ENGINEERING) are all present in the table(s)
- ✅ **expects-bonus-percentage**: The expected output column contains bonus percentages (e.g. 15%, 12%, 0%) — not bonus amounts. The method returns a percentage, and the table should reflect that.
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'bonusByLevelAndDepartment' → 'Bonus By Level And Department'). Not a generic name like 'test1' or 'testMethod'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express — such as how bonus rates are applied (e.g. as percentage of base salary), or open questions. Does NOT merely restate the column names or summarise what the rows show.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Senior in Sales', 'Contractor regardless of department') — not outcomes ('Gets 15%', 'No bonus').
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Level', 'Department', 'Bonus %') — not implementation terms like 'employeeLevel', 'deptCode', 'result'.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-14-weekly-pay

**16/21** · 1307679 tokens · 264939ms

- ✅ **1.2-error-has-expected-column**: Negative rate rejection has an expected-exception column.
- ✅ **1.3-depth-overtime-boundary**: The 40/41 pair shows where overtime starts (at 40, just over at 41).
- ✅ **1.4-depth-combined-scenario**: An all-bands row shows the bands compose rather than shadow each other.
- ✅ **1.5-depth-error-edge-cases**: Negative hours edge case is made explicit in a row, not silently chosen.
- ❌ **1.6-readability-empty-cells**: Blank cells (not 0) mark absent bands; parameters are Integer/Double, not int/double.
  > All parameters are primitive double, not Integer/Double. Tables use explicit 0 values (e.g., 'Sunday Hours | 0') rather than blank cells to denote absent bands. This prevents the readability pattern described.
- ✅ **1.7-readability-scenario-names**: Scenario names describe work patterns (e.g. 'Part-time', 'Five hours overtime', 'Full Sunday shift') — not 'Test case 1' or expected outcomes
- ✅ **1.8-correctness-expected-values**: Arithmetic is internally consistent with the rules the rows state.
- ❌ **1.9-correctness-value-set-semantics**: Zero rate is tested over several hour shapes (value set), all collapsing to 0.
  > Only one row tests zero rate: 'Zero hourly rate is allowed | 40 | 5 | 0 | 0.00 | 0.00'. A value set over multiple hour patterns (e.g., {0, 40, 50} weekday hours all at rate 0) would be stronger but is not present.
- ✅ **1.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ❌ **1.11-format-description**: @Description provides context beyond what the table rows already express and does NOT restate the rate multipliers or overtime threshold
  > @Description states 'Overtime threshold is fixed at 40 hours/week; hours beyond it are paid at 1.5x' and 'Sunday hours and holiday hours are both always paid at double time (2x base rate)' — restating multipliers already visible in rows
- ✅ **1.12-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **1.13-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **1.14-depth-zero-rate**: Zero rate is allowed and yields zero pay—shown as an explicit row.
- ✅ **1.15-format-clean-method**: The test method body contains no if/ternary null-handling or parsing logic.
- ✅ **business-language-columns**: Column names use domain/business language, not implementation terms.
- ✅ **concerns-decomposed**: Multiple @TableTest methods address distinct concerns, not one monolithic table.
- ✅ **minimal-rows-per-concern**: Each table has only rows needed to express its concern—no unnecessary permutations.
- ❌ **separates-classification-and-calculation**: Payable hours categorisation and pay calculation are in separate @TableTest methods.
  > The test file does not separate classification (which hours fall into which band) from calculation (hours × rate). Instead, it jumps directly to pay amounts. No table shows '41 weekday hours → 40 regular + 1 overtime' as a classification step; the 41-hour row shows only the final pay (830.00).
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-15-reis-discount

**25/27** · 2287509 tokens · 411343ms

- ✅ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
- ✅ **2.2-children-flat-discount**: Children's flat 20% discount is represented — either as a row in the eligibility table or a separate note.
- ✅ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers derived from the 'every fifth trip' rule, with boundary values or value sets showing which ticket counts produce which discount. The maximum 40% discount is represented.
- ✅ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
- ❌ **window-boundary-uses-purchase-time**: The 30-day window is exercised at time-of-purchase granularity, not whole days only. At least one pair of rows must distinguish just-inside from just-outside by less than a day.
  > countSingleTicketsInWindow uses only whole-day boundaries (30 vs 31 days). The @TypeConverter parses 'daysAgo' as integers and subtracts whole days: 'PURCHASE_TIME.minusDays(daysAgo)'. No sub-day granularity is tested.
- ✅ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
- ✅ **2.15-ticket-count-uses-value-sets**: The discount ladder table uses value sets for the ticket count column to group counts within the same tier — not one row per boundary value.
- ✅ **2.16-no-duplicate-tier-mapping**: The tier-to-discount mapping is expressed once — not repeated across multiple tables.
- ✅ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ✅ **2.11-format-description**: @Description is present on at least the class or the tier method and provides context beyond what the table rows already express.
- ✅ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **2.14-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **2.17-zone-irrelevance-visible**: The eligibility/passenger-type table includes a Zone column (or equivalent) with a value set to show that the discount applies regardless of zone.
  > discountForCategoryAndTripCount table has no Zone column. countSingleTicketsInWindow tests zone-independence but that is the counting table, not the eligibility table. The eligibility table should explicitly show zone irrelevance.
- ✅ **zone-independent-counting**: The rolling-window counting concern shows by row that a past purchase's zone does not affect whether it counts toward the travel count — for example history entries whose zones differ (or a zone value set) reaching the same count.
- ✅ **2.18-adult-senior-value-set**: Adult and senior are expressed as a value set {ADULT, SENIOR} (or equivalent) in at least one row of the eligibility table.
- ✅ **2.19-depth-all-tiers**: All 9 discount tiers are represented in the ladder table: 0% (tickets 1-4), 5% (5-9), 10% (10-14), 15% (15-19), 20% (20-24), 25% (25-29), 30% (30-34), 35% (35-39), and 40% (40+).
- ✅ **2.20-readability-one-row-per-tier**: Each tier in the discount ladder is expressed as a single row with a value set for ticket counts — not split across multiple rows.
- ✅ **2.21-readability-relative-time**: The rolling window table expresses time relatively (e.g. '14 days ago', '30 days ago', or an integer 'Days ago' column) rather than using absolute dates.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. Expectation columns must vary and not be verbatim copies of inputs.
- ✅ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body. No threshold or operation should appear only in method body or field.
- ✅ **title-states-system-behaviour**: Each @DisplayName states what the code under test does, not an external fact it depends on.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-18-convert-from-code

**20/26** · 826034 tokens · 148196ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **black-box-columns**: Table columns represent the method's public inputs (applicant type, age, claim count) and observable outputs (decision, premium) — not internal state like hasActivePolicy, internalRiskScore, or calculateRiskScore.
  > The second @TableTest @Description states: 'Risk score is age/10 + claimCount * 15' and 'score exceeds 75', exposing the internal formula and threshold logic.
- ✅ **observable-io-only**: No column is named after private fields or internal variables (hasActivePolicy, internalRiskScore, riskScore). The test treats the method as a black box.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Applicant type', 'Age', 'Claims', 'Decision?', 'Premium?') — not code identifiers like 'applicantType', 'claimCount', 'String'.
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Renewal with no claims', 'Senior applicant') — not outcomes ('Auto approved', 'Rejected').
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Decision?' or 'Premium?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.
- ❌ **separates-decision-and-premium**: Decision logic (AUTO_APPROVED/REJECTED/APPROVED) and premium calculation are in separate @TableTest methods
  > The second @TableTest 'rejectsApplicationsAboveRiskThreshold' includes both Status? and Premium? columns in the same table, mixing decision and premium concerns.
- ✅ **no-reimplemented-internals**: Test method body does not recompute internal formulas (e.g. risk score calculation). It only calls the public API (evaluateApplication) and asserts on the return value's fields.
- ❌ **description-no-internals**: @Description explains the test's purpose and business context — not internal formulas or implementation details.
  > Second @Description states: 'Risk score is age/10 + claimCount * 15. Applications are rejected once the score exceeds 75.' This exposes internal formula and threshold.
- ❌ **depth-decision-boundaries**: Approval decision table covers the rejection boundary thoroughly: 5 claims triggers rejection regardless of realistic age (risk score crosses 75), 4 claims results in approval. Rows at 4 and 5 claims with varying ages demonstrate this cliff.
  > No row with 4 claims appears in any table. The second table shows 5 claims at ages 9 and 10, but lacks the critical 4-claim boundary row that would prove the cliff.
- ❌ **depth-premium-boundaries**: Approved premium table covers boundary effects: the premium jump from 0 to 1 claim (impact of +15 risk score on premium), and the premium difference at age 64 vs 65 (standard vs senior formula). These boundary conditions document how premium changes at each threshold.
  > The premium table `calculatesPremiumByAgeTier` shows age 64 vs 65 boundary (rows 1-2: 142.00 vs 273.50), but does NOT isolate the 0-to-1 claim impact. Row 3 (age 30, 0 claims, 106.00) and row 4 (age 90, 0 claims, 231.50) both have 0 claims; no row pair holds age constant while varying claims 0→1.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. Judge each @TableTest table independently; the assertion FAILS if any table meets any of these conditions: (1) its expectation column holds the same value in every row; (2) in every row, each expectation cell is a verbatim copy of a cell in an input column of the same row; (3) an implementation that ignored the input columns entirely and returned a fixed value would satisfy every row. Otherwise the assertion PASSES.
- ❌ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body. The assertion FAILS if any of these hold for any @TableTest: (1) a value needed to predict the expectation (a threshold, rate, cutoff, multiplier, reference date) appears only in the method body or a field, and in neither a column, the @DisplayName, nor the @Description; (2) the operation applied to the inputs cannot be named from the column headers and cell values alone; (3) a helper method used in the assertion applies a comparison criterion (ordering, tolerance, subset matching) that is stated nowhere in the table, title, or description. Otherwise the assertion PASSES.
  > The `rejectsApplicationsAboveRiskThreshold` @Description states 'Risk score is age/10 + claimCount * 15' and 'exceeds 75', exposing the internal formula and threshold. Per spec, 'the risk score is internal...must **not** appear as a column or in `@Description` — the reader sees its effect through the input/output rows, not the arithmetic.'
- ✅ **title-states-system-behaviour**: Each @DisplayName (or the method name it falls back to) states what the code under test does, not an external fact it depends on. Apply the check mechanically: strike the system under test from the sentence — if it still reads as true, because it merely restates a regulation, standard, format, or domain fact, the title is about something else and fails.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against InsuranceEvaluator

### ⚠️ Eval eval-20-collections-and-quoting

**16/17** · 1242601 tokens · 325555ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **list-syntax-correct**: List values in the table use bracket syntax like [tech:java, biz:sales, dev:ci] — not comma-separated strings without brackets.
  > ["tech:java", "dev:python", "biz:sales", "ops:deploy"] uses quoted strings inside brackets, but the assertion requires unquoted syntax [tech:java, biz:sales, dev:ci] which would be a parse error. The response uses correct quoting instead.
- ✅ **empty-list-explicit**: Empty list input uses [] (not a blank cell, which represents null). The distinction between empty list and null is preserved.
- ✅ **special-chars-quoted**: Values containing pipes (|), brackets, or colons (:) are properly quoted (e.g. "tech:java") so they don't conflict with table syntax.
- ✅ **set-syntax-correct**: Set<String> values in the table use curly brace syntax like {tech, dev} — not bracket syntax [tech, dev] which is for lists.
- ✅ **newline-in-cell**: A cell value containing a newline does not break the table row structure: the newline is either escaped as \n inside the cell, or the value is expressed as a list of lines joined in the test body.
- ✅ **pipe-quoted**: Values containing pipe characters (|) are quoted so they don't conflict with the markdown table column separator.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Filtered tags?' or 'Result?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **no-blank-collection-elements**: No collection value in any table contains a blank element. `[a, , c]`, `[a, b, ]` and `[, a, b]` are parse errors, not collections holding a null element.
- ✅ **empty-string-element-quoted**: An empty tag inside a tag list is written as a quoted empty string ("" or ''), not as a blank element.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-22-event-registration-tt

**27/28** · 573142 tokens · 153979ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **validation-rules-covered**: Email validation and name-required error scenarios are present — at least one row for invalid email and one for missing name.
- ✅ **blank-for-absent-optional**: Dietary requirements and/or accessibility needs use blank cells (null) when not provided — not 'N/A' or 'none'.
- ✅ **blank-vs-value-set-correct**: When testing pricing rules, irrelevant inputs use value sets or representative values — not blanks.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Result?', 'Price?')
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Name', 'Email', 'Registration date', 'Group size', 'Price?') — not code identifiers.
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Early-bird single attendee', 'Missing name') — not outcomes ('Rejected', '20% off').
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations.
- ✅ **separates-validation-and-pricing**: Input validation and pricing/discount calculation are in separate @TableTest methods.
- ✅ **validation-includes-optional-fields**: Validation @TableTest includes Dietary Requirements and Accessibility Needs columns, with rows showing both null and non-null values are accepted.
- ✅ **descriptive-registration-date**: Registration date uses descriptive values (e.g. 'before cutoff', 'on cutoff', 'after cutoff') with a @TypeConverter method converting to actual dates.
- ✅ **cutoff-date-column-if-literal-dates**: If registration dates are literal (e.g. '2025-02-28' instead of descriptive), the early-bird cutoff date appears as a separate policy column so the reader can see both dates and verify the comparison. Passes automatically if descriptive date values are used.
- ✅ **description-no-irrelevant-information**: If @Description is present, it does not include information already visible in the table columns or derivable from the table structure — such as fixed input values that could be columns, or restating a rule the rows already demonstrate. It PASSES when the description adds what the rows cannot: a threshold or constant fixed in the method body, currency, where/when the rule applies, or naming an interpretation as a deliberate assumption.
- ✅ **discount-column-preferred**: Output column is 'Discount?' (percentage or amount off) rather than 'Price?' — making verification simpler (discount is the rule's direct output; price requires knowing the base price). If 'Price?' is used instead, both 'Base price' and 'Price?' columns must be present so the reader can trace the calculation.
- ✅ **single-assertion-in-method**: Each @TableTest method applies one uniform assertion pattern to every row — not different assertions per scenario. Several unconditional assertions checking distinct observable outputs of one result (e.g. success, message, resulting cart) count as one uniform pattern; the anti-pattern is branching on the row (if/switch/ternary/try-catch) to choose what is asserted.
- ✅ **optional-fields-has-expected-column**: The optional-fields acceptance table includes an expectation/output column (e.g. 'Approved?', 'Result?', or 'Status?') asserting that each combination is accepted — not just varying inputs without any expected column.
- ✅ **description-no-redundant-field-values**: Scenario names and @Description text do not restate values the table already shows. The assertion FAILS if either: (1) a scenario name contains a literal value that also appears in an input cell of the same row (e.g. scenario 'Age 35, score 700' beside Age and Credit score columns holding 35 and 700); or (2) @Description states a fixed value for an input that is already a column in that table. Otherwise it PASSES.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. Judge each @TableTest table independently; the assertion FAILS if any table meets any of these conditions: (1) its expectation column holds the same value in every row; (2) in every row, each expectation cell is a verbatim copy of a cell in an input column of the same row; (3) an implementation that ignored the input columns entirely and returned a fixed value would satisfy every row. Otherwise the assertion PASSES.
- ✅ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body. The assertion FAILS if any of these hold for any @TableTest: (1) a value needed to predict the expectation (a threshold, rate, cutoff, multiplier, reference date) appears only in the method body or a field, and in neither a column, the @DisplayName, nor the @Description; (2) the operation applied to the inputs cannot be named from the column headers and cell values alone; (3) a helper method used in the assertion applies a comparison criterion (ordering, tolerance, subset matching) that is stated nowhere in the table, title, or description. Otherwise the assertion PASSES.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-23-loan-approval-tt

**21/22** · 540843 tokens · 149866ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **threshold-verifiable-from-table**: Each policy threshold (650 for non-seniors, 600 for seniors) is verifiable from the table, in one of two ways: (a) a dedicated policy column (e.g. 'Credit threshold') beside the applicant's score, or (b) boundary rows that bracket it — within one age band, a row that qualifies just above the threshold and a row that fails at/below it, carrying different decisions.
- ✅ **concrete-domain-values**: Cell values use concrete domain terms (e.g. '70', '650', 'APPROVED') — not abstract codes like 'CATEGORY_A' or '1'. Boolean true/false is acceptable for yes/no flags when the parameter type is boolean.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Senior at lower threshold', 'Missing income') — not outcomes ('Approved', 'Rejected').
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Age', 'Credit score', 'Stable income', 'Decision?') — not code identifiers like 'customerAge', 'boolean'.
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Decision?' or 'Status?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **concerns-decomposed**: The distinct concerns (age→threshold, credit boundary, income status) are each covered, without a monolithic table that re-proves unrelated rules through redundant permutations. A single table is acceptable when the concerns share the same input columns and each is covered by only a row or two.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ✅ **covers-age-credit-income**: Age boundary policy, credit score categorisation, and income status are each covered by minimal scenarios. This may be one @TableTest whose rows exercise each concern (boundary pairs for age/credit, income held-constant rows for income status) or separate methods per concern with a combining verdict — both are correct given only a row or two per concern.
- ✅ **description-no-redundant-field-values**: Scenario names and @Description text do not restate values the table already shows. The assertion FAILS if either: (1) a scenario name contains a literal value that also appears in an input cell of the same row (e.g. scenario 'Age 35, score 700' beside Age and Credit score columns holding 35 and 700); or (2) @Description states a fixed value for an input that is already a column in that table.
- ✅ **depth-stable-income-effect**: The effect of stable income on the outcome is visible from table rows, not merely implied. The assertion PASSES if some single @TableTest table contains two rows that differ in the stable-income column (or equivalent) while holding the credit score and age columns equal, and those two rows carry different expected decisions.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. Judge each @TableTest table independently; the assertion FAILS if any table meets any of these conditions: (1) its expectation column holds the same value in every row; (2) in every row, each expectation cell is a verbatim copy of a cell in an input column of the same row; (3) an implementation that ignored the input columns entirely and returned a fixed value would satisfy every row.
- ✅ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body.
- ❌ **title-states-system-behaviour**: Each @DisplayName states what the code under test does, not an external fact it depends on.
  > The @Description states 'Standard applicants are approved above a credit score of 650; senior applicants (age 65+) are approved above a lower threshold of 600.' Striking 'the code under test' leaves 'applicants are approved above 650/600' — a domain rule, not what the evaluator does. The description restates policy thresholds already carried by the table rows.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-25-convert-from-spock

**21/26** · 3006845 tokens · 514486ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells. Rows with no options use a blank cell or [:].
  > The test uses `Map<String, String>?` parameters with blank cells (null), not `[:]` empty maps. The response states 'blank cells resolve straight to null before any custom converter runs, bypassing it entirely' and uses a helper instead of a converter on every row.
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
  > No @TypeConverter method is present. The response states 'blank cells resolve straight to null before any custom converter runs' and uses a private `buildOptions` helper instead of a converter to handle null-to-default logic.
- ✅ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table — not as three separate length/width/height columns
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
- ✅ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern — not one monolithic table mixing base rates, surcharges, dimensional weight, and carrier equivalence
- ❌ **concern-not-over-split**: Do not fragment a single concern across multiple @TableTest methods that share the same fixture and assertion and differ only in which one sub-rule they exercise.
  > Surcharges are split across 4 separate methods (oversizeSurcharge, hazmatHandlingSurcharge, fragileSurcharge, insuranceSurcharge) with identical fixture (EU standard, 3.0kg, 30x20x15) and same assertion type, each varying one option. Should be one table.
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. An implementation that ignored the input columns entirely and returned a fixed value would not satisfy every row.
- ✅ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body. Thresholds, rates, cutoffs, multipliers, and comparison criteria must appear in columns, @DisplayName, or @Description.
- ✅ **numeric-types-correct**: Parameter types correspond to the ShippingCostCalculator.calculateShippingCost signature: weight is a double, dimensions are a List<Integer>, and cost / insured value are BigDecimal (compared via compareTo, not equals).
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('EU express, light package', 'Oversized with fragile') — not outcomes ('8.00', 'Surcharge applied') or generic labels ('Test 1').
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ✅ **business-language-columns**: Column names use domain language ('Zone', 'Weight (kg)', 'Dimensions', 'Options', 'Cost?') — not code identifiers ('shippingZone', 'weightKg', 'dims')
- ✅ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **output-is-kotlin**: The generated test file is written in Kotlin (.kt), not Java or Groovy. Uses Kotlin syntax: fun, val/var, Kotlin imports.
- ✅ **no-groovy-syntax**: Output contains no Groovy or Spock syntax: no def, where:, expect:, given:, GString interpolation (#variable), Spock assertions (thrown(), old()), or Groovy list literals ([] as Set).
- ✅ **has-tabletest-dependency**: The build file (build.gradle.kts) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against ShippingCostCalculator
- ✅ **spock-dependency-removed**: The build file no longer declares Spock or Groovy dependencies — the old framework is fully removed, not just the test code.

### ⚠️ Eval eval-26-convert-from-kotest

**20/26** · 1443172 tokens · 362383ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells. Rows with no options use a blank cell or [:].
  > appliesSurchargesToBaseCost uses three separate columns: 'Fragile | Insured Value | Handling' instead of a single 'Options' map column. No [:].
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
  > No @TypeConverter method is present in the test file. PackageOptions is constructed manually in appliesSurchargesToBaseCost: 'val opts = PackageOptions(...)'
- ✅ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table — not as three separate length/width/height columns
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
- ✅ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern — not one monolithic table mixing base rates, surcharges, dimensional weight, and carrier equivalence
- ✅ **concern-not-over-split**: Do not fragment a single concern across multiple @TableTest methods that share the same fixture and assertion and differ only in which one sub-rule they exercise.
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. Expectation column does not hold the same value in every row; cells are not verbatim copies of input columns; implementation cannot return a fixed value.
- ✅ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body. No threshold, rate, cutoff, multiplier, or reference date appears only in method body or field.
- ✅ **numeric-types-correct**: Parameter types correspond to ShippingCostCalculator.calculateShippingCost signature: weight is a double, dimensions are a List<Integer>, and cost / insured value are BigDecimal (compared via compareTo, not equals).
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('EU express, light package', 'Oversized with fragile') — not outcomes ('8.00', 'Surcharge applied') or generic labels ('Test 1').
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ❌ **business-language-columns**: Column names use domain language ('Zone', 'Weight (kg)', 'Dimensions', 'Options', 'Cost?') — not code identifiers ('shippingZone', 'weightKg', 'dims').
  > appliesSurchargesToBaseCost uses code-style columns 'Fragile | Insured Value | Handling' instead of domain-language 'Options' map. Also 'Base Rate?' and 'Total Cost?' are correct, but the three separate option columns violate this.
- ✅ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **output-is-kotlin**: The generated test file is written in Kotlin (.kt), not Java. Uses Kotlin syntax: fun, val/var, Kotlin imports.
- ✅ **no-kotest-syntax**: Output contains no Kotest syntax: no forAll, row(), withData, shouldBe, context(), FunSpec, or Kotest imports.
- ✅ **has-tabletest-dependency**: The build file (build.gradle.kts) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against ShippingCostCalculator
- ✅ **kotest-dependency-removed**: The build file no longer declares Kotest dependencies — the old framework is fully removed, not just the test code.

### ⚠️ Eval eval-27-convert-from-testng

**19/25** · 1445041 tokens · 337704ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells. Rows with no options use a blank cell or [:].
  > The test methods use separate boolean/BigDecimal/String parameters (e.g., `boolean fragile`, `BigDecimal insuredValue`, `String handling`) instead of a single map column. No `[:]` or map syntax appears in any table.
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
  > No @TypeConverter method is present in the test class. Options are constructed manually in the test body (e.g., `options.setFragile(fragile)`) rather than converted from a map.
- ✅ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table — not as three separate length/width/height columns
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
- ✅ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern — not one monolithic table mixing base rates, surcharges, dimensional weight, and carrier equivalence
- ❌ **concern-not-over-split**: Do not fragment a single concern across multiple @TableTest methods that share the same fixture and assertion and differ only in which one sub-rule they exercise. FAILS when two or more such same-fixture, single-sub-rule tables exist for one concern.
  > Surcharges are split across 6 separate methods (appliesOversizeSurchargeWhenAnyDimensionExceedsLimit, appliesFragileSurcharge, appliesInsurancePremiumWithMinimum, appliesHazmatHandlingFee, plus combinesSurchargesInOrder). Each fixes zone EU standard, weight 3.0kg, and varies only one surcharge option, violating the 'one table per concern' rule for surcharges.
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. An implementation that ignored the input columns entirely and returned a fixed value would not satisfy every row.
- ✅ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body. Fails if a value needed to predict the expectation appears only in the method body or a field, not in a column, @DisplayName, or @Description.
- ✅ **numeric-types-correct**: Parameter types correspond to the ShippingCostCalculator.calculateShippingCost signature: weight is a double, dimensions are a List<Integer>, and cost / insured value are BigDecimal (compared via compareTo, not equals).
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('EU express, light package', 'Oversized with fragile') — not outcomes ('8.00', 'Surcharge applied') or generic labels ('Test 1').
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ✅ **business-language-columns**: Column names use domain language ('Zone', 'Weight (kg)', 'Dimensions', 'Options', 'Cost?') — not code identifiers ('shippingZone', 'weightKg', 'dims')
- ✅ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **no-testng-artifacts**: Output contains no TestNG syntax: no @DataProvider, @Test(dataProvider=...), Object[][], or TestNG imports (org.testng.*).
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against ShippingCostCalculator
- ✅ **testng-dependency-removed**: The build file no longer declares a TestNG dependency — the old framework is fully removed, not just the test code.

### ⚠️ Eval eval-28-convert-from-methodsource

**18/25** · 986014 tokens · 330205ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells. Rows with no options use a blank cell or [:].
  > The test uses a helper method `options(boolean fragile, BigDecimal insuredValue, String handling)` to construct PackageOptions objects. No map column or @TypeConverter is present; options are built programmatically in the method body, not declared in the table.
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
  > No @TypeConverter method is present in the test class. The `options()` helper constructs PackageOptions manually; there is no converter to transform map cells into domain objects.
- ✅ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table — not as three separate length/width/height columns
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
- ✅ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern — not one monolithic table mixing base rates, surcharges, dimensional weight, and carrier equivalence
- ❌ **concern-not-over-split**: Do not fragment a single concern across multiple @TableTest methods that share the same fixture and assertion and differ only in which one sub-rule they exercise. FAILS when two or more such same-fixture, single-sub-rule tables exist for one concern (e.g. a separate table per surcharge). PASSES when each @TableTest addresses a genuinely distinct concern with its own inputs.
  > Surcharges are split across 6 separate tables (shouldAddOversizeSurchargeWhenAnyDimensionExceedsThreshold, shouldApplyFragileMultiplier, shouldAddInsurancePremiumWithMinimum, shouldAddHazmatHandlingFee, shouldCombineFragileMultiplierWithInsurancePremium, and a partial hazmat in shouldApplyFragileMultiplier). Each fixes zone EU standard, weight 3.0kg, and varies only one surcharge option, violating the constraint that surcharges should be one table.
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. An implementation that ignored the input columns entirely and returned a fixed value would satisfy every row only if all three conditions hold: (1) expectation column holds the same value in every row; (2) in every row, each expectation cell is a verbatim copy of a cell in an input column of the same row; (3) an implementation that ignored the input columns entirely and returned a fixed value would satisfy every row.
- ❌ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body. FAILS if a value needed to predict the expectation (threshold, rate, cutoff, multiplier, reference date) appears only in the method body or a field, and in neither a column, the @DisplayName, nor the @Description.
  > The oversize threshold (100cm) is not stated in the table or @Description; it appears only in the method name 'ExceedsThreshold' and the test logic. The insurance floor ($3.00) and percentage (0.6%) are stated in @Description but not in the table columns themselves. The fragile multiplier (1.15) is not stated anywhere in the table or description.
- ✅ **numeric-types-correct**: Parameter types correspond to the ShippingCostCalculator.calculateShippingCost signature: weight is a double, dimensions are a List<Integer>, and cost / insured value are BigDecimal (compared via compareTo, not equals).
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('EU express, light package', 'Oversized with fragile') — not outcomes ('8.00', 'Surcharge applied') or generic labels ('Test 1').
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ✅ **business-language-columns**: Column names use domain language ('Zone', 'Weight (kg)', 'Dimensions', 'Options', 'Cost?') — not code identifiers ('shippingZone', 'weightKg', 'dims').
- ✅ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **no-methodsource-artifacts**: Output contains no @MethodSource syntax: no @MethodSource, Stream<Arguments>, Arguments.of, or JUnit Jupiter parameterized imports (org.junit.jupiter.params.provider.*).
- ✅ **has-tabletest-dependency**: The build file (pom.xml) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against ShippingCostCalculator

### ⚠️ Eval eval-29-shopping-cart-tt

**26/29** · 968444 tokens · 322446ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **concerns-decomposed**: Multiple @TableTest methods are used, each addressing a distinct concern — not one monolithic table mixing item operations, coupon logic, total calculation, and checkout.
- ✅ **minimal-rows-per-concern**: Each @TableTest has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.
- ✅ **separates-item-coupon-total-checkout**: Item operations (add/remove), coupon application (validity and replacement), cart total calculation (coupon type effects on price), and checkout (stock verification) are in separate @TableTest methods.
- ✅ **rows-independently-executable**: Each table row is independently executable — no row depends on a prior row's result. The table expresses rules, not a sequential test script. Each row specifies its own preconditions.
- ✅ **coupon-before-after-columns**: Coupon application table uses before/after columns (e.g. 'Active coupon' / 'Active coupon after?') to specify the state change when entering, replacing, or entering invalid coupon codes.
- ✅ **coupon-validity-not-coupon-types**: Coupon application table tests validity and replacement (valid code, expired code, nonexistent code, replacing active coupon) — not coupon type effects on price. Coupon type effects belong in the cart total table.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Message?', 'Total?', 'Active coupon after?')
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Add to empty cart', 'Replace active coupon with expired') — not outcomes ('Error', 'Success').
- ❌ **business-language-columns**: Column names use domain/business language (e.g. 'Product', 'Quantity', 'Message?', 'Cart total?') — not code identifiers like 'productId', 'result'.
  > Column 'Product Id' uses code-ism 'Id' where cell values are product names (Widget); should be 'Product'. Also 'Cart Items' vs 'Cart Before' inconsistency.
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **single-assertion-in-method**: Each @TableTest method applies one uniform assertion pattern to every row — not different assertions per scenario. Several unconditional assertions checking distinct observable outputs of one result (e.g. success, message, resulting cart) count as one uniform pattern; the anti-pattern is branching on the row (if/switch/ternary/try-catch) to choose what is asserted.
- ✅ **test-data-visible**: Product prices and coupon rules (type, amount, expiry status) are visible in the table via columns, @Description, or explicit setup — not silently hardcoded. The reader can trace how inputs lead to outputs.
- ✅ **type-converters-for-complex-objects**: TypeConverter methods are used to convert map/string table values into domain objects (e.g. Map → Cart, Map → InventoryService, map → Coupon). Test method bodies contain only arrange-act-assert — no object construction from raw table values.
- ✅ **coupon-as-single-column**: Coupon data is expressed in a single column with a @TypeConverter — not spread across separate sparse columns for coupon type, value, and target product.
- ✅ **uses-standard-map-syntax**: Columns representing maps use standard TableTest map syntax — [k: v, k2: v2] for entries and [:] for empty map — not a custom bracket notation.
- ✅ **description-not-redundant-with-scenarios**: @Description annotations do not merely repeat information that the scenario names already convey.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable.
- ✅ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body.
- ✅ **title-states-system-behaviour**: Each @DisplayName (or the method name it falls back to) states what the code under test does, not an external fact it depends on.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-30-order-splitting-tt

**19/24** · 3329150 tokens · 664880ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **concerns-decomposed**: Multiple @TableTest methods are used, each addressing a distinct concern — not one monolithic table mixing all splitting rules.
  > Only 4 @TableTest methods present: fulfillment/address, availability, warehouse, companion. The spec requires 5 concerns (fulfillment type, delivery address, availability, warehouse allocation, companion grouping). Fulfillment type and delivery address are combined in one method, which is permitted ('either reads as a clean split'), but the response claims '4 focused @TableTest methods (19 rows total), one per splitting concern' — this is inaccurate. The actual count is 4, not 5.
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. Each @TableTest table independently must not have: (1) expectation column with same value in every row; (2) every expectation cell a verbatim copy of an input cell; (3) fixed return value satisfying all rows.
- ✅ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body. Fails if: (1) a threshold/rate/cutoff appears only in method body; (2) operation cannot be named from headers/values; (3) helper comparison criterion stated nowhere.
- ❌ **minimal-rows-per-concern**: Each @TableTest has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.
  > Fulfillment/address table has 6 rows; spec obligations are (a) same type+address groups, (b) different address splits, (c) different fulfillment type splits, (d) pickup with no address groups. Row 6 ('Delivery split by address, plus a separate pickup item') combines (b)+(c), which is optional but adds a permutation. Availability table has 6 rows for 3 core obligations (a) all in-stock, (b) in-stock not held, (c) delayed items group; rows 2 and 3 separately test 'not held for backordered' and 'not held for pre-ordered', which under the two-state model are redundant — spec notes this as 'mild over-coverage'.
- ✅ **concern-fulfillment-method**: Fulfillment method splitting is represented in a @TableTest — its own, or one shared with the delivery-address split — showing that items with different fulfillment types (store pickup vs home delivery) cannot share a shipment.
- ✅ **concern-delivery-address**: Delivery address splitting is represented in a @TableTest — its own, or one shared with the fulfillment-method split — showing that items going to different addresses must be in separate shipments.
- ✅ **concern-availability**: Availability splitting is represented as its own @TableTest — in-stock items ship immediately, backordered/pre-ordered items ship when available.
- ✅ **concern-warehouse-allocation**: Warehouse allocation (minimising shipments) is represented as its own @TableTest — choosing which warehouses to ship from to minimise total shipment count.
- ✅ **concern-companion-products**: Companion product grouping is represented as its own @TableTest — paired items (e.g. camera body and lens) should ship together from the same location when possible.
- ✅ **all-outputs-same-table**: Each @TableTest method includes all output columns for its concern in the same table — e.g. warehouse allocation includes both the assignment and shipment count, not split across methods.
- ❌ **native-collection-output**: A compound output — a collection of items, or items keyed by a tag — is expressed as a native TableTest list, map, or set (possibly nested, e.g. [[a, b], [c]] or [W1: [a, b]]), not as a hand-rolled string that encodes the structure with embedded brackets or colons (e.g. "W1:[a,b]") and is assembled or parsed by a helper. FAILS when an expectation cell packs multiple values into one quoted scalar that the test builds via a stringifying helper. PASSES when the output column is a native collection — sets for order-independent semantics, or ordered lists/maps with a canonical sort. Scalar outputs (a number, an enum, or a single message) are exempt.
  > Expected shipments use string encoding like ["W1:[camera,lens,mic]"] and ["IMMEDIATE:[camera,lens]"] built by describeByWarehouse/describeByAvailability helpers, not native TableTest collections like [W1: {camera, lens, mic}].
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Shipments?', 'Groups?')
- ✅ **scenario-names-describe-conditions**: Scenario names describe the business situation (e.g. 'Consolidate: two beats three', 'Everything at one warehouse') — not 'Test case 2'.
- ✅ **business-language-columns**: Column names use business/domain language (e.g. 'Shipments?' not 'Result?', 'Warehouse stock' not 'inventory_map'). The tables read as a specification a product person could review.
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

