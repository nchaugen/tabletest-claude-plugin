# Eval Review — tabletest variant=next, Iteration 2

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-haiku-4-5 · **Date:** 2026-07-21 · **Evals:** 5

## Summary

93/108 (86.1%) · 5758002 tokens · 1700.4s · $4.8382

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

## Delta vs Iteration 1

No changes.

## Resource Comparison vs Iteration 1

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-2-parse-dates | 12/15 | — | 620279 | — | 82.1 | — |
| eval-14-weekly-pay | 19/20 | — | 1685249 | — | 256.1 | — |
| eval-22-event-registration-tt | 22/27 | — | 1188105 | — | 316.8 | — |
| eval-29-shopping-cart-tt | 22/26 | — | 1326166 | — | 603.6 | — |
| eval-30-order-splitting-tt | 18/20 | — | 938203 | — | 441.9 | — |

## Per-Eval Results

### ⚠️ Eval eval-2-parse-dates

**12/15** · 620279 tokens · 82068ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell (not the string 'null') in the table
- ✅ **exception-handled-cleanly**: The empty string / exception case is handled — either via a Throws? column, assertThrows inside the method, or a separate @TableTest — rather than being silently omitted
- ✅ **localdate-result-column**: There is a result column typed as LocalDate (or using a string representation that maps to LocalDate)
- ✅ **type-conversion-addressed**: The response addresses type conversion for non-trivial column types — either by providing a @TypeConverter/converter method, or by using a cell representation that TableTest's built-in conversion handles (e.g. ISO-8601 date strings for LocalDate — relying on built-in conversion counts as addressed).
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'dateParsing' → 'Date Parsing'). Not a generic name like 'test1' or 'testMethod'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express — such as where/when the rule applies, or open questions. Does NOT merely restate the column names or summarise what the rows show. It is acceptable to omit @Description if the table already conveys all relevant context (e.g. supported date formats are visible as table rows).
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **empty-string-uses-quotes**: Empty string input uses quoted syntax (e.g. '' or "") in the table — not a blank cell, which represents null. The distinction between empty string and null is preserved.
- ❌ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
  > [split vote 1/3 pass] Single @TableTest mixing valid parsing cases (ISO, slash, short-year formats) with null input and exception case. No separation of valid vs. invalid concerns.
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
  > [split vote 1/3 pass] One monolithic table with 5 rows combining three format concerns plus null and exception, rather than separate focused tables per concern.
- ❌ **separates-valid-and-invalid**: Valid parsing cases and error/exception cases are in separate @TableTest methods
  > All cases (valid and exception) are in a single @TableTest method with a Throws? column to conditionally handle exceptions, rather than separate methods
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-14-weekly-pay

**19/20** · 1685249 tokens · 256051ms

- ✅ **1.2-error-has-expected-column**: The error/rejection table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
- ✅ **1.3-depth-overtime-boundary**: Overtime threshold boundary is covered: at least a value at the threshold (40) and just above (41 or similar). Not just values well below and well above.
- ✅ **1.4-depth-combined-scenario**: A combined scenario is present: a row with weekday + Sunday + holiday hours, testing the composition of all hour types.
- ✅ **1.5-depth-error-edge-cases**: Error/edge cases covered: at least negative hours (floored at zero) and negative or missing rate (rejected).
- ❌ **1.6-readability-empty-cells**: Sunday and Holiday hour columns use empty cells (not 0) when those inputs are not relevant to a scenario — making it easier to spot which rows involve weekend/holiday hours. This is a readability preference; parameter types should be Integer (not int) to support null from blank cells.
  > PayCalculatorTest.calculatesPay uses explicit 0 values (e.g., '40 | 0 | 0 | 0') rather than empty cells for non-relevant columns
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
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-22-event-registration-tt

**22/27** · 1188105 tokens · 316756ms

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
  > @Description states 'Base price is £100 for every row' and 'Early-bird applies... before the 2025-03-01 cutoff (20% off); group discount applies to groups of 5 or more (15% off)' — these rules are already shown by the table rows.
