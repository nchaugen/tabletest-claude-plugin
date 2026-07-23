package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverterSources;

@TypeConverterSources(ReisTestConverters.class)
public class ReisTravelCountTest {

    @Description("""
        Purchase time is always 2024-03-31T12:00 (ReisTestConverters.REFERENCE_PURCHASE_TIME).
        History entries use "<TicketType>@<days before purchase>", e.g. "SINGLE@5" is a single
        ticket bought 5 days before the purchase being counted. Traveler category and zone on
        past purchases do not affect this count; the parser fixes them to ADULT/ZONE_1.
        """)
    @TableTest("""
        Scenario                                       | History                          | Count?
        No purchase history                            | []                               | 0
        Single tickets within the window               | [SINGLE@1, SINGLE@15, SINGLE@29] | 3
        Non-single tickets are not counted              | [WEEKLY@1, MONTHLY@2]            | 0
        Exactly 30 days before purchase is included     | [SINGLE@30]                      | 1
        Just past 30 days before purchase is excluded    | [SINGLE@31]                      | 0
        Mixed recency and ticket type                    | [SINGLE@5, SINGLE@31, WEEKLY@10] | 1
        """)
    void countsSingleTicketsInLast30Days(List<PastPurchase> history, int count) {
        assertEquals(count,
                SingleTicketHistory.countSingleTicketsInLast30Days(history, ReisTestConverters.REFERENCE_PURCHASE_TIME));
    }
}
