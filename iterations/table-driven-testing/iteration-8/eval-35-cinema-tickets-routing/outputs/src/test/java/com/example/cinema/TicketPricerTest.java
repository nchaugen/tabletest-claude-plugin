package com.example.cinema;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TicketPricerTest {

    private final TicketPricer pricer = new TicketPricer();

    @DisplayName("Prices a ticket from age and matinee timing")
    @Description("""
        Age is in completed years. Children are under 12, seniors are 65 and
        over, everyone in between pays the adult price. A matinee screening
        takes 2 euros off the age-based price, whichever tier it is.
        """)
    @TableTest("""
        Scenario                        | Age | Matinee | Price?
        Young child                     | 5   | false   | 8
        Just under the child cutoff     | 11  | false   | 8
        Just at the adult threshold     | 12  | false   | 14
        Adult                           | 30  | false   | 14
        Just under the senior threshold | 64  | false   | 14
        Just at the senior threshold    | 65  | false   | 10
        Senior                          | 80  | false   | 10
        Child at a matinee              | 5   | true    | 6
        Adult at a matinee              | 30  | true    | 12
        Senior at a matinee             | 65  | true    | 8
        """)
    void pricesTicketFromAgeAndMatineeTiming(int age, boolean matinee, int price) {
        assertEquals(price, pricer.priceInEuros(age, matinee));
    }
}
