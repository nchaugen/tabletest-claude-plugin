package com.example;

import org.tabletest.junit.TableTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscountServiceTest {

    private final DiscountService discountService = new DiscountService();

    @TableTest("""
        Scenario        | Tier   | Amount | Discount?
        Gold customer   | GOLD   | 100    | 20
        Silver customer | SILVER | 100    | 10
        Bronze customer | BRONZE | 100    | 0
        """)
    void calculatesDiscountByCustomerTier(String tier, int amount, int discount) {
        assertEquals(discount, discountService.calculate(tier, amount));
    }
}
