package payroll;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.function.Executable;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

class PayCalculatorTest {

    @Description("""
        The 40-hour weekly threshold and the 1.5x / 2x multipliers are fixed by pay policy,
        not configurable inputs, so they are not columns; the 40/41-hour rows pin the threshold
        directly. Weekday, Sunday and holiday hours are assumed to allow negative values (e.g.
        corrections to a previous week), which is why the floor-at-zero rows below use negative
        weekday hours to drive the net below zero.
        """)
    @TableTest("""
        Scenario                                                     | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Weekly Pay?
        Weekday hours at the overtime threshold                      | 40            | 0            | 0             | 20          | 800
        Weekday hour just past the threshold                         | 41            | 0            | 0             | 20          | 830
        Sunday hours pay double regardless of weekday hours          | 0             | 6            | 0             | 20          | 240
        Holiday hours pay double regardless of weekday hours         | 0             | 0            | 4             | 20          | 160
        Sunday and holiday hours do not count toward the weekday cap | 35            | 10           | 5             | 20          | 1300
        Negative weekday hours floor pay at zero                     | -10           | 0            | 0             | 20          | 0
        Net pay at exactly zero is not floored further               | -10           | 5            | 0             | 20          | 0
        Net pay just above zero is paid in full                      | -10           | 5.5          | 0             | 20          | 20
        """)
    void calculatesWeeklyPay(
            double weekdayHours, double sundayHours, double holidayHours, double hourlyRate, double weeklyPay) {
        assertEquals(
                weeklyPay,
                PayCalculator.calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate),
                0.001);
    }

    @Description("""
        Weekday, Sunday and holiday hours are held at zero throughout: this rule concerns only
        the hourly rate and does not depend on hours worked.
        """)
    @TableTest("""
        Scenario            | Hourly Rate | Throws?
        At the minimum rate | 0           |
        Just below zero     | -0.01       | java.lang.IllegalArgumentException
        """)
    void rejectsNegativeHourlyRate(double hourlyRate, Class<? extends Throwable> throws_) {
        assertEquals(throws_, thrownBy(() -> PayCalculator.calculateWeeklyPay(0, 0, 0, hourlyRate)));
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
