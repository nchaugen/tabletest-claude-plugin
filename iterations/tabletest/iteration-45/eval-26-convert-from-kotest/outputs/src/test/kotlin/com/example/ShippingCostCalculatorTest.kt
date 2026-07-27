package com.example

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.tabletest.junit.Description
import org.tabletest.junit.TableTest
import org.tabletest.junit.TypeConverter
import java.math.BigDecimal

class ShippingCostCalculatorTest {

    private val calculator = ShippingCostCalculator()
    private val smallDimensions = listOf(5, 5, 5)
    private val euStandard = ShippingZone("EU", "standard")
    private val euExpress = ShippingZone("EU", "express")

    @DisplayName("Looks up the base rate by zone and weight tier")
    @Description("""
        Dimensions are held small enough that dimensional weight never overrides the
        actual weight and never triggers the oversize fee. No package options are
        applied, and carrier is held constant (see "Ignores carrier when calculating
        cost" for that concern).
        """)
    @TableTest("""
        Scenario                | Region | Speed    | Weight (kg)         | Cost?
        EU standard, light      | EU     | standard | {0.1, 0.5, 1.0}     | 5.00
        EU standard, medium     | EU     | standard | {1.01, 3.0, 5.0}    | 7.50
        EU standard, heavy      | EU     | standard | {5.01, 10.0, 15.0}  | 12.50
        EU standard, very heavy | EU     | standard | {15.01, 25.0, 50.0} | 20.00
        EU express, light       | EU     | express  | {0.1, 0.5, 1.0}     | 8.00
        EU express, medium      | EU     | express  | {1.01, 3.0, 5.0}    | 12.00
        EU express, heavy       | EU     | express  | {5.01, 10.0, 15.0}  | 20.00
        EU express, very heavy  | EU     | express  | {15.01, 25.0, 50.0} | 32.00
        US standard, light      | US     | standard | {0.1, 0.5, 1.0}     | 7.00
        US standard, medium     | US     | standard | {1.01, 3.0, 5.0}    | 10.50
        US standard, heavy      | US     | standard | {5.01, 10.0, 15.0}  | 17.50
        US standard, very heavy | US     | standard | {15.01, 25.0, 50.0} | 28.00
        US express, light       | US     | express  | {0.1, 0.5, 1.0}     | 11.00
        US express, medium      | US     | express  | {1.01, 3.0, 5.0}    | 16.50
        US express, heavy       | US     | express  | {5.01, 10.0, 15.0}  | 30.00
        US express, very heavy  | US     | express  | {15.01, 25.0, 50.0} | 45.00
        """)
    fun looksUpBaseRateByZoneAndWeight(region: String, speed: String, weight: Double, cost: BigDecimal) {
        val zone = ShippingZone(region, speed)
        val actual = calculator.calculateShippingCost(zone, weight, smallDimensions, PackageOptions(), Carrier.DHL)
        assertMoneyEquals(cost, actual)
    }

    @DisplayName("Uses the greater of actual and dimensional weight")
    @Description("""
        Zone is held at EU standard (base rates: up to 1kg 5.00, up to 5kg 7.50, up
        to 15kg 12.50). No package options are applied and dimensions stay within
        the oversize limit.
        """)
    @TableTest("""
        Scenario                          | Weight (kg) | Dimensions (cm) | Cost?
        Actual weight exceeds dimensional | 3.0         | [10, 10, 10]    | 7.50
        Dimensional weight exceeds actual | 1.0         | [70, 50, 10]    | 12.50
        Dimensional weight equals actual  | 5.0         | [50, 50, 10]    | 7.50
        """)
    fun usesTheGreaterOfActualAndDimensionalWeight(weight: Double, dimensions: List<Int>, cost: BigDecimal) {
        val actual = calculator.calculateShippingCost(euStandard, weight, dimensions, PackageOptions(), Carrier.DHL)
        assertMoneyEquals(cost, actual)
    }

    @DisplayName("Adds an oversize fee once any dimension passes the limit")
    @Description("""
        Zone is EU standard and weight is held at 3.0kg (base rate 7.50), so the
        oversize fee is the only source of variation. No package options are
        applied.
        """)
    @TableTest("""
        Scenario                      | Dimensions (cm) | Cost?
        At the oversize limit         | [100, 5, 5]     | 7.50
        Just past the oversize limit  | [101, 5, 5]     | 17.50
        """)
    fun addsOversizeFeeForDimensionsPastTheLimit(dimensions: List<Int>, cost: BigDecimal) {
        val actual = calculator.calculateShippingCost(euStandard, 3.0, dimensions, PackageOptions(), Carrier.DHL)
        assertMoneyEquals(cost, actual)
    }

    @DisplayName("Applies package option surcharges to the base rate")
    @Description("""
        Zone is EU standard and weight is held at 3.0kg (base rate 7.50) with
        dimensions small enough to avoid the oversize fee and dimensional-weight
        override. Carrier is held constant.
        """)
    @TableTest("""
        Scenario                          | Options                            | Cost?
        No adjustments                    | [:]                                 | 7.50
        Fragile package                   | [fragile: true]                     | 8.625
        Hazmat handling                   | [handling: hazmat]                  | 15.50
        Fragile and hazmat together       | [fragile: true, handling: hazmat]   | 17.825
        Insured at the minimum premium    | [insuredValue: 500]                 | 10.50
        Insured above the minimum premium | [insuredValue: 600]                 | 11.10
        Fragile and insured together      | [fragile: true, insuredValue: 200]  | 11.625
        """)
    fun appliesPackageOptionSurcharges(options: PackageOptions, cost: BigDecimal) {
        val actual = calculator.calculateShippingCost(euStandard, 3.0, smallDimensions, options, Carrier.DHL)
        assertMoneyEquals(cost, actual)
    }

    @DisplayName("Ignores carrier when calculating cost")
    @Description("""
        Zone is EU express and weight is held at 3.0kg (base rate 12.00). No
        package options are applied and dimensions avoid the oversize fee.
        """)
    @TableTest("""
        Scenario    | Carrier           | Cost?
        Any carrier | {DHL, UPS, FEDEX} | 12.00
        """)
    fun ignoresCarrierWhenCalculatingCost(carrier: Carrier, cost: BigDecimal) {
        val actual = calculator.calculateShippingCost(euExpress, 3.0, smallDimensions, PackageOptions(), carrier)
        assertMoneyEquals(cost, actual)
    }

    private fun assertMoneyEquals(expected: BigDecimal, actual: BigDecimal) {
        assertEquals(0, expected.compareTo(actual))
    }
}

@TypeConverter
fun parsePackageOptions(options: Map<String, String>): PackageOptions {
    return PackageOptions(
        isFragile = options["fragile"]?.toBoolean() ?: false,
        insuredValue = options["insuredValue"]?.let { BigDecimal(it) },
        handling = options["handling"]
    )
}
