Looking at the test, I need to:
1. Create a `@TypeConverter` for `ShippingZone` (two-arg constructor, no built-in support)
2. Use `List<Integer>` directly with `[...]` syntax (built-in)
3. Use blank cells for nullable `Integer insuredValue` and `String handling`
4. Consolidate the three "same rate" rows into one with a value set `{DHL, UPS, FEDEX}`

```java
public class ShippingCostCalculatorTest {

    private ShippingCostCalculator calculator = new ShippingCostCalculator();

    @TableTest("""
        Scenario                       | Zone        | Weight | Dimensions   | Fragile | Insured value | Handling | Carrier           | Cost?
        EU standard light              | EU standard | 0.5    | [20, 15, 10] | false   |               |          | DHL               | 5.00
        EU standard medium             | EU standard | 3.0    | [30, 20, 15] | false   |               |          | DHL               | 7.50
        EU standard heavy              | EU standard | 10.0   | [40, 30, 20] | false   |               |          | DHL               | 12.50
        EU standard very heavy         | EU standard | 25.0   | [50, 40, 30] | false   |               |          | UPS               | 20.00
        EU express light               | EU express  | 0.5    | [20, 15, 10] | false   |               |          | DHL               | 8.00
        EU express medium, any carrier | EU express  | 3.0    | [30, 20, 15] | false   |               |          | {DHL, UPS, FEDEX} | 12.00
        US standard light              | US standard | 0.5    | [20, 15, 10] | false   |               |          | FEDEX             | 7.00
        US express heavy               | US express  | 10.0   | [40, 30, 20] | false   |               |          | DHL               | 30.00
        Dimensional weight override    | EU standard | 1.0    | [70, 50, 10] | false   |               |          | DHL               | 12.50
        Fragile surcharge              | EU standard | 3.0    | [30, 20, 15] | true    |               |          | DHL               | 8.625
        Insured surcharge              | EU standard | 3.0    | [30, 20, 15] | false   | 500           |          | DHL               | 10.50
        Fragile and insured            | EU standard | 3.0    | [30, 20, 15] | true    | 200           |          | DHL               | 11.625
        Hazmat handling                | EU standard | 3.0    | [30, 20, 15] | false   |               | hazmat   | DHL               | 15.50
        Oversize surcharge             | EU standard | 3.0    | [120, 5, 5]  | false   |               |          | DHL               | 17.50
        """)
    void calculateShippingCost(ShippingZone zone, BigDecimal weight, List<Integer> dimensions,
            boolean fragile, Integer insuredValue, String handling,
            Carrier carrier, BigDecimal expected) {
        PackageOptions opts = new PackageOptions();
        opts.setFragile(fragile);
        opts.setInsuredValue(insuredValue);
        opts.setHandling(handling);
        assertEquals(expected, calculator.calculateShippingCost(zone, weight, dimensions, opts, carrier));
    }

    @TypeConverter
    public static ShippingZone parseShippingZone(String value) {
        String[] parts = value.split(" ", 2);
        return new ShippingZone(parts[0], parts[1]);
    }
}
```

Key decisions:

- **`ShippingZone` converter**: `"EU standard"` splits on the first space into region + service tier. Place it in the same `public` class so TableTest discovers it automatically.
- **`List<Integer>` dimensions**: JUnit's built-in conversion handles `[20, 15, 10]` directly — no converter needed and no manual `Arrays.stream(...).boxed()` in the method.
- **Blank cells for nullable columns**: `Insured value` and `Handling` use blank cells for null rows; `Integer` (boxed) accepts null, and `String` does too.
- **Value set for carrier-agnostic rate**: The original three "same rate" rows collapse into one row with `{DHL, UPS, FEDEX}`, which asserts all three produce `12.00` and makes the intent explicit.
- **`PackageOptions` construction without conditionals**: Always calling the setters (passing `false`/`null` where blank) removes the `if`-guards from the original and keeps the method body straightforward.