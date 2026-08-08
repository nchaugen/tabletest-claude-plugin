package com.example;

import org.tabletest.junit.TypeConverter;

import java.time.LocalDateTime;
import java.util.Map;

public class ReisTestConverters {

    @TypeConverter
    public static PastPurchase parsePastPurchase(Map<String, String> fields) {
        return new PastPurchase(
                LocalDateTime.parse(fields.get("purchasedAt")),
                TravelerCategory.valueOf(fields.getOrDefault("travelerCategory", "ADULT")),
                TicketType.valueOf(fields.get("ticketType")),
                ZoneValidity.valueOf(fields.get("zone")));
    }

    @TypeConverter
    public static DiscountPercentage parseDiscountPercentage(Integer percentage) {
        return new DiscountPercentage(percentage);
    }
}
