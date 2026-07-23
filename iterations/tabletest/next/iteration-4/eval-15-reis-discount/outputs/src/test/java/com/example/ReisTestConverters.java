package com.example;

import java.time.LocalDateTime;
import org.tabletest.junit.TypeConverter;

public class ReisTestConverters {

    public static final LocalDateTime REFERENCE_PURCHASE_TIME = LocalDateTime.of(2024, 3, 31, 12, 0);

    @TypeConverter
    public static PastPurchase parsePastPurchase(String entry) {
        String[] parts = entry.split("@");
        TicketType ticketType = TicketType.valueOf(parts[0]);
        int daysBeforePurchase = Integer.parseInt(parts[1]);
        return new PastPurchase(
                REFERENCE_PURCHASE_TIME.minusDays(daysBeforePurchase),
                TravelerCategory.ADULT,
                ticketType,
                ZoneValidity.ZONE_1);
    }

    @TypeConverter
    public static DiscountPercentage parseDiscountPercentage(String value) {
        return new DiscountPercentage(Integer.parseInt(value));
    }
}
