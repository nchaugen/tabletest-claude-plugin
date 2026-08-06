All 32 generated test cases pass (18 base-rate rows + 9 surcharge rows + 2 dimensional-weight rows + 3 carrier-value-set expansions).

Summary of the conversion:

- **`src/test/kotlin/com/example/ShippingCostCalculatorTest.kt`** (new) replaces the Spock spec. Instead of one monolithic `where:` table, it's split into 4 focused `@TableTest` tables, each covering one concern:
  1. `determinesBaseFeeByRegionSpeedAndWeight` — the region/speed/weight rate ladder, with full boundary coverage (both sides of each tier limit) for EU standard, and one row per tier for the other three region/speed combinations.
  2. `appliesSurchargesToTheBaseFee` — oversize, hazmat, fragile and insurance surcharges and their combination, including the oversize threshold boundary and the insurance premium-vs-minimum boundary (below/at/above) that the original spec never exercised.
  3. `usesTheLargerOfActualAndDimensionalWeight` — the actual-vs-dimensional weight comparison.
  4. `ignoresCarrierWhenCalculatingTheFee` — a value-set row proving carrier (`DHL`/`UPS`/`FEDEX`) has no effect on price.
- Added `@TypeConverter` functions for `ShippingZone` (from `"EU standard"`-style cells) and `PackageOptions` (from map cells like `[fragile: true, insuredValue: 200]`), so no domain objects are constructed inside test bodies.
- `build.gradle.kts`: added `org.tabletest:tabletest-junit:1.2.2`, and removed the now-unused `groovy` plugin plus the Groovy/Spock dependencies (this was the only Groovy/Spock test in the project).
- Deleted the old `ShippingCostCalculatorSpec.groovy` and the now-empty `src/test/groovy` tree.

One assumption worth flagging: BigDecimal equality is checked via `compareTo` (not `equals`), since the calculator's `stripTrailingZeros()` can change scale (e.g. `10.500` → `10.5`) without changing the numeric value — a plain `assertEquals` on BigDecimals would have been scale-sensitive and spuriously failed.