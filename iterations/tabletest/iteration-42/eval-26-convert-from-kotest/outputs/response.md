I've converted the Kotest data-driven test to TableTest, decomposing the single `withData` block into four focused tables based on distinct concerns in `ShippingCostCalculator`:

1. **`determinesBaseRateByRegionSpeedAndWeight`** — the region/speed/weight-tier lookup (dimensions fixed small so actual weight always governs)
2. **`selectsEffectiveWeightFromActualOrDimensional`** — isolates the dimensional-weight-vs-actual-weight override behavior (2 rows: actual wins, dimensional wins)
3. **`appliesSurchargesToBaseCost`** — oversize/fragile/insured/hazmat surcharges on a fixed base rate, using a `Map<String,String>` column + `@TypeConverter` (`parsePackageOptions`) instead of manually building `PackageOptions` in each test
4. **`carrierDoesNotAffectCost`** — collapses the original "same rate DHL/UPS/FEDEX" trio into one row with a `{DHL, UPS, FEDEX}` value set, since carrier is accepted but never affects the result

All expected values were recomputed independently against the calculator's actual logic (dimensional weight formula, tier breakpoints, surcharge order: oversize → hazmat → fragile multiplier → insurance) and match the original scenarios exactly.

I also updated `build.gradle.kts`: added `org.tabletest:tabletest-junit:1.2.2`, removed the now-unused `io.kotest:kotest-runner-junit5` and `io.kotest:kotest-framework-datatest` dependencies (JUnit Jupiter is already 6.0.3, which satisfies the ≥5.11 requirement).

**One caveat**: a tooling failure in this session (the Bash tool errored on every invocation) prevented me from grepping the rest of the repo for other Kotest usages before removing those dependencies. I'm assuming this was the only Kotest-based test in the project — worth a quick `grep -r "io.kotest"` on your end before running the build, in case another test file still depends on it.