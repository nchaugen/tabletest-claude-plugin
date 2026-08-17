package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReisDiscountLadderTest {

    @Description("""
        Ticket Number In Rolling Window is the position of the ticket now being purchased among
        the single tickets bought in the trailing 30 days, counting this purchase itself (so the
        very first single ticket in a 30-day window is ticket number 1).
        """)
    @TableTest("""
        Scenario                     | Ticket Number In Rolling Window | Discount %?
        Before the first tier        | 4                               | 0
        At the first tier            | 5                               | 5
        Last of the first tier       | 9                               | 5
        At the second tier           | 10                              | 10
        Last of the second tier      | 14                              | 10
        At the third tier            | 15                              | 15
        Last of the third tier       | 19                              | 15
        At the fourth tier           | 20                              | 20
        Last of the fourth tier      | 24                              | 20
        At the fifth tier            | 25                              | 25
        Last of the fifth tier       | 29                              | 25
        At the sixth tier            | 30                              | 30
        Last of the sixth tier       | 34                              | 30
        At the seventh tier          | 35                              | 35
        Last of the seventh tier     | 39                              | 35
        At the eighth (maximum) tier | 40                              | 40
        Past the maximum tier        | 45                              | 40
        """)
    void risesFivePercentagePointsEveryFiveTicketsUpToTheMaximum(int ticketNumberInRollingWindow, int discountPercentage) {
        assertEquals(discountPercentage, new ReisDiscountLadder().percentageFor(ticketNumberInRollingWindow));
    }
}
