# Eval Review — tabletest variant=next, Iteration 1

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-haiku-4-5 · **Date:** 2026-07-07 · **Evals:** 8

## Summary

136/156 (87.2%) · 8749253 tokens · 2135.2s · $6.7073

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

## Per-Eval Results

### ⚠️ Eval eval-2-parse-dates

**11/15** · 611286 tokens · 81686ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell (not the string 'null') in the table
- ✅ **exception-handled-cleanly**: The empty string / exception case is handled — either via a Throws? column, assertThrows inside the method, or a separate @TableTest — rather than being silently omitted
- ✅ **localdate-result-column**: There is a result column typed as LocalDate (or using a string representation that maps to LocalDate)
- ✅ **type-conversion-addressed**: The response addresses type conversion for non-trivial column types — either by providing a @TypeConverter/converter method, or by using a cell representation that TableTest's built-in conversion handles (e.g. ISO-8601 date strings for LocalDate — relying on built-in conversion counts as addressed).
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'dateParsing' → 'Date Parsing'). Not a generic name like 'test1' or 'testMethod'.
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express — such as where/when the rule applies, or open questions. Does NOT merely restate the column names or summarise what the rows show.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **empty-string-uses-quotes**: Empty string input uses quoted syntax (e.g. '' or "") in the table — not a blank cell, which represents null. The distinction between empty string and null is preserved.
  > No quoted empty string ('""' or "''") found in table cells
- ❌ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules.
  > Only one @TableTest method (parsesDate) exists; exception case is in separate @Test but not decomposed from valid parsing logic within the single table
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns.
  > Single table mixes valid parsing formats (ISO, slash, short-year) with null-handling (blank result cell) — exception and valid cases remain in one table despite being separate concerns
- ❌ **separates-valid-and-invalid**: Valid parsing cases and error/exception cases are in separate @TableTest methods
  > parsesDate @TableTest includes null input (valid case); rejectsEmptyString is separate @Test but not a separate @TableTest, so valid/invalid are not both in separate @TableTest methods
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-14-weekly-pay

**19/20** · 1173080 tokens · 271657ms

- ✅ **1.2-error-has-expected-column**: The error/rejection table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
- ✅ **1.3-depth-overtime-boundary**: Overtime threshold boundary is covered: at least a value at the threshold (40) and just above (41 or similar). Not just values well below and well above.
- ✅ **1.4-depth-combined-scenario**: A combined scenario is present: a row with weekday + Sunday + holiday hours, testing the composition of all hour types.
- ✅ **1.5-depth-error-edge-cases**: Error/edge cases covered: at least negative hours (floored at zero) and negative or missing rate (rejected).
- ❌ **1.6-readability-empty-cells**: Sunday and Holiday hour columns use empty cells (not 0) when those inputs are not relevant to a scenario — making it easier to spot which rows involve weekend/holiday hours. This is a readability preference; parameter types should be Integer (not int) to support null from blank cells.
  > Rows use explicit '0' values (e.g., 'Zero hours | 0 | 20.00 | 0.00') instead of empty cells, and parameters are BigDecimal not Integer/null-supporting types
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
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-15-reis-discount

**19/20** · 1519914 tokens · 431214ms

- ✅ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
- ✅ **2.2-children-flat-discount**: Children's flat 20% discount is represented — either as a row in the eligibility table or a separate note. It does not follow the ladder.
- ✅ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers derived from the 'every fifth trip' rule, with boundary values or value sets showing which ticket counts produce which discount. The maximum 40% discount is represented.
- ✅ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
- ✅ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
- ✅ **2.15-ticket-count-uses-value-sets**: The discount ladder table uses value sets for the ticket count column to group counts within the same tier (e.g. {5, 6, 7, 8, 9} or {5, 9} for min/max → 5%) — not one row per boundary value. The 'every fifth trip' rule creates tiers; the table should express tiers, not enumerate boundaries.
- ✅ **2.16-no-duplicate-tier-mapping**: The tier-to-discount mapping is expressed once — not repeated across multiple tables. A price application table (if present) should test the arithmetic (base price × discount → final price) without re-enumerating which ticket counts map to which tier.
- ✅ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ✅ **2.11-format-description**: @Description is present on at least the class or the tier method and provides context beyond what the table rows already express — such as the discount ladder structure, eligibility rules, or application context. Does NOT merely restate the column names or summarise what the rows show.
- ✅ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **2.14-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **2.17-zone-irrelevance-visible**: The eligibility/passenger-type table includes a Zone column (or equivalent) with a value set to show that the discount applies regardless of zone — rather than silently omitting zone. The requirement explicitly says 'regardless of the zones you travel in'.
- ❌ **2.18-adult-senior-value-set**: Adult and senior are expressed as a value set {ADULT, SENIOR} (or equivalent) in at least one row of the eligibility table, since they follow identical discount rules. Not enumerated as separate rows with the same outcome.
  > ReisDiscountRuleTest has separate rows for ADULT and SENIOR in rule-selection table, not combined as {ADULT, SENIOR} value set
