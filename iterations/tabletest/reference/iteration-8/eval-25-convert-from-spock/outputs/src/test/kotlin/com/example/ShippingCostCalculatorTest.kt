package com.example

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.tabletest.junit.Description
import org.tabletest.junit.TableTest
import org.tabletest.junit.TypeConverter
import java.math.BigDecimal

class ShippingCostCalculatorTest {

    private val calculator = ShippingCostCalculator()

    @DisplayName("Rate bracket is chosen by region, speed and weight")
    @Description("""
        Every row ships a 10x10x10 cm package, whose volumetric weight of 0.2 kg never exceeds the
        actual weight, so the actual weight alone selects the bracket. No options apply, so the cost
        is the bracket rate with nothing added. The paired rows either side of 1, 5 and 15 kg say
        that a bracket includes its upper bound; the last three hold the weight at 5 kg so the
        region and the speed are the only things that move.
        """)
    @TableTest("""
        Scenario                        | Region | Speed    | Weight (kg) | Shipping cost?
        EU standard, at the 1 kg bound  | EU     | standard | 1           | 5.00
        EU standard, just past 1 kg     | EU     | standard | 1.01        | 7.50
        EU standard, at the 5 kg bound  | EU     | standard | 5           | 7.50
        EU standard, just past 5 kg     | EU     | standard | 5.01        | 12.50
        EU standard, at the 15 kg bound | EU     | standard | 15          | 12.50
        EU standard, just past 15 kg    | EU     | standard | 15.01       | 20.00
        EU express, same 5 kg package   | EU     | express  | 5           | 12.00
        US standard, same 5 kg package  | US     | standard | 5           | 10.50
        US express, same 5 kg package   | US     | express  | 5           | 16.50
        """)
    fun ratesByRegionSpeedAndWeight(
        region: String, speed: String, weight: Double, shippingCost: BigDecimal
    ) {
        assertSameAmount(shippingCost, calculator.calculateShippingCost(
            ShippingZone(region, speed), weight, listOf(10, 10, 10), PackageOptions(), Carrier.DHL))
    }

    @DisplayName("Effective weight is the greater of actual and volumetric")
    @Description("""
        Volumetric weight is length x width x height over the volumetric divisor, and whichever of
        it and the actual weight is greater selects the bracket the sibling table lists. The actual
        weight is held at 1 kg so the package shape is the only thing that moves.
        """)
    @TableTest("""
        Scenario                            | Weight (kg) | Dimensions (cm) | Volumetric divisor (cm3 per kg) | Shipping cost?
        Compact package, actual weight wins | 1.0         | [10, 10, 10]    | 5000                            | 5.00
        Bulky package, volumetric wins      | 1.0         | [70, 50, 10]    | 5000                            | 12.50
        """)
    fun takesTheGreaterOfActualAndVolumetricWeight(
        weight: Double, dimensions: List<Int>, volumetricDivisor: Int, shippingCost: BigDecimal
    ) {
        assertSameAmount(shippingCost, calculator.calculateShippingCost(
            ShippingZone("EU", "standard"), weight, dimensions, PackageOptions(), Carrier.DHL))
    }

    @DisplayName("Surcharges compose onto the base rate in a fixed order")
    @Description("""
        Every row ships a 3 kg package EU standard, whose base rate is 7.50. A dimension over 100 cm
        adds a flat 10.00, hazmat handling adds a flat 8.00, fragile packaging multiplies whatever
        has accumulated by 1.15, and insurance adds 0.6% of the insured value or 3.00, whichever is
        larger. The last two rows place the multiplier: the hazmat fee falls inside it and the
        insurance premium falls outside.
        """)
    @TableTest("""
        Scenario                             | Dimensions (cm) | Options                            | Shipping cost?
        No options, no oversize              | [30, 20, 15]    | [:]                                | 7.50
        Longest side at the oversize limit   | [100, 5, 5]     | [:]                                | 7.50
        Longest side past the oversize limit | [101, 5, 5]     | [:]                                | 17.50
        Hazmat handling                      | [30, 20, 15]    | [handling: hazmat]                 | 15.50
        Fragile packaging                    | [30, 20, 15]    | [fragile: true]                    | 8.625
        Insured under the minimum premium    | [30, 20, 15]    | [insuredValue: 200]                | 10.50
        Insured where the premium meets it   | [30, 20, 15]    | [insuredValue: 500]                | 10.50
        Insured over the minimum premium     | [30, 20, 15]    | [insuredValue: 1000]               | 13.50
        Fragile on top of a hazmat fee       | [30, 20, 15]    | [fragile: true, handling: hazmat]  | 17.825
        Fragile and insured                  | [30, 20, 15]    | [fragile: true, insuredValue: 200] | 11.625
        """)
    fun addsSurchargesToTheBaseRate(
        dimensions: List<Int>, options: PackageOptions, shippingCost: BigDecimal
    ) {
        assertSameAmount(shippingCost, calculator.calculateShippingCost(
            ShippingZone("EU", "standard"), 3.0, dimensions, options, Carrier.DHL))
    }

    @DisplayName("Carrier does not affect the shipping cost")
    @Description("""
        A 3 kg 30x20x15 cm package EU express, base rate 12.00, no options. Every carrier the
        calculator accepts is in the one row, so a cost that read the carrier at all would fail it.
        """)
    @TableTest("""
        Scenario                           | Carrier           | Shipping cost?
        Every carrier the calculator takes | {DHL, UPS, FEDEX} | 12.00
        """)
    fun pricesEveryCarrierAlike(carrier: Carrier, shippingCost: BigDecimal) {
        assertSameAmount(shippingCost, calculator.calculateShippingCost(
            ShippingZone("EU", "express"), 3.0, listOf(30, 20, 15), PackageOptions(), carrier))
    }

    private fun assertSameAmount(expected: BigDecimal, actual: BigDecimal) =
        assertEquals(0, expected.compareTo(actual)) { "expected $expected but was $actual" }
}

@TypeConverter
fun parsePackageOptions(options: Map<String, String>): PackageOptions = PackageOptions(
    isFragile = options["fragile"].toBoolean(),
    insuredValue = options["insuredValue"]?.let { BigDecimal(it) },
    handling = options["handling"]
)
