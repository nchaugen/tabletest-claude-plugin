package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecentSingleTicketCounterTest {

    private static final LocalDateTime PURCHASE_TIME = LocalDateTime.of(2026, 6, 30, 12, 0);

    private final RecentSingleTicketCounter counter = new ReisRecentSingleTicketCounter();

    @Description("""
        Purchase time is fixed at 2026-06-30T12:00 for every row; "Days Ago" is measured back
        from that instant, and a purchase exactly 30 days before it is still inside the window.
        The count includes the ticket currently being purchased, so an empty history counts as 1.
        Assumption: a past purchase counts toward the total regardless of the traveler category
        or zone it was recorded under - only its ticket type and recency matter.
        """)
    @TableTest("""
        Scenario                                               | Purchase History                                                                                                                                                                    | Recent Single Ticket Count?
        No prior purchases                                     | []                                                                                                                                                                                  | 1
        Single ticket exactly 30 days ago is inside the window | [[daysAgo: 30, type: SINGLE, category: ADULT, zone: ZONE_1]]                                                                                                                        | 2
        Single ticket 31 days ago falls outside the window     | [[daysAgo: 31, type: SINGLE, category: ADULT, zone: ZONE_1]]                                                                                                                        | 1
        Period tickets in the window are not counted           | [[daysAgo: 5, type: WEEKLY, category: ADULT, zone: ZONE_1]]                                                                                                                         | 1
        Recorded category and zone do not affect the count     | [[daysAgo: 5, type: SINGLE, category: CHILD, zone: ZONE_1], [daysAgo: 5, type: SINGLE, category: SENIOR, zone: ZONE_2], [daysAgo: 5, type: SINGLE, category: ADULT, zone: ZONE_3]]  | 4
        Qualifying and non-qualifying purchases both present   | [[daysAgo: 1, type: SINGLE, category: ADULT, zone: ZONE_1], [daysAgo: 1, type: MONTHLY, category: ADULT, zone: ZONE_1], [daysAgo: 40, type: SINGLE, category: ADULT, zone: ZONE_1]] | 2
        """)
    void countsSingleTicketsPurchasedInTheTrailingThirtyDays(List<PastPurchase> purchaseHistory, int recentSingleTicketCount) {
        assertEquals(recentSingleTicketCount, counter.countIncludingCurrentPurchase(purchaseHistory, PURCHASE_TIME));
    }

    @TypeConverter
    public static PastPurchase parsePastPurchase(Map<String, String> fields) {
        int daysAgo = Integer.parseInt(fields.get("daysAgo"));
        return new PastPurchase(
                PURCHASE_TIME.minusDays(daysAgo),
                TravelerCategory.valueOf(fields.get("category")),
                TicketType.valueOf(fields.get("type")),
                ZoneValidity.valueOf(fields.get("zone")));
    }
}
