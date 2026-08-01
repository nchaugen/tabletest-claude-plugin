package com.example.cinema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

class TicketPricerTest {

    private final TicketPricer pricer = new TicketPricer();

    @DisplayName("Prices a ticket from the visitor's age and whether the screening is a matinee")
    @Description("""
        Age is assumed non-negative; the given rules do not specify behavior
        for negative ages, so that case is not covered here.
        """)
    @TableTest("""
        Scenario                   | Age           | Matinee | Price?
        Under 12                   | {0, 6, 11}    | false   | 8
        12 up to 65                | {12, 35, 64}  | false   | 14
        65 and older               | {65, 90, 120} | false   | 10
        Child ticket at a matinee  | 8             | true    | 6
        Adult ticket at a matinee  | 30            | true    | 12
        Senior ticket at a matinee | 70            | true    | 8
        """)
    void pricesTicketByAgeAndScreeningTime(int age, boolean matinee, int price) {
        assertEquals(price, pricer.priceInEuros(age, matinee));
    }
}
