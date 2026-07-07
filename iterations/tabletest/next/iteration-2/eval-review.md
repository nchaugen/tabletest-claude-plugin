# Eval Review — tabletest variant=next, Iteration 2

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-haiku-4-5 · **Date:** 2026-07-07 · **Evals:** 8

## Summary

137/156 (87.8%) · 8456194 tokens · 2485.4s · $7.4252

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

## Delta vs Iteration 1

**Regressions (9):**
- ❌ eval-14-weekly-pay: `1.15-format-clean-method`
- ❌ eval-15-reis-discount: `2.9-correctness-value-set-tier-semantics`
- ❌ eval-15-reis-discount: `2.15-ticket-count-uses-value-sets`
- ❌ eval-15-reis-discount: `2.19-depth-all-tiers`
- ❌ eval-15-reis-discount: `2.20-readability-one-row-per-tier`
- ❌ eval-15-reis-discount: `minimal-rows-per-concern`
- ❌ eval-22-event-registration-tt: `description-no-redundant-field-values`
- ❌ eval-23-loan-approval-tt: `concerns-decomposed`
- ❌ eval-23-loan-approval-tt: `minimal-rows-per-concern`

**Improvements (10):**
- ✅ eval-2-parse-dates: `empty-string-uses-quotes`
- ✅ eval-2-parse-dates: `concerns-decomposed`
- ✅ eval-2-parse-dates: `minimal-rows-per-concern`
- ✅ eval-2-parse-dates: `separates-valid-and-invalid`
- ✅ eval-15-reis-discount: `2.18-adult-senior-value-set`
- ✅ eval-22-event-registration-tt: `descriptive-registration-date`
- ✅ eval-22-event-registration-tt: `cutoff-date-column-if-literal-dates`
- ✅ eval-26-convert-from-kotest: `no-kotest-syntax`
- ✅ eval-28-convert-from-methodsource: `options-as-map`
- ✅ eval-28-convert-from-methodsource: `options-type-converter`

## Resource Comparison vs Iteration 1

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-2-parse-dates | 15/15 | 11/15 | 1048070 | 611286 | 133.4 | 81.7 |
| eval-14-weekly-pay | 18/20 | 19/20 | 660607 | 1173080 | 230.6 | 271.7 |
| eval-15-reis-discount | 15/20 | 19/20 | 903931 | 1519914 | 353.1 | 431.2 |
| eval-22-event-registration-tt | 21/25 | 20/25 | 696253 | 1032970 | 119.3 | 203.3 |
| eval-23-loan-approval-tt | 13/18 | 15/18 | 660004 | 552848 | 167.4 | 170.2 |
| eval-26-convert-from-kotest | 18/20 | 17/20 | 1342070 | 1355817 | 315.6 | 266.3 |
| eval-28-convert-from-methodsource | 18/18 | 16/18 | 1297515 | 850665 | 368.0 | 301.5 |
| eval-30-order-splitting-tt | 19/20 | 19/20 | 1847744 | 1652673 | 798.0 | 409.4 |

## Per-Eval Results

### ✅ Eval eval-2-parse-dates

**15/15** · 1048070 tokens · 133421ms

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
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-14-weekly-pay

**18/20** · 660607 tokens · 230632ms

- ✅ **1.2-error-has-expected-column**: The error/rejection table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
- ✅ **1.3-depth-overtime-boundary**: Overtime threshold boundary is covered: at least a value at the threshold (40) and just above (41 or similar). Not just values well below and well above.
- ✅ **1.4-depth-combined-scenario**: A combined scenario is present: a row with weekday + Sunday + holiday hours, testing the composition of all hour types.
- ✅ **1.5-depth-error-edge-cases**: Error/edge cases covered: at least negative hours (floored at zero) and negative or missing rate (rejected).
- ❌ **1.6-readability-empty-cells**: Sunday and Holiday hour columns use empty cells (not 0) when those inputs are not relevant to a scenario — making it easier to spot which rows involve weekend/holiday hours. This is a readability preference; parameter types should be Integer (not int) to support null from blank cells.
  > All rows use explicit '0' values (e.g., '| 0 |') rather than empty cells; parameters are 'double' not 'Integer'
