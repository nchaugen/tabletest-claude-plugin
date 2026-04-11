Working through the analysis before writing code:

**Concerns:** (1) Calculate weekly pay — hours × rate with overtime and special-day rules. (2) Validate hourly rate — reject negatives.

**Boundaries for the 40-hour threshold:** 39 hrs (just below), 40 hrs (at limit), 41 hrs (first overtime hour). These three rows together verify the threshold is inclusive at 40.

**Value sets:** Sunday and holiday hours are separate columns (independent inputs), so no value set. Zero hours is a meaningful value (0 hrs worked), not "absent" — keep as `0` not blank.

**Arithmetic verified:**
- 41 weekday @ $10: 40×10 + 1×15 = **415**
- 50 weekday @ $10: 40×10 + 10×15 = **550**
- 45 weekday + 8 sunday + 8 holiday @ $20: 800 + 150 + 320 + 320 = **1590**

**Open question:** The "total pay cannot go below zero" floor cannot be triggered with non-negative hours and a valid (non-negative) rate — clarify whether this guards against an unmentioned deduction scenario.

**Type converter needed:** `BigDecimal` is not auto-converted by JUnit. Exception `Class<?>` can use fully-qualified names (`java.lang.IllegalArgumentException`) to avoid a second converter.

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WeeklyPayTest {

    @DisplayName("Weekly pay calculation")
    @Description("""
            Weekday hours: first 40 at base rate, each hour above 40 at 1.5× (time-and-a-half).
            Sunday hours: always at 2× base rate, independent of weekday overtime.
            Holiday hours: always at 2× base rate, independent of weekday overtime.
            Zero rate is valid and produces zero pay regardless of hours.
            Open: the 'total pay cannot go below zero' floor cannot be triggered by
            non-negative hours with a non-negative rate — confirm whether this guards
            against a deduction scenario not described in these requirements.
            """)
    @TableTest("""
            Scenario                        | Weekday hrs | Sunday hrs | Holiday hrs | Rate  | Total pay?
            No hours worked                 | 0           | 0          | 0           | 10.00 | 0.00
            Weekday hours under 40          | 20          | 0          | 0           | 10.00 | 200.00
            Just below overtime threshold   | 39          | 0          | 0           | 10.00 | 390.00
            Exactly at overtime threshold   | 40          | 0          | 0           | 10.00 | 400.00
            One overtime hour               | 41          | 0          | 0           | 10.00 | 415.00
            Ten overtime hours              | 50          | 0          | 0           | 10.00 | 550.00
            Sunday hours only               | 0           | 8          | 0           | 10.00 | 160.00
            Holiday hours only              | 0           | 0          | 8           | 10.00 | 160.00
            Weekday and Sunday combined     | 40          | 8          | 0           | 10.00 | 560.00
            Weekday and holiday combined    | 40          | 0          | 8           | 10.00 | 560.00
            All types with overtime         | 45          | 8          | 8           | 20.00 | 1590.00
            Zero rate produces zero pay     | 40          | 8          | 8           | 0.00  | 0.00
            """)
    void calculateWeeklyPay(int weekdayHours, int sundayHours, int holidayHours,
                            BigDecimal rate, BigDecimal expectedTotal) {
        BigDecimal actual = WeeklyPayCalculator.calculate(weekdayHours, sundayHours, holidayHours, rate);
        assertEquals(expectedTotal, actual);
    }

    @DisplayName("Invalid hourly rate")
    @Description("Zero is valid. Only negative values are rejected.")
    @TableTest("""
            Scenario         | Rate    | Throws?
            Minimum negative | -0.01   | java.lang.IllegalArgumentException
            Large negative   | -100.00 | java.lang.IllegalArgumentException
            """)
    void rejectsNegativeHourlyRate(BigDecimal rate, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> WeeklyPayCalculator.calculate(40, 0, 0, rate));
    }

    @TypeConverter
    public static BigDecimal toBigDecimal(String value) {
        return new BigDecimal(value);
    }
}
```

A few things to note:

- The **three boundary rows** (39 / 40 / 41 weekday hours) pin down that the threshold is inclusive at 40. A missing row here would leave the boundary direction ambiguous.
- **`java.lang.IllegalArgumentException`** in the `Throws?` column is resolved by JUnit's built-in `Class<?>` conversion — no second `@TypeConverter` needed.
- The class is `public` (required for `@TypeConverter` discovery).
- Replace `WeeklyPayCalculator.calculate(...)` with your actual method signature once the implementation exists.