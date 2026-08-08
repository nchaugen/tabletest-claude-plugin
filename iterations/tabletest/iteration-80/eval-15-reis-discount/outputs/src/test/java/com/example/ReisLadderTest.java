package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverterSources;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TypeConverterSources(ReisTestConverters.class)
class ReisLadderTest {

    @Description("""
        Maximum Discount (%) is the Reis policy cap; ticket numbers past the eighth
        threshold stay at the cap instead of continuing to climb.
        """)
    @TableTest("""
        Scenario                                                 | Ticket Number | Maximum Discount (%) | Discount?
        Below the first five-ticket threshold                    | {1, 4}        | 40                   | 0
        At the first five-ticket threshold                       | {5, 9}        | 40                   | 5
        At the second five-ticket threshold                      | {10, 14}      | 40                   | 10
        At the third five-ticket threshold                       | {15, 19}      | 40                   | 15
        At the fourth five-ticket threshold                      | {20, 24}      | 40                   | 20
        At the fifth five-ticket threshold                       | {25, 29}      | 40                   | 25
        At the sixth five-ticket threshold                       | {30, 34}      | 40                   | 30
        At the seventh five-ticket threshold                     | {35, 39}      | 40                   | 35
        At the eighth five-ticket threshold                      | {40, 44}      | 40                   | 40
        Past the eighth threshold, the discount stays at the cap | 45            | 40                   | 40
        """)
    void appliesTheReisLadderDiscountForTicketNumber(int ticketNumber, int maxDiscountPercent, DiscountPercentage discount) {
        var ladder = new ReisLadder();
        assertEquals(discount, ladder.discountForTicketNumber(ticketNumber, maxDiscountPercent));
    }
}