- ❌ **discount-column-preferred**: Output column is 'Discount?' (percentage or amount off) rather than 'Price?' — making verification simpler (discount is the rule's direct output; price requires knowing the base price). If 'Price?' is used instead, both 'Base price' and 'Price?' columns must be present so the reader can trace the calculation.
  > Table includes both 'Discount?' and 'Price?' columns, but 'Base price' column is missing; base price (£100) is only stated in @Description, not as a column.
- ❌ **single-assertion-in-method**: Each @TableTest method has a single, uniform assertion pattern applied to all rows — not different assertions per scenario. Validation checks one thing (e.g. error message or acceptance status); pricing checks one thing (e.g. discount or price). No if/switch in assertion logic.
  > Method has 2 assertions; Method has 2 assertions
- ✅ **optional-fields-has-expected-column**: The optional-fields acceptance table includes an expectation/output column (e.g. 'Approved?', 'Result?', or 'Status?') asserting that each combination is accepted — not just varying inputs without any expected column.
- ❌ **description-no-redundant-field-values**: Scenario names and @Description text do not restate values the table already shows. The assertion FAILS if either: (1) a scenario name contains a literal value that also appears in an input cell of the same row (e.g. scenario 'Age 35, score 700' beside Age and Credit score columns holding 35 and 700); or (2) @Description states a fixed value for an input that is already a column in that table. Otherwise it PASSES. Stating a constant that is NOT a column — a threshold, a policy figure, a value fixed in the method body — is correct and must not fail this assertion.
  > [split vote 1/3 pass] validatesRegistration @Description states 'group size (1)' and 'fixed date (2025-06-01)' which are input parameters in the method signature, violating the rule against restating fixed column-like values in the description
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. Judge each @TableTest table independently; the assertion FAILS if any table meets any of these conditions: (1) its expectation column holds the same value in every row; (2) in every row, each expectation cell is a verbatim copy of a cell in an input column of the same row; (3) an implementation that ignored the input columns entirely and returned a fixed value would satisfy every row. Otherwise the assertion PASSES. Do not fail a table merely because its values are simple or round — only these three conditions matter.
- ❌ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body. The assertion FAILS if any of these hold for any @TableTest: (1) a value needed to predict the expectation (a threshold, rate, cutoff, multiplier, reference date) appears only in the method body or a field, and in neither a column, the @DisplayName, nor the @Description; (2) the operation applied to the inputs cannot be named from the column headers and cell values alone; (3) a helper method used in the assertion applies a comparison criterion (ordering, tolerance, subset matching) that is stated nowhere in the table, title, or description. Otherwise the assertion PASSES. Setup that merely constructs the objects named by the columns is not a failure.
  > appliesEarlyBirdAndGroupDiscount uses 'compareTo' for BigDecimal assertion, applying a tolerance/comparison criterion not stated in table, @DisplayName, or @Description — reader cannot infer this from the table alone.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-29-shopping-cart-tt

**22/26** · 1326166 tokens · 603561ms

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
  > Method has 3 assertions; Method has 3 assertions; Method has 3 assertions; Method has 2 assertions
- ✅ **test-data-visible**: Product prices and coupon rules (type, amount, expiry status) are visible in the table via columns, @Description, or explicit setup — not silently hardcoded. The reader can trace how inputs lead to outputs.
- ✅ **type-converters-for-complex-objects**: TypeConverter methods are used to convert map/string table values into domain objects (e.g. Map → Cart, Map → InventoryService, map → Coupon). Test method bodies contain only arrange-act-assert — no object construction from raw table values.
- ❌ **coupon-as-single-column**: Coupon data is expressed in a single column with a @TypeConverter — not spread across separate sparse columns for coupon type, value, and target product. The column format may be map syntax (e.g. [type: PERCENTAGE, value: 10]) or readable shorthand (e.g. '20% cart', '$5 off', '50% off widget').
  > shouldCalculateTotal table has separate columns 'Coupon Type', 'Coupon Amount', 'Coupon Target Product' instead of single coupon column with @TypeConverter.
- ✅ **uses-standard-map-syntax**: Columns representing maps (e.g. cart contents, product catalogue, inventory levels) use standard TableTest map syntax — [k: v, k2: v2] for entries and [:] for empty map — not a custom bracket notation. Custom string-parsing TypeConverters are not needed for data that maps naturally to Map<String, X>.
- ❌ **description-not-redundant-with-scenarios**: @Description annotations do not repeat information that the scenario names already convey. Coupon type behavior (what PERCENTAGE/FIXED/PRODUCT mean) should be expressed through descriptive scenario names, not duplicated in the description.
  > @Description for shouldCalculateTotal states 'A product-specific coupon discounts only the named product...' which duplicates what scenario names like 'Product-specific discount applies' already convey.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. Judge each @TableTest table independently; the assertion FAILS if any table meets any of these conditions: (1) its expectation column holds the same value in every row; (2) in every row, each expectation cell is a verbatim copy of a cell in an input column of the same row; (3) an implementation that ignored the input columns entirely and returned a fixed value would satisfy every row. Otherwise the assertion PASSES. Do not fail a table merely because its values are simple or round — only these three conditions matter.
- ❌ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body. The assertion FAILS if any of these hold for any @TableTest: (1) a value needed to predict the expectation (a threshold, rate, cutoff, multiplier, reference date) appears only in the method body or a field, and in neither a column, the @DisplayName, nor the @Description; (2) the operation applied to the inputs cannot be named from the column headers and cell values alone; (3) a helper method used in the assertion applies a comparison criterion (ordering, tolerance, subset matching) that is stated nowhere in the table, title, or description. Otherwise the assertion PASSES. Setup that merely constructs the objects named by the columns is not a failure.
  > shouldCalculateTotal does not show percentage 10 in any column for 'valid' coupons; buildCouponStore hardcodes 'Coupon.percentage(10)' in method body only, making the percentage rate unstated in the table itself.
- ✅ **title-states-system-behaviour**: Each @DisplayName (or the method name it falls back to) states what the code under test does, not an external fact it depends on. Apply the check mechanically: strike the system under test from the sentence — if it still reads as true, because it merely restates a regulation, standard, format, or domain fact, the title is about something else and fails.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-30-order-splitting-tt

**18/20** · 938203 tokens · 441932ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **concerns-decomposed**: Multiple @TableTest methods are used, each addressing a distinct concern — not one monolithic table mixing all splitting rules.
- ✅ **minimal-rows-per-concern**: Each @TableTest has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.
- ✅ **concern-fulfillment-method**: Fulfillment method splitting is represented as its own @TableTest — items with different fulfillment types (store pickup vs home delivery) cannot share a shipment.
- ✅ **concern-delivery-address**: Delivery address splitting is represented as its own @TableTest — items going to different addresses must be in separate shipments.
- ✅ **concern-availability**: Availability splitting is represented as its own @TableTest — in-stock items ship immediately, backordered/pre-ordered items ship when available.
- ✅ **concern-warehouse-allocation**: Warehouse allocation (minimising shipments) is represented as its own @TableTest — choosing which warehouses to ship from to minimise total shipment count.
- ✅ **concern-companion-products**: Companion product grouping is represented as its own @TableTest — paired items (e.g. camera body and lens) should ship together from the same location when possible.
- ✅ **all-outputs-same-table**: Each @TableTest method includes all output columns for its concern in the same table — e.g. warehouse allocation includes both the assignment and shipment count, not split across methods. All outputs of the same concern belong together.
- ❌ **scalar-quantity-for-warehouse**: Warehouse allocation table uses scalar quantity columns (e.g. 'Qty ordered') not item lists, when product identity doesn't matter for the logic.
  > [split vote 1/3 pass] selectsMinimalWarehouseCombination uses 'Availability' column with product IDs mapped to warehouses (e.g. 'camera:WH1+WH2'), not scalar quantities
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Shipments?', 'Groups?')
- ✅ **scenario-names-describe-conditions**: Scenario names describe the business situation (e.g. 'Consolidate: two beats three', 'Everything at one warehouse') — not 'Test case 2'.
- ❌ **business-language-columns**: Column names use business/domain language (e.g. 'Shipments?' not 'Result?', 'Warehouse stock' not 'inventory_map'). The tables read as a specification a product person could review.
  > Column names include 'Shipment Groups?', 'Shipments?', 'Shipment Count?', 'Warehouses Used?', 'Same Warehouse?' - these use question marks and are somewhat business-oriented, but columns like 'Items', 'Availability', 'Result' are generic. The 'splitsByAvailability' table uses technical terms like 'IMMEDIATE:' and 'WHEN_AVAILABLE:' which are implementation details, not business language.
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

## Variant vs Official (iterations 40, 39 merged)

### Per-Eval Resource Comparison

| Eval | Pass (off) | Pass (var) | Tokens (off) | Tokens (var) | Tok Δ | Cost (off) | Cost (var) | Cost Δ | Time (off) | Time (var) | Time Δ |
|------|------------|------------|--------------|--------------|-------|------------|------------|--------|------------|------------|--------|
| eval-2-parse-dates | 15/15 | 12/15 | 815800 | 620279 | -24% | $0.5174 | $0.4217 | -19% | 115.2s | 82.1s | -29% |
| eval-14-weekly-pay | 19/20 | 19/20 | 1307679 | 1685249 | +29% | $0.9082 | $1.1031 | +21% | 264.9s | 256.1s | -3% |
| eval-22-event-registration-tt | 24/27 | 22/27 | 573142 | 1188105 | +107% | $0.5159 | $0.7911 | +53% | 154.0s | 316.8s | +106% |
| eval-29-shopping-cart-tt | 23/26 | 22/26 | 968444 | 1326166 | +37% | $0.9054 | $1.3393 | +48% | 322.4s | 603.6s | +87% |
| eval-30-order-splitting-tt | 18/20 | 18/20 | 3329150 | 938203 | -72% | $2.2750 | $1.1830 | -48% | 664.9s | 441.9s | -34% |
| **Totals (5 comparable)** | **99/108** | **93/108** | **6994215** | **5758002** | **-18%** | **$5.1219** | **$4.8382** | **-6%** | **1521.4s** | **1700.4s** | **+12%** |

**Comparable summary (5 evals in both):**

| Source | Pass Rate | Tokens | Cost | Time |
|--------|-----------|--------|------|------|
| next (iter 2) | 93/108 (86.1%) | 5758002 | $4.8382 | 1700.4s |
| official | 99/108 (91.7%) | 6994215 | $5.1219 | 1521.4s |
| **Δ** | | **-18%** | **-6%** | **+12%** |

### Per-Assertion Comparison

| Eval | Assertion | official | next |
|------|-----------|----------|------|
| eval-2-parse-dates | concerns-decomposed | ✅ | ❌ |
| eval-2-parse-dates | minimal-rows-per-concern | ✅ | ❌ |
| eval-2-parse-dates | separates-valid-and-invalid | ✅ | ❌ |
| eval-22-event-registration-tt | discount-column-preferred | ✅ | ❌ |
| eval-22-event-registration-tt | description-no-redundant-field-values | ✅ | ❌ |
| eval-29-shopping-cart-tt | business-language-columns | ❌ | ✅ |
| eval-29-shopping-cart-tt | coupon-as-single-column | ✅ | ❌ |
| eval-29-shopping-cart-tt | description-not-redundant-with-scenarios | ✅ | ❌ |

### Load-Bearing Assertions

Assertions that pass with official skill but fail with next variant:

- eval-2-parse-dates: `concerns-decomposed`
- eval-2-parse-dates: `minimal-rows-per-concern`
- eval-2-parse-dates: `separates-valid-and-invalid`
- eval-22-event-registration-tt: `discount-column-preferred`
- eval-22-event-registration-tt: `description-no-redundant-field-values`
- eval-29-shopping-cart-tt: `coupon-as-single-column`
- eval-29-shopping-cart-tt: `description-not-redundant-with-scenarios`

### Attention Needed

Evals with failed assertions or disproportionate resource usage (>2x official):

| Eval | Issue | Details |
|------|-------|---------|
| eval-22-event-registration-tt | High tokens | 1188105 vs 573142 (+107%) |
| eval-2-parse-dates | Failed assertions | `concerns-decomposed`, `minimal-rows-per-concern`, `separates-valid-and-invalid` |
| eval-22-event-registration-tt | Failed assertions | `discount-column-preferred`, `description-no-redundant-field-values` |
| eval-29-shopping-cart-tt | Failed assertions | `coupon-as-single-column`, `description-not-redundant-with-scenarios` |

