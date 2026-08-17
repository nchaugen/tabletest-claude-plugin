package com.example

import org.junit.jupiter.api.Assertions.assertEquals
import org.tabletest.junit.Description
import org.tabletest.junit.TableTest
import org.tabletest.junit.TypeConverter
import java.math.BigDecimal

class ShippingCostCalculatorTest {

    private val calculator = ShippingCostCalculator()

    @TableTest(
        """
        Scenario                                        | Zone        | Weight (kg) | Dimensions (cm) | Options | Carrier | Cost?
        EU standard, weight at the 1kg boundary         | EU standard | 1.0         | [10, 10, 10]    | [:]     | DHL     | 5.00
        EU standard, weight just past the 1kg boundary  | EU standard | 1.01        | [10, 10, 10]    | [:]     | DHL     | 7.50
        EU standard, weight at the 5kg boundary         | EU standard | 5.0         | [10, 10, 10]    | [:]     | DHL     | 7.50
        EU standard, weight just past the 5kg boundary  | EU standard | 5.01        | [10, 10, 10]    | [:]     | DHL     | 12.50
        EU standard, weight at the 15kg boundary        | EU standard | 15.0        | [10, 10, 10]    | [:]     | DHL     | 12.50
        EU standard, weight just past the 15kg boundary | EU standard | 15.01       | [10, 10, 10]    | [:]     | DHL     | 20.00
        EU express, light package                       | EU express  | 0.5         | [10, 10, 10]    | [:]     | DHL     | 8.00
        EU express, medium package                      | EU express  | 3.0         | [10, 10, 10]    | [:]     | DHL     | 12.00
        EU express, heavy package                       | EU express  | 10.0        | [10, 10, 10]    | [:]     | DHL     | 20.00
        EU express, very heavy package                  | EU express  | 25.0        | [10, 10, 10]    | [:]     | DHL     | 32.00
        US standard, light package                      | US standard | 0.5         | [10, 10, 10]    | [:]     | DHL     | 7.00
        US standard, medium package                     | US standard | 3.0         | [10, 10, 10]    | [:]     | DHL     | 10.50
        US standard, heavy package                      | US standard | 10.0        | [10, 10, 10]    | [:]     | DHL     | 17.50
        US standard, very heavy package                 | US standard | 25.0        | [10, 10, 10]    | [:]     | DHL     | 28.00
        US express, light package                       | US express  | 0.5         | [10, 10, 10]    | [:]     | DHL     | 11.00
        US express, medium package                      | US express  | 3.0         | [10, 10, 10]    | [:]     | DHL     | 16.50
        US express, heavy package                       | US express  | 10.0        | [10, 10, 10]    | [:]     | DHL     | 30.00
        US express, very heavy package                  | US express  | 25.0        | [10, 10, 10]    | [:]     | DHL     | 45.00
        """
    )
    fun `computes the base rate from zone and weight`(
        zone: ShippingZone, weightKg: Double, dimensions: List<Int>,
        options: PackageOptions, carrier: Carrier, cost: BigDecimal
    ) {
        assertCost(cost, calculator.calculateShippingCost(zone, weightKg, dimensions, options, carrier))
    }

    @TableTest(
        """
        Scenario                                     | Zone        | Weight (kg) | Dimensions (cm) | Options | Carrier | Cost?
        Actual weight exceeds the dimensional weight | EU standard | 3.0         | [10, 10, 10]    | [:]     | DHL     | 7.50
        Dimensional weight exceeds the actual weight | EU standard | 1.0         | [70, 50, 10]    | [:]     | DHL     | 12.50
        """
    )
    fun `charges for the larger of actual and dimensional weight`(
        zone: ShippingZone, weightKg: Double, dimensions: List<Int>,
        options: PackageOptions, carrier: Carrier, cost: BigDecimal
    ) {
        assertCost(cost, calculator.calculateShippingCost(zone, weightKg, dimensions, options, carrier))
    }

    @TableTest(
        """
        Scenario                                | Zone        | Weight (kg) | Dimensions (cm) | Options | Carrier | Cost?
        Dimensions at the oversize threshold    | EU standard | 3.0         | [100, 5, 5]     | [:]     | DHL     | 7.50
        Length just past the oversize threshold | EU standard | 3.0         | [101, 5, 5]     | [:]     | DHL     | 17.50
        Width just past the oversize threshold  | EU standard | 3.0         | [5, 101, 5]     | [:]     | DHL     | 17.50
        """
    )
    fun `adds a flat fee for oversize packages`(
        zone: ShippingZone, weightKg: Double, dimensions: List<Int>,
        options: PackageOptions, carrier: Carrier, cost: BigDecimal
    ) {
        assertCost(cost, calculator.calculateShippingCost(zone, weightKg, dimensions, options, carrier))
    }

