package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReisDiscountCalculatorTest {

    private static final LocalDateTime FIXED_PURCHASE_TIME = LocalDateTime.parse("2026-08-10T12:00:00");

    private final ReisDiscountCalculator calculator = new ReisDiscountCalculator();

    @Description("""
        Purchase time is fixed at 2026-08-10T12:00:00 for every row; each history entry is written
        as the number of days before that instant. The 30-day window is inclusive: a purchase
        exactly 30 days before purchase time still counts. Zone of past purchases is fixed to
        ZONE_1 since it does not affect eligibility; zone-independence of the ticket being
        purchased is exercised separately, in calculatesTheDiscountForASinglePurchase below.
        Assumption: adult and senior purchases share one personal ladder, since the discount
        belongs to the traveller rather than to the category label.
        """)
    @TableTest("""
        Scenario                                                     | Purchase History                                                                                                                                    | Purchase Time       | Tickets In Last 30 Days (Incl. This One)?
        No prior purchases                                           | []                                                                                                                                                  | 2026-08-10T12:00:00 | 1
        Weekly ticket does not count toward the single-ticket ladder | [[days: 5, category: ADULT, ticketType: WEEKLY]]                                                                                                    | 2026-08-10T12:00:00 | 1
        Child ticket does not count toward the personal ladder       | [[days: 5, category: CHILD, ticketType: SINGLE]]                                                                                                    | 2026-08-10T12:00:00 | 1
        Senior ticket counts the same as an adult ticket             | [[days: 5, category: SENIOR, ticketType: SINGLE]]                                                                                                   | 2026-08-10T12:00:00 | 2
        Purchase exactly 30 days ago is still in the window          | [[days: 30, category: ADULT, ticketType: SINGLE]]                                                                                                   | 2026-08-10T12:00:00 | 2
        Purchase 31 days ago has fallen out of the window            | [[days: 31, category: ADULT, ticketType: SINGLE]]                                                                                                   | 2026-08-10T12:00:00 | 1
        Several eligible purchases within the window accumulate      | [[days: 1, category: ADULT, ticketType: SINGLE], [days: 10, category: SENIOR, ticketType: SINGLE], [days: 29, category: ADULT, ticketType: SINGLE]] | 2026-08-10T12:00:00 | 4
        """)
    void countsTicketsPurchasedInTheTrailing30DayWindow(
            List<PastPurchase> purchaseHistory, LocalDateTime purchaseTime, int ticketCount) {
        assertEquals(ticketCount, calculator.ticketCountIncludingThisPurchase(purchaseHistory, purchaseTime));
    }

    @TableTest("""
        Scenario                                                 | Tickets In Last 30 Days (Incl. This One) | Max Discount % (Policy) | Discount %?
        Tickets 1 to 4                                           | {1, 4}                                   | 40                      | 0
        Tickets 5 to 9                                           | {5, 9}                                   | 40                      | 5
        Tickets 10 to 14                                         | {10, 14}                                 | 40                      | 10
        Tickets 15 to 19                                         | {15, 19}                                 | 40                      | 15
        Tickets 20 to 24                                         | {20, 24}                                 | 40                      | 20
        Tickets 25 to 29                                         | {25, 29}                                 | 40                      | 25
        Tickets 30 to 34                                         | {30, 34}                                 | 40                      | 30
        Tickets 35 to 39                                         | {35, 39}                                 | 40                      | 35
        Tickets 40 to 44, ladder value meets the cap             | {40, 44}                                 | 40                      | 40
        Tickets 45 and beyond, ladder value would exceed the cap | {45, 500}                                | 40                      | 40
        """)
    void looksUpTheDiscountForTicketCount(int ticketCount, int maxDiscountPercent, int discountPercent) {
        assertEquals(discountPercent, calculator.discountForTicketCount(ticketCount).value());
    }

    @Description("""
        Traveler category is fixed to CHILD throughout: children receive the flat discount
        regardless of travel history. Zone of the ticket being purchased is fixed to ZONE_1, since
        zone-independence is exercised in calculatesTheDiscountForASinglePurchase below.
        """)
    @TableTest("""
        Scenario                                                              | Purchase History                                                                                                                                                                                                                                 | Purchase Time       | Discount %?
        No purchase history                                                   | []                                                                                                                                                                                                                                               | 2026-08-10T12:00:00 | 20
        Frequent-traveller history that would raise an adult to a higher tier | [[days: 1, category: ADULT, ticketType: SINGLE], [days: 2, category: ADULT, ticketType: SINGLE], [days: 3, category: ADULT, ticketType: SINGLE], [days: 4, category: ADULT, ticketType: SINGLE], [days: 5, category: ADULT, ticketType: SINGLE]] | 2026-08-10T12:00:00 | 20
        """)
    void appliesTheFlatDiscountForChildren(List<PastPurchase> purchaseHistory, LocalDateTime purchaseTime, int discountPercent) {
        DiscountPercentage discount =
                calculator.calculateDiscount(TravelerCategory.CHILD, ZoneValidity.ZONE_1, purchaseTime, purchaseHistory);
        assertEquals(discountPercent, discount.value());
    }

    @Description("""
        Exercises the public entry point end to end: routes between the flat child discount and
        the personal ladder based on traveler category, and confirms the zone of the ticket being
        purchased never changes the outcome. Reuses the fixed purchase instant and day-offset
        notation from countsTicketsPurchasedInTheTrailing30DayWindow above.
        """)
    @TableTest("""
        Scenario                                                 | Traveler Category | Zone                     | Purchase History                                                                                                                                                                                    | Purchase Time       | Discount %?
        Child ignores the zone of the ticket being purchased     | CHILD             | {ZONE_1, ZONE_2, ZONE_3} | []                                                                                                                                                                                                  | 2026-08-10T12:00:00 | 20
        Adult reaches the ladder's first discount tier, any zone | ADULT             | {ZONE_1, ZONE_2, ZONE_3} | [[days: 1, category: ADULT, ticketType: SINGLE], [days: 5, category: ADULT, ticketType: SINGLE], [days: 10, category: ADULT, ticketType: SINGLE], [days: 20, category: SENIOR, ticketType: SINGLE]] | 2026-08-10T12:00:00 | 5
        Senior also accumulates the personal ladder discount     | SENIOR            | {ZONE_1, ZONE_2, ZONE_3} | [[days: 1, category: ADULT, ticketType: SINGLE], [days: 5, category: ADULT, ticketType: SINGLE], [days: 10, category: ADULT, ticketType: SINGLE], [days: 20, category: SENIOR, ticketType: SINGLE]] | 2026-08-10T12:00:00 | 5
        """)
    void calculatesTheDiscountForASinglePurchase(TravelerCategory travelerCategory, ZoneValidity zone,
            List<PastPurchase> purchaseHistory, LocalDateTime purchaseTime, int discountPercent) {
        DiscountPercentage discount = calculator.calculateDiscount(travelerCategory, zone, purchaseTime, purchaseHistory);
        assertEquals(discountPercent, discount.value());
    }

    @TypeConverter
    public static PastPurchase toPastPurchase(Map<String, String> fields) {
        LocalDateTime purchasedAt = FIXED_PURCHASE_TIME.minusDays(Long.parseLong(fields.get("days")));
        TravelerCategory category = TravelerCategory.valueOf(fields.get("category"));
        TicketType ticketType = TicketType.valueOf(fields.get("ticketType"));
        return new PastPurchase(purchasedAt, category, ticketType, ZoneValidity.ZONE_1);
    }
}
