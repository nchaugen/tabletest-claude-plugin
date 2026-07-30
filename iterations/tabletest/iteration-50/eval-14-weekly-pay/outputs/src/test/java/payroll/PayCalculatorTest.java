package payroll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PayCalculatorTest {

    @DisplayName("Splits weekday hours into regular and overtime hours")
    @TableTest("""
        Scenario                          | Weekday Hours | Regular Hours? | Overtime Hours?
        Below the overtime threshold      | 30             | 30             | 0
        At the overtime threshold         | 40             | 40             | 0
        Just past the overtime threshold  | 45             | 40             | 5
        """)
    void splitsWeekdayHoursIntoRegularAndOvertime(double weekdayHours, double regularHours, double overtimeHours) {
        WeekdaySplit split = PayCalculator.splitWeekdayHours(weekdayHours);

        assertEquals(regularHours, split.regularHours(), 0.0001);
        assertEquals(overtimeHours, split.overtimeHours(), 0.0001);
    }

    @DisplayName("Computes weekly pay from classified hours and hourly rate")
    @Description("""
        Assumes regular/overtime/Sunday/holiday hours may be negative to represent a
        correction or adjustment entry (e.g. deducting hours credited in error). Open
        question: whether such corrections are actually part of this feature or belong
        to a separate adjustments capability.
        """)
    @TableTest("""
        Scenario                          | Regular Hours | Overtime Hours | Sunday Hours | Holiday Hours | Hourly Rate | Total Pay?
        Regular hours paid at base rate   | 40             | 0               | 0             | 0              | 20           | 800
        Overtime hours paid at 1.5x       | 0              | 5               | 0             | 0              | 20           | 150
        Sunday hours paid at 2x           | 0              | 0               | 8             | 0              | 20           | 320
        Holiday hours paid at 2x          | 0              | 0               | 0             | 8              | 20           | 320
        All hour types combined           | 40             | 5               | 8             | 8              | 20           | 1590
        Correction drives pay below zero  | -10            | 0               | 0             | 0              | 20           | 0
        """)
    void computesWeeklyPayFromClassifiedHours(double regularHours, double overtimeHours,
                                               double sundayHours, double holidayHours,
                                               BigDecimal hourlyRate, BigDecimal totalPay) {
        BigDecimal actual = PayCalculator.calculatePay(regularHours, overtimeHours, sundayHours, holidayHours, hourlyRate);

        assertEquals(0, totalPay.compareTo(actual));
    }

    @DisplayName("Rejects a negative hourly rate")
    @Description("""
        Fixed for all rows: 40 regular hours, 0 overtime, 0 Sunday, 0 holiday hours -
        rate validation does not depend on the hours worked.
        """)
    @TableTest("""
        Scenario      | Hourly Rate | Throws?
        Zero rate     | 0            |
        Positive rate | 25           |
        Negative rate | -0.01        | java.lang.IllegalArgumentException
        """)
    void rejectsNegativeHourlyRate(BigDecimal hourlyRate, Class<? extends Throwable> throwsException) {
        Class<? extends Throwable> thrown = thrownBy(() ->
            PayCalculator.calculatePay(40, 0, 0, 0, hourlyRate));

        assertEquals(throwsException, thrown);
    }

    @Test
    void combinesSplitAndPayCalculationEndToEnd() {
        BigDecimal totalPay = PayCalculator.calculateWeeklyPay(45, 8, 8, new BigDecimal("20"));

        assertEquals(0, new BigDecimal("1590").compareTo(totalPay));
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
