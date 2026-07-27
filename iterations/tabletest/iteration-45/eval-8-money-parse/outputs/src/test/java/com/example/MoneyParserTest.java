package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MoneyParserTest {

    private final MoneyParser parser = new MoneyParser();

    @DisplayName("Parses a valid amount string, or passes through null input")
    @TableTest("""
        Scenario            | Input | Money?
        Two decimal places  | 10.00 | 10.00
        Smallest unit       | 0.01  | 0.01
        Null input          |       |
        """)
    void parsesValidAmounts(String input, Money expected) {
        assertEquals(expected, parser.parse(input));
    }

    @DisplayName("Rejects invalid amount input")
    @TableTest("""
        Scenario         | Input  | Throws?
        Empty string     | ''     | java.lang.IllegalArgumentException
        Letters only     | abc    | java.lang.IllegalArgumentException
        Negative amount  | -5.00  | java.lang.IllegalArgumentException
        """)
    void rejectsInvalidAmounts(String input, Class<? extends Throwable> throws_) {
        assertThrows(throws_, () -> parser.parse(input));
    }

    @TypeConverter
    public static Money parseMoney(String value) {
        return new Money(new BigDecimal(value));
    }
}
