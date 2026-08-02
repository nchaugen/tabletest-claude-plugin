package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DiscountLadderTest {

    private final DiscountLadder ladder = new DiscountLadder();

    @Description("""
        The ladder adds 5% for every five single tickets accumulated in the trailing 30-day
        window, counting the ticket currently being purchased - so "Prior Single Tickets" of 4
        means this purchase is the fifth ticket. The discount is capped at 40%.
        """)
    @TableTest("""
        Scenario                                    | Prior Single Tickets (30 Days) | Discount %?
        No discount below the fifth ticket          | 3                              | 0
        First rung reached at the fifth ticket      | 4                              | 5
        Discount holds until the next rung          | 8                              | 5
        Second rung reached at the tenth ticket      | 9                              | 10
        Approaching the maximum discount             | 38                             | 35
        Maximum discount reached exactly at the cap  | 39                             | 40
        Discount never exceeds the maximum           | 44                             | 40
        """)
    void appliesTheDiscountLadder(int priorSingleTickets, int discountPercent) {
        assertEquals(discountPercent, ladder.discountPercentFor(priorSingleTickets));
    }
}
