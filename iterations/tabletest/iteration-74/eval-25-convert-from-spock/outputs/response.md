Converted successfully. Summary:

**What changed:**
- `build.gradle.kts` — dropped the `groovy` plugin and `spock-core`/`groovy` test dependencies (no longer used anywhere), added `org.tabletest:tabletest-junit:1.2.2`.
- Deleted `src/test/groovy/com/example/ShippingCostCalculatorSpec.groovy`.
- Added `src/test/kotlin/com/example/ShippingCostCalculatorTest.kt`.

**Design decisions (the original spec mixed several concerns in one `where:` block, which I split by rule):**
1. `determinesTheBaseRateFromRegionSpeedAndWeight` — full boundary coverage for EU standard's 4 tiers, one row per tier for the other three region/speed combinations (same pattern as any repeated rate ladder).
2. `usesWhicheverOfActualOrDimensionalWeightIsGreater` — dimensional-weight override, isolated from the rate ladder.
3. `addsAnOversizeFeeWhenAnyDimensionExceedsTheThreshold` — boundary at the 100 cm threshold, shown via two different dimensions to prove it isn't length-specific.
4. `addsAHazmatFeeOnlyForHazmatHandling` — includes a non-hazmat handling string as a new case (the original never tested that "hazmat" is checked by exact value).
5. `appliesAFragileMultiplierToTheBaseRate`
6. `addsAnInsuranceSurchargeWithAMinimumCharge` — straddles the point where the flat $3 minimum stops applying (new boundary the original didn't test).
7. `pricesTheSameRegardlessOfCarrier` — carrier is accepted but never read by the code, so this is expressed as a value set over `{DHL, UPS, FEDEX}` rather than duplicated rows everywhere.
8. `combinesSurchargesInTheCorrectOrder` — a genuine combining table: proves hazmat fees are included in the amount the fragile multiplier applies to, while insurance is added afterward and escapes it. This ordering isn't observable from any single-concern table.

Assumed cm/kg units (matches the 5000 volumetric divisor, a standard courier convention) and used `assertEquals(0, expected.compareTo(actual))` for `BigDecimal` comparisons, since the original Spock `==` compared by value while `BigDecimal.equals()` is scale-sensitive and the production code's `stripTrailingZeros()` produces inconsistent scales (e.g. `20.00` → `2E+1`).

Ran `gradle test`: all 37 generated cases across the 8 tables pass, no wrapper was present so I used the globally installed Gradle 9.6.1. No `.gradle`/`build` artifacts were left behind.