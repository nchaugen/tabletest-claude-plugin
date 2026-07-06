# Eval Review — tabletest, Iteration 32

**Model:** sonnet · **Date:** 2026-04-12 · **Evals:** 3

## Summary

38/51 (74.5%) · 632606 tokens · 315.1s · $0.7856

## Delta vs Iteration 31

**Regressions (10):**
- ❌ eval-14-weekly-pay: `1.10-format-displayname`
- ❌ eval-14-weekly-pay: `1.11-format-description`
- ❌ eval-14-weekly-pay: `1.14-depth-zero-rate`
- ❌ eval-14-weekly-pay: `1.15-format-clean-method`
- ❌ eval-14-weekly-pay: `concerns-decomposed`
- ❌ eval-14-weekly-pay: `minimal-rows-per-concern`
- ❌ eval-14-weekly-pay: `has-tabletest-dependency`
- ❌ eval-25-convert-from-spock: `concerns-decomposed`
- ❌ eval-25-convert-from-spock: `has-descriptive-title`
- ❌ eval-25-convert-from-spock: `tests-pass`

**Improvements (7):**
- ✅ eval-1-convert-repetitive-tests: `has-tabletest-dependency`
- ✅ eval-14-weekly-pay: `1.6-readability-empty-cells`
- ✅ eval-14-weekly-pay: `1.12-format-annotation-order`
- ✅ eval-14-weekly-pay: `compiles`
- ✅ eval-25-convert-from-spock: `options-as-map`
- ✅ eval-25-convert-from-spock: `options-type-converter`
- ✅ eval-25-convert-from-spock: `has-tabletest-dependency`

## Resource Comparison vs Iteration 31

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-1-convert-repetitive-tests | 13/13 | 12/13 | 189292 | 310432 | 24.9 | 58.6 |
| eval-14-weekly-pay | 11/20 | 15/20 | 45273 | 404915 | 74.7 | 354.7 |
| eval-25-convert-from-spock | 14/18 | 14/18 | 398041 | 625338 | 215.5 | 380.7 |

## Per-Eval Results

### ✅ Eval eval-1-convert-repetitive-tests

**13/13** · 189292 tokens · 24899ms

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
- ✅ **has-tabletest-dependency**: The build file (pom.xml) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against DiscountService

### ⚠️ Eval eval-14-weekly-pay

**11/20** · 45273 tokens · 74733ms

- ✅ **1.2-error-has-expected-column**: The error/rejection table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
- ✅ **1.3-depth-overtime-boundary**: Overtime threshold boundary is covered: at least a value at the threshold (40) and just above (41 or similar). Not just values well below and well above.
- ✅ **1.4-depth-combined-scenario**: A combined scenario is present: a row with weekday + Sunday + holiday hours, testing the composition of all hour types.
- ❌ **1.5-depth-error-edge-cases**: Error/edge cases covered: at least negative hours (floored at zero) and negative or missing rate (rejected).
  > Table covers negative rate ('Negative hourly rate' row) but does NOT include a row testing negative hours (floored at zero). Only non-negative hour values appear.
- ✅ **1.6-readability-empty-cells**: Sunday and Holiday hour columns use empty cells (not 0) when those inputs are not relevant to a scenario — making it easier to spot which rows involve weekend/holiday hours. This is a readability preference; parameter types should be Integer (not int) to support null from blank cells.
- ✅ **1.7-readability-scenario-names**: Scenario names describe work patterns (e.g. 'Part-time', 'Five hours overtime', 'Full Sunday shift') — not 'Test case 1' or expected outcomes.
- ✅ **1.8-correctness-expected-values**: Expected values in intermediate and total columns are arithmetically correct for every row, consistent with the formulas in @Description.
- ✅ **1.9-correctness-value-set-semantics**: Value sets are only used where all values in the set produce the same expected result (e.g. any rate × 0 = 0). Not used where results would differ.
- ❌ **1.10-format-displayname**: @DisplayName is present on test methods, OR method names read as clear descriptive titles when converted from camelCase/snake_case.
  > No @DisplayName annotations present in the response. This is a spec-by-example markdown table, not Java code with annotations. No test methods provided.
