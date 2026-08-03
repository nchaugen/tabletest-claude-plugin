package payroll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.function.Executable;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PayCalculatorTest {

    @DisplayName("Splits weekday hours into regular and overtime portions at the 40-hour threshold")
    @TableTest("""
        Scenario                         | Weekday Hours | Regular Hours? | Overtime Hours?
        No hours worked                  | 0             | 0              | 0
        Below the overtime threshold     | 30            | 30             | 0
        At the overtime threshold        | 40            | 40             | 0
        Just past the overtime threshold | 41            | 40             | 1
        """)
    void splitsWeekdayHours(double weekdayHours, double regularHours, double overtimeHours) {
        HoursSplit split = PayCalculator.splitWeekdayHours(weekdayHours);
        assertEquals(regularHours, split.regularHours());
        assertEquals(overtimeHours, split.overtimeHours());
    }

    @DisplayName("Combines regular, overtime, Sunday and holiday hours into weekly pay at their respective rates")
    @Description("""
        Regular hours are paid at the base hourly rate; overtime is 1.5x and Sunday and
        holiday hours are 2x. Each row isolates one rate, and the last row shows them
        combining into a single weekly total.
        """)
    @TableTest("""
        Scenario                              | Regular Hours | Overtime Hours | Sunday Hours | Holiday Hours | Hourly Rate | Weekly Pay?
        No hours worked                       | 0             | 0              | 0            | 0             | 20          | 0
        Only regular hours                    | 40            | 0              | 0            | 0             | 20          | 800
        Only overtime hours                   | 0             | 5              | 0            | 0             | 20          | 150
        Only Sunday hours                     | 0             | 0              | 8            | 0             | 20          | 320
        Only holiday hours                    | 0             | 0              | 0            | 8             | 20          | 320
        Regular, overtime, Sunday and holiday | 40            | 5              | 8            | 8             | 20          | 1590
        """)
    void combinesHoursIntoWeeklyPay(double regularHours, double overtimeHours, double sundayHours,
                                     double holidayHours, double hourlyRate, double weeklyPay) {
        assertEquals(weeklyPay,
            PayCalculator.calculateWeeklyPay(regularHours, overtimeHours, sundayHours, holidayHours, hourlyRate));
    }

    @DisplayName("Floors weekly pay at zero when negative hour corrections outweigh positive hours")
    @Description("""
        Assumption: hour columns may carry negative values to represent correction entries
        (e.g. reversing a previous overpayment). Weekly pay is never allowed to go below zero,
        however negative the underlying correction is.
        """)
    @TableTest("""
        Scenario                                   | Regular Hours | Overtime Hours | Sunday Hours | Holiday Hours | Hourly Rate | Weekly Pay?
        Negative contribution absorbed, no floor   | 40            | 0              | -10          | 0             | 20          | 400
        Correction lands exactly at zero           | 40            | 0              | -20          | 0             | 20          | 0
        Correction pushes just below zero, floored | 40            | 0              | -21          | 0             | 20          | 0
        """)
    void floorsWeeklyPayAtZero(double regularHours, double overtimeHours, double sundayHours,
                                double holidayHours, double hourlyRate, double weeklyPay) {
        assertEquals(weeklyPay,
            PayCalculator.calculateWeeklyPay(regularHours, overtimeHours, sundayHours, holidayHours, hourlyRate));
    }

    @DisplayName("Rejects a negative hourly rate")
    @Description("""
        Regular, overtime, Sunday and holiday hours are held fixed at 40/0/0/0 since this
        table is about the rate boundary, not about how hours combine (see
        combinesHoursIntoWeeklyPay).
        """)
    @TableTest("""
        Scenario             | Hourly Rate | Throws?
        Positive hourly rate | 20          |
        Zero hourly rate     | 0           |
        Just below zero      | -0.01       | java.lang.IllegalArgumentException
        """)
    void rejectsNegativeHourlyRate(double hourlyRate, Class<? extends Throwable> throwsException) {
        assertEquals(throwsException,
            thrownBy(() -> PayCalculator.calculateWeeklyPay(40, 0, 0, 0, hourlyRate)));
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
