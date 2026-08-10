package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SingleTicketTravelCountTest {

    private static final LocalDateTime PURCHASE_TIME = LocalDateTime.of(2026, 8, 10, 12, 0);

    @Description("""
        Purchase History rows are expressed as days before Purchase Time (2026-08-10T12:00).
        Traveler category and zone validity of past purchases do not affect this rule, so the
        conversion fixes them to ADULT and ZONE_1.
        """)
    @TableTest("""
        Scenario                                                   | Purchase History                                                                                                                           | Window (Days) | Count?
        No purchase history                                        | []                                                                                                                                         | 30            | 0
        Single ticket purchased today                              | [[daysAgo: 0, ticketType: SINGLE]]                                                                                                         | 30            | 1
        Single ticket at the edge of the window                    | [[daysAgo: 30, ticketType: SINGLE]]                                                                                                        | 30            | 1
        Single ticket just outside the window                      | [[daysAgo: 31, ticketType: SINGLE]]                                                                                                        | 30            | 0
        Weekly and monthly tickets are not counted                 | [[daysAgo: 5, ticketType: WEEKLY], [daysAgo: 6, ticketType: MONTHLY]]                                                                      | 30            | 0
        Mixed history counts only single tickets inside the window | [[daysAgo: 1, ticketType: SINGLE], [daysAgo: 5, ticketType: WEEKLY], [daysAgo: 45, ticketType: SINGLE], [daysAgo: 10, ticketType: SINGLE]] | 30            | 2
        """)
    void countsSingleTicketsPurchasedInTheTrailingWindow(List<PastPurchase> purchaseHistory, int windowDays, int count) {
        assertEquals(count, SingleTicketTravelCount.inLast30Days(purchaseHistory, PURCHASE_TIME));
    }

    @TypeConverter
    public static PastPurchase parsePastPurchase(Map<String, String> fields) {
        long daysAgo = Long.parseLong(fields.get("daysAgo"));
        TicketType ticketType = TicketType.valueOf(fields.get("ticketType"));
        return new PastPurchase(PURCHASE_TIME.minusDays(daysAgo), TravelerCategory.ADULT, ticketType, ZoneValidity.ZONE_1);
    }
}
