package com.example.cinema;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TicketPricerTest {

    private final TicketPricer pricer = new TicketPricer();

    @DisplayName("Prices a ticket from the visitor's age and the matinee discount")
    @Description("""
        Age bands: under 12 is a child, 12 through 64 is an adult, 65 and over
        is a senior. The matinee discount is a flat 2 euros off the age-based
        price, for every age band. Ages are assumed non-negative; behaviour
        for negative ages is unspecified and not covered here.
        """)
    @TableTest("""
        Scenario                             | Age | Matinee | Price?
        Child just below the adult cutoff    | 11  | false   | 8
        Adult just at the cutoff             | 12  | false   | 14
        Adult just before the senior cutoff  | 64  | false   | 14
        Senior at the cutoff                 | 65  | false   | 10
        Child, matinee screening             | 8   | true    | 6
        Adult, matinee screening             | 30  | true    | 12
        Senior, matinee screening            | 70  | true    | 8
        """)
    void pricesTicketFromAgeAndMatinee(int age, boolean matinee, int price) {
        assertEquals(price, pricer.priceInEuros(age, matinee));
    }
}
