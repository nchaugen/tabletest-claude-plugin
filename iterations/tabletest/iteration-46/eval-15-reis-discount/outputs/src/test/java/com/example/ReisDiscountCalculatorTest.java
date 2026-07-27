package com.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReisDiscountCalculatorTest {

    private final ReisDiscountCalculator calculator = new ReisDiscountCalculator();

    @DisplayName("Computes the Reis accumulation ladder discount from a recent ticket count")
    @Description("""
        Applies only to adult and senior travelers; children receive a flat discount that does not
        use this ladder (see discountFor). The recent ticket count is the number of single tickets
        purchased in the trailing 30 days, including the ticket currently being purchased.
        """)
    @TableTest("""
        Scenario                                  | Recent Ticket Count | Discount %?
        Fewer than five recent tickets             | {0, 2, 4}           | 0
        Five to nine recent tickets                | {5, 7, 9}           | 5
        Ten to fourteen recent tickets              | {10, 12, 14}        | 10
        Fifteen to nineteen recent tickets          | {15, 17, 19}        | 15
        Twenty to twenty-four recent tickets        | {20, 22, 24}        | 20
        Twenty-five to twenty-nine recent tickets   | {25, 27, 29}        | 25
        Thirty to thirty-four recent tickets        | {30, 32, 34}        | 30
        Thirty-five to thirty-nine recent tickets   | {35, 37, 39}        | 35
        Forty or more recent tickets, capped        | {40, 45, 100}       | 40
        """)
    void computesLadderDiscount(int recentTicketCount, int discountPercent) {
        assertEquals(new DiscountPercentage(discountPercent), calculator.ladderDiscount(recentTicketCount));
    }

    @DisplayName("Counts single tickets purchased in the trailing 30 days as of the purchase time")
    @Description("""
        Purchase time is fixed at 2024-06-30T12:00:00 for every row; only purchase history varies.
        The 30-day window is inclusive of its lower boundary: a purchase exactly 30 days before the
        purchase time counts, one 31 days before does not. Traveler category and zone are held
        constant (ADULT, ZONE_1) in the synthesized history entries; neither affects this count.
        """)
    @TableTest("""
        Scenario                                    | Purchase History                                                                                                          | Recent Single Ticket Count?
        Empty history                                | []                                                                                                                         | 0
        Single ticket one day before purchase        | ["2024-06-29T12:00:00@SINGLE"]                                                                                            | 1
        Single ticket exactly 30 days before purchase | ["2024-05-31T12:00:00@SINGLE"]                                                                                            | 1
        Single ticket 31 days before purchase         | ["2024-05-30T12:00:00@SINGLE"]                                                                                            | 0
        Mixed ticket types, only singles counted      | ["2024-06-05T09:00:00@SINGLE", "2024-06-10T09:00:00@WEEKLY", "2024-06-15T09:00:00@SINGLE", "2024-06-20T09:00:00@MONTHLY"] | 2
        Multiple single tickets across the window     | ["2024-06-01T09:00:00@SINGLE", "2024-06-10T09:00:00@SINGLE", "2024-06-20T09:00:00@SINGLE", "2024-06-29T09:00:00@SINGLE"] | 4
        """)
    void countsRecentSingleTickets(List<PastPurchase> purchaseHistory, int expectedCount) {
        LocalDateTime purchaseTime = LocalDateTime.parse("2024-06-30T12:00:00");

        assertEquals(expectedCount, calculator.recentSingleTicketCount(purchaseHistory, purchaseTime));
    }

    @DisplayName("Selects the discount rule to apply by traveler category, ignoring zone")
    @Description("""
        Recent ticket count is a value set on the child row because children receive a flat discount
        regardless of how many tickets they have purchased. Adult and senior travelers are treated
        identically, so they share a row; both delegate to the accumulation ladder, whose own tiers
        are covered by computesLadderDiscount, so only one representative count is used here.
        """)
    @TableTest("""
        Scenario                                                              | Traveler Category | Recent Ticket Count | Zone                                | Discount %?
        Child ticket, flat discount regardless of ticket count or zone        | CHILD              | {0, 5, 40}           | {ZONE_1, ZONE_2, ZONE_3}            | 20
        Adult and senior tickets use the accumulation ladder regardless of zone | {ADULT, SENIOR}    | 5                    | {ZONE_1, ZONE_2, ZONE_3}            | 5
        """)
    void selectsDiscountRuleByCategory(TravelerCategory travelerCategory, int recentTicketCount, ZoneValidity zone, int discountPercent) {
        assertEquals(new DiscountPercentage(discountPercent), calculator.discountFor(travelerCategory, zone, recentTicketCount));
    }

    @Test
    void combinesRecentTicketCountAndCategoryIntoAFinalDiscount() {
        List<PastPurchase> history = List.of(
            new PastPurchase(LocalDateTime.parse("2024-06-01T09:00:00"), TravelerCategory.ADULT, TicketType.SINGLE, ZoneValidity.ZONE_2),
            new PastPurchase(LocalDateTime.parse("2024-06-10T09:00:00"), TravelerCategory.ADULT, TicketType.SINGLE, ZoneValidity.ZONE_2),
            new PastPurchase(LocalDateTime.parse("2024-06-20T09:00:00"), TravelerCategory.ADULT, TicketType.SINGLE, ZoneValidity.ZONE_2),
            new PastPurchase(LocalDateTime.parse("2024-06-25T09:00:00"), TravelerCategory.ADULT, TicketType.SINGLE, ZoneValidity.ZONE_2)
        );
        LocalDateTime purchaseTime = LocalDateTime.parse("2024-06-30T12:00:00");

        DiscountPercentage discount = calculator.discountFor(TravelerCategory.ADULT, ZoneValidity.ZONE_2, history, purchaseTime);

        assertEquals(new DiscountPercentage(5), discount);
    }

    @TypeConverter
    public static PastPurchase parsePastPurchase(String input) {
        String[] parts = input.split("@");
        LocalDateTime purchasedAt = LocalDateTime.parse(parts[0]);
        TicketType ticketType = TicketType.valueOf(parts[1]);
        return new PastPurchase(purchasedAt, TravelerCategory.ADULT, ticketType, ZoneValidity.ZONE_1);
    }
}
