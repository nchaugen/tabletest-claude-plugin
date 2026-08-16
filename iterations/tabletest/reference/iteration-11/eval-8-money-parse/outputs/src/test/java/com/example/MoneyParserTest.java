package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MoneyParserTest {

    private final MoneyParser parser = new MoneyParser();

    @DisplayName("An amount keeps the scale it was written with")
    @Description("""
        The requirement gives two amounts and leaves the questions a BigDecimal raises open, so two
        rows here answer them and are the ones to argue with. The row without a fraction says the
        parser adds none: 5 comes back as 5, and a parser normalising to two places would return
        5.00, which is a different BigDecimal. The zero row places zero on the accepted side of the
        line the negative rule draws, since the requirement rejects below zero and never says where
        zero itself falls.
        """)
    @TableTest("""
        Scenario                                | Input | Money?
        Two decimal places, as the prompt gives | 10.00 | 10.00
        The smallest amount above zero          | 0.01  | 0.01
        Zero, which the prompt never places     | 0.00  | 0.00
        No fraction written, and none added     | 5     | 5
        No input at all                         |       |
        """)
    void keepsTheScaleTheAmountWasWrittenWith(String input, Money money) {
        assertEquals(money, parser.parse(input));
    }

    @DisplayName("Input that is not an amount is rejected")
    @Description("""
        The first three rows are the rejections the requirement names. The last settles a point it
        leaves open: a well-formed amount carrying anything the format does not mention is rejected
        rather than quietly stripped, so a caller cannot smuggle a currency in and get a number back.
        """)
    @TableTest("""
        Scenario                             | Input  | Rejected with?
        Empty string                         | ''     | java.lang.IllegalArgumentException
        Letters where an amount was expected | abc    | java.lang.IllegalArgumentException
        An amount below zero                 | -5.00  | java.lang.IllegalArgumentException
        A currency symbol the format omits   | $10.00 | java.lang.IllegalArgumentException
        """)
    void rejectsInputThatIsNotAnAmount(String input, Class<? extends Throwable> rejectedWith) {
        assertThrows(rejectedWith, () -> parser.parse(input));
    }

    @TypeConverter
    public static Money parseMoney(String amount) {
        return new Money(new BigDecimal(amount));
    }
}
