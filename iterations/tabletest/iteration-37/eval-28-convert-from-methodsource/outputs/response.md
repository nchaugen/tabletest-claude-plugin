This looks solid — annotation order is correct (`@Description` → `@TableTest`), every table's rows are traceable, thresholds are visible with boundary rows, and value sets are used only where results are identical. All 38 generated test cases pass, and the full suite is green.

## Summary

Converted the single `@MethodSource` test (17 hand-listed scenarios covering multiple concerns) into six `@TableTest` methods, each isolating one concern of `ShippingCostCalculator`:

1. **`resolvesBaseRateByZoneSpeedAndWeight`** — the region/speed/weight rate lookup, including boundary rows at/just-over the 1kg and 15kg tier thresholds (20 rows)
2. **`appliesDimensionalWeightOverride`** — when volumetric weight overrides actual weight and crosses rate tiers (3 rows)
3. **`appliesFlatSurcharges`** — oversize and hazmat fees, including their combination (4 rows)
4. **`appliesFragileMultiplier`** — the multiplicative surcharge, including that it applies *after* flat surcharges (3 rows)
5. **`appliesInsurancePremiumWithMinimumFloor`** — the 0.6% premium vs. its $3.00 floor, with a value set for "any value below the floor" and explicit at/just-over boundary rows (4 rows, one is a 2-value set)
6. **`carrierDoesNotAffectCost`** — consolidates 3 near-duplicate original rows into one row with a `{DHL, UPS, FEDEX}` value set, since carrier is accepted but unused by the implementation

Also added the missing `org.tabletest:tabletest-junit:1.2.1` test dependency to `pom.xml` (JUnit was already 5.12.2, which satisfies the ≥5.11 requirement). All 38 generated test cases pass, and `mvn test` is green for the whole project.