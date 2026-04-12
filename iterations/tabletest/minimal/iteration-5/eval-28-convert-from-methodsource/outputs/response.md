No project files found, so I'll work from the test as given. Here's the TableTest conversion:

---

**Decomposition:** The test mixes two concerns — (1) base cost by zone, weight, and carrier, and (2) surcharges applied on top. I'll split those into two `@TableTest` methods. The "same rate" rows collapse naturally into a value set.

`ShippingZone` needs two columns (`Region` + `Service`) since it takes two constructor args. `BigDecimal` and `Carrier` (enum) convert automatically via JUnit.

```java
public class ShippingCostCalculatorTest {

    private final ShippingCostCalculator calculator = new ShippingCostCalculator();

    @Description("""
        Surcharge columns (Fragile, Insured Value, Handling) are absent here —
        PackageOptions is constructed with defaults to isolate base cost behaviour.
        """)
    @TableTest("""
        Scenario                   | Region | Service  | Weight | Dimensions   | Carrier           | Cost?
        EU standard light          | EU     | standard | 0.5    | [20, 15, 10] | DHL               | 5.00
        EU standard medium         | EU     | standard | 3.0    | [30, 20, 15] | DHL               | 7.50
        EU standard heavy          | EU     | standard | 10.0   | [40, 30, 20] | DHL               | 12.50
        EU standard very heavy     | EU     | standard | 25.0   | [50, 40, 30] | UPS               | 20.00
        EU express light           | EU     | express  | 0.5    | [20, 15, 10] | DHL               | 8.00
        EU express medium          | EU     | express  | 3.0    | [30, 20, 15] | {DHL, UPS, FEDEX} | 12.00
        US standard light          | US     | standard | 0.5    | [20, 15, 10] | FEDEX             | 7.00
        US express heavy           | US     | express  | 10.0   | [40, 30, 20] | DHL               | 30.00
        Dimensional weight applies | EU     | standard | 1.0    | [70, 50, 10] | DHL               | 12.50
        """)
    void calculatesBaseShippingCost(String region, String service, BigDecimal weight,
            List<Integer> dimensions, Carrier carrier, BigDecimal expected) {
        ShippingZone zone = new ShippingZone(region, service);
        BigDecimal result = calculator.calculateShippingCost(zone, weight, dimensions, new PackageOptions(), carrier);
        assertEquals(expected, result);
    }

    @Description("Zone is EU standard, weight is 3.0 kg, dimensions are [30, 20, 15], carrier is DHL for all rows.")
    @TableTest("""
        Scenario             | Fragile | Insured Value | Handling | Dimensions    | Cost?
        Fragile surcharge    | true    |               |          | [30, 20, 15]  | 8.625
        Insurance surcharge  | false   | 500           |          | [30, 20, 15]  | 10.50
        Fragile and insured  | true    | 200           |          | [30, 20, 15]  | 11.625
        Hazmat handling      | false   |               | hazmat   | [30, 20, 15]  | 15.50
        Oversize surcharge   | false   |               |          | [120, 5, 5]   | 17.50
        """)
    void appliesSurcharges(boolean fragile, Integer insuredValue, String handling,
            List<Integer> dimensions, BigDecimal expected) {
        ShippingZone zone = new ShippingZone("EU", "standard");
        PackageOptions opts = new PackageOptions();
        opts.setFragile(fragile);
        if (insuredValue != null) opts.setInsuredValue(insuredValue);
        if (handling != null) opts.setHandling(handling);
        BigDecimal result = calculator.calculateShippingCost(zone, new BigDecimal("3.0"), dimensions, opts, Carrier.DHL);
        assertEquals(expected, result);
    }
}
```

**Key decisions:**

- **Value set** `{DHL, UPS, FEDEX}` on the EU express medium row replaces the three "same rate" rows — all three produce `12.00` so the semantics are correct.
- **`@Description`** on `appliesSurcharges` records the fixed inputs (EU standard, 3.0 kg, DHL) that are hardcoded in the method body but affect outcomes.
- **`Dimensions` stays a column** in `appliesSurcharges` because the oversize row uses different dimensions `[120, 5, 5]` — it can't be a fixed constant.
- **Two null guards** remain in `appliesSurcharges` because `setInsuredValue(null)` and `setHandling(null)` may not be valid calls — this is setup plumbing, not branching test logic.
- The **dimensional weight** scenario stays in the base cost table since it affects which weight tier applies, not a surcharge.