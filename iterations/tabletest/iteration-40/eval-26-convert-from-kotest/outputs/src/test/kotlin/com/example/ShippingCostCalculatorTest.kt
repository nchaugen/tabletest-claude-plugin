package com.example

import org.junit.jupiter.api.Assertions.assertEquals
import org.tabletest.junit.Description
import org.tabletest.junit.TableTest
import java.math.BigDecimal

class ShippingCostCalculatorTest {

    private val calculator = ShippingCostCalculator()

    @Description("""
        Dimensions are fixed at [1, 1, 1] cm, which is negligible for dimensional weight,
        and carrier is fixed at DHL. No package options are applied. Dimensional weight
        override is covered separately in choosesGreaterOfActualAndDimensionalWeight;
        carrier's lack of effect on price is covered in carrierDoesNotAffectCost.
        """)
    @TableTest("""
        Scenario                        | Region | Speed    | Weight | Base Rate?
        EU standard, light parcel       | EU     | standard | 0.5    | 5.00
        EU standard, medium parcel      | EU     | standard | 3.0    | 7.50
        EU standard, heavy parcel       | EU     | standard | 10.0   | 12.50
        EU standard, very heavy parcel  | EU     | standard | 25.0   | 20.00
        EU express, light parcel        | EU     | express  | 0.5    | 8.00
        EU express, medium parcel       | EU     | express  | 3.0    | 12.00
        EU express, heavy parcel        | EU     | express  | 10.0   | 20.00
        EU express, very heavy parcel   | EU     | express  | 25.0   | 32.00
        US standard, light parcel       | US     | standard | 0.5    | 7.00
        US standard, medium parcel      | US     | standard | 3.0    | 10.50
        US standard, heavy parcel       | US     | standard | 10.0   | 17.50
        US standard, very heavy parcel  | US     | standard | 25.0   | 28.00
        US express, light parcel        | US     | express  | 0.5    | 11.00
        US express, medium parcel       | US     | express  | 3.0    | 16.50
        US express, heavy parcel        | US     | express  | 10.0   | 30.00
        US express, very heavy parcel   | US     | express  | 25.0   | 45.00
        """)
    fun calculatesBaseRateByRegionSpeedAndWeight(region: String, speed: String, weight: Double, baseRate: BigDecimal) {
        val cost = calculator.calculateShippingCost(
            ShippingZone(region, speed), weight, listOf(1, 1, 1), PackageOptions(), Carrier.DHL
        )
        assertEquals(0, cost.compareTo(baseRate))
    }

    @Description("""
        Region and speed fixed to EU standard; the rate-by-weight lookup itself is covered
        in calculatesBaseRateByRegionSpeedAndWeight. Effective weight is whichever is
        greater of actual weight and dimensional weight (length x width x height / 5000).
        """)
    @TableTest("""
        Scenario                                 | Weight | Dimensions   | Base Rate?
        Actual weight exceeds dimensional weight  | 25.0   | [10, 10, 10] | 20.00
        Dimensional weight exceeds actual weight  | 1.0    | [70, 50, 10] | 12.50
        """)
    fun choosesGreaterOfActualAndDimensionalWeight(weight: Double, dimensions: List<Int>, baseRate: BigDecimal) {
        val cost = calculator.calculateShippingCost(
            ShippingZone("EU", "standard"), weight, dimensions, PackageOptions(), Carrier.DHL
        )
        assertEquals(0, cost.compareTo(baseRate))
    }

    @Description("""
        Region/speed/weight fixed to EU standard at 3kg (base rate 7.50, see
        calculatesBaseRateByRegionSpeedAndWeight) except where dimensions trigger the
        oversize fee. Surcharges apply in order: +10.00 oversize fee (any dimension over
        100cm) -> +8.00 hazmat fee -> x1.15 fragile multiplier -> + insurance premium
        (0.6% of insured value, 3.00 minimum). Insured values of 200 and 500 both floor to
        the 3.00 minimum; 1000 exceeds it.
        """)
    @TableTest("""
        Scenario                                       | Dimensions   | Fragile | Insured Value | Handling | Total Cost?
        No surcharges apply                            | [30, 20, 15] | false   |               |          | 7.50
        One dimension exceeds 100cm                    | [120, 5, 5]  | false   |               |          | 17.50
        Hazmat handling requested                      | [30, 20, 15] | false   |               | hazmat   | 15.50
        Fragile handling requested                     | [30, 20, 15] | true    |               |          | 8.625
        Insured value below the minimum premium        | [30, 20, 15] | false   | 200           |          | 10.50
        Insured value at the minimum premium boundary  | [30, 20, 15] | false   | 500           |          | 10.50
        Insured value above the minimum premium        | [30, 20, 15] | false   | 1000          |          | 13.50
        Fragile and insured together                   | [30, 20, 15] | true    | 200           |          | 11.625
        """)
    fun appliesSurchargesToBaseCost(
        dimensions: List<Int>, fragile: Boolean, insuredValue: BigDecimal?, handling: String?, totalCost: BigDecimal
    ) {
        val opts = PackageOptions(isFragile = fragile, insuredValue = insuredValue, handling = handling)
        val cost = calculator.calculateShippingCost(ShippingZone("EU", "standard"), 3.0, dimensions, opts, Carrier.DHL)
        assertEquals(0, cost.compareTo(totalCost))
    }

    @TableTest("""
        Scenario                              | Carrier           | Total Cost?
        Carrier choice has no effect on price | {DHL, UPS, FEDEX} | 12.00
        """)
    fun carrierDoesNotAffectCost(carrier: Carrier, totalCost: BigDecimal) {
        val cost = calculator.calculateShippingCost(
            ShippingZone("EU", "express"), 3.0, listOf(30, 20, 15), PackageOptions(), carrier
        )
        assertEquals(0, cost.compareTo(totalCost))
    }
}
