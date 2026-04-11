Here is the test class:

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WeeklyPayCalculatorTest {

    @DisplayName("Weekly pay calculation")
    @Description("""
            Overtime threshold: 40 weekday hours.
            Weekday hours up to 40: paid at 1× rate (regular).
            Weekday hours above 40: paid at 1.5× rate (overtime).
            Sunday hours: always 2× rate (double time).
            Holiday hours: always 2× rate (double time).
            Total pay is non-negative when rate ≥ 0 and hours ≥ 0.
            Open: whether fractional or negative hour values are valid inputs is not specified.
            """)
    @TableTest("""
            Scenario                         | Weekday hrs | Sunday hrs | Holiday hrs | Rate  | Weekly pay?
            No hours worked                  | 0           |            |             | 20.00 | 0.00
            Below overtime threshold         | 39          |            |             | 20.00 | 780.00
            At overtime threshold            | 40          |            |             | 20.00 | 800.00
            One hour over overtime threshold | 41          |            |             | 20.00 | 830.00
            Heavy overtime                   | 50          |            |             | 20.00 | 1100.00
            Sunday hours only                |             | 8          |             | 20.00 | 320.00
            Holiday hours only               |             |            | 8           | 20.00 | 320.00
            Weekday at limit plus Sunday     | 40          | 8          |             | 20.00 | 1120.00
            Overtime plus Sunday             | 45          | 8          |             | 20.00 | 1270.00
            All three types combined         | 45          | 8          | 8           | 20.00 | 1590.00
            Zero rate                        | 40          | 8          | 8           | 0.00  | 0.00
            """)
    void shouldCalculateWeeklyPay(Integer weekdayHrs, Integer sundayHrs, Integer holidayHrs,
                                   BigDecimal rate, BigDecimal weeklyPay) {
        int weekday = weekdayHrs != null ? weekdayHrs : 0;
        int sunday = sundayHrs != null ? sundayHrs : 0;
        int holiday = holidayHrs != null ? holidayHrs : 0;

        BigDecimal result = new WeeklyPayCalculator().calculate(weekday, sunday, holiday, rate);

        assertThat(result).isEqualByComparingTo(weeklyPay);
    }

    @DisplayName("Negative rate rejection")
    @TableTest("""
            Scenario       | Rate   | Throws?
            Negative rate  | -1.00  | IllegalArgumentException
            """)
    void shouldRejectNegativeRate(BigDecimal rate, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> new WeeklyPayCalculator().calculate(40, 0, 0, rate));
    }
}
```

**Two tables, two concerns:**

`shouldCalculateWeeklyPay` — 11 rows targeting every distinct rule:
- Rows 1–5 pin down the overtime threshold: below, at (40), and above (41, 50)
- Rows 6–7 verify Sunday and holiday are both 2×, independently
- Rows 8–10 verify the types combine additively
- Row 11 verifies zero rate produces zero pay (covers the "cannot go below zero" floor)

Blank cells for `sundayHrs`/`holidayHrs` signal "not part of this scenario" — the method handles `null → 0`. `isEqualByComparingTo` avoids BigDecimal scale mismatches.

`shouldRejectNegativeRate` — a single validation row. The hours passed are irrelevant; only the rate triggers the rejection.

**Dependency to add** (`org.tabletest:tabletest-junit`):
```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```