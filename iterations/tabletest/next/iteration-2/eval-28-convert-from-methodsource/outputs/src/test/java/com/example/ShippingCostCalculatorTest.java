package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShippingCostCalculatorTest {

    private final ShippingCostCalculator calculator = new ShippingCostCalculator();

    @Description("""
        Dimensions are fixed at [10, 10, 10] (well below the oversize threshold,
        and its dimensional weight of 0.2kg never exceeds the actual weights used
        here) so the rule under test is purely region + speed + actual weight.
        Dimensional weight override, the oversize fee, and package-option
        surcharges are covered in the dedicated tables below.
        """)
    @TableTest("""
        Scenario                | Region | Speed    | Weight | Base Rate?
        EU standard light       | EU     | standard | 0.5    | 5.00
        EU standard medium      | EU     | standard | 3.0    | 7.50
        EU standard heavy       | EU     | standard | 10.0   | 12.50
        EU standard very heavy  | EU     | standard | 25.0   | 20.00
        EU express light        | EU     | express  | 0.5    | 8.00
        EU express medium       | EU     | express  | 3.0    | 12.00
        US standard light       | US     | standard | 0.5    | 7.00
        US express heavy        | US     | express  | 10.0   | 30.00
        """)
    void calculatesBaseRateByRegionSpeedAndWeight(String region, String speed, double weight, BigDecimal baseRate) {
        ShippingZone zone = new ShippingZone(region, speed);
        BigDecimal result = calculator.calculateShippingCost(zone, weight, List.of(10, 10, 10), new PackageOptions(), Carrier.DHL);
        assertEquals(0, result.compareTo(baseRate));
    }

    @Description("""
        Effective billable weight is the greater of actual weight and dimensional
        weight (volume / 5000). Zone fixed to EU standard since the rate lookup
        itself is covered by calculatesBaseRateByRegionSpeedAndWeight.
        """)
    @TableTest("""
        Scenario                 | Weight | Dimensions   | Total Cost?
        Actual weight wins       | 20.0   | [10, 10, 10] | 20.00
        Dimensional weight wins  | 1.0    | [70, 50, 10] | 12.50
        """)
    void effectiveWeightIsGreaterOfActualOrDimensionalWeight(double weight, List<Integer> dimensions, BigDecimal totalCost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        BigDecimal result = calculator.calculateShippingCost(zone, weight, dimensions, new PackageOptions(), Carrier.DHL);
        assertEquals(0, result.compareTo(totalCost));
    }

    @Description("""
        A flat $10.00 oversize fee applies when any single dimension exceeds
        100 (100 itself is not oversize). Weight fixed at 3.0kg and zone fixed
        to EU standard (base rate 7.50) to isolate this surcharge.
        """)
    @TableTest("""
        Scenario                          | Dimensions   | Total Cost?
        Longest side at threshold         | [100, 5, 5]  | 7.50
        Longest side just over threshold  | [101, 5, 5]  | 17.50
        Large oversize package            | [120, 5, 5]  | 17.50
        """)
    void addsFlatFeeWhenAnyDimensionExceedsOversizeThreshold(List<Integer> dimensions, BigDecimal totalCost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        BigDecimal result = calculator.calculateShippingCost(zone, 3.0, dimensions, new PackageOptions(), Carrier.DHL);
        assertEquals(0, result.compareTo(totalCost));
    }

    @Description("""
        Weight fixed at 3.0kg, dimensions [30, 20, 15], zone EU standard (base
        rate 7.50). The hazmat fee is added before the fragile multiplier; the
        insurance premium is added after. The insurance minimum-premium floor
        is covered separately in insurancePremiumUsesGreaterOfCalculatedPremiumOrMinimum.
        Open question: this table does not exercise hazmat combined with
        fragile or insurance — ordering would follow the same rule but is
        untested here.
        """)
    @TableTest("""
        Scenario             | Options                             | Total Cost?
        No special options   | [:]                                  | 7.50
        Fragile package      | [fragile: true]                      | 8.625
        Insured package       | [insuredValue: 500]                  | 10.50
        Fragile and insured  | [fragile: true, insuredValue: 200]    | 11.625
        Hazmat handling      | [handling: hazmat]                    | 15.50
        """)
    void appliesPackageOptionSurcharges(PackageOptions options, BigDecimal totalCost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        BigDecimal result = calculator.calculateShippingCost(zone, 3.0, List.of(30, 20, 15), options, Carrier.DHL);
        assertEquals(0, result.compareTo(totalCost));
    }

    @Description("""
        Insurance premium is insuredValue x 0.006, floored at a $3.00 minimum.
        Weight fixed at 3.0kg, dimensions [30, 20, 15], zone EU standard (base
        rate 7.50).
        """)
    @TableTest("""
        Scenario                      | Insured Value | Total Cost?
        Premium below minimum floor   | 200           | 10.50
        Premium at minimum threshold  | 500           | 10.50
        Premium above minimum         | 600           | 11.10
        """)
    void insurancePremiumUsesGreaterOfCalculatedPremiumOrMinimum(BigDecimal insuredValue, BigDecimal totalCost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        PackageOptions options = new PackageOptions();
        options.setInsuredValue(insuredValue);
        BigDecimal result = calculator.calculateShippingCost(zone, 3.0, List.of(30, 20, 15), options, Carrier.DHL);
        assertEquals(0, result.compareTo(totalCost));
    }

    @Description("""
        Carrier does not influence price under the current model. Fixed
        inputs elsewhere in this class default to Carrier.DHL since it has
        no effect on the result.
        """)
    @TableTest("""
        Scenario                           | Carrier            | Total Cost?
        Price is the same for all carriers | {DHL, UPS, FEDEX}  | 12.00
        """)
    void totalCostIsIndependentOfCarrier(Carrier carrier, BigDecimal totalCost) {
        ShippingZone zone = new ShippingZone("EU", "express");
        BigDecimal result = calculator.calculateShippingCost(zone, 3.0, List.of(30, 20, 15), new PackageOptions(), carrier);
        assertEquals(0, result.compareTo(totalCost));
    }

    @TypeConverter
    public static PackageOptions parseOptions(Map<String, String> config) {
        PackageOptions options = new PackageOptions();
        if (config == null) {
            return options;
        }
        if (config.containsKey("fragile")) {
            options.setFragile(Boolean.parseBoolean(config.get("fragile")));
        }
        if (config.containsKey("insuredValue")) {
            options.setInsuredValue(new BigDecimal(config.get("insuredValue")));
        }
        if (config.containsKey("handling")) {
            options.setHandling(config.get("handling"));
        }
        return options;
    }
}
