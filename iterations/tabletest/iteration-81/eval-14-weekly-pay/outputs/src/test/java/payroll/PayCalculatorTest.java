package payroll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.function.Executable;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PayCalculatorTest {

    private final PayCalculator calculator = new PayCalculator();

    @DisplayName("Computes weekly pay from weekday, Sunday and holiday hours")
    @Description("""
        The standard workweek is 40 hours; weekday hours beyond that are overtime.
        """)
    @TableTest("""
        Scenario                                       | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Pay?
        Regular hours only                             | 35            | 0            | 0             | 10.00       | 350.00
        At the overtime threshold                      | 40            | 0            | 0             | 10.00       | 400.00
        Just past the overtime threshold               | 41            | 0            | 0             | 10.00       | 415.00
        Sunday hours only                              | 0             | 5            | 0             | 12.00       | 120.00
        Holiday hours only                             | 0             | 0            | 8             | 15.00       | 240.00
        Regular, overtime, Sunday and holiday combined | 45            | 6            | 8             | 10.00       | 755.00
        """)
    void computesWeeklyPay(double weekdayHours, double sundayHours, double holidayHours, BigDecimal hourlyRate,
                            BigDecimal pay) {
        assertEquals(0, pay.compareTo(
            calculator.calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate)));
    }

    @DisplayName("Floors total pay at zero when hour corrections make it negative")
    @Description("""
        Assumes weekday, Sunday and holiday hours may be negative, e.g. to record a
        correction against a prior week's entry; only the hourly rate is validated
        as non-negative. Open question: should negative hours instead be rejected
        as invalid input?
        """)
    @TableTest("""
        Scenario                     | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Pay?
        Computed pay below zero      | -41           | 20           | 0             | 10.00       | 0.00
        Computed pay exactly at zero | -40           | 20           | 0             | 10.00       | 0.00
        Computed pay just above zero | -39           | 20           | 0             | 10.00       | 10.00
        """)
    void floorsTotalPayAtZero(double weekdayHours, double sundayHours, double holidayHours, BigDecimal hourlyRate,
                               BigDecimal pay) {
        assertEquals(0, pay.compareTo(
            calculator.calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate)));
    }

    @DisplayName("Rejects a negative hourly rate")
    @Description("""
        Weekday, Sunday and holiday hours are held at zero throughout since this
        rule depends only on the hourly rate.
        """)
    @TableTest("""
        Scenario                            | Hourly Rate | Throws?
        Zero rate, the minimum allowed      | 0.00        |
        Just below the minimum allowed rate | -0.01       | java.lang.IllegalArgumentException
        """)
    void rejectsANegativeHourlyRate(BigDecimal hourlyRate, Class<? extends Throwable> throwsException) {
        assertEquals(throwsException, thrownBy(() -> calculator.calculateWeeklyPay(0, 0, 0, hourlyRate)));
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
