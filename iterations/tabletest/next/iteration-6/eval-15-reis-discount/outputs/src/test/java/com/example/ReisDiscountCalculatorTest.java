package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverterSources;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TypeConverterSources(PastPurchaseFixtures.class)
public class ReisDiscountCalculatorTest {

    private final ReisDiscountCalculator calculator = new ReisDiscountCalculator();

    @Description("""
        Establishes precedence only — the ladder's own tiers are covered by
        ReisDiscountLadderTest and the trailing-window count by
        SingleTicketFrequencyCounterTest. The 9-single-ticket history in the child
        row would itself be worth a 10% ladder discount for an adult, chosen to prove
        a child gets the flat 20% instead, not the ladder result.
        """)
    @TableTest("""
        Scenario                                                      | Category      | Zone                          | History                                                          | Discount?
        Child gets the flat discount regardless of history and zone   | CHILD         | {ZONE_1, ZONE_2, ZONE_3}      | {[], [1-SINGLE, 2-SINGLE, 3-SINGLE, 4-SINGLE, 5-SINGLE, 6-SINGLE, 7-SINGLE, 8-SINGLE, 9-SINGLE]} | 20
        Adult or senior discount follows the ladder regardless of zone | {ADULT, SENIOR} | {ZONE_1, ZONE_2, ZONE_3}    | [1-SINGLE, 2-SINGLE, 3-SINGLE, 4-SINGLE]                          | 5
        """)
    void routesSingleTicketDiscountByTravelerCategory(TravelerCategory category, ZoneValidity zone, List<PastPurchase> history, int discount) {
        assertEquals(discount, calculator.calculateDiscount(category, zone, history, PastPurchaseFixtures.NOW).value());
    }
}
