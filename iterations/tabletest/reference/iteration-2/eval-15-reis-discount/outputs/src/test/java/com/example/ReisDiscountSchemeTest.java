package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverterSources;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TypeConverterSources(ReisTestConverters.class)
class ReisDiscountSchemeTest {

    @DisplayName("Selects the discount scheme for a single ticket from the traveller category")
    @Description("""
        Zone carries a value set on every row because Reis applies "regardless of the zones you
        travel in" — the same category and ticket number yield the same discount in every zone.
        Tickets This Window is the position established by RecentSingleTicketCounter; this table
        only turns that position into a scheme, and uses one representative rung to do it.
        """)
    @TableTest("""
        Scenario                                       | Traveller Category | Tickets This Window | Zone                     | Discount?
        Children pay a flat rate whatever their travel | CHILD              | {1, 5, 40}          | {ZONE_1, ZONE_2, ZONE_3} | 20
        Adults and seniors climb the same Reis ladder  | {ADULT, SENIOR}    | 5                   | {ZONE_1, ZONE_2, ZONE_3} | 5
        """)
    void selectsDiscountSchemeByTravellerCategory(
        TravelerCategory travelerCategory,
        int ticketsThisWindow,
        ZoneValidity zone,
        DiscountPercentage discount
    ) {
        assertEquals(
            discount,
            new SingleTicketDiscount().discountFor(travelerCategory, ticketsThisWindow, zone));
    }

    @DisplayName("Raises the discount one rung for every fifth ticket, stopping at the maximum")
    @Description("""
        Tickets This Window counts the ticket being bought as well as the qualifying purchases
        behind it, which is why the fifth ticket is the first to earn a discount. This table is
        the only place the count-to-percentage mapping is stated.
        """)
    @TableTest("""
        Scenario                                  | Tickets This Window | Discount?
        Below the first rung                      | {1, 4}              | 0
        The fifth ticket earns the first rung     | {5, 9}              | 5
        The tenth ticket earns the second rung    | {10, 14}            | 10
        The fifteenth ticket earns the third rung | {15, 19}            | 15
        The twentieth ticket earns the fourth     | {20, 24}            | 20
        The twenty-fifth earns the fifth          | {25, 29}            | 25
        The thirtieth earns the sixth             | {30, 34}            | 30
        The thirty-fifth earns the seventh        | {35, 39}            | 35
        The ladder stops at the maximum           | {40, 100}           | 40
        """)
    void raisesTheDiscountEveryFifthTicket(int ticketsThisWindow, DiscountPercentage discount) {
        assertEquals(discount, new ReisLadder().discountFor(ticketsThisWindow));
    }
}
