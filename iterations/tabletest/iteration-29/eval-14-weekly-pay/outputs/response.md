Here are the tests:

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

class WeeklyPayCalculatorTest {

    @DisplayName("Weekday pay: regular and overtime hours")
    @TableTest("""
        Scenario              | Weekday hrs | Rate  | Pay?
        No hours              | 0           | 20.00 | 0.00
        Under threshold       | 39          | 20.00 | 780.00
        At threshold          | 40          | 20.00 | 800.00
        One hour overtime     | 41          | 20.00 | 830.00
        Heavy overtime        | 50          | 20.00 | 1100.00
        """)
    // Overtime threshold: 40 hours. Hours up to 40 → 1× rate; hours above 40 → 1.5× rate.
    // e.g. 41 hrs at $20.00 = (40 × $20.00) + (1 × $30.00) = $830.00
    void shouldCalculateWeekdayPay(int weekdayHours, BigDecimal rate, BigDecimal expectedPay) {
        BigDecimal pay = new WeeklyPayCalculator().calculate(weekdayHours, 0, 0, rate);
        assertEquals(0, expectedPay.compareTo(pay));
    }

    @DisplayName("Weekly pay with Sunday and holiday hours")
    @TableTest("""
        Scenario                | Weekday hrs | Sunday hrs | Holiday hrs | Rate  | Pay?
        No hours                | 0           | 0          | 0           | 20.00 | 0.00
        Weekday only            | 40          | 0          | 0           | 20.00 | 800.00
        Sunday only             | 0           | 8          | 0           | 20.00 | 320.00
        Holiday only            | 0           | 0          | 8           | 20.00 | 320.00
        Sunday and holiday      | 0           | 4          | 4           | 20.00 | 320.00
        Weekday and Sunday      | 40          | 8          | 0           | 20.00 | 1120.00
        All hour types          | 40          | 8          | 4           | 20.00 | 1280.00
        All types with overtime | 45          | 8          | 4           | 20.00 | 1430.00
        Zero rate               | 40          | 8          | 4           | 0.00  | 0.00
        """)
    // Sunday and holiday hours are always paid at 2× the base rate, regardless of total hours worked.
    // Total pay cannot go below zero (floor applied after summing all components).
    void shouldCalculateWeeklyPay(int weekdayHours, int sundayHours, int holidayHours,
                                   BigDecimal rate, BigDecimal expectedPay) {
        BigDecimal pay = new WeeklyPayCalculator().calculate(weekdayHours, sundayHours, holidayHours, rate);
        assertEquals(0, expectedPay.compareTo(pay));
    }

    @DisplayName("Reject negative hourly rate")
    @TableTest("""
        Scenario       | Weekday hrs | Sunday hrs | Holiday hrs | Rate   | Throws?
        Negative rate  | 40          | 0          | 0           | -20.00 | IllegalArgumentException
        Just negative  | 0           | 0          | 0           | -0.01  | IllegalArgumentException
        """)
    void shouldRejectNegativeRate(int weekdayHours, int sundayHours, int holidayHours,
                                   BigDecimal rate, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> new WeeklyPayCalculator().calculate(weekdayHours, sundayHours, holidayHours, rate));
    }
}
```

**Three tables, three concerns:**

- **`shouldCalculateWeekdayPay`** — pins the 39/40/41 boundary where overtime kicks in. Sunday and holiday hours are hardcoded to 0 so this table is purely about the threshold.

- **`shouldCalculateWeeklyPay`** — tests every combination of hour types. The "Sunday and holiday" row (4+4 hours = $320) confirms both types are independently 2× and additive. The "All types with overtime" row (45 wkd + 8 sun + 4 hol) verifies the full formula end-to-end: `(40×$20) + (5×$30) + (8×$40) + (4×$40) = $1,430`.

- **`shouldRejectNegativeRate`** — uses a `Throws?` column rather than hardcoding the exception. "Just negative" (-0.01) tests the boundary of the rate rule.

**Notes:**
- `assertEquals(0, expectedPay.compareTo(pay))` is used instead of `assertEquals(expectedPay, pay)` to avoid BigDecimal scale mismatches (`800.0` vs `800.00`).
- The floor rule (pay ≥ 0) can't be triggered with valid non-negative inputs; it's documented in the `@Description` comment on `shouldCalculateWeeklyPay` as a safeguard.
- Adjust `new WeeklyPayCalculator().calculate(...)` to match whatever class/method name your implementation uses.