Here is the `WeeklyPayCalculatorTest`:

```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Weekly pay calculator")
@Description(
    "Calculates weekly pay for hourly employees. "
    + "Weekday hours up to 40 earn the base rate; hours beyond 40 earn 1.5× (overtime). "
    + "Sunday and holiday hours always earn 2× (double time), regardless of weekday hours worked. "
    + "Total pay is floored at zero. A negative hourly rate is rejected before any calculation.")
class WeeklyPayCalculatorTest {

    private final WeeklyPayCalculator calculator = new WeeklyPayCalculator();

    @TableTest("""
        Scenario                                 | Weekday hrs | Sunday hrs | Holiday hrs | Rate  | Regular pay? | Overtime pay? | Premium pay? | Weekly pay?
        No hours worked                          | 0           | 0          | 0           | 15.00 | 0.00         | 0.00          | 0.00         | 0.00
        Under-40 weekday hours only              | 32          | 0          | 0           | 20.00 | 640.00       | 0.00          | 0.00         | 640.00
        Exactly at 40-hour threshold             | 40          | 0          | 0           | 20.00 | 800.00       | 0.00          | 0.00         | 800.00
        First hour of overtime                   | 41          | 0          | 0           | 20.00 | 800.00       | 30.00         | 0.00         | 830.00
        Ten hours of overtime                    | 50          | 0          | 0           | 20.00 | 800.00       | 300.00        | 0.00         | 1100.00
        Sunday hours only                        | 0           | 8          | 0           | 15.00 | 0.00         | 0.00          | 240.00       | 240.00
        Holiday hours only                       | 0           | 0          | 8           | 15.00 | 0.00         | 0.00          | 240.00       | 240.00
        Weekday under 40 with Sunday and holiday | 32          | 4          | 4           | 20.00 | 640.00       | 0.00          | 320.00       | 960.00
        Overtime weekday with Sunday and holiday | 45          | 6          | 3           | 20.00 | 800.00       | 150.00        | 360.00       | 1310.00
        """)
    @DisplayName("Weekly pay breakdown")
    @Description(
        "Regular pay = weekday hours (capped at 40) × rate. "
        + "Overtime pay = weekday hours beyond 40 × 1.5 × rate. "
        + "Premium pay = (Sunday hours + holiday hours) × 2 × rate. "
        + "Weekly pay = regular + overtime + premium.")
    void shouldCalculateWeeklyPay(
            int weekdayHours, int sundayHours, int holidayHours, double rate,
            double regularPay, double overtimePay, double premiumPay, double weeklyPay) {
        WeeklyPayBreakdown result = calculator.calculate(weekdayHours, sundayHours, holidayHours, rate);
        assertAll(
            () -> assertEquals(regularPay,  result.regularPay(),  0.001, "regular pay"),
            () -> assertEquals(overtimePay, result.overtimePay(), 0.001, "overtime pay"),
            () -> assertEquals(premiumPay,  result.premiumPay(),  0.001, "premium pay"),
            () -> assertEquals(weeklyPay,   result.totalPay(),    0.001, "weekly pay")
        );
    }

    @TableTest("""
        Scenario                               | Weekday hrs | Sunday hrs | Holiday hrs | Rate
        Negative rate rejected, hours present  | 40          | 0          | 0           | {-0.01, -1.00, -25.00}
        Negative rate rejected, no hours       | 0           | 0          | 0           | {-0.01, -1.00, -25.00}
        """)
    @DisplayName("Negative hourly rate is rejected")
    @Description(
        "A negative hourly rate must be rejected regardless of the hours submitted. "
        + "The check must occur before any calculation.")
    void shouldRejectNegativeHourlyRate(int weekdayHours, int sundayHours, int holidayHours, double rate) {
        assertThrows(IllegalArgumentException.class,
            () -> calculator.calculate(weekdayHours, sundayHours, holidayHours, rate));
    }
}
```

---

**Key design decisions:**

**`shouldCalculateWeeklyPay` — traceability columns.** The spec defines three distinct pay tiers, so `Regular pay?`, `Overtime pay?`, and `Premium pay?` are included as intermediate expectation columns alongside `Weekly pay?`. This lets the reader trace which rule contributes what, and makes failures self-diagnosing (you can see which tier is wrong without reading the implementation).

**Boundary rows.** The 40-hour threshold is tested at 40 (no overtime) and 41 (first overtime hour) as separate rows — these boundary conditions are where off-by-one bugs hide.

**`shouldRejectNegativeHourlyRate` — value sets.** The magnitude of the negative rate doesn't affect the outcome (always rejected), so `{-0.01, -1.00, -25.00}` documents this with 3 generated cases per row. Two rows cover the "with hours" and "no hours" cases to confirm the check is unconditional.

**`WeeklyPayBreakdown` return type.** The API is designed to return a breakdown record so the intermediate columns are observable. If your implementation returns just a `double`, drop the three intermediate columns and keep only `Weekly pay?`.