- ✅ **1.7-readability-scenario-names**: Scenario names describe work patterns (e.g. 'Part-time', 'Five hours overtime', 'Full Sunday shift') — not 'Test case 1' or expected outcomes.
- ✅ **1.8-correctness-expected-values**: Expected values in intermediate and total columns are arithmetically correct for every row, consistent with the formulas in @Description.
- ✅ **1.9-correctness-value-set-semantics**: Value sets are only used where all values in the set produce the same expected result (e.g. any rate × 0 = 0). Not used where results would differ.
- ✅ **1.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ✅ **1.11-format-description**: @Description is present on at least the pay calculation method and provides context beyond what the table rows already express — such as the overtime threshold (40 hours), premium multipliers (Sunday 2×, Holiday 2.5×), or other fixed assumptions not visible in the table. Does NOT merely restate the column names or summarise what the rows show.
- ✅ **1.12-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **1.13-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **1.14-depth-zero-rate**: A row where the hourly rate is zero, showing that pay is zero regardless of hours worked. This is a distinct edge case from negative rate (which is rejected).
- ❌ **1.15-format-clean-method**: The test method body contains no if/ternary null-handling or parsing logic. Null-to-default conversion (e.g. blank Sunday hours → 0) is handled via a @TypeConverter method or a private helper, keeping the test method limited to arrange/act/assert.
  > rejectsNegativeHourlyRate method contains 'if (throws_ != null) { assertThrows... } else { assertDoesNotThrow... }' null-handling logic in the test body
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Weekday hours', 'Sunday hours', 'Hourly rate', 'Weekly pay?') — not implementation terms like 'weekdayHrs', 'int', 'param1'.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ✅ **separates-classification-and-calculation**: Payable hours categorisation (1x regular, 1.5x overtime, 2x Sunday/holiday) and pay calculation (hours × rate) are in separate @TableTest methods
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-15-reis-discount

**15/20** · 903931 tokens · 353118ms

- ✅ **2.1-decomposition-concern-separation**: Discount ladder, and traveller eligibility are in separate tables. Rolling window counting as a third table is desirable.
- ✅ **2.2-children-flat-discount**: Children's flat 20% discount is represented — either as a row in the eligibility table or a separate note. It does not follow the ladder.
- ✅ **2.3-depth-tier-boundaries**: The discount ladder has multiple tiers derived from the 'every fifth trip' rule, with boundary values or value sets showing which ticket counts produce which discount. The maximum 40% discount is represented.
- ✅ **2.4-depth-rolling-window-boundary**: The 30-day rolling window boundary is tested: a ticket at exactly 30 days is included, at 31 days is excluded.
- ❌ **2.9-correctness-value-set-tier-semantics**: Value sets in the tier table contain exactly the values that produce the same discount. No cross-tier contamination.
  > discountLadderByRecentTicketCount uses {ADULT, SENIOR} as a value set for traveler category (not tiers), and uses individual rows for ticket counts rather than value sets per tier
- ❌ **2.15-ticket-count-uses-value-sets**: The discount ladder table uses value sets for the ticket count column to group counts within the same tier (e.g. {5, 6, 7, 8, 9} or {5, 9} for min/max → 5%) — not one row per boundary value. The 'every fifth trip' rule creates tiers; the table should express tiers, not enumerate boundaries.
  > discountLadderByRecentTicketCount uses one row per boundary (3, 4, 9, 24, 39, 49, 199) instead of value sets like {5-9} for each tier
