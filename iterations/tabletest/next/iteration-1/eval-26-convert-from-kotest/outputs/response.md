All 39 generated test cases pass (24 from the base-rate table's carrier value-set expansion, plus 2+2+2+2+4+3 from the other six tables).

## Summary

Converted `ShippingCostCalculatorTest.kt` from Kotest's `withData` to seven `@TableTest` methods, decomposed by concern rather than keeping one flat table:

1. **`calculatesBaseRateByZoneAndWeight`** — zone/weight tier lookup; carrier is exercised as a `{DHL, UPS, FEDEX}` value set since it doesn't affect price (confirmed by reading the source — `carrier` is an unused parameter)
2. **`appliesDimensionalWeightOverride`** — volumetric weight overriding actual weight
3. **`appliesOversizeSurcharge`** — includes a new boundary row at exactly the 100 threshold
4. **`appliesHazmatFee`**
5. **`appliesFragileMultiplier`**
6. **`appliesInsurancePremium`** — below/at/above the 3.00 minimum (added an "above minimum" case not in the original)
7. **`combinesFragileAndInsurance`** — demonstrates insurance is applied after the fragile multiplier

Key assumptions (documented in the code as `@Description` where relevant, since this was a non-interactive run):
- Added a package-level `@TypeConverter fun parseShippingZone` so zone appears as a single readable `"EU standard"`-style column instead of two.
- Base-rate table uses fixed small dimensions `[10, 10, 10]` (dimensional weight ≈ 0.2, always negligible) to isolate the rate lookup from the dimensional-weight-override concern, which gets its own table.
- Kept `assertEquals(0, result.compareTo(expected))` instead of `equals`/`shouldBe`, matching the original's reasoning: `stripTrailingZeros()` can change scale (e.g., `20.00` → `2E+1`), so comparison must be value-based.
- Removed the now-unused Kotest dependencies from `build.gradle.kts` (verified no other test file uses them) and added `org.tabletest:tabletest-junit:1.2.1`.

Verified independently in Python that every expected value in the new tables matches the calculator's actual logic, then ran the full suite with Gradle: build succeeded, 39/39 generated cases pass, 0 failures/errors.