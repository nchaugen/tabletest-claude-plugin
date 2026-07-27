package com.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.function.Executable;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateParserTest {

    private final DateParser parser = new DateParser();

    @DisplayName("Parses a date string in any supported format")
    @Description("""
        Short year format assumes a 2000s pivot (24 -> 2024). Slash format is
        day/month/year.
        """)
    @TableTest("""
        Scenario           | Input      | Parsed?    | Throws?
        ISO format         | 2024-01-15 | 2024-01-15 |
        Slash format       | 15/01/2024 | 2024-01-15 |
        Short year format  | 24-01-15   | 2024-01-15 |
        Null input         |            |            |
        Empty string       | ''         |            | java.lang.IllegalArgumentException
        """)
    void parsesSupportedDateFormats(String input, LocalDate parsed, Class<? extends Throwable> throws_) {
        assertEquals(throws_, thrownBy(() -> assertEquals(parsed, parser.parseDate(input))));
    }

    private static Class<? extends Throwable> thrownBy(Executable action) {
        try {
            action.execute();
            return null;
        } catch (Throwable thrown) {
            return thrown.getClass();
        }
    }
}
