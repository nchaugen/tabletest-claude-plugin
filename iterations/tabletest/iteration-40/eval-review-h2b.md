# Eval Review — tabletest, Iteration 40

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-sonnet-5 · **Date:** 2026-07-25 · **Evals:** 2

## Summary

46/55 (83.6%) · 1794478 tokens · 470.6s · $1.4606

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

## Delta vs Iteration 39

**Eval definition changed — not comparable (2):**
- ⚠️ eval-18-convert-from-code: fingerprint differs from iteration 39; re-baseline to compare
- ⚠️ eval-29-shopping-cart-tt: fingerprint differs from iteration 39; re-baseline to compare

## Resource Comparison vs Iteration 39

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-18-convert-from-code | 21/26 | 19/21 | 826034 | 826034 | 148.2 | 148.2 |
| eval-29-shopping-cart-tt | 25/29 | 20/23 | 968444 | 968444 | 322.4 | 322.4 |

## Per-Eval Results

### ⚠️ Eval eval-18-convert-from-code

**21/26** · 826034 tokens · 148196ms

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

### ⚠️ Eval eval-29-shopping-cart-tt

**25/29** · 968444 tokens · 322446ms

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

