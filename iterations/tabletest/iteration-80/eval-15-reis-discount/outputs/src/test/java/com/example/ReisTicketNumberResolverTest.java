package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverterSources;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TypeConverterSources(ReisTestConverters.class)
class ReisTicketNumberResolverTest {

    @Description("""
        Window (Days) is the trailing window Reis measures back from Purchase Time; a
        purchase exactly that many days before Purchase Time still counts. Only single
        tickets count toward the number, regardless of the zone travelled. Past
        purchases are not tied to a particular traveler category here, since this
        table assumes category does not affect which of them count.
        """)
    @TableTest("""
        Scenario                                                 | Purchase Time       | Past Purchases                                                                                                                                                                                                                                                                                   | Window (Days) | Ticket Number?
        No prior purchases                                       | 2026-08-08T00:00:00 | []                                                                                                                                                                                                                                                                                               | 30            | 1
        Single ticket bought earlier today                       | 2026-08-08T12:00:00 | [[purchasedAt: "2026-08-08T09:00:00", ticketType: SINGLE, zone: ZONE_1]]                                                                                                                                                                                                                         | 30            | 2
        Purchase from exactly the window edge still counts       | 2026-08-08T00:00:00 | [[purchasedAt: "2026-07-09T00:00:00", ticketType: SINGLE, zone: ZONE_1]]                                                                                                                                                                                                                         | 30            | 2
        Purchase from just past the window edge no longer counts | 2026-08-08T00:00:00 | [[purchasedAt: "2026-07-08T23:59:59", ticketType: SINGLE, zone: ZONE_1]]                                                                                                                                                                                                                         | 30            | 1
        Only single tickets count toward the number              | 2026-08-08T00:00:00 | [[purchasedAt: "2026-08-01T00:00:00", ticketType: SINGLE, zone: ZONE_1], [purchasedAt: "2026-08-02T00:00:00", ticketType: WEEKLY, zone: ZONE_1], [purchasedAt: "2026-08-03T00:00:00", ticketType: MONTHLY, zone: ZONE_1]]                                                                        | 30            | 2
        Zone travelled does not affect whether a trip counts     | 2026-08-08T00:00:00 | [[purchasedAt: "2026-08-01T00:00:00", ticketType: SINGLE, zone: ZONE_1], [purchasedAt: "2026-08-02T00:00:00", ticketType: SINGLE, zone: ZONE_2], [purchasedAt: "2026-08-03T00:00:00", ticketType: SINGLE, zone: ZONE_3]]                                                                         | 30            | 4
        Several qualifying tickets accumulate toward the number  | 2026-08-08T00:00:00 | [[purchasedAt: "2026-08-01T00:00:00", ticketType: SINGLE, zone: ZONE_1], [purchasedAt: "2026-08-02T00:00:00", ticketType: SINGLE, zone: ZONE_1], [purchasedAt: "2026-08-03T00:00:00", ticketType: SINGLE, zone: ZONE_1], [purchasedAt: "2026-08-04T00:00:00", ticketType: SINGLE, zone: ZONE_1]] | 30            | 5
        """)
    void determinesTicketNumberFromRecentSingleTicketPurchases(LocalDateTime purchaseTime, List<PastPurchase> pastPurchases, int windowDays, int ticketNumber) {
        var resolver = new ReisTicketNumberResolver();
        assertEquals(ticketNumber, resolver.determineTicketNumber(pastPurchases, purchaseTime, windowDays));
    }
}
