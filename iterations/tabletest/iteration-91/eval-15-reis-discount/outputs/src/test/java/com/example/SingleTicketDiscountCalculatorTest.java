package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SingleTicketDiscountCalculatorTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 6, 30, 12, 0);

    private final SingleTicketDiscountCalculator calculator =
            new ReisSingleTicketDiscountCalculator(new RollingSingleTicketCounter(), new ReisDiscountLadder());

    @TableTest("""
        Scenario                                                            | Traveler Category | Purchase History                                                                                                                                              | Discount %?
        Child always gets the flat discount, regardless of travel history   | CHILD             | {[], [[hoursAgo: 24, type: SINGLE], [hoursAgo: 48, type: SINGLE], [hoursAgo: 72, type: SINGLE], [hoursAgo: 96, type: SINGLE], [hoursAgo: 120, type: SINGLE]]} | 20
        Adult or senior with no recent single tickets starts at no discount | {ADULT, SENIOR}   | []                                                                                                                                                            | 0
        """)
    void appliesAFlatChildDiscountAndStartsAdultsAndSeniorsOnTheReisLadder(TravelerCategory travelerCategory, List<PastPurchase> purchaseHistory, int discountPercentage) {
        assertEquals(discountPercentage, calculator.calculateDiscount(travelerCategory, purchaseHistory, NOW).value());
    }

    @Description("""
        Confirms the calculator feeds an adult's or senior's trailing single-ticket count (this
        purchase included) through the Reis ladder from ReisDiscountLadderTest, rather than
        exercising the ladder's own tiers again here.
        """)
    @TableTest("""
        Scenario                                                                | Traveler Category | Purchase History                                                                                                         | Discount %?
        Adult's discount reflects four recent single tickets plus this purchase | ADULT             | [[hoursAgo: 24, type: SINGLE], [hoursAgo: 48, type: SINGLE], [hoursAgo: 72, type: SINGLE], [hoursAgo: 96, type: SINGLE]] | 5
        """)
    void wiresTheTrailingSingleTicketCountIntoTheReisLadder(TravelerCategory travelerCategory, List<PastPurchase> purchaseHistory, int discountPercentage) {
        assertEquals(discountPercentage, calculator.calculateDiscount(travelerCategory, purchaseHistory, NOW).value());
    }

    @TypeConverter
    public static PastPurchase parsePastPurchase(Map<String, String> fields) {
        long hoursAgo = Long.parseLong(fields.get("hoursAgo"));
        TicketType ticketType = TicketType.valueOf(fields.get("type"));
        return new PastPurchase(NOW.minusHours(hoursAgo), TravelerCategory.ADULT, ticketType, ZoneValidity.ZONE_1);
    }
}
