package com.example

import io.kotest.matchers.shouldBe
import org.tabletest.junit.Description
import org.tabletest.junit.TableTest
import java.math.BigDecimal

class ShippingCostCalculatorTest {

    private val calculator = ShippingCostCalculator()

    @Description("""
        Dimensions are fixed at a negligible [10, 10, 10] cm so dimensional
        (volumetric) weight never exceeds actual weight here - the interaction
        between the two is covered separately below. Carrier does not affect
        cost; the {DHL, UPS, FEDEX} value set on the EU express row
        demonstrates this explicitly even though carrier is passed on every
        call.
        """)
    @TableTest("""
        Scenario                            | Region | Speed    | Weight (kg) | Carrier           | Cost?
        EU standard, light                  | EU     | standard | 0.5         | DHL               | 5.00
        EU standard, medium                 | EU     | standard | 3.0         | DHL               | 7.50
        EU standard, heavy                  | EU     | standard | 10.0        | DHL               | 12.50
        EU standard, very heavy             | EU     | standard | 25.0        | UPS               | 20.00
        EU express, light                   | EU     | express  | 0.5         | DHL               | 8.00
        EU express, medium, carrier ignored | EU     | express  | 3.0         | {DHL, UPS, FEDEX} | 12.00
        US standard, light                  | US     | standard | 0.5         | FEDEX             | 7.00
        US express, heavy                   | US     | express  | 10.0        | DHL               | 30.00
        """)
    fun `resolves base rate by zone, speed, and weight`(
        region: String,
        speed: String,
        weight: Double,
        carrier: Carrier,
        cost: BigDecimal
    ) {
        val zone = ShippingZone(region, speed)
        val dimensions = listOf(10, 10, 10)
        calculator.calculateShippingCost(zone, weight, dimensions, PackageOptions(), carrier)
            .compareTo(cost) shouldBe 0
    }

    @Description("""
        Effective weight is the greater of actual weight and dimensional
        weight (volume / 5000). Zone fixed at EU standard, carrier fixed at
        DHL.
        """)
    @TableTest("""
        Scenario                   | Weight (kg) | Dimensions (cm) | Cost?
        Actual weight governs      | 3.0         | [30, 20, 15]    | 7.50
        Dimensional weight governs | 1.0         | [70, 50, 10]    | 12.50
        """)
    fun `uses the greater of actual and dimensional weight`(
        weight: Double,
        dimensions: List<Int>,
        cost: BigDecimal
    ) {
        val zone = ShippingZone("EU", "standard")
        calculator.calculateShippingCost(zone, weight, dimensions, PackageOptions(), Carrier.DHL)
            .compareTo(cost) shouldBe 0
    }

    @Description("""
        A flat 10.00 surcharge applies when any single dimension exceeds
        100cm. Zone fixed at EU standard, weight fixed at 3.0kg, carrier
        fixed at DHL.
        """)
    @TableTest("""
        Scenario           | Dimensions (cm) | Cost?
        Within size limit  | [30, 20, 15]    | 7.50
        Exceeds size limit | [120, 5, 5]     | 17.50
        """)
    fun `adds oversize surcharge when a dimension exceeds the threshold`(
        dimensions: List<Int>,
        cost: BigDecimal
    ) {
        val zone = ShippingZone("EU", "standard")
        calculator.calculateShippingCost(zone, 3.0, dimensions, PackageOptions(), Carrier.DHL)
            .compareTo(cost) shouldBe 0
    }

    @Description("""
        Fragile packages incur a 15% multiplier on the base rate. Zone fixed
        at EU standard, weight fixed at 3.0kg, dimensions fixed at
        [30, 20, 15], carrier fixed at DHL.
        """)
    @TableTest("""
        Scenario          | Fragile | Cost?
        Standard handling | false   | 7.50
        Fragile handling  | true    | 8.625
        """)
    fun `adds fragile multiplier surcharge`(fragile: Boolean, cost: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val options = PackageOptions(isFragile = fragile)
        calculator.calculateShippingCost(zone, 3.0, listOf(30, 20, 15), options, Carrier.DHL)
            .compareTo(cost) shouldBe 0
    }

    @Description("""
        Insurance premium is 0.6% of the insured value, with a 3.00 minimum.
        An insured value of 500 yields a premium of exactly 3.00 (500 x
        0.006), landing right at the minimum - this table does not yet cover
        a value large enough for the 0.6% rate to exceed the minimum. Zone
        fixed at EU standard, weight fixed at 3.0kg, dimensions fixed at
        [30, 20, 15], carrier fixed at DHL.
        """)
    @TableTest("""
        Scenario                    | Insured Value | Cost?
        No insurance                |               | 7.50
        Insured, premium at minimum | 500           | 10.50
        """)
    fun `adds insurance premium with a minimum`(insuredValue: BigDecimal?, cost: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val options = PackageOptions(insuredValue = insuredValue)
        calculator.calculateShippingCost(zone, 3.0, listOf(30, 20, 15), options, Carrier.DHL)
            .compareTo(cost) shouldBe 0
    }

    @Description("""
        A flat 8.00 fee applies when handling is "hazmat". Zone fixed at EU
        standard, weight fixed at 3.0kg, dimensions fixed at [30, 20, 15],
        carrier fixed at DHL.
        """)
    @TableTest("""
        Scenario          | Handling | Cost?
        Standard handling |          | 7.50
        Hazmat handling   | hazmat   | 15.50
        """)
    fun `adds hazmat handling fee`(handling: String?, cost: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val options = PackageOptions(handling = handling)
        calculator.calculateShippingCost(zone, 3.0, listOf(30, 20, 15), options, Carrier.DHL)
            .compareTo(cost) shouldBe 0
    }

    @Description("""
        Confirms fragile and insurance surcharges apply cumulatively: the
        fragile multiplier is applied to the base rate first, then the
        insurance premium is added on top. Zone fixed at EU standard, weight
        fixed at 3.0kg, dimensions fixed at [30, 20, 15], carrier fixed at
        DHL.
        """)
    @TableTest("""
        Scenario          | Fragile | Insured Value | Cost?
        Neither surcharge | false   |               | 7.50
        Fragile only      | true    |               | 8.625
        Insured only      | false   | 200           | 10.50
        Both surcharges   | true    | 200           | 11.625
        """)
    fun `combines fragile and insurance surcharges`(
        fragile: Boolean,
        insuredValue: BigDecimal?,
        cost: BigDecimal
    ) {
        val zone = ShippingZone("EU", "standard")
        val options = PackageOptions(isFragile = fragile, insuredValue = insuredValue)
        calculator.calculateShippingCost(zone, 3.0, listOf(30, 20, 15), options, Carrier.DHL)
            .compareTo(cost) shouldBe 0
    }
}
