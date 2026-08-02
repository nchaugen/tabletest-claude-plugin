package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverterSources;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TypeConverterSources(TestConverters.class)
public class ReisDiscountCalculatorTest {

    private final DiscountCalculator calculator = new ReisDiscountCalculator();

    @Description("""
        Discount for a new single-ticket purchase. CHILD always receives the flat 20%
        discount, independent of travel history and zone. ADULT and SENIOR receive the
        Reis ladder discount (see ReisDiscountLadderTest and SingleTicketTripCounterTest
        for its tier and window rules), which is likewise independent of the zone traveled.
        """)
    @TableTest("""
        Scenario                                              | Traveler Category | Zone                     | Purchase Time       | Past Purchases                                                                                                                                                                    | Discount %?
        Child gets the flat discount regardless of zone       | CHILD              | {ZONE_1, ZONE_2, ZONE_3} | 2026-08-02T08:00:00 | []                                                                                                                                                                                | 20
        Child flat discount ignores travel frequency          | CHILD              | ZONE_1                   | 2026-08-02T08:00:00 | [[purchasedAt: '2026-07-05T08:00:00', ticketType: SINGLE], [purchasedAt: '2026-07-12T08:00:00', ticketType: SINGLE], [purchasedAt: '2026-07-19T08:00:00', ticketType: SINGLE], [purchasedAt: '2026-07-26T08:00:00', ticketType: SINGLE]] | 20
        Adult or senior with no recent trips gets no discount | {ADULT, SENIOR}    | {ZONE_1, ZONE_2, ZONE_3} | 2026-08-02T08:00:00 | []                                                                                                                                                                                | 0
        Adult or senior reaching the first discount tier      | {ADULT, SENIOR}    | ZONE_2                   | 2026-08-02T08:00:00 | [[purchasedAt: '2026-07-05T08:00:00', ticketType: SINGLE], [purchasedAt: '2026-07-12T08:00:00', ticketType: SINGLE], [purchasedAt: '2026-07-19T08:00:00', ticketType: SINGLE], [purchasedAt: '2026-07-26T08:00:00', ticketType: SINGLE]] | 5
        """)
    void calculatesDiscountForANewSingleTicket(TravelerCategory travelerCategory, ZoneValidity zone, LocalDateTime purchaseTime, List<PastPurchase> pastPurchases, DiscountPercentage discount) {
        assertEquals(discount, calculator.calculateDiscount(travelerCategory, zone, purchaseTime, pastPurchases));
    }
}
