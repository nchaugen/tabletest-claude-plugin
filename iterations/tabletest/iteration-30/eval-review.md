# Eval Review — tabletest, Iteration 30

**Model:** sonnet · **Date:** 2026-04-12 · **Evals:** 3

## Summary

29/51 (56.9%) · 2409669 tokens · 753.3s · $1.9928

## Delta vs Iteration 29

**Regressions (15):**
- ❌ eval-1-convert-repetitive-tests: `has-tabletest-annotation`
- ❌ eval-1-convert-repetitive-tests: `has-question-mark-column`
- ❌ eval-1-convert-repetitive-tests: `has-three-data-rows`
- ❌ eval-1-convert-repetitive-tests: `single-assertion-in-method`
- ❌ eval-1-convert-repetitive-tests: `no-if-switch-in-method`
- ❌ eval-1-convert-repetitive-tests: `scenario-column-present`
- ❌ eval-1-convert-repetitive-tests: `has-tabletest-dependency`
- ❌ eval-14-weekly-pay: `has-tabletest-dependency`
- ❌ eval-25-convert-from-spock: `has-tabletest-annotation`
- ❌ eval-25-convert-from-spock: `uses-value-sets`
- ❌ eval-25-convert-from-spock: `scenario-column-present`
- ❌ eval-25-convert-from-spock: `scenario-names-describe-conditions`
- ❌ eval-25-convert-from-spock: `has-question-mark-column`
- ❌ eval-25-convert-from-spock: `business-language-columns`
- ❌ eval-25-convert-from-spock: `has-tabletest-dependency`

**Improvements (6):**
- ✅ eval-14-weekly-pay: `1.8-correctness-expected-values`
- ✅ eval-25-convert-from-spock: `concerns-decomposed`
- ✅ eval-25-convert-from-spock: `numeric-types-correct`
- ✅ eval-25-convert-from-spock: `has-descriptive-title`
- ✅ eval-25-convert-from-spock: `compiles`
- ✅ eval-25-convert-from-spock: `tests-pass`

## Resource Comparison vs Iteration 29

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-1-convert-repetitive-tests | 6/13 | 10/10 | 217728 | 66658 | 32.7 | 16.6 |
| eval-14-weekly-pay | 16/20 | 14/18 | 938833 | 194257 | 255.5 | 210.2 |
| eval-25-convert-from-spock | 7/18 | 8/17 | 1253108 | 104664 | 465.1 | 127.7 |

## Per-Eval Results

### ⚠️ Eval eval-1-convert-repetitive-tests

**6/13** · 217728 tokens · 32674ms

- ❌ **has-tabletest-annotation**: Output contains a @TableTest annotation
  > No @TableTest annotation found
- ❌ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Discount?' or 'Expected?')
  > No table headers found
- ❌ **has-three-data-rows**: Table has exactly 3 data rows (GOLD, SILVER, BRONZE)
  > Found 0 data row(s)
- ❌ **single-assertion-in-method**: Test method body contains only one assertEquals (or equivalent assertion) — not three
  > No @TableTest method bodies found
- ❌ **no-if-switch-in-method**: Test method body contains no if or switch statements
  > No @TableTest method bodies found
- ❌ **scenario-column-present**: Table has a scenario/description column as the leftmost column
  > No table headers found
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **has-tabletest-dependency**: The build file (pom.xml) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
  > Build file does not contain tabletest-junit dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against DiscountService

### ⚠️ Eval eval-14-weekly-pay

**16/20** · 938833 tokens · 255537ms

- ✅ **1.2-error-has-expected-column**: The error/rejection table has an expected column (e.g. 'Throws?' or 'Exception?') specifying the exception type per row — rather than hardcoding the exception type in @Description or the method name.
- ✅ **1.3-depth-overtime-boundary**: Overtime threshold boundary is covered: at least a value at the threshold (40) and just above (41 or similar). Not just values well below and well above.
- ✅ **1.4-depth-combined-scenario**: A combined scenario is present: a row with weekday + Sunday + holiday hours, testing the composition of all hour types.
- ❌ **1.5-depth-error-edge-cases**: Error/edge cases covered: at least negative hours (floored at zero) and negative or missing rate (rejected).
  > Table covers negative rate rejection but does NOT cover negative hours (no row with negative weekday/Sunday/holiday hours)
- ❌ **1.6-readability-empty-cells**: Sunday and Holiday hour columns use empty cells (not 0) when those inputs are not relevant to a scenario — making it easier to spot which rows involve weekend/holiday hours. This is a readability preference; parameter types should be Integer (not int) to support null from blank cells.
  > All rows use explicit 0 values (e.g., '| 0 |') rather than empty cells; parameters are double, not Integer
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
- ❌ **separates-classification-and-calculation**: Payable hours categorisation (1x regular, 1.5x overtime, 2x Sunday/holiday) and pay calculation (hours × rate) are in separate @TableTest methods
  > Single @TableTest method 'shouldCalculateWeeklyPay' combines both classification and full pay calculation; no separate table for hour classification
- ❌ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
  > Build file does not contain tabletest-junit dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

### ⚠️ Eval eval-25-convert-from-spock

**7/18** · 1253108 tokens · 465132ms

- ❌ **has-tabletest-annotation**: Output contains a @TableTest annotation
  > No @TableTest annotation found
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells. Rows with no options use a blank cell or [:].
  > Response mentions 'Insured Value' and 'Handling' columns use blank cells for null, indicating separate columns rather than collapsed map syntax like [fragile: true, insuredValue: 500]
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
  > Response describes '@TypeConverter parseBigDecimal' for handling BigDecimal, not a converter for Map to PackageOptions with defaults
- ❌ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table — not as three separate length/width/height columns
  > Response mentions '70×50×10 cm³' in context but does not explicitly confirm dimensions are represented as [L, W, H] list syntax in table
- ❌ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
  > No value set syntax {a, b} found
- ✅ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern — not one monolithic table mixing base rates, surcharges, dimensional weight, and carrier equivalence
- ✅ **numeric-types-correct**: Weight and cost parameters use BigDecimal (not double or String). Dimensions use Integer or int.
- ❌ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
  > No table headers found
- ❌ **scenario-names-describe-conditions**: Scenario names describe conditions ('EU express, light package', 'Oversized with fragile') — not outcomes ('8.00', 'Surcharge applied') or generic labels ('Test 1').
  > Response describes method names and table focus but does not show actual row-level scenario names or example conditions in the table data
- ❌ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
  > No table headers found
- ❌ **business-language-columns**: Column names use domain language ('Zone', 'Weight (kg)', 'Dimensions', 'Options', 'Cost?') — not code identifiers ('shippingZone', 'weightKg', 'dims')
  > Response mentions 'Insured Value' and 'Handling' columns but does not show full column headers; no explicit confirmation of business-language naming convention
- ❌ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
  > No @TableTest method bodies found
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **no-groovy-syntax**: Output contains no Groovy or Spock syntax: no def, where:, expect:, given:, GString interpolation (#variable), Spock assertions (thrown(), old()), or Groovy list literals ([] as Set).
- ❌ **has-tabletest-dependency**: The build file (build.gradle.kts) includes org.tabletest:tabletest-junit:1.2.1 as a test dependency
  > Build file does not contain tabletest-junit dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding
- ✅ **tests-pass**: The generated tests pass when executed against ShippingCostCalculator

