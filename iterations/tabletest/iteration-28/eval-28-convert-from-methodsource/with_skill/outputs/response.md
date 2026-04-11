Now I have everything I need. Let me analyze the test:

- `ShippingZone` needs a `@TypeConverter` (two-part: region + service)
- `Carrier` is an enum → JUnit converts natively
- `BigDecimal`, `List<Integer>` → natively supported
- `fragile`/`insuredValue`/`handling` are optional → blank cells for null/false using boxed `Boolean`
- The last three "same rate" rows test carrier neutrality for EU express medium → collapse into one row with a `{DHL, UPS, FEDEX}` value set
- Class must be `public` for `@TypeConverter` discovery

Here's the conversion:

```java
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

public class ShippingCostCalculatorTest {

    private final ShippingCostCalculator calculator = new ShippingCostCalculator();

    @TableTest("""
        Scenario                    | Zone        | Weight | Dimensions    | Fragile | Insured value | Handling | Carrier           | Cost?
        EU standard light           | EU standard | 0.5    | [20, 15, 10]  |         |               |          | DHL               | 5.00
        EU standard medium          | EU standard | 3.0    | [30, 20, 15]  |         |               |          | DHL               | 7.50
        EU standard heavy           | EU standard | 10.0   | [40, 30, 20]  |         |               |          | DHL               | 12.50
        EU standard very heavy      | EU standard | 25.0   | [50, 40, 30]  |         |               |          | UPS               | 20.00
        EU express light            | EU express  | 0.5    | [20, 15, 10]  |         |               |          | DHL               | 8.00
        EU express medium           | EU express  | 3.0    | [30, 20, 15]  |         |               |          | {DHL, UPS, FEDEX} | 12.00
        US standard light           | US standard | 0.5    | [20, 15, 10]  |         |               |          | FEDEX             | 7.00
        US express heavy            | US express  | 10.0   | [40, 30, 20]  |         |               |          | DHL               | 30.00
        Dimensional weight override | EU standard | 1.0    | [70, 50, 10]  |         |               |          | DHL               | 12.50
        Fragile surcharge           | EU standard | 3.0    | [30, 20, 15]  | true    |               |          | DHL               | 8.625
        Insured surcharge           | EU standard | 3.0    | [30, 20, 15]  |         | 500           |          | DHL               | 10.50
        Fragile and insured         | EU standard | 3.0    | [30, 20, 15]  | true    | 200           |          | DHL               | 11.625
        Hazmat handling             | EU standard | 3.0    | [30, 20, 15]  |         |               | hazmat   | DHL               | 15.50
        Oversize surcharge          | EU standard | 3.0    | [120, 5, 5]   |         |               |          | DHL               | 17.50
        """)
    void shouldCalculateShippingCost(ShippingZone zone, BigDecimal weight, List<Integer> dimensions,
            Boolean fragile, Integer insuredValue, String handling,
            Carrier carrier, BigDecimal expectedCost) {
        PackageOptions opts = new PackageOptions();
        if (Boolean.TRUE.equals(fragile)) opts.setFragile(true);
        if (insuredValue != null) opts.setInsuredValue(insuredValue);
        if (handling != null) opts.setHandling(handling);
        assertEquals(expectedCost,
                calculator.calculateShippingCost(zone, weight, dimensions, opts, carrier));
    }

    @TypeConverter
    public static ShippingZone parseZone(String value) {
        String[] parts = value.split(" ", 2);
        return new ShippingZone(parts[0], parts[1]);
    }
}
```

**Key decisions:**

- **`{DHL, UPS, FEDEX}` value set** on "EU express medium" — the original test had `same rate DHL/UPS/FEDEX` as three separate scenarios, all with identical inputs and expected output. The value set collapses them into one row that explicitly reads as "carrier doesn't affect the rate here." This also absorbs the original "EU express medium" row (UPS, 12.00), which was the same scenario.

- **Blank cells** for `Fragile`, `Insured value`, and `Handling` — using boxed `Boolean`/`Integer` means blank → `null`, keeping the 11 non-surcharge rows clean.

- **`ShippingZone` converter** — parses `"EU standard"` into `new ShippingZone("EU", "standard")`. The `split(" ", 2)` handles any service tier that might contain spaces.

- **`public` class** — required for `@TypeConverter` discovery.