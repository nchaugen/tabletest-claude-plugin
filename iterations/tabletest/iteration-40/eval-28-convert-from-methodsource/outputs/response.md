All 29 generated test cases pass (up from the original 17 `@MethodSource` cases — the carrier table's value set alone expands 1 row into 3 cases).

## Summary of the conversion

`src/test/java/com/example/ShippingCostCalculatorTest.java` is now 8 `@TableTest` methods instead of one `@ParameterizedTest`/`@MethodSource` pair, decomposed by concern per the calculator's actual logic (`ShippingCostCalculator.java`):

1. **`shouldLookUpBaseRateByRegionSpeedAndWeight`** — the region/speed/weight rate table, with boundary rows added at 1kg/5kg/15kg to make the tier thresholds visible.
2. **`shouldUseTheLargerOfActualAndDimensionalWeight`** — actual vs. volumetric weight.
3. **`shouldAddOversizeSurchargeWhenAnyDimensionExceedsThreshold`** — the >100cm surcharge, with a control row and an at-threshold row added.
4. **`shouldApplyFragileMultiplier`** — the 1.15× multiplier.
5. **`shouldAddInsurancePremiumWithMinimum`** — the 0.6%/$3 floor, with rows added below/at/above the floor.
6. **`shouldAddHazmatHandlingFee`** — the flat $8 fee.
7. **`shouldCombineFragileMultiplierWithInsurancePremium`** — verifies insurance is computed on the fragile-multiplied base but its floor isn't affected by the multiplier (a genuine order-of-operations interaction, kept as its own table).
8. **`shouldNotVaryCostByCarrier`** — collapses the original 3 "same rate" scenarios (DHL/UPS/FEDEX) into one row using a `{DHL, UPS, FEDEX}` value set, since carrier never affects cost.

All 17 original scenarios/expected values are preserved exactly; I added a handful of cheap control/boundary rows (documented via `@Description`) rather than inventing new behavior. Carrier is fixed to `DHL` in tables 1–7 since it's proven irrelevant in table 8 — noted in a class comment.

Also added the `org.tabletest:tabletest-junit:1.2.1` test-scope dependency to `pom.xml` (JUnit Jupiter was already 5.12.2, which satisfies the 5.11+ requirement).