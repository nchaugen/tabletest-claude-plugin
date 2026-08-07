package com.example

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.tabletest.junit.Description
import org.tabletest.junit.TableTest
import org.tabletest.junit.TypeConverter
import java.math.BigDecimal

class ShippingCostCalculatorTest {

    private val calculator = ShippingCostCalculator()

    @DisplayName("Determines the base shipping rate from region, speed and weight")
    @Description(
        """
        Dimensions are in centimetres and default to a small package (10x10x10) that never
        outweighs the actual weight, except in the two rows that contrast actual against
        dimensional weight. No package options or oversize fee apply here - see
        appliesPackageHandlingSurcharges and addsAnOversizeFee. Carrier is held at DHL
        throughout - see chargesTheSameRateRegardlessOfCarrier.
        """
    )
    @TableTest(
        """
        Scenario                                 | Zone        | Weight (kg) | Dimensions (cm) | Cost?
        EU standard, at the first tier limit     | EU standard | 1.0         | [10, 10, 10]    | 5.00
        EU standard, second tier begins          | EU standard | 1.01        | [10, 10, 10]    | 7.50
        EU standard, at the second tier limit    | EU standard | 5.0         | [10, 10, 10]    | 7.50
        EU standard, third tier begins           | EU standard | 5.01        | [10, 10, 10]    | 12.50
        EU standard, at the third tier limit     | EU standard | 15.0        | [10, 10, 10]    | 12.50
        EU standard, fourth tier begins          | EU standard | 15.01       | [10, 10, 10]    | 20.00
        EU express, first tier                   | EU express  | 0.5         | [10, 10, 10]    | 8.00
        EU express, second tier                  | EU express  | 3.0         | [10, 10, 10]    | 12.00
        EU express, third tier                   | EU express  | 10.0        | [10, 10, 10]    | 20.00
        EU express, fourth tier                  | EU express  | 25.0        | [10, 10, 10]    | 32.00
        US standard, first tier                  | US standard | 0.5         | [10, 10, 10]    | 7.00
        US standard, second tier                 | US standard | 3.0         | [10, 10, 10]    | 10.50
        US standard, third tier                  | US standard | 10.0        | [10, 10, 10]    | 17.50
        US standard, fourth tier                 | US standard | 25.0        | [10, 10, 10]    | 28.00
        US express, first tier                   | US express  | 0.5         | [10, 10, 10]    | 11.00
        US express, second tier                  | US express  | 3.0         | [10, 10, 10]    | 16.50
        US express, third tier                   | US express  | 10.0        | [10, 10, 10]    | 30.00
        US express, fourth tier                  | US express  | 25.0        | [10, 10, 10]    | 45.00
        Actual weight exceeds dimensional weight | EU standard | 1.0         | [20, 15, 10]    | 5.00
        Dimensional weight exceeds actual weight | EU standard | 1.0         | [70, 50, 10]    | 12.50
        """
    )
    fun determinesBaseShippingRate(zone: ShippingZone, weight: Double, dimensions: List<Int>, cost: BigDecimal) {
        val options = PackageOptions()
        assertEquals(
            0,
            cost.compareTo(calculator.calculateShippingCost(zone, weight, dimensions, options, Carrier.DHL))
        )
    }

    @DisplayName("Adds an oversize fee when a dimension exceeds the threshold")
    @Description(
        """
        Weight and zone match the base-rate scenario in determinesBaseShippingRate
        (EU standard, 1kg -> 5.00 for a small package), shifted to 3kg so the fee's
        effect is isolated from the weight-tier boundaries. No other package options apply.
        """
    )
    @TableTest(
        """
        Scenario                         | Zone        | Weight (kg) | Dimensions (cm) | Oversize Threshold (cm) | Cost?
        At the oversize threshold        | EU standard | 3.0         | [100, 1, 1]     | 100                     | 7.50
        Just past the oversize threshold | EU standard | 3.0         | [101, 1, 1]     | 100                     | 17.50
        """
    )
    fun addsAnOversizeFee(zone: ShippingZone, weight: Double, dimensions: List<Int>, threshold: Int, cost: BigDecimal) {
        val options = PackageOptions()
        assertEquals(
            0,
            cost.compareTo(calculator.calculateShippingCost(zone, weight, dimensions, options, Carrier.DHL))
        )
    }

    @DisplayName("Applies package handling surcharges to the base rate")
    @Description(
        """
        Zone is EU standard, weight 3kg, dimensions 10x10x10cm throughout - base rate 7.50,
        no oversize fee (see determinesBaseShippingRate and addsAnOversizeFee). The insurance
        premium has a minimum, which the below/above-minimum rows straddle. The fragile
        combination rows show that additive fees (hazmat) are multiplied along with the base
        rate, while the insurance premium is added afterwards and is not multiplied.
        """
    )
    @TableTest(
        """
        Scenario                               | Options                            | Cost?
        No handling options                    | [:]                                | 7.50
        Fragile packaging                      | [fragile: true]                    | 8.625
        Hazmat handling                        | [handling: hazmat]                 | 15.50
        Insured below the minimum premium      | [insuredValue: 200]                | 10.50
        Insured above the minimum premium      | [insuredValue: 1000]               | 13.50
        Fragile packaging with hazmat handling | [fragile: true, handling: hazmat]  | 17.825
        Fragile packaging with insurance       | [fragile: true, insuredValue: 200] | 11.625
        """
    )
    fun appliesPackageHandlingSurcharges(options: PackageOptions, cost: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val dimensions = listOf(10, 10, 10)
        assertEquals(
            0,
            cost.compareTo(calculator.calculateShippingCost(zone, 3.0, dimensions, options, Carrier.DHL))
        )
    }

    @DisplayName("Charges the same rate regardless of carrier")
    @Description(
        """
        Zone is EU express, weight 3kg, dimensions 10x10x10cm (base rate 12.00, no package
        options) - see determinesBaseShippingRate. calculateShippingCost accepts a carrier,
        but the base rate and every surcharge are independent of it.
        """
    )
    @TableTest(
        """
        Scenario    | Carrier           | Cost?
        Any carrier | {DHL, UPS, FEDEX} | 12.00
        """
    )
    fun chargesTheSameRateRegardlessOfCarrier(carrier: Carrier, cost: BigDecimal) {
        val zone = ShippingZone("EU", "express")
        val dimensions = listOf(10, 10, 10)
        val options = PackageOptions()
        assertEquals(
            0,
            cost.compareTo(calculator.calculateShippingCost(zone, 3.0, dimensions, options, carrier))
        )
    }
}

@TypeConverter
fun parseShippingZone(value: String): ShippingZone {
    val (region, speed) = value.split(" ", limit = 2)
    return ShippingZone(region, speed)
}

@TypeConverter
fun parsePackageOptions(fields: Map<String, String>): PackageOptions {
    return PackageOptions(
        isFragile = fields["fragile"]?.toBoolean() ?: false,
        insuredValue = fields["insuredValue"]?.let { BigDecimal(it) },
        handling = fields["handling"]
    )
}
