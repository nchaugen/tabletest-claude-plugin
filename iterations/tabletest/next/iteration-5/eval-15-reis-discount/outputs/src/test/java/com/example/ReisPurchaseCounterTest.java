package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReisPurchaseCounterTest {

    private final ReisPurchaseCounter counter = new ReisPurchaseCounter();

    @Description("""
        Counts only SINGLE tickets purchased in the 30 days up to and including
        the purchase date; WEEKLY and MONTHLY tickets never count toward Reis.
        Assumption: the 30-day window is inclusive of the day exactly 30 days
        before the purchase date (open question: not specified by the source
        requirements, which only say "in the last 30 days").
        """)
    @TableTest("""
        Scenario                                  | Single Ticket Dates                   | Other Ticket Dates    | Now        | Qualifying Count?
        No purchase history                       | []                                     | []                    | 2026-06-30 | 0
        Counts single tickets in the window        | [2026-06-01, 2026-06-15]               | []                    | 2026-06-30 | 2
        Non-single tickets are excluded            | [2026-06-01]                           | [2026-06-15, 2026-06-20] | 2026-06-30 | 1
        Exactly 30 days before still counts        | [2026-05-31]                           | []                    | 2026-06-30 | 1
        One day past the window is excluded        | [2026-05-30]                           | []                    | 2026-06-30 | 0
        Old and recent tickets mixed                | [2026-05-01, 2026-06-15, 2026-06-29]   | []                    | 2026-06-30 | 2
        """)
    void countsQualifyingTickets(List<LocalDate> singleTicketDates, List<LocalDate> otherTicketDates,
                                  LocalDate now, int qualifyingCount) {
        List<PastPurchase> history = new ArrayList<>();
        for (LocalDate date : singleTicketDates) {
            history.add(new PastPurchase(date.atStartOfDay(), TravelerCategory.ADULT, TicketType.SINGLE, ZoneValidity.ZONE_1));
        }
        for (LocalDate date : otherTicketDates) {
            history.add(new PastPurchase(date.atStartOfDay(), TravelerCategory.ADULT, TicketType.WEEKLY, ZoneValidity.ZONE_1));
        }

        assertEquals(qualifyingCount, counter.countPriorSingleTicketsInLast30Days(history, now));
    }
}
