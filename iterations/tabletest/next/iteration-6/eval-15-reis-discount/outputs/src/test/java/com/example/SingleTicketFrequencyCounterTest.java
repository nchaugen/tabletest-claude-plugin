package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverterSources;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TypeConverterSources(PastPurchaseFixtures.class)
public class SingleTicketFrequencyCounterTest {

    private final SingleTicketFrequencyCounter counter = new SingleTicketFrequencyCounter();

    @Description("""
        History values encode "{daysAgo}-{ticketType}" relative to a fixed purchase
        time (PastPurchaseFixtures.NOW). The trailing window is the 30 days up to and
        including that purchase time, so a purchase exactly 30 days before counts and
        one 31 days before does not. Traveler category and zone of past purchases are
        fixed defaults in the fixture — they do not affect this count.
        """)
    @TableTest("""
        Scenario                                           | History                       | Single Tickets In Window?
        No purchase history                                | []                            | 0
        Only non-single ticket types                       | [3-WEEKLY, 10-MONTHLY]        | 0
        Multiple single tickets within the window           | [1-SINGLE, 15-SINGLE, 29-SINGLE] | 3
        Single ticket exactly thirty days before purchase   | [30-SINGLE]                   | 1
        Single ticket thirty-one days before purchase       | [31-SINGLE]                   | 0
        Single tickets mixed with other types and older trips | [2-SINGLE, 2-WEEKLY, 40-SINGLE] | 1
        """)
    void countsSingleTicketPurchasesWithinTrailingWindow(List<PastPurchase> history, int count) {
        assertEquals(count, counter.countInTrailingWindow(history, PastPurchaseFixtures.NOW));
    }
}
