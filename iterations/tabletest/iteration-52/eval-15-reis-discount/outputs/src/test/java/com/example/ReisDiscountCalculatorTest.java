package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverterSources;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TypeConverterSources(PastPurchaseConverters.class)
public class ReisDiscountCalculatorTest {

    private final DiscountCalculator calculator = new ReisDiscountCalculator();

    @Description("""
        Purchase time is fixed at 2026-06-15T10:00:00 for every row (see PastPurchaseConverters
        for the history notation). Zone and traveler category here describe the ticket being
        purchased now, not any past purchase. The exact discount ladder is specified in
        DiscountLadderTest; these rows only need to show that a child always gets the flat rate
        and that an adult or senior is routed to the ladder regardless of zone.
        """)
    @TableTest("""
        Scenario                                          | Traveler Category | Zone                     | History                             | Discount %?
        Child gets a flat discount regardless of history  | CHILD             | {ZONE_1, ZONE_2, ZONE_3} | {'', 5d WEEKLY;10d;15d;20d;25d}     | 20
        Adult or senior discount follows the ticket ladder| {ADULT, SENIOR}   | {ZONE_1, ZONE_2, ZONE_3} | 5d;10d;15d;20d                      | 5
        Adult or senior discount grows with more tickets  | {ADULT, SENIOR}   | ZONE_1                   | 1d;4d;7d;10d;13d;16d;19d;22d;25d    | 10
        """)
    void calculatesTheSingleTicketDiscount(TravelerCategory travelerCategory, ZoneValidity zone,
                                            List<PastPurchase> history, int discountPercent) {
        DiscountPercentage result = calculator.calculateDiscount(
                travelerCategory, zone, PastPurchaseConverters.REFERENCE_PURCHASE_TIME, history);

        assertEquals(discountPercent, result.value().intValue());
    }
}
