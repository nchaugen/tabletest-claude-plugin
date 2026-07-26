package payroll;

import org.junit.jupiter.api.function.Executable;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WeeklyPayCalculatorTest {

    @TableTest("""
        Scenario                          | Weekday Hours | Regular Hours? | Overtime Hours?
        Below the overtime threshold      | 25            | 25             | 0
        At the overtime threshold         | 40            | 40             | 0
        Just past the overtime threshold  | 40.5          | 40             | 0.5
        """)
    void classifiesWeekdayHours(double weekdayHours, double regularHours, double overtimeHours) {
        WeekdayHoursSplit split = WeeklyPayCalculator.classifyWeekdayHours(weekdayHours);
        assertEquals(regularHours, split.regularHours(), 0.001);
        assertEquals(overtimeHours, split.overtimeHours(), 0.001);
    }

    @Description("""
        Regular and overtime hours are the output of classifyWeekdayHours above; this table takes
        them as direct inputs to test the pay arithmetic in isolation. Sunday and holiday hours are
        always paid at double time, independent of the 40-hour weekday threshold. Assumption: hour
        inputs may be negative to represent timesheet corrections, so the "cannot go below zero" rule
        has an observable trigger; whatever the computed raw total, pay is floored at 0.00. Hourly
        rate validation is covered separately below.
        """)
    @TableTest("""
        Scenario                             | Regular Hours | Overtime Hours | Sunday Hours | Holiday Hours | Hourly Rate | Total Pay?
        Regular hours only                   | 25            | 0              | 0            | 0             | 20.00       | 500.00
        Overtime paid at time-and-a-half     | 40            | 5              | 0            | 0             | 20.00       | 950.00
        Sunday hours paid at double time     | 0             | 0              | 8            | 0             | 20.00       | 320.00
        Holiday hours paid at double time    | 0             | 0              | 0            | 8             | 20.00       | 320.00
        All categories combined              | 40            | 5              | 8            | 8             | 20.00       | 1590.00
        Negative correction floored at zero  | -50           | 0              | 0            | 0             | 20.00       | 0.00
        """)
    void calculatesWeeklyPay(double regularHours, double overtimeHours, double sundayHours,
                              double holidayHours, double hourlyRate, double totalPay) {
        double actual = WeeklyPayCalculator.calculateWeeklyPay(
            regularHours, overtimeHours, sundayHours, holidayHours, hourlyRate);
        assertEquals(totalPay, actual, 0.001);
    }

    @TableTest("""
        Scenario       | Hourly Rate | Throws?
        Positive rate  | 20.00       |
        Zero rate      | 0.00        |
        Negative rate  | -0.01       | IllegalArgumentException
        """)
    void rejectsNegativeHourlyRate(double hourlyRate, Class<? extends Exception> throwsException) {
        assertOutcome(throwsException,
            () -> WeeklyPayCalculator.calculateWeeklyPay(0, 0, 0, 0, hourlyRate));
    }

    private static void assertOutcome(Class<? extends Exception> expected, Executable action) {
        if (expected != null) {
            assertThrows(expected, action);
        } else {
            assertDoesNotThrow(action);
        }
    }
}
