package payroll;

import org.junit.jupiter.api.function.Executable;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WeeklyPayCalculatorTest {

    @TableTest("""
        Scenario                 | Weekday Hours | Overtime Threshold (Policy) | Regular Hours? | Overtime Hours?
        Below the threshold      | 32            | 40                          | 32              | 0
        At the threshold         | 40            | 40                          | 40              | 0
        Just past the threshold  | 40.5          | 40                          | 40              | 0.5
        Well past the threshold  | 50            | 40                          | 40              | 10
        """)
    void splitsWeekdayHoursIntoRegularAndOvertime(double weekdayHours, double overtimeThreshold,
                                                   double regularHours, double overtimeHours) {
        HoursSplit split = WeeklyPayCalculator.classifyWeekdayHours(weekdayHours, overtimeThreshold);
        assertEquals(regularHours, split.regularHours());
        assertEquals(overtimeHours, split.overtimeHours());
    }

    @Description("""
        Overtime is paid at 1.5x the base rate; Sunday and holiday hours are always
        paid at 2x. These multipliers are fixed pay rules, not a configurable policy,
        so they are not columns here.
        Assumption: hours are non-negative counts of hours worked; the spec only
        requires rejecting a negative rate (see rejectsNegativeHourlyRate), not
        negative hours. Given non-negative hours and rate, this arithmetic can never
        produce a negative total, so the "pay cannot go below zero" rule has no
        reachable case here beyond the zero-hours baseline.
        """)
    @TableTest("""
        Scenario                | Regular Hours | Overtime Hours | Sunday Hours | Holiday Hours | Hourly Rate | Total Pay?
        No hours worked         | 0              | 0               | 0            | 0              | 20          | 0
        Regular hours only      | 40             | 0               | 0            | 0              | 20          | 800
        Overtime hours only     | 0              | 10              | 0            | 0              | 20          | 300
        Sunday hours only       | 0              | 0               | 8            | 0              | 20          | 320
        Holiday hours only      | 0              | 0               | 0            | 8              | 20          | 320
        All hour types combined | 40             | 5               | 8            | 8              | 20          | 1590
        """)
    void calculatesTotalPayFromClassifiedHoursAndRate(double regularHours, double overtimeHours,
                                                        double sundayHours, double holidayHours,
                                                        BigDecimal hourlyRate, BigDecimal totalPay) {
        BigDecimal actual = WeeklyPayCalculator.calculatePay(
                regularHours, overtimeHours, sundayHours, holidayHours, hourlyRate);
        assertEquals(0, totalPay.compareTo(actual));
    }

    @TableTest("""
        Scenario          | Hourly Rate | Throws?
        Non-negative rate | {0, 15}     |
        Negative rate     | -0.01       | java.lang.IllegalArgumentException
        """)
    void rejectsNegativeHourlyRate(BigDecimal hourlyRate, Class<? extends Throwable> throws_) {
        Class<? extends Throwable> thrown = thrownBy(() ->
                WeeklyPayCalculator.calculatePay(0, 0, 0, 0, hourlyRate));
        assertEquals(throws_, thrown);
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
