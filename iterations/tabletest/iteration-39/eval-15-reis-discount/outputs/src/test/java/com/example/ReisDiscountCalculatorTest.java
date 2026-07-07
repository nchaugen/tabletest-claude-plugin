package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReisDiscountCalculatorTest {

    private static final LocalDateTime PURCHASE_TIME = LocalDateTime.parse("2026-01-31T10:00:00");

    @Description("""
        The trip ladder: every 5th single ticket in the rolling 30-day window (counting the
        ticket being purchased now) raises the discount by 5%, up to a 40% cap.
        Trips In Window is the derived count fed in directly; how it is derived from purchase
        history is covered separately.
        """)
    @TableTest("""
        Scenario                        | Trips In Window | Discount?
        No trips yet                    | 0                | 0
        Below first tier                | {1, 2, 3, 4}     | 0
        Exactly at first tier           | 5                | 5
        Within first tier               | {6, 7, 8, 9}     | 5
        Exactly at second tier          | 10               | 10
        Within second tier              | {11, 12, 13, 14} | 10
        Exactly at seventh tier         | 35               | 35
        Within seventh tier             | {36, 37, 38, 39} | 35
        Exactly at the cap              | 40               | 40
        Beyond the cap stays capped     | {41, 45, 100}    | 40
        """)
    void discountForTripCount(int tripsInWindow, int expectedDiscount) {
        assertEquals(new DiscountPercentage(expectedDiscount),
            ReisDiscountCalculator.discountForTripCount(tripsInWindow));
    }

    @Description("""
        Only SINGLE-ticket purchases within the trailing 30 days before the purchase count
        towards the trip window. Assumption: the boundary instant itself (exactly 30 days
        before purchaseTime) still counts as in-window. Zone must never affect the count.
        """)
    @TableTest("""
        Scenario                                   | History                                                                                  | Count?
        No history                                  | []                                                                                       | 0
        Only single tickets, all within window       | [1/SINGLE/ZONE_1, 15/SINGLE/ZONE_2, 29/SINGLE/ZONE_1]                                    | 3
        Weekly and monthly tickets are not counted   | [2/SINGLE/ZONE_1, 5/WEEKLY/ZONE_1, 10/MONTHLY/ZONE_2]                                    | 1
        Exactly 30 days ago is still in window       | [30/SINGLE/ZONE_1]                                                                       | 1
        31 days ago has fallen out of the window     | [31/SINGLE/ZONE_1]                                                                       | 0
        Mix of in-window and out-of-window trips     | [5/SINGLE/ZONE_1, 29/SINGLE/ZONE_2, 30/SINGLE/ZONE_3, 31/SINGLE/ZONE_1, 45/SINGLE/ZONE_2] | 3
        Zone does not affect the count               | [1/SINGLE/ZONE_1, 2/SINGLE/ZONE_2, 3/SINGLE/ZONE_3]                                      | 3
        """)
    void countSingleTicketsInWindow(List<PastPurchase> history, int expectedCount) {
        assertEquals(expectedCount,
            ReisDiscountCalculator.countSingleTicketsInWindow(history, PURCHASE_TIME));
    }

    @Description("""
        CHILD always gets a flat 20% discount, independent of travel frequency. ADULT and
        SENIOR are treated identically and follow the trip ladder (see discountForTripCount).
        """)
    @TableTest("""
        Scenario                                        | Traveler Category | Trips In Window | Discount?
        Child gets a flat discount regardless of trips   | CHILD              | {1, 5, 40}       | 20
        Adult below the first tier                       | ADULT              | 3                | 0
        Adult at a tier                                   | ADULT              | 10               | 10
        Senior follows the same ladder as adult           | SENIOR             | 10               | 10
        Adult and senior are capped the same              | {ADULT, SENIOR}    | 45               | 40
        """)
    void discountForCategoryAndTripCount(TravelerCategory travelerCategory, int tripsInWindow, int expectedDiscount) {
        assertEquals(new DiscountPercentage(expectedDiscount),
            ReisDiscountCalculator.discountForCategoryAndTripCount(travelerCategory, tripsInWindow));
    }

    @Description("""
        End-to-end wiring through the public calculateDiscount API, backed by a fake
        PurchaseHistoryRepository. All rows share the same purchaseTime. Also demonstrates
        that the discount is recomputed fresh each time from recent history rather than
        ratcheted: a drop in recent travel lowers it, more recent travel raises it.
        """)
    @TableTest("""
        Scenario                                            | Traveler Category | History                                                                                                    | Discount?
        Child ignores history entirely                       | CHILD              | [1/SINGLE/ZONE_1, 2/SINGLE/ZONE_2, 3/SINGLE/ZONE_3, 4/SINGLE/ZONE_1]                                       | 20
        New adult rider has not reached the first tier        | ADULT              | [1/SINGLE/ZONE_1, 15/SINGLE/ZONE_2]                                                                        | 0
        Adult reaches the fifth trip                          | ADULT              | [1/SINGLE/ZONE_1, 2/SINGLE/ZONE_1, 3/SINGLE/ZONE_2, 4/SINGLE/ZONE_3]                                       | 5
        Senior with the same trip pattern gets the same tier  | SENIOR             | [1/SINGLE/ZONE_1, 2/SINGLE/ZONE_1, 3/SINGLE/ZONE_2, 4/SINGLE/ZONE_3]                                       | 5
        Infrequent rider whose recent trips dropped off        | ADULT              | [1/SINGLE/ZONE_1, 35/SINGLE/ZONE_1, 40/SINGLE/ZONE_2, 45/SINGLE/ZONE_1]                                    | 0
        Frequent rider with many recent trips                  | ADULT              | [1/SINGLE/ZONE_1, 2/SINGLE/ZONE_1, 3/SINGLE/ZONE_1, 4/SINGLE/ZONE_1, 5/SINGLE/ZONE_1, 6/SINGLE/ZONE_1, 7/SINGLE/ZONE_1, 8/SINGLE/ZONE_1, 9/SINGLE/ZONE_1] | 10
        """)
    void calculatesDiscountEndToEnd(TravelerCategory travelerCategory, List<PastPurchase> history, int expectedDiscount) {
        PurchaseHistoryRepository repository = () -> history;
        ReisDiscountCalculator calculator = new ReisDiscountCalculator(repository);

        assertEquals(new DiscountPercentage(expectedDiscount),
            calculator.calculateDiscount(travelerCategory, PURCHASE_TIME));
    }

    @TypeConverter
    public static PastPurchase parsePastPurchase(String entry) {
        String[] parts = entry.split("/");
        int daysAgo = Integer.parseInt(parts[0]);
        TicketType ticketType = TicketType.valueOf(parts[1]);
        ZoneValidity zone = ZoneValidity.valueOf(parts[2]);
        return new PastPurchase(PURCHASE_TIME.minusDays(daysAgo), TravelerCategory.ADULT, ticketType, zone);
    }
}
