Here's the converted test:

```java
import org.tabletest.junit.TableTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShippingCostCalculatorTest {

    private final ShippingCostCalculator calculator = new ShippingCostCalculator();

    @TableTest("""
        Scenario                    | Region | Speed    | Weight | Dims          | Fragile | Insured value | Handling | Carrier           | Cost?
        EU standard light           | EU     | standard | 0.5    | [20, 15, 10]  | false   |               |          | DHL               | 5.00
        EU standard medium          | EU     | standard | 3.0    | [30, 20, 15]  | false   |               |          | DHL               | 7.50
        EU standard heavy           | EU     | standard | 10.0   | [40, 30, 20]  | false   |               |          | DHL               | 12.50
        EU standard very heavy      | EU     | standard | 25.0   | [50, 40, 30]  | false   |               |          | UPS               | 20.00
        EU express light            | EU     | express  | 0.5    | [20, 15, 10]  | false   |               |          | DHL               | 8.00
        EU express, all carriers    | EU     | express  | 3.0    | [30, 20, 15]  | false   |               |          | {DHL, UPS, FEDEX} | 12.00
        US standard light           | US     | standard | 0.5    | [20, 15, 10]  | false   |               |          | FEDEX             | 7.00
        US express heavy            | US     | express  | 10.0   | [40, 30, 20]  | false   |               |          | DHL               | 30.00
        Dimensional weight override | EU     | standard | 1.0    | [70, 50, 10]  | false   |               |          | DHL               | 12.50
        Fragile surcharge           | EU     | standard | 3.0    | [30, 20, 15]  | true    |               |          | DHL               | 8.625
        Insured surcharge           | EU     | standard | 3.0    | [30, 20, 15]  | false   | 500           |          | DHL               | 10.50
        Fragile and insured         | EU     | standard | 3.0    | [30, 20, 15]  | true    | 200           |          | DHL               | 11.625
        Hazmat handling             | EU     | standard | 3.0    | [30, 20, 15]  | false   |               | hazmat   | DHL               | 15.50
        Oversize surcharge          | EU     | standard | 3.0    | [120, 5, 5]   | false   |               |          | DHL               | 17.50
        """)
    void shouldCalculateShippingCost(String region, String speed, double weight,
                                     List<Integer> dims, boolean fragile,
                                     Integer insuredValue, String handling,
                                     Carrier carrier, double expectedCost) {
        var zone = new ShippingZone();
        zone.setRegion(region);
        zone.setSpeed(speed);

        var opts = new PackageOptions();
        opts.setFragile(fragile);
        opts.setInsuredValue(insuredValue);
        opts.setHandling(handling);

        assertEquals(expectedCost, calculator.calculateShippingCost(zone, weight, dims, opts, carrier), 0.001);
    }
}
```

A few things to note about the conversion:

**Value set consolidation**: The original had four redundant rows — "EU express medium" (UPS) plus three "same rate" rows (DHL/UPS/FEDEX, all → 12.00). These express a single claim: *carrier doesn't affect the rate for EU express*. The `{DHL, UPS, FEDEX}` value set captures that intent in one row and still generates three test executions.

**`Dims` as `List<Integer>`**: Rather than three separate columns, `[20, 15, 10]` maps directly to `List<Integer>` via built-in JUnit conversion — cleaner and matches how the argument is actually used.

**Blank cells for optional inputs**: `Insured value` and `Handling` use blank (null) for rows where those options don't apply, with `Integer` and `String` parameter types to accept null. `Carrier` enum values drop the `Carrier.` prefix — JUnit's built-in enum conversion handles that.

**Dependency**: If it's not in your `pom.xml` yet, you'll need `org.tabletest:tabletest-junit` with a JUnit Jupiter 5.11+ engine.