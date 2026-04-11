Looking at the test: `ShippingZone` is a custom type needing a `@TypeConverter`, `Carrier` is an enum (JUnit handles built-in), dimensions become `List<Integer>`, and the three "same rate" rows (DHL/UPS/FEDEX all producing 12.00) are a value set opportunity.

No `pom.xml` found in the eval directory, so I can't verify the dependency — make sure `org.tabletest:tabletest-junit` is in your build file (see `references/dependency-setup.md` if needed).

Here's the conversion:

```java
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;
import org.junit.jupiter.api.DisplayName;
import io.tabletest.annotation.Description;

public class ShippingCostCalculatorTest {

    private ShippingCostCalculator calculator = new ShippingCostCalculator();

    @DisplayName("Shipping cost calculation")
    @Description("""
        Blank Fragile = not fragile.
        Blank Insured value = uninsured.
        Blank Handling = no special handling.
        """)
    @TableTest("""
        Scenario                     | Zone        | Weight | Dimensions   | Fragile | Insured value | Handling | Carrier           | Cost?
        EU standard, light           | EU/standard | 0.5    | [20, 15, 10] |         |               |          | DHL               | 5.00
        EU standard, medium          | EU/standard | 3.0    | [30, 20, 15] |         |               |          | DHL               | 7.50
        EU standard, heavy           | EU/standard | 10.0   | [40, 30, 20] |         |               |          | DHL               | 12.50
        EU standard, very heavy      | EU/standard | 25.0   | [50, 40, 30] |         |               |          | UPS               | 20.00
        EU express, light            | EU/express  | 0.5    | [20, 15, 10] |         |               |          | DHL               | 8.00
        EU express, carrier-agnostic | EU/express  | 3.0    | [30, 20, 15] |         |               |          | {DHL, UPS, FEDEX} | 12.00
        US standard, light           | US/standard | 0.5    | [20, 15, 10] |         |               |          | FEDEX             | 7.00
        US express, heavy            | US/express  | 10.0   | [40, 30, 20] |         |               |          | DHL               | 30.00
        Dimensional weight overrides | EU/standard | 1.0    | [70, 50, 10] |         |               |          | DHL               | 12.50
        Fragile surcharge            | EU/standard | 3.0    | [30, 20, 15] | true    |               |          | DHL               | 8.625
        Insurance surcharge          | EU/standard | 3.0    | [30, 20, 15] |         | 500           |          | DHL               | 10.50
        Fragile and insured          | EU/standard | 3.0    | [30, 20, 15] | true    | 200           |          | DHL               | 11.625
        Hazmat handling              | EU/standard | 3.0    | [30, 20, 15] |         |               | hazmat   | DHL               | 15.50
        Oversize surcharge           | EU/standard | 3.0    | [120, 5, 5]  |         |               |          | DHL               | 17.50
        """)
    void calculateShippingCost(ShippingZone zone, BigDecimal weight, List<Integer> dimensions,
                               Boolean fragile, Integer insuredValue, String handling,
                               Carrier carrier, BigDecimal expectedCost) {
        PackageOptions opts = new PackageOptions();
        if (Boolean.TRUE.equals(fragile)) opts.setFragile(true);
        if (insuredValue != null) opts.setInsuredValue(insuredValue);
        if (handling != null) opts.setHandling(handling);
        BigDecimal result = calculator.calculateShippingCost(zone, weight, dimensions, opts, carrier);
        assertEquals(expectedCost, result);
    }

    @TypeConverter
    public static ShippingZone parseShippingZone(String value) {
        String[] parts = value.split("/");
        return new ShippingZone(parts[0], parts[1]);
    }
}
```

**Key decisions:**

- **`Zone` column with `@TypeConverter`**: `EU/standard` is more compact than two columns and keeps the zone concept unified. The converter splits on `/` to construct `ShippingZone`.
- **`Boolean fragile` (boxed)**: Blank cells become `null`; `Boolean.TRUE.equals(null)` is safely false. Using the primitive would require writing `false` on every row.
- **Value set `{DHL, UPS, FEDEX}`**: The original "EU express medium" row and three "same rate" rows all test EU/express, 3.0, [30,20,15] → 12.00. They collapse into one row asserting carrier doesn't affect the rate.
- **`@Description`**: The blank-cell semantics for the three optional columns aren't obvious from the table itself, so the description adds real information.
- **17 rows → 14 rows** (3 rows collapsed into 1 value-set row generating 3 cases).