- ✅ **2.19-depth-all-tiers**: All 9 discount tiers are represented in the ladder table: 0% (tickets 1-4), 5% (5-9), 10% (10-14), 15% (15-19), 20% (20-24), 25% (25-29), 30% (30-34), 35% (35-39), and 40% (40+). Not just a subset of tiers.
- ✅ **2.20-readability-one-row-per-tier**: Each tier in the discount ladder is expressed as a single row with a value set for ticket counts — not split across multiple rows (e.g. separate rows for 'tier kicks in' and 'tier holds'). One tier = one row.
- ✅ **2.21-readability-relative-time**: The rolling window table expresses time relatively (e.g. '14 days ago', '30 days ago', or an integer 'Days ago' column) rather than using absolute dates, making the table readable without needing to know the reference date.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-22-event-registration-tt

**20/25** · 1032970 tokens · 203261ms

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
- ✅ **validation-includes-optional-fields**: Validation @TableTest includes Dietary Requirements and Accessibility Needs columns, with rows showing both null (blank) and non-null values are accepted.
- ❌ **descriptive-registration-date**: Registration date uses descriptive values (e.g. 'before cutoff', 'on cutoff', 'after cutoff') with a @TypeConverter method converting to actual dates — not raw date literals.
  > appliesPricingAndDiscountRules table uses raw date literals '2025-06-01', '2025-01-01', '2025-02-28' instead of descriptive values; no @TypeConverter present
- ❌ **cutoff-date-column-if-literal-dates**: If registration dates are literal (e.g. '2025-02-28'), the early-bird cutoff date appears as a separate policy column so the reader can see both dates.
  > appliesPricingAndDiscountRules uses literal dates but does not include an 'Early-bird cutoff' or 'Cutoff date' column; cutoff is only mentioned in @Description
- ❌ **description-no-irrelevant-information**: If @Description is present, it does not include information that is already visible in the table columns or that can be derived from the table structure.
  > @Description for appliesPricingAndDiscountRules states 'Early-bird: registrations before 2025-03-01 get 20% off', and base price is £100—these rules are demonstrated by the table rows themselves
- ❌ **discount-column-preferred**: Output column is 'Discount?' (percentage or amount off) rather than 'Price?' — making verification simpler. If 'Price?' is used, both 'Base price' and 'Price?' columns must be present.
  > Table has 'Price?' column but no 'Base price' column; 'Discount?' is present, but reader must infer the base price (£100) from @Description, not the table
- ❌ **single-assertion-in-method**: Each @TableTest method has a single, uniform assertion pattern applied to all rows — not different assertions per scenario. Validation checks one thing (e.g. error message or acceptance status); pricing checks one thing (e.g. discount or price). No if/switch in assertion logic.
  > Method has 2 assertions; Method has 3 assertions
- ✅ **optional-fields-has-expected-column**: The optional-fields acceptance table includes an expectation/output column (e.g. 'Approved?', 'Result?', 'Status?') asserting that each combination is accepted.
- ✅ **description-no-redundant-field-values**: Scenario descriptions do not include specific field values that are already visible in the table or that serve as 'any valid value' placeholders.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-23-loan-approval-tt

