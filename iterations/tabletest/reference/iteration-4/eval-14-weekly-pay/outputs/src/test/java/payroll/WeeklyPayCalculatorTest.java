package payroll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.function.Executable;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WeeklyPayCalculatorTest {

    @DisplayName("Pays each band of hours at its own rate")
    @Description("""
        One pay week, Monday to Sunday, in whole pounds. The calculator receives the hours the
        requirement names — weekday, Sunday and holiday — and applies the overtime threshold itself;
        where weekday hours stop being regular is its business, not the caller's. A blank cell means
        no hours of that kind were worked, and reaches the calculator as null. The hourly rate is 10
        so that one hour of a band prices the band directly.
        """)
    @TableTest("""
        Scenario                    | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Weekly Pay?
        No hours worked             |               |              |               | 10          | 0
        One weekday hour            | 1             |              |               | 10          | 10
        At the overtime threshold   | 40            |              |               | 10          | 400
        One hour past the threshold | 41            |              |               | 10          | 415
        One Sunday hour             |               | 1            |               | 10          | 20
        One holiday hour            |               |              | 1             | 10          | 20
        A week touching every band  | 41            | 8            | 8             | 10          | 735
        Any hours at a zero rate    | {1, 41, 100}  |              |               | 0           | 0
        """)
    void paysEachBandOfHoursAtItsOwnRate(
            Integer weekdayHours,
            Integer sundayHours,
            Integer holidayHours,
            int hourlyRate,
            int weeklyPay) {
        int pay = WeeklyPayCalculator.calculateWeeklyPay(
                weekdayHours, sundayHours, holidayHours, hourlyRate);

        assertEquals(weeklyPay, pay);
    }

    @DisplayName("Never pays less than zero, however large the correction")
    @Description("""
        The prompt does not say what a negative hour count means. This table reads it as a
        timesheet correction rather than as invalid input, because that is the only reading under
        which the prompt's "total pay cannot go below zero" rule can be exercised at all: reject
        every negative and nothing can ever drive a week below zero. Holiday hours are held at none,
        since the correction is what this table is about.
        """)
    @TableTest("""
        Scenario                               | Weekday Hours | Sunday Hours | Hourly Rate | Weekly Pay?
        Correction smaller than the week's pay | 40            | -10          | 10          | 200
        Correction cancelling the week's pay   | 40            | -20          | 10          | 0
        Correction larger than the week's pay  | 40            | -21          | 10          | 0
        """)
    void neverPaysLessThanZero(
            Integer weekdayHours,
            Integer sundayHours,
            int hourlyRate,
            int weeklyPay) {
        int pay = WeeklyPayCalculator.calculateWeeklyPay(weekdayHours, sundayHours, null, hourlyRate);

        assertEquals(weeklyPay, pay);
    }

    @DisplayName("Rejects a negative hourly rate")
    @Description("""
        Hours are held at forty weekday hours and none of anything else, because this table is about
        the rate boundary rather than about how the bands combine.
        """)
    @TableTest("""
        Scenario                | Hourly Rate | Throws?
        A zero rate             | 0           |
        A rate just below zero  | -1          | java.lang.IllegalArgumentException
        """)
    void rejectsANegativeHourlyRate(int hourlyRate, Class<? extends Throwable> expectedException) {
        Class<? extends Throwable> thrown =
                thrownBy(() -> WeeklyPayCalculator.calculateWeeklyPay(40, null, null, hourlyRate));

        assertEquals(expectedException, thrown);
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
