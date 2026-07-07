All 25 generated test cases pass. The conversion is complete:

**What changed:**
- `src/test/kotlin/com/example/ShippingCostCalculatorTest.kt` — converted from a single Kotest `withData` block into 7 focused `@TableTest` methods, decomposed by concern:
  1. Base rate by zone/weight/dimensional-override (9 rows)
  2. Oversize surcharge, with threshold boundary rows (3 rows)
  3. Hazmat handling surcharge (2 rows)
  4. Fragile surcharge (2 rows)
  5. Insurance surcharge, with below/at/above-minimum boundary rows (4 rows)
  6. Combined fragile + insurance interaction (2 rows)
  7. Carrier irrelevance, using a value set `{DHL, UPS, FEDEX}` (1 row → 3 cases)
- `build.gradle.kts` — swapped the Kotest dependencies for `org.tabletest:tabletest-junit:1.2.1`; assertions now use plain JUnit 5 (`assertEquals(0, expected.compareTo(actual))`, preserving the original's scale-insensitive `BigDecimal` comparison).

**Key assumptions made (no follow-up possible):**
- `Carrier` never actually affects the computed cost (confirmed by reading `ShippingCostCalculator` — the parameter is unused in the calculation). I fixed it to `Carrier.DHL` in all tables except one dedicated table that proves the invariance via a value set, replacing the original's three duplicate "same rate" rows.
- Added a few boundary rows not present in the original (oversize at exactly 100/101, insurance premium below/at/above the $3 minimum) since the skill's threshold-visibility guidance calls for showing both sides of a boundary once a table exposes it.
- All 25 generated test cases pass under `gradle test`.