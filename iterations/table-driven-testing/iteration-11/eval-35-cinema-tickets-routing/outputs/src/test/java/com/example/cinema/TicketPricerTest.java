package com.example.cinema;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TicketPricerTest {

    @DisplayName("Prices a ticket by age category, discounted for matinee screenings")
    @Description("""
        Age categories: children are under 12, seniors are 65 and over, everyone
        else pays the adult price. The matinee discount applies uniformly across
        all three categories.
        """)
    @TableTest("""
        Scenario                  | Age | Matinee | Price?
        Last age as a child       | 11  | false   | 8
        First age as an adult     | 12  | false   | 14
        Last age as an adult      | 64  | false   | 14
        First age as a senior     | 65  | false   | 10
        Matinee screening, child  | 11  | true    | 6
        Matinee screening, adult  | 12  | true    | 12
        Matinee screening, senior | 65  | true    | 8
        """)
    void pricesTicketByAgeAndMatinee(int age, boolean matinee, int price) {
        assertEquals(price, new TicketPricer().priceInEuros(age, matinee));
    }
}
