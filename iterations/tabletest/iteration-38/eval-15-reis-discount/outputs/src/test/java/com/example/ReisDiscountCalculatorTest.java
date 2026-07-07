package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverterSources;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TypeConverterSources(PastPurchaseConverters.class)
public class ReisDiscountCalculatorTest {

    @DisplayName("Reis discount for a new single ticket purchase")
    @Description("""
        Children always get a flat 20% discount, regardless of travel history.
        Adults and seniors get the Reis ladder discount, which combines the
        qualifying ticket count in the trailing 30-day window (ReisWindow) with
        the ladder tier lookup (ReisLadder) - the ticket being purchased counts
        as the next ticket in that window. The discount does not depend on the
        zone of the ticket being purchased, so zone is not a parameter here.
        """)
    @TableTest("""
        Scenario                                        | Traveler Category | Past Purchases                                                                                                                                                                                                                                       | Purchase Time     | Discount?
        Child with no purchase history                  | CHILD             | []                                                                                                                                                                                                                                                   | 2026-06-30T12:00   | 20
        Child with extensive recent travel               | CHILD             | ["2026-06-25T12:00 SINGLE", "2026-06-26T12:00 SINGLE", "2026-06-27T12:00 SINGLE", "2026-06-28T12:00 SINGLE", "2026-06-29T12:00 SINGLE"]                                                                                                            | 2026-06-30T12:00   | 20
        Adult buying first single ticket ever            | ADULT             | []                                                                                                                                                                                                                                                   | 2026-06-30T12:00   | 0
        Adult buying fifth ticket in window               | ADULT             | ["2026-06-01T12:00 SINGLE", "2026-06-05T12:00 SINGLE", "2026-06-10T12:00 SINGLE", "2026-06-15T12:00 SINGLE"]                                                                                                                                        | 2026-06-30T12:00   | 5
        Adult ticket outside window does not count        | ADULT             | ["2026-05-01T12:00 SINGLE"]                                                                                                                                                                                                                          | 2026-06-30T12:00   | 0
        Senior buying tenth ticket in window                | SENIOR            | ["2026-06-01T12:00 SINGLE", "2026-06-05T12:00 SINGLE", "2026-06-10T12:00 SINGLE", "2026-06-12T12:00 SINGLE", "2026-06-15T12:00 SINGLE", "2026-06-18T12:00 SINGLE", "2026-06-20T12:00 SINGLE", "2026-06-22T12:00 SINGLE", "2026-06-25T12:00 SINGLE"] | 2026-06-30T12:00 | 10
        Weekly and monthly tickets do not inflate discount  | ADULT             | ["2026-06-01T12:00 SINGLE", "2026-06-05T12:00 SINGLE", "2026-06-10T12:00 SINGLE", "2026-06-15T12:00 SINGLE", "2026-06-20T12:00 WEEKLY", "2026-06-22T12:00 MONTHLY", "2026-06-25T12:00 WEEKLY"]                                                     | 2026-06-30T12:00 | 5
        """)
    void calculatesDiscount(TravelerCategory travelerCategory, List<PastPurchase> pastPurchases, LocalDateTime purchaseTime, int discount) {
        assertEquals(discount, new ReisDiscountCalculator().calculateDiscount(travelerCategory, purchaseTime, pastPurchases).value());
    }
}
