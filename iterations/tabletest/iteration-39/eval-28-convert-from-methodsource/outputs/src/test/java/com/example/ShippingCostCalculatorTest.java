package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Carrier never affects cost (see shouldNotVaryCostByCarrier below), so every other
// table below fixes it to Carrier.DHL rather than treating it as a variable input.
class ShippingCostCalculatorTest {

    private final ShippingCostCalculator calculator = new ShippingCostCalculator();

    @Description("""
        Dimensions are kept small ([1, 1, 1]) so volumetric weight never exceeds the
        given weight and the actual weight always drives the rate lookup; dimensional
        weight override is covered separately below.
        """)
    @TableTest("""
        Scenario                     | Region | Speed    | Weight (kg) | Rate?
        EU standard, light           | EU     | standard | 0.5         | 5.00
        EU standard, at 1kg boundary | EU     | standard | 1           | 5.00
        EU standard, medium          | EU     | standard | 3.0         | 7.50
        EU standard, at 5kg boundary | EU     | standard | 5           | 7.50
        EU standard, heavy           | EU     | standard | 10.0        | 12.50
        EU standard, at 15kg boundary| EU     | standard | 15          | 12.50
        EU standard, very heavy      | EU     | standard | 25.0        | 20.00
        EU express, light            | EU     | express  | 0.5         | 8.00
        EU express, medium           | EU     | express  | 3.0         | 12.00
        US standard, light           | US     | standard | 0.5         | 7.00
        US express, heavy            | US     | express  | 10.0        | 30.00
        """)
    void shouldLookUpBaseRateByRegionSpeedAndWeight(String region, String speed, double weight, BigDecimal rate) {
        ShippingZone zone = new ShippingZone(region, speed);
        BigDecimal result = calculator.calculateShippingCost(
            zone, weight, List.of(1, 1, 1), new PackageOptions(), Carrier.DHL);
        assertEquals(0, result.compareTo(rate));
    }

    @TableTest("""
        Scenario                     | Weight (kg) | Dimensions   | Rate?
        Actual weight governs        | 3.0         | [10, 10, 10] | 7.50
        Dimensional weight governs   | 1.0         | [70, 50, 10] | 12.50
        """)
    void shouldUseTheLargerOfActualAndDimensionalWeight(double weight, List<Integer> dimensions, BigDecimal rate) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        BigDecimal result = calculator.calculateShippingCost(
            zone, weight, dimensions, new PackageOptions(), Carrier.DHL);
        assertEquals(0, result.compareTo(rate));
    }

    @Description("""
        Zone is EU standard and weight is 3.0kg throughout (base rate 7.50), so the
        only thing that varies is whether any single dimension exceeds the 100cm
        oversize threshold.
        """)
    @TableTest("""
        Scenario                    | Dimensions  | Total?
        Not oversize                | [30, 20, 15]| 7.50
        At the oversize threshold   | [100, 5, 5] | 7.50
        Oversize                    | [120, 5, 5] | 17.50
        """)
    void shouldAddOversizeSurchargeWhenAnyDimensionExceedsThreshold(List<Integer> dimensions, BigDecimal total) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        BigDecimal result = calculator.calculateShippingCost(
            zone, 3.0, dimensions, new PackageOptions(), Carrier.DHL);
        assertEquals(0, result.compareTo(total));
    }

    @TableTest("""
        Scenario     | Fragile? | Total?
        Not fragile  | false    | 7.50
        Fragile      | true     | 8.625
        """)
    void shouldApplyFragileMultiplier(boolean fragile, BigDecimal total) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        PackageOptions options = options(fragile, null, null);
        BigDecimal result = calculator.calculateShippingCost(
            zone, 3.0, List.of(30, 20, 15), options, Carrier.DHL);
        assertEquals(0, result.compareTo(total));
    }

    @Description("""
        Insurance premium is 0.6% of the insured value, with a $3.00 minimum.
        Insured Value 500 sits exactly at the point where the percentage equals
        the minimum (500 * 0.006 = 3.00).
        """)
    @TableTest("""
        Scenario                     | Insured Value | Total?
        No insurance                 |                | 7.50
        Below minimum, floor applies | 200            | 10.50
        At the minimum               | 500            | 10.50
        Above the minimum            | 1000           | 13.50
        """)
    void shouldAddInsurancePremiumWithMinimum(BigDecimal insuredValue, BigDecimal total) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        PackageOptions options = options(false, insuredValue, null);
        BigDecimal result = calculator.calculateShippingCost(
            zone, 3.0, List.of(30, 20, 15), options, Carrier.DHL);
        assertEquals(0, result.compareTo(total));
    }

    @TableTest("""
        Scenario             | Handling | Total?
        No special handling  |          | 7.50
        Hazmat handling      | hazmat   | 15.50
        """)
    void shouldAddHazmatHandlingFee(String handling, BigDecimal total) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        PackageOptions options = options(false, null, handling);
        BigDecimal result = calculator.calculateShippingCost(
            zone, 3.0, List.of(30, 20, 15), options, Carrier.DHL);
        assertEquals(0, result.compareTo(total));
    }

    @Description("""
        Every row is fragile, to show that the insurance premium (and its $3.00
        floor) is computed on top of the fragile-multiplied base but is itself
        unaffected by the multiplier.
        """)
    @TableTest("""
        Scenario                             | Insured Value | Total?
        Insurance floor applies despite fragile | 200         | 11.625
        Insurance premium exceeds the floor     | 1000        | 14.625
        """)
    void shouldCombineFragileMultiplierWithInsurancePremium(BigDecimal insuredValue, BigDecimal total) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        PackageOptions options = options(true, insuredValue, null);
        BigDecimal result = calculator.calculateShippingCost(
            zone, 3.0, List.of(30, 20, 15), options, Carrier.DHL);
        assertEquals(0, result.compareTo(total));
    }

    @TableTest("""
        Scenario                    | Carrier                | Total?
        Carrier does not affect cost| {DHL, UPS, FEDEX}       | 12.00
        """)
    void shouldNotVaryCostByCarrier(Carrier carrier, BigDecimal total) {
        ShippingZone zone = new ShippingZone("EU", "express");
        BigDecimal result = calculator.calculateShippingCost(
            zone, 3.0, List.of(30, 20, 15), new PackageOptions(), carrier);
        assertEquals(0, result.compareTo(total));
    }

    private static PackageOptions options(boolean fragile, BigDecimal insuredValue, String handling) {
        PackageOptions opts = new PackageOptions();
        opts.setFragile(fragile);
        if (insuredValue != null) opts.setInsuredValue(insuredValue);
        if (handling != null) opts.setHandling(handling);
        return opts;
    }
}
