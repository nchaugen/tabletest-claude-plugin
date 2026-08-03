package com.example;

import org.tabletest.junit.TypeConverter;

import java.time.LocalDateTime;
import java.util.Map;

public class PastPurchaseConverters {

    public static final LocalDateTime REFERENCE_PURCHASE_TIME = LocalDateTime.of(2026, 8, 3, 10, 0);

    @TypeConverter
    public static PastPurchase toPastPurchase(Map<String, String> fields) {
        int daysAgo = Integer.parseInt(fields.get("daysAgo"));
        TicketType ticketType = TicketType.valueOf(fields.getOrDefault("ticketType", "SINGLE"));
        ZoneValidity zone = ZoneValidity.valueOf(fields.getOrDefault("zone", "ZONE_1"));
        TravelerCategory category = TravelerCategory.valueOf(fields.getOrDefault("category", "ADULT"));
        return new PastPurchase(REFERENCE_PURCHASE_TIME.minusDays(daysAgo), category, ticketType, zone);
    }
}
