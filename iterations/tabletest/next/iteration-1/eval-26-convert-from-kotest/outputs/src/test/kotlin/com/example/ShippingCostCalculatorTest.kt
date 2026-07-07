package com.example

import org.junit.jupiter.api.Assertions.assertEquals
import org.tabletest.junit.Description
import org.tabletest.junit.TableTest
import org.tabletest.junit.TypeConverter
import java.math.BigDecimal

@TypeConverter
fun parseShippingZone(input: String): ShippingZone {
    val (region, speed) = input.split(" ")
    return ShippingZone(region, speed)
}

class ShippingCostCalculatorTest {

    private val calculator = ShippingCostCalculator()

    private fun options(
        fragile: Boolean = false,
        insuredValue: BigDecimal? = null,
        handling: String? = null
    ): PackageOptions {
        val opts = PackageOptions()
        opts.isFragile = fragile
        opts.insuredValue = insuredValue
        opts.handling = handling
        return opts
    }

    private fun cost(
        zone: ShippingZone,
        weight: Double,
        dimensions: List<Int>,
        carrier: Carrier = Carrier.DHL,
        fragile: Boolean = false,
        insuredValue: BigDecimal? = null,
        handling: String? = null
    ): BigDecimal = calculator.calculateShippingCost(
        zone, weight, dimensions, options(fragile, insuredValue, handling), carrier
    )

    @Description("""
        Dimensions are fixed at a small [10, 10, 10] so the dimensional (volumetric) weight
        (0.200) never exceeds the actual weight, isolating the base-rate lookup from the
        dimensional-weight override tested separately below. Carrier does not affect price,
        so it is exercised as a value set on every row.
        """)
    @TableTest("""
        Scenario                | Zone         | Weight | Carrier           | Base Rate?
        EU standard light       | EU standard  | 0.5    | {DHL, UPS, FEDEX} | 5.00
        EU standard medium      | EU standard  | 3.0    | {DHL, UPS, FEDEX} | 7.50
        EU standard heavy       | EU standard  | 10.0   | {DHL, UPS, FEDEX} | 12.50
        EU standard very heavy  | EU standard  | 25.0   | {DHL, UPS, FEDEX} | 20.00
        EU express light        | EU express   | 0.5    | {DHL, UPS, FEDEX} | 8.00
        EU express medium       | EU express   | 3.0    | {DHL, UPS, FEDEX} | 12.00
        US standard light       | US standard  | 0.5    | {DHL, UPS, FEDEX} | 7.00
        US express heavy        | US express   | 10.0   | {DHL, UPS, FEDEX} | 30.00
        """)
    fun calculatesBaseRateByZoneAndWeight(zone: ShippingZone, weight: Double, carrier: Carrier, baseRate: BigDecimal) {
        val result = cost(zone, weight, listOf(10, 10, 10), carrier = carrier)
        assertEquals(0, result.compareTo(baseRate))
    }

    @Description("""
        Volumetric (dimensional) weight is charged instead of actual weight whenever it is
        larger. Both rows use the EU standard zone; the second row's dimensions push the
        dimensional weight from 1.0kg up to 7.0kg, moving it into the next pricing tier.
        """)
    @TableTest("""
        Scenario                          | Weight | Dimensions   | Rate?
        Actual weight governs             | 3.0    | [30, 20, 15] | 7.50
        Dimensional weight overrides tier  | 1.0    | [70, 50, 10] | 12.50
        """)
    fun appliesDimensionalWeightOverride(weight: Double, dimensions: List<Int>, rate: BigDecimal) {
        val result = cost(ShippingZone("EU", "standard"), weight, dimensions)
        assertEquals(0, result.compareTo(rate))
    }

    @TableTest("""
        Scenario                        | Dimensions   | Fee?
        At threshold, not oversize      | [100, 5, 5]  | 7.50
        One dimension exceeds threshold | [120, 5, 5]  | 17.50
        """)
    fun appliesOversizeSurcharge(dimensions: List<Int>, fee: BigDecimal) {
        val result = cost(ShippingZone("EU", "standard"), 3.0, dimensions)
        assertEquals(0, result.compareTo(fee))
    }

    @TableTest("""
        Scenario             | Handling | Total?
        No special handling  |          | 7.50
        Hazmat handling      | hazmat   | 15.50
        """)
    fun appliesHazmatFee(handling: String?, total: BigDecimal) {
        val result = cost(ShippingZone("EU", "standard"), 3.0, listOf(30, 20, 15), handling = handling)
        assertEquals(0, result.compareTo(total))
    }

    @TableTest("""
        Scenario     | Fragile | Total?
        Not fragile  | false   | 7.50
        Fragile      | true    | 8.625
        """)
    fun appliesFragileMultiplier(fragile: Boolean, total: BigDecimal) {
        val result = cost(ShippingZone("EU", "standard"), 3.0, listOf(30, 20, 15), fragile = fragile)
        assertEquals(0, result.compareTo(total))
    }

    @Description("""
        The insurance premium is 0.6% of the insured value, with a 3.00 minimum. 200 falls
        below the minimum, 500 sits exactly at it (500 * 0.006 = 3.00), and 1000 exceeds it.
        """)
    @TableTest("""
        Scenario               | Insured Value | Total?
        No insurance           |               | 7.50
        Below minimum premium  | 200           | 10.50
        At minimum premium     | 500           | 10.50
        Above minimum premium  | 1000          | 13.50
        """)
    fun appliesInsurancePremium(insuredValue: BigDecimal?, total: BigDecimal) {
        val result = cost(ShippingZone("EU", "standard"), 3.0, listOf(30, 20, 15), insuredValue = insuredValue)
        assertEquals(0, result.compareTo(total))
    }

    @Description("""
        Insurance is applied after the fragile multiplier, not before: fragile alone gives
        7.50 * 1.15 = 8.625, and adding the 3.00 insurance minimum on top gives 11.625 -
        not 1.15 applied to an already-insured base.
        """)
    @TableTest("""
        Scenario                      | Fragile | Insured Value | Total?
        Fragile only                  | true    |               | 8.625
        Insured only                  | false   | 200           | 10.50
        Fragile and insured combined  | true    | 200           | 11.625
        """)
    fun combinesFragileAndInsurance(fragile: Boolean, insuredValue: BigDecimal?, total: BigDecimal) {
        val result = cost(ShippingZone("EU", "standard"), 3.0, listOf(30, 20, 15), fragile = fragile, insuredValue = insuredValue)
        assertEquals(0, result.compareTo(total))
    }
}
