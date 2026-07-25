# Eval Review — tabletest, Iteration 40

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-sonnet-5 · **Date:** 2026-07-25 · **Evals:** 17

## Summary

314/365 (86.0%) · 20956288 tokens · 4379.3s · $14.8965

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

## Delta vs Iteration 39

**Eval definition changed — not comparable (17):**
- ⚠️ eval-29-shopping-cart-tt: fingerprint differs from iteration 39; re-baseline to compare
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
- ⚠️ eval-30-order-splitting-tt: fingerprint differs from iteration 39; re-baseline to compare

## Resource Comparison vs Iteration 39

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-29-shopping-cart-tt | 24/29 | 20/23 | 968444 | 968444 | 322.4 | 322.4 |
| eval-1-convert-repetitive-tests | 13/13 | 13/13 | 438137 | 438137 | 48.3 | 48.3 |
| eval-2-parse-dates | 15/15 | 15/15 | 815800 | 815800 | 115.2 | 115.2 |
| eval-7-permission-check | 11/13 | 13/13 | 585941 | 585941 | 59.9 | 59.9 |
| eval-8-money-parse | 14/15 | 15/15 | 606672 | 606672 | 87.6 | 87.6 |
| eval-9-bonus-contractor-structure | 12/13 | 13/13 | 553264 | 553264 | 82.4 | 82.4 |
| eval-14-weekly-pay | 17/21 | 19/20 | 1307679 | 1307679 | 264.9 | 264.9 |
| eval-15-reis-discount | 22/27 | 16/20 | 2287509 | 2287509 | 411.3 | 411.3 |
| eval-18-convert-from-code | 20/26 | 19/21 | 826034 | 826034 | 148.2 | 148.2 |
| eval-20-collections-and-quoting | 17/17 | 15/15 | 1242601 | 1811829 | 325.6 | 301.8 |
| eval-22-event-registration-tt | 25/28 | 21/25 | 573142 | 573142 | 154.0 | 154.0 |
| eval-23-loan-approval-tt | 19/22 | 12/18 | 540843 | 540843 | 149.9 | 149.9 |
| eval-25-convert-from-spock | 23/26 | 19/20 | 3006845 | 3006845 | 514.5 | 514.5 |
| eval-26-convert-from-kotest | 24/26 | 17/20 | 1443172 | 1443172 | 362.4 | 362.4 |
| eval-27-convert-from-testng | 20/25 | 17/19 | 1445041 | 1445041 | 337.7 | 337.7 |
| eval-28-convert-from-methodsource | 18/25 | 16/18 | 986014 | 986014 | 330.2 | 330.2 |
| eval-30-order-splitting-tt | 20/24 | 19/20 | 3329150 | 3329150 | 664.9 | 664.9 |

## Per-Eval Results

### ⚠️ Eval eval-29-shopping-cart-tt

**24/29** · 968444 tokens · 322446ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **concerns-decomposed**: Multiple @TableTest methods are used, each addressing a distinct concern
- ✅ **minimal-rows-per-concern**: Each @TableTest has only the rows needed to express its concern's rules
- ✅ **separates-item-coupon-total-checkout**: Item ops, coupon, total, checkout are in separate @TableTest methods
- ✅ **rows-independently-executable**: Each table row is independently executable
- ✅ **coupon-before-after-columns**: Coupon application table uses before/after columns
- ✅ **coupon-validity-not-coupon-types**: Coupon application table tests validity/replacement, not price effect
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Message?', 'Total?', 'Active coupon after?')
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes
- ✅ **business-language-columns**: Column names use domain/business language, not code identifiers
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **single-assertion-in-method**: Each @TableTest method applies one uniform assertion pattern to every row — not different assertions per scenario. Several unconditional assertions checking distinct observable outputs of one result (e.g. success, message, resulting cart) count as one uniform pattern; the anti-pattern is branching on the row (if/switch/ternary/try-catch) to choose what is asserted.
- ✅ **test-data-visible**: Product prices and coupon rules are visible via columns
- ❌ **type-converters-for-complex-objects**: TypeConverter methods convert table values into domain objects; bodies only arrange-act-assert
  > Cart cart = Cart.withItems(Map.of()).withActiveCouponCode(activeCouponBefore); CouponStore store = code -> Optional.ofNullable(couponInStore); — object construction from raw table values in method body
