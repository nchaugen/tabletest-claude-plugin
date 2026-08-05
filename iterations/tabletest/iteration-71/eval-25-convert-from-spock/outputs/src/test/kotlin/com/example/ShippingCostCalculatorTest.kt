package com.example

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.tabletest.junit.Description
import org.tabletest.junit.TableTest
import org.tabletest.junit.TypeConverter
import java.math.BigDecimal

class ShippingCostCalculatorTest {

    private val calculator = ShippingCostCalculator()

    @DisplayName("Looks up the base shipping rate from region, speed and weight")
    @Description("""
        Dimensions are held at 10 x 10 x 10 cm and package options are left at their defaults
        throughout, so the dimensional weight (0.2 kg) never overtakes the actual weight and no
        surcharge applies. Carrier is fixed at DHL; carrier independence is verified separately below.
        """)
    @TableTest("""
        Scenario                                   | Region | Speed    | Weight (kg) | Shipping Cost?
        EU standard, top of the light tier         | EU     | standard | 1.0         | 5.00
        EU standard, bottom of the medium tier     | EU     | standard | 1.01        | 7.50
        EU standard, top of the medium tier        | EU     | standard | 5.0         | 7.50
        EU standard, bottom of the heavy tier      | EU     | standard | 5.01        | 12.50
        EU standard, top of the heavy tier         | EU     | standard | 15.0        | 12.50
        EU standard, bottom of the very heavy tier | EU     | standard | 15.01       | 20.00
        EU express, top of the light tier          | EU     | express  | 1.0         | 8.00
        EU express, bottom of the medium tier      | EU     | express  | 1.01        | 12.00
        EU express, top of the medium tier         | EU     | express  | 5.0         | 12.00
        EU express, bottom of the heavy tier       | EU     | express  | 5.01        | 20.00
        EU express, top of the heavy tier          | EU     | express  | 15.0        | 20.00
        EU express, bottom of the very heavy tier  | EU     | express  | 15.01       | 32.00
        US standard, top of the light tier         | US     | standard | 1.0         | 7.00
        US standard, bottom of the medium tier     | US     | standard | 1.01        | 10.50
        US standard, top of the medium tier        | US     | standard | 5.0         | 10.50
        US standard, bottom of the heavy tier      | US     | standard | 5.01        | 17.50
        US standard, top of the heavy tier         | US     | standard | 15.0        | 17.50
        US standard, bottom of the very heavy tier | US     | standard | 15.01       | 28.00
        US express, top of the light tier          | US     | express  | 1.0         | 11.00
        US express, bottom of the medium tier      | US     | express  | 1.01        | 16.50
        US express, top of the medium tier         | US     | express  | 5.0         | 16.50
        US express, bottom of the heavy tier       | US     | express  | 5.01        | 30.00
        US express, top of the heavy tier          | US     | express  | 15.0        | 30.00
        US express, bottom of the very heavy tier  | US     | express  | 15.01       | 45.00
        """)
    fun looksUpBaseRate(region: String, speed: String, weightKg: Double, shippingCost: BigDecimal) {
        val zone = ShippingZone(region, speed)
        val cost = calculator.calculateShippingCost(zone, weightKg, listOf(10, 10, 10), PackageOptions(), Carrier.DHL)
        assertMoneyEquals(shippingCost, cost)
    }

    @DisplayName("Charges for the dimensional weight when it exceeds the actual weight")
    @Description("""
        Region and speed are fixed at EU standard, whose rate ladder is established above, so the
        tier reached by the effective weight is what each row demonstrates.
        """)
    @TableTest("""
        Scenario                                 | Weight (kg) | Dimensions (cm) | Shipping Cost?
        Actual weight exceeds dimensional weight | 3.0         | [10, 10, 10]    | 7.50
        Dimensional weight exceeds actual weight | 1.0         | [70, 50, 10]    | 12.50
        """)
    fun chargesForTheGreaterOfActualAndDimensionalWeight(weightKg: Double, dimensions: List<Int>, shippingCost: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val cost = calculator.calculateShippingCost(zone, weightKg, dimensions, PackageOptions(), Carrier.DHL)
        assertMoneyEquals(shippingCost, cost)
    }

