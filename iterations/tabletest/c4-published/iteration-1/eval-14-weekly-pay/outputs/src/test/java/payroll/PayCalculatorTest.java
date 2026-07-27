package payroll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PayCalculatorTest {

    @DisplayName("Weekday hours split into regular and overtime by the 40-hour threshold")
    @TableTest("""
        Scenario                         | Weekday Hours | Regular Hours? | Overtime Hours?
        Below the overtime threshold     | 32            | 32             | 0
        At the overtime threshold        | 40            | 40             | 0
        Just past the overtime threshold | 45            | 40             | 5
        """)
    void classifiesWeekdayHours(double weekdayHours, double regularHours, double overtimeHours) {
        WeekdayHoursBreakdown breakdown = PayCalculator.classifyWeekdayHours(weekdayHours);
        assertEquals(regularHours, breakdown.regularHours());
        assertEquals(overtimeHours, breakdown.overtimeHours());
    }

    @DisplayName("Weekly pay by hour type and rate")
    @Description("""
        Regular hours pay the base rate, overtime pays 1.5x, and Sunday and
        holiday hours both always pay 2x, regardless of whether the hours
        also happen to be overtime. Pay is the sum of all four contributions.

        Regular, overtime, Sunday and holiday hours are assumed non-negative
        in normal use, but this calculation does not reject negative values
        outright — instead the total pay floors at zero, covering
        corrections or adjustments that might otherwise push the total below
        zero.
        """)
    @TableTest("""
        Scenario                            | Regular Hours | Overtime Hours | Sunday Hours | Holiday Hours | Hourly Rate | Total Pay?
        Regular hours only                  | 40            | 0              | 0            | 0              | 15.00       | 600.00
        Overtime hours only                 | 0             | 5              | 0            | 0              | 15.00       | 112.50
        Sunday hours only                   | 0             | 0              | 8            | 0              | 15.00       | 240.00
        Holiday hours only                  | 0             | 0              | 0            | 8              | 15.00       | 240.00
        All hour types combined             | 40            | 5              | 8            | 8              | 15.00       | 1192.50
        Zero hourly rate                    | 40            | 5              | 8            | 8              | 0.00        | 0.00
        Negative hours floor total pay      | -50           | 0              | 0            | 0              | 15.00       | 0.00
        """)
    void calculatesPayByHourType(
        double regularHours,
        double overtimeHours,
        double sundayHours,
        double holidayHours,
        BigDecimal hourlyRate,
        BigDecimal totalPay
    ) {
        assertEquals(
            0,
            totalPay.compareTo(
                PayCalculator.calculatePay(regularHours, overtimeHours, sundayHours, holidayHours, hourlyRate)
            )
        );
    }

    @DisplayName("Hourly rate validation")
    @Description("""
        A rate of zero is valid and is exercised as part of the weekly pay
        arithmetic table above; this table covers the rejection case.
        """)
    @TableTest("""
        Scenario              | Hourly Rate | Throws?
        Negative hourly rate  | -10.00      | java.lang.IllegalArgumentException
        """)
    void rejectsNegativeHourlyRate(BigDecimal hourlyRate, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> PayCalculator.validateHourlyRate(hourlyRate));
    }

    @Test
    @DisplayName("Weekly pay end to end wires weekday classification into the arithmetic")
    void calculatesWeeklyPayEndToEnd() {
        assertEquals(
            0,
            new BigDecimal("1192.50").compareTo(
                PayCalculator.calculateWeeklyPay(45, 8, 8, new BigDecimal("15.00"))
            )
        );
    }
}
