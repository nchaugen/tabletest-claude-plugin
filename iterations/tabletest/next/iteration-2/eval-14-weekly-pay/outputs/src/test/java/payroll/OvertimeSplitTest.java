package payroll;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OvertimeSplitTest {

    @Description("""
        The standard workweek is 40 hours. Weekday hours up to this threshold
        are regular hours; hours beyond it are overtime.
        """)
    @TableTest("""
        Scenario                  | Weekday Hours | Regular Hours? | Overtime Hours?
        Zero hours worked         | 0             | 0              | 0
        Well under standard week  | 20            | 20             | 0
        At the 40-hour threshold  | 40            | 40             | 0
        Just over the threshold   | 41            | 40             | 1
        Well beyond the threshold | 55            | 40             | 15
        """)
    void splitsWeekdayHoursIntoRegularAndOvertime(double weekdayHours, double regularHours, double overtimeHours) {
        OvertimeSplit split = OvertimeSplit.from(weekdayHours);

        assertEquals(regularHours, split.regularHours());
        assertEquals(overtimeHours, split.overtimeHours());
    }
}
