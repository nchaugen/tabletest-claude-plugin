Working through the design workflow before writing code.

**Step 1 — Name each concern:**
1. Classify weekday hours — which are regular (≤40) vs overtime (>40)?
2. Sunday/holiday double time — hours at 2× rate
3. Full weekly pay — combining all components
4. Input validation — negative rate rejected

**Step 2–4 — Sketch examples, boundaries, irrelevant inputs:**

- Weekday overtime threshold: test 0, 30, 40, 41, 50 hours
- Sunday/holiday: 2× applies regardless of weekday hours
- Rate is irrelevant to overtime classification; hours count is irrelevant to rate validation
- Value sets: negative rate rejected regardless of hours worked

**Step 5 — Separate rule from arithmetic:**

Table 1 isolates the overtime threshold rule (weekday only, Sunday=0, Holiday=0). Table 2 isolates the double-time rule. Table 3 tests combined totals end-to-end.

**Step 6–8 — No state transitions; thresholds visible as boundary rows; open question noted: pay floor can't naturally trigger once rate validation is enforced.**

**Step 9 — Code:**

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WeeklyPayCalculatorTest {

    @DisplayName("Weekday overtime threshold")
    @Description("""
            Sunday hours = 0 and holiday hours = 0 for all rows.
            Weekday hours up to 40 pay at the base rate; hours beyond 40 pay at 1.5× the base rate.
            """)
    @TableTest("""
            Scenario             | Weekday hours | Rate  | Pay?
            No hours             | 0             | 20.00 | 0.00
            Under threshold      | 30            | 20.00 | 600.00
            Exactly at threshold | 40            | 20.00 | 800.00
            One hour overtime    | 41            | 20.00 | 830.00
            10 hours overtime    | 50            | 20.00 | 1100.00
            """)
    void weekdayOvertimeThreshold(int weekdayHours, BigDecimal rate, BigDecimal expectedPay) {
        BigDecimal result = WeeklyPayCalculator.calculate(weekdayHours, 0, 0, rate);
        assertEquals(0, expectedPay.compareTo(result));
    }

    @DisplayName("Sunday and holiday double time")
    @Description("Weekday hours = 0 for all rows. Both Sunday and holiday hours pay at 2× the base rate.")
    @TableTest("""
            Scenario          | Sunday hours | Holiday hours | Rate  | Pay?
            No hours          | 0            | 0             | 20.00 | 0.00
            Sunday only       | 8            | 0             | 20.00 | 320.00
            Holiday only      | 0            | 8             | 20.00 | 320.00
            Sunday + holiday  | 8            | 8             | 20.00 | 640.00
            """)
    void sundayAndHolidayDoubleTime(int sundayHours, int holidayHours, BigDecimal rate, BigDecimal expectedPay) {
        BigDecimal result = WeeklyPayCalculator.calculate(0, sundayHours, holidayHours, rate);
        assertEquals(0, expectedPay.compareTo(result));
    }

    @DisplayName("Combined weekly pay")
    @Description("""
            Formula:
              regular pay  = min(weekday hours, 40) × rate
              overtime pay = max(0, weekday hours − 40) × 1.5 × rate
              Sunday pay   = Sunday hours × 2 × rate
              holiday pay  = holiday hours × 2 × rate
              total pay    = regular + overtime + Sunday + holiday (floored at zero)

            The pay floor is a defensive constraint; it cannot be triggered naturally because
            negative rates are rejected before calculation reaches this point.
            """)
    @TableTest("""
            Scenario                | Weekday | Sunday | Holiday | Rate  | Pay?
            Standard 40-hour week   | 40      | 0      | 0       | 20.00 | 800.00
            With overtime           | 50      | 0      | 0       | 20.00 | 1100.00
            Standard week + Sunday  | 40      | 8      | 0       | 20.00 | 1120.00
            Standard week + holiday | 40      | 0      | 8       | 20.00 | 1120.00
            Overtime + Sunday       | 50      | 8      | 0       | 20.00 | 1420.00
            All types combined      | 50      | 8      | 4       | 20.00 | 1580.00
            Zero rate               | 40      | 8      | 4       | 0.00  | 0.00
            Different rate          | 40      | 0      | 0       | 15.00 | 600.00
            """)
    void combinedWeeklyPay(int weekdayHours, int sundayHours, int holidayHours, BigDecimal rate, BigDecimal expectedPay) {
        BigDecimal result = WeeklyPayCalculator.calculate(weekdayHours, sundayHours, holidayHours, rate);
        assertEquals(0, expectedPay.compareTo(result));
    }

    @DisplayName("Negative hourly rate rejected")
    @Description("Zero rate is valid (results in zero pay). Only rates below zero are rejected.")
    @TableTest("""
            Scenario                      | Weekday | Sunday | Holiday | Rate   | Throws?
            Negative rate with hours      | 40      | 0      | 0       | -1.00  | java.lang.IllegalArgumentException
            Negative rate with zero hours | 0       | 0      | 0       | -10.00 | java.lang.IllegalArgumentException
            """)
    void negativeRateRejected(int weekdayHours, int sundayHours, int holidayHours, BigDecimal rate,
                               Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> WeeklyPayCalculator.calculate(weekdayHours, sundayHours, holidayHours, rate));
    }
}
```

**Arithmetic verified:**
- 41 hrs × $20: `40×20 + 1×30 = 800 + 30 = 830` ✓
- 50 hrs × $20: `40×20 + 10×30 = 800 + 300 = 1100` ✓
- 8 Sunday hrs × $20: `8×40 = 320` ✓
- All combined (50wd, 8sun, 4hol, $20): `800 + 300 + 320 + 160 = 1580` ✓

Four tables, one concern each. The overtime threshold table uses boundary rows at 40 and 41 to pin the exact cutoff. The double-time table isolates Sunday/holiday in a 2×2 structure. The combined table tests interactions. The validation table shows the rule applies regardless of hours worked.