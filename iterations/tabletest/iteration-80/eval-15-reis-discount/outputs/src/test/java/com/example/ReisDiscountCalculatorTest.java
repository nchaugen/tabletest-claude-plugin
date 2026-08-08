package com.example;

import org.junit.jupiter.api.Test;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverterSources;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TypeConverterSources(ReisTestConverters.class)
class ReisDiscountCalculatorTest {

    @Description("""
        Ticket Number In Window is the position already established for this purchase
        by ReisTicketNumberResolver; this table only decides how traveler category
        turns that position into a discount.
        """)
    @TableTest("""
        Scenario                                               | Traveler Category | Ticket Number In Window | Discount?
        Adult follows their position on the Reis ladder        | ADULT             | 5                       | 5
        Senior follows their position on the Reis ladder too   | SENIOR            | 10                      | 10
        Child receives a flat rate regardless of ticket number | CHILD             | {1, 5, 40}              | 20
        """)
    void resolvesDiscountByTravelerCategory(TravelerCategory travelerCategory, int ticketNumberInWindow, DiscountPercentage discount) {
        var calculator = new ReisDiscountCalculator();
        assertEquals(discount, calculator.resolveDiscount(travelerCategory, ticketNumberInWindow));
    }

    @Test
    void wiresPurchaseHistoryThroughToTheFinalDiscount() {
        var purchaseTime = LocalDateTime.parse("2026-08-08T00:00:00");
        var purchaseHistory = List.of(
                new PastPurchase(LocalDateTime.parse("2026-08-01T00:00:00"), TravelerCategory.ADULT, TicketType.SINGLE, ZoneValidity.ZONE_1),
                new PastPurchase(LocalDateTime.parse("2026-08-02T00:00:00"), TravelerCategory.ADULT, TicketType.SINGLE, ZoneValidity.ZONE_1),
                new PastPurchase(LocalDateTime.parse("2026-08-03T00:00:00"), TravelerCategory.ADULT, TicketType.SINGLE, ZoneValidity.ZONE_1),
                new PastPurchase(LocalDateTime.parse("2026-08-04T00:00:00"), TravelerCategory.ADULT, TicketType.SINGLE, ZoneValidity.ZONE_1));

        var calculator = new ReisDiscountCalculator();

        assertEquals(new DiscountPercentage(5), calculator.calculateDiscount(TravelerCategory.ADULT, purchaseHistory, purchaseTime));
    }
}
