package com.example;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;

public class ShippingCostCalculatorTest {

    private ShippingCostCalculator calculator = new ShippingCostCalculator();

    @DataProvider(name = "shippingData")
    public Object[][] shippingData() {
        return new Object[][] {
            { "EU standard light",
              new ShippingZone("EU", "standard"), 0.5,
              new int[]{20, 15, 10}, false, null, null,
              Carrier.DHL, new BigDecimal("5.00") },
            { "EU standard medium",
              new ShippingZone("EU", "standard"), 3.0,
              new int[]{30, 20, 15}, false, null, null,
              Carrier.DHL, new BigDecimal("7.50") },
            { "EU standard heavy",
              new ShippingZone("EU", "standard"), 10.0,
              new int[]{40, 30, 20}, false, null, null,
              Carrier.DHL, new BigDecimal("12.50") },
            { "EU standard very heavy",
              new ShippingZone("EU", "standard"), 25.0,
              new int[]{50, 40, 30}, false, null, null,
              Carrier.UPS, new BigDecimal("20.00") },
            { "EU express light",
              new ShippingZone("EU", "express"), 0.5,
              new int[]{20, 15, 10}, false, null, null,
              Carrier.DHL, new BigDecimal("8.00") },
            { "EU express medium",
              new ShippingZone("EU", "express"), 3.0,
              new int[]{30, 20, 15}, false, null, null,
              Carrier.UPS, new BigDecimal("12.00") },
            { "US standard light",
              new ShippingZone("US", "standard"), 0.5,
              new int[]{20, 15, 10}, false, null, null,
              Carrier.FEDEX, new BigDecimal("7.00") },
            { "US express heavy",
              new ShippingZone("US", "express"), 10.0,
              new int[]{40, 30, 20}, false, null, null,
              Carrier.DHL, new BigDecimal("30.00") },
            { "dimensional weight override",
              new ShippingZone("EU", "standard"), 1.0,
              new int[]{70, 50, 10}, false, null, null,
              Carrier.DHL, new BigDecimal("12.50") },
            { "fragile surcharge",
              new ShippingZone("EU", "standard"), 3.0,
              new int[]{30, 20, 15}, true, null, null,
              Carrier.DHL, new BigDecimal("8.625") },
            { "insured surcharge",
              new ShippingZone("EU", "standard"), 3.0,
              new int[]{30, 20, 15}, false, new BigDecimal("500"), null,
              Carrier.DHL, new BigDecimal("10.50") },
            { "fragile and insured",
              new ShippingZone("EU", "standard"), 3.0,
              new int[]{30, 20, 15}, true, new BigDecimal("200"), null,
              Carrier.DHL, new BigDecimal("11.625") },
            { "hazmat handling",
              new ShippingZone("EU", "standard"), 3.0,
              new int[]{30, 20, 15}, false, null, "hazmat",
              Carrier.DHL, new BigDecimal("15.50") },
            { "oversize surcharge",
              new ShippingZone("EU", "standard"), 3.0,
              new int[]{120, 5, 5}, false, null, null,
              Carrier.DHL, new BigDecimal("17.50") },
            { "same rate DHL",
              new ShippingZone("EU", "express"), 3.0,
              new int[]{30, 20, 15}, false, null, null,
              Carrier.DHL, new BigDecimal("12.00") },
            { "same rate UPS",
              new ShippingZone("EU", "express"), 3.0,
              new int[]{30, 20, 15}, false, null, null,
              Carrier.UPS, new BigDecimal("12.00") },
            { "same rate FEDEX",
              new ShippingZone("EU", "express"), 3.0,
              new int[]{30, 20, 15}, false, null, null,
              Carrier.FEDEX, new BigDecimal("12.00") },
        };
    }

    @Test(dataProvider = "shippingData")
    public void testShippingCost(String scenario, ShippingZone zone,
            double weight, int[] dimensions, boolean fragile,
            BigDecimal insuredValue, String handling,
            Carrier carrier, BigDecimal expected) {
        List<Integer> dims = Arrays.stream(dimensions).boxed().collect(Collectors.toList());
        PackageOptions opts = new PackageOptions();
        if (fragile) opts.setFragile(true);
        if (insuredValue != null) opts.setInsuredValue(insuredValue);
        if (handling != null) opts.setHandling(handling);
        BigDecimal result = calculator.calculateShippingCost(zone, weight, dims, opts, carrier);
        assertEquals(result.compareTo(expected), 0, "Failed for: " + scenario);
    }
}
