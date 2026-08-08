package payroll;

import org.junit.jupiter.api.function.Executable;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WeeklyPayCalculatorTest {

    private final WeeklyPayCalculator calculator = new WeeklyPayCalculator();

    @Description("""
        Overtime Threshold (hrs) is the weekday hour count above which the overtime rate applies.
        It is fixed policy the calculator does not take as a parameter, so every row holds it at 40.
        """)
    @TableTest("""
        Scenario                                                | Weekday Hours | Sunday Hours | Holiday Hours | Overtime Threshold (hrs) | Hourly Rate | Weekly Pay?
        Weekday hours below the overtime threshold              | 39            | 0            | 0             | 40                       | 20          | 780
        Weekday hours at the overtime threshold                 | 40            | 0            | 0             | 40                       | 20          | 800
        Weekday hours just past the overtime threshold          | 41            | 0            | 0             | 40                       | 20          | 830
        Sunday hours paid at double time                        | 0             | 10           | 0             | 40                       | 20          | 400
        Holiday hours paid at double time                       | 0             | 0            | 5             | 40                       | 20          | 200
        Weekday overtime combined with Sunday and holiday hours | 45            | 8            | 8             | 40                       | 20          | 1590
        """)
    void calculatesWeeklyPay(double weekdayHours, double sundayHours, double holidayHours,
                              int overtimeThresholdHours, double hourlyRate, double weeklyPay) {
        assertEquals(weeklyPay, calculator.calculate(weekdayHours, sundayHours, holidayHours, hourlyRate), 0.001);
    }

    @Description("""
        Assumes negative hour values can occur (e.g. timesheet correction entries) even though only a
        negative hourly rate is explicitly rejected. Any resulting negative subtotal is floored to
        zero rather than paid out.
        """)
    @TableTest("""
        Scenario                               | Weekday Hours   | Sunday Hours | Holiday Hours | Hourly Rate | Weekly Pay?
        No hours worked                        | 0               | 0            | 0             | 20          | 0
        Negative hours are floored to zero pay | {-1, -10, -100} | 0            | 0             | 20          | 0
        """)
    void floorsWeeklyPayAtZero(double weekdayHours, double sundayHours, double holidayHours,
                                double hourlyRate, double weeklyPay) {
        assertEquals(weeklyPay, calculator.calculate(weekdayHours, sundayHours, holidayHours, hourlyRate), 0.001);
    }

    @TableTest("""
        Scenario                         | Hourly Rate | Throws?
        Zero hourly rate is accepted     | 0           |
        Negative hourly rate is rejected | -0.01       | java.lang.IllegalArgumentException
        """)
    void rejectsNegativeHourlyRate(double hourlyRate, Class<? extends Throwable> throws_) {
        assertEquals(throws_, thrownBy(() -> calculator.calculate(10, 0, 0, hourlyRate)));
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
