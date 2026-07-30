package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MoneyParserTest {

    private final MoneyParser parser = new MoneyParser();

    @DisplayName("Parses a valid amount string into Money, and null input into null")
    @TableTest("""
        Scenario           | Input | Money?
        Two decimal places | 10.00 | 10.00
        Minimum unit        | 0.01  | 0.01
        Null input          |       |
        """)
    void parsesAmountString(String input, Money expected) {
        assertEquals(expected, parser.parse(input));
    }

    @DisplayName("Rejects malformed or negative amount strings")
    @TableTest("""
        Scenario     | Input  | Throws?
        Empty string | ''     | java.lang.IllegalArgumentException
        Letters only | abc    | java.lang.IllegalArgumentException
        Negative     | -5.00  | java.lang.IllegalArgumentException
        """)
    void rejectsInvalidAmountString(String input, Class<? extends Throwable> throws_) {
        assertThrows(throws_, () -> parser.parse(input));
    }

    @TypeConverter
    public static Money parseMoney(String input) {
        return new Money(new BigDecimal(input));
    }
}