- ✅ **coupon-as-single-column**: Coupon data is expressed in a single column with a @TypeConverter
- ✅ **uses-standard-map-syntax**: Columns representing maps use standard TableTest map syntax
- ✅ **description-not-redundant-with-scenarios**: @Description annotations do not merely repeat scenario names
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable
- ✅ **rule-statable-from-table**: A reader can state the rule from the table alone
- ✅ **title-states-system-behaviour**: Each @DisplayName states what the code under test does
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ❌ **quantifier-covered-by-rows**: Quantified claims must be covered by rows/value sets
  > 'floored at zero regardless of which coupon type' but floor rows only use {PERCENT 50, FIXED 5.00}, omitting PRODUCT type
- ✅ **assertion-criteria-declared**: Comparison criteria applied by the assertion must be stated on the published surface
- ❌ **consistent-quantity-naming**: Same observable quantity carries the same column name across tables
  > 'Cart Before' in addsItemsFromTheCatalogue/removesItemsFromTheCart vs 'Cart Items' in checksOutTheCart/calculatesTheCartTotal for the same cart-contents input

### ✅ Eval eval-1-convert-repetitive-tests

**13/13** · 438137 tokens · 48254ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Discount?' or 'Expected?')
- ✅ **has-three-data-rows**: Table has exactly 3 data rows (GOLD, SILVER, BRONZE)
- ✅ **single-assertion-in-method**: Each @TableTest method applies one uniform assertion pattern to every row — not different assertions per scenario. Several unconditional assertions checking distinct observable outputs of one result (e.g. success, message, resulting cart) count as one uniform pattern; the anti-pattern is branching on the row (if/switch/ternary/try-catch) to choose what is asserted.
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'discountByCustomerTier' → 'Discount By Customer Tier'). Not a generic name like 'test1' or 'testMethod'.
- ✅ **description-if-present-adds-information**: undefined
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
- ✅ **type-conversion-addressed**: The response addresses type conversion for non-trivial column types
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'dateParsing' → 'Date Parsing'). Not a generic name like 'test1' or 'testMethod'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **empty-string-uses-quotes**: Empty string input uses quoted syntax (e.g. '' or "") in the table — not a blank cell, which represents null. The distinction between empty string and null is preserved.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules
- ✅ **separates-valid-and-invalid**: Valid parsing cases and error/exception cases are in separate @TableTest methods
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-7-permission-check

**11/13** · 585941 tokens · 59895ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **uses-value-sets**: Table uses value-set syntax (curly braces like {READ, WRITE} or {READ, WRITE, DELETE}) to express rules that hold across multiple actions — rather than one row per role/action combination
- ✅ **fewer-than-nine-rows**: Table has fewer than 9 data rows (i.e. does not enumerate every role/action combination individually)
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ❌ **no-duplicate-role-output**: No two rows share both the same Role value and the same boolean output — forces value set consolidation (e.g. USER can READ and USER can WRITE, both true, should be one row with {READ, WRITE}).
  > User can read | USER | READ | true' and 'User can write | USER | WRITE | true' are separate rows with same role/output; also 'Guest cannot write | GUEST | WRITE | false' and 'Guest cannot delete | GUEST | DELETE | false' duplicate role+output.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'permissionsByRoleAndAction' → 'Permissions By Role And Action'). Not a generic name like 'test1' or 'canPerform'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express... Does NOT merely restate the column names... Acceptable to omit.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **scenario-names-describe-conditions**: Scenario names describe conditions ('Admin performs any action', 'Guest reads') — not outcomes ('Allowed', 'Denied').
  > Scenario names like 'User cannot delete' and 'Guest cannot write' encode the allowed/denied outcome directly in the name rather than describing the condition.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-8-money-parse

