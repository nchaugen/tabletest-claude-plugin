The conversion is complete and verified — all 34 generated test cases pass against the real implementation.

## What I did

**Decomposed the single 17-row Spock table into 7 focused `@TableTest` tables**, one per concern in `ShippingCostCalculator.calculateShippingCost`:

1. `determinesBaseRate` — region/speed/weight rate ladder, with full boundary pairs (1, 5, 15 kg) on the EU-standard class and one row per tier on the other three classes, since they share the same breakpoints.
2. `resolvesEffectiveWeight` — proves dimensional (volumetric) weight can override actual weight when it's larger.
3. `addsOversizeSurcharge` — boundary at the 100 cm threshold.
4. `addsHazmatHandlingFee` — null vs. an unrelated handling code vs. `"hazmat"` (proves it's an exact match, not "any handling").
5. `appliesFragileMultiplier` — the 15% surcharge.
6. `addsInsurancePremium` — percentage-of-value vs. flat-minimum boundary (crossing at insured value 500).
7. `combinesFragileWithOtherSurcharges` — a genuine precedence table: proves oversize/hazmat fees are folded in *before* the fragile multiplier, but the insurance premium is added *after* and isn't multiplied. This ordering isn't observable from any single-concern table alone.

I dropped `carrier` from most tables since the implementation never reads it — instead I stated that explicitly with a `{DHL, UPS, FEDEX}` value set on one row rather than repeating three near-duplicate carrier scenarios as the original did.

**Migration cleanup:**
- Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle.kts`
- Removed the `groovy` plugin and the `spock-core`/`groovy` test dependencies (nothing else in the project used Groovy)
- Deleted the old `ShippingCostCalculatorSpec.groovy` and the now-empty `src/test/groovy` tree
- New file: `src/test/kotlin/com/example/ShippingCostCalculatorTest.kt`

**One correctness catch worth flagging:** the calculator does `.setScale(3, HALF_UP).stripTrailingZeros()`, which changes a `BigDecimal`'s scale (e.g. `20.00` → `2E+1`). Plain `BigDecimal.equals()` is scale-sensitive and would spuriously fail on whole-number results, so I added a small `assertMoneyEquals` helper using `compareTo` instead — verified this was actually necessary by testing `stripTrailingZeros()` behavior directly before writing the assertions.

Ran `gradle test`: **34/34 pass, 0 failures**.