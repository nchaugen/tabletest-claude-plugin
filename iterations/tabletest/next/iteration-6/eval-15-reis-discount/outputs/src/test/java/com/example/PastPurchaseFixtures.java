package com.example;

import org.tabletest.junit.TypeConverter;

import java.time.LocalDateTime;

public class PastPurchaseFixtures {

    public static final LocalDateTime NOW = LocalDateTime.of(2026, 7, 24, 10, 0);

    // "-" separator, not ":" — a colon in a list element is parsed as map key:value syntax by TableTest.
    @TypeConverter
    public static PastPurchase parsePastPurchase(String value) {
        String[] parts = value.split("-");
        long daysAgo = Long.parseLong(parts[0]);
        TicketType ticketType = TicketType.valueOf(parts[1]);
        return new PastPurchase(NOW.minusDays(daysAgo), TravelerCategory.ADULT, ticketType, ZoneValidity.ZONE_1);
    }
}
