package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReisDiscountCalculatorTest {

    @Description("""
        The Reis ladder gives 0% below ticket five, then +5% every five tickets, capped at 40%.
        Ticket Number is this purchase's ordinal position among single tickets bought in the
        trailing 30-day window (this purchase included).
        """)
    @TableTest("""
        Scenario                                 | Ticket Number        | Discount?
        Below the first tier                     | {1, 2, 3, 4}         | 0
        First tier reached at ticket five         | {5, 6, 7, 8, 9}      | 5
        Second tier                               | {10, 11, 12, 13, 14} | 10
        Third tier                                | {15, 16, 17, 18, 19} | 15
        Fourth tier                               | {20, 21, 22, 23, 24} | 20
        Fifth tier                                | {25, 26, 27, 28, 29} | 25
        Sixth tier                                | {30, 31, 32, 33, 34} | 30
        Seventh tier                              | {35, 36, 37, 38, 39} | 35
        Eighth tier reaches the maximum discount  | {40, 41, 42, 43, 44} | 40
        Discount never exceeds the maximum        | {45, 60, 200}        | 40
        """)
    void discountLadderByTicketNumber(int ticketNumber, int discount) {
        assertEquals(discount, ReisDiscountCalculator.discountForTicketNumber(ticketNumber).value());
    }

    @Description("""
        Only SINGLE tickets count toward Reis, and only those purchased within the 30 days
        up to and including the purchase time. The count returned does not include the
        purchase currently being made. Traveler category and zone are irrelevant to counting,
        so history entries default to ADULT / ZONE_1.
        """)
    @TableTest("""
        Scenario                                             | History                                                   | Purchase Time | Prior Single Tickets?
        No purchase history                                  | []                                                        | 2024-07-31    | 0
        Single ticket exactly 30 days before counts           | [2024-07-01 SINGLE]                                      | 2024-07-31    | 1
        Single ticket 31 days before is outside the window    | [2024-06-30 SINGLE]                                      | 2024-07-31    | 0
        Weekly and monthly tickets are not counted            | [2024-07-30 WEEKLY, 2024-07-29 MONTHLY]                  | 2024-07-31    | 0
        Only single tickets count among mixed history         | [2024-07-20 SINGLE, 2024-07-21 WEEKLY, 2024-07-23 SINGLE] | 2024-07-31   | 2
        Multiple single tickets within the window accumulate  | [2024-07-01 SINGLE, 2024-07-15 SINGLE, 2024-07-30 SINGLE] | 2024-07-31   | 3
        """)
    void countsRecentSingleTicketsWithinThirtyDayWindow(List<PastPurchase> history, LocalDateTime purchaseTime, int priorSingleTickets) {
        assertEquals(priorSingleTickets, ReisDiscountCalculator.countRecentSingleTickets(history, purchaseTime));
    }

    @Description("""
        Children always receive the flat 20% discount regardless of travel history. Adults and
        seniors instead follow the Reis ladder (see discountLadderByTicketNumber for tier
        boundaries); this table only proves that both categories delegate to the same ladder
        and that the prior-ticket count is correctly offset to a ticket number.
        """)
    @TableTest("""
        Scenario                                     | Traveler Category | Prior Single Tickets | Discount?
        Children always get the flat discount        | CHILD             | {0, 4, 9, 44}         | 20
        Adults and seniors follow the discount ladder | {ADULT, SENIOR}   | 4                     | 5
        """)
    void dispatchesDiscountByTravelerCategory(TravelerCategory travelerCategory, int priorSingleTickets, int discount) {
        assertEquals(discount, ReisDiscountCalculator.calculateDiscount(travelerCategory, priorSingleTickets).value());
    }

    @org.junit.jupiter.api.Test
    void calculatesDiscountFromInjectedPurchaseHistory() {
        LocalDateTime purchaseTime = LocalDate.of(2024, 7, 31).atStartOfDay();
        PurchaseHistoryRepository repository = () -> List.of(
                new PastPurchase(LocalDate.of(2024, 7, 1).atStartOfDay(), TravelerCategory.ADULT, TicketType.SINGLE, ZoneValidity.ZONE_1),
                new PastPurchase(LocalDate.of(2024, 7, 15).atStartOfDay(), TravelerCategory.ADULT, TicketType.SINGLE, ZoneValidity.ZONE_2),
                new PastPurchase(LocalDate.of(2024, 7, 20).atStartOfDay(), TravelerCategory.ADULT, TicketType.WEEKLY, ZoneValidity.ZONE_1),
                new PastPurchase(LocalDate.of(2024, 7, 30).atStartOfDay(), TravelerCategory.ADULT, TicketType.SINGLE, ZoneValidity.ZONE_1));
        ReisDiscountCalculator calculator = new ReisDiscountCalculator(repository);

        DiscountPercentage discount = calculator.calculateDiscount(TravelerCategory.ADULT, purchaseTime);

        // 3 prior single tickets in the window -> ticket number 4 -> below the first tier.
        assertEquals(0, discount.value());
    }

    @TypeConverter
    public static LocalDateTime parsePurchaseTime(String value) {
        return LocalDate.parse(value).atStartOfDay();
    }

    @TypeConverter
    public static PastPurchase parsePastPurchase(String value) {
        String[] parts = value.split(" ");
        LocalDateTime purchasedAt = LocalDate.parse(parts[0]).atStartOfDay();
        TicketType ticketType = TicketType.valueOf(parts[1]);
        return new PastPurchase(purchasedAt, TravelerCategory.ADULT, ticketType, ZoneValidity.ZONE_1);
    }
}
