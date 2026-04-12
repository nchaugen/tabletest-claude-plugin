Here is the test class I'd write:

```java
import org.tabletest.junit.TableTest;
import org.junit.jupiter.api.Description;
import static org.junit.jupiter.api.Assertions.*;

public class WeeklyPayCalculatorTest {

    @Description("""
        Sunday hours = 0, holiday hours = 0 for all rows.
        Rate = 10.00 for all rows.
        """)
    @TableTest("""
        Scenario               | Weekday Hours | Weekly Pay?
        No hours worked        | 0             | 0.00
        Below overtime limit   | 20            | 200.00
        Exactly at limit       | 40            | 400.00
        One hour over limit    | 41            | 415.00
        Twenty hours over      | 60            | 700.00
        """)
    void weekdayOvertimeThreshold(int weekdayHours, double expectedPay) {
        assertEquals(expectedPay, WeeklyPayCalculator.calculate(weekdayHours, 0, 0, 10.00), 0.001);
    }

    @Description("""
        Weekday hours = 0 for all rows. Rate = 10.00 for all rows.
        """)
    @TableTest("""
        Scenario      | Sunday Hours | Holiday Hours | Weekly Pay?
        Sunday only   | 8            | 0             | 160.00
        Holiday only  | 0            | 8             | 160.00
        Both          | 4            | 4             | 160.00
        """)
    void doubleTimeRates(int sundayHours, int holidayHours, double expectedPay) {
        assertEquals(expectedPay, WeeklyPayCalculator.calculate(0, sundayHours, holidayHours, 10.00), 0.001);
    }

    @TableTest("""
        Scenario             | Weekday Hrs | Sunday Hrs | Holiday Hrs | Rate  | Weekly Pay?
        Standard week        | 40          | 0          | 0           | 15.00 | 600.00
        Overtime and Sunday  | 50          | 8          | 0           | 10.00 | 710.00
        Regular with holiday | 32          | 0          | 8           | 20.00 | 960.00
        Zero hours           | 0           | 0          | 0           | 25.00 | 0.00
        """)
    void combinedWeeklyPay(int weekdayHours, int sundayHours, int holidayHours, double rate, double expectedPay) {
        assertEquals(expectedPay, WeeklyPayCalculator.calculate(weekdayHours, sundayHours, holidayHours, rate), 0.001);
    }

    @TableTest("""
        Scenario       | Rate    | Throws?
        Negative rate  | -1.00   | IllegalArgumentException
        Very negative  | -100.00 | IllegalArgumentException
        """)
    void negativeRateRejected(double rate, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> WeeklyPayCalculator.calculate(40, 0, 0, rate));
    }
}
```

---

**Design rationale:**

**`weekdayOvertimeThreshold`** — isolates the overtime classification rule. Fixed rate (10.00) and no Sunday/holiday hours so the expected pay is purely traceable from weekday hours. Tests boundary at exactly 40h and just past it.

**`doubleTimeRates`** — isolates the Sunday/holiday 2× multiplier rule. Separate concern from overtime. The "Both" row (4+4=160) confirms Sunday and holiday are additive and each independently gets 2×.

**`combinedWeeklyPay`** — realistic combined scenarios. "Overtime and Sunday" (50h weekday + 8h Sunday, rate 10.00): 40×10 + 10×15 + 8×20 = 400 + 150 + 160 = **710**. "Regular with holiday" (32h weekday + 8h holiday, rate 20.00): 32×20 + 8×40 = 640 + 320 = **960**. "Zero hours" also covers the pay-floor guarantee.

**`negativeRateRejected`** — uses the `Throws?` column pattern so each row's expected outcome is visible in the table. Zero rate is not rejected (pay = 0.00, satisfied by "Zero hours" row above).