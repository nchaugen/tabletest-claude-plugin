package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShippingCostCalculatorTest {

    private final ShippingCostCalculator calculator = new ShippingCostCalculator();

    @DisplayName("Calculates the base rate from zone and weight tier")
    @Description("""
        Dimensions fixed at 1x1x1 cm so the billable weight always equals the actual
        weight (billable weight is covered separately, see
        chargesByDimensionalWeightWhenItExceedsActual). No fragile, insurance, or
        hazmat options. Carrier fixed at DHL — price does not depend on carrier
        (see chargesSameRegardlessOfCarrier).
        """)
    @TableTest("""
        Scenario                            | Zone         | Weight (kg) | Base Rate?
        EU standard, up to 1 kg              | EU/standard  | {0.5, 1}    | 5.00
        EU standard, over 1 kg up to 5 kg    | EU/standard  | {1.01, 5}   | 7.50
        EU standard, over 5 kg up to 15 kg   | EU/standard  | {5.01, 15}  | 12.50
        EU standard, over 15 kg              | EU/standard  | {15.01, 25} | 20.00
        EU express, up to 1 kg               | EU/express   | {0.5, 1}    | 8.00
        EU express, over 1 kg up to 5 kg     | EU/express   | {1.01, 5}   | 12.00
        EU express, over 5 kg up to 15 kg    | EU/express   | {5.01, 15}  | 20.00
        EU express, over 15 kg               | EU/express   | {15.01, 25} | 32.00
        US standard, up to 1 kg              | US/standard  | {0.5, 1}    | 7.00
        US standard, over 1 kg up to 5 kg    | US/standard  | {1.01, 5}   | 10.50
        US standard, over 5 kg up to 15 kg   | US/standard  | {5.01, 15}  | 17.50
        US standard, over 15 kg              | US/standard  | {15.01, 25} | 28.00
        US express, up to 1 kg               | US/express   | {0.5, 1}    | 11.00
        US express, over 1 kg up to 5 kg     | US/express   | {1.01, 5}   | 16.50
        US express, over 5 kg up to 15 kg    | US/express   | {5.01, 15}  | 30.00
        US express, over 15 kg               | US/express   | {15.01, 25} | 45.00
        """)
    void calculatesBaseRateByZoneAndWeight(ShippingZone zone, double weight, BigDecimal baseRate) {
        BigDecimal result = calculator.calculateShippingCost(
                zone, weight, List.of(1, 1, 1), new PackageOptions(), Carrier.DHL);
        assertEquals(0, result.compareTo(baseRate));
    }

    @DisplayName("Charges by dimensional weight when it exceeds the actual weight")
    @Description("""
        Zone fixed at EU standard, no other options. Billable weight is the greater
        of the actual weight and the dimensional weight (length x width x height / 5000).
        """)
    @TableTest("""
        Scenario                                     | Weight (kg) | Dimensions   | Total Cost?
        Actual weight exceeds the dimensional weight  | 12          | [10, 10, 10] | 12.50
        Dimensional weight exceeds the actual weight  | 1           | [40, 30, 5]  | 7.50
        """)
    void chargesByDimensionalWeightWhenItExceedsActual(double weight, List<Integer> dimensions, BigDecimal totalCost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        BigDecimal result = calculator.calculateShippingCost(
                zone, weight, dimensions, new PackageOptions(), Carrier.DHL);
        assertEquals(0, result.compareTo(totalCost));
    }

    @DisplayName("Adds an oversize fee when a dimension exceeds the threshold")
    @Description("Zone fixed at EU standard, 3 kg (base rate $7.50), no other options.")
    @TableTest("""
        Scenario                            | Dimensions   | Total Cost?
        At the oversize threshold (100 cm)  | [100, 5, 5]  | 7.50
        Just over the threshold (101 cm)    | [101, 5, 5]  | 17.50
        """)
    void addsOversizeFeeForDimensionsOverThreshold(List<Integer> dimensions, BigDecimal totalCost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        BigDecimal result = calculator.calculateShippingCost(
                zone, 3.0, dimensions, new PackageOptions(), Carrier.DHL);
        assertEquals(0, result.compareTo(totalCost));
    }

    @DisplayName("Applies a 15% multiplier for fragile packages")
    @Description("Zone fixed at EU standard, 3 kg, dimensions 10x10x10 cm (base rate $7.50).")
    @TableTest("""
        Scenario     | Options         | Total Cost?
        Not fragile  | [:]             | 7.50
        Fragile      | [fragile: true] | 8.625
        """)
    void appliesFragileMultiplier(PackageOptions options, BigDecimal totalCost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        BigDecimal result = calculator.calculateShippingCost(
                zone, 3.0, List.of(10, 10, 10), options, Carrier.DHL);
        assertEquals(0, result.compareTo(totalCost));
    }

    @DisplayName("Charges the greater of the computed premium or the insurance minimum")
    @Description("""
        Zone fixed at EU standard, 3 kg, dimensions 10x10x10 cm (base rate $7.50).
        Insurance premium is 0.6% of the insured value, with a $3.00 minimum.
        """)
    @TableTest("""
        Scenario                    | Options              | Total Cost?
        No insurance declared       | [:]                  | 7.50
        Premium at the minimum      | [insuredValue: 500]  | 10.50
        Premium above the minimum   | [insuredValue: 600]  | 11.10
        """)
    void addsInsuranceSurchargeAboveMinimum(PackageOptions options, BigDecimal totalCost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        BigDecimal result = calculator.calculateShippingCost(
                zone, 3.0, List.of(10, 10, 10), options, Carrier.DHL);
        assertEquals(0, result.compareTo(totalCost));
    }

    @DisplayName("Adds a flat fee for hazmat handling, ignoring other handling instructions")
    @Description("Zone fixed at EU standard, 3 kg, dimensions 10x10x10 cm (base rate $7.50).")
    @TableTest("""
        Scenario                          | Options               | Total Cost?
        No handling instruction           | [:]                   | 7.50
        Non-hazmat handling instruction   | [handling: priority]  | 7.50
        Hazmat handling instruction       | [handling: hazmat]    | 15.50
        """)
    void addsHazmatHandlingFee(PackageOptions options, BigDecimal totalCost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        BigDecimal result = calculator.calculateShippingCost(
                zone, 3.0, List.of(10, 10, 10), options, Carrier.DHL);
        assertEquals(0, result.compareTo(totalCost));
    }

    @DisplayName("Adds flat fees before the fragile multiplier, then adds insurance last")
    @Description("""
        Zone fixed at EU standard, 3 kg, dimensions 10x10x10 cm (base rate $7.50). Shows
        an interaction neither single-rule table above shows: flat fees (oversize,
        hazmat) are added to the base before the fragile multiplier is applied, while
        insurance is added afterward, on top of the multiplied total.
        """)
    @TableTest("""
        Scenario                                           | Options                              | Total Cost?
        Hazmat fee is added before the fragile multiplier  | [handling: hazmat, fragile: true]    | 17.825
        Insurance is added after the fragile multiplier    | [fragile: true, insuredValue: 200]   | 11.625
        """)
    void combinesSurchargesInFixedOrder(PackageOptions options, BigDecimal totalCost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        BigDecimal result = calculator.calculateShippingCost(
                zone, 3.0, List.of(10, 10, 10), options, Carrier.DHL);
        assertEquals(0, result.compareTo(totalCost));
    }

    @DisplayName("Charges the same price regardless of carrier")
    @Description("Zone fixed at EU express, 3 kg, dimensions 30x20x15 cm (base rate $12.00), no other options.")
    @TableTest("""
        Scenario                       | Carrier            | Total Cost?
        Same price for every carrier   | {DHL, UPS, FEDEX}  | 12.00
        """)
    void chargesSameRegardlessOfCarrier(Carrier carrier, BigDecimal totalCost) {
        ShippingZone zone = new ShippingZone("EU", "express");
        BigDecimal result = calculator.calculateShippingCost(
                zone, 3.0, List.of(30, 20, 15), new PackageOptions(), carrier);
        assertEquals(0, result.compareTo(totalCost));
    }

    @TypeConverter
    public static ShippingZone parseZone(String value) {
        String[] parts = value.split("/");
        return new ShippingZone(parts[0], parts[1]);
    }

    @TypeConverter
    public static PackageOptions parsePackageOptions(Map<String, String> config) {
        PackageOptions options = new PackageOptions();
        if (Boolean.parseBoolean(config.getOrDefault("fragile", "false"))) {
            options.setFragile(true);
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
