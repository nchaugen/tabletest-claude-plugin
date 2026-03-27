Here's the test file:

```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Weekly pay calculation")
class WeeklyPayCalculatorTest {

    private final WeeklyPayCalculator calculator = new WeeklyPayCalculator();

    @TableTest("""
        Scenario                         | Weekday hrs | Sunday hrs | Holiday hrs | Rate           | Weekly pay?
        No hours worked                  | 0           | 0          | 0           | {15.00, 20.00} | 0.00
        Standard week, exactly 40        | 40          | 0          | 0           | 20.00          | 800.00
        Partial standard week            | 25          | 0          | 0           | 20.00          | 500.00
        Overtime: 5 extra hours          | 45          | 0          | 0           | 20.00          | 950.00
        Heavy overtime                   | 60          | 0          | 0           | 20.00          | 1400.00
        Sunday hours only                | 0           | 8          | 0           | 20.00          | 320.00
        Holiday hours only               | 0           | 0          | 8           | 20.00          | 320.00
        Sunday and holiday combined      | 0           | 4          | 4           | 20.00          | 320.00
        Overtime with Sunday and holiday | 45          | 4          | 2           | 20.00          | 1190.00
        Zero rate                        | 40          | 8          | 2           | 0.00           | 0.00
        """)
    void shouldCalculateWeeklyPay(int weekdayHours, int sundayHours, int holidayHours,
                                  BigDecimal rate, BigDecimal expectedPay) {
        BigDecimal actual = calculator.calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, rate);
        assertEquals(0, expectedPay.compareTo(actual),
            () -> "expected " + expectedPay + " but got " + actual);
    }

    @Test
    void shouldRejectNegativeHourlyRate() {
        assertThrows(IllegalArgumentException.class,
            () -> calculator.calculateWeeklyPay(40, 0, 0, new BigDecimal("-10.00")));
    }
}
```

**Key decisions:**

- **`{15.00, 20.00}` value set** on "No hours worked" — rate is irrelevant when there are no hours, so both values generate rows that assert `0.00`. This documents the "doesn't matter" relationship directly in the table.

- **"Zero rate" row** (`rate = 0.00`) — confirms a zero rate is accepted (not rejected like a negative rate) and produces zero pay. This is the boundary between valid and invalid rates.

- **Separate `@Test` for negative rate** — only one rejection scenario exists, so a table would add noise. Exception testing is isolated from the main pay calculation table, keeping each method's assertion logic uniform.

- **`compareTo` instead of `equals`** — `BigDecimal.equals` considers scale (`800.0` ≠ `800.00`); `compareTo` checks numeric value only.

**Dependency** (if not already present):
```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```