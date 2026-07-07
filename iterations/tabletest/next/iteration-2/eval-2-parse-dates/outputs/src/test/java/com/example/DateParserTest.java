package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DateParserTest {

    private final DateParser parser = new DateParser();

    @Description("""
        Short year format (yy-MM-dd) is assumed to expand into the 2000s,
        e.g. 24-01-15 -> 2024-01-15. Open question: how should years outside
        the 2000s be handled?
        """)
    @TableTest("""
        Scenario          | Input      | Parsed?
        ISO format        | 2024-01-15 | 2024-01-15
        Slash format      | 15/01/2024 | 2024-01-15
        Short year format | 24-01-15   | 2024-01-15
        Null input        |            |
        """)
    void parsesSupportedDateFormats(String input, LocalDate parsed) {
        assertEquals(parsed, parser.parseDate(input));
    }

    @TableTest("""
        Scenario     | Input | Throws?
        Empty string | ''    | java.lang.IllegalArgumentException
        """)
    void rejectsEmptyInput(String input, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> parser.parseDate(input));
    }
}
