package com.example.cinema;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TicketPricerTest {

    private final TicketPricer pricer = new TicketPricer();

    @DisplayName("Prices a ticket from the visitor's age tier and the matinee discount")
    @Description("""
        Age tiers: under 12 is a child, 12 to 64 is an adult, 65 and over is a senior.
        Matinee screenings take 2 euros off the age-tier price, for every tier.
        """)
    @TableTest("""
        Scenario                      | Age | Matinee | Price?
        Last day as a child           | 11  | false   | 8
        First day as an adult         | 12  | false   | 14
        Last day as an adult          | 64  | false   | 14
        First day as a senior         | 65  | false   | 10
        Matinee discount for a child  | 5   | true    | 6
        Matinee discount for an adult | 30  | true    | 12
        Matinee discount for a senior | 70  | true    | 8
        """)
    void pricesTicketsByAgeAndScreeningTime(int age, boolean matinee, int price) {
        assertEquals(price, pricer.priceInEuros(age, matinee));
    }
}
