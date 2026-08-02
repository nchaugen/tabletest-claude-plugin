package com.example;

import org.tabletest.junit.TypeConverter;

import java.time.LocalDateTime;
import java.util.Map;

public class TestConverters {

    @TypeConverter
    public static PastPurchase parsePastPurchase(Map<String, String> fields) {
        return new PastPurchase(
            LocalDateTime.parse(fields.get("purchasedAt")),
            TravelerCategory.valueOf(fields.getOrDefault("travelerCategory", "ADULT")),
            TicketType.valueOf(fields.getOrDefault("ticketType", "SINGLE")),
            ZoneValidity.valueOf(fields.getOrDefault("zoneValidity", "ZONE_1"))
        );
    }

    @TypeConverter
    public static DiscountPercentage parseDiscountPercentage(String value) {
        return new DiscountPercentage(Integer.parseInt(value));
    }
}
