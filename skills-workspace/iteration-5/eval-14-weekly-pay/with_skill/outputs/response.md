Here are the tests I designed. Since I couldn't write the file to disk (no write permission), here's the complete test class:

```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

// Dependency (add to pom.xml / build.gradle):
//   groupId: org.tabletest  artifactId: tabletest-junit
//   JUnit Jupiter 5.11+

@DisplayName("Weekly Pay Calculator")
@Description(
    "Calculates gross weekly pay for hourly employees. "
    + "Weekday hours up to 40 are paid at the base rate; hours beyond 40 are overtime at 1.5×. "
    + "Sunday and holiday hours are always paid at 2× regardless of weekly total. "
    + "Total pay has a zero floor. A negative hourly rate is rejected.")
class WeeklyPayCalculatorTest {

    @TableTest("""
        Scenario                         | Weekday hrs | Sunday hrs | Holiday hrs | Rate  | Regular pay? | Overtime pay? | Premium pay? | Total pay?
        No hours worked                  | 0           | 0          | 0           | 15.00 | 0.00         | 0.00          | 0.00         | 0.00
        Standard week, no overtime       | 40          | 0          | 0           | 15.00 | 600.00       | 0.00          | 0.00         | 600.00
        Exactly at overtime threshold    | 40          | 0          | 0           | 10.00 | 400.00       | 0.00          | 0.00         | 400.00
        One hour over threshold          | 41          | 0          | 0           | 10.00 | 400.00       | 15.00         | 0.00         | 415.00
        Heavy overtime week              | 48          | 0          | 0           | 20.00 | 800.00       | 240.00        | 0.00         | 1040.00
        Sunday hours only                | 0           | 8          | 0           | 15.00 | 0.00         | 0.00          | 240.00       | 240.00
        Holiday hours only               | 0           | 0          | 6           | 15.00 | 0.00         | 0.00          | 180.00       | 180.00
        Overtime plus Sunday and holiday | 48          | 4          | 2           | 20.00 | 800.00       | 240.00        | 240.00       | 1280.00
        Zero rate, all hour types        | 40          | 8          | 4           | 0.00  | 0.00         | 0.00          | 0.00         | 0.00
        """)
    @Description(
        "Regular pay  = min(weekdayHrs, 40) × rate. "
        + "Overtime pay = max(weekdayHrs − 40, 0) × rate × 1.5. "
        + "Premium pay  = (sundayHrs + holidayHrs) × rate × 2.")
    void shouldCalculateWeeklyPay(
            int weekdayHrs, int sundayHrs, int holidayHrs, BigDecimal rate,
            BigDecimal expectedRegular, BigDecimal expectedOvertime,
            BigDecimal expectedPremium, BigDecimal expectedTotal) {

        WeeklyPayResult result = WeeklyPayCalculator.calculate(weekdayHrs, sundayHrs, holidayHrs, rate);

        assertThat(result.regularPay()).isEqualByComparingTo(expectedRegular);
        assertThat(result.overtimePay()).isEqualByComparingTo(expectedOvertime);
        assertThat(result.premiumPay()).isEqualByComparingTo(expectedPremium);
        assertThat(result.totalPay()).isEqualByComparingTo(expectedTotal);
    }

    @TableTest("""
        Scenario              | Rate
        Small negative rate   | -0.01
        Typical negative rate | -1.00
        Large negative rate   | -100.00
        """)
    @Description("A negative hourly rate is not a valid input and must be rejected before any calculation occurs.")
    void shouldRejectNegativeRate(BigDecimal rate) {
        assertThatThrownBy(() -> WeeklyPayCalculator.calculate(40, 0, 0, rate))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
```

**Key design decisions:**

**Traceability columns** (`Regular pay?`, `Overtime pay?`, `Premium pay?`, `Total pay?`) — each row shows the full breakdown so a failing test immediately reveals which component is wrong. This assumes `WeeklyPayResult` is a record with those four accessors; adjust if your API returns only the total.

**Overtime threshold boundary** — two rows test exactly 40h (no OT) and 41h (1h OT), pinning the `<= 40` vs `> 40` boundary precisely.

**`Overtime plus Sunday and holiday`** — the most complex row exercises all three pay types together, verifying they combine correctly.

**Rate validation** — three rows covering the boundary (near-zero, typical, large); the three cases collapse naturally into separate rows rather than a value set since they're just sampling a range.

**The zero-floor rule** is covered implicitly by the "No hours worked" row (result is 0.00, not negative) and the "Zero rate" row. If your API can ever receive negative hours as input, add rows for those.