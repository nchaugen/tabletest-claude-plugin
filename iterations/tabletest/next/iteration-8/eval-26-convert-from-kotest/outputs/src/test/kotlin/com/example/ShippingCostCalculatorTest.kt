package com.example

import org.junit.jupiter.api.Assertions.assertEquals
import org.tabletest.junit.Description
import org.tabletest.junit.TableTest
import org.tabletest.junit.TypeConverter
import java.math.BigDecimal

@TypeConverter
fun parsePackageOptions(config: Map<String, String>): PackageOptions {
    val options = PackageOptions()
    config["fragile"]?.let { options.isFragile = it.toBoolean() }
    config["insuredValue"]?.let { options.insuredValue = BigDecimal(it) }
    config["handling"]?.let { options.handling = it }
    return options
}

class ShippingCostCalculatorTest {

    private val calculator = ShippingCostCalculator()

    @Description("""
        Dimensions are fixed to [10, 10, 10] in every row -- well below the dimensional-weight
        threshold, so effective weight always equals actual weight here. Carrier is fixed to
        DHL; carrier choice does not affect cost (see carrierDoesNotAffectCost).
        """)
    @TableTest("""
        Scenario                     | Region | Speed    | Weight | Base Cost?
        EU standard, light tier      | EU     | standard | 0.5    | 5.00
        EU standard, medium tier     | EU     | standard | 3.0    | 7.50
        EU standard, heavy tier      | EU     | standard | 10.0   | 12.50
        EU standard, very heavy tier | EU     | standard | 25.0   | 20.00
        EU express, light tier       | EU     | express  | 0.5    | 8.00
        EU express, medium tier      | EU     | express  | 3.0    | 12.00
        US standard, light tier      | US     | standard | 0.5    | 7.00
        US express, heavy tier       | US     | express  | 10.0   | 30.00
        """)
    fun determinesBaseRateByRegionSpeedAndWeight(region: String, speed: String, weight: Double, baseCost: BigDecimal) {
        val zone = ShippingZone(region, speed)
        val cost = calculator.calculateShippingCost(zone, weight, listOf(10, 10, 10), PackageOptions(), Carrier.DHL)
        assertEquals(0, baseCost.compareTo(cost))
    }

    @Description("""
        Zone is fixed to EU standard. Effective weight is the greater of actual weight and
        dimensional weight (volume / 5000, rounded to 3 decimal places).
        """)
    @TableTest("""
        Scenario                     | Weight | Dimensions   | Base Cost?
        Actual weight is larger      | 25.0   | [50, 40, 30] | 20.00
        Dimensional weight is larger | 1.0    | [70, 50, 10] | 12.50
        """)
    fun selectsEffectiveWeightFromActualOrDimensional(weight: Double, dimensions: List<Int>, baseCost: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val cost = calculator.calculateShippingCost(zone, weight, dimensions, PackageOptions(), Carrier.DHL)
        assertEquals(0, baseCost.compareTo(cost))
    }

    @Description("""
        Zone is fixed to EU standard, weight to 3.0kg (base rate 7.50 with dimensions
        [30, 20, 15]). Surcharges apply in order: oversize fee, then hazmat fee, then the
        fragile multiplier, then the insurance premium -- added last, so it is unaffected by
        the fragile multiplier.
        """)
    @TableTest("""
        Scenario             | Dimensions   | Options                            | Total Cost?
        No surcharges        | [30, 20, 15] | [:]                                | 7.50
        Oversize dimension   | [120, 5, 5]  | [:]                                | 17.50
        Fragile              | [30, 20, 15] | [fragile: true]                    | 8.625
        Insured              | [30, 20, 15] | [insuredValue: 500]                | 10.50
        Fragile and insured  | [30, 20, 15] | [fragile: true, insuredValue: 200] | 11.625
        Hazmat handling      | [30, 20, 15] | [handling: hazmat]                 | 15.50
        """)
    fun appliesSurchargesToBaseCost(dimensions: List<Int>, options: PackageOptions, totalCost: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val cost = calculator.calculateShippingCost(zone, 3.0, dimensions, options, Carrier.DHL)
        assertEquals(0, totalCost.compareTo(cost))
    }

    @Description("""
        Fixed for all rows: zone = EU express, weight = 3.0kg, dimensions = [30, 20, 15]
        (base rate 12.00, no other surcharges).
        """)
    @TableTest("""
        Scenario                            | Carrier           | Total Cost?
        Carrier choice does not affect cost | {DHL, UPS, FEDEX} | 12.00
        """)
    fun carrierDoesNotAffectCost(carrier: Carrier, totalCost: BigDecimal) {
        val zone = ShippingZone("EU", "express")
        val cost = calculator.calculateShippingCost(zone, 3.0, listOf(30, 20, 15), PackageOptions(), carrier)
        assertEquals(0, totalCost.compareTo(cost))
    }
}
