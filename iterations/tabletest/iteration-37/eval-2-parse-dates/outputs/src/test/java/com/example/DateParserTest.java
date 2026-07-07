package com.example;

import org.junit.jupiter.api.Test;
import org.tabletest.junit.TableTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DateParserTest {

    private final DateParser parser = new DateParser();

    @TableTest("""
        Scenario     | Input      | Parsed Date?
        ISO format   | 2024-01-15 | 2024-01-15
        Slash format | 15/01/2024 | 2024-01-15
        Short year   | 24-01-15   | 2024-01-15
        Null input   |            |
        """)
    void parsesSupportedDateFormats(String input, LocalDate parsedDate) {
        assertEquals(parsedDate, parser.parseDate(input));
    }

    @Test
    void rejectsEmptyInput() {
        assertThrows(IllegalArgumentException.class, () -> parser.parseDate(""));
    }
}
