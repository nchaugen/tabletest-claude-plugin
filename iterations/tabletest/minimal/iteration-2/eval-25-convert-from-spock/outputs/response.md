Here's the converted test, decomposed by concern:

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShippingCostCalculatorTest {

    private final ShippingCostCalculator calculator = new ShippingCostCalculator();

    @DisplayName("Base shipping cost by region, speed, and weight")
    @Description("""
            Standard package dimensions (30 × 20 × 15 cm) used throughout.
            No fragile, insurance, or special handling applied.
            """)
    @TableTest("""
            Scenario               | region | speed    | weight | Cost?
            EU standard light      | EU     | standard | 0.5    | 5.00
            EU standard medium     | EU     | standard | 3.0    | 7.50
            EU standard heavy      | EU     | standard | 10.0   | 12.50
            EU standard very heavy | EU     | standard | 25.0   | 20.00
            EU express light       | EU     | express  | 0.5    | 8.00
            EU express medium      | EU     | express  | 3.0    | 12.00
            US standard light      | US     | standard | 0.5    | 7.00
            US express heavy       | US     | express  | 10.0   | 30.00
            """)
    void baseShippingCost(String region, String speed, double weight, double expectedCost) {
        ShippingZone zone = new ShippingZone();
        zone.region = region;
        zone.speed = speed;
        assertEquals(expectedCost, calculator.calculateShippingCost(
                zone, weight, List.of(30, 20, 15), new PackageOptions(), Carrier.DHL));
    }

    @DisplayName("Carrier does not affect shipping cost")
    @Description("EU express, 3.0 kg, standard dimensions (30 × 20 × 15 cm).")
    @TableTest("""
            Scenario    | carrier           | Cost?
            Any carrier | {DHL, UPS, FEDEX} | 12.00
            """)
    void carrierDoesNotAffectCost(Carrier carrier, double expectedCost) {
        ShippingZone zone = new ShippingZone();
        zone.region = "EU";
        zone.speed = "express";
        assertEquals(expectedCost, calculator.calculateShippingCost(
                zone, 3.0, List.of(30, 20, 15), new PackageOptions(), carrier));
    }

    @DisplayName("Dimensional weight override")
    @Description("""
            When dimensional weight (length × width × height / volumetric divisor)
            exceeds actual weight, dimensional weight is used for pricing.
            EU standard, no surcharges.
            """)
    @TableTest("""
            Scenario                     | weight | length | width | height | Cost?
            Actual weight used           | 3.0    | 30     | 20    | 15     | 7.50
            Dimensional weight overrides | 1.0    | 70     | 50    | 10     | 12.50
            """)
    void dimensionalWeightOverride(double weight, int length, int width, int height, double expectedCost) {
        ShippingZone zone = new ShippingZone();
        zone.region = "EU";
        zone.speed = "standard";
        assertEquals(expectedCost, calculator.calculateShippingCost(
                zone, weight, List.of(length, width, height), new PackageOptions(), Carrier.DHL));
    }

    @DisplayName("Fragile and insurance surcharges")
    @Description("Base: EU standard, 3.0 kg, 30 × 20 × 15 cm.")
    @TableTest("""
            Scenario          | fragile | insuredValue | Cost?
            No surcharges     | false   |              | 7.50
            Fragile           | true    |              | 8.625
            Insured           | false   | 500          | 10.50
            Fragile + insured | true    | 200          | 11.625
            """)
    void fragilAndInsuranceSurcharges(boolean fragile, Integer insuredValue, double expectedCost) {
        ShippingZone zone = new ShippingZone();
        zone.region = "EU";
        zone.speed = "standard";
        PackageOptions opts = new PackageOptions();
        opts.setFragile(fragile);
        if (insuredValue != null) opts.setInsuredValue(insuredValue);
        assertEquals(expectedCost, calculator.calculateShippingCost(
                zone, 3.0, List.of(30, 20, 15), opts, Carrier.DHL));
    }

    @DisplayName("Special handling surcharges")
    @Description("Base: EU standard, 3.0 kg, 30 × 20 × 15 cm.")
    @TableTest("""
            Scenario    | handling | Cost?
            No handling |          | 7.50
            Hazmat      | hazmat   | 15.50
            """)
    void specialHandlingSurcharges(String handling, double expectedCost) {
        ShippingZone zone = new ShippingZone();
        zone.region = "EU";
        zone.speed = "standard";
        PackageOptions opts = new PackageOptions();
        if (handling != null) opts.setHandling(handling);
        assertEquals(expectedCost, calculator.calculateShippingCost(
                zone, 3.0, List.of(30, 20, 15), opts, Carrier.DHL));
    }

    @DisplayName("Oversize surcharge")
    @Description("Base: EU standard, 3.0 kg.")
    @TableTest("""
            Scenario      | length | width | height | Cost?
            Standard size | 30     | 20    | 15     | 7.50
            Oversize      | 120    | 5     | 5      | 17.50
            """)
    void oversizeSurcharge(int length, int width, int height, double expectedCost) {
        ShippingZone zone = new ShippingZone();
        zone.region = "EU";
        zone.speed = "standard";
        assertEquals(expectedCost, calculator.calculateShippingCost(
                zone, 3.0, List.of(length, width, height), new PackageOptions(), Carrier.DHL));
    }
}
```

A few things worth noting about this conversion:

**Decomposition into 6 concerns** — rather than one flat table with 13+ columns (most null per row), each concern gets its own method. This also makes it clear what the code under test actually does.

**`{DHL, UPS, FEDEX}` value set** — the "same rate" rows in the original were explicitly asserting carrier independence. The value set expresses this directly: all three values must produce `12.00`.

**`insuredValue` as `Integer`** — boxed type so a blank cell becomes `null`. The `if (insuredValue != null)` guard in setup is unavoidable given the API. Same for `handling`.

**`@Description` for fixed inputs** — the region, speed, weight, and dimensions that are constant across a concern's rows are documented there rather than polluting the table.