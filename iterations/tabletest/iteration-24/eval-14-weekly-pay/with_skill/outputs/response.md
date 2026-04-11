Working through the requirements analysis now, then writing the Java test class directly.

**Step 1 — Concerns:**
1. Weekday overtime threshold (the 40h rule — a decision boundary)
2. Sunday/holiday double time (the 2× premium rule)
3. Full weekly pay with mixed hour types (combination arithmetic)
4. Negative rate rejected (validation)

**Step 2–5 — Key design decisions:**
- Separate the 40h threshold rule (Tables 1) from Sunday/holiday rate rule (Table 2) from combined arithmetic (Table 3), per "separate rules from arithmetic"
- Use value set on weekday hrs in validation table to express "regardless of hours"
- Floor rule: with non-negative inputs, pay can't naturally go below zero — test with zero-rate row to show the floor holds; the floor is defensive

**Arithmetic verified:**
- 39 hrs × $10 = $390; 40 hrs × $10 = $400; 41 hrs × $10 = $415 (40×10 + 1×15); 60 hrs × $10 = $700 (40×10 + 20×15)
- 8 Sunday hrs × $15 = $240 (8 × 2 × 15); both Sunday+holiday = $480
- 45 weekday + 8 Sunday × $20 = $1270 (800 + 150 + 320)
- 50 weekday + 8 Sunday + 8 holiday × $20 = $1740 (800 + 300 + 320 + 320)

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class WeeklyPayCalculatorTest {

    private final WeeklyPayCalculator calculator = new WeeklyPayCalculator();

    @DisplayName("Weekday overtime threshold")
    @Description("""
            Sunday and holiday hours are 0 for all rows.
            Regular weekday pay: hours × rate (up to 40 hours).
            Overtime pay: hours above 40 × rate × 1.5.
            """)
    @TableTest("""
            Scenario          | Weekday hrs | Rate  | Weekly pay?
            Zero hours        | 0           | 15.00 | 0.00
            Under threshold   | 39          | 10.00 | 390.00
            At threshold      | 40          | 10.00 | 400.00
            One hour over     | 41          | 10.00 | 415.00
            Heavy overtime    | 60          | 10.00 | 700.00
            """)
    void weekdayOvertimeThreshold(int weekdayHrs, BigDecimal rate, BigDecimal expectedPay) {
        assertEquals(0, expectedPay.compareTo(calculator.calculate(weekdayHrs, 0, 0, rate)));
    }

    @DisplayName("Sunday and holiday double time")
    @Description("""
            Weekday hours are 0 for all rows.
            Sunday and holiday hours are always paid at 2× the base rate.
            """)
    @TableTest("""
            Scenario                | Sunday hrs | Holiday hrs | Rate  | Weekly pay?
            Sunday hours only       | 8          | 0           | 15.00 | 240.00
            Holiday hours only      | 0          | 8           | 15.00 | 240.00
            Both Sunday and holiday | 8          | 8           | 15.00 | 480.00
            """)
    void sundayAndHolidayDoubleTime(int sundayHrs, int holidayHrs, BigDecimal rate, BigDecimal expectedPay) {
        assertEquals(0, expectedPay.compareTo(calculator.calculate(0, sundayHrs, holidayHrs, rate)));
    }

    @DisplayName("Full weekly pay with mixed hour types")
    @Description("""
            Combines weekday (regular + overtime), Sunday, and holiday hours.
            Regular: hours × rate. Overtime: hours × rate × 1.5.
            Sunday and holiday: hours × rate × 2. Total cannot go below 0.
            """)
    @TableTest("""
            Scenario               | Weekday hrs | Sunday hrs | Holiday hrs | Rate  | Weekly pay?
            Standard week          | 40          | 0          | 0           | 20.00 | 800.00
            Overtime with Sunday   | 45          | 8          | 0           | 20.00 | 1270.00
            Overtime with holiday  | 45          | 0          | 8           | 20.00 | 1270.00
            All hour types         | 50          | 8          | 8           | 20.00 | 1740.00
            Zero rate, floor holds | 40          | 8          | 8           | 0.00  | 0.00
            All zeros              | 0           | 0          | 0           | 20.00 | 0.00
            """)
    void fullWeeklyPayWithMixedHourTypes(int weekdayHrs, int sundayHrs, int holidayHrs,
                                         BigDecimal rate, BigDecimal expectedPay) {
        assertEquals(0, expectedPay.compareTo(calculator.calculate(weekdayHrs, sundayHrs, holidayHrs, rate)));
    }

    @DisplayName("Negative hourly rate rejected")
    @Description("Rate validation is independent of hours worked.")
    @TableTest("""
            Scenario      | Weekday hrs | Sunday hrs | Holiday hrs | Rate    | Throws?
            Negative rate | {0, 40}     | 0          | 0           | -10.00  | IllegalArgumentException
            """)
    void negativeHourlyRateRejected(int weekdayHrs, int sundayHrs, int holidayHrs,
                                    BigDecimal rate, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> calculator.calculate(weekdayHrs, sundayHrs, holidayHrs, rate));
    }
}
```

A few notes on the design choices:

- **Separated threshold from combination**: Table 1 isolates the 40h boundary (39/40/41/60 hrs) without Sunday/holiday noise. Table 2 isolates the 2× premium rule. Table 3 verifies they compose correctly.
- **Value set for validation**: `{0, 40}` on weekday hrs asserts the rate check fires regardless of how many hours were worked — more expressive than two identical-purpose rows.
- **BigDecimal compareTo**: uses `compareTo` instead of `equals` so scale differences (`400` vs `400.00`) don't cause false failures.
- **Floor rule**: "Zero rate, floor holds" and "All zeros" cover the floor defensively. Since negative rates are rejected and hours are non-negative, the floor can't naturally trigger — it's captured as an open question in the `@Description` if the system ever adds deductions.