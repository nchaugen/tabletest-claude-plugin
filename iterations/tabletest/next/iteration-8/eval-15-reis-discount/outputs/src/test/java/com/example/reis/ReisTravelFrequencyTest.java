package com.example.reis;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReisTravelFrequencyTest {

    private static final Instant PURCHASE_TIME = Instant.parse("2026-06-30T12:00:00Z");
    private static final String PASSENGER_ID = "passenger-1";

    @Description("""
        Counts single tickets this passenger purchased in the trailing 30 days,
        measured back from the time of the purchase in progress (which is not yet
        in history). The window is inclusive of its start: a purchase exactly 30 days
        before counts, one 31 days before does not. Only SINGLE tickets count; other
        ticket types (e.g. period tickets) are ignored regardless of how recent they are.
        """)
    @TableTest("""
        Scenario                              | Prior Purchases                | Count?
        No prior purchases                    | []                              | 0
        Several recent single tickets          | [SINGLE/1, SINGLE/2, SINGLE/3]  | 3
        Exactly 30 days ago still counts       | [SINGLE/30]                     | 1
        31 days ago falls outside the window   | [SINGLE/31]                     | 0
        Period tickets are not counted         | [PERIOD/1, PERIOD/2]            | 0
        Mix of single and period tickets       | [SINGLE/1, PERIOD/2, SINGLE/3]  | 2
        """)
    void countsSingleTicketsInLast30Days(List<String> priorPurchases, int count) {
        FakeTicketPurchaseHistory history = new FakeTicketPurchaseHistory();
        priorPurchases.forEach(entry -> history.record(typeOf(entry), timeOf(entry)));

        assertEquals(count, ReisTravelFrequency.countSingleTicketsInLast30Days(history, PASSENGER_ID, PURCHASE_TIME));
    }

    private static TicketType typeOf(String entry) {
        return TicketType.valueOf(entry.split("/")[0]);
    }

    private static Instant timeOf(String entry) {
        return PURCHASE_TIME.minus(Long.parseLong(entry.split("/")[1]), ChronoUnit.DAYS);
    }
}
