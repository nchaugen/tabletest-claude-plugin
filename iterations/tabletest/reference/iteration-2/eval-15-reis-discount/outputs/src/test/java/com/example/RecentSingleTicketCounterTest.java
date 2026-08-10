package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverterSources;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TypeConverterSources(ReisTestConverters.class)
class RecentSingleTicketCounterTest {

    @DisplayName("Counts a past purchase towards the ticket number only when it qualifies")
    @Description("""
        Purchased is expressed relative to the purchase being made, and the window is measured at
        that instant rather than by whole days: exactly 30 days before still counts, an hour past
        that does not. Zone carries a value set because a past purchase counts whatever zone it
        was bought for. Traveller category is not an input here — the history belongs to one
        traveller, so every entry in it is that traveller's.
        """)
    @TableTest("""
        Scenario                                          | Ticket Type       | Purchased      | Zone                     | Counts?
        A single ticket inside the window counts          | SINGLE            | T-29d23h59m59s | {ZONE_1, ZONE_2, ZONE_3} | true
        A single ticket exactly at the window edge counts | SINGLE            | T-30d          | {ZONE_1, ZONE_2, ZONE_3} | true
        An hour past the edge falls outside the window    | SINGLE            | T-30d1h        | {ZONE_1, ZONE_2, ZONE_3} | false
        Period tickets never count                        | {WEEKLY, MONTHLY} | T-1d           | {ZONE_1, ZONE_2, ZONE_3} | false
        """)
    void countsAPastPurchaseOnlyWhenItQualifies(
        TicketType ticketType,
        LocalDateTime purchased,
        ZoneValidity zone,
        boolean counts
    ) {
        PastPurchase pastPurchase =
            new PastPurchase(purchased, TravelerCategory.ADULT, ticketType, zone);
        assertEquals(counts, counterAtPurchaseTime().counts(pastPurchase));
    }

    @DisplayName("Numbers the ticket being bought after the qualifying purchases behind it")
    @Description("""
        The number includes the purchase being made, which is why an empty history yields 1.
        Whether an individual entry qualifies is decided by countsAPastPurchaseOnlyWhenItQualifies;
        this table shows only that qualifying entries are summed and others passed over. Zone is
        omitted from the entry shape for the same reason.
        """)
    @TableTest("""
        Scenario                                      | Purchase History                        | Tickets This Window?
        The first ticket, with nothing behind it      | []                                      | 1
        Period tickets in the history are passed over | [SINGLE@T-1d, WEEKLY@T-2d, SINGLE@T-3d] | 3
        Purchases older than the window drop out      | [SINGLE@T-1d, SINGLE@T-31d]             | 2
        """)
    void numbersTheTicketBeingBought(List<PastPurchase> purchaseHistory, int ticketsThisWindow) {
        assertEquals(ticketsThisWindow, counterAtPurchaseTime().ticketNumberFor(purchaseHistory));
    }

    private RecentSingleTicketCounter counterAtPurchaseTime() {
        return new RecentSingleTicketCounter(ReisTestConverters.PURCHASE_TIME);
    }
}
