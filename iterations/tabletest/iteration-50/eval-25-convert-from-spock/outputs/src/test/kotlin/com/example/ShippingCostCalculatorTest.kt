package com.example

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.tabletest.junit.Description
import org.tabletest.junit.TableTest
import org.tabletest.junit.TypeConverter
import java.math.BigDecimal

class ShippingCostCalculatorTest {

    private val calculator = ShippingCostCalculator()

    @DisplayName("Determines the base rate from region, speed, and weight")
    @Description("""
        Dimensions are fixed at 10x10x10 cm (dimensional weight 0.2 kg), well below every actual
        weight tested here, so the actual weight always determines the tier -- dimensional override
        and the oversize fee are covered in their own tables below. No fragile, insurance, or hazmat
        options apply, and the carrier does not affect the result (see below).
        """)
    @TableTest("""
        Scenario                | Zone        | Weight | Cost?
        EU standard, up to 1kg  | EU standard | 0.5    | 5.00
        EU standard, up to 5kg  | EU standard | 3.0    | 7.50
        EU standard, up to 15kg | EU standard | 10.0   | 12.50
        EU standard, over 15kg  | EU standard | 25.0   | 20.00
        EU express, up to 1kg   | EU express  | 0.5    | 8.00
        EU express, up to 5kg   | EU express  | 3.0    | 12.00
        US standard, up to 1kg  | US standard | 0.5    | 7.00
        US express, up to 15kg  | US express  | 10.0   | 30.00
        """)
    fun determinesBaseRateFromRegionSpeedAndWeight(zone: ShippingZone, weight: Double, cost: BigDecimal) {
        val actual = calculator.calculateShippingCost(zone, weight, listOf(10, 10, 10), PackageOptions(), Carrier.DHL)
        assertCost(cost, actual)
    }

    @DisplayName("Uses the greater of actual and dimensional weight")
    @Description("""
        Zone is fixed at EU standard; only the actual weight and the dimensions vary, isolating how
        the effective weight is the greater of the actual weight and the dimensional weight.
        """)
    @TableTest("""
        Scenario                     | Weight | Dimensions   | Cost?
        Actual weight dominates      | 1.0    | [20, 15, 10] | 5.00
        Dimensional weight dominates | 1.0    | [70, 50, 10] | 12.50
        """)
    fun usesGreaterOfActualAndDimensionalWeight(weight: Double, dimensions: List<Int>, cost: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val actual = calculator.calculateShippingCost(zone, weight, dimensions, PackageOptions(), Carrier.DHL)
        assertCost(cost, actual)
    }

    @DisplayName("Adds an oversize fee when a dimension exceeds 100 cm")
    @Description("""
        Zone is fixed at EU standard and weight at 3 kg -- comfortably above the dimensional weight
        of every row here, so dimensional override never applies -- and only the longest side of the
        package varies.
        """)
    @TableTest("""
        Scenario                               | Dimensions    | Cost?
        Longest side at the oversize threshold | [100, 10, 10] | 7.50
        Longest side just over the threshold   | [101, 10, 10] | 17.50
        """)
    fun addsOversizeFeeOverThreshold(dimensions: List<Int>, cost: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val actual = calculator.calculateShippingCost(zone, 3.0, dimensions, PackageOptions(), Carrier.DHL)
        assertCost(cost, actual)
    }

    @DisplayName("Adds surcharges for fragile, insured, and hazmat packages")
    @Description("""
        Zone is fixed at EU standard with a 3 kg, 30x20x15 cm package (not oversize, no dimensional
        override) -- only the package options vary.
        """)
    @TableTest("""
        Scenario                     | Adjustments                        | Cost?
        No adjustments               | [:]                                | 7.50
        Fragile packaging            | [fragile: true]                    | 8.625
        Insured for 500              | [insuredValue: 500]                | 10.50
        Fragile and insured for 200  | [fragile: true, insuredValue: 200] | 11.625
        Hazmat handling              | [handling: hazmat]                 | 15.50
        """)
    fun addsSurchargesForOptions(options: PackageOptions, cost: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val actual = calculator.calculateShippingCost(zone, 3.0, listOf(30, 20, 15), options, Carrier.DHL)
        assertCost(cost, actual)
    }

    @DisplayName("Ignores the carrier when calculating cost")
    @Description("""
        Zone is fixed at EU express with a 3 kg, 30x20x15 cm package.
        """)
    @TableTest("""
        Scenario                       | Carrier           | Cost?
        Cost is independent of carrier | {DHL, UPS, FEDEX} | 12.00
        """)
    fun ignoresCarrier(carrier: Carrier, cost: BigDecimal) {
        val zone = ShippingZone("EU", "express")
        val actual = calculator.calculateShippingCost(zone, 3.0, listOf(30, 20, 15), PackageOptions(), carrier)
        assertCost(cost, actual)
    }

    private fun assertCost(expected: BigDecimal, actual: BigDecimal) {
        assertEquals(0, expected.compareTo(actual)) { "expected $expected but was $actual" }
    }
}

@TypeConverter
fun parseShippingZone(zone: String): ShippingZone {
    val (region, speed) = zone.split(" ")
    return ShippingZone(region, speed)
}

@TypeConverter
fun parsePackageOptions(adjustments: Map<String, String>): PackageOptions {
    val options = PackageOptions()
    if (adjustments["fragile"] == "true") options.isFragile = true
    adjustments["insuredValue"]?.let { options.insuredValue = BigDecimal(it) }
    adjustments["handling"]?.let { options.handling = it }
    return options
}
