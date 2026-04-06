Looking at the test, I need to:
1. Handle custom types: `ShippingZone` (needs `@TypeConverter`) and `PackageOptions` (multiple properties, best handled via a map-based converter)
2. Consolidate the three "same rate" rows (DHL/UPS/FEDEX all → 12.00 for EU express medium) into a single value set row
3. Replace `int[]` with `List<Integer>` so JUnit can handle the `[l, w, h]` table syntax

No build file was found, so you'll need to add the dependency to your `pom.xml` or `build.gradle`:
- Maven: `org.tabletest:tabletest-junit` (test scope)
- Gradle: `testImplementation "org.tabletest:tabletest-junit:VERSION"`

Here's the converted test:

```java
import org.junit.jupiter.api.Assertions.assertEquals;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ShippingCostCalculatorTest {

    private ShippingCostCalculator calculator = new ShippingCostCalculator();

    @TableTest("""
        Scenario                    | Zone        | Weight kg | Dimensions   | Options                       | Carrier           | Cost?
        EU standard, light          | EU standard | 0.5       | [20, 15, 10] |                               | DHL               | 5.00
        EU standard, medium         | EU standard | 3.0       | [30, 20, 15] |                               | DHL               | 7.50
        EU standard, heavy          | EU standard | 10.0      | [40, 30, 20] |                               | DHL               | 12.50
        EU standard, very heavy     | EU standard | 25.0      | [50, 40, 30] |                               | UPS               | 20.00
        EU express, light           | EU express  | 0.5       | [20, 15, 10] |                               | DHL               | 8.00
        EU express, medium          | EU express  | 3.0       | [30, 20, 15] |                               | {DHL, UPS, FEDEX} | 12.00
        US standard, light          | US standard | 0.5       | [20, 15, 10] |                               | FEDEX             | 7.00
        US express, heavy           | US express  | 10.0      | [40, 30, 20] |                               | DHL               | 30.00
        Dimensional weight override | EU standard | 1.0       | [70, 50, 10] |                               | DHL               | 12.50
        Fragile surcharge           | EU standard | 3.0       | [30, 20, 15] | [fragile: true]               | DHL               | 8.625
        Insured surcharge           | EU standard | 3.0       | [30, 20, 15] | [insured: 500]                | DHL               | 10.50
        Fragile and insured         | EU standard | 3.0       | [30, 20, 15] | [fragile: true, insured: 200] | DHL               | 11.625
        Hazmat handling             | EU standard | 3.0       | [30, 20, 15] | [handling: hazmat]            | DHL               | 15.50
        Oversize surcharge          | EU standard | 3.0       | [120, 5, 5]  |                               | DHL               | 17.50
        """)
    void shouldCalculateShippingCost(ShippingZone zone, BigDecimal weight,
            List<Integer> dimensions, PackageOptions options,
            Carrier carrier, BigDecimal expected) {
        BigDecimal result = calculator.calculateShippingCost(zone, weight, dimensions, options, carrier);
        assertEquals(expected, result);
    }

    @TypeConverter
    public static ShippingZone parseZone(String value) {
        String[] parts = value.split(" ", 2);
        return new ShippingZone(parts[0], parts[1]);
    }

    @TypeConverter
    public static PackageOptions parseOptions(Map<String, String> options) {
        PackageOptions opts = new PackageOptions();
        if (options == null) return opts;
        if ("true".equals(options.get("fragile"))) opts.setFragile(true);
        if (options.containsKey("insured")) opts.setInsuredValue(Integer.parseInt(options.get("insured")));
        if (options.containsKey("handling")) opts.setHandling(options.get("handling"));
        return opts;
    }
}
```

Key decisions:

**`Options` column with map-based converter** — `PackageOptions` has three independent properties (`fragile`, `insuredValue`, `handling`) that are mostly absent. Using a map column lets each row show only what's special: blank = default `PackageOptions`, `[fragile: true]` = fragile only, etc. This avoids three separate `Fragile`/`Insured value`/`Handling` columns where 14 of 17 rows would be blank across all three.

**Value set `{DHL, UPS, FEDEX}`** — The original test had four rows for "EU express medium" (one carrier-specific row plus three "same rate" rows). Since all three carriers produce 12.00 for that combination, a single value set row captures the intent — any carrier costs the same — and removes the redundancy.

**`List<Integer>` for dimensions** — Replaces `int[]` so JUnit's built-in list parsing handles `[20, 15, 10]` directly; no converter needed.

**`ShippingZone` converter** — Parses `"EU standard"` → `new ShippingZone("EU", "standard")`. The zone values in the table (`EU standard`, `EU express`, `US standard`, `US express`) are readable domain terms rather than code like `new ShippingZone(...)`.