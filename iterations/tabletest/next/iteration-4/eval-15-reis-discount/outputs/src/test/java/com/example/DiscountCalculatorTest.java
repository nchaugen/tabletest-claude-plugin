package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverterSources;

@TypeConverterSources(ReisTestConverters.class)
public class DiscountCalculatorTest {

    private final DiscountCalculator calculator = new ReisDiscountCalculator();

    @Description("""
        Purchase time is always 2024-03-31T12:00 (ReisTestConverters.REFERENCE_PURCHASE_TIME).
        History uses the "<TicketType>@<days before purchase>" notation from
        ReisTravelCountTest. Zone is varied with a value set to show it never changes the
        result, for children and for adults/seniors alike. The travel-count-to-discount
        ladder itself is covered by ReisDiscountLadderTest; here the count only needs to
        reach the first rung (4 prior single tickets + the one being bought = 5) to show
        that the current purchase counts toward its own discount.
        """)
    @TableTest("""
        Scenario                                           | Traveler Category | Zone                     | History                                   | Discount?
        Child gets the flat discount, any zone              | CHILD             | {ZONE_1, ZONE_2, ZONE_3} | []                                        | 20
        Child discount ignores travel history                | CHILD             | ZONE_1                   | [SINGLE@1, SINGLE@2, SINGLE@3, SINGLE@4] | 20
        Adult with no recent travel gets no discount yet    | ADULT             | ZONE_1                   | []                                        | 0
        Adult reaches the first Reis rung, any zone          | ADULT             | {ZONE_1, ZONE_2, ZONE_3} | [SINGLE@1, SINGLE@2, SINGLE@3, SINGLE@4] | 5
        Senior reaches the first Reis rung, any zone         | SENIOR            | {ZONE_1, ZONE_2, ZONE_3} | [SINGLE@1, SINGLE@2, SINGLE@3, SINGLE@4] | 5
        """)
    void calculatesDiscountForPurchase(TravelerCategory travelerCategory, ZoneValidity zone,
            List<PastPurchase> history, DiscountPercentage discount) {
        assertEquals(discount,
                calculator.calculateDiscount(travelerCategory, zone, history, ReisTestConverters.REFERENCE_PURCHASE_TIME));
    }
}
