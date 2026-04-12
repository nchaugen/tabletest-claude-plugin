Looking at the Spock test, I can identify four distinct concerns:

1. **Base shipping rate** — how region, speed, and weight determine price (8 scenarios)
2. **Dimensional weight override** — large/light packages billed at volumetric weight (1 scenario)
3. **Package option surcharges** — fragile, insurance, hazmat (4 scenarios)
4. **Oversize surcharge** — longest dimension exceeds threshold (1 scenario)

The three "same rate DHL/UPS/FEDEX" rows from the original can be collapsed into a value set on the EU express medium row in the base rate table, asserting carrier-independence directly.

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShippingCostCalculatorTest {

    private final ShippingCostCalculator calculator = new ShippingCostCalculator();

    @DisplayName("Base shipping rate by region, speed, and weight")
    @Description("""
            Package dimensions fixed at 20×15×10 cm for all rows. No package option surcharges applied.
            """)
    @TableTest("""
            Scenario                | Region | Speed    | Weight | Carrier           | Cost?
            EU standard, light      | EU     | standard | 0.5    | DHL               | 5.00
            EU standard, medium     | EU     | standard | 3.0    | DHL               | 7.50
            EU standard, heavy      | EU     | standard | 10.0   | DHL               | 12.50
            EU standard, very heavy | EU     | standard | 25.0   | UPS               | 20.00
            EU express, light       | EU     | express  | 0.5    | DHL               | 8.00
            EU express, medium      | EU     | express  | 3.0    | {DHL, UPS, FEDEX} | 12.00
            US standard, light      | US     | standard | 0.5    | FEDEX             | 7.00
            US express, heavy       | US     | express  | 10.0   | DHL               | 30.00
            """)
    void baseShippingRate(String region, String speed, double weight, Carrier carrier, double expectedCost) {
        ShippingZone zone = new ShippingZone();
        zone.setRegion(region);
        zone.setSpeed(speed);

        assertEquals(expectedCost,
                calculator.calculateShippingCost(zone, weight, List.of(20, 15, 10), new PackageOptions(), carrier),
                0.001);
    }

    @DisplayName("Dimensional weight override")
    @Description("""
            When volumetric weight (L × W × H ÷ divisor) exceeds actual weight,
            the volumetric weight is used for rate tier lookup instead.
            EU region, standard speed, no surcharges.
            """)
    @TableTest("""
            Scenario                       | Weight | Length | Width | Height | Cost?
            Volumetric weight takes effect | 1.0    | 70     | 50    | 10     | 12.50
            """)
    void dimensionalWeightOverride(double weight, int length, int width, int height, double expectedCost) {
        ShippingZone zone = new ShippingZone();
        zone.setRegion("EU");
        zone.setSpeed("standard");

        assertEquals(expectedCost,
                calculator.calculateShippingCost(zone, weight, List.of(length, width, height), new PackageOptions(), Carrier.DHL),
                0.001);
    }

    @DisplayName("Package option surcharges")
    @Description("""
            All rows: EU region, standard speed, 3.0 kg, 30×20×15 cm. Base rate is 7.50.
            """)
    @TableTest("""
            Scenario              | Fragile | Insured value | Handling | Cost?
            Fragile surcharge     | true    |               |          | 8.625
            Insurance surcharge   | false   | 500           |          | 10.50
            Fragile and insured   | true    | 200           |          | 11.625
            Hazmat handling       | false   |               | hazmat   | 15.50
            """)
    void packageOptionSurcharges(boolean fragile, Integer insuredValue, String handling, double expectedCost) {
        ShippingZone zone = new ShippingZone();
        zone.setRegion("EU");
        zone.setSpeed("standard");

        assertEquals(expectedCost,
                calculator.calculateShippingCost(zone, 3.0, List.of(30, 20, 15), packageOptions(fragile, insuredValue, handling), Carrier.DHL),
                0.001);
    }

    @DisplayName("Oversize surcharge")
    @Description("""
            Applied when the longest package dimension exceeds the oversize threshold.
            EU region, standard speed, 3.0 kg, no package option surcharges.
            """)
    @TableTest("""
            Scenario         | Length | Width | Height | Cost?
            Oversize package | 120    | 5     | 5      | 17.50
            """)
    void oversizeSurcharge(int length, int width, int height, double expectedCost) {
        ShippingZone zone = new ShippingZone();
        zone.setRegion("EU");
        zone.setSpeed("standard");

        assertEquals(expectedCost,
                calculator.calculateShippingCost(zone, 3.0, List.of(length, width, height), new PackageOptions(), Carrier.DHL),
                0.001);
    }

    private PackageOptions packageOptions(boolean fragile, Integer insuredValue, String handling) {
        PackageOptions opts = new PackageOptions();
        if (fragile) opts.setFragile(true);
        if (insuredValue != null) opts.setInsuredValue(insuredValue);
        if (handling != null) opts.setHandling(handling);
        return opts;
    }
}
```

A few things worth noting in the conversion:

- **Carrier value set** on `EU express, medium` (`{DHL, UPS, FEDEX}`) replaces the three separate "same rate" rows. It directly asserts the invariant: carrier has no effect on cost for that scenario.
- **`Integer insuredValue`** (boxed) lets blank cells arrive as `null` so the setup helper can skip the setter call, matching the original Spock guard.
- The `packageOptions()` helper keeps the conditional setup out of the test method body, which should contain only arrange-act-assert.
- Single-row tables for dimensional weight and oversize are intentional — they're focused, named concerns that are easy to extend with additional boundary rows later.