package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscountServiceTest {

    private final DiscountService discountService = new DiscountService();

    @Description("""
        Amount varies for the gold tier to show the discount is a percentage
        of the purchase amount, not a fixed value tied to amount = 100.
        """)
    @TableTest("""
        Scenario                     | Tier   | Amount | Discount?
        Gold customer                | GOLD   | 100    | 20
        Gold customer, larger amount | GOLD   | 250    | 50
        Silver customer              | SILVER | 100    | 10
        Bronze customer              | BRONZE | 100    | 0
        """)
    void calculatesDiscountForCustomerTier(String tier, int amount, int discount) {
        assertEquals(discount, discountService.calculate(tier, amount));
    }
}
