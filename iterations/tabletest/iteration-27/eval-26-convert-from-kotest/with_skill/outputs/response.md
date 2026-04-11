Good. I have what I need. Here's the design:

**Two tables** split by concern:
1. **Base rates** — zone × weight × carrier (including dimensional weight and carrier parity via value set)
2. **Package option surcharges** — fragile, insured value, handling, oversize (fixed zone/weight/carrier)

`ShippingZone` splits naturally into two `String` columns (Region, Speed), constructed in the test body. `Carrier`, `BigDecimal`, `List<Int>`, and `Boolean` all convert built-in — no `@TypeConverter` needed.

```kotlin
class ShippingCostCalculatorTest {

    private val calculator = ShippingCostCalculator()

    @DisplayName("Base shipping rates")
    @Description("""
        All rows use default PackageOptions (no fragile, no insurance, no special handling).
        Weight in kg; dimensions in cm [length, width, height].
        """)
    @TableTest("""
        Scenario                    | Region | Speed    | Weight | Dimensions    | Carrier           | Cost?
        EU standard light           | EU     | standard | 0.5    | [20, 15, 10]  | DHL               | 5.00
        EU standard medium          | EU     | standard | 3.0    | [30, 20, 15]  | DHL               | 7.50
        EU standard heavy           | EU     | standard | 10.0   | [40, 30, 20]  | DHL               | 12.50
        EU standard very heavy      | EU     | standard | 25.0   | [50, 40, 30]  | UPS               | 20.00
        EU express light            | EU     | express  | 0.5    | [20, 15, 10]  | DHL               | 8.00
        EU express medium           | EU     | express  | 3.0    | [30, 20, 15]  | {DHL, UPS, FEDEX} | 12.00
        US standard light           | US     | standard | 0.5    | [20, 15, 10]  | FEDEX             | 7.00
        US express heavy            | US     | express  | 10.0   | [40, 30, 20]  | DHL               | 30.00
        Dimensional weight governs  | EU     | standard | 1.0    | [70, 50, 10]  | DHL               | 12.50
        """)
    fun shouldCalculateBaseRate(
        region: String, speed: String, weight: BigDecimal,
        dims: List<Int>, carrier: Carrier, expected: BigDecimal,
    ) {
        val zone = ShippingZone(region = region, speed = speed)
        calculator.calculateShippingCost(zone, weight, dims, PackageOptions(), carrier) shouldBe expected
    }

    @DisplayName("Package option surcharges")
    @Description("""
        Base: EU standard zone, 3.0 kg, carrier DHL. Baseline cost without surcharges: 7.50.
        """)
    @TableTest("""
        Scenario            | Dimensions    | Fragile | Insured Value | Handling | Cost?
        No surcharges       | [30, 20, 15]  | false   |               |          | 7.50
        Fragile surcharge   | [30, 20, 15]  | true    |               |          | 8.625
        Insured surcharge   | [30, 20, 15]  | false   | 500           |          | 10.50
        Fragile and insured | [30, 20, 15]  | true    | 200           |          | 11.625
        Hazmat handling     | [30, 20, 15]  | false   |               | hazmat   | 15.50
        Oversize surcharge  | [120, 5, 5]   | false   |               |          | 17.50
        """)
    fun shouldApplySurcharges(
        dims: List<Int>, fragile: Boolean, insuredValue: Int?,
        handling: String?, expected: BigDecimal,
    ) {
        val zone = ShippingZone(region = "EU", speed = "standard")
        val opts = PackageOptions().apply {
            isFragile = fragile
            insuredValue?.let { this.insuredValue = it }
            handling?.let { this.handling = it }
        }
        calculator.calculateShippingCost(zone, BigDecimal("3.0"), dims, opts, Carrier.DHL) shouldBe expected
    }
}
```

A few things worth noting:

**Carrier parity as a value set** — the three "same rate DHL/UPS/FEDEX" rows in the original collapsed into one row with `{DHL, UPS, FEDEX}` on the EU express medium row. This documents the intent clearly: the cost is 12.00 *regardless of carrier* for that combination.

**"No surcharges" baseline row** — added to the surcharges table so the 7.50 baseline is explicit and all surcharge amounts are traceable from it.

**Dimensional weight governs** — kept in the base rates table rather than a separate single-row table, since it still tests the same concern: what does the calculator return for these zone/weight/dims inputs?

**`Int?` for `insuredValue`** — blank cells become `null` for nullable types, which is how JUnit handles them. Kotlin's `?.let` avoids any explicit `if` in the test body.