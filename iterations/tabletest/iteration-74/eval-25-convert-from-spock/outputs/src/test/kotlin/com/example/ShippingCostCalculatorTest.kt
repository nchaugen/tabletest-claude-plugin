package com.example

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.tabletest.junit.Description
import org.tabletest.junit.TableTest
import java.math.BigDecimal

class ShippingCostCalculatorTest {

    private val calculator = ShippingCostCalculator()

    @Description("""
        Dimensions are held at 10 x 10 x 10 -- well under the oversize threshold, and too small
        for the dimensional (volumetric) weight to exceed the actual weight -- so actual weight
        alone decides the tier. The package is not fragile, uninsured, has no special handling,
        and the carrier is fixed at DHL; none of those affect the base rate (see the tables
        below). Every EU standard boundary is exercised; the other region/speed combinations
        share the same tier structure and get one row per tier.
    """)
    @TableTest("""
        Scenario                   | Region | Speed    | Weight (kg) | Cost?
        EU standard, tier 1 limit  | EU     | standard | 1.0         | 5.00
        EU standard, tier 2 begins | EU     | standard | 1.01        | 7.50
        EU standard, tier 2 limit  | EU     | standard | 5.0         | 7.50
        EU standard, tier 3 begins | EU     | standard | 5.01        | 12.50
        EU standard, tier 3 limit  | EU     | standard | 15.0        | 12.50
        EU standard, tier 4 begins | EU     | standard | 15.01       | 20.00
        EU express, tier 1         | EU     | express  | 0.5         | 8.00
        EU express, tier 2         | EU     | express  | 3.0         | 12.00
        EU express, tier 3         | EU     | express  | 10.0        | 20.00
        EU express, tier 4         | EU     | express  | 25.0        | 32.00
        US standard, tier 1        | US     | standard | 0.5         | 7.00
        US standard, tier 2        | US     | standard | 3.0         | 10.50
        US standard, tier 3        | US     | standard | 10.0        | 17.50
        US standard, tier 4        | US     | standard | 25.0        | 28.00
        US express, tier 1         | US     | express  | 0.5         | 11.00
        US express, tier 2         | US     | express  | 3.0         | 16.50
        US express, tier 3         | US     | express  | 10.0        | 30.00
        US express, tier 4         | US     | express  | 25.0        | 45.00
        """)
    fun determinesTheBaseRateFromRegionSpeedAndWeight(region: String, speed: String, weight: Double, cost: BigDecimal) {
        val zone = ShippingZone(region, speed)
        val dims = listOf(10, 10, 10)
        val opts = PackageOptions()

        val actual = calculator.calculateShippingCost(zone, weight, dims, opts, Carrier.DHL)

        assertEquals(0, cost.compareTo(actual))
    }

    @Description("""
        Region and speed are fixed at EU standard so the row's cost reveals which weight -- the
        actual weight or the dimensional weight computed from length x width x height -- was used
        to select the rate tier.
    """)
    @TableTest("""
        Scenario                   | Weight (kg) | Length (cm) | Width (cm) | Height (cm) | Cost?
        Actual weight governs      | 3.0         | 30          | 20         | 15          | 7.50
        Dimensional weight governs | 1.0         | 70          | 50         | 10          | 12.50
        """)
    fun usesWhicheverOfActualOrDimensionalWeightIsGreater(weight: Double, length: Int, width: Int, height: Int, cost: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val dims = listOf(length, width, height)
        val opts = PackageOptions()

        val actual = calculator.calculateShippingCost(zone, weight, dims, opts, Carrier.DHL)

        assertEquals(0, cost.compareTo(actual))
    }

    @Description("""
        Weight, region and speed are fixed at EU standard, 3.0 kg (7.50 base) so the surcharge is
        visible on its own. Any single dimension over the threshold triggers the fee, so the
        boundary is shown on length and confirmed again on width to show the check is not
        specific to one dimension.
    """)
    @TableTest("""
        Scenario                  | Length (cm) | Width (cm) | Height (cm) | Cost?
        At the oversize threshold | 100         | 5          | 5           | 7.50
        Just past it, via length  | 101         | 5          | 5           | 17.50
        Just past it, via width   | 5           | 101        | 5           | 17.50
        """)
    fun addsAnOversizeFeeWhenAnyDimensionExceedsTheThreshold(length: Int, width: Int, height: Int, cost: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val dims = listOf(length, width, height)
        val opts = PackageOptions()

        val actual = calculator.calculateShippingCost(zone, 3.0, dims, opts, Carrier.DHL)

        assertEquals(0, cost.compareTo(actual))
    }