**15/18** · 552848 tokens · 170182ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **threshold-as-column**: Policy thresholds (650/600 credit scores) appear in a dedicated policy column (e.g. 'Credit threshold') separate from the applicant's actual credit score column.
  > The table has 'Credit Score' column but no separate 'Credit threshold' column. Thresholds (650, 600) are only mentioned in descriptions and scenario names, not as dedicated input columns.
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
- ❌ **separates-age-credit-income**: Age boundary policy, credit score categorisation, and income status are separated into distinct @TableTest methods, with a method combining these for the expected verdict.
  > Only one combined @TableTest method 'evaluatesApprovalByAgeAdjustedThresholdAndIncome' covers all three concerns (age, credit, income) together. No separate methods for each concern, and no combining method afterward.
- ❌ **description-no-redundant-field-values**: Scenario descriptions and @Description text do not include specific field values that are already visible in the table or that serve as 'any valid value' placeholders.
  > @Description states 'credit score above 650' and 'score above 600' — values already visible in table rows ('651', '650', '601', '600'). Also restates the logic visible from table data.
- ✅ **depth-stable-income-effect**: The standardApplicantCreditThreshold table (or equivalent non-senior table) clearly documents the effect of stable income: rows show that above-threshold credit score + stable income = APPROVED, and above-threshold credit score + no stable income = REJECTED.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-26-convert-from-kotest

