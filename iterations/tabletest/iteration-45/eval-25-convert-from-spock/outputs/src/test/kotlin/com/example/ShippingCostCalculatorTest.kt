package com.example

import org.junit.jupiter.api.Assertions.assertEquals
import org.tabletest.junit.Description
import org.tabletest.junit.TableTest
import java.math.BigDecimal

class ShippingCostCalculatorTest {

    private val calculator = ShippingCostCalculator()

    @Description("""
        Dimensions are held small (20 x 15 x 10) so dimensional weight never exceeds
        actual weight, and carrier is fixed to DHL — neither affects the base rate
        (see determinesEffectiveWeightFromDimensions and carrierDoesNotAffectCost).
        """)
    @TableTest("""
        Scenario                        | Region | Speed    | Weight | Base Rate?
        EU standard, light weight       | EU     | standard | 0.5    | 5.00
        EU standard, medium weight      | EU     | standard | 3.0    | 7.50
        EU standard, heavy weight       | EU     | standard | 10.0   | 12.50
        EU standard, very heavy weight  | EU     | standard | 25.0   | 20.00
        EU express, light weight        | EU     | express  | 0.5    | 8.00
        EU express, medium weight       | EU     | express  | 3.0    | 12.00
        US standard, light weight       | US     | standard | 0.5    | 7.00
        US express, heavy weight        | US     | express  | 10.0   | 30.00
        """)
    fun looksUpBaseRateByRegionSpeedAndWeight(region: String, speed: String, weight: Double, baseRate: BigDecimal) {
        val zone = ShippingZone(region, speed)
        val cost = calculator.calculateShippingCost(zone, weight, listOf(20, 15, 10), PackageOptions(), Carrier.DHL)
        assertEquals(0, baseRate.compareTo(cost))
    }

    @Description("""
        Region is EU, speed is standard, carrier is DHL, for all rows.
        """)
    @TableTest("""
        Scenario                                  | Weight | Length | Width | Height | Base Rate?
        Actual weight exceeds dimensional weight  | 3.0    | 30     | 20    | 15     | 7.50
        Dimensional weight exceeds actual weight  | 1.0    | 70     | 50    | 10     | 12.50
        """)
    fun determinesEffectiveWeightFromDimensions(weight: Double, length: Int, width: Int, height: Int, baseRate: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val cost = calculator.calculateShippingCost(zone, weight, listOf(length, width, height), PackageOptions(), Carrier.DHL)
        assertEquals(0, baseRate.compareTo(cost))
    }

    @Description("""
        Region is EU, speed is standard, weight is 3.0, carrier is DHL, for all rows.
        Oversize threshold is 100 (any dimension unit).
        """)
    @TableTest("""
        Scenario                                   | Length | Width | Height | Total Cost?
        Within the oversize threshold              | 30     | 20    | 15     | 7.50
        One dimension past the oversize threshold  | 120    | 5     | 5      | 17.50
        """)
    fun appliesOversizeFeePastThreshold(length: Int, width: Int, height: Int, totalCost: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val cost = calculator.calculateShippingCost(zone, 3.0, listOf(length, width, height), PackageOptions(), Carrier.DHL)
        assertEquals(0, totalCost.compareTo(cost))
    }

    @Description("""
        Region is EU, speed is standard, weight is 3.0, dimensions are 30 x 20 x 15, carrier is DHL, for all rows.
        """)
    @TableTest("""
        Scenario          | Fragile | Total Cost?
        Standard package  | false   | 7.50
        Fragile package   | true    | 8.625
        """)
    fun appliesFragileSurcharge(fragile: Boolean, totalCost: BigDecimal) {
        val options = PackageOptions(isFragile = fragile)
        val zone = ShippingZone("EU", "standard")
        val cost = calculator.calculateShippingCost(zone, 3.0, listOf(30, 20, 15), options, Carrier.DHL)
        assertEquals(0, totalCost.compareTo(cost))
    }

    @Description("""
        Region is EU, speed is standard, weight is 3.0, dimensions are 30 x 20 x 15, carrier is DHL, for all rows.
        Insurance premium is a percentage of insured value, floored at a fixed minimum.
        """)
    @TableTest("""
        Scenario                              | Insured Value | Total Cost?
        Computed premium at or below minimum  | {200, 500}    | 10.50
        Computed premium above minimum        | 1000          | 13.50
        """)
    fun appliesInsurancePremiumWithMinimum(insuredValue: BigDecimal, totalCost: BigDecimal) {
        val options = PackageOptions(insuredValue = insuredValue)
        val zone = ShippingZone("EU", "standard")
        val cost = calculator.calculateShippingCost(zone, 3.0, listOf(30, 20, 15), options, Carrier.DHL)
        assertEquals(0, totalCost.compareTo(cost))
    }

    @Description("""
        Region is EU, speed is standard, weight is 3.0, dimensions are 30 x 20 x 15, carrier is DHL, for all rows.
        """)
    @TableTest("""
        Scenario             | Handling | Total Cost?
        No special handling  |          | 7.50
        Hazmat handling      | hazmat   | 15.50
        """)
    fun appliesHazmatHandlingFee(handling: String?, totalCost: BigDecimal) {
        val options = PackageOptions(handling = handling)
        val zone = ShippingZone("EU", "standard")
        val cost = calculator.calculateShippingCost(zone, 3.0, listOf(30, 20, 15), options, Carrier.DHL)
        assertEquals(0, totalCost.compareTo(cost))
    }

    @Description("""
        Region is EU, speed is standard, weight is 3.0, dimensions are 30 x 20 x 15, carrier is DHL.
        Shows that the fragile multiplier applies to the base rate before the insurance
        premium is added: insurance is computed on, and added to, the already-multiplied
        base rather than being multiplied itself.
        """)
    @TableTest("""
        Scenario                     | Fragile | Insured Value | Total Cost?
        Fragile package, insured    | true    | 200           | 11.625
        """)
    fun appliesFragileMultiplierBeforeInsurancePremium(fragile: Boolean, insuredValue: BigDecimal, totalCost: BigDecimal) {
        val options = PackageOptions(isFragile = fragile, insuredValue = insuredValue)
        val zone = ShippingZone("EU", "standard")
        val cost = calculator.calculateShippingCost(zone, 3.0, listOf(30, 20, 15), options, Carrier.DHL)
        assertEquals(0, totalCost.compareTo(cost))
    }

    @Description("""
        Region is EU, speed is express, weight is 3.0, dimensions are 30 x 20 x 15.
        """)
    @TableTest("""
        Scenario                        | Carrier           | Total Cost?
        Cost is independent of carrier  | {DHL, UPS, FEDEX} | 12.00
        """)
    fun carrierDoesNotAffectCost(carrier: Carrier, totalCost: BigDecimal) {
        val zone = ShippingZone("EU", "express")
        val cost = calculator.calculateShippingCost(zone, 3.0, listOf(30, 20, 15), PackageOptions(), carrier)
        assertEquals(0, totalCost.compareTo(cost))
    }
}
