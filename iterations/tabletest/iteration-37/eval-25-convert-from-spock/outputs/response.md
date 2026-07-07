I converted the Spock spec into a JUnit 5 `@TableTest` (Kotlin, matching your main sources) at `src/test/kotlin/com/example/ShippingCostCalculatorTest.kt`. Both old and new versions currently run side by side — 17/17 pass in each — so the conversion is verified equivalent.

Since the original 17 rows mixed several concerns into one giant table, I decomposed it into 5 focused tables:

1. **`shouldResolveBaseRateByRegionSpeedAndWeight`** — 8 rows, the region/speed/weight-bracket rule (fixed small dims so dimensional weight can't interfere).
2. **`shouldUseDimensionalWeightWhenItExceedsActualWeight`** — the 1 row where dimensional weight overrides actual weight.
3. **`shouldAddOversizeSurchargeWhenAnyDimensionExceedsThreshold`** — the 1 oversize row.
4. **`shouldApplyHandlingAndInsuranceSurcharges`** — 4 rows for fragile/insured/hazmat.
5. **`shouldNotVaryRateByCarrier`** — the 3 "same rate" rows collapsed into **one row using a `{DHL, UPS, FEDEX}` value set**, since carrier turned out to have zero effect on price (it's an unused parameter in `calculateShippingCost`).

8+1+1+4+3 = 17, so no scenarios were dropped or invented.

One thing worth knowing: since the calculator's result comes back via `stripTrailingZeros()`, comparing `BigDecimal`s with plain `assertEquals` would fail on scale mismatches (e.g. `5` vs `5.00`) — Spock's `==` hid this because Groovy compares BigDecimals by value. I used `assertEquals(0, rate.compareTo(result))` to match that value-based semantics.

I also added `org.tabletest:tabletest-junit:1.2.1` to `build.gradle.kts`.

**Before I delete the old Spock spec** (`src/test/groovy/com/example/ShippingCostCalculatorSpec.groovy`) and the now-unused Groovy/Spock dependencies from `build.gradle.kts` — do you want me to go ahead with that cleanup, or would you like to keep them around for now?