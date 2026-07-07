package com.example;

import org.tabletest.junit.TypeConverter;

import java.time.LocalDateTime;

public class PastPurchaseConverters {

    @TypeConverter
    public static PastPurchase parsePastPurchase(String input) {
        String[] parts = input.split(" ");
        LocalDateTime purchasedAt = LocalDateTime.parse(parts[0]);
        TicketType ticketType = TicketType.valueOf(parts[1]);
        return new PastPurchase(purchasedAt, TravelerCategory.ADULT, ticketType, ZoneValidity.ZONE_1);
    }
}
