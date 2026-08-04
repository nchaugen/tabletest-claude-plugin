package com.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.function.Executable;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateParserTest {

    private final DateParser parser = new DateParser();

    @DisplayName("Parses recognized date formats, treating null as absent and empty input as invalid")
    @Description("""
        Slash format is interpreted as DD/MM/YYYY. Short year is interpreted as YY-MM-DD
        with the century assumed to be 20xx. Open question: whether a pivot year is needed
        once short-year inputs from the 1900s are required.
        """)
    @TableTest("""
        Scenario     | Input      | Parsed?    | Throws?
        ISO format   | 2024-01-15 | 2024-01-15 |
        Slash format | 15/01/2024 | 2024-01-15 |
        Short year   | 24-01-15   | 2024-01-15 |
        Null input   |            |            |
        Empty string | ''         |            | java.lang.IllegalArgumentException
        """)
    void parsesRecognizedDateFormats(String input, LocalDate parsed, Class<? extends Throwable> throws_) {
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
