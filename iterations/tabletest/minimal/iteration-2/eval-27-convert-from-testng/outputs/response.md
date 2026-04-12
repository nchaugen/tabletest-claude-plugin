No build file available, so I'll note the dependency requirement. Here's the conversion:

```java
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

public class ShippingCostCalculatorTest {

    private ShippingCostCalculator calculator = new ShippingCostCalculator();

    @DisplayName("Base shipping rates by zone, service level, weight, and carrier")
    @TableTest("""
        Scenario                    | Zone        | Weight | Dimensions   | Carrier           | Cost?
        EU standard light           | EU/standard | 0.5    | [20, 15, 10] | DHL               | 5.00
        EU standard medium          | EU/standard | 3.0    | [30, 20, 15] | DHL               | 7.50
        EU standard heavy           | EU/standard | 10.0   | [40, 30, 20] | DHL               | 12.50
        EU standard very heavy      | EU/standard | 25.0   | [50, 40, 30] | UPS               | 20.00
        EU express light            | EU/express  | 0.5    | [20, 15, 10] | DHL               | 8.00
        EU express medium           | EU/express  | 3.0    | [30, 20, 15] | {DHL, UPS, FEDEX} | 12.00
        US standard light           | US/standard | 0.5    | [20, 15, 10] | FEDEX             | 7.00
        US express heavy            | US/express  | 10.0   | [40, 30, 20] | DHL               | 30.00
        Dimensional weight override | EU/standard | 1.0    | [70, 50, 10] | DHL               | 12.50
        """)
    void baseShippingRates(ShippingZone zone, BigDecimal weight, List<Integer> dimensions,
                           Carrier carrier, BigDecimal expected) {
        BigDecimal result = calculator.calculateShippingCost(
                zone, weight, dimensions, new PackageOptions(), carrier);
        assertEquals(result, expected);
    }

    @DisplayName("Package option surcharges applied to base rate")
    @Description("""
        All rows use EU standard zone, 3.0 kg, carrier DHL.
        Base cost for standard dimensions [30, 20, 15] without surcharges is 7.50.
        """)
    @TableTest("""
        Scenario            | Dimensions   | Fragile | Insured Value | Handling | Cost?
        No surcharges       | [30, 20, 15] | false   |               |          | 7.50
        Fragile             | [30, 20, 15] | true    |               |          | 8.625
        Insured             | [30, 20, 15] | false   | 500           |          | 10.50
        Fragile and insured | [30, 20, 15] | true    | 200           |          | 11.625
        Hazmat handling     | [30, 20, 15] | false   |               | hazmat   | 15.50
        Oversize            | [120, 5, 5]  | false   |               |          | 17.50
        """)
    void packageOptionSurcharges(List<Integer> dimensions, boolean fragile,
                                  Integer insuredValue, String handling, BigDecimal expected) {
        PackageOptions opts = new PackageOptions();
        opts.setFragile(fragile);
        if (insuredValue != null) opts.setInsuredValue(insuredValue);
        if (handling != null) opts.setHandling(handling);
        BigDecimal result = calculator.calculateShippingCost(
                new ShippingZone("EU", "standard"), new BigDecimal("3.0"),
                dimensions, opts, Carrier.DHL);
        assertEquals(result, expected);
    }

    @TypeConverter
    public static ShippingZone parseShippingZone(String value) {
        String[] parts = value.split("/");
        return new ShippingZone(parts[0], parts[1]);
    }
}
```

If `tabletest-junit` isn't already in your build file, add:
```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```

**Key decisions:**

- **Two tables instead of one** — base rates and surcharges are separate concerns; the surcharge rows all share the same zone/weight/carrier, which would otherwise be noisy repeated columns.
- **`{DHL, UPS, FEDEX}` value set** — the three "same rate" rows collapsed into one, asserting carrier is irrelevant for EU express medium.
- **Dimensional weight override stays in base rates** — it determines the billed weight tier, which is the same pipeline as zone/weight → cost.
- **`Carrier` is an input column** (no `?`) — the calculator accepts a carrier; these tests don't verify carrier selection.
- **`@TypeConverter` for `ShippingZone`** — parses `"EU/standard"` into `new ShippingZone("EU", "standard")`. `BigDecimal`, `List<Integer>`, `Carrier` (enum), and `boolean` all convert via JUnit built-ins.
- **`PackageOptions` flattened into columns** — fragile/insuredValue/handling as separate columns keep the table readable and avoid a custom converter for a composite object.