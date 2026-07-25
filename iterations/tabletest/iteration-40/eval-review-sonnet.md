# Eval Review — tabletest, Iteration 40

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-sonnet-5 · **Date:** 2026-07-25 · **Evals:** 6

## Summary

133/155 (85.8%) · 11178666 tokens · 2532.1s · $8.0533

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

## Delta vs Iteration 39

**Eval definition changed — not comparable (6):**
- ⚠️ eval-25-convert-from-spock: fingerprint differs from iteration 39; re-baseline to compare
- ⚠️ eval-26-convert-from-kotest: fingerprint differs from iteration 39; re-baseline to compare
- ⚠️ eval-27-convert-from-testng: fingerprint differs from iteration 39; re-baseline to compare
- ⚠️ eval-28-convert-from-methodsource: fingerprint differs from iteration 39; re-baseline to compare
- ⚠️ eval-29-shopping-cart-tt: fingerprint differs from iteration 39; re-baseline to compare
- ⚠️ eval-30-order-splitting-tt: fingerprint differs from iteration 39; re-baseline to compare

## Resource Comparison vs Iteration 39

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-25-convert-from-spock | 22/26 | 19/20 | 3006845 | 3006845 | 514.5 | 514.5 |
| eval-26-convert-from-kotest | 23/26 | 17/20 | 1443172 | 1443172 | 362.4 | 362.4 |
| eval-27-convert-from-testng | 21/25 | 17/19 | 1445041 | 1445041 | 337.7 | 337.7 |
| eval-28-convert-from-methodsource | 20/25 | 16/18 | 986014 | 986014 | 330.2 | 330.2 |
| eval-29-shopping-cart-tt | 27/29 | 20/23 | 968444 | 968444 | 322.4 | 322.4 |
| eval-30-order-splitting-tt | 20/24 | 19/20 | 3329150 | 3329150 | 664.9 | 664.9 |

## Per-Eval Results

### ⚠️ Eval eval-25-convert-from-spock

**22/26** · 3006845 tokens · 514486ms

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

**23/26** · 1443172 tokens · 362383ms

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

**21/25** · 1445041 tokens · 337704ms

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

**20/25** · 986014 tokens · 330205ms

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

**27/29** · 968444 tokens · 322446ms

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

**20/24** · 3329150 tokens · 664880ms

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

