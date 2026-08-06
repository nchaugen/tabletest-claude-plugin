package payroll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.function.Executable;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WeeklyPayCalculatorTest {

    @DisplayName("Pays weekday hours at the base rate up to 40 hours, and time-and-a-half beyond it")
    @Description("""
        Weekday Hours may be negative to represent a payroll correction carried over from a
        prior period. The result here is not floored at zero — flooring the combined weekly
        total is a separate rule, verified in calculatesTotalWeeklyPay.
        """)
    @TableTest("""
        Scenario                         | Weekday Hours | Hourly Rate | Weekday Pay?
        Below the overtime threshold     | 30            | 20          | 600
        At the overtime threshold        | 40            | 20          | 800
        Just past the overtime threshold | 41            | 20          | 830
        Well past the overtime threshold | 48            | 15          | 780
        No hours worked                  | 0             | 20          | 0
        Negative hours from a correction | -5            | 20          | -100
        """)
    void computesWeekdayPay(double weekdayHours, double hourlyRate, double weekdayPay) {
        assertEquals(weekdayPay, WeeklyPayCalculator.weekdayPay(weekdayHours, hourlyRate));
    }

    @DisplayName("Pays Sunday and holiday hours at double time")
    @Description("""
        This formula backs both Sunday pay and holiday pay, since both are always paid at
        double time regardless of the category. Hours may be negative to represent a payroll
        correction; the result here is not floored at zero (see calculatesTotalWeeklyPay).
        """)
    @TableTest("""
        Scenario                         | Hours | Hourly Rate | Pay?
        Sunday shift                     | 8     | 20          | 320
        Holiday shift                    | 6     | 15          | 180
        No hours worked                  | 0     | 20          | 0
        Negative hours from a correction | -4    | 20          | -160
        """)
    void computesDoubleTimePay(double hours, double hourlyRate, double pay) {
        assertEquals(pay, WeeklyPayCalculator.doubleTimePay(hours, hourlyRate));
    }

    @DisplayName("Rejects a negative hourly rate")
    @Description("""
        Weekday, Sunday and holiday hours are held at 0 throughout — their effect on pay is
        covered by computesWeekdayPay and computesDoubleTimePay. This table is only about the
        hourly rate.
        """)
    @TableTest("""
        Scenario        | Hourly Rate | Throws?
        Rate below zero | -0.01       | java.lang.IllegalArgumentException
        Rate at zero    | 0           |
        Rate above zero | 20          |
        """)
    void rejectsNegativeHourlyRate(double hourlyRate, Class<? extends Throwable> throws_) {
        assertEquals(throws_, thrownBy(() -> WeeklyPayCalculator.calculate(0, 0, 0, hourlyRate)));
    }

    @DisplayName("Combines category pay into a weekly total floored at zero")
    @Description("""
        Weekday Hours may be negative to represent a payroll correction (see computesWeekdayPay).
        This table verifies that weekday, Sunday and holiday pay combine additively, and that a
        combined total which would otherwise be negative is floored at zero rather than paid out
        as a negative amount.
        """)
    @TableTest("""
        Scenario                                     | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Total Pay?
        All categories combine additively            | 40            | 8            | 8             | 20          | 1440
        Correction exactly balances category pay     | -50           | 25           | 0             | 10          | 0
        Correction exceeds the other categories' pay | -100          | 0            | 0             | 10          | 0
        """)
    void calculatesTotalWeeklyPay(double weekdayHours, double sundayHours, double holidayHours,
                                   double hourlyRate, double totalPay) {
        assertEquals(totalPay, WeeklyPayCalculator.calculate(weekdayHours, sundayHours, holidayHours, hourlyRate));
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
