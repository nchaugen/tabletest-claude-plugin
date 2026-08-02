package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReisDiscountCalculatorTest {

    private static final LocalDateTime REFERENCE_PURCHASE_TIME = LocalDateTime.parse("2026-08-02T10:00:00");

    private final ReisTravelFrequency travelFrequency = new ReisTravelFrequencyImpl();
    private final ReisDiscountLadder discountLadder = new ReisDiscountLadderImpl();
    private final ReisDiscountCalculator calculator = new ReisDiscountCalculatorImpl(travelFrequency, discountLadder);

    @DisplayName("Counts single-ticket purchases within the trailing 30-day window")
    @Description("""
        Purchase time is fixed at 2026-08-02T10:00:00 for every row. Each history entry
        is `[daysAgo: N, type: TICKETTYPE]`, optionally with `category` and `zone` keys
        (default ADULT / ZONE_1) shown only where a row is specifically about them.
        """)
    @TableTest("""
        Scenario                                        | Purchase History                                                                             | Count?
        No purchase history                             | []                                                                                            | 0
        Single purchase inside the window               | [[daysAgo: 5, type: SINGLE]]                                                                 | 1
        Purchase exactly 30 days ago is included         | [[daysAgo: 30, type: SINGLE]]                                                                | 1
        Purchase 31 days ago falls outside the window    | [[daysAgo: 31, type: SINGLE]]                                                                | 0
        Older purchases outside the window are dropped   | [[daysAgo: 10, type: SINGLE], [daysAgo: 40, type: SINGLE]]                                  | 1
        Weekly and monthly tickets do not count          | [[daysAgo: 5, type: WEEKLY], [daysAgo: 5, type: MONTHLY]]                                    | 0
        Purchase in zone 1 counts                        | [[daysAgo: 5, type: SINGLE, zone: ZONE_1]]                                                   | 1
        Purchase in zone 3 counts the same                | [[daysAgo: 5, type: SINGLE, zone: ZONE_3]]                                                   | 1
        Adult and senior purchases share one count       | [[daysAgo: 5, type: SINGLE, category: ADULT], [daysAgo: 6, type: SINGLE, category: SENIOR]] | 2
        """)
    void countsSingleTicketPurchasesInLast30Days(List<PastPurchase> purchaseHistory, int count) {
        assertEquals(count, travelFrequency.countSingleTicketPurchasesInLast30Days(purchaseHistory, REFERENCE_PURCHASE_TIME));
    }

    @DisplayName("Determines the Reis discount tier from the purchase number")
    @Description("""
        Purchase number is the count of single tickets purchased in the trailing 30 days,
        including the ticket now being purchased.
        """)
    @TableTest("""
        Scenario                                        | Purchase Number | Discount %?
        Before the first tier                           | 1               | 0
        Just short of the first tier                    | 4               | 0
        First discount applies at the fifth ticket       | 5               | 5
        Plateau before the second tier                  | 9               | 5
        Second tier begins at the tenth ticket           | 10              | 10
        Plateau before the top natural tier              | 39              | 35
        Top natural tier reached at the fortieth ticket  | 40              | 40
        Ladder is capped at the 40% maximum              | 45              | 40
        """)
    void discountForPurchaseNumber(int purchaseNumber, int discountPercent) {
        assertEquals(discountPercent, discountLadder.discountForPurchaseNumber(purchaseNumber).value());
    }

    @DisplayName("Dispatches the Reis discount calculation by traveler category")
    @Description("""
        Purchase time is fixed at 2026-08-02T10:00:00 for every row, matching the
        reference time used above. History entries use the same
        `[daysAgo: N, type: TICKETTYPE]` notation.
        """)
    @TableTest("""
        Scenario                                                    | Traveler Category | Purchase History                                                                                                | Discount %?
        Child gets the flat discount with no history                | CHILD              | []                                                                                                              | 20
        Child gets the flat discount regardless of travel frequency | CHILD              | [[daysAgo: 1, type: SINGLE], [daysAgo: 2, type: SINGLE], [daysAgo: 3, type: SINGLE], [daysAgo: 4, type: SINGLE]] | 20
        Adult buying their first single ticket gets no discount yet | ADULT              | []                                                                                                              | 0
        Adult reaches the first discount on their fifth ticket      | ADULT              | [[daysAgo: 1, type: SINGLE], [daysAgo: 2, type: SINGLE], [daysAgo: 3, type: SINGLE], [daysAgo: 4, type: SINGLE]] | 5
        Senior reaches the first discount on their fifth ticket     | SENIOR             | [[daysAgo: 1, type: SINGLE], [daysAgo: 2, type: SINGLE], [daysAgo: 3, type: SINGLE], [daysAgo: 4, type: SINGLE]] | 5
        """)
    void calculatesDiscount(TravelerCategory travelerCategory, List<PastPurchase> purchaseHistory, int discountPercent) {
        DiscountPercentage discount = calculator.calculateDiscount(travelerCategory, REFERENCE_PURCHASE_TIME, () -> purchaseHistory);
        assertEquals(discountPercent, discount.value());
    }

    @TypeConverter
    public static PastPurchase parsePastPurchase(Map<String, String> fields) {
        long daysAgo = Long.parseLong(fields.get("daysAgo"));
        TicketType type = TicketType.valueOf(fields.get("type"));
        TravelerCategory category = fields.containsKey("category")
            ? TravelerCategory.valueOf(fields.get("category"))
            : TravelerCategory.ADULT;
        ZoneValidity zone = fields.containsKey("zone")
            ? ZoneValidity.valueOf(fields.get("zone"))
            : ZoneValidity.ZONE_1;
        return new PastPurchase(REFERENCE_PURCHASE_TIME.minusDays(daysAgo), category, type, zone);
    }
}
