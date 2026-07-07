package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TripNumberCalculatorTest {

    private final TripNumberCalculator calculator = new TripNumberCalculator();

    @Description("""
        Trip number is the sequence position of the new purchase: the count of past
        SINGLE-ticket purchases within the trailing window ending at Purchase Time, plus
        one for the ticket being bought now. WEEKLY/MONTHLY history entries never count.
        The window is inclusive of its start instant (a purchase exactly Trailing Window
        (Days) ago still counts) and exclusive of Purchase Time itself. Purchase Time is
        fixed at 2026-06-15T00:00:00 for every row; Past Purchases dates are chosen
        relative to it so each boundary can be read directly off the row.
        """)
    @TableTest("""
        Scenario                              | Purchase Time       | Past Purchases                                                                                                            | Trailing Window (Days) | Trip Number?
        No prior trips                        | 2026-06-15T00:00:00 | []                                                                                                                        | 30                      | 1
        Three prior single trips within window| 2026-06-15T00:00:00 | ["2026-06-10T00:00:00 SINGLE", "2026-06-05T00:00:00 SINGLE", "2026-05-31T00:00:00 SINGLE"]                               | 30                      | 4
        Four prior single trips within window | 2026-06-15T00:00:00 | ["2026-06-10T00:00:00 SINGLE", "2026-06-05T00:00:00 SINGLE", "2026-05-31T00:00:00 SINGLE", "2026-05-26T00:00:00 SINGLE"] | 30                      | 5
        Trip exactly at the window boundary   | 2026-06-15T00:00:00 | ["2026-05-16T00:00:00 SINGLE"]                                                                                            | 30                      | 2
        Trip just past the window boundary    | 2026-06-15T00:00:00 | ["2026-05-15T00:00:00 SINGLE"]                                                                                            | 30                      | 1
        History includes non-single ticket types | 2026-06-15T00:00:00 | ["2026-06-10T00:00:00 WEEKLY", "2026-06-05T00:00:00 MONTHLY", "2026-05-31T00:00:00 SINGLE"]                          | 30                      | 2
        Narrower trailing window              | 2026-06-15T00:00:00 | ["2026-06-10T00:00:00 SINGLE", "2026-05-31T00:00:00 SINGLE"]                                                             | 7                       | 2
        """)
    void calculatesTripNumberFromRecentSingleTicketPurchases(LocalDateTime purchaseTime, List<PastPurchase> pastPurchases,
                                                              int trailingWindowDays, int tripNumber) {
        assertEquals(tripNumber, calculator.calculateTripNumber(pastPurchases, purchaseTime, trailingWindowDays));
    }

    @TypeConverter
    public static PastPurchase parseHistoryEntry(String input) {
        String[] parts = input.split(" ");
        return new PastPurchase(LocalDateTime.parse(parts[0]), TravelerCategory.ADULT, TicketType.valueOf(parts[1]), ZoneValidity.ZONE_1);
    }
}
