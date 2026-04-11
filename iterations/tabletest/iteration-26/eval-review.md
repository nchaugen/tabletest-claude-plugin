# Eval Review — Iteration 26

**Model:** sonnet · **Date:** 2026-04-05 · **Evals:** 4

## Summary

**with_skill:** 46/60 (76.7%) · 743954 tokens · 983.3s · $1.5768

## Delta vs Iteration 25

No changes.

## Resource Comparison vs Iteration 25

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-25-convert-from-spock | 10/15 | — | 200782 | — | 147.8 | — |
| eval-26-convert-from-kotest | 9/15 | — | 132257 | — | 180.1 | — |
| eval-27-convert-from-testng | 12/15 | — | 201361 | — | 159.2 | — |
| eval-28-convert-from-methodsource | 15/15 | — | 209554 | — | 496.3 | — |

## Per-Eval Results

### ⚠️ Eval eval-25-convert-from-spock [with_skill]

**10/15** · 200782 tokens · 147765ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options are collapsed into a single map column
  > Fragile | Insured value | Handling are three separate columns in the table, not collapsed into [fragile: true, insuredValue: 500] format
- ❌ **options-type-converter**: A @TypeConverter method is present
  > No @TypeConverter method in the output; PackageOptions created manually with if-null checks instead
- ✅ **dimensions-as-list**: Dimensions are represented as [L, W, H] list
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX}
- ❌ **concerns-decomposed**: Multiple @TableTest methods exist for distinct concerns
  > Only one @TableTest method (shouldCalculateShippingCost) present; all scenarios in single table
- ❌ **numeric-types-correct**: Weight and cost use BigDecimal; dimensions use Integer/int
  > Parameters: 'double weight...List<Integer> dimensions...double cost' — weight and cost are double, not BigDecimal
- ✅ **scenario-column-present**: Table has scenario/description column as leftmost
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **business-language-columns**: Column names use domain language
- ❌ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
  > Method contains: 'if (fragile) opts.setFragile(true); if (insuredValue != null)...; if (handling != null)...'
- ✅ **annotation-order**: Annotations in order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Test method has @DisplayName or descriptive method name
- ✅ **no-groovy-syntax**: Output contains no Groovy or Spock syntax

### ⚠️ Eval eval-26-convert-from-kotest [with_skill]

**9/15** · 132257 tokens · 180070ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options (fragile, insuredValue, handling) are collapsed into a single map column like [fragile: true, insuredValue: 500] — not kept as three separate columns with mostly-blank cells. Rows with no options use a blank cell or [:].
  > Fragile | Insured value | Handling are separate columns with blanks
- ❌ **options-type-converter**: A @TypeConverter method is present that accepts Map<String, String> (or similar) and returns PackageOptions, applying defaults for missing keys.
  > Response states "No TypeConverter needed"; none present
- ✅ **dimensions-as-list**: Dimensions are represented as a [L, W, H] list in the table — not as three separate length/width/height columns
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} (or subset) to express carrier equivalence, rather than duplicating rows per carrier
- ❌ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing a distinct concern — not one monolithic table mixing base rates, surcharges, dimensional weight, and carrier equivalence
  > Only one @TableTest method shown; single table mixes all concerns
- ✅ **numeric-types-correct**: Weight and cost parameters use BigDecimal (not double or String). Dimensions use Integer or int.
- ✅ **scenario-column-present**: Each table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('EU express, light package', 'Oversized with fragile') — not outcomes ('8.00', 'Surcharge applied') or generic labels ('Test 1').
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Cost?', 'Base rate?')
- ❌ **business-language-columns**: Column names use domain language ('Zone', 'Weight (kg)', 'Dimensions', 'Options', 'Cost?') — not code identifiers ('shippingZone', 'weightKg', 'dims')
  > "Dims" used; assertion explicitly lists 'dims' as bad example
- ❌ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
  > Three if statements: if (fragile == true), if (insuredValue...), if (handling...)
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Each test method has a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ❌ **no-kotest-syntax**: Output contains no Kotest syntax: no forAll, row(), withData, shouldBe, context(), FunSpec, or Kotest imports.
  > "shouldBe" keyword in assertion: "... shouldBe cost"

### ⚠️ Eval eval-27-convert-from-testng [with_skill]

**12/15** · 201361 tokens · 159167ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **options-as-map**: Package options collapsed into single map column [fragile: true, insuredValue: 500], not three separate columns
  > | Fragile | Insured value | Handling | ...with mostly blank cells per row
- ❌ **options-type-converter**: A @TypeConverter method accepts Map<String, String> and returns PackageOptions
  > Response states 'No custom @TypeConverter needed'; buildOptions is helper, not @TypeConverter
- ✅ **dimensions-as-list**: Dimensions represented as [L, W, H] list, not three separate columns
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX}
- ❌ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing distinct concern
  > Only one @TableTest method (calculateShippingCost); all concerns in single table
- ✅ **numeric-types-correct**: Weight and cost use BigDecimal; dimensions use Integer/int
- ✅ **scenario-column-present**: Each table has scenario/description column as leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes or generic labels
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **business-language-columns**: Column names use domain language, not code identifiers
- ✅ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
- ✅ **annotation-order**: Annotations in order: @DisplayName (if present), @Description, @TableTest
- ✅ **has-descriptive-title**: Each test method has @DisplayName or descriptive method name
- ✅ **no-testng-artifacts**: Output contains no TestNG syntax or imports

### ✅ Eval eval-28-convert-from-methodsource [with_skill]

**15/15** · 209554 tokens · 496263ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **options-as-map**: Package options collapsed into single map column with [key: value] format, rows with no options use [:]
- ✅ **options-type-converter**: A @TypeConverter method accepts Map<String, String> and returns PackageOptions with defaults
- ✅ **dimensions-as-list**: Dimensions represented as [L, W, H] list, not three separate columns
- ✅ **uses-value-sets**: At least one table uses value set syntax {DHL, UPS, FEDEX} for carrier equivalence
- ✅ **concerns-decomposed**: Multiple @TableTest methods exist, each addressing distinct concern
- ✅ **numeric-types-correct**: Weight and cost use BigDecimal; dimensions use Integer or int
- ✅ **scenario-column-present**: Each table has scenario/description column as leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions, not outcomes or generic labels
- ✅ **has-question-mark-column**: At least one output column name ends with '?'
- ✅ **business-language-columns**: Column names use domain language, not code identifiers
- ✅ **no-if-switch-in-method**: Test method bodies contain no if or switch statements
- ✅ **annotation-order**: Annotations in order: @DisplayName, @Description, @TableTest
- ✅ **has-descriptive-title**: Each test method has @DisplayName with clear descriptive title
- ✅ **no-methodsource-artifacts**: Output contains no @MethodSource, Stream<Arguments>, Arguments.of, or params.provider imports

