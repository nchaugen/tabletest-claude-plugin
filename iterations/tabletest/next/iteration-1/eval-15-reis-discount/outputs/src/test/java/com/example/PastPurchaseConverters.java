package com.example;

import java.time.LocalDateTime;

import org.tabletest.junit.TypeConverter;

public class PastPurchaseConverters {

    public static final LocalDateTime PURCHASE_TIME = LocalDateTime.parse("2026-02-15T09:00:00");

    @TypeConverter
    public static PastPurchase parsePastPurchase(String token) {
        String[] parts = token.split(" ");
        int daysBeforePurchase = Integer.parseInt(parts[0].substring(0, parts[0].length() - 1));
        TicketType ticketType = TicketType.valueOf(parts[1]);
        TravelerCategory travelerCategory = parts.length > 2 ? TravelerCategory.valueOf(parts[2]) : TravelerCategory.ADULT;
        return new PastPurchase(PURCHASE_TIME.minusDays(daysBeforePurchase), travelerCategory, ticketType, ZoneValidity.ZONE_1);
    }
}
