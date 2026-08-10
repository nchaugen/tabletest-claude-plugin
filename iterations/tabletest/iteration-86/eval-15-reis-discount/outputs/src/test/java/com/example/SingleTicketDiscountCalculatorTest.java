package com.example;

import org.junit.jupiter.api.Test;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SingleTicketDiscountCalculatorTest {

    @Description("""
        Recent Single-Ticket Count (Last 30 Days) includes the single ticket currently being
        purchased. Children receive a flat discount regardless of travel frequency. Adults and
        seniors receive the Reis ladder discount instead; only representative counts are used
        here to prove the routing -- the full tier ladder is covered in ReisDiscountLadderTest.
        """)
    @TableTest("""
        Scenario                                             | Traveler Category | Recent Single-Ticket Count (Last 30 Days) | Discount?
        Child gets the flat discount regardless of frequency | CHILD             | {0, 5, 40}                                | 20
        Adult with no recent Reis-qualifying travel          | ADULT             | 0                                         | 0
        Adult or senior reaching the first Reis tier         | {ADULT, SENIOR}   | 5                                         | 5
        """)
    void routesToTheFlatOrReisDiscountByTravelerCategory(TravelerCategory travelerCategory, int recentSingleTicketCount, DiscountPercentage discount) {
        assertEquals(discount, SingleTicketDiscountCalculator.calculateDiscount(travelerCategory, recentSingleTicketCount));
    }

    @Test
    void combinesTravelHistoryAndCategoryThroughThePublicEntryPoint() {
        LocalDateTime purchaseTime = LocalDateTime.of(2026, 8, 10, 12, 0);
        List<PastPurchase> fourSingleTicketsInTheLast30Days = List.of(
                new PastPurchase(purchaseTime.minusDays(1), TravelerCategory.ADULT, TicketType.SINGLE, ZoneValidity.ZONE_1),
                new PastPurchase(purchaseTime.minusDays(5), TravelerCategory.ADULT, TicketType.SINGLE, ZoneValidity.ZONE_2),
                new PastPurchase(purchaseTime.minusDays(10), TravelerCategory.ADULT, TicketType.SINGLE, ZoneValidity.ZONE_1),
                new PastPurchase(purchaseTime.minusDays(15), TravelerCategory.ADULT, TicketType.SINGLE, ZoneValidity.ZONE_3));

        DiscountPercentage discount = SingleTicketDiscountCalculator.calculateDiscount(
                TravelerCategory.ADULT, fourSingleTicketsInTheLast30Days, purchaseTime);

        assertEquals(new DiscountPercentage(5), discount);
    }

    @TypeConverter
    public static DiscountPercentage parseDiscountPercentage(String value) {
        return new DiscountPercentage(Integer.parseInt(value));
    }
}
