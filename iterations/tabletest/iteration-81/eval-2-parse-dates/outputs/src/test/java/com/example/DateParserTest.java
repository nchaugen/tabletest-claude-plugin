package com.example;

import org.tabletest.junit.TableTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateParserTest {

    private final DateParser parser = new DateParser();

    @TableTest("""
        Scenario     | Input      | Parsed?
        ISO format   | 2024-01-15 | 2024-01-15
        Slash format | 15/01/2024 | 2024-01-15
        Short year   | 24-01-15   | 2024-01-15
        """)
    void parsesSupportedDateFormats(String input, LocalDate parsed) {
        assertEquals(parsed, parser.parseDate(input));
    }

    @TableTest("""
        Scenario     | Input | Parsed? | Throws?
        Null input   |       |         |
        Empty string | ''    |         | java.lang.IllegalArgumentException
        """)
    void handlesAbsentInput(String input, LocalDate parsed, Class<? extends Throwable> throws_) {
        ParseResult result = tryParse(input);
        assertEquals(parsed, result.value());
        assertEquals(throws_, result.thrown());
    }

    private ParseResult tryParse(String input) {
        try {
            return new ParseResult(parser.parseDate(input), null);
        } catch (Throwable thrown) {
            return new ParseResult(null, thrown.getClass());
        }
    }

    private record ParseResult(LocalDate value, Class<? extends Throwable> thrown) {
    }
}
