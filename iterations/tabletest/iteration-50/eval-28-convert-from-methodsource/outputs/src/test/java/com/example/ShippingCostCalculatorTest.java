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

    @DisplayName("Resolves the base shipping rate from zone and weight")
    @Description("""
        Dimensions are fixed at 10x10x10mm - small enough that dimensional weight
        (0.2kg) never exceeds actual weight, so effective weight always equals the
        input weight. Package carries no fragile, insured, or hazmat options;
        carrier is DHL (see computesCostIndependentOfCarrier for carrier's lack of
        effect on cost).
        """)
    @TableTest("""
        Scenario           | Zone         | Weight (kg) | Base Rate?
        Up to 1kg           | EU standard | {0.5, 1}    | 5.00
        Over 1kg to 5kg      | EU standard | {1.5, 5}    | 7.50
        Over 5kg to 15kg     | EU standard | {6, 15}     | 12.50
        Over 15kg            | EU standard | {16, 25}    | 20.00
        Up to 1kg            | EU express  | {0.5, 1}    | 8.00
        Over 1kg to 5kg      | EU express  | {1.5, 5}    | 12.00
        Over 5kg to 15kg     | EU express  | {6, 15}     | 20.00
        Over 15kg            | EU express  | {16, 25}    | 32.00
        Up to 1kg            | US standard | {0.5, 1}    | 7.00
        Over 1kg to 5kg      | US standard | {1.5, 5}    | 10.50
        Over 5kg to 15kg     | US standard | {6, 15}     | 17.50
        Over 15kg            | US standard | {16, 25}    | 28.00
        Up to 1kg            | US express  | {0.5, 1}    | 11.00
        Over 1kg to 5kg      | US express  | {1.5, 5}    | 16.50
        Over 5kg to 15kg     | US express  | {6, 15}     | 30.00
        Over 15kg            | US express  | {16, 25}    | 45.00
        """)
    void resolvesBaseRateByZoneAndWeight(ShippingZone zone, double weight, BigDecimal baseRate) {
        List<Integer> dimensions = List.of(10, 10, 10);
        PackageOptions options = new PackageOptions();
        BigDecimal result = calculator.calculateShippingCost(zone, weight, dimensions, options, Carrier.DHL);
        assertEquals(0, result.compareTo(baseRate));
    }

    @DisplayName("Uses whichever of actual or dimensional weight is greater")
    @Description("""
        Zone is fixed at EU standard; package carries no fragile, insured, or
        hazmat options; carrier is DHL. Both rows resolve to the same effective
        weight (3.0kg) by different routes, confirming the correct operand wins
        in each direction.
        """)
    @TableTest("""
        Scenario                           | Weight (kg) | Dimensions (mm) | Total Cost?
        Actual weight exceeds dimensional   | 3.0         | [10, 10, 10]    | 7.50
        Dimensional weight exceeds actual   | 1.0         | [50, 30, 10]    | 7.50
        """)
    void selectsGreaterOfActualOrDimensionalWeight(double weight, List<Integer> dimensions, BigDecimal totalCost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        PackageOptions options = new PackageOptions();
        BigDecimal result = calculator.calculateShippingCost(zone, weight, dimensions, options, Carrier.DHL);
        assertEquals(0, result.compareTo(totalCost));
    }

    @DisplayName("Adds a flat surcharge when any dimension exceeds 100mm")
    @Description("""
        Zone is fixed at EU standard and weight at 3.0kg (base rate 7.50, per
        resolvesBaseRateByZoneAndWeight), so the only source of cost change is
        the oversize surcharge. Package carries no fragile, insured, or hazmat
        options; carrier is DHL.
        """)
    @TableTest("""
        Scenario                              | Dimensions (mm)  | Total Cost?
        At the oversize threshold (100mm)      | [100, 5, 5]      | 7.50
        Just past the oversize threshold (101mm)| [101, 5, 5]      | 17.50
        """)
    void addsOversizeSurchargeForLongDimensions(List<Integer> dimensions, BigDecimal totalCost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        PackageOptions options = new PackageOptions();
        BigDecimal result = calculator.calculateShippingCost(zone, 3.0, dimensions, options, Carrier.DHL);
        assertEquals(0, result.compareTo(totalCost));
    }

    @DisplayName("Applies package option surcharges to the base shipping cost")
    @Description("""
        Zone is fixed at EU standard, weight at 3.0kg, dimensions at
        30x20x15mm (base rate 7.50, non-oversize); carrier is DHL. Rows
        combining fragile with insurance or hazmat show ordering: the fragile
        multiplier applies to the hazmat surcharge but not to the insurance
        premium, which is added afterward.
        """)
    @TableTest("""
        Scenario                          | Options                             | Total Cost?
        No additional options              | [:]                                 | 7.50
        Fragile package                    | [fragile: true]                     | 8.625
        Handling other than hazmat         | [handling: signature]               | 7.50
        Hazmat handling                    | [handling: hazmat]                  | 15.50
        Insured at the premium/minimum threshold | [insuredValue: 500]           | 10.50
        Insured above the premium/minimum threshold | [insuredValue: 501]        | 10.506
        Fragile and insured package        | [fragile: true, insuredValue: 200]  | 11.625
        Fragile package with hazmat handling | [fragile: true, handling: hazmat] | 17.825
        """)
    void appliesPackageOptionSurcharges(PackageOptions options, BigDecimal totalCost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        List<Integer> dimensions = List.of(30, 20, 15);
        BigDecimal result = calculator.calculateShippingCost(zone, 3.0, dimensions, options, Carrier.DHL);
        assertEquals(0, result.compareTo(totalCost));
    }

    @DisplayName("Computes shipping cost independent of carrier")
    @TableTest("""
        Scenario                     | Carrier              | Total Cost?
        Any carrier, same package     | {DHL, UPS, FEDEX}    | 12.00
        """)
    void computesCostIndependentOfCarrier(Carrier carrier, BigDecimal totalCost) {
        ShippingZone zone = new ShippingZone("EU", "express");
        List<Integer> dimensions = List.of(30, 20, 15);
        PackageOptions options = new PackageOptions();
        BigDecimal result = calculator.calculateShippingCost(zone, 3.0, dimensions, options, carrier);
        assertEquals(0, result.compareTo(totalCost));
    }

    @TypeConverter
    public static ShippingZone parseZone(String value) {
        String[] parts = value.split(" ");
        return new ShippingZone(parts[0], parts[1]);
    }

    @TypeConverter
    public static PackageOptions parseOptions(Map<String, String> config) {
        PackageOptions options = new PackageOptions();
        options.setFragile(Boolean.parseBoolean(config.getOrDefault("fragile", "false")));
        if (config.containsKey("insuredValue")) {
            options.setInsuredValue(new BigDecimal(config.get("insuredValue")));
        }
        options.setHandling(config.get("handling"));
        return options;
    }
}
