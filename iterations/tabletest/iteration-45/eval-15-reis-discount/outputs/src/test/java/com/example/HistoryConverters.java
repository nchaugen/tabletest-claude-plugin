package com.example;

import java.time.LocalDateTime;

import org.tabletest.junit.TypeConverter;

public class HistoryConverters {

    public static final LocalDateTime PURCHASE_TIME = LocalDateTime.parse("2026-01-31T10:00:00");
    public static final LocalDateTime CUTOFF_30_DAYS_AGO = PURCHASE_TIME.minusDays(30);

    @TypeConverter
    public static PastPurchase parseHistoryEntry(String value) {
        String[] parts = value.trim().split("\\s+");
        LocalDateTime purchasedAt = switch (parts[0]) {
            case "beforeCutoff" -> CUTOFF_30_DAYS_AGO.minusDays(1);
            case "onCutoff" -> CUTOFF_30_DAYS_AGO;
            case "afterCutoff" -> CUTOFF_30_DAYS_AGO.plusDays(1);
            default -> throw new IllegalArgumentException("Unknown cutoff relation: " + parts[0]);
        };
        TicketType ticketType = TicketType.valueOf(parts[1]);
        return new PastPurchase(purchasedAt, TravelerCategory.ADULT, ticketType, ZoneValidity.ZONE_1);
    }
}
