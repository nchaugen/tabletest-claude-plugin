package payroll;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WeeklyPayCalculatorTest {

    @TableTest("""
        Scenario                        | Weekday Hours | Overtime Threshold | Regular Hours? | Overtime Hours?
        No hours worked                 | 0             | 40                 | 0              | 0
        Under the threshold             | 32            | 40                 | 32             | 0
        At the threshold                | 40            | 40                 | 40             | 0
        Just over the threshold         | 41            | 40                 | 40             | 1
        Well over the threshold         | 48            | 40                 | 40             | 8
        Fractional hours over threshold | 40.5          | 40                 | 40             | 0.5
        """)
    void shouldSplitWeekdayHoursIntoRegularAndOvertime(BigDecimal weekdayHours, BigDecimal overtimeThreshold,
                                                        BigDecimal expectedRegularHours, BigDecimal expectedOvertimeHours) {
        HourSplit split = WeeklyPayCalculator.splitWeekdayHours(weekdayHours, overtimeThreshold);
        assertMoneyEquals(expectedRegularHours, split.regularHours());
        assertMoneyEquals(expectedOvertimeHours, split.overtimeHours());
    }

    @Description("""
        Overtime is paid at 1.5x the base rate; Sunday and holiday hours are always paid
        at 2x the base rate. This table takes already-classified regular/overtime hours
        as direct inputs -- see shouldSplitWeekdayHoursIntoRegularAndOvertime for the
        40-hour threshold rule that produces them.
        """)
    @TableTest("""
        Scenario                   | Regular Hours | Overtime Hours | Sunday Hours | Holiday Hours | Hourly Rate | Total Pay?
        Regular hours only         | 40            | 0              | 0            | 0              | 15.00       | 600.00
        Overtime hours only        | 0             | 5              | 0            | 0              | 15.00       | 112.50
        Sunday hours only          | 0             | 0              | 8            | 0              | 15.00       | 240.00
        Holiday hours only         | 0             | 0              | 0            | 8              | 15.00       | 240.00
        All hour types combined    | 40            | 5              | 8            | 8              | 15.00       | 1192.50
        Zero rate                  | 40            | 5              | 8            | 8              | 0.00        | 0.00
        Zero hours                 | 0             | 0              | 0            | 0              | 15.00       | 0.00
        """)
    void shouldCalculatePayFromClassifiedHours(BigDecimal regularHours, BigDecimal overtimeHours,
                                                BigDecimal sundayHours, BigDecimal holidayHours,
                                                BigDecimal hourlyRate, BigDecimal expectedTotalPay) {
        BigDecimal totalPay = WeeklyPayCalculator.calculatePay(regularHours, overtimeHours, sundayHours, holidayHours, hourlyRate);
        assertMoneyEquals(expectedTotalPay, totalPay);
    }

    @Description("""
        Exercises the composed feature (raw weekday/Sunday/holiday hours in, total pay out)
        rather than re-testing the 40-hour threshold or per-category arithmetic already
        covered elsewhere. Assumption: only the hourly rate is validated per the spec: hours
        are not independently checked for negativity. The "negative hours floored at zero"
        row covers the spec's "total pay cannot go below zero" rule for the one case where
        it is reachable -- a negative raw hours value flowing through, e.g. from an upstream
        correction. Open question: should negative hours be rejected instead of floored?
        """)
    @TableTest("""
        Scenario                           | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Total Pay?
        Straight weekday pay               | 35            | 0            | 0             | 20.00       | 700.00
        Weekday pay with overtime          | 45            | 0            | 0             | 20.00       | 950.00
        Combined weekday, Sunday, holiday  | 45            | 6            | 4             | 20.00       | 1350.00
        Zero hours, zero rate              | 0             | 0            | 0             | 0.00        | 0.00
        Negative hours floored at zero     | -10           | 0            | 0             | 15.00       | 0.00
        """)
    void shouldCalculateWeeklyPay(BigDecimal weekdayHours, BigDecimal sundayHours,
                                   BigDecimal holidayHours, BigDecimal hourlyRate, BigDecimal expectedTotalPay) {
        BigDecimal totalPay = WeeklyPayCalculator.calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate);
        assertMoneyEquals(expectedTotalPay, totalPay);
    }

    @TableTest("""
        Scenario                            | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Throws?
        Negative rate, hours irrelevant     | {0, 45}       | 0            | 0             | -0.01       | java.lang.IllegalArgumentException
        Deeply negative rate                | 0             | 0            | 0             | -50.00      | java.lang.IllegalArgumentException
        """)
    void shouldRejectNegativeHourlyRate(BigDecimal weekdayHours, BigDecimal sundayHours, BigDecimal holidayHours,
                                         BigDecimal hourlyRate, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> WeeklyPayCalculator.calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate));
    }

    private static void assertMoneyEquals(BigDecimal expected, BigDecimal actual) {
        assertEquals(0, expected.compareTo(actual), () -> "expected " + expected + " but was " + actual);
    }
}
