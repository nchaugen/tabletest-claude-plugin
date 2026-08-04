package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscountServiceTest {

    private final DiscountService discountService = new DiscountService();

    @Description("""
        Amount is held at 100 throughout: the rule under test is the discount
        rate applied per tier, not the multiplication itself.
        """)
    @TableTest("""
        Scenario        | Tier   | Amount | Discount?
        Gold customer   | GOLD   | 100    | 20
        Silver customer | SILVER | 100    | 10
        Bronze customer | BRONZE | 100    | 0
        """)
    void appliesDiscountRateByCustomerTier(String tier, int amount, int discount) {
        assertEquals(discount, discountService.calculate(tier, amount));
    }
}
