package com.example

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.tabletest.junit.Description
import org.tabletest.junit.TableTest
import java.math.BigDecimal

class ShippingCostCalculatorTest {

    private val calculator = ShippingCostCalculator()

    @DisplayName("Determines the base rate from region, speed and weight")
    @Description("""
        Dimensions are held at 1x1x1 cm, negligible relative to any weight below,
        so the effective weight used for rating equals Weight (kg).
        """)
    @TableTest("""
        Scenario                        | Region | Speed    | Weight (kg) | Carrier           | Shipping Cost?
        EU standard, top of light tier  | EU     | standard | 1.0         | DHL               | 5.00
        EU standard, medium tier begins | EU     | standard | 1.01        | DHL               | 7.50
        EU standard, top of medium tier | EU     | standard | 5.0         | DHL               | 7.50
        EU standard, heavy tier begins  | EU     | standard | 5.01        | DHL               | 12.50
        EU standard, top of heavy tier  | EU     | standard | 15.0        | DHL               | 12.50
        EU standard, very heavy begins  | EU     | standard | 15.01       | DHL               | 20.00
        EU express, light               | EU     | express  | 1.0         | DHL               | 8.00
        EU express, medium              | EU     | express  | 5.0         | {DHL, UPS, FEDEX} | 12.00
        EU express, heavy               | EU     | express  | 15.0        | DHL               | 20.00
        EU express, very heavy          | EU     | express  | 25.0        | DHL               | 32.00
        US standard, light              | US     | standard | 1.0         | DHL               | 7.00
        US standard, medium             | US     | standard | 5.0         | DHL               | 10.50
        US standard, heavy              | US     | standard | 15.0        | DHL               | 17.50
        US standard, very heavy         | US     | standard | 25.0        | DHL               | 28.00
        US express, light               | US     | express  | 1.0         | DHL               | 11.00
        US express, medium              | US     | express  | 5.0         | DHL               | 16.50
        US express, heavy               | US     | express  | 15.0        | DHL               | 30.00
        US express, very heavy          | US     | express  | 25.0        | DHL               | 45.00
        """)
    fun determinesBaseRate(region: String, speed: String, weight: Double, carrier: Carrier, shippingCost: BigDecimal) {
        assertMoneyEquals(shippingCost, cost(region = region, speed = speed, weight = weight, dimensions = listOf(1, 1, 1), carrier = carrier))
    }

    @DisplayName("Uses whichever of actual or dimensional weight is greater")
    @Description("""
        Region and speed are held at EU standard, whose rate ladder is established in
        the base-rate table above: 7.50 up to 5 kg effective weight, 12.50 above it.
        """)
    @TableTest("""
        Scenario                                    | Weight (kg) | Dimensions (cm) | Shipping Cost?
        Actual weight decides, dimensions small     | 4.9         | [10, 10, 10]    | 7.50
        Dimensional weight overrides and lifts tier | 4.9         | [60, 50, 10]    | 12.50
        """)
    fun resolvesEffectiveWeight(weight: Double, dimensions: List<Int>, shippingCost: BigDecimal) {
        assertMoneyEquals(shippingCost, cost(weight = weight, dimensions = dimensions))
    }

    @DisplayName("Adds a flat surcharge when any dimension exceeds the oversize threshold")
    @Description("""
        Weight, region, speed and the width/height of the parcel are fixed at 3 kg,
        EU standard, 10 cm - already covered by the tables above - so only the length
        dimension crosses the oversize threshold.
        """)
    @TableTest("""
        Scenario                         | Dimensions (cm) | Shipping Cost?
        At the oversize threshold        | [100, 10, 10]   | 7.50
        Just past the oversize threshold | [101, 10, 10]   | 17.50
        """)
    fun addsOversizeSurcharge(dimensions: List<Int>, shippingCost: BigDecimal) {
        assertMoneyEquals(shippingCost, cost(dimensions = dimensions))
    }

    @DisplayName("Adds a flat fee when handling is hazmat")
    @Description("""
        Weight, region, speed and dimensions are fixed at 3 kg, EU standard, 10x10x10
        cm - already covered by the tables above.
        """)
    @TableTest("""
        Scenario                 | Handling | Shipping Cost?
        No handling instructions |          | 7.50
        Non-hazmat handling code | priority | 7.50
        Hazmat handling          | hazmat   | 15.50
        """)
    fun addsHazmatHandlingFee(handling: String?, shippingCost: BigDecimal) {
        assertMoneyEquals(shippingCost, cost(handling = handling))
    }

    @DisplayName("Applies a surcharge multiplier when the package is fragile")
    @TableTest("""
        Scenario    | Fragile | Shipping Cost?
        Not fragile | false   | 7.50
        Fragile     | true    | 8.625
        """)
    fun appliesFragileMultiplier(fragile: Boolean, shippingCost: BigDecimal) {
        assertMoneyEquals(shippingCost, cost(fragile = fragile))
    }

    @DisplayName("Adds an insurance premium of whichever is greater: a percentage of the insured value, or a flat minimum")
    @Description("""
        Base cost before insurance is fixed at 7.50 (3 kg, EU standard) - established
        in the base-rate table above.
        """)
    @TableTest("""
        Scenario                       | Insured Value | Shipping Cost?
        No insurance requested         |               | 7.50
        Premium below the minimum      | 499           | 10.50
        Premium just above the minimum | 501           | 10.506
        """)
    fun addsInsurancePremium(insuredValue: BigDecimal?, shippingCost: BigDecimal) {
        assertMoneyEquals(shippingCost, cost(insuredValue = insuredValue))
    }

    @DisplayName("Applies the fragile multiplier to flat fees but not to the insurance premium")
    @Description("""
        Confirms the order surcharges combine in, beyond what the oversize, hazmat,
        fragile and insurance tables above show individually.
        """)
    @TableTest("""
        Scenario                     | Dimensions (cm) | Handling | Fragile | Insured Value | Shipping Cost?
        Oversize, hazmat and fragile | [101, 10, 10]   | hazmat   | true    |               | 29.325
        Fragile and insured          | [10, 10, 10]    |          | true    | 200           | 11.625
        """)
    fun combinesFragileWithOtherSurcharges(
        dimensions: List<Int>, handling: String?, fragile: Boolean, insuredValue: BigDecimal?, shippingCost: BigDecimal
    ) {
        assertMoneyEquals(shippingCost, cost(dimensions = dimensions, handling = handling, fragile = fragile, insuredValue = insuredValue))
    }

    private fun cost(
        weight: Double = 3.0,
        dimensions: List<Int> = listOf(10, 10, 10),
        region: String = "EU",
        speed: String = "standard",
        fragile: Boolean = false,
        insuredValue: BigDecimal? = null,
        handling: String? = null,
        carrier: Carrier = Carrier.DHL
    ): BigDecimal {
        val zone = ShippingZone(region = region, speed = speed)
        val options = PackageOptions(isFragile = fragile, insuredValue = insuredValue, handling = handling)
        return calculator.calculateShippingCost(zone, weight, dimensions, options, carrier)
    }

    private fun assertMoneyEquals(expected: BigDecimal, actual: BigDecimal) {
        assertEquals(0, expected.compareTo(actual)) { "expected $expected but was $actual" }
    }
}
