package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReisDiscountCalculatorTest {

    @Description("""
        Reis routes a new single-ticket purchase by traveler category: children always
        receive the flat 20% discount, unaffected by their purchase history, while adults
        and seniors are routed to the personal discount ladder computed from their recent
        single-ticket purchases. Zone is not a parameter of this calculation, since the
        discount applies regardless of the zones traveled. Purchase Time is fixed for every
        row; the 30-day window itself is exercised in countsOnlyPurchasesWithinTheLast30Days.
        """)
    @TableTest("""
        Scenario                             | Traveler Category | Purchase Time       | Purchase History                                                                                                                                                                                                                                                                                                                                                                                                                         | Discount?
        No purchase history                  | CHILD             | 2026-03-31T09:00:00 | []                                                                                                                                                                                                                                                                                                                                                                                                                                       | 20
        Frequent traveler, still flat        | CHILD             | 2026-03-31T09:00:00 | [[purchasedAt: "2026-03-25T10:00:00", travelerCategory: ADULT, ticketType: SINGLE, zoneValidity: ZONE_1], [purchasedAt: "2026-03-25T10:00:00", travelerCategory: ADULT, ticketType: SINGLE, zoneValidity: ZONE_1], [purchasedAt: "2026-03-25T10:00:00", travelerCategory: ADULT, ticketType: SINGLE, zoneValidity: ZONE_1], [purchasedAt: "2026-03-25T10:00:00", travelerCategory: ADULT, ticketType: SINGLE, zoneValidity: ZONE_1]]     | 20
        Adult reaches the first ladder step  | ADULT             | 2026-03-31T09:00:00 | [[purchasedAt: "2026-03-25T10:00:00", travelerCategory: ADULT, ticketType: SINGLE, zoneValidity: ZONE_1], [purchasedAt: "2026-03-25T10:00:00", travelerCategory: ADULT, ticketType: SINGLE, zoneValidity: ZONE_1], [purchasedAt: "2026-03-25T10:00:00", travelerCategory: ADULT, ticketType: SINGLE, zoneValidity: ZONE_1], [purchasedAt: "2026-03-25T10:00:00", travelerCategory: ADULT, ticketType: SINGLE, zoneValidity: ZONE_1]]     | 5
        Senior reaches the first ladder step | SENIOR            | 2026-03-31T09:00:00 | [[purchasedAt: "2026-03-25T10:00:00", travelerCategory: SENIOR, ticketType: SINGLE, zoneValidity: ZONE_1], [purchasedAt: "2026-03-25T10:00:00", travelerCategory: SENIOR, ticketType: SINGLE, zoneValidity: ZONE_1], [purchasedAt: "2026-03-25T10:00:00", travelerCategory: SENIOR, ticketType: SINGLE, zoneValidity: ZONE_1], [purchasedAt: "2026-03-25T10:00:00", travelerCategory: SENIOR, ticketType: SINGLE, zoneValidity: ZONE_1]] | 5
        """)
    void routesTheDiscountByTravelerCategory(TravelerCategory travelerCategory, LocalDateTime purchaseTime, List<PastPurchase> purchaseHistory, DiscountPercentage discount) {
        ReisDiscountCalculator calculator = new ReisDiscountCalculator();
        assertEquals(discount, calculator.calculateDiscount(travelerCategory, purchaseTime, purchaseHistory));
    }

    @Description("""
        The ladder discount steps up 5 percentage points for every 5 qualifying single
        tickets purchased in the last 30 days, up to a maximum of 40%. Recent Single Ticket
        Count is the number of prior qualifying tickets only; the ticket currently being
        purchased is the next one, which is why the first discount applies once the count
        reaches 4 (it becomes the 5th ticket).
        """)
    @TableTest("""
        Scenario                                            | Recent Single Ticket Count | Discount?
        One trip short of the first step                    | 3                          | 0
        First step reached                                  | 4                          | 5
        Last trip of the first step                         | 8                          | 5
        Second step reached                                 | 9                          | 10
        Last trip of the second step                        | 13                         | 10
        Third step reached                                  | 14                         | 15
        Last trip of the third step                         | 18                         | 15
        Fourth step reached                                 | 19                         | 20
        Last trip of the fourth step                        | 23                         | 20
        Fifth step reached                                  | 24                         | 25
        Last trip of the fifth step                         | 28                         | 25
        Sixth step reached                                  | 29                         | 30
        Last trip of the sixth step                         | 33                         | 30
        Seventh step reached                                | 34                         | 35
        Last trip of the seventh step                       | 38                         | 35
        Eighth step reached, maximum discount hit naturally | 39                         | 40
        Well past the maximum, discount stays capped        | 44                         | 40
        """)
    void computesTheDiscountFromTheRecentPurchaseCount(int recentSingleTicketCount, DiscountPercentage discount) {
        ReisDiscountCalculator calculator = new ReisDiscountCalculator();
        assertEquals(discount, calculator.discountForRecentPurchaseCount(recentSingleTicketCount));
    }

    @Description("""
        Traveler Category and purchase date are held valid (ADULT, purchased within the
        last 30 days) throughout, so only the ticket type varies.
        """)
    @TableTest("""
        Scenario                           | Traveler Category | Purchase Time       | Purchase History                                                                                                                                                                                                                                                                                                             | Recent Single Ticket Count?
        No purchases at all                | ADULT             | 2026-03-31T09:00:00 | []                                                                                                                                                                                                                                                                                                                           | 0
        Weekly and monthly tickets ignored | ADULT             | 2026-03-31T09:00:00 | [[purchasedAt: "2026-03-25T10:00:00", travelerCategory: ADULT, ticketType: SINGLE, zoneValidity: ZONE_1], [purchasedAt: "2026-03-25T10:00:00", travelerCategory: ADULT, ticketType: WEEKLY, zoneValidity: ZONE_1], [purchasedAt: "2026-03-25T10:00:00", travelerCategory: ADULT, ticketType: MONTHLY, zoneValidity: ZONE_1]] | 1
        Several single tickets are summed  | ADULT             | 2026-03-31T09:00:00 | [[purchasedAt: "2026-03-25T10:00:00", travelerCategory: ADULT, ticketType: SINGLE, zoneValidity: ZONE_1], [purchasedAt: "2026-03-25T10:00:00", travelerCategory: ADULT, ticketType: SINGLE, zoneValidity: ZONE_1], [purchasedAt: "2026-03-25T10:00:00", travelerCategory: ADULT, ticketType: WEEKLY, zoneValidity: ZONE_1]]  | 2
        """)
    void countsOnlySingleTicketPurchases(TravelerCategory travelerCategory, LocalDateTime purchaseTime, List<PastPurchase> purchaseHistory, Integer recentSingleTicketCount) {
        ReisDiscountCalculator calculator = new ReisDiscountCalculator();
        assertEquals(recentSingleTicketCount, calculator.countRecentSingleTicketPurchases(travelerCategory, purchaseTime, purchaseHistory));
    }

    @Description("""
        Ticket type and purchase date are held valid (SINGLE, purchased within the last 30
        days) throughout, so only the traveler category varies. Assumption: since the
        available purchase history carries no traveler or account identifier, a past
        purchase counts toward the ladder only when its traveler category matches the new
        purchase's traveler category; purchases made for a child, or for the other of
        adult/senior, never count.
        """)
    @TableTest("""
        Scenario                            | Traveler Category | Purchase Time       | Purchase History                                                                                                                                                                                                                                                                                                                                                                                                                      | Recent Single Ticket Count?
        Adult counts only adult purchases   | ADULT             | 2026-03-31T09:00:00 | [[purchasedAt: "2026-03-25T10:00:00", travelerCategory: ADULT, ticketType: SINGLE, zoneValidity: ZONE_1], [purchasedAt: "2026-03-25T10:00:00", travelerCategory: ADULT, ticketType: SINGLE, zoneValidity: ZONE_1], [purchasedAt: "2026-03-25T10:00:00", travelerCategory: SENIOR, ticketType: SINGLE, zoneValidity: ZONE_1], [purchasedAt: "2026-03-25T10:00:00", travelerCategory: CHILD, ticketType: SINGLE, zoneValidity: ZONE_1]] | 2
        Senior counts only senior purchases | SENIOR            | 2026-03-31T09:00:00 | [[purchasedAt: "2026-03-25T10:00:00", travelerCategory: ADULT, ticketType: SINGLE, zoneValidity: ZONE_1], [purchasedAt: "2026-03-25T10:00:00", travelerCategory: ADULT, ticketType: SINGLE, zoneValidity: ZONE_1], [purchasedAt: "2026-03-25T10:00:00", travelerCategory: SENIOR, ticketType: SINGLE, zoneValidity: ZONE_1], [purchasedAt: "2026-03-25T10:00:00", travelerCategory: CHILD, ticketType: SINGLE, zoneValidity: ZONE_1]] | 1
        """)
    void countsOnlyPurchasesByTheSameTravelerCategory(TravelerCategory travelerCategory, LocalDateTime purchaseTime, List<PastPurchase> purchaseHistory, Integer recentSingleTicketCount) {
        ReisDiscountCalculator calculator = new ReisDiscountCalculator();
        assertEquals(recentSingleTicketCount, calculator.countRecentSingleTicketPurchases(travelerCategory, purchaseTime, purchaseHistory));
    }

    @Description("""
        Traveler category and ticket type are held valid (ADULT, SINGLE) throughout, so only
        the purchase date relative to a fixed Purchase Time varies. Assumption: "the last 30
        days" is a closed window, so a purchase made exactly 30 days before Purchase Time
        still counts, and only a purchase older than that is excluded.
        """)
    @TableTest("""
        Scenario                                  | Traveler Category | Purchase Time       | Purchase History                                                                                                                                                                                                   | Recent Single Ticket Count?
        Purchase from earlier the same day counts | ADULT             | 2026-03-31T09:00:00 | [[purchasedAt: "2026-03-31T08:00:00", travelerCategory: ADULT, ticketType: SINGLE, zoneValidity: ZONE_1]]                                                                                                          | 1
        Purchase exactly 30 days before counts    | ADULT             | 2026-03-31T09:00:00 | [[purchasedAt: "2026-03-01T09:00:00", travelerCategory: ADULT, ticketType: SINGLE, zoneValidity: ZONE_1]]                                                                                                          | 1
        Purchase just past 30 days is excluded    | ADULT             | 2026-03-31T09:00:00 | [[purchasedAt: "2026-03-01T08:59:59", travelerCategory: ADULT, ticketType: SINGLE, zoneValidity: ZONE_1]]                                                                                                          | 0
        Recent and stale purchases combined       | ADULT             | 2026-03-31T09:00:00 | [[purchasedAt: "2026-03-25T10:00:00", travelerCategory: ADULT, ticketType: SINGLE, zoneValidity: ZONE_1], [purchasedAt: "2026-01-15T10:00:00", travelerCategory: ADULT, ticketType: SINGLE, zoneValidity: ZONE_1]] | 1
        """)
    void countsOnlyPurchasesWithinTheLast30Days(TravelerCategory travelerCategory, LocalDateTime purchaseTime, List<PastPurchase> purchaseHistory, Integer recentSingleTicketCount) {
        ReisDiscountCalculator calculator = new ReisDiscountCalculator();
        assertEquals(recentSingleTicketCount, calculator.countRecentSingleTicketPurchases(travelerCategory, purchaseTime, purchaseHistory));
    }

    @Description("""
        Traveler category, ticket type and purchase date are held valid (ADULT, SINGLE,
        purchased within the last 30 days) throughout, so only the zone traveled varies -
        Reis applies regardless of the zones traveled.
        """)
    @TableTest("""
        Scenario                    | Traveler Category | Purchase Time       | Purchase History                                                                                          | Recent Single Ticket Count?
        Travel in a single zone     | ADULT             | 2026-03-31T09:00:00 | [[purchasedAt: "2026-03-25T10:00:00", travelerCategory: ADULT, ticketType: SINGLE, zoneValidity: ZONE_1]] | 1
        Travel across several zones | ADULT             | 2026-03-31T09:00:00 | [[purchasedAt: "2026-03-25T10:00:00", travelerCategory: ADULT, ticketType: SINGLE, zoneValidity: ZONE_3]] | 1
        """)
    void countsPurchasesRegardlessOfZone(TravelerCategory travelerCategory, LocalDateTime purchaseTime, List<PastPurchase> purchaseHistory, Integer recentSingleTicketCount) {
        ReisDiscountCalculator calculator = new ReisDiscountCalculator();
        assertEquals(recentSingleTicketCount, calculator.countRecentSingleTicketPurchases(travelerCategory, purchaseTime, purchaseHistory));
    }

    @TypeConverter
    public static PastPurchase parsePastPurchase(Map<String, String> fields) {
        return new PastPurchase(
            LocalDateTime.parse(fields.get("purchasedAt")),
            TravelerCategory.valueOf(fields.get("travelerCategory")),
            TicketType.valueOf(fields.get("ticketType")),
            ZoneValidity.valueOf(fields.get("zoneValidity")));
    }

    @TypeConverter
    public static DiscountPercentage parseDiscountPercentage(String value) {
        return new DiscountPercentage(Integer.valueOf(value));
    }
}
