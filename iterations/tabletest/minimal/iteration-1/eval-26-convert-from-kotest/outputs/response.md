Looking at this test, I can see three distinct concerns:
1. **Base rate** — zone, speed, weight, and carrier determine the cost
2. **Dimensional weight** — volumetric weight can override actual weight
3. **Package option surcharges** — fragile, insurance, hazmat
4. **Oversize surcharge** — triggered by large dimensions, separate from package options

The "same rate for all carriers" rows collapse nicely into a value set. Here's the conversion:

```kotlin
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.tabletest.junit.Description
import org.tabletest.junit.TableTest
import java.math.BigDecimal

class ShippingCostCalculatorTest {

    private val calculator = ShippingCostCalculator()

    @DisplayName("Base shipping rate by zone, speed, weight, and carrier")
    @Description("""
        No package options (fragile, insurance, handling) are set for any row.
        Dims are sized proportionate to weight and do not trigger dimensional weight override.
        """)
    @TableTest("""
        Scenario               | Region | Speed    | Weight (kg) | Dims         | Carrier           | Cost?
        EU standard light      | EU     | standard | 0.5         | [20, 15, 10] | DHL               | 5.00
        EU standard medium     | EU     | standard | 3.0         | [30, 20, 15] | DHL               | 7.50
        EU standard heavy      | EU     | standard | 10.0        | [40, 30, 20] | DHL               | 12.50
        EU standard very heavy | EU     | standard | 25.0        | [50, 40, 30] | UPS               | 20.00
        EU express light       | EU     | express  | 0.5         | [20, 15, 10] | DHL               | 8.00
        EU express medium      | EU     | express  | 3.0         | [30, 20, 15] | {DHL, UPS, FEDEX} | 12.00
        US standard light      | US     | standard | 0.5         | [20, 15, 10] | FEDEX             | 7.00
        US express heavy       | US     | express  | 10.0        | [40, 30, 20] | DHL               | 30.00
        """)
    fun calculatesBaseRate(
        region: String, speed: String, weight: BigDecimal, dims: List<Int>,
        carrier: Carrier, expectedCost: BigDecimal
    ) {
        val zone = ShippingZone(region = region, speed = speed)
        calculator.calculateShippingCost(zone, weight, dims, PackageOptions(), carrier) shouldBe expectedCost
    }

    @DisplayName("Dimensional weight override")
    @Description("""
        Dimensional weight = L × W × H / 5000.
        When dimensional weight exceeds actual weight, it is used for rate selection.
        Zone is EU standard, carrier is DHL.
        """)
    @TableTest("""
        Scenario                     | Actual Weight (kg) | Dims         | Cost?
        Volume exceeds actual weight | 1.0                | [70, 50, 10] | 12.50
        """)
    fun appliesDimensionalWeightOverride(weight: BigDecimal, dims: List<Int>, expectedCost: BigDecimal) {
        val zone = ShippingZone(region = "EU", speed = "standard")
        calculator.calculateShippingCost(zone, weight, dims, PackageOptions(), Carrier.DHL) shouldBe expectedCost
    }

    @DisplayName("Package option surcharges")
    @Description("""
        All rows use EU standard zone, 3.0 kg, [30, 20, 15] dims, DHL carrier.
        Base cost without surcharges for these inputs is 7.50.
        """)
    @TableTest("""
        Scenario              | Fragile | Insured Value | Handling | Cost?
        Fragile               | true    |               |          | 8.625
        Insurance (500)       | false   | 500           |          | 10.50
        Fragile + insured 200 | true    | 200           |          | 11.625
        Hazmat                | false   |               | hazmat   | 15.50
        """)
    fun appliesPackageOptionSurcharges(
        fragile: Boolean, insuredValue: Int?, handling: String?,
        expectedCost: BigDecimal
    ) {
        val zone = ShippingZone(region = "EU", speed = "standard")
        val opts = PackageOptions().apply {
            if (fragile) isFragile = true
            insuredValue?.let { this.insuredValue = it }
            handling?.let { this.handling = it }
        }
        calculator.calculateShippingCost(zone, BigDecimal("3.0"), listOf(30, 20, 15), opts, Carrier.DHL) shouldBe expectedCost
    }

    @DisplayName("Oversize dimension surcharge")
    @Description("""
        Oversize is triggered when at least one dimension exceeds the carrier threshold.
        This is determined from dims alone — no package option flag is needed.
        Zone is EU standard, carrier is DHL.
        """)
    @TableTest("""
        Scenario           | Weight (kg) | Dims        | Cost?
        One oversized side | 3.0         | [120, 5, 5] | 17.50
        """)
    fun appliesOversizeSurcharge(weight: BigDecimal, dims: List<Int>, expectedCost: BigDecimal) {
        val zone = ShippingZone(region = "EU", speed = "standard")
        calculator.calculateShippingCost(zone, weight, dims, PackageOptions(), Carrier.DHL) shouldBe expectedCost
    }
}
```

A few decisions worth noting:

- **`{DHL, UPS, FEDEX}` value set** on the EU express medium row — the original had three identical rows to assert all carriers return the same rate. The value set expresses "regardless of carrier" directly.
- **Oversize gets its own table** — it's triggered by dims (not a `PackageOptions` flag), so lumping it with fragile/insurance/hazmat would mix two different surcharge mechanisms. The separate table makes that distinction visible.
- **`@Description` for surcharges table** — the fixed zone/weight/carrier/dims are inputs not shown in the table columns, so they're documented in the description rather than bloating every row.
- **No custom `@TypeConverter` needed** — `BigDecimal` and enums are handled by JUnit's built-in conversion; `List<Int>` works natively with `[20, 15, 10]` syntax.