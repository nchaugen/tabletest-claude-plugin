package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReisDiscountCalculatorTest {

    private static final LocalDateTime PURCHASE_TIME = LocalDateTime.parse("2026-07-07T10:00:00");

    @DisplayName("Reis discount mechanism by traveler category")
    @Description("""
        Purchase time is fixed at 2026-07-07T10:00:00 for every row. "Prior Single Tickets"
        is the number of single tickets already counted in the rolling 30-day window before
        this purchase; each one is dated 1 day before the purchase, well inside the window
        (window boundaries are covered in filtersHistoryByTicketTypeAndRecency instead).
        Children always get a flat 20% discount regardless of travel history. Adults and
        seniors instead start the Reis ladder, which reaches its first step (5%) on the 5th
        single ticket (see discountLadderByRecentTicketCount for the full ladder). Zone never
        affects the discount.
        """)
    @TableTest("""
        Scenario                 | Traveler Category | Prior Single Tickets | Zone                    | Discount %?
        Child, new traveler      | CHILD              | 0                    | {ZONE_1, ZONE_2, ZONE_3} | 20
        Child, frequent traveler | CHILD              | 50                   | {ZONE_1, ZONE_2, ZONE_3} | 20
        Adult, first ticket      | ADULT              | 0                    | {ZONE_1, ZONE_2, ZONE_3} | 0
        Adult, fifth ticket      | ADULT              | 4                    | {ZONE_1, ZONE_2, ZONE_3} | 5
        Senior, first ticket     | SENIOR             | 0                    | {ZONE_1, ZONE_2, ZONE_3} | 0
        Senior, fifth ticket     | SENIOR             | 4                    | {ZONE_1, ZONE_2, ZONE_3} | 5
        """)
    void discountMechanismByTravelerCategory(TravelerCategory travelerCategory, int priorSingleTickets, ZoneValidity zone, int discountPercent) {
        ReisDiscountCalculator calculator = new ReisDiscountCalculatorImpl(new FakePurchaseHistoryRepository(historyWith(priorSingleTickets)));

        DiscountPercentage result = calculator.calculateDiscount(travelerCategory, zone, PURCHASE_TIME);

        assertEquals(discountPercent, result.value());
    }

    @DisplayName("Reis discount ladder by recent ticket count")
    @Description("""
        Purchase time fixed at 2026-07-07T10:00:00; zone fixed at ZONE_1 (shown not to matter
        in discountMechanismByTravelerCategory). "Prior Single Tickets" purchases are all dated
        1 day before the purchase, inside the 30-day window. The ladder is identical for adults
        and seniors, so Traveler Category is a value set here. Every fifth single ticket
        (including the one being purchased now) raises the discount another 5%, capped at 40%.
        """)
    @TableTest("""
        Scenario                         | Traveler Category | Prior Single Tickets | Discount %?
        Below first step                 | {ADULT, SENIOR}    | 3                     | 0
        Reaches first step                | {ADULT, SENIOR}    | 4                     | 5
        Between steps                     | {ADULT, SENIOR}    | 8                     | 5
        Reaches second step               | {ADULT, SENIOR}    | 9                     | 10
        Mid ladder                        | {ADULT, SENIOR}    | 24                    | 25
        Reaches maximum                   | {ADULT, SENIOR}    | 39                    | 40
        Beyond maximum stays capped       | {ADULT, SENIOR}    | 49                    | 40
        Far beyond maximum stays capped   | {ADULT, SENIOR}    | 199                   | 40
        """)
    void discountLadderByRecentTicketCount(TravelerCategory travelerCategory, int priorSingleTickets, int discountPercent) {
        ReisDiscountCalculator calculator = new ReisDiscountCalculatorImpl(new FakePurchaseHistoryRepository(historyWith(priorSingleTickets)));

        DiscountPercentage result = calculator.calculateDiscount(travelerCategory, ZoneValidity.ZONE_1, PURCHASE_TIME);

        assertEquals(discountPercent, result.value());
    }

    @DisplayName("Reis history filtering by ticket type and recency")
    @Description("""
        Purchase time fixed at 2026-07-07T10:00:00; traveler category fixed at ADULT and zone
        fixed at ZONE_1 (neither affects which past tickets count, shown elsewhere). Each
        history entry is written as "<days ago>d <ticket type>". Only SINGLE tickets purchased
        within the last 30 days count toward the ladder, and the window is assumed inclusive of
        exactly 30 days ago. Three qualifying single tickets are held fixed and the fourth is
        varied to show it either pushes the count to the first discount step (5%) or is excluded
        and leaves the discount at 0%.
        """)
    @TableTest("""
        Scenario                          | Purchase History                                | Discount %?
        All recent single tickets count    | [1d SINGLE, 5d SINGLE, 10d SINGLE, 20d SINGLE]  | 5
        Weekly tickets are excluded        | [1d SINGLE, 5d SINGLE, 10d SINGLE, 20d WEEKLY]  | 0
        Monthly tickets are excluded       | [1d SINGLE, 5d SINGLE, 10d SINGLE, 20d MONTHLY] | 0
        Exactly 30 days ago still counts   | [1d SINGLE, 5d SINGLE, 10d SINGLE, 30d SINGLE]  | 5
        Just past 30 days ago is excluded  | [1d SINGLE, 5d SINGLE, 10d SINGLE, 31d SINGLE]  | 0
        """)
    void filtersHistoryByTicketTypeAndRecency(List<PastPurchase> purchaseHistory, int discountPercent) {
        ReisDiscountCalculator calculator = new ReisDiscountCalculatorImpl(new FakePurchaseHistoryRepository(purchaseHistory));

        DiscountPercentage result = calculator.calculateDiscount(TravelerCategory.ADULT, ZoneValidity.ZONE_1, PURCHASE_TIME);

        assertEquals(discountPercent, result.value());
    }

    @TypeConverter
    public static PastPurchase parsePastPurchase(String descriptor) {
        String[] parts = descriptor.split(" ");
        int daysAgo = Integer.parseInt(parts[0].substring(0, parts[0].length() - 1));
        TicketType ticketType = TicketType.valueOf(parts[1]);
        return new PastPurchase(PURCHASE_TIME.minusDays(daysAgo), TravelerCategory.ADULT, ticketType, ZoneValidity.ZONE_1);
    }

    private static List<PastPurchase> historyWith(int priorSingleTicketCount) {
        List<PastPurchase> purchases = new ArrayList<>();
        for (int i = 0; i < priorSingleTicketCount; i++) {
            purchases.add(new PastPurchase(PURCHASE_TIME.minusDays(1), TravelerCategory.ADULT, TicketType.SINGLE, ZoneValidity.ZONE_1));
        }
        return purchases;
    }

    private static class FakePurchaseHistoryRepository implements PurchaseHistoryRepository {
        private final List<PastPurchase> purchases;

        FakePurchaseHistoryRepository(List<PastPurchase> purchases) {
            this.purchases = purchases;
        }

        @Override
        public List<PastPurchase> loadPurchaseHistory() {
            return purchases;
        }
    }
}
