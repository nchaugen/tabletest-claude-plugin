package payroll;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WeeklyPayCalculatorTest {

    private static final double DELTA = 0.001;

    @DisplayName("Splits weekday hours into regular and overtime hours at the 40-hour threshold")
    @Description("""
        Weekday hours up to and including 40 are regular hours; anything beyond that is overtime.
        Assumes weekday hours may be negative to represent a correction to a prior period, in which
        case the negative amount passes through as regular hours and overtime is always zero. The
        rule that total pay cannot go below zero is applied later, when gross pay is computed.
        """)
    @TableTest("""
        Scenario                          | Weekday Hours | Regular Hours? | Overtime Hours?
        Below the overtime threshold      | 30            | 30             | 0
        At the overtime threshold         | 40            | 40             | 0
        Just past the overtime threshold  | 40.5          | 40             | 0.5
        Zero hours worked                 | 0             | 0              | 0
        Negative correction hours         | -5            | -5             | 0
        """)
    void splitsWeekdayHoursIntoRegularAndOvertime(double weekdayHours, double regularHours, double overtimeHours) {
        WeekdayHoursSplit split = WeeklyPayCalculator.splitWeekdayHours(weekdayHours);

        assertEquals(regularHours, split.regularHours(), DELTA);
        assertEquals(overtimeHours, split.overtimeHours(), DELTA);
    }

    @DisplayName("Computes gross pay from classified hours at their applicable rate multiplier")
    @Description("""
        Regular hours pay the base rate, overtime pays 1.5x, and Sunday and holiday hours always
        pay 2x. Assumes negative regular hours (a prior-period correction, per the split rule above)
        can drive the raw total below zero; gross pay is floored at zero in that case.
        """)
    @TableTest("""
        Scenario                                    | Regular Hours | Overtime Hours | Sunday Hours | Holiday Hours | Hourly Rate | Gross Pay?
        Regular hours only                          | 40            | 0              | 0            | 0             | 20.00       | 800.00
        Overtime hours only                         | 0             | 10             | 0            | 0             | 20.00       | 300.00
        Sunday hours only                            | 0             | 0              | 8            | 0             | 20.00       | 320.00
        Holiday hours only                           | 0             | 0              | 0            | 8             | 20.00       | 320.00
        All hour types combined                      | 40            | 5              | 8            | 8             | 20.00       | 1590.00
        Negative correction offsets exactly to zero  | -10           | 0              | 5            | 0             | 20.00       | 0.00
        Negative correction exceeds earnings         | -50           | 0              | 0            | 0             | 20.00       | 0.00
        """)
    void computesGrossPayFromClassifiedHours(double regularHours, double overtimeHours, double sundayHours,
                                              double holidayHours, double hourlyRate, double grossPay) {
        assertEquals(grossPay,
            WeeklyPayCalculator.computeGrossPay(regularHours, overtimeHours, sundayHours, holidayHours, hourlyRate),
            DELTA);
    }

    @DisplayName("Rejects an hourly rate below zero")
    @TableTest("""
        Scenario               | Hourly Rate | Throws?
        Zero rate accepted     | 0           |
        Just below zero rate   | -0.01       | java.lang.IllegalArgumentException
        """)
    void rejectsNegativeHourlyRate(double hourlyRate, Class<? extends Exception> throws_) {
        if (throws_ == null) {
            assertDoesNotThrow(() -> WeeklyPayCalculator.validateHourlyRate(hourlyRate));
        } else {
            assertThrows(throws_, () -> WeeklyPayCalculator.validateHourlyRate(hourlyRate));
        }
    }
}
