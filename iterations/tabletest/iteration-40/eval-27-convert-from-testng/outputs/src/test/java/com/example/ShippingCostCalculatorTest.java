package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShippingCostCalculatorTest {

    private static final List<Integer> DEFAULT_DIMENSIONS = List.of(10, 10, 10);

    private final ShippingCostCalculator calculator = new ShippingCostCalculator();

    @Description("""
        Dimensions are fixed at [10, 10, 10] (dimensional weight ~0.2kg), so the
        effective weight always equals the actual weight for these rows.
        Dimensional weight overriding actual weight is covered by
        appliesDimensionalWeightWhenGreaterThanActual. No fragile/insured/hazmat
        options are applied. Carrier is fixed to DHL since carrier does not
        affect price (see carrierDoesNotAffectPrice).
        """)
    @TableTest("""
        Scenario                | Region | Speed    | Weight | Base Rate?
        EU standard, light      | EU     | standard | 0.5    | 5.00
        EU standard, medium     | EU     | standard | 3.0    | 7.50
        EU standard, heavy      | EU     | standard | 10.0   | 12.50
        EU standard, very heavy | EU     | standard | 25.0   | 20.00
        EU express, light       | EU     | express  | 0.5    | 8.00
        EU express, medium      | EU     | express  | 3.0    | 12.00
        US standard, light      | US     | standard | 0.5    | 7.00
        US express, heavy       | US     | express  | 10.0   | 30.00
        """)
    void calculatesBaseRateByZoneAndWeight(String region, String speed, double weight, BigDecimal baseRate) {
        ShippingZone zone = new ShippingZone(region, speed);
        BigDecimal result = calculator.calculateShippingCost(
                zone, weight, DEFAULT_DIMENSIONS, new PackageOptions(), Carrier.DHL);
        assertCost(baseRate, result);
    }

    @Description("""
        Zone is fixed to EU standard. Actual weight is held at 1.0kg in both
        rows; only dimensions vary, isolating whether the billable weight comes
        from the actual weight or the dimensional (volumetric) weight.
        """)
    @TableTest("""
        Scenario                                | Dimensions   | Rate?
        Small package, actual weight used        | [10, 10, 10] | 5.00
        Large package, dimensional weight used    | [70, 50, 10] | 12.50
        """)
    void appliesDimensionalWeightWhenGreaterThanActual(List<Integer> dimensions, BigDecimal rate) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        BigDecimal result = calculator.calculateShippingCost(
                zone, 1.0, dimensions, new PackageOptions(), Carrier.DHL);
        assertCost(rate, result);
    }

    @Description("""
        Zone is fixed to EU standard, weight fixed to 3.0kg (base rate 7.50),
        so any surcharge is due solely to the oversize dimension threshold (100).
        """)
    @TableTest("""
        Scenario              | Dimensions   | Total Cost?
        At the size limit     | [100, 5, 5]  | 7.50
        Just over the limit   | [101, 5, 5]  | 17.50
        """)
    void appliesOversizeSurchargeWhenAnyDimensionExceedsLimit(List<Integer> dimensions, BigDecimal totalCost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        BigDecimal result = calculator.calculateShippingCost(
                zone, 3.0, dimensions, new PackageOptions(), Carrier.DHL);
        assertCost(totalCost, result);
    }

    @Description("""
        Zone is fixed to EU standard, weight fixed to 3.0kg (base rate 7.50).
        """)
    @TableTest("""
        Scenario                  | Fragile | Total Cost?
        Standard handling         | false   | 7.50
        Fragile surcharge applied | true    | 8.625
        """)
    void appliesFragileSurcharge(boolean fragile, BigDecimal totalCost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        PackageOptions options = new PackageOptions();
        options.setFragile(fragile);
        BigDecimal result = calculator.calculateShippingCost(
                zone, 3.0, DEFAULT_DIMENSIONS, options, Carrier.DHL);
        assertCost(totalCost, result);
    }

    @Description("""
        Zone is fixed to EU standard, weight fixed to 3.0kg (base rate 7.50).
        Premium is 0.6% of insured value, with a $3.00 minimum. Insured values
        of 200 and 500 both resolve to the $3.00 minimum (below and exactly at
        it); 1000 exceeds the minimum, so the computed premium is charged instead.
        """)
    @TableTest("""
        Scenario                             | Insured Value | Total Cost?
        No insurance                         |                | 7.50
        Below minimum premium, minimum charged | 200          | 10.50
        At minimum premium exactly            | 500            | 10.50
        Above minimum, premium charged        | 1000           | 13.50
        """)
    void appliesInsurancePremiumWithMinimum(BigDecimal insuredValue, BigDecimal totalCost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        PackageOptions options = new PackageOptions();
        options.setInsuredValue(insuredValue);
        BigDecimal result = calculator.calculateShippingCost(
                zone, 3.0, DEFAULT_DIMENSIONS, options, Carrier.DHL);
        assertCost(totalCost, result);
    }

    @Description("""
        Zone is fixed to EU standard, weight fixed to 3.0kg (base rate 7.50).
        """)
    @TableTest("""
        Scenario              | Handling | Total Cost?
        No special handling   |          | 7.50
        Hazmat handling       | hazmat   | 15.50
        """)
    void appliesHazmatHandlingFee(String handling, BigDecimal totalCost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        PackageOptions options = new PackageOptions();
        options.setHandling(handling);
        BigDecimal result = calculator.calculateShippingCost(
                zone, 3.0, DEFAULT_DIMENSIONS, options, Carrier.DHL);
        assertCost(totalCost, result);
    }

    @Description("""
        Zone is fixed to EU standard, weight fixed to 3.0kg (base rate 7.50).
        Verifies the order surcharges are combined in: hazmat/oversize fees are
        added to the base first, the fragile multiplier is applied next, and the
        insurance premium is added last (so it is not itself multiplied by the
        fragile rate).
        """)
    @TableTest("""
        Scenario             | Fragile | Handling | Insured Value | Total Cost?
        Fragile and insured  | true    |          | 200           | 11.625
        Fragile and hazmat   | true    | hazmat   |               | 17.825
        """)
    void combinesSurchargesInOrder(boolean fragile, String handling, BigDecimal insuredValue, BigDecimal totalCost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        PackageOptions options = new PackageOptions();
        options.setFragile(fragile);
        options.setHandling(handling);
        options.setInsuredValue(insuredValue);
        BigDecimal result = calculator.calculateShippingCost(
                zone, 3.0, DEFAULT_DIMENSIONS, options, Carrier.DHL);
        assertCost(totalCost, result);
    }

    @Description("""
        Zone is fixed to EU express, weight fixed to 3.0kg (base rate 12.00).
        The carrier parameter does not influence price for any combination of
        zone/weight/options; this table asserts that regardless of carrier.
        """)
    @TableTest("""
        Scenario                          | Carrier              | Total Cost?
        Same zone and weight, any carrier | {DHL, UPS, FEDEX}    | 12.00
        """)
    void carrierDoesNotAffectPrice(Carrier carrier, BigDecimal totalCost) {
        ShippingZone zone = new ShippingZone("EU", "express");
        BigDecimal result = calculator.calculateShippingCost(
                zone, 3.0, DEFAULT_DIMENSIONS, new PackageOptions(), carrier);
        assertCost(totalCost, result);
    }

    private static void assertCost(BigDecimal expected, BigDecimal actual) {
        assertEquals(0, expected.compareTo(actual), () -> "expected " + expected + " but was " + actual);
    }
}
