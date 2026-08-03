package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReisDiscountTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 1, 31, 12, 0);

    @DisplayName("Counts single tickets purchased in the trailing 30 days")
    @Description("""
        Purchase time is fixed at NOW (2026-01-31T12:00) for every row; each history entry's
        timing is expressed as days before NOW via the History column, so a row states the
        boundary directly rather than requiring the reader to compare absolute timestamps.
        Open assumption: a purchase exactly 30 days before NOW falls outside the window - the
        window covers the 30 days immediately preceding the purchase, not including that instant.
        Each history entry's traveler category and zone do not affect the count and are held
        fixed at ADULT / ZONE_1 throughout; only ticket type and timing matter here.
        """)
    @TableTest("""
        Scenario                                                   | History                                                                                                                                   | Single Tickets Counted?
        No past purchases                                          | []                                                                                                                                        | 0
        Single ticket just inside the window                       | [[daysAgo: 29, ticketType: SINGLE]]                                                                                                       | 1
        Single ticket exactly 30 days ago falls outside the window | [[daysAgo: 30, ticketType: SINGLE]]                                                                                                       | 0
        Weekly and monthly tickets are not counted                 | [[daysAgo: 1, ticketType: WEEKLY], [daysAgo: 2, ticketType: MONTHLY]]                                                                     | 0
        Mixed ticket types, some outside the window                | [[daysAgo: 1, ticketType: SINGLE], [daysAgo: 2, ticketType: WEEKLY], [daysAgo: 3, ticketType: SINGLE], [daysAgo: 35, ticketType: SINGLE]] | 2
        """)
    void countsOnlySingleTicketsWithinTheTrailingThirtyDays(List<PastPurchase> history, int ticketsCounted) {
        assertEquals(ticketsCounted, new ReisTicketCounter().countSingleTicketsInLast30Days(history, NOW));
    }

    @DisplayName("Applies the Reis discount ladder based on tickets purchased")
    @Description("""
        Ticket count already includes the ticket currently being purchased - buying the fifth
        single ticket within 30 days is what first reaches a discount, per the product rules.
        This ladder applies to adult and senior travelers; children receive a separate flat
        discount, covered in a different table.
        """)
    @TableTest("""
        Scenario                                          | Tickets In Last 30 Days | Discount?
        Fourth ticket, one below the first tier           | 4                       | 0%
        Fifth ticket reaches the first tier               | 5                       | 5%
        Ninth ticket, last of the first tier              | 9                       | 5%
        Tenth ticket reaches the second tier              | 10                      | 10%
        Fourteenth ticket, last of the second tier        | 14                      | 10%
        Fifteenth ticket reaches the third tier           | 15                      | 15%
        Nineteenth ticket, last of the third tier         | 19                      | 15%
        Twentieth ticket reaches the fourth tier          | 20                      | 20%
        Twenty-fourth ticket, last of the fourth tier     | 24                      | 20%
        Twenty-fifth ticket reaches the fifth tier        | 25                      | 25%
        Twenty-ninth ticket, last of the fifth tier       | 29                      | 25%
        Thirtieth ticket reaches the sixth tier           | 30                      | 30%
        Thirty-fourth ticket, last of the sixth tier      | 34                      | 30%
        Thirty-fifth ticket reaches the seventh tier      | 35                      | 35%
        Thirty-ninth ticket, last of the seventh tier     | 39                      | 35%
        Fortieth ticket reaches the top tier              | 40                      | 40%
        Very frequent traveler, discount stays at the cap | 50                      | 40%
        """)
    void appliesTheReisDiscountLadderByTicketCount(int ticketCount, DiscountPercentage discount) {
        assertEquals(discount, new ReisDiscountLadder().discountForTicketCount(ticketCount));
    }

    @DisplayName("Resolves the single-ticket discount by traveler category, independent of zone")
    @Description("""
        Purchase time is fixed at NOW for every row, same convention as the ticket-count table,
        and History uses the same daysAgo/ticketType shorthand. Zone is varied across every zone
        value on each row to show it never changes the outcome, for every traveler category.
        """)
    @TableTest("""
        Scenario                                              | Traveler Category | Zone                     | History                                                                                                                                    | Discount?
        Adult with no prior purchases pays the standard price | ADULT             | {ZONE_1, ZONE_2, ZONE_3} | []                                                                                                                                         | 0%
        Adult reaches the first Reis tier on the fifth ticket | ADULT             | {ZONE_1, ZONE_2, ZONE_3} | [[daysAgo: 1, ticketType: SINGLE], [daysAgo: 5, ticketType: SINGLE], [daysAgo: 10, ticketType: SINGLE], [daysAgo: 20, ticketType: SINGLE]] | 5%
        Senior also accumulates the Reis discount             | SENIOR            | {ZONE_1, ZONE_2, ZONE_3} | [[daysAgo: 1, ticketType: SINGLE], [daysAgo: 5, ticketType: SINGLE], [daysAgo: 10, ticketType: SINGLE], [daysAgo: 20, ticketType: SINGLE]] | 5%
        Child gets the flat discount regardless of history    | CHILD             | {ZONE_1, ZONE_2, ZONE_3} | [[daysAgo: 1, ticketType: SINGLE], [daysAgo: 2, ticketType: SINGLE]]                                                                       | 20%
        """)
    void resolvesSingleTicketDiscountByTravelerCategory(TravelerCategory travelerCategory, ZoneValidity zone,
                                                          List<PastPurchase> history, DiscountPercentage discount) {
        SingleTicketDiscountCalculator calculator = new ReisSingleTicketDiscountCalculator(
                new InMemoryPurchaseHistoryRepository(history), new ReisTicketCounter(), new ReisDiscountLadder());

        assertEquals(discount, calculator.calculateDiscount(travelerCategory, zone, NOW));
    }

    @TypeConverter
    public static PastPurchase parsePastPurchase(Map<String, String> fields) {
        int daysAgo = Integer.parseInt(fields.get("daysAgo"));
        TicketType ticketType = TicketType.valueOf(fields.get("ticketType"));
        return new PastPurchase(NOW.minusDays(daysAgo), TravelerCategory.ADULT, ticketType, ZoneValidity.ZONE_1);
    }

    @TypeConverter
    public static DiscountPercentage parseDiscountPercentage(String value) {
        return new DiscountPercentage(Integer.valueOf(value.replace("%", "")));
    }

    private static class InMemoryPurchaseHistoryRepository implements PurchaseHistoryRepository {
        private final List<PastPurchase> history;

        InMemoryPurchaseHistoryRepository(List<PastPurchase> history) {
            this.history = history;
        }

        @Override
        public List<PastPurchase> loadPurchaseHistory() {
            return history;
        }
    }
}
