    package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReisDiscountCalculatorTest {

    @DisplayName("Determines whether a past purchase counts toward a traveler's Reis frequency")
    @Description("""
        Only single tickets accumulate Reis frequency, and only within the trailing
        30 days before the new purchase.
        """)
    @TableTest("""
        Scenario                             | Ticket Type      | Purchased At        | Purchase Time       | Counts Toward Reis Frequency?
        Well within the window               | SINGLE           | 2026-01-20T00:00:00 | 2026-01-30T00:00:00 | true
        Exactly 30 days before purchase       | SINGLE           | 2025-12-31T00:00:00 | 2026-01-30T00:00:00 | true
        31 days before purchase               | SINGLE           | 2025-12-30T00:00:00 | 2026-01-30T00:00:00 | false
        Non-single ticket within the window   | {WEEKLY, MONTHLY}| 2026-01-20T00:00:00 | 2026-01-30T00:00:00 | false
        """)
    void countsPastPurchaseTowardReisFrequency(TicketType ticketType, LocalDateTime purchasedAt,
                                                LocalDateTime purchaseTime, boolean countsTowardReisFrequency) {
        assertEquals(countsTowardReisFrequency,
            ReisDiscountCalculator.countsTowardReisFrequency(ticketType, purchasedAt, purchaseTime));
    }

    @DisplayName("Counts the past purchases that count toward a traveler's Reis frequency")
    @Description("""
        Per-purchase eligibility (ticket type, 30-day window) is covered above; this table
        exercises aggregation across a full purchase history. Traveler category and zone are
        irrelevant to the count, so history entries use fixed placeholder values for them.
        """)
    @TableTest("""
        Scenario                                    | History                                                          | Purchase Time       | Tickets in Last 30 Days?
        No purchase history                         | []                                                                | 2026-01-30T00:00:00 | 0
        Mix of qualifying and non-qualifying tickets | [SINGLE 2026-01-20, WEEKLY 2026-01-20, SINGLE 2025-11-01]        | 2026-01-30T00:00:00 | 1
        Multiple qualifying tickets in the window    | [SINGLE 2026-01-05, SINGLE 2026-01-15, SINGLE 2026-01-25]        | 2026-01-30T00:00:00 | 3
        """)
    void countsQualifyingPurchasesInHistory(List<PastPurchase> history, LocalDateTime purchaseTime, int ticketsInLast30Days) {
        assertEquals(ticketsInLast30Days, ReisDiscountCalculator.countQualifyingTickets(history, purchaseTime));
    }

    @DisplayName("Resolves the Reis discount from traveler category and ticket count")
    @Description("""
        Children always receive a flat 20% discount regardless of travel frequency. Adults and
        seniors climb a ladder that rises 5 percentage points per 5 tickets purchased in the
        last 30 days (including the ticket now being purchased), capped at 40%.
        """)
    @TableTest("""
        Scenario                                        | Traveler Category | Tickets in Last 30 Days | Discount %?
        Child traveler, any travel frequency            | CHILD             | {1, 5, 40}               | 20
        Fewer than 5 tickets in the last 30 days         | {ADULT, SENIOR}   | {1, 4}                   | 0
        5 to 9 tickets in the last 30 days               | {ADULT, SENIOR}   | {5, 9}                   | 5
        10 to 14 tickets in the last 30 days             | {ADULT, SENIOR}   | {10, 14}                 | 10
        15 to 19 tickets in the last 30 days             | {ADULT, SENIOR}   | {15, 19}                 | 15
        20 to 24 tickets in the last 30 days             | {ADULT, SENIOR}   | {20, 24}                 | 20
        25 to 29 tickets in the last 30 days             | {ADULT, SENIOR}   | {25, 29}                 | 25
        30 to 34 tickets in the last 30 days             | {ADULT, SENIOR}   | {30, 34}                 | 30
        35 to 39 tickets in the last 30 days             | {ADULT, SENIOR}   | {35, 39}                 | 35
        40 or more tickets, discount capped at maximum   | {ADULT, SENIOR}   | {40, 45, 100}            | 40
        """)
    void resolvesReisDiscountFromTicketCount(TravelerCategory travelerCategory, int ticketsInLast30Days, int discountPercentage) {
        assertEquals(new DiscountPercentage(discountPercentage),
            ReisDiscountCalculator.discountForTicketCount(travelerCategory, ticketsInLast30Days));
    }

    @TypeConverter
    public static PastPurchase parsePastPurchase(String value) {
        String[] parts = value.split(" ", 2);
        TicketType ticketType = TicketType.valueOf(parts[0]);
        LocalDateTime purchasedAt = LocalDate.parse(parts[1]).atStartOfDay();
        return new PastPurchase(purchasedAt, TravelerCategory.ADULT, ticketType, ZoneValidity.ZONE_1);
    }
}
