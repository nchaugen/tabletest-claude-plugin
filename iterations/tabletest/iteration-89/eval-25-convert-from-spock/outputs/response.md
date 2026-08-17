All 36 test invocations pass (the carrier table's single row expands to 3 via its value set). Summary of the conversion:

**What changed**
- `src/test/groovy/com/example/ShippingCostCalculatorSpec.groovy` deleted; replaced by `src/test/kotlin/com/example/ShippingCostCalculatorTest.kt`.
- `build.gradle.kts`: dropped the `groovy` plugin and Spock/Groovy dependencies, added `org.tabletest:tabletest-junit:1.2.2`.

**Table design** — the original spec put every behaviour in one flat `where:` block. I split it into 8 focused tables, one per rule the calculator applies:

1. `computes the base rate from zone and weight` — 18 rows, full boundary coverage (1/1.01, 5/5.01, 15/15.01 kg) for EU standard, one row per tier for the other three zone/speed combos (the boundary positions don't move between them).
2. `charges for the larger of actual and dimensional weight` — 2 rows, actual-wins vs. dimensional-wins.
3. `adds a flat fee for oversize packages` — boundary at 100cm (100 vs. 101), plus a row showing any dimension (not just length) can trigger it.
4. `applies a multiplier for fragile packages`
5. `adds an insurance premium for insured packages` — boundary at the $500 insured-value point where the 0.6% premium starts exceeding the $3 minimum.
6. `adds a flat fee for hazmat handling` — includes a non-hazmat handling value to prove the string comparison is exact.
7. `charges the same rate regardless of carrier` — one row, carrier as a value set `{DHL, UPS, FEDEX}`, since the parameter is accepted but never read by the implementation.
8. `applies flat fees and multipliers in a fixed order` — a small combining table (with `@Description`) proving the one thing no single-concern table shows: flat fees are added before the fragile multiplier, which is applied before the insurance premium.

**Mechanics**: `Zone` (e.g. `"EU standard"`) and `Options` (map cells like `[fragile: true, insuredValue: 200]`) go through `@TypeConverter` functions (`parseZone`, `parseOptions`) so the test method bodies stay pure arrange-act-assert. Cost comparisons use `expected.compareTo(actual) == 0` rather than `assertEquals`, since the calculator's `stripTrailingZeros()` changes BigDecimal scale.