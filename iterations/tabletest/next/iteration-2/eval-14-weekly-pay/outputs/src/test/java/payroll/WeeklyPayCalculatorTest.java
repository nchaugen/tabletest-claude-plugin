package payroll;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WeeklyPayCalculatorTest {

    private final WeeklyPayCalculator calculator = new WeeklyPayCalculator();

    @Description("""
        Overtime Threshold shows the 40-hour boundary where weekday hours switch
        from the base rate to 1.5x overtime. Sunday and Holiday Hours are held at
        zero here to isolate weekday-only behavior; their own premiums are covered
        in separate tables below.
        """)
    @TableTest("""
        Scenario                     | Weekday Hours | Overtime Threshold | Hourly Rate | Sunday Hours | Holiday Hours | Pay?
        Zero hours                   | 0             | 40                  | 20.00       | 0            | 0              | 0.00
        Below threshold              | 30            | 40                  | 20.00       | 0            | 0              | 600.00
        At threshold                 | 40            | 40                  | 20.00       | 0            | 0              | 800.00
        Just over threshold          | 41            | 40                  | 20.00       | 0            | 0              | 830.00
        Well past threshold          | 45            | 40                  | 20.00       | 0            | 0              | 950.00
        Different rate with overtime | 45            | 40                  | 10.00       | 0            | 0              | 475.00
        """)
    void calculatesWeekdayRegularAndOvertimePay(double weekdayHours, int overtimeThreshold, double hourlyRate,
                                                 double sundayHours, double holidayHours, double expectedPay) {
        double pay = calculator.calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate);
        assertEquals(expectedPay, pay, 0.001);
    }

    @Description("""
        Sunday hours are always paid at 2x the base rate, with no 40-hour cap of
        their own. Weekday and Holiday Hours are held at zero to isolate this
        concern.
        """)
    @TableTest("""
        Scenario                                        | Sunday Hours | Hourly Rate | Weekday Hours | Holiday Hours | Pay?
        No sunday hours                                  | 0            | 20.00       | 0             | 0              | 0.00
        Few sunday hours                                 | 5            | 20.00       | 0             | 0              | 200.00
        Sunday hours exceed weekday overtime threshold   | 45           | 20.00       | 0             | 0              | 1800.00
        Different rate                                   | 8            | 15.00       | 0             | 0              | 240.00
        """)
    void calculatesSundayDoubleTimePay(double sundayHours, double hourlyRate, double weekdayHours,
                                       double holidayHours, double expectedPay) {
        double pay = calculator.calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate);
        assertEquals(expectedPay, pay, 0.001);
    }

    @Description("""
        Holiday hours are always paid at 2x the base rate, with no 40-hour cap of
        their own. Weekday and Sunday Hours are held at zero to isolate this
        concern.
        """)
    @TableTest("""
        Scenario                                        | Holiday Hours | Hourly Rate | Weekday Hours | Sunday Hours | Pay?
        No holiday hours                                 | 0             | 20.00       | 0             | 0            | 0.00
        Few holiday hours                                | 6             | 20.00       | 0             | 0            | 240.00
        Holiday hours exceed weekday overtime threshold  | 42            | 20.00       | 0             | 0            | 1680.00
        Different rate                                   | 4             | 25.00       | 0             | 0            | 200.00
        """)
    void calculatesHolidayDoubleTimePay(double holidayHours, double hourlyRate, double weekdayHours,
                                        double sundayHours, double expectedPay) {
        double pay = calculator.calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate);
        assertEquals(expectedPay, pay, 0.001);
    }

    @Description("""
        Weekday overtime, Sunday double-time, and Holiday double-time are
        calculated independently per category and summed; none of them affects
        another category's threshold.
        """)
    @TableTest("""
        Scenario                        | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Pay?
        Regular weekday hours only      | 35            | 0            | 0             | 20.00       | 700.00
        Weekday overtime plus sunday    | 45            | 6            | 0             | 20.00       | 1190.00
        Weekday overtime plus holiday   | 44            | 0            | 8             | 20.00       | 1240.00
        All categories together         | 42            | 4            | 3             | 15.00       | 855.00
        """)
    void calculatesCombinedWeeklyPay(double weekdayHours, double sundayHours, double holidayHours,
                                     double hourlyRate, double expectedPay) {
        double pay = calculator.calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate);
        assertEquals(expectedPay, pay, 0.001);
    }

    @Description("""
        Only the Hourly Rate is rejected when negative (see
        rejectsNegativeHourlyRate below); hours themselves are not validated, so a
        negative hours figure can still reach the calculation (e.g. a correction
        to a prior over-report) and drive the raw total below zero. Total pay is
        floored at zero based on the net total across all categories, not by
        clamping each category independently before summing.
        """)
    @TableTest("""
        Scenario                                | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Pay?
        Negative weekday hours alone            | -10           | 0            | 0             | 20.00       | 0.00
        Negative sunday hours alone              | 0             | -5           | 0             | 20.00       | 0.00
        Negative holiday hours alone             | 0             | 0            | -3            | 20.00       | 0.00
        Positive pay outweighed by negative hours| 10            | -20          | 0             | 20.00       | 0.00
        Positive and negative hours net to zero  | 10            | -5           | 0             | 20.00       | 0.00
        """)
    void flooresTotalPayAtZero(double weekdayHours, double sundayHours, double holidayHours,
                               double hourlyRate, double expectedPay) {
        double pay = calculator.calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate);
        assertEquals(expectedPay, pay, 0.001);
    }

    @TableTest("""
        Scenario                       | Hourly Rate | Weekday Hours | Sunday Hours | Holiday Hours | Throws?
        Negative rate                  | -0.01       | 10            | 0             | 0              | java.lang.IllegalArgumentException
        Clearly negative rate          | -50.00      | 10            | 0             | 0              | java.lang.IllegalArgumentException
        Zero rate accepted             | 0.00        | 10            | 0             | 0              |
        Positive rate accepted         | 20.00       | 10            | 0             | 0              |
        """)
    void rejectsNegativeHourlyRate(double hourlyRate, double weekdayHours, double sundayHours,
                                   double holidayHours, Class<? extends Exception> throws_) {
        if (throws_ != null) {
            assertThrows(throws_, () -> calculator.calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate));
        } else {
            assertDoesNotThrow(() -> calculator.calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate));
        }
    }
}
