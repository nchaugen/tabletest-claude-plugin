Here's the TableTest conversion. I've decomposed the 17 rows into two tables by concern — physical shipping attributes vs. declared package options — and collapsed the three identical "same rate" carrier rows into a value set.

```java
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShippingCostCalculatorTest {

    private final ShippingCostCalculator calculator = new ShippingCostCalculator();

    @Description("Dimensional weight = length × width × height / 5000; used when it exceeds actual weight.")
    @TableTest("""
        Scenario                      | Region | Speed    | Weight | Length | Width | Height | Carrier           | Cost?
        EU standard light             | EU     | standard | 0.5    | 20     | 15    | 10     | DHL               | 5.00
        EU standard medium            | EU     | standard | 3.0    | 30     | 20    | 15     | DHL               | 7.50
        EU standard heavy             | EU     | standard | 10.0   | 40     | 30    | 20     | DHL               | 12.50
        EU standard very heavy        | EU     | standard | 25.0   | 50     | 40    | 30     | UPS               | 20.00
        EU express light              | EU     | express  | 0.5    | 20     | 15    | 10     | DHL               | 8.00
        EU express medium             | EU     | express  | 3.0    | 30     | 20    | 15     | {DHL, UPS, FEDEX} | 12.00
        US standard light             | US     | standard | 0.5    | 20     | 15    | 10     | FEDEX             | 7.00
        US express heavy              | US     | express  | 10.0   | 40     | 30    | 20     | DHL               | 30.00
        Dimensional weight overrides  | EU     | standard | 1.0    | 70     | 50    | 10     | DHL               | 12.50
        Oversize dimension surcharge  | EU     | standard | 3.0    | 120    | 5     | 5      | DHL               | 17.50
        """)
    void shippingCost(String region, String speed, double weight,
                      int length, int width, int height,
                      Carrier carrier, double expectedCost) {
        var zone = new ShippingZone();
        zone.region = region;
        zone.speed = speed;
        assertEquals(expectedCost,
                calculator.calculateShippingCost(zone, weight, List.of(length, width, height), new PackageOptions(), carrier));
    }

    @Description("All rows: EU standard zone, 3.0 kg, 30 × 20 × 15 cm package. Base cost is 7.50.")
    @TableTest("""
        Scenario              | Fragile | Insured Value | Handling | Cost?
        Fragile surcharge     | true    |               |          | 8.625
        Insured surcharge     | false   | 500           |          | 10.50
        Fragile and insured   | true    | 200           |          | 11.625
        Hazmat handling       | false   |               | hazmat   | 15.50
        """)
    void packageOptionSurcharges(boolean fragile, Integer insuredValue, String handling, double expectedCost) {
        var zone = new ShippingZone();
        zone.region = "EU";
        zone.speed = "standard";
        var opts = new PackageOptions();
        if (fragile) opts.setFragile(true);
        if (insuredValue != null) opts.setInsuredValue(insuredValue);
        if (handling != null) opts.setHandling(handling);
        assertEquals(expectedCost,
                calculator.calculateShippingCost(zone, 3.0, List.of(30, 20, 15), opts, Carrier.DHL));
    }
}
```

Key decisions:

- **Two tables instead of one**: `shippingCost` covers how physical attributes (region, speed, weight, dimensions) determine cost. `packageOptionSurcharges` covers how declared options (fragile, insured, hazmat) add to a known base. These are separate rules.

- **Carrier value set** (`{DHL, UPS, FEDEX}`) on the EU express medium row replaces the original three "same rate" rows. The value set expresses the intent directly: carrier has no effect on rate.

- **Oversize and dimensional weight in the first table** — both are triggered by physical dimensions, so they belong with the dimension columns, not with fragile/insured/hazmat surcharges.

- **`Integer insuredValue`** (boxed) lets blank cells become `null` without a custom converter. `String handling` is already nullable.

- **`@Description`** on `packageOptionSurcharges` documents the fixed inputs (EU, standard, 3.0 kg) that don't appear as columns but affect every row's base cost.