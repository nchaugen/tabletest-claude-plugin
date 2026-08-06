package payroll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.function.Executable;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PayCalculatorTest {

    @DisplayName("Calculates weekly pay from weekday, Sunday and holiday hours")
    @Description("""
        Assumes a standard 40-hour work week is the overtime threshold for weekday hours.
        Sunday and holiday hours are always paid at double time regardless of how many hours
        are worked, and do not count toward the weekday overtime threshold or accrue their
        own overtime.
        """)
    @TableTest("""
        Scenario                                           | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Weekly Pay?
        Weekday hours below the overtime threshold         | 35            | 0            | 0             | 20.00       | 700.00
        Weekday hours at the overtime threshold            | 40            | 0            | 0             | 20.00       | 800.00
        Weekday hours just past the overtime threshold     | 41            | 0            | 0             | 20.00       | 830.00
        Sunday hours paid at double time                   | 0             | 10           | 0             | 15.00       | 300.00
        Holiday hours paid at double time                  | 0             | 0            | 8             | 15.00       | 240.00
        Weekday overtime, Sunday and holiday hours combine | 45            | 5            | 8             | 20.00       | 1470.00
        """)
    void calculatesWeeklyPay(double weekdayHours, double sundayHours, double holidayHours,
                              BigDecimal hourlyRate, BigDecimal weeklyPay) {
        assertEquals(0, weeklyPay.compareTo(
                PayCalculator.calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate)));
    }

    @DisplayName("Floors weekly pay at zero when the computed total is negative")
    @Description("""
        Weekly pay is the sum of weekday, Sunday and holiday pay. Since the hourly rate is
        rejected outright when negative, the only way this sum can go negative is a negative
        hours value (e.g. a correction or adjustment entry). This table assumes negative hours
        are accepted as input and the resulting negative total is pinned at zero rather than
        rejected.
        """)
    @TableTest("""
        Scenario                                                               | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Weekly Pay?
        Negative hours produce a negative computed total                       | -20           | 0            | 0             | 20.00       | 0.00
        Computed pay is exactly zero                                           | 0             | 0            | 0             | 20.00       | 0.00
        Negative weekday pay offset by positive Sunday pay still nets negative | -50           | 10           | 0             | 20.00       | 0.00
        """)
    void flooredAtZero(double weekdayHours, double sundayHours, double holidayHours,
                        BigDecimal hourlyRate, BigDecimal weeklyPay) {
        assertEquals(0, weeklyPay.compareTo(
                PayCalculator.calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate)));
    }

    @DisplayName("Rejects a negative hourly rate")
    @TableTest("""
        Scenario                    | Hourly Rate | Throws?
        At the minimum rate         | 0.00        |
        Just below the minimum rate | -0.01       | java.lang.IllegalArgumentException
        """)
    void rejectsNegativeHourlyRate(BigDecimal hourlyRate, Class<? extends Throwable> throws_) {
        assertEquals(throws_, thrownBy(() -> PayCalculator.calculateWeeklyPay(10, 0, 0, hourlyRate)));
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
