All 42 rows pass. Summary of the conversion:

- **Removed**: `src/test/groovy/com/example/ShippingCostCalculatorSpec.groovy`, the `groovy` plugin, and the `groovy`/`spock-core` dependencies from `build.gradle.kts`.
- **Added**: `src/test/kotlin/com/example/ShippingCostCalculatorTest.kt` (Kotlin/JUnit), and the `org.tabletest:tabletest-junit:1.2.2` dependency.
- **Decomposed the one Spock table into 8 focused `@TableTest` tables**, each isolating one rule, since the original mixed region/speed/weight rate lookup, dimensional weight, oversize, hazmat, fragile, insurance, and carrier all into one 17-row table with gaps in boundary coverage:
  1. `looksUpBaseRate` — full 4-region×speed rate ladder with both sides of every tier boundary (24 rows; the original only sampled 6 combos with no boundaries)
  2. `chargesForTheGreaterOfActualAndDimensionalWeight` — which of actual/dimensional weight wins
  3. `addsAnOversizeFee` — boundary at the 100cm threshold
  4. `addsAHazmatFee` — absent / non-matching / matching handling value
  5. `appliesAFragileMultiplier`
  6. `addsAnInsurancePremium` — below/at/above the $3.00 floor
  7. `appliesTheFragileMultiplierBeforeInsurance` — a combining table that proves the ordering (fragile multiplies first, insurance is added after — an interaction neither table 5 nor 6 alone shows)
  8. `doesNotVaryByCarrier` — the three "same rate" rows collapsed into one row with a `{DHL, UPS, FEDEX}` value set, since carrier never affects the result

- Package-level `PackageOptions` and a `Map`-based `@TypeConverter` are shared across tables 4–7 so fragile/insuredValue/handling are expressed consistently as an `Options` map column rather than ad hoc booleans/strings per table.
- `BigDecimal` comparisons use `compareTo` (via a small `assertMoneyEquals` helper) rather than `equals`/`==`, since the production code's `stripTrailingZeros()` changes scale and Kotlin's `==`/JUnit's `assertEquals` on `BigDecimal` are scale-sensitive — the original Groovy `==` masked this because Groovy's `==` uses `compareTo`.

Ran `gradle test`: build succeeded, 42/42 test cases passed (0 failures, 0 errors).