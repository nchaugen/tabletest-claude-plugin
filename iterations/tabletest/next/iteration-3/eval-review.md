# Eval Review — tabletest variant=next, Iteration 3

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-haiku-4-5 · **Date:** 2026-07-22 · **Evals:** 4

## Summary

83/89 (93.3%) · 3003581 tokens · 847.1s · $2.6169

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

## Delta vs Iteration 2

**Regressions (2):**
- ❌ eval-2-parse-dates: `type-conversion-addressed`
- ❌ eval-29-shopping-cart-tt: `business-language-columns`

**Improvements (9):**
- ✅ eval-2-parse-dates: `concerns-decomposed`
- ✅ eval-2-parse-dates: `minimal-rows-per-concern`
- ✅ eval-2-parse-dates: `separates-valid-and-invalid`
- ✅ eval-22-event-registration-tt: `discount-column-preferred`
- ✅ eval-22-event-registration-tt: `description-no-redundant-field-values`
- ✅ eval-22-event-registration-tt: `rule-statable-from-table`
- ✅ eval-29-shopping-cart-tt: `coupon-as-single-column`
- ✅ eval-29-shopping-cart-tt: `description-not-redundant-with-scenarios`
- ✅ eval-29-shopping-cart-tt: `rule-statable-from-table`

## Resource Comparison vs Iteration 2

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-2-parse-dates | 14/15 | 12/15 | 591885 | 620279 | 183.6 | 82.1 |
| eval-22-event-registration-tt | 25/27 | 22/27 | 771866 | 1188105 | 176.1 | 316.8 |
| eval-23-loan-approval-tt | 20/21 | — | 787298 | — | 197.6 | — |
| eval-29-shopping-cart-tt | 24/26 | 22/26 | 852532 | 1326166 | 289.9 | 603.6 |

## Per-Eval Results

### ⚠️ Eval eval-2-parse-dates

**14/15** · 591885 tokens · 183607ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell (not the string 'null') in the table
- ✅ **exception-handled-cleanly**: The empty string / exception case is handled — either via a Throws? column, assertThrows inside the method, or a separate @TableTest — rather than being silently omitted
- ✅ **localdate-result-column**: There is a result column typed as LocalDate (or using a string representation that maps to LocalDate)
- ❌ **type-conversion-addressed**: The response addresses type conversion for non-trivial column types — either by providing a @TypeConverter/converter method, or by using a cell representation that TableTest's built-in conversion handles (e.g. ISO-8601 date strings for LocalDate — relying on built-in conversion counts as addressed).
  > No @TypeConverter or converter method provided. ISO-8601 strings used (2024-01-15 format), but LocalDate parameter expects actual LocalDate objects. Built-in conversion handling is not documented in the code.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'dateParsing' → 'Date Parsing'). Not a generic name like 'test1' or 'testMethod'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express — such as where/when the rule applies, or open questions. Does NOT merely restate the column names or summarise what the rows show. It is acceptable to omit @Description if the table already conveys all relevant context (e.g. supported date formats are visible as table rows).
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **empty-string-uses-quotes**: Empty string input uses quoted syntax (e.g. '' or "") in the table — not a blank cell, which represents null. The distinction between empty string and null is preserved.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ✅ **separates-valid-and-invalid**: Valid parsing cases and error/exception cases are in separate @TableTest methods
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-22-event-registration-tt

**25/27** · 771866 tokens · 176076ms

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
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations.
- ✅ **separates-validation-and-pricing**: Input validation and pricing/discount calculation are in separate @TableTest methods
- ✅ **validation-includes-optional-fields**: Validation @TableTest includes Dietary Requirements and Accessibility Needs columns, with rows showing both null (blank) and non-null values.
- ✅ **descriptive-registration-date**: Registration date uses descriptive values (e.g. 'before cutoff', 'on cutoff', 'after cutoff') with a @TypeConverter method — not raw date literals.
- ✅ **cutoff-date-column-if-literal-dates**: If registration dates are literal (e.g. '2025-02-28' instead of descriptive), the early-bird cutoff date appears as a separate policy column so the reader can see both dates and verify the comparison. Passes automatically if descriptive date values are used.
- ❌ **description-no-irrelevant-information**: If @Description is present, it does not include information that is already visible in the table columns or that can be derived from the table structure — such as fixed input values that could be columns, or restating the discount rules that the rows already demonstrate.
  > @Description states 'Base price is fixed at £100 for every registration. Early-bird cutoff is 2025-03-01...' — these are fixed values not shown as columns, which is appropriate. However, it restates discount stacking behavior ('higher discount...is used instead of stacking') that the row 'Both apply, higher discount wins, no stacking' already demonstrates.