**17/20** · 1355817 tokens · 266287ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells. Rows with no options use a blank cell or [:].
  > Options appear as separate columns: 'Fragile | true', 'Insured Value |', 'Handling |' in individual @TableTest methods, not collapsed into a single map column like '[fragile: true, insuredValue: 500]'
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
  > Only 'parseShippingZone' @TypeConverter is defined. No @TypeConverter for Map to PackageOptions exists; options are passed as individual parameters.
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
- ✅ **output-is-kotlin**: The generated test file is written in Kotlin (.kt), not Java. Uses Kotlin syntax: fun, val/var, Kotlin imports.
- ❌ **no-kotest-syntax**: Output contains no Kotest syntax: no forAll, row(), withData, shouldBe, context(), FunSpec, or Kotest imports.
  > Found: it(
- ✅ **has-tabletest-dependency**: The build file (build.gradle.kts) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against ShippingCostCalculator
- ✅ **kotest-dependency-removed**: The build file no longer declares Kotest dependencies — the old framework is fully removed, not just the test code.

### ⚠️ Eval eval-28-convert-from-methodsource

**16/18** · 850665 tokens · 301504ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells. Rows with no options use a blank cell or [:].
  > The test uses separate boolean/BigDecimal/String parameters (fragile, insuredValue, handling) in the @TableTest table, not a collapsed map like [fragile: true, insuredValue: 500].
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
  > No @TypeConverter method is present in the test file. Options are constructed directly via PackageOptions.setters.
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
- ✅ **tests-pass**: The generated tests pass when executed against ShippingCostCalculator

### ⚠️ Eval eval-30-order-splitting-tt

**19/20** · 1652673 tokens · 409438ms

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
  > minimizesShipmentCountAcrossWarehouses uses 'Products' and 'Stock By Warehouse' columns with product identities (P1, P2, P3), not scalar quantities
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

## Variant vs Official (iterations 38, 37, 35 merged)

### Per-Eval Resource Comparison

| Eval | Pass (off) | Pass (var) | Tokens (off) | Tokens (var) | Tok Δ | Cost (off) | Cost (var) | Cost Δ | Time (off) | Time (var) | Time Δ |
|------|------------|------------|--------------|--------------|-------|------------|------------|--------|------------|------------|--------|
| eval-2-parse-dates | 13/15 | 11/15 | 609045 | 611286 | +0% | $0.3650 | $0.3534 | -3% | 64.1s | 81.7s | +27% |
| eval-14-weekly-pay | 16/20 | 19/20 | 700566 | 1173080 | +67% | $0.5940 | $0.8250 | +39% | 171.1s | 271.7s | +59% |
| eval-15-reis-discount | 12/20 | 19/20 | 3752644 | 1519914 | -59% | $2.1646 | $1.2324 | -43% | 553.4s | 431.2s | -22% |
| eval-22-event-registration-tt | 22/25 | 20/25 | 490058 | 1032970 | +111% | $0.4791 | $0.6873 | +43% | 141.8s | 203.3s | +43% |
| eval-23-loan-approval-tt | 13/18 | 15/18 | 743981 | 552848 | -26% | $0.6012 | $0.5301 | -12% | 171.0s | 170.2s | +-0% |
| eval-26-convert-from-kotest | 16/20 | 17/20 | 1483578 | 1355817 | -9% | $1.1802 | $0.9558 | -19% | 363.3s | 266.3s | -27% |
| eval-28-convert-from-methodsource | 15/18 | 16/18 | 1192800 | 850665 | -29% | $1.0527 | $0.8672 | -18% | 320.5s | 301.5s | -6% |
| eval-30-order-splitting-tt | 19/20 | 19/20 | 967814 | 1652673 | +71% | $1.0599 | $1.2562 | +19% | 366.7s | 409.4s | +12% |
| **Totals (8 comparable)** | **126/156** | **136/156** | **9940486** | **8749253** | **-12%** | **$7.4967** | **$6.7073** | **-11%** | **2151.9s** | **2135.2s** | **-1%** |

**Comparable summary (8 evals in both):**

| Source | Pass Rate | Tokens | Cost | Time |
|--------|-----------|--------|------|------|
| next (iter 1) | 136/156 (87.2%) | 8749253 | $6.7073 | 2135.2s |
| official | 126/156 (80.8%) | 9940486 | $7.4967 | 2151.9s |
| **Δ** | | **-12%** | **-11%** | **-1%** |

### Per-Assertion Comparison

| Eval | Assertion | official | next |
|------|-----------|----------|------|
| eval-2-parse-dates | type-conversion-addressed | ❌ | ✅ |
| eval-2-parse-dates | concerns-decomposed | ✅ | ❌ |
| eval-2-parse-dates | minimal-rows-per-concern | ✅ | ❌ |
| eval-2-parse-dates | separates-valid-and-invalid | ✅ | ❌ |
| eval-14-weekly-pay | 1.11-format-description | ❌ | ✅ |
| eval-14-weekly-pay | 1.15-format-clean-method | ❌ | ✅ |
| eval-14-weekly-pay | separates-classification-and-calculation | ❌ | ✅ |
| eval-15-reis-discount | 2.4-depth-rolling-window-boundary | ❌ | ✅ |
| eval-15-reis-discount | 2.9-correctness-value-set-tier-semantics | ❌ | ✅ |
| eval-15-reis-discount | 2.15-ticket-count-uses-value-sets | ❌ | ✅ |
| eval-15-reis-discount | 2.17-zone-irrelevance-visible | ❌ | ✅ |
| eval-15-reis-discount | 2.19-depth-all-tiers | ❌ | ✅ |
| eval-15-reis-discount | 2.20-readability-one-row-per-tier | ❌ | ✅ |
| eval-15-reis-discount | 2.21-readability-relative-time | ❌ | ✅ |
| eval-22-event-registration-tt | descriptive-registration-date | ✅ | ❌ |
| eval-22-event-registration-tt | cutoff-date-column-if-literal-dates | ✅ | ❌ |
| eval-23-loan-approval-tt | concerns-decomposed | ❌ | ✅ |
| eval-23-loan-approval-tt | minimal-rows-per-concern | ❌ | ✅ |
| eval-26-convert-from-kotest | kotest-dependency-removed | ❌ | ✅ |
| eval-28-convert-from-methodsource | no-if-switch-in-method | ❌ | ✅ |

### Load-Bearing Assertions

Assertions that pass with official skill but fail with next variant:

- eval-2-parse-dates: `concerns-decomposed`
- eval-2-parse-dates: `minimal-rows-per-concern`
- eval-2-parse-dates: `separates-valid-and-invalid`
- eval-22-event-registration-tt: `descriptive-registration-date`
- eval-22-event-registration-tt: `cutoff-date-column-if-literal-dates`

### Attention Needed

Evals with failed assertions or disproportionate resource usage (>2x official):

| Eval | Issue | Details |
|------|-------|---------|
| eval-22-event-registration-tt | High tokens | 1032970 vs 490058 (+111%) |
| eval-2-parse-dates | Failed assertions | `concerns-decomposed`, `minimal-rows-per-concern`, `separates-valid-and-invalid` |
| eval-22-event-registration-tt | Failed assertions | `descriptive-registration-date`, `cutoff-date-column-if-literal-dates` |