**14/15** · 606672 tokens · 87611ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell in the valid inputs @TableTest table (not the string 'null', and not as a separate @Test method). The blank cell maps to a null parameter.
- ✅ **exception-has-expected-column**: The exception/invalid-input table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
- ✅ **exception-cases-handled**: The exception cases (empty string, letters-only, negative) are handled — via a Throws? column, assertThrows in the method body, or a separate @TableTest — not silently omitted
- ✅ **result-column-with-question-mark**: Valid inputs have a result column name ending with '?' (e.g. 'Money?' or 'Result?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'moneyParsing' → 'Money Parsing'). Not a generic name like 'test1' or 'testMethod'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express...
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern...
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.
- ✅ **separates-valid-and-invalid**: Valid parsing cases and error/exception cases are in separate @TableTest methods
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-9-bonus-contractor-structure

**12/13** · 553264 tokens · 82396ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **contractor-uses-value-set**: CONTRACTOR row uses a value set for Department (e.g. {SALES, ENGINEERING}) to express 'regardless of department' — not a dummy placeholder like 'ANY' or 'N/A', and not enumerated as separate rows.
- ✅ **four-core-rules-covered**: The four level×department combinations are all present in the table(s)
- ✅ **expects-bonus-percentage**: The expected output column contains bonus percentages, not bonus amounts.
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'bonusByLevelAndDepartment' → 'Bonus By Level And Department'). Not a generic name like 'test1' or 'testMethod'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes.
  > Contractor gets no bonus | CONTRACTOR | {SALES, ENGINEERING} | 0.0
- ✅ **business-language-columns**: Column names use domain/business language, not implementation terms.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-14-weekly-pay

**17/21** · 1307679 tokens · 264939ms

- ✅ **1.2-error-has-expected-column**: The error/rejection table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row
- ✅ **1.3-depth-overtime-boundary**: Overtime threshold boundary is covered: at least a value at the threshold (40) and just above (41 or similar).
- ✅ **1.4-depth-combined-scenario**: A combined scenario is present: a row with weekday + Sunday + holiday hours
- ✅ **1.5-depth-error-edge-cases**: Negative hours handled by a visible example row stating the chosen semantics; negative rate rejected
- ❌ **1.6-readability-empty-cells**: Sunday and Holiday hour columns use empty cells (not 0) when irrelevant; Integer params
  > No premium hours worked | 0 | 0 | 20.00 | 0.00 (uses 0, not blank; params are double)
- ✅ **1.7-readability-scenario-names**: Scenario names describe work patterns, not 'Test case 1' or outcomes
- ✅ **1.8-correctness-expected-values**: Expected values in intermediate and total columns are arithmetically correct for every row
- ✅ **1.9-correctness-value-set-semantics**: Value sets only used where all values produce same expected result; vacuous pass if none used
- ✅ **1.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ❌ **1.11-format-description**: @Description provides context beyond rows, does not restate multipliers
  > Overtime threshold is fixed at 40 hours/week; hours beyond it are paid at 1.5x.
- ✅ **1.12-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **1.13-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **1.14-depth-zero-rate**: A row where the hourly rate is zero, showing pay is zero regardless of hours
- ✅ **1.15-format-clean-method**: The test method body contains no if/ternary null-handling or parsing logic. Null-to-default conversion (e.g. blank Sunday hours → 0) is handled via a @TypeConverter method or a private helper, keeping the test method limited to arrange/act/assert.
- ✅ **business-language-columns**: Column names use domain/business language ... not implementation terms
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules
- ❌ **separates-classification-and-calculation**: Payable hours categorisation ... and pay calculation ... are in separate @TableTest methods
  > 'Just past the overtime threshold | 41 | 20.00 | 830.00' mixes classification (overtime boundary) with rate multiplication in one table/method
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **held-constants-declared**: A value the rule's outcome depends on ... must be visible as a column or named in the @DisplayName/@Description

### ⚠️ Eval eval-15-reis-discount

**22/27** · 2287509 tokens · 411343ms

- ✅ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
- ✅ **2.2-children-flat-discount**: Children's flat 20% discount is represented.
- ✅ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers with boundary values/value sets; max 40% represented.
- ✅ **2.4-depth-rolling-window-boundary**: 30-day rolling window boundary tested: 30 days included, 31 excluded.
- ❌ **window-boundary-uses-purchase-time**: 30-day window exercised at time-of-purchase granularity, not whole days only.
  > History rows use daysAgo integers (29,30,31) and converter does PURCHASE_TIME.minusDays(daysAgo) with no hour-level distinction
