package com.example

import org.junit.jupiter.api.Assertions.assertEquals
import org.tabletest.junit.Description
import org.tabletest.junit.TableTest
import java.math.BigDecimal

class ShippingCostCalculatorTest {

    private val calculator = ShippingCostCalculator()

    @Description("""
        Dimensions are fixed at 10x10x10 cm so the dimensional weight (0.2 kg) never
        exceeds the actual weight tested here, isolating the region/speed/weight tier
        lookup from the dimensional weight override (see dimensionalWeightOverride).
        Carrier does not affect price (see carrierDoesNotAffectCost).
        """)
    @TableTest("""
        Scenario               | Region | Speed    | Weight | Base Fee?
        EU standard light      | EU     | standard | 0.5    | 5.00
        EU standard medium     | EU     | standard | 3.0    | 7.50
        EU standard heavy      | EU     | standard | 10.0   | 12.50
        EU standard very heavy | EU     | standard | 25.0   | 20.00
        EU express light       | EU     | express  | 0.5    | 8.00
        EU express medium      | EU     | express  | 3.0    | 12.00
        US standard light      | US     | standard | 0.5    | 7.00
        US express heavy       | US     | express  | 10.0   | 30.00
        """)
    fun baseRateByRegionSpeedAndWeight(region: String, speed: String, weight: Double, baseFee: BigDecimal) {
        val zone = ShippingZone(region, speed)
        val fee = calculator.calculateShippingCost(zone, weight, listOf(10, 10, 10), PackageOptions(), Carrier.DHL)
        assertFee(baseFee, fee)
    }

    @Description("""
        Region is fixed to EU standard (base fee 7.50 for up to 5 kg) so Base Fee? shows
        only the effect of dimensional weight overriding the actual weight.
        """)
    @TableTest("""
        Scenario                                      | Weight | Dimensions   | Base Fee?
        Actual weight wins (heavier than dimensional)  | 3.0    | [30, 20, 15] | 7.50
        Dimensional weight wins (bulky, light package) | 1.0    | [70, 50, 10] | 12.50
        """)
    fun dimensionalWeightOverride(weight: Double, dimensions: List<Int>, baseFee: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val fee = calculator.calculateShippingCost(zone, weight, dimensions, PackageOptions(), Carrier.DHL)
        assertFee(baseFee, fee)
    }

    @Description("""
        Region is fixed to EU standard, weight to 3.0 kg (base fee 7.50), so Fee? shows
        only the oversize surcharge of 10.00, charged when any dimension exceeds 100 cm.
        """)
    @TableTest("""
        Scenario                                        | Dimensions   | Fee?
        Within size limit                               | [30, 20, 15] | 7.50
        Exceeds size limit (one dimension over 100 cm)  | [120, 5, 5]  | 17.50
        """)
    fun oversizeSurcharge(dimensions: List<Int>, fee: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val result = calculator.calculateShippingCost(zone, 3.0, dimensions, PackageOptions(), Carrier.DHL)
        assertFee(fee, result)
    }

    @Description("""
        Region is fixed to EU standard, weight to 3.0 kg, dimensions to 30x20x15 cm
        (base fee 7.50), so Fee? shows only the hazmat handling surcharge of 8.00.
        """)
    @TableTest("""
        Scenario             | Options            | Fee?
        No special handling  |                    | 7.50
        Hazmat handling      | [handling: hazmat] | 15.50
        """)
    fun hazmatHandlingSurcharge(options: Map<String, String>?, fee: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val result = calculator.calculateShippingCost(zone, 3.0, listOf(30, 20, 15), buildOptions(options), Carrier.DHL)
        assertFee(fee, result)
    }

    @Description("""
        Region is fixed to EU standard, weight to 3.0 kg, dimensions to 30x20x15 cm
        (base fee 7.50), so Fee? shows only the 1.15x fragile-handling multiplier.
        """)
    @TableTest("""
        Scenario     | Options          | Fee?
        Not fragile  |                  | 7.50
        Fragile      | [fragile: true]  | 8.625
        """)
    fun fragileSurcharge(options: Map<String, String>?, fee: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val result = calculator.calculateShippingCost(zone, 3.0, listOf(30, 20, 15), buildOptions(options), Carrier.DHL)
        assertFee(fee, result)
    }

    @Description("""
        Region is fixed to EU standard, weight to 3.0 kg, dimensions to 30x20x15 cm
        (base fee 7.50). Insurance premium is 0.6% of insured value with a 3.00
        minimum, added after any fragile multiplier has been applied to the base fee.
        """)
    @TableTest("""
        Scenario                                   | Options                             | Fee?
        No insurance                               |                                     | 7.50
        Insurance premium at minimum                | [insuredValue: 500]                 | 10.50
        Insurance combined with fragile surcharge   | [fragile: true, insuredValue: 200]  | 11.625
        """)
    fun insuranceSurcharge(options: Map<String, String>?, fee: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val result = calculator.calculateShippingCost(zone, 3.0, listOf(30, 20, 15), buildOptions(options), Carrier.DHL)
        assertFee(fee, result)
    }

    @Description("""
        Region is fixed to EU express, weight to 3.0 kg, dimensions to 30x20x15 cm
        (base fee 12.00). Carrier is not a pricing input.
        """)
    @TableTest("""
        Scenario                               | Carrier           | Fee?
        Fee is identical regardless of carrier | {DHL, UPS, FEDEX} | 12.00
        """)
    fun carrierDoesNotAffectCost(carrier: Carrier, fee: BigDecimal) {
        val zone = ShippingZone("EU", "express")
        val result = calculator.calculateShippingCost(zone, 3.0, listOf(30, 20, 15), PackageOptions(), carrier)
        assertFee(fee, result)
    }

    private fun assertFee(expected: BigDecimal, actual: BigDecimal) {
        assertEquals(0, expected.compareTo(actual)) { "expected $expected but was $actual" }
    }

    private fun buildOptions(config: Map<String, String>?): PackageOptions {
        if (config == null) return PackageOptions()
        return PackageOptions(
            isFragile = config["fragile"]?.toBoolean() ?: false,
            insuredValue = config["insuredValue"]?.let { BigDecimal(it) },
            handling = config["handling"]
        )
    }
}