- ✅ **discount-column-preferred**: Output column is 'Discount?' (percentage or amount off) rather than 'Price?' — making verification simpler. If 'Price?' is used instead, both 'Base price' and 'Price?' columns must be present so the reader can trace the calculation.
- ❌ **single-assertion-in-method**: Each @TableTest method has a single, uniform assertion pattern applied to all rows — not different assertions per scenario. Validation checks one thing (e.g. error message or acceptance status); pricing checks one thing (e.g. discount or price). No if/switch in assertion logic.
  > Method has 2 assertions; Method has 2 assertions
- ✅ **optional-fields-has-expected-column**: The optional-fields acceptance table includes an expectation/output column (e.g. 'Approved?', 'Result?', or 'Status?') asserting that each combination is accepted — not just varying inputs without any expected column.
- ✅ **description-no-redundant-field-values**: Scenario names and @Description text do not restate values the table already shows. The assertion FAILS if either: (1) a scenario name contains a literal value that also appears in an input cell of the same row; or (2) @Description states a fixed value for an input that is already a column in that table.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. The assertion FAILS if: (1) expectation column holds the same value in every row; (2) every expectation is a verbatim copy of an input cell; or (3) a fixed-return implementation would satisfy every row.
- ✅ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body. The assertion FAILS if: (1) a value needed to predict expectation appears only in method body/field, not in column/title/@Description; (2) the operation cannot be named from headers/cells; or (3) a helper criterion is not stated in table/title/description.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-23-loan-approval-tt

**20/21** · 787298 tokens · 197604ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **threshold-as-column**: Policy thresholds (650/600 credit scores) appear in a dedicated policy column (e.g. 'Credit threshold') separate from the applicant's actual credit score column.
  > The table shows 'Credit Score' column with values like 650, 651, 600, 601. Thresholds 650 and 600 are mentioned only in @Description text, not in a separate 'Credit threshold' column.
- ✅ **concrete-domain-values**: Cell values use concrete domain terms (e.g. '70', '650', 'APPROVED') — not abstract codes.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Senior at lower threshold', 'Missing income') — not outcomes.
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Age', 'Credit score', 'Decision?') — not code identifiers.
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Decision?' or 'Status?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **concerns-decomposed**: Multiple tables are used, each addressing a distinct concern — not one monolithic table.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations.
- ✅ **separates-age-credit-income**: Age boundary policy, credit score categorisation, and income status are separated into distinct @TableTest methods.
- ✅ **description-no-redundant-field-values**: Scenario names and @Description text do not restate values the table already shows.
- ✅ **depth-stable-income-effect**: The effect of stable income on the outcome is visible from table rows, differing stable-income column with equal credit score/age.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. No expectation column all identical, no verbatim copies, no fixed-value implementations.
- ✅ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body.
- ✅ **title-states-system-behaviour**: Each @DisplayName states what the code under test does, not an external fact it depends on.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-29-shopping-cart-tt

**24/26** · 852532 tokens · 289857ms

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
  > Column 'Product Id' uses camelCase-like identifier style; expected 'Product' or domain term; 'Coupon Code' is acceptable but 'Product Id' deviates
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
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
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. Judge each @TableTest table independently; the assertion FAILS if any table meets any of these conditions: (1) its expectation column holds the same value in every row; (2) in every row, each expectation cell is a verbatim copy of a cell in an input column of the same row; (3) an implementation that ignored the input columns entirely and returned a fixed value would satisfy every row. Otherwise the assertion PASSES.
- ✅ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body. The assertion FAILS if any of these hold for any @TableTest: (1) a value needed to predict the expectation (a threshold, rate, cutoff, multiplier, reference date) appears only in the method body or a field, and in neither a column, the @DisplayName, nor the @Description; (2) the operation applied to the inputs cannot be named from the column headers and cell values alone; (3) a helper method used in the assertion applies a comparison criterion (ordering, tolerance, subset matching) that is stated nowhere in the table, title, or description. Otherwise the assertion PASSES.
- ✅ **title-states-system-behaviour**: Each @DisplayName (or the method name it falls back to) states what the code under test does, not an external fact it depends on. Apply the check mechanically: strike the system under test from the sentence — if it still reads as true, because it merely restates a regulation, standard, format, or domain fact, the title is about something else and fails.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

