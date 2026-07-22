package payroll;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PayCalculatorTest {

    @Description("""
        Regular hours are paid at the base rate, overtime at 1.5x, and Sunday
        and holiday hours always at 2x, regardless of how many regular or
        overtime hours were also worked that week.

        Hours may be negative to represent corrections from a prior pay
        period; this was not specified explicitly, so it is assumed here.
        Whatever the source of a negative figure, total pay must never drop
        below zero.
        """)
    @TableTest("""
        Scenario                                              | Regular Hours | Overtime Hours | Sunday Hours | Holiday Hours | Rate  | Weekly Pay?
        Regular hours only                                    | 40            | 0              | 0            | 0             | 15.00 | 600.00
        Overtime paid at time-and-a-half                      | 40            | 10             | 0            | 0             | 15.00 | 825.00
        Sunday hours paid at double time                      | 0             | 0              | 8            | 0             | 15.00 | 240.00
        Holiday hours paid at double time                     | 0             | 0              | 0            | 8             | 15.00 | 240.00
        All hour types combined                               | 32            | 8              | 6            | 8             | 20.00 | 1440.00
        Zero hours worked                                     | 0             | 0              | 0            | 0             | 15.00 | 0.00
        Negative regular hours floor at zero                  | -10           | 0              | 0            | 0             | 15.00 | 0.00
        Large negative adjustment floors at zero              | -50           | -10            | 0            | 0             | 20.00 | 0.00
        Negative hours offset by premium hours stay positive  | -5            | 0              | 10           | 0             | 15.00 | 225.00
        """)
    void calculatesPay(double regularHours, double overtimeHours, double sundayHours, double holidayHours,
                        double rate, double weeklyPay) {
        assertEquals(weeklyPay, PayCalculator.calculatePay(regularHours, overtimeHours, sundayHours, holidayHours, rate));
    }

    @Description("""
        End-to-end check that weekday hours are split into regular/overtime
        before the per-category rates are applied, confirming the pieces
        tested separately in OvertimeSplitTest and calculatesPay compose
        correctly. Detailed threshold and floor edge cases are covered there,
        not repeated here.
        """)
    @TableTest("""
        Scenario                                       | Weekday Hours | Sunday Hours | Holiday Hours | Rate  | Weekly Pay?
        Standard week, no premium hours                | 40            | 0            | 0             | 15.00 | 600.00
        Week with overtime and Sunday work              | 45            | 6            | 0             | 15.00 | 892.50
        Week with holiday hours only                    | 0             | 0            | 8             | 20.00 | 320.00
        Full combination of overtime, Sunday, holiday   | 48            | 8            | 8             | 18.00 | 1512.00
        """)
    void calculatesWeeklyPay(double weekdayHours, double sundayHours, double holidayHours, double rate, double weeklyPay) {
        assertEquals(weeklyPay, PayCalculator.calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, rate));
    }
}
