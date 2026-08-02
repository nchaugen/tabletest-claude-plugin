package payroll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.function.Executable;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WeeklyPayCalculatorTest {

    @DisplayName("Pays regular and overtime rates for weekday hours")
    @TableTest("""
        Scenario                         | Weekday Hours | Overtime Threshold (hrs) | Hourly Rate | Weekly Pay?
        No hours worked                  | 0             | 40                       | 20          | 0
        Below the overtime threshold     | 30            | 40                       | 20          | 600
        At the overtime threshold        | 40            | 40                       | 20          | 800
        Just past the overtime threshold | 40.5          | 40                       | 20          | 815
        """)
    void paysRegularAndOvertimeRatesForWeekdayHours(double weekdayHours, int overtimeThresholdHours, double hourlyRate, double weeklyPay) {
        assertEquals(weeklyPay, WeeklyPayCalculator.calculateWeeklyPay(weekdayHours, 0, 0, hourlyRate), 0.001);
    }

    @DisplayName("Pays Sunday hours at double time")
    @TableTest("""
        Scenario               | Sunday Hours | Hourly Rate | Weekly Pay?
        No Sunday hours worked | 0            | 20          | 0
        Sunday hours worked    | 6            | 20          | 240
        """)
    void paysSundayHoursAtDoubleTime(double sundayHours, double hourlyRate, double weeklyPay) {
        assertEquals(weeklyPay, WeeklyPayCalculator.calculateWeeklyPay(0, sundayHours, 0, hourlyRate), 0.001);
    }

    @DisplayName("Pays holiday hours at double time")
    @TableTest("""
        Scenario                | Holiday Hours | Hourly Rate | Weekly Pay?
        No holiday hours worked | 0             | 20          | 0
        Holiday hours worked    | 8             | 20          | 320
        """)
    void paysHolidayHoursAtDoubleTime(double holidayHours, double hourlyRate, double weeklyPay) {
        assertEquals(weeklyPay, WeeklyPayCalculator.calculateWeeklyPay(0, 0, holidayHours, hourlyRate), 0.001);
    }

    @DisplayName("Floors total weekly pay at zero")
    @Description("""
        Assumes weekday hours may be negative to represent pay corrections or
        adjustments, since the rules do not otherwise explain when a negative
        total could occur. Sunday and holiday hours are held at representative
        values because the floor applies to the combined total regardless of
        which component is negative.
        """)
    @TableTest("""
        Scenario                                     | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Weekly Pay?
        Positive components net to a positive total  | 5             | 0            | 0             | 10          | 50
        Negative and positive components net to zero | -10           | 5            | 0             | 10          | 0
        Negative components outweigh positive ones   | -11           | 5            | 0             | 10          | 0
        """)
    void floorsTotalWeeklyPayAtZero(double weekdayHours, double sundayHours, double holidayHours, double hourlyRate, double weeklyPay) {
        assertEquals(weeklyPay, WeeklyPayCalculator.calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate), 0.001);
    }

    @DisplayName("Rejects a negative hourly rate")
    @TableTest("""
        Scenario                  | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Throws?
        Zero rate is accepted     | 10            | 0            | 0             | 0           |
        Negative rate is rejected | 10            | 0            | 0             | -0.01       | java.lang.IllegalArgumentException
        """)
    void rejectsNegativeHourlyRate(double weekdayHours, double sundayHours, double holidayHours, double hourlyRate, Class<? extends Throwable> throwsException) {
        assertEquals(throwsException, thrownBy(() -> WeeklyPayCalculator.calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate)));
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
