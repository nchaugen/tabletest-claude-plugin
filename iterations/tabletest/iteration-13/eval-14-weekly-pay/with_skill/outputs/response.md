Here's the complete test class:

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WeeklyPayCalculatorTest {

    @DisplayName("Weekly pay calculation")
    @TableTest("""
        Scenario                          | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Weekly Pay?
        Partial week, no extras           | 32            |              |               | 10.00       | 320.00
        Exactly at overtime threshold     | 40            |              |               | 10.00       | 400.00
        First hour of overtime            | 41            |              |               | 10.00       | 415.00
        10 hours overtime                 | 50            |              |               | 10.00       | 550.00
        Sunday hours only                 |               | 8            |               | 10.00       | 160.00
        Holiday hours only                |               |              | 8             | 10.00       | 160.00
        Full week plus Sunday             | 40            | 8            |               | 10.00       | 560.00
        Overtime plus holiday             | 45            |              | 8             | 10.00       | 635.00
        All three types with overtime     | 50            | 8            | 8             | 10.00       | 870.00
        No hours worked                   | 0             | 0            | 0             | 10.00       | 0.00
        Zero hourly rate                  | 40            | 8            | 8             | 0.00        | 0.00
        """)
    void shouldCalculateWeeklyPay(Integer weekdayHours, Integer sundayHours, Integer holidayHours,
                                  BigDecimal hourlyRate, BigDecimal expectedPay) {
        int weekday = weekdayHours != null ? weekdayHours : 0;
        int sunday  = sundayHours  != null ? sundayHours  : 0;
        int holiday = holidayHours != null ? holidayHours : 0;

        BigDecimal actual = WeeklyPayCalculator.calculateWeeklyPay(weekday, sunday, holiday, hourlyRate);

        assertEquals(0, expectedPay.compareTo(actual),
                "Expected " + expectedPay + " but got " + actual);
    }

    @DisplayName("Negative hourly rate rejection")
    @TableTest("""
        Scenario                                 | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Throws?
        Negative rate regardless of hours worked | {0, 40}       |              |               | -10.00      | IllegalArgumentException
        """)
    void shouldRejectNegativeRate(Integer weekdayHours, Integer sundayHours, Integer holidayHours,
                                  BigDecimal hourlyRate, Class<? extends Exception> throws_) {
        int weekday = weekdayHours != null ? weekdayHours : 0;
        int sunday  = sundayHours  != null ? sundayHours  : 0;
        int holiday = holidayHours != null ? holidayHours : 0;

        assertThrows(throws_, () ->
                WeeklyPayCalculator.calculateWeeklyPay(weekday, sunday, holiday, hourlyRate));
    }

    @TypeConverter
    Class<? extends Exception> toExceptionClass(String name) throws ClassNotFoundException {
        return Class.forName("java.lang." + name).asSubclass(Exception.class);
    }
}
```

**Key decisions:**

- **Two tables** — pay calculation and rejection use different assertion logic (`assertEquals` vs `assertThrows`), so they're kept separate
- **Boundary rows** — 40 (threshold, no overtime) and 41 (first overtime hour) are both present to pin down the inclusive/exclusive behaviour at the boundary
- **Blank Sunday/Holiday cells** → `Integer` params (nullable), defaulted to `0` in the method body; using `Integer` instead of `int` allows null
- **Value set on rejection table** — `{0, 40}` expresses that the exception fires regardless of hours worked, generating two test cases from one row
- **`compareTo` for BigDecimal equality** — avoids false failures from scale differences (e.g., `400` vs `400.00`)
- **`@TypeConverter`** — converts the string `"IllegalArgumentException"` to the class object; keeps the table readable without FQCN