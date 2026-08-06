All 35 generated test cases pass (0 failures, 0 errors) — that's 18 + 2 + 6 + 2 + 2 + 2 + 3 rows from the seven tables, matching my count exactly.

## Summary

I converted the single 17-row Spock table into **7 focused `@TableTest` methods** in `src/test/kotlin/com/example/ShippingCostCalculatorTest.kt` (Kotlin, matching the production code), rather than a 1:1 port, because the original mixed several independent rules into one table:

1. **`determinesBaseRateFromRegionSpeedAndWeight`** — the region/speed/weight-tier rate ladder, with full boundary coverage on EU standard and one row per tier for the other three combinations (18 rows)
2. **`selectsTheGreaterOfActualAndDimensionalWeight`** — dimensional vs. actual weight (2 rows)
3. **`appliesFlatFeesForOversizeAndHazardousHandling`** — oversize threshold (boundary-tested at 100/101cm) and hazmat fee, including that they stack (6 rows)
4. **`appliesAFragileSurcharge`** — the 15% multiplier (2 rows)
5. **`computesInsurancePremium`** — percentage vs. minimum, boundary-tested at insured value 499/501 (2 rows)
6. **`combinesFragileSurchargeWithOtherFees`** — a genuine combining table proving fragile multiplies the hazmat fee but *not* insurance, since insurance is added after (2 rows)
7. **`determinesCostIndependentlyOfCarrier`** — carrier expressed as a value set `{DHL, UPS, FEDEX}` instead of 3 duplicated rows, since carrier turned out to have no effect on cost at all

Other changes:
- Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle.kts` and removed the `groovy` plugin plus Spock/Groovy dependencies, since this was the only Groovy/Spock test in the project.
- Deleted the old `.groovy` spec and its now-empty directory tree.
- `PackageOptions` (fragile/insuredValue/handling — an object with several optional fields) is built via a `@TypeConverter` from a map cell like `[fragile: true, handling: hazmat]`, per TableTest convention, rather than assembled inline in each test method.
- All BigDecimal comparisons use `compareTo` (via a small `assertCost` helper) rather than `equals`, since the calculator strips trailing zeros and scale-sensitive equality would spuriously fail (e.g. `7.50` vs `7.5`).

One assumption: I verified every expected value directly against the calculator's source logic (not just copied from the original Spock table), and found the numbers already matched — so no behavior discrepancies surfaced during conversion.