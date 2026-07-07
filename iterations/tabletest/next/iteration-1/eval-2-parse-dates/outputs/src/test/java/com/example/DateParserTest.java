package com.example;

import org.junit.jupiter.api.Test;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DateParserTest {

    private final DateParser parser = new DateParser();

    @Description("""
        Short year values are assumed to belong to the 2000s (24 -> 2024).
        Open: behaviour for years outside the 2000-2099 range is undecided.
        """)
    @TableTest("""
        Scenario     | Input      | Result?
        ISO format   | 2024-01-15 | 2024-01-15
        Slash format | 15/01/2024 | 2024-01-15
        Short year   | 24-01-15   | 2024-01-15
        Null input   |            |
        """)
    void parsesDate(String input, LocalDate result) {
        assertEquals(result, parser.parseDate(input));
    }

    @Test
    void rejectsEmptyString() {
        assertThrows(IllegalArgumentException.class, () -> parser.parseDate(""));
    }
}
