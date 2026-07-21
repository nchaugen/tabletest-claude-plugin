That's fine — I won't run git commands. The conversion itself is complete and verified via the test run.

## Summary

Converted `ShippingCostCalculatorSpec.groovy` (17 Spock `where:` rows in one big table) into `src/test/kotlin/com/example/ShippingCostCalculatorTest.kt` using 7 focused `@TableTest` methods, each isolating one concern of `calculateShippingCost`:

1. `baseRateByRegionSpeedAndWeight` — region/speed/weight tier lookup (dims fixed small so dimensional weight can't interfere)
2. `dimensionalWeightOverride` — actual vs. dimensional weight, whichever is larger wins
3. `oversizeSurcharge` — +10.00 when any dimension > 100 cm
4. `hazmatHandlingSurcharge` — +8.00 for hazmat handling
5. `fragileSurcharge` — 1.15× multiplier
6. `insuranceSurcharge` — 0.6% premium with $3.00 minimum, and its interaction with the fragile multiplier
7. `carrierDoesNotAffectCost` — one row with a `{DHL, UPS, FEDEX}` value set, replacing the three "same rate" Spock rows, proving carrier is irrelevant to price

Also:
- Added `org.tabletest:tabletest-junit:1.2.1` to `build.gradle.kts` and removed the `groovy` plugin, `groovy`, and `spock-core` dependencies (migration is now complete — no Spock left in the build).
- Deleted the old `.groovy` spec file and its now-empty directory.
- Used `assertFee` (a `compareTo`-based helper) instead of raw `assertEquals` for `BigDecimal`, since the calculator's `stripTrailingZeros()` output can have a different scale than table literals like `"20.00"`, and `BigDecimal.equals()` is scale-sensitive (Groovy's `==` used `compareTo` semantics, so this preserves original behavior).

One real bug I hit and fixed: I initially used a `@TypeConverter` targeting `PackageOptions` with blank cells meaning "defaults." That failed — I decompiled `tabletest-junit-1.2.1`'s sources and confirmed blank cells resolve straight to `null` before any custom converter runs, bypassing it entirely. Fixed by typing those parameters as `Map<String, String>?` and doing the null-to-default logic in a private `buildOptions` helper instead.

Ran `gradle test`: **all 22 generated test cases pass.**