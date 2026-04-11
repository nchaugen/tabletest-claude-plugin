Good — `{DHL, UPS, FEDEX}` in a `Carrier` (enum) column will expand as a value set since the parameter isn't `Set<?>`. The three "same rate" rows collapse into one.

Here's the conversion:

```java
class ShippingCostCalculatorTest {

    private final ShippingCostCalculator calculator = new ShippingCostCalculator();

    @DisplayName("Shipping cost calculation")
    @TableTest("""
        Scenario                     | Region | Speed    | Weight | Dimensions   | Fragile | Insured value | Handling | Carrier           | Cost?
        EU standard, light           | EU     | standard | 0.5    | [20, 15, 10] | false   |               |          | DHL               | 5.00
        EU standard, medium          | EU     | standard | 3.0    | [30, 20, 15] | false   |               |          | DHL               | 7.50
        EU standard, heavy           | EU     | standard | 10.0   | [40, 30, 20] | false   |               |          | DHL               | 12.50
        EU standard, very heavy      | EU     | standard | 25.0   | [50, 40, 30] | false   |               |          | UPS               | 20.00
        EU express, light            | EU     | express  | 0.5    | [20, 15, 10] | false   |               |          | DHL               | 8.00
        EU express, medium           | EU     | express  | 3.0    | [30, 20, 15] | false   |               |          | UPS               | 12.00
        US standard, light           | US     | standard | 0.5    | [20, 15, 10] | false   |               |          | FEDEX             | 7.00
        US express, heavy            | US     | express  | 10.0   | [40, 30, 20] | false   |               |          | DHL               | 30.00
        Dimensional weight override  | EU     | standard | 1.0    | [70, 50, 10] | false   |               |          | DHL               | 12.50
        Fragile surcharge            | EU     | standard | 3.0    | [30, 20, 15] | true    |               |          | DHL               | 8.625
        Insured surcharge            | EU     | standard | 3.0    | [30, 20, 15] | false   | 500           |          | DHL               | 10.50
        Fragile and insured          | EU     | standard | 3.0    | [30, 20, 15] | true    | 200           |          | DHL               | 11.625
        Hazmat handling              | EU     | standard | 3.0    | [30, 20, 15] | false   |               | hazmat   | DHL               | 15.50
        Oversize surcharge           | EU     | standard | 3.0    | [120, 5, 5]  | false   |               |          | DHL               | 17.50
        EU express, carrier-agnostic | EU     | express  | 3.0    | [30, 20, 15] | false   |               |          | {DHL, UPS, FEDEX} | 12.00
        """)
    void shouldCalculateShippingCost(String region, String speed, double weight, List<Integer> dimensions,
                                     boolean fragile, Integer insuredValue, String handling,
                                     Carrier carrier, double cost) {
        var zone = new ShippingZone();
        zone.setRegion(region);
        zone.setSpeed(speed);

        var opts = new PackageOptions();
        if (fragile) opts.setFragile(true);
        if (insuredValue != null) opts.setInsuredValue(insuredValue);
        if (handling != null) opts.setHandling(handling);

        assertEquals(cost, calculator.calculateShippingCost(zone, weight, dimensions, opts, carrier));
    }
}
```

Key decisions:

- **Dimensions column**: `length`/`width`/`height` merged into a single `List<Integer>` column (`[20, 15, 10]`) — the calculator already takes a `List`, and this saves three columns.
- **Carrier column**: Spock's `Carrier.DHL` becomes just `DHL` — JUnit's built-in enum conversion handles it.
- **`Insured value` / `Handling`**: blank cells map to `null`; parameter types are `Integer` and `String` (boxed/nullable) to allow it.
- **Last three rows collapsed**: The "same rate" rows (DHL/UPS/FEDEX all → 12.00) become one row with `{DHL, UPS, FEDEX}` value set. This communicates the intent explicitly — the carrier is irrelevant to the rate for this scenario — rather than restating the same fact three times.