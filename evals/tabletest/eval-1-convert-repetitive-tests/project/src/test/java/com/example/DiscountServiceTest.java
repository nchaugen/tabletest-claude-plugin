package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscountServiceTest {

    private final DiscountService discountService = new DiscountService();

    @Test
    void returnsDiscountForGoldCustomer() {
        assertEquals(20, discountService.calculate("GOLD", 100));
    }

    @Test
    void returnsDiscountForSilverCustomer() {
        assertEquals(10, discountService.calculate("SILVER", 100));
    }

    @Test
    void returnsNoDiscountForBronzeCustomer() {
        assertEquals(0, discountService.calculate("BRONZE", 100));
    }
}
