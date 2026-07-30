package com.example;

import org.junit.jupiter.api.function.Executable;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateParserTest {

    private final DateParser parser = new DateParser();

    @Description("""
        Short year inputs (yy-MM-dd) are assumed to belong to the 2000s (24 -> 2024).
        Open: behaviour for years outside the 2000s window is undecided.
        """)
    @TableTest("""
        Scenario       | Input      | Parsed Date?
        ISO format     | 2024-01-15 | 2024-01-15
        Slash format   | 15/01/2024 | 2024-01-15
        Short year     | 24-01-15   | 2024-01-15
        Null input     |            |
        """)
    void parsesRecognizedDateFormats(String input, LocalDate parsedDate) {
        assertEquals(parsedDate, parser.parseDate(input));
    }

    @TableTest("""
        Scenario     | Input | Throws?
        Empty string | ''    | java.lang.IllegalArgumentException
        """)
    void rejectsEmptyInput(String input, Class<? extends Throwable> throws_) {
        assertEquals(throws_, thrownBy(() -> parser.parseDate(input)));
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