    @TableTest(
        """
        Scenario                 | Zone        | Weight (kg) | Dimensions (cm) | Options         | Carrier | Cost?
        Package handled normally | EU standard | 3.0         | [10, 10, 10]    | [:]             | DHL     | 7.50
        Package marked fragile   | EU standard | 3.0         | [10, 10, 10]    | [fragile: true] | DHL     | 8.625
        """
    )
    fun `applies a multiplier for fragile packages`(
        zone: ShippingZone, weightKg: Double, dimensions: List<Int>,
        options: PackageOptions, carrier: Carrier, cost: BigDecimal
    ) {
        assertCost(cost, calculator.calculateShippingCost(zone, weightKg, dimensions, options, carrier))
    }

    @TableTest(
        """
        Scenario                            | Zone        | Weight (kg) | Dimensions (cm) | Options             | Carrier | Cost?
        No insured value declared           | EU standard | 3.0         | [10, 10, 10]    | [:]                 | DHL     | 7.50
        Insurance premium below the minimum | EU standard | 3.0         | [10, 10, 10]    | [insuredValue: 499] | DHL     | 10.50
        Insurance premium above the minimum | EU standard | 3.0         | [10, 10, 10]    | [insuredValue: 501] | DHL     | 10.506
        """
    )
    fun `adds an insurance premium for insured packages`(
        zone: ShippingZone, weightKg: Double, dimensions: List<Int>,
        options: PackageOptions, carrier: Carrier, cost: BigDecimal
    ) {
        assertCost(cost, calculator.calculateShippingCost(zone, weightKg, dimensions, options, carrier))
    }

    @TableTest(
        """
        Scenario                         | Zone        | Weight (kg) | Dimensions (cm) | Options              | Carrier | Cost?
        No handling instructions         | EU standard | 3.0         | [10, 10, 10]    | [:]                  | DHL     | 7.50
        Non-hazmat handling instructions | EU standard | 3.0         | [10, 10, 10]    | [handling: standard] | DHL     | 7.50
        Hazmat handling instructions     | EU standard | 3.0         | [10, 10, 10]    | [handling: hazmat]   | DHL     | 15.50
        """
    )
    fun `adds a flat fee for hazmat handling`(
        zone: ShippingZone, weightKg: Double, dimensions: List<Int>,
        options: PackageOptions, carrier: Carrier, cost: BigDecimal
    ) {
        assertCost(cost, calculator.calculateShippingCost(zone, weightKg, dimensions, options, carrier))
    }

    @TableTest(
        """
        Scenario                        | Zone       | Weight (kg) | Dimensions (cm) | Options | Carrier           | Cost?
        Any carrier gives the same cost | EU express | 3.0         | [10, 10, 10]    | [:]     | {DHL, UPS, FEDEX} | 12.00
        """
    )
    fun `charges the same rate regardless of carrier`(
        zone: ShippingZone, weightKg: Double, dimensions: List<Int>,
        options: PackageOptions, carrier: Carrier, cost: BigDecimal
    ) {
        assertCost(cost, calculator.calculateShippingCost(zone, weightKg, dimensions, options, carrier))
    }

    @Description(
        """
        Neither single-concern table above shows how the surcharges combine. These rows pin the
        order: flat fees (oversize, hazmat) are added first, the fragile multiplier is applied to
        that subtotal, and the insurance premium is added last, after the multiplier.
        """
    )
    @TableTest(
        """
        Scenario                                                    | Zone        | Weight (kg) | Dimensions (cm) | Options                            | Carrier | Cost?
        Flat fees are added before the fragile multiplier           | EU standard | 3.0         | [101, 5, 5]     | [fragile: true, handling: hazmat]  | DHL     | 29.325
        The fragile multiplier applies before the insurance premium | EU standard | 3.0         | [10, 10, 10]    | [fragile: true, insuredValue: 200] | DHL     | 11.625
        """
    )
    fun `applies flat fees and multipliers in a fixed order`(
        zone: ShippingZone, weightKg: Double, dimensions: List<Int>,
        options: PackageOptions, carrier: Carrier, cost: BigDecimal
    ) {
        assertCost(cost, calculator.calculateShippingCost(zone, weightKg, dimensions, options, carrier))
    }

    private fun assertCost(expected: BigDecimal, actual: BigDecimal) {
        assertEquals(0, expected.compareTo(actual), "expected $expected but was $actual")
    }
}

@TypeConverter
fun parseZone(value: String): ShippingZone {
    val (region, speed) = value.split(" ")
    return ShippingZone(region, speed)
}

@TypeConverter
fun parseOptions(fields: Map<String, String>): PackageOptions =
    PackageOptions(
        isFragile = fields["fragile"]?.toBoolean() ?: false,
        insuredValue = fields["insuredValue"]?.let { BigDecimal(it) },
        handling = fields["handling"]
    )
