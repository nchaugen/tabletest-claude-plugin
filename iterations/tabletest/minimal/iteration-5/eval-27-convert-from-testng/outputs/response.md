Here is the converted test. I'll present it directly since file writes aren't permitted in this environment:

```java
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShippingCostCalculatorTest {

    private ShippingCostCalculator calculator = new ShippingCostCalculator();

    // ------------------------------------------------------------------
    // Base rate: zone × service level × weight
    // ------------------------------------------------------------------

    @TableTest("""
        Scenario               | Zone        | Weight | Dimensions    | Carrier | Cost?
        EU standard light      | EU/standard | 0.5    | [20, 15, 10]  | DHL     | 5.00
        EU standard medium     | EU/standard | 3.0    | [30, 20, 15]  | DHL     | 7.50
        EU standard heavy      | EU/standard | 10.0   | [40, 30, 20]  | DHL     | 12.50
        EU standard very heavy | EU/standard | 25.0   | [50, 40, 30]  | UPS     | 20.00
        EU express light       | EU/express  | 0.5    | [20, 15, 10]  | DHL     | 8.00
        EU express medium      | EU/express  | 3.0    | [30, 20, 15]  | UPS     | 12.00
        US standard light      | US/standard | 0.5    | [20, 15, 10]  | FEDEX   | 7.00
        US express heavy       | US/express  | 10.0   | [40, 30, 20]  | DHL     | 30.00
        """)
    void baseRateByZoneAndWeight(ShippingZone zone, BigDecimal weight, List<Integer> dimensions,
                                 Carrier carrier, BigDecimal cost) {
        BigDecimal result = calculator.calculateShippingCost(zone, weight, dimensions, new PackageOptions(), carrier);
        assertEquals(cost, result);
    }

    // ------------------------------------------------------------------
    // Dimensional weight override
    // ------------------------------------------------------------------

    @TableTest("""
        Scenario                   | Zone        | Actual Weight | Dimensions   | Carrier | Cost?
        Large dims override weight | EU/standard | 1.0           | [70, 50, 10] | DHL     | 12.50
        """)
    void dimensionalWeightOverride(ShippingZone zone, BigDecimal weight, List<Integer> dimensions,
                                   Carrier carrier, BigDecimal cost) {
        BigDecimal result = calculator.calculateShippingCost(zone, weight, dimensions, new PackageOptions(), carrier);
        assertEquals(cost, result);
    }

    // ------------------------------------------------------------------
    // Surcharges applied on top of base rate
    // ------------------------------------------------------------------

    @TableTest("""
        Scenario            | Zone        | Weight | Dimensions   | Fragile | Insured Value | Handling | Carrier | Cost?
        No surcharges       | EU/standard | 3.0    | [30, 20, 15] | false   |               |          | DHL     | 7.50
        Fragile             | EU/standard | 3.0    | [30, 20, 15] | true    |               |          | DHL     | 8.625
        Insured             | EU/standard | 3.0    | [30, 20, 15] | false   | 500           |          | DHL     | 10.50
        Fragile and insured | EU/standard | 3.0    | [30, 20, 15] | true    | 200           |          | DHL     | 11.625
        Hazmat handling     | EU/standard | 3.0    | [30, 20, 15] | false   |               | hazmat   | DHL     | 15.50
        Oversize            | EU/standard | 3.0    | [120, 5, 5]  | false   |               |          | DHL     | 17.50
        """)
    void surcharges(ShippingZone zone, BigDecimal weight, List<Integer> dimensions,
                    boolean fragile, Integer insuredValue, String handling,
                    Carrier carrier, BigDecimal cost) {
        PackageOptions opts = new PackageOptions();
        if (fragile) opts.setFragile(true);
        if (insuredValue != null) opts.setInsuredValue(insuredValue);
        if (handling != null) opts.setHandling(handling);
        BigDecimal result = calculator.calculateShippingCost(zone, weight, dimensions, opts, carrier);
        assertEquals(cost, result);
    }

    // ------------------------------------------------------------------
    // Carrier-agnostic pricing: cost is the same regardless of carrier
    // ------------------------------------------------------------------

    @TableTest("""
        Scenario        | Zone       | Weight | Dimensions   | Carrier | Cost?
        DHL same rate   | EU/express | 3.0    | [30, 20, 15] | DHL     | 12.00
        UPS same rate   | EU/express | 3.0    | [30, 20, 15] | UPS     | 12.00
        FEDEX same rate | EU/express | 3.0    | [30, 20, 15] | FEDEX   | 12.00
        """)
    void carrierAgnosticPricing(ShippingZone zone, BigDecimal weight, List<Integer> dimensions,
                                Carrier carrier, BigDecimal cost) {
        BigDecimal result = calculator.calculateShippingCost(zone, weight, dimensions, new PackageOptions(), carrier);
        assertEquals(cost, result);
    }

    // ------------------------------------------------------------------
    // Type converters
    // ------------------------------------------------------------------

    @TypeConverter
    public static ShippingZone parseShippingZone(String value) {
        String[] parts = value.split("/", 2);
        return new ShippingZone(parts[0], parts[1]);
    }
}
```

**Key decisions:**

**Decomposed into 4 concerns** rather than one monolithic table:
- `baseRateByZoneAndWeight` — the core pricing matrix
- `dimensionalWeightOverride` — a single-row table that's structurally distinct (actual vs. billed weight)
- `surcharges` — all the option-driven cost additions; the three sparse option columns (`Fragile`, `Insured Value`, `Handling`) are sparse but intentionally so — each row tests a specific surcharge combination
- `carrierAgnosticPricing` — documents the invariant that all three carriers produce the same rate for EU express medium

**Type conversion:**
- `ShippingZone` needs a `@TypeConverter` — the `"EU/standard"` notation is readable and avoids a separate Region + Level columns
- `Carrier` is an enum, JUnit converts it natively
- `BigDecimal` is converted natively from string literals
- `List<Integer>` works with JUnit's built-in collection conversion from `[n, n, n]` syntax
- `Integer` (boxed) for `insuredValue` so blank cells become `null`

**The `surcharges` method still has `if` branches** — this is necessary because `PackageOptions` construction is inherently conditional on which options are set. The columns represent the observable inputs; the construction is unavoidable plumbing. If `PackageOptions` had a builder or constructor accepting nullables, that could be collapsed further.