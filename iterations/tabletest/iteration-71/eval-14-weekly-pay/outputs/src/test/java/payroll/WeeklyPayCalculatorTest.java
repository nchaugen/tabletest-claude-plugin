package payroll;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static payroll.WeeklyPayCalculator.calculateWeeklyPay;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.function.Executable;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

class WeeklyPayCalculatorTest {

    @DisplayName("Calculates weekday pay with overtime beyond 40 hours")
    @Description("""
        The 40-hour overtime threshold is a fixed business rule, not a parameter of the
        calculation. Sunday and holiday hours are held at zero here; each has its own table
        below.
        """)
    @TableTest("""
        Scenario                     | Weekday Hours | Hourly Rate ($) | Weekly Pay ($)?
        Below the overtime threshold | 20            | 15              | 300
        At the overtime threshold    | 40            | 20              | 800
        Just past the threshold      | 41            | 10              | 415
        Well past the threshold      | 50            | 8               | 440
        """)
    void calculatesWeekdayPayWithOvertimeBeyond40Hours(BigDecimal weekdayHours, BigDecimal hourlyRate,
                                                        BigDecimal weeklyPay) {
        assertEquals(0, weeklyPay.compareTo(
            calculateWeeklyPay(weekdayHours, BigDecimal.ZERO, BigDecimal.ZERO, hourlyRate)));
    }

    @DisplayName("Pays Sunday hours at double time with no overtime threshold")
    @Description("""
        Unlike weekday hours, Sunday hours are always paid at 2x with no 40-hour cap.
        Weekday and holiday hours are held at zero here; each has its own table.
        """)
    @TableTest("""
        Scenario                       | Sunday Hours | Hourly Rate ($) | Weekly Pay ($)?
        Some Sunday hours              | 8            | 20              | 320
        Sunday hours beyond 40, no cap | 50           | 10              | 1000
        """)
    void paysSundayHoursAtDoubleTimeWithNoOvertimeThreshold(BigDecimal sundayHours, BigDecimal hourlyRate,
                                                             BigDecimal weeklyPay) {
        assertEquals(0, weeklyPay.compareTo(
            calculateWeeklyPay(BigDecimal.ZERO, sundayHours, BigDecimal.ZERO, hourlyRate)));
    }

    @DisplayName("Pays holiday hours at double time with no overtime threshold")
    @Description("""
        Unlike weekday hours, holiday hours are always paid at 2x with no 40-hour cap.
        Weekday and Sunday hours are held at zero here; each has its own table.
        """)
    @TableTest("""
        Scenario                        | Holiday Hours | Hourly Rate ($) | Weekly Pay ($)?
        Some holiday hours              | 8             | 20              | 320
        Holiday hours beyond 40, no cap | 50            | 10              | 1000
        """)
    void paysHolidayHoursAtDoubleTimeWithNoOvertimeThreshold(BigDecimal holidayHours, BigDecimal hourlyRate,
                                                              BigDecimal weeklyPay) {
        assertEquals(0, weeklyPay.compareTo(
            calculateWeeklyPay(BigDecimal.ZERO, BigDecimal.ZERO, holidayHours, hourlyRate)));
    }

    @DisplayName("Sums weekday, Sunday and holiday pay independently")
    @Description("""
        Confirms the three pay components combine by simple addition: Sunday and holiday
        hours do not count toward the weekday overtime threshold, and weekday overtime does
        not change how Sunday or holiday hours are paid.
        """)
    @TableTest("""
        Scenario                                 | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate ($) | Weekly Pay ($)?
        Regular weekday hours plus premium hours | 35            | 10           | 5             | 10              | 650
        Weekday overtime plus premium hours      | 45            | 10           | 0             | 10              | 675
        """)
    void sumsWeekdayPayWithSundayAndHolidayPayIndependently(BigDecimal weekdayHours, BigDecimal sundayHours,
                                                             BigDecimal holidayHours, BigDecimal hourlyRate,
                                                             BigDecimal weeklyPay) {
        assertEquals(0, weeklyPay.compareTo(
            calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate)));
    }

    @DisplayName("Floors total weekly pay at zero")
    @Description("""
        Hour values may be negative to represent timesheet corrections; the calculation is
        not floored per component, but the final total pay can never go below zero.
        """)
    @TableTest("""
        Scenario                            | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate ($) | Weekly Pay ($)?
        Net negative correction is floored  | -10           | 0            | 0             | 20              | 0
        Small negative total is floored     | -1            | 0            | 0             | 1               | 0
        Exactly zero total stays zero       | -20           | 10           | 0             | 10              | 0
        Small positive total is not floored | 1             | 0            | 0             | 1               | 1
        """)
    void floorsTotalWeeklyPayAtZero(BigDecimal weekdayHours, BigDecimal sundayHours, BigDecimal holidayHours,
                                     BigDecimal hourlyRate, BigDecimal weeklyPay) {
        assertEquals(0, weeklyPay.compareTo(
            calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate)));
    }

    @DisplayName("Rejects a negative hourly rate")
    @Description("""
        Weekday, Sunday and holiday hours are held at zero since only the rate's sign is
        under test here.
        """)
    @TableTest("""
        Scenario                  | Hourly Rate ($) | Throws?
        Zero rate is allowed      | 0               |
        Negative rate is rejected | -0.01           | java.lang.IllegalArgumentException
        """)
    void rejectsANegativeHourlyRate(BigDecimal hourlyRate, Class<? extends Throwable> throws_) {
        assertEquals(throws_, thrownBy(() ->
            calculateWeeklyPay(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, hourlyRate)));
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
