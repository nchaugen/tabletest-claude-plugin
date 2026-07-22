package payroll;

import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HourlyRateTest {

    @TableTest("""
        Scenario                     | Rate  | Value?
        Zero rate at the boundary    | 0.00  | 0.00
        Typical positive rate        | 15.00 | 15.00
        """)
    void acceptsNonNegativeRates(double rate, double value) {
        assertEquals(value, new HourlyRate(rate).value());
    }

    @TableTest("""
        Scenario                       | Rate   | Throws?
        Just below the zero boundary   | -0.01  | java.lang.IllegalArgumentException
        Clearly negative rate          | -25.00 | java.lang.IllegalArgumentException
        """)
    void rejectsNegativeRates(double rate, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> new HourlyRate(rate));
    }
}
