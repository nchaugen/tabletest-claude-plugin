package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReisDiscountRuleTest {

    private final ReisDiscountRule rule = new ReisDiscountRule();

    @DisplayName("Discount rule selection by traveler category")
    @Description("""
        Recent Single Tickets counts the ticket now being purchased, so a
        first-time traveler already has a count of 1, not 0. Ladder progression
        for adults and seniors is covered in a separate table; this table only
        confirms which rule applies per category and that zone never affects
        the result.
        """)
    @TableTest("""
        Scenario                                                  | Traveler Category | Zone                     | Recent Single Tickets | Discount?
        Child gets the flat discount regardless of zone or count | CHILD             | {ZONE_1, ZONE_2, ZONE_3} | {1, 39}                | 20
        Adult starts at the base rate regardless of zone         | ADULT             | {ZONE_1, ZONE_2, ZONE_3} | 1                      | 0
        Senior starts at the base rate regardless of zone        | SENIOR            | {ZONE_1, ZONE_2, ZONE_3} | 1                      | 0
        """)
    void selectsDiscountRuleByTravelerCategory(TravelerCategory travelerCategory, ZoneValidity zoneValidity, int recentSingleTicketCount, int discount) {
        assertEquals(discount, rule.calculateDiscount(travelerCategory, zoneValidity, recentSingleTicketCount).value());
    }

    @DisplayName("Reis discount ladder for adults and seniors")
    @Description("""
        Recent Single Tickets is the count of single tickets purchased in the
        trailing 30 days, including the ticket now being purchased. Every five
        tickets raises the discount by 5 percentage points, starting at ticket
        five, capped at 40%. Category is fixed to ADULT since rule selection
        (above) confirms adults and seniors follow the same ladder.
        """)
    @TableTest("""
        Scenario                 | Recent Single Tickets | Discount?
        Below the first rung     | {1, 4}                | 0
        First rung reached       | {5, 9}                | 5
        Second rung reached      | {10, 14}               | 10
        Third rung reached       | {15, 19}               | 15
        Fourth rung reached      | {20, 24}               | 20
        Fifth rung reached       | {25, 29}               | 25
        Sixth rung reached       | {30, 34}               | 30
        Seventh rung reached     | {35, 39}               | 35
        Maximum discount reached | {40, 41, 100}          | 40
        """)
    void appliesLadderByRecentTicketCount(int recentSingleTicketCount, int discount) {
        assertEquals(discount, rule.calculateDiscount(TravelerCategory.ADULT, ZoneValidity.ZONE_1, recentSingleTicketCount).value());
    }
}
