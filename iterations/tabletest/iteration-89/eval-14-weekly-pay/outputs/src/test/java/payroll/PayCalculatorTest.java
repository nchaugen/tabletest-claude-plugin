package payroll;

import org.junit.jupiter.api.function.Executable;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PayCalculatorTest {

    private final PayCalculator calculator = new PayCalculator();

    @TableTest("""
        Scenario                    | Weekday Hours | Hourly Rate | Weekly Pay?
        Day before the overtime cap | 39            | 20          | 780
        At the overtime cap         | 40            | 20          | 800
        First hour of overtime      | 41            | 20          | 830
        """)
    void paysWeekdayHoursAtBaseRateUpToFortyAndOvertimeBeyondIt(double weekdayHours, double hourlyRate, double weeklyPay) {
        assertEquals(weeklyPay, calculator.calculateWeeklyPay(weekdayHours, 0, 0, hourlyRate));
    }

    @TableTest("""
        Scenario                                  | Sunday Hours | Holiday Hours | Hourly Rate | Weekly Pay?
        Sunday hours only                         | 10           | 0             | 20          | 400
        Holiday hours only                        | 0            | 8             | 20          | 320
        Sunday and holiday hours in the same week | 5            | 3             | 20          | 320
        Sunday hours past forty stay double time  | 42           | 0             | 20          | 1680
        """)
    void paysSundayAndHolidayHoursAtDoubleTime(double sundayHours, double holidayHours, double hourlyRate, double weeklyPay) {
        assertEquals(weeklyPay, calculator.calculateWeeklyPay(0, sundayHours, holidayHours, hourlyRate));
    }

    @Description("""
        The 40-hour overtime threshold applies only to weekday hours, not to the sum of
        weekday, Sunday and holiday hours. Row 2 exercises this directly: 35 weekday hours
        plus 10 Sunday hours total 45, past 40, yet the weekday portion stays at the base
        rate because weekday hours alone are under the threshold.
        """)
    @TableTest("""
        Scenario                                            | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Weekly Pay?
        Weekday overtime combined with Sunday hours         | 45            | 10           | 0             | 20          | 1350
        Weekday hours under the threshold despite the total | 35            | 10           | 0             | 20          | 1100
        All three hour categories in the same week          | 42            | 5            | 3             | 20          | 1180
        """)
    void scopesTheOvertimeThresholdToWeekdayHoursAlone(double weekdayHours, double sundayHours, double holidayHours, double hourlyRate, double weeklyPay) {
        assertEquals(weeklyPay, calculator.calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate));
    }

    @Description("""
        Hours may be negative to represent corrections or adjustments from a prior period.
        These rows hold Sunday hours steady while the weekday adjustment moves the raw total
        across zero, showing the floor engages below zero and nowhere else.
        """)
    @TableTest("""
        Scenario                                      | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Weekly Pay?
        Negative adjustment drives raw pay below zero | -50           | 0            | 0             | 20          | 0
        Raw pay lands exactly at zero                 | -10           | 5            | 0             | 20          | 0
        Raw pay stays positive, no flooring applied   | -5            | 10           | 0             | 20          | 300
        """)
    void floorsWeeklyPayAtZero(double weekdayHours, double sundayHours, double holidayHours, double hourlyRate, double weeklyPay) {
        assertEquals(weeklyPay, calculator.calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate));
    }

    @TableTest("""
        Scenario               | Hourly Rate | Throws?
        At the minimum rate    | 0           |
        Just below the minimum | -0.01       | java.lang.IllegalArgumentException
        """)
    void rejectsANegativeHourlyRate(double hourlyRate, Class<? extends Throwable> throws_) {
        assertEquals(throws_, thrownBy(() -> calculator.calculateWeeklyPay(0, 0, 0, hourlyRate)));
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
