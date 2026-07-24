package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReisDiscountLadderTest {

    private final ReisDiscountLadder ladder = new ReisDiscountLadder();

    @Description("""
        Applies to adult and senior single-ticket purchases only. "Prior Single
        Tickets" is the trailing-window count from SingleTicketFrequencyCounter — the
        count of single tickets already purchased, not including the one being bought
        now. The ticket now being bought is therefore ticket number (prior count + 1),
        and every fifth ticket raises the discount by another 5 percentage points, up
        to a 40% maximum. Open question: the source spec does not say whether the
        ladder should ever start below zero tickets purchased; ticket 1 is assumed to
        carry no discount, matching "the first discount applies to ticket number five".
        """)
    @TableTest("""
        Scenario                                                  | Prior Single Tickets | Discount?
        First ticket, before any tier is reached                  | 0                    | 0
        Fourth ticket, still below the fifth-ticket threshold     | 3                    | 0
        Fifth ticket, first tier begins                           | 4                    | 5
        Ninth ticket, first tier still holds                      | 8                    | 5
        Tenth ticket, ladder climbs to the second tier            | 9                    | 10
        Fifteenth ticket                                          | 14                   | 15
        Twentieth ticket                                          | 19                   | 20
        Twenty-fifth ticket                                       | 24                   | 25
        Thirtieth ticket                                          | 29                   | 30
        Thirty-fifth ticket                                       | 34                   | 35
        Fortieth ticket, ladder reaches the maximum naturally     | 39                   | 40
        Forty-fifth ticket, maximum discount caps further climbing | 44                  | 40
        """)
    void calculatesLadderDiscountFromPriorSingleTicketCount(int priorSingleTickets, int discount) {
        assertEquals(discount, ladder.discountFor(priorSingleTickets).value());
    }
}
