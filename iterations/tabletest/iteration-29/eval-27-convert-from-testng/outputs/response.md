No existing project files — this is a standalone conversion. Now let me design and write the TableTest.

Key design decisions:
- Split `ShippingZone` into `Region` + `Service` columns (avoid needing a type converter)
- `Dimensions` as `List<Integer>` — JUnit converts `[20, 15, 10]` automatically
- `Fragile` as `Boolean` (boxed) — blank = null = not set
- `Insured Value` as `Integer` — blank = null
- `Handling` as `String` — blank = null
- `Carrier` enum — JUnit converts by name automatically
- Rows 15–17 (same cost for DHL/UPS/FEDEX) collapse into one row with `{DHL, UPS, FEDEX}`

```java
import org.junit.jupiter.api.BeforeEach;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShippingCostCalculatorTest {

    private ShippingCostCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new ShippingCostCalculator();
    }

    @TableTest("""
        Scenario                    | Region | Service  | Weight | Dimensions   | Fragile | Insured Value | Handling | Carrier           | Cost?
        EU standard light           | EU     | standard | 0.5    | [20, 15, 10] |         |               |          | DHL               | 5.00
        EU standard medium          | EU     | standard | 3.0    | [30, 20, 15] |         |               |          | DHL               | 7.50
        EU standard heavy           | EU     | standard | 10.0   | [40, 30, 20] |         |               |          | DHL               | 12.50
        EU standard very heavy      | EU     | standard | 25.0   | [50, 40, 30] |         |               |          | UPS               | 20.00
        EU express light            | EU     | express  | 0.5    | [20, 15, 10] |         |               |          | DHL               | 8.00
        EU express medium           | EU     | express  | 3.0    | [30, 20, 15] |         |               |          | UPS               | 12.00
        US standard light           | US     | standard | 0.5    | [20, 15, 10] |         |               |          | FEDEX             | 7.00
        US express heavy            | US     | express  | 10.0   | [40, 30, 20] |         |               |          | DHL               | 30.00
        Dimensional weight override | EU     | standard | 1.0    | [70, 50, 10] |         |               |          | DHL               | 12.50
        Fragile surcharge           | EU     | standard | 3.0    | [30, 20, 15] | true    |               |          | DHL               | 8.625
        Insured surcharge           | EU     | standard | 3.0    | [30, 20, 15] |         | 500           |          | DHL               | 10.50
        Fragile and insured         | EU     | standard | 3.0    | [30, 20, 15] | true    | 200           |          | DHL               | 11.625
        Hazmat handling             | EU     | standard | 3.0    | [30, 20, 15] |         |               | hazmat   | DHL               | 15.50
        Oversize surcharge          | EU     | standard | 3.0    | [120, 5, 5]  |         |               |          | DHL               | 17.50
        Same rate all carriers      | EU     | express  | 3.0    | [30, 20, 15] |         |               |          | {DHL, UPS, FEDEX} | 12.00
        """)
    void calculateShippingCost(String region, String service, BigDecimal weight,
            List<Integer> dimensions, Boolean fragile, Integer insuredValue,
            String handling, Carrier carrier, BigDecimal expected) {
        ShippingZone zone = new ShippingZone(region, service);
        PackageOptions opts = new PackageOptions();
        if (Boolean.TRUE.equals(fragile)) opts.setFragile(true);
        if (insuredValue != null) opts.setInsuredValue(insuredValue);
        if (handling != null) opts.setHandling(handling);
        BigDecimal result = calculator.calculateShippingCost(zone, weight, dimensions, opts, carrier);
        assertEquals(expected, result);
    }
}
```

The main structural changes:

- **17 rows → 15**: The three "same rate" rows collapse into one using `{DHL, UPS, FEDEX}`, which generates a test per value and makes the intent explicit — the cost doesn't depend on the carrier for this combination.
- **`ShippingZone` split**: `Region` + `Service` as plain strings, constructed in the method body. No type converter needed.
- **Optional surcharge columns**: `Fragile`, `Insured Value`, and `Handling` are blank for the majority of rows. `Boolean` (boxed) handles the null/false distinction cleanly without needing a default value.
- **`@DataProvider` → inline table**: The data and the assertion logic are co-located, making the test self-documenting.