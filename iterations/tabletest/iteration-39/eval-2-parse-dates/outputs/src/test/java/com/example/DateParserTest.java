package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DateParserTest {

    private final DateParser parser = new DateParser();

    @Description("""
        Recognizes ISO (yyyy-MM-dd), slash (dd/MM/yyyy), and short-year (yy-MM-dd) formats.
        Assumption: slash format is day/month/year. Assumption: two-digit years are
        interpreted as 20xx (e.g. 24 -> 2024).
        """)
    @TableTest("""
        Scenario                | Input      | Parsed?
        ISO format              | 2024-01-15 | 2024-01-15
        Slash format (dd/MM/yy) | 15/01/2024 | 2024-01-15
        Short year (yy-MM-dd)   | 24-01-15   | 2024-01-15
        Null input              |            |
        """)
    void parsesRecognizedFormats(String input, LocalDate parsed) {
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
