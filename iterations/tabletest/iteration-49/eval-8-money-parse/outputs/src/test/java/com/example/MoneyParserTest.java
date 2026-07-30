package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MoneyParserTest {

    private final MoneyParser parser = new MoneyParser();

    @DisplayName("Parses a decimal amount string into Money, or null for null input")
    @TableTest("""
        Scenario           | Input | Money?
        Two decimal places | 10.00 | 10.00
        Minimum unit       | 0.01  | 0.01
        Null input         |       |
        """)
    void parsesAmount(String input, Money expected) {
        assertEquals(expected, parser.parse(input));
    }

    @DisplayName("Rejects a string that is not a valid non-negative amount")
    @TableTest("""
        Scenario     | Input  | Throws?
        Empty string | ''     | java.lang.IllegalArgumentException
        Letters only | abc    | java.lang.IllegalArgumentException
        Negative     | -5.00  | java.lang.IllegalArgumentException
        """)
    void rejectsInvalidInput(String input, Class<? extends Throwable> throws_) {
        assertThrows(throws_, () -> parser.parse(input));
    }

    @TypeConverter
    public static Money parseMoney(String value) {
        return new Money(new BigDecimal(value));
    }
}
