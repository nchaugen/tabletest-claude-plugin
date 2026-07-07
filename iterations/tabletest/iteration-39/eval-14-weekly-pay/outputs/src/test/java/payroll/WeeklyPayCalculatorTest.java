package payroll;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WeeklyPayCalculatorTest {

    @Description("""
        Overtime threshold is fixed at 40 hours/week; hours beyond it are paid at 1.5x.
        Negative hours are treated as a pay adjustment/correction (e.g. a prior overpayment
        being clawed back) since the spec requires total pay to be able to go below zero
        before being floored - assumption, as the spec does not describe negative hours.
        """)
    @TableTest("""
        Scenario                                | Weekday Hours | Hourly Rate | Weekday Pay?
        No hours worked                         | 0             | 20.00       | 0.00
        Hours below the overtime threshold      | 30            | 20.00       | 600.00
        At the overtime threshold               | 40            | 20.00       | 800.00
        Just past the overtime threshold        | 41            | 20.00       | 830.00
        Well past the overtime threshold        | 50            | 20.00       | 1100.00
        Overtime with a different hourly rate   | 45            | 15.50       | 736.25
        Negative hours treated as an adjustment | -5            | 20.00       | -100.00
        """)
    void calculatesWeekdayPay(double weekdayHours, double hourlyRate, double weekdayPay) {
        assertEquals(weekdayPay, WeeklyPayCalculator.calculateWeekdayPay(weekdayHours, hourlyRate), 0.001);
    }

    @Description("""
        Sunday hours and holiday hours are both always paid at double time (2x base rate),
        regardless of how many hours were worked - assumption: they are separate premium
        categories that do not interact with the 40-hour weekday overtime threshold.
        Negative hours are treated as a pay adjustment, as in the weekday pay table above.
        """)
    @TableTest("""
        Scenario                                   | Sunday Hours | Holiday Hours | Hourly Rate | Premium Pay?
        No premium hours worked                    | 0            | 0             | 20.00       | 0.00
        Sunday hours only                          | 10           | 0             | 20.00       | 400.00
        Holiday hours only                         | 0            | 8             | 20.00       | 320.00
        Sunday and holiday hours combined          | 5            | 5             | 20.00       | 400.00
        Premium pay with a different hourly rate   | 6            | 0             | 12.50       | 150.00
        Negative premium hours treated as an adjustment | -3      | 0             | 20.00       | -120.00
        """)
    void calculatesPremiumPay(double sundayHours, double holidayHours, double hourlyRate, double premiumPay) {
        assertEquals(premiumPay, WeeklyPayCalculator.calculatePremiumPay(sundayHours, holidayHours, hourlyRate), 0.001);
    }

    @Description("""
        Weekly pay is the sum of weekday pay and premium pay. If that sum would be
        negative, total pay is floored at zero, per the "total pay cannot go below
        zero" rule.
        """)
    @TableTest("""
        Scenario                                       | Weekday Pay | Premium Pay | Total Pay?
        Positive weekday and premium pay                | 800.00      | 400.00      | 1200.00
        Only weekday pay                                | 800.00      | 0.00        | 800.00
        Only premium pay                                | 0.00        | 320.00      | 320.00
        Negative weekday pay offset by premium pay       | -200.00     | 400.00      | 200.00
        Combined pay lands exactly on zero               | -200.00     | 200.00      | 0.00
        Combined pay would go negative, floored at zero  | -500.00     | 100.00      | 0.00
        """)
    void combinesWeeklyPayAndFloorsAtZero(double weekdayPay, double premiumPay, double totalPay) {
        assertEquals(totalPay, WeeklyPayCalculator.combineWeeklyPay(weekdayPay, premiumPay), 0.001);
    }

    @TableTest("""
        Scenario                    | Hourly Rate | Throws?
        Negative hourly rate        | -0.01       | java.lang.IllegalArgumentException
        Large negative hourly rate  | -50.00      | java.lang.IllegalArgumentException
        """)
    void rejectsNegativeHourlyRate(double hourlyRate, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> WeeklyPayCalculator.calculateWeeklyPay(10, 0, 0, hourlyRate));
    }

    @Description("""
        End-to-end scenarios combining the rules from the tables above: weekday pay
        (with overtime), Sunday pay, and holiday pay all contribute to one week's total.
        """)
    @TableTest("""
        Scenario                                          | Weekday Hours | Sunday Hours | Holiday Hours | Hourly Rate | Total Pay?
        Standard 40-hour week, no premium hours            | 40            | 0            | 0             | 20.00       | 800.00
        Week with weekday overtime                         | 45            | 0            | 0             | 20.00       | 950.00
        Week with Sunday hours                             | 35            | 8            | 0             | 20.00       | 1020.00
        Week with holiday hours                            | 32            | 0            | 8             | 20.00       | 960.00
        Week with overtime, Sunday, and holiday hours       | 42            | 6            | 4             | 15.00       | 945.00
        Zero hourly rate is allowed                        | 40            | 5            | 0             | 0.00        | 0.00
        """)
    void calculatesWeeklyPay(double weekdayHours, double sundayHours, double holidayHours, double hourlyRate, double totalPay) {
        assertEquals(totalPay, WeeklyPayCalculator.calculateWeeklyPay(weekdayHours, sundayHours, holidayHours, hourlyRate), 0.001);
    }
}
