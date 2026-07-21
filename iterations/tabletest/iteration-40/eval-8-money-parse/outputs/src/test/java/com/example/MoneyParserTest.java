package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MoneyParserTest {

    private MoneyParser parser;

    @BeforeEach
    void setUp() {
        parser = new MoneyParser();
    }

    @TableTest("""
        Scenario                | Input | Parsed?
        Whole amount with cents | 10.00 | 10.00
        Smallest unit           | 0.01  | 0.01
        Null input               |       |
        """)
    void parsesInputIntoMoney(String input, Money parsed) {
        assertEquals(parsed, parser.parse(input));
    }

    @TableTest("""
        Scenario         | Input  | Throws?
        Empty string     | ''     | java.lang.IllegalArgumentException
        Letters only     | abc    | java.lang.IllegalArgumentException
        Negative amount  | -5.00  | java.lang.IllegalArgumentException
        """)
    void rejectsInvalidInput(String input, Class<? extends Exception> throws_) {
        assertThrows(throws_, () -> parser.parse(input));
    }

    @TypeConverter
    public static Money parseExpectedMoney(String input) {
        if (input == null) {
            return null;
        }
        return new Money(new BigDecimal(input));
    }
}
