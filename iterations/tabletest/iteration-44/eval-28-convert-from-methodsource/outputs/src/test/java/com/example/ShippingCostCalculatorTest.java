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

    @DisplayName("Base shipping rate by zone and weight tier")
    @Description("""
        Dimensions are fixed at [10, 10, 10] cm, which produce a negligible dimensional
        weight (0.2 kg) so the actual weight always applies. No fragile, insurance, or
        hazmat options are set, so cost equals the base rate alone. Carrier is fixed to
        DHL; carrierDoesNotAffectShippingCost confirms carrier choice has no effect.
        Weight tier boundaries (1kg, 5kg, 15kg) are shown for the EU standard zone; the
        other zones each show one representative weight per tier since the same
        thresholds apply uniformly across zones.
        """)
    @TableTest("""
        Scenario                     | Zone        | Weight (kg) | Cost?
        EU standard at 1kg boundary  | EU standard | 1.0         | 5.00
        EU standard just over 1kg    | EU standard | 1.01        | 7.50
        EU standard at 5kg boundary  | EU standard | 5.0         | 7.50
        EU standard just over 5kg    | EU standard | 5.01        | 12.50
        EU standard at 15kg boundary | EU standard | 15.0        | 12.50
        EU standard just over 15kg   | EU standard | 15.01       | 20.00
        EU express light tier        | EU express  | 0.5         | 8.00
        EU express medium tier       | EU express  | 3.0         | 12.00
        EU express heavy tier        | EU express  | 10.0        | 20.00
        EU express very heavy tier   | EU express  | 25.0        | 32.00
        US standard light tier       | US standard | 0.5         | 7.00
        US standard medium tier      | US standard | 3.0         | 10.50
        US standard heavy tier       | US standard | 10.0        | 17.50
        US standard very heavy tier  | US standard | 25.0        | 28.00
        US express light tier        | US express  | 0.5         | 11.00
        US express medium tier       | US express  | 3.0         | 16.50
        US express heavy tier        | US express  | 10.0        | 30.00
        US express very heavy tier   | US express  | 25.0        | 45.00
        """)
    void baseRateByZoneAndWeight(ShippingZone zone, double weight, BigDecimal cost) {
        BigDecimal result = calculator.calculateShippingCost(
            zone, weight, List.of(10, 10, 10), new PackageOptions(), Carrier.DHL);
        assertEquals(0, result.compareTo(cost));
    }

    @DisplayName("Carrier choice does not affect shipping cost")
    @TableTest("""
        Scenario                      | Zone       | Weight (kg) | Dimensions   | Carrier           | Cost?
        Any carrier yields same rate  | EU express | 3.0         | [30, 20, 15] | {DHL, UPS, FEDEX} | 12.00
        """)
    void carrierDoesNotAffectShippingCost(ShippingZone zone, double weight,
            List<Integer> dimensions, Carrier carrier, BigDecimal cost) {
        BigDecimal result = calculator.calculateShippingCost(
            zone, weight, dimensions, new PackageOptions(), carrier);
        assertEquals(0, result.compareTo(cost));
    }

    @DisplayName("Effective weight is the greater of actual and dimensional weight")
    @Description("""
        Dimensional weight = (length x width x height) / 5000. Zone is fixed to EU
        standard and options are defaults; base rate by zone and weight is verified
        separately in baseRateByZoneAndWeight.
        """)
    @TableTest("""
        Scenario                          | Actual Weight (kg) | Dimensions   | Cost?
        Actual weight exceeds dimensional | 3.0                | [10, 10, 10] | 7.50
        Dimensional weight exceeds actual | 1.0                | [70, 50, 10] | 12.50
        """)
    void effectiveWeightUsesGreaterOfActualAndDimensional(
            double actualWeight, List<Integer> dimensions, BigDecimal cost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        BigDecimal result = calculator.calculateShippingCost(
            zone, actualWeight, dimensions, new PackageOptions(), Carrier.DHL);
        assertEquals(0, result.compareTo(cost));
    }

    @DisplayName("Oversize dimensions add a flat surcharge")
    @Description("""
        A package is oversize when any single dimension exceeds 100 cm. Zone is fixed
        to EU standard, weight to 3.0 kg (base rate 7.50), and options are defaults so
        the surcharge is isolated.
        """)
    @TableTest("""
        Scenario                       | Dimensions  | Cost?
        Largest dimension at the limit | [100, 1, 1] | 7.50
        Largest dimension past limit   | [101, 1, 1] | 17.50
        """)
    void oversizeSurcharge(List<Integer> dimensions, BigDecimal cost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        BigDecimal result = calculator.calculateShippingCost(
            zone, 3.0, dimensions, new PackageOptions(), Carrier.DHL);
        assertEquals(0, result.compareTo(cost));
    }

    @DisplayName("Fragile packages incur a 15% surcharge")
    @Description("""
        Zone fixed to EU standard, weight 3.0 kg, dimensions [30, 20, 15]
        (base rate 7.50, no oversize, insurance, or hazmat handling).
        """)
    @TableTest("""
        Scenario     | Options          | Cost?
        Not fragile  | [:]              | 7.50
        Fragile      | [fragile: true]  | 8.625
        """)
    void fragileSurcharge(PackageOptions options, BigDecimal cost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        BigDecimal result = calculator.calculateShippingCost(
            zone, 3.0, List.of(30, 20, 15), options, Carrier.DHL);
        assertEquals(0, result.compareTo(cost));
    }

    @DisplayName("Insurance premium is the greater of 0.6% of insured value and a $3.00 minimum")
    @Description("""
        Zone fixed to EU standard, weight 3.0 kg, dimensions [30, 20, 15]
        (base rate 7.50, no fragile or hazmat surcharge).
        """)
    @TableTest("""
        Scenario                     | Options               | Cost?
        No insurance                | [:]                   | 7.50
        Premium at the $3.00 minimum| [insuredValue: 500]   | 10.50
        Premium above the minimum   | [insuredValue: 2000]  | 19.50
        """)
    void insurancePremium(PackageOptions options, BigDecimal cost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        BigDecimal result = calculator.calculateShippingCost(
            zone, 3.0, List.of(30, 20, 15), options, Carrier.DHL);
        assertEquals(0, result.compareTo(cost));
    }

    @DisplayName("Hazmat handling adds a flat $8.00 fee")
    @Description("""
        Zone fixed to EU standard, weight 3.0 kg, dimensions [30, 20, 15]
        (base rate 7.50, no fragile or insurance surcharge). Only the exact string
        "hazmat" triggers the fee; any other handling value does not.
        """)
    @TableTest("""
        Scenario                  | Options               | Cost?
        No handling specified     | [:]                   | 7.50
        Non-hazmat handling value | [handling: priority]  | 7.50
        Hazmat handling           | [handling: hazmat]    | 15.50
        """)
    void hazmatHandlingFee(PackageOptions options, BigDecimal cost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        BigDecimal result = calculator.calculateShippingCost(
            zone, 3.0, List.of(30, 20, 15), options, Carrier.DHL);
        assertEquals(0, result.compareTo(cost));
    }

    @DisplayName("Fragile surcharge applies to oversize and hazmat fees, but insurance is added afterward")
    @Description("""
        Zone fixed to EU standard, weight 3.0 kg (base rate 7.50). This shows the order
        surcharges combine in - a detail no single-surcharge table above can show on
        its own.
        """)
    @TableTest("""
        Scenario                                       | Dimensions   | Options                              | Cost?
        Fragile multiplies oversize and hazmat fees    | [120, 5, 5]  | [fragile: true, handling: hazmat]    | 29.325
        Insurance is added after the fragile surcharge | [30, 20, 15] | [fragile: true, insuredValue: 1000]  | 14.625
        """)
    void feeOrderingRelativeToFragileAndInsurance(
            List<Integer> dimensions, PackageOptions options, BigDecimal cost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        BigDecimal result = calculator.calculateShippingCost(
            zone, 3.0, dimensions, options, Carrier.DHL);
        assertEquals(0, result.compareTo(cost));
    }

    @TypeConverter
    public static ShippingZone parseShippingZone(String value) {
        String[] parts = value.split(" ");
        return new ShippingZone(parts[0], parts[1]);
    }

    @TypeConverter
    public static PackageOptions parsePackageOptions(Map<String, String> config) {
        PackageOptions options = new PackageOptions();
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
