package com.example

import org.junit.jupiter.api.Assertions.assertEquals
import org.tabletest.junit.Description
import org.tabletest.junit.TableTest
import java.math.BigDecimal

class ShippingCostCalculatorTest {

    private val calculator = ShippingCostCalculator()

    @TableTest("""
        Scenario                    | Region | Speed    | Weight | Dimensions   | Base Cost?
        EU standard, light          | EU     | standard | 0.5    | [20, 15, 10] | 5.00
        EU standard, medium         | EU     | standard | 3.0    | [30, 20, 15] | 7.50
        EU standard, heavy          | EU     | standard | 10.0   | [40, 30, 20] | 12.50
        EU standard, very heavy     | EU     | standard | 25.0   | [50, 40, 30] | 20.00
        EU express, light           | EU     | express  | 0.5    | [20, 15, 10] | 8.00
        EU express, medium          | EU     | express  | 3.0    | [30, 20, 15] | 12.00
        US standard, light          | US     | standard | 0.5    | [20, 15, 10] | 7.00
        US express, heavy           | US     | express  | 10.0   | [40, 30, 20] | 30.00
        Dimensional weight override | EU     | standard | 1.0    | [70, 50, 10] | 12.50
        """)
    fun calculatesBaseRateByZoneWeightAndDimensions(
        region: String, speed: String, weight: Double, dimensions: List<Int>, baseCost: BigDecimal
    ) {
        val cost = calculator.calculateShippingCost(
            ShippingZone(region, speed), weight, dimensions, PackageOptions(), Carrier.DHL
        )
        assertEquals(0, baseCost.compareTo(cost))
    }

    @Description("""
        All rows use EU standard shipping at 3.0 kg, giving a base cost of 7.50
        before the oversize surcharge. A package is oversize if any single
        dimension exceeds 100.
        """)
    @TableTest("""
        Scenario                      | Dimensions   | Total Cost?
        At oversize threshold         | [100, 5, 5]  | 7.50
        Just over oversize threshold  | [101, 5, 5]  | 17.50
        Well over oversize threshold  | [120, 5, 5]  | 17.50
        """)
    fun addsOversizeSurchargeForLargeDimensions(dimensions: List<Int>, totalCost: BigDecimal) {
        val cost = calculator.calculateShippingCost(
            ShippingZone("EU", "standard"), 3.0, dimensions, PackageOptions(), Carrier.DHL
        )
        assertEquals(0, totalCost.compareTo(cost))
    }

    @Description("""
        All rows use EU standard shipping at 3.0 kg with non-oversize dimensions,
        giving a base cost of 7.50 before the hazmat surcharge.
        """)
    @TableTest("""
        Scenario             | Handling | Total Cost?
        No special handling  |          | 7.50
        Hazmat handling      | hazmat   | 15.50
        """)
    fun addsHazmatHandlingSurcharge(handling: String?, totalCost: BigDecimal) {
        val options = PackageOptions(handling = handling)
        val cost = calculator.calculateShippingCost(
            ShippingZone("EU", "standard"), 3.0, listOf(30, 20, 15), options, Carrier.DHL
        )
        assertEquals(0, totalCost.compareTo(cost))
    }

    @Description("""
        All rows use EU standard shipping at 3.0 kg with non-oversize dimensions,
        giving a base cost of 7.50 before the fragile surcharge.
        """)
    @TableTest("""
        Scenario     | Fragile | Total Cost?
        Not fragile  | false   | 7.50
        Fragile      | true    | 8.625
        """)
    fun addsFragileSurcharge(fragile: Boolean, totalCost: BigDecimal) {
        val options = PackageOptions(isFragile = fragile)
        val cost = calculator.calculateShippingCost(
            ShippingZone("EU", "standard"), 3.0, listOf(30, 20, 15), options, Carrier.DHL
        )
        assertEquals(0, totalCost.compareTo(cost))
    }

    @Description("""
        All rows use EU standard shipping at 3.0 kg with non-oversize dimensions,
        giving a base cost of 7.50 before insurance. Insurance premium is the
        insured value times 0.6%, with a 3.00 minimum.
        """)
    @TableTest("""
        Scenario                            | Insured Value | Total Cost?
        No insurance                        |                | 7.50
        Insured value below minimum premium | 100            | 10.50
        Insured value at minimum premium    | 500            | 10.50
        Insured value above minimum premium | 1000           | 13.50
        """)
    fun addsInsuranceSurcharge(insuredValue: BigDecimal?, totalCost: BigDecimal) {
        val options = PackageOptions(insuredValue = insuredValue)
        val cost = calculator.calculateShippingCost(
            ShippingZone("EU", "standard"), 3.0, listOf(30, 20, 15), options, Carrier.DHL
        )
        assertEquals(0, totalCost.compareTo(cost))
    }

    @Description("""
        All rows use EU standard shipping at 3.0 kg with non-oversize dimensions
        (base cost 7.50) and are fragile, confirming the fragile multiplier is
        applied to the base cost only, before the insurance premium is added.
        """)
    @TableTest("""
        Scenario                                       | Insured Value | Total Cost?
        Fragile with insurance at minimum premium      | 200            | 11.625
        Fragile with insurance above minimum premium   | 1000           | 14.625
        """)
    fun combinesFragileAndInsuranceSurcharges(insuredValue: BigDecimal, totalCost: BigDecimal) {
        val options = PackageOptions(isFragile = true, insuredValue = insuredValue)
        val cost = calculator.calculateShippingCost(
            ShippingZone("EU", "standard"), 3.0, listOf(30, 20, 15), options, Carrier.DHL
        )
        assertEquals(0, totalCost.compareTo(cost))
    }

    @Description("Carrier does not influence shipping cost in the current implementation.")
    @TableTest("""
        Scenario     | Carrier             | Total Cost?
        Any carrier  | {DHL, UPS, FEDEX}   | 12.00
        """)
    fun carrierDoesNotAffectShippingCost(carrier: Carrier, totalCost: BigDecimal) {
        val cost = calculator.calculateShippingCost(
            ShippingZone("EU", "express"), 3.0, listOf(30, 20, 15), PackageOptions(), carrier
        )
        assertEquals(0, totalCost.compareTo(cost))
    }
}
