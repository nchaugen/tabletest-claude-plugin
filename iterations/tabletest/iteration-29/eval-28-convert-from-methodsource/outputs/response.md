Now I have everything I need. Here's the converted TableTest:

The design decisions:
- **Split into two tables** by concern: base rates (routing logic) vs. surcharges (additive fees)
- **Two String columns** (`Region` + `Service`) instead of `ShippingZone` to avoid a TypeConverter — the method constructs the zone from them
- **Value set** on `Carrier` to consolidate the three "same rate" rows and row 6 into one
- **Boxed `Boolean` and `Integer`** for optional surcharge inputs so blank cells map to null
- **`@Description`** on the surcharges table to document the fixed inputs not visible as columns

```java
public class ShippingCostCalculatorTest {

    private final ShippingCostCalculator calculator = new ShippingCostCalculator();

    @DisplayName("Base shipping rate by zone, weight, and carrier")
    @TableTest("""
        Scenario                    | Region | Service  | Weight | Dimensions   | Carrier            | Cost?
        EU standard light           | EU     | standard | 0.5    | [20, 15, 10] | DHL                | 5.00
        EU standard medium          | EU     | standard | 3.0    | [30, 20, 15] | DHL                | 7.50
        EU standard heavy           | EU     | standard | 10.0   | [40, 30, 20] | DHL                | 12.50
        EU standard very heavy      | EU     | standard | 25.0   | [50, 40, 30] | UPS                | 20.00
        EU express light            | EU     | express  | 0.5    | [20, 15, 10] | DHL                | 8.00
        EU express medium           | EU     | express  | 3.0    | [30, 20, 15] | {DHL, UPS, FEDEX}  | 12.00
        US standard light           | US     | standard | 0.5    | [20, 15, 10] | FEDEX              | 7.00
        US express heavy            | US     | express  | 10.0   | [40, 30, 20] | DHL                | 30.00
        Dimensional weight override | EU     | standard | 1.0    | [70, 50, 10] | DHL                | 12.50
        """)
    void shouldCalculateBaseShippingRate(String region, String service, BigDecimal weight,
            List<Integer> dimensions, Carrier carrier, BigDecimal cost) {
        ShippingZone zone = new ShippingZone(region, service);
        BigDecimal result = calculator.calculateShippingCost(zone, weight, dimensions, new PackageOptions(), carrier);
        assertEquals(cost, result);
    }

    @DisplayName("Surcharges applied to base shipping cost")
    @Description("""
        All scenarios use EU standard zone, 3.0 kg actual weight, and DHL carrier.
        Base rate for these inputs without surcharges is 7.50.
        """)
    @TableTest("""
        Scenario            | Dimensions    | Fragile | Insured value | Handling | Cost?
        No surcharges       | [30, 20, 15]  |         |               |          | 7.50
        Fragile             | [30, 20, 15]  | true    |               |          | 8.625
        Insured             | [30, 20, 15]  |         | 500           |          | 10.50
        Fragile and insured | [30, 20, 15]  | true    | 200           |          | 11.625
        Hazmat handling     | [30, 20, 15]  |         |               | hazmat   | 15.50
        Oversize            | [120, 5, 5]   |         |               |          | 17.50
        """)
    void shouldApplySurcharges(List<Integer> dimensions, Boolean fragile,
            Integer insuredValue, String handling, BigDecimal cost) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        PackageOptions opts = new PackageOptions();
        if (Boolean.TRUE.equals(fragile)) opts.setFragile(true);
        if (insuredValue != null) opts.setInsuredValue(insuredValue);
        if (handling != null) opts.setHandling(handling);
        BigDecimal result = calculator.calculateShippingCost(zone, new BigDecimal("3.0"), dimensions, opts, Carrier.DHL);
        assertEquals(cost, result);
    }
}
```

A few things worth noting:

**Value set on `Carrier`**: The original had rows 6, 15, 16, 17 all asserting 12.00 for EU express medium with different carriers. The value set `{DHL, UPS, FEDEX}` collapses these into one row and makes the intent explicit — all carriers tie at the same rate for this zone.

**`Boolean` (boxed) for `Fragile`**: Blank cells in the surcharges table become `null`, and `Boolean.TRUE.equals(null)` returns `false`, so absent fragile flag is handled without an explicit check.

**Split by concern**: The base rate table tests routing logic (zone × weight → carrier assignment and price). The surcharges table tests additive fee logic. They share the same `calculateShippingCost` call but answer different questions, and splitting makes both tables shorter and more focused.

**No `@TypeConverter` needed**: `BigDecimal`, `List<Integer>`, `Carrier` (enum), `Boolean`, and `Integer` all convert via JUnit's built-in converters. `ShippingZone` is constructed in the method body from two plain `String` columns.