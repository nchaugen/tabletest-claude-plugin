package com.example;

import org.tabletest.junit.TypeConverter;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads the table notations this suite uses: a time written relative to the purchase being made,
 * a history entry written as a ticket type at such a time, and a discount written as a percentage.
 */
public class ReisTestConverters {

    /** The instant the purchase under test is made. Every T- value in a table is relative to it. */
    public static final LocalDateTime PURCHASE_TIME = LocalDateTime.of(2026, 3, 1, 12, 0);

    private static final Pattern T_MINUS_SYNTAX =
        Pattern.compile("T-(?:(\\d+)d)?(?:(\\d+)h)?(?:(\\d+)m)?(?:(\\d+)s)?");

    @TypeConverter
    public static LocalDateTime parseRelativeTime(String relativeTime) {
        Matcher syntax = T_MINUS_SYNTAX.matcher(relativeTime);
        if (!syntax.matches()) {
            throw new IllegalArgumentException("Not a T- relative time: " + relativeTime);
        }
        return PURCHASE_TIME
            .minusDays(number(syntax.group(1)))
            .minusHours(number(syntax.group(2)))
            .minusMinutes(number(syntax.group(3)))
            .minusSeconds(number(syntax.group(4)));
    }

    @TypeConverter
    public static PastPurchase parseHistoryEntry(String entry) {
        String[] parts = entry.split("@", 2);
        return new PastPurchase(
            parseRelativeTime(parts[1]),
            TravelerCategory.ADULT,
            TicketType.valueOf(parts[0]),
            ZoneValidity.ZONE_1);
    }

    @TypeConverter
    public static DiscountPercentage parseDiscountPercentage(Integer percentage) {
        return new DiscountPercentage(percentage);
    }

    private static long number(String digits) {
        return digits == null ? 0 : Long.parseLong(digits);
    }
}
