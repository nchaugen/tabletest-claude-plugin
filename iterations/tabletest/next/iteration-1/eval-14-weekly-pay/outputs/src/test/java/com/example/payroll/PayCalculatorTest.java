package com.example.payroll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.function.Executable;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PayCalculatorTest {

    private PayCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new PayCalculator();
    }

    @DisplayName("Weekday pay: regular hours vs. overtime")
    @Description("""
        Weekday hours up to 40 are paid at the base rate; hours beyond 40 are
        overtime at 1.5x the base rate. 40 is the boundary (still regular pay);
        41 is the first overtime hour.
        """)
    @TableTest("""
        Scenario                       | Weekday Hours | Hourly Rate | Weekday Pay?
        Zero hours                     | 0             | 20.00       | 0.00
        Below overtime threshold       | 30            | 20.00       | 600.00
        At overtime threshold          | 40            | 20.00       | 800.00
        Just past overtime threshold   | 41            | 20.00       | 830.00
        Well past overtime threshold   | 50            | 20.00       | 1100.00
        """)
    void calculatesWeekdayPay(BigDecimal weekdayHours, BigDecimal hourlyRate, BigDecimal weekdayPay) {
        assertThat(calculator.calculateWeekdayPay(weekdayHours, hourlyRate)).isEqualByComparingTo(weekdayPay);
    }

    @DisplayName("Double-time pay for Sunday and holiday hours")
    @Description("""
        Sunday hours and holiday hours are both always paid at 2x the base
        rate, with no threshold - the same rule and calculation applies to
        both categories, so calculateWeeklyPay uses this for each.
        """)
    @TableTest("""
        Scenario   | Hours | Hourly Rate | Double-Time Pay?
        Zero hours | 0     | 20.00       | 0.00
        Some hours | 8     | 20.00       | 320.00
        Many hours | 12    | 20.00       | 480.00
        """)
    void calculatesDoubleTimePay(BigDecimal hours, BigDecimal hourlyRate, BigDecimal doubleTimePay) {
        assertThat(calculator.calculateDoubleTimePay(hours, hourlyRate)).isEqualByComparingTo(doubleTimePay);
    }

    @DisplayName("Weekly pay combines weekday, Sunday and holiday pay, floored at zero")
    @Description("""
        Assumption: hours are normally non-negative, but the calculator accepts
        negative hour values to represent payroll corrections/adjustments -
        this is the only way total pay could go below zero given non-negative
        rates, and is why a floor-at-zero rule exists at all. Open question:
        confirm with product whether negative hour inputs are a real scenario
        before implementing, since the spec does not say so explicitly.
        """)
    @TableTest("""
        Scenario                            | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Weekly Pay?
        Regular week only                   | 40            | 0            | 0             | 20.00       | 800.00
        Regular week with overtime          | 45            | 0            | 0             | 20.00       | 950.00
        Regular week with Sunday hours      | 40            | 8            | 0             | 20.00       | 1120.00
        Regular week with holiday hours     | 40            | 0            | 8             | 20.00       | 1120.00
        Overtime, Sunday and holiday combined | 45          | 8            | 8             | 20.00       | 1590.00
        Zero rate pays nothing              | 40            | 8            | 8             | 0.00        | 0.00
        Negative adjustment floors at zero  | -50           | 0            | 0             | 20.00       | 0.00
        Negative adjustment partly offset still floors at zero | -50 | 8   | 0             | 20.00       | 0.00
        """)
    void calculatesWeeklyPay(BigDecimal weekdayHours, BigDecimal sundayHours, BigDecimal holidayHours,
                              BigDecimal hourlyRate, BigDecimal weeklyPay) {
        assertThat(calculator.calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate))
            .isEqualByComparingTo(weeklyPay);
    }

    @DisplayName("Hourly rate validation")
    @Description("""
        A negative hourly rate is invalid regardless of hours worked. Zero is
        the valid lower boundary (an unpaid rate is allowed; only a negative
        rate is rejected).
        """)
    @TableTest("""
        Scenario              | Hourly Rate | Throws?
        Just below zero       | -0.01       | java.lang.IllegalArgumentException
        Deeply negative       | -50.00      | java.lang.IllegalArgumentException
        Zero, valid boundary  | 0.00        |
        Positive rate         | 25.50       |
        """)
    void validatesHourlyRate(BigDecimal hourlyRate, Class<? extends Exception> throws_) {
        assertRateHandling(hourlyRate, throws_);
    }

    private void assertRateHandling(BigDecimal hourlyRate, Class<? extends Exception> expectedException) {
        Executable action = () -> calculator.calculateWeeklyPay(
            BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.ZERO, hourlyRate);
        if (expectedException != null) {
            assertThrows(expectedException, action);
        } else {
            assertDoesNotThrow(action);
        }
    }
}
