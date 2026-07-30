package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DateParserTest {

    private final DateParser parser = new DateParser();

    @DisplayName("Parses date strings in ISO, slash, and short-year formats")
    @Description("""
        Slash format is day/month/year (DD/MM/YYYY); in the given example the first
        component (15) exceeds 12, confirming day-first order. Short-year format
        prefixes a two-digit year interpreted as 2000 + YY. Null input passes
        through as null rather than being rejected.
        """)
    @TableTest("""
        Scenario           | Input      | Parsed?
        ISO format         | 2024-01-15 | 2024-01-15
        Slash format        | 15/01/2024 | 2024-01-15
        Short year format   | 24-01-15   | 2024-01-15
        Null input          |            |
        """)
    void parsesSupportedDateFormats(String input, LocalDate parsed) {
        assertEquals(parsed, parser.parseDate(input));
    }

    @DisplayName("Rejects empty date input")
    @TableTest("""
        Scenario     | Input | Throws?
        Empty string | ''    | java.lang.IllegalArgumentException
        """)
    void rejectsEmptyInput(String input, Class<? extends Throwable> throws_) {
        assertThrows(throws_, () -> parser.parseDate(input));
    }
}
