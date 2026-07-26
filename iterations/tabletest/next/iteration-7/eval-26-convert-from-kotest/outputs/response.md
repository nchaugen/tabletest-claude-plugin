All 32 generated test cases pass. Summary of the conversion at `src/test/kotlin/com/example/ShippingCostCalculatorTest.kt`:

- Replaced the single Kotest `FunSpec`/`withData` table (17 rows mixing every concern) with 5 `@TableTest` methods, each isolating one rule of the calculator:
  - `resolvesBaseRateByZoneAndWeight` — the zone/weight rate table itself (16 rows, all 4 region×speed combos × 4 weight tiers, using neutral dimensions so dimensional weight can't interfere)
  - `selectsEffectiveWeight` — which of actual vs. dimensional weight wins
  - `appliesOversizeSurcharge` — the >100cm flat fee, with a boundary row at exactly 100
  - `appliesPackageOptionSurcharges` — fragile/insurance/hazmat, collapsed into one `Options` map column (with a `@TypeConverter` building `PackageOptions`) since they're all optional fields on the same parameter; includes the insurance floor boundary and the fragile+insurance ordering case
  - `rateIsIndependentOfCarrier` — the 3 original carrier-only rows collapsed into one row using a `{DHL, UPS, FEDEX}` value set
- Removed the Kotest dependencies (`kotest-runner-junit5`, `kotest-framework-datatest`) from `build.gradle.kts` (nothing else in the repo used them) and added `org.tabletest:tabletest-junit:1.2.2`; JUnit Jupiter was already 6.0.3, satisfying the ≥5.11 requirement.
- Assertions use `assertEquals(0, cost.compareTo(expected))` (JUnit Jupiter's `Assertions`), preserving the original's scale-insensitive `BigDecimal` comparison.
- Verified with `gradle test`: build succeeded, 32/32 generated cases passed, 0 failures/skips.