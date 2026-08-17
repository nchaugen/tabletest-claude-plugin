package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RollingSingleTicketCounterTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 6, 30, 12, 0);

    @Description("""
        "Now" is fixed at 2026-06-30T12:00 for every row; only Hours Ago (measured back from "now")
        affects the count, so its exact value carries no separate obligation of its own.
        """)
    @TableTest("""
        Scenario                                                     | Purchase History                                                                                                                                            | Trailing Single Count?
        No purchase history                                          | []                                                                                                                                                          | 0
        Single ticket well within the window                         | [[hoursAgo: 240, type: SINGLE]]                                                                                                                             | 1
        Weekly ticket in the window does not count                   | [[hoursAgo: 240, type: WEEKLY]]                                                                                                                             | 0
        Monthly ticket in the window does not count                  | [[hoursAgo: 240, type: MONTHLY]]                                                                                                                            | 0
        Single ticket exactly 30 days ago still counts               | [[hoursAgo: 720, type: SINGLE]]                                                                                                                             | 1
        Single ticket just past 30 days ago no longer counts         | [[hoursAgo: 721, type: SINGLE]]                                                                                                                             | 0
        Counts only the in-window single tickets among mixed history | [[hoursAgo: 50, type: SINGLE], [hoursAgo: 100, type: WEEKLY], [hoursAgo: 200, type: SINGLE], [hoursAgo: 800, type: SINGLE], [hoursAgo: 719, type: MONTHLY]] | 2
        """)
    void countsSingleTicketsPurchasedInTheTrailing30Days(List<PastPurchase> purchaseHistory, int trailingSingleCount) {
        assertEquals(trailingSingleCount, new RollingSingleTicketCounter().count(purchaseHistory, NOW));
    }

    @TypeConverter
    public static PastPurchase parsePastPurchase(Map<String, String> fields) {
        long hoursAgo = Long.parseLong(fields.get("hoursAgo"));
        TicketType ticketType = TicketType.valueOf(fields.get("type"));
        return new PastPurchase(NOW.minusHours(hoursAgo), TravelerCategory.ADULT, ticketType, ZoneValidity.ZONE_1);
    }
}
