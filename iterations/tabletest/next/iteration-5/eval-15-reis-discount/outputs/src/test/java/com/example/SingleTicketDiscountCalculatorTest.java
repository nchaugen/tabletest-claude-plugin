package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SingleTicketDiscountCalculatorTest {

    private final SingleTicketDiscountCalculator calculator = new SingleTicketDiscountCalculator();

    @Description("""
        Adults and seniors are treated identically: their discount follows the Reis
        ladder (see ReisDiscountLadderTest for the full tier boundaries) regardless
        of which zones they travel in. Children instead always get a flat 20%
        discount, regardless of their travel history or zone.
        """)
    @TableTest("""
        Scenario                                                  | Traveler Category | Prior Qualifying Tickets | Zone                            | Discount %?
        Adult or senior below the first threshold, any zone      | {ADULT, SENIOR}    | 3                        | {ZONE_1, ZONE_2, ZONE_3}         | 0
        Adult or senior reaches the first discount tier, any zone | {ADULT, SENIOR}    | 4                        | {ZONE_1, ZONE_2, ZONE_3}         | 5
        Adult or senior at the maximum discount, any zone         | {ADULT, SENIOR}    | 39                       | {ZONE_1, ZONE_2, ZONE_3}         | 40
        Child gets a flat discount regardless of history and zone | CHILD              | {0, 20, 100}             | {ZONE_1, ZONE_2, ZONE_3}         | 20
        """)
    void calculatesDiscountByTravelerCategory(TravelerCategory category, int priorQualifyingTickets,
                                               ZoneValidity zone, int discountPercentage) {
        assertEquals(discountPercentage, calculator.calculateDiscount(category, zone, priorQualifyingTickets).value());
    }
}
