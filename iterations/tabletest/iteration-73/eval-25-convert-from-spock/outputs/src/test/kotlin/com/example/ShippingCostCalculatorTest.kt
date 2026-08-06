package com.example

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.tabletest.junit.Description
import org.tabletest.junit.TableTest
import org.tabletest.junit.TypeConverter
import java.math.BigDecimal

class ShippingCostCalculatorTest {

    private val calculator = ShippingCostCalculator()

    @DisplayName("Determines the base shipping rate from region, speed, and weight")
    @Description("""
        Dimensions are held at 10x10x10 cm, giving a dimensional weight of 0.2 kg that never
        exceeds the actual weight tested, so the ladder reflects actual weight alone
        (effective-weight selection has its own table below). No fragile, insurance, or
        handling options are applied, and the carrier is fixed at DHL - cost does not depend
        on carrier (see the carrier table below).
        """)
    @TableTest("""
        Scenario                     | Region | Speed    | Weight (kg) | Base Rate?
        EU standard, at tier 1 limit | EU     | standard | 1           | 5.00
        EU standard, tier 2 begins   | EU     | standard | 1.01        | 7.50
        EU standard, at tier 2 limit | EU     | standard | 5           | 7.50
        EU standard, tier 3 begins   | EU     | standard | 5.01        | 12.50
        EU standard, at tier 3 limit | EU     | standard | 15          | 12.50
        EU standard, tier 4 begins   | EU     | standard | 15.01       | 20.00
        EU express, tier 1           | EU     | express  | 0.5         | 8.00
        EU express, tier 2           | EU     | express  | 3.0         | 12.00
        EU express, tier 3           | EU     | express  | 10.0        | 20.00
        EU express, tier 4           | EU     | express  | 25.0        | 32.00
        US standard, tier 1          | US     | standard | 0.5         | 7.00
        US standard, tier 2          | US     | standard | 3.0         | 10.50
        US standard, tier 3          | US     | standard | 10.0        | 17.50
        US standard, tier 4          | US     | standard | 25.0        | 28.00
        US express, tier 1           | US     | express  | 0.5         | 11.00
        US express, tier 2           | US     | express  | 3.0         | 16.50
        US express, tier 3           | US     | express  | 10.0        | 30.00
        US express, tier 4           | US     | express  | 25.0        | 45.00
        """)
    fun determinesBaseRateFromRegionSpeedAndWeight(region: String, speed: String, weight: Double, baseRate: BigDecimal) {
        val zone = ShippingZone(region, speed)
        val result = calculator.calculateShippingCost(zone, weight, listOf(10, 10, 10), PackageOptions(), Carrier.DHL)
        assertCost(baseRate, result)
    }

    @DisplayName("Selects the greater of actual and dimensional weight as the effective weight")
    @Description("""
        Zone is fixed at EU standard - region/speed pricing has its own table above. No
        fragile, insurance, or handling options are applied.
        """)
    @TableTest("""
        Scenario                                 | Weight (kg) | Dimensions (cm) | Base Rate?
        Actual weight exceeds dimensional weight | 3.0         | [10, 10, 10]    | 7.50
        Dimensional weight exceeds actual weight | 1.0         | [70, 50, 10]    | 12.50
        """)
    fun selectsTheGreaterOfActualAndDimensionalWeight(weight: Double, dimensions: List<Int>, baseRate: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val result = calculator.calculateShippingCost(zone, weight, dimensions, PackageOptions(), Carrier.DHL)
        assertCost(baseRate, result)
    }

    @DisplayName("Applies flat fees for oversize dimensions and hazardous handling")
    @Description("""
        Zone is fixed at EU standard with weight 3.0 kg (base rate 7.50), isolating the flat
        fees from the base-rate ladder above. No fragile or insurance options are applied.
        """)
    @TableTest("""
        Scenario                                    | Dimensions (cm) | Options               | Total Cost?
        Neither surcharge applies                   | [10, 10, 10]    | [:]                   | 7.50
        At the oversize threshold                   | [100, 5, 5]     | [:]                   | 7.50
        Just past the oversize threshold            | [101, 5, 5]     | [:]                   | 17.50
        Non-hazmat handling has no effect           | [10, 10, 10]    | [handling: signature] | 7.50
        Hazmat handling adds its flat fee           | [10, 10, 10]    | [handling: hazmat]    | 15.50
        Oversize and hazmat fees combine additively | [101, 5, 5]     | [handling: hazmat]    | 25.50
        """)
    fun appliesFlatFeesForOversizeAndHazardousHandling(dimensions: List<Int>, options: PackageOptions, totalCost: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val result = calculator.calculateShippingCost(zone, 3.0, dimensions, options, Carrier.DHL)
        assertCost(totalCost, result)
    }

