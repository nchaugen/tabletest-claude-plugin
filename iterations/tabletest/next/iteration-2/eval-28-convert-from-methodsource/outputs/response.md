This looks good. The conversion is complete, verified, and committed to disk (uncommitted in git — not asked to commit).

## Summary

Converted `ShippingCostCalculatorTest` from a single 17-row `@MethodSource` table into 6 focused `@TableTest` methods, decomposed by concern (the original mixed region/speed rate lookup, dimensional weight override, oversize fee, fragile/insurance/hazmat surcharges, and carrier-independence all into one flat table):

1. **`calculatesBaseRateByRegionSpeedAndWeight`** — region/speed/weight → base rate, with dimensions fixed to isolate the rule.
2. **`effectiveWeightIsGreaterOfActualOrDimensionalWeight`** — actual vs. dimensional weight, "wins over" framing.
3. **`addsFlatFeeWhenAnyDimensionExceedsOversizeThreshold`** — added boundary rows at 100 (not oversize) and 101 (oversize) around the original 120 case, making the threshold visible.
4. **`appliesPackageOptionSurcharges`** — collapsed the sparse `fragile`/`insuredValue`/`handling` fields into a single `Options` map column with a `@TypeConverter`, per the skill's "collapse sparse columns" pattern.
5. **`insurancePremiumUsesGreaterOfCalculatedPremiumOrMinimum`** — new boundary table (200/500/600) making the $3.00 insurance floor explicit.
6. **`totalCostIsIndependentOfCarrier`** — consolidated the original's 3 near-identical "same rate DHL/UPS/FEDEX" rows into one row using a `{DHL, UPS, FEDEX}` value set.

Assumptions (documented in each table's `@Description`):
- Dimensions/weight/zone are fixed to representative defaults in tables that aren't testing that specific concern.
- Added a few boundary rows beyond the original's exact data points to make thresholds visible (oversize at 100/101, insurance at 200/500/600), since these constants exist in the source and the skill's design guidance calls for boundary visibility.
- Noted as an open question that hazmat+fragile/insurance interaction ordering isn't exercised (only fragile+insurance was, matching the original).

Added the `org.tabletest:tabletest-junit:1.2.1` test dependency to `pom.xml` (JUnit 5.12.2 already satisfies the ≥5.11 requirement). Ran `mvn test`: all 24 generated invocations pass.