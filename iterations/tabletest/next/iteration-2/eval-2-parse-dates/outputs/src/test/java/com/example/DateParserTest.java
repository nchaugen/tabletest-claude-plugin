package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DateParserTest {

    private final DateParser parser = new DateParser();

    @Description("""
        Slash-formatted input is assumed to be day/month/year.
        Short-year input is assumed to be yy-MM-dd, with the year
        taken as 20yy.
        """)
    @TableTest("""
        Scenario           | Input       | Parsed?    | Throws?
        ISO format         | 2024-01-15  | 2024-01-15 |
        Slash format       | 15/01/2024  | 2024-01-15 |
        Short year         | 24-01-15    | 2024-01-15 |
        Null input         |             |            |
        Empty string       | ''          |            | java.lang.IllegalArgumentException
        """)
    void parsesDate(String input, LocalDate parsed, Class<? extends Exception> throws_) {
        assertParsesTo(input, parsed, throws_);
    }

    private void assertParsesTo(String input, LocalDate expected, Class<? extends Exception> throws_) {
        if (throws_ != null) {
            assertThrows(throws_, () -> parser.parseDate(input));
        } else {
            assertEquals(expected, parser.parseDate(input));
        }
    }
}
