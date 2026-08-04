package payroll;

import org.junit.jupiter.api.function.Executable;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PayCalculatorTest {

    @Description("""
        The 40-hour overtime threshold is a fixed rule of the calculation, not a
        configurable input, so weekday hours are tested straddling 40/41 rather than
        through a threshold column.
        Weekday hours are assumed to be able to go negative (e.g. a correction to a
        prior week), which is the only way the weighted sum can go negative and
        exercises the "total pay cannot go below zero" floor.
        """)
    @TableTest("""
        Scenario                                       | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Weekly Pay?
        Weekday hours below the overtime threshold     | 30            | 0            | 0             | 10          | 300
        Weekday hours at the overtime threshold        | 40            | 0            | 0             | 10          | 400
        Weekday hours just past the overtime threshold | 41            | 0            | 0             | 10          | 415
        Sunday hours only                              | 0             | 8            | 0             | 10          | 160
        Holiday hours only                             | 0             | 0            | 8             | 10          | 160
        All hour types combined                        | 45            | 8            | 8             | 10          | 795
        Zero hourly rate                               | 45            | 8            | 8             | 0           | 0
        Negative weekday hours                         | -10           | 0            | 0             | 10          | 0
        """)
    void calculatesWeeklyPayFromHoursAndRate(double weekdayHours, double sundayHours, double holidayHours,
                                              double hourlyRate, double weeklyPay) {
        assertEquals(weeklyPay, PayCalculator.calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate));
    }

    @Description("""
        Weekday, Sunday and holiday hours are held at zero throughout since this rule
        is about the hourly rate only and they belong to the arithmetic rule covered
        in calculatesWeeklyPayFromHoursAndRate.
        """)
    @TableTest("""
        Scenario               | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Throws?
        At the minimum rate    | 0             | 0            | 0             | 0           |
        Just below the minimum | 0             | 0            | 0             | -0.01       | java.lang.IllegalArgumentException
        """)
    void rejectsNegativeHourlyRate(double weekdayHours, double sundayHours, double holidayHours,
                                    double hourlyRate, Class<? extends Throwable> throws_) {
        assertEquals(throws_, thrownBy(() -> PayCalculator.calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate)));
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
