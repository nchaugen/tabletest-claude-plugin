package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverterSources;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TypeConverterSources(PastPurchaseConverters.class)
public class RecentPurchaseCounterTest {

    private final RecentPurchaseCounter counter = new RecentPurchaseCounter();

    @Description("""
        Purchase time is fixed at 2026-06-15T10:00:00 for every row (see PastPurchaseConverters
        for the history notation). The 30-day window is measured back from that moment and
        includes a purchase made exactly 30 days earlier. Purchase history is assumed to already
        be scoped to the purchasing traveler.
        """)
    @TableTest("""
        Scenario                                  | History                       | Prior Single Tickets (30 Days)?
        No purchase history                       | ''                            | 0
        Single tickets within the window all count| 5d;15d;29d                    | 3
        Purchase exactly 30 days ago still counts  | 30d                           | 1
        Purchase just past 30 days ago drops out   | 30d1s                         | 0
        Non-single tickets are not counted         | 5d WEEKLY;10d MONTHLY;15d     | 1
        """)
    void countsSingleTicketPurchasesInTheTrailingWindow(List<PastPurchase> history, int priorSingleTickets) {
        assertEquals(priorSingleTickets,
                counter.countRecentSingleTickets(history, PastPurchaseConverters.REFERENCE_PURCHASE_TIME));
    }
}
