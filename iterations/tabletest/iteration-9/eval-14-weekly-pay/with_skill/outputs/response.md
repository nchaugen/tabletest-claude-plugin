Now I have what I need. Let me design the tables before writing code.

**Concerns to test:**
1. Pay calculation — covers regular pay (≤40h), overtime (>40h), Sunday/holiday premium, and combinations
2. Validation — negative rate rejection

**Traceability approach:** Include regular, overtime, and premium pay columns so failures pinpoint which component is wrong, not just that the total is wrong.

Let me verify the arithmetic for each row first:
- 45h weekday @ $20: regular = 40×20 = 800, OT = 5×1.5×20 = 150 → 950
- 8h Sunday @ $20: 8×2×20 = 320
- 45h + 8h sunday @ $20: 800 + 150 + 320 = 1270
- 45h + 4h sunday + 8h holiday @ $20: 800 + 150 + (4+8)×40 = 1430

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class WeeklyPayCalculatorTest {

    private final WeeklyPayCalculator calculator = new WeeklyPayCalculator();

    @DisplayName("Weekly pay calculation")
    @Description("""
        Rate is the base hourly rate. Weekday hours up to 40 are paid at rate;
        hours above 40 (overtime) are paid at 1.5× rate. Sunday and holiday
        hours are always paid at 2× rate, regardless of weekday hour count.
        Regular, Overtime, and Premium columns show the expected component
        breakdown to trace which part fails when the total is wrong.
        """)
    @TableTest("""
        Scenario                      | Weekday hrs | Sunday hrs | Holiday hrs | Rate  | Regular pay? | Overtime pay? | Premium pay? | Weekly pay?
        No hours worked               | 0           | 0          | 0           | 20.00 | 0.00         | 0.00          | 0.00         | 0.00
        Standard 40-hour week         | 40          | 0          | 0           | 20.00 | 800.00       | 0.00          | 0.00         | 800.00
        Just under overtime threshold | 39          | 0          | 0           | 20.00 | 780.00       | 0.00          | 0.00         | 780.00
        One hour overtime             | 41          | 0          | 0           | 20.00 | 800.00       | 30.00         | 0.00         | 830.00
        Five hours overtime           | 45          | 0          | 0           | 20.00 | 800.00       | 150.00        | 0.00         | 950.00
        Sunday hours only             | 0           | 8          | 0           | 20.00 | 0.00         | 0.00          | 320.00       | 320.00
        Holiday hours only            | 0           | 0          | 8           | 20.00 | 0.00         | 0.00          | 320.00       | 320.00
        Sunday and holiday hours      | 0           | 4          | 4           | 20.00 | 0.00         | 0.00          | 320.00       | 320.00
        Regular week plus Sunday      | 40          | 8          | 0           | 20.00 | 800.00       | 0.00          | 320.00       | 1120.00
        Overtime plus Sunday          | 45          | 8          | 0           | 20.00 | 800.00       | 150.00        | 320.00       | 1270.00
        All three hour types          | 45          | 4          | 8           | 20.00 | 800.00       | 150.00        | 480.00       | 1430.00
        Partial weekday week          | 20          | 0          | 0           | 15.00 | 300.00       | 0.00          | 0.00         | 300.00
        Zero rate                     | 40          | 8          | 4           | 0.00  | 0.00         | 0.00          | 0.00         | 0.00
        """)
    void shouldCalculateWeeklyPay(int weekdayHrs, int sundayHrs, int holidayHrs, BigDecimal rate,
                                   BigDecimal regularPay, BigDecimal overtimePay,
                                   BigDecimal premiumPay, BigDecimal weeklyPay) {
        WeeklyPayResult result = calculator.calculate(weekdayHrs, sundayHrs, holidayHrs, rate);
        assertAll(
            () -> assertEquals(regularPay,  result.regularPay(),  "regular pay"),
            () -> assertEquals(overtimePay, result.overtimePay(), "overtime pay"),
            () -> assertEquals(premiumPay,  result.premiumPay(),  "premium pay"),
            () -> assertEquals(weeklyPay,   result.weeklyPay(),   "weekly pay")
        );
    }

    @DisplayName("Negative hourly rate rejection")
    @TableTest("""
        Scenario      | Rate   | Throws?
        Negative rate | -1.00  | java.lang.IllegalArgumentException
        Very negative | -10.00 | java.lang.IllegalArgumentException
        """)
    void shouldRejectNegativeRate(BigDecimal rate, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> calculator.calculate(40, 0, 0, rate));
    }
}
```

---

**Design notes:**

- The traceability columns (`Regular pay?`, `Overtime pay?`, `Premium pay?`) expose the internal breakdown. This assumes `WeeklyPayResult` is a record/class with those fields — if the API only returns a total `BigDecimal`, drop those columns and assert only `weeklyPay`.

- The "Sunday and holiday hours" row (0 weekday, 4 sunday, 4 holiday) verifies both premium types contribute at 2×, and that Sunday/holiday hours don't interact with the weekday overtime clock.

- "Just under overtime threshold" (39h) and "One hour overtime" (41h) bracket the 40h boundary explicitly.

- The "pay cannot go below zero" floor is implicitly verified by the "No hours worked" and "Zero rate" rows both producing `0.00` — the rule is a safety net against negative arithmetic, not a scenario that requires dedicated inputs to trigger.

- `int` (not `Integer`) is used for hour columns since they're never blank — all scenarios provide explicit values. `BigDecimal` for monetary values; JUnit's implicit conversion handles `String → BigDecimal`.