Conversion complete. Summary of what changed:

**`pom.xml`** — added the `org.tabletest:tabletest-junit:1.2.2` test dependency (JUnit 5.12.2 already satisfied the ≥5.11 requirement).

**`ShippingCostCalculatorTest.java`** — replaced the single flat `@MethodSource` test (17 rows mixing every concern) with three focused `@TableTest` methods, decomposed by rule:

1. **`resolvesBaseCost`** — zone/weight/dimensions → base cost, including the dimensional-weight-override case. Options and carrier held constant (declared in `@Description`).
2. **`appliesSurchargesToBaseCost`** — fragile/insured/hazmat/oversize surcharges layered on a fixed base cost (7.50), including the fragile+insured row that proves the multiplier applies *before* the insurance premium is added.
3. **`costIsUnaffectedByCarrier`** — the original three "same rate DHL/UPS/FEDEX" rows collapsed into one row using a `{DHL, UPS, FEDEX}` value set, since carrier turned out to have zero effect on cost (it's an unused parameter in the implementation).

Two `@TypeConverter` methods build `ShippingZone` (from `"EU standard"`-style strings) and `PackageOptions` (from a sparse `[fragile: true, insuredValue: 200]`-style map), keeping construction out of the test bodies — a key fix was making the test class `public` so TableTest could discover these converters.

Verified: `mvn test` passes, 18 test invocations total, all original expected values preserved.