package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverterSources;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TypeConverterSources(PastPurchaseConverters.class)
public class ReisWindowTest {

    @DisplayName("Qualifying ticket count in the trailing 30-day window")
    @Description("""
        Counts SINGLE ticket purchases in the 30 days before the purchase time.
        The window boundary is exclusive: a ticket purchased exactly 30 days
        before the purchase time does not count. Traveler category and zone on
        past purchases are fixed to ADULT/ZONE_1 in this table since they do
        not affect the count.
        """)
    @TableTest("""
        Scenario                                        | Past Purchases                                                                    | Purchase Time     | Qualifying Count?
        No purchase history                             | []                                                                                 | 2026-06-30T12:00   | 0
        Single ticket within window                     | ["2026-06-01T12:00 SINGLE"]                                                        | 2026-06-30T12:00   | 1
        Ticket exactly 30 days before is excluded        | ["2026-05-31T12:00 SINGLE"]                                                        | 2026-06-30T12:00   | 0
        Ticket one minute inside the boundary            | ["2026-05-31T12:01 SINGLE"]                                                        | 2026-06-30T12:00   | 1
        Weekly and monthly tickets do not count          | ["2026-06-01T12:00 WEEKLY", "2026-06-05T12:00 MONTHLY"]                            | 2026-06-30T12:00   | 0
        Only single tickets counted among mixed types    | ["2026-06-01T12:00 SINGLE", "2026-06-02T12:00 WEEKLY", "2026-06-03T12:00 SINGLE"]   | 2026-06-30T12:00   | 2
        Old ticket well outside window ignored           | ["2026-01-01T12:00 SINGLE"]                                                        | 2026-06-30T12:00   | 0
        """)
    void countsQualifyingTickets(List<PastPurchase> pastPurchases, LocalDateTime purchaseTime, int qualifyingCount) {
        assertEquals(qualifyingCount, ReisWindow.countQualifyingTickets(pastPurchases, purchaseTime));
    }
}
