package payroll;

import org.junit.jupiter.api.function.Executable;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PayCalculatorTest {

    @Description("""
        Sunday and holiday hours are tracked as separate inputs from weekday hours and never count
        toward this 40-hour weekday threshold; see computesDoubleTimePayForSundayAndHolidayHours.
        Hours may be negative to represent timesheet corrections.
        """)
    @TableTest("""
        Scenario                  | Weekday Hours | Overtime Threshold (hrs) | Regular Hours? | Overtime Hours?
        Below the threshold       | 32            | 40                       | 32             | 0
        At the threshold          | 40            | 40                       | 40             | 0
        Just past the threshold   | 40.5          | 40                       | 40             | 0.5
        Negative correction hours | -5            | 40                       | -5             | 0
        """)
    void splitsWeekdayHoursAtTheOvertimeThreshold(BigDecimal weekdayHours, BigDecimal overtimeThreshold,
                                                   BigDecimal regularHours, BigDecimal overtimeHours) {
        HourSplit split = PayCalculator.classifyWeekdayHours(weekdayHours, overtimeThreshold);
        assertEqualsIgnoringScale(regularHours, split.regularHours());
        assertEqualsIgnoringScale(overtimeHours, split.overtimeHours());
    }

    @TableTest("""
        Scenario                      | Regular Hours | Overtime Hours | Hourly Rate | Weekday Pay?
        Regular hours only            | 32            | 0              | 20          | 640
        Regular and overtime combined | 40            | 5              | 20          | 950
        Negative correction hours     | -5            | 0              | 20          | -100
        Zero hourly rate              | 10            | 2              | 0           | 0
        """)
    void computesWeekdayPayFromRegularAndOvertimeHours(BigDecimal regularHours, BigDecimal overtimeHours,
                                                         BigDecimal hourlyRate, BigDecimal weekdayPay) {
        assertEqualsIgnoringScale(weekdayPay, PayCalculator.weekdayPay(regularHours, overtimeHours, hourlyRate));
    }

    @Description("""
        Sunday and holiday hours are always paid at double time (2x); they do not interact with the
        40-hour weekday overtime threshold.
        """)
    @TableTest("""
        Scenario                    | Sunday Hours | Holiday Hours | Hourly Rate | Double-Time Pay?
        Sunday hours only           | 6            | 0             | 20          | 240
        Holiday hours only          | 0            | 8             | 20          | 320
        Sunday and holiday combined | 6            | 8             | 20          | 560
        Negative correction hours   | -3           | 0             | 20          | -120
        Zero hourly rate            | 5            | 5             | 0           | 0
        """)
    void computesDoubleTimePayForSundayAndHolidayHours(BigDecimal sundayHours, BigDecimal holidayHours,
                                                         BigDecimal hourlyRate, BigDecimal doubleTimePay) {
        assertEqualsIgnoringScale(doubleTimePay,
            PayCalculator.doubleTimePay(sundayHours, holidayHours, hourlyRate));
    }

    @Description("""
        Weekday, Sunday, and holiday hours are held at zero throughout since only the hourly rate
        boundary is under test here.
        """)
    @TableTest("""
        Scenario                  | Hourly Rate | Throws?
        Zero rate is accepted     | 0           |
        Negative rate is rejected | -0.01       | java.lang.IllegalArgumentException
        """)
    void rejectsANegativeHourlyRate(BigDecimal hourlyRate, Class<? extends Throwable> throws_) {
        assertEquals(throws_, thrownBy(() ->
            PayCalculator.calculateWeeklyPay(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, hourlyRate)));
    }

    @Description("""
        Total pay cannot go below zero, even when a negative hour correction would otherwise produce
        a negative subtotal.
        """)
    @TableTest("""
        Scenario                                           | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Total Pay?
        Ordinary week within the weekday threshold         | 40            | 0            | 0             | 20          | 800
        Week combining overtime, Sunday, and holiday hours | 45            | 6            | 8             | 20          | 1510
        Large negative correction floors total pay at zero | -100          | 0            | 0             | 20          | 0
        All hours zero                                     | 0             | 0            | 0             | 20          | 0
        """)
    void sumsWeekdaySundayAndHolidayPayIntoTotalWeeklyPay(BigDecimal weekdayHours, BigDecimal sundayHours,
                                                            BigDecimal holidayHours, BigDecimal hourlyRate,
                                                            BigDecimal totalPay) {
        assertEqualsIgnoringScale(totalPay,
            PayCalculator.calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate));
    }

    @TypeConverter
    public static BigDecimal parseAmount(String value) {
        return new BigDecimal(value);
    }

    private static void assertEqualsIgnoringScale(BigDecimal expected, BigDecimal actual) {
        assertEquals(0, expected.compareTo(actual), () -> "expected " + expected + " but was " + actual);
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
