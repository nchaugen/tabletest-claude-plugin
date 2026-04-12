No build file found in the eval directory, so I'll proceed with the conversion and note the dependency assumption.

Let me work through the design before writing code.

**Concern analysis:**

1. **Base shipping rate** — zone + weight + carrier → cost (no surcharges; dimensions irrelevant when standard-sized)
2. **Dimension-based adjustments** — dimensional weight override + oversize surcharge (both stem from dimensions)
3. **PackageOptions surcharges** — fragile, insured value, hazmat handling

The "same rate DHL/UPS/FEDEX" rows collapse into a value set `{DHL, UPS, FEDEX}` in the EU express medium row.

Here's the converted test:

```java
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

public class ShippingCostCalculatorTest {

    private final ShippingCostCalculator calculator = new ShippingCostCalculator();

    @DisplayName("Base shipping rate by zone and weight")
    @Description("""
            Dimensions are fixed at [30, 20, 15] cm — within normal bounds so
            dimensional weight does not apply. See dimensionBasedAdjustments for
            scenarios where dimensions affect the billed weight or add a surcharge.
            """)
    @TableTest("""
            Scenario               | Zone        | Weight (kg) | Carrier           | Cost?
            EU standard light      | EU/standard | 0.5         | DHL               | 5.00
            EU standard medium     | EU/standard | 3.0         | DHL               | 7.50
            EU standard heavy      | EU/standard | 10.0        | DHL               | 12.50
            EU standard very heavy | EU/standard | 25.0        | UPS               | 20.00
            EU express light       | EU/express  | 0.5         | DHL               | 8.00
            EU express medium      | EU/express  | 3.0         | {DHL, UPS, FEDEX} | 12.00
            US standard light      | US/standard | 0.5         | FEDEX             | 7.00
            US express heavy       | US/express  | 10.0        | DHL               | 30.00
            """)
    void baseShippingRate(ShippingZone zone, BigDecimal weight, Carrier carrier, BigDecimal cost) {
        List<Integer> dimensions = List.of(30, 20, 15);
        BigDecimal result = calculator.calculateShippingCost(
                zone, weight, dimensions, new PackageOptions(), carrier);
        assertEquals(cost, result);
    }

    @DisplayName("Dimension-based adjustments")
    @Description("""
            All rows use EU/standard zone, DHL carrier, and default PackageOptions.
            """)
    @TableTest("""
            Scenario                   | Weight (kg) | Dimensions   | Cost?
            Dimensional weight applies | 1.0         | [70, 50, 10] | 12.50
            Oversize dimension         | 3.0         | [120, 5, 5]  | 17.50
            """)
    void dimensionBasedAdjustments(BigDecimal weight, List<Integer> dimensions, BigDecimal cost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        BigDecimal result = calculator.calculateShippingCost(
                zone, weight, dimensions, new PackageOptions(), Carrier.DHL);
        assertEquals(cost, result);
    }

    @DisplayName("PackageOptions surcharges")
    @Description("""
            All rows use EU/standard zone, 3.0 kg, dimensions [30, 20, 15], DHL carrier.
            Base cost without surcharges is 7.50.
            """)
    @TableTest("""
            Scenario            | Fragile | Insured value | Handling | Cost?
            No surcharges       | false   |               |          | 7.50
            Fragile             | true    |               |          | 8.625
            Insured $500        | false   | 500           |          | 10.50
            Fragile and insured | true    | 200           |          | 11.625
            Hazmat              | false   |               | hazmat   | 15.50
            """)
    void packageOptionSurcharges(boolean fragile, Integer insuredValue, String handling, BigDecimal cost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        List<Integer> dimensions = List.of(30, 20, 15);
        PackageOptions opts = new PackageOptions();
        opts.setFragile(fragile);
        if (insuredValue != null) opts.setInsuredValue(insuredValue);
        if (handling != null) opts.setHandling(handling);
        BigDecimal result = calculator.calculateShippingCost(
                zone, new BigDecimal("3.0"), dimensions, opts, Carrier.DHL);
        assertEquals(cost, result);
    }

    @TypeConverter
    public static ShippingZone parseShippingZone(String value) {
        String[] parts = value.split("/");
        return new ShippingZone(parts[0], parts[1]);
    }
}
```

**Key design decisions:**

- **Three tables by concern**: base rate, dimension-based adjustments, PackageOptions surcharges. Each table varies exactly the inputs relevant to that concern and holds the rest constant in the method body or `@Description`.
- **Value set for carrier parity**: `{DHL, UPS, FEDEX}` on the EU express medium row replaces three separate rows and makes the "same rate regardless of carrier" assertion explicit and intentional.
- **Dimension-based table**: Groups dimensional weight and oversize together — both are triggered by dimensions, making it easy to see all dimension-driven cost effects in one place.
- **`@TypeConverter` for `ShippingZone`**: Parses `"EU/standard"` notation, keeping zone readable as a single compact column value.
- **`BigDecimal`**: No custom converter needed — JUnit 5 converts via `BigDecimal(String)` implicitly.
- **`Carrier` enum**: JUnit handles enum conversion by name directly.

**One dependency note**: ensure `org.tabletest:tabletest-junit` is on the classpath, and JUnit Jupiter ≥ 5.11. If it's not in `pom.xml`/`build.gradle` yet, add it with `<scope>test</scope>`.