    @DisplayName("Applies a 15% surcharge for fragile packaging")
    @Description("""
        Zone is fixed at EU standard with weight 3.0 kg and dimensions 10x10x10 cm (base rate
        7.50). No insurance or handling options are applied.
        """)
    @TableTest("""
        Scenario                    | Options         | Total Cost?
        No fragile surcharge        | [:]             | 7.50
        Fragile packaging surcharge | [fragile: true] | 8.625
        """)
    fun appliesAFragileSurcharge(options: PackageOptions, totalCost: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val result = calculator.calculateShippingCost(zone, 3.0, listOf(10, 10, 10), options, Carrier.DHL)
        assertCost(totalCost, result)
    }

    @DisplayName("Computes the insurance premium as the greater of a percentage rate or a minimum")
    @Description("""
        Zone is fixed at EU standard with weight 3.0 kg and dimensions 10x10x10 cm (base rate
        7.50). The premium rate is 0.6% of the insured value with a $3.00 minimum; 499 and
        501 straddle the insured value (500) at which the percentage overtakes the minimum.
        No fragile or handling options are applied.
        """)
    @TableTest("""
        Scenario                                      | Options             | Total Cost?
        Premium below the minimum uses the minimum    | [insuredValue: 499] | 10.50
        Premium above the minimum uses the percentage | [insuredValue: 501] | 10.506
        """)
    fun computesInsurancePremium(options: PackageOptions, totalCost: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val result = calculator.calculateShippingCost(zone, 3.0, listOf(10, 10, 10), options, Carrier.DHL)
        assertCost(totalCost, result)
    }

    @DisplayName("Combines the fragile surcharge with other fees in calculation order")
    @Description("""
        Zone is fixed at EU standard with weight 3.0 kg and dimensions 10x10x10 cm (base rate
        7.50). Shows that the fragile surcharge multiplies fees added earlier in the
        calculation (the hazmat fee) but not the insurance premium, which is added last -
        an interaction neither the fragile table nor the insurance table above shows alone.
        """)
    @TableTest("""
        Scenario                                               | Options                            | Total Cost?
        Fragile surcharge applies to the hazmat fee            | [fragile: true, handling: hazmat]  | 17.825
        Insurance premium is added after the fragile surcharge | [fragile: true, insuredValue: 200] | 11.625
        """)
    fun combinesFragileSurchargeWithOtherFees(options: PackageOptions, totalCost: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val result = calculator.calculateShippingCost(zone, 3.0, listOf(10, 10, 10), options, Carrier.DHL)
        assertCost(totalCost, result)
    }

    @DisplayName("Determines shipping cost independent of the carrier")
    @Description("""
        Zone is fixed at EU express with weight 3.0 kg and dimensions 10x10x10 cm (base rate
        12.00). No fragile, insurance, or handling options are applied.
        """)
    @TableTest("""
        Scenario                           | Carrier           | Total Cost?
        Cost is the same for every carrier | {DHL, UPS, FEDEX} | 12.00
        """)
    fun determinesCostIndependentlyOfCarrier(carrier: Carrier, totalCost: BigDecimal) {
        val zone = ShippingZone("EU", "express")
        val result = calculator.calculateShippingCost(zone, 3.0, listOf(10, 10, 10), PackageOptions(), carrier)
        assertCost(totalCost, result)
    }

    private fun assertCost(expected: BigDecimal, actual: BigDecimal) {
        assertEquals(0, expected.compareTo(actual), "expected $expected but was $actual")
    }
}

@TypeConverter
fun parsePackageOptions(fields: Map<String, String>): PackageOptions {
    val options = PackageOptions()
    fields["fragile"]?.let { options.isFragile = it.toBoolean() }
    fields["insuredValue"]?.let { options.insuredValue = BigDecimal(it) }
    fields["handling"]?.let { options.handling = it }
    return options
}
