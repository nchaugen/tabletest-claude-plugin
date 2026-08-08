package com.example;

import org.junit.jupiter.api.function.Executable;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoneyParserTest {

    private final MoneyParser parser = new MoneyParser();

    @TableTest("""
        Scenario           | Input | Parsed?
        Whole cents amount | 10.00 | 10.00
        Smallest amount    | 0.01  | 0.01
        Null input         |       |
        """)
    void parsesAmountsIntoMoney(String input, Money parsed) {
        assertEquals(parsed, parser.parse(input));
    }

    @TableTest("""
        Scenario       | Input | Throws?
        Empty string   | ''    | java.lang.IllegalArgumentException
        Letters only   | abc   | java.lang.IllegalArgumentException
        Negative value | -5.00 | java.lang.IllegalArgumentException
        """)
    void rejectsInvalidAmounts(String input, Class<? extends Throwable> throws_) {
        assertEquals(throws_, thrownBy(() -> parser.parse(input)));
    }

    @TypeConverter
    public static Money parseMoney(String value) {
        return new Money(new BigDecimal(value));
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
