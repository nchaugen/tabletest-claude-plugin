package com.example;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverterSources;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TypeConverterSources(PastPurchaseConverters.class)
public class SingleTicketFrequencyTest {

    private final SingleTicketFrequency frequency = new SingleTicketFrequency();

    @DisplayName("Counting recent single tickets for the Reis ladder")
    @Description("""
        Purchase time is fixed at 2026-02-15T09:00:00 for every row (see
        PastPurchaseConverters). Purchase history entries are written as
        "<days before purchase>d <TicketType> <TravelerCategory>"; the
        category token defaults to ADULT when omitted.

        Open assumption: only SINGLE tickets bought under the same traveler
        category as the new purchase count towards the trailing 30-day total.
        Reis is described as a personal discount, so a change in traveler
        category (e.g. a household card used for different family members)
        should not carry travel frequency over from another category.
        """)
    @TableTest("""
        Scenario                                                 | Traveler Category | Purchase History                                                        | Recent Ticket Count?
        No prior purchases                                       | ADULT             | []                                                                       | 0
        Single ticket within the window counts                   | ADULT             | [10d SINGLE ADULT]                                                      | 1
        Several single tickets within the window all count       | ADULT             | [1d SINGLE ADULT, 10d SINGLE ADULT, 20d SINGLE ADULT, 29d SINGLE ADULT] | 4
        Ticket exactly thirty days old still counts               | ADULT             | [30d SINGLE ADULT]                                                      | 1
        Ticket older than thirty days is excluded                 | ADULT             | [31d SINGLE ADULT]                                                      | 0
        Weekly and monthly tickets are not counted                | ADULT             | [5d WEEKLY ADULT, 5d MONTHLY ADULT, 5d SINGLE ADULT]                    | 1
        Tickets bought under a different traveler category don't count | ADULT        | [5d SINGLE ADULT, 5d SINGLE CHILD, 5d SINGLE SENIOR]                    | 1
        Seniors accumulate their own history the same way         | SENIOR            | [5d SINGLE SENIOR, 5d SINGLE ADULT]                                     | 1
        """)
    void countsRecentSingleTicketsInTrailingThirtyDays(TravelerCategory travelerCategory, List<PastPurchase> purchaseHistory, int recentTicketCount) {
        assertEquals(recentTicketCount, frequency.countRecentSingleTickets(purchaseHistory, PastPurchaseConverters.PURCHASE_TIME, travelerCategory));
    }
}