- ✅ **2.9-correctness-value-set-tier-semantics**: Value sets in tier table contain exactly the values that produce the same discount; no cross-tier contamination.
- ✅ **2.15-ticket-count-uses-value-sets**: Discount ladder table uses value sets for ticket count column to group counts within same tier.
- ❌ **2.16-no-duplicate-tier-mapping**: Tier-to-discount mapping expressed once, not repeated across multiple tables.
  > discountForCategoryAndTripCount repeats ladder values (0,10,40) and calculatesDiscountEndToEnd repeats (0,5,10,20) beyond the dedicated ladder table
- ✅ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ✅ **2.11-format-description**: @Description present, provides context beyond restating rows.
- ✅ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **2.14-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **2.17-zone-irrelevance-visible**: Eligibility/passenger-type table includes a Zone column with a value set showing discount applies regardless of zone.
  > discountForCategoryAndTripCount table columns: Scenario | Traveler Category | Trips In Window | Discount? — no Zone column
- ✅ **zone-independent-counting**: The rolling-window counting concern shows by row that a past purchase's zone does not affect whether it counts toward the travel count
- ✅ **2.18-adult-senior-value-set**: Adult and senior are expressed as a value set {ADULT, SENIOR}
- ❌ **2.19-depth-all-tiers**: All 9 discount tiers are represented in the ladder table
  > Table only covers 0, 5, 10, 35, 40 - rows for 15, 20, 25, 30 tiers are absent
- ❌ **2.20-readability-one-row-per-tier**: Each tier expressed as a single row, not split across multiple rows
  > Exactly at first tier | 5 | 5 ... Within first tier | {6, 7, 8, 9} | 5 -- same tier split into two rows
- ✅ **2.21-readability-relative-time**: The rolling window table expresses time relatively
- ✅ **concerns-decomposed**: Multiple tables each addressing a distinct concern
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable
- ✅ **rule-statable-from-table**: A reader can state the rule from the table alone
- ✅ **title-states-system-behaviour**: Each @DisplayName (or method name) states what the code under test does
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **held-constants-declared**: A value the rule's outcome depends on, and which the table holds constant for every row, must be visible as a column or named in the @DisplayName/@Description as deliberately held fixed.
- ✅ **quantifier-covered-by-rows**: When a title or description quantifies over a domain, the rows must actually cover that domain.

### ⚠️ Eval eval-18-convert-from-code

**20/26** · 826034 tokens · 148196ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **black-box-columns**: Table columns represent the method's public inputs and observable outputs, not internal state
- ✅ **observable-io-only**: No column is named after private fields or internal variables
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **business-language-columns**: Column names use domain/business language, not code identifiers
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Decision?' or 'Premium?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **concerns-decomposed**: Multiple tables each addressing a distinct concern
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules
- ❌ **separates-decision-and-premium**: Decision logic and premium calculation are in separate @TableTest methods
  > autoApprovesRenewalsWithNoClaims and rejectsApplicationsAboveRiskThreshold both assert 'Status?' and 'Premium?' together
- ✅ **no-reimplemented-internals**: Test method body does not recompute internal formulas
- ❌ **description-no-internals**: @Description explains purpose/business context, not internal formulas
  > "Risk score is age/10 + claimCount * 15. Applications are rejected once the score exceeds 75."
- ❌ **depth-decision-boundaries**: Decision table covers 4 vs 5 claims cliff across varying ages, not senior threshold
  > Boundary rows use age9/claims5 vs age10/claims5, not 4-vs-5 claim rows with varying age as required
- ❌ **depth-premium-boundaries**: Approved premium table covers boundary effects: the premium jump from 0 to 1 claim (impact of +15 risk score on premium), and the premium difference at age 64 vs 65 (standard vs senior formula). These boundary conditions document how premium changes at each threshold.
  > calculatesPremiumByAgeTier rows: age64/claims1=142.00, age65/claims1=273.50, age30/claims0=106.00, age90/claims0=231.50 - no same-age 0-vs-1 claim pair isolating claim impact within the premium table.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable.
- ✅ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body.
- ✅ **title-states-system-behaviour**: Each @DisplayName (or the method name it falls back to) states what the code under test does, not an external fact it depends on.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against InsuranceEvaluator
- ✅ **held-constants-declared**: A value the rule's outcome depends on, and which the table holds constant for every row, must be visible as a column or named in the @DisplayName/@Description as deliberately held fixed.
- ✅ **assertion-criteria-declared**: A comparison criterion applied by the assertion must be stated on the published surface.

### ✅ Eval eval-20-collections-and-quoting

