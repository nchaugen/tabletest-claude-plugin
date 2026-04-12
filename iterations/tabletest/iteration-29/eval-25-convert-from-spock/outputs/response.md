There's no Java project here to check for dependencies, so I'll note what to verify. Let me analyze the Spock test and produce the TableTest conversion.

The key observations:
- The last 3 rows (`same rate DHL/UPS/FEDEX`) have identical inputs and expected output — perfect for a value set `{DHL, UPS, FEDEX}`, collapsing 17 rows to 15
- `fragile`, `insuredValue`, and `handling` are optional — use blank cells for null/not-set
- Object construction (`ShippingZone`, `PackageOptions`) lives in the method body, not as table columns

```java
import org.junit.jupiter.api.*;
import org.tabletest.junit.*;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ShippingCostCalculatorTest {

    ShippingCostCalculator calculator = new ShippingCostCalculator();

    @TableTest("""
        Scenario                        | Region | Speed    | Weight | Length | Width | Height | Fragile | Insured Value | Handling | Carrier           | Cost?
        EU standard, light package      | EU     | standard | 0.5    | 20     | 15    | 10     |         |               |          | DHL               | 5.00
        EU standard, medium package     | EU     | standard | 3.0    | 30     | 20    | 15     |         |               |          | DHL               | 7.50
        EU standard, heavy package      | EU     | standard | 10.0   | 40     | 30    | 20     |         |               |          | DHL               | 12.50
        EU standard, very heavy package | EU     | standard | 25.0   | 50     | 40    | 30     |         |               |          | UPS               | 20.00
        EU express, light package       | EU     | express  | 0.5    | 20     | 15    | 10     |         |               |          | DHL               | 8.00
        EU express, medium package      | EU     | express  | 3.0    | 30     | 20    | 15     |         |               |          | UPS               | 12.00
        US standard, light package      | US     | standard | 0.5    | 20     | 15    | 10     |         |               |          | FEDEX             | 7.00
        US express, heavy package       | US     | express  | 10.0   | 40     | 30    | 20     |         |               |          | DHL               | 30.00
        Dimensional weight override     | EU     | standard | 1.0    | 70     | 50    | 10     |         |               |          | DHL               | 12.50
        Fragile surcharge               | EU     | standard | 3.0    | 30     | 20    | 15     | true    |               |          | DHL               | 8.625
        Insured surcharge               | EU     | standard | 3.0    | 30     | 20    | 15     |         | 500           |          | DHL               | 10.50
        Fragile and insured surcharge   | EU     | standard | 3.0    | 30     | 20    | 15     | true    | 200           |          | DHL               | 11.625
        Hazmat handling surcharge       | EU     | standard | 3.0    | 30     | 20    | 15     |         |               | hazmat   | DHL               | 15.50
        Oversize surcharge              | EU     | standard | 3.0    | 120    | 5     | 5      |         |               |          | DHL               | 17.50
        Same rate regardless of carrier | EU     | express  | 3.0    | 30     | 20    | 15     |         |               |          | {DHL, UPS, FEDEX} | 12.00
        """)
    void calculateShippingCost(String region, String speed, double weight,
                               int length, int width, int height,
                               Boolean fragile, Integer insuredValue, String handling,
                               Carrier carrier, BigDecimal expected) {
        ShippingZone zone = new ShippingZone();
        zone.setRegion(region);
        zone.setSpeed(speed);

        List<Integer> dims = List.of(length, width, height);

        PackageOptions opts = new PackageOptions();
        if (Boolean.TRUE.equals(fragile)) opts.setFragile(true);
        if (insuredValue != null) opts.setInsuredValue(insuredValue);
        if (handling != null) opts.setHandling(handling);

        assertEquals(expected, calculator.calculateShippingCost(zone, weight, dims, opts, carrier));
    }
}
```

A few notes on the conversion:

- **`Carrier` enum**: JUnit converts `DHL` → `Carrier.DHL` automatically by name
- **`BigDecimal`**: JUnit converts the cost values automatically; handles `8.625`/`11.625` exactly
- **Blank cells** for `Fragile`, `Insured Value`, `Handling` map to `null` — the setup treats null as "not set", mirroring the original `if (fragile)` / `if (insuredValue != null)` guards
- **Value set `{DHL, UPS, FEDEX}`** on the last row generates 3 test cases, all asserting `12.00` — this replaces the three `same rate` rows and makes the intent explicit: carrier doesn't affect the rate here
- If `calculateShippingCost` returns `double` rather than `BigDecimal`, change the parameter type and use `assertEquals(expected, actual, 0.001)` instead