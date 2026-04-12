Looking at the test, I can identify two distinct concerns:

1. **Base shipping cost** — zone, weight, dimensions, and carrier drive the base cost (including dimensional weight logic)
2. **Surcharges** — fragile, insured value, hazmat handling, and oversize dimensions add to a fixed base

The "same rate" rows (DHL/UPS/FEDEX all giving 12.00 for EU express medium) collapse neatly into a value set on the `Carrier` column.

```java
import org.tabletest.junit.TableTest;

class ShippingCostCalculatorTest {

    private final ShippingCostCalculator calculator = new ShippingCostCalculator();

    @DisplayName("Base shipping cost by zone and weight")
    @TableTest("""
        Scenario                    | Region | Service  | Weight | Dimensions    | Carrier           | Cost?
        EU standard light           | EU     | standard | 0.5    | [20, 15, 10]  | DHL               | 5.00
        EU standard medium          | EU     | standard | 3.0    | [30, 20, 15]  | DHL               | 7.50
        EU standard heavy           | EU     | standard | 10.0   | [40, 30, 20]  | DHL               | 12.50
        EU standard very heavy      | EU     | standard | 25.0   | [50, 40, 30]  | UPS               | 20.00
        EU express light            | EU     | express  | 0.5    | [20, 15, 10]  | DHL               | 8.00
        EU express medium           | EU     | express  | 3.0    | [30, 20, 15]  | {DHL, UPS, FEDEX} | 12.00
        US standard light           | US     | standard | 0.5    | [20, 15, 10]  | FEDEX             | 7.00
        US express heavy            | US     | express  | 10.0   | [40, 30, 20]  | DHL               | 30.00
        Dimensional weight override | EU     | standard | 1.0    | [70, 50, 10]  | DHL               | 12.50
        """)
    void baseShippingCost(String region, String service, BigDecimal weight,
            List<Integer> dimensions, Carrier carrier, BigDecimal expectedCost) {
        ShippingZone zone = new ShippingZone(region, service);
        BigDecimal result = calculator.calculateShippingCost(zone, weight, dimensions, new PackageOptions(), carrier);
        assertEquals(expectedCost, result);
    }

    @DisplayName("Surcharges for package options and physical dimensions")
    @Description("""
        Fixed inputs for all rows: EU standard zone, 3.0 kg actual weight, carrier DHL.
        Base cost without surcharges is 7.50.
        """)
    @TableTest("""
        Scenario            | Fragile | Insured Value | Handling | Dimensions    | Cost?
        No surcharges       | false   |               |          | [30, 20, 15]  | 7.50
        Fragile             | true    |               |          | [30, 20, 15]  | 8.625
        Insured (500)       | false   | 500           |          | [30, 20, 15]  | 10.50
        Fragile and insured | true    | 200           |          | [30, 20, 15]  | 11.625
        Hazmat handling     | false   |               | hazmat   | [30, 20, 15]  | 15.50
        Oversize dimension  | false   |               |          | [120, 5, 5]   | 17.50
        """)
    void surcharges(boolean fragile, Integer insuredValue, String handling,
            List<Integer> dimensions, BigDecimal expectedCost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        BigDecimal result = calculator.calculateShippingCost(zone, new BigDecimal("3.0"), dimensions,
                packageOptions(fragile, insuredValue, handling), Carrier.DHL);
        assertEquals(expectedCost, result);
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

Key decisions:

- **`{DHL, UPS, FEDEX}` value set** on the EU express medium row replaces the three "same rate" rows — it asserts carrier is irrelevant for that scenario, which is the actual intent of those tests
- **`Region` + `Service` as string columns** instead of a `@TypeConverter` for `ShippingZone` — the object construction (`new ShippingZone(region, service)`) is arrangement, not parsing logic
- **`packageOptions()` helper** keeps the test method free of `if`/`null` checks while preserving the original defensive-setter pattern; `insuredValue` uses `Integer` (boxed) so blank cells come through as `null`
- **`@Description`** on the surcharge table documents the fixed inputs (zone, weight, carrier) that are hardcoded in the method body and invisible in the table; the base row in that table makes the 7.50 baseline explicit so surcharge amounts are directly traceable