package com.example

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.tabletest.junit.Description
import org.tabletest.junit.TableTest
import org.tabletest.junit.TypeConverter
import java.math.BigDecimal

private val EU_STANDARD = ShippingZone("EU", "standard")
private val EU_EXPRESS = ShippingZone("EU", "express")
private val NEGLIGIBLE_DIMENSIONS = listOf(1, 1, 1)

@TypeConverter
fun parseShippingZone(value: String): ShippingZone {
    val (region, speed) = value.split(" ", limit = 2)
    return ShippingZone(region, speed)
}

@TypeConverter
fun parsePackageOptions(fields: Map<String, String>): PackageOptions {
    val options = PackageOptions()
    fields["fragile"]?.let { options.isFragile = it.toBoolean() }
    fields["insuredValue"]?.let { options.insuredValue = BigDecimal(it) }
    fields["handling"]?.let { options.handling = it }
    return options
}

class ShippingCostCalculatorTest {

    private val calculator = ShippingCostCalculator()

    @DisplayName("Resolves the base shipping rate from zone and weight")
    @Description("""
        Dimensions are held at a negligible [1, 1, 1] so effective weight equals
        actual weight for every row; the choice between actual and dimensional
        weight is covered separately below. Carrier does not affect cost
        (covered separately) and is held at DHL. No fragile, insurance, or
        hazmat options are applied.
        """)
    @TableTest("""
        Scenario                | Zone        | Weight (kg) | Base Rate?
        EU standard, light      | EU standard | 0.5         | 5.00
        EU standard, medium     | EU standard | 3.0         | 7.50
        EU standard, heavy      | EU standard | 10.0        | 12.50
        EU standard, very heavy | EU standard | 25.0        | 20.00
        EU express, light       | EU express  | 0.5         | 8.00
        EU express, medium      | EU express  | 3.0         | 12.00
        US standard, light      | US standard | 0.5         | 7.00
        US express, heavy       | US express  | 10.0        | 30.00
        """)
    fun resolvesBaseRateFromZoneAndWeight(zone: ShippingZone, weightKg: Double, baseRate: BigDecimal) {
        val cost = calculator.calculateShippingCost(zone, weightKg, NEGLIGIBLE_DIMENSIONS, PackageOptions(), Carrier.DHL)
        assertCostEquals(baseRate, cost)
    }

    @DisplayName("Sizes the shipment by the larger of actual and dimensional weight")
    @Description("""
        Zone is held at EU standard, so the weight-band lookup itself is
        covered by the base-rate table above; only the choice between actual
        and dimensional weight varies here. Carrier is held at DHL — carrier
        does not affect cost (covered separately).
        """)
    @TableTest("""
        Scenario                     | Actual Weight (kg) | Dimensions (cm) | Base Rate?
        Actual weight dominates      | 3.0                 | [30, 20, 15]    | 7.50
        Dimensional weight dominates | 1.0                 | [70, 50, 10]    | 12.50
        """)
    fun sizesShipmentByLargerOfActualAndDimensionalWeight(
        actualWeightKg: Double,
        dimensionsCm: List<Int>,
        baseRate: BigDecimal
    ) {
        val cost = calculator.calculateShippingCost(EU_STANDARD, actualWeightKg, dimensionsCm, PackageOptions(), Carrier.DHL)
        assertCostEquals(baseRate, cost)
    }

    @DisplayName("Adds package surcharges to the base shipping cost")
    @Description("""
        Zone is EU standard at 3.0kg, an unsurcharged base rate of 7.50
        (covered by the base-rate table above). Each row shows how the
        package's dimensions and options modify that base. Carrier is held
        at DHL — carrier does not affect cost (covered separately).
        """)
    @TableTest("""
        Scenario             | Dimensions (cm) | Options                             | Cost?
        No surcharges        | [30, 20, 15]    | [:]                                 | 7.50
        Oversize dimensions  | [120, 5, 5]     | [:]                                 | 17.50
        Fragile packaging    | [30, 20, 15]    | [fragile: true]                     | 8.625
        Hazmat handling      | [30, 20, 15]    | [handling: hazmat]                  | 15.50
        Insured shipment     | [30, 20, 15]    | [insuredValue: 500]                 | 10.50
        Fragile and insured  | [30, 20, 15]    | [fragile: true, insuredValue: 200]  | 11.625
        """)
    fun addsPackageSurchargesToBaseCost(dimensionsCm: List<Int>, options: PackageOptions, cost: BigDecimal) {
        val actual = calculator.calculateShippingCost(EU_STANDARD, 3.0, dimensionsCm, options, Carrier.DHL)
        assertCostEquals(cost, actual)
    }

    @DisplayName("Charges the same rate regardless of carrier")
    @Description("""
        Zone is EU express at 3.0kg (base rate 12.00, covered by the
        base-rate table above) with no package options; only carrier varies.
        """)
    @TableTest("""
        Scenario                | Carrier           | Cost?
        Any carrier, same zone  | {DHL, UPS, FEDEX}  | 12.00
        """)
    fun chargesSameRateRegardlessOfCarrier(carrier: Carrier, cost: BigDecimal) {
        val actual = calculator.calculateShippingCost(EU_EXPRESS, 3.0, listOf(30, 20, 15), PackageOptions(), carrier)
        assertCostEquals(cost, actual)
    }

    private fun assertCostEquals(expected: BigDecimal, actual: BigDecimal) {
        assertEquals(0, expected.compareTo(actual))
    }
}
