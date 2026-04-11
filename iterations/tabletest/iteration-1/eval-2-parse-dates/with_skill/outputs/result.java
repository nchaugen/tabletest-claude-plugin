import org.junit.jupiter.api.Test;
import org.tabletest.junit.TableTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParseDateTest {

    @TableTest("""
            Scenario           | Input      | Parsed Date?
            ISO format         | 2024-01-15 | 2024-01-15
            Slash format       | 15/01/2024 | 2024-01-15
            Short year         | 24-01-15   | 2024-01-15
            Null input         |            |
            """)
    void parsesDateFromVariousFormats(String input, LocalDate expectedDate) {
        assertEquals(expectedDate, parseDate(input));
    }

    @TableTest("""
            Scenario      | Input | Exception?
            Empty string  | ''    | java.lang.IllegalArgumentException
            """)
    void throwsOnInvalidInput(String input, Class<? extends Throwable> expectedException) {
        assertThrows(expectedException, () -> parseDate(input));
    }
}
