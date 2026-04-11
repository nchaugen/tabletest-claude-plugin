Now let me analyze the test structure before finalizing the design.

**Analysis:**

- `Carrier` is an **input** to `calculateShippingCost()`, not an output — the test only asserts the cost.
- The test mixes two concerns: base rate calculation (zone/weight/dimensions/carrier → cost) and surcharge calculation (fragile/insured/handling → cost addons).
- Rows 6, 15, 16, 17 all test EU express medium with the same cost (12.00) across different carriers — a perfect value-set opportunity.
- Oversize is dimension-driven (no PackageOptions flags) — belongs with the base rate table.
- `ShippingZone` and `BigDecimal` need `@TypeConverter` methods; `PackageOptions` can be handled cleanly with a map-based converter.

**Splitting into two tables:**

1. **`shouldCalculateBaseShippingRate`** — zone, weight, dimensions, carrier → cost (no surcharges, all default `PackageOptions`)
2. **`shouldApplySurcharges`** — fixed base case (EU standard, 3.0 kg, DHL), varying `PackageOptions` → cost

---

```java
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Description;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShippingCostCalculatorTest {

    private final ShippingCostCalculator calculator = new ShippingCostCalculator();

    @DisplayName("Base shipping rates")
    @TableTest("""
        Scenario                     | Zone        | Weight | Dimensions    | Carrier           | Cost?
        EU standard, light           | EU/standard | 0.5    | [20, 15, 10]  | DHL               | 5.00
        EU standard, medium          | EU/standard | 3.0    | [30, 20, 15]  | DHL               | 7.50
        EU standard, heavy           | EU/standard | 10.0   | [40, 30, 20]  | DHL               | 12.50
        EU standard, very heavy      | EU/standard | 25.0   | [50, 40, 30]  | UPS               | 20.00
        EU express, light            | EU/express  | 0.5    | [20, 15, 10]  | DHL               | 8.00
        EU express, medium           | EU/express  | 3.0    | [30, 20, 15]  | {DHL, UPS, FEDEX} | 12.00
        US standard, light           | US/standard | 0.5    | [20, 15, 10]  | FEDEX             | 7.00
        US express, heavy            | US/express  | 10.0   | [40, 30, 20]  | DHL               | 30.00
        Dimensional weight override  | EU/standard | 1.0    | [70, 50, 10]  | DHL               | 12.50
        Oversize surcharge           | EU/standard | 3.0    | [120, 5, 5]   | DHL               | 17.50
        """)
    void shouldCalculateBaseShippingRate(ShippingZone zone, BigDecimal weight,
            List<Integer> dimensions, Carrier carrier, BigDecimal expectedCost) {
        BigDecimal result = calculator.calculateShippingCost(
                zone, weight, dimensions, new PackageOptions(), carrier);
        assertEquals(expectedCost, result);
    }

    @DisplayName("Package surcharges")
    @Description("""
        All rows use EU standard zone, 3.0 kg, [30, 20, 15], DHL.
        Base cost without surcharges: 7.50.
        """)
    @TableTest("""
        Scenario             | Options                             | Cost?
        No surcharges        | [:]                                 | 7.50
        Fragile              | [fragile: true]                     | 8.625
        Insured              | [insuredValue: 500]                 | 10.50
        Fragile and insured  | [fragile: true, insuredValue: 200]  | 11.625
        Hazmat handling      | [handling: hazmat]                  | 15.50
        """)
    void shouldApplySurcharges(PackageOptions opts, BigDecimal expectedCost) {
        BigDecimal result = calculator.calculateShippingCost(
                new ShippingZone("EU", "standard"), new BigDecimal("3.0"),
                List.of(30, 20, 15), opts, Carrier.DHL);
        assertEquals(expectedCost, result);
    }

    @TypeConverter
    public static ShippingZone parseShippingZone(String value) {
        String[] parts = value.split("/");
        return new ShippingZone(parts[0], parts[1]);
    }

    @TypeConverter
    public static BigDecimal parseBigDecimal(String value) {
        return value == null ? null : new BigDecimal(value);
    }

    @TypeConverter
    public static PackageOptions parsePackageOptions(Map<String, String> props) {
        PackageOptions opts = new PackageOptions();
        if (props == null || props.isEmpty()) return opts;
        if ("true".equals(props.get("fragile"))) opts.setFragile(true);
        if (props.containsKey("insuredValue")) opts.setInsuredValue(Integer.parseInt(props.get("insuredValue")));
        if (props.containsKey("handling")) opts.setHandling(props.get("handling"));
        return opts;
    }
}
```

---

**Key decisions:**

- **Two tables** because zone/weight/dimensions and PackageOptions flags are different concerns. Mixing them would require many blank cells or confuse which inputs drive which outputs.
- **`{DHL, UPS, FEDEX}` value set** on the EU express medium row collapses four original rows (6, 15, 16, 17) into one, explicitly communicating that the rate is carrier-neutral for that zone/weight.
- **Oversize in Table 1** — it's triggered purely by dimensions (no `PackageOptions` flags set), so it belongs alongside other dimension-driven scenarios like dimensional weight override.
- **Map-based `PackageOptions` converter** keeps the surcharge test method body free of conditionals — the converter handles construction from the `[key: value]` map cells, and `[:]` gives a clean default.
- **`parseBigDecimal`** is needed for both `Weight` and `Cost?` columns since JUnit has no built-in `BigDecimal` converter.