**17/17** · 1242601 tokens · 325555ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **list-syntax-correct**: List values in the table use bracket syntax like [tech:java, biz:sales, dev:ci] — not comma-separated strings without brackets.
- ✅ **empty-list-explicit**: Empty list input uses [] (not a blank cell, which represents null).
- ✅ **special-chars-quoted**: Values containing pipes, brackets, or colons are properly quoted.
- ✅ **set-syntax-correct**: Set<String> values use curly brace syntax like {tech, dev} — not bracket syntax.
- ✅ **newline-in-cell**: A cell value containing a newline is escaped as \n or expressed as joined lines, not a literal break.
- ✅ **pipe-quoted**: Values containing pipe characters are quoted so they don't conflict with the column separator.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Filtered tags?' or 'Result?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **no-blank-collection-elements**: No collection value in any table contains a blank element.
- ✅ **empty-string-element-quoted**: An empty tag is written as a quoted empty string, not a blank element inside a collection.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-22-event-registration-tt

**25/28** · 573142 tokens · 153979ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **validation-rules-covered**: Email validation and name-required error scenarios are present — at least one row for invalid email and one for missing name.
- ✅ **blank-for-absent-optional**: Dietary requirements and/or accessibility needs use blank cells (null) when not provided — not 'N/A' or 'none'.
- ✅ **blank-vs-value-set-correct**: Irrelevant inputs (e.g. dietary requirements don't affect price) use value sets or representative values — not blanks.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Result?', 'Price?')
- ✅ **business-language-columns**: Column names use domain/business language — not code identifiers.
- ❌ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes.
  > No discount applies | ... | Early-bird discount applies | ... | Group discount applies | ... | Both apply, early-bird wins
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **concerns-decomposed**: Multiple tables each addressing a distinct concern — not one monolithic table mixing unrelated rules.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.
- ✅ **separates-validation-and-pricing**: Input validation and pricing/discount calculation are in separate @TableTest methods.
- ✅ **validation-includes-optional-fields**: Validation @TableTest includes Dietary Requirements and Accessibility Needs columns, with rows showing both null and non-null values.
- ✅ **descriptive-registration-date**: Registration date uses descriptive values with a @TypeConverter method converting to actual dates.
- ✅ **cutoff-date-column-if-literal-dates**: If registration dates are literal ... early-bird cutoff date appears as a separate policy column ... Passes automatically if descriptive date values are used.
- ✅ **description-no-irrelevant-information**: @Description does not include information already visible in the table columns or derivable from the table structure...
- ✅ **discount-column-preferred**: Output column is 'Discount?' ... If 'Price?' is used instead, both 'Base price' and 'Price?' columns must be present...
- ✅ **single-assertion-in-method**: Each @TableTest method applies one uniform assertion pattern to every row — not different assertions per scenario. Several unconditional assertions checking distinct observable outputs of one result (e.g. success, message, resulting cart) count as one uniform pattern; the anti-pattern is branching on the row (if/switch/ternary/try-catch) to choose what is asserted.
- ✅ **optional-fields-has-expected-column**: The optional-fields acceptance table includes an expectation/output column...
- ✅ **description-no-redundant-field-values**: Scenario names and @Description text do not restate values the table already shows...
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable...
- ✅ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body...
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **quantifier-covered-by-rows**: When a title or description quantifies over a domain... rows must actually cover that domain.

### ⚠️ Eval eval-23-loan-approval-tt

**19/22** · 540843 tokens · 149866ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **threshold-verifiable-from-table**: Each policy threshold (650 for non-seniors, 600 for seniors) is verifiable from the table...
- ✅ **concrete-domain-values**: Cell values use concrete domain terms...
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ❌ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes.
  > Below threshold rejects regardless of stable income ... Below threshold rejects even with missing income info
