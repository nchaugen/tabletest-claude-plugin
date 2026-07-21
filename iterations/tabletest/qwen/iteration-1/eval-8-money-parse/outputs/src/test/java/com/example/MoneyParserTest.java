package com.example;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

public class MoneyParserTest {

     @TypeConverter
    public static BigDecimal parseMoney(String input) {
        if (input == null || input.isBlank()) {
            return null;
          }
        return new BigDecimal(input);
      }

      /**
       * Valid inputs return a Money instance; null input returns null.
       */
      @TableTest("""
         Scenario                | Input        | Expected?
         Ten dollars              | 10.00        | true
         One cent                 | 0.01         | true
          Null input               |              | false
            """)
    void parse_returnsMoneyForValidInput(String input, BigDecimal expected, boolean returnsMoney) {
        Money actual = new MoneyParser().parse(input);
        if (returnsMoney) {
            Assertions.assertNotNull(actual);
            Assertions.assertEquals(expected, actual.amount());
          } else {
            Assertions.assertNull(actual);
          }
       }

       /**
        * Invalid inputs throw IllegalArgumentException.
        */
       @TableTest("""
          Scenario              | Input
           Empty input            | ''
           Non-numeric input      | abc
           Negative value         | -5.00
             """)
    void parse_throwsForInvalidInput(String input) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new MoneyParser().parse(input));
       }
}
