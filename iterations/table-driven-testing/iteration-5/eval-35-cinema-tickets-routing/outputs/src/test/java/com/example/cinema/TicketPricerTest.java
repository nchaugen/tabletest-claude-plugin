package com.example.cinema;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TicketPricerTest {

    private final TicketPricer pricer = new TicketPricer();

    @DisplayName("Determines the standard ticket price from the visitor's age band")
    @Description("""
        Standard (non-matinee) screenings. Age bands: children are under 12,
        seniors are 65 and over, everyone in between is an adult.
        """)
    @TableTest("""
        Scenario   | Age           | Standard Price (Euros)?
        Under 12   | {0, 1, 11}    | 8
        12 to 64   | {12, 40, 64}  | 14
        65 and up  | {65, 90, 120} | 10
        """)
    void determinesStandardPriceByAgeBand(int age, int standardPrice) {
        assertEquals(standardPrice, pricer.priceInEuros(age, false));
    }

    @DisplayName("Reduces the ticket price by 2 euros for matinee screenings")
    @Description("""
        The matinee discount applies uniformly across all age bands. The
        Standard Price column is the non-matinee price for the same age
        (see determinesStandardPriceByAgeBand), so the discount is traceable
        to its baseline.
        """)
    @TableTest("""
        Scenario         | Age | Standard Price (Euros)? | Matinee Price (Euros)?
        Child, matinee   | 8   | 8                        | 6
        Adult, matinee   | 30  | 14                       | 12
        Senior, matinee  | 70  | 10                       | 8
        """)
    void appliesMatineeDiscount(int age, int standardPrice, int matineePrice) {
        assertEquals(standardPrice, pricer.priceInEuros(age, false));
        assertEquals(matineePrice, pricer.priceInEuros(age, true));
    }
}
