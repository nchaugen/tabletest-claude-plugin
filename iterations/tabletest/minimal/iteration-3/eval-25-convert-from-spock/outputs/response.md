Looking at the Spock test, a few things to handle:
- `insuredValue` and `handling` are nullable — blank cells + boxed/reference types
- `carrier` is an enum — JUnit converts by constant name (`DHL`, not `Carrier.DHL`)
- Last 3 rows ("same rate DHL/UPS/FEDEX") all assert the same result — a value set collapses them to one row

```java
public class ShippingCostCalculatorTest {

    private final ShippingCostCalculator calculator = new ShippingCostCalculator();

    @TableTest("""
        Scenario                    | region | speed    | weight | length | width | height | fragile | insuredValue | handling | carrier           | cost?
        EU standard light           | EU     | standard | 0.5    | 20     | 15    | 10     | false   |              |          | DHL               | 5.00
        EU standard medium          | EU     | standard | 3.0    | 30     | 20    | 15     | false   |              |          | DHL               | 7.50
        EU standard heavy           | EU     | standard | 10.0   | 40     | 30    | 20     | false   |              |          | DHL               | 12.50
        EU standard very heavy      | EU     | standard | 25.0   | 50     | 40    | 30     | false   |              |          | UPS               | 20.00
        EU express light            | EU     | express  | 0.5    | 20     | 15    | 10     | false   |              |          | DHL               | 8.00
        EU express medium           | EU     | express  | 3.0    | 30     | 20    | 15     | false   |              |          | UPS               | 12.00
        US standard light           | US     | standard | 0.5    | 20     | 15    | 10     | false   |              |          | FEDEX             | 7.00
        US express heavy            | US     | express  | 10.0   | 40     | 30    | 20     | false   |              |          | DHL               | 30.00
        Dimensional weight override | EU     | standard | 1.0    | 70     | 50    | 10     | false   |              |          | DHL               | 12.50
        Fragile surcharge           | EU     | standard | 3.0    | 30     | 20    | 15     | true    |              |          | DHL               | 8.625
        Insured surcharge           | EU     | standard | 3.0    | 30     | 20    | 15     | false   | 500          |          | DHL               | 10.50
        Fragile and insured         | EU     | standard | 3.0    | 30     | 20    | 15     | true    | 200          |          | DHL               | 11.625
        Hazmat handling             | EU     | standard | 3.0    | 30     | 20    | 15     | false   |              | hazmat   | DHL               | 15.50
        Oversize surcharge          | EU     | standard | 3.0    | 120    | 5     | 5      | false   |              |          | DHL               | 17.50
        Same rate EU express        | EU     | express  | 3.0    | 30     | 20    | 15     | false   |              |          | {DHL, UPS, FEDEX} | 12.00
        """)
    void calculateShippingCost(String region, String speed, double weight,
                               int length, int width, int height,
                               boolean fragile, Integer insuredValue, String handling,
                               Carrier carrier, double cost) {
        var zone = new ShippingZone();
        zone.region = region;
        zone.speed = speed;

        var opts = new PackageOptions();
        opts.setFragile(fragile);
        if (insuredValue != null) opts.setInsuredValue(insuredValue);
        if (handling != null) opts.setHandling(handling);

        assertEquals(cost, calculator.calculateShippingCost(zone, weight, List.of(length, width, height), opts, carrier), 0.001);
    }
}
```

Key decisions:
- **Value set on `carrier`**: the last 3 rows in the original assert identical outputs — `{DHL, UPS, FEDEX}` makes that intent explicit and saves 2 rows
- **Blank cells** for `insuredValue` and `handling` map to `null`; `Integer` (boxed) instead of `int` so JUnit accepts the blank
- **`opts.setFragile(fragile)`** directly instead of a conditional — matches the Spock behaviour assuming `false` is the default
- **Enum names** without the class prefix (`DHL` not `Carrier.DHL`) — JUnit resolves the type from the parameter