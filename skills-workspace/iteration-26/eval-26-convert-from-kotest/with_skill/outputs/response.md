Good. `BigDecimal` and `List<Int>` are handled by JUnit's built-in conversion, and `{DHL, UPS, FEDEX}` is valid value-set syntax for enum columns. The four "same rate" rows (EU express medium + 3 variants) collapse into one row with a carrier value set.

Here's the converted test:

```kotlin
class ShippingCostCalculatorTest {

    private val calculator = ShippingCostCalculator()

    @TableTest("""
        Scenario                    | Region | Speed    | Weight | Dims         | Fragile | Insured value | Handling | Carrier           | Cost?
        EU standard, light          | EU     | standard | 0.5    | [20, 15, 10] |         |               |          | DHL               | 5.00
        EU standard, medium         | EU     | standard | 3.0    | [30, 20, 15] |         |               |          | DHL               | 7.50
        EU standard, heavy          | EU     | standard | 10.0   | [40, 30, 20] |         |               |          | DHL               | 12.50
        EU standard, very heavy     | EU     | standard | 25.0   | [50, 40, 30] |         |               |          | UPS               | 20.00
        EU express, light           | EU     | express  | 0.5    | [20, 15, 10] |         |               |          | DHL               | 8.00
        EU express, medium          | EU     | express  | 3.0    | [30, 20, 15] |         |               |          | {DHL, UPS, FEDEX} | 12.00
        US standard, light          | US     | standard | 0.5    | [20, 15, 10] |         |               |          | FEDEX             | 7.00
        US express, heavy           | US     | express  | 10.0   | [40, 30, 20] |         |               |          | DHL               | 30.00
        Dimensional weight override | EU     | standard | 1.0    | [70, 50, 10] |         |               |          | DHL               | 12.50
        Fragile package             | EU     | standard | 3.0    | [30, 20, 15] | true    |               |          | DHL               | 8.625
        Insured package             | EU     | standard | 3.0    | [30, 20, 15] |         | 500           |          | DHL               | 10.50
        Fragile and insured         | EU     | standard | 3.0    | [30, 20, 15] | true    | 200           |          | DHL               | 11.625
        Hazmat handling             | EU     | standard | 3.0    | [30, 20, 15] |         |               | hazmat   | DHL               | 15.50
        Oversize package            | EU     | standard | 3.0    | [120, 5, 5]  |         |               |          | DHL               | 17.50
        """)
    fun shouldCalculateShippingCost(
        region: String,
        speed: String,
        weight: BigDecimal,
        dims: List<Int>,
        fragile: Boolean?,
        insuredValue: Int?,
        handling: String?,
        carrier: Carrier,
        cost: BigDecimal,
    ) {
        val zone = ShippingZone(region = region, speed = speed)
        val opts = PackageOptions()
        if (fragile == true) opts.isFragile = true
        if (insuredValue != null) opts.insuredValue = insuredValue
        if (handling != null) opts.handling = handling
        calculator.calculateShippingCost(zone, weight, dims, opts, carrier) shouldBe cost
    }
}
```

A few things to note:

**Value set on carrier** — the original had four separate rows all asserting cost `12.00` for EU express medium (one per carrier). `{DHL, UPS, FEDEX}` on a single row expresses the intent more directly: *any carrier gives the same rate here*.

**`ShippingZone` decomposed** — rather than a TypeConverter for `ShippingZone`, the two fields become plain `String` columns and the object is constructed in the test body. This keeps the table readable without a converter.

**Blank cells for optional package options** — `Fragile`, `Insured value`, and `Handling` are all nullable. Blank means "not set"; the test body only applies each option when the value is non-null.

**No TypeConverter needed** — `BigDecimal`, `List<Int>`, and `Carrier` (enum) are all handled by JUnit's built-in conversion.