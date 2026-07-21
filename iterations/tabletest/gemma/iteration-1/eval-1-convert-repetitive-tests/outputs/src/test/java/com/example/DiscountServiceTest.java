package com.example;

import org.junit.jupiter.api.Test;
import org.tabletest.junit.TableTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscountServiceTest {

    private final DiscountService discountService = new DiscountService();

    @TableTest("""
        Scenario         | Level  | Amount | Discount?
        Gold customer    | GOLD   | 100    | 20
        Silver customer  | SILVER | 100    | 10
        Bronze customer  | BRONZE | 100    | 0
        """)
    void calculateDiscount(String level, int amount, int expectedDiscount) {
        assertEquals(expectedDiscount, discountService.calculate(level, amount));
    }
}
