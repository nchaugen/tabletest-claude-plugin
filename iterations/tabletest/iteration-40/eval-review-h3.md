# Eval Review — tabletest, Iteration 40

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-sonnet-5 · **Date:** 2026-07-25 · **Evals:** 5

## Summary

110/128 (85.9%) · 7394353 tokens · 1559.9s · $5.2309

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

## Delta vs Iteration 39

**Eval definition changed — not comparable (5):**
- ⚠️ eval-15-reis-discount: fingerprint differs from iteration 39; re-baseline to compare
- ⚠️ eval-22-event-registration-tt: fingerprint differs from iteration 39; re-baseline to compare
- ⚠️ eval-23-loan-approval-tt: fingerprint differs from iteration 39; re-baseline to compare
- ⚠️ eval-25-convert-from-spock: fingerprint differs from iteration 39; re-baseline to compare
- ⚠️ eval-28-convert-from-methodsource: fingerprint differs from iteration 39; re-baseline to compare

## Resource Comparison vs Iteration 39

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-15-reis-discount | 21/27 | 16/20 | 2287509 | 2287509 | 411.3 | 411.3 |
| eval-22-event-registration-tt | 26/28 | 21/25 | 573142 | 573142 | 154.0 | 154.0 |
| eval-23-loan-approval-tt | 20/22 | 12/18 | 540843 | 540843 | 149.9 | 149.9 |
| eval-25-convert-from-spock | 24/26 | 19/20 | 3006845 | 3006845 | 514.5 | 514.5 |
| eval-28-convert-from-methodsource | 19/25 | 16/18 | 986014 | 986014 | 330.2 | 330.2 |

## Per-Eval Results

### ⚠️ Eval eval-15-reis-discount

**21/27** · 2287509 tokens · 411343ms

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

### ⚠️ Eval eval-22-event-registration-tt

**26/28** · 573142 tokens · 153979ms

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

**20/22** · 540843 tokens · 149866ms

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

**24/26** · 3006845 tokens · 514486ms

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

### ⚠️ Eval eval-28-convert-from-methodsource

**19/25** · 986014 tokens · 330205ms

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

