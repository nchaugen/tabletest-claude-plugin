Here's the test I'd write — two focused `@TableTest` methods:

```java
import org.tabletest.junit.TableTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Description;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Weekly pay calculation")
class WeeklyPayCalculatorTest {

    private WeeklyPayCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new WeeklyPayCalculator();
    }

    @Description(
        "Weekly pay = (weekday hours up to 40) × rate"
        + " + (weekday hours beyond 40) × 1.5 × rate  [overtime]"
        + " + sunday hours × 2 × rate                  [double time]"
        + " + holiday hours × 2 × rate                 [double time]."
        + " Total pay is floored at zero."
    )
    @TableTest("""
        Scenario                         | Weekday hours | Sunday hours | Holiday hours | Rate  | Weekly pay?
        No hours worked                  | 0             | 0            | 0             | 10.00 | 0.00
        Zero rate                        | 40            | 0            | 0             | 0.00  | 0.00
        Standard 40-hour week            | 40            | 0            | 0             | 10.00 | 400.00
        One overtime hour                | 41            | 0            | 0             | 10.00 | 415.00
        Eight overtime hours             | 48            | 0            | 0             | 10.00 | 520.00
        Sunday hours only                | 0             | 8            | 0             | 10.00 | 160.00
        Holiday hours only               | 0             | 0            | 8             | 10.00 | 160.00
        Full week with Sunday            | 40            | 8            | 0             | 10.00 | 560.00
        Overtime with Sunday and holiday | 48            | 4            | 2             | 10.00 | 640.00
        """)
    void calculatesWeeklyPay(int weekdayHours, int sundayHours, int holidayHours,
                             BigDecimal rate, BigDecimal expectedPay) {
        BigDecimal actual = calculator.calculate(weekdayHours, sundayHours, holidayHours, rate);
        assertEquals(0, actual.compareTo(expectedPay),
            () -> "Expected " + expectedPay + " but got " + actual);
    }

    @Description("A negative hourly rate must be rejected before any calculation is attempted.")
    @TableTest("""
        Scenario      | Rate
        Negative rate | {-10.00, -1.00, -0.01}
        """)
    void rejectsNegativeHourlyRate(BigDecimal rate) {
        assertThrows(IllegalArgumentException.class,
            () -> calculator.calculate(0, 0, 0, rate));
    }
}
```

**Design notes:**

- **Two tables, two concerns**: pay calculation and input validation are tested separately since they use different assertion logic.

- **`calculatesWeeklyPay`** covers the key boundary (exactly 40 hours = no overtime, 41 = overtime kicks in), each premium type in isolation (Sunday-only, holiday-only), and a combined scenario to confirm components add correctly. The `Zero rate` row confirms 0 × anything = 0 and the rate isn't hardcoded.

- **`rejectsNegativeHourlyRate`** uses a value set `{-10.00, -1.00, -0.01}` — all three must throw because they all violate the same rule. This generates 3 tests from one row.

- **`BigDecimal` comparison** uses `compareTo` (not `equals`) to avoid scale mismatches (`400.00` vs `400.0`).

- The **zero floor** is covered by `@Description` rather than a dedicated row, since valid non-negative inputs can never produce negative pay under these rules — no dedicated test scenario is possible without introducing negative hours, which the spec doesn't define.

If `org.tabletest:tabletest-junit` isn't yet in your build file, add it as a test dependency alongside JUnit Jupiter 5.11+.