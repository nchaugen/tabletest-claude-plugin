No build files found, so I can't check the dependency — make sure `org.tabletest:tabletest-junit` is on the classpath.

Here's the converted test:

```java
public class ShippingCostCalculatorTest {

    private final ShippingCostCalculator calculator = new ShippingCostCalculator();

    @Description("""
            No special handling or surcharges applied.
            Dimensional weight is not triggered (package dimensions compact relative to actual weight).
            """)
    @TableTest("""
            Scenario               | Zone        | Weight | Dimensions   | Carrier           | Cost?
            EU standard light      | EU standard | 0.5    | [20, 15, 10] | DHL               | 5.00
            EU standard medium     | EU standard | 3.0    | [30, 20, 15] | DHL               | 7.50
            EU standard heavy      | EU standard | 10.0   | [40, 30, 20] | DHL               | 12.50
            EU standard very heavy | EU standard | 25.0   | [50, 40, 30] | UPS               | 20.00
            EU express light       | EU express  | 0.5    | [20, 15, 10] | DHL               | 8.00
            EU express medium      | EU express  | 3.0    | [30, 20, 15] | {DHL, UPS, FEDEX} | 12.00
            US standard light      | US standard | 0.5    | [20, 15, 10] | FEDEX             | 7.00
            US express heavy       | US express  | 10.0   | [40, 30, 20] | DHL               | 30.00
            """)
    void baseShippingCost(ShippingZone zone, BigDecimal weight, List<Integer> dimensions,
            Carrier carrier, BigDecimal cost) {
        BigDecimal result = calculator.calculateShippingCost(
                zone, weight, dimensions, new PackageOptions(), carrier);
        assertEquals(cost, result);
    }

    @Description("""
            When volumetric weight (L × W × H / 3500) exceeds actual weight,
            the volumetric weight is used to determine the rate tier.
            Fixed: EU standard zone, DHL carrier.
            """)
    @TableTest("""
            Scenario                        | Actual Weight | Dimensions   | Cost?
            Volumetric weight triggers tier | 1.0           | [70, 50, 10] | 12.50
            """)
    void dimensionalWeightOverride(BigDecimal weight, List<Integer> dimensions, BigDecimal cost) {
        BigDecimal result = calculator.calculateShippingCost(
                new ShippingZone("EU", "standard"), weight, dimensions, new PackageOptions(), Carrier.DHL);
        assertEquals(cost, result);
    }

    @Description("""
            Fixed: EU standard zone, 3.0 kg, DHL carrier. Base cost before surcharges: 7.50.
            Fragile: +15%. Insurance: 0.6% of declared value, minimum 3.00.
            Hazmat: flat +8.00. Oversize (longest side > 100 cm): flat +10.00.
            """)
    @TableTest("""
            Scenario            | Fragile | Insured Value | Handling | Dimensions   | Cost?
            Fragile only        | true    |               |          | [30, 20, 15] | 8.625
            Insurance 500       | false   | 500           |          | [30, 20, 15] | 10.50
            Fragile and insured | true    | 200           |          | [30, 20, 15] | 11.625
            Hazmat handling     | false   |               | hazmat   | [30, 20, 15] | 15.50
            Oversize package    | false   |               |          | [120, 5, 5]  | 17.50
            """)
    void shippingCostSurcharges(boolean fragile, Integer insuredValue, String handling,
            List<Integer> dimensions, BigDecimal cost) {
        PackageOptions opts = new PackageOptions();
        opts.setFragile(fragile);
        if (insuredValue != null) opts.setInsuredValue(insuredValue);
        if (handling != null) opts.setHandling(handling);
        BigDecimal result = calculator.calculateShippingCost(
                new ShippingZone("EU", "standard"), new BigDecimal("3.0"), dimensions, opts, Carrier.DHL);
        assertEquals(cost, result);
    }

    @TypeConverter
    public static ShippingZone parseShippingZone(String value) {
        String[] parts = value.split(" ", 2);
        return new ShippingZone(parts[0], parts[1]);
    }
}
```

**Key decisions:**

- **Three tables, three concerns**: base rates, dimensional weight, and surcharges. The original mixed all of these into one flat list.
- **Value set for carrier tie**: `{DHL, UPS, FEDEX}` on the EU express medium row replaces the three near-duplicate "same rate" scenarios — and makes the invariant explicit ("result is identical regardless of carrier").
- **Fixed values in `@Description`**: Zone, weight, and carrier are the same for all surcharge rows, so they stay out of the table and live in the description instead.
- **`Dimensions` column in the surcharge table**: Oversize is triggered by dimensions (`[120, 5, 5]`), not by `PackageOptions`, so dimensions belong as a data column rather than being fixed.
- **`Integer insuredValue`** (boxed): blank cells produce `null`; the converter/method guards against it.
- **`parseShippingZone` converter**: converts `"EU standard"` → `new ShippingZone("EU", "standard")`. The class must be `public` for TableTest to discover it.