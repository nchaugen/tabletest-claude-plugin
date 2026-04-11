Now I have everything I need. Here's the design reasoning, then the code:

**Key design decisions:**
- `ShippingZone` splits into `Region` + `Speed` columns — no TypeConverter needed, clean object construction in the method
- `PackageOptions` becomes a single `Options` column with a TypeConverter — most rows are blank (no options), and the few that aren't are compact and readable (`fragile`, `insured:500`, `fragile,insured:200`, `hazmat`)
- The four "same rate" rows (EU express medium for DHL/UPS/FEDEX, plus the original EU express medium row for UPS) collapse into one row with value set `{DHL, UPS, FEDEX}` — from 4 rows to 1

```kotlin
import org.tabletest.junit.TableTest
import org.tabletest.junit.TypeConverter
import java.math.BigDecimal

class ShippingCostCalculatorTest {

    private val calculator = ShippingCostCalculator()

    @Description("""
        Options column: blank for standard shipment, or a comma-separated list of:
          fragile              — adds fragile handling surcharge
          insured:<value>      — adds insurance surcharge for declared value
          <handling-code>      — sets special handling (e.g. hazmat)
        Oversize surcharge is triggered by a dimension exceeding the threshold (see dims column).
        Dimensional weight is calculated from dims and may override actual weight.
        """)
    @TableTest("""
        Scenario                    | Region | Speed    | Weight | Dims          | Options             | Carrier           | Expected Cost?
        EU standard light           | EU     | standard | 0.5    | [20, 15, 10]  |                     | DHL               | 5.00
        EU standard medium          | EU     | standard | 3.0    | [30, 20, 15]  |                     | DHL               | 7.50
        EU standard heavy           | EU     | standard | 10.0   | [40, 30, 20]  |                     | DHL               | 12.50
        EU standard very heavy      | EU     | standard | 25.0   | [50, 40, 30]  |                     | UPS               | 20.00
        EU express light            | EU     | express  | 0.5    | [20, 15, 10]  |                     | DHL               | 8.00
        EU express medium           | EU     | express  | 3.0    | [30, 20, 15]  |                     | {DHL, UPS, FEDEX} | 12.00
        US standard light           | US     | standard | 0.5    | [20, 15, 10]  |                     | FEDEX             | 7.00
        US express heavy            | US     | express  | 10.0   | [40, 30, 20]  |                     | DHL               | 30.00
        Dimensional weight override | EU     | standard | 1.0    | [70, 50, 10]  |                     | DHL               | 12.50
        Fragile surcharge           | EU     | standard | 3.0    | [30, 20, 15]  | fragile             | DHL               | 8.625
        Insured surcharge           | EU     | standard | 3.0    | [30, 20, 15]  | insured:500         | DHL               | 10.50
        Fragile and insured         | EU     | standard | 3.0    | [30, 20, 15]  | fragile,insured:200 | DHL               | 11.625
        Hazmat handling             | EU     | standard | 3.0    | [30, 20, 15]  | hazmat              | DHL               | 15.50
        Oversize surcharge          | EU     | standard | 3.0    | [120, 5, 5]   |                     | DHL               | 17.50
        """)
    fun `should calculate shipping cost`(
        region: String,
        speed: String,
        weight: BigDecimal,
        dims: List<Int>,
        opts: PackageOptions,
        carrier: Carrier,
        expectedCost: BigDecimal
    ) {
        val zone = ShippingZone(region = region, speed = speed)
        calculator.calculateShippingCost(zone, weight, dims, opts, carrier) shouldBe expectedCost
    }

    companion object {
        @JvmStatic
        @TypeConverter
        fun parsePackageOptions(value: String?): PackageOptions {
            val opts = PackageOptions()
            if (value.isNullOrBlank()) return opts
            for (part in value.split(",")) {
                when {
                    part == "fragile" -> opts.isFragile = true
                    part.startsWith("insured:") -> opts.insuredValue = part.substringAfter(":").toInt()
                    else -> opts.handling = part
                }
            }
            return opts
        }
    }
}
```

**Dependency** — if `org.tabletest:tabletest-junit` isn't in your build file yet, add it:

```groovy
// Gradle
testImplementation "org.tabletest:tabletest-junit:VERSION"
```
```xml
<!-- Maven -->
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```

**What changed from the original:**
- 17 rows → 14 rows: the four "same rate" rows (EU express medium for DHL, UPS, FEDEX, plus the duplicate UPS row) collapse into one value set row — `{DHL, UPS, FEDEX}` documents that the carrier doesn't affect cost for this scenario
- The `if (fragile)` / null-check boilerplate is gone from the test method — the TypeConverter handles it, keeping the method to a single `calculateShippingCost(…) shouldBe expectedCost` call