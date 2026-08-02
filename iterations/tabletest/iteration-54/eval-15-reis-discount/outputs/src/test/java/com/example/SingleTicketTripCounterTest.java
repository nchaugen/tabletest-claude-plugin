package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverterSources;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TypeConverterSources(TestConverters.class)
public class SingleTicketTripCounterTest {

    private final SingleTicketTripCounter tripCounter = new SingleTicketTripCounter();

    @Description("""
        Purchase history is assumed to already be scoped to the traveler buying the new
        ticket, so no filtering by traveler category is applied here. Only SINGLE tickets
        purchased in the 30 days immediately before the purchase time qualify; the window
        is inclusive of the instant exactly 30 days before purchase time. The trip number
        returned includes the ticket currently being purchased, i.e. it equals the count
        of qualifying past purchases plus one.
        """)
    @TableTest("""
        Scenario                                   | Purchase Time       | Past Purchases                                                                                                                                                                            | Trip Number?
        First-ever ticket                          | 2026-08-02T08:00:00 | []                                                                                                                                                                                        | 1
        Three single tickets already this month    | 2026-08-02T08:00:00 | [[purchasedAt: '2026-07-05T08:00:00', ticketType: SINGLE], [purchasedAt: '2026-07-12T08:00:00', ticketType: SINGLE], [purchasedAt: '2026-07-19T08:00:00', ticketType: SINGLE]] | 4
        Purchase exactly 30 days ago counts         | 2026-08-02T08:00:00 | [[purchasedAt: '2026-07-03T08:00:00', ticketType: SINGLE]]                                                                                                                       | 2
        Purchase just over 30 days ago is excluded  | 2026-08-02T08:00:00 | [[purchasedAt: '2026-07-03T07:59:59', ticketType: SINGLE]]                                                                                                                       | 1
        Non-single ticket in window is not counted  | 2026-08-02T08:00:00 | [[purchasedAt: '2026-07-20T08:00:00', ticketType: WEEKLY]]                                                                                                                       | 1
        Mixed history counts only qualifying trips  | 2026-08-02T08:00:00 | [[purchasedAt: '2026-07-20T08:00:00', ticketType: SINGLE], [purchasedAt: '2026-07-21T08:00:00', ticketType: WEEKLY], [purchasedAt: '2026-06-01T08:00:00', ticketType: SINGLE]] | 2
        """)
    void countsTripNumberFromQualifyingPastPurchases(LocalDateTime purchaseTime, List<PastPurchase> pastPurchases, int tripNumber) {
        assertEquals(tripNumber, tripCounter.tripNumberInTrailingWindow(pastPurchases, purchaseTime));
    }
}
