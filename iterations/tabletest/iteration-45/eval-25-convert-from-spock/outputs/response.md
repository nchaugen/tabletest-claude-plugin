Conversion complete and verified (`gradle test` → 23/23 passing).

**What changed:**
- Added `src/test/kotlin/com/example/ShippingCostCalculatorTest.kt` — 8 `@TableTest` methods, one per concern (base rate lookup, dimensional weight override, oversize fee, fragile surcharge, insurance premium/floor, hazmat fee, fragile-before-insurance ordering, carrier independence) instead of one 18-row Spock table mixing all of them.
- Deleted `src/test/groovy/com/example/ShippingCostCalculatorSpec.groovy` and the `groovy` plugin/Spock deps in `build.gradle.kts`; added `org.tabletest:tabletest-junit:1.2.2`.

**Assumptions made (non-interactive run):**
- Target language is Kotlin (matches `ShippingCostCalculator.kt`), not Java.
- Decomposed by concern per the TableTest skill rather than one big table — each method holds irrelevant inputs constant (documented in `@Description`) and varies only its own axis.
- Added one new, independently-verified data point (insuredValue = 1000 → 13.50) because the original spec only ever exercised the insurance floor (500 and 200 both land exactly on the $3 minimum) and never showed the percentage rate actually driving the result above the floor.
- Kept the original's implicit "carrier never affects cost" and "fragile multiplies before insurance is added" observations as their own small tables, since those are real interactions the single-concern tables can't otherwise show.
- BigDecimal comparisons use `compareTo` (via `assertEquals(0, expected.compareTo(actual))`) to match Groovy's scale-insensitive `==` semantics on `BigDecimal`.