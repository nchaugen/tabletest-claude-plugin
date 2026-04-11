Here's the test class:

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WeeklyPayCalculator")
class WeeklyPayCalculatorTest {

    private final WeeklyPayCalculator calculator = new WeeklyPayCalculator();

    @DisplayName("Weekly pay calculation")
    @Description("""
            Overtime threshold is 40 weekday hours. Hours beyond 40 are paid at 1.5× the base rate.
            Sunday and holiday hours are always paid at 2× the base rate regardless of weekday hours worked.
            Sunday and holiday hours do not count toward the 40-hour overtime threshold.
            Total pay is floored at zero — with validated non-negative rates and non-negative hours
            this floor should not be reachable in practice; it is a defensive guarantee.
            """)
    @TableTest("""
            Scenario                                  | Weekday hrs | Sunday hrs | Holiday hrs | Rate  | Regular pay? | Overtime pay? | Sunday pay? | Holiday pay? | Total pay?
            No hours worked                           | 0           | 0          | 0           | 20.00 | 0.00         | 0.00          | 0.00        | 0.00         | 0.00
            Under overtime threshold                  | 30          | 0          | 0           | 20.00 | 600.00       | 0.00          | 0.00        | 0.00         | 600.00
            Just under overtime threshold             | 39          | 0          | 0           | 20.00 | 780.00       | 0.00          | 0.00        | 0.00         | 780.00
            At overtime threshold                     | 40          | 0          | 0           | 20.00 | 800.00       | 0.00          | 0.00        | 0.00         | 800.00
            Just over overtime threshold              | 41          | 0          | 0           | 20.00 | 800.00       | 30.00         | 0.00        | 0.00         | 830.00
            Heavy overtime                            | 60          | 0          | 0           | 20.00 | 800.00       | 600.00        | 0.00        | 0.00         | 1400.00
            Sunday hours only                         | 0           | 8          | 0           | 20.00 | 0.00         | 0.00          | 320.00      | 0.00         | 320.00
            Holiday hours only                        | 0           | 0          | 8           | 20.00 | 0.00         | 0.00          | 0.00        | 320.00       | 320.00
            Sunday and holiday don't trigger overtime | 35          | 8          | 8           | 20.00 | 700.00       | 0.00          | 320.00      | 320.00       | 1340.00
            All hour types with weekday overtime      | 45          | 8          | 8           | 20.00 | 800.00       | 150.00        | 320.00      | 320.00       | 1590.00
            Zero rate                                 | 40          | 8          | 8           | 0.00  | 0.00         | 0.00          | 0.00        | 0.00         | 0.00
            """)
    void shouldCalculateWeeklyPay(int weekdayHrs, int sundayHrs, int holidayHrs, BigDecimal rate,
                                   BigDecimal regularPay, BigDecimal overtimePay, BigDecimal sundayPay,
                                   BigDecimal holidayPay, BigDecimal totalPay) {
        WeeklyPayCalculator.WeeklyPay result = calculator.calculate(weekdayHrs, sundayHrs, holidayHrs, rate);
        assertAll(
            () -> assertMoney(regularPay,  result.regularPay(),  "regular pay"),
            () -> assertMoney(overtimePay, result.overtimePay(), "overtime pay"),
            () -> assertMoney(sundayPay,   result.sundayPay(),   "Sunday pay"),
            () -> assertMoney(holidayPay,  result.holidayPay(),  "holiday pay"),
            () -> assertMoney(totalPay,    result.totalPay(),    "total pay")
        );
    }

    @DisplayName("Rate validation")
    @TableTest("""
            Scenario         | Weekday hrs | Sunday hrs | Holiday hrs | Rate  | Throws?
            Negative rate    | 40          | 0          | 0           | -1.00 | IllegalArgumentException
            Negative decimal | 0           | 8          | 0           | -0.01 | IllegalArgumentException
            """)
    void shouldRejectNegativeRate(int weekdayHrs, int sundayHrs, int holidayHrs, BigDecimal rate,
                                   Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> calculator.calculate(weekdayHrs, sundayHrs, holidayHrs, rate));
    }

    // BigDecimal.equals() is scale-sensitive; compareTo() is not.
    private static void assertMoney(BigDecimal expected, BigDecimal actual, String label) {
        assertEquals(0, actual.compareTo(expected),
            () -> label + ": expected " + expected + " but was " + actual);
    }

    @TypeConverter
    static Class<? extends Exception> toExceptionClass(String name) throws ClassNotFoundException {
        try {
            return Class.forName(name).asSubclass(Exception.class);
        } catch (ClassNotFoundException e) {
            return Class.forName("java.lang." + name).asSubclass(Exception.class);
        }
    }
}
```

**Two tables:**

**`shouldCalculateWeeklyPay`** — 11 rows covering:
- The 39/40/41 boundary rows make the overtime threshold explicit
- `Sunday and holiday don't trigger overtime` (35 weekday hrs + premium hrs) — guards against an implementation that counts all hours toward the threshold
- Traceability columns (`Regular pay?`, `Overtime pay?`, `Sunday pay?`, `Holiday pay?`) let you pinpoint which component is wrong when a row fails, not just that the total is off

**`shouldRejectNegativeRate`** — validates the rejection rule. The `@TypeConverter` resolves `IllegalArgumentException` from the table string to the `Class<?>` parameter without needing fully-qualified names in the table.

**Assumed API:** `WeeklyPayCalculator.calculate(int, int, int, BigDecimal)` returns a `WeeklyPay` record exposing `regularPay()`, `overtimePay()`, `sundayPay()`, `holidayPay()`, `totalPay()`. Adjust method/type names to match your implementation.