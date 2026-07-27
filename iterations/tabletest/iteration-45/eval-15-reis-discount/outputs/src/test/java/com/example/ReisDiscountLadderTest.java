package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

class ReisDiscountLadderTest {

    private final ReisDiscountLadder ladder = new ReisDiscountLadderImpl();

    @DisplayName("Raises the discount by 5% for every five tickets, capped at 40%")
    @Description("""
            Ticket count is the ordinal number of the ticket now being purchased,
            including itself - not a count of prior purchases only.
            """)
    @TableTest("""
            Scenario                    | Ticket Count      | Discount %?
            Below the first tier        | {1, 2, 3, 4}       | 0
            First tier reached          | {5, 7, 9}          | 5
            Second tier                 | {10, 12, 14}       | 10
            Third tier                  | {15, 17, 19}       | 15
            Fourth tier                 | {20, 22, 24}       | 20
            Fifth tier                  | {25, 27, 29}       | 25
            Sixth tier                  | {30, 32, 34}       | 30
            Seventh tier                | {35, 37, 39}       | 35
            Maximum discount reached    | {40, 45, 100}      | 40
            """)
    void computesDiscountFromTicketCount(int ticketCount, int discountPercent) {
        assertEquals(new DiscountPercentage(discountPercent), ladder.discountForTicketCount(ticketCount));
    }
}
