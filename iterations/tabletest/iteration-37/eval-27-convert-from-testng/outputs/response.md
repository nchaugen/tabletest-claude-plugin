Converted successfully — the build passes with 17 test executions, matching the original coverage. Summary of the conversion at `src/test/java/com/example/ShippingCostCalculatorTest.java`:

- **`shouldCalculateShippingCost`** — 14 rows covering zone/speed rate tiers, dimensional-weight override, fragile/insured/hazmat surcharges, and oversize fee, with `Carrier` fixed to `DHL` and constructed via a `@TypeConverter`-free direct build (`Region`/`Speed` → `ShippingZone`, blank cells → `null` for `Insured Value`/`Handling`).
- **`carrierDoesNotAffectShippingCost`** — new focused table using a value set `{DHL, UPS, FEDEX}` to assert cost is identical regardless of carrier.

**Worth noting:** `calculateShippingCost` takes a `Carrier` parameter but never reads it in the implementation. Your original TestNG suite had 4 rows (`EU express medium`, `same rate DHL/UPS/FEDEX`) that varied only by carrier and always expected 12.00 — clear evidence carrier is orthogonal to price, so I consolidated that into one value-set row instead of cross-multiplying carrier across every scenario. Worth confirming this is intentional (unused parameter) rather than a missing carrier-based pricing rule.

I also updated `build.gradle`: swapped TestNG for JUnit Jupiter 5.11.4 + `tabletest-junit:1.2.1` and switched the `test` task to `useJUnitPlatform()`, since this was the only test class in the project.