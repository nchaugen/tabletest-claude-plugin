import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.assertThrows;

import org.tabletest.junit.TableTest;

public class WeeklyPayCalculatorTest {

      /**
       * Base weekday pay: hours up to and including 40 at the base rate.
       */
      @TableTest("""
        Scenario                   | Weekday hrs| Sunday hrs| Holiday hrs| Rate $/hr| Total pay?
        No work all days           |            0|           0|            0|        0.0|           0.00
         40 hours at base rate      |           40|           0|            0|       25.0|          1000.00
         Under 40 hours             |           38|           0|            0|       25.0|           950.00
          """)
    void base_pay_at_base_rate(int weekdayHours, int sundayHours,
                               int holidayHours, double hourlyRate,
                               double expectedPay) {
        var calc = new WeeklyPayCalculator();
        assertEquals(expectedPay, calc.calculatePay(weekdayHours, sundayHours,
                                                            holidayHours, hourlyRate), 0.001);
      }

      /**
       * Overtime applies to weekday hours strictly above 40 at 1.5× base rate.
       */
      @TableTest("""
        Scenario                             | Weekday hrs| Sunday hrs| Holiday hrs| Rate $/hr| Total pay?
         Exactly 40: no overtime yet          |           40|           0|            0|       25.0|          1000.00
         One hour over                        |           41|           0|            0|       25.0|          1037.50
         + Sunday double time with OT (A)     |           45|           8|            0|       25.0|          1525.00
          """)
    void overtime_after_40_weekday_hours(int weekdayHours, int sundayHours,
                                         int holidayHours, double hourlyRate,
                                         double expectedPay) {
        var calc = new WeeklyPayCalculator();
        assertEquals(expectedPay, calc.calculatePay(weekdayHours, sundayHours,
                                                            holidayHours, hourlyRate), 0.001);
      }

      /**
       * Sunday hours paid at 2× the base rate (double time).
       */
      @TableTest("""
         Scenario          | Weekday hrs| Sunday hrs| Holiday hrs| Rate $/hr| Total pay?
         Zero Sunday hrs    |            0|           0|            0|       30.0|           0.00
          10 hours double-time   |        20|          10|            0|       30.0|           900.00
          """)
    void sunday_double_time(int weekdayHours, int sundayHours,
                            int holidayHours, double hourlyRate,
                            double expectedPay) {
        var calc = new WeeklyPayCalculator();
        assertEquals(expectedPay, calc.calculatePay(weekdayHours, sundayHours,
                                                            holidayHours, hourlyRate), 0.001);
      }

      /**
       * Holiday hours paid at 2× the base rate (double time).
       */
      @TableTest("""
         Scenario              | Weekday hrs| Sunday hrs| Holiday hrs| Rate $/hr| Total pay?
         Zero holiday hrs       |            0|           0|            0|       30.0|           0.00
          8 hours double-time   |        20|           0|            8|       30.0|           960.00
          """)
    void holiday_double_time(int weekdayHours, int sundayHours,
                             int holidayHours, double hourlyRate,
                             double expectedPay) {
        var calc = new WeeklyPayCalculator();
        assertEquals(expectedPay, calc.calculatePay(weekdayHours, sundayHours,
                                                            holidayHours, hourlyRate), 0.001);
      }

      /**
       * Negative hourly rate is invalid and must be rejected.
       */
     @Test
    void negative_hourly_rate_rejected() {
        assertThrows(IllegalArgumentException.class,
              () -> new WeeklyPayCalculator().calculatePay(40, 0, 0, -10.0));
      }

      /**
       * Combined test: all rules applied together in one scenario.
       */
      @TableTest("""
         Scenario                      | Weekday hrs| Sunday hrs| Holiday hrs| Rate $/hr| Total pay?
         Full week: base+OT+Sun+hols   |           50|           8|            4|       30.00|        2820.00
          """)
    void combined_multiple_rules(int weekdayHours, int sundayHours,
                                 int holidayHours, double hourlyRate,
                                 double expectedPay) {
        var calc = new WeeklyPayCalculator();
        assertEquals(expectedPay, calc.calculatePay(weekdayHours, sundayHours,
                                                            holidayHours, hourlyRate), 0.001);
      }
}
