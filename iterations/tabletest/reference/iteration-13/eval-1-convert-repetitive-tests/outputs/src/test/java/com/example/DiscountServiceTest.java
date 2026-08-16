package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscountServiceTest {

    private final DiscountService discountService = new DiscountService();

    @DisplayName("Discount follows the customer tier")
    @Description("""
        Discount is an amount off the order, not a rate. Every row orders 100, which is the one
        order value at which the amount and the tier's percentage read the same, so the table
        cannot tell them apart on its own.
        """)
    @TableTest("""
        Scenario         | Customer tier | Order amount | Discount?
        Gold customer    | GOLD          | 100          | 20
        Silver customer  | SILVER        | 100          | 10
        Bronze customer  | BRONZE        | 100          | 0
        """)
    void discountsByCustomerTier(String customerTier, int orderAmount, int discount) {
        assertEquals(discount, discountService.calculate(customerTier, orderAmount));
    }
}
