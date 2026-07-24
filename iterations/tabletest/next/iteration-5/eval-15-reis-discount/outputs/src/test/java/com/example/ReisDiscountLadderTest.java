package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReisDiscountLadderTest {

    private final ReisDiscountLadder ladder = new ReisDiscountLadder();

    @Description("""
        Prior Qualifying Tickets is the count of single tickets already purchased
        in the last 30 days (not counting the ticket about to be bought). The first
        discount applies to ticket number five, so the tier increases every time the
        prior count reaches a multiple of 5 minus one (4, 9, 14, ...). The discount
        never exceeds 40%, even though the ladder's own arithmetic would otherwise
        keep climbing past that point.
        """)
    @TableTest("""
        Scenario                              | Prior Qualifying Tickets | Discount %?
        Below the first threshold             | {0, 1, 2, 3}             | 0
        First discount tier, from ticket five | {4, 5, 6, 7, 8}          | 5
        Second discount tier                  | {9, 10, 11, 12, 13}      | 10
        Third discount tier                   | {14, 15, 16, 17, 18}     | 15
        Tier just below the maximum           | {34, 35, 36, 37, 38}     | 35
        Maximum discount reached              | 39                       | 40
        Discount never exceeds the maximum    | {44, 100, 500}           | 40
        """)
    void looksUpDiscountTier(int priorQualifyingTickets, int discountPercentage) {
        assertEquals(discountPercentage, ladder.discountPercentageFor(priorQualifyingTickets));
    }
}
