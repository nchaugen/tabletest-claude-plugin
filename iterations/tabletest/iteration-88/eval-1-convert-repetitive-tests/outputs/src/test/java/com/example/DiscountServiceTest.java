package com.example;

import org.tabletest.junit.TableTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscountServiceTest {

    private final DiscountService discountService = new DiscountService();

    @TableTest("""
        Scenario              | Tier   | Amount | Discount?
        Gold tier gets 20%    | GOLD   | 100    | 20
        Silver tier gets 10%  | SILVER | 100    | 10
        Bronze tier gets none | BRONZE | 100    | 0
        """)
    void calculatesDiscountFromCustomerTier(String tier, int amount, int discount) {
        assertEquals(discount, discountService.calculate(tier, amount));
    }
}
