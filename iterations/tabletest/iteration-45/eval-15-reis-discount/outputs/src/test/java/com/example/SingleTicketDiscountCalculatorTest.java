package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.example.HistoryConverters.PURCHASE_TIME;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverterSources;

@TypeConverterSources(HistoryConverters.class)
class SingleTicketDiscountCalculatorTest {

    private final SingleTicketDiscountCalculator calculator =
            new SingleTicketDiscountCalculatorImpl(new SingleTicketHistoryCounterImpl(), new ReisDiscountLadderImpl());

    @DisplayName("Picks the flat child rate or the Reis history-based rate by traveler category")
    @Description("""
            Zone never affects the discount, for either traveler category. Purchase time
            is fixed at 2026-01-31T10:00:00 (see SingleTicketHistoryCounterTest for the
            30-day window rules and ReisDiscountLadderTest for the full tier ladder) -
            this table only shows which rate a category selects and that the two pieces
            wire together correctly.
            """)
    @TableTest("""
            Scenario                                             | Category           | Zone                       | History                                                                                                    | Discount %?
            Child ticket, no travel history                      | CHILD              | {ZONE_1, ZONE_2, ZONE_3}   | []                                                                                                         | 20
            Child ticket, frequent traveler history               | CHILD              | ZONE_1                     | [afterCutoff SINGLE, afterCutoff SINGLE, afterCutoff SINGLE, afterCutoff SINGLE, afterCutoff SINGLE, afterCutoff SINGLE, afterCutoff SINGLE, afterCutoff SINGLE, afterCutoff SINGLE, afterCutoff SINGLE] | 20
            Adult or senior, no qualifying travel history        | {ADULT, SENIOR}    | {ZONE_1, ZONE_2, ZONE_3}   | []                                                                                                         | 0
            Adult or senior reaches the first Reis tier          | {ADULT, SENIOR}    | {ZONE_1, ZONE_2, ZONE_3}   | [afterCutoff SINGLE, afterCutoff SINGLE, afterCutoff SINGLE, afterCutoff SINGLE]                          | 5
            Adult or senior with a busier travel pattern         | {ADULT, SENIOR}    | ZONE_2                     | [afterCutoff SINGLE, afterCutoff SINGLE, afterCutoff SINGLE, afterCutoff SINGLE, afterCutoff SINGLE, afterCutoff SINGLE, afterCutoff SINGLE, afterCutoff SINGLE, afterCutoff SINGLE]                     | 10
            """)
    void calculatesSingleTicketDiscount(TravelerCategory category, ZoneValidity zone, List<PastPurchase> history, int discountPercent) {
        assertEquals(new DiscountPercentage(discountPercent), calculator.calculateDiscount(category, zone, history, PURCHASE_TIME));
    }
}
