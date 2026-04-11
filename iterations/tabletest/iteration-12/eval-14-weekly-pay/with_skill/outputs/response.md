Here's the test class:

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WeeklyPayCalculatorTest {

    @DisplayName("Weekly pay calculation")
    @Description("""
        Overtime threshold: 40 weekday hours.
        Regular pay:  weekday hours (up to 40) × rate.
        Overtime pay: weekday hours beyond 40 × 1.5 × rate.
        Premium pay:  (Sunday hours + holiday hours) × 2 × rate.
        Weekly pay = regular + overtime + premium, floored at 0.00.
        Blank intermediate cells represent 0.00 (no contribution from that component).
        Open question: when weekday hours are negative, does the implementation return
        the raw product (e.g. -50.00) as regularPay before the floor, or clamp hours
        at 0 first? Table assumes the raw product is returned as the intermediate value.
        """)
    @TableTest("""
        Scenario                  | Weekday hrs | Sunday hrs | Holiday hrs | Rate           | Regular pay? | Overtime pay? | Premium pay? | Weekly pay?
        Part-time                 | 20          |            |             | 15.00          | 300.00       |               |              | 300.00
        Exactly 40 weekday hours  | 40          |            |             | 15.00          | 600.00       |               |              | 600.00
        One hour overtime         | 41          |            |             | 20.00          | 800.00       | 30.00         |              | 830.00
        Ten hours overtime        | 50          |            |             | 10.00          | 400.00       | 150.00        |              | 550.00
        Sunday shift only         |             | 8          |             | 15.00          |              |               | 240.00       | 240.00
        Holiday shift only        |             |            | 8           | 15.00          |              |               | 240.00       | 240.00
        Weekday with Sunday       | 35          | 8          |             | 20.00          | 700.00       |               | 320.00       | 1020.00
        Full combined week        | 45          | 8          | 8           | 20.00          | 800.00       | 150.00        | 640.00       | 1590.00
        Zero hours, any rate      | 0           | 0          | 0           | {10.00, 20.00} |              |               |              | 0.00
        Negative weekday floored  | -5          |            |             | 10.00          | -50.00       |               |              | 0.00
        """)
    void shouldCalculateWeeklyPay(Integer weekdayHrs, Integer sundayHrs, Integer holidayHrs,
            BigDecimal rate, BigDecimal regularPay, BigDecimal overtimePay,
            BigDecimal premiumPay, BigDecimal weeklyPay) {
        WeeklyPay result = WeeklyPayCalculator.calculate(weekdayHrs, sundayHrs, holidayHrs, rate);
        assertAll(
            () -> assertEquals(orZero(regularPay), result.regularPay()),
            () -> assertEquals(orZero(overtimePay), result.overtimePay()),
            () -> assertEquals(orZero(premiumPay), result.premiumPay()),
            () -> assertEquals(weeklyPay, result.total())
        );
    }

    @DisplayName("Invalid pay inputs")
    @TableTest("""
        Scenario       | Weekday hrs | Sunday hrs | Holiday hrs | Rate             | Throws?
        Negative rate  | 40          |            |             | {-0.01, -100.00} | IllegalArgumentException
        """)
    void shouldRejectNegativeRate(Integer weekdayHrs, Integer sundayHrs, Integer holidayHrs,
            BigDecimal rate, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> WeeklyPayCalculator.calculate(weekdayHrs, sundayHrs, holidayHrs, rate));
    }

    private static BigDecimal orZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
```

**Design notes:**

- **Two tables**: pay calculation and invalid inputs — separated because error rows can't share assertion logic with the pay rows.
- **Three intermediate columns** (`Regular pay?`, `Overtime pay?`, `Premium pay?`) let you trace which rule contributes to the total. When a row fails, you'll see exactly where the discrepancy is.
- **Blank cells** for Sunday/Holiday hrs when not relevant (requires `Integer`, not `int`, so null is representable). The `orZero` helper treats blank intermediate expectation cells as 0.00 without putting if-logic inside the test method.
- **Overtime boundary** is covered at exactly 40 (no OT) and 41 (OT kicks in) — not just mid-range values.
- **Combined scenario** (`Full combined week`) exercises all three components simultaneously.
- **Value set** on the zero-hours rate column (`{10.00, 20.00}`) asserts the result is 0.00 regardless of rate — semantically correct since 0 × anything = 0.
- **Value set** on the negative rate (`{-0.01, -100.00}`) asserts both throw — the sign matters, not the magnitude.
- The open question about negative hours intermediate state is surfaced in `@Description` rather than silently resolved.

**Dependency** (if not already present):
```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```