package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DiscountLadderTest {

    @DisplayName("Computes the ladder discount from the number of single tickets in the trailing 30 days")
    @Description("""
        Ticket Count includes the ticket currently being purchased, not just the prior tickets found
        in history. The ladder rises 5 percentage points for every 5 tickets and never exceeds Max
        Discount (Policy).
        """)
    @TableTest("""
        Scenario                                 | Ticket Count | Max Discount (Policy) | Discount %?
        First ticket, no accumulation yet        | 1            | 40                    | 0
        Just below the first discount tier       | 4            | 40                    | 0
        First discount tier, the fifth ticket    | 5            | 40                    | 5
        Just below the second discount tier      | 9            | 40                    | 5
        Second discount tier, the tenth ticket   | 10           | 40                    | 10
        Just below the natural ladder maximum    | 39           | 40                    | 35
        Ladder reaches the maximum naturally     | 40           | 40                    | 40
        Ladder would exceed the maximum uncapped | 45           | 40                    | 40
        """)
    void computesLadderDiscount(int ticketCount, int maxDiscountPercent, int discountPercent) {
        assertEquals(discountPercent, new DiscountLadder().discountForTicketCount(ticketCount, maxDiscountPercent));
    }
}