    @DisplayName("Adds an oversize fee once any dimension passes the oversize threshold")
    @Description("""
        Weight is fixed at 3.0 kg on the EU standard ladder (7.50, the "top of the medium tier"
        row above); only the longest dimension varies.
        """)
    @TableTest("""
        Scenario                         | Dimensions (cm) | Shipping Cost?
        At the oversize threshold        | [100, 5, 5]     | 7.50
        Just past the oversize threshold | [101, 5, 5]     | 17.50
        """)
    fun addsAnOversizeFee(dimensions: List<Int>, shippingCost: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val cost = calculator.calculateShippingCost(zone, 3.0, dimensions, PackageOptions(), Carrier.DHL)
        assertMoneyEquals(shippingCost, cost)
    }

    @DisplayName("Adds a hazmat fee only for hazmat handling")
    @Description("""
        Weight, dimensions, region and speed are fixed at the same EU standard baseline as above
        (7.50); only the handling option varies.
        """)
    @TableTest("""
        Scenario                  | Options               | Shipping Cost?
        No handling specified     | [:]                   | 7.50
        Non-hazmat handling value | [handling: signature] | 7.50
        Hazmat handling           | [handling: hazmat]    | 15.50
        """)
    fun addsAHazmatFee(options: PackageOptions, shippingCost: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val cost = calculator.calculateShippingCost(zone, 3.0, listOf(30, 20, 15), options, Carrier.DHL)
        assertMoneyEquals(shippingCost, cost)
    }

    @DisplayName("Applies a fragile multiplier to the cost so far")
    @Description("""
        Weight, dimensions, region and speed are fixed at the same EU standard baseline as above
        (7.50); only the fragile option varies.
        """)
    @TableTest("""
        Scenario    | Options         | Shipping Cost?
        Not fragile | [:]             | 7.50
        Fragile     | [fragile: true] | 8.625
        """)
    fun appliesAFragileMultiplier(options: PackageOptions, shippingCost: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val cost = calculator.calculateShippingCost(zone, 3.0, listOf(30, 20, 15), options, Carrier.DHL)
        assertMoneyEquals(shippingCost, cost)
    }

    @DisplayName("Adds an insurance premium of the insured value or the minimum, whichever is greater")
    @Description("""
        Weight, dimensions, region and speed are fixed at the same EU standard baseline as above
        (7.50); only the insured value varies. The minimum premium is reached at an insured value
        of 500.
        """)
    @TableTest("""
        Scenario                  | Options              | Shipping Cost?
        No insurance              | [:]                  | 7.50
        Premium below the minimum | [insuredValue: 200]  | 10.50
        Premium at the minimum    | [insuredValue: 500]  | 10.50
        Premium above the minimum | [insuredValue: 1000] | 13.50
        """)
    fun addsAnInsurancePremium(options: PackageOptions, shippingCost: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val cost = calculator.calculateShippingCost(zone, 3.0, listOf(30, 20, 15), options, Carrier.DHL)
        assertMoneyEquals(shippingCost, cost)
    }

    @DisplayName("Applies the fragile multiplier before adding the insurance premium")
    @Description("""
        Neither table above shows the two combined: if insurance were added before the fragile
        multiplier, an insured value of 200 would give (7.50 + 3.00) * 1.15 = 12.075, not 11.625.
        Weight, dimensions, region and speed are fixed at the same EU standard baseline as above.
        """)
    @TableTest("""
        Scenario                                               | Options                             | Shipping Cost?
        Fragile multiplier applies before insurance is added   | [fragile: true, insuredValue: 200]  | 11.625
        Insurance premium is not multiplied by the fragile fee | [fragile: true, insuredValue: 1000] | 14.625
        """)
    fun appliesTheFragileMultiplierBeforeInsurance(options: PackageOptions, shippingCost: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val cost = calculator.calculateShippingCost(zone, 3.0, listOf(30, 20, 15), options, Carrier.DHL)
        assertMoneyEquals(shippingCost, cost)
    }

    @DisplayName("Does not vary the shipping cost by carrier")
    @Description("""
        Baseline is the EU express rate for a 3.0 kg parcel (12.00, the "top of the medium tier"
        row above).
        """)
    @TableTest("""
        Scenario                  | Carrier           | Shipping Cost?
        Same rate for any carrier | {DHL, UPS, FEDEX} | 12.00
        """)
    fun doesNotVaryByCarrier(carrier: Carrier, shippingCost: BigDecimal) {
        val zone = ShippingZone("EU", "express")
        val cost = calculator.calculateShippingCost(zone, 3.0, listOf(30, 20, 15), PackageOptions(), carrier)
        assertMoneyEquals(shippingCost, cost)
    }

    private fun assertMoneyEquals(expected: BigDecimal, actual: BigDecimal) {
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
