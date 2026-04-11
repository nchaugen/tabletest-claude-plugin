Working through the requirements analysis before writing the code:

**Concerns:** "Calculate weekday pay" (has an overtime threshold rule), "Calculate premium hours pay" (Sunday/holiday always at 2×), "Calculate total weekly pay" (combining all types), "Validate hourly rate" (reject negatives).

**Boundary check for overtime threshold:** test at 39, 40, 41 hours to pin the exact threshold.

**Verify arithmetic:**
- 30 × $20 = $600; 40 × $20 = $800; 41hrs: 40×$20 + 1×$30 = $830; 50hrs: $800 + 10×$30 = $1,100 ✓
- 5 Sunday × ($20×2) = $200; 3 Holiday × $40 = $120; (4+4) × $40 = $320 ✓
- 45 wkdy + 5 Sun @ $10: (40×10 + 5×15) + 5×20 = 475 + 100 = $575 ✓
- 32 wkdy + 8 Hol @ $25: 32×25 + 8×50 = 800 + 400 = $1,200 ✓
- 50 wkdy + 4 Sun + 2 Hol @ $20: 800 + 300 + 160 + 80 = $1,340 ✓

**Open question:** "Total cannot go below zero" — with a non-negative rate and non-negative hours, total is always ≥ 0. This may apply if negative hours (corrections) can be submitted.

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WeeklyPayCalculatorTest {

    @DisplayName("Weekday pay — regular and overtime rates")
    @Description("""
        Regular pay = min(weekdayHours, 40) × rate.
        Overtime pay = max(weekdayHours − 40, 0) × 1.5 × rate.
        Overtime threshold is 40 hours (policy constant).
        Sunday and holiday hours are 0 for all rows to isolate weekday rules.
        """)
    @TableTest("""
        Scenario             | Weekday hrs | Rate  | Pay?
        No hours             | 0           | 20.00 | 0.00
        Below threshold      | 30          | 20.00 | 600.00
        At threshold exactly | 40          | 20.00 | 800.00
        One hour overtime    | 41          | 20.00 | 830.00
        Ten hours overtime   | 50          | 20.00 | 1100.00
        """)
    void weekdayPayWithOvertimeThreshold(int weekdayHours, BigDecimal rate, BigDecimal expectedPay) {
        assertMoneyEquals(expectedPay, WeeklyPayCalculator.calculate(weekdayHours, 0, 0, rate));
    }

    @DisplayName("Sunday and holiday pay — double time rate")
    @Description("""
        Sunday and holiday hours are always paid at 2× the base rate,
        regardless of how many weekday hours are worked.
        Weekday hours are 0 for all rows to isolate premium-hour rules.
        """)
    @TableTest("""
        Scenario              | Sunday hrs | Holiday hrs | Rate  | Pay?
        No premium hours      | 0          | 0           | 20.00 | 0.00
        Sunday hours only     | 5          | 0           | 20.00 | 200.00
        Holiday hours only    | 0          | 3           | 20.00 | 120.00
        Both Sunday + holiday | 4          | 4           | 20.00 | 320.00
        """)
    void premiumHoursPay(int sundayHours, int holidayHours, BigDecimal rate, BigDecimal expectedPay) {
        assertMoneyEquals(expectedPay, WeeklyPayCalculator.calculate(0, sundayHours, holidayHours, rate));
    }

    @DisplayName("Total weekly pay — all hour types combined")
    @Description("""
        Full calculation combining weekday (regular + overtime), Sunday, and holiday hours.
        Weekday pay = min(w, 40) × rate + max(w − 40, 0) × 1.5 × rate.
        Sunday and holiday pay = hours × 2 × rate each.
        Open: can negative hours (corrections) be submitted, and how does the
        pay floor of zero apply in that case?
        """)
    @TableTest("""
        Scenario                     | Weekday hrs | Sunday hrs | Holiday hrs | Rate  | Pay?
        Regular week, no premium     | 40          | 0          | 0           | 15.00 | 600.00
        Overtime and Sunday hours    | 45          | 5          | 0           | 10.00 | 575.00
        Holiday week, no overtime    | 32          | 0          | 8           | 25.00 | 1200.00
        All types with overtime      | 50          | 4          | 2           | 20.00 | 1340.00
        Zero hours                   | 0           | 0          | 0           | 20.00 | 0.00
        """)
    void totalWeeklyPay(int weekdayHours, int sundayHours, int holidayHours,
                        BigDecimal rate, BigDecimal expectedPay) {
        assertMoneyEquals(expectedPay,
            WeeklyPayCalculator.calculate(weekdayHours, sundayHours, holidayHours, rate));
    }

    @DisplayName("Negative hourly rate rejected")
    @TableTest("""
        Scenario        | Rate
        Just below zero | -0.01
        Large negative  | -50.00
        """)
    void negativeRateIsRejected(BigDecimal rate) {
        assertThrows(IllegalArgumentException.class,
            () -> WeeklyPayCalculator.calculate(0, 0, 0, rate));
    }

    private static void assertMoneyEquals(BigDecimal expected, BigDecimal actual) {
        assertEquals(0, expected.compareTo(actual),
            () -> "Expected " + expected + " but was " + actual);
    }
}
```

Four tables, each testing a distinct concern:

1. **Weekday pay** — pins the overtime threshold at exactly 40 hours, with boundary rows at 40 and 41.
2. **Premium hours** — isolates Sunday and holiday at 2×, showing each type alone and combined.
3. **Total weekly pay** — integration rows that mix all hour types; "Overtime and Sunday hours" is the key row verifying that weekday overtime and Sunday premium stack correctly.
4. **Rate validation** — all rows throw `IllegalArgumentException`, so the exception class is hardcoded in the assertion rather than a `Throws?` column.

`assertMoneyEquals` uses `compareTo` rather than `equals` to avoid `BigDecimal` scale mismatches (`600.0` vs `600.00`). The open question about negative hours and the pay floor is noted in `@Description` on the combined table rather than silently resolved.