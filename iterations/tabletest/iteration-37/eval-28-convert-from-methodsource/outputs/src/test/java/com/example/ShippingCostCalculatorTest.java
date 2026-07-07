package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShippingCostCalculatorTest {

    private final ShippingCostCalculator calculator = new ShippingCostCalculator();

    @Description("""
        Dimensions are fixed at [10, 10, 10] cm (dimensional weight 0.200kg) and no package
        options are applied, isolating the region/speed/weight rate lookup from dimensional
        weight overrides and surcharges. Carrier is fixed to DHL, which does not affect cost.
        """)
    @TableTest("""
        Scenario                     | Region | Speed    | Weight (kg) | Cost?
        EU standard light            | EU     | standard | 0.5         | 5.00
        EU standard medium           | EU     | standard | 3.0         | 7.50
        EU standard heavy            | EU     | standard | 10          | 12.50
        EU standard very heavy       | EU     | standard | 25          | 20.00
        EU standard at 1kg boundary  | EU     | standard | 1           | 5.00
        EU standard just over 1kg    | EU     | standard | 1.01        | 7.50
        EU standard at 15kg boundary | EU     | standard | 15          | 12.50
        EU standard just over 15kg   | EU     | standard | 15.01       | 20.00
        EU express light             | EU     | express  | 0.5         | 8.00
        EU express medium            | EU     | express  | 3.0         | 12.00
        EU express heavy             | EU     | express  | 10          | 20.00
        EU express very heavy        | EU     | express  | 25          | 32.00
        US standard light            | US     | standard | 0.5         | 7.00
        US standard medium           | US     | standard | 3.0         | 10.50
        US standard heavy            | US     | standard | 10          | 17.50
        US standard very heavy       | US     | standard | 25          | 28.00
        US express light             | US     | express  | 0.5         | 11.00
        US express medium            | US     | express  | 3.0         | 16.50
        US express heavy             | US     | express  | 10          | 30.00
        US express very heavy        | US     | express  | 25          | 45.00
        """)
    void resolvesBaseRateByZoneSpeedAndWeight(String region, String speed, double weight, BigDecimal cost) {
        ShippingZone zone = new ShippingZone(region, speed);
        BigDecimal result = calculator.calculateShippingCost(
            zone, weight, List.of(10, 10, 10), new PackageOptions(), Carrier.DHL);
        assertEquals(0, result.compareTo(cost));
    }

    @Description("""
        Zone is fixed to EU standard with no package options and carrier DHL, isolating how
        dimensional weight (volume / 5000) can override the actual weight used for the rate
        lookup.
        """)
    @TableTest("""
        Scenario                            | Weight (kg) | Dimensions (cm) | Cost?
        Actual weight drives cost           | 10          | [10, 10, 10]    | 12.50
        Dimensional weight crosses a tier   | 1.0         | [70, 50, 10]    | 12.50
        Dimensional weight reaches top tier | 1.0         | [100, 100, 10]  | 20.00
        """)
    void appliesDimensionalWeightOverride(double weight, List<Integer> dimensions, BigDecimal cost) {
        BigDecimal result = calculator.calculateShippingCost(
            new ShippingZone("EU", "standard"), weight, dimensions, new PackageOptions(), Carrier.DHL);
        assertEquals(0, result.compareTo(cost));
    }

    @Description("""
        Zone is fixed to EU standard with weight 3.0kg (base rate 7.50), no fragile or
        insured options, isolating the flat oversize and hazmat surcharges from the
        multiplicative fragile surcharge and the insurance premium.
        """)
    @TableTest("""
        Scenario                     | Dimensions (cm) | Handling | Cost?
        No surcharge                 | [30, 20, 15]     |          | 7.50
        Oversize package              | [120, 5, 5]      |          | 17.50
        Hazmat handling               | [30, 20, 15]     | hazmat   | 15.50
        Oversize and hazmat combined  | [120, 5, 5]      | hazmat   | 25.50
        """)
    void appliesFlatSurcharges(List<Integer> dimensions, String handling, BigDecimal cost) {
        PackageOptions opts = new PackageOptions();
        if (handling != null) opts.setHandling(handling);
        BigDecimal result = calculator.calculateShippingCost(
            new ShippingZone("EU", "standard"), 3.0, dimensions, opts, Carrier.DHL);
        assertEquals(0, result.compareTo(cost));
    }

    @Description("""
        Zone is fixed to EU standard with weight 3.0kg, no insured value. Fragile multiplies
        the base rate plus any flat surcharges already applied (oversize, hazmat), rather
        than the base rate alone.
        """)
    @TableTest("""
        Scenario                              | Dimensions (cm) | Handling | Fragile? | Cost?
        Not fragile                           | [30, 20, 15]     |          | false    | 7.50
        Fragile                               | [30, 20, 15]     |          | true     | 8.625
        Fragile applies after flat surcharges | [120, 5, 5]      | hazmat   | true     | 29.325
        """)
    void appliesFragileMultiplier(List<Integer> dimensions, String handling, boolean fragile, BigDecimal cost) {
        PackageOptions opts = new PackageOptions();
        if (handling != null) opts.setHandling(handling);
        if (fragile) opts.setFragile(true);
        BigDecimal result = calculator.calculateShippingCost(
            new ShippingZone("EU", "standard"), 3.0, dimensions, opts, Carrier.DHL);
        assertEquals(0, result.compareTo(cost));
    }

    @Description("""
        Zone is fixed to EU standard with weight 3.0kg and dimensions [30, 20, 15] cm (base
        rate 7.50), no fragile or hazmat options, isolating the insurance premium (0.6% of
        insured value) from its 3.00 minimum floor.
        """)
    @TableTest("""
        Scenario                     | Insured Value | Cost?
        Premium below minimum        | {200, 499}    | 10.50
        At minimum threshold         | 500           | 10.50
        Just over minimum threshold  | 501           | 10.506
        Premium exceeds minimum      | 1000          | 13.50
        """)
    void appliesInsurancePremiumWithMinimumFloor(BigDecimal insuredValue, BigDecimal cost) {
        PackageOptions opts = new PackageOptions();
        opts.setInsuredValue(insuredValue);
        BigDecimal result = calculator.calculateShippingCost(
            new ShippingZone("EU", "standard"), 3.0, List.of(30, 20, 15), opts, Carrier.DHL);
        assertEquals(0, result.compareTo(cost));
    }

    @Description("""
        Zone is fixed to EU express with weight 3.0kg and dimensions [30, 20, 15] cm.
        Carrier is accepted by the calculator but does not affect the resulting cost.
        """)
    @TableTest("""
        Scenario     | Carrier           | Cost?
        Any carrier  | {DHL, UPS, FEDEX} | 12.00
        """)
    void carrierDoesNotAffectCost(Carrier carrier, BigDecimal cost) {
        BigDecimal result = calculator.calculateShippingCost(
            new ShippingZone("EU", "express"), 3.0, List.of(30, 20, 15), new PackageOptions(), carrier);
        assertEquals(0, result.compareTo(cost));
    }
}
