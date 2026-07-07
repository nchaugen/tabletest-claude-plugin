import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PayCalculatorTest {

    @Description("""
        Overtime applies only to weekday hours beyond 40 per week. Sunday and holiday
        hours are always paid at double time, independent of weekday hours worked.
        """)
    @TableTest("""
        Scenario                            | Weekday Hours | Sunday Hours | Holiday Hours | Rate  | Total Pay?
        Under overtime threshold             | 30            | 0            | 0             | 20.00 | 600.00
        At overtime threshold                | 40            | 0            | 0             | 20.00 | 800.00
        Just over overtime threshold         | 41            | 0            | 0             | 20.00 | 830.00
        Well over overtime threshold         | 50            | 0            | 0             | 20.00 | 1100.00
        Sunday hours only                    | 0             | 10           | 0             | 20.00 | 400.00
        Holiday hours only                   | 0             | 0            | 8             | 20.00 | 320.00
        Weekday overtime, Sunday and holiday | 45            | 5            | 8             | 15.00 | 1102.50
        Zero hours worked                    | 0             | 0            | 0             | 20.00 | 0.00
        """)
    void shouldCalculateWeeklyPay(double weekdayHours, double sundayHours, double holidayHours, double rate, double totalPay) {
        assertEquals(totalPay, PayCalculator.calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, rate), 0.001);
    }

    @Description("""
        Isolates hourly rate validation from pay calculation. Weekday hours are fixed at
        40 and Sunday/holiday hours at 0 for every row.
        """)
    @TableTest("""
        Scenario             | Rate    | Throws?
        Zero rate is allowed | 0.00    |
        Just below zero      | -0.01   | java.lang.IllegalArgumentException
        Clearly negative     | -50.00  | java.lang.IllegalArgumentException
        """)
    void shouldRejectNegativeRate(double rate, Class<? extends Exception> throws_) {
        if (throws_ == null) {
            assertDoesNotThrow(() -> PayCalculator.calculateWeeklyPay(40, 0, 0, rate));
        } else {
            assertThrows(throws_, () -> PayCalculator.calculateWeeklyPay(40, 0, 0, rate));
        }
    }

    @Description("""
        Assumes weekday/Sunday hours may be negative to represent pay corrections, while
        the hourly rate itself is rejected when negative (see shouldRejectNegativeRate).
        Open question: confirm with the business that negative hours are a valid
        correction input rather than something that should be rejected outright.
        """)
    @TableTest("""
        Scenario                              | Weekday Hours | Sunday Hours | Rate  | Total Pay?
        Correction drives pay below zero       | -50           | 0            | 20.00 | 0.00
        Correction exactly offsets earned pay  | -20           | 10           | 10.00 | 0.00
        Correction reduces but stays positive  | -10           | 20           | 20.00 | 600.00
        """)
    void shouldNotAllowTotalPayBelowZero(double weekdayHours, double sundayHours, double rate, double totalPay) {
        assertEquals(totalPay, PayCalculator.calculateWeeklyPay(weekdayHours, sundayHours, 0, rate), 0.001);
    }
}
