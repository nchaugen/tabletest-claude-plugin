Looking at this test, I can identify two distinct concerns:

1. **Base cost routing** — how zone, weight, and dims determine cost (no special options)
2. **Surcharges** — how `PackageOptions` flags add to the base cost

The original 17 rows also have redundancy: rows 6 and 15–17 all test EU express medium at 12.00 for different carriers — a value set collapses those four rows into one.

```kotlin
class ShippingCostCalculatorTest {

    private val calculator = ShippingCostCalculator()

    @TableTest("""
        Scenario                    | Region | Speed    | Weight | Dims         | Carrier           | Expected cost?
        EU standard light           | EU     | standard | 0.5    | [20, 15, 10] | DHL               | 5.00
        EU standard medium          | EU     | standard | 3.0    | [30, 20, 15] | DHL               | 7.50
        EU standard heavy           | EU     | standard | 10.0   | [40, 30, 20] | DHL               | 12.50
        EU standard very heavy      | EU     | standard | 25.0   | [50, 40, 30] | UPS               | 20.00
        EU express light            | EU     | express  | 0.5    | [20, 15, 10] | DHL               | 8.00
        EU express medium           | EU     | express  | 3.0    | [30, 20, 15] | {DHL, UPS, FEDEX} | 12.00
        US standard light           | US     | standard | 0.5    | [20, 15, 10] | FEDEX             | 7.00
        US express heavy            | US     | express  | 10.0   | [40, 30, 20] | DHL               | 30.00
        Dimensional weight override | EU     | standard | 1.0    | [70, 50, 10] | DHL               | 12.50
        """)
    fun `base shipping cost by zone and weight`(
        region: String, speed: String, weight: BigDecimal,
        dims: List<Int>, carrier: Carrier, expectedCost: BigDecimal
    ) {
        val zone = ShippingZone(region = region, speed = speed)
        calculator.calculateShippingCost(zone, weight, dims, PackageOptions(), carrier) shouldBe expectedCost
    }

    @Description("""
        Base package: EU standard, 3.0 kg, DHL carrier. Base cost without surcharges: 7.50.
        Oversize applies when any dimension exceeds 100 cm.
        """)
    @TableTest("""
        Scenario            | Dims         | Fragile | Insured value | Handling | Expected cost?
        Fragile surcharge   | [30, 20, 15] | true    |               |          | 8.625
        Insurance surcharge | [30, 20, 15] | false   | 500           |          | 10.50
        Fragile and insured | [30, 20, 15] | true    | 200           |          | 11.625
        Hazmat handling     | [30, 20, 15] | false   |               | hazmat   | 15.50
        Oversize surcharge  | [120, 5, 5]  | false   |               |          | 17.50
        """)
    fun `surcharges on EU standard package`(
        dims: List<Int>, fragile: Boolean, insuredValue: Int?,
        handling: String?, expectedCost: BigDecimal
    ) {
        val zone = ShippingZone(region = "EU", speed = "standard")
        val opts = PackageOptions().apply {
            isFragile = fragile
            insuredValue?.let { this.insuredValue = it }
            handling?.let { this.handling = it }
        }
        calculator.calculateShippingCost(zone, BigDecimal("3.0"), dims, opts, Carrier.DHL) shouldBe expectedCost
    }
}
```

A few things worth noting:

- **`{DHL, UPS, FEDEX}` value set** on the EU express medium row replaces rows 6, 15, 16, and 17. The value set is valid here because all three carriers genuinely produce the same cost — the test is asserting carrier-agnostic pricing for that zone/weight.
- **Blank cells** for `Insured value` and `Handling` become `null` via the nullable `Int?` / `String?` parameter types. The `?.let` calls in the method body handle the conditional assignment without `if` statements.
- **`BigDecimal` and `Carrier`** convert automatically via JUnit's built-in converters — no `@TypeConverter` needed.
- **`Dims` as a column in the surcharges table** lets the oversize row naturally vary dimensions while keeping all surcharge scenarios together.