- ✅ **2.16-no-duplicate-tier-mapping**: The tier-to-discount mapping is expressed once — not repeated across multiple tables. A price application table (if present) should test the arithmetic (base price × discount → final price) without re-enumerating which ticket counts map to which tier.
- ✅ **2.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
- ✅ **2.11-format-description**: @Description is present on at least the class or the tier method and provides context beyond what the table rows already express — such as the discount ladder structure, eligibility rules, or application context. Does NOT merely restate the column names or summarise what the rows show.
- ✅ **2.13-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **2.14-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **2.17-zone-irrelevance-visible**: The eligibility/passenger-type table includes a Zone column (or equivalent) with a value set to show that the discount applies regardless of zone — rather than silently omitting zone. The requirement explicitly says 'regardless of the zones you travel in'.
- ✅ **2.18-adult-senior-value-set**: Adult and senior are expressed as a value set {ADULT, SENIOR} (or equivalent) in at least one row of the eligibility table, since they follow identical discount rules. Not enumerated as separate rows with the same outcome.
- ❌ **2.19-depth-all-tiers**: All 9 discount tiers are represented in the ladder table: 0% (tickets 1-4), 5% (5-9), 10% (10-14), 15% (15-19), 20% (20-24), 25% (25-29), 30% (30-34), 35% (35-39), and 40% (40+). Not just a subset of tiers.
  > discountLadderByRecentTicketCount covers 0%, 5%, 10%, 25%, 40% but omits 15%, 20%, 30%, 35% tiers; only 5 of 9 tiers shown
- ❌ **2.20-readability-one-row-per-tier**: Each tier in the discount ladder is expressed as a single row with a value set for ticket counts — not split across multiple rows (e.g. separate rows for 'tier kicks in' and 'tier holds'). One tier = one row.
  > discountLadderByRecentTicketCount enumerates multiple rows per tier (e.g. rows for 3, 4, 8, 9, 24, 39, 49, 199) without value sets grouping ticket counts
- ✅ **2.21-readability-relative-time**: The rolling window table expresses time relatively (e.g. '14 days ago', '30 days ago', or an integer 'Days ago' column) rather than using absolute dates, making the table readable without needing to know the reference date.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
  > discountLadderByRecentTicketCount has 8 rows for a single concern (tiers); should have ~9 rows (one per tier) with value sets, not enumerated boundaries
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-22-event-registration-tt

**21/25** · 696253 tokens · 119306ms

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
- ✅ **concerns-decomposed**: Multiple tables are used, each addressing a distinct concern — not one monolithic table.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations.
- ✅ **separates-validation-and-pricing**: Input validation and pricing/discount calculation are in separate @TableTest methods.
- ✅ **validation-includes-optional-fields**: Validation @TableTest includes Dietary Requirements and Accessibility Needs columns, with rows showing both null (blank) and non-null values are accepted.
- ✅ **descriptive-registration-date**: Registration date uses descriptive values with a @TypeConverter method converting to actual dates — not raw date literals.
- ✅ **cutoff-date-column-if-literal-dates**: If registration dates are literal, the early-bird cutoff date appears as a separate policy column. Passes automatically if descriptive date values are used.
- ❌ **description-no-irrelevant-information**: If @Description is present, it does not include information already visible in table columns or derived from table structure.
  > @Description for validatesRegistration states 'registrationDate and groupSize are fixed to values' — these are input implementation details not visible in the table; also states 'name is validated before email' which drives error precedence, a rule not visible in columns.
- ❌ **discount-column-preferred**: Output column is 'Discount?' (percentage or amount off) rather than 'Price?' — or if 'Price?' is used, both 'Base price' and 'Price?' columns must be present.
  > Table uses both 'Discount?' and 'Price?' columns, but no 'Base price' column present. Reader must infer £100 base from @Description, not see it in the table.
- ❌ **single-assertion-in-method**: Each @TableTest method has a single, uniform assertion pattern applied to all rows — not different assertions per scenario. Validation checks one thing (e.g. error message or acceptance status); pricing checks one thing (e.g. discount or price). No if/switch in assertion logic.
  > Method has 2 assertions; Method has 3 assertions
