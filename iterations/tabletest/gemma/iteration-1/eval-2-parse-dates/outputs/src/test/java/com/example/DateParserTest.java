package com.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tabletest.junit.TableTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DateParserTest {

    private final DateParser parser = new DateParser();

    @DisplayName("Parse date from various supported formats")
    @TableTest("""
        Scenario     | Input       | Expected Date?
        ISO format   | 2024-01-15  | 2024-01-15
        Slash format | 15/01/2024  | 2024-01-15
        Short year   | 24-01-15    | 2024-01-15
        Null input   |             |
        """)
    void shouldParseDates(String input, LocalDate expectedDate) {
        assertEquals(expectedDate, parser.parseDate(input));
    }

    @DisplayName("Reject empty date strings")
    @TableTest("""
        Scenario     | Input | Throws?
        Empty string | ''    | IllegalArgumentException
        """)
    void shouldRejectEmptyInput(String input, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> parser.parseDate(input));
    }
}
