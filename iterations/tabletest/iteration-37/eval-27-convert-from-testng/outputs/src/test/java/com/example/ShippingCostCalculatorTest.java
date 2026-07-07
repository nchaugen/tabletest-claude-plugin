package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShippingCostCalculatorTest {

    private final ShippingCostCalculator calculator = new ShippingCostCalculator();

    @Description("""
        Carrier is fixed to DHL here since it does not affect price
        (see carrierDoesNotAffectShippingCost).
        """)
    @TableTest("""
        Scenario                     | Region | Speed    | Weight | Dimensions    | Fragile | Insured Value | Handling | Cost?
        EU standard light            | EU     | standard | 0.5    | [20, 15, 10]  | false   |                |          | 5.00
        EU standard medium           | EU     | standard | 3.0    | [30, 20, 15]  | false   |                |          | 7.50
        EU standard heavy            | EU     | standard | 10.0   | [40, 30, 20]  | false   |                |          | 12.50
        EU standard very heavy       | EU     | standard | 25.0   | [50, 40, 30]  | false   |                |          | 20.00
        EU express light             | EU     | express  | 0.5    | [20, 15, 10]  | false   |                |          | 8.00
        EU express medium            | EU     | express  | 3.0    | [30, 20, 15]  | false   |                |          | 12.00
        US standard light            | US     | standard | 0.5    | [20, 15, 10]  | false   |                |          | 7.00
        US express heavy             | US     | express  | 10.0   | [40, 30, 20]  | false   |                |          | 30.00
        Dimensional weight override  | EU     | standard | 1.0    | [70, 50, 10]  | false   |                |          | 12.50
        Fragile surcharge            | EU     | standard | 3.0    | [30, 20, 15]  | true    |                |          | 8.625
        Insured surcharge            | EU     | standard | 3.0    | [30, 20, 15]  | false   | 500            |          | 10.50
        Fragile and insured          | EU     | standard | 3.0    | [30, 20, 15]  | true    | 200            |          | 11.625
        Hazmat handling              | EU     | standard | 3.0    | [30, 20, 15]  | false   |                | hazmat   | 15.50
        Oversize surcharge           | EU     | standard | 3.0    | [120, 5, 5]   | false   |                |          | 17.50
        """)
    void shouldCalculateShippingCost(String region, String speed, double weight, List<Integer> dimensions,
            boolean fragile, BigDecimal insuredValue, String handling, BigDecimal cost) {
        ShippingZone zone = new ShippingZone(region, speed);
        PackageOptions options = new PackageOptions();
        options.setFragile(fragile);
        options.setInsuredValue(insuredValue);
        options.setHandling(handling);

        BigDecimal result = calculator.calculateShippingCost(zone, weight, dimensions, options, Carrier.DHL);

        assertEquals(0, result.compareTo(cost));
    }

    @TableTest("""
        Scenario                       | Carrier            | Cost?
        Carrier does not affect price  | {DHL, UPS, FEDEX}  | 12.00
        """)
    void carrierDoesNotAffectShippingCost(Carrier carrier, BigDecimal cost) {
        ShippingZone zone = new ShippingZone("EU", "express");
        List<Integer> dimensions = List.of(30, 20, 15);
        PackageOptions options = new PackageOptions();

        BigDecimal result = calculator.calculateShippingCost(zone, 3.0, dimensions, options, carrier);

        assertEquals(0, result.compareTo(cost));
    }
}
