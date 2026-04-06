Two custom types need converters (`ShippingZone` and `PackageOptions`), but once handled they clean up the method body entirely. I also notice that "EU express medium" and the three "same rate" rows are all the same inputs with the same expected cost — a value set consolidates them into one row that explicitly documents carrier independence.

Here's the conversion:

```java
public class ShippingCostCalculatorTest {

    private final ShippingCostCalculator calculator = new ShippingCostCalculator();

    @Description("""
        Dimensional weight = (L × W × H) / divisor; billing weight is the greater of
        actual and dimensional weight. The "EU express, any carrier" row documents that
        cost is identical regardless of carrier for that zone and weight combination.
        """)
    @TableTest("""
        Scenario                    | Zone        | Weight | Dimensions   | Options             | Carrier           | Shipping Cost?
        EU standard light           | EU standard | 0.5    | [20, 15, 10] |                     | DHL               | 5.00
        EU standard medium          | EU standard | 3.0    | [30, 20, 15] |                     | DHL               | 7.50
        EU standard heavy           | EU standard | 10.0   | [40, 30, 20] |                     | DHL               | 12.50
        EU standard very heavy      | EU standard | 25.0   | [50, 40, 30] |                     | UPS               | 20.00
        EU express light            | EU express  | 0.5    | [20, 15, 10] |                     | DHL               | 8.00
        EU express, any carrier     | EU express  | 3.0    | [30, 20, 15] |                     | {DHL, UPS, FEDEX} | 12.00
        US standard light           | US standard | 0.5    | [20, 15, 10] |                     | FEDEX             | 7.00
        US express heavy            | US express  | 10.0   | [40, 30, 20] |                     | DHL               | 30.00
        Dimensional weight override | EU standard | 1.0    | [70, 50, 10] |                     | DHL               | 12.50
        Fragile surcharge           | EU standard | 3.0    | [30, 20, 15] | fragile             | DHL               | 8.625
        Insured surcharge           | EU standard | 3.0    | [30, 20, 15] | insured:500         | DHL               | 10.50
        Fragile and insured         | EU standard | 3.0    | [30, 20, 15] | fragile,insured:200 | DHL               | 11.625
        Hazmat handling             | EU standard | 3.0    | [30, 20, 15] | hazmat              | DHL               | 15.50
        Oversize surcharge          | EU standard | 3.0    | [120, 5, 5]  |                     | DHL               | 17.50
        """)
    void shouldCalculateShippingCost(ShippingZone zone, BigDecimal weight,
                                     List<Integer> dimensions, PackageOptions opts,
                                     Carrier carrier, BigDecimal cost) {
        assertEquals(cost, calculator.calculateShippingCost(zone, weight, dimensions, opts, carrier));
    }

    @TypeConverter
    public static ShippingZone parseShippingZone(String value) {
        String[] parts = value.split(" ", 2);
        return new ShippingZone(parts[0], parts[1]);
    }

    @TypeConverter
    public static PackageOptions parsePackageOptions(String value) {
        PackageOptions opts = new PackageOptions();
        if (value == null || value.isBlank()) return opts;
        for (String option : value.split(",")) {
            option = option.trim();
            if (option.equals("fragile")) {
                opts.setFragile(true);
            } else if (option.startsWith("insured:")) {
                opts.setInsuredValue(Integer.parseInt(option.substring(8)));
            } else {
                opts.setHandling(option);
            }
        }
        return opts;
    }
}
```

A few things worth noting:

**Value set consolidation**: The original "EU express medium" row and the three "same rate" rows were all identical inputs — same zone, weight, and dimensions, differing only in carrier, all producing 12.00. The `{DHL, UPS, FEDEX}` value set collapses these 4 rows into 1 and makes the intent explicit: carrier is irrelevant to cost here.

**`PackageOptions` converter**: The three optional fields (`fragile`, `insuredValue`, `handling`) were keeping the original method body cluttered with `if` guards. Packing them into a single `Options` column with a small DSL (`fragile`, `insured:500`, `hazmat`) makes each row self-contained and keeps the test method to a single assertion. The blank `Options` cells for the majority of rows read as "no special handling".

**`ShippingZone` converter**: "EU standard" → `new ShippingZone("EU", "standard")` — the split-on-space approach handles all four zone combinations in the data.

**`public class`**: Required for TableTest to discover the `@TypeConverter` methods — the class must be `public`, not package-private.