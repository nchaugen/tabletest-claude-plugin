The types are all JUnit-native (`List<Integer>`, enums, primitives) — no custom converters needed. I'll split into two tables by concern: base rates and package surcharges. This also lets me compress the three carrier-parity rows into a single value-set row.

Here's the converted test:

```java
import org.junit.jupiter.api.TestInstance;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.Description;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ShippingCostCalculatorTest {

    ShippingCostCalculator calculator = new ShippingCostCalculator();

    @TableTest("""
        Scenario                | Region | Speed    | Weight | Dimensions    | Carrier           | Cost?
        EU standard, light      | EU     | standard | 0.5    | [20, 15, 10]  | DHL               | 5.00
        EU standard, medium     | EU     | standard | 3.0    | [30, 20, 15]  | DHL               | 7.50
        EU standard, heavy      | EU     | standard | 10.0   | [40, 30, 20]  | DHL               | 12.50
        EU standard, very heavy | EU     | standard | 25.0   | [50, 40, 30]  | UPS               | 20.00
        EU express, light       | EU     | express  | 0.5    | [20, 15, 10]  | DHL               | 8.00
        EU express, medium      | EU     | express  | 3.0    | [30, 20, 15]  | UPS               | 12.00
        US standard, light      | US     | standard | 0.5    | [20, 15, 10]  | FEDEX             | 7.00
        US express, heavy       | US     | express  | 10.0   | [40, 30, 20]  | DHL               | 30.00
        Dimensional weight      | EU     | standard | 1.0    | [70, 50, 10]  | DHL               | 12.50
        Carrier parity          | EU     | express  | 3.0    | [30, 20, 15]  | {DHL, UPS, FEDEX} | 12.00
        """)
    void shouldCalculateBaseShippingRate(String region, String speed, double weight,
                                          List<Integer> dimensions, Carrier carrier, double cost) {
        var zone = new ShippingZone();
        zone.region = region;
        zone.speed = speed;
        assertEquals(cost, calculator.calculateShippingCost(zone, weight, dimensions, new PackageOptions(), carrier), 0.001);
    }

    @Description("All rows use EU standard zone, weight 3.0 kg, carrier DHL.")
    @TableTest("""
        Scenario             | Fragile | Insured Value | Handling | Dimensions   | Cost?
        No surcharges        | false   |               |          | [30, 20, 15] | 7.50
        Fragile              | true    |               |          | [30, 20, 15] | 8.625
        Insured              | false   | 500           |          | [30, 20, 15] | 10.50
        Fragile and insured  | true    | 200           |          | [30, 20, 15] | 11.625
        Hazmat handling      | false   |               | hazmat   | [30, 20, 15] | 15.50
        Oversize             | false   |               |          | [120, 5, 5]  | 17.50
        """)
    void shouldApplyPackageSurcharges(boolean fragile, Integer insuredValue, String handling,
                                       List<Integer> dimensions, double cost) {
        var zone = new ShippingZone();
        zone.region = "EU";
        zone.speed = "standard";
        var opts = new PackageOptions();
        if (fragile) opts.setFragile(true);
        if (insuredValue != null) opts.setInsuredValue(insuredValue);
        if (handling != null) opts.setHandling(handling);
        assertEquals(cost, calculator.calculateShippingCost(zone, 3.0, dimensions, opts, Carrier.DHL), 0.001);
    }
}
```

**Key decisions:**

- **Two tables** — base rates and surcharges are different concerns. Base rates vary region/speed/weight; surcharges vary `PackageOptions` against a fixed zone. Keeping them separate makes each table's logic easier to read.
- **Value set for carrier parity** — `{DHL, UPS, FEDEX}` replaces the three near-identical Spock rows (same rate DHL/UPS/FEDEX). Each value generates a separate test asserting the same cost.
- **`List<Integer>` for dimensions** — `[20, 15, 10]` syntax parses via JUnit's built-in collection conversion; no `@TypeConverter` needed.
- **Carrier enum** — JUnit converts enum values by name automatically (`DHL` → `Carrier.DHL`).
- **`double` with delta** — matches the Spock test's use of doubles; `assertEquals(..., 0.001)` handles floating-point precision.

If TableTest isn't yet in your build, add `org.tabletest:tabletest-junit:VERSION` as a test dependency.