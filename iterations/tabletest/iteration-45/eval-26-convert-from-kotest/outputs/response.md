I converted the Kotest `withData` test into 5 `@TableTest` methods, each isolating one concern of `calculateShippingCost`, and verified all 63 generated cases pass with `gradle test`.

**What changed:**
- `src/test/kotlin/com/example/ShippingCostCalculatorTest.kt` — rewritten as a plain JUnit 5 test class with five `@TableTest` methods:
  - `looksUpBaseRateByZoneAndWeight` (16 rows, weight expressed as tier-spanning value sets) — the rate lookup table
  - `usesTheGreaterOfActualAndDimensionalWeight` (3 rows) — dimensional-weight override, including the exact-tie boundary
  - `addsOversizeFeeForDimensionsPastTheLimit` (2 rows) — the oversize boundary
  - `appliesPackageOptionSurcharges` (7 rows, `PackageOptions` collapsed into one map column with a `@TypeConverter`) — fragile/hazmat/insurance and their combinations, including the fragile×hazmat ordering interaction
  - `ignoresCarrierWhenCalculatingCost` (1 row, `{DHL, UPS, FEDEX}` value set) — replacing the three near-duplicate "same rate" cases
- `build.gradle.kts` — removed the now-unused `kotest-runner-junit5`/`kotest-framework-datatest` dependencies and added `org.tabletest:tabletest-junit:1.2.2`.

**Assumptions made** (no follow-up possible in this run):
- Kept the original `compareTo`-based equality (via a small `assertMoneyEquals` helper) since the calculator's `stripTrailingZeros()` output has a variable `BigDecimal` scale that plain `equals` would reject.
- Confirmed from the source that `carrier` is entirely unused by `calculateShippingCost`, so its irrelevance is now an explicit single-row value-set table rather than three duplicated scenarios.
- Added a few boundary rows the original Kotest suite didn't have (e.g., exact-tie dimensional weight, at/just-past the oversize limit, at/above the insurance minimum) to make the tier and threshold logic fully verifiable, per the table-design guidance.