It looks like the write permission isn't granted. Here are the JUnit 5 tests I'd write for `WeeklyPayCalculator`:

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WeeklyPayCalculatorTest {

    // --- Base weekday hours (no overtime) ---

    @Test
    void exactlyFortyWeekdayHoursPaidAtBaseRate() {
        double pay = WeeklyPayCalculator.calculate(40, 0, 0, 20.0);
        assertEquals(800.0, pay, 0.001);
    }

    @Test
    void fewerThanFortyWeekdayHoursPaidAtBaseRate() {
        double pay = WeeklyPayCalculator.calculate(32, 0, 0, 10.0);
        assertEquals(320.0, pay, 0.001);
    }

    @Test
    void zeroHoursYieldsZeroPay() {
        double pay = WeeklyPayCalculator.calculate(0, 0, 0, 15.0);
        assertEquals(0.0, pay, 0.001);
    }

    // --- Weekday overtime (beyond 40 hours) ---

    @Test
    void overtimeHoursPaidAtOneAndAHalfTimesBaseRate() {
        // 40 regular + 8 overtime: (40 * 10) + (8 * 15) = 520
        double pay = WeeklyPayCalculator.calculate(48, 0, 0, 10.0);
        assertEquals(520.0, pay, 0.001);
    }

    @Test
    void singleOvertimeHour() {
        // (40 * 20) + (1 * 30) = 830
        double pay = WeeklyPayCalculator.calculate(41, 0, 0, 20.0);
        assertEquals(830.0, pay, 0.001);
    }

    // --- Sunday hours (always double time) ---

    @Test
    void sundayHoursPaidAtDoubleRate() {
        double pay = WeeklyPayCalculator.calculate(0, 8, 0, 10.0);
        assertEquals(160.0, pay, 0.001);
    }

    @Test
    void sundayHoursDoNotCountTowardOvertimeThreshold() {
        // 40 weekday at base + 8 Sunday at 2x: (40*10) + (8*20) = 560
        double pay = WeeklyPayCalculator.calculate(40, 8, 0, 10.0);
        assertEquals(560.0, pay, 0.001);
    }

    // --- Holiday hours (always double time) ---

    @Test
    void holidayHoursPaidAtDoubleRate() {
        double pay = WeeklyPayCalculator.calculate(0, 0, 8, 10.0);
        assertEquals(160.0, pay, 0.001);
    }

    @Test
    void holidayHoursDoNotCountTowardOvertimeThreshold() {
        // 40 weekday at base + 8 holiday at 2x: (40*10) + (8*20) = 560
        double pay = WeeklyPayCalculator.calculate(40, 0, 8, 10.0);
        assertEquals(560.0, pay, 0.001);
    }

    // --- Combined all three types ---

    @Test
    void combinationOfAllHourTypes() {
        // 45 weekday (40 reg + 5 OT) + 4 Sunday + 3 holiday, rate=10
        // (40*10) + (5*15) + (4*20) + (3*20) = 400+75+80+60 = 615
        double pay = WeeklyPayCalculator.calculate(45, 4, 3, 10.0);
        assertEquals(615.0, pay, 0.001);
    }

    @Test
    void allThreeTypesNoOvertime() {
        // (20*10) + (4*20) + (4*20) = 200+80+80 = 360
        double pay = WeeklyPayCalculator.calculate(20, 4, 4, 10.0);
        assertEquals(360.0, pay, 0.001);
    }

    // --- Pay floor ---

    @Test
    void totalPayIsNeverNegative() {
        double pay = WeeklyPayCalculator.calculate(0, 0, 0, 0.0);
        assertTrue(pay >= 0.0);
    }

    // --- Invalid inputs ---

    @Test
    void negativeHourlyRateIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
            WeeklyPayCalculator.calculate(40, 0, 0, -10.0)
        );
    }

    @Test
    void zeroRateWithHoursYieldsZeroPay() {
        double pay = WeeklyPayCalculator.calculate(40, 8, 4, 0.0);
        assertEquals(0.0, pay, 0.001);
    }

    // --- Fractional values ---

    @Test
    void fractionalOvertimeHours() {
        // 40.5 weekday: (40*10) + (0.5*15) = 407.5
        double pay = WeeklyPayCalculator.calculate(40.5, 0, 0, 10.0);
        assertEquals(407.5, pay, 0.001);
    }

    @Test
    void fractionalRate() {
        double pay = WeeklyPayCalculator.calculate(10, 0, 0, 12.5);
        assertEquals(125.0, pay, 0.001);
    }
}
```

The tests assume a signature of `WeeklyPayCalculator.calculate(weekdayHours, sundayHours, holidayHours, hourlyRate)`. Key cases covered:

| Group | Tests |
|---|---|
| Weekday regular | 0 hrs, <40 hrs, exactly 40 hrs |
| Weekday overtime | 1 OT hour, 8 OT hours |
| Sunday double-time | standalone, combined with full weekday week |
| Holiday double-time | standalone, combined with full weekday week |
| Combined | all three types with/without OT |
| Pay floor | zero-pay result is non-negative |
| Invalid rate | negative rate throws `IllegalArgumentException` |
| Fractional | partial overtime hour, fractional rate |