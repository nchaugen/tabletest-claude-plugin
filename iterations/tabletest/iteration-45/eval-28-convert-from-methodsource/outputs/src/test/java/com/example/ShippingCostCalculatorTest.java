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

    @DisplayName("Determines base cost from shipping zone, weight, and dimensional weight")
    @Description("""
        Package options are absent (no fragile, insured, or hazmat handling) in every row.
        Carrier is fixed at DHL; carrier choice does not affect cost
        (see costIsUnaffectedByCarrier).
        """)
    @TableTest("""
        Scenario                                  | Zone        | Weight (kg) | Dimensions (cm) | Cost?
        Light package                             | EU standard | 0.5         | [20, 15, 10]    | 5.00
        Medium package                            | EU standard | 3.0         | [30, 20, 15]    | 7.50
        Heavy package                             | EU standard | 10.0        | [40, 30, 20]    | 12.50
        Very heavy package                        | EU standard | 25.0        | [50, 40, 30]    | 20.00
        Light package, express                    | EU express  | 0.5         | [20, 15, 10]    | 8.00
        Medium package, express                   | EU express  | 3.0         | [30, 20, 15]    | 12.00
        Light package, US standard                | US standard | 0.5         | [20, 15, 10]    | 7.00
        Heavy package, US express                 | US express  | 10.0        | [40, 30, 20]    | 30.00
        Dimensional weight exceeds actual weight  | EU standard | 1.0         | [70, 50, 10]    | 12.50
        """)
    void resolvesBaseCost(ShippingZone zone, double weight, List<Integer> dimensions, BigDecimal cost) {
        BigDecimal result = calculator.calculateShippingCost(zone, weight, dimensions, new PackageOptions(), Carrier.DHL);
        assertEquals(0, result.compareTo(cost));
    }

    @DisplayName("Applies fragile, insurance, hazmat, and oversize surcharges to the base cost")
    @Description("""
        All rows use an EU standard zone at 3.0 kg actual weight, where the base cost
        before surcharges is 7.50 (see resolvesBaseCost). Carrier is fixed at DHL and
        does not affect cost (see costIsUnaffectedByCarrier). The fragile-and-insured row
        shows that the fragile multiplier applies to the base cost before the insurance
        premium is added.
        """)
    @TableTest("""
        Scenario             | Dimensions (cm) | Options                            | Cost?
        No surcharges        | [30, 20, 15]    | [:]                                | 7.50
        Fragile surcharge    | [30, 20, 15]    | [fragile: true]                    | 8.625
        Insured surcharge    | [30, 20, 15]    | [insuredValue: 500]                | 10.50
        Fragile and insured  | [30, 20, 15]    | [fragile: true, insuredValue: 200] | 11.625
        Hazmat handling      | [30, 20, 15]    | [handling: hazmat]                 | 15.50
        Oversize dimension   | [120, 5, 5]     | [:]                                | 17.50
        """)
    void appliesSurchargesToBaseCost(List<Integer> dimensions, PackageOptions options, BigDecimal cost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        BigDecimal result = calculator.calculateShippingCost(zone, 3.0, dimensions, options, Carrier.DHL);
        assertEquals(0, result.compareTo(cost));
    }

    @DisplayName("Carrier choice does not affect shipping cost")
    @TableTest("""
        Scenario                     | Zone        | Weight (kg) | Dimensions (cm) | Carrier           | Cost?
        Carrier does not affect cost | EU express   | 3.0        | [30, 20, 15]    | {DHL, UPS, FEDEX} | 12.00
        """)
    void costIsUnaffectedByCarrier(ShippingZone zone, double weight, List<Integer> dimensions, Carrier carrier, BigDecimal cost) {
        BigDecimal result = calculator.calculateShippingCost(zone, weight, dimensions, new PackageOptions(), carrier);
        assertEquals(0, result.compareTo(cost));
    }

    @TypeConverter
    public static ShippingZone parseZone(String value) {
        String[] parts = value.split(" ");
        return new ShippingZone(parts[0], parts[1]);
    }

    @TypeConverter
    public static PackageOptions parseOptions(Map<String, String> config) {
        PackageOptions options = new PackageOptions();
        if ("true".equals(config.get("fragile"))) {
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