- ✅ **optional-fields-has-expected-column**: The optional-fields acceptance table includes an expectation/output column asserting that each combination is accepted.
- ❌ **description-no-redundant-field-values**: Scenario descriptions do not include specific field values already visible in table or that serve as 'any valid value' placeholders.
  > @Description for calculatesPriceAndDiscount states 'registrationDate and groupSize are fixed to values that would otherwise be discount-eligible' — lists implementation details (fixed inputs, discount eligibility) redundant to table structure.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-23-loan-approval-tt

**13/18** · 660004 tokens · 167365ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **threshold-as-column**: Policy thresholds (650/600 credit scores) appear in a dedicated policy column (e.g. 'Credit threshold') separate from the applicant's actual credit score column.
  > The table has 'Credit Score' column but no separate 'Credit threshold' column. Thresholds (650/600) are only mentioned in @Description text, not as dedicated table columns.
- ✅ **concrete-domain-values**: Cell values use concrete domain terms (e.g. '70', '650', 'APPROVED') — not abstract codes.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Senior at lower threshold', 'Missing income') — not outcomes ('Approved', 'Rejected').
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Age', 'Credit score', 'Stable income', 'Decision?') — not code identifiers.
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Decision?' or 'Status?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table.
  > Only one @TableTest method exists. The table mixes age boundaries, credit score thresholds, stable income effects, and null income handling in a single 12-row table.
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations.
  > Single monolithic table with 12 rows covering multiple concerns (age boundaries, credit thresholds, income effects, null handling) — permutations and concerns not separated.
- ❌ **separates-age-credit-income**: Age boundary policy, credit score categorisation, and income status are separated into distinct @TableTest methods.
  > Only one @TableTest method 'shouldEvaluateLoan' exists. No separate methods for age boundaries, credit categorization, or income status.
- ❌ **description-no-redundant-field-values**: Scenario descriptions and @Description text do not include specific field values that are already visible in the table.
  > @Description states 'senior status applies starting at age 65 (inclusive)' and 'thresholds are exclusive lower bounds: the score must be strictly greater' — these are implementation details and field values visible/testable in table rows.
- ✅ **depth-stable-income-effect**: The table clearly documents the effect of stable income: rows show above-threshold credit score + stable income = APPROVED, and above-threshold credit score + no stable income = REJECTED.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-26-convert-from-kotest

**18/20** · 1342070 tokens · 315578ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells. Rows with no options use a blank cell or [:].
  > Options are passed as `PackageOptions(isFragile=true, insuredValue=...)` objects, not collapsed into map columns in the table. Each table has separate Handling, Fragile, or Insured Value columns.
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
  > No @TypeConverter methods are visible in the test class. Options are constructed directly in test code, not via type conversion from table columns.
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
- ✅ **no-kotest-syntax**: Output contains no Kotest syntax: no forAll, row(), withData, shouldBe, context(), FunSpec, or Kotest imports.
- ✅ **has-tabletest-dependency**: The build file (build.gradle.kts) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against ShippingCostCalculator
- ✅ **kotest-dependency-removed**: The build file no longer declares Kotest dependencies — the old framework is fully removed, not just the test code.

### ✅ Eval eval-28-convert-from-methodsource

**18/18** · 1297515 tokens · 367989ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells. Rows with no options use a blank cell or [:].
- ✅ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
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

