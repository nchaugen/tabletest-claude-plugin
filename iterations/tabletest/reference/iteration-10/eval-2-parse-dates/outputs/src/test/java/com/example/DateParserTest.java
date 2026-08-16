package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DateParserTest {

    private final DateParser parser = new DateParser();

    @DisplayName("Every supported format reaches the same date")
    @Description("""
        The first three rows carry one date written three ways, so equivalence is the thing the
        table shows rather than three unrelated parses. The century a two-digit year belongs to is
        the one point the requirement leaves open: it fixes 24 as 2024 and says nothing further, so
        the fourth row takes the simplest rule that agrees with it — the two digits are the last two
        of a year in the 2000s — and is where a reviewer who wants 1999 changes one cell.
        """)
    @TableTest("""
        Scenario                                     | Input      | Parsed date?
        ISO, the format the date is compared in      | 2024-01-15 | 2024-01-15
        Day first, separated by slashes              | 15/01/2024 | 2024-01-15
        Two-digit year, the century the prompt gives | 24-01-15   | 2024-01-15
        Two-digit year, the century it leaves open   | 99-01-15   | 2099-01-15
        No input at all                              |            |
        """)
    void parsesEverySupportedFormatToTheSameDate(String input, LocalDate parsedDate) {
        assertEquals(parsedDate, parser.parseDate(input));
    }

    @DisplayName("Input that is not a date is rejected, not returned empty")
    @Description("""
        The requirement names only the empty string as an error. The second row settles what happens
        to input in none of the supported formats: rejected the same way, rather than coming back as
        a null the caller cannot tell from an absent input. Absent input is the row above, in the
        other table, and returns null by design.
        """)
    @TableTest("""
        Scenario                          | Input      | Rejected with?
        Empty string                      | ''         | java.lang.IllegalArgumentException
        Text in none of the three formats | not a date | java.lang.IllegalArgumentException
        """)
    void rejectsInputThatIsNotADate(String input, Class<? extends Throwable> rejectedWith) {
        assertThrows(rejectedWith, () -> parser.parseDate(input));
    }
}
