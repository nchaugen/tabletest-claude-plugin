package payroll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.function.Executable;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WeeklyPayCalculatorTest {

    private final WeeklyPayCalculator calculator = new WeeklyPayCalculator();

    @DisplayName("Splits weekday hours into regular and overtime at the 40-hour threshold")
    @TableTest("""
        Scenario                          | Weekday Hours | Regular Hours? | Overtime Hours?
        No hours worked                   | 0             | 0              | 0
        Below the overtime threshold      | 25            | 25             | 0
        At the overtime threshold         | 40            | 40             | 0
        Just past the overtime threshold  | 40.5          | 40             | 0.5
        """)
    void splitsWeekdayHours(double weekdayHours, double regularHours, double overtimeHours) {
        WeekdayHoursSplit split = calculator.splitWeekdayHours(weekdayHours);

        assertEquals(regularHours, split.regularHours());
        assertEquals(overtimeHours, split.overtimeHours());
    }

    @DisplayName("Calculates total weekly pay from weekday, Sunday, and holiday hours")
    @Description("""
        Weekday hours up to 40 pay the base rate; hours beyond 40 pay 1.5x.
        Sunday and holiday hours always pay 2x. Assumes hour inputs may be
        negative (e.g. a correction for a prior overpayment); the resulting
        pay is floored at zero rather than allowed to go negative.
        """)
    @TableTest("""
        Scenario                                | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Total Pay?
        No hours worked                         | 0             | 0            | 0             | 20.00       | 0.00
        Regular weekday hours only               | 25            | 0            | 0             | 20.00       | 500.00
        Weekday hours with overtime              | 45            | 0            | 0             | 20.00       | 950.00
        Sunday hours                             | 0             | 8            | 0             | 20.00       | 320.00
        Holiday hours                            | 0             | 0            | 8             | 20.00       | 320.00
        Weekday, Sunday, and holiday hours combined | 45         | 8            | 8             | 20.00       | 1590.00
        Negative hours drive pay below zero      | -10           | 0            | 0             | 20.00       | 0.00
        """)
    void calculatesWeeklyPay(double weekdayHours, double sundayHours, double holidayHours,
                              BigDecimal hourlyRate, BigDecimal totalPay) {
        BigDecimal actual = calculator.calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate);

        assertEquals(0, totalPay.compareTo(actual));
    }

    @DisplayName("Rejects a negative hourly rate")
    @Description("""
        Weekday, Sunday, and holiday hours are fixed at 0 for every row since
        only the hourly rate is under test here.
        """)
    @TableTest("""
        Scenario              | Hourly Rate | Throws?
        Zero rate is allowed  | 0.00        |
        Negative rate         | -0.01       | java.lang.IllegalArgumentException
        """)
    void rejectsNegativeHourlyRate(BigDecimal hourlyRate, Class<? extends Throwable> throws_) {
        assertEquals(throws_, thrownBy(() -> calculator.calculateWeeklyPay(0, 0, 0, hourlyRate)));
    }

    private static Class<? extends Throwable> thrownBy(Executable action) {
        try {
            action.execute();
            return null;
        } catch (Throwable thrown) {
            return thrown.getClass();
        }
    }
}
