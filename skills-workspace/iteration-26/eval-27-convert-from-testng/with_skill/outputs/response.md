Now I have enough to design the conversion. Let me analyze the test structure before writing any code.

**Key observations:**
- `ShippingZone` has two fields (country + service level) → split into two columns, construct in test body
- `int[]` dimensions → use `List<Integer>` with `[20, 15, 10]` syntax
- `boolean fragile`, `Integer insuredValue`, `String handling` are optional inputs → blank cells for absent/null, use `Boolean` (boxed) for fragile
- `BigDecimal` and `Carrier` (enum) → both handled by JUnit's built-in conversion
- No custom `@TypeConverter` needed
- Rows "same rate DHL/UPS/FEDEX" (last 3) all test the same scenario — perfect value set candidate: `{DHL, UPS, FEDEX}`
- The TestNG `assertEquals(result, expected)` order (actual, expected) flips to JUnit 5's `assertEquals(expected, actual)`

Here's the converted test:

```java
import org.junit.jupiter.api.Test;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShippingCostCalculatorTest {

    private final ShippingCostCalculator calculator = new ShippingCostCalculator();

    @TableTest("""
        Scenario                    | Country | Service  | Weight | Dimensions    | Fragile | Insured value | Handling | Carrier           | Cost?
        EU standard light           | EU      | standard | 0.5    | [20, 15, 10]  |         |               |          | DHL               | 5.00
        EU standard medium          | EU      | standard | 3.0    | [30, 20, 15]  |         |               |          | DHL               | 7.50
        EU standard heavy           | EU      | standard | 10.0   | [40, 30, 20]  |         |               |          | DHL               | 12.50
        EU standard very heavy      | EU      | standard | 25.0   | [50, 40, 30]  |         |               |          | UPS               | 20.00
        EU express light            | EU      | express  | 0.5    | [20, 15, 10]  |         |               |          | DHL               | 8.00
        EU express medium           | EU      | express  | 3.0    | [30, 20, 15]  |         |               |          | UPS               | 12.00
        US standard light           | US      | standard | 0.5    | [20, 15, 10]  |         |               |          | FEDEX             | 7.00
        US express heavy            | US      | express  | 10.0   | [40, 30, 20]  |         |               |          | DHL               | 30.00
        Dimensional weight override | EU      | standard | 1.0    | [70, 50, 10]  |         |               |          | DHL               | 12.50
        Fragile surcharge           | EU      | standard | 3.0    | [30, 20, 15]  | true    |               |          | DHL               | 8.625
        Insured surcharge           | EU      | standard | 3.0    | [30, 20, 15]  |         | 500           |          | DHL               | 10.50
        Fragile and insured         | EU      | standard | 3.0    | [30, 20, 15]  | true    | 200           |          | DHL               | 11.625
        Hazmat handling             | EU      | standard | 3.0    | [30, 20, 15]  |         |               | hazmat   | DHL               | 15.50
        Oversize surcharge          | EU      | standard | 3.0    | [120, 5, 5]   |         |               |          | DHL               | 17.50
        Same rate across carriers   | EU      | express  | 3.0    | [30, 20, 15]  |         |               |          | {DHL, UPS, FEDEX} | 12.00
        """)
    void calculateShippingCost(String country, String service, BigDecimal weight,
            List<Integer> dimensions, Boolean fragile, Integer insuredValue, String handling,
            Carrier carrier, BigDecimal cost) {
        ShippingZone zone = new ShippingZone(country, service);
        BigDecimal result = calculator.calculateShippingCost(zone, weight, dimensions, buildOptions(fragile, insuredValue, handling), carrier);
        assertEquals(cost, result);
    }

    private static PackageOptions buildOptions(Boolean fragile, Integer insuredValue, String handling) {
        PackageOptions opts = new PackageOptions();
        if (Boolean.TRUE.equals(fragile)) opts.setFragile(true);
        if (insuredValue != null) opts.setInsuredValue(insuredValue);
        if (handling != null) opts.setHandling(handling);
        return opts;
    }
}
```

**Dependency note:** TableTest uses JUnit Jupiter, so you'll need to add it to your build file (replacing TestNG for this test):

```xml
<!-- Maven -->
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```

---

**Key decisions explained:**

- **`Country` + `Service` columns instead of `ShippingZone`** — splitting the two fields into concrete string columns makes each row readable at a glance; `ShippingZone` is constructed in the test body from those two values.
- **`Boolean fragile` (boxed)** — primitive `boolean` can't be null, so the boxed type lets blank cells represent "not fragile" naturally.
- **`buildOptions` helper** — keeps the `@TableTest` method body clean (arrange, act, assert) while still handling the optional-field construction logic.
- **`{DHL, UPS, FEDEX}` value set** — the last three rows in the original all assert the same cost for the same inputs, differing only by carrier. The value set expresses "carrier doesn't affect cost for EU express medium" declaratively in one row instead of three.
- **`Arrays.stream` import dropped** — `List<Integer>` comes directly from the table; no manual boxing needed.