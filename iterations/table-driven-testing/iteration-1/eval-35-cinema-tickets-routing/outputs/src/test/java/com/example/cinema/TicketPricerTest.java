package com.example.cinema;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TicketPricerTest {

    private final TicketPricer pricer = new TicketPricer();

    @Description("""
        Age tiers: child = under 12, adult = 12 up to 64, senior = 65 and over.
        All rows are non-matinee screenings; see shouldApplyMatineeDiscount
        for the matinee reduction.
        """)
    @TableTest("""
        Scenario             | Age            | Price?
        Child (under 12)     | {0, 6, 11}     | 8
        Adult lower boundary | 12             | 14
        Adult (12-64)        | {13, 40, 64}   | 14
        Senior (65 and over) | {65, 80, 120}  | 10
        """)
    void shouldPriceByAgeTier(int age, int price) {
        assertEquals(price, pricer.priceInEuros(age, false));
    }

    @Description("""
        Matinee screenings are 2 euros off the standard price, regardless of
        age tier. Ages are representative values from each tier; see
        shouldPriceByAgeTier for the full age-tier boundaries.
        """)
    @TableTest("""
        Scenario       | Age | Price?
        Child matinee  | 6   | 6
        Adult matinee  | 40  | 12
        Senior matinee | 80  | 8
        """)
    void shouldApplyMatineeDiscount(int age, int price) {
        assertEquals(price, pricer.priceInEuros(age, true));
    }
}
