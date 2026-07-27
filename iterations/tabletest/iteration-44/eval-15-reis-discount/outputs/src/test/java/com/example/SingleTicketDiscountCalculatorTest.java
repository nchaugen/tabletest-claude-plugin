package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SingleTicketDiscountCalculatorTest {

    private static final LocalDateTime PURCHASE_TIME = LocalDateTime.of(2026, 6, 30, 12, 0);

    private final SingleTicketDiscountCalculator calculator = new SingleTicketDiscountCalculator();

    @DisplayName("Single-ticket discount by traveler category and purchase number")
    @Description("""
        Purchase Number is this traveler's count of single-ticket purchases in the trailing 30
        days, including the ticket being purchased now (see determinesPurchaseNumber for how
        this number is derived from purchase history).

        Assumption: "ticket number five" and "every fifth trip" from the brief are interpreted
        as steps of that Purchase Number, with ADULT and SENIOR following an identical ladder.
        Open question: the brief does not say whether a traveler can drop below a tier already
        reached earlier in the current 30-day window before it fully rolls over. This table
        assumes the discount is recalculated fresh from the current Purchase Number on every
        purchase, with no memory of a previously higher tier.
        """)
    @TableTest("""
        Scenario                                | Traveler Category | Purchase Number      | Discount?
        Child traveler, any purchase number      | CHILD              | {1, 5, 40}           | 20
        Below the first discount tier            | {ADULT, SENIOR}    | {1, 2, 3, 4}         | 0
        First discount tier                      | {ADULT, SENIOR}    | {5, 6, 7, 8, 9}      | 5
        Second discount tier                     | {ADULT, SENIOR}    | {10, 11, 12, 13, 14} | 10
        Third discount tier                       | {ADULT, SENIOR}    | {15, 16, 17, 18, 19} | 15
        Fourth discount tier                     | {ADULT, SENIOR}    | {20, 21, 22, 23, 24} | 20
        Fifth discount tier                      | {ADULT, SENIOR}    | {25, 26, 27, 28, 29} | 25
        Sixth discount tier                      | {ADULT, SENIOR}    | {30, 31, 32, 33, 34} | 30
        Seventh discount tier                    | {ADULT, SENIOR}    | {35, 36, 37, 38, 39} | 35
        Maximum discount, reached and capped     | {ADULT, SENIOR}    | {40, 45, 100}        | 40
        """)
    void singleTicketDiscount(TravelerCategory travelerCategory, int purchaseNumber, DiscountPercentage discount) {
        assertEquals(discount, calculator.singleTicketDiscount(travelerCategory, purchaseNumber));
    }

    @DisplayName("Purchase number derived from recent single-ticket history")
    @Description("""
        Purchase time is fixed at 2026-06-30T12:00 for every row; each history entry encodes
        how long before that moment the past purchase happened, e.g. "29d" is 29 days before
        purchase time. Only SINGLE tickets purchased within the trailing 30 days count toward
        the purchase number; other ticket types and older purchases are excluded.

        Open question: the brief describes the count as "single tickets you have purchased",
        without saying whether a past purchase made under a different traveler category (e.g.
        bought as a child, now traveling as an adult) should still count. This table assumes it
        does, which is why traveler category on past purchases is shown not to affect the count.
        """)
    @TableTest("""
        Scenario                                                       | History                                                                                                              | Purchase Number?
        No purchase history                                            | []                                                                                                                   | 1
        Several qualifying single tickets in the window                | [29d SINGLE ADULT ZONE_1, 20d SINGLE ADULT ZONE_1, 10d SINGLE ADULT ZONE_1, 1d SINGLE ADULT ZONE_1]                | 5
        Non-single tickets are not counted                             | [5d WEEKLY ADULT ZONE_1, 5d MONTHLY ADULT ZONE_1, 5d SINGLE ADULT ZONE_1]                                            | 2
        Purchase exactly 30 days ago still counts                      | [30d SINGLE ADULT ZONE_1]                                                                                           | 2
        Purchase just past the 30-day window is excluded               | [31d SINGLE ADULT ZONE_1]                                                                                           | 1
        Zone travelled does not affect the count                       | [5d SINGLE ADULT ZONE_1, 5d SINGLE ADULT ZONE_2, 5d SINGLE ADULT ZONE_3]                                            | 4
        Traveler category on past purchases does not affect the count  | [5d SINGLE CHILD ZONE_1, 5d SINGLE SENIOR ZONE_1]                                                                   | 3
        """)
    void determinesPurchaseNumber(List<PastPurchase> history, int purchaseNumber) {
        assertEquals(purchaseNumber, calculator.determinePurchaseNumber(history, PURCHASE_TIME));
    }

    @TypeConverter
    public static DiscountPercentage parseDiscountPercentage(String value) {
        return new DiscountPercentage(Integer.parseInt(value));
    }

    @TypeConverter
    public static PastPurchase parsePastPurchase(String value) {
        String[] parts = value.split(" ");
        int daysAgo = Integer.parseInt(parts[0].substring(0, parts[0].length() - 1));
        TicketType ticketType = TicketType.valueOf(parts[1]);
        TravelerCategory travelerCategory = TravelerCategory.valueOf(parts[2]);
        ZoneValidity zoneValidity = ZoneValidity.valueOf(parts[3]);
        return new PastPurchase(PURCHASE_TIME.minusDays(daysAgo), travelerCategory, ticketType, zoneValidity);
    }
}
