Here is the complete test class:

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WeeklyPayCalculatorTest {

    @TypeConverter
    static BigDecimal parseMoney(String value) {
        return new BigDecimal(value);
    }

    @TypeConverter
    static Class<? extends Exception> parseException(String name) throws ClassNotFoundException {
        return Class.forName(name.contains(".") ? name : "java.lang." + name)
                    .asSubclass(Exception.class);
    }

    @DisplayName("Weekday pay: regular and overtime threshold")
    @Description("""
            Sunday hours = 0 and holiday hours = 0 in all rows.
            Regular pay: first 40 weekday hours × rate.
            Overtime pay: hours above 40 × 1.5 × rate.
            Open: should fractional hours be supported, or are hours always whole integers?
            """)
    @TableTest("""
            Scenario           | Weekday hrs | Rate  | Weekly pay?
            No hours worked    | 0           | 20.00 | 0.00
            Zero hourly rate   | 40          | 0.00  | 0.00
            Under 40 hours     | 30          | 20.00 | 600.00
            Exactly 40 hours   | 40          | 20.00 | 800.00
            One hour overtime  | 41          | 20.00 | 830.00
            Ten hours overtime | 50          | 20.00 | 1100.00
            """)
    void weekdayPay(int weekdayHours, BigDecimal rate, BigDecimal expectedPay) {
        BigDecimal actual = WeeklyPayCalculator.calculate(weekdayHours, 0, 0, rate);
        assertThat(actual).isEqualByComparingTo(expectedPay);
    }

    @DisplayName("Premium pay: Sunday and holiday hours")
    @Description("""
            Weekday hours = 0 in all rows.
            Both Sunday and holiday hours are always paid at 2× the hourly rate, regardless of total hours.
            """)
    @TableTest("""
            Scenario                    | Sunday hrs | Holiday hrs | Rate  | Weekly pay?
            No premium hours            | 0          | 0           | 20.00 | 0.00
            Sunday hours only           | 8          | 0           | 20.00 | 320.00
            Holiday hours only          | 0          | 8           | 20.00 | 320.00
            Sunday and holiday combined | 8          | 8           | 20.00 | 640.00
            """)
    void premiumPay(int sundayHours, int holidayHours, BigDecimal rate, BigDecimal expectedPay) {
        BigDecimal actual = WeeklyPayCalculator.calculate(0, sundayHours, holidayHours, rate);
        assertThat(actual).isEqualByComparingTo(expectedPay);
    }

    @DisplayName("Total weekly pay: all hour types combined")
    @Description("""
            Rate = $20.00/hr in all rows. Verifies that regular, overtime, Sunday, and holiday
            pay components sum correctly when multiple hour types appear together.
            Open: can total pay go below zero? The requirement states a zero-pay floor,
            but no deductions are described — this constraint may be forward-looking.
            """)
    @TableTest("""
            Scenario                         | Weekday hrs | Sunday hrs | Holiday hrs | Weekly pay?
            Weekday only, no overtime        | 40          | 0          | 0           | 800.00
            Weekday and Sunday premium       | 40          | 8          | 0           | 1120.00
            Overtime and holiday premium     | 50          | 0          | 8           | 1420.00
            Overtime with Sunday and holiday | 45          | 4          | 4           | 1270.00
            """)
    void totalWeeklyPay(int weekdayHours, int sundayHours, int holidayHours, BigDecimal expectedPay) {
        BigDecimal rate = new BigDecimal("20.00");
        BigDecimal actual = WeeklyPayCalculator.calculate(weekdayHours, sundayHours, holidayHours, rate);
        assertThat(actual).isEqualByComparingTo(expectedPay);
    }

    @DisplayName("Negative hourly rate is rejected")
    @TableTest("""
            Scenario          | Rate  | Throws?
            Negative rate     | -5.00 | IllegalArgumentException
            One cent negative | -0.01 | IllegalArgumentException
            """)
    void negativeRateIsRejected(BigDecimal rate, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> WeeklyPayCalculator.calculate(40, 0, 0, rate));
    }
}
```

**Four tables, each with a distinct concern:**

- **`weekdayPay`** — focuses on the 40-hour threshold. Tests 0, under, exactly-at, one-over (boundary), and well-over 40. Also confirms `rate=0` is accepted and yields zero pay.
- **`premiumPay`** — confirms Sunday and holiday hours independently and in combination all use 2×. Weekday=0 in all rows (noted in `@Description`) keeps this table focused.
- **`totalWeeklyPay`** — verifies the components add up correctly across mixed scenarios. Rate is fixed at $20 (in `@Description`) since rate variation is already covered above.
- **`negativeRateIsRejected`** — the boundary at `rate=0` is tested as valid in `weekdayPay`; these two rows confirm the rejection side at `-0.01` and `-5.00`.

Two open questions surfaced in the `@Description` annotations: whether fractional hours should be supported, and when the "total ≥ 0" floor could ever trigger (no deductions are defined in the spec).