All 34 generated tests pass on a clean build (18 base-rate + 3 effective-weight + 3 oversize + 7 surcharge + 3 carrier value-set cases). The conversion is complete.

**Summary of the conversion** (`src/test/java/com/example/ShippingCostCalculatorTest.java`):

I replaced the single 17-row `@MethodSource` test with five `@TableTest` methods, each isolating one concern of `calculateShippingCost`:

1. **`shouldSelectBaseRateByRegionSpeedAndWeight`** — the region/speed/weight-tier rate card, with explicit boundary rows (e.g. 1.0kg vs 1.01kg) for EU standard and representative points for the other three region/speed combos. Dimensions are pinned to `[1,1,1]` so dimensional weight can't interfere.
2. **`shouldUseTheLargerOfActualAndDimensionalWeight`** — actual-vs-dimensional weight selection (dominant-actual, dominant-dimensional, and an equal-boundary case).
3. **`shouldApplyOversizeFeeWhenAnyDimensionExceeds100mm`** — the 100mm oversize threshold, with an at-boundary (100, not oversize) and just-over (101, oversize) row.
4. **`shouldApplySurchargesOnTopOfBaseRate`** — fragile multiplier, hazmat fee, and insurance premium (with its 3.00 floor and a boundary row), including the original combined fragile+insured case that proves insurance is added *after* the fragile multiplier, not multiplied by it.
5. **`shouldNotVaryCostByCarrier`** — collapses the original three "same rate" DHL/UPS/FEDEX rows into one row using a `{DHL, UPS, FEDEX}` value set, since carrier is accepted but never affects cost.

Assumptions made (documented via `@Description` on each table since this was a non-interactive run):
- Fixed dimensions/zone/weight per table are chosen so only the column(s) under test can move the result — stated explicitly rather than left implicit.
- I added a few boundary and floor rows (e.g. 100mm/101mm, insurance-at-500) beyond the original's coverage, since the skill's guidance is to make thresholds visible; this only exercises the same public method more thoroughly, no behavior changed.

Also added the missing `org.tabletest:tabletest-junit:1.2.1` test dependency to `pom.xml` (JUnit was already at 5.12.2, which satisfies the 5.11+ requirement).