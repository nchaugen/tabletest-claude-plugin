package com.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.function.Executable;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoneyParserTest {

    private final MoneyParser parser = new MoneyParser();

    @DisplayName("Parses money from valid input, treating null as absent")
    @TableTest("""
        Scenario           | Input | Parsed?
        Whole and fraction | 10.00 | 10.00
        Smallest unit      | 0.01  | 0.01
        Null input         |       |
        """)
    void parsesMoneyFromValidInput(String input, Money parsed) {
        assertEquals(parsed, parser.parse(input));
    }

    @DisplayName("Rejects malformed or negative monetary input")
    @TableTest("""
        Scenario        | Input | Throws?
        Empty string    | ''    | java.lang.IllegalArgumentException
        Letters only    | abc   | java.lang.IllegalArgumentException
        Negative amount | -5.00 | java.lang.IllegalArgumentException
        """)
    void rejectsInvalidMoneyInput(String input, Class<? extends Throwable> throws_) {
        assertEquals(throws_, thrownBy(() -> parser.parse(input)));
    }

    @TypeConverter
    public static Money parseMoney(String value) {
        return new Money(new java.math.BigDecimal(value));
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
