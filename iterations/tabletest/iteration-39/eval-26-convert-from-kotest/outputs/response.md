Everything runs and passes cleanly. Summary of the conversion:

**File changed:** `src/test/kotlin/com/example/ShippingCostCalculatorTest.kt`
**Build change:** `build.gradle.kts` — swapped `kotest-runner-junit5`/`kotest-framework-datatest` for `org.tabletest:tabletest-junit:1.2.1`

I decomposed the single flat Kotest data list into four `@TableTest` methods by concern, since the original 17 rows mixed several unrelated rules together:

1. **`calculatesBaseRateByRegionSpeedAndWeight`** — the region/speed/weight-tier rate lookup (16 rows, one per region×speed×tier combination — the original only sampled 8 of these 16 branches, so I filled in the rest using the rate constants in `ShippingCostCalculator`). Dimensions are fixed to a negligible `[1,1,1]` and carrier to `DHL` since neither affects this concern.
2. **`choosesGreaterOfActualAndDimensionalWeight`** — isolates the "effective weight = max(actual, volume/5000)" rule with one row per direction.
3. **`appliesSurchargesToBaseCost`** — the oversize/hazmat/fragile/insurance modifiers layered on a fixed base cost, including a boundary case the original didn't cover (insured value exactly at vs. above the $3.00 minimum premium).
4. **`carrierDoesNotAffectCost`** — uses a value set `{DHL, UPS, FEDEX}` to declare "regardless of carrier" in one row instead of three near-duplicate rows, since I confirmed `carrier` is accepted but never read by `calculateShippingCost`.

Assumptions made (no follow-up possible): assertion style stayed with `assertEquals(0, cost.compareTo(expected))` mirroring the original's scale-independent `BigDecimal` comparison, since the project has no AssertJ; and I added the extra base-rate/insurance-boundary rows to make thresholds fully visible rather than leaving them as gaps, per the skill's decomposition guidance — flagged via `@Description` on each table. Ran `gradle test`: all 29 generated cases pass.