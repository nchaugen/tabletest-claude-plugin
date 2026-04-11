import org.junit.jupiter.api.Assertions;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParseDateTest {

    // --- Table 1: Valid inputs and null ---

    @TableTest("""
        Scenario        | Input      | Parsed Date?
        ISO format      | 2024-01-15 | 2024-01-15
        Slash format    | 15/01/2024 | 2024-01-15
        Short year      | 24-01-15   | 2024-01-15
        Null input      |            |
        """)
    void parsesDateFormats(String input, LocalDate expectedDate) {
        assertEquals(expectedDate, parseDate(input));
    }

    // --- Table 2: Inputs that throw ---

    @TableTest("""
        Scenario      | Input | Exception?
        Empty string  | ''    | java.lang.IllegalArgumentException
        """)
    void parseDateThrows(String input, Class<? extends Throwable> expectedException) {
        assertThrows(expectedException, () -> parseDate(input));
    }

    // --- Type converter for the input column in Table 1 ---
    // Not needed: input is String, no conversion required.

    // --- Type converter for the Parsed Date? column ---
    // The expectation column holds ISO-format strings (e.g. 2024-01-15), which
    // JUnit's built-in LocalDate converter handles automatically.

    // --- Method under test (placeholder — replace with your actual implementation) ---
    private LocalDate parseDate(String input) {
        if (input == null) {
            return null;
        }
        if (input.isEmpty()) {
            throw new IllegalArgumentException("Input must not be empty");
        }
        // ISO format: yyyy-MM-dd
        if (input.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return LocalDate.parse(input, DateTimeFormatter.ISO_LOCAL_DATE);
        }
        // Slash format: dd/MM/yyyy
        if (input.matches("\\d{2}/\\d{2}/\\d{4}")) {
            return LocalDate.parse(input, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        // Short year format: yy-MM-dd
        if (input.matches("\\d{2}-\\d{2}-\\d{2}")) {
            return LocalDate.parse(input, DateTimeFormatter.ofPattern("uu-MM-dd"));
        }
        throw new IllegalArgumentException("Unrecognised date format: " + input);
    }
}
