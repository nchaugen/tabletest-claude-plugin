package com.example

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.tabletest.junit.Description
import org.tabletest.junit.TableTest
import org.tabletest.junit.TypeConverter
import java.math.BigDecimal

class ShippingCostCalculatorTest {

    private val calculator = ShippingCostCalculator()

    @DisplayName("Determines the base fee from region, speed and weight")
    @Description("""
        Dimensions ([10, 10, 10] cm), package options (none) and carrier (DHL) are held fixed
        throughout — the dimensional-weight override is covered in its own table below, and the
        carrier is shown not to affect the fee in its own table further down.
        """)
    @TableTest("""
        Scenario                         | Zone        | Weight (kg) | Fee?
        At the light tier limit          | EU standard | 1           | 5.00
        Just above the light tier limit  | EU standard | 1.01        | 7.50
        At the medium tier limit         | EU standard | 5           | 7.50
        Just above the medium tier limit | EU standard | 5.01        | 12.50
        At the heavy tier limit          | EU standard | 15          | 12.50
        Just above the heavy tier limit  | EU standard | 15.01       | 20.00
        Light tier                       | EU express  | 0.5         | 8.00
        Medium tier                      | EU express  | 3           | 12.00
        Heavy tier                       | EU express  | 10          | 20.00
        Very heavy tier                  | EU express  | 20          | 32.00
        Light tier                       | US standard | 0.5         | 7.00
        Medium tier                      | US standard | 3           | 10.50
        Heavy tier                       | US standard | 10          | 17.50
        Very heavy tier                  | US standard | 20          | 28.00
        Light tier                       | US express  | 0.5         | 11.00
        Medium tier                      | US express  | 3           | 16.50
        Heavy tier                       | US express  | 10          | 30.00
        Very heavy tier                  | US express  | 20          | 45.00
        """)
    fun determinesBaseFeeByRegionSpeedAndWeight(zone: ShippingZone, weightKg: Double, fee: BigDecimal) {
        val dimensions = listOf(10, 10, 10)
        val actual = calculator.calculateShippingCost(zone, weightKg, dimensions, PackageOptions(), Carrier.DHL)
        assertFee(fee, actual)
    }

    @DisplayName("Applies oversize, hazmat, fragile and insurance surcharges to the base fee")
    @Description("""
        Zone is held at EU standard and weight at 3kg throughout, so the base fee is always 7.50
        (see the base-fee table above for how that figure is derived) — isolating how the four
        surcharges apply and combine.
        """)
    @TableTest("""
        Scenario                            | Dimensions (cm) | Options                            | Fee?
        No surcharges apply                 | [30, 20, 15]    | [:]                                | 7.50
        At the oversize limit               | [100, 5, 5]     | [:]                                | 7.50
        Just past the oversize limit        | [101, 5, 5]     | [:]                                | 17.50
        Hazmat handling                     | [30, 20, 15]    | [handling: hazmat]                 | 15.50
        Fragile packaging                   | [30, 20, 15]    | [fragile: true]                    | 8.625
        Insurance premium below the minimum | [30, 20, 15]    | [insuredValue: 200]                | 10.50
        Insurance premium at the minimum    | [30, 20, 15]    | [insuredValue: 500]                | 10.50
        Insurance premium above the minimum | [30, 20, 15]    | [insuredValue: 1000]               | 13.50
        Fragile and insured together        | [30, 20, 15]    | [fragile: true, insuredValue: 200] | 11.625
        """)
    fun appliesSurchargesToTheBaseFee(dimensions: List<Int>, options: PackageOptions, fee: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val actual = calculator.calculateShippingCost(zone, 3.0, dimensions, options, Carrier.DHL)
        assertFee(fee, actual)
    }

    @DisplayName("Charges based on the larger of actual and dimensional weight")
    @Description("""
        Zone is held at EU standard with no package options, isolating the comparison between
        actual weight and the weight implied by the package dimensions.
        """)
    @TableTest("""
        Scenario                          | Weight (kg) | Dimensions (cm) | Fee?
        Actual weight exceeds dimensional | 3.0         | [10, 10, 10]    | 7.50
        Dimensional weight exceeds actual | 1.0         | [70, 50, 10]    | 12.50
        """)
    fun usesTheLargerOfActualAndDimensionalWeight(weightKg: Double, dimensions: List<Int>, fee: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val actual = calculator.calculateShippingCost(zone, weightKg, dimensions, PackageOptions(), Carrier.DHL)
        assertFee(fee, actual)
    }

    @DisplayName("Charges the same fee regardless of carrier")
    @TableTest("""
        Scenario                        | Carrier           | Fee?
        Carrier does not affect the fee | {DHL, UPS, FEDEX} | 12.00
        """)
    fun ignoresCarrierWhenCalculatingTheFee(carrier: Carrier, fee: BigDecimal) {
        val zone = ShippingZone("EU", "express")
        val actual = calculator.calculateShippingCost(zone, 3.0, listOf(30, 20, 15), PackageOptions(), carrier)
        assertFee(fee, actual)
    }

    private fun assertFee(expected: BigDecimal, actual: BigDecimal) {
        assertEquals(0, expected.compareTo(actual), "expected $expected but was $actual")
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
