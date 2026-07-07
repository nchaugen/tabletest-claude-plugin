package com.example.cinema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

class TicketPricerTest {

    private final TicketPricer pricer = new TicketPricer();

    @Description("""
        Regular (non-matinee) screenings only. The matinee discount is
        covered separately in shouldApplyMatineeDiscount.
        """)
    @TableTest("""
        Scenario                 | Age | Price?
        Youngest child           | 0   | 8
        Just under 12            | 11  | 8
        Just turns adult         | 12  | 14
        Middle-aged adult        | 40  | 14
        Day before 65th birthday | 64  | 14
        Turns 65                 | 65  | 10
        Well into retirement     | 90  | 10
        """)
    void shouldPriceByAgeTier(int age, int price) {
        assertEquals(price, pricer.priceInEuros(age, false));
    }

    @Description("""
        Matinee screenings are 2 euros off the regular price for every
        age tier. One representative age per tier is enough here since
        tier boundaries are already covered by shouldPriceByAgeTier.
        """)
    @TableTest("""
        Scenario        | Age | Price?
        Child matinee   | 10  | 6
        Adult matinee   | 30  | 12
        Senior matinee  | 70  | 8
        """)
    void shouldApplyMatineeDiscount(int age, int price) {
        assertEquals(price, pricer.priceInEuros(age, true));
    }
}