- ❌ **1.11-format-description**: @Description is present on at least the pay calculation method and provides context beyond what the table rows already express — such as the overtime threshold (40 hours), premium multipliers (Sunday 2×, Holiday 2.5×), or other fixed assumptions not visible in the table. Does NOT merely restate the column names or summarise what the rows show.
  > No @Description annotation present. The 'Pay derivations' section explains calculations (e.g. '40 × 10 + n × 15') but this is markdown, not a Java annotation. No premium multipliers (2×, 2.5×) mentioned explicitly.
- ✅ **1.12-format-annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **1.13-format-description-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **1.14-depth-zero-rate**: A row where the hourly rate is zero, showing that pay is zero regardless of hours worked. This is a distinct edge case from negative rate (which is rejected).
  > Table includes only negative rate row (-5.00). No row with hourly rate = 0.00 to test zero-rate edge case separately from negative-rate rejection.
- ❌ **1.15-format-clean-method**: The test method body contains no if/ternary null-handling or parsing logic. Null-to-default conversion (e.g. blank Sunday hours → 0) is handled via a @TypeConverter method or a private helper, keeping the test method limited to arrange/act/assert.
  > No Java test method provided; this is markdown table specification only. Cannot assess method body implementation details.
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Weekday hours', 'Sunday hours', 'Hourly rate', 'Weekly pay?') — not implementation terms like 'weekdayHrs', 'int', 'param1'.
- ❌ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
  > Single table mixes normal pay calculation (rows 1–11) and error cases (row 12 with negative rate). Should be separated into 'Valid inputs' and 'Invalid/error inputs' tables.
- ❌ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
  > Single table contains both calculation and error logic. Without separation, cannot assess minimality per concern. Rows could be pruned if error handling were in a separate table.
- ❌ **separates-classification-and-calculation**: Payable hours categorisation (1x regular, 1.5x overtime, 2x Sunday/holiday) are in separate @TableTest methods from pay calculation (hours × rate)
  > No separate @TableTest methods provided. Single merged table shows only final pay calculation. No table shows the intermediate step of classifying hours into regular/overtime/Sunday/holiday categories.
- ❌ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
  > Build file does not contain tabletest-junit dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-25-convert-from-spock

**14/18** · 398041 tokens · 215504ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells. Rows with no options use a blank cell or [:].
- ✅ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
- ✅ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table — not as three separate length/width/height columns
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
- ❌ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern — not one monolithic table mixing base rates, surcharges, dimensional weight, and carrier equivalence
  > Only one @TableTest method 'shouldCalculateShippingCost' present; all 15 scenarios (base rates, fragile surcharge, dimensional weight, carrier equivalence) are in a single table
- ❌ **numeric-types-correct**: Weight and cost parameters use BigDecimal (not double or String). Dimensions use Integer or int.
  > Method signature shows 'double weight' parameter; should be BigDecimal. Dimensions correctly use 'List<Integer>'. Cost correctly uses 'BigDecimal expectedCost'
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('EU express, light package', 'Oversized with fragile') — not outcomes ('8.00', 'Surcharge applied') or generic labels ('Test 1').
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ✅ **business-language-columns**: Column names use domain language ('Zone', 'Weight (kg)', 'Dimensions', 'Options', 'Cost?') — not code identifiers ('shippingZone', 'weightKg', 'dims')
- ✅ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ❌ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
  > Method 'shouldCalculateShippingCost' lacks @DisplayName annotation; no descriptive titles present on any test method
- ✅ **no-groovy-syntax**: Output contains no Groovy or Spock syntax: no def, where:, expect:, given:, GString interpolation (#variable), Spock assertions (thrown(), old()), or Groovy list literals ([] as Set).
- ✅ **has-tabletest-dependency**: The build file (build.gradle.kts) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ❌ **tests-pass**: The generated tests pass when executed against ShippingCostCalculator
  > Tests failed: 
34 tests completed, 13 failed

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':test'.
> There were failing tests. See the report at: file:///private/var/folde

