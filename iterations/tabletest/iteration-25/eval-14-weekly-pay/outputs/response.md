Here is `WeeklyPayTest.java`:

```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class WeeklyPayTest {

    private WeeklyPayCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new WeeklyPayCalculator();
    }

    @DisplayName("Weekly pay calculation")
    @Description("""
            Regular pay:  weekday hours up to 40 × hourly rate.
            Overtime pay: weekday hours beyond 40 × 1.5 × hourly rate.
            Sunday pay:   Sunday hours × 2 × hourly rate (always, regardless of weekday total).
            Holiday pay:  holiday hours × 2 × hourly rate (always, regardless of weekday total).
            Total pay floor is zero; the result cannot be negative.
            """)
    @TableTest("""
            Scenario                            | Weekday hrs | Sunday hrs | Holiday hrs | Rate  | Total pay?
            No hours worked                     | 0           | 0          | 0           | 20.00 | 0.00
            Weekday hours under threshold       | 35          | 0          | 0           | 20.00 | 700.00
            Weekday hours at threshold          | 40          | 0          | 0           | 20.00 | 800.00
            One overtime hour                   | 41          | 0          | 0           | 20.00 | 830.00
            Significant overtime                | 50          | 0          | 0           | 20.00 | 1100.00
            Sunday hours only                   | 0           | 8          | 0           | 20.00 | 320.00
            Holiday hours only                  | 0           | 0          | 8           | 20.00 | 320.00
            Sunday with weekday under threshold | 35          | 8          | 0           | 20.00 | 1020.00
            Sunday with weekday overtime        | 45          | 8          | 0           | 20.00 | 1270.00
            Holiday with weekday overtime       | 45          | 0          | 8           | 20.00 | 1270.00
            All pay types in one week           | 42          | 4          | 4           | 20.00 | 1180.00
            Zero hourly rate                    | 40          | 0          | 0           | 0.00  | 0.00
            """)
    void calculatesWeeklyPay(int weekdayHours, int sundayHours, int holidayHours,
                              BigDecimal rate, BigDecimal expectedPay) {
        BigDecimal actual = calculator.calculate(weekdayHours, sundayHours, holidayHours, rate);
        assertEquals(0, expectedPay.compareTo(actual),
                () -> "Expected " + expectedPay + " but was " + actual);
    }

    @DisplayName("Reject negative hourly rate")
    @TableTest("""
            Scenario      | Weekday hrs | Sunday hrs | Holiday hrs | Rate  | Throws?
            Negative rate | {0, 40}     | 0          | 0           | -1.00 | IllegalArgumentException
            """)
    void rejectsNegativeRate(int weekdayHours, int sundayHours, int holidayHours,
                              BigDecimal rate, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> calculator.calculate(weekdayHours, sundayHours, holidayHours, rate));
    }
}
```

**Two tables, two concerns:**

**`calculatesWeeklyPay`** — 12 rows covering:
- Boundary at exactly 40 weekday hours (no overtime) and 41 (overtime kicks in)
- Sunday and holiday hours in isolation, combined with sub-threshold weekday, and combined with overtime weekday — these pairs together assert the 2× rate applies regardless of OT status
- All three pay types in one week
- Zero hours and zero rate edge cases

**`rejectsNegativeRate`** — Uses a value set `{0, 40}` for weekday hours, generating 2 tests. This makes the assertion explicit: a negative rate is rejected regardless of how many hours were worked.

**Notes for wiring up:**
- Assumes `WeeklyPayCalculator.calculate(int weekdayHours, int sundayHours, int holidayHours, BigDecimal rate)` returns `BigDecimal`
- `assertEquals(0, expected.compareTo(actual))` avoids false failures from BigDecimal scale differences (e.g. `800` vs `800.00`)
- `@Description` is from `org.tabletest.junit.Description`