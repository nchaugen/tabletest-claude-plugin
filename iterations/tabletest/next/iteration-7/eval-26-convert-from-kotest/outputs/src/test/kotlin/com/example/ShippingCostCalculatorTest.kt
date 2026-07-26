package com.example

import org.junit.jupiter.api.Assertions.assertEquals
import org.tabletest.junit.Description
import org.tabletest.junit.TableTest
import org.tabletest.junit.TypeConverter
import java.math.BigDecimal

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

    @Description("""
        Isolates the zone/weight -> rate lookup. Dimensions are fixed small enough that
        dimensional weight never overrides the tested weights, and package options are left
        at their defaults, so only the rate table itself is exercised.
        """)
    @TableTest("""
        Scenario                | Region | Speed    | Weight | Rate?
        EU standard light       | EU     | standard | 0.5    | 5.00
        EU standard medium      | EU     | standard | 3.0    | 7.50
        EU standard heavy       | EU     | standard | 10.0   | 12.50
        EU standard very heavy  | EU     | standard | 25.0   | 20.00
        EU express light        | EU     | express  | 0.5    | 8.00
        EU express medium       | EU     | express  | 3.0    | 12.00
        EU express heavy        | EU     | express  | 10.0   | 20.00
        EU express very heavy   | EU     | express  | 25.0   | 32.00
        US standard light       | US     | standard | 0.5    | 7.00
        US standard medium      | US     | standard | 3.0    | 10.50
        US standard heavy       | US     | standard | 10.0   | 17.50
        US standard very heavy  | US     | standard | 25.0   | 28.00
        US express light        | US     | express  | 0.5    | 11.00
        US express medium       | US     | express  | 3.0    | 16.50
        US express heavy        | US     | express  | 10.0   | 30.00
        US express very heavy   | US     | express  | 25.0   | 45.00
        """)
    fun resolvesBaseRateByZoneAndWeight(region: String, speed: String, weight: Double, rate: BigDecimal) {
        val cost = calculator.calculateShippingCost(
            ShippingZone(region, speed), weight, listOf(10, 10, 10), PackageOptions(), Carrier.DHL
        )
        assertEquals(0, cost.compareTo(rate))
    }

    @Description("""
        Effective weight is the greater of actual weight and dimensional weight
        (volume / 5000). Fixed to the EU standard zone with default package options
        so only the weight-selection rule is exercised.
        """)
    @TableTest("""
        Scenario                   | Weight | Dimensions   | Rate?
        Actual weight governs      | 3.0    | [30, 20, 15] | 7.50
        Dimensional weight governs | 1.0    | [70, 50, 10] | 12.50
        """)
    fun selectsEffectiveWeight(weight: Double, dimensions: List<Int>, rate: BigDecimal) {
        val cost = calculator.calculateShippingCost(
            ShippingZone("EU", "standard"), weight, dimensions, PackageOptions(), Carrier.DHL
        )
        assertEquals(0, cost.compareTo(rate))
    }

    @Description("""
        A flat 10.00 fee applies when any single dimension exceeds 100. Fixed to the EU
        standard zone at a weight where dimensional weight never overrides actual weight,
        so the base rate stays 7.50 throughout.
        """)
    @TableTest("""
        Scenario               | Dimensions  | Total Cost?
        Within size limit      | [100, 5, 5] | 7.50
        Just over size limit   | [101, 5, 5] | 17.50
        Large oversize package | [120, 5, 5] | 17.50
        """)
    fun appliesOversizeSurcharge(dimensions: List<Int>, totalCost: BigDecimal) {
        val cost = calculator.calculateShippingCost(
            ShippingZone("EU", "standard"), 3.0, dimensions, PackageOptions(), Carrier.DHL
        )
        assertEquals(0, cost.compareTo(totalCost))
    }

    @Description("""
        Fixed to the EU standard zone at 3.0kg with dimensions that keep dimensional weight
        below actual weight, so the base rate is always 7.50 and only the package-option
        surcharges vary. Fragile applies a 1.15x multiplier to the base rate; insurance adds
        max(insuredValue * 0.006, 3.00) on top of that; hazmat handling adds a flat 8.00.
        Only the literal string "hazmat" triggers the handling fee.
        """)
    @TableTest("""
        Scenario                      | Options                            | Total Cost?
        No options                    | [:]                                | 7.50
        Hazmat handling                | [handling: hazmat]                 | 15.50
        Non-hazmat handling ignored    | [handling: signature-required]     | 7.50
        Fragile surcharge              | [fragile: true]                    | 8.625
        Insured below minimum premium  | [insuredValue: 100]                | 10.50
        Insured at minimum premium     | [insuredValue: 500]                | 10.50
        Insured above minimum premium  | [insuredValue: 2000]               | 19.50
        Fragile and insured            | [fragile: true, insuredValue: 200] | 11.625
        """)
    fun appliesPackageOptionSurcharges(options: PackageOptions, totalCost: BigDecimal) {
        val cost = calculator.calculateShippingCost(
            ShippingZone("EU", "standard"), 3.0, listOf(30, 20, 15), options, Carrier.DHL
        )
        assertEquals(0, cost.compareTo(totalCost))
    }

    @Description("Fixed to the EU express zone at 3.0kg; carrier choice does not affect price.")
    @TableTest("""
        Scenario                          | Carrier           | Total Cost?
        Rate is the same across carriers  | {DHL, UPS, FEDEX} | 12.00
        """)
    fun rateIsIndependentOfCarrier(carrier: Carrier, totalCost: BigDecimal) {
        val cost = calculator.calculateShippingCost(
            ShippingZone("EU", "express"), 3.0, listOf(30, 20, 15), PackageOptions(), carrier
        )
        assertEquals(0, cost.compareTo(totalCost))
    }
}
