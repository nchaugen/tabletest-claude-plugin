package com.example

import org.junit.jupiter.api.Assertions.assertEquals
import org.tabletest.junit.Description
import org.tabletest.junit.TableTest
import java.math.BigDecimal

class ShippingCostCalculatorTest {

    private val calculator = ShippingCostCalculator()

    @Description("""
        Package is a fixed 10x10x10 cm so dimensional weight (0.002 kg) never
        overrides actual weight, isolating the region/speed/weight-bracket
        rule. Carrier does not affect the rate (see shouldNotVaryRateByCarrier).
        """)
    @TableTest("""
        Scenario                       | Region | Speed    | Weight | Rate?
        Light parcel, EU standard      | EU     | standard | 0.5    | 5.00
        Medium parcel, EU standard     | EU     | standard | 3.0    | 7.50
        Heavy parcel, EU standard      | EU     | standard | 10.0   | 12.50
        Very heavy parcel, EU standard | EU     | standard | 25.0   | 20.00
        Light parcel, EU express       | EU     | express  | 0.5    | 8.00
        Medium parcel, EU express      | EU     | express  | 3.0    | 12.00
        Light parcel, US standard      | US     | standard | 0.5    | 7.00
        Heavy parcel, US express       | US     | express  | 10.0   | 30.00
        """)
    fun shouldResolveBaseRateByRegionSpeedAndWeight(region: String, speed: String, weight: Double, rate: BigDecimal) {
        val zone = ShippingZone(region = region, speed = speed)
        val result = calculator.calculateShippingCost(zone, weight, listOf(10, 10, 10), PackageOptions(), Carrier.DHL)
        assertEquals(0, rate.compareTo(result))
    }

    @Description("""
        Effective weight is the greater of actual weight and dimensional
        weight (length x width x height / 5000). The EU standard rate at
        that effective weight applies.
        """)
    @TableTest("""
        Scenario                          | Weight | Length | Width | Height | Rate?
        Dimensional weight exceeds actual  | 1.0    | 70     | 50    | 10     | 12.50
        """)
    fun shouldUseDimensionalWeightWhenItExceedsActualWeight(
        weight: Double, length: Int, width: Int, height: Int, rate: BigDecimal
    ) {
        val zone = ShippingZone(region = "EU", speed = "standard")
        val result = calculator.calculateShippingCost(zone, weight, listOf(length, width, height), PackageOptions(), Carrier.DHL)
        assertEquals(0, rate.compareTo(result))
    }

    @Description("""
        Base rate for a 3.0 kg, EU standard package is ${'$'}7.50. Any single
        dimension over 100 cm adds a flat ${'$'}10.00 oversize surcharge.
        """)
    @TableTest("""
        Scenario           | Length | Width | Height | Rate?
        Oversize dimension | 120    | 5     | 5      | 17.50
        """)
    fun shouldAddOversizeSurchargeWhenAnyDimensionExceedsThreshold(
        length: Int, width: Int, height: Int, rate: BigDecimal
    ) {
        val zone = ShippingZone(region = "EU", speed = "standard")
        val result = calculator.calculateShippingCost(zone, 3.0, listOf(length, width, height), PackageOptions(), Carrier.DHL)
        assertEquals(0, rate.compareTo(result))
    }

    @Description("""
        Base rate for a 3.0 kg, EU standard, 30x20x15 cm package is
        ${'$'}7.50. Fragile (x1.15) and hazmat (+${'$'}8.00) surcharges apply
        to that base; insurance premium (0.6% of insured value, ${'$'}3.00
        minimum) is added last.
        """)
    @TableTest("""
        Scenario                     | Fragile | Insured Value | Handling | Rate?
        Fragile package              | true    |                |          | 8.625
        Insured package              | false   | 500            |          | 10.50
        Fragile and insured package  | true    | 200            |          | 11.625
        Hazmat handling              | false   |                | hazmat   | 15.50
        """)
    fun shouldApplyHandlingAndInsuranceSurcharges(
        fragile: Boolean, insuredValue: BigDecimal?, handling: String?, rate: BigDecimal
    ) {
        val zone = ShippingZone(region = "EU", speed = "standard")
        val options = PackageOptions(isFragile = fragile, insuredValue = insuredValue, handling = handling)
        val result = calculator.calculateShippingCost(zone, 3.0, listOf(30, 20, 15), options, Carrier.DHL)
        assertEquals(0, rate.compareTo(result))
    }

    @TableTest("""
        Scenario                     | Carrier           | Rate?
        Rate is carrier-independent  | {DHL, UPS, FEDEX} | 12.00
        """)
    fun shouldNotVaryRateByCarrier(carrier: Carrier, rate: BigDecimal) {
        val zone = ShippingZone(region = "EU", speed = "express")
        val result = calculator.calculateShippingCost(zone, 3.0, listOf(30, 20, 15), PackageOptions(), carrier)
        assertEquals(0, rate.compareTo(result))
    }
}
