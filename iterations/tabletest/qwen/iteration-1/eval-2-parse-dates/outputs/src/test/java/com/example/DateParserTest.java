package com.example;

import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

class DateParserTest {

      /**
       * Valid date inputs parsed into expected LocalDate values.
       */
      @TableTest("""
        Scenario                     | Input               | Result?
        ISO year-month-day           | 2024-01-15         | 2024-01-15
        Slash day/month/year         | 15/01/2024         | 2024-01-15
        Short two-digit year (YY)    | 24-01-15           | 2024-01-15
        Null input                   |                     |
          """)
    void parsesDate(String input, LocalDate result) {
        LocalDate actual = DateParser.parseDate(input);
        if (result == null) {
            org.junit.jupiter.api.Assertions.assertNull(actual);
         } else {
            org.junit.jupiter.api.Assertions.assertEquals(result, actual);
         }
      }

      /**
       * Empty string input is rejected with an exception.
       */
      @TableTest("""
        Scenario           | Input        | Throws?
        Empty string        | ''           | java.lang.IllegalArgumentException
          """)
    void rejectsEmptyString(String input, Class<? extends Throwable> throws_) {
        org.junit.jupiter.api.Assertions.assertThrows(throws_, () -> DateParser.parseDate(input));
      }

      // ── Type converter: single parser handles all date formats in table cells ──

      @TypeConverter
    static LocalDate parseTableDate(String value) {
        try {
            return LocalDate.parse(value);                      // ISO yyyy-MM-dd
         } catch (DateTimeParseException e) {
            // Try slash format: dd/MM/yyyy
            String[] bySlash = value.split("/");
            if (bySlash.length == 3) {
                return LocalDate.of(Integer.parseInt(bySlash[2]),
                                    Integer.parseInt(bySlash[1]),
                                    Integer.parseInt(bySlash[0]));
             }
            // Try short year format: yy-MM-dd
            String[] byDash = value.split("-");
            if (byDash.length == 3) {
                int year = 2000 + Integer.parseInt(byDash[0]);
                return LocalDate.of(year,
                                    Integer.parseInt(byDash[1]),
                                    Integer.parseInt(byDash[2]));
             }
            throw new DateTimeParseException("Unparseable date: " + value, value, 0);
         }
      }

      @TypeConverter
    static Class<? extends Throwable> parseExceptionType(String value) {
        switch (value) {
            case "java.lang.IllegalArgumentException":
                return IllegalArgumentException.class;
            default:
                throw new IllegalArgumentException("Unknown exception type: " + value);
         }
      }

}
