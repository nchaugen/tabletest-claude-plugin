Looking at this test, I can identify three distinct concerns to decompose into separate `@TableTest` methods:

1. **Base shipping rate** — zone (region + service level) + weight + carrier → cost (also covers carrier independence with a value set)
2. **Dimensional weight override** — when volumetric weight exceeds actual weight
3. **Package surcharges** — fragile, insurance, hazmat, oversize

The 17 original rows collapse to 10 (base: 8, dim weight: 2, surcharges: 5), with the three "same rate" carrier rows consolidated into a single `{DHL, UPS, FEDEX}` value set row.

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShippingCostCalculatorTest {

    private ShippingCostCalculator calculator = new ShippingCostCalculator();

    @DisplayName("Base shipping rate by zone and weight")
    @TableTest("""
        Scenario               | Region | Service  | Weight | Dimensions    | Carrier           | Cost?
        EU standard light      | EU     | standard | 0.5    | [20, 15, 10]  | DHL               | 5.00
        EU standard medium     | EU     | standard | 3.0    | [30, 20, 15]  | DHL               | 7.50
        EU standard heavy      | EU     | standard | 10.0   | [40, 30, 20]  | DHL               | 12.50
        EU standard very heavy | EU     | standard | 25.0   | [50, 40, 30]  | UPS               | 20.00
        EU express light       | EU     | express  | 0.5    | [20, 15, 10]  | DHL               | 8.00
        EU express medium      | EU     | express  | 3.0    | [30, 20, 15]  | {DHL, UPS, FEDEX} | 12.00
        US standard light      | US     | standard | 0.5    | [20, 15, 10]  | FEDEX             | 7.00
        US express heavy       | US     | express  | 10.0   | [40, 30, 20]  | DHL               | 30.00
        """)
    void baseShippingRate(String region, String service, BigDecimal weight,
                          List<Integer> dims, Carrier carrier, BigDecimal cost) {
        ShippingZone zone = new ShippingZone(region, service);
        PackageOptions opts = new PackageOptions();
        assertEquals(cost, calculator.calculateShippingCost(zone, weight, dims, opts, carrier));
    }

    @DisplayName("Dimensional weight overrides light packages")
    @Description("""
        Dimensional weight = L × W × H / 3500 cm³. The greater of actual weight
        or dimensional weight is used for rating. Both scenarios use EU standard
        zone, DHL carrier.
        """)
    @TableTest("""
        Scenario                     | Actual Weight | Dimensions    | Cost?
        Actual weight used           | 3.0           | [30, 20, 15]  | 7.50
        Dimensional weight overrides | 1.0           | [70, 50, 10]  | 12.50
        """)
    void dimensionalWeightOverride(BigDecimal actualWeight, List<Integer> dims, BigDecimal cost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        PackageOptions opts = new PackageOptions();
        assertEquals(cost, calculator.calculateShippingCost(zone, actualWeight, dims, opts, Carrier.DHL));
    }

    @DisplayName("Package surcharges")
    @Description("""
        All scenarios use EU standard zone, 3.0 kg actual weight, DHL carrier.
        Base cost without surcharges is 7.50.
        """)
    @TableTest("""
        Scenario            | Fragile | Insured Value | Handling | Dimensions    | Cost?
        Fragile             | true    |               |          | [30, 20, 15]  | 8.625
        Insured             | false   | 500           |          | [30, 20, 15]  | 10.50
        Fragile and insured | true    | 200           |          | [30, 20, 15]  | 11.625
        Hazmat              | false   |               | hazmat   | [30, 20, 15]  | 15.50
        Oversize            | false   |               |          | [120, 5, 5]   | 17.50
        """)
    void packageSurcharges(boolean fragile, Integer insuredValue, String handling,
                           List<Integer> dims, BigDecimal cost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        BigDecimal weight = new BigDecimal("3.0");
        PackageOptions opts = new PackageOptions();
        if (fragile) opts.setFragile(true);
        if (insuredValue != null) opts.setInsuredValue(insuredValue);
        if (handling != null) opts.setHandling(handling);
        assertEquals(cost, calculator.calculateShippingCost(zone, weight, dims, opts, Carrier.DHL));
    }
}
```

A few things worth noting:

- **`{DHL, UPS, FEDEX}` value set** on the EU express medium row consolidates the three "same rate" rows from the original. The value set asserts carrier is irrelevant for that zone/weight combination — which is the actual intent of those test cases.
- **`Integer insuredValue`** uses the boxed type so blank cells become `null` rather than causing a primitive conversion error.
- **`BigDecimal`** and **`Carrier`** (enum) convert via JUnit's built-in converters — no `@TypeConverter` needed.
- The `@Description` on `dimensionalWeightOverride` documents the divisor (3500) which is not derivable from the table alone.