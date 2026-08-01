package com.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.function.Executable;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateParserTest {

    private final DateParser parser = new DateParser();

    @DisplayName("Parses a date string into a LocalDate")
    @Description("""
        Accepted formats: ISO (yyyy-MM-dd), slash (dd/MM/yyyy) and short year (yy-MM-dd).
        Short year is assumed to resolve into the 2000s century (24 -> 2024) since the
        feature description does not state a pivot year.
        A null input returns null rather than throwing; an empty string is rejected.
        """)
    @TableTest("""
        Scenario     | Input      | Parsed?    | Throws?
        ISO format   | 2024-01-15 | 2024-01-15 |
        Slash format | 15/01/2024 | 2024-01-15 |
        Short year   | 24-01-15   | 2024-01-15 |
        Null input   |            |            |
        Empty string | ''         |            | java.lang.IllegalArgumentException
        """)
    void parsesDateString(String input, LocalDate parsed, Class<? extends Throwable> throwsException) {
        assertEquals(throwsException, thrownBy(() -> assertEquals(parsed, parser.parseDate(input))));
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
