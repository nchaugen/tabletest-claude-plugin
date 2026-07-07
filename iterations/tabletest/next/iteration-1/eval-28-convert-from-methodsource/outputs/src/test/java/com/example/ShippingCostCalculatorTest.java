package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShippingCostCalculatorTest {

    private final ShippingCostCalculator calculator = new ShippingCostCalculator();

    @Description("""
        Dimensions are fixed at 1x1x1mm so the dimensional weight is always
        negligible and the actual weight alone determines the rate tier.
        Carrier does not affect cost (see shouldNotVaryCostByCarrier).
        """)
    @TableTest("""
        Scenario                                | Region | Speed    | Weight | Base Cost?
        EU standard, at 1kg boundary            | EU     | standard | 1.0    | 5.00
        EU standard, just above 1kg boundary    | EU     | standard | 1.01   | 7.50
        EU standard, at 5kg boundary            | EU     | standard | 5.0    | 7.50
        EU standard, just above 5kg boundary    | EU     | standard | 5.01   | 12.50
        EU standard, at 15kg boundary           | EU     | standard | 15.0   | 12.50
        EU standard, just above 15kg boundary   | EU     | standard | 15.01  | 20.00
        EU express, up to 1kg                   | EU     | express  | 0.5    | 8.00
        EU express, up to 5kg                   | EU     | express  | 3.0    | 12.00
        EU express, up to 15kg                  | EU     | express  | 10.0   | 20.00
        EU express, over 15kg                   | EU     | express  | 25.0   | 32.00
        US standard, up to 1kg                  | US     | standard | 0.5    | 7.00
        US standard, up to 5kg                  | US     | standard | 3.0    | 10.50
        US standard, up to 15kg                 | US     | standard | 10.0   | 17.50
        US standard, over 15kg                  | US     | standard | 25.0   | 28.00
        US express, up to 1kg                   | US     | express  | 0.5    | 11.00
        US express, up to 5kg                   | US     | express  | 3.0    | 16.50
        US express, up to 15kg                  | US     | express  | 10.0   | 30.00
        US express, over 15kg                   | US     | express  | 25.0   | 45.00
        """)
    void shouldSelectBaseRateByRegionSpeedAndWeight(String region, String speed, double weight, BigDecimal baseCost) {
        ShippingZone zone = new ShippingZone(region, speed);
        BigDecimal result = calculator.calculateShippingCost(zone, weight, List.of(1, 1, 1), new PackageOptions(), Carrier.DHL);
        assertEquals(0, result.compareTo(baseCost));
    }

    @Description("""
        Zone fixed at EU standard, so the resulting cost maps directly to the
        rate tiers in shouldSelectBaseRateByRegionSpeedAndWeight. Dimensional
        weight is (length * width * height) / 5000.
        """)
    @TableTest("""
        Scenario                            | Weight | Dimensions    | Cost?
        Actual weight is larger             | 10.0   | [10, 10, 10]  | 12.50
        Dimensional weight is larger        | 1.0    | [70, 50, 10]  | 12.50
        Actual and dimensional weight equal | 3.0    | [50, 30, 10]  | 7.50
        """)
    void shouldUseTheLargerOfActualAndDimensionalWeight(double weight, List<Integer> dimensions, BigDecimal cost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        BigDecimal result = calculator.calculateShippingCost(zone, weight, dimensions, new PackageOptions(), Carrier.DHL);
        assertEquals(0, result.compareTo(cost));
    }

    @Description("""
        Zone fixed at EU standard, weight fixed at 3.0kg (base rate 7.50, with
        dimensional weight always below 3.0kg) so only the oversize fee varies
        the cost. The oversize fee applies when any single dimension exceeds
        100mm.
        """)
    @TableTest("""
        Scenario                                     | Dimensions   | Cost?
        Largest dimension within threshold           | [30, 20, 15] | 7.50
        Largest dimension at the 100mm threshold     | [100, 5, 5]  | 7.50
        Largest dimension just over the 100mm threshold | [101, 5, 5] | 17.50
        """)
    void shouldApplyOversizeFeeWhenAnyDimensionExceeds100mm(List<Integer> dimensions, BigDecimal cost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        BigDecimal result = calculator.calculateShippingCost(zone, 3.0, dimensions, new PackageOptions(), Carrier.DHL);
        assertEquals(0, result.compareTo(cost));
    }

    @Description("""
        Base rate is fixed at 7.50 (EU standard, 3.0kg, 30x20x15mm - not
        oversize). Insurance premium is max(insuredValue * 0.006, 3.00) and is
        added after the fragile multiplier, so the premium itself is never
        multiplied by 1.15.
        """)
    @TableTest("""
        Scenario                              | Fragile | Insured Value | Handling | Cost?
        No surcharges                         | false   |               |          | 7.50
        Fragile packaging                     | true    |               |          | 8.625
        Hazmat handling                       | false   |               | hazmat   | 15.50
        Insured, premium below minimum        | false   | 200           |          | 10.50
        Insured, premium at minimum boundary  | false   | 500           |          | 10.50
        Insured, premium above minimum        | false   | 2000          |          | 19.50
        Fragile and insured combined          | true    | 200           |          | 11.625
        """)
    void shouldApplySurchargesOnTopOfBaseRate(boolean fragile, BigDecimal insuredValue, String handling, BigDecimal cost) {
        PackageOptions options = new PackageOptions();
        options.setFragile(fragile);
        options.setInsuredValue(insuredValue);
        options.setHandling(handling);
        ShippingZone zone = new ShippingZone("EU", "standard");
        BigDecimal result = calculator.calculateShippingCost(zone, 3.0, List.of(30, 20, 15), options, Carrier.DHL);
        assertEquals(0, result.compareTo(cost));
    }

    @Description("""
        Zone fixed at EU express, weight fixed at 3.0kg (base rate 12.00, no
        surcharges). Confirms carrier has no effect on cost even though it is
        accepted as a parameter.
        """)
    @TableTest("""
        Scenario                       | Carrier             | Cost?
        Carrier choice does not affect cost | {DHL, UPS, FEDEX} | 12.00
        """)
    void shouldNotVaryCostByCarrier(Carrier carrier, BigDecimal cost) {
        ShippingZone zone = new ShippingZone("EU", "express");
        BigDecimal result = calculator.calculateShippingCost(zone, 3.0, List.of(30, 20, 15), new PackageOptions(), carrier);
        assertEquals(0, result.compareTo(cost));
    }
}
