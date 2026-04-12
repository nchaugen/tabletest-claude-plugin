No build file or source files in this workspace, so I'll produce the converted code directly.

---

**Design analysis before writing:**

The original 17 rows cover three distinct concerns:
1. **Base rate** — how zone (region + speed), weight, and carrier determine cost (8 rows, including the 3 "same rate" rows which collapse into a value set `{DHL, UPS, FEDEX}`)
2. **Dimension effects** — dimensional weight override and oversize surcharge (2 rows)
3. **Package surcharges** — fragile, insurance, hazmat combinations (4 rows + 1 baseline)

The `ShippingZone` fields expand into two plain `String` columns (`Region`, `Speed`) — no converter needed. `Carrier` is a JUnit-convertible enum. `BigDecimal`, `List<Int>` are handled by built-in conversion. `PackageOptions` is constructed in the method body from three nullable/boolean columns; no if/switch, just `apply`.

---

```kotlin
import org.tabletest.junit.TableTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class ShippingCostCalculatorTest {

    private val calculator = ShippingCostCalculator()

    @DisplayName("Base shipping cost by zone and weight")
    @TableTest("""
        Scenario               | Region | Speed    | Weight | Dimensions   | Carrier           | Cost?
        EU standard light      | EU     | standard | 0.5    | [20, 15, 10] | DHL               | 5.00
        EU standard medium     | EU     | standard | 3.0    | [30, 20, 15] | DHL               | 7.50
        EU standard heavy      | EU     | standard | 10.0   | [40, 30, 20] | DHL               | 12.50
        EU standard very heavy | EU     | standard | 25.0   | [50, 40, 30] | UPS               | 20.00
        EU express light       | EU     | express  | 0.5    | [20, 15, 10] | DHL               | 8.00
        EU express medium      | EU     | express  | 3.0    | [30, 20, 15] | {DHL, UPS, FEDEX} | 12.00
        US standard light      | US     | standard | 0.5    | [20, 15, 10] | FEDEX             | 7.00
        US express heavy       | US     | express  | 10.0   | [40, 30, 20] | DHL               | 30.00
        """)
    fun baseShippingCost(
        region: String, speed: String,
        weight: BigDecimal, dimensions: List<Int>,
        carrier: Carrier, cost: BigDecimal
    ) {
        val zone = ShippingZone(region = region, speed = speed)
        calculator.calculateShippingCost(zone, weight, dimensions, PackageOptions(), carrier) shouldBe cost
    }

    @DisplayName("Dimension effects on cost")
    @Description("""
        Dimensional weight = (l × w × h) / divisor; replaces actual weight when greater.
        Oversize surcharge applies when the longest side exceeds the oversize threshold.
        All rows use EU standard zone, carrier DHL, no other surcharges.
        """)
    @TableTest("""
        Scenario                            | Weight | Dimensions   | Cost?
        Dimensional weight overrides actual | 1.0    | [70, 50, 10] | 12.50
        Oversize surcharge                  | 3.0    | [120, 5, 5]  | 17.50
        """)
    fun dimensionEffectsOnCost(weight: BigDecimal, dimensions: List<Int>, cost: BigDecimal) {
        val zone = ShippingZone(region = "EU", speed = "standard")
        calculator.calculateShippingCost(zone, weight, dimensions, PackageOptions(), Carrier.DHL) shouldBe cost
    }

    @DisplayName("Package surcharges")
    @Description("""
        All rows use EU standard zone, 3.0 kg, dimensions [30, 20, 15], carrier DHL.
        Fragile and insurance surcharges are additive.
        """)
    @TableTest("""
        Scenario            | Fragile | Insured Value | Handling | Cost?
        No surcharges       | false   |               |          | 7.50
        Fragile             | true    |               |          | 8.625
        Insurance           | false   | 500           |          | 10.50
        Fragile and insured | true    | 200           |          | 11.625
        Hazmat handling     | false   |               | hazmat   | 15.50
        """)
    fun packageSurcharges(
        fragile: Boolean, insuredValue: Int?, handling: String?,
        cost: BigDecimal
    ) {
        val zone = ShippingZone(region = "EU", speed = "standard")
        val opts = PackageOptions().apply {
            isFragile = fragile
            insuredValue?.also { this.insuredValue = it }
            handling?.also { this.handling = it }
        }
        calculator.calculateShippingCost(zone, BigDecimal("3.0"), listOf(30, 20, 15), opts, Carrier.DHL) shouldBe cost
    }
}
```

**Key decisions:**

- **`{DHL, UPS, FEDEX}` value set** on the EU express medium row replaces the three "same rate" rows — asserting the cost is carrier-invariant for that combination rather than repeating the row three times
- **`Region` + `Speed` columns** instead of a `ShippingZone` converter — the zone fields are simple strings and the constructor call is minimal setup, not logic
- **`dimensionEffectsOnCost`** groups the two dimension-driven mechanisms (dimensional weight and oversize) as a 2-row table rather than forcing a single-row `@Test` or folding them into the base rate table where they'd obscure the weight-tier pattern
- **Baseline "No surcharges" row** added to the surcharges table so each surcharge row has a visible reference point
- **Blank cells** for `insuredValue` and `handling` map to `null` via nullable parameter types (`Int?`, `String?`); the `apply` block sets properties only when present