- ✅ **business-language-columns**: Column names use domain/business language.
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Decision?' or 'Status?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **concerns-decomposed**: The distinct concerns are each covered without cross-multiplying into redundant rows.
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules.
  > Senior qualifying score with missing income information | 70 | 610 | | PENDING_REVIEW (redundant with row 8's null-income case)
- ✅ **covers-age-credit-income**: Age boundary policy, credit score categorisation, and income status are each covered by minimal scenarios.
- ❌ **description-no-redundant-field-values**: Scenario names and @Description text do not restate values the table already shows.
  > Standard applicants are approved above a credit score of 650... lower threshold of 600... senior age cutoff is inclusive at 65.
- ✅ **depth-stable-income-effect**: The effect of stable income on the outcome is visible from table rows.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable.
- ✅ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body.
- ✅ **title-states-system-behaviour**: Each @DisplayName (or method name) states what the code under test does, not an external fact.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **held-constants-declared**: A value the rule's outcome depends on, held constant across rows, must be visible as a column or named in title/description.

### ⚠️ Eval eval-25-convert-from-spock

**23/26** · 3006845 tokens · 514486ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **options-as-map**: Package options collapsed into a single map column
- ❌ **options-type-converter**: A @TypeConverter method converts Map<String,String> to PackageOptions with defaults
  > options: Map<String, String>? ... private fun buildOptions(config: Map<String, String>?): PackageOptions { if (config == null) return PackageOptions() ... }
- ✅ **dimensions-as-list**: Dimensions represented as [L, W, H] list
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
- ✅ **concerns-decomposed**: Multiple @TableTest methods, each a distinct concern
- ❌ **concern-not-over-split**: Surcharges concern not fragmented across multiple same-fixture tables
  > oversizeSurcharge, hazmatHandlingSurcharge, fragileSurcharge, insuranceSurcharge all fix EU standard, weight 3.0, dimensions 30x20x15, Fee? column
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable
- ✅ **rule-statable-from-table**: A reader can state the rule from the table alone
- ✅ **numeric-types-correct**: Parameter types correspond to signature (double weight, List<Integer> dims, BigDecimal cost via compareTo)
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ✅ **business-language-columns**: Column names use domain language
- ✅ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **output-is-kotlin**: The generated test file is written in Kotlin (.kt), not Java or Groovy. Uses Kotlin syntax: fun, val/var, Kotlin imports.
- ✅ **no-groovy-syntax**: Output contains no Groovy or Spock syntax: no def, where:, expect:, given:, GString interpolation (#variable), Spock assertions (thrown(), old()), or Groovy list literals ([] as Set).
- ✅ **has-tabletest-dependency**: The build file (build.gradle.kts) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against ShippingCostCalculator
- ✅ **spock-dependency-removed**: The build file no longer declares Spock or Groovy dependencies — the old framework is fully removed, not just the test code.
- ✅ **consistent-quantity-naming**: Across the @TableTest methods in one class, the same observable quantity carries the same column name.
- ❌ **titles-form-a-family**: Titles function as a scannable catalogue with consistent grammatical shape.
  > Method names are mostly noun phrases (baseRateByRegionSpeedAndWeight, dimensionalWeightOverride, oversizeSurcharge, hazmatHandlingSurcharge, fragileSurcharge, insuranceSurcharge) but 'carrierDoesNotAffectCost' breaks the shape as a full predicate sentence.
- ✅ **minimal-rows-per-concern**: Each @TableTest has only the rows needed to express its concern's rules.

### ⚠️ Eval eval-26-convert-from-kotest

**24/26** · 1443172 tokens · 362383ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options collapsed into a single map column
  > Scenario | Dimensions | Fragile | Insured Value | Handling | Total Cost? — three separate option columns, not a map
- ❌ **options-type-converter**: @TypeConverter for PackageOptions present
  > No @TypeConverter method anywhere; PackageOptions built manually: 'val opts = PackageOptions(isFragile = fragile, insuredValue = insuredValue, handling = handling)'
- ✅ **dimensions-as-list**: Dimensions represented as [L, W, H] list
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
- ✅ **concerns-decomposed**: Multiple @TableTest methods, each a distinct concern
- ✅ **concern-not-over-split**: Surcharges kept in one table, not split per sub-rule
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable
- ✅ **rule-statable-from-table**: Rule statable from the table alone
- ✅ **numeric-types-correct**: Parameter types match SUT signature
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ✅ **business-language-columns**: Column names use domain language
- ✅ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **output-is-kotlin**: The generated test file is written in Kotlin (.kt), not Java. Uses Kotlin syntax: fun, val/var, Kotlin imports.
- ✅ **no-kotest-syntax**: Output contains no Kotest syntax: no forAll, row(), withData, shouldBe, context(), FunSpec, or Kotest imports.
- ✅ **has-tabletest-dependency**: The build file (build.gradle.kts) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against ShippingCostCalculator
- ✅ **kotest-dependency-removed**: The build file no longer declares Kotest dependencies — the old framework is fully removed, not just the test code.
- ✅ **consistent-quantity-naming**: Across the @TableTest methods in one class, the same observable quantity carries the same column name.
- ❌ **titles-form-a-family**: Read the class's test titles as the sorted index a reader would scan, stripped of their tables.
  > Method names: 'calculatesBaseRateByRegionSpeedAndWeight', 'choosesGreaterOfActualAndDimensionalWeight', 'appliesSurchargesToBaseCost' are verb-first, but 'carrierDoesNotAffectCost' is subject-first, breaking the consistent grammatical shape.
- ✅ **minimal-rows-per-concern**: Each @TableTest has only the rows needed to express its concern's rules.

### ⚠️ Eval eval-27-convert-from-testng

**20/25** · 1445041 tokens · 337704ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options collapsed into a single map column
  > Separate columns used: 'Scenario | Fragile | Total Cost?', 'Scenario | Insured Value | Total Cost?', 'Scenario | Handling | Total Cost?' instead of a single Options map column.
- ❌ **options-type-converter**: A @TypeConverter method converts Map<String,String> to PackageOptions
  > No @TypeConverter is defined anywhere; PackageOptions is built manually via 'options.setFragile(fragile);' etc. in each method body.
- ✅ **dimensions-as-list**: Dimensions represented as [L, W, H] list
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
- ✅ **concerns-decomposed**: Multiple @TableTest methods each addressing a distinct concern
- ❌ **concern-not-over-split**: Do not fragment a single concern across multiple same-fixture tables
  > appliesOversizeSurchargeWhenAnyDimensionExceedsLimit, appliesFragileSurcharge, appliesInsurancePremiumWithMinimum, appliesHazmatHandlingFee all fix 'EU standard, weight 3.0kg (base rate 7.50)' and vary a single surcharge, sharing the same 'Total Cost?' output.
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable
- ✅ **rule-statable-from-table**: A reader can state the rule from the table alone
- ✅ **numeric-types-correct**: Parameter types match calculateShippingCost signature
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes or generic labels
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ✅ **business-language-columns**: Column names use domain language
- ✅ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **no-testng-artifacts**: Output contains no TestNG syntax: no @DataProvider, @Test(dataProvider=...), Object[][], or TestNG imports (org.testng.*).
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against ShippingCostCalculator
- ✅ **testng-dependency-removed**: The build file no longer declares a TestNG dependency — the old framework is fully removed, not just the test code.
- ❌ **consistent-quantity-naming**: Across the @TableTest methods in one class, the same observable quantity carries the same column name.
  > Base rate table uses 'Base Rate?' while the dimensional-weight table for the identical looked-up quantity uses 'Rate?' (see 'EU standard, light ... 5.00' vs 'Small package, actual weight used | [10, 10, 10] | 5.00').
- ✅ **titles-form-a-family**: Read the class's test titles as the sorted index a reader would scan.
- ✅ **minimal-rows-per-concern**: Each @TableTest has only the rows needed to express its concern's rules.

### ⚠️ Eval eval-28-convert-from-methodsource

**18/25** · 986014 tokens · 330205ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options collapsed into a single map column
  > Separate columns used: 'Fragile? | Total?', 'Insured Value | Total?', 'Handling | Total?' instead of one Options map column
- ❌ **options-type-converter**: @TypeConverter method present for PackageOptions
  > private static PackageOptions options(boolean fragile, BigDecimal insuredValue, String handling) { ... } is a manual helper, no @TypeConverter annotation anywhere
- ✅ **dimensions-as-list**: Dimensions represented as [L, W, H] list
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
- ✅ **concerns-decomposed**: Multiple @TableTest methods, each a distinct concern
- ❌ **concern-not-over-split**: Surcharges concern not fragmented across many same-fixture tables
  > shouldAddOversizeSurchargeWhenAnyDimensionExceedsThreshold, shouldApplyFragileMultiplier, shouldAddInsurancePremiumWithMinimum, shouldAddHazmatHandlingFee all fix zone EU standard, weight 3.0kg, same Total? column
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable
- ✅ **rule-statable-from-table**: A reader can state the rule from the table alone
- ✅ **numeric-types-correct**: Parameter types match signature (double weight, List<Integer> dims, BigDecimal cost)
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ✅ **business-language-columns**: Column names use domain language
- ✅ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **no-methodsource-artifacts**: Output contains no @MethodSource syntax: no @MethodSource, Stream<Arguments>, Arguments.of, or JUnit Jupiter parameterized imports (org.junit.jupiter.params.provider.*).
- ✅ **has-tabletest-dependency**: The build file (pom.xml) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against ShippingCostCalculator
- ✅ **consistent-quantity-naming**: Across the @TableTest methods in one class, the same observable quantity carries the same column name.
- ❌ **titles-form-a-family**: Read the class's test titles (each @DisplayName, or the method name it falls back to) as the sorted index a reader would scan, stripped of their tables. FAILS when the titles do not function as a catalogue: when three or more share a leading word that carries no information ('should…', 'test…', 'verify…'), so the varying subject arrives last and the list cannot be scanned; or when siblings describing the same kind of rule use unrelated grammatical shapes (a bare noun phrase beside an active sentence beside a should-name), so no scanning pattern exists. PASSES when the varying subject comes first or early and one consistent shape runs across the family. Judge the set, never a title in isolation — a title that reads perfectly on its own page can still be an unscannable entry in the index, which is the failure this assertion exists to catch. A class with fewer than three @TableTest methods PASSES.
  > Not graded
- ✅ **minimal-rows-per-concern**: Each @TableTest has only the rows needed to express its concern's rules...
- ❌ **held-constants-declared**: A value the rule's outcome depends on, and which the table holds constant for every row, must be visible as a column or named in the @DisplayName/@Description.
  > // Carrier never affects cost (see shouldNotVaryCostByCarrier below), so every other table below fixes it to Carrier.DHL rather than treating it as a variable input. -- this is a source-code comment, not a column or per-table @Description, and it fixes carrier in tables 1-7 without declaration there.

### ⚠️ Eval eval-30-order-splitting-tt

**20/24** · 3329150 tokens · 664880ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **concerns-decomposed**: Multiple @TableTest methods are used, each addressing a distinct concern — not one monolithic table mixing all splitting rules. Closely-related concerns may share one method... four methods covering five concerns is fine.
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable.
- ✅ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body.
- ❌ **minimal-rows-per-concern**: Each @TableTest has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.
  > Fulfillment/address table has 6 rows including 'Delivery split by address, plus a separate pickup item' — a second interaction row combining (b)+(c) beyond the single optional interaction allowed.
- ✅ **concern-fulfillment-method**: Fulfillment method splitting is represented in a @TableTest.
- ✅ **concern-delivery-address**: Delivery address splitting is represented in a @TableTest.
- ✅ **concern-availability**: Availability splitting is represented as its own @TableTest.
- ✅ **concern-warehouse-allocation**: Warehouse allocation (minimising shipments) is represented as its own @TableTest.
- ✅ **concern-companion-products**: Companion product grouping is represented as its own @TableTest.
- ✅ **all-outputs-same-table**: Each @TableTest method includes all output columns for its concern in the same table.
- ❌ **native-collection-output**: Compound output expressed as native TableTest list/map/set, not hand-rolled string
  > Shipments? cells like ["IMMEDIATE:[camera,lens]"] and ["W1:[camera,lens,mic]"] built via describeByWarehouse/describeByAvailability helpers that concatenate strings with colons and brackets.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Shipments?', 'Groups?')
- ✅ **scenario-names-describe-conditions**: Scenario names describe business situations, not 'Test case N'
- ✅ **business-language-columns**: Column names use business/domain language
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ❌ **held-constants-declared**: Held-constant values the outcome depends on must be visible as a column or named in @DisplayName/@Description
  > splitsShipmentsByFulfillmentTypeAndAddress has no @Description and body silently fixes all items to StockStatus.IN_STOCK and warehouse 'W1' via `inventory.addStock("W1", item.getProductId(), StockStatus.IN_STOCK)` with no column or note stating this.
- ❌ **assertion-criteria-declared**: Comparison criteria (ordering, normalization) must be stated on the published surface
  > describeByItems/describeByAvailability/describeByWarehouse all call `.sorted()` to canonicalize order, but no @Description or column states shipments/products are compared without regard to order.

