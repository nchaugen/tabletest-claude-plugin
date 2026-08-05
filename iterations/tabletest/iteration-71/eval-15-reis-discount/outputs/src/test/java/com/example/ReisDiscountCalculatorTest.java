package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReisDiscountCalculatorTest {

    private final ReisDiscountCalculator calculator = new ReisDiscountCalculator();

    @Description("""
        The ladder adds 5% for every 5 trips in the rolling 30-day window and caps at 40%.
        Both figures are fixed Reis policy rather than configurable inputs, so they show up
        as the tier boundaries in Discount %? instead of as separate columns. Trip count
        already includes the ticket being purchased now; see countsTripsTowardTheRollingReisTotal
        for how that count is derived from purchase history.
        """)
    @TableTest("""
        Scenario                                  | Trips In Last 30 Days | Discount %?
        Below the first discount tier             | 4                     | 0
        At the first discount tier                | 5                     | 5
        Last trip before the second tier          | 9                     | 5
        At the second discount tier               | 10                    | 10
        Last trip before the third tier           | 14                    | 10
        At the third discount tier                | 15                    | 15
        Last trip before the fourth tier          | 19                    | 15
        At the fourth discount tier               | 20                    | 20
        Last trip before the fifth tier           | 24                    | 20
        At the fifth discount tier                | 25                    | 25
        Last trip before the sixth tier           | 29                    | 25
        At the sixth discount tier                | 30                    | 30
        Last trip before the seventh tier         | 34                    | 30
        At the seventh discount tier              | 35                    | 35
        Last trip before the maximum tier         | 39                    | 35
        At the maximum discount                   | 40                    | 40
        Beyond the maximum, discount stays capped | 45                    | 40
        """)
    void appliesTheReisLadderFromTripsInTheWindow(int tripsInLast30Days, DiscountPercentage discount) {
        assertEquals(discount, calculator.discountForTripCount(tripsInLast30Days));
    }

    @Description("""
        Purchase Time is held constant at 2026-08-05T12:00:00 across every row; history entries
        are written as absolute timestamps that read as clear offsets from it. The window is the
        30 days up to and including the purchase time, so a trip from exactly 30 days ago still
        counts. ZoneValidity is fixed at ZONE_1 throughout because Reis applies regardless of zone.
        Only SINGLE tickets bought by an ADULT or SENIOR count toward the total; CHILD tickets are
        excluded because children get the flat discount rather than the ladder (see
        resolvesWhichDiscountRuleAppliesByTravelerCategory). The count includes the ticket being
        purchased now, which is why an empty history still yields 1.
        """)
    @TableTest("""
        Scenario                                                | Purchase Time       | Purchase History                                                                                                                                                                                                                                                                                                            | Trips At Purchase?
        No earlier trips                                        | 2026-08-05T12:00:00 | []                                                                                                                                                                                                                                                                                                                          | 1
        One qualifying trip well inside the window              | 2026-08-05T12:00:00 | [[purchasedAt: '2026-07-27T12:00:00', ticketType: SINGLE, travelerCategory: ADULT, zoneValidity: ZONE_1]]                                                                                                                                                                                                                   | 2
        Prior trip exactly at the 30-day boundary               | 2026-08-05T12:00:00 | [[purchasedAt: '2026-07-06T12:00:00', ticketType: SINGLE, travelerCategory: ADULT, zoneValidity: ZONE_1]]                                                                                                                                                                                                                   | 2
        Prior trip just past the 30-day boundary                | 2026-08-05T12:00:00 | [[purchasedAt: '2026-07-06T11:00:00', ticketType: SINGLE, travelerCategory: ADULT, zoneValidity: ZONE_1]]                                                                                                                                                                                                                   | 1
        Weekly ticket does not count toward the total           | 2026-08-05T12:00:00 | [[purchasedAt: '2026-07-26T12:00:00', ticketType: WEEKLY, travelerCategory: ADULT, zoneValidity: ZONE_1]]                                                                                                                                                                                                                   | 1
        Child ticket does not count toward the total            | 2026-08-05T12:00:00 | [[purchasedAt: '2026-07-26T12:00:00', ticketType: SINGLE, travelerCategory: CHILD, zoneValidity: ZONE_1]]                                                                                                                                                                                                                   | 1
        Senior trips count toward the same total as adult trips | 2026-08-05T12:00:00 | [[purchasedAt: '2026-07-26T12:00:00', ticketType: SINGLE, travelerCategory: SENIOR, zoneValidity: ZONE_1]]                                                                                                                                                                                                                  | 2
        Multiple qualifying trips accumulate                    | 2026-08-05T12:00:00 | [[purchasedAt: '2026-08-02T12:00:00', ticketType: SINGLE, travelerCategory: ADULT, zoneValidity: ZONE_1], [purchasedAt: '2026-07-30T12:00:00', ticketType: SINGLE, travelerCategory: ADULT, zoneValidity: ZONE_1], [purchasedAt: '2026-07-27T12:00:00', ticketType: SINGLE, travelerCategory: ADULT, zoneValidity: ZONE_1]] | 4
        """)
    void countsTripsTowardTheRollingReisTotal(LocalDateTime purchaseTime, List<PastPurchase> purchaseHistory, int tripsAtPurchase) {
        assertEquals(tripsAtPurchase, calculator.countTripsInWindow(purchaseHistory, purchaseTime));
    }

    @Description("""
        Traveler category decides which discount rule applies: CHILD always gets the flat 20% rate
        (fixed policy, not a column), while ADULT and SENIOR both accumulate the ladder from
        countTripsInWindow. The same travel history is reused for the flat and ladder rows to show
        that category, not history, is what decides which rule fires.
        """)
    @TableTest("""
        Scenario                                                                                | Traveler Category | Purchase Time       | Purchase History                                                                                                                                                                                                                                                                                                                                                                                                                     | Discount %?
        Child receives the flat discount with no travel history                                 | CHILD             | 2026-08-05T12:00:00 | []                                                                                                                                                                                                                                                                                                                                                                                                                                   | 20
        Child receives the flat discount despite a travel history that would trigger the ladder | CHILD             | 2026-08-05T12:00:00 | [[purchasedAt: '2026-08-03T12:00:00', ticketType: SINGLE, travelerCategory: ADULT, zoneValidity: ZONE_1], [purchasedAt: '2026-08-01T12:00:00', ticketType: SINGLE, travelerCategory: ADULT, zoneValidity: ZONE_1], [purchasedAt: '2026-07-30T12:00:00', ticketType: SINGLE, travelerCategory: ADULT, zoneValidity: ZONE_1], [purchasedAt: '2026-07-28T12:00:00', ticketType: SINGLE, travelerCategory: ADULT, zoneValidity: ZONE_1]] | 20
        Adult and senior discounts follow the travel-based ladder, not the flat rate            | {ADULT, SENIOR}   | 2026-08-05T12:00:00 | [[purchasedAt: '2026-08-03T12:00:00', ticketType: SINGLE, travelerCategory: ADULT, zoneValidity: ZONE_1], [purchasedAt: '2026-08-01T12:00:00', ticketType: SINGLE, travelerCategory: ADULT, zoneValidity: ZONE_1], [purchasedAt: '2026-07-30T12:00:00', ticketType: SINGLE, travelerCategory: ADULT, zoneValidity: ZONE_1], [purchasedAt: '2026-07-28T12:00:00', ticketType: SINGLE, travelerCategory: ADULT, zoneValidity: ZONE_1]] | 5
        """)
    void resolvesWhichDiscountRuleAppliesByTravelerCategory(TravelerCategory travelerCategory, LocalDateTime purchaseTime, List<PastPurchase> purchaseHistory, DiscountPercentage discount) {
        assertEquals(discount, calculator.calculateSingleTicketDiscount(travelerCategory, purchaseTime, purchaseHistory));
    }

    @TypeConverter
    public static DiscountPercentage parseDiscountPercentage(String value) {
        return new DiscountPercentage(Integer.parseInt(value));
    }

    @TypeConverter
    public static PastPurchase parsePastPurchase(Map<String, String> fields) {
        return new PastPurchase(
            LocalDateTime.parse(fields.get("purchasedAt")),
            TravelerCategory.valueOf(fields.get("travelerCategory")),
            TicketType.valueOf(fields.get("ticketType")),
            ZoneValidity.valueOf(fields.get("zoneValidity")));
    }
}
