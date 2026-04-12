Key findings from the reference:
- `BigDecimal` is not in the explicit built-in list but JUnit 5's `StringToNumberConverter` handles it — I'll use it directly.
- `ShippingZone` is custom, but splitting into `Region`/`Speed` columns avoids needing a type converter entirely.
- `Carrier` is an enum — handled natively.
- The three "same rate" rows (DHL/UPS/FEDEX) are a perfect value set: same inputs, same result, only carrier varies.

Here's the converted test:

```kotlin
class ShippingCostCalculatorTest {

    private val calculator = ShippingCostCalculator()

    @DisplayName("Shipping cost calculation")
    @TableTest("""
        Scenario                    | Region | Speed    | Weight | Dims          | Fragile | Insured value | Handling | Carrier           | Expected cost?
        EU standard light           | EU     | standard | 0.5    | [20, 15, 10]  |         |               |          | DHL               | 5.00
        EU standard medium          | EU     | standard | 3.0    | [30, 20, 15]  |         |               |          | DHL               | 7.50
        EU standard heavy           | EU     | standard | 10.0   | [40, 30, 20]  |         |               |          | DHL               | 12.50
        EU standard very heavy      | EU     | standard | 25.0   | [50, 40, 30]  |         |               |          | UPS               | 20.00
        EU express light            | EU     | express  | 0.5    | [20, 15, 10]  |         |               |          | DHL               | 8.00
        EU express medium           | EU     | express  | 3.0    | [30, 20, 15]  |         |               |          | UPS               | 12.00
        US standard light           | US     | standard | 0.5    | [20, 15, 10]  |         |               |          | FEDEX             | 7.00
        US express heavy            | US     | express  | 10.0   | [40, 30, 20]  |         |               |          | DHL               | 30.00
        Dimensional weight override | EU     | standard | 1.0    | [70, 50, 10]  |         |               |          | DHL               | 12.50
        Fragile surcharge           | EU     | standard | 3.0    | [30, 20, 15]  | true    |               |          | DHL               | 8.625
        Insured surcharge           | EU     | standard | 3.0    | [30, 20, 15]  |         | 500           |          | DHL               | 10.50
        Fragile and insured         | EU     | standard | 3.0    | [30, 20, 15]  | true    | 200           |          | DHL               | 11.625
        Hazmat handling             | EU     | standard | 3.0    | [30, 20, 15]  |         |               | hazmat   | DHL               | 15.50
        Oversize surcharge          | EU     | standard | 3.0    | [120, 5, 5]   |         |               |          | DHL               | 17.50
        Same rate                   | EU     | express  | 3.0    | [30, 20, 15]  |         |               |          | {DHL, UPS, FEDEX} | 12.00
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
        expectedCost: BigDecimal
    ) {
        val zone = ShippingZone(region = region, speed = speed)
        val opts = PackageOptions().apply {
            if (fragile == true) isFragile = true
            if (insuredValue != null) this.insuredValue = insuredValue
            if (handling != null) this.handling = handling
        }
        calculator.calculateShippingCost(zone, weight, dims, opts, carrier) shouldBe expectedCost
    }
}
```

Notable changes from the original:

- **`ShippingZone` split into `Region`/`Speed` columns** — avoids a type converter and makes the zone inputs directly readable in the table.
- **Blank cells for absent options** — `Fragile`, `Insured value`, and `Handling` are blank (null) in rows where they don't apply, with nullable parameter types to match. The original used `false`/`null` literals in positional args, which was easy to misread.
- **Value set for same-rate carriers** — the three "same rate" rows collapse to one row with `{DHL, UPS, FEDEX}`, which explicitly documents that the result is carrier-independent for that zone/weight combination. That intent was hidden in the original.
- **`BigDecimal` converts natively** via JUnit 5's `StringToNumberConverter`, and `Carrier` as an enum converts by name — no `@TypeConverter` methods needed.