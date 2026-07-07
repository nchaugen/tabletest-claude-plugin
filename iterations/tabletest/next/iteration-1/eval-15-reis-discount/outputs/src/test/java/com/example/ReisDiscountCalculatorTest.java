package com.example;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverterSources;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TypeConverterSources(PastPurchaseConverters.class)
public class ReisDiscountCalculatorTest {

    private final ReisDiscountCalculator calculator = new ReisDiscountCalculator();

    @DisplayName("End-to-end Reis discount for a new single ticket purchase")
    @Description("""
        Ties the discount rule (ReisDiscountRuleTest) and the trailing-30-day
        ticket count (SingleTicketFrequencyTest) together. Purchase time is
        fixed at 2026-02-15T09:00:00 (see PastPurchaseConverters). The ticket
        now being purchased counts as one more single ticket than what is in
        the history, so four qualifying past tickets plus this one reach the
        fifth ticket overall. Rule selection, zone irrelevance, and ladder
        tiers are exhaustively covered elsewhere; this table only confirms
        the two concerns are wired together correctly.
        """)
    @TableTest("""
        Scenario                                                      | Traveler Category | Zone                     | Purchase History                                                                                                                                                       | Discount?
        First single ticket has no discount yet                       | ADULT             | ZONE_1                   | []                                                                                                                                                                     | 0
        Fifth ticket in thirty days reaches the first rung, any zone  | ADULT             | {ZONE_1, ZONE_2, ZONE_3} | [1d SINGLE ADULT, 8d SINGLE ADULT, 15d SINGLE ADULT, 22d SINGLE ADULT]                                                                                                 | 5
        Tenth ticket in thirty days reaches the second rung           | SENIOR            | ZONE_3                   | [1d SINGLE SENIOR, 3d SINGLE SENIOR, 6d SINGLE SENIOR, 9d SINGLE SENIOR, 12d SINGLE SENIOR, 15d SINGLE SENIOR, 18d SINGLE SENIOR, 21d SINGLE SENIOR, 24d SINGLE SENIOR] | 10
        """)
    void calculatesDiscountFromCategoryZoneAndHistory(TravelerCategory travelerCategory, ZoneValidity zoneValidity, List<PastPurchase> purchaseHistory, int discount) {
        assertEquals(discount, calculator.calculateDiscount(travelerCategory, zoneValidity, purchaseHistory, PastPurchaseConverters.PURCHASE_TIME).value());
    }
}
