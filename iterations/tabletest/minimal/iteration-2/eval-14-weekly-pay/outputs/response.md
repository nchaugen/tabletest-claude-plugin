Here is the complete test class:

```java
package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import static org.junit.jupiter.api.Assertions.*;

public class WeeklyPayCalculatorTest {

    @DisplayName("Weekday overtime threshold")
    @Description("""
            Hourly rate is $10.00 for all rows. No sunday or holiday hours.
            The overtime threshold is 40 hours: weekday hours up to 40 are paid at
            the base rate (1×); hours beyond 40 are paid at time-and-a-half (1.5×).
            """)
    @TableTest("""
            Scenario              | Weekday hours | Pay?
            No hours worked       | 0             | 0.00
            One below threshold   | 39            | 390.00
            At the 40-hour limit  | 40            | 400.00
            One overtime hour     | 41            | 415.00
            Twenty overtime hours | 60            | 700.00
            """)
    void weekdayOvertimeThreshold(int weekdayHours, double expectedPay) {
        assertEquals(expectedPay, WeeklyPayCalculator.calculate(weekdayHours, 0, 0, 10.00), 0.001);
    }

    @DisplayName("Sunday and holiday double-time rate")
    @Description("""
            Hourly rate is $10.00 for all rows. No weekday hours.
            Sunday and holiday hours always attract the 2× rate,
            regardless of how many weekday hours were worked.
            """)
    @TableTest("""
            Scenario                | Sunday hours | Holiday hours | Pay?
            Sunday only             | 8            | 0             | 160.00
            Holiday only            | 0            | 6             | 120.00
            Both sunday and holiday | 5            | 4             | 180.00
            No special hours        | 0            | 0             | 0.00
            """)
    void sundayAndHolidayDoubleTimeRate(int sundayHours, int holidayHours, double expectedPay) {
        assertEquals(expectedPay, WeeklyPayCalculator.calculate(0, sundayHours, holidayHours, 10.00), 0.001);
    }

    @DisplayName("Total weekly pay")
    @Description("""
            Total pay is clamped to zero. With a non-negative rate and non-negative hours,
            the floor applies only as a safeguard against floating-point edge cases.
            Open: is a rate of zero valid? Treated as valid here (all hours produce zero pay).
            """)
    @TableTest("""
            Scenario                 | Weekday | Sunday | Holiday | Rate           | Pay?
            Zero hours, any rate     | 0       | 0      | 0       | {10.00, 20.00} | 0.00
            Zero rate                | 40      | 0      | 0       | 0.00           | 0.00
            Standard 40-hour week    | 40      | 0      | 0       | 10.00          | 400.00
            With overtime            | 50      | 0      | 0       | 10.00          | 550.00
            Sunday adds double-time  | 40      | 8      | 0       | 10.00          | 560.00
            Holiday adds double-time | 40      | 0      | 4       | 10.00          | 480.00
            Higher rate scales all   | 20      | 0      | 0       | 20.00          | 400.00
            Full mix with overtime   | 45      | 4      | 2       | 20.00          | 1190.00
            """)
    void totalWeeklyPay(int weekdayHours, int sundayHours, int holidayHours, double rate, double expectedPay) {
        assertEquals(expectedPay, WeeklyPayCalculator.calculate(weekdayHours, sundayHours, holidayHours, rate), 0.001);
    }

    @DisplayName("Negative hourly rate rejected")
    @TableTest("""
            Scenario                  | Weekday | Sunday | Holiday | Rate   | Throws?
            Negative rate, no hours   | 0       | 0      | 0       | -1.00  | IllegalArgumentException
            Negative rate, with hours | 40      | 0      | 0       | -15.00 | IllegalArgumentException
            """)
    void negativeRateRejected(int weekdayHours, int sundayHours, int holidayHours,
                              double rate, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> WeeklyPayCalculator.calculate(weekdayHours, sundayHours, holidayHours, rate));
    }

    @TypeConverter
    public static Class<? extends Exception> parseException(String value) throws ClassNotFoundException {
        String className = value.contains(".") ? value : "java.lang." + value;
        return Class.forName(className).asSubclass(Exception.class);
    }
}
```

**Four tables, one concern each:**

- **Weekday overtime threshold** — boundary tests at 39/40/41 hours; isolates the classification rule with a fixed rate so expected pay directly traces from hours worked.
- **Sunday and holiday double-time rate** — tests both types independently and together, with no weekday hours to avoid mixing concerns.
- **Total weekly pay** — integration arithmetic. Value set `{10.00, 20.00}` on the zero-hours row asserts the floor holds regardless of rate. The "full mix" row (45 weekday + 4 sunday + 2 holiday at $20) = $800 + $150 + $160 + $80 = $1,190.
- **Negative hourly rate rejected** — `@TypeConverter` lets the table use `IllegalArgumentException` instead of the fully-qualified `java.lang.IllegalArgumentException`.

The assumed API is `WeeklyPayCalculator.calculate(int weekdayHours, int sundayHours, int holidayHours, double hourlyRate)` throwing `IllegalArgumentException` for negative rates.