    @Description("""
        Weight, region and speed are fixed at EU standard, 3.0 kg (7.50 base). Only the literal
        "hazmat" handling value adds the fee; any other handling string is treated like no
        special handling.
    """)
    @TableTest("""
        Scenario             | Handling | Cost?
        No special handling  |          | 7.50
        Hazmat handling      | hazmat   | 15.50
        Other handling value | standard | 7.50
        """)
    fun addsAHazmatFeeOnlyForHazmatHandling(handling: String?, cost: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val dims = listOf(10, 10, 10)
        val opts = PackageOptions(handling = handling)

        val actual = calculator.calculateShippingCost(zone, 3.0, dims, opts, Carrier.DHL)

        assertEquals(0, cost.compareTo(actual))
    }

    @Description("""
        Weight, region and speed are fixed at EU standard, 3.0 kg (7.50 base).
    """)
    @TableTest("""
        Scenario    | Fragile | Cost?
        Not fragile | false   | 7.50
        Fragile     | true    | 8.625
        """)
    fun appliesAFragileMultiplierToTheBaseRate(fragile: Boolean, cost: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val dims = listOf(10, 10, 10)
        val opts = PackageOptions(isFragile = fragile)

        val actual = calculator.calculateShippingCost(zone, 3.0, dims, opts, Carrier.DHL)

        assertEquals(0, cost.compareTo(actual))
    }

    @Description("""
        Weight, region and speed are fixed at EU standard, 3.0 kg (7.50 base). Insuring the
        package adds a surcharge with a minimum charge; rows straddle the insured value at which
        the minimum stops applying and the proportional premium takes over.
    """)
    @TableTest("""
        Scenario                                   | Insured Value | Cost?
        No insurance                               |               | 7.50
        Below the point the minimum stops applying | 200           | 10.50
        At the point the minimum stops applying    | 500           | 10.50
        Just past that point                       | 501           | 10.506
        """)
    fun addsAnInsuranceSurchargeWithAMinimumCharge(insuredValue: BigDecimal?, cost: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val dims = listOf(10, 10, 10)
        val opts = PackageOptions(insuredValue = insuredValue)

        val actual = calculator.calculateShippingCost(zone, 3.0, dims, opts, Carrier.DHL)

        assertEquals(0, cost.compareTo(actual))
    }

    @Description("""
        Weight, region and speed are fixed at EU express, 3.0 kg (12.00 base, no surcharges). The
        carrier parameter is accepted but not read by the pricing rule, so every carrier value set
        here must produce the same cost.
    """)
    @TableTest("""
        Scenario           | Carrier           | Cost?
        EU express package | {DHL, UPS, FEDEX} | 12.00
        """)
    fun pricesTheSameRegardlessOfCarrier(carrier: Carrier, cost: BigDecimal) {
        val zone = ShippingZone("EU", "express")
        val dims = listOf(10, 10, 10)
        val opts = PackageOptions()

        val actual = calculator.calculateShippingCost(zone, 3.0, dims, opts, carrier)

        assertEquals(0, cost.compareTo(actual))
    }

    @DisplayName("Applies additive fees before the fragile multiplier, and insurance after it")
    @Description("""
        Neither single-concern table above shows the order surcharges combine in. Weight, region
        and speed are fixed at EU standard, 3.0 kg (7.50 base).
    """)
    @TableTest("""
        Scenario                        | Fragile | Handling | Insured Value | Cost?
        Fragile package with hazmat fee | true    | hazmat   |               | 17.825
        Fragile and insured package     | true    |          | 200           | 11.625
        """)
    fun combinesSurchargesInTheCorrectOrder(fragile: Boolean, handling: String?, insuredValue: BigDecimal?, cost: BigDecimal) {
        val zone = ShippingZone("EU", "standard")
        val dims = listOf(10, 10, 10)
        val opts = PackageOptions(isFragile = fragile, insuredValue = insuredValue, handling = handling)

        val actual = calculator.calculateShippingCost(zone, 3.0, dims, opts, Carrier.DHL)

        assertEquals(0, cost.compareTo(actual))
    }
}
