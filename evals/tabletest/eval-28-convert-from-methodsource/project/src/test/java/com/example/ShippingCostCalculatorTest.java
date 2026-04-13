package com.example;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShippingCostCalculatorTest {

    private final ShippingCostCalculator calculator = new ShippingCostCalculator();

    static Stream<Arguments> shippingData() {
        return Stream.of(
            Arguments.of("EU standard light",
                new ShippingZone("EU", "standard"), 0.5,
                List.of(20, 15, 10), false, null, null,
                Carrier.DHL, new BigDecimal("5.00")),
            Arguments.of("EU standard medium",
                new ShippingZone("EU", "standard"), 3.0,
                List.of(30, 20, 15), false, null, null,
                Carrier.DHL, new BigDecimal("7.50")),
            Arguments.of("EU standard heavy",
                new ShippingZone("EU", "standard"), 10.0,
                List.of(40, 30, 20), false, null, null,
                Carrier.DHL, new BigDecimal("12.50")),
            Arguments.of("EU standard very heavy",
                new ShippingZone("EU", "standard"), 25.0,
                List.of(50, 40, 30), false, null, null,
                Carrier.UPS, new BigDecimal("20.00")),
            Arguments.of("EU express light",
                new ShippingZone("EU", "express"), 0.5,
                List.of(20, 15, 10), false, null, null,
                Carrier.DHL, new BigDecimal("8.00")),
            Arguments.of("EU express medium",
                new ShippingZone("EU", "express"), 3.0,
                List.of(30, 20, 15), false, null, null,
                Carrier.UPS, new BigDecimal("12.00")),
            Arguments.of("US standard light",
                new ShippingZone("US", "standard"), 0.5,
                List.of(20, 15, 10), false, null, null,
                Carrier.FEDEX, new BigDecimal("7.00")),
            Arguments.of("US express heavy",
                new ShippingZone("US", "express"), 10.0,
                List.of(40, 30, 20), false, null, null,
                Carrier.DHL, new BigDecimal("30.00")),
            Arguments.of("dimensional weight override",
                new ShippingZone("EU", "standard"), 1.0,
                List.of(70, 50, 10), false, null, null,
                Carrier.DHL, new BigDecimal("12.50")),
            Arguments.of("fragile surcharge",
                new ShippingZone("EU", "standard"), 3.0,
                List.of(30, 20, 15), true, null, null,
                Carrier.DHL, new BigDecimal("8.625")),
            Arguments.of("insured surcharge",
                new ShippingZone("EU", "standard"), 3.0,
                List.of(30, 20, 15), false, new BigDecimal("500"), null,
                Carrier.DHL, new BigDecimal("10.50")),
            Arguments.of("fragile and insured",
                new ShippingZone("EU", "standard"), 3.0,
                List.of(30, 20, 15), true, new BigDecimal("200"), null,
                Carrier.DHL, new BigDecimal("11.625")),
            Arguments.of("hazmat handling",
                new ShippingZone("EU", "standard"), 3.0,
                List.of(30, 20, 15), false, null, "hazmat",
                Carrier.DHL, new BigDecimal("15.50")),
            Arguments.of("oversize surcharge",
                new ShippingZone("EU", "standard"), 3.0,
                List.of(120, 5, 5), false, null, null,
                Carrier.DHL, new BigDecimal("17.50")),
            Arguments.of("same rate DHL",
                new ShippingZone("EU", "express"), 3.0,
                List.of(30, 20, 15), false, null, null,
                Carrier.DHL, new BigDecimal("12.00")),
            Arguments.of("same rate UPS",
                new ShippingZone("EU", "express"), 3.0,
                List.of(30, 20, 15), false, null, null,
                Carrier.UPS, new BigDecimal("12.00")),
            Arguments.of("same rate FEDEX",
                new ShippingZone("EU", "express"), 3.0,
                List.of(30, 20, 15), false, null, null,
                Carrier.FEDEX, new BigDecimal("12.00"))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("shippingData")
    void testShippingCost(String scenario, ShippingZone zone,
            double weight, List<Integer> dimensions, boolean fragile,
            BigDecimal insuredValue, String handling,
            Carrier carrier, BigDecimal expected) {
        PackageOptions opts = new PackageOptions();
        if (fragile) opts.setFragile(true);
        if (insuredValue != null) opts.setInsuredValue(insuredValue);
        if (handling != null) opts.setHandling(handling);
        BigDecimal result = calculator.calculateShippingCost(zone, weight, dimensions, opts, carrier);
        assertEquals(0, result.compareTo(expected));
    }
}
