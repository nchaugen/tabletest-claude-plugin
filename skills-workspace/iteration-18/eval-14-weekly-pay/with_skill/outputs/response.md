Here is the test class I would write at `src/test/java/com/example/WeeklyPayCalculatorTest.java`:

```java
package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class WeeklyPayCalculatorTest {

    @TypeConverter
    static BigDecimal parseMoney(String s) {
        return new BigDecimal(s);
    }

    @DisplayName("Weekly pay components")
    @Description("""
            Overtime threshold: 40 weekday hours.
            Regular pay     = min(weekday hours, 40) × rate.
            Overtime pay    = max(weekday hours − 40, 0) × 1.5 × rate.
            Double-time pay = (Sunday hours + holiday hours) × 2 × rate.
            Weekly pay      = regular pay + overtime pay + double-time pay (floor: 0).
            """)
    @TableTest("""
            Scenario                         | Weekday hrs | Sunday hrs | Holiday hrs | Rate  | Regular pay? | Overtime pay? | Double-time pay? | Weekly pay?
            No hours worked                  | 0           | 0          | 0           | 20.00 | 0.00         | 0.00          | 0.00             | 0.00
            Just below overtime threshold    | 39          | 0          | 0           | 20.00 | 780.00       | 0.00          | 0.00             | 780.00
            At overtime threshold            | 40          | 0          | 0           | 20.00 | 800.00       | 0.00          | 0.00             | 800.00
            One hour of overtime             | 41          | 0          | 0           | 20.00 | 800.00       | 30.00         | 0.00             | 830.00
            Five hours of overtime           | 45          | 0          | 0           | 20.00 | 800.00       | 150.00        | 0.00             | 950.00
            Sunday hours only                | 0           | 8          | 0           | 20.00 | 0.00         | 0.00          | 320.00           | 320.00
            Holiday hours only               | 0           | 0          | 8           | 20.00 | 0.00         | 0.00          | 320.00           | 320.00
            Weekday with Sunday              | 40          | 8          | 0           | 20.00 | 800.00       | 0.00          | 320.00           | 1120.00
            Overtime with Sunday and holiday | 45          | 8          | 8           | 20.00 | 800.00       | 150.00        | 640.00           | 1590.00
            Zero rate                        | 40          | 8          | 8           | 0.00  | 0.00         | 0.00          | 0.00             | 0.00
            """)
    void calculatesWeeklyPay(int weekdayHrs, int sundayHrs, int holidayHrs, BigDecimal rate,
                             BigDecimal regularPay, BigDecimal overtimePay,
                             BigDecimal doubleTimePay, BigDecimal weeklyPay) {
        WeeklyPayResult result = WeeklyPayCalculator.calculate(weekdayHrs, sundayHrs, holidayHrs, rate);
        assertAll(
                () -> assertEquals(regularPay, result.regularPay()),
                () -> assertEquals(overtimePay, result.overtimePay()),
                () -> assertEquals(doubleTimePay, result.doubleTimePay()),
                () -> assertEquals(weeklyPay, result.weeklyPay())
        );
    }

    @DisplayName("Invalid hourly rate rejection")
    @TableTest("""
            Scenario          | Rate                    | Throws?
            Any negative rate | {-0.01, -1.00, -100.00} | IllegalArgumentException
            """)
    void rejectsNegativeHourlyRate(BigDecimal rate, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> WeeklyPayCalculator.calculate(40, 0, 0, rate));
    }
}
```

---

**Design decisions:**

**Two tables, two concerns:**
- `calculatesWeeklyPay` — the pay calculation rules and arithmetic
- `rejectsNegativeHourlyRate` — the validation/rejection concern

**Traceability columns** (`Regular pay?`, `Overtime pay?`, `Double-time pay?`) — when a row fails you can see which pay component is wrong, not just that the total is off.

**Overtime boundary coverage** — three rows cover the threshold precisely: 39 (no overtime), 40 (exactly at threshold, no overtime), 41 (one hour triggers overtime). This pins down the inclusive/exclusive boundary.

**Value set for rejection** — `{-0.01, -1.00, -100.00}` asserts that *any* negative rate is rejected, not just one specific value, without repeating three identical rows.

**`@TypeConverter`** — `BigDecimal` isn't auto-converted by JUnit, so `parseMoney` handles it for all pay and rate columns.

**Assumptions about the implementation:** `WeeklyPayCalculator.calculate(...)` returns a `WeeklyPayResult` record/object with `regularPay()`, `overtimePay()`, `doubleTimePay()`, and `weeklyPay()` accessors. Adapt these to match your actual API.