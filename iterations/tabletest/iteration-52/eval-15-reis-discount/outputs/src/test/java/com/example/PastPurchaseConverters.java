package com.example;

import org.tabletest.junit.TypeConverter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Shared table notation for {@link PastPurchase} history cells: entries are written as an offset
 * before {@link #REFERENCE_PURCHASE_TIME}, semicolon-separated, e.g. {@code "5d;15d;29d"}.
 * An entry is {@code "<days>d"} or {@code "<days>d<seconds>s"} (the latter for boundary cases,
 * e.g. {@code "30d1s"} = 30 days and 1 second before the reference time), optionally followed by
 * a ticket type (default {@code SINGLE}) and traveler category (default {@code ADULT}), e.g.
 * {@code "10d WEEKLY"} or {@code "10d SINGLE SENIOR"}. Zone is always {@code ZONE_1}. An empty
 * string converts to an empty history.
 */
public class PastPurchaseConverters {

    public static final LocalDateTime REFERENCE_PURCHASE_TIME = LocalDateTime.of(2026, 6, 15, 10, 0, 0);

    private static final Pattern OFFSET = Pattern.compile("(\\d+)d(?:(\\d+)s)?");

    @TypeConverter
    public static List<PastPurchase> parseHistory(String spec) {
        List<PastPurchase> history = new ArrayList<>();
        if (spec.isEmpty()) {
            return history;
        }
        for (String entry : spec.split(";")) {
            history.add(parseEntry(entry.trim()));
        }
        return history;
    }

    private static PastPurchase parseEntry(String entry) {
        String[] tokens = entry.split(" ");
        Matcher offset = OFFSET.matcher(tokens[0]);
        if (!offset.matches()) {
            throw new IllegalArgumentException("Invalid history entry offset: " + tokens[0]);
        }
        long days = Long.parseLong(offset.group(1));
        long seconds = offset.group(2) != null ? Long.parseLong(offset.group(2)) : 0;
        LocalDateTime purchasedAt = REFERENCE_PURCHASE_TIME.minusDays(days).minusSeconds(seconds);
        TicketType ticketType = tokens.length > 1 ? TicketType.valueOf(tokens[1]) : TicketType.SINGLE;
        TravelerCategory travelerCategory = tokens.length > 2 ? TravelerCategory.valueOf(tokens[2]) : TravelerCategory.ADULT;
        return new PastPurchase(purchasedAt, travelerCategory, ticketType, ZoneValidity.ZONE_1);
    }
}