**19/20** · 1847744 tokens · 798000ms

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
  > choosesWarehouseCombinationMinimisingShipmentCount table still uses 'Items' column with product lists [cam/1/DELIVERY/home...] not scalar quantities
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
| eval-2-parse-dates | 13/15 | 15/15 | 609045 | 1048070 | +72% | $0.3650 | $0.5663 | +55% | 64.1s | 133.4s | +108% |
| eval-14-weekly-pay | 16/20 | 18/20 | 700566 | 660607 | -6% | $0.5940 | $0.6694 | +13% | 171.1s | 230.6s | +35% |
| eval-15-reis-discount | 12/20 | 15/20 | 3752644 | 903931 | -76% | $2.1646 | $0.9394 | -57% | 553.4s | 353.1s | -36% |
| eval-22-event-registration-tt | 22/25 | 21/25 | 490058 | 696253 | +42% | $0.4791 | $0.4740 | -1% | 141.8s | 119.3s | -16% |
| eval-23-loan-approval-tt | 13/18 | 13/18 | 743981 | 660004 | -11% | $0.6012 | $0.5218 | -13% | 171.0s | 167.4s | -2% |
| eval-26-convert-from-kotest | 16/20 | 18/20 | 1483578 | 1342070 | -10% | $1.1802 | $0.9570 | -19% | 363.3s | 315.6s | -13% |
| eval-28-convert-from-methodsource | 15/18 | 18/18 | 1192800 | 1297515 | +9% | $1.0527 | $1.0764 | +2% | 320.5s | 368.0s | +15% |
| eval-30-order-splitting-tt | 19/20 | 19/20 | 967814 | 1847744 | +91% | $1.0599 | $2.2209 | +110% | 366.7s | 798.0s | +118% |
| **Totals (8 comparable)** | **126/156** | **137/156** | **9940486** | **8456194** | **-15%** | **$7.4967** | **$7.4252** | **-1%** | **2151.9s** | **2485.4s** | **+15%** |

**Comparable summary (8 evals in both):**

| Source | Pass Rate | Tokens | Cost | Time |
|--------|-----------|--------|------|------|
| next (iter 2) | 137/156 (87.8%) | 8456194 | $7.4252 | 2485.4s |
| official | 126/156 (80.8%) | 9940486 | $7.4967 | 2151.9s |
| **Δ** | | **-15%** | **-1%** | **+15%** |

### Per-Assertion Comparison

| Eval | Assertion | official | next |
|------|-----------|----------|------|
| eval-2-parse-dates | type-conversion-addressed | ❌ | ✅ |
| eval-2-parse-dates | empty-string-uses-quotes | ❌ | ✅ |
| eval-14-weekly-pay | 1.11-format-description | ❌ | ✅ |
| eval-14-weekly-pay | separates-classification-and-calculation | ❌ | ✅ |
| eval-15-reis-discount | 2.4-depth-rolling-window-boundary | ❌ | ✅ |
| eval-15-reis-discount | 2.17-zone-irrelevance-visible | ❌ | ✅ |
| eval-15-reis-discount | 2.18-adult-senior-value-set | ❌ | ✅ |
| eval-15-reis-discount | 2.21-readability-relative-time | ❌ | ✅ |
| eval-15-reis-discount | minimal-rows-per-concern | ✅ | ❌ |
| eval-22-event-registration-tt | description-no-redundant-field-values | ✅ | ❌ |
| eval-26-convert-from-kotest | no-kotest-syntax | ❌ | ✅ |
| eval-26-convert-from-kotest | kotest-dependency-removed | ❌ | ✅ |
| eval-28-convert-from-methodsource | options-as-map | ❌ | ✅ |
| eval-28-convert-from-methodsource | options-type-converter | ❌ | ✅ |
| eval-28-convert-from-methodsource | no-if-switch-in-method | ❌ | ✅ |

### Load-Bearing Assertions

Assertions that pass with official skill but fail with next variant:

- eval-15-reis-discount: `minimal-rows-per-concern`
- eval-22-event-registration-tt: `description-no-redundant-field-values`

### Attention Needed

Evals with failed assertions or disproportionate resource usage (>2x official):

| Eval | Issue | Details |
|------|-------|---------|
| eval-30-order-splitting-tt | High cost | $2.2209 vs $1.0599 (+110%) |
| eval-15-reis-discount | Failed assertions | `minimal-rows-per-concern` |
| eval-22-event-registration-tt | Failed assertions | `description-no-redundant